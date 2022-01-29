package dota;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;

public class DotaMatchStatCollector {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    private AllHeroStats allHeroStats;
    private final Map<String, String> heroes;
    private static final int MATCHES_BATCH_SIZE = 101; // seems like 100 every 10 seconds is good because sometimes I get less
    private static final int SAVE_EVERY_X_REQUESTS = 25;

    private static DotaMatchStatCollector instance;

    static {
        try {
            instance = new DotaMatchStatCollector();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DotaMatchStatCollector getInstance() {
        return instance;
    }

    private DotaMatchStatCollector() throws Exception {
        try {
            loadDataFromFile();
            System.out.println("HeroStats loaded from file successfully");
        } catch (IOException e) {
            System.out.println("Unable to find file to load. Starting with new HeroStats");
            allHeroStats = new AllHeroStats();
        }
        heroes = getHeroNameMappings();
        allHeroStats.setAllCalculatedFieldsForSerialization();
    }

    public void startReadingFromAPI() throws Exception {
        String lastSequenceNumber;
        try {
            lastSequenceNumber = retryGetAndSetLastSeqNumber(); // pass prev offset each time
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        int saveCounter = 0;
        while(true) {
            System.out.println("Executing batch " + saveCounter + " of " + SAVE_EVERY_X_REQUESTS);
            JSONArray matches;
            try {
                matches = getMatchesJsonArray(lastSequenceNumber);
            } catch(Exception e) {
                e.printStackTrace();
                lastSequenceNumber = retryGetAndSetLastSeqNumber();
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
                allHeroStats.setAllCalculatedFieldsForSerialization();
                saveDataToFile(lastSequenceNumber);
                saveCounter = 0;
            }
            Thread.sleep(10000); // don't pound the server too fast
        }
    }

    private String retryGetAndSetLastSeqNumber() throws InterruptedException {
        System.out.println("Getting most recent seq number to start at");
        String lastSequenceNumber = "0";
        while(lastSequenceNumber.equals("0")) {
            try{
                lastSequenceNumber = getMostRecentSequenceNumber();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Upstream server error, waiting for 1 minute before retrying");
                for(int i =1; i < 7; i++){
                    Thread.sleep(10000); // don't pound the server too fast even when error state
                    System.out.println(i + "0");
                }
            }
        }
        return lastSequenceNumber;
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
        String url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=F4AB12444F7DB98F6462D9CB58656B4E&matches_requested=1";
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
        return matchesArray.getJSONObject(0).get("match_seq_num").toString(); // empty json array error can't be handled so just retry
    }

    private JSONArray getMatchesJsonArray(String offsetSeqNumber) throws JSONException, IOException {
        String url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistoryBySequenceNum/v1?key=F4AB12444F7DB98F6462D9CB58656B4E&start_at_match_seq_num=" + offsetSeqNumber + "&matches_requested=" + DotaMatchStatCollector.MATCHES_BATCH_SIZE;
        JsonWebRequest jsonWebRequest = new JsonWebRequest();
        JSONObject responseObj;
        try {
            responseObj = jsonWebRequest.getJsonData(url);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        if(responseObj == null) {
            return new JSONArray();
        }
        JSONObject mostRecentGame = responseObj.getJSONObject("result");
        return mostRecentGame.getJSONArray("matches");
    }

    private boolean saveMatchStatistics(AllHeroStats allHeroStats, JSONObject match) {
        try {
            int gameMode = (Integer) match.get("game_mode");
            int lobbyType = (Integer) match.get("lobby_type");
            if (!(gameMode == 22) || !(lobbyType == 7)) {
                return false;
            }
            JSONArray players = match.getJSONArray("players");
            String outcome = match.get("radiant_win").toString();
            boolean radiant_win;
            radiant_win = outcome.equals("true");

            //FOr each player's hero add won/lost against stat
            for (int i = 0; i < players.length(); i++) {
                JSONObject innerObj = players.getJSONObject((i));//a single player
                String hero_id = innerObj.get("hero_id").toString();
                if(hero_id.equals("0")) {
                    System.out.println("hero_id was 0.");
                    throw new IllegalStateException("hero_id was 0");
                }
                String player_slot = innerObj.get("player_slot").toString();
                boolean won = (Integer.parseInt(player_slot) < 10 && radiant_win) || (Integer.parseInt(player_slot) > 100 && !radiant_win);
                for (int j = 0; j < players.length(); j++) {// each opponent
                    String player2_slot = players.getJSONObject((j)).get("player_slot").toString();
                    if (abs(Integer.parseInt(player_slot) - Integer.parseInt(player2_slot)) < 100) {
                        continue;
                    }
                    if(heroes.get(hero_id) == null || heroes.get(players.getJSONObject((j)).get("hero_id").toString()) == null) {
                        System.out.println("Hero in game was null, ignoring match data.");
                        throw new IllegalStateException("Hero in game was null, ignoring match data.");
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
        }
        if(allHeroStatsDeepCopy == null) {
            System.out.println("There was an issue copying data for visualization. Empty string returned.");
        }
        return allHeroStatsDeepCopy;
    }

    // TODO: move below methods to controller
    public String getHeroJsonAlphabetically() {
        AllHeroStats allHeroStatsDeepCopy = copyAllHeroData();
        return VisualizeData.getHerosAlphabetically(allHeroStatsDeepCopy);
    }

    public String getHeroJsonByUnitWinRate() { // modifies the stats so need to create deep copy
        AllHeroStats allHeroStatsDeepCopy = copyAllHeroData();
        return VisualizeData.getHerosByUnitWinRate(allHeroStatsDeepCopy, new WinRateByUnitComparator());
    }

    public String getHeroJsonByAggregateWinRate() { // modifies the stats so need to create deep copy
        AllHeroStats allHeroStatsDeepCopy = copyAllHeroData();
        return VisualizeData.getHerosByAggregateWinRate(allHeroStatsDeepCopy, new WinRateComparator());
    }

    public String getHeroJsonKeepTopNCountersPerHero(int numCounters) { // modifies the stats so need to create deep copy
        AllHeroStats allHeroStatsDeepCopy = copyAllHeroData();
        return VisualizeData.getHerosAlphabeticallyIncludeTopNCounters(allHeroStatsDeepCopy, numCounters);
    }

    /**
     * Returns a JSON String representation of the top x heroes to pick during the first picking phase
     * @param numHeroes the number of hero's to return
     * @return a JSON String with the best choice of hero first to the worst being last
     */
    public String getBestFirstPickPool(int numHeroes) {
        AllHeroStats allHeroStatsDeepCopy = copyAllHeroData();
        return VisualizeData.getBestFirstPickPool(allHeroStatsDeepCopy, numHeroes);
    }

    public String getBestSecondPickPool(String opponentHero1, String opponentHero2, int numHeroes) {
        AllHeroStats allHeroStatsDeepCopy = copyAllHeroData();
        return VisualizeData.getBestSecondPickPool(allHeroStatsDeepCopy, opponentHero1, opponentHero2, numHeroes);
    }

    public String getBestFinalPickPool(String opponentHero1, String opponentHero2, String opponentHero3, String opponentHero4, int numHeroes) {
        AllHeroStats allHeroStatsDeepCopy = copyAllHeroData();
        return VisualizeData.getBestFinalPickPool(allHeroStatsDeepCopy, opponentHero1, opponentHero2, opponentHero3, opponentHero4, numHeroes);
    }

    public double getStatisticalAdvantageTeamScore(String hero1, String hero2, String hero3, String hero4, String hero5, String op1, String op2, String op3, String op4, String op5) {
        AllHeroStats allHeroStatsDeepCopy = copyAllHeroData();
        return VisualizeData.getStatisticalAdvantageTeamScore(allHeroStatsDeepCopy, hero1, hero2, hero3, hero4, hero5, op1, op2, op3, op4, op5);
    }


}
