import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.ServerError;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/19/21
 */
public class JsonWebRequest {

    public JSONObject getJsonData(String url) throws IOException, JSONException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        System.out.println("\nSending 'GET' request to URL : " + url);
        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);
        String response = readInputStream(con.getInputStream());
        return new JSONObject(response);
    }

    private String readInputStream(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder responseString = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            responseString.append(line);
        }
        bufferedReader.close();
        return responseString.toString();
    }

}
