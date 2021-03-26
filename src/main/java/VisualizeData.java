import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/21/21
 */
public class VisualizeData {

    private static final double HIGH_WIN_RATE = .55;
    private static final double LOW_WIN_RATE = .45;
    private static final int MINIMUM_MATCHES_PLAYED = 10;

    public static String getSignificantWinRatesVsOtherHerosJson(AllHeroStats allHeroStats) {
        String json = "";
        Map<String, SingleHeroStats> allSignificantHeroStats = allHeroStats.getHeroStats()
                .entrySet()
                .stream()
                .peek(outerObject -> outerObject.getValue().setOpponents(outerObject.getValue().getOpponents()
                        .entrySet()
                        .stream()
                        .filter(innerObject -> hasSignificantStats(innerObject.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
                .filter(outerObject -> outerObject.getValue().getOpponents().size() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        allSignificantHeroStats = new TreeMap<>(allSignificantHeroStats);

        try {
            ObjectMapper objectMapper= new ObjectMapper();
            json = objectMapper.writeValueAsString(allSignificantHeroStats);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getHerosWhosWinRateIsAboveAverageAcrossTheBoard(AllHeroStats allHeroStats) {
        String json = "";
        Map<String, SingleHeroStats> allSignificantHeroStats = allHeroStats.getHeroStats()
                .entrySet()
                .stream()
                .peek(outerObject -> outerObject.getValue().setOpponents(outerObject.getValue().getOpponents()
                        .entrySet()
                        .stream()
                        .filter(innerObject -> hasSignificantStats(innerObject.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
                .filter(outerObject -> outerObject.getValue().getOpponents().size() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        allSignificantHeroStats = new TreeMap<>(allSignificantHeroStats);

        try {
            ObjectMapper objectMapper= new ObjectMapper();
            json = objectMapper.writeValueAsString(allSignificantHeroStats);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static boolean hasSignificantStats(WinLossTotals winLossTotals) {
        return (winLossTotals.getWinRate() > HIGH_WIN_RATE && winLossTotals.getTotalNumGames() > MINIMUM_MATCHES_PLAYED) || (winLossTotals.getWinRate() < LOW_WIN_RATE && winLossTotals.getTotalNumGames() > MINIMUM_MATCHES_PLAYED);
    }
}
