package solver;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import reader.BinaryCSP;

/**
 * @author Thao P. Nguyen
 * The general Solver solves a binary constraint problem.
 * 
 * Solver supports 2 variable ordering strategies: 
 *   - ascending
 *   - smallest-domain first
 * 
 * Solver uses search and propagation to find the solutions.
 *   - Search: implemented recursively using the main recurse method, findNextVar.
 *             findNextVar calls branchLeft and branchRight methods to employ 2-way branching.
 *   - Propagation: propagation strategies specified by updateConsistency method.
 * 
 * Subclasses (ForwardChecking and MAC) extends the general Solver and override:
 *   - updateConsistency: to specify propagation algorithm
 */
public abstract class Solver {

    protected HashMap<Integer, Variable> variables;
    protected ArcsCurator arcsCurator;

    protected PriorityQueue<Variable> varQ;
    protected ArrayDeque<Arc> arcQ;
    protected ArrayDeque<Variable> assignments;
    protected ArrayDeque<HashMap<Integer, Integer>> solutions;
    protected int nodeCounter = 0;
    protected int arcReviseCounter = 0;
    protected Long timeStarted;
    protected Long timeTaken;

    public Solver(BinaryCSP csp) {
        // map id to Variable objects
        variables = new HashMap<>();
        for (int varId = 0; varId < csp.getNoVariables(); varId++) {
            variables.put(varId, new Variable(varId, csp.getLB(varId), csp.getUB(varId)));
        }
        arcsCurator = new ArcsCurator(csp.getConstraints());
    }

    protected abstract boolean updateConsistency(Variable var, ArrayDeque<Variable> pruners);

    /**
     * Solve constraint problem and print solving stats to terminal
     * with option to change variable ordering strategy and option to print all solutions.
     * @param ascendingVarOrder true if choosing natural ascending variable order, false if smallest domain first
     * @param showSolutions if print all solutions to screen
     * @return all possible solutions to the CSP
     */
    public ArrayDeque<HashMap<Integer, Integer>> solve(boolean ascendingVarOrder, boolean showSolutions) {
        // start timer if timer hasn't been started by override method
        if (timeStarted == null) { 
            restartStats();
            timeStarted = System.currentTimeMillis();
        }
        
        // set up variable queue
        if (ascendingVarOrder) {
            setAscendingVarQ();
        } else {
            setSmallestDomainVarQ();
        }
        assignments = new ArrayDeque<>();
        solutions = new ArrayDeque<>();

        // start solving
        findNextVar();
        printResult(showSolutions);
        return solutions;
    }

    /**
     * Search for assignment of the next unassigned variable in queue.
     */
    protected void findNextVar() {
        nodeCounter++;
        // check if all variables have been assigned
        if (!varQ.isEmpty()) {
            Variable var = varQ.poll();
            branchLeft(var);
            branchRight(var);
        } else {
            // save solution
            HashMap<Integer, Integer> solution = new HashMap<>();
            for (Variable var: assignments) {
                solution.put(var.getId(), var.getAssignedVal());
            }
            solutions.add(solution);
        }
    }

    /**
     * Assign first value in domain to variable and propagate.
     */
    private void branchLeft(Variable var) {
        // record variables that have pruned their domain during propagation
        ArrayDeque<Variable> pruners = new ArrayDeque<>();
        // assign first value to variable
        assignments.addLast(var);
        var.assignFirstVal();
        if (updateConsistency(var, pruners)) {// propagate and check for consistency
            findNextVar();
        }
        undoPruning(pruners);
        undoAssign(var);
    }


    /**
     * Remove first value in domain from variable and propagate.
     */
    private void branchRight(Variable var) {
        // record variables that have pruned their domain during propagation
        ArrayDeque<Variable> pruners = new ArrayDeque<>();
        // remove first value from variable
        var.removeFirstVal();
        varQ.add(var);
        if (!var.isWipedOut()) {
            if (updateConsistency(var, pruners)) {// propagate and check for consistency
                findNextVar();
            }
        }
        undoPruning(pruners);
        var.unprune(); // restore value
    }


    private void undoAssign(Variable var) {
        assignments.removeLast();
        var.unprune();
    }

    /**
     * Restore all pruned values during propagation.
     * @param pruners vairables that have pruned their domains.
     */
    protected void undoPruning(ArrayDeque<Variable> pruners) {
        for (Variable p : pruners) {
            p.unprune();
        }
    }

    /**
     * Initialize a variable priority queue with ascending order of variable ids.
     */
    public void setAscendingVarQ() {
        // compare strategy for priority queue
        Comparator<Variable> ascendingOrder = new Comparator<Variable>() {
            @Override
            public int compare(Variable firstVar, Variable secondVar) {
                return firstVar.getId() - secondVar.getId();
            }
        };

        // set up queue and populate
        varQ = new PriorityQueue<>(variables.size(), ascendingOrder);
        populateVarQ();
    }

    /**
     * Initialize a variable priority queue with smallest domain first.
     */
    public void setSmallestDomainVarQ() {
        // compare strategy for priority queue
        Comparator<Variable> smallestDomain = new Comparator<Variable>() {
            @Override
            public int compare(Variable firstVar, Variable secondVar) {
                return firstVar.getDomain().size() - secondVar.getDomain().size();
            }
        };

        // set up queue and populate
        varQ = new PriorityQueue<>(variables.size(), smallestDomain);
        populateVarQ();
    }

    private void populateVarQ() {
        for (int varId : variables.keySet()) {
            varQ.add(variables.get(varId));
        }
    }

    /**
     * Get the variable ids of all assigned variables.
     */
    public ArrayDeque<Integer> getAssignedId() {
        ArrayDeque<Integer> assignedId = new ArrayDeque<>();
        for (Variable var : assignments) {
            assignedId.add(var.getId()); 
        }
        return assignedId;
    }

    public void printResult(boolean showSolutions) {
        timeTaken = System.currentTimeMillis() - timeStarted;
        System.out.println(String.format("There are %d solutions.", solutions.size()));
        System.out.println("Time taken: " + timeTaken);
        System.out.println("Nodes used: " + nodeCounter);
        System.out.println("Arc revisions: " + arcReviseCounter);
        if (showSolutions) {
            for (HashMap<Integer, Integer> sol : solutions) {
                System.out.println("Sol 1: " + sol.toString());
            }
        }
    }

    protected void restartStats() {
        timeStarted = null;
        nodeCounter = 0;
        arcReviseCounter = 0;
        timeTaken = null;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public int getNodeCounts() {
        return nodeCounter;
    }

    public int getArcReviseCounts() {
        return arcReviseCounter;
    }

    public int getSolutionCounts() {
        return solutions.size();
    }
}