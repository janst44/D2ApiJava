import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

import static java.lang.Math.abs;

public class HttpGetData {

  class Totals{
    public Totals(){
      this.wins = 0;
      this.losses = 0;
    }
    public int wins;
    public int losses;
    public double getWinRate(){
      return wins / (wins + losses);
    }
    public int getTotalNumGames(){return (wins + losses);};
  }

  class Stats{
    public Stats(){
      opponents = new HashMap<String, Totals>();
    }
    private Map<String, Totals> opponents;

    //Update w/l of an matchup
    public void add(String id, String oppId, boolean win){
      if(opponents.containsKey(oppId)){
        if(win) {
          opponents.get(oppId).wins++;
        }
        else{
          opponents.get(oppId).losses++;
        }
        if((opponents.get(oppId).getTotalNumGames() > 3) && !(id.equals(oppId))){
          System.out.println("ID: " + id + " " + " oppID: " + oppId);
        }
      }
      else{
        opponents.put(oppId, new Totals());
        if(win) {
          opponents.get(oppId).wins++;
        }
        else{
          opponents.get(oppId).losses++;
        }
      }
    }
  }

  class CounterStats{
    public CounterStats(){
      counters = new HashMap<String, Stats>();
    }
    private Map<String, Stats> counters;

    //Add new hero or update stats for existing hero
    public void add(String id, String oppId, boolean win){
      if(counters.containsKey(id)){
        counters.get(id).add(id, oppId, win);
      }
      else{
        counters.put(id, new Stats());
        counters.get(id).add(id, oppId, win);
      }
    }
  }


  public static void main(String[] args) {
    try {
      HttpGetData program = new HttpGetData();
      program.makeRequest();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void makeRequest() throws Exception {

    CounterStats counterStats = new CounterStats();

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

    Set<String> a = new HashSet<String>();

    for(int i = 0; i < jArr.length(); i++){
      JSONObject innerObj = jArr.getJSONObject((i));
      // System.out.println(innerObj.get("match_id"));//each match id to make another request with
      a.add(innerObj.get("match_id").toString());
      // for(Iterator it = innerObj.keys(); it.hasNext(); ){
      //   String key = (String)it.next();
      //   System.out.println(key + ":" + innerObj.get(key));
      // }
    }
    for (String temp : a) {
      System.out.println(temp);
      url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?match_id="+temp+"&key=F4AB12444F7DB98F6462D9CB58656B4E";
      obj = new URL(url);
      con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("User-Agent", "Mozilla/5.0");
      responseCode = con.getResponseCode();
      in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      response = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      myResponse = new JSONObject(response.toString());
      form_data = myResponse.getJSONObject("result");
      jArr = (JSONArray) form_data.getJSONArray("players");
      String outcome = form_data.get("radiant_win").toString();
      boolean radiant_win;
      if(outcome.equals("true")){
        radiant_win = true;
      }
      else{
        radiant_win = false;
      }

      //FOr each player's hero add won/lost against stat
      for(int i = 0; i < jArr.length(); i++){
        JSONObject innerObj = jArr.getJSONObject((i));//a single player
        String hero_id = innerObj.get("hero_id").toString();
        String player_slot = innerObj.get("player_slot").toString();
        boolean won = false;
        if((Integer.parseInt(player_slot) < 10 && radiant_win) || (Integer.parseInt(player_slot) > 100 && !radiant_win)){
          won = true;
        }
        for(int j = 0; j < jArr.length(); j++){// each opponent
          String player2_slot = jArr.getJSONObject((j)).get("player_slot").toString();
          if(abs(Integer.parseInt(player_slot) - Integer.parseInt(player2_slot)) < 100){
            continue;
          }
          counterStats.add(hero_id, jArr.getJSONObject((j)).get("hero_id").toString(), won);
        }
      }
      Thread.sleep(1200);
    }
  }
}


