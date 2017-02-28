package ghostinthecell.entity.maker;

/**
 * Created by Mohamed BELMAHI on 28/02/2017.
 */
public enum  EntityType {
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
