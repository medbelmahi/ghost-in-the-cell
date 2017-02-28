package ghostinthecell.challenge.strategy;

import ghostinthecell.Board;
import ghostinthecell.challenge.actions.Action;
import ghostinthecell.entity.Factory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
public class UnderAttackCyborgs extends GameStrategy {


    public UnderAttackCyborgs(Factory factory) {
        super(factory);
    }

    @Override
    public List<Action> processing(Board game) {
        List<Action> actions = new ArrayList<>();
        
        return null;
    }
}
