package ghostinthecell;

import ghostinthecell.entity.maker.EntityData;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
public class Player {

    public static void main(String args[]) {

        Date begin = new Date();

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

        gameBoard.firstTurnCalculator();

        Date end = new Date();
        BigDecimal duration = BigDecimal.valueOf(end.getTime()).subtract(BigDecimal.valueOf(begin.getTime()));
        System.err.println(duration.longValue());

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