package ghostinthecell.challenge.actions;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Action {
    public static final String SPACE = " ";

    private String printedValue;

    public Action(String printedValue) {
        this.printedValue = printedValue;
    }

    public String writeAction() {
        return this.printedValue;
    }
}
