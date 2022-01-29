package dota;

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
    public @ResponseBody ResponseEntity<String> getBestSecondPickPool(@RequestParam String op1, @RequestParam String op2, @RequestParam(value = "numHeroes", defaultValue = "10") int numHeroes) {
        return new ResponseEntity<>(DotaMatchStatCollector.getInstance().getBestSecondPickPool(op1, op2, numHeroes), HttpStatus.OK);
    }

    @GetMapping("/getBestFinalPickPool")
    public @ResponseBody ResponseEntity<String> getBestFinalPickPool(@RequestParam String op1, @RequestParam String op2, @RequestParam String op3, @RequestParam String op4, @RequestParam(value = "numHeroes", defaultValue = "10") int numHeroes) {
        return new ResponseEntity<>(DotaMatchStatCollector.getInstance().getBestFinalPickPool(op1, op2, op3, op4, numHeroes), HttpStatus.OK);
    }

    @GetMapping("/getStatisticalAdvantageTeamScore")
    public @ResponseBody ResponseEntity<Double> getStatisticalAdvantageTeamScore(String h1, String h2, String h3, String h4, String h5, String op1, String op2, String op3, String op4, String op5) {
        return new ResponseEntity<>(DotaMatchStatCollector.getInstance().getStatisticalAdvantageTeamScore(h1, h2, h3, h4, h5, op1, op2, op3, op4, op5), HttpStatus.OK);
    }
}
