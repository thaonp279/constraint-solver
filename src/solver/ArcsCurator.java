package solver;
import java.util.ArrayDeque;
import java.util.ArrayList;

import reader.BinaryConstraint;
import reader.BinaryTuple;

/**
 * @author Thao P. Nguyen
 * A collection and curator of all arcs of a constraint problem.
 * The ArcsCurator curates the arcs given the requirements on the first and second variables.
 */
public class ArcsCurator {
    private final ArrayDeque<Arc> arcs;

    /**
     * Convert each constraint of the CSP into two directional Arc objects.
     * @param constraints
     */
    public ArcsCurator(ArrayList<BinaryConstraint> constraints) {
        arcs = new ArrayDeque<>();
        for (BinaryConstraint c : constraints) {
            int firstVar = c.getFirstVar();
            int secondVar = c.getSecondVar();
            ArrayList<BinaryTuple> tuples = c.getTuples();

            // add first arc
            arcs.add(new Arc(firstVar, secondVar, tuples));

            // add reversed arc
            ArrayList<BinaryTuple> reversedTuples = new ArrayList<>();
            for (BinaryTuple t : tuples) {
                reversedTuples.add(t.getReversedTuple());
            }
            arcs.add(new Arc(secondVar, firstVar, reversedTuples));
        }
    }

    public ArrayDeque<Arc> getAllArcs() {
        return arcs;
    }

    /**
     * Curate a collection of arcs whose second variable has the given id 
     * and whose first variable is not from the illegalFirstVars set.
     * @param secondVar the sevond variable id
     * @param illegalFirstVars the collection of first variable ids to avoid
     */
    public ArrayDeque<Arc> getArcsToExcept(int secondVar, ArrayDeque<Integer> illegalFirstVars) {
        ArrayDeque<Arc> qualifiedArcs = new ArrayDeque<>();
        for (Arc arc : arcs) {
            if (arc.isTo(secondVar) && !illegalFirstVars.contains(arc.getFirstVar())) {
                qualifiedArcs.addFirst(arc); 
            }
        } 
        return qualifiedArcs;
    }

    /**
     * Curate a collection of arcs whose second variable has the given id 
     * and whose first variable doesn't have the given id.
     * @param secondVar the sevond variable id
     * @param illegalFirstVar the first variable id to avoid
     */
    public ArrayDeque<Arc> getArcsToExcept(int secondVar, int illegalFirstVar) {
        ArrayDeque<Integer> illegalFirstVars = new ArrayDeque<>();
        illegalFirstVars.add(illegalFirstVar);
        return getArcsToExcept(secondVar, illegalFirstVars);
    }

    /**
     * Curate a collection of arcs whose second variable has the given id 
     * @param secondVar the sevond variable id
     */
    public ArrayDeque<Arc> getArcsTo(int secondVar) {
        return getArcsToExcept(secondVar, new ArrayDeque<>());
    }
}
