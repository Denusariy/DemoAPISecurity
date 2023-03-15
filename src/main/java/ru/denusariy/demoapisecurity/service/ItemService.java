package ru.denusariy.demoapisecurity.service;

import ru.denusariy.demoapisecurity.domain.dto.request.ItemRequest;
import ru.denusariy.demoapisecurity.domain.dto.response.ItemResponse;

public interface ItemService {
    ItemResponse saveItem(ItemRequest newItem);
    ItemResponse updateItem(ItemRequest updatedItem, int id);
    String deleteItem(int id);

}
