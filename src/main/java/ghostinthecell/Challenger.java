package ghostinthecell;

import ghostinthecell.challenge.actions.Action;
import ghostinthecell.custom.BadProducerComparator;
import ghostinthecell.custom.BestProducerComparator;
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

    public TreeSet<Factory> underMyEyes = new TreeSet<>(new BestProducerComparator());

    public Challenger(Map<Integer, Entity> entities, Board game, Set<Factory> gameFactories) {
        this.entities = entities;
        this.game = game;
        this.gameFactories = gameFactories;
    }

    public List<Action> makeActions() {

        List<Action> actions = new ArrayList<>();

        for (Factory myFactory : myFactories) {
            List<Action> actions_ = myFactory.action(game);
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
