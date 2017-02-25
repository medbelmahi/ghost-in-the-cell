package ghostinthecell.entity;

import ghostinthecell.Board;
import ghostinthecell.entity.maker.EntityData;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public abstract class Entity {
    int cyborgsCount;
    int owner;
    int id;

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
                return "ME";
                case Board.OPPONENT:
                    return "OPPONENT";
                    case Board.NEUTRAL:
                        return "NEUTRAL";
        }
        return "";
    }

    public boolean cyborgsMoreThenOrEqual(Entity entity) {
        return this.cyborgsCount >= entity.cyborgsCount;
    }

    public int id() {
        return id;
    }
}
