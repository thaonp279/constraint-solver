package reader;
/**
 * @author University of St Andrews, CS4402
 * Assumes tuple values are integers
 */
public final class BinaryTuple {
  private int val1, val2 ;
  
  public BinaryTuple(int v1, int v2) {
    val1 = v1 ;
    val2 = v2 ;
  }
  
  public String toString() {
    return "<"+val1+", "+val2+">" ;
  }
  
  public boolean matches(int v1, int v2) {
    return (val1 == v1) && (val2 == v2) ;
  }

  public BinaryTuple getReversedTuple() {
    return new BinaryTuple(val2, val1);
  }
}
