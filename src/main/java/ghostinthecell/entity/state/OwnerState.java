package ghostinthecell.entity.state;

import ghostinthecell.challenge.strategy.GameStrategy;
import ghostinthecell.challenge.strategy.Standard;
import ghostinthecell.challenge.strategy.UnderAttackByBomb;
import ghostinthecell.entity.Factory;

/**
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
public enum OwnerState {
    ME("ME"){
        @Override
        public void updateStrategy(Factory factory) {
            GameStrategy gameStrategy = new Standard(factory);
            if (factory.isUnderAttackByBomb()) {
                gameStrategy = new UnderAttackByBomb(factory);
            } else if (factory.isUnderAttackByCyborgs()) {
                //gameStrategy = new UnderAttackCyborgs(factory);
            }
            factory.setStrategy(gameStrategy);
        }
    }, OPPONENT("OPPONENT") {
        @Override
        public void updateStrategy(Factory factory) {

        }
    }, NEUTRAL("NEUTRAL") {
        @Override
        public void updateStrategy(Factory factory) {

        }
    }, OTHER("OTHER") {
        @Override
        public void updateStrategy(Factory factory) {
            //do nothing
        }
    };

    private String stateValue;
    OwnerState(String state) {
        this.stateValue = state;
    }

    @Override
    public String toString() {
        return stateValue;
    }

    public abstract void updateStrategy(Factory factory);
}
