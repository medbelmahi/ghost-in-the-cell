package ghostinthecell.entity;

import java.util.Map;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Troop extends Entity{

    private int source;
    private int target;
    private int remaining;
    private Factory sourceFactory;
    private Factory targetFactory;

    public Troop(int id) {
        super(id);
    }

    @Override
    public void update(int... args) {
        this.owner = args[0];
        this.source = args[1];
        this.target = args[2];
        this.cyborgsCount = args[3];
        this.remaining = args[4];
    }

    public void matchFactories(Map<Integer, Entity> entities) {
        this.sourceFactory = (Factory) entities.get(this.source);
        this.targetFactory = (Factory) entities.get(this.target);
        this.targetFactory.addComingTroop(this);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
