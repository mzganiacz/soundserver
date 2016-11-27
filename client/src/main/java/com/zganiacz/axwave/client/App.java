package com.zganiacz.axwave.client;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Dynamo on 23.11.2016.
 */
public class App {

    private static Logger LOGGER = Logger.getLogger(Server.class.getCanonicalName());

    public static void main(String[] args) {
        Options options = new Options();

        Option k = new Option("k", true, "interval, default=2");
        k.setRequired(false);
        k.setType(Integer.class);
        options.addOption(k);

        Option n = new Option("n", true, "packetLengthInSeconds, default=4");
        n.setRequired(false);
        n.setType(Integer.class);
        options.addOption(n);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Axwave Server", options);

            System.exit(1);
            return;
        }

        Integer interval = Integer.getInteger(cmd.getOptionValue('k', "2"));
        Integer packetLengthInSeconds = Integer.getInteger(cmd.getOptionValue('n', "4"));

        try {
            new Server(interval, packetLengthInSeconds).serve();
        } catch (IOException e) {
            LOGGER.severe("IOException in Server");
            e.printStackTrace();
        }
    }


}
