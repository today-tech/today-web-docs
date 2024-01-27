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

package cn.taketoday.web.doc.gradle.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedModuleVersion;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.result.ArtifactResult;
import org.gradle.api.artifacts.result.ComponentArtifactsResult;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskAction;
import org.gradle.jvm.JvmLibrary;
import org.gradle.language.base.artifact.SourcesArtifact;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import cn.taketoday.core.io.ClassPathResource;
import cn.taketoday.lang.Constant;
import cn.taketoday.util.CollectionUtils;
import cn.taketoday.web.doc.DocConfig;
import cn.taketoday.web.doc.gradle.extension.WebDocPluginExtension;
import cn.taketoday.web.doc.gradle.util.ArtifactFilterUtils;
import cn.taketoday.web.doc.gradle.util.CustomArtifact;
import cn.taketoday.web.doc.gradle.util.SourceSetUtils;

/**
 * Web Docs task
 *
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 */
public abstract class WebDocTask extends DefaultTask {

  /**
   * default config file
   */
  public static final String DEFAULT_CONFIG = "web-doc/default.json";

  /**
   * default java source dir
   */
  public static final String SRC_MAIN_JAVA_PATH = "src/main/java";

  /**
   * Task action
   */
  @TaskAction
  public void action() throws IOException {
    Logger logger = getLogger();
    Project project = getProject();
    logger.quiet("Web-docs start creating API Documentation.");
    WebDocPluginExtension pluginExtension = project.getExtensions().getByType(WebDocPluginExtension.class);
    Set<String> excludes = pluginExtension.getExclude();
    Set<String> includes = pluginExtension.getInclude();
    JavaProjectBuilder javaProjectBuilder = createJavaProjectBuilder(project, excludes, includes);
    DocConfig docConfig = readConfig(pluginExtension);
    executeAction(docConfig, javaProjectBuilder, logger);
  }

