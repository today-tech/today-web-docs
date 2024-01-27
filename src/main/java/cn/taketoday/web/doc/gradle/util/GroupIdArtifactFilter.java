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
public class GroupIdArtifactFilter implements ArtifactFilter {

  private final static Set<String> GROUPID_SET = Set.of(
          "org.apache.commons", "io.fabric8", "io.kubernetes", "org.jooq",
          "org.mortbay.jetty", "com.google.http-client", "jakarta.xml.bind", "org.mariadb.jdbc",
          "jakarta.transaction", "jakarta.persistence", "javax.servlet", "io.projectreactor",
          "org.mapstruct", "io.sundr", "org.aspectj", "org.slf4j", "com.esotericsoftware.yamlbeans",
          "jakarta.activation", "commons-httpclient", "org.apache.curator", "org.apache.hive", "org.apache.hadoop",
          "org.hibernate", "org.bouncycastle", "io.vavr", "org.projectlombok", "org.freemarker", "com.auth0", "org.apache.logging.log4j",
          "com.google.protobuf", "org.postgresql", "com.microsoft.sqlserver", "io.etcd", "org.apache.flink",
          "org.apache.rocketmq", "org.apache.kafka", "org.apache.hudi", "com.rabbitmq", "org.apache.dubbo", "cn.hutool",
          "com.alibaba.nacos", "com.alibaba.csp", "io.zipkin.zipkin2", "org.apache.skywalking", "com.ctrip.framework.apollo",
          "org.apache.shardingsphere", "ru.yandex.clickhouse", "com.clickhouse", "org.apache.activemq", "org.bytedeco",
          "ws.schild", "io.netty", "io.micrometer", "org.apache.pulsar");

  @Override
  public boolean ignoreArtifact(CustomArtifact artifact) {
    String groupId = artifact.getGroupId();
    return GROUPID_SET.stream().anyMatch(groupId::contains);
  }

}
