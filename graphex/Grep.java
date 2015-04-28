package graphex;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

class Grep{

  public static void main(String[] args){
    System.out.println("Regex was: " + args[0]);
    try {
      FileInputStream input = new FileInputStream(args[1]);
      BufferedReader br = new BufferedReader(new InputStreamReader(input));
      String line;
      while((line = br.readLine()) != null) {
        System.out.println(line);
      }
    } catch (java.io.IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}