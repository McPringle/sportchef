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
package ch.sportchef.hamcrest.matcher;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import javax.validation.constraints.NotNull;

@ToString
@AllArgsConstructor
public class PatternMatcher extends TypeSafeMatcher<String> {

    private final String pattern;

    public static PatternMatcher matchesPattern(@NotNull final String pattern) {
        return new PatternMatcher(pattern);
    }

    @Override
    protected boolean matchesSafely(@NotNull final  String item) {
        return item.matches(pattern);
    }

    @Override
    public void describeTo(@NotNull final Description description) {
        description.appendText(String.format("matches pattern = '%s'", pattern)); //NON-NLS
    }

}
