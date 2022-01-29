package dota;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/19/21
 */
public class AllHeroStats {
    public AllHeroStats(){
        heroStats = new ArrayList<>();
    }

    // hero name to stats
    private List<SingleHeroStats> heroStats;
    private int totalGamesRecorded;

    public void setHeroStats(List<SingleHeroStats> heroStats) {
        this.heroStats = heroStats;
        setTotalGamesRecorded();
    }

    public int getTotalGamesRecorded() {
        return totalGamesRecorded;
    }

    public void setTotalGamesRecorded(int totalGamesRecorded) {
        this.totalGamesRecorded = totalGamesRecorded;
    }
    public void setTotalGamesRecorded() {
        int runningTotal = 0;
        for (SingleHeroStats singleHeroStats : heroStats) {
            for(WinLossTotals winLossTotals : singleHeroStats.getOpponents().values()) {
                runningTotal += winLossTotals.getTotalNumGames();
            }
        }
        this.totalGamesRecorded = runningTotal;
    }

    //Add new hero or update stats for existing hero
    public void add(String name, String opponentName, boolean win){
        if (heroStats.stream().filter(stat -> name.equals(stat.getHeroName())).findFirst().orElse(null) == null) {
            heroStats.add(new SingleHeroStats(name));
        }
        heroStats.stream().filter(stat -> name.equals(stat.getHeroName())).findFirst().orElse(null).add(opponentName, win);
    }

    public void setAllCalculatedFieldsForSerialization() {
        setTotalGamesRecorded();
        for (SingleHeroStats singleHeroStats : heroStats) {
            singleHeroStats.setAllCalculatedFields(totalGamesRecorded, this);
        }
    }

    public List<SingleHeroStats> getHeroStats() {
        return heroStats;
    }
}
