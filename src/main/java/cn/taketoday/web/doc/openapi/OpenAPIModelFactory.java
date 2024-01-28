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
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

import cn.taketoday.core.Pair;
import cn.taketoday.http.HttpMethod;
import cn.taketoday.lang.Assert;
import cn.taketoday.lang.Nullable;
import cn.taketoday.util.ClassUtils;
import cn.taketoday.web.annotation.DELETE;
import cn.taketoday.web.annotation.DeleteMapping;
import cn.taketoday.web.annotation.GET;
import cn.taketoday.web.annotation.GetMapping;
import cn.taketoday.web.annotation.PATCH;
import cn.taketoday.web.annotation.POST;
import cn.taketoday.web.annotation.PUT;
import cn.taketoday.web.annotation.PatchMapping;
import cn.taketoday.web.annotation.PostMapping;
import cn.taketoday.web.annotation.PutMapping;
import cn.taketoday.web.doc.DocConfig;
import cn.taketoday.web.doc.MappingAnnotation;
import cn.taketoday.web.doc.gradle.util.AnnotationUtils;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 * @since 1.0 2024/1/27 21:02
 */
public class OpenAPIModelFactory {

  static final String RestController = "cn.taketoday.web.annotation.RestController";

  static final String RequestMapping = "cn.taketoday.web.annotation.RequestMapping";

  public OpenAPI createOpenAPI(DocConfig docConfig, JavaProjectBuilder projectBuilder) {
    OpenAPI openAPI = new OpenAPI();
    Collection<JavaClass> classes = projectBuilder.getClasses();
    Paths paths = new Paths();

    for (JavaClass javaClass : classes) {
      Paths current = processClass(javaClass);
      if (current != null) {
        paths.putAll(current);
      }
    }
    openAPI.setPaths(paths);
    return openAPI;
  }

  @Nullable
  private Paths processClass(JavaClass javaClass) {
    if (isEndpoint(javaClass)) {
      Paths paths = new Paths();
      List<MappingAnnotation> mappings = getMappingAnnotations();

      List<JavaMethod> methods = javaClass.getMethods();
      for (JavaMethod method : methods) {
        Pair<String, PathItem> pair = processMethod(mappings, javaClass, method);
        if (pair != null) {
          paths.addPathItem(pair.first, pair.second);
        }
      }
      return paths;
    }
    return null;
  }

  @Nullable
  private Pair<String, PathItem> processMethod(List<MappingAnnotation> mappings, JavaClass declaringClass, JavaMethod method) {
    List<JavaAnnotation> annotations = method.getAnnotations();
    PathItem pathItem = new PathItem();

    for (MappingAnnotation mapping : mappings) {
      JavaAnnotation annotation = AnnotationUtils.getAnnotation(method, mapping.annotationName);
      if (annotation != null) {
        HttpMethod httpMethod = HttpMethod.resolve(getHttpMethod(mapping, annotation));
        Assert.state(httpMethod != null, "HttpMethod not supported");
        Operation operation = new Operation();
        for (JavaParameter param : method.getParameters()) {
          Parameter parameter = getParameter(method, param);
          if (parameter != null) {
            operation.addParametersItem(parameter);
          }
        }
        switch (httpMethod) {
          case GET -> pathItem.setGet(operation);
          case POST -> { }
          case PUT -> { }
          case DELETE -> { }
          case PATCH -> { }
          case TRACE -> { }
          case HEAD -> { }
          case OPTIONS -> { }
        }

      }
    }

    return null;
  }

  private final BiPredicate<JavaMethod, JavaParameter> argumentFilter = new BiPredicate<JavaMethod, JavaParameter>() {

    @Override
    public boolean test(JavaMethod method, JavaParameter parameter) {
      return true;
    }
  };

  @Nullable
  private Parameter getParameter(JavaMethod method, JavaParameter argument) {
    if (argumentFilter.test(method, argument)) {
      Parameter parameter = new Parameter();
      parameter.setName(argument.getName());
      parameter.setDescription(argument.getComment());
      parameter.setDeprecated(AnnotationUtils.isAnnotationPresent(argument, Deprecated.class));

      parameter.setRequired(AnnotationUtils.isAnnotationPresent(argument, Nullable.class));

      for (JavaAnnotation annotation : argument.getAnnotations()) {
        String simpleName = ClassUtils.getSimpleName(annotation.getType().getName());
        if (Objects.equals(simpleName, Nullable.class.getSimpleName())) {

        }
      }

      return parameter;
    }
    return null;
  }

  private String getHttpMethod(MappingAnnotation mapping, JavaAnnotation annotation) {
    String httpMethod;
    if (mapping.method == null) {
      AnnotationValue annotationValue = annotation.getProperty(mapping.methodAttr);
      httpMethod = AnnotationUtils.getValue(annotationValue);
    }
    else {
      httpMethod = mapping.method;
    }
    return httpMethod;
  }

  private List<MappingAnnotation> getMappingAnnotations() {
    List<MappingAnnotation> mappings = new ArrayList<>();
    MappingAnnotation prototype = new MappingAnnotation(RequestMapping, List.of("value", "path"),
            "produces", "consumes", "method", null, "params");

    mappings.add(prototype);
    mappings.add(prototype.withAnnotation(GET.class));
    mappings.add(prototype.withAnnotation(GetMapping.class));

    mappings.add(prototype.withAnnotation(POST.class));
    mappings.add(prototype.withAnnotation(PostMapping.class));

    mappings.add(prototype.withAnnotation(PUT.class));
    mappings.add(prototype.withAnnotation(PutMapping.class));

    mappings.add(prototype.withAnnotation(PATCH.class));
    mappings.add(prototype.withAnnotation(PatchMapping.class));

    mappings.add(prototype.withAnnotation(DELETE.class));
    mappings.add(prototype.withAnnotation(DeleteMapping.class));
    return mappings;
  }

  private boolean isEndpoint(JavaClass javaClass) {
    return AnnotationUtils.isAnnotationPresent(javaClass, RestController);
  }

  static class D {

  }
}
