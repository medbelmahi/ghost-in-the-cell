import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.util.List;
import java.awt.*;
import java.util.*;






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
        if (OwnerState.ME.equals(this.owner())) {
            this.sourceFactory = (Factory) entities.get(this.source);
            this.targetFactory = (Factory) entities.get(this.target);
        }
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

    public void warnFactory() {
        if (!alreadyWarned) {
            targetFactory.bomb(this);
            this.warnedFactories.add(targetFactory);
            alreadyWarned = true;
        }
    }
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
 * Created by Mohamed BELMAHI on 02/03/2017.
 */



/**
 * Given a connected directed graph, find all paths between any two input points.
 */
class FindAllPaths<T extends Factory> {

    private final GraphFindAllPaths<T> graph;

    /**
     * Takes in a graph. This graph should not be changed by the client
     */
    public FindAllPaths(GraphFindAllPaths<T> graph) {
        if (graph == null) {
            throw new NullPointerException("The input graph cannot be null.");
        }
        this.graph = graph;
    }


    private void validate(T source, T destination) {

        if (source == null) {
            throw new NullPointerException("The source: " + source + " cannot be  null.");
        }
        if (destination == null) {
            throw new NullPointerException("The destination: " + destination + " cannot be  null.");
        }
        if (source.equals(destination)) {
            throw new IllegalArgumentException("The source and destination: " + source + " cannot be the same.");
        }
    }

    /**
     * Returns the list of paths, where path itself is a list of nodes.
     *
     * @param source      the source node
     * @param destination the destination node
     * @return List of all paths
     */
    public List<List<T>> getAllPaths(T source, T destination) {
        validate(source, destination);
        Map<List<T>, Integer> pathWithCost = new HashMap<List<T>, Integer>();

        List<List<T>> paths = new ArrayList<List<T>>();
        List<Integer> totalCost = new ArrayList<Integer>();
        Integer cost = new Integer(0);
        recursive(source, destination, paths, new LinkedHashSet<T>(), totalCost, cost, new HashMap<T, Integer>());
        for (int i = 0; i < paths.size(); i++) {
            pathWithCost.put(paths.get(i), totalCost.get(i));
        }
        return paths;
    }

    // so far this dude ignore's cycles.
    private void recursive(T current, T destination, List<List<T>> paths, LinkedHashSet<T> path, List<Integer> totalCost, Integer cost, Map<T, Integer> allEdges) {
        path.add(current);
        if (allEdges.get(current) != null) {
            cost = cost + allEdges.get(current);
        }
        if (current == destination) {
            cost = cost + allEdges.get(current);
            paths.add(new ArrayList<T>(path));

            cost = cost - allEdges.get(current);
            totalCost.add(cost);
            path.remove(current);
            return;
        }

        allEdges = graph.edgesFrom(current);

        final Set<T> edges = graph.edgesFrom(current).keySet();

        for (T t : edges) {
            if (!path.contains(t)) {
                //System.out.println(t);
                recursive(t, destination, paths, path, totalCost, cost, allEdges);
            }
        }

        path.remove(current);
    }


    /**
     * Returns the list of paths, where path itself is a list of nodes.
     *
     * @param source      the source node
     * @param destination the destination node
     * @return List of all paths
     */
    public Map<List<T>, Integer> getAllPathsWithCost(T source, T destination) {
        validate(source, destination);
        Map<List<T>, Integer> pathWithCost = new HashMap<List<T>, Integer>();

        List<List<T>> paths = new ArrayList<List<T>>();
        List<Integer> totalCost = new ArrayList<Integer>();
        Integer cost = new Integer(0);
        recursiveWithCost(source, destination, paths, new LinkedHashSet<T>(), totalCost, cost, new HashMap<T, Integer>());
        for (int i = 0; i < paths.size(); i++) {
            pathWithCost.put(paths.get(i), totalCost.get(i));
        }
        return pathWithCost;
    }

