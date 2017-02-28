package ghostinthecell.challenge.actions;

import ghostinthecell.entity.Factory;

/**
 * Created by Mohamed BELMAHI on 27/02/2017.
 */
public class Increase extends Action {
    public Increase(Factory factory) {
        super("INC");
        factory.decreaseCyborgs(10);
    }
}
