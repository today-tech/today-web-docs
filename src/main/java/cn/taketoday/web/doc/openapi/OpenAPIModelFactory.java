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

package cn.taketoday.web.doc.openapi;

import com.thoughtworks.qdox.JavaProjectBuilder;

import cn.taketoday.web.doc.DocConfig;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 * @since 1.0 2024/1/27 21:02
 */
public class OpenAPIModelFactory {

  public OpenAPI createOpenAPI(DocConfig docConfig, JavaProjectBuilder projectBuilder) {
    OpenAPI openAPI = new OpenAPI();



    return openAPI;
  }

}