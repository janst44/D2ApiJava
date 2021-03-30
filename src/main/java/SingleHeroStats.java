import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/19/21
 */
public class SingleHeroStats {
    private Map<String, WinLossTotals> opponents;
    private double totalWinRateAsThisHero;
    private int totalGamesPlayed;
    private double totalWinRateByOpponentByUnitAsThisHero;

    public SingleHeroStats(){
        opponents = new TreeMap<>();
        totalWinRateAsThisHero = 0;
        totalGamesPlayed = 0;
        totalWinRateByOpponentByUnitAsThisHero = 0;
    }

    public Map<String, WinLossTotals> getOpponents() {
        return opponents;
    }

    public void setOpponents(Map<String, WinLossTotals> opponents) {
        this.opponents = opponents;
    }

    public void setOpponentByString(String name, WinLossTotals winLossTotals) {
        opponents.put(name, winLossTotals);
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

    public double getTotalWinRateAsThisHero() {
        return totalWinRateAsThisHero;
    }

    public void setTotalWinRateAsThisHero() {
        int winCount = 0;
        int lossCount = 0;
        for (WinLossTotals winLossTotals : opponents.values())  {
            winCount += winLossTotals.wins;
            lossCount += winLossTotals.losses;
        }
        if(winCount + lossCount == 0) {
            return;
        }
        this.totalWinRateAsThisHero = (double) winCount/(winCount + lossCount);
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void setTotalGamesPlayedAsThisHero() {
        int numGamesPlayed = 0;
        for (WinLossTotals winLossTotals : opponents.values())  {
            numGamesPlayed += winLossTotals.getTotalNumGames();
        }
        this.totalGamesPlayed = numGamesPlayed;
    }

    public double getTotalWinRateByOpponentByUnitAsThisHero() {
        return totalWinRateByOpponentByUnitAsThisHero;
    }

    public void setTotalWinRateByOpponentByUnitAsThisHero() {
        int winCount = 0;
        int lossCount = 0;
        for (WinLossTotals winLossTotals : opponents.values())  {
            if(winLossTotals.getWinRate() > .5)
            {
                winCount++;
            }
            lossCount++;
        }
        if(winCount + lossCount == 0) {
            return;
        }
        this.totalWinRateByOpponentByUnitAsThisHero = (double) winCount/(winCount + lossCount);
    }
}
