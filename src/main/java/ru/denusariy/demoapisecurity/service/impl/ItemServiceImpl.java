package ru.denusariy.demoapisecurity.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.denusariy.demoapisecurity.domain.dto.request.ItemRequest;
import ru.denusariy.demoapisecurity.domain.dto.response.ItemResponse;
import ru.denusariy.demoapisecurity.domain.entity.Item;
import ru.denusariy.demoapisecurity.exception.ItemNotFoundException;
import ru.denusariy.demoapisecurity.repository.ItemRepository;
import ru.denusariy.demoapisecurity.service.ItemService;
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ItemResponse saveItem(ItemRequest newItem) {
        return convertToResponseDTO(itemRepository.save(convertToItem(newItem)));
    }

    @Override
    @Transactional
    public ItemResponse updateItem(ItemRequest updatedItem, int id) {
        Item itemToBeUpdated = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(
                String.format("Товар с id %d не найден", id)
        ));
        itemToBeUpdated.setName(updatedItem.getName());
        itemToBeUpdated.setArticle(updatedItem.getArticle());
        itemRepository.save(itemToBeUpdated);
        return convertToResponseDTO(itemToBeUpdated);
    }

    @Override
    @Transactional
    public String deleteItem(int id) {
        Item itemToBeDeleted = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(
                String.format("Товар с id %d не найден", id)
        ));
        long article = itemToBeDeleted.getArticle();
        itemRepository.delete(itemToBeDeleted);
        return "Удален товар " + article;
    }

    public ItemResponse convertToResponseDTO(Item item) {
        return modelMapper.map(item, ItemResponse.class);
    }

    public  Item convertToItem(ItemRequest itemRequest) {
        return modelMapper.map(itemRequest, Item.class);
    }
}
