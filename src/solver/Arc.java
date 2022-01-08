package solver;
import java.util.ArrayList;

import reader.BinaryTuple;

/**
 * @author Thao P. Nguyen
 * A binary directional constraint between 2 variables.
 * Each tuple represents their acceptable values as a pair.
 */
public class Arc {
    
  private int firstVar, secondVar ;
  private ArrayList<BinaryTuple> tuples ;
  
  public Arc(int fv, int sv, ArrayList<BinaryTuple> t) {
    firstVar = fv ;
    secondVar = sv ;
    tuples = t ;
  }

  /**
   * Check whether the given values for the 2 variables are consistent with the constraint.
   * @param firstVal the value for the first variable
   * @param secondVal the value for the second variable
   */
  public boolean isConsistentWith(int firstVal, int secondVal) {
    for (BinaryTuple tuple : tuples) {
        if (tuple.matches(firstVal, secondVal)) {
            return true;
        }
    } 
    return false;
  }

  /**
   * Check whether the second variable of the arc has the given id
   * @param var an integer value that represents the variable id
   */
  public boolean isTo(int var) {
      if (var == secondVar) {
          return true;
      }
      return false;
  }

  /**
   * Check whether the first variable of the arc has the given id
   * @param var an integer value that represents the variable id
   */
  public boolean isFrom(int var) {
      if (var == firstVar) {
          return true;
      }
      return false;
  }

  public int getFirstVar() {
      return firstVar;
  }

  public int getSecondVar() {
      return secondVar;
  }
}
