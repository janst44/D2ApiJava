import java.util.Comparator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/30/21
 */
public class WinRateByUnitComparator implements Comparator<Map.Entry<String, SingleHeroStats>> {
    @Override
    public int compare(Map.Entry<String, SingleHeroStats> o1, Map.Entry<String, SingleHeroStats> o2) {
        if(o1.getValue().getTotalWinRateByOpponentByUnitAsThisHero() < o2.getValue().getTotalWinRateByOpponentByUnitAsThisHero()) {
            return 1;
        }
        if(o1.getValue().getTotalWinRateByOpponentByUnitAsThisHero() > o2.getValue().getTotalWinRateByOpponentByUnitAsThisHero()) {
            return -1;
        }
        if(o1.getValue().getTotalWinRateAsThisHero() < o2.getValue().getTotalWinRateAsThisHero()) {
            return 1;
        }
        if(o1.getValue().getTotalWinRateAsThisHero() > o2.getValue().getTotalWinRateAsThisHero()) {
            return -1;
        }
        if(o1.getValue().getTotalGamesPlayed() < o2.getValue().getTotalGamesPlayed()) {
            return 1;
        }
        if(o1.getValue().getTotalGamesPlayed() > o2.getValue().getTotalGamesPlayed()) {
            return -1;
        }
        return 1;
    }

}