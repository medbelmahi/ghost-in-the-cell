package ghostinthecell.challenge.actions;

import ghostinthecell.entity.Factory;

/**
 * Created by Mohamed BELMAHI on 27/02/2017.
 */
public class Increase extends Action {

    private final Factory factoryToIncrease;

    public Increase(Factory factory) {
        super("INC");
        factoryToIncrease = factory;
        factory.decreaseCyborgs(10);
    }

    @Override
    public String writeAction() {
        return super.writeAction() + " " + factoryToIncrease.id();
    }
}
