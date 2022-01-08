package solver;

import java.util.ArrayDeque;
import java.util.HashMap;

import reader.BinaryCSP;

/**
 * @author Thao P. Nguyen
 * Solver that uses Maintaining Arc Consistency propagation.
 */
public class MACSolver extends Solver {
    public MACSolver(BinaryCSP csp) {
        super(csp);
    } 

    /**
     * Establish initial arc consistency before searching.
     */
    @Override
    public ArrayDeque<HashMap<Integer, Integer>> solve(boolean ascendingVarOrder, boolean showSolutions) {
        restartStats();
        timeStarted = System.currentTimeMillis();
        if (initialConsistency()) {
            return super.solve(ascendingVarOrder, showSolutions);
        }
        // if not initially consistent, show no solutions
        assignments = new ArrayDeque<>();
        solutions = new ArrayDeque<>();
        printResult(showSolutions);
        return solutions;
    }

    /**
     * Initialize arc queues and check for consistency.
     * @return
     */
    private boolean initialConsistency() {
        // record variables that pruned their domains during propagation
        ArrayDeque<Variable> pruners = new ArrayDeque<>();
        arcQ = new ArrayDeque<>(arcsCurator.getAllArcs()) {
            @Override
            public boolean add(Arc arc) { // only add if arc is not already in queue
                return !this.contains(arc) && super.add(arc);
            }
        };
        if (updateConsistency(pruners)) {
            return true;
        }
        undoPruning(pruners);
        return false;
    }

    /**
     * Iterate over arc queues and maintain arc consistency.
     * @return whether all arcs are consistent and no variables have been wiped out.
     */
    private boolean updateConsistency(ArrayDeque<Variable> pruners) {
        while (!arcQ.isEmpty()) {
            arcReviseCounter++;
            Arc arc = arcQ.removeFirst();
            Variable firstVar = variables.get(arc.getFirstVar());
            Variable secondVar = variables.get(arc.getSecondVar());
            if (firstVar.pruneDomain(arc, secondVar)) {
                pruners.add(firstVar);
                if (firstVar.isWipedOut()) {
                    arcQ.clear();
                    return false;
                }
                addArcsSupportedByExcept(firstVar, secondVar);
            }
        }
        return true;
    }

    /**
     * Add arcs impacted by variable assignment then maintain arc consistency.
     */
    @Override
    protected boolean updateConsistency(Variable var, ArrayDeque<Variable> pruners) {
        addArcsSupportedBy(var);
        return updateConsistency(pruners);
    }

    private void addArcsSupportedByExcept(Variable supporter, Variable illegalSupportee) {
        ArrayDeque<Arc> arcs2Add = arcsCurator.getArcsToExcept(supporter.getId(), illegalSupportee.getId());
        for (Arc newArc : arcs2Add) {
            arcQ.addLast(newArc);
        }
    }

    private void addArcsSupportedBy(Variable supporter) {
        ArrayDeque<Arc> arcs2Add = arcsCurator.getArcsTo(supporter.getId());
        for (Arc newArc : arcs2Add) {
            arcQ.addLast(newArc);
        }
    }

}
