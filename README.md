# Binary Constraint Solver with Forward Checking and MAC Propagation
Provided a binary constraint problem, the solver searches for the solution using 2-way branching and propagates using Forward Checking or MAC.


## Binary Constraint Problem Format
The binary constraint problem is specified by the number of variables, the domain of each of the variable and the constraints. 
- The domain is specified by the format: lower bound, upper bound
- The constraint is specified by the format: c(i, j) for the variables number i, j and followed by the possible tuples of value of i, j

For example, the following constraint problem has 2 variables of the domain 1..3. The 2 variables are be equal.\
2\
1, 3\
1, 3\
c(0, 1)\
1, 1\
2, 2\
3, 3

An example sudoku problem is provided in instances folder.

## Constraint Solver
The solver supports 2 variable ordering strategies: 
- ascending
- smallest-domain first (default)

The solver uses search and propagation to find the solutions.
- Search: implemented recursively with 2-way branching
- Propagation: forward checking, and MAC (default)
    
## Run the Program
1. Compile the program with terminal line\
find . -name '*.java' > sources.txt\
javac @sources.txt

2. Run the program\
java solver.SolverMain <files.csp> [flags]
    
optional flags:\
    -forward-checking : set solver type to forward checking\
    -ascending-var : set variable ordering to ascending variable id\
    -solutions-to-stdout : print solutions to terminal
    
    
# Credit
Constraint problem format and reader component provided by University of St Andrews, CS4402 - Constraint Programming.
