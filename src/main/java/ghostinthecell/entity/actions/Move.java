package ghostinthecell.entity.actions;

import ghostinthecell.entity.Factory;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Move extends Action {

    public static final String SPACE = " ";
    int source;
    int destination;
    int cyborgCount;

    public Move(Factory myFactory, Factory neutralFactory, int i) {
        super("MOVE");
        this.source = myFactory.id();
        this.destination = neutralFactory.id();
        this.cyborgCount = i;
    }

    @Override
    public String writeAction() {
        return super.writeAction() + SPACE + this.source + SPACE + this.destination + SPACE + this.cyborgCount;
    }
}
