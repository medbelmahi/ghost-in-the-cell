package ghostinthecell.challenge.actions;

import ghostinthecell.entity.Factory;
import ghostinthecell.entity.state.OwnerState;

import java.util.List;

/**
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
public class Request {
    private final Factory from;
    private int cyborgsCount;
    private final FactoryRequestType requestType;

    public Request(Factory from, int cyborgsCount, FactoryRequestType requestType) {
        this.from = from;
        this.cyborgsCount = cyborgsCount;
        this.requestType = requestType;
    }

    public int priorityCompare(Request o2) {

        if (this.requestType.equals(o2.requestType)) {
            return 0;
        } else if (FactoryRequestType.HELP.equals(this.requestType)) {
            return 1;
        } else if (FactoryRequestType.INCREASE.equals(this.requestType) && FactoryRequestType.ATTACK.equals(o2.requestType)) {
            return 1;
        }

        return -1;
    }

    public boolean addActions(List<Action> actions) {
        this.from.replyToMyRequest(this);

        if (!this.done()) {
            for (Factory factory : this.from.sortedByDistance) {
                if (factory.owner().equals(OwnerState.ME)) {
                    if (this.done()) {
                        return true;
                    } else {
                        actions.add(factory.reply(this));
                    }
                }
            }
        }

        return this.done();
    }

    private boolean done() {
        return this.cyborgsCount <= 0;
    }


    public Action makeAction(Factory factory, Factory from, int cyborgsCount) {
        int necessaryCyborgs = this.cyborgsCount < cyborgsCount ? this.cyborgsCount : cyborgsCount;
        return new Move(factory, from, necessaryCyborgs);
    }

    public void subNeeds(int cyborgsCount) {
        this.cyborgsCount -= cyborgsCount;
    }


    public Factory getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return "from : " + from.id() + " cyborgsCount : " + cyborgsCount + " type : "+requestType.toString();
    }
}
