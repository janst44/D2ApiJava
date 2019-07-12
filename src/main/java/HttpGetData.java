import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
public class HttpGetData {

  public static void main(String[] args) {
    try {
      HttpGetData.makeRequest();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void makeRequest() throws Exception {
    String url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=F4AB12444F7DB98F6462D9CB58656B4E";
    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    // optional default is GET
    con.setRequestMethod("GET");
    //add request header
    con.setRequestProperty("User-Agent", "Mozilla/5.0");
    int responseCode = con.getResponseCode();
    //System.out.println("\nSending 'GET' request to URL : " + url);
    //System.out.println("Response Code : " + responseCode);
    BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    //print in String
    //System.out.println(response.toString());
    //Read JSON response and print
    JSONObject myResponse = new JSONObject(response.toString());
    System.out.println("result after Reading JSON Response");
    JSONObject form_data = myResponse.getJSONObject("result");
    JSONArray jArr = (JSONArray) form_data.getJSONArray("matches");
    for(int i = 0; i < jArr.length(); i++){
      JSONObject innerObj = jArr.getJSONObject((i));
      for(Iterator it = innerObj.keys(); it.hasNext(); ){
        String key = (String)it.next();
        System.out.println(key + ":" + innerObj.get(key));
      }
    }
    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    //String x = form_data.getString("status");
    //System.out.println(x);
    //System.out.println("results- "+myResponse.getString("result.status"));

  }
}


