package ghostinthecell;

import ghostinthecell.actions.Action;
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


    private Map<Integer, Entity> entities;
    private TreeSet<Factory> myFactories = new TreeSet<Factory>(new BadProducerComparator());
    private TreeSet<Factory> opponentFactories = new TreeSet<Factory>(new BestProducerComparator());
    private TreeSet<Factory> neutralFactories = new TreeSet<Factory>(new BestProducerComparator());
    private List<Troop> myTroops = new ArrayList<>();
    private List<Troop> opponentTroops = new ArrayList<>();

    public Challenger(Map<Integer, Entity> entities) {
        this.entities = entities;
    }

    public List<Action> makeActions() {

        List<Action> actions = new ArrayList<>();

        for (Factory myFactory : myFactories) {
            Action action = myFactory.action(neutralFactories, opponentFactories);
            actions.add(action);
        }

        return actions;
    }

    public void processing() {

        myFactories.clear();
        opponentFactories.clear();
        neutralFactories.clear();

        for (Entity entity : entities.values()) {
            if (entity instanceof Factory) {
                Factory factory = (Factory) entity;
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
            } else {
                Troop troop = (Troop) entity;
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
        }
    }
}
