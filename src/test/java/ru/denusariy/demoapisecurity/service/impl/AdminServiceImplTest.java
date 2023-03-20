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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.denusariy.demoapisecurity.domain.dto.request.AdminRequest;
import ru.denusariy.demoapisecurity.domain.dto.response.UserResponse;
import ru.denusariy.demoapisecurity.domain.entity.User;
import ru.denusariy.demoapisecurity.domain.enums.Authority;
import ru.denusariy.demoapisecurity.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @InjectMocks
    private AdminServiceImpl adminService;
    @Mock
    private UserRepository userRepositoryMock;
    @Spy
    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    class ConvertToResponseDTOTest{

        @Test
        void should_CompareUserToUserResponseDTO() {
            //given
            User user = new User(1, "User", "Userov", "user@gmail.com", "12345",
                    Set.of(Authority.BROWS));
            UserResponse expected = new UserResponse("User", "Userov", "user@gmail.com",
                    Set.of(Authority.BROWS));
            //when
            UserResponse actual = adminService.convertToResponseDTO(user);
            //then
            assertAll(
                    () -> assertEquals(expected.getFirstName(), actual.getFirstName()),
                    () -> assertEquals(expected.getLastName(), actual.getLastName()),
                    () -> assertEquals(expected.getEmail(), actual.getEmail()),
                    () -> assertEquals(expected.getAuthorities(), actual.getAuthorities())
            );
        }
    }

    @Nested
    class FindAllTest{

        @Test
        void should_ReturnAllAdmins_When_ListIsNotEmpty() {
            //given
            User admin = new User(1, "Admin", "Adminov", "admin@gmail.com", "123456",
                    Set.of(Authority.BROWS, Authority.CREATE));
            UserResponse result = new UserResponse("Admin", "Adminov", "admin@gmail.com",
                    Set.of(Authority.BROWS, Authority.CREATE));
            List<UserResponse> expected = List.of(result);
            when(userRepositoryMock.findByAuthoritiesEquals(Authority.CREATE)).thenReturn(List.of(admin));
            //when
            List<UserResponse> actual = adminService.findAll();
            //then
            assertAll(
                    () -> assertEquals(expected.get(0).getFirstName(), actual.get(0).getFirstName()),
                    () -> assertEquals(expected.get(0).getLastName(), actual.get(0).getLastName()),
                    () -> assertEquals(expected.get(0).getEmail(), actual.get(0).getEmail()),
                    () -> actual.get(0).getAuthorities().equals(Authority.CREATE)
            );
        }

        @Test
        void should_ReturnAllAdmins_When_ListIsEmpty() {
            //given
            List<UserResponse> expected = Collections.emptyList();
            when(userRepositoryMock.findByAuthoritiesEquals(Authority.CREATE)).thenReturn(Collections.emptyList());
            //when
            List<UserResponse> actual = adminService.findAll();
            //then
            assertEquals(expected, actual);
        }
    }

    @Nested
    class UpdateAdminTest{

        @Test
        void should_ThrowException_When_EmailIsNotPresent() {
            //given
            AdminRequest updatedAdmin = new AdminRequest("Admin", "Adminov", "admin@gmail.com",
                    "123456", Set.of(Authority.CREATE));
            when(userRepositoryMock.findByEmail(any())).thenThrow(UsernameNotFoundException.class);
            //when
            Executable executable = () -> adminService.updateAdmin(updatedAdmin);
            //then
            assertThrows(UsernameNotFoundException.class, executable);

        }
        @Test
        void should_UpdateAdminAndReturnDTO_When_EmailIsPresent() {
            //given
            AdminRequest updatedAdmin = new AdminRequest("Admin2", "Adminov2", "admin@gmail.com",
                    "1234", Set.of(Authority.CREATE));
            User admin = new User(1, "Admin", "Adminov", "admin@gmail.com", "123456",
                    Set.of(Authority.BROWS, Authority.CREATE));
            UserResponse expected = new UserResponse("Admin2", "Adminov2", "admin@gmail.com",
                    Set.of(Authority.CREATE));
            when(userRepositoryMock.findByEmail(anyString())).thenReturn(Optional.of(admin));
            //when
            UserResponse actual = adminService.updateAdmin(updatedAdmin);
            //then
            assertAll(
                    () -> assertEquals(expected.getFirstName(), actual.getFirstName()),
                    () -> assertEquals(expected.getLastName(), actual.getLastName()),
                    () -> assertEquals(expected.getEmail(), actual.getEmail()),
                    () -> assertEquals(expected.getAuthorities(), actual.getAuthorities()),
                    () -> verify(userRepositoryMock).save(any())
            );
        }
    }

    @Nested
    class AppointAdminTest{

        @Test
        void should_ThrowException_When_EmailIsNotPresent() {
            AdminRequest updatedAdmin = new AdminRequest("Admin", "Adminov", "admin@gmail.com",
                    "123456", Set.of(Authority.CREATE));
            when(userRepositoryMock.findByEmail(any())).thenThrow(UsernameNotFoundException.class);
            //when
            Executable executable = () -> adminService.appointAdmin(updatedAdmin);
            //then
            assertThrows(UsernameNotFoundException.class, executable);
        }

        @Test
        void should_AppointAdminAndReturnDTO_When_EmailIsPresent() {
            //given
            AdminRequest updatedAdmin = new AdminRequest(null, null, "user@gmail.com",
                    "1234", Set.of(Authority.BROWS, Authority.CREATE, Authority.UPDATE));
            User admin = new User(1, "User", "Userov", "user@gmail.com", "1234",
                    Set.of(Authority.BROWS));
            UserResponse expected = new UserResponse("User", "Userov", "user@gmail.com",
                    Set.of(Authority.BROWS, Authority.CREATE, Authority.UPDATE));
            when(userRepositoryMock.findByEmail(anyString())).thenReturn(Optional.of(admin));
            //when
            UserResponse actual = adminService.appointAdmin(updatedAdmin);
            //then
            assertAll(
                    () -> assertEquals(expected.getFirstName(), actual.getFirstName()),
                    () -> assertEquals(expected.getLastName(), actual.getLastName()),
                    () -> assertEquals(expected.getEmail(), actual.getEmail()),
                    () -> assertEquals(expected.getAuthorities(), actual.getAuthorities()),
                    () -> verify(userRepositoryMock).save(any())
            );
        }
    }

    @Nested
    class RemoveAdminTest{

        @Test
        void should_ThrowException_When_EmailIsNotPresent() {
            when(userRepositoryMock.findByEmail(any())).thenThrow(UsernameNotFoundException.class);
            //when
            Executable executable = () -> adminService.removeAdmin(anyString());
            //then
            assertThrows(UsernameNotFoundException.class, executable);
        }

        @Test
        void should_ChangeAdminToUserAndReturnDTO_WhenEmailIsPresent() {
            //given
            User admin = new User(1, "Admin", "Adminov", "admin@gmail.com", "1234",
                    Set.of(Authority.BROWS, Authority.CREATE, Authority.UPDATE));
            UserResponse expected = new UserResponse("Admin", "Adminov", "admin@gmail.com",
                    Set.of(Authority.BROWS));
            when(userRepositoryMock.findByEmail("admin@gmail.com")).thenReturn(Optional.of(admin));
            //when
            UserResponse actual = adminService.removeAdmin("admin@gmail.com");
            //then
            assertAll(
                    () -> assertEquals(expected.getFirstName(), actual.getFirstName()),
                    () -> assertEquals(expected.getLastName(), actual.getLastName()),
                    () -> assertEquals(expected.getEmail(), actual.getEmail()),
                    () -> assertEquals(expected.getAuthorities(), actual.getAuthorities()),
                    () -> verify(userRepositoryMock).save(any())
            );
        }
    }
}