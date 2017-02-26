package ghostinthecell.entity;

import java.util.Comparator;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class BadProducerComparator implements Comparator<Factory>{

    @Override
    public int compare(Factory f1, Factory f2) {
        return f2.compareProductivity(f1);
    }
}
