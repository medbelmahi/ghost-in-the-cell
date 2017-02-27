package ghostinthecell.entity;

import java.util.Map;

/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
public abstract class Tripper extends Entity {

    protected int source;
    protected int target;
    protected int remaining;
    protected Factory sourceFactory;
    protected Factory targetFactory;

    public Tripper(int id) {
        super(id);
    }

    @Override
    public void update(int... args) {
        this.owner = args[0];
        this.source = args[1];
        this.target = args[2];
    }

    public abstract void matchFactories(Map<Integer, Entity> entities);

}
