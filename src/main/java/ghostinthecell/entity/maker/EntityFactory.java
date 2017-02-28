package ghostinthecell.entity.maker;

import ghostinthecell.entity.Bomb;
import ghostinthecell.entity.Entity;
import ghostinthecell.entity.Factory;
import ghostinthecell.entity.Troop;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class EntityFactory {

    public static Entity newEntity(EntityType entityType, int entityId, int turn) {
        switch (entityType) {
            case TROOP :
                return new Troop(entityId, turn);
            case FACTORY :
                return new Factory(entityId, turn);
            case BOMB:
                return new Bomb(entityId, turn);
        }
        return null;
    }
}
