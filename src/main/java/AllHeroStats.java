import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/19/21
 */
public class AllHeroStats {
    public AllHeroStats(){
        heroStats = new TreeMap<>();
    }

    // hero name to stats
    private Map<String, SingleHeroStats> heroStats;

    public void setHeroStats(Map<String, SingleHeroStats> heroStats) {
        this.heroStats = heroStats;
    }

    //Add new hero or update stats for existing hero
    public void add(String name, String opponentName, boolean win){
        if(heroStats.containsKey(name)){
            heroStats.get(name).add(opponentName, win);
        }
        else{
            heroStats.put(name, new SingleHeroStats());
            heroStats.get(name).add(opponentName, win);
        }
    }

    public void batchUpdateSingleStats() {
        for (SingleHeroStats singleHeroStats : heroStats.values()) {
            singleHeroStats.setTotalGamesPlayedAsThisHero();
            singleHeroStats.setTotalWinRateAsThisHero();
            singleHeroStats.setTotalWinRateByOpponentByUnitAsThisHero();
        }
    }

    public Map<String, SingleHeroStats> getHeroStats() {
        return heroStats;
    }
}
