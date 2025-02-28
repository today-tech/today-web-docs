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

import org.gradle.internal.impldep.org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

import cn.taketoday.web.doc.DocConfig;
import cn.taketoday.web.doc.MappingAnnotation;
import cn.taketoday.web.doc.gradle.util.AnnotationUtils;
import infra.core.Pair;
import infra.http.HttpMethod;
import infra.lang.Assert;
import infra.lang.Nullable;
import infra.util.StringUtils;
import infra.web.annotation.DELETE;
import infra.web.annotation.DeleteMapping;
import infra.web.annotation.GET;
import infra.web.annotation.GetMapping;
import infra.web.annotation.PATCH;
import infra.web.annotation.POST;
import infra.web.annotation.PUT;
import infra.web.annotation.PatchMapping;
import infra.web.annotation.PostMapping;
import infra.web.annotation.PutMapping;
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

  private Pair<String, PathItem> processMethod(List<MappingAnnotation> mappings, JavaClass declaringClass, JavaMethod method) {
    PathItem pathItem = new PathItem();

    for (MappingAnnotation mapping : mappings) {
      JavaAnnotation annotation = AnnotationUtils.getAnnotation(method, mapping.annotationName);
      if (annotation != null) {
        Operation operation = new Operation();
        for (JavaParameter param : method.getParameters()) {
          Parameter parameter = getParameter(method, param);
          if (parameter != null) {
            operation.addParametersItem(parameter);
          }
        }

        HttpMethod httpMethod = getHttpMethod(mapping, annotation);
        switch (httpMethod) {
          case GET -> pathItem.setGet(operation);
          case PUT -> pathItem.setPut(operation);
          case POST -> pathItem.setPost(operation);
          case PATCH -> pathItem.setPatch(operation);
          case DELETE -> pathItem.setDelete(operation);
          case HEAD -> pathItem.setHead(operation);
          case TRACE -> pathItem.setTrace(operation);
          case OPTIONS -> pathItem.setOptions(operation);
        }

        List<String> paths = new ArrayList<>();
        for (String pathAttr : mapping.pathAttr) {
          AnnotationValue annotationValue = annotation.getProperty(pathAttr);
          if (annotationValue != null) {
            String path = AnnotationUtils.getValue(annotationValue);
            if (StringUtils.hasText(path)) {
              paths.add(path);
            }
          }
        }

        if (!paths.isEmpty()) {
          return Pair.of(paths.get(0), pathItem);
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

  private HttpMethod getHttpMethod(MappingAnnotation mapping, JavaAnnotation annotation) {
    HttpMethod httpMethod;
    if (mapping.method == null) {
      AnnotationValue annotationValue = annotation.getProperty(mapping.methodAttr);
      httpMethod = HttpMethod.resolve(AnnotationUtils.getValue(annotationValue));
      Assert.state(httpMethod != null, "HttpMethod not supported");
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
    mappings.add(prototype.withAnnotation(GET.class).withMethod(HttpMethod.GET));
    mappings.add(prototype.withAnnotation(GetMapping.class).withMethod(HttpMethod.GET));

    mappings.add(prototype.withAnnotation(POST.class).withMethod(HttpMethod.POST));
    mappings.add(prototype.withAnnotation(PostMapping.class).withMethod(HttpMethod.POST));

    mappings.add(prototype.withAnnotation(PUT.class).withMethod(HttpMethod.PUT));
    mappings.add(prototype.withAnnotation(PutMapping.class).withMethod(HttpMethod.PUT));

    mappings.add(prototype.withAnnotation(PATCH.class).withMethod(HttpMethod.PATCH));
    mappings.add(prototype.withAnnotation(PatchMapping.class).withMethod(HttpMethod.PATCH));

    mappings.add(prototype.withAnnotation(DELETE.class).withMethod(HttpMethod.DELETE));
    mappings.add(prototype.withAnnotation(DeleteMapping.class).withMethod(HttpMethod.DELETE));
    return mappings;
  }

  private boolean isEndpoint(JavaClass javaClass) {
    return AnnotationUtils.isAnnotationPresent(javaClass, RestController);
  }

  static class D {

  }
}
