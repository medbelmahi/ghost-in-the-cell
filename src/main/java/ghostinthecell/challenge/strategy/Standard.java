package ghostinthecell.challenge.strategy;

import ghostinthecell.Board;
import ghostinthecell.challenge.actions.Action;
import ghostinthecell.challenge.actions.Increase;
import ghostinthecell.challenge.actions.Move;
import ghostinthecell.challenge.actions.Wait;
import ghostinthecell.entity.Factory;
import ghostinthecell.entity.state.OwnerState;

import java.util.*;

/**
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
public class Standard extends GameStrategy {

    public Standard(Factory factory) {
        super(factory);
    }

    @Override
    public List<Action> processing(Board game) {

        List<Action> actions = new ArrayList<>();

        new SafetyFirst(this.factory).processing(game);
        int totalFactories = game.me.myFactories.size() + game.me.neutralFactories.size() + game.me.opponentFactories.size();
        if (this.factory.productionSize < 3
                && this.factory.cyborgsCountMoreOrEqual(10)
                && game.me.myFactories.size() >= (totalFactories / 3)) {
            actions.add(new Increase(this.factory));
        }

        boolean done = moveCyborgs(game.me.underMyEyes, actions, true) ||
                moveCyborgs(game.me.neutralFactories, actions, false) ||
                moveCyborgs(game.me.opponentFactories, actions, false);

        actions.add(new Wait());
        return actions;
    }

    private boolean moveCyborgs(TreeSet<Factory> factories, List<Action> actions, boolean useSort) {
        boolean done = false;
        if (factories.size() > 0) {
            Iterator<Factory> it = useSort ? bestTargetSort(factories).iterator() : nearbySort(factories).iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                int necessaryCyborgs = factory.necessaryCyborgs(this.factory);
                if (!factory.isReachable() && this.factory.hasMoreCyborgsThen(necessaryCyborgs)) {
                    actions.add(new Move(this.factory, factory, necessaryCyborgs));

                    done = true;
                }
            }
        }

        return done;
    }

    private TreeSet<Factory> bestTargetSort(TreeSet<Factory> underMyEyes) {
        Comparator<Factory> bestTargetComparator = new Comparator<Factory>() {
            @Override
            public int compare(Factory o1, Factory o2) {

                if (o1.productionSize == o2.productionSize) {
                    //Integer distance_1 = nextFactories.get(o1);
                    //Integer distance_2 = nextFactories.get(o2);

                    //int o1_Factor = distance_1;
                    //int o2_factor = distance_2;

                    //Factory nearOpponentFactory = o1.nearFactory(OwnerState.OPPONENT);

                    Integer distanceFromNearOpponentFactory_1 = o1.nextFactories.get(o1.nearFactory(OwnerState.OPPONENT));
                    Integer distanceFromNearOpponentFactory_2 = o2.nextFactories.get(o2.nearFactory(OwnerState.OPPONENT));
                    return distanceFromNearOpponentFactory_1 < distanceFromNearOpponentFactory_2 ? 1 : -1;
                }

                return o1.productionSize < o2.productionSize ? 1 : -1;
            }
        };

        TreeSet<Factory> nearBySortedSet = new TreeSet<>(bestTargetComparator);

        for (Factory factory : underMyEyes) {
            nearBySortedSet.add(factory);
        }

        return nearBySortedSet;
    }

    private TreeSet<Factory> nearbySort(Set<Factory> factories) {
        final Factory myFactory = this.factory;
        Comparator<Factory> nearByComparator = new Comparator<Factory>() {
            @Override
            public int compare(Factory o1, Factory o2) {

                //if (o1.productionSize == o2.productionSize) {
                    Integer distance_1 = myFactory.nextFactories.get(o1);
                    Integer distance_2 = myFactory.nextFactories.get(o2);
                    return distance_1 == distance_2 ? 0 : distance_1 > distance_2 ? 1 : -1;
                //}

                //return o1.productionSize < o2.productionSize ? 1 : -1;
            }
        };

        TreeSet<Factory> nearBySortedSet = new TreeSet<>(nearByComparator);

        for (Factory factory : factories) {
            nearBySortedSet.add(factory);
        }

        return nearBySortedSet;
    }
}
