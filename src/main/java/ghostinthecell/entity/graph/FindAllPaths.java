package ghostinthecell.entity.graph;

/**
 * Created by Mohamed BELMAHI on 02/03/2017.
 */

import ghostinthecell.entity.Factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Given a connected directed graph, find all paths between any two input points.
 */
public class FindAllPaths<T extends Object> {

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
        }

        allEdges = graph.edgesFrom(current);

        final Set<T> edges = graph.edgesFrom(current).keySet();

        for (T t : edges) {
            if (!path.contains(t)) {
                //System.out.println(t);
                recursiveWithCost(t, destination, paths, path, totalCost, cost, allEdges);
            }
        }

        path.remove(current);
    }


    public static void main(String[] args) {
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
        for (Map.Entry<List<String>, Integer> s : pathWithCost.entrySet()) {
            System.out.println(s);
        }

        // assertEquals(paths, findAllPaths.getAllPaths("A", "D"));
    }


}