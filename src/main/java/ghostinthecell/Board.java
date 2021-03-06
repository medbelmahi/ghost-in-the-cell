package ghostinthecell;

import ghostinthecell.challenge.actions.Action;
import ghostinthecell.entity.Entity;
import ghostinthecell.entity.Factory;
import ghostinthecell.entity.graph.GraphFindAllPaths;
import ghostinthecell.entity.maker.EntityData;
import ghostinthecell.entity.maker.EntityFactory;
import ghostinthecell.entity.maker.EntityType;

import java.util.*;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Board {

    public static final int ME = 1;
    public static final int OPPONENT = -1;
    public static final int NEUTRAL = 0;

    private Map<Integer, Entity> entities = new HashMap<>();
    private Set<Factory> gameFactories = new HashSet<>();
    public Challenger me;
    public static GraphFindAllPaths<Factory> graph = new GraphFindAllPaths<Factory>();

    private int turn;

    public Board() {
        this.me = new Challenger(entities, this, gameFactories);
        turn = 0;
    }

    public void writeDistance(int factory1, int factory2, int distance) {
        Factory firstFactory = getFactory(factory1);
        Factory secondFactory = getFactory(factory2);

        firstFactory.addDistance(secondFactory, distance);
        secondFactory.addDistance(firstFactory, distance);

        addIntoGraph(firstFactory, secondFactory, distance);
    }

    private void addIntoGraph(Factory firstFactory, Factory secondFactory, int distance) {
        //if (OwnerState.ME.equals(firstFactory.owner()) && OwnerState.ME.equals(secondFactory.owner())) {
            graph.addNode(firstFactory);
            graph.addNode(secondFactory);
            graph.addEdge(firstFactory, secondFactory, distance);
        //}
    }

    private Factory getFactory(int factoryID) {
        Factory factory = (Factory) entities.get(factoryID);
        if (factory == null) {
            factory = new Factory(factoryID, turn);
            entities.put(factoryID, factory);
            gameFactories.add(factory);
        }
        return factory;
    }

    public void updateEntityData(int entityId, String entityType, EntityData entityData) {

        Entity entity = entities.get(entityId);
        if (entity == null) {
            entity = EntityFactory.newEntity(EntityType.valueOf(entityType), entityId, turn);
            entities.put(entityId, entity);
        }
        entity.update(entityData);
    }

    public String doAction() {

        List<Action> actions = me.makeActions();

        String actionsOutput = "";
        for (Action action : actions) {
            actionsOutput += action.writeAction() + ";";
        }

        return actionsOutput + "MSG Amiral";
    }

    public void processing() {
        this.me.processing();
    }

    public void newTurn() {
        this.turn++;
    }

    public boolean isDeadEntity(Entity entity) {
        return entity.deadEntity(turn);
    }
}
