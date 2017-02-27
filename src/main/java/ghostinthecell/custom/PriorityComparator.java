package ghostinthecell.custom;

import ghostinthecell.challenge.strategy.GameStrategy;

import java.util.Comparator;

/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
public class PriorityComparator implements Comparator<GameStrategy> {
    @Override
    public int compare(GameStrategy s1, GameStrategy s2) {
        return s1.compareWith(s2);
    }
}
