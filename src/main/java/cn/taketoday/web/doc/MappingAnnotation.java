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

package cn.taketoday.web.doc;

import java.lang.annotation.Annotation;
import java.util.List;

import cn.taketoday.lang.Nullable;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 * @see cn.taketoday.web.annotation.RequestMapping
 * @see cn.taketoday.web.annotation.GET
 * @see cn.taketoday.web.annotation.POST
 * @see cn.taketoday.web.annotation.PUT
 * @see cn.taketoday.web.annotation.PATCH
 * @see cn.taketoday.web.annotation.DELETE
 * @since 1.0 2024/1/28 12:41
 */
public class MappingAnnotation {

  public final String annotationName;

  public final List<String> pathAttr;

  public final String producesAttr;

  public final String consumesAttr;

  public final String methodAttr;

  @Nullable
  public final String method;

  public final String paramsAttr;

  public MappingAnnotation(String annotationName, List<String> pathAttr,
          String producesAttr, String consumesAttr, String methodAttr, @Nullable String method, String paramsAttr) {
    this.annotationName = annotationName;
    this.pathAttr = pathAttr;
    this.producesAttr = producesAttr;
    this.consumesAttr = consumesAttr;
    this.methodAttr = methodAttr;
    this.method = method;
    this.paramsAttr = paramsAttr;
  }

  public MappingAnnotation withAnnotation(String annotationName) {
    return new MappingAnnotation(annotationName, pathAttr, producesAttr, consumesAttr, methodAttr, method, paramsAttr);
  }

  public MappingAnnotation withAnnotation(Class<? extends Annotation> type) {
    return new MappingAnnotation(type.getName(), pathAttr, producesAttr, consumesAttr, methodAttr, method, paramsAttr);
  }

  public MappingAnnotation withPath(List<String> pathAttr) {
    return new MappingAnnotation(annotationName, pathAttr, producesAttr, consumesAttr, methodAttr, method, paramsAttr);
  }

  public MappingAnnotation withProduces(String producesProp) {
    return new MappingAnnotation(annotationName, pathAttr, producesProp, consumesAttr, methodAttr, method, paramsAttr);
  }

  public MappingAnnotation withConsumes(String consumesProp) {
    return new MappingAnnotation(annotationName, pathAttr, producesAttr, consumesProp, methodAttr, method, paramsAttr);
  }

  public MappingAnnotation withMethod(@Nullable String method) {
    return new MappingAnnotation(annotationName, pathAttr, producesAttr, consumesAttr, methodAttr, method, paramsAttr);
  }

}
