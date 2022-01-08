package solver;
import java.util.ArrayDeque;

/**
 * @author Thao P. Nguyen
 * Variable contains their numbered id and their current domain.
 * Variable also saves its previous domain in domainHistory every time its domain is pruned.
 */
public class Variable {
    private int id;
    private ArrayDeque<Integer> domain;
    private ArrayDeque<ArrayDeque<Integer>> domainHistory;

    /**
     * Given its variable id, a lower bound and upper bound,
     * the Variable object generates all possible values in its domain.
     * @param varId numbered id
     * @param lowerBound minimum value (inclusive)
     * @param upperBound maximum value (inclusive)
     */
    public Variable(int varId, int lowerBound, int upperBound) {
        id = varId;
        domain = new ArrayDeque<>();
        for (int d = lowerBound; d <= upperBound; d ++) {
            domain.addLast(d);
        }
        domainHistory = new ArrayDeque<>();
    }

    /**
     * @return whether variable has been assigned a value.
     */
    public boolean isAssigned() {
        if (domain.size() == 1) {
            return true;
        }
        return false;
    }

    public int getAssignedVal() throws IllegalStateException {
        if (isAssigned()) {
            return domain.getFirst();
        }
        throw new IllegalStateException("Variable hasn't been assigned any value yet.");
    }

    public int assignFirstVal() {
        saveDomainHistory();
        int val = domain.getFirst();
        domain.clear();
        domain.addLast(val);
        return val;
    }

    public int removeFirstVal() {
        saveDomainHistory();
        return domain.removeFirst();
    }

    /**
     * Update variable's domain to contain only those consistent with the given arc.
     * @param arc the arc from this variable to the secondVar
     * @param secondVar the second variable on the arc, the var that provides support to this variable.
     * @return whether a change to the domain has been made.
     */
    public boolean pruneDomain(Arc arc, Variable secondVar) {
        boolean changed = false;
        ArrayDeque<Integer> newDomain = domain.clone();
        for (int firstVal : domain) {
            // find support
            boolean supported = false;
            for (int secondVal : secondVar.getDomain()) {
                if (arc.isConsistentWith(firstVal, secondVal)) {
                    supported = true;
                    break;
                }
            }
            // remove unsupported domain value
            if (!supported) {
                newDomain.remove(firstVal);
                changed = true;
            }
        }
        
        // save domain history and update new domain
        if (changed) {
            saveDomainHistory();
            domain = newDomain;
        }
        return changed;
    }

    /**
     * Undo the last domain change.
     */
    public void unprune() {
        domain = domainHistory.removeLast();
    }

    public int getId() {
        return id;
    }

    public ArrayDeque<Integer> getDomain() {
        return domain;
    }

    /**
     * @return whether the domain has been wiped out.
     */
    public boolean isWipedOut() {
        if (domain.size() == 0) {
            return true;
        }
        return false;
    }

    private void saveDomainHistory() {
        ArrayDeque<Integer> clone = domain.clone();
        domainHistory.addLast(clone);
    }

    public String toString() {
        StringBuffer result = new StringBuffer() ;
        result.append("Variable " + id + ":\n");
        result.append("[ ");
        for (int d : domain) {
            result.append(d + " ,");
        }
        result.append("]");
        return result.toString();
    }

}
