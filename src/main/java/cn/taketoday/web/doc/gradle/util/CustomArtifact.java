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

import java.util.Objects;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 */
public class CustomArtifact {

  /**
   * Artifact ID
   */
  private String artifactId;

  /**
   * Artifact Group
   */
  private String groupId;

  /**
   * Artifact Version
   */
  private String version;

  /**
   * Build CustomArtifact
   *
   * @return CustomArtifact
   */
  public static CustomArtifact builder() {
    return new CustomArtifact();
  }

  /**
   * Build CustomArtifact with ArtifactDisplayName
   *
   * @param artifactDisplayName Artifact Display Name
   * @return CustomArtifact
   */
  public static CustomArtifact builder(String artifactDisplayName) {
    CustomArtifact artifact = builder();
    if (Objects.isNull(artifactDisplayName)) {
      return artifact;
    }
    String[] displayInfo = artifactDisplayName.split(":");
    artifact.setArtifactId(displayInfo[1]);
    artifact.setGroup(displayInfo[0]);
    if (displayInfo.length > 2) {
      artifact.setVersion(displayInfo[2]);
    }
    return artifact;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public CustomArtifact setArtifactId(String artifactId) {
    this.artifactId = artifactId;
    return this;
  }

  public String getGroupId() {
    return groupId;
  }

  public CustomArtifact setGroup(String group) {
    this.groupId = group;
    return this;
  }

  public String getVersion() {
    return version;
  }

  public CustomArtifact setVersion(String version) {
    this.version = version;
    return this;
  }
}
