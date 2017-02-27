package ghostinthecell.custom;

import ghostinthecell.entity.Factory;

import java.util.Comparator;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class BestProducerComparator implements Comparator<Factory> {

    @Override
    public int compare(Factory f1, Factory f2) {
        return f1.score() < f2.score() ? 1 : -1;
    }
}
