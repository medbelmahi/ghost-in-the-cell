package ghostinthecell.entity;

import ghostinthecell.Board;
import ghostinthecell.Challenger;
import ghostinthecell.challenge.actions.Action;
import ghostinthecell.challenge.strategy.GameStrategy;
import ghostinthecell.entity.state.OwnerState;

import java.util.*;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Factory extends Entity {

    public static final int SAFTY_GUARDIANS = 5;

    public Map<Factory, Integer> nextFactories = new HashMap<>();
    public int productionSize;
    private Set<Troop> comingTroops = new HashSet<>();
    private Bomb bomb;
    private GameStrategy usedStrategy;

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

    public int necessaryCyborgs() {

        int necessaryCyborgs = this.cyborgsCount + 1;
        for (Troop comingTroop : comingTroops) {
            if (comingTroop.owner().equals(Challenger.OPPONENT)) {
                necessaryCyborgs += comingTroop.cyborgsCount;
            }
        }
        return necessaryCyborgs;
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

    public boolean isUnderAttackByBomb() {
        return this.bomb != null && bomb.warnedFactories.contains(this);
    }

    public int score() {
        int score = 0;
        score += productionSize * 5;
        score += 10 / (cyborgsCount + 1);

        return score;
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

    public void updateStrategy() {
        this.owner().updateStrategy(this);
    }

    public void setStrategy(GameStrategy strategy) {
        this.usedStrategy = strategy;
    }

    public List<Action> action(Board game) {
        List<Action> actions = this.usedStrategy.processing(game);
        return actions;
    }

    public boolean cyborgsCountMoreOrEqual(int size) {
        return this.cyborgsCount >= size;
    }

    public int cyborgsToMove(int size) {
        return this.cyborgsCount / size;
    }

    public boolean hasMoreCyborgsThen(int count) {
        return this.cyborgsCount > count;
    }

    public boolean isUnderAttackByCyborgs() {
        for (Troop comingTroop : this.comingTroops) {
            if (OwnerState.OPPONENT.equals(comingTroop.owner())) {
                return true;
            }
        }
        return false;
    }
}
