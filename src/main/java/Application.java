/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/19/21
 */
public class Application {
    public static void main(String[] args) {
        try {
            DotaMatchStatCollector dotaMatchStatCollector = new DotaMatchStatCollector();
            dotaMatchStatCollector.startReadingFromAPI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
