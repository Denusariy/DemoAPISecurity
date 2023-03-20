package ru.denusariy.demoapisecurity.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.denusariy.demoapisecurity.repository.ItemRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ItemControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ItemRepository itemRepository;

    @Nested
    class UserPageTest{
        @Test
        @WithUserDetails("user@mail.com")
        public void should_ReturnUserPage_When_UserAuthenticated() throws Exception{
            var requestBuilder = MockMvcRequestBuilders.get("/api/v1/items");
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(content().string("Hello, User!"));
        }

        @Test
        public void should_Return403_When_UserNotAuthenticated() throws Exception{
            var requestBuilder = MockMvcRequestBuilders.get("/api/v1/items");
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class CreateTest{
        @Test
        @WithUserDetails("admin@mail.com")
        public void should_CreateItemAndReturnResponse_When_UserHasAuthority() throws Exception{
            var requestBuilder = MockMvcRequestBuilders.post("/api/v1/items/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "name": "item2",
                            "article": 2
                        }
                        """);
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                        {
                            "name": "item2",
                            "article": 2
                        }
                        """));
            assertEquals(2, itemRepository.count());
        }

        @Test
        @WithUserDetails("user@mail.com")
        public void should_Return403_When_UserHasNoAuthority() throws Exception{
            var requestBuilder = MockMvcRequestBuilders.post("/api/v1/items/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "name": "item2",
                            "article": 2
                        }
                        """);
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isForbidden());
            assertEquals(1, itemRepository.count());
        }
    }

    @Nested
    class UpdateTest{
        @Test
        @WithUserDetails("super@mail.com")
        public void should_UpdateItemAndReturnResponse_When_UserHasAuthorityAndIdIsPresent() throws Exception{
            var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/items/update/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "name": "item2",
                            "article": 20
                        }
                        """);
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                        {
                            "name": "item2",
                            "article": 20
                        }
                        """));
        }

        @Test
        @WithUserDetails("super@mail.com")
        public void should_ReturnErrorMessage_When_UserHasAuthorityAndIdIsNotPresent() throws Exception{
            var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/items/update/44")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "name": "item2",
                            "article": 20
                        }
                        """);
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                        {
                            "message": "Товар с id 44 не найден"
                        }
                        """));
        }

        @Test
        @WithUserDetails("user@mail.com")
        public void should_Return403_When_UserHasNoAuthority() throws Exception{
            var requestBuilder = MockMvcRequestBuilders.post("/api/v1/items/update/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "name": "item2",
                            "article": 2
                        }
                        """);
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class DeleteTest{
        @Test
        @WithUserDetails("super@mail.com")
        public void should_DeleteItemAndReturnResponse_When_UserHasAuthorityAndIdIsPresent() throws Exception{
            var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/items/delete/1");
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(content().string("Удален товар 22222"));
            assertEquals(0, itemRepository.count());
        }

        @Test
        @WithUserDetails("super@mail.com")
        public void should_ReturnErrorMessage_When_UserHasAuthorityAndIdIsNotPresent() throws Exception{
            var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/items/delete/44");
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                        {
                            "message": "Товар с id 44 не найден"
                        }
                        """));
            assertEquals(1, itemRepository.count());
        }

        @Test
        @WithUserDetails("admin@mail.com")
        public void should_Return403_When_UserHasNoAuthority() throws Exception{
            var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/items/delete/1");
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isForbidden());

            assertEquals(1, itemRepository.count());
        }
    }

}