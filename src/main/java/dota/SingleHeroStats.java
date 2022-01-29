package dota;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/19/21
 */
public class SingleHeroStats {
    private String heroName;
    private Map<String, WinLossTotals> opponents;
    private double totalWinRateAsThisHero;
    private int totalGamesPlayed;
    private double totalWinRateByOpponentByUnitAsThisHero;
    private double popularityScore;
    private double aggregateCounterPopularityScore;
    private double numHerosBetterAgainst;
    private double numHerosWorseAgainst;
    private double firstPickRating;

    public SingleHeroStats(String heroName){
        this.heroName = heroName;
        opponents = new TreeMap<>();

        totalWinRateAsThisHero = 0;
        totalGamesPlayed = 0;
        totalWinRateByOpponentByUnitAsThisHero = 0;
        popularityScore = 0;
        aggregateCounterPopularityScore = 0;
        numHerosBetterAgainst = 0;
        numHerosWorseAgainst = 0;
        firstPickRating = 0;
    }

    // copy constructor
    public SingleHeroStats(String heroName, SingleHeroStats singleHeroStats){
        this.heroName = heroName;
        this.opponents = new LinkedHashMap<>();
        this.totalWinRateAsThisHero = singleHeroStats.totalWinRateAsThisHero;
        this.totalGamesPlayed = singleHeroStats.totalGamesPlayed;
        this.totalWinRateByOpponentByUnitAsThisHero=singleHeroStats.totalWinRateByOpponentByUnitAsThisHero;
        this.popularityScore = singleHeroStats.popularityScore;
        this.aggregateCounterPopularityScore = singleHeroStats.aggregateCounterPopularityScore;
        this.numHerosBetterAgainst = singleHeroStats.numHerosBetterAgainst;
        this.numHerosWorseAgainst = singleHeroStats.numHerosWorseAgainst;
        this.firstPickRating = singleHeroStats.firstPickRating;
    }

    public void setAllCalculatedFields(int totalGamesRecorded, AllHeroStats allHeroStats) { // do not change the order of these setters, the latter ones depend on the former
        this.setTotalWinRateAsThisHero();
        this.setTotalWinRateByOpponentByUnitAsThisHero();
        this.setTotalGamesPlayedAsThisHero();
        this.setPopularityScoreComputed(totalGamesRecorded);
        this.setAggregateCounterPopularityScore(allHeroStats); // also sets numHerosBetterAgainst and numHerosWorseAgainst
        this.setFirstPickRating();
    }

    // use this method to set opponents in copy
    public void setOpponentByString(String name, WinLossTotals winLossTotals) {
        opponents.put(name, winLossTotals);
    }

    public Map<String, WinLossTotals> getOpponents() {
        return opponents;
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

    public double getPopularityScore() {
        return popularityScore;
    }

    public void setPopularityScoreComputed(int totalGamesRecorded) {
        this.popularityScore = totalGamesRecorded > 0 ? ((double) this.totalGamesPlayed / totalGamesRecorded) : 0;
    }

    public double getAggregateCounterPopularityScore() {
        return aggregateCounterPopularityScore;
    }

    public void setAggregateCounterPopularityScore(AllHeroStats allHeroStats) {
        Set<String> countersHeroNames = new HashSet<>();
        for (Map.Entry<String, WinLossTotals> entry : opponents.entrySet())  {
            if(entry.getValue().getWinRate() < .5)
            {
                countersHeroNames.add(entry.getKey());
            }
        }
        // add all the popularities scores up for each hero with a positive win rate against this hero and then divide by the number of scores
        double sumCounterHeroPopularity = 0;
        for(Map.Entry<String, SingleHeroStats> entry : allHeroStats.getHeroStats().entrySet()) {
            if (countersHeroNames.contains(entry.getKey())) {
                sumCounterHeroPopularity += (0.5 - entry.getValue().getPopularityScore());
            }
        }
        aggregateCounterPopularityScore = sumCounterHeroPopularity;
        numHerosWorseAgainst = countersHeroNames.size();
        numHerosBetterAgainst = allHeroStats.getHeroStats().size() - numHerosWorseAgainst;
    }

    public void setAggregateCounterPopularityScore(double aggregateCounterPopularityScore) {
        this.aggregateCounterPopularityScore = aggregateCounterPopularityScore;
    }

    public void setOpponents(Map<String, WinLossTotals> opponents) {
        this.opponents = opponents;
    }

    public void setTotalWinRateAsThisHero(double totalWinRateAsThisHero) {
        this.totalWinRateAsThisHero = totalWinRateAsThisHero;
    }

    public void setTotalGamesPlayed(int totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    public void setTotalWinRateByOpponentByUnitAsThisHero(double totalWinRateByOpponentByUnitAsThisHero) {
        this.totalWinRateByOpponentByUnitAsThisHero = totalWinRateByOpponentByUnitAsThisHero;
    }

    public void setPopularityScore(double popularityScore) {
        this.popularityScore = popularityScore;
    }

    public double getNumHerosBetterAgainst() {
        return numHerosBetterAgainst;
    }

    public void setNumHerosBetterAgainst(double numHerosBetterAgainst) {
        this.numHerosBetterAgainst = numHerosBetterAgainst;
    }

    public double getNumHerosWorseAgainst() {
        return numHerosWorseAgainst;
    }

    public void setNumHerosWorseAgainst(double numHerosWorseAgainst) {
        this.numHerosWorseAgainst = numHerosWorseAgainst;
    }

    public void setFirstPickRating(double firstPickRating) {
        this.firstPickRating = firstPickRating;
    }

    public double getFirstPickRating() {
        return firstPickRating;
    }

    public void setFirstPickRating() {
        this.firstPickRating = (aggregateCounterPopularityScore * (numHerosBetterAgainst/numHerosWorseAgainst) * totalWinRateByOpponentByUnitAsThisHero * (totalWinRateAsThisHero/2));
    }
}
