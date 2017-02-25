package ghostinthecell;

import ghostinthecell.entity.Entity;
import ghostinthecell.entity.Factory;
import ghostinthecell.entity.maker.EntityData;
import ghostinthecell.entity.maker.EntityFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Board {

    public static final int ME = 1;
    public static final int OPPONENT = -1;
    public static final int NEUTRAL = 0;

    Map<Integer, Entity> entities = new HashMap<>();


    public void writeDistance(int factory1, int factory2, int distance) {
        Factory firstFactory = getFactory(factory1);
        Factory secondFactory = getFactory(factory2);

        firstFactory.addDistance(secondFactory, distance);
        secondFactory.addDistance(firstFactory, distance);
    }

    private Factory getFactory(int factoryID) {
        Factory factory = (Factory) entities.get(factoryID);
        if (factory == null) {
            factory = new Factory(factoryID);
        }
        return factory;
    }

    public void updateEntityData(int entityId, String entityType, EntityData entityData) {
        Entity entity = entities.get(entityId);
        if (entity == null) {
            entity = EntityFactory.constract(entityType, entityId);
        }
        entity.update(entityData);
    }
}
