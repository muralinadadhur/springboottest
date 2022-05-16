package com.chegg.release.checklist.executor;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.chegg.release.checklist.config.CMCProperties;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreateTicket {

    private CMCProperties cmcProperties;
    IssueField issueField = null;
    BasicIssue jira_key = null;

    @Autowired
    public CreateTicket(CMCProperties cmcProperties) {
        this.cmcProperties = cmcProperties;
    }

    public String createCMCTicket(String jiraProject, IssueRestClient jiraIssueRestClient) throws ExecutionException, InterruptedException {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", "Learning Services");
            jsonObject.put("id", "13150");

            IssueInputBuilder issueInputBuilder = new IssueInputBuilder();
            issueInputBuilder.setProjectKey(jiraProject);
            issueInputBuilder.setIssueTypeId(10401L);
            issueInputBuilder.setSummary(cmcProperties.getSummary());
            issueInputBuilder.setDescription(cmcProperties.getDescription());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
            issueInputBuilder.setFieldValue("customfield_12502", ZonedDateTime.now().format(formatter));
            issueInputBuilder.setFieldValue("customfield_12503", cmcProperties.getCustomfield_12503());
            List<String> issueLabels = new ArrayList<String>();
            issueLabels.add(cmcProperties.getLabel());
            issueInputBuilder.setFieldValue(IssueFieldId.LABELS_FIELD.id, issueLabels);
            issueInputBuilder.setFieldValue("customfield_12507", cmcProperties.getCustomfield_12507());
            issueInputBuilder.setFieldValue("customfield_14700", cmcProperties.getCustomfield_14700());
            issueInputBuilder.setFieldValue("customfield_14701", cmcProperties.getCustomfield_14701());
            issueField = new IssueField("13150", "Learning Services", null, jsonObject);
            issueInputBuilder.setFieldValue("customfield_13610", issueField);
            jira_key = jiraIssueRestClient.createIssue(issueInputBuilder.build()).get();
            System.out.println("CMC ticket created : " + jira_key.getKey());
        } catch (Exception createTicketException) {
            throw createTicketException;
        }
        return jira_key.getKey();
    }
}

