package com.satishlabs.gateway.proxy;

import com.satishlabs.gateway.config.GatewayProperties;
import com.satishlabs.gateway.security.JwtValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class GatewayProxyController {

	private final RestTemplate restTemplate;
	private final GatewayProperties gatewayProperties;
	private final JwtValidator jwtValidator;

	@GetMapping("/**")
	public ResponseEntity<byte[]> get(HttpServletRequest request) throws IOException {
		return proxy(request, HttpMethod.GET, null);
	}

	@PostMapping("/**")
	public ResponseEntity<byte[]> post(HttpServletRequest request, @RequestBody(required = false) byte[] body) throws IOException {
		return proxy(request, HttpMethod.POST, body);
	}

	@PutMapping("/**")
	public ResponseEntity<byte[]> put(HttpServletRequest request, @RequestBody(required = false) byte[] body) throws IOException {
		return proxy(request, HttpMethod.PUT, body);
	}

	@DeleteMapping("/**")
	public ResponseEntity<byte[]> delete(HttpServletRequest request) throws IOException {
		return proxy(request, HttpMethod.DELETE, null);
	}

	private ResponseEntity<byte[]> proxy(HttpServletRequest request, HttpMethod method, byte[] body) throws IOException {
		String requestUri = request.getRequestURI();
		String pathInfo = requestUri.replaceFirst("^/api", "");
		if (pathInfo.isEmpty()) pathInfo = "/";
		if (!pathInfo.startsWith("/")) pathInfo = "/" + pathInfo;
		String queryString = request.getQueryString();
		String pathWithQuery = pathInfo + (queryString != null ? "?" + queryString : "");

		// JWT validation for protected paths
		if (!isPublicPath(requestUri, method)) {
			String authHeader = request.getHeader("Authorization");
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			String token = authHeader.substring(7);
			if (!jwtValidator.isValid(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		}

		String targetUrl = resolveTargetUrl(pathWithQuery);
		if (targetUrl == null) {
			return ResponseEntity.notFound().build();
		}

		HttpHeaders forwardHeaders = copyHeaders(request);
		// Add X-User-Id for downstream services when JWT is present
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			jwtValidator.extractUserId(authHeader.substring(7))
					.ifPresent(userId -> forwardHeaders.set("X-User-Id", String.valueOf(userId)));
		}

		HttpEntity<byte[]> entity = new HttpEntity<>(body != null ? body : new byte[0], forwardHeaders);
		try {
			ResponseEntity<byte[]> response = restTemplate.exchange(
					URI.create(targetUrl),
					method,
					entity,
					byte[].class
			);
			HttpHeaders responseHeaders = new HttpHeaders();
			response.getHeaders().forEach((k, v) -> {
				if (!k.equalsIgnoreCase("Transfer-Encoding")) responseHeaders.addAll(k, v);
			});
			return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
		} catch (HttpStatusCodeException e) {
			return ResponseEntity.status(e.getStatusCode())
					.headers(e.getResponseHeaders())
					.body(e.getResponseBodyAsByteArray());
		}
	}

	private boolean isPublicPath(String requestUri, HttpMethod method) {
		if (requestUri.startsWith("/api/auth/") || requestUri.equals("/api/auth")) return true;
		if (requestUri.startsWith("/api/puja") && method == HttpMethod.GET) return true;
		if (requestUri.startsWith("/api/pandit") && method == HttpMethod.GET) return true;
		if (requestUri.startsWith("/api/items") && method == HttpMethod.GET) return true;
		if (requestUri.startsWith("/oauth2/") || requestUri.startsWith("/login/")) return true;
		return false;
	}

	private String resolveTargetUrl(String pathWithQuery) {
		String path = pathWithQuery.contains("?") ? pathWithQuery.substring(0, pathWithQuery.indexOf("?")) : pathWithQuery;
		String query = pathWithQuery.contains("?") ? pathWithQuery.substring(pathWithQuery.indexOf("?")) : "";
		String base = path + query;
		if (path.startsWith("/auth") || path.startsWith("/user")) {
			return gatewayProperties.getAuthServiceUrl() + base;
		}
		if (path.startsWith("/puja")) {
			return gatewayProperties.getPujaServiceUrl() + base;
		}
		if (path.startsWith("/pandit")) {
			return gatewayProperties.getPanditServiceUrl() + base;
		}
		if (path.startsWith("/orders") || path.startsWith("/items")
				|| path.startsWith("/admin/items") || path.startsWith("/admin/products") || path.startsWith("/admin/orders")) {
			return gatewayProperties.getOrderServiceUrl() + base;
		}
		if (path.startsWith("/admin/pandit")) {
			return gatewayProperties.getPanditServiceUrl() + base;
		}
		if (path.startsWith("/admin/puja")) {
			return gatewayProperties.getPujaServiceUrl() + base;
		}
		if (path.startsWith("/notify")) {
			return gatewayProperties.getNotificationServiceUrl() + base;
		}
		return null;
	}

	private HttpHeaders copyHeaders(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		Enumeration<String> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			if (name.equalsIgnoreCase("Host") || name.equalsIgnoreCase("Connection")) continue;
			headers.addAll(name, Collections.list(request.getHeaders(name)));
		}
		if (headers.getContentType() == null) {
			headers.setContentType(MediaType.APPLICATION_JSON);
		}
		return headers;
	}
}
