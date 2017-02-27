package ghostinthecell.entity;

import ghostinthecell.Board;
import ghostinthecell.Challenger;
import ghostinthecell.entity.maker.EntityData;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public abstract class Entity {
    int cyborgsCount;
    int owner;
    int id;
    int currentTurn = 0;

    public Entity(int id) {
        this.id = id;
    }

    public void update(EntityData entityData) {
        entityData.writeInto(this);
    }

    public abstract void update(int... args);

    public String owner() {
        switch (owner) {
            case Board.ME :
                return Challenger.ME;
                case Board.OPPONENT:
                    return Challenger.OPPONENT;
                    case Board.NEUTRAL:
                        return Challenger.NEUTRAL;
        }
        return "";
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

}
