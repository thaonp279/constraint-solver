package solver;

import reader.BinaryCSP;
import reader.BinaryCSPReader;

public class SolverMain {

    /**
     * @author Thao P. Nguyen
     * Solve a constraint problem given the csp file and optional flags.
     * default settings:
     * 1. Solver type: MAC
     * 2. Variable ordering: smallest domain first
     * 3. Print only stats to terminal
     * 
     * optional flags:
     * -forward-checking : set solver type to forward checking
     * -ascending-var : set variable ordering to ascending variable id
     * -solutions-to-stdout : print solutions to terminal
     * 
     * @param args <files.csp> [flags]
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java SolverMain <file.csp>");
            return;
        }

        // parse flags
        int nArgs = args.length;
        String fileName = args[0];
        boolean forwardChecking = false;
        boolean ascendingVarOrder = false;
        boolean showSolutions = false;
        for (int i = 1; i < nArgs; i++) {
            if (args[i].equals("-forward-checking")) {
                forwardChecking = true;
            } else if (args[i].equals("-ascending-var")) {
                ascendingVarOrder = true;
            } else if (args[i].equals("-solutions-to-stdout")) {
                showSolutions = true;
            }
        }

        // set up solver
        BinaryCSPReader reader = new BinaryCSPReader();
        BinaryCSP csp = reader.readBinaryCSP(fileName);
        Solver solver;
        if (forwardChecking) {
            solver = new FCSolver(csp);
        } else {
            solver = new MACSolver(csp);
        }
        solver.solve(ascendingVarOrder, showSolutions);

    }
}
