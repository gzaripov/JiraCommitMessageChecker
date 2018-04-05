package gzaripov;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.issueLinks.IssueLinkHtmlRenderer;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.util.ui.UIUtil;
import gzaripov.conf.Configuration;

import javax.swing.*;
import java.awt.*;

/**
 * @author Grigory Zaripov
 * @since 11.01.2018
 */
class IssueReferenceChecker extends CheckinHandler {
    private final CheckinProjectPanel panel;

    IssueReferenceChecker(CheckinProjectPanel panel) {
        this.panel = panel;
    }

    @Override
    public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        final JCheckBox refCheckBox = new JCheckBox("Check reference to issue in message");

        refCheckBox.addChangeListener(e -> {
            boolean selected = ((JCheckBox) e.getSource()).isSelected();
            if (selected != isAutoAppendEnabled()) {
                saveCheckMessage(selected);
            }
        });

        return new RefreshableOnComponent() {
            @Override
            public JComponent getComponent() {
                JPanel root = new JPanel(new BorderLayout());
                root.add(refCheckBox, "West");
                return root;
            }

            @Override
            public void refresh() {
            }

            @Override
            public void saveState() {
                saveCheckMessage(refCheckBox.isSelected());
            }

            @Override
            public void restoreState() {
                refCheckBox.setSelected(isCheckMessageEnabled());
            }
        };
    }

    private boolean isCheckMessageEnabled() {
        return Configuration.getInstance(panel.getProject()).isCheckIssueReferenceChecked();
    }

    private void saveCheckMessage(boolean isEnabled) {
        Configuration.getInstance(panel.getProject()).setCheckIssueReferenceChecked(isEnabled);
    }

    private boolean isAutoAppendEnabled() {
        return Configuration.getInstance(panel.getProject()).isCheckIssueAutoAppendChecked();
    }

    @Override
    public ReturnResult beforeCheckin() {
        if (!isCheckMessageEnabled() || isAutoAppendEnabled()) {
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

        if (issueMatcher.findIssue(panel.getCommitMessage())) {
            return ReturnResult.COMMIT;
        }

        return askUserShouldCommit(currentBranch) ?
                ReturnResult.COMMIT : ReturnResult.CANCEL;
    }

    private boolean askUserShouldCommit(String branch) {
        String message = "Commit message doesn't contain reference to the issue " + branch +
                ".\nAre you sure to commit as is?";
        int yesNo = Messages.showYesNoDialog(message,
                "Missing Issue Reference",
                UIUtil.getWarningIcon());
        return yesNo == Messages.YES;
    }
}