package ghostinthecell.challenge.strategy;

import ghostinthecell.Challenger;

/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
public abstract class GameStrategy {
    private int priority;

    public GameStrategy(int priority) {
        this.priority = priority;
    }

    public int compareWith(GameStrategy gameStrategy) {
        int sub = this.priority - gameStrategy.priority;
        return sub == 0 ? 0 : sub > 1 ? 1 : -1 ;
    }

    public abstract void processing(Challenger challenger);
}
