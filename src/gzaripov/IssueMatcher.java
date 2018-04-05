package gzaripov;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class IssueMatcher {
    private final static String ISSUE_REGEX = "(^|[\\s().?!,-{}])%s($|[\\s().?!,-{}])";
    private final String issue;
    private final Pattern pattern;

    public IssueMatcher(String branch) throws Exception {
        this.issue = parseIssue(branch);
        String pattern = String.format(ISSUE_REGEX, this.issue);
        this.pattern = Pattern.compile(pattern, CASE_INSENSITIVE);
    }

    private String parseIssue(String branchName) throws Exception {
        if (branchName.contains("/")) {
            branchName = branchName.split("/")[1];
        }
        String[] branchParts = branchName.split("-");

        if (branchParts.length < 2) {
            throw new Exception("Invalid branch format");
        }

        return branchParts[0] + "-" + branchParts[1];
    }

    public boolean findIssue(String text) {
        Matcher matcher = this.pattern.matcher(text);
        return matcher.find();
    }

    public String appendIssue(String text) {
        if (!text.trim().endsWith("\n")) {
           text += "\n";
        }
        return text + "Relates to " + issue;
    }
}
