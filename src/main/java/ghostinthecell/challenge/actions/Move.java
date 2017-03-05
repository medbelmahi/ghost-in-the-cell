package ghostinthecell.challenge.actions;

import ghostinthecell.entity.Factory;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Move extends Action {
    Factory source;
    Factory destination;
    int cyborgCount;

    public Move(Factory myFactory, Factory neutralFactory, int cyborgCount) {
        super("MOVE");
        this.source = myFactory;
        this.destination = neutralFactory;
        this.cyborgCount = cyborgCount;
        myFactory.decreaseCyborgs(cyborgCount);
    }

    @Override
    public String writeAction() {
        return super.writeAction() + SPACE + this.source.id() + SPACE + this.destination.id() + SPACE + this.cyborgCount;
    }
}
