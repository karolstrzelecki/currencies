/**
 *
 *  @author Strzelecki Karol S17435
 *
 */

package zad1;


import javafx.application.Application;

public class Main {
  public static void main(String[] args) {
    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("USD");
    Double rate2 = s.getNBPRate();

    // ...
    // część uruchamiająca GUI



    String s1 = s.getCountry();
    String s2 = s.getCity();
    String s3 = s.getRateFor();


    Application.launch(UserInterface.class, s1, s2, s3);




  }
}
