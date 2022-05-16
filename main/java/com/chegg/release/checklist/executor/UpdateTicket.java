package com.chegg.release.checklist.executor;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.chegg.release.checklist.model.CMCStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class UpdateTicket {
    private Issue issue;

    private ValidateTicket validateTicket;

    @Autowired
    public UpdateTicket(ValidateTicket validateTicket) {
        this.validateTicket = validateTicket;
    }

    public Boolean updateTicketDetails(String jira_key, IssueRestClient jiraIssueRestClient) throws InterruptedException, ExecutionException, JSONException {
        Boolean release_checklist_flag = false;
        try {
            if (validateTicket.getTicketDetails(jira_key, jiraIssueRestClient)) {
                issue = jiraIssueRestClient.getIssue(jira_key).get();
                Set<String> jira_label = issue.getLabels();
                Iterator iterator = jira_label.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().toString().equalsIgnoreCase("automated-release-checklist")) {
                        release_checklist_flag = true;
                        break;
                    }
                }
                if (release_checklist_flag) {
                    StringBuilder jira_description_builder = new StringBuilder();
                    jira_description_builder.append("h2. Release checklist (updated with a new commit hash):\n\n");
                    jira_description_builder.append("Please mark with a (/)  if the line item is applicable for the release.\n\n");
                    jira_description_builder.append("Please mark with a  ✖️  if the line item it is not applicable for the release.\n\n");
                    jira_description_builder.append("|Most recent commit that was merged to the main branch (also indicates the head of main branch)|" + "{{" + System.getenv("CI_COMMIT_SHA") + "}}" + "|\n");
                    if (System.getenv("CI_COMMIT_BEFORE_SHA") != null) {
                        jira_description_builder.append("|Current commit hash in production|" + "{{" + System.getenv("CI_COMMIT_BEFORE_SHA") + "}}" + "|\n");
                    } else {
                        jira_description_builder.append("|Current commit hash in production|" + "N/A" + "|\n");
                    }
                    jira_description_builder.append("|Smoke Tests completed| |\n");
                    jira_description_builder.append("|Load Tests| |\n");
                    jira_description_builder.append("|FMA Tests| |\n");
                    jira_description_builder.append("|ECS Restarts after secrets/param store changes| |\n");
                    jira_description_builder.append("|Staging Health check post master build deployment| |\n");
                    jira_description_builder.append("|Infrastructure changes completed in test| |\n");
                    jira_description_builder.append("|DB schema changes| |\n");
                    jira_description_builder.append("|Other config changes| |\n");
                    IssueInput issueInput = new IssueInputBuilder().setDescription(jira_description_builder.toString()).build();
                    jiraIssueRestClient.updateIssue(jira_key, issueInput);
                    System.out.println("CMC ticket updated : " + jira_key);
                    return true;
                } else {
                    System.out.println("JIRA ticket not updated for release checklist. Please provide a valid CMC ticket.");
                }
            }
        } catch (Exception updateTicketException) {
            throw updateTicketException;
        }
        return false;
    }
}
