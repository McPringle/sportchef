/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016 Marcus Fihlon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.business.user.boundary;

import ch.sportchef.business.exception.ExpectationFailedException;
import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class UsersResourceTest {

    private User createJohnDoe(@NotNull final Long userId) {
        return User.builder()
                .userId(userId)
                .firstName("John")
                .lastName("Doe")
                .phone("+41 79 555 00 01")
                .email("john.doe@sportchef.ch")
                .build();
    }

    private User createJaneDoe(@NotNull final Long userId) {
        return User.builder()
                .userId(userId)
                .firstName("Jane")
                .lastName("Doe")
                .phone("+41 79 555 00 02")
                .email("jane.doe@sportchef.ch")
                .build();
    }

    @Test
    public void saveWithSuccess() throws URISyntaxException {
        // arrange
        final User userToCreate = createJohnDoe(0L);
        final User savedUser = createJohnDoe(1L);
        final String location = "http://localhost:8080/sportchef/api/users/1";
        final UriInfo uriInfoMock = mock(UriInfo.class);
        final UriBuilder uriBuilderMock = mock(UriBuilder.class);
        final UserService userServiceMock = mock(UserService.class);
        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
        final URI uri = new URI(location);
        when(uriBuilderMock.build()).thenReturn(uri);

        when(userServiceMock.create(userToCreate)).thenReturn(savedUser);
        final UsersResource usersResource = new UsersResource(userServiceMock);

        // act
        final Response response = usersResource.save(userToCreate, uriInfoMock);

        //assert
        assertThat(response.getStatus(), is(CREATED.getStatusCode()));
        assertThat(response.getHeaderString("Location"), is(location));
        verify(userServiceMock, times(1)).create(userToCreate);
        verify(uriInfoMock, times(1)).getAbsolutePathBuilder();
        verify(uriBuilderMock, times(1)).path(anyString());
        verify(uriBuilderMock, times(1)).build();
    }

    @Test
    public void saveWithExpectationFailed() {
        // arrange
        final User userToCreate = createJohnDoe(0L);
        final UserService userServiceMock = mock(UserService.class);
        doThrow(new ExpectationFailedException("Email address has to be unique"))
                .when(userServiceMock).create(userToCreate);
        final UsersResource usersResource = new UsersResource(userServiceMock);

        // act & assert
        assertThrows(ExpectationFailedException.class,
                () -> usersResource.save(userToCreate, null));
    }

    @Test
    public void findAll() {
        // arrange
        final User user1 = createJohnDoe(1L);
        final User user2 = createJaneDoe(2L);
        final List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        final UserService userServiceMock = mock(UserService.class);
        when(userServiceMock.findAll())
                .thenReturn(users);
        final UsersResource usersResource = new UsersResource(userServiceMock);

        // act
        final Response response = usersResource.findAll();
        final List<User> list = (List<User>) response.getEntity();
        final User responseUser1 = list.get(0);
        final User responseUser2 = list.get(1);

        // assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(responseUser1, is(user1));
        assertThat(responseUser2, is(user2));
        verify(userServiceMock, times(1)).findAll();
    }

    @Test
    public void find() {
        // arrange
        final long userId = 1L;
        final UserService userServiceMock = mock(UserService.class);
        final UsersResource usersResource = new UsersResource(userServiceMock);

        // act
        final UserResource userResource = usersResource.find(userId);

        // assert
        assertThat(userResource, notNullValue());
    }
}
