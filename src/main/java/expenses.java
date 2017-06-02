import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class expenses {


    private static final String EXIT_COMMAND = "exit";
    private static final String ADD_COMMAND = "add";
    private static final String LIST_COMMAND = "list";
    private static final String CLEAR_COMMAND = "clear";
    private static final String TOTAL_COMMAND = "total";
    private static final String HELP_COMMAND = "help";

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {

        StringBuilder sb = new StringBuilder();
        CommandProcessor commandProcessor = new CommandProcessor();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        sb.append("Hello, please give one of the following command:\n");
        sb.append("\t- add\n");
        sb.append("\t- list\n");
        sb.append("\t- clear\n");
        sb.append("\t- total\n");
        sb.append("For more details type 'help'.\n");
        sb.append("For closing program type 'exit'.");
        System.out.println(sb);

        exit: while (true){


            String input = br.readLine();
            String command = input.split(" ")[0].toLowerCase();

            switch (command){

                case ADD_COMMAND:
                    System.out.println(commandProcessor.addCommand(input));
                    break;

                case LIST_COMMAND:
                    System.out.println(commandProcessor.listCommand());
                    break;

                case CLEAR_COMMAND:
                    System.out.println(commandProcessor.clearCommand(input));
                    break;

                case TOTAL_COMMAND:
                    System.out.println(commandProcessor.totalCommand(input));
                    break;

                case HELP_COMMAND:
                    System.out.println(commandProcessor.helpCommand());
                    break;

                case EXIT_COMMAND:
                    System.out.println(EXIT_COMMAND);
                    break exit;

                default:
                    System.out.println("Wrong command.");
            }
        }
    }
}
