package ghostinthecell.entity;

import ghostinthecell.Challenger;
import ghostinthecell.entity.state.OwnerState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
public class Bomb extends Tripper {

    boolean alreadyWarned = false;
    List<Factory> warnedFactories = new ArrayList<>();

    public Bomb(int entityId, int currentTurn) {
        super(entityId, currentTurn);
    }

    @Override
    public void update(int... args) {
        super.update(args);
        this.remaining = args[3];
        System.err.println("update bomb ...");
    }

    @Override
    public void matchFactories(Map<Integer, Entity> entities) {
        if (OwnerState.ME.equals(this.owner())) {
            this.sourceFactory = (Factory) entities.get(this.source);
            this.targetFactory = (Factory) entities.get(this.target);
        }
    }

    @Override
    public void moveInto(Challenger challenger) {
        challenger.addBomb(this);
    }

    @Override
    public void myFightIsOver() {
        warnedFactories.clear();
    }

    public void warnFactories(TreeSet<Factory> myFactories) {
        if (!alreadyWarned) {
            warnedFactories.addAll(myFactories);
            for (Factory myFactory : myFactories) {
                myFactory.bomb(this);
            }
            alreadyWarned = true;
        }
    }

    public void warnFactory() {
        if (!alreadyWarned) {
            targetFactory.bomb(this);
            this.warnedFactories.add(targetFactory);
            alreadyWarned = true;
        }
    }
}
