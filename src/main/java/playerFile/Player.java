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

    int turn;

    public Board() {
        this.me = new Challenger(entities, this);
        turn = 0;
    }

    public void writeDistance(int factory1, int factory2, int distance) {
        Factory firstFactory = getFactory(factory1);
        Factory secondFactory = getFactory(factory2);

        firstFactory.addDistance(secondFactory, distance);
        secondFactory.addDistance(firstFactory, distance);
    }

    private Factory getFactory(int factoryID) {
        Factory factory = (Factory) entities.get(factoryID);
        if (factory == null) {
            factory = new Factory(factoryID, turn);
            entities.put(factoryID, factory);
        }
        return factory;
    }

    public void updateEntityData(int entityId, String entityType, EntityData entityData) {

        Entity entity = entities.get(entityId);
        if (entity == null) {
            entity = EntityFactory.constract(entityType, entityId, turn);
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
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
class BombAction extends Action {


    public BombAction(String printedValue) {
        super("BOMB");
    }
}


/**
 * Created by Mohamed BELMAHI on 27/02/2017.
 */
class Increase extends Action {
    public Increase(String printedValue) {
        super("INC");
    }
}



/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Move extends Action {

    public static final String SPACE = " ";
    Factory source;
    Factory destination;
    int cyborgCount;

    public Move(Factory myFactory, Factory neutralFactory, int cyborgCount) {
        super("MOVE");
        this.source = myFactory;
        this.destination = neutralFactory;
        this.cyborgCount = cyborgCount;
    }

    @Override
    public String writeAction() {
        return super.writeAction() + SPACE + this.source.id() + SPACE + this.destination.id() + SPACE + this.cyborgCount;
    }
}


/**
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
class Request {
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
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
abstract class GameStrategy {
    private int priority;

    public GameStrategy(int priority) {
        this.priority = priority;
    }

    public int compareWith(GameStrategy gameStrategy) {
        int sub = this.priority - gameStrategy.priority;
        return sub == 0 ? 0 : sub > 1 ? 1 : -1 ;
    }

    public abstract void processing(Challenger challenger);
}



/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
class UnderAttackBomb extends GameStrategy {

    public UnderAttackBomb() {
        super(0);
    }

    @Override
    public void processing(Challenger challenger) {
        //boolean isWarned = challenger.warnYourFactories();
    }
}



/**
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
class UnderAttackCyborgs extends GameStrategy {

    public UnderAttackCyborgs(int priority) {
        super(priority);
    }

    @Override
    public void processing(Challenger challenger) {

    }
}




/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Challenger {

    public static final String ME = "ME";
    public static final String OPPONENT = "OPPONENT";
    public static final String NEUTRAL = "NEUTRAL";

    private Board game;
    private Map<Integer, Entity> entities;
    private TreeSet<Factory> myFactories = new TreeSet<Factory>(new BadProducerComparator());
    private TreeSet<Factory> opponentFactories = new TreeSet<Factory>(new BestProducerComparator());
    private TreeSet<Factory> neutralFactories = new TreeSet<Factory>(new BestProducerComparator());
    private TreeSet<GameStrategy> gameStrategies = new TreeSet<>(new PriorityComparator());
    private List<Troop> myTroops = new ArrayList<>();
    private List<Troop> opponentTroops = new ArrayList<>();
    private List<Bomb> myBombs = new ArrayList<>();
    private List<Bomb> opponentBombs = new ArrayList<>();

    private TreeSet<Factory> underMyEyes = new TreeSet<>(new BestProducerComparator());

    public Challenger(Map<Integer, Entity> entities, Board game) {
        this.entities = entities;
        this.game = game;

        this.gameStrategies.add(new UnderAttackBomb());
    }

    public List<Action> makeActions() {

        List<Action> actions = new ArrayList<>();

        for (Factory myFactory : myFactories) {
            List<Action> actions_ = myFactory.action(neutralFactories, opponentFactories, underMyEyes);
            actions.addAll(actions_);
        }

        return actions;
    }

    public void processing() {

        myFactories.clear();
        myTroops.clear();
        myBombs.clear();

        opponentFactories.clear();
        opponentTroops.clear();
        opponentBombs.clear();

        neutralFactories.clear();
        System.err.println("entities size : " + entities.values().size());

        Iterator<Entity> it = entities.values().iterator();
        while (it.hasNext()) {
            Entity entity = it.next();
            if (game.isDeadEntity(entity)) {
                entity.myFightIsOver();
                it.remove();
            } else {
                entity.moveInto(this);
            }
        }

        initUnderMyEyes();

        for (Factory underMyEye : underMyEyes) {
            System.err.println("under my eye : " + underMyEye.id());
        }

        for (GameStrategy gameStrategy : gameStrategies) {
            gameStrategy.processing(this);
        }
    }

    private void initUnderMyEyes() {
        this.underMyEyes.clear();

        for (Factory neutralFactory : neutralFactories) {
            Factory nearFactory = neutralFactory.nearFactory();
            if (nearFactory.owner().equals(OwnerState.ME)) {
                underMyEyes.add(neutralFactory);
            }
        }
    }

    public void addFactory(Factory factory) {
        switch (factory.owner()) {
            case ME:
                myFactories.add(factory);
                break;
            case OPPONENT:
                opponentFactories.add(factory);
                break;
            case NEUTRAL:
                neutralFactories.add(factory);
                break;
        }
    }

    public void addTroop(Troop troop) {
        switch (troop.owner()) {
            case ME:
                myTroops.add(troop);
                break;
            case OPPONENT:
                opponentTroops.add(troop);
                break;
        }
        troop.matchFactories(entities);
    }

    public void addBomb(Bomb bomb) {
        switch (bomb.owner()) {
            case ME:
                myBombs.add(bomb);
                break;
            case OPPONENT:
                opponentBombs.add(bomb);
                bomb.warnFactories(myFactories);
                break;
        }
    }
}





/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class BadProducerComparator implements Comparator<Factory>{

    @Override
    public int compare(Factory f1, Factory f2) {
        return f2.compareProductivity(f1);
    }
}




/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class BestProducerComparator implements Comparator<Factory> {

    @Override
    public int compare(Factory f1, Factory f2) {
        return f1.score() < f2.score() ? 1 : -1;
    }
}




/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
class PriorityComparator implements Comparator<GameStrategy> {
    @Override
    public int compare(GameStrategy s1, GameStrategy s2) {
        return s1.compareWith(s2);
    }
}





/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
class Bomb extends Tripper {

    boolean alreadyWarned = false;
    List<Factory> warnedFactories = new ArrayList<>();

    public Bomb(int entityId, int currentTurn) {
        super(entityId, currentTurn);
    }

    @Override
    public void update(int... args) {
        super.update(args);
        this.remaining = args[3];
        System.err.println("update bomb ...");
    }

    @Override
    public void matchFactories(Map<Integer, Entity> entities) {
        //this.sourceFactory = (Factory) entities.get(this.source);
        //this.targetFactory = (Factory) entities.get(this.target);
    }

    @Override
    public void moveInto(Challenger challenger) {
        challenger.addBomb(this);
    }

    @Override
    public void myFightIsOver() {
        warnedFactories.clear();
    }

    public void warnFactories(TreeSet<Factory> myFactories) {
        if (!alreadyWarned) {
            warnedFactories.addAll(myFactories);
            for (Factory myFactory : myFactories) {
                myFactory.bomb(this);
            }
            alreadyWarned = true;
        }
    }
}



/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
abstract class Entity {
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




/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Factory extends Entity {
    private Map<Factory, Integer> nextFactories = new HashMap<>();
    private int productionSize;
    private Set<Troop> comingTroops = new HashSet<>();
    private Bomb bomb;

    public Factory(int id, int currentTurn) {
        super(id, currentTurn + 1);
    }

    @Override
    public void update(int... args) {
        super.update(args);
        this.cyborgsCount = args[1];
        this.productionSize = args[2];
        comingTroops.clear();
    }

    public void addDistance(Factory secondFactory, int distance) {
        nextFactories.put(secondFactory, distance);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public void moveInto(Challenger challenger) {
        challenger.addFactory(this);
    }

    @Override
    public void myFightIsOver() {
        //do nothing
    }

    public int compareProductivity(Factory factory) {
        return this.productionSize > factory.productionSize ? 1 : -1;
    }

    public List<Action> action(TreeSet<Factory> neutralFactories, TreeSet<Factory> opponentFactories, TreeSet<Factory> underMyEyes) {

        List<Action> actions = new ArrayList<>();
        if (isUnderAttack()) {
            System.err.println("id : " + id + " is under attack");

            if (underMyEyes.size() > 0) {
                dispatchCyborgs(underMyEyes, actions);
            } else if (neutralFactories.size() > 0) {
                dispatchCyborgs(neutralFactories, actions);
            } else {
                dispatchCyborgs(opponentFactories, actions);
            }

            return actions;
        }


        int restCyborgs = this.cyborgsCount;

        if (underMyEyes.size() > 0 && restCyborgs > 6) {
            int cyborgsToMove = productionSize > 2 ? 2 : 1;

            Iterator<Factory> it = bestTargetSort(underMyEyes).iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable() && factory.productionSize > 0) {
                    actions.add(new Move(this, factory, factory.necessaryCyborgs()));
                    restCyborgs -= factory.necessaryCyborgs();

                    if (restCyborgs <= 6) {
                        break;
                    }
                }
            }
            //return actions;
        }

        if (neutralFactories.size() > 0 && restCyborgs > 6) {
            int cyborgsToMove = productionSize > 2 ? 2 : 1;

            Iterator<Factory> it = nearbySort(neutralFactories).iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable() && factory.productionSize > 0) {
                    actions.add(new Move(this, factory, factory.necessaryCyborgs()));
                    restCyborgs -= factory.necessaryCyborgs();

                    if (restCyborgs <= 6) {
                        break;
                    }
                }
            }
            //return actions;
        }


        if (opponentFactories.size() > 0 && restCyborgs > 6) {
            int cyborgsToMove = productionSize > 2 ? 5 : 3;
            Iterator<Factory> it = nearbySort(opponentFactories).iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                if (!factory.isReachable() && factory.productionSize > 0) {
                    actions.add(new Move(this, factory, factory.necessaryCyborgs()));
                    restCyborgs -= factory.necessaryCyborgs();
                    if (restCyborgs <= 5) {
                        break;
                    }
                }
            }
            return actions;
        }
        actions.add(new Wait());
        return actions;
    }

    private TreeSet<Factory> bestTargetSort(TreeSet<Factory> underMyEyes) {
        Comparator<Factory> bestTargetComparator = new Comparator<Factory>() {
            @Override
            public int compare(Factory o1, Factory o2) {

                if (o1.productionSize == o2.productionSize) {
                    //Integer distance_1 = nextFactories.get(o1);
                    //Integer distance_2 = nextFactories.get(o2);

                    //int o1_Factor = distance_1;
                    //int o2_factor = distance_2;

                    //Factory nearOpponentFactory = o1.nearFactory(OwnerState.OPPONENT);

                    Integer distanceFromNearOpponentFactory_1 = o1.nextFactories.get(o1.nearFactory(OwnerState.OPPONENT));
                    Integer distanceFromNearOpponentFactory_2 = o2.nextFactories.get(o2.nearFactory(OwnerState.OPPONENT));

                    return distanceFromNearOpponentFactory_1 < distanceFromNearOpponentFactory_2 ? 1 : -1;
                }

                return o1.productionSize < o2.productionSize ? 1 : -1;
            }
        };

        TreeSet<Factory> nearBySortedSet = new TreeSet<>(bestTargetComparator);

        for (Factory factory : underMyEyes) {
            nearBySortedSet.add(factory);
        }

        return nearBySortedSet;
    }

    private int necessaryCyborgs() {

        int necessaryCyborgs = this.cyborgsCount + 1;
        for (Troop comingTroop : comingTroops) {
            if (comingTroop.owner().equals(Challenger.OPPONENT)) {
                necessaryCyborgs += comingTroop.cyborgsCount;
            }
        }
        return necessaryCyborgs;
    }

    private void dispatchCyborgs(TreeSet<Factory> factories, List<Action> actions) {
        int restCyborgs = this.cyborgsCount;
        if (this.cyborgsCount >= factories.size()) {
            int cyborgsToMove = this.cyborgsCount / factories.size();
            for (Factory factory : factories) {
                Action action = new Move(this, factory, cyborgsToMove);
                actions.add(action);
                /*restCyborgs -= cyborgsToMove;
                if (productionSize == 0 && restCyborgs <= 6) {
                    break;
                }*/
            }
        } else {
            for (Factory factory : factories) {
                Action action = new Move(this, factory, 1);
                actions.add(action);
                restCyborgs--;
                if (restCyborgs == 0) {
                    break;
                }
            }
        }
    }

    public boolean isReachable() {

        int myCyborgsCount = 0;
        int opponentCyborgsCount = 0;

        for (Troop comingTroop : comingTroops) {
            switch (comingTroop.owner().toString()) {
                case Challenger.ME:
                    myCyborgsCount += comingTroop.cyborgsCount;
                    break;
                case Challenger.OPPONENT :
                    opponentCyborgsCount += comingTroop.cyborgsCount;
                    break;
            }
        }
        return myCyborgsCount > opponentCyborgsCount;
    }

    public void addComingTroop(Troop troop) {
        this.comingTroops.add(troop);
    }

    public void bomb(Bomb bomb) {
        this.bomb = bomb;
    }

    private boolean isUnderAttack() {
        return this.bomb != null && bomb.warnedFactories.contains(this);
    }

    public int score() {
        int score = 0;
        score += productionSize * 5;
        score += 10 / (cyborgsCount + 1);

        return score;
    }

    private TreeSet<Factory> nearbySort(Set<Factory> factories) {

        Comparator<Factory> nearByComparator = new Comparator<Factory>() {
            @Override
            public int compare(Factory o1, Factory o2) {

                if (o1.productionSize == o2.productionSize) {
                    Integer distance_1 = nextFactories.get(o1);
                    Integer distance_2 = nextFactories.get(o2);
                    return distance_1 > distance_2 ? 1 : -1;
                }

                return o1.productionSize < o2.productionSize ? 1 : -1;
            }
        };

        TreeSet<Factory> nearBySortedSet = new TreeSet<>(nearByComparator);

        for (Factory factory : factories) {
            nearBySortedSet.add(factory);
        }

        return nearBySortedSet;
    }

    public Factory nearFactory() {
        Integer distance = Integer.MAX_VALUE;
        Factory nearFactory = null;
        for (Map.Entry<Factory, Integer> integerFactory : nextFactories.entrySet()) {
            Integer nesDistance = integerFactory.getValue();
            Factory newFactory = integerFactory.getKey();
            if (OwnerState.ME.equals(newFactory.owner())) {
                if (distance > nesDistance) {
                    nearFactory = newFactory;
                    distance = nesDistance;
                }
            } else if (OwnerState.OPPONENT.equals(newFactory.owner())) {
                if (distance >= nesDistance) {
                    nearFactory = newFactory;
                    distance = nesDistance;
                }
            }
        }
        return nearFactory;
    }

    public Factory nearFactory(OwnerState ownerState) {
        Integer distance = Integer.MAX_VALUE;
        Factory nearFactory = null;
        for (Map.Entry<Factory, Integer> integerFactory : nextFactories.entrySet()) {
            Integer nesDistance = integerFactory.getValue();
            Factory newFactory = integerFactory.getKey();

            if (distance > nesDistance && ownerState.equals(newFactory.owner())) {
                nearFactory = newFactory;
                distance = nesDistance;
            }
        }
        return nearFactory;
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
        entity.nextTurn();
    }
}



