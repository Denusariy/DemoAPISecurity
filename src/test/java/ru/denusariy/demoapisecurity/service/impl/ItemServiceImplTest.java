package ru.denusariy.demoapisecurity.service.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.denusariy.demoapisecurity.domain.dto.request.ItemRequest;
import ru.denusariy.demoapisecurity.domain.dto.response.ItemResponse;
import ru.denusariy.demoapisecurity.domain.entity.Item;
import ru.denusariy.demoapisecurity.exception.ItemNotFoundException;
import ru.denusariy.demoapisecurity.repository.ItemRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepositoryMock;
    @Spy
    private ModelMapper modelMapperMock;

    @Nested
    class ConvertToDTOTest {
         @Test
         void should_ConvertItemToResponseDTO() {
            //given
            Item item = new Item(1, "item1", 1111);
            ItemResponse expected = new ItemResponse("item1", 1111);
            //when
            ItemResponse actual = itemService.convertToResponseDTO(item);
            //then
            assertAll(
                    () -> assertEquals(expected.getName(), actual.getName()),
                    () -> assertEquals(expected.getArticle(), actual.getArticle()));
        }
    }

    @Nested
    class ConvertToItemTest {
        @Test
        void should_ConvertItemRequestDTOToItem() {
            //given
            ItemRequest request = new ItemRequest("item1", 1111);
            Item expected = new Item(0, "item1", 1111);
            //when
            Item actual = itemService.convertToItem(request);
            //then
            assertAll(
                    () -> assertEquals(expected.getName(), actual.getName()),
                    () -> assertEquals(expected.getArticle(), actual.getArticle()));
        }
    }

    @Nested
    class CreateItemTest{
        @Test
        void should_ReturnResponseDTO_When_SaveNewItem() {
            //given
            ItemRequest request = new ItemRequest("item1", 1111);
            Item newItem = new Item(0, "item1", 1111);
            ItemResponse expected = new ItemResponse("item1", 1111);
            when(itemRepositoryMock.save(newItem)).thenReturn(newItem);
            //when
            ItemResponse actual = itemService.saveItem(request);
            //then
            assertAll(
                    () -> assertEquals(expected.getName(), actual.getName()),
                    () -> assertEquals(expected.getArticle(), actual.getArticle()),
                    () -> verify(itemRepositoryMock).save(any())
            );
        }
    }

    @Nested
    class UpdateItemTest{

        @Test
        void should_ThrowException_When_IdIsNotPresent() {
            //given
            ItemRequest updatedItem = new ItemRequest("Item1", 333);
            when(itemRepositoryMock.findById(anyInt())).thenThrow(ItemNotFoundException.class);
            //when
            Executable executable = () -> itemService.updateItem(updatedItem, anyInt());
            //then
            assertThrows(ItemNotFoundException.class, executable);

        }
        @Test
        void should_UpdateAdminAndReturnDTO_When_Email_Is_Present() {
            //given
            ItemRequest updatedItem = new ItemRequest("Item1", 333);
            Item item = new Item(1, "Item2", 555);
            ItemResponse expected = new ItemResponse("Item1", 333);
            when(itemRepositoryMock.findById(1)).thenReturn(Optional.of(item));
            //when
            ItemResponse actual = itemService.updateItem(updatedItem, 1);
            //then
            assertAll(
                    () -> assertEquals(expected.getName(), actual.getName()),
                    () -> assertEquals(expected.getArticle(), actual.getArticle()),
                    () -> verify(itemRepositoryMock).save(item)
            );
        }
    }

    @Nested
    class DeleteItemTest{

        @Test
        void should_ThrowException_When_IdIsNotPresent() {
            //given
            when(itemRepositoryMock.findById(1)).thenThrow(ItemNotFoundException.class);
            //when
            Executable executable = () -> itemService.deleteItem(1);
            //then
            assertAll(
                    () -> assertThrows(ItemNotFoundException.class, executable),
                    () -> verify(itemRepositoryMock, never()).delete(any())
            );

        }
        @Test
        void should_DeleteItemAndReturnArticle_When_IdIsPresent() {
            //given
            Item item = new Item(1, "Item2", 555);
            when(itemRepositoryMock.findById(1)).thenReturn(Optional.of(item));
            //when
            String actual = itemService.deleteItem(1);
            //then
            assertAll(
                    () -> assertEquals("Удален товар 555", actual),
                    () -> verify(itemRepositoryMock).delete(item)
            );
        }
    }
}