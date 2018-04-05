package gzaripov;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import gzaripov.conf.Configuration;

import javax.swing.*;
import java.awt.*;

/**
 * @author Grigory Zaripov
 * @since 12.01.2018
 */
class IssueAutoAppender extends CheckinHandler {
    private final CheckinProjectPanel panel;

    IssueAutoAppender(CheckinProjectPanel panel) {
        this.panel = panel;
    }

    @Override
    public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        final JCheckBox appendCheckBox = new JCheckBox("Automatically add issue if not specified");

        appendCheckBox.addChangeListener(e -> {
            boolean selected = ((JCheckBox) e.getSource()).isSelected();
            if (selected != isAutoAppendEnabled()) {
                saveAutoAppend(selected);
            }
        });

        //Configuration.getInstance(panel.getProject())

        return new RefreshableOnComponent() {
            @Override
            public JComponent getComponent() {
                JPanel root = new JPanel(new BorderLayout());
                root.add(appendCheckBox, "West");
                return root;
            }

            @Override
            public void refresh() {
            }

            @Override
            public void saveState() {
                saveAutoAppend(appendCheckBox.isSelected());
            }

            @Override
            public void restoreState() {
                appendCheckBox.setSelected(isAutoAppendEnabled());
            }
        };
    }

    private boolean isAutoAppendEnabled() {
        return Configuration.getInstance(panel.getProject()).isCheckIssueAutoAppendChecked();
    }

    private void saveAutoAppend(boolean isEnabled) {
        Configuration.getInstance(panel.getProject()).setCheckIssueAutoAppendChecked(isEnabled);
    }

    @Override
    public ReturnResult beforeCheckin() {
        if (!isAutoAppendEnabled()) {
            return super.beforeCheckin();
        }

        Project project = panel.getProject();

        String currentBranch;
        IssueMatcher issueMatcher;

        try {
            currentBranch = new CurrentBranchExtractor(project).extract();
            issueMatcher = new IssueMatcher(currentBranch);
        } catch (Exception e) {
            return ReturnResult.COMMIT;
        }

        String commitMessage = panel.getCommitMessage();

        if (issueMatcher.findIssue(commitMessage)) {
            return ReturnResult.COMMIT;
        }

        panel.setCommitMessage(issueMatcher.appendIssue(commitMessage));

        return ReturnResult.COMMIT;
    }
}
