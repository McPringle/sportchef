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
package ch.sportchef.business.configuration.entity;

import javax.validation.constraints.NotNull;
import java.util.Properties;

public class Configuration {

    private final Properties properties;

    public Configuration(@NotNull final Properties properties) {
        this.properties = properties;
    }

    public String getContactCompany() {
        return this.properties.getProperty("contact.company", null);
    }

    public String getContactName() {
        return this.properties.getProperty("contact.name", null);
    }

    public String getContactStreet() {
        return this.properties.getProperty("contact.street", null);
    }

    public String getContactCity() {
        return this.properties.getProperty("contact.city", null);
    }

    public String getContactPhone() {
        return this.properties.getProperty("contact.phone", null);
    }

    public String getContactEmail() {
        return this.properties.getProperty("contact.email", null);
    }

    public String getContactWeb() {
        return this.properties.getProperty("contact.web", null);
    }
}
