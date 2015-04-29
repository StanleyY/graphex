package graphex;

import java.util.ArrayList;
import java.util.HashMap;

public class NFAnode{
  public int number;
  public boolean visited = false;
  public HashMap<Character, ArrayList<NFAnode>> edges = new HashMap<Character, ArrayList<NFAnode>>();
  public boolean isStartState = false;

  public NFAnode(int nodeNumber){
    number = nodeNumber;
  }

  public void addEdge(char symbol, NFAnode node){
    if(edges.get(symbol) == null){
      edges.put(symbol, new ArrayList<NFAnode>());
    }
    edges.get(symbol).add(node);
  }

  public String toString(){
    return "NFAnode: " + number;
  }
}
