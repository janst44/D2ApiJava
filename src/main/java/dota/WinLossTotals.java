package dota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/19/21
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WinLossTotals implements Comparable<WinLossTotals> {
    public WinLossTotals(){
        this.wins = 0;
        this.losses = 0;
    }
    public int wins;
    public int losses;
    public double getWinRate(){
        if(wins + losses == 0) {
            return 0;
        }
        return (double) wins / (wins + losses);
    }
    public int getTotalNumGames(){return (wins + losses);}

    @Override
    public int compareTo(WinLossTotals winLossTotals) {
        if(this.getWinRate() > winLossTotals.getWinRate()) {
            return - 1;
        }
        return 1;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }
}
