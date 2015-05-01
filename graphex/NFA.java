package graphex;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class NFA{

  private final String EDGE_DOT_FORMAT = "%s -> %s[label=\"%s\"];";
  private final char EPSILON = '\u03B5';

  public NFAnode root;
  public NFAnode[] nodeList;
  public int startState = 0;
  public int endState;
  private int nodeNumber = 0;
  private char[] input;
  private int parsePosition = 0;

  public NFA(String regex){
    input = regex.toCharArray();
    root = generateNewNode();
    root.isStartState = true;
    generateNFA(root);
    nodeList = new NFAnode[nodeNumber];
    generateNodeList(root);
    endState = nodeNumber - 1;
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
    parseRegex(root);
  }

  private NFAnode parseRegex(NFAnode start){
    if(parsePosition < input.length){
      NFAnode end = parseChar(start);
      if(parsePosition < input.length && input[parsePosition] == '|'){
        end = parseUnion(start, end);
      }
      return end;
    }
    return null;
  }

  private NFAnode parseChar(NFAnode start){
    if(parsePosition >= input.length || input[parsePosition] == '|' || input[parsePosition] == ')'){
      return start;
    }
    if(input[parsePosition] == '('){
      try{
        return parseParens(start);
      } catch (InvalidException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
    else{
      NFAnode end = generateNewNode();
      start.addEdge(input[parsePosition], end);
      parsePosition++;
      if(parsePosition < input.length && input[parsePosition] == '*'){
        end = parseStar(start, end);
      }
      return parseChar(end);
    }
    return null;
  }

  private NFAnode parseStar(NFAnode start, NFAnode end){
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

    start.addEdge(EPSILON, original_start);
    start.addEdge(EPSILON, new_end);
    end.addEdge(EPSILON, original_start);
    end.addEdge(EPSILON, new_end);

    parsePosition++;
    return new_end;
  }

  private NFAnode parseUnion(NFAnode leftStart, NFAnode leftEnd){
    parsePosition++; //Consume the |
    NFAnode commonStart = generateNewNode();
    NFAnode rightStart = generateNewNode();
    NFAnode rightEnd = parseRegex(rightStart);
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

    leftStart.addEdge(EPSILON, commonStart);
    leftStart.addEdge(EPSILON, rightStart);
    leftEnd.addEdge(EPSILON, commonEnd);
    rightEnd.addEdge(EPSILON, commonEnd);
    return commonEnd;
  }

  private NFAnode parseParens(NFAnode start) throws InvalidException{
    parsePosition++; //Consume the start parens
    NFAnode end = start;
    while(input[parsePosition] != ')'){
      end = parseRegex(end);
      if(parsePosition > input.length) throw new InvalidException();
    }
    parsePosition++; //Consume the end parens
    if(parsePosition < input.length && input[parsePosition] == '*'){
      end = parseStar(start, end);
    }
    return parseChar(end);
  }

  private void generateNodeList(NFAnode node){
    if(!node.visited){
      nodeList[node.number] = node;
      node.visited = true;
      for(Character transition : node.edges.keySet()){
        for(NFAnode n : node.edges.get(transition)){
          generateNodeList(n);
        }
      }
    }
  }

  public void generateDOTfile(String regex, String filename){
    try{
      PrintWriter output = new PrintWriter(filename);
      output.println("digraph {");
      output.println("graph [fontname=\"Courier\"];");
      output.println("labelloc=\"t\";");
      output.println("label=\""+ regex +"\";");
      output.println( endState + " [shape = \"doublecircle\"];");
      output.println("node [shape = \"circle\"];");
      output.println("-1[style=\"invis\"];");
      output.println("-1->" + root.number + ";");
      generateNodeDOT(output);
      output.println("}");
      output.close();
    } catch (java.io.IOException e){
      e.printStackTrace();
      System.exit(1);
    }
  }

  private void generateNodeDOT(PrintWriter out){
    NFAnode node;
    for(int i = 0; i < nodeList.length; i++){
      node = nodeList[i];
      for(Character transition : node.edges.keySet()){
        for(NFAnode n : node.edges.get(transition)){
          out.println(String.format(EDGE_DOT_FORMAT, node.number, n.number, transition));
        }
      }
    }
  }

}