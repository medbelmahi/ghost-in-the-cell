package ghostinthecell.entity;

import ghostinthecell.Challenger;
import ghostinthecell.challenge.actions.Action;
import ghostinthecell.challenge.actions.Move;
import ghostinthecell.challenge.actions.Wait;
import ghostinthecell.entity.state.OwnerState;

import java.util.*;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Factory extends Entity {
    private Map<Factory, Integer> nextFactories = new HashMap<>();
    private int productionSize;
    private Set<Troop> comingTroops = new HashSet<>();
    private Bomb bomb;

    public Factory(int id, int currentTurn) {
        super(id, currentTurn + 1);
    }

    @Override
    public void update(int... args) {
        super.update(args);
        this.cyborgsCount = args[1];
        this.productionSize = args[2];
        comingTroops.clear();
    }

    public void addDistance(Factory secondFactory, int distance) {
        nextFactories.put(secondFactory, distance);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public void moveInto(Challenger challenger) {
        challenger.addFactory(this);
    }

    @Override
    public void myFightIsOver() {
        //do nothing
    }

    public int compareProductivity(Factory factory) {
        return this.productionSize > factory.productionSize ? 1 : -1;
    }

    public List<Action> action(TreeSet<Factory> neutralFactories, TreeSet<Factory> opponentFactories, TreeSet<Factory> underMyEyes) {

        List<Action> actions = new ArrayList<>();
        if (isUnderAttack()) {
            System.err.println("id : " + id + " is under attack");

            if (underMyEyes.size() > 0) {
                dispatchCyborgs(underMyEyes, actions);
            } else if (neutralFactories.size() > 0) {
                dispatchCyborgs(neutralFactories, actions);
            } else {
                dispatchCyborgs(opponentFactories, actions);
            }

            return actions;
        }


        int restCyborgs = this.cyborgsCount;

        if (underMyEyes.size() > 0 && restCyborgs > 6) {
            int cyborgsToMove = productionSize > 2 ? 2 : 1;

            Iterator<Factory> it = bestTargetSort(underMyEyes).iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable() && factory.productionSize > 0) {
                    actions.add(new Move(this, factory, factory.necessaryCyborgs()));
                    restCyborgs -= factory.necessaryCyborgs();

                    if (restCyborgs <= 6) {
                        break;
                    }
                }
            }
            //return actions;
        }

        if (neutralFactories.size() > 0 && restCyborgs > 6) {
            int cyborgsToMove = productionSize > 2 ? 2 : 1;

            Iterator<Factory> it = nearbySort(neutralFactories).iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable() && factory.productionSize > 0) {
                    actions.add(new Move(this, factory, factory.necessaryCyborgs()));
                    restCyborgs -= factory.necessaryCyborgs();

                    if (restCyborgs <= 6) {
                        break;
                    }
                }
            }
            //return actions;
        }


        if (opponentFactories.size() > 0 && restCyborgs > 6) {
            int cyborgsToMove = productionSize > 2 ? 5 : 3;
            Iterator<Factory> it = nearbySort(opponentFactories).iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable() && factory.productionSize > 0) {
                    actions.add(new Move(this, factory, factory.necessaryCyborgs()));
                    restCyborgs -= factory.necessaryCyborgs();
                    if (restCyborgs <= 5) {
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

    private int necessaryCyborgs() {

        int necessaryCyborgs = this.cyborgsCount + 1;
        for (Troop comingTroop : comingTroops) {
            if (comingTroop.owner().equals(Challenger.OPPONENT)) {
                necessaryCyborgs += comingTroop.cyborgsCount;
            }
        }
        return necessaryCyborgs;
    }

    private void dispatchCyborgs(TreeSet<Factory> factories, List<Action> actions) {
        int restCyborgs = this.cyborgsCount;
        if (this.cyborgsCount >= factories.size()) {
            int cyborgsToMove = this.cyborgsCount / factories.size();
            for (Factory factory : factories) {
                Action action = new Move(this, factory, cyborgsToMove);
                actions.add(action);
                /*restCyborgs -= cyborgsToMove;
                if (productionSize == 0 && restCyborgs <= 6) {
                    break;
                }*/
            }
        } else {
            for (Factory factory : factories) {
                Action action = new Move(this, factory, 1);
                actions.add(action);
                restCyborgs--;
                if (restCyborgs == 0) {
                    break;
                }
            }
        }
    }

    public boolean isReachable() {

        int myCyborgsCount = 0;
        int opponentCyborgsCount = 0;

        for (Troop comingTroop : comingTroops) {
            switch (comingTroop.owner().toString()) {
                case Challenger.ME:
                    myCyborgsCount += comingTroop.cyborgsCount;
                    break;
                case Challenger.OPPONENT :
                    opponentCyborgsCount += comingTroop.cyborgsCount;
                    break;
            }
        }
        return myCyborgsCount > opponentCyborgsCount;
    }

    public void addComingTroop(Troop troop) {
        this.comingTroops.add(troop);
    }

    public void bomb(Bomb bomb) {
        this.bomb = bomb;
    }

    private boolean isUnderAttack() {
        return this.bomb != null && bomb.warnedFactories.contains(this);
    }

    public int score() {
        int score = 0;
        score += productionSize * 5;
        score += 10 / (cyborgsCount + 1);

        return score;
    }

    private TreeSet<Factory> nearbySort(Set<Factory> factories) {

        Comparator<Factory> nearByComparator = new Comparator<Factory>() {
            @Override
            public int compare(Factory o1, Factory o2) {

                if (o1.productionSize == o2.productionSize) {
                    Integer distance_1 = nextFactories.get(o1);
                    Integer distance_2 = nextFactories.get(o2);
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

    public Factory nearFactory() {
        Integer distance = Integer.MAX_VALUE;
        Factory nearFactory = null;
        for (Map.Entry<Factory, Integer> integerFactory : nextFactories.entrySet()) {
            Integer nesDistance = integerFactory.getValue();
            Factory newFactory = integerFactory.getKey();
            if (OwnerState.ME.equals(newFactory.owner())) {
                if (distance > nesDistance) {
                    nearFactory = newFactory;
                    distance = nesDistance;
                }
            } else if (OwnerState.OPPONENT.equals(newFactory.owner())) {
                if (distance >= nesDistance) {
                    nearFactory = newFactory;
                    distance = nesDistance;
                }
            }
        }
        return nearFactory;
    }

    public Factory nearFactory(OwnerState ownerState) {
        Integer distance = Integer.MAX_VALUE;
        Factory nearFactory = null;
        for (Map.Entry<Factory, Integer> integerFactory : nextFactories.entrySet()) {
            Integer nesDistance = integerFactory.getValue();
            Factory newFactory = integerFactory.getKey();

            if (distance > nesDistance && ownerState.equals(newFactory.owner())) {
                nearFactory = newFactory;
                distance = nesDistance;
            }
        }
        return nearFactory;
    }
}
