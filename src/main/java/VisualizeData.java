import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

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

    public static String getHerosAlphabeticallyIncludeTop5Counters(AllHeroStats allHeroStats) {
        String json = "";
        Map<String, SingleHeroStats> top5Counters = allHeroStats.getHeroStats();

        int n = 5;
        Iterator<Map.Entry<String, SingleHeroStats>> iterator = top5Counters.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, SingleHeroStats> entry = iterator.next();
            List<Map.Entry<String, WinLossTotals>> greatest = findGreatest(entry.getValue().getOpponents(), n);
            SingleHeroStats singleHeroStats = new SingleHeroStats();
            for (Map.Entry<String, WinLossTotals> oneOfTheGreatestCounters : greatest) {
                singleHeroStats.setOpponentByString(oneOfTheGreatestCounters.getKey(), oneOfTheGreatestCounters.getValue());
            }
            top5Counters.replace(entry.getKey(), singleHeroStats);
        }
        allHeroStats.setHeroStats(top5Counters);
        allHeroStats.batchUpdateSingleStats(); // needs to calculate computer fields now since we created new SingleHeroStats
        try {
            ObjectMapper objectMapper= new ObjectMapper();
            json = objectMapper.writeValueAsString(allHeroStats);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>>
    findGreatest(Map<K, V> map, int n)
    {
        Comparator<? super Map.Entry<K, V>> comparator =
                new Comparator<Map.Entry<K, V>>()
                {
                    @Override
                    public int compare(Map.Entry<K, V> e0, Map.Entry<K, V> e1)
                    {
                        V v0 = e0.getValue();
                        V v1 = e1.getValue();
                        return v0.compareTo(v1);
                    }
                };
        PriorityQueue<Map.Entry<K, V>> highest =
                new PriorityQueue<Map.Entry<K,V>>(n, comparator);
        for (Map.Entry<K, V> entry : map.entrySet())
        {
            highest.offer(entry);
            while (highest.size() > n)
            {
                highest.poll();
            }
        }

        List<Map.Entry<K, V>> result = new ArrayList<Map.Entry<K,V>>();
        while (highest.size() > 0)
        {
            result.add(highest.poll());
        }
        return result;
    }

    private static String getSortedData(AllHeroStats allHeroStats, Comparator comparator) {
        String json = "";
        List<Map.Entry<String, SingleHeroStats>> list = new ArrayList<>(allHeroStats.getHeroStats().entrySet());
        Collections.sort(list, comparator);
        Map<String, SingleHeroStats> sortedMap = new LinkedHashMap<>();
        for(Object entry : list){
            Map.Entry<String, SingleHeroStats> addType = (Map.Entry<String, SingleHeroStats>) entry;
            sortedMap.put(addType.getKey(), addType.getValue());
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
