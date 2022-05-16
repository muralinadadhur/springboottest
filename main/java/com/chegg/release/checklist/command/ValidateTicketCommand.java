package com.chegg.release.checklist.command;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.chegg.release.checklist.client.BaseJiraClient;
import com.chegg.release.checklist.executor.ValidateTicket;
import com.chegg.release.checklist.exception.ProcessJiraExceptions;

import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@Slf4j
@CommandLine.Command(
        name = "verify",
        mixinStandardHelpOptions = true,
        description = ": Command to verify if the CMC ticket has been approved.")
public class ValidateTicketCommand implements Callable<Integer> {

    @CommandLine.Option(
            names = {"--n", "--name"},
            defaultValue = "unknown",
            description = "Command action type",
            required = true)
    private String jira_key;

    private ValidateTicket validateTicketExecutor;
    private BaseJiraClient baseJiraClient;
    private ProcessJiraExceptions processJiraExceptions;

    @Autowired
    public ValidateTicketCommand(ValidateTicket validateTicketExecutor, BaseJiraClient baseJiraClient, ProcessJiraExceptions processJiraExceptions) {
        this.validateTicketExecutor = validateTicketExecutor;
        this.baseJiraClient = baseJiraClient;
        this.processJiraExceptions = processJiraExceptions;
    }

    @Override
    public Integer call() {
        try {
            System.out.println("Verifying approval status on ticket : " + jira_key);
            IssueRestClient issueRestClient = baseJiraClient.getJiraClientInstance().getIssueClient();
            if (issueRestClient != null) {
                if (validateTicketExecutor.getTicketDetails(jira_key, issueRestClient)) {
                    return 0;
                }
            }
        } catch (Exception jiraException) {
            processJiraExceptions.handleExceptions(jiraException);
        }
        return 1;
    }
}
