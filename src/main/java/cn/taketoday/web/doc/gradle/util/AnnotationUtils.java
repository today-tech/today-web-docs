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

package cn.taketoday.web.doc.gradle.util;

import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.expression.Add;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.Expression;
import com.thoughtworks.qdox.model.expression.FieldRef;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import infra.lang.Constant;
import infra.lang.Nullable;
import infra.util.StringUtils;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 * @since 1.0 2024/1/27 23:20
 */
public abstract class AnnotationUtils {

  @Nullable
  public static JavaAnnotation getAnnotation(Collection<JavaAnnotation> annotations, String type) {
    for (JavaAnnotation annotation : annotations) {
      if (Objects.equals(annotation.getType().getName(), type)) {
        return annotation;
      }
    }
    return null;
  }

  public static JavaAnnotation getAnnotation(Collection<JavaAnnotation> annotations, Class<? extends Annotation> annotationClass) {
    return getAnnotation(annotations, annotationClass.getName());
  }

  @Nullable
  public static JavaAnnotation getAnnotation(JavaAnnotatedElement annotatedElement, String type) {
    return getAnnotation(annotatedElement.getAnnotations(), type);
  }

  @Nullable
  public static JavaAnnotation getAnnotation(JavaAnnotatedElement annotatedElement, Class<?> annotationClass) {
    return getAnnotation(annotatedElement, annotationClass.getName());
  }

  public static boolean isAnnotationPresent(JavaAnnotatedElement annotatedElement, Class<? extends Annotation> annotationClass) {
    return getAnnotation(annotatedElement, annotationClass) != null;
  }

  public static boolean isAnnotationPresent(JavaAnnotatedElement annotatedElement, String annotationClass) {
    return getAnnotation(annotatedElement, annotationClass) != null;
  }

  public static boolean isAnnotationPresent(Collection<JavaAnnotation> annotations, Class<? extends Annotation> annotationClass) {
    return getAnnotation(annotations, annotationClass) != null;
  }

  public static boolean isAnnotationPresent(Collection<JavaAnnotation> annotations, String annotationClass) {
    return getAnnotation(annotations, annotationClass) != null;
  }

  /**
   * resolve the string of {@link Add} which has {@link FieldRef}(to be exact is {@link FieldRef}) children,
   * the value of {@link FieldRef} will be resolved with the real value of it if it is the static final member of any other class
   *
   * @param annotationValue annotationValue
   * @return annotation value
   */
  public static String getValue(AnnotationValue annotationValue) {
    if (annotationValue instanceof Add add) {
      String leftValue = getValue(add.getLeft());
      String rightValue = getValue(add.getRight());
      return removeQuotes(leftValue + rightValue);
    }
    else {
      if (annotationValue instanceof FieldRef fieldRef) {
        JavaField javaField = fieldRef.getField();
        if (javaField != null) {
          return removeQuotes(javaField.getInitializationExpression());
        }
      }
      return Optional.ofNullable(annotationValue)
              .map(Expression::getParameterValue)
              .map(Object::toString)
              .orElse(Constant.BLANK);
    }
  }

  /**
   * Remove single or double quotes in query keywords to avoid sql errors
   *
   * @param str String
   * @return String
   */
  public static String removeQuotes(String str) {
    if (StringUtils.hasText(str)) {
      return str.replaceAll("'", Constant.BLANK).replaceAll("\"", Constant.BLANK);
    }
    else {
      return Constant.BLANK;
    }
  }

  /**
   * Remove double quotes
   *
   * @param str String
   * @return String
   */
  public static String removeDoubleQuotes(String str) {
    if (StringUtils.hasText(str)) {
      return str.replaceAll("\"", Constant.BLANK);
    }
    else {
      return Constant.BLANK;
    }
  }
}
