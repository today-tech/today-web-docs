/*
 * Copyright 2017 - 2024 the original author or authors.
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

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 */
public class ContainsArtifactFilter implements ArtifactFilter {

  private final static Set<String> CONTAINS_SET = new HashSet<>();

  static {
    CONTAINS_SET.add("log4j");
    CONTAINS_SET.add("logback");
    CONTAINS_SET.add("slf4j");
    CONTAINS_SET.add("swagger");
    CONTAINS_SET.add("dom4j");
    CONTAINS_SET.add("jsr");
    CONTAINS_SET.add("jtds");
  }

  @Override
  public boolean ignoreArtifact(CustomArtifact artifact) {
    String artifactId = artifact.getArtifactId();
    return CONTAINS_SET.stream().anyMatch(artifactId::contains);
  }
}
