package optimizationMethods;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsExchange;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by home on 10.05.2018.
 */
class SimplexMethodAllHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) throws IOException {
        t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        t.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
        if(t.getRequestMethod().compareTo("OPTIONS") == 0) {

            System.out.print(t.getRequestMethod().compareTo("OPTIONS"));
            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            os.close();
            return;
        }

        // parse request
        InputStream is = t.getRequestBody();
        String jsonString = IOUtils.toString(is);
        JSONObject obj = new JSONObject(jsonString);

        // Get data from request
//        int rows = obj.getInt("rows");
//        int cols = obj.getInt("cols");

        JSONArray array = obj.getJSONArray("table");
        float [][] table = new float[array.length()][];

        for (int i = 0; i < array.length(); i++) {
            JSONArray tempArray = array.getJSONArray(i);
            table[i] = new float[tempArray.length()];
            for (int j = 0; j < tempArray.length(); j++) {
                table[i][j] = tempArray.getFloat(j);
            }
        }

        solveSymplexAll(2, 2, table);

        // send response
        String response = "";

        response += "ToDo" + "\n";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }

    public static void solveSymplexAll(int rows, int cols, float[][] table) {
        Symplex symplex = new Symplex(rows, cols);
        symplex.fillTable(table);

        boolean quit = false;

        // print it out
        System.out.println("---Starting set---");
        symplex.print();

        // if table is not optimal re-iterate
        while(!quit){
            Symplex.ERROR err = symplex.compute();

            if(err == Symplex.ERROR.IS_OPTIMAL){
                symplex.print();
                quit = true;
            }
            else if(err == Symplex.ERROR.UNBOUNDED){
                System.out.println("---Solution is unbounded---");
                quit = true;
            }
        }
    }

    public static void parseQuery(String query, Map<String,
            Object> parameters) throws UnsupportedEncodingException {

        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0],
                            System.getProperty("file.encoding"));
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1],
                            System.getProperty("file.encoding"));
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>) obj;
                        values.add(value);

                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<String>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }
}

