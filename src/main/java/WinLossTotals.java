import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/19/21
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WinLossTotals {
    public WinLossTotals(){
        this.wins = 0;
        this.losses = 0;
    }
    public int wins;
    public int losses;
    public double getWinRate(){
        return (double) wins / (wins + losses);
    }
    public int getTotalNumGames(){return (wins + losses);}
}
