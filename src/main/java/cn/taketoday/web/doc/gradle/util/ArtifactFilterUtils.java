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

import java.util.List;

/**
 * Artifact filter util
 *
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 */
public abstract class ArtifactFilterUtils {

  static List<ArtifactFilter> artifactFilters = List.of(
          new GroupIdArtifactFilter(),
          new StartsWithArtifactFilter(),
          new ContainsArtifactFilter(),
          new CommonArtifactFilter(),
          new InfraArtifactFilter()
  );

  /**
   * ignoreArtifact
   *
   * @param artifact Artifact
   * @return boolean
   */
  public static boolean ignoreArtifact(CustomArtifact artifact) {
    for (ArtifactFilter artifactFilter : artifactFilters) {
      if (artifactFilter.ignoreArtifact(artifact)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Ignore Infra Artifact
   *
   * @param artifact Artifact
   * @return boolean
   */
  public static boolean ignoreInfraArtifactById(CustomArtifact artifact) {
    ArtifactFilter infraArtifactArtifactFilter = new InfraArtifactFilter();
    return infraArtifactArtifactFilter.ignoreArtifact(artifact);
  }
}
