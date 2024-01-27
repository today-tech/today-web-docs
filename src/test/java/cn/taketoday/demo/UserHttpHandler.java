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

import cn.taketoday.web.annotation.DELETE;
import cn.taketoday.web.annotation.GET;
import cn.taketoday.web.annotation.PATCH;
import cn.taketoday.web.annotation.POST;
import cn.taketoday.web.annotation.PUT;
import cn.taketoday.web.annotation.PathVariable;
import cn.taketoday.web.annotation.RequestBody;
import cn.taketoday.web.annotation.RequestMapping;
import cn.taketoday.web.annotation.RestController;

/**
 * User APIs
 */
@RestController
@RequestMapping("/api/users")
public class UserHttpHandler {

  /**
   * Get user by id
   *
   * @param id user id
   */
  @GET("/{id}")
  public User getById(long id) {
    User user = new User();
    user.setId(id);
    user.setName("name");
    user.setUsername("username");
    return user;
  }

  /**
   * Create user
   *
   * @param body user request body
   */
  @POST
  public void create(@RequestBody User body) {

  }

  /**
   * Update user info
   *
   * @param id user id
   * @param body user request body
   */
  @PUT("/{id}")
  public void update(@PathVariable long id, @RequestBody User body) {
    //
  }

  /**
   * Update user name
   *
   * @param name user name
   * @param id user id
   */
  @PATCH("/{id}")
  public void updateName(@PathVariable long id, String name) {
    //
  }

  /**
   * Delete user by id
   *
   * @param id user id
   */
  @DELETE("/{id}")
  public void deleteById(@PathVariable long id) {
    //
  }

}