    // so far this dude ignore's cycles.
    private void recursiveWithCost(T current, T destination, List<List<T>> paths, LinkedHashSet<T> path, List<Integer> totalCost, Integer cost, Map<T, Integer> allEdges) {
        path.add(current);
        if (allEdges.get(current) != null) {
            cost = cost + allEdges.get(current);
        }
        if (current == destination) {
            cost = cost + allEdges.get(current);
            paths.add(new ArrayList<T>(path));

            cost = cost - allEdges.get(current);
            totalCost.add(cost);
            path.remove(current);
            return;
        } else if (OwnerState.ME.equals(current.owner())) {

            allEdges = graph.edgesFrom(current);

            final Set<T> edges = graph.edgesFrom(current).keySet();

            for (T t : edges) {
                if (!path.contains(t)) {
                    //System.out.println(t);
                    recursiveWithCost(t, destination, paths, path, totalCost, cost, allEdges);
                }
            }
        }


        path.remove(current);
    }


    /*public static <T extends Object> void main(String[] args) {
        GraphFindAllPaths<String> graphFindAllPaths = new GraphFindAllPaths<String>();
        graphFindAllPaths.addNode("A");
        graphFindAllPaths.addNode("B");
        graphFindAllPaths.addNode("C");
        graphFindAllPaths.addNode("D");

        graphFindAllPaths.addEdge("A", "B", 10);
        graphFindAllPaths.addEdge("A", "C", 15);
        graphFindAllPaths.addEdge("B", "A", 10);
        graphFindAllPaths.addEdge("C", "A", 15);
        graphFindAllPaths.addEdge("B", "D", 10);
        graphFindAllPaths.addEdge("C", "D", 20);
        graphFindAllPaths.addEdge("D", "B", 10);
        graphFindAllPaths.addEdge("D", "C", 20);

        graphFindAllPaths.addEdge("B", "C", 5);
        graphFindAllPaths.addEdge("C", "B", 5);


        FindAllPaths<String> findAllPaths = new FindAllPaths<String>(graphFindAllPaths);

        System.out.println("All possible Paths : ");
        for (List<String> path : findAllPaths.getAllPaths("D", "A")) {
            System.out.println(path);
        }

        System.out.println("\nAll possible paths with total distance : ");
        Map<List<String>, Integer> pathWithCost = findAllPaths.getAllPathsWithCost("D", "A");

        SortedSet<Map.Entry<List<String>, Integer>> sortedPathWithCost = findAllPaths.sortedPathWithCost("D", "A");

        for (Map.Entry<List<String>, Integer> s : sortedPathWithCost) {
            System.out.println(s);
        }

        // assertEquals(paths, findAllPaths.getAllPaths("A", "D"));
    }*/

    public SortedSet<Map.Entry<List<T>, Integer>> sortedPathWithCost(T source, T destination) {

        Map<List<T>, Integer> pathWithCost = getAllPathsWithCost(source, destination);

        Comparator<Map.Entry<List<T>, Integer>> comparator = new Comparator<Map.Entry<List<T>, Integer>>() {
            @Override
            public int compare(Map.Entry<List<T>, Integer> entry_1, Map.Entry<List<T>, Integer> entry_2) {
                return entry_1.getValue() >= entry_2.getValue() ? 1 : -1;
            }
        };
        SortedSet<Map.Entry<List<T>, Integer>> sortedSet = new TreeSet<Map.Entry<List<T>, Integer>>(comparator);
        sortedSet.addAll(pathWithCost.entrySet());
        return sortedSet;
    }


}




