package graphex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Grep{

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
      br.close();
      input.close();
      return output;
    } catch (java.io.IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  static String cleanRegex(String regex){
    StringBuilder sb = new StringBuilder(regex);
    while(sb.indexOf("**") != -1){
      sb.deleteCharAt(sb.indexOf("**"));
    }
    while(sb.indexOf("||") != -1){
      sb.deleteCharAt(sb.indexOf("||"));
    }
    return sb.toString();
  }

  static void runRegex(String regex, String filename, DFA dfa){
    try {
      FileInputStream input = new FileInputStream(filename);
      BufferedReader br = new BufferedReader(new InputStreamReader(input));
      String line;

      while((line = br.readLine()) != null){
        if(dfa.isValid(line.toCharArray())) {
          System.out.println(line);
        }
      }
      br.close();
      input.close();
    } catch (java.io.IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void main(String[] args){
    String regex = args[0];
    regex = cleanRegex(regex);
    String inputFilename = args[1];
    String dfaFilename = "DFA.dot";
    String nfaFilename = "NFA.dot";
    ArrayList<Character> alphabet = generateAlphabet(inputFilename);

    NFA graphNFA = new NFA(regex);
    DFA graphDFA = new DFA(graphNFA.nodeList, graphNFA.startState, graphNFA.endState, alphabet);

    System.out.println("Alphabet: " + alphabet);
    System.out.println("Regex was: " + regex);

    runRegex(regex, inputFilename, graphDFA);
    graphNFA.generateDOTfile(regex, nfaFilename);
    graphDFA.generateDOTfile(regex, dfaFilename);
  }
}