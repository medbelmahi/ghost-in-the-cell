package ghostinthecell.challenge.strategy;

import ghostinthecell.Board;
import ghostinthecell.challenge.actions.Action;
import ghostinthecell.challenge.actions.Increase;
import ghostinthecell.challenge.actions.Move;
import ghostinthecell.challenge.actions.Wait;
import ghostinthecell.entity.Factory;

import java.util.ArrayList;
import java.util.Iterator;
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


        if (this.factory.productionSize < 3 && this.factory.cyborgsCountMoreOrEqual(10)) {
            actions.add(new Increase(this.factory));
        }/* else {
            if (game.me.underMyEyes.size() > 0) {
                dispatchCyborgs(game.me.underMyEyes, actions);
            } else if (game.me.neutralFactories.size() > 0) {
                dispatchCyborgs(game.me.neutralFactories, actions);
            } else {
                dispatchCyborgs(game.me.opponentFactories, actions);
            }
        }*/

        //moveCyborgs(game.me.underMyEyes, actions);
        moveCyborgs(game.me.neutralFactories, actions);
        moveCyborgs(game.me.opponentFactories, actions);
        moveCyborgs(game.me.myFactories, actions);

        actions.add(new Wait());
        return actions;
    }

    private boolean moveCyborgs(TreeSet<Factory> factories, List<Action> actions) {
        boolean done = false;
        if (factories.size() > 0) {
            Iterator<Factory> it = factories.iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                int necessaryCyborgs1 = 0;
                int necessaryCyborgs = this.factory.cyborgsCountMoreOrEqual(necessaryCyborgs1) ? necessaryCyborgs1 : this.factory.cyborgsCount;
                if (!factory.isUnderAttackByBomb() && this.factory.hasMoreCyborgs()) {
                    actions.add(new Move(this.factory, factory, necessaryCyborgs));
                    done = true;
                }
            }
        }

        return done;
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
