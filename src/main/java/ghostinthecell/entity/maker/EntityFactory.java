package ghostinthecell.entity.maker;

import ghostinthecell.entity.Entity;
import ghostinthecell.entity.Troop;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class EntityFactory {

    public static final String TROOP = "TROOP";

    public static Entity constract(String entityType, int entityId) {
        switch (entityType) {
            case TROOP :
                new Troop(entityId);
        }
        return null;
    }
}