/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class EntityFactory {

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



/**
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
enum OwnerState {
    ME("ME"), OPPONENT("OPPONENT"), NEUTRAL("NEUTRAL"), OTHER("OTHER");

    private String stateValue;
    OwnerState(String state) {
        this.stateValue = state;
    }

    @Override
    public String toString() {
        return stateValue;
    }
}



/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
abstract class Tripper extends Entity {

    protected int source;
    protected int target;
    protected int remaining;
    protected Factory sourceFactory;
    protected Factory targetFactory;

    public Tripper(int id, int currentTurn) {
        super(id, currentTurn);
    }

    @Override
    public void update(int... args) {
        super.update(args);
        this.source = args[1];
        this.target = args[2];
    }

    public abstract void matchFactories(Map<Integer, Entity> entities);

}




/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Troop extends Tripper{

    public Troop(int id, int currentTurn) {
        super(id, currentTurn);
    }

    @Override
    public void update(int... args) {
        super.update(args);
        this.cyborgsCount = args[3];
        this.remaining = args[4];
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public void moveInto(Challenger challenger) {
        challenger.addTroop(this);
    }

    @Override
    public void myFightIsOver() {
        //do nothing
    }

    public void matchFactories(Map<Integer, Entity> entities) {
        this.sourceFactory = (Factory) entities.get(this.source);
        this.targetFactory = (Factory) entities.get(this.target);
        this.targetFactory.addComingTroop(this);
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
            //System.err.println(factory1 + " --> " + factory2 + " : " + distance);
            gameBoard.writeDistance(factory1, factory2, distance);
        }

        // game loop
        while (true) {
            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
            System.err.println("entityCount : " + entityCount);
            gameBoard.newTurn();
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