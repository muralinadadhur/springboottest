package com.chegg.release.checklist.client;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BaseJiraClient {

    private String jira_url;
    private String jira_username;
    private String jira_token;

    JiraRestClientFactory jiraRestClientFactory;
    JiraRestClient jiraRestClient = null;

    public JiraRestClient getJiraClientInstance() throws InterruptedException, ExecutionException, URISyntaxException {
        try {
            jira_url = System.getenv("JIRA_SERVER_URL");
            jira_username = System.getenv("JIRA_USER");
            jira_token = System.getenv("JIRA_API_TOKEN");
            URI uri = new URI(jira_url);
            jiraRestClientFactory = new AsynchronousJiraRestClientFactory();
            jiraRestClient =
                    jiraRestClientFactory.createWithBasicHttpAuthentication(uri, jira_username, jira_token);
            if (jiraRestClient != null) {
                System.out.println("Connected to JIRA host successfully. "
                                + jiraRestClient.getMetadataClient().getServerInfo().get().getBaseUri());
            } else {
                log.error("Authentication error. Please validate your JIRA credentials  before connecting to this host.");
            }
        }
        catch (Exception jiraClientException) {
          throw jiraClientException;
        }
        return jiraRestClient;
    }
}
