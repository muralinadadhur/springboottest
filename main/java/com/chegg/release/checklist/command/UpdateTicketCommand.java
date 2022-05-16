package com.chegg.release.checklist.command;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.chegg.release.checklist.client.BaseJiraClient;
import com.chegg.release.checklist.exception.ProcessJiraExceptions;
import java.util.concurrent.Callable;
import com.chegg.release.checklist.executor.UpdateTicket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@Slf4j
@CommandLine.Command(
        name = "update",
        mixinStandardHelpOptions = true,
        description = ": Update description for an existing ticket.")
public class UpdateTicketCommand implements Callable<Integer> {

    @CommandLine.Option(
            names = {"--t", "--ticket"},
            defaultValue = "unknown",
            description = "Provide the CMC ticket to be updated.",
            required = true)
    private String jiraTicketKey;

    private UpdateTicket updateTicketExecutor;
    private BaseJiraClient baseJiraClient;
    private ProcessJiraExceptions processJiraExceptions;

    @Autowired
    public UpdateTicketCommand(UpdateTicket updateTicketExecutor, BaseJiraClient baseJiraClient, ProcessJiraExceptions processJiraExceptions) {
        this.updateTicketExecutor = updateTicketExecutor;
        this.baseJiraClient = baseJiraClient;
        this.processJiraExceptions = processJiraExceptions;
    }

    @Override
    public Integer call() {
        try {
            System.out.println("Updating the existing CMC ticket.");
            IssueRestClient issueRestClient = baseJiraClient.getJiraClientInstance().getIssueClient();
            if (issueRestClient != null) {
                if (updateTicketExecutor.updateTicketDetails(jiraTicketKey, issueRestClient)) {
                    System.out.println("Jira ticket updated: " + jiraTicketKey);
                    return 0;
                }
            }
        } catch (Exception updateException) {
            processJiraExceptions.handleExceptions(updateException);
        }
        return 1;
    }
}
