import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.util.List;
import java.awt.*;
import java.util.*;





/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Board {

    public static final int ME = 1;
    public static final int OPPONENT = -1;
    public static final int NEUTRAL = 0;

    Map<Integer, Entity> entities = new HashMap<>();
    Challenger me;

    public Board() {
        this.me = new Challenger(entities);
    }

    public void writeDistance(int factory1, int factory2, int distance) {
        Factory firstFactory = getFactory(factory1);
        Factory secondFactory = getFactory(factory2);

        firstFactory.addDistance(secondFactory, distance);
        secondFactory.addDistance(firstFactory, distance);

        entities.put(factory1, firstFactory);
        entities.put(factory2, secondFactory);
    }

    private Factory getFactory(int factoryID) {
        Factory factory = (Factory) entities.get(factoryID);
        if (factory == null) {
            factory = new Factory(factoryID);
        }
        return factory;
    }

    public void updateEntityData(int entityId, String entityType, EntityData entityData) {

        System.err.println("entity id : " + entityId);

        Entity entity = entities.get(entityId);
        if (entity == null) {
            entity = EntityFactory.constract(entityType, entityId);
        }
        entity.update(entityData);
    }

    public String doAction() {

        Action action = me.makeAction();

        return action.writeAction();
    }

    public void processing() {
        this.me.processing();
    }
}




/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Challenger {

    private Map<Integer, Entity> entities;
    private List<Factory> myFactories = new ArrayList<>();
    private List<Factory> opponentFactories = new ArrayList<>();
    private List<Factory> neutralFactories = new ArrayList<>();

    public Challenger(Map<Integer, Entity> entities) {
        this.entities = entities;
    }

    public Action makeAction() {

        for (Factory neutralFactory : neutralFactories) {
            for (Factory myFactory : myFactories) {
                if (myFactory.cyborgsMoreThenOrEqual(neutralFactory)) {
                    return new Move(myFactory, neutralFactory, myFactory.necessaryCyborgs());
                }
            }
        }

        for (Factory opponentFactory : opponentFactories) {
            for (Factory myFactory : myFactories) {
                if (myFactory.cyborgsMoreThenOrEqual(opponentFactory)) {
                    return new Move(myFactory, opponentFactory, myFactory.necessaryCyborgs());
                }
            }
        }

        return new Wait();
    }

    public void processing() {

        myFactories.clear();
        opponentFactories.clear();
        neutralFactories.clear();

        for (Entity entity : entities.values()) {
            if (entity instanceof Factory) {
                Factory factory = (Factory) entity;
                switch (factory.owner()) {
                    case "ME" : myFactories.add(factory); break;
                    case "OPPONENT" : opponentFactories.add(factory); break;
                    case "NEUTRAL" : neutralFactories.add(factory); break;
                }
            }
        }
    }
}




/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Action {

    private String printedValue;

    public Action(String printedValue) {
        this.printedValue = printedValue;
    }

    public String writeAction() {
        return this.printedValue;
    }
}



/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Move extends Action {

    public static final String SPACE = " ";
    int source;
    int destination;
    int cyborgCount;

    public Move(Factory myFactory, Factory neutralFactory, int i) {
        super("MOVE");
        this.source = myFactory.id();
        this.destination = neutralFactory.id();
        this.cyborgCount = i;
    }

    @Override
    public String writeAction() {
        return super.writeAction() + SPACE + this.source + SPACE + this.destination + SPACE + this.cyborgCount;
    }
}


/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Wait extends Action {

    public Wait() {
        super("WAIT");
    }
}



/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
abstract class Entity {
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



/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Factory extends Entity {
    private Map<Factory, Integer> nextFactories = new HashMap<>();
    private int productionSize;

    public Factory(int id) {
        super(id);
    }

    @Override
    public void update(int... args) {
        this.owner = args[0];
        this.cyborgsCount = args[1];
        this.productionSize = args[2];
    }

    public void addDistance(Factory secondFactory, int distance) {
        nextFactories.put(secondFactory, distance);
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((Entity) obj).id;
    }


    public int necessaryCyborgs() {
        return cyborgsCount;
    }
}




/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class EntityData {

    private final int arg1;
    private final int arg2;
    private final int arg3;
    private final int arg4;
    private final int arg5;

    public EntityData(int arg1, int arg2, int arg3, int arg4, int arg5) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.arg4 = arg4;
        this.arg5 = arg5;
    }

    public void writeInto(Entity entity) {
        entity.update(arg1, arg2, arg3, arg4, arg5);
    }
}



/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class EntityFactory {

    public static final String TROOP = "TROOP";
    public static final String FACTORY = "FACTORY";

    public static Entity constract(String entityType, int entityId) {
        switch (entityType) {
            case TROOP :
                return new Troop(entityId);
            case FACTORY :
                return new Factory(entityId);
        }
        return null;
    }
}


/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Troop extends Entity{

    private int source;
    private int target;
    private int remaining;

    public Troop(int id) {
        super(id);
    }

    @Override
    public void update(int... args) {
        this.owner = args[0];
        this.source = args[1];
        this.target = args[2];
        this.cyborgsCount = args[3];
        this.remaining = args[4];
    }
}




/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {

        Board gameBoard = new Board();

        Scanner in = new Scanner(System.in);
        int factoryCount = in.nextInt(); // the number of factories
        int linkCount = in.nextInt(); // the number of links between factories
        for (int i = 0; i < linkCount; i++) {
            int factory1 = in.nextInt();
            int factory2 = in.nextInt();
            int distance = in.nextInt();

            gameBoard.writeDistance(factory1, factory2, distance);
        }

        // game loop
        while (true) {
            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                int arg5 = in.nextInt();

                EntityData entityData = new EntityData(arg1, arg2, arg3, arg4, arg5);
                gameBoard.updateEntityData(entityId, entityType, entityData);
            }

            gameBoard.processing();

            // Write an doAction using System.out.println()
            // To debug: System.err.println("Debug messages...");

            String action = gameBoard.doAction();
            // Any valid doAction, such as "WAIT" or "MOVE source destination cyborgs"
            System.out.println(action);
        }
    }
}