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
public class CommonArtifactFilter implements ArtifactFilter {

  static final Set<String> CommonArtifacts = Set.of("jsqlparser", "disruptor", "snakeyaml", "HikariCP",
          "mysql-connector-java", "mysql-connector-j", "classmate", "commons-codec",
          "commons-beanutils", "commons-beanutils-core", "today-web",
          "today-orm", "today-aspects", "hibernate-validator", "xstream", "today-tx",
          "javassist", "javafaker", "qdox", "gson", "antlr4-runtime", "velocity",
          "beetl", "xml-apis", "mchange-commons-java", "druid", "mssql-jdbc", "easyexcel",
          "zookeeper", "okio", "okhttp", "joda-time", "protobuf-java", "jenkins-client",
          "jose4j", "gson-fire", "joda-convert", "kafka-clients", "kubernetes-client",
          "client-java-proto", "java-driver-core", "java-driver-query-builder", "java-driver-mapper-runtime");

  @Override
  public boolean ignoreArtifact(CustomArtifact artifact) {
    String artifactId = artifact.getArtifactId();
    return CommonArtifacts.contains(artifactId);
  }
}
