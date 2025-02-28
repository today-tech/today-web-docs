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

import org.gradle.api.Project;
import org.gradle.api.internal.tasks.DefaultSourceSetContainer;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import infra.util.CollectionUtils;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 */
public abstract class SourceSetUtils {

  public static final String SOURCE_SETS = "sourceSets";

  public static final String MAIN = "main";

  public static Set<File> getMainJava(Project project) {
    Object sets = project.getProperties().get(SOURCE_SETS);
    if (!(sets instanceof DefaultSourceSetContainer sourceSets)) {
      return Collections.emptySet();
    }
    Logger log = project.getLogger();

    Set<File> srcDirs = sourceSets.getAt(MAIN).getJava().getSrcDirs();
    if (CollectionUtils.isEmpty(srcDirs)) {
      log.info("Invalid source set configuration, use the default directory: {}/main/java", project.getPath());
      return Collections.emptySet();
    }
    return srcDirs;
  }

  /**
   * try using the default project structure: src/main/java
   */
  public static File getDefaultMainJava(Project project) {
    String projectDir = project.getProjectDir().getPath();
    String projectCodePath = String.join(File.pathSeparator, projectDir, "src/main/java");
    File src = new File(projectCodePath);

    return src.exists() && src.listFiles() != null
            ? src
            : null;
  }

}
