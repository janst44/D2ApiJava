import java.util.Comparator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 4/9/21
 */
public class SingleHeroStatsFinalPickComparator implements Comparator<Map.Entry<String, SingleHeroStats>> {
    @Override
    public int compare(Map.Entry<String, SingleHeroStats> t1, Map.Entry<String, SingleHeroStats> t2) {
        if (t1.getValue().getFirstPickRating() < t2.getValue().getFirstPickRating()) {
            return 1;
        }
        return -1;
    }
}
