# Cleanup – Unnecessary / Legacy Items

## Root `src/` (legacy duplicate)

The repository has a **root-level `src/`** folder (e.g. `src/main/java/com/satishlabs/auth/...`) that is a **duplicate** of **auth-module**. The build uses only the Maven modules (auth-module, auth-service, etc.); the root `src/` is **not** part of any module and is legacy from before the multi-module layout.

**Recommendation:** Delete the root `src/` folder when no process is locking it (close IDE or any process using those files):

- **Windows (PowerShell):**  
  `Remove-Item -Recurse -Force src`  
  (from the repo root)
- **Linux/macOS:**  
  `rm -rf src`

If you get "Access denied", close Cursor/IDE and any Java process, then run the command again.

## Removed in this cleanup

- **Note** – Removed (curl examples / scratch notes; not part of the codebase).
