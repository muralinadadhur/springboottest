package com.chegg.release.checklist.exception;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.util.ErrorCollection;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Slf4j
public class ProcessJiraExceptions {
    public void handleExceptions(Exception genericException) {
        if (genericException.getCause() instanceof RestClientException) {
            RestClientException restClientException = (RestClientException) genericException.getCause();
            Optional<Integer> option = restClientException.getStatusCode();
            Collection<ErrorCollection> errorCollection;
            switch (option.get()) {
                case 400:
                    errorCollection = restClientException.getErrorCollections();
                    for (ErrorCollection errorCollection1 : errorCollection) {
                        log.error("Bad Request: The request does not have the required fields, " +
                                "or the fields the request has are invalid in some way: {} - {}",
                                errorCollection1.getStatus(), errorCollection1.getErrors());
                    }
                    break;
                case 401:
                    log.error("Connection Error: {} - {}", option.get(), "Unauthorized access. Verify JIRA_USER and JIRA_API_TOKEN setting before connecting to the JIRA host.");
                    break;
                case 404:
                    errorCollection = restClientException.getErrorCollections();
                    for (ErrorCollection errorCollection1 : errorCollection) {
                        log.error("Unable to get ticket details: {} - {}", errorCollection1.getStatus(), errorCollection1.getErrorMessages());
                    }
                    break;
            }
        } else {
            log.error("Unable to execute this JIRA command - {}", genericException.toString());
        }
    }
}
