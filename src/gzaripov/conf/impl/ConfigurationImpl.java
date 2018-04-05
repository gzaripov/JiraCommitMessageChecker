package gzaripov.conf.impl;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import gzaripov.conf.Configuration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

@State(name = "JiraPluginConfiguration")
public class ConfigurationImpl implements Configuration, PersistentStateComponent<ConfigurationImpl.State> {
    @SuppressWarnings("WeakerAccess")
    static class State {
        public boolean checkIssueReferenceChecked;
        public boolean checkIssueAutoAppendChecked;

        public State() {
            checkIssueReferenceChecked = true;
            checkIssueAutoAppendChecked = false;
        }
    }

    @SuppressWarnings("WeakerAccess")
    State state;

    public ConfigurationImpl(Project project) {
        state = new State();
    }

    @Nullable
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(State state) {
        this.state = state;
    }

    @Override
    public boolean isCheckIssueReferenceChecked() {
        return state.checkIssueReferenceChecked;
    }

    @Override
    public void setCheckIssueReferenceChecked(boolean checked) {
        state.checkIssueReferenceChecked = checked;
    }

    @Override
    public boolean isCheckIssueAutoAppendChecked() {
        return state.checkIssueAutoAppendChecked;
    }

    @Override
    public void setCheckIssueAutoAppendChecked(boolean checked) {
        state.checkIssueAutoAppendChecked = checked;
    }
}
