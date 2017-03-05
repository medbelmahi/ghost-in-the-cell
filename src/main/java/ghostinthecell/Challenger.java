package ghostinthecell;

import ghostinthecell.challenge.actions.*;
import ghostinthecell.custom.BadProducerComparator;
import ghostinthecell.custom.BestProducerComparator;
import ghostinthecell.custom.RequestPriorityComparator;
import ghostinthecell.entity.Bomb;
import ghostinthecell.entity.Entity;
import ghostinthecell.entity.Factory;
import ghostinthecell.entity.Troop;
import ghostinthecell.entity.state.OwnerState;

import java.util.*;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Challenger {

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
    private TreeSet<Request> requests = new TreeSet<>(new RequestPriorityComparator());

    private Factory currentTarget;
    private Factory currentAttackCentre;

    public Challenger(Map<Integer, Entity> entities, Board game, Set<Factory> gameFactories) {
        this.entities = entities;
        this.game = game;
        this.gameFactories = gameFactories;
        this.bombSize = 2;
    }

    public List<Action> makeActions() {

        List<Action> actions = new ArrayList<>();

        List<Request> requests = new ArrayList<>();
        for (Factory myFactory : myFactories) {
            requests.addAll(myFactory.makeRequests());
        }

        if (currentAttackCentre == null || !currentAttackCentre.owner().equals(OwnerState.ME)) {
            return actions;
        }
        requests.addAll(currentAttackCentre.attackRequest(currentTarget));


        System.err.println("request size : " + requests.size());

        increaseAction(actions, requests);

        for (Request request : requests) {
            System.err.println(request.toString());
            request.addActions(actions);
        }

        actions.addAll(currentAttackCentre.attackActions(currentTarget));

        bombAction(actions);

        for (Action action : actions) {
            System.err.println("action : " + action.writeAction());
        }

        return actions;
    }

    private void increaseAction(List<Action> actions, List<Request> requests) {

        if (game.me.myFactories.size() >= (game.gameFactories.size() / 2)) {
            Factory toBeIncreased = nextIncreasable();

            if (toBeIncreased != null) {
                if (toBeIncreased.cyborgsCountMoreOrEqual(10)) {
                    actions.add(new Increase(toBeIncreased));
                } else {
                    requests.add(new Request(toBeIncreased, 10, FactoryRequestType.INCREASE));
                }
            }
        }
    }

    private void bombAction(List<Action> actions) {
        if (this.bombSize > 0 && this.myFactories.size() > 0) {
            for (Factory opponentFactory : this.opponentFactories) {
                if (opponentFactory.productionSize == 3 && opponentFactory.cyborgsCount >= 10 && !opponentFactory.isUnderAttackByBomb()) {
                    actions.add(new BombAction(opponentFactory.nearFactory(OwnerState.ME), opponentFactory));
                    this.bombSize--;
                    break;
                }
            }
        }
    }

    public void processing() {

        myFactories.clear();
        myTroops.clear();
        myBombs.clear();

        opponentFactories.clear();
        opponentTroops.clear();
        opponentBombs.clear();

        neutralFactories.clear();

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

        if (currentTarget == null
                || OwnerState.ME.equals(currentTarget.owner())
                || currentAttackCentre == null
                || !OwnerState.ME.equals(currentAttackCentre.owner())) {

            currentTarget = nextTarget();
            currentAttackCentre = currentTarget.nearFactory(OwnerState.ME);

            //System.err.println("currentTarget : " + currentTarget != null ? currentTarget.id() : "null");
            //System.err.println("currentAttackCentre : " + currentAttackCentre != null ? currentAttackCentre.id() : "null");
        }

    }

    private Factory nextIncreasable() {
        TreeSet<Factory> factories = new TreeSet<>(new Comparator<Factory>() {
            @Override
            public int compare(Factory o1, Factory o2) {
                int totalDistance_1 = 0;
                int totalDistance_2 = 0;
                for (Factory myFactory : opponentFactories) {
                    int distance_1 = myFactory.getDistanceFrom(o1);
                    int distance_2 = myFactory.getDistanceFrom(o2);

                    totalDistance_1 += distance_1;
                    totalDistance_2 += distance_2;
                }

                return totalDistance_1 == totalDistance_2 ? 0 : totalDistance_1 < totalDistance_2 ? 1 : -1;
            }
        });

        for (Factory myFactory : myFactories) {
            if (myFactory.productionSize < 3) {
                factories.add(myFactory);
            }
        }

        return factories.size() > 0 ? factories.first() : null;
    }

    private Factory nextTarget() {
        TreeSet<Factory> factories = new TreeSet<>(new Comparator<Factory>() {
            @Override
            public int compare(Factory o1, Factory o2) {
                int totalDistance_1 = 0;
                int totalDistance_2 = 0;
                for (Factory myFactory : myFactories) {
                    int distance_1 = myFactory.getDistanceFrom(o1);
                    int distance_2 = myFactory.getDistanceFrom(o2);

                    totalDistance_1 += distance_1;
                    totalDistance_2 += distance_2;
                }

                return totalDistance_1 == totalDistance_2 ? 0 : totalDistance_1 > totalDistance_2 ? 1 : -1;
            }
        });

        factories.addAll(neutralFactories);
        factories.addAll(opponentFactories);
        return factories.first();
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
