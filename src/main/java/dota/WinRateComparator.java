package dota;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/30/21
 */
public class WinRateComparator implements Comparator<SingleHeroStats> {
    @Override
    public int compare(SingleHeroStats o1,  SingleHeroStats o2) {
        if(o1.getTotalWinRateAsThisHero() < o2.getTotalWinRateAsThisHero()) {
            return 1;
        }
        if(o1.getTotalWinRateAsThisHero() > o2.getTotalWinRateAsThisHero()) {
            return -1;
        }
        if(o1.getTotalWinRateByOpponentByUnitAsThisHero() < o2.getTotalWinRateByOpponentByUnitAsThisHero()) {
            return 1;
        }
        if(o1.getTotalWinRateByOpponentByUnitAsThisHero() > o2.getTotalWinRateByOpponentByUnitAsThisHero()) {
            return -1;
        }
        if(o1.getTotalGamesPlayed() < o2.getTotalGamesPlayed()) {
            return 1;
        }
        if(o1.getTotalGamesPlayed() > o2.getTotalGamesPlayed()) {
            return -1;
        }
        return 1;
    }
}
