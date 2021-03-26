import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/19/21
 */
public class SingleHeroStats {

    private Map<String, WinLossTotals> opponents;

    public SingleHeroStats(){
        opponents = new TreeMap<>();
    }

    public Map<String, WinLossTotals> getOpponents() {
        return opponents;
    }

    public void setOpponents(Map<String, WinLossTotals> opponents) {
        this.opponents = opponents;
    }

    //Update w/l of an matchup
    public void add(String oppName, boolean win){
        if (!opponents.containsKey(oppName)) {
            opponents.put(oppName, new WinLossTotals());
        }
        if(win) {
            opponents.get(oppName).wins++;
        }
        else{
            opponents.get(oppName).losses++;
        }
    }

//    public double getTotalWinRateAsThisHero() {
//        int winCount = 0;
//        int lossCount = 0;
//        for (WinLossTotals winLossTotals : opponents.values())  {
//            winCount += winLossTotals.wins;
//            lossCount += winLossTotals.losses;
//        }
//        return (double) winCount/(winCount + lossCount);
//    }
//
//    public int getTotalGamesPlayedAsThisHero() {
//        int numGamesPlayed = 0;
//        for (WinLossTotals winLossTotals : opponents.values())  {
//            numGamesPlayed += winLossTotals.getTotalNumGames();
//        }
//        return numGamesPlayed;
//    }
//
//    public double getTotalWinRateByOpponentByUnitAsThisHero() {
//        int winCount = 0;
//        int lossCount = 0;
//        for (WinLossTotals winLossTotals : opponents.values())  {
//            if(winLossTotals.getWinRate() > .5)
//            {
//                winCount++;
//            }
//            lossCount++;
//        }
//        return (double) winCount/(winCount + lossCount);
//    }
}
