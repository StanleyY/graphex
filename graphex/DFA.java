package graphex;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class DFA{

  HashMap<TreeSet<Integer>, TreeSet<Integer>> transitionTable;
  ArrayList<TreeSet<Integer>> nodes = new ArrayList<TreeSet<Integer>>();
  ArrayList<TreeSet<Integer>> epsilonClosureTable;
  NFAnode[] nfaList;

  public DFA(NFAnode[] nfa, int startState){
    transitionTable = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
    epsilonClosureTable = new ArrayList<TreeSet<Integer>>();
    for(int i = 0; i < nfa.length; i++){
      epsilonClosureTable.add(null);
    }
    nfaList = nfa;
    generateDFA(startState);
  }

  private void generateDFA(int startState){
    TreeSet<Integer> startNode = epsilonClosure(startState);
    System.out.println(startNode);
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

    if(nfaList[number].edges.get('ε') != null){
      for(NFAnode n : nfaList[number].edges.get('ε')){
        set.add(n.number);
        list.add(n.number);
      }
    }

    for(int i = 0; i < set.size(); i++){
      int currentNode = set.get(i);
      if(epsilonClosureTable.get(currentNode) == null){
        if(nfaList[currentNode].edges.get('ε') != null){
          for(NFAnode n : nfaList[currentNode].edges.get('ε')){
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