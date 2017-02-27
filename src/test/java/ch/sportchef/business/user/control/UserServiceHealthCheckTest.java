/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016, 2017 Marcus Fihlon
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
package ch.sportchef.business.user.control;

import com.codahale.metrics.health.HealthCheck;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static java.lang.Boolean.FALSE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceHealthCheckTest {

    @Test
    public void checkHealthy() {
        // arrange
        final UserService userServiceMock = mock(UserService.class);
        when(userServiceMock.findAll()).thenReturn(new ArrayList<>(0));
        final UserServiceHealthCheck healthCheck = new UserServiceHealthCheck(userServiceMock);

        // act
        final HealthCheck.Result result = healthCheck.check();

        // assert
        assertThat(result, is(HealthCheck.Result.healthy()));
    }

    @Test
    public void checkUnhealthy() {
        // arrange
        final UserService userServiceMock = mock(UserService.class);
        when(userServiceMock.findAll()).thenReturn(null);
        final UserServiceHealthCheck healthCheck = new UserServiceHealthCheck(userServiceMock);

        // act
        final HealthCheck.Result result = healthCheck.check();

        // assert
        assertThat(result, is(HealthCheck.Result.unhealthy("Can't access users!")));
    }

    @Test
    public void checkException() {
        // arrange
        final UserService userServiceMock = mock(UserService.class);
        when(userServiceMock.findAll()).thenThrow(new RuntimeException("Test Message"));
        final UserServiceHealthCheck healthCheck = new UserServiceHealthCheck(userServiceMock);

        // act
        final HealthCheck.Result result = healthCheck.check();

        // assert
        assertThat(result.isHealthy(), is(FALSE));
        assertThat(result.getMessage(), is("Test Message"));
    }

}