  private static DocConfig readConfig(WebDocPluginExtension pluginExtension) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    File file = pluginExtension.getConfigFile();
    if (file == null) {
      ClassPathResource resource = new ClassPathResource(DEFAULT_CONFIG);
      try (InputStream inputStream = resource.getInputStream()) {
        return objectMapper.readValue(inputStream, DocConfig.class);
      }
    }
    return objectMapper.readValue(file, DocConfig.class);
  }

  /**
   * Classloading
   *
   * @return JavaProjectBuilder
   */
  private JavaProjectBuilder createJavaProjectBuilder(Project project, Set<String> excludes, Set<String> includes) {
    SortedClassLibraryBuilder classLibraryBuilder = new SortedClassLibraryBuilder();
    classLibraryBuilder.setErrorHander(e -> getLogger().error("Parse error", e));
    JavaProjectBuilder projectBuilder = new JavaProjectBuilder(classLibraryBuilder);
    projectBuilder.setEncoding(Constant.DEFAULT_ENCODING);
    projectBuilder.setErrorHandler(e -> getLogger().warn(e.getMessage()));

    Set<File> set = SourceSetUtils.getMainJava(project);
    if (CollectionUtils.isNotEmpty(set)) {
      for (File file : set) {
        projectBuilder.addSourceTree(file);
      }
    }
    File src = SourceSetUtils.getDefaultMainJava(project);
    if (src != null) {
      getLogger().quiet("Code path: " + src);
      projectBuilder.addSourceTree(src);
    }
    loadSourcesDependencies(projectBuilder, project, excludes, includes);
    return projectBuilder;
  }

  /**
   * load sources
   */
  private void loadSourcesDependencies(JavaProjectBuilder javaDocBuilder, Project project, Set<String> excludes, Set<String> includes) {
    Configuration compileConfiguration = project.getConfigurations().getByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME);
    ArrayList<ComponentIdentifier> binaryDependencies = new ArrayList<>();

    TreeMap<String, Project> allModules = getAllModule(project.getRootProject());
    Set<ResolvedArtifact> resolvedArtifacts = compileConfiguration.getResolvedConfiguration().getResolvedArtifacts();
    for (ResolvedArtifact resolvedArtifact : resolvedArtifacts) {
      String displayName = resolvedArtifact.getId().getComponentIdentifier().getDisplayName();
      CustomArtifact moduleArtifact = null;
      boolean selfModule = displayName.startsWith("project :");
      if (selfModule) {
        ResolvedModuleVersion version = resolvedArtifact.getModuleVersion();
        moduleArtifact = CustomArtifact.builder();
        moduleArtifact.setGroup(version.getId().getGroup());
        moduleArtifact.setArtifactId(version.getId().getName());
        moduleArtifact.setVersion(version.getId().getVersion());
        // add local source
        String artifactName = moduleArtifact.getGroupId() + ":" + moduleArtifact.getArtifactId();
        addModuleSourceTree(javaDocBuilder, allModules, artifactName);

      }
      CustomArtifact artifact = selfModule ? moduleArtifact : CustomArtifact.builder(displayName);
      if (ArtifactFilterUtils.ignoreArtifact(artifact) || ArtifactFilterUtils.ignoreInfraArtifactById(artifact)) {
        continue;
      }
      String artifactName = artifact.getGroupId() + ":" + artifact.getArtifactId();
      if (matches(excludes, artifactName)) {
        continue;
      }
      if (matches(includes, artifactName)) {
        if (selfModule) {
          addModuleSourceTree(javaDocBuilder, allModules, displayName);
          continue;
        }
        binaryDependencies.add(resolvedArtifact.getId().getComponentIdentifier());
        continue;
      }
      if (includes.isEmpty() && !selfModule) {
        binaryDependencies.add(resolvedArtifact.getId().getComponentIdentifier());
      }
    }
    Set<ComponentArtifactsResult> artifactsResults = project.getDependencies().createArtifactResolutionQuery()
            .forComponents(binaryDependencies)
            .withArtifacts(JvmLibrary.class, SourcesArtifact.class)
            .execute()
            .getResolvedComponents();

    for (ComponentArtifactsResult artifactResult : artifactsResults) {
      for (ArtifactResult sourcesResult : artifactResult.getArtifacts(SourcesArtifact.class)) {
        if (sourcesResult instanceof ResolvedArtifactResult) {
          this.loadSourcesDependency(javaDocBuilder, (ResolvedArtifactResult) sourcesResult);
        }
      }
    }
  }

  /**
   * @param javaDocBuilder JavaProjectBuilder
   * @param artifact Artifact
   */
  private void loadSourcesDependency(JavaProjectBuilder javaDocBuilder, ResolvedArtifactResult artifact) {
    try (JarFile jarFile = new JarFile(artifact.getFile())) {
      for (Enumeration<?> entries = jarFile.entries(); entries.hasMoreElements(); ) {
        JarEntry entry = (JarEntry) entries.nextElement();
        String name = entry.getName();
        if (name.endsWith(".java") && !name.endsWith("/package-info.java")) {
          javaDocBuilder.addSource(
                  new URL("jar:" + artifact.getFile().toURI().toURL() + "!/" + name));
        }
      }
    }
    catch (Throwable e) {
      getLogger().warn("Unable to load jar source " + artifact + " : " + e.getMessage());
    }
  }

  private void addModuleSourceTree(JavaProjectBuilder javaDocBuilder, TreeMap<String, Project> allModules, String artifactName) {
    Project module = allModules.getOrDefault(artifactName, null);
    if (module != null) {
      String modelSrc = String.join(File.separator, module.getProjectDir().getAbsolutePath(), SRC_MAIN_JAVA_PATH);
      javaDocBuilder.addSourceTree(new File(modelSrc));
    }
  }

  private TreeMap<String, Project> getAllModule(Project rootProject) {
    TreeMap<String, Project> result = new TreeMap<>();
    if (Objects.isNull(rootProject)) {
      return result;
    }
    if (rootProject.getDepth() != 0) {
      result.put(rootProject.getGroup() + ":" + rootProject.getName(), rootProject);
    }
    if (rootProject.getChildProjects().isEmpty()) {
      return result;
    }
    rootProject.getChildProjects().forEach((k, v) -> result.putAll(getAllModule(v)));
    return result;
  }

  /**
   * execute action
   *
   * @param apiConfig ApiConfig
   * @param javaProjectBuilder JavaProjectBuilder
   * @param logger Logger
   */
  public abstract void executeAction(DocConfig apiConfig, JavaProjectBuilder javaProjectBuilder, Logger logger) throws IOException;

  static boolean matches(Collection<String> patterns, String str) {
    if (patterns == null) {
      return false;
    }
    for (String patternStr : patterns) {
      Pattern pattern = Pattern.compile(patternStr);
      if (pattern.matcher(str).matches()) {
        return true;
      }
    }
    return false;
  }

}
