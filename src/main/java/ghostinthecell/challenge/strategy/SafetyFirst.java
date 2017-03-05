package ghostinthecell.challenge.strategy;

import ghostinthecell.Board;
import ghostinthecell.challenge.actions.Action;
import ghostinthecell.entity.Factory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed BELMAHI on 04/03/2017.
 */
public class SafetyFirst extends GameStrategy{

    public SafetyFirst(Factory factory) {
        super(factory);
    }

    @Override
    public List<Action> processing(Board game) {
        List<Action> actions = new ArrayList<>();

        this.factory.decreaseCyborgs(this.factory.necessaryCyborgsForSafety());

        return actions;
    }
}
