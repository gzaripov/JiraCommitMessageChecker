package gzaripov.conf;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface Configuration {
    static Configuration getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, Configuration.class);
    }

    boolean isCheckIssueReferenceChecked();

    void setCheckIssueReferenceChecked(boolean checked);

    boolean isCheckIssueAutoAppendChecked();

    void setCheckIssueAutoAppendChecked(boolean checked);
}
