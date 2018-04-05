package gzaripov;

import com.intellij.dvcs.repo.Repository;
import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.openapi.project.Project;

public class CurrentBranchExtractor {
    private final Project project;

    CurrentBranchExtractor(Project project) {
        this.project = project;
    }

    public String extract() {
        return VcsRepositoryManager.getInstance(project)
                .getRepositories()
                .stream()
                .map(Repository::getCurrentBranchName)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("There is no current branch"));
    }
}
