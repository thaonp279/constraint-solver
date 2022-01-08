package solver;

import java.util.ArrayDeque;

import reader.BinaryCSP;

/**
 * @author Thao P. Nguyen
 * Solver that uses forward checking propagation.
 */
public class FCSolver extends Solver {

    public FCSolver(BinaryCSP csp) {
        super(csp);
    }
    
    /**
     * Forward checking propagation.
     * Check future arcs onto current variable.
     * @return boolean value of whether future arcs are consistent with var assignment
     */
    @Override
    protected boolean updateConsistency(Variable var, ArrayDeque<Variable> pruners) {
        ArrayDeque<Arc> futureArcs = arcsCurator.getArcsToExcept(var.getId(), getAssignedId());
        for (Arc arc : futureArcs) {
            arcReviseCounter++;
            Variable futureVar = variables.get(arc.getFirstVar());
            if (futureVar.pruneDomain(arc, var)) {
                pruners.add(futureVar);
            }
            if (futureVar.isWipedOut()) {
                return false;
            }
        }
        return true;
    }
}
