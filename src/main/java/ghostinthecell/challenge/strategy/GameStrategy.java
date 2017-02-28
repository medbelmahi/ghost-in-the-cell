package ghostinthecell.challenge.strategy;

import ghostinthecell.Board;
import ghostinthecell.challenge.actions.Action;
import ghostinthecell.entity.Factory;

import java.util.List;

/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
public abstract class GameStrategy {
    Factory factory;

    public GameStrategy(Factory factory) {
        this.factory = factory;
    }

    public abstract List<Action> processing(Board game);
}
