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

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AdminControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Nested
    class FindAllTest {

        @Test
        @WithUserDetails("super@mail.com")
        public void should_ReturnListOfAdmins_When_AdminHasAuthority() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get("/api/v1/admins");
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                            [
                                {
                                    "firstName":"Admin",
                                    "lastName":"Adminov",
                                    "email":"admin@mail.com",
                                    "authorities": [
                                        "BROWS",
                                        "CREATE"
                                        ]
                                },
                                {
                                    "firstName":"Super",
                                    "lastName":"Adminov",
                                    "email":"super@mail.com",
                                    "authorities": [
                                        "UPDATE",
                                        "DELETE",
                                        "BROWS",
                                        "CREATE",
                                        "SUPER_ADMIN"
                                        ]
                                }
                            ]
                            """));
        }

        @Test
        @WithUserDetails("admin@mail.com")
        public void should_Return403_When_AdminHasNoAuthority() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get("/api/v1/admins");
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class AppointTest {
        @Test
        @WithUserDetails("admin@mail.com")
        public void should_Return403_When_AdminHasNoAuthority() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.post("/api/v1/admins/appoint");
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithUserDetails("super@mail.com")
        public void should_AppointUserAndReturnResponseDTO_When_AdminHasAuthorityAndEmailIsPresent() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.post("/api/v1/admins/appoint")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "firstName": null,
                                    "lastName": null,
                                    "email": "admin@mail.com",
                                    "authorities": [
                                        "CREATE",
                                        "BROWS",
                                        "DELETE"
                                    ]
                                }
                            """);
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                            {
                                "firstName": "Admin",
                                "lastName": "Adminov",
                                "email": "admin@mail.com",
                                "authorities": [
                                    "CREATE",
                                    "BROWS",
                                    "DELETE"
                                ]
                            }
                            """));
        }

        @Test
        @WithUserDetails("super@mail.com")
        public void should_ReturnErrorMessage_When_UserHasAuthorityAndIdEmailNotPresent() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.post("/api/v1/admins/appoint")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "firstName": null,
                                    "lastName": null,
                                    "email": "gambit@mail.com",
                                    "authorities": [
                                        "CREATE"
                                    ]
                                }
                            """);
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                            {
                                "message": "Пользователь gambit@mail.com не найден!"
                            }
                            """));
        }
    }

    @Nested
    class RemoveTest {
        @Test
        @WithUserDetails("admin@mail.com")
        public void should_Return403_When_AdminHasNoAuthority() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.post("/api/v1/admins/remove");
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithUserDetails("super@mail.com")
        public void should_RemoveAdminAndReturnResponseDTO_When_AdminHasAuthorityAndEmailIsPresent() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.post("/api/v1/admins/remove")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("admin@mail.com");
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                            {
                                "firstName": "Admin",
                                "lastName": "Adminov",
                                "email": "admin@mail.com",
                                "authorities": [
                                    "BROWS"
                                ]
                            }
                            """));
        }

        @Test
        @WithUserDetails("super@mail.com")
        public void should_ReturnErrorMessage_When_AdminHasAuthorityAndIdEmailNotPresent() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.post("/api/v1/admins/remove")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("gambit@mail.com");
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                            {
                                "message": "Пользователь gambit@mail.com не найден!"
                            }
                            """));
        }
    }

    @Nested
    class UpdateTest {
        @Test
        @WithUserDetails("admin@mail.com")
        public void should_Return403_When_AdminHasNoAuthority() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.post("/api/v1/admins/update");
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithUserDetails("super@mail.com")
        public void should_UpdateAdminAndReturnResponseDTO_When_AdminHasAuthorityAndEmailIsPresent() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.post("/api/v1/admins/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "firstName": "Test",
                                    "lastName": "Testov",
                                    "email": "admin@mail.com",
                                    "password": "pass",
                                    "authorities": [
                                        "DELETE"
                                    ]
                                }
                            """);
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                            {
                                "firstName": "Test",
                                "lastName": "Testov",
                                "email": "admin@mail.com",
                                "authorities": [
                                    "DELETE"
                                ]
                            }
                            """));
        }

        @Test
        @WithUserDetails("super@mail.com")
        public void should_ReturnErrorMessage_When_AdminHasAuthorityAndIdEmailNotPresent() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.post("/api/v1/admins/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "firstName": "Test",
                                    "lastName": "Testov",
                                    "email": "test@mail.com",
                                    "password": "pass",
                                    "authorities": [
                                        "DELETE"
                                    ]
                                }
                            """);
            mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpect(authenticated())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                            {
                                "message": "Пользователь test@mail.com не найден!"
                            }
                            """));
        }
    }

}