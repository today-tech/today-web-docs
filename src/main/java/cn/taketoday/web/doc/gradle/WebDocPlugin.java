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

package cn.taketoday.web.doc.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;

import cn.taketoday.web.doc.gradle.extension.WebDocPluginExtension;
import cn.taketoday.web.doc.gradle.task.OpenAPITask;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 */
public class WebDocPlugin implements Plugin<Project> {

  /**
   * Task group
   */
  public static final String GROUP = "Documentation";

  /**
   * Plugin extension name
   */
  public static final String EXTENSION_NAME = "webdoc";

  /**
   * Generate OpenAPI document
   */
  public static final String OPEN_API_TASK = "webDocOpenApi";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(JavaPlugin.class);
    Task javaCompileTask = project.getTasks().getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME);

    OpenAPITask task = project.getTasks().create(OPEN_API_TASK, OpenAPITask.class);
    task.setGroup(GROUP);
    task.dependsOn(javaCompileTask);

    // extend project-model to get our settings/configuration via nice configuration
    project.getExtensions().create(EXTENSION_NAME, WebDocPluginExtension.class);
  }

}
