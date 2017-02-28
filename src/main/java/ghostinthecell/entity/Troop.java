package ghostinthecell.entity;

import ghostinthecell.Challenger;

import java.util.Map;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Troop extends Tripper{

    public Troop(int id, int currentTurn) {
        super(id, currentTurn);
    }

    @Override
    public void update(int... args) {
        super.update(args);
        this.cyborgsCount = args[3];
        this.remaining = args[4];
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public void moveInto(Challenger challenger) {
        challenger.addTroop(this);
    }

    @Override
    public void myFightIsOver() {
        //do nothing
    }

    public void matchFactories(Map<Integer, Entity> entities) {
        this.sourceFactory = (Factory) entities.get(this.source);
        this.targetFactory = (Factory) entities.get(this.target);
        this.targetFactory.addComingTroop(this);
    }
}
