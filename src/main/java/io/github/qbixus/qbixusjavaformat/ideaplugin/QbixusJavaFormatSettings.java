/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.qbixus.qbixusjavaformat.ideaplugin;

import com.google.googlejavaformat.java.JavaFormatterOptions;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "QbixusJavaFormatSettings",
    storages = {@Storage("qbixus-java-format.xml")})
class QbixusJavaFormatSettings implements PersistentStateComponent<QbixusJavaFormatSettings.State> {

  private final Project project;

  private State state = new State();

  QbixusJavaFormatSettings(Project project) {
    this.project = project;
  }

  boolean isEnabled() {
    return state.enabled.equals(EnabledState.ENABLED);
  }

  void setEnabled(boolean enabled) {
    setEnabled(enabled ? EnabledState.ENABLED : EnabledState.DISABLED);
  }

  void setEnabled(EnabledState enabled) {
    if (enabled.equals(EnabledState.ENABLED)) {
      JreConfigurationChecker.checkJreConfiguration(project);
    }
    state.enabled = enabled;
  }

  boolean isUninitialized() {
    return state.enabled.equals(EnabledState.UNKNOWN);
  }

  JavaFormatterOptions.Style getStyle() {
    return state.style;
  }

  void setStyle(JavaFormatterOptions.Style style) {
    state.style = style;
  }

  @Nullable
  @Override
  public State getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull State state) {
    this.state = state;
  }

  static QbixusJavaFormatSettings getInstance(Project project) {
    return project.getService(QbixusJavaFormatSettings.class);
  }

  enum EnabledState {

    UNKNOWN,
    ENABLED,
    DISABLED
  }

  static class State {

    public JavaFormatterOptions.Style style = JavaFormatterOptions.Style.GOOGLE;

    private EnabledState enabled = EnabledState.UNKNOWN;

    public String getEnabled() {
      switch (enabled) {
        case ENABLED:
          return "true";
        case DISABLED:
          return "false";
        default:
          return null;
      }
    }

    // enabled used to be a boolean so we use bean property methods for backwards compatibility
    public void setEnabled(@Nullable String enabledStr) {
      if (enabledStr == null) {
        enabled = EnabledState.UNKNOWN;
      } else if (Boolean.parseBoolean(enabledStr)) {
        enabled = EnabledState.ENABLED;
      } else {
        enabled = EnabledState.DISABLED;
      }
    }
  }
}
