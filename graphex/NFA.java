package graphex;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class NFA{

  private String EDGE_DOT_FORMAT = "%s -> %s[label=\"%s\"];";

  public NFAnode root;
  public NFAnode[] nodeList;
  public int startState = 0;
  private int nodeNumber = 0;
  private char[] input;
  private int parsePosition = 0;

  public NFA(String regex){
    input = regex.toCharArray();
    root = generateNewNode();
    root.isStartState = true;
    generateNFA(root);
    nodeList = new NFAnode[nodeNumber];
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

  private void generateNFA(NFAnode root){
    if(input[parsePosition] == '('){
      try{
        parseNext(parseParens(root));
      } catch (InvalidException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
    else {
      parseNext(parseChar(root));
    }
  }

  private NFAnode parseChar(NFAnode start){
    System.out.println("parsing " + input[parsePosition]);
    if(parsePosition + 1 < input.length){
      if(input[parsePosition] == '('){
        try{
          return parseParens(start);
        } catch (InvalidException e) {
          e.printStackTrace();
          System.exit(1);
        }
      }else if(input[parsePosition + 1] == '*'){
        NFAnode end = generateNewNode();
        start.addEdge(input[parsePosition], end);
        parsePosition++;
        end = parseStar(start, end);
        return end;
      }else if(input[parsePosition + 1] == '|'){
        NFAnode end = generateNewNode();
        start.addEdge(input[parsePosition], end);
        parsePosition++;
        end = parseUnion(start, end);
        return end;
      }
      else{
        NFAnode end = generateNewNode();
        start.addEdge(input[parsePosition], end);
        parsePosition++;
        return end;
      }
    }
    else{
      NFAnode end = generateNewNode();
      start.addEdge(input[parsePosition], end);
      parsePosition++;
      return end;
    }
    return null;
  }

  private NFAnode parseStar(NFAnode start, NFAnode end){
    System.out.println("adding *");
    NFAnode original_start = generateNewNode();
    NFAnode new_end = generateNewNode();
    int temp = original_start.number;

    // Swapping reference
    if(start.isStartState) {
      startState = temp;
    }
    original_start.number = start.number;
    original_start.edges = start.edges;
    start.number = temp;
    start.edges = new HashMap<Character, ArrayList<NFAnode>>();

    start.addEdge('ε', original_start);
    start.addEdge('ε', new_end);
    end.addEdge('ε', original_start);
    end.addEdge('ε', new_end);

    parsePosition++;
    return new_end;
  }

  private NFAnode parseUnion(NFAnode leftStart, NFAnode leftEnd){
    System.out.println("adding |");
    parsePosition++; //Consume the |
    NFAnode commonStart = generateNewNode();
    NFAnode rightStart = generateNewNode();
    NFAnode rightEnd = parseChar(rightStart);
    NFAnode commonEnd = generateNewNode();
    int temp = commonStart.number;

    // Swapping reference
    if(leftStart.isStartState) {
      startState = temp;
    }
    commonStart.number = leftStart.number;
    commonStart.edges = leftStart.edges;
    leftStart.number = temp;
    leftStart.edges = new HashMap<Character, ArrayList<NFAnode>>();

    leftStart.addEdge('ε', commonStart);
    leftStart.addEdge('ε', rightStart);
    leftEnd.addEdge('ε', commonEnd);
    rightEnd.addEdge('ε', commonEnd);
    return commonEnd;
  }

  private void parseNext(NFAnode start){
    if(parsePosition < input.length){
      System.out.println("PARSING NEXT");
      start = parseChar(start);
      parseNext(start);
    }
  }

  private NFAnode parseParens(NFAnode start) throws InvalidException{
    parsePosition++; //Consume the start parens
    NFAnode end = start;
    while(input[parsePosition] != ')'){
      end = parseChar(end);
      if(parsePosition > input.length) throw new InvalidException();
    }
    parsePosition++; //Consume the end parens
    if(parsePosition < input.length){
      if(input[parsePosition] == '*'){
        end = parseStar(start, end);
      } else if(input[parsePosition] == '|'){
        end = parseUnion(start, end);
      }
    }
    return end;
  }

  public void generateDOTfile(){
    try{
      PrintWriter output = new PrintWriter("NFA.dot");
      output.println("digraph {");
      output.println( (nodeNumber - 1) + " [shape = \"doublecircle\"];");
      output.println("node [shape = \"circle\"];");
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
      nodeList[node.number] = node;
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