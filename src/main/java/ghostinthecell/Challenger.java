package ghostinthecell;

import ghostinthecell.entity.Entity;
import ghostinthecell.entity.Factory;
import ghostinthecell.entity.actions.Action;
import ghostinthecell.entity.actions.Move;
import ghostinthecell.entity.actions.Wait;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mohamed BELMAHI on 25/02/2017.
 */
public class Challenger {

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
