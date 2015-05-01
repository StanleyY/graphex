package graphex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Grep{

  static NFA graphNFA;
  static DFA graphDFA;

  static ArrayList<Character> generateAlphabet(String filename){
    try {
      FileInputStream input = new FileInputStream(filename);
      BufferedReader br = new BufferedReader(new InputStreamReader(input));
      ArrayList<Character> output = new ArrayList<Character>();
      String line;

      while((line = br.readLine()) != null){
        for(char c : line.toCharArray()) {
          if(output.indexOf(c) == -1){
            output.add(c);
          }
        }
      }
      return output;
    } catch (java.io.IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  public static void main(String[] args){
    String regex = args[0];
    String filename = args[1];
    String dfaFilename = "DFA.dot";
    String nfaFilename = "NFA.dot";
    ArrayList<Character> alphabet = generateAlphabet(filename);

    System.out.println("Regex was: " + regex);
    graphNFA = new NFA(regex);

    graphNFA.generateDOTfile(regex, nfaFilename);

    System.out.println("\nBeginning DFA\n");
    graphDFA = new DFA(graphNFA.nodeList, graphNFA.startState, graphNFA.endState, alphabet);

    graphDFA.generateDOTfile(regex, dfaFilename);
    /*
    for(int i=0; i < graphNFA.nodeList.length; i++){
      System.out.println("Node: " + graphNFA.nodeList[i].number);
      System.out.println("Edges: " + graphNFA.nodeList[i].edges.toString());
    }*/
    System.out.println(alphabet);
  }
}