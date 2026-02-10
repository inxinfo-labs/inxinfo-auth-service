package com.satishlabs.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Product category for API (e.g. GET /items/categories). Products = physical items; distinct from Puja Types. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDto {
    private String value;
    private String displayName;
}