/**
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
class GraphFindAllPaths<T extends Factory> implements Iterable<T> {

    public final Map<T, Map<T, Integer>> graph = new HashMap<>();

    /**
     *  Adds a new node to the graph. If the node already exists then its a
     *  no-op.
     *
     * @param node  Adds to a graph. If node is null then this is a no-op.
     * @return      true if node is added, false otherwise.
     */
    public boolean addNode(T node) {
        if (node == null) {
            throw new NullPointerException("The input node cannot be null.");
        }
        if (graph.containsKey(node)) return false;

        graph.put(node, new HashMap<T, Integer>());
        return true;
    }

    /**
     * Given the source and destination node it would add an arc from source
     * to destination node. If an arc already exists then the value would be
     * updated the new value.
     *
     * @param source                    the source node.
     * @param destination               the destination node.
     * @param distance                    if length if
     * @throws NullPointerException     if source or destination is null.
     * @throws NoSuchElementException   if either source of destination does not exists.
     */
    public void addEdge (T source, T destination, Integer distance) {
        if (source == null || destination == null) {
            throw new NullPointerException("Source and Destination, both should be non-null.");
        }
        if (!graph.containsKey(source) || !graph.containsKey(destination)) {
            throw new NoSuchElementException("Source and Destination, both should be part of graph");
        }
        /* A node would always be added so no point returning true or false */
        graph.get(source).put(destination, distance);
    }


    /**
     * Removes an edge from the graph.
     *
     * @param source        If the source node.
     * @param destination   If the destination node.
     * @throws NullPointerException     if either source or destination specified is null
     * @throws NoSuchElementException   if graph does not contain either source or destination
     */
    public void removeEdge (T source, T destination) {
        if (source == null || destination == null) {
            throw new NullPointerException("Source and Destination, both should be non-null.");
        }
        if (!graph.containsKey(source) || !graph.containsKey(destination)) {
            throw new NoSuchElementException("Source and Destination, both should be part of graph");
        }
        graph.get(source).remove(destination);
    }

    /**
     * Given a node, returns the edges going outward that node,
     * as an immutable map.
     *
     * @param node The node whose edges should be queried.
     * @return An immutable view of the edges leaving that node.
     * @throws NullPointerException   If input node is null.
     * @throws NoSuchElementException If node is not in graph.
     */
    public Map<T, Integer> edgesFrom(T node) {
        if (node == null) {
            throw new NullPointerException("The node should not be null.");
        }
        Map<T, Integer> edges = graph.get(node);
        if (edges == null) {
            throw new NoSuchElementException("Source node does not exist.");
        }
        return Collections.unmodifiableMap(edges);
    }

    /**
     * Returns the iterator that travels the nodes of a graph.
     *
     * @return an iterator that travels the nodes of a graph.
     */
    public Iterator<T> iterator() {
        return graph.keySet().iterator();
    }

    public Integer distance(T factory, T factory1) {
        for (Map.Entry<T, Integer> entry : graph.get(factory).entrySet()) {
            //System.err.println("f1 : " + entry.getValue().id() + " f2 : " + factory1.id());
            if (entry.getKey().equals(factory1)) {
                return entry.getValue();
            }
        }
        return new Integer(0);
    }
}




