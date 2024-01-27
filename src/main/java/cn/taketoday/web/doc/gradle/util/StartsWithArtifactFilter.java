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

import java.util.Set;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 */
public class StartsWithArtifactFilter implements ArtifactFilter {

  private final static Set<String> PREFIX_SET = Set.of(
          "maven", "asm", "tomcat", "jboss", "undertow", "jackson",
          "micrometer", "sharding", "flexmark", "netty",
          "hibernate-core", "springdoc-openapi", "poi", "commons-io",
          "commons-lang", "commons-logging", "jaxb", "byte-buddy",
          "rxjava", "kotlin", "checker-qual", "nacos", "junit",
          "caffeine", "lettuce-core", "json", "elasticsearch", "guava",
          "fastjson", "bcprov", "aws-java-sdk", "hadoop", "xml", "sundr-codegen"
  );

  @Override
  public boolean ignoreArtifact(CustomArtifact artifact) {
    String artifactId = artifact.getArtifactId();
    return PREFIX_SET.stream().anyMatch(artifactId::startsWith);
  }

}
