package ghostinthecell.entity;

import ghostinthecell.Challenger;
import ghostinthecell.challenge.actions.*;
import ghostinthecell.entity.graph.FindAllPaths;
import ghostinthecell.entity.graph.GraphFindAllPaths;
import ghostinthecell.entity.state.OwnerState;

import java.util.*;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Factory extends Entity {

    public Map<Factory, Integer> nextFactories = new HashMap<>();
    public TreeSet<Factory> sortedByDistance;
    private Map<Factory, Integer> factoryWithDistance;
    LinkedHashMap<Factory, LinkedList<LinkedList<Factory>>> allPathsToFactory = new LinkedHashMap<>();
    public int productionSize;
    private Set<Troop> comingTroops = new HashSet<>();
    private Bomb bomb;
    boolean attackCentre = false;



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

        if (this.owner().equals(OwnerState.OPPONENT)) {
            necessaryCyborgs += (this.factoryWithDistance.get(factory) + 1) * productionSize;
        }

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
        for (Factory factory : sortedByDistance) {
            if (ownerState.equals(factory.owner())) {
                return factory;
            }
        }
        return null;
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

    public void initMe(GraphFindAllPaths<Factory> graph) {
        FindAllPaths<Factory> findAllPaths = new FindAllPaths<>(graph);

        final Map<Factory, Integer> factoryWithDistance = new HashMap<>();
        //init all Paths to other factories
        Set<Factory> otherFactories = graph.edgesFrom(this).keySet();
        for (Factory otherFactory : otherFactories) {
                SortedSet<Map.Entry<List<Factory>, Integer>> sortedPathWithCost = findAllPaths.sortedPathWithCost(this, otherFactory);

                Iterator<Map.Entry<List<Factory>, Integer>> it = sortedPathWithCost.iterator();
                LinkedList<LinkedList<Factory>> linkedListOfPaths = new LinkedList<>();
                while (it.hasNext()) {
                    Map.Entry<List<Factory>, Integer> next = it.next();
                    linkedListOfPaths.add(new LinkedList<Factory>(next.getKey()));
                }
                allPathsToFactory.put(otherFactory, linkedListOfPaths);
                factoryWithDistance.put(otherFactory, graph.distance(this, otherFactory));
        }

        this.factoryWithDistance = factoryWithDistance;

        //init sortedByDistance
        this.sortedByDistance = new TreeSet<>(new Comparator<Factory>() {
            @Override
            public int compare(Factory o1, Factory o2) {

                int distance_1 = factoryWithDistance.get(o1);
                int distance_2 = factoryWithDistance.get(o2);
                return distance_1 >= distance_2 ? 1 : -1;
            }
        });
        this.sortedByDistance.addAll(otherFactories);
    }

    public List<Request> makeRequests() {
        List<Request> requests = new ArrayList<>();
        if (isUnderAttackByCyborgs()) {

            int cyborgsCount = necessaryCyborgsForSafety();
            cyborgsCount = cyborgsCount < this.cyborgsCount ? 0 : cyborgsCount - this.cyborgsCount;
            if (cyborgsCount > 0) {
                requests.add(new Request(this, cyborgsCount, FactoryRequestType.HELP));
                this.cyborgsCount = 0;
            } else {
                this.cyborgsCount -= cyborgsCount;
            }
        }


        return requests;
    }

    public int getDistanceFrom(Factory o1) {
        return factoryWithDistance.get(o1);
    }

    public List<Request> attackRequest(Factory currentTarget) {

        List<Request> requests = new ArrayList<>();
        requests.add(buildAttackRequest(currentTarget));

        for (Factory factory : this.sortedByDistance) {
            if (!factory.equals(currentTarget) && !factory.owner().equals(OwnerState.ME)) {
                requests.add(buildAttackRequest(factory));
            }
        }

        return requests;
    }

    private Request buildAttackRequest(Factory currentTarget) {
        Request request = new Request(this, currentTarget.necessaryCyborgs(this), FactoryRequestType.ATTACK);
        return request;
    }

    public Action reply(Request request) {
        if (this.cyborgsCount > 0) {
            //LinkedList<LinkedList<Factory>> allPaths = allPathsToFactory.get(request.getFrom());
            //for (LinkedList<Factory> path : allPaths) {
                //if (safetyPath(path)) {
                    return request.makeAction(this, request.getFrom(), this.cyborgsCount);
                //}
            //}
        }
        return new Wait();
    }

    private Factory getFirstEle(LinkedList<Factory> path) {
        for (Factory factory : path) {
            if (!factory.equals(this)) {
                return factory;
            }
        }
        return this;
    }

    private boolean safetyPath(LinkedList<Factory> path) {
        for (Factory factory : path) {
            if (!factory.owner().equals(OwnerState.ME)) {
                return false;
            }
        }
        return true;
    }

    public void replyToMyRequest(Request request) {
        request.subNeeds(this.cyborgsCount);
    }

    private Action attackAction(Factory currentTarget) {
        if (this.cyborgsCount > 0 && !currentTarget.isReachable()) {
            int necessaryCyborgs = currentTarget.necessaryCyborgs(this);
            if (necessaryCyborgs <= this.cyborgsCount) {
                return new Move(this, currentTarget, necessaryCyborgs);
            }
            //necessaryCyborgs = necessaryCyborgs < this.cyborgsCount ? necessaryCyborgs : this.cyborgsCount;
        }

        return new Wait();
    }

    public List<Action> attackActions(Factory currentTarget) {
        List<Action> actions = new ArrayList<>();
        actions.add(attackAction(currentTarget));

        for (Factory factory : this.sortedByDistance) {
            if (!factory.equals(currentTarget)) {
                actions.add(attackAction(factory));
            }
        }

        return actions;
    }
}
