package graphex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Grep{

  static NFA graphNFA;

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
    ArrayList<Character> alphabet = generateAlphabet(filename);

    graphNFA = new NFA(regex);

    graphNFA.generateDOTfile();
    System.out.println("Regex was: " + regex);
    System.out.println(alphabet);
  }
}