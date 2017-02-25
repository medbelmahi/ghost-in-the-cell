package ghostinthecell.entity;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Troop extends Entity{

    private int source;
    private int target;
    private int remaining;

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
}
