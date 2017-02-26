package ghostinthecell.entity;

import ghostinthecell.Challenger;
import ghostinthecell.actions.Action;
import ghostinthecell.actions.Move;
import ghostinthecell.actions.Wait;

import java.util.*;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Factory extends Entity {
    private Map<Factory, Integer> nextFactories = new HashMap<>();
    private int productionSize;
    private Set<Troop> comingTroops = new HashSet<>();

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


    public int necessaryCyborgs() {
        return cyborgsCount == 0 ? 1 : cyborgsCount;
    }

    public int compareProductivity(Factory factory) {
        return this.productionSize > factory.productionSize ? 1 : -1;
    }

    public Action action(TreeSet<Factory> neutralFactories, TreeSet<Factory> opponentFactories) {
        if (neutralFactories.size() > 0) {
            Iterator<Factory> it = neutralFactories.iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable()) {
                    return new Move(this, factory, productionSize > 2 ? 2 : 1);
                }
            }
        }

        if (opponentFactories.size() > 0) {
            Iterator<Factory> it = opponentFactories.iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable()) {
                    return new Move(this, factory, productionSize > 2 ? 5 : 3);
                }
            }
        }

        return new Wait();
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
}
