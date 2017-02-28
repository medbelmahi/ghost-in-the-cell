package ghostinthecell.entity;

import ghostinthecell.Board;
import ghostinthecell.Challenger;
import ghostinthecell.entity.maker.EntityData;
import ghostinthecell.entity.state.OwnerState;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public abstract class Entity {
    int cyborgsCount;
    private OwnerState owner;
    int id;
    int currentTurn;

    public Entity(int id, int currentTurn) {
        this.id = id;
        this.currentTurn = currentTurn - 1;
    }

    public void update(EntityData entityData) {
        entityData.writeInto(this);
    }

    public void update(int... args) {
        this.owner = ownerState(args[0]);
    }

    public OwnerState ownerState(int ownerInput) {
        switch (ownerInput) {
            case Board.ME :
                return OwnerState.ME;
            case Board.OPPONENT:
                return OwnerState.OPPONENT;
            case Board.NEUTRAL:
                return OwnerState.NEUTRAL;
        }
        return OwnerState.OTHER;
    }

    public int id() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((Entity) obj).id;
    }

    public abstract void moveInto(Challenger challenger);

    public void nextTurn() {
        currentTurn++;
    }

    public boolean deadEntity(int turn) {
        return currentTurn < turn;
    }

    public OwnerState owner() {
        return owner;
    }

    public abstract void myFightIsOver();
}
