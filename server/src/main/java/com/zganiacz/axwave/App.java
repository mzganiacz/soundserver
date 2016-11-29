package com.zganiacz.axwave;

import com.zganiacz.axwave.server.Server;
import org.apache.commons.cli.*;

import java.util.logging.Logger;

/**
 * Created by Dynamo on 23.11.2016.
 */
public class App {

    private static Logger LOGGER = Logger.getLogger(App.class.getCanonicalName());

    public static void main(String[] args) {
        Options options = new Options();

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

        Integer port = Integer.parseInt(cmd.getOptionValue("port", "1984"));

        try {
            new Server(port).serve();
        } catch (Exception e) {
            LOGGER.severe("Exception in Server");
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
