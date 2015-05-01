package graphex;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class DFA{
  private final char EPSILON = '\u03B5';

  HashMap<TreeSet<Integer>, TreeSet<Integer>> transitionTable;
  ArrayList<TreeSet<Integer>> dfaList = new ArrayList<TreeSet<Integer>>();
  ArrayList<TreeSet<Integer>> epsilonClosureTable;
  ArrayList<Character> alphabet;
  NFAnode[] nfaList;
  int currentNode = 0;
  int nfaStart;
  int nfaEnd;

  public DFA(NFAnode[] nfa, int startState, int endState, ArrayList<Character> alpha){
    transitionTable = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
    epsilonClosureTable = new ArrayList<TreeSet<Integer>>();
    alphabet = alpha;
    for(int i = 0; i < nfa.length; i++){
      epsilonClosureTable.add(null);
    }
    nfaList = nfa;
    generateDFA(startState);
    System.out.println("DFA");
    System.out.println(dfaList);
  }

  private class DFAnode{
    public int number;
    public boolean visited = false;
    public HashMap<Character, Integer> edges = new HashMap<Character, Integer>();
    public boolean isStart = false;
    public boolean isAccept = false;

    public DFAnode(int nodeNumber){
      number = nodeNumber;
    }

    public DFAnode(int nodeNumber, boolean isAccepting){
      number = nodeNumber;
      isAccept = isAccepting;
    }
  }

  private void generateDFA(int startState){
    TreeSet<Integer> startNode = epsilonClosure(startState);
    dfaList.add(startNode);
    System.out.println(startNode);
    while(currentNode < dfaList.size()){
      transitionLookup(currentNode);
      currentNode++;
    }
  }

  private void transitionLookup(int nodeNumber){
    TreeSet<Integer> currentSet = dfaList.get(nodeNumber);
    for(char trans : alphabet){
      TreeSet<Integer> tempNode = new TreeSet<Integer>();
      for(int i : currentSet){
        System.out.println("Trying for: " + trans);
        ArrayList<NFAnode> edges = nfaList[i].edges.get(trans);
        if(edges != null){
          System.out.println("Found Edges for: " + trans);
          for(NFAnode n : edges){
            tempNode.add(n.number);
            for(int x : epsilonClosure(n.number)){
              tempNode.add(x);
            }
          }
        }
      }
      if(tempNode.size() > 0) {
        dfaList.add(tempNode);
      }
    }
  }

  private TreeSet<Integer> epsilonClosure(int number){
    if(epsilonClosureTable.get(number) == null) {
      generateEpsilonClosure(number);
    }
    return epsilonClosureTable.get(number);
  }

  private void generateEpsilonClosure(int number){
    if(epsilonClosureTable.get(number) != null) return;

    ArrayList<Integer> set = new ArrayList<Integer>();
    TreeSet<Integer> list = new TreeSet<Integer>();
    list.add(number);

    if(nfaList[number].edges.get(EPSILON) != null){
      for(NFAnode n : nfaList[number].edges.get(EPSILON)){
        set.add(n.number);
        list.add(n.number);
      }
    }

    for(int i = 0; i < set.size(); i++){
      int currentNode = set.get(i);
      if(epsilonClosureTable.get(currentNode) == null){
        if(nfaList[currentNode].edges.get(EPSILON) != null){
          for(NFAnode n : nfaList[currentNode].edges.get(EPSILON)){
            if(!list.contains(n.number)){
              set.add(n.number);
              list.add(n.number);
            }
          }
        }
      }
      else{
        for(int node: epsilonClosureTable.get(currentNode)){
          if(!list.contains(node)){
            set.add(node);
            list.add(node);
          }
        }
      }
    }
    epsilonClosureTable.set(number, list);
    //System.out.println("Added to " + number + ", The Set: " + list);
  }
}