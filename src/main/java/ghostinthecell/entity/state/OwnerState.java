package ghostinthecell.entity.state;

/**
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
public enum OwnerState {
    ME("ME"), OPPONENT("OPPONENT"), NEUTRAL("NEUTRAL"), OTHER("OTHER");

    private String stateValue;
    OwnerState(String state) {
        this.stateValue = state;
    }

    @Override
    public String toString() {
        return stateValue;
    }
}
