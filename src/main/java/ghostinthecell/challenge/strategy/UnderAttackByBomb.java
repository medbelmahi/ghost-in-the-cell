package ghostinthecell.challenge.strategy;

import ghostinthecell.Board;
import ghostinthecell.challenge.actions.Action;
import ghostinthecell.challenge.actions.Move;
import ghostinthecell.challenge.actions.Wait;
import ghostinthecell.entity.Factory;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
public class UnderAttackByBomb extends GameStrategy {

    public UnderAttackByBomb(Factory factory) {
        super(factory);
    }


    @Override
    public List<Action> processing(Board game) {
        List<Action> actions = new ArrayList<>();
        System.err.println("id : " + this.factory.id() + " is under attack");

        if (game.me.underMyEyes.size() > 0) {
            dispatchCyborgs(game.me.underMyEyes, actions);
        } else if (game.me.neutralFactories.size() > 0) {
            dispatchCyborgs(game.me.neutralFactories, actions);
        } else {
            dispatchCyborgs(game.me.opponentFactories, actions);
        }

        actions.add(new Wait());
        return actions;
    }

    private void dispatchCyborgs(TreeSet<Factory> factories, List<Action> actions) {
        if (this.factory.cyborgsCountMoreOrEqual(factories.size())) {
            int cyborgsToMove = this.factory.cyborgsToMove(factories.size());
            for (Factory factory : factories) {
                Action action = new Move(this.factory, factory, cyborgsToMove);
                actions.add(action);
            }
        } else {
            for (Factory factory : factories) {
                Action action = new Move(this.factory, factory, 1);
                actions.add(action);
                if (!this.factory.hasMoreCyborgs()) {
                    break;
                }
            }
        }
    }
}
