package com.chegg.release.checklist.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@Component
@Slf4j
public class JiraCommandLineRunner implements CommandLineRunner, ExitCodeGenerator {

    private JiraCommands jiraCommands;
    private final IFactory factory;
    private int exitCode;

    @Autowired
    public JiraCommandLineRunner(JiraCommands jiraCommands, IFactory iFactory) {
        this.jiraCommands = jiraCommands;
        this.factory = iFactory;
    }

    @Override
    public void run(String... args) throws Exception {
        CommandLine cli = new CommandLine(jiraCommands, factory);
        this.exitCode = cli.execute(args);
        System.out.println("Exit Code Generated : " + String.valueOf(this.exitCode));
    }

    @Override
    public int getExitCode() {
        return this.exitCode;
    }
}
