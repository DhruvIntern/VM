package com.vaidhyamegha.tools.data_generator;

import org.kohsuke.args4j.Option;

public class CommandLineArgs {
    @Option(name = "-f", aliases = "--file", usage = "Path to the input GraphQL file", required = true)
    private String inputFile;

    public String getInputFile() {
        return inputFile;
    }
}
