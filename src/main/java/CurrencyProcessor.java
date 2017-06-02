import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;
/**
 * Created by aprots on 01.06.17.
 */
class CurrencyProcessor {

    private String readAll(Reader rd) throws IOException {

        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1){
            sb.append((char) cp);
        }

        return sb.toString();
    }

    private JSONObject readJsonFromUrl(String url) throws IOException {

        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);

            return json;

        }
    }

    Set<String> getAvailableCurrencySet() throws IOException {
        JSONObject json = readJsonFromUrl("http://api.fixer.io/latest");
        json = json.getJSONObject("rates");
        Iterator<String> keys = json.keys();
        Set<String> result = json.keySet();
        TreeSet<String> resultTree = new TreeSet<String>(result);
        resultTree.add("EUR");

        return resultTree;
    }


    Map<String, Object> getRates(String currency) throws IOException {

        JSONObject json = readJsonFromUrl(String.format("http://api.fixer.io/latest?base=%s",currency));
        json = json.getJSONObject("rates");
        Map<String,Object> result = json.toMap();

        return result;
    }



}
