package ghostinthecell.entity;

import ghostinthecell.Challenger;
import ghostinthecell.challenge.actions.Action;
import ghostinthecell.challenge.actions.Move;
import ghostinthecell.challenge.actions.Wait;

import java.util.*;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Factory extends Entity {
    private Map<Factory, Integer> nextFactories = new HashMap<>();
    private int productionSize;
    private Set<Troop> comingTroops = new HashSet<>();
    private Bomb bomb;

    public Factory(int id) {
        super(id);
    }

    @Override
    public void update(int... args) {
        this.owner = args[0];
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

    public int compareProductivity(Factory factory) {
        return this.productionSize > factory.productionSize ? 1 : -1;
    }

    public List<Action> action(TreeSet<Factory> neutralFactories, TreeSet<Factory> opponentFactories) {

        List<Action> actions = new ArrayList<>();
        if (isUnderAttack()) {
            System.err.println("id : " + id + " is under attack");
            if (neutralFactories.size() > 0) {
                dispatchCyborgs(neutralFactories, actions);
            } else {
                dispatchCyborgs(opponentFactories, actions);
            }

            return actions;
        }


        int restCyborgs = this.cyborgsCount;

        if (neutralFactories.size() > 0 && cyborgsCount > 6) {
            int cyborgsToMove = productionSize > 2 ? 2 : 1;

            Iterator<Factory> it = neutralFactories.iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable()) {
                    actions.add(new Move(this, factory, factory.necessaryCyborgs()));
                    restCyborgs -= factory.necessaryCyborgs();

                    if (restCyborgs <= 6) {
                        break;
                    }
                }
            }
            return actions;
        }


        if (opponentFactories.size() > 0 && cyborgsCount > 6) {
            int cyborgsToMove = productionSize > 2 ? 5 : 3;
            Iterator<Factory> it = opponentFactories.iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable()) {
                    actions.add(new Move(this, factory, cyborgsToMove));
                    restCyborgs -= cyborgsToMove;
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
        if (this.cyborgsCount > factories.size()) {
            int cyborgsToMove = this.cyborgsCount / factories.size();
            for (Factory factory : factories) {
                Action action = new Move(this, factory, cyborgsToMove);
                actions.add(action);
                restCyborgs -= cyborgsToMove;
                if (productionSize == 0 && restCyborgs <= 6) {
                    break;
                }
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
            switch (comingTroop.owner()) {
                case Challenger.ME :
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
        return this.bomb != null;
    }

    public int score() {
        int score = 0;
        score += productionSize * 5;
        score += 10 / (cyborgsCount + 1);

        return score;
    }
}
