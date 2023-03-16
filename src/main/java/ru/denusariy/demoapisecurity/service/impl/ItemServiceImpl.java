package ru.denusariy.demoapisecurity.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.denusariy.demoapisecurity.domain.dto.request.ItemRequest;
import ru.denusariy.demoapisecurity.domain.dto.response.ItemResponse;
import ru.denusariy.demoapisecurity.service.ItemService;
@Service
@Transactional
public class ItemServiceImpl implements ItemService {
    @Override
    public ItemResponse saveItem(ItemRequest newItem) {
        //some logic
        return null;
    }

    @Override
    public ItemResponse updateItem(ItemRequest updatedItem, int id) {
        //some logic
        return null;
    }

    @Override
    public String deleteItem(int id) {
        //some logic
        return null;
    }
}
