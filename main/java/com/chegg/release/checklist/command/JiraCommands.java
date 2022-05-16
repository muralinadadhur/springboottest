package com.chegg.release.checklist.command;

import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

/**
 * Command for particular action type.
 */
@CommandLine.Command(
        name = "jira",
        mixinStandardHelpOptions = true,
        description = "Command line to validate Release checklist.",
        synopsisSubcommandLabel = "SUB-COMMAND",
        subcommands = {ValidateTicketCommand.class, CreateTicketCommand.class, UpdateTicketCommand.class})
@Component
@Slf4j
public class JiraCommands implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println("Please execute sub-command for specific action");
        return Integer.valueOf(-1);
    }
}
