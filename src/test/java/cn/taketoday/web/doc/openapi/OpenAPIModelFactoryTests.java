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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import cn.taketoday.core.io.ClassPathResource;
import cn.taketoday.lang.Constant;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 * @since 1.0 2024/1/27 21:37
 */
class OpenAPIModelFactoryTests {

  private JavaProjectBuilder createJavaProjectBuilder() {
    SortedClassLibraryBuilder classLibraryBuilder = new SortedClassLibraryBuilder();
    JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder(classLibraryBuilder);
    javaDocBuilder.setEncoding(Constant.DEFAULT_ENCODING);
    javaDocBuilder.addSourceTree(new File("src/test/java/cn/taketoday/demo"));

    return javaDocBuilder;
  }


  @Test
  void buildOpenApi() throws IOException {
    ClassPathResource resource = new ClassPathResource("web-doc/default.json");
    JavaProjectBuilder projectBuilder = createJavaProjectBuilder();
    Collection<JavaClass> classes = projectBuilder.getClasses();
    ObjectMapper objectMapper = new ObjectMapper();
//    OpenApiBuilder.buildOpenApi(apiConfig, createJavaProjectBuilder());
  }
}