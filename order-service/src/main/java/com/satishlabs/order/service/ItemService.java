package com.satishlabs.order.service;

import java.util.List;

import com.satishlabs.order.dto.request.ItemRequest;
import com.satishlabs.order.dto.response.ItemResponse;

public interface ItemService {
    List<ItemResponse> getAllItems();
    List<ItemResponse> getActiveItems();
    ItemResponse getItemById(Long id);
    ItemResponse createItem(ItemRequest request);
    ItemResponse updateItem(Long id, ItemRequest request);
    void deleteItem(Long id);
}
