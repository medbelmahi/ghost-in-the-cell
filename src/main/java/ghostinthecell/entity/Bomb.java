package ghostinthecell.entity;

import ghostinthecell.Challenger;

import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
public class Bomb extends Tripper {

    boolean alreadyWarned = false;

    public Bomb(int entityId) {
        super(entityId);
    }

    @Override
    public void update(int... args) {
        super.update(args);
        this.remaining = args[3];
    }

    @Override
    public void matchFactories(Map<Integer, Entity> entities) {
        //this.sourceFactory = (Factory) entities.get(this.source);
        //this.targetFactory = (Factory) entities.get(this.target);
    }

    @Override
    public void moveInto(Challenger challenger) {
        challenger.addBomb(this);
    }

    public void warnFactories(TreeSet<Factory> myFactories) {
        if (!alreadyWarned) {
            for (Factory myFactory : myFactories) {
                myFactory.bomb(this);
            }
        } else {
            alreadyWarned = true;
        }
    }
}
