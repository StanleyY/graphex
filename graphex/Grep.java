package graphex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Grep{

  static boolean generateNFA = false;
  static boolean generateDFA = false;
  static String dfaFilename = "DFA.dot";
  static String nfaFilename = "NFA.dot";

  static ArrayList<Character> generateAlphabet(String filename, String regex){
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

      String ignore = "|()*";
      for(char c : regex.toCharArray()) {
        if(!ignore.contains("" + c) && output.indexOf(c) == -1){
          output.add(c);
        }
      }
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

  static int parseArgs(String[] args){
    if(args.length % 2 == 1 || args.length < 2 || args.length > 6) {
      System.out.println("Usage: java graphex.Grep [-n NFA-FILE] [-d DFA-FILE] REGEX FILE");
      System.exit(1);
    }
    int offset = 0;
    if(args.length > 2) {
      if(args[0].equals("-n")){
        generateNFA = true;
        nfaFilename = args[1];
        offset += 2;
      } else if(args[0].equals("-d")){
        generateDFA = true;
        dfaFilename = args[1];
        offset += 2;
      }
      if(args[2].equals("-n")){
        generateNFA = true;
        nfaFilename = args[3];
        offset += 2;
      } else if(args[2].equals("-d")){
        generateDFA = true;
        dfaFilename = args[3];
        offset += 2;
      }
    }
    if(args.length - offset != 2){
      System.out.println("Usage: java graphex.Grep [-n NFA-FILE] [-d DFA-FILE] REGEX FILE");
      System.exit(1);
    }
    return offset;
  }

  public static void main(String[] args){
    int offset = parseArgs(args);
    String regex = args[offset];
    regex = cleanRegex(regex);
    String inputFilename = args[offset + 1];
    ArrayList<Character> alphabet = generateAlphabet(inputFilename, regex);
    System.out.println(alphabet);
    NFA graphNFA = new NFA(regex);
    DFA graphDFA = new DFA(graphNFA.nodeList, graphNFA.startState, graphNFA.endState, alphabet);

    if(generateNFA) {
      graphNFA.generateDOTfile(regex, nfaFilename);
    }
    if(generateDFA) {
      graphDFA.generateDOTfile(regex, dfaFilename);
    }

    runRegex(regex, inputFilename, graphDFA);
  }
}