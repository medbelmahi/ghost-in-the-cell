package ghostinthecell.entity.maker;

import ghostinthecell.entity.Bomb;
import ghostinthecell.entity.Entity;
import ghostinthecell.entity.Factory;
import ghostinthecell.entity.Troop;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class EntityFactory {

    public static final String TROOP = "TROOP";
    public static final String FACTORY = "FACTORY";
    public static final String BOMB = "BOMB";

    public static Entity constract(String entityType, int entityId, int turn) {
        switch (entityType) {
            case TROOP :
                return new Troop(entityId, turn);
            case FACTORY :
                return new Factory(entityId, turn);
            case BOMB:
                System.err.println("boooob");
                return new Bomb(entityId, turn);
        }
        return null;
    }
}
