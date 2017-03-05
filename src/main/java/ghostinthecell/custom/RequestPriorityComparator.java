package ghostinthecell.custom;

import ghostinthecell.challenge.actions.Request;

import java.util.Comparator;

/**
 * Created by Mohamed BELMAHI on 05/03/2017.
 */
public class RequestPriorityComparator implements Comparator<Request> {
    @Override
    public int compare(Request o1, Request o2) {
        return o1.priorityCompare(o2);
    }
}
