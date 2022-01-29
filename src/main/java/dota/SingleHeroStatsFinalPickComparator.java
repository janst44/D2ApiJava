package dota;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 4/9/21
 */
public class SingleHeroStatsFinalPickComparator implements Comparator<SingleHeroStats> {
    @Override
    public int compare(SingleHeroStats t1, SingleHeroStats t2) {
        if (t1.getFirstPickRating() < t2.getFirstPickRating()) {
            return 1;
        }
        return -1;
    }
}
