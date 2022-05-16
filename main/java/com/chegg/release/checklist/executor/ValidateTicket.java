package com.chegg.release.checklist.executor;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.chegg.release.checklist.model.CMCStatus;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ExecutionException;


@Component
@Slf4j
public class ValidateTicket {

    private Issue issue;

    public Boolean getTicketDetails(String jira_key, IssueRestClient jiraIssueRestClient) throws InterruptedException, ExecutionException, JSONException {
        try {
            JSONObject cmcApprovalStatus;
            IssueField issueField;
            issue = jiraIssueRestClient.getIssue(jira_key).get();
            if (!(issue.getStatus().getName().equalsIgnoreCase(CMCStatus.CLOSED.getCmcStatus()))) {
                System.out.println("Ticket Summary : " + issue.getSummary());
                issueField = issue.getFieldByName("CMC Approved");
                cmcApprovalStatus = (JSONObject) issueField.getValue();
                if (cmcApprovalStatus.get("value").toString().equalsIgnoreCase("Yes")) {
                    System.out.println("CMC Approval Status: " + CMCStatus.APPROVED.getCmcStatus());
                    if (issue.getStatus().getName().equalsIgnoreCase(CMCStatus.IN_PROGRESS.getCmcStatus()) ||
                            issue.getStatus().getName().equalsIgnoreCase(CMCStatus.APPROVED.getCmcStatus())) {
                        System.out.println("CMC Status: " + issue.getStatus().getName());
                        System.out.println("CMC Ticket in valid status, proceeding with the production deployment.");
                        return true;
                    } else {
                        System.out.println("CMC Status: " + issue.getStatus().getName());
                        if (issue.getStatus().getName().equalsIgnoreCase(CMCStatus.OPEN.getCmcStatus())) {
                            System.out.println("CMC Ticket should be in " + CMCStatus.APPROVED.getCmcStatus() + " or " + CMCStatus.IN_PROGRESS.getCmcStatus() + " status " +
                                    "before proceeding with production deployment.");
                        } else if (issue.getStatus().getName().equalsIgnoreCase(CMCStatus.REVIEW.getCmcStatus())) {
                            System.out.println("CMC Ticket already in " + CMCStatus.REVIEW.getCmcStatus() + " status. Please open a new CMC for approval and production deployment.");
                        }
                    }
                } else {
                    System.out.println("CMC Approval Status: " + CMCStatus.WAITING_ON_APPROVAL.getCmcStatus());
                    System.out.println("Please get the CMC ticket approved and update status to In Progress/Approved before proceeding with the production deployment.");
                }
            } else {
                System.out.println("Ticket Status: " + CMCStatus.CLOSED.getCmcStatus() + ". Please provide a valid CMC ticket for deployment.");
            }
        } catch (Exception validateTicketException) {
            throw validateTicketException;
        }
        return false;
    }
}
