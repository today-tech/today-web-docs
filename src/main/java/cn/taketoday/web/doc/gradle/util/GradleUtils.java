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

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;

import cn.taketoday.web.doc.DocConfig;
import infra.util.FileCopyUtils;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 */
public abstract class GradleUtils {

  /**
   * Gson Object
   */
  public final static Gson GSON = new GsonBuilder()
          .addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
              return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
              return false;
            }
          }).create();

  /**
   * Build ApiConfig
   *
   * @param configFile config file
   * @param project Project object
   * @param log gradle plugin log
   */
  public static DocConfig buildConfig(File configFile, Project project, boolean increment, Logger log) throws IOException {
    ClassLoader classLoader = getRuntimeClassLoader(project);
    byte[] bytes = FileCopyUtils.copyToByteArray(configFile);
    String data = new String(bytes, StandardCharsets.UTF_8);

    return new DocConfig();
  }

  /**
   * Get Class by name
   *
   * @param className class name
   * @param classLoader urls
   * @return Class
   */
  public static Class getClassByClassName(String className, ClassLoader classLoader) {
    try {
      return classLoader.loadClass(className);
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Get classloader
   *
   * @param project MavenProject
   * @return ClassLoader
   */
  public static ClassLoader getRuntimeClassLoader(Project project) {
    try {
      Configuration compileConfiguration = project.getConfigurations().getByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME);
      Set<File> fileSet = compileConfiguration.getFiles();
      ArrayList<URL> urls = new ArrayList<>();
      for (File file : fileSet) {
        urls.add(file.toURI().toURL());
      }
      SourceSetContainer ssc = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
      FileCollection classesDir = ssc.getByName(SourceSet.MAIN_SOURCE_SET_NAME).getOutput().getClassesDirs();
      Set<File> fileSet1 = classesDir.getFiles();
      for (File file : fileSet1) {
        urls.add(file.toURI().toURL());
      }
      URL[] runtimeUrls = urls.toArray(new URL[0]);
      return new URLClassLoader(runtimeUrls, Thread.currentThread().getContextClassLoader());
    }
    catch (MalformedURLException e) {
      throw new RuntimeException("Unable to load project runtime !", e);
    }
  }

}
