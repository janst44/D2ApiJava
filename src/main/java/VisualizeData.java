import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/21/21
 */
public class VisualizeData {

    public static String getHerosByUnitWinRate(AllHeroStats allHeroStats, WinRateByUnitComparator winRateByUnitComparator) {
        return getSortedData(allHeroStats, winRateByUnitComparator);
    }

    public static String getHerosByAggregateWinRate(AllHeroStats allHeroStats, WinRateComparator winRateComparator) {
        return getSortedData(allHeroStats, winRateComparator);
    }

    public static String getHerosAlphabeticallyIncludeTopNCounters(AllHeroStats allHeroStats, int numCountersToGet) {
        String json = "";
        Map<String, SingleHeroStats> top5Counters = allHeroStats.getHeroStats();

        for (Map.Entry<String, SingleHeroStats> entry : top5Counters.entrySet()) {
            List<Map.Entry<String, WinLossTotals>> greatest = findGreatest(entry.getValue().getOpponents(), numCountersToGet);
            SingleHeroStats singleHeroStats = new SingleHeroStats(entry.getValue());
            for (Map.Entry<String, WinLossTotals> oneOfTheGreatestCounters : greatest) {
                singleHeroStats.setOpponentByString(oneOfTheGreatestCounters.getKey(), oneOfTheGreatestCounters.getValue());
            }
            top5Counters.replace(entry.getKey(), singleHeroStats);
        }
        allHeroStats.setHeroStats(top5Counters);
        try {
            ObjectMapper objectMapper= new ObjectMapper();
            json = objectMapper.writeValueAsString(allHeroStats);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * If none of a hero's counters are popular hero's that is good
     */
    public static String getBestFirstPickPool(AllHeroStats allHeroStats, int numToGet) {
        String json = "";
        PriorityQueue<Map.Entry<String, SingleHeroStats>> pq = new PriorityQueue<>(numToGet, new SingleHeroStatsFirstPickComparator());
        pq.addAll(allHeroStats.getHeroStats().entrySet());

        Map<String, SingleHeroStats> bestFirstPicks = new LinkedHashMap<>();
        while(numToGet > 0) {
            Map.Entry<String, SingleHeroStats> entry = pq.poll();
            bestFirstPicks.put(entry.getKey(), entry.getValue());
            numToGet--;
        }
        allHeroStats.setHeroStats(bestFirstPicks);
        try {
            ObjectMapper objectMapper= new ObjectMapper();
            json = objectMapper.writeValueAsString(allHeroStats);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Maximize aggregate counter pick given two of the opponents heroes
     */
    public static String getBestSecondPickPool(AllHeroStats allHeroStats, String op1, String op2, int numToGet) {
        SingleHeroStats singleHeroStats = getAggregateCounterPick(allHeroStats, Arrays.asList(op1, op2));
        singleHeroStats.getOpponents().remove(op1);
        singleHeroStats.getOpponents().remove(op2);
        List<Map.Entry<String, WinLossTotals>> greatest = findGreatest(singleHeroStats.getOpponents(), numToGet);
        Stream<Map.Entry<String, WinLossTotals>> input = greatest.stream();
        List<Map.Entry<String, WinLossTotals>> output =
                input.collect(ArrayList::new,
                        (list, e) -> list.add(0, e),
                        (list1, list2) -> list1.addAll(0, list2));
        Map<String, SingleHeroStats> bestSecondPicks = new LinkedHashMap<>();
        for(String heroName : output.stream().map(Map.Entry::getKey).collect(Collectors.toList())) {
            bestSecondPicks.put(heroName, allHeroStats.getHeroStats().get(heroName));
        }
        allHeroStats.setHeroStats(bestSecondPicks);

        String json = "";
        try {
            ObjectMapper objectMapper= new ObjectMapper();
            json = objectMapper.writeValueAsString(allHeroStats);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static SingleHeroStats getAggregateCounterPick(AllHeroStats allHeroStats,  List<String> heroesToCounter) {
        LinkedList<String> removableList = new LinkedList<>(heroesToCounter);
        SingleHeroStats singleHeroStats = allHeroStats.getHeroStats().get(removableList.get(0));
        removableList.remove(0);
        for(Map.Entry<String, WinLossTotals> entry: singleHeroStats.getOpponents().entrySet()) {
            if(removableList.contains(entry.getKey())){
                continue;
            }
            for (String hero: removableList) {
                if(!allHeroStats.getHeroStats().get(hero).getOpponents().containsKey(entry.getKey())){
                    continue;
                }
                entry.getValue().losses += allHeroStats.getHeroStats().get(hero).getOpponents().get(entry.getKey()).losses;
                entry.getValue().wins += allHeroStats.getHeroStats().get(hero).getOpponents().get(entry.getKey()).wins;
            }
        }
        return singleHeroStats;
    }

    /**
     * Minimize counter pick whilst still counterpicking
     */
    public static String getBestFinalPickPool(AllHeroStats allHeroStats, String op1, String op2, String op3, String op4, int numToGet) {
        SingleHeroStats singleHeroStats = getAggregateCounterPick(allHeroStats, Arrays.asList(op1, op2, op3, op4));
        singleHeroStats.getOpponents().remove(op1);
        singleHeroStats.getOpponents().remove(op2);
        singleHeroStats.getOpponents().remove(op3);
        singleHeroStats.getOpponents().remove(op4);
        List<Map.Entry<String, WinLossTotals>> greatest = findGreatest(singleHeroStats.getOpponents(), numToGet);
        Stream<Map.Entry<String, WinLossTotals>> input = greatest.stream();
        List<Map.Entry<String, WinLossTotals>> output =
                input.collect(ArrayList::new,
                        (list, e) -> list.add(0, e),
                        (list1, list2) -> list1.addAll(0, list2));
        Map<String, SingleHeroStats> bestFinalPicks = new LinkedHashMap<>();
        for(String heroName : output.stream().map(Map.Entry::getKey).collect(Collectors.toList())) {
            bestFinalPicks.put(heroName, allHeroStats.getHeroStats().get(heroName));
        }
        allHeroStats.setHeroStats(bestFinalPicks);

        String json = "";
        try {
            ObjectMapper objectMapper= new ObjectMapper();
            json = objectMapper.writeValueAsString(allHeroStats);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static double getStatisticalAdvantageTeamScore(AllHeroStats allHeroStats, String hero1, String hero2, String hero3, String hero4, String hero5, String op1, String op2, String op3, String op4, String op5) {
        SingleHeroStats opponentAggregateWinRates = getAggregateCounterPick(allHeroStats, Arrays.asList(op1, op2, op3, op4, op5));
        WinLossTotals hero1Compare = opponentAggregateWinRates.getOpponents().get(hero1);
        WinLossTotals hero2Compare = opponentAggregateWinRates.getOpponents().get(hero2);
        WinLossTotals hero3Compare = opponentAggregateWinRates.getOpponents().get(hero3);
        WinLossTotals hero4Compare = opponentAggregateWinRates.getOpponents().get(hero4);
        WinLossTotals hero5Compare = opponentAggregateWinRates.getOpponents().get(hero5);

        double sumWins = hero1Compare.getWins() + hero2Compare.getWins() + hero3Compare.getWins() + hero4Compare.getWins() + hero5Compare.getWins();
        double sumLosses = hero1Compare.getLosses() + hero2Compare.getLosses() + hero3Compare.getLosses() + hero4Compare.getLosses() + hero5Compare.getLosses();
        return (sumWins + sumLosses) == 0 ? 0 : (sumLosses/(sumWins + sumLosses));

    }

    private static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>>
    findGreatest(Map<K, V> map, int n)
    {
        Comparator<? super Map.Entry<K, V>> comparator =
                (Comparator<Map.Entry<K, V>>) (e0, e1) -> {
                    V v0 = e0.getValue();
                    V v1 = e1.getValue();
                    return v0.compareTo(v1);
                };
        PriorityQueue<Map.Entry<K, V>> highest =
                new PriorityQueue<>(n, comparator);
        for (Map.Entry<K, V> entry : map.entrySet())
        {
            highest.offer(entry);
            while (highest.size() > n)
            {
                highest.poll();
            }
        }

        List<Map.Entry<K, V>> result = new ArrayList<>();
        while (highest.size() > 0)
        {
            result.add(highest.poll());
        }
        return result;
    }

    private static String getSortedData(AllHeroStats allHeroStats, Comparator comparator) {
        String json = "";
        List<Map.Entry<String, SingleHeroStats>> list = new ArrayList<>(allHeroStats.getHeroStats().entrySet());
        list.sort(comparator);
        Map<String, SingleHeroStats> sortedMap = new LinkedHashMap<>();
        for(Map.Entry<String, SingleHeroStats> entry : list){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        try {
            ObjectMapper objectMapper= new ObjectMapper();
            json = objectMapper.writeValueAsString(sortedMap);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
}
