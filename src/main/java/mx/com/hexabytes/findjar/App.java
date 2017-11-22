package mx.com.hexabytes.findjar;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The executable entry point.
 * @author rherrera
 */
public class App {
    /**
     * Gets the {@link Options} supported by this app.
     * @return options supported by this app.
     */
    private static Options getOptions() {
        Option.Builder builder;
        Options options = new Options();
        for (Args arg : Args.values()) {
            builder = Option.builder(arg.name()).desc(arg.getDescription());
            if (arg.isMultiple()) {
                builder.hasArgs();
            } else if (arg.hasArgument()) {
                builder.hasArg().argName(arg.getArgumentName());
            }
            options.addOption(builder.required(arg.isRequired()).build());
        }
        return options;
    }
    /**
     * Prints the available options to run this app.
     * @param options options available for this app.
     */
    private static void printOptions(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        String command = "java -jar findjar.jar <options>";
        formatter.printHelp(command, "", options, "");
    }
    /**
     * Determines whether help was requested.
     * @param args the command line arguments.
     * @return {@code true} if help was requested, {@code false} otherwise.
     * @throws ParseException if command cannot be parsed.
     */
    private static boolean isHelpRequested(String[] args)
            throws ParseException {
        String argument = "-" + Args.help.name();
        for(String arg : args) {
            if (arg.equalsIgnoreCase(argument)) {
                return true;
            }
        }
        return false;
    }
    /**
     * The main entry point.
     * @param args console arguments.
     */
    @SuppressWarnings("UseSpecificCatch")
    public static void main(String... args) {
        CommandLine command;
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            if (isHelpRequested(args)) {
                printOptions(options);
            } else {
                command = parser.parse(options, args);
                Executor.execute(command);
            }
        } catch(ParseException | IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            printOptions(options);
        } catch(Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
