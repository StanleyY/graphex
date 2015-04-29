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
      root = generateNewNode();
      generateNFA(root);
    } catch (InvalidException e){
      e.printStackTrace();
      System.exit(1);
    }
  }

  private class NFAnode{
    int number;
    boolean visited = false;
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

  private NFAnode generateNewNode(){
    NFAnode node = new NFAnode(nodeNumber);
    nodeNumber++;
    return node;
  }

  private void generateNFA(NFAnode root) throws InvalidException{
    if(input[parsePosition] == '('){
      parseParens(root);
    }
    else {
      parseChar(root);
    }
  }

  private void parseChar(NFAnode start){
    NFAnode end = generateNewNode();
    System.out.println("adding " + input[parsePosition]);
    if(parsePosition + 1 < input.length){
      if(input[parsePosition + 1] == '*'){
        start.addEdge(input[parsePosition], end);
        parsePosition++;
        parseStar(start, end);
      }
      else{
        start.addEdge(input[parsePosition], end);
        parsePosition++;
      }
    }
    else{
      start.addEdge(input[parsePosition], end);
      parsePosition++;
    }
    parseNext(end);
  }

  private void parseStar(NFAnode start, NFAnode end){
    System.out.println("adding *");
    NFAnode original_start = generateNewNode();
    NFAnode new_end = generateNewNode();
    int temp = original_start.number;

    // Swapping reference
    original_start.number = start.number;
    original_start.edges = start.edges;
    start.number = temp;
    start.edges = new HashMap<Character, ArrayList<NFAnode>>();

    start.addEdge('ε', original_start);
    start.addEdge('ε', new_end);
    end.addEdge('ε', original_start);
    end.addEdge('ε', new_end);

    parsePosition++;
    parseNext(new_end);
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
    if(!node.visited){
      node.visited = true;
      for(Character transition : node.edges.keySet()){
        for(NFAnode n : node.edges.get(transition)){
          out.println(String.format(EDGE_DOT_FORMAT, node.number, n.number, transition));
          generateNodeDOT(out, n);
        }
      }
    }
  }

}