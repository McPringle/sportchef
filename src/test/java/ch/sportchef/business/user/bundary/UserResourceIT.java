/**
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2015 Marcus Fihlon
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/ <http://www.gnu.org/licenses/>>.
 */
package ch.sportchef.business.user.bundary;

import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.junit.Rule;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.EXPECTATION_FAILED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UserResourceIT {

    @Rule
    public final JAXRSClientProvider provider = buildWithURI("http://localhost:8080/sportchef/api/users");

    @Test
    public void crud() {
        // create
        final String location = createUserWithSuccess();
        final String notFoundLocation = location.substring(0, location.lastIndexOf("/") + 1) + Long.MAX_VALUE;
        createUserWithBadRequest();
        createUserWithExpectationFailed();

        // read
        readOneUserWithSuccess(location);
        readOneUserWithNotFound(notFoundLocation);
        readAllUsers(location); // location of created user to do asserts

        // update
        final JsonObject userToConflict = updateUserWithSuccess(location);
//        updateUserWithConflict(location, userToConflict);
        updateUserWithNotFound(notFoundLocation);

        // delete
        deleteUserWithSuccess(location);
        deleteUserWithNotFound(location);
    }

    private long getUserId(final String location) {
        return Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
    }

    private String createUserWithSuccess() {
        // arrange
        final JsonObject userToCreate = Json.createObjectBuilder()
                .add("firstName", "John")
                .add("lastName", "Doe")
                .add("phone", "+41 79 555 00 01")
                .add("email", "john.doe@sportchef.ch")
                .build();

        // act
        final Response response = this.provider.target().request(MediaType.APPLICATION_JSON).post(Entity.json(userToCreate));

        //assert
        assertThat(response.getStatus(), is(CREATED.getStatusCode()));
        final String location = response.getHeaderString("Location");
        assertThat(location, notNullValue());
        final long userId = getUserId(location);
        assertTrue(userId > 0);

        return location;
    }

    private void createUserWithBadRequest() {
        // arrange
        final JsonObject userToCreate = Json.createObjectBuilder()
                .add("firstName", "")
                .add("lastName", "")
                .add("phone", "")
                .add("email", "")
                .build();

        // act
        final Response response = this.provider.target().request(MediaType.APPLICATION_JSON).post(Entity.json(userToCreate));

        //assert
        assertThat(response.getStatus(), is(BAD_REQUEST.getStatusCode()));
    }

    private void createUserWithExpectationFailed() {
        // arrange
        final JsonObject userToCreate = Json.createObjectBuilder()
                .add("firstName", "John")
                .add("lastName", "Doe")
                .add("phone", "+41 79 555 00 01")
                .add("email", "john.doe@sportchef.ch")
                .build();

        // act
        final Response response = this.provider.target().request(MediaType.APPLICATION_JSON).post(Entity.json(userToCreate));

        //assert
        assertThat(response.getStatus(), is(EXPECTATION_FAILED.getStatusCode()));
        final String expectation = response.getHeaderString("Expectation");
        assertThat(expectation, is("Email address has to be unique"));
    }

    private void readOneUserWithSuccess(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location)
                .request(MediaType.APPLICATION_JSON).get();
        final JsonObject jsonObject = response.readEntity(JsonObject.class);

        // assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertNotNull(jsonObject);
        assertThat(jsonObject.getJsonNumber("userId").longValue(), is(getUserId(location)));
        assertThat(jsonObject.getString("firstName"), is("John"));
        assertThat(jsonObject.getString("lastName"), is("Doe"));
        assertThat(jsonObject.getString("phone"), is("+41 79 555 00 01"));
        assertThat(jsonObject.getString("email"), is("john.doe@sportchef.ch"));
    }

    private void readOneUserWithNotFound(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location)
                .request(MediaType.APPLICATION_JSON).get();
        final JsonObject jsonObject = response.readEntity(JsonObject.class);

        // assert
        assertThat(response.getStatus(), is(NOT_FOUND.getStatusCode()));
        assertNull(jsonObject);
    }

    private void readAllUsers(final String location) {
        // arrange

        // act
        final Response response = this.provider.target()
                .request(MediaType.APPLICATION_JSON).get();
        final JsonArray jsonArray = response.readEntity(JsonArray.class);
        final JsonObject jsonObject = jsonArray.size() > 0 ? jsonArray.getJsonObject(jsonArray.size() - 1) : null;

        // assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertFalse(jsonArray.isEmpty());
        assertNotNull(jsonObject);
        assertThat(jsonObject.getJsonNumber("userId").longValue(), is(getUserId(location)));
        assertThat(jsonObject.getString("firstName"), is("John"));
        assertThat(jsonObject.getString("lastName"), is("Doe"));
        assertThat(jsonObject.getString("phone"), is("+41 79 555 00 01"));
        assertThat(jsonObject.getString("email"), is("john.doe@sportchef.ch"));
    }

    private JsonObject updateUserWithSuccess(final String location) {
        // arrange
        final JsonObject userToUpdate = Json.createObjectBuilder()
                .add("userId", getUserId(location))
                .add("firstName", "Jane")
                .add("lastName", "Doe")
                .add("phone", "+41 79 555 00 01")
                .add("email", "jane.doe@sportchef.ch")
                .build();

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).put(Entity.json(userToUpdate));
        final JsonObject jsonObject = response.readEntity(JsonObject.class);

        //assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(response.getHeaderString("Location"), is(location));
        assertNotNull(jsonObject);
        assertThat(jsonObject.getJsonNumber("userId").longValue(), is(getUserId(location)));
        assertThat(jsonObject.getString("firstName"), is("Jane"));
        assertThat(jsonObject.getString("lastName"), is("Doe"));
        assertThat(jsonObject.getString("phone"), is("+41 79 555 00 01"));
        assertThat(jsonObject.getString("email"), is("jane.doe@sportchef.ch"));

        return userToUpdate;
    }

    private void updateUserWithConflict(final String location, final JsonObject userToUpdate) {
        // arrange

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).put(Entity.json(userToUpdate));

        //assert
        assertThat(response.getStatus(), is(CONFLICT.getStatusCode()));
    }

    private void updateUserWithNotFound(final String location) {
        // arrange
        final JsonObject userToUpdate = Json.createObjectBuilder()
                .add("userId", getUserId(location))
                .add("firstName", "Jane")
                .add("lastName", "Doe")
                .add("phone", "+41 79 555 00 01")
                .add("email", "jane.doe@sportchef.ch")
                .build();

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).put(Entity.json(userToUpdate));

        //assert
        assertThat(response.getStatus(), is(NOT_FOUND.getStatusCode()));
    }

    private void deleteUserWithSuccess(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).delete();

        //assert
        assertThat(response.getStatus(), is(NO_CONTENT.getStatusCode()));
    }

    private void deleteUserWithNotFound(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).delete();

        //assert
        assertThat(response.getStatus(), is(NOT_FOUND.getStatusCode()));
    }

}
