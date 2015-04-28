package graphex;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class NFA{

  String EDGE_DOT_FORMAT = "%s -> %s[label=\"%s\"];";

  NFAnode root;
  int nodeNumber = 0;
  char[] input;
  int parsePosition = 0;

  public NFA(String regex){
    try{
      input = regex.toCharArray();
      root = generateNFA();
    } catch (InvalidException e){
      e.printStackTrace();
      System.exit(1);
    }
  }

  private class NFAnode{
    int number;
    HashMap<Character, ArrayList<NFAnode>> edges = new HashMap<Character, ArrayList<NFAnode>>();

    public NFAnode(int nodeNumber){
      number = nodeNumber;
    }

    public void addEdge(char symbol, NFAnode node){
      if(edges.get(symbol) == null){
        edges.put(symbol, new ArrayList<NFAnode>());
      }
      edges.get(symbol).add(node);
    }
  }

  private class InvalidException extends Exception {
    public InvalidException() {
      super("Invalid Regex");
    }
  }

  private NFAnode generateNFA() throws InvalidException{
    NFAnode start = generateNewNode();

    if(input[parsePosition] == '('){
      parseParens(start);
    }
    else {
      parseChar(start);
    }
    return start;
  }

  private NFAnode generateNewNode(){
    NFAnode node = new NFAnode(nodeNumber);
    nodeNumber++;
    return node;
  }

  private void parseChar(NFAnode start){
    NFAnode end = generateNewNode();
    System.out.println("adding " + input[parsePosition]);
    start.addEdge(input[parsePosition], end);
    parsePosition++;
    parseNext(end);
  }

  private void parseNext(NFAnode start){
    if(parsePosition < input.length){
      System.out.println("PARSING NEXT");
      parseChar(start);
    }
  }

  private void parseParens(NFAnode start){
    parsePosition++; //Consume the open parens
  }

  public void generateDOTfile(){
    try{
      PrintWriter output = new PrintWriter("NFA.dot");
      output.println("digraph {");
      generateNodeDOT(output, root);
      output.println("}");
      output.close();
    } catch (java.io.IOException e){
      e.printStackTrace();
      System.exit(1);
    }
  }

  private void generateNodeDOT(PrintWriter out, NFAnode node){
    for(Character transition : node.edges.keySet()){
      for(NFAnode n : node.edges.get(transition)){
        out.println(String.format(EDGE_DOT_FORMAT, node.number, n.number, transition));
        generateNodeDOT(out, n);
      }
    }
  }

}