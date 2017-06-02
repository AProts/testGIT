import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

class CommandProcessor {

    private DBProcessor dbProcessor;
    private CurrencyProcessor currencyProcessor;
    private Set<String> availableCurrencies;
    private String pattern = "[A-Z]{3}";
    private DecimalFormat twoDForm = new DecimalFormat("#.##");
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    CommandProcessor() throws IOException {
        dbProcessor = new DBProcessor();
        currencyProcessor = new CurrencyProcessor();
        availableCurrencies = currencyProcessor.getAvailableCurrencySet();
    }

    String addCommand(String commandLine) throws SQLException, ClassNotFoundException, IOException {

        Date date1;
        Double price;
        String[] command = commandLine.split(" ", 5);

        if (command.length < 5) return "Wrong command.";

        try {
            java.util.Date date = formatter.parse(command[1]);
            date1 = new Date(date.getTime());
        } catch (ParseException e) {
            return "Wrong dtae format.";
        }

        try {
            price = Double.parseDouble(command[2]);
            price = Double.valueOf(twoDForm.format(price));

        } catch (NumberFormatException e) {
            return "Wrong price format.";
        }

        String currency = command[3].toUpperCase();

        if (!currency.matches(pattern) || !availableCurrencies.contains(currency)) return "Wrong currency.";

        dbProcessor.addRecord(date1, price, currency, command[4]);

        return listCommand();
    }

    String listCommand() throws SQLException, ClassNotFoundException {
        ResultSet rs;
        Date toBeDisplayed = null;
        StringBuilder sb = new StringBuilder();
        rs = dbProcessor.displayRecords();
        String result;

        while (rs.next()) {

            Date current = rs.getDate("day");

            if (toBeDisplayed == null || !toBeDisplayed.equals(current)) {
                toBeDisplayed = current;
                sb.append("\n");
                sb.append(current.toString());
                sb.append("\n");
            }

            sb.append(rs.getString("product"));
            sb.append(" ");
            sb.append(rs.getDouble("price"));
            sb.append(" ");
            sb.append(rs.getString("currency"));
            sb.append("\n");
        }

        result = sb.toString();

        return !result.equals("")? result: "Records list is empty.";
    }

    String clearCommand(String commandLine) throws SQLException, ClassNotFoundException {

        Date date1;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String[] command = commandLine.split(" ", 3);

        if (command.length != 2) return "Wrong command.";

        try {
            java.util.Date date = formatter.parse(command[1]);
            date1 = new Date(date.getTime());
        } catch (ParseException e) {
            return "Wrong dtae format.";
        }

        dbProcessor.removeRecords(date1);
        return listCommand();
    }

    String totalCommand(String commandLine) throws SQLException, ClassNotFoundException, IOException{
        String[] command = commandLine.split(" ", 3);
        Map<String, Object> currencyRates;
        ResultSet rs;
        rs = dbProcessor.displayRecords();
        double totalExpense = 0;
        double rate;

        if (command.length != 2) return "Wrong command.";

        String currency = command[1].toUpperCase();

        if (!currency.matches(pattern) || !availableCurrencies.contains(currency)) return "Wrong currency.";

        currencyRates = currencyProcessor.getRates(currency);

        while (rs.next()) {
            String recordCurrency = rs.getString("currency");
            double recordPrice = rs.getDouble("price");


            if (!recordCurrency.equals(currency)) {
                rate = (double)currencyRates.get(recordCurrency);
                recordPrice /= rate;
            }

            totalExpense  += recordPrice;
            totalExpense = Double.valueOf(twoDForm.format(totalExpense));
        }

        return String.format("%.2f %s", totalExpense, currency);
    }


    String helpCommand(){

        StringBuilder sb = new StringBuilder();
        sb.append("* add 2017-04-25 12 USD Jogurt  — adds expense entry to the list\n");
        sb.append("  of user expenses. Expenses for various dates could be added in\n");
        sb.append("  any order. Command accepts following parameters:\n\n");
        sb.append("  2017-04-25 — is the date when expense occurred\n");
        sb.append("  12 — is an amount of money spent\n");
        sb.append("  USD — the currency in which expense occurred\n");
        sb.append("  Jogurt — is the name of product purchased\n\n");
        sb.append("* list — shows the list of all expenses sorted by date\n\n");
        sb.append("* clear 2017-04-25 — removes all expenses for specified date,\n");
        sb.append("  where:\n\n");
        sb.append("  2017-04-25 — is the date for which all expenses should be\n");
        sb.append("  removed\n\n");
        sb.append("* total PLN — this command calculate the total amount of money spent and\n");
        sb.append("  present it in specified currency, where:\n\n");
        sb.append("  PLN — is the currency in which total amount of expenses should\n");
        sb.append("  be presented\n\n");
        sb.append("* exit - close the app");


        return sb.toString();
    }

}
