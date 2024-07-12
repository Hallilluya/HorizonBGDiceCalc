package hallilluya.horizonbgdicecalc;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HorizonBGDiceCalcController {

    @FXML
    private Button btncalc;

    @FXML
    private Label lblcrits;

    @FXML
    private Label lblmost;

    @FXML
    private Label lblpips;

    @FXML
    private TextField txfblack;

    @FXML
    private TextField txfblue;

    @FXML
    private TextField txforange;
    
    private int[][] exponents;
    private int expRow;
    
    @FXML
    void initialize() {
      
    }
    
    private void test(int testInt){
      int factorial = 1;
      for(int i= 1; i <= testInt; i++){
        factorial *= i;
      }
      System.out.println("test: " + factorial);
    }
    
    @FXML
    void calculate(ActionEvent event) {
      //test(2);
      
      //Getting dice amounts
      int amount_orange = 0;
      int amount_blue = 0;
      int amount_black = 0;
      if(!txforange.getText().equals(""))
        amount_orange = Integer.parseInt(txforange.getText());
      if(!txfblue.getText().equals(""))
        amount_blue = Integer.parseInt(txfblue.getText());
      if(!txfblack.getText().equals(""))
        amount_black = Integer.parseInt(txfblack.getText());
      
      
      // other outcomes: pips
      int maxPips = 2*amount_orange + 3*amount_blue;
      double[] chancesOrangePips = getChancesPips(amount_orange, "Orange");
      double[] chancesBlue = getChancesPips(amount_blue, "Blue");
      double[] chancesFinalPips = new double[maxPips +1];
      for(int i=0; i <= 2*amount_orange; i++){
        for(int j=0; j <= 3*amount_blue; j++){
          chancesFinalPips[i+j] += chancesOrangePips[i] * chancesBlue[j];
        }
      }
      String pipsText = "";
      for(int i=0; i < chancesFinalPips.length; i++){
        String extra;
        if(chancesFinalPips[i] < 0.001 && chancesFinalPips[i] != 0){
          extra = i + ": <0.1%"; // done to not display a chance of 0
        } else{
          DecimalFormat df = new DecimalFormat("##.#"); // Sets the format to 1 number after the decimal sign
          df.setRoundingMode(RoundingMode.HALF_UP); // HALF_UP = standard mathematical rounding
          extra = i + ": " +  df.format(chancesFinalPips[i] *100) + "%";
        }
        if(i+1 < chancesFinalPips.length)
          extra += "  ;   ";
        pipsText += extra;
      }
      lblpips.setText(pipsText);
      
      
      // other outcomes: crits
      int maxCrits = amount_orange + amount_black;
      double[] chancesOrangeCrits = new double[amount_orange +1];
      int nCko = 1; // Amount of ways to get i crits with the orange dice
      for(int i=0; i <= amount_orange; i++){
        int expOrangeCrits1 = Integer.max(amount_orange - i, 0);
        int expOrangeCrits2 = Integer.max(i, 0);
        chancesOrangeCrits[i] = Math.pow(0.83333, expOrangeCrits1) * Math.pow(0.16667, expOrangeCrits2) * nCko; // calculation of the chance to get i crits with the orange dice
        nCko = nCko * (amount_orange - i) / (i+1); // Calculating next number in Pascal's triangle for the orange dice
      }
      
      double[] chancesBlack = new double[amount_black +1];
      int nCkb = 1; // Amount of ways to get i crits with the black dice
      for(int i=0; i <= amount_black; i++){
        int expBlack1 = Integer.max(amount_black - i, 0);
        int expBlack2 = Integer.max(i, 0);
        chancesBlack[i] = Math.pow(0.5, expBlack1) * Math.pow(0.5, expBlack2) * nCkb; // calculation of the chance to get i crits with the black dice
        nCkb = nCkb * (amount_black - i) / (i+1); // Calculating next number in Pascal's triangle for the black dice
      }
      
      double[] chancesFinalCrits = new double[maxCrits +1];
      for(int i=0; i <= amount_orange; i++){
        for(int j=0; j <= amount_black; j++){
          chancesFinalCrits[i+j] += chancesOrangeCrits[i] * chancesBlack[j];
        }
      }
      String critText = "";
      for(int i=0; i < chancesFinalCrits.length; i++){
        String extra;
        if(chancesFinalCrits[i] < 0.001){
          extra = i + ": <0.1%"; // done to not display a chance of 0
        } else{
          DecimalFormat df = new DecimalFormat("##.#"); // Sets the format to 1 number after the decimal sign
          df.setRoundingMode(RoundingMode.HALF_UP); // HALF_UP = standard mathematical rounding
          extra = i + ": " +  df.format(chancesFinalCrits[i] *100) + "%";
        }
        if(i+1 < chancesFinalCrits.length)
          extra += "  ;   ";
        critText += extra;
      }
      lblcrits.setText(critText);
      
      
      //most likely outcome
      int pipsMostLikely = 0;
      for(int i=0; i <= maxPips; i++){
        if(chancesFinalPips[i] > chancesFinalPips[pipsMostLikely])
          pipsMostLikely = i;
      }
      int critsMostLikely = 0;
      for(int i=0; i <= maxCrits; i++){
        if(chancesFinalCrits[i] > chancesFinalCrits[critsMostLikely])
          critsMostLikely = i;
      }
      lblmost.setText(pipsMostLikely + " pips, " + critsMostLikely + " crits");
      
      System.out.println("");
    }
    
    private double[] getChancesPips(int diceAmount, String diceType){
      int combinations = Math.round((diceAmount+1)*(diceAmount+2) /2);
      exponents = new int[diceAmount][combinations];
      int arr[] = {1, 2, 3};
      CombinationRepetition(arr, 3, diceAmount);
      double chances[] = new double[1];
      if (diceType.equals("Orange"))
        chances = new double[2 * diceAmount +1];
      else if (diceType.equals("Blue"))
        chances = new double[3 * diceAmount +1];
      
      for(int i=0; i < combinations; i++){
        int exponent1 = 0;
        int exponent2 = 0;
        int exponent3 = 0;
        for(int j=0; j < diceAmount; j++){
          int currentExp = exponents[j][i];
          switch (currentExp) {
            case 1:
              exponent1++;
              break;
            case 2:
              exponent2++;
              break;
            case 3:
              exponent3++;
              break;
            default:
              break;
          }
        }
        
        int factorialExp1 = factorial(exponent1);
        int factorialExp2 = factorial(exponent2);
        int factorialExp3 = factorial(exponent3);
        int factorialExpTot = factorial(diceAmount);
        
        double chance = Math.pow(0.33333, exponent1) * Math.pow(0.5, exponent2) * Math.pow(0.166667, exponent3) * factorialExpTot / (factorialExp1 * factorialExp2 * factorialExp3);
        if(diceType.equals("Orange"))
          chances[exponent2 + exponent3*2] += chance;
        else if(diceType.equals("Blue"))
          chances[exponent1 + exponent2*2 + exponent3*3] += chance;
      }
      return chances;
    }
    
    private int factorial(int testInt){
      int factorial = 1;
      for(int i= 1; i <= testInt; i++){
        factorial *= i;
      }
      return factorial;
    }
    
    // The main function that prints all combinations of size r 
    // in arr[] of size n with repetitions. This function mainly 
    // uses CombinationRepetitionUtil() 
    private void CombinationRepetition(int arr[], int n, int r) {
      expRow = 0;
      // Allocate memory 
      int chosen[] = new int[r];
      // Call the recursive function 
      CombinationRepetitionUtil(chosen, arr, 0, r, 0, n - 1);
    }
    
    private void CombinationRepetitionUtil(int chosen[], int arr[], int index, int r, int start, int end) {
      // Since index has become r, current combination is ready to be printed, print 
      if(index == r) {
        //System.out.println("Combination: ");
        for (int i = 0; i < r; i++) {
          exponents[i][expRow] = arr[chosen[i]];
          //System.out.printf("%d ", arr[chosen[i]]);
        }
        expRow++;
        //System.out.print("\n");
        return;
      }
 
      // One by one choose all elements (without considering the fact whether element is already chosen or not) and recur
      for (int i = start; i <= end; i++) {
        chosen[index] = i;
        CombinationRepetitionUtil(chosen, arr, index + 1, r, i, end);
      }
    }
    
    
    
}
