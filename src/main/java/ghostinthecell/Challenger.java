package ghostinthecell;

import ghostinthecell.challenge.actions.Action;
import ghostinthecell.challenge.strategy.GameStrategy;
import ghostinthecell.challenge.strategy.UnderAttack;
import ghostinthecell.custom.BadProducerComparator;
import ghostinthecell.custom.BestProducerComparator;
import ghostinthecell.custom.PriorityComparator;
import ghostinthecell.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Challenger {

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

    public Challenger(Map<Integer, Entity> entities, Board game) {
        this.entities = entities;
        this.game = game;

        this.gameStrategies.add(new UnderAttack());
    }

    public List<Action> makeActions() {

        List<Action> actions = new ArrayList<>();

        for (Factory myFactory : myFactories) {
            List<Action> actions_ = myFactory.action(neutralFactories, opponentFactories);
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

        for (Entity entity : entities.values()) {
            if (game.isDeadEntity(entity)) {
                entities.remove(entity.id());
            } else {
                entity.moveInto(this);
            }
        }

        for (GameStrategy gameStrategy : gameStrategies) {
            gameStrategy.processing(this);
        }

        for (Factory neutralFactory : neutralFactories) {
            System.err.println("id " + neutralFactory.id());
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
