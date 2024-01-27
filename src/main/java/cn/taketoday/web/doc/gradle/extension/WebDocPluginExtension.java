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

package cn.taketoday.web.doc.gradle.extension;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cn.taketoday.lang.Nullable;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 */
public class WebDocPluginExtension {

  /**
   * config file
   */
  @Nullable
  private File configFile;

  /**
   * exclude artifact
   */
  private final Set<String> exclude = new HashSet<>();

  /**
   * include artifact
   */
  private final Set<String> include = new HashSet<>();

  /**
   * Smart doc config file, like web-doc.json
   *
   * @return Config File
   */
  @Nullable
  public File getConfigFile() {
    return configFile;
  }

  public void setConfigFile(@Nullable File configFile) {
    this.configFile = configFile;
  }

  /**
   * Excludes artifacts
   *
   * @param excludes Array of artifact
   * @return SmartDocPluginExtension
   */
  public WebDocPluginExtension exclude(String... excludes) {
    this.exclude.addAll(Arrays.asList(excludes));
    return this;
  }

  /**
   * Get sets of exclude
   *
   * @return Set
   */
  public Set<String> getExclude() {
    return exclude;
  }

  /**
   * Includes artifacts
   *
   * @param includes Array of artifact
   * @return SmartDocPluginExtension
   */
  public WebDocPluginExtension include(String... includes) {
    this.include.addAll(Arrays.asList(includes));
    return this;
  }

  public Set<String> getInclude() {
    return include;
  }

}
