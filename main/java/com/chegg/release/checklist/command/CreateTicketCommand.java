package com.chegg.release.checklist.command;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.chegg.release.checklist.client.BaseJiraClient;
import com.chegg.release.checklist.executor.CreateTicket;
import com.chegg.release.checklist.exception.ProcessJiraExceptions;

import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@Slf4j
@CommandLine.Command(
        name = "create",
        mixinStandardHelpOptions = true,
        description = ": Command to create a new CMC ticket.")
public class CreateTicketCommand implements Callable<Integer> {

    @CommandLine.Option(
            names = {"--p", "--project"},
            defaultValue = "unknown",
            description = "Provide project for creating the CMC ticket.",
            required = true)
    String jira_project;

    private CreateTicket createTicketExecutor;
    private BaseJiraClient baseJiraClient;
    private ProcessJiraExceptions processJiraExceptions;

    @Autowired
    public CreateTicketCommand(CreateTicket createTicketExecutor, BaseJiraClient baseJiraClient,ProcessJiraExceptions processJiraExceptions) {
        this.createTicketExecutor = createTicketExecutor;
        this.baseJiraClient = baseJiraClient;
        this.processJiraExceptions = processJiraExceptions;
    }

    @Override
    public Integer call() {
        String jiraTicketKey;
        try {
            System.out.println("Creating a CMC for release");
            IssueRestClient issueRestClient = baseJiraClient.getJiraClientInstance().getIssueClient();
            if (issueRestClient != null) {
                jiraTicketKey = createTicketExecutor.createCMCTicket(jira_project, issueRestClient);
                System.out.println("Jira ticket created: " + jiraTicketKey);
                return 0;
            }
        } catch (Exception createException) {
                processJiraExceptions.handleExceptions(createException);
        }
        return 1;
    }
}
