package com.zganiacz.axwave;

import com.zganiacz.axwave.client.Client;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Dynamo on 23.11.2016.
 */
public class ClientApp {

    private static Logger LOGGER = Logger.getLogger(ServerApp.class.getCanonicalName());

    public static void main(String[] args) {
        Options options = new Options();

        options.addOption(buildOption("k", "interval, default=2", Integer.class));

        options.addOption(buildOption("n", "packetLengthInSeconds, default=4", Integer.class));

        options.addOption(buildOption("host", "host, default=localhost", String.class));

        options.addOption(buildOption("port", "port, default=1984", Integer.class));

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

        Integer interval = Integer.parseInt(cmd.getOptionValue('k', "2"));
        Integer packetLengthInSeconds = Integer.parseInt(cmd.getOptionValue('n', "4"));
        String host = cmd.getOptionValue("host", "localhost");
        Integer port = Integer.parseInt(cmd.getOptionValue("port", "1984"));

        try {
            new Client(interval, packetLengthInSeconds, host, port).connectAndSend();
        } catch (IOException e) {
            LOGGER.severe("IOException in Client");
            e.printStackTrace();
        }
    }

    private static Option buildOption(String name, String description, Class<?> type) {
        Option k = new Option(name, true, description);
        k.setRequired(false);
        k.setType(type);
        return k;
    }


}
