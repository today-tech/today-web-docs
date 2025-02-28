/*
 * Copyright 2024 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [https://www.gnu.org/licenses/]
 */

package cn.taketoday.demo;

import java.util.Objects;

import infra.core.style.ToStringBuilder;

/**
 * User model
 *
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 * @since 1.0 2024/1/27 22:08
 */
public class User {

  /**
   * user id
   */
  private Long id;

  /**
   * user name
   */
  private String name;

  /**
   * user login name
   */
  private String username;

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof User user))
      return false;
    return Objects.equals(id, user.id)
            && Objects.equals(name, user.name)
            && Objects.equals(username, user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, username);
  }

  @Override
  public String toString() {
    return ToStringBuilder.forInstance(this)
            .append("id", id)
            .append("name", name)
            .append("username", username)
            .toString();
  }
}
