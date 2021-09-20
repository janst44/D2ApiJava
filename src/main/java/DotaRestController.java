import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 4/27/21
 */

@RestController
public class DotaRestController {

    @GetMapping("/getHeroJsonAlphabetically")
    public @ResponseBody ResponseEntity<String> getHeroJsonAlphabetically() {
        return new ResponseEntity<>(DotaMatchStatCollector.getInstance().getHeroJsonAlphabetically(), HttpStatus.OK);
    }

    @GetMapping("/getHeroJsonByUnitWinRate")
    public @ResponseBody ResponseEntity<String> getHeroJsonByUnitWinRate() {
        return new ResponseEntity<>(DotaMatchStatCollector.getInstance().getHeroJsonByUnitWinRate(), HttpStatus.OK);
    }

    @GetMapping("/getHeroJsonByAggregateWinRate")
    public @ResponseBody ResponseEntity<String> getHeroJsonByAggregateWinRate() {
        return new ResponseEntity<>(DotaMatchStatCollector.getInstance().getHeroJsonByAggregateWinRate(), HttpStatus.OK);
    }

    @GetMapping("/getHeroJsonKeepTopNCountersPerHero")
    public @ResponseBody ResponseEntity<String> getHeroJsonKeepTopNCountersPerHero(@RequestParam(value = "numCounters", defaultValue = "10") int numCounters) {
        return new ResponseEntity<>(DotaMatchStatCollector.getInstance().getHeroJsonKeepTopNCountersPerHero(numCounters), HttpStatus.OK);
    }

    @GetMapping("/getBestFirstPickPool")
    public @ResponseBody ResponseEntity<String> getBestFirstPickPool(@RequestParam(value = "numHeroes", defaultValue = "10") int numHeroes) {
        return new ResponseEntity<>(DotaMatchStatCollector.getInstance().getBestFirstPickPool(numHeroes), HttpStatus.OK);
    }

    @GetMapping("/getBestSecondPickPool")
    public @ResponseBody ResponseEntity<String> getBestSecondPickPool(String opponentHero1, String opponentHero2, @RequestParam(value = "numHeroes", defaultValue = "10") int numHeroes) {
        return new ResponseEntity<>(DotaMatchStatCollector.getInstance().getBestSecondPickPool(opponentHero1, opponentHero2, numHeroes), HttpStatus.OK);
    }

    @GetMapping("/getBestFinalPickPool")
    public @ResponseBody ResponseEntity<String> getBestFinalPickPool(String opponentHero1, String opponentHero2, String opponentHero3, String opponentHero4, @RequestParam(value = "numHeroes", defaultValue = "10") int numHeroes) {
        return new ResponseEntity<>(DotaMatchStatCollector.getInstance().getBestFinalPickPool(opponentHero1, opponentHero2, opponentHero3, opponentHero4, numHeroes), HttpStatus.OK);
    }

    @GetMapping("/getStatisticalAdvantageTeamScore")
    public @ResponseBody ResponseEntity<Double> getStatisticalAdvantageTeamScore(String hero1, String hero2, String hero3, String hero4, String hero5, String op1, String op2, String op3, String op4, String op5) {
        return new ResponseEntity<>(DotaMatchStatCollector.getInstance().getStatisticalAdvantageTeamScore(hero1, hero2, hero3, hero4, hero5, op1, op2, op3, op4, op5), HttpStatus.OK);
    }
}
