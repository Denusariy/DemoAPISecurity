package ru.denusariy.demoapisecurity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.denusariy.demoapisecurity.domain.dto.request.ItemRequest;
import ru.denusariy.demoapisecurity.domain.dto.response.ItemResponse;
import ru.denusariy.demoapisecurity.service.ItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/create")
    public ResponseEntity<ItemResponse> create(@RequestBody ItemRequest newItem) {
        return ResponseEntity.ok(itemService.saveItem(newItem));
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<ItemResponse> update(@PathVariable("id") int id, @RequestBody ItemRequest updatedItem) {
        return ResponseEntity.ok(itemService.updateItem(updatedItem, id));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> delete(@PathVariable("id") int id) {
        return ResponseEntity.ok(itemService.deleteItem(id));
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello, Admin!");
    }
}