/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Factory extends Entity {

    public static final int SAFTY_GUARDIANS = 5;

    public Map<Factory, Integer> nextFactories = new HashMap<>();
    public int productionSize;
    private Set<Troop> comingTroops = new HashSet<>();
    private Bomb bomb;
    private GameStrategy usedStrategy;

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

    public int necessaryCyborgs(Factory factory) {

        int necessaryCyborgs = this.cyborgsCount + 1;
        for (Troop comingTroop : comingTroops) {
            if (comingTroop.owner().equals(Challenger.OPPONENT)) {
                necessaryCyborgs += comingTroop.cyborgsCount;
            }
        }
        if (productionSize == 0) {
            necessaryCyborgs += 10;
        }

        Integer distance = Board.graph.distance(this, factory);
        System.err.println("distance : " + distance);
        necessaryCyborgs += distance * this.productionSize;

        return necessaryCyborgs;
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

    public boolean isUnderAttackByBomb() {
        return this.bomb != null && bomb.warnedFactories.contains(this);
    }

    public int score() {
        int score = 0;
        score += productionSize * 5;
        score += 10 / (cyborgsCount + 1);

        return score;
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

    public void updateStrategy() {
        this.owner().updateStrategy(this);
    }

    public void setStrategy(GameStrategy strategy) {
        this.usedStrategy = strategy;
    }

    public List<Action> action(Board game) {
        List<Action> actions = this.usedStrategy.processing(game);
        return actions;
    }

    public boolean cyborgsCountMoreOrEqual(int size) {
        return this.cyborgsCount >= size;
    }

    public int cyborgsToMove(int size) {
        return this.cyborgsCount / size;
    }

    public boolean hasMoreCyborgsThen(int count) {
        return this.cyborgsCount > count;
    }

    public boolean isUnderAttackByCyborgs() {
        for (Troop comingTroop : this.comingTroops) {
            if (OwnerState.OPPONENT.equals(comingTroop.owner())) {
                return true;
            }
        }
        return false;
    }

    public int necessaryCyborgsForSafety() {
        int necessaryCyborgsForSafety = 0;
        for (Troop comingTroop : this.comingTroops) {
            if (OwnerState.OPPONENT.equals(comingTroop.owner())) {
                necessaryCyborgsForSafety += comingTroop.cyborgsCount;
            }
        }

        return necessaryCyborgsForSafety;
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
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
enum OwnerState {
    ME("ME"){
        @Override
        public void updateStrategy(Factory factory) {
            GameStrategy gameStrategy = new Standard(factory);
            if (factory.isUnderAttackByBomb()) {
                gameStrategy = new UnderAttackByBomb(factory);
            } else if (factory.isUnderAttackByCyborgs()) {
                //gameStrategy = new UnderAttackCyborgs(factory);
            }
            factory.setStrategy(gameStrategy);
        }
    }, OPPONENT("OPPONENT") {
        @Override
        public void updateStrategy(Factory factory) {

        }
    }, NEUTRAL("NEUTRAL") {
        @Override
        public void updateStrategy(Factory factory) {

        }
    }, OTHER("OTHER") {
        @Override
        public void updateStrategy(Factory factory) {
            //do nothing
        }
    };

    private String stateValue;
    OwnerState(String state) {
        this.stateValue = state;
    }

    @Override
    public String toString() {
        return stateValue;
    }

    public abstract void updateStrategy(Factory factory);
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
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
enum  EntityType {
    FACTORY("FACTORY"), TROOP("TROOP"), BOMB("BOMB");

    private String name;

    EntityType(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}



/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class EntityFactory {

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



/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
abstract class Entity {
    public int cyborgsCount;
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

    public void decreaseCyborgs(int cyborgCount) {
        this.cyborgsCount -= cyborgCount;
    }

    public boolean hasMoreCyborgs() {
        return cyborgsCount > 0;
    }
}


class Constant {

    private static final int MIN_FACTORY_COUNT = 7;
    private static final int MIN_PRODUCTION_RATE = 0;
    private static final int MAX_PRODUCTION_RATE = 3;
    private static final int MIN_TOTAL_PRODUCTION_RATE = 4;
    private static final int BOMBS_PER_PLAYER = 2;
    private static final int PLAYER_INIT_UNITS_MIN = 15;
    private static final int PLAYER_INIT_UNITS_MAX = 30;
    private static final int WIDTH = 16000;
    private static final int HEIGHT = 6500;
    private static final int EXTRA_SPACE_BETWEEN_FACTORIES = 300;
    private static final int COST_INCREASE_PRODUCTION = 10;
    private static final int DAMAGE_DURATION = 5;
    private static int FACTORY_RADIUS;

}




/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Challenger {

    public static final String ME = "ME";
    public static final String OPPONENT = "OPPONENT";
    public static final String NEUTRAL = "NEUTRAL";

    private Board game;
    private Set<Factory> gameFactories;
    private Map<Integer, Entity> entities;
    public TreeSet<Factory> myFactories = new TreeSet<Factory>(new BadProducerComparator());
    public TreeSet<Factory> opponentFactories = new TreeSet<Factory>(new BestProducerComparator());
    public TreeSet<Factory> neutralFactories = new TreeSet<Factory>(new BestProducerComparator());
    private List<Troop> myTroops = new ArrayList<>();
    private List<Troop> opponentTroops = new ArrayList<>();
    private List<Bomb> myBombs = new ArrayList<>();
    private List<Bomb> opponentBombs = new ArrayList<>();
    private int bombSize;

    public TreeSet<Factory> underMyEyes = new TreeSet<>(new BestProducerComparator());

    public Challenger(Map<Integer, Entity> entities, Board game, Set<Factory> gameFactories) {
        this.entities = entities;
        this.game = game;
        this.gameFactories = gameFactories;
        this.bombSize = 2;
    }

    public List<Action> makeActions() {

        List<Action> actions = new ArrayList<>();

        for (Factory myFactory : myFactories) {
            System.err.println("my factory : " + myFactory.id());
            List<Action> actions_ = myFactory.action(game);
            actions.addAll(actions_);
        }

        if (this.bombSize > 0 && this.myFactories.size() > 0) {
            for (Factory opponentFactory : this.opponentFactories) {
                if (opponentFactory.productionSize == 3 && opponentFactory.cyborgsCount > 20 && !opponentFactory.isUnderAttackByBomb()) {
                    actions.add(new BombAction(opponentFactory.nearFactory(OwnerState.ME), opponentFactory));
                    this.bombSize--;
                    break;
                }
            }
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

        setStrategy();
    }

    private void setStrategy() {
        for (Factory gameFactory : gameFactories) {
            gameFactory.updateStrategy();
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
        bomb.matchFactories(entities);
        switch (bomb.owner()) {
            case ME:
                myBombs.add(bomb);
                bomb.warnFactory();
                break;
            case OPPONENT:
                opponentBombs.add(bomb);
                bomb.warnFactories(myFactories);
                break;
        }
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




/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Board {

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





/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
class BombAction extends Action {

    private final Factory source;
    private final Factory destination;

    public BombAction(Factory source, Factory destination) {
        super("BOMB");
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String writeAction() {
        return super.writeAction() + SPACE + source.id() + SPACE + destination.id();
    }
}


/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Action {
    public static final String SPACE = " ";

    private String printedValue;

    public Action(String printedValue) {
        this.printedValue = printedValue;
    }

    public String writeAction() {
        return this.printedValue;
    }
}



/**
 * Created by Mohamed BELMAHI on 27/02/2017.
 */
class Increase extends Action {

    private final Factory factoryToIncrease;

    public Increase(Factory factory) {
        super("INC");
        factoryToIncrease = factory;
        factory.decreaseCyborgs(10);
    }

    @Override
    public String writeAction() {
        return super.writeAction() + " " + factoryToIncrease.id();
    }
}



/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class Move extends Action {
    Factory source;
    Factory destination;
    int cyborgCount;

    public Move(Factory myFactory, Factory neutralFactory, int cyborgCount) {
        super("MOVE");
        this.source = myFactory;
        this.destination = neutralFactory;
        this.cyborgCount = cyborgCount;
        myFactory.decreaseCyborgs(cyborgCount);
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
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
class Standard extends GameStrategy {

    public Standard(Factory factory) {
        super(factory);
    }

    @Override
    public List<Action> processing(Board game) {

        List<Action> actions = new ArrayList<>();

        new SafetyFirst(this.factory).processing(game);
        int totalFactories = game.me.myFactories.size() + game.me.neutralFactories.size() + game.me.opponentFactories.size();
        if (this.factory.productionSize < 3
                && this.factory.cyborgsCountMoreOrEqual(15)
                && game.me.myFactories.size() >= (totalFactories / 3)) {
            actions.add(new Increase(this.factory));
        }

        boolean done = moveCyborgs(game.me.underMyEyes, actions, true) ||
                moveCyborgs(game.me.neutralFactories, actions, false) ||
                moveCyborgs(game.me.opponentFactories, actions, false);

        actions.add(new Wait());
        return actions;
    }

    private boolean moveCyborgs(TreeSet<Factory> factories, List<Action> actions, boolean useSort) {
        boolean done = false;
        if (factories.size() > 0) {
            Iterator<Factory> it = useSort ? bestTargetSort(factories).iterator() : nearbySort(factories).iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                int necessaryCyborgs = factory.necessaryCyborgs(this.factory);
                if (!factory.isReachable() && this.factory.hasMoreCyborgsThen(necessaryCyborgs)) {
                    actions.add(new Move(this.factory, factory, necessaryCyborgs));

                    done = true;
                }
            }
        }

        return done;
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

    private TreeSet<Factory> nearbySort(Set<Factory> factories) {
        final Factory myFactory = this.factory;
        Comparator<Factory> nearByComparator = new Comparator<Factory>() {
            @Override
            public int compare(Factory o1, Factory o2) {

                //if (o1.productionSize == o2.productionSize) {
                    Integer distance_1 = myFactory.nextFactories.get(o1);
                    Integer distance_2 = myFactory.nextFactories.get(o2);
                    return distance_1 == distance_2 ? 0 : distance_1 > distance_2 ? 1 : -1;
                //}

                //return o1.productionSize < o2.productionSize ? 1 : -1;
            }
        };

        TreeSet<Factory> nearBySortedSet = new TreeSet<>(nearByComparator);

        for (Factory factory : factories) {
            nearBySortedSet.add(factory);
        }

        return nearBySortedSet;
    }
}




/**
 * Created by Mohamed BELMAHI on 04/03/2017.
 */
class SafetyFirst extends GameStrategy{

    public SafetyFirst(Factory factory) {
        super(factory);
    }

    @Override
    public List<Action> processing(Board game) {
        List<Action> actions = new ArrayList<>();

        this.factory.decreaseCyborgs(this.factory.necessaryCyborgsForSafety());

        return actions;
    }
}




/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
class UnderAttackByBomb extends GameStrategy {

    public UnderAttackByBomb(Factory factory) {
        super(factory);
    }


    @Override
    public List<Action> processing(Board game) {
        List<Action> actions = new ArrayList<>();
        System.err.println("id : " + this.factory.id() + " is under attack");


        if (this.factory.productionSize < 3 && this.factory.cyborgsCountMoreOrEqual(10)) {
            actions.add(new Increase(this.factory));
        }/* else {
            if (game.me.underMyEyes.size() > 0) {
                dispatchCyborgs(game.me.underMyEyes, actions);
            } else if (game.me.neutralFactories.size() > 0) {
                dispatchCyborgs(game.me.neutralFactories, actions);
            } else {
                dispatchCyborgs(game.me.opponentFactories, actions);
            }
        }*/

        moveCyborgs(game.me.underMyEyes, actions);
        moveCyborgs(game.me.neutralFactories, actions);
        moveCyborgs(game.me.opponentFactories, actions);
        moveCyborgs(game.me.myFactories, actions);

        actions.add(new Wait());
        return actions;
    }

    private boolean moveCyborgs(TreeSet<Factory> factories, List<Action> actions) {
        boolean done = false;
        if (factories.size() > 0) {
            Iterator<Factory> it = factories.iterator();
            while (it.hasNext()) {
                Factory factory = it.next();
                int necessaryCyborgs1 = factory.necessaryCyborgs(this.factory);
                int necessaryCyborgs = this.factory.cyborgsCountMoreOrEqual(necessaryCyborgs1) ? necessaryCyborgs1 : this.factory.cyborgsCount;
                if (!factory.isUnderAttackByBomb() && this.factory.hasMoreCyborgs()) {
                    actions.add(new Move(this.factory, factory, necessaryCyborgs));
                    done = true;
                }
            }
        }

        return done;
    }

    private void dispatchCyborgs(TreeSet<Factory> factories, List<Action> actions) {
        if (this.factory.cyborgsCountMoreOrEqual(factories.size())) {
            int cyborgsToMove = this.factory.cyborgsToMove(factories.size());
            for (Factory factory : factories) {
                Action action = new Move(this.factory, factory, cyborgsToMove);
                actions.add(action);
            }
        } else {
            for (Factory factory : factories) {
                Action action = new Move(this.factory, factory, 1);
                actions.add(action);
                if (!this.factory.hasMoreCyborgs()) {
                    break;
                }
            }
        }
    }
}




/**
 * Created by Mohamed BELMAHI on 04/03/2017.
 */
class Plan {
    List<Action> actions = new ArrayList<>();
    List<Request> requests = new ArrayList<>();
}




/**
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
class UnderAttackCyborgs extends GameStrategy {


    public UnderAttackCyborgs(Factory factory) {
        super(factory);
    }

    @Override
    public List<Action> processing(Board game) {
        List<Action> actions = new ArrayList<>();

        return null;
    }
}




/**
 * Created by Mohamed BELMAHI on 26/02/2017.
 */
abstract class GameStrategy {
    Factory factory;

    public GameStrategy(Factory factory) {
        this.factory = factory;
    }

    public abstract List<Action> processing(Board game);
}





/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
class BestProducerComparator implements Comparator<Factory> {

    @Override
    public int compare(Factory f1, Factory f2) {
        return f1.score() < f2.score() ? 1 : f1.score() != f2.score() ? -1 : 0;
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