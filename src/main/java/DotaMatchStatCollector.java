import java.io.IOException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Math.abs;

public class DotaMatchStatCollector {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    private AllHeroStats allHeroStats;
    private Map<String, String> heroes;
    private static final int MATCHES_BATCH_SIZE = 101; // seems like 100 every 10 seconds is good because sometimes I get less
    private static final int SAVE_EVERY_X_REQUESTS = 25;

    public DotaMatchStatCollector() throws Exception {
        try {
            loadDataFromFile();
            System.out.println("HeroStats loaded from file successfully");
        } catch (IOException e) {
            System.out.println("Unable to find file to load. Starting with new HeroStats");
            allHeroStats = new AllHeroStats();
        }
        heroes = getHeroNameMappings();
        allHeroStats.batchUpdateSingleStats();
    }

    public void startReadingFromAPI() throws Exception {
        String lastSequenceNumber = "";
        try {
            lastSequenceNumber = getMostRecentSequenceNumber(); // pass prev offset each time
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        int saveCounter = 0;
        while(true) {
            JSONArray matches;
            try {
                matches = getMatchesJsonArray(lastSequenceNumber, MATCHES_BATCH_SIZE);
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("Empty Result!");
                continue;
            }
            saveCounter +=1;
            int numSuccessful = 0;
            for (int i = 0; i < matches.length(); i++) {
                JSONObject match = matches.getJSONObject(i);
                boolean saved = saveMatchStatistics(allHeroStats, match);
                if(saved) {
                    numSuccessful++;
                }
            }
            lastSequenceNumber = matches.getJSONObject(matches.length()-1).get("match_seq_num").toString();
            if(numSuccessful != matches.length()) {
                System.out.println(ANSI_RED + numSuccessful + ANSI_RESET + " in batch of " + matches.length() + " saved successfully");
            } else{
                System.out.println(numSuccessful + " of " + matches.length() + " saved successfully");
            }
            if(saveCounter == SAVE_EVERY_X_REQUESTS) {
                allHeroStats.batchUpdateSingleStats();
                saveDataToFile(lastSequenceNumber);
                saveCounter = 0;
            }
            getHeroJsonKeepTop5CountersPerHero();
            Thread.sleep(10000); // don't pound the server too fast
        }
    }

    private Map<String, String> getHeroNameMappings() throws Exception {
        String url = "https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/?key=F4AB12444F7DB98F6462D9CB58656B4E&language=en_us&format=JSON";
        JsonWebRequest jsonWebRequest = new JsonWebRequest();
        JSONObject responseObj;
        try {
            responseObj = jsonWebRequest.getJsonData(url);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        JSONObject resultObj = responseObj.getJSONObject("result");
        JSONArray heroArray = resultObj.getJSONArray("heroes");

        Map<String, String> heroes = new HashMap<>();
        for(int i = 0; i < heroArray.length(); i++){
            JSONObject hero = heroArray.getJSONObject((i));
            heroes.put(hero.get("id").toString(), hero.get("localized_name").toString());
        }
        return heroes;
    }

    private String getMostRecentSequenceNumber() throws IOException, JSONException {
        String url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=F4AB12444F7DB98F6462D9CB58656B4E&game_mode=1&matches_requested=1";
        JsonWebRequest jsonWebRequest = new JsonWebRequest();
        JSONObject responseObj;
        try {
            responseObj = jsonWebRequest.getJsonData(url);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        JSONObject mostRecentGame = responseObj.getJSONObject("result");
        JSONArray matchesArray = mostRecentGame.getJSONArray("matches");
        return matchesArray.getJSONObject(0).get("match_seq_num").toString();
    }

    private JSONArray getMatchesJsonArray(String offsetSeqNumber, int batchSize) throws JSONException {
        String url = "http://api.steampowered.com/IDOTA2Match_570/GetMatchHistoryBySequenceNum/v1?key=F4AB12444F7DB98F6462D9CB58656B4E&start_at_match_seq_num=" + offsetSeqNumber + "&matches_requested=" + batchSize;
        JsonWebRequest jsonWebRequest = new JsonWebRequest();
        JSONObject responseObj = null;
        try {
            responseObj = jsonWebRequest.getJsonData(url);
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(responseObj == null) {
            return new JSONArray();
        }
        JSONObject mostRecentGame = responseObj.getJSONObject("result");
        return mostRecentGame.getJSONArray("matches");
    }

    private boolean saveMatchStatistics(AllHeroStats allHeroStats, JSONObject match) {
        try {
            JSONArray players = match.getJSONArray("players");
            String outcome = match.get("radiant_win").toString();
            boolean radiant_win;
            if (outcome.equals("true")) {
                radiant_win = true;
            } else {
                radiant_win = false;
            }

            //FOr each player's hero add won/lost against stat
            for (int i = 0; i < players.length(); i++) {
                JSONObject innerObj = players.getJSONObject((i));//a single player
                String hero_id = innerObj.get("hero_id").toString();
                if(hero_id.equals("0")) {
                    break;
                }
                String player_slot = innerObj.get("player_slot").toString();
                boolean won = false;
                if ((Integer.parseInt(player_slot) < 10 && radiant_win) || (Integer.parseInt(player_slot) > 100 && !radiant_win)) {
                    won = true;
                }
                for (int j = 0; j < players.length(); j++) {// each opponent
                    String player2_slot = players.getJSONObject((j)).get("player_slot").toString();
                    if (abs(Integer.parseInt(player_slot) - Integer.parseInt(player2_slot)) < 100) {
                        continue;
                    }
                    allHeroStats.add(heroes.get(hero_id), heroes.get(players.getJSONObject((j)).get("hero_id").toString()), won);
                }
            }
            return true;
        } catch (Exception e) {
            try {
                System.out.println("Unable to save match_id: " + match.get("match_id"));
            } catch (JSONException ex) {
                ex.printStackTrace();
                System.out.println("Encountered match_id that couldn't be parsed");
            }
            e.printStackTrace();
        }
        return false;
    }

    private void loadDataFromFile() throws IOException {
        this.allHeroStats = JsonUtilities.getAllHeroObj(FileOperations.loadDataFromFile());
    }

    private void saveDataToFile(String lastSequenceNumber) {
        FileOperations.saveDataToFile(lastSequenceNumber, JsonUtilities.getAllHeroJson(allHeroStats));
    }

    private AllHeroStats copyAllHeroData() {
        AllHeroStats allHeroStatsDeepCopy = null;
        try{
            allHeroStatsDeepCopy = JsonUtilities.getAllHeroObj(JsonUtilities.getAllHeroJson(allHeroStats));
        } catch (IOException e) {
            e.printStackTrace();
            allHeroStatsDeepCopy = null;
        }
        if(allHeroStatsDeepCopy == null) {
            System.out.println("There was an issue copying data for visualization. Empty string returned.");
        }
        return allHeroStatsDeepCopy;
    }

    // TODO: move below methods to controller
    public String getHeroJsonAlphabetically() {
        return JsonUtilities.getAllHeroJson(allHeroStats);
    }

    public String getHeroJsonByUnitWinRate() { // modifies the stats so need to create deep copy
        AllHeroStats allHeroStatsDeepCopy = copyAllHeroData();
        return VisualizeData.getHerosByUnitWinRate(allHeroStatsDeepCopy, new WinRateByUnitComparator());
    }

    public String getHeroJsonByAggregateWinRate() { // modifies the stats so need to create deep copy
        AllHeroStats allHeroStatsDeepCopy = copyAllHeroData();
        return VisualizeData.getHerosByAggregateWinRate(allHeroStatsDeepCopy, new WinRateComparator());
    }

    public String getHeroJsonKeepTop5CountersPerHero() { // modifies the stats so need to create deep copy
        AllHeroStats allHeroStatsDeepCopy = copyAllHeroData();
        return VisualizeData.getHerosAlphabeticallyIncludeTop5Counters(allHeroStatsDeepCopy);
    }

}
