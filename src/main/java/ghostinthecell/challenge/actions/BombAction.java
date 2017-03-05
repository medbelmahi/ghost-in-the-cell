package ghostinthecell.challenge.actions;

import ghostinthecell.entity.Factory;

/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
public class BombAction extends Action {

    private final Factory source;
    private final Factory destination;

    public BombAction(Factory source, Factory destination) {
        super("BOMB");
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String writeAction() {
        return super.writeAction() + SPACE + source.id() + SPACE + destination.id();
    }
}
