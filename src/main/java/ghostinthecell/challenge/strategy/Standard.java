package ghostinthecell.challenge.strategy;

import ghostinthecell.Board;
import ghostinthecell.challenge.actions.Action;
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

        if (game.me.underMyEyes.size() > 0 && this.factory.hasMoreCyborgsThen(6)) {
            Iterator<Factory> it = bestTargetSort(game.me.underMyEyes).iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable() && factory.productionSize > 0) {
                    actions.add(new Move(this.factory, factory, factory.necessaryCyborgs()));
                    if (this.factory.hasMoreCyborgsThen(6)) {
                        break;
                    }
                }
            }
        }

        if (game.me.neutralFactories.size() > 0 && this.factory.hasMoreCyborgsThen(6)) {

            Iterator<Factory> it = nearbySort(game.me.neutralFactories).iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable() && factory.productionSize > 0) {
                    actions.add(new Move(this.factory, factory, factory.necessaryCyborgs()));
                    if (this.factory.hasMoreCyborgsThen(6)) {
                        break;
                    }
                }
            }
        }


        if (game.me.opponentFactories.size() > 0 && this.factory.hasMoreCyborgsThen(6)) {
            Iterator<Factory> it = nearbySort(game.me.opponentFactories).iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable() && factory.productionSize > 0) {
                    actions.add(new Move(this.factory, factory, factory.necessaryCyborgs()));
                    if (this.factory.hasMoreCyborgsThen(6)) {
                        break;
                    }
                }
            }
            return actions;
        }
        actions.add(new Wait());
        return actions;
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

                if (o1.productionSize == o2.productionSize) {
                    Integer distance_1 = myFactory.nextFactories.get(o1);
                    Integer distance_2 = myFactory.nextFactories.get(o2);
                    return distance_1 > distance_2 ? 1 : -1;
                }

                return o1.productionSize < o2.productionSize ? 1 : -1;
            }
        };

        TreeSet<Factory> nearBySortedSet = new TreeSet<>(nearByComparator);

        for (Factory factory : factories) {
            nearBySortedSet.add(factory);
        }

        return nearBySortedSet;
    }
}
