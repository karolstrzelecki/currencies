package zad1;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UserInterface extends Application {

            private static Service s;
            private static String weatherJSON;
            private static Double rateFixer;
            private static Double rateNBP;
            private static String wikiURL;


            private Scene scene;
            private TabPane tabPane;
            private TilePane t1, t2, t3;
            private Tab tab1, tab2, tab3;
            private GridPane choicePane;
            private BorderPane  mainPane, bottomPane, browserPane;
            private HBox buttonBox;

            private Label la1,lb1,lc1;


            private TextArea textArea;
            private Button weatherButton, ratingNBPButton, ratingFixerButton,
                    descriptionButton, getDataButton;
            private TextField tx1, tx2, tx3;
            private WebView webView;
            private WebEngine webEngine;


    public UserInterface() {
    }

    private void prepareScene(Stage primaryStage){



                tabPane = new TabPane();
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

                tab1 = new Tab("Wprowadź Dane");
                tab2 = new Tab( "Wyszukiwanie");
                tab3 = new Tab ("Wikipedia");

                tabPane.getTabs().addAll(tab1, tab2, tab3);


        choicePane = new GridPane();
        choicePane.setPadding(new Insets(15,15,15,15));
        t1 = new TilePane();
        la1 = new Label("Kraj");
        tx1 = new TextField();
        tx1.setText(s.getCountry());



        t1.getChildren().add(la1);
        t1.getChildren().add(tx1);


        t2 = new TilePane();
        lb1 = new Label("Miasto");
        tx2 = new TextField();
        tx2.setText(s.getCity());

        t2.getChildren().add(lb1);
        t2.getChildren().add(tx2);


        t3 = new TilePane();
        lc1 = new Label("Wprowadz walute");
        tx3 = new TextField();
        tx3.setText(s.getRateFor());
        t3.getChildren().add(lc1);
        t3.getChildren().add(tx3);

        getDataButton = new Button("Wprowadz");



        choicePane.add(t1, 0, 0,2,1);
        choicePane.add(t2, 0,1,2,1);
        choicePane.add(t3,0,2,2,1);
        choicePane.add(getDataButton, 0, 3,2,1);




        mainPane = new BorderPane();
                mainPane.setPadding(new Insets(15,15,15,15));

                textArea = new TextArea();
                mainPane.setCenter(textArea);

                bottomPane = new BorderPane();
                bottomPane.setPadding(new Insets(10,0,0,0));

                buttonBox = new HBox();

                weatherButton = new Button("Pogoda");
                ratingFixerButton = new Button("Kurs Fixer");
                ratingNBPButton = new Button("Kurs NBP");
                descriptionButton = new Button("Pobierz opis");

                buttonBox.getChildren().add(weatherButton);
                buttonBox.getChildren().add(ratingFixerButton);
                buttonBox.getChildren().add(ratingNBPButton);
                buttonBox.getChildren().add(descriptionButton);




                getDataButton.setOnAction(action ->{
                    String country = tx1.getText();
                    String city = tx2.getText();
                    String currencyISO = tx3.getText();

                    //System.out.println(country + " " + city + " " + currencyISO);

                    tx1.setText(country);
                    tx2.setText(city);
                    tx3.setText(currencyISO);

                    s = new Service(country);
                    weatherJSON = s.getWeather(city);
                    rateFixer = s.getRateFor(currencyISO);
                    rateNBP = s.getNBPRate();
                    wikiURL = s.getWikiDescription(city);


                });





                weatherButton.setOnAction((event -> {
                    if(weatherJSON == null ){
                        getData("W kraju: " +s.getCountry() + "\nnie wystepuje miasto: " +s.getCity());
                    }else {
                        getData(formatWeatherJSON(weatherJSON));
                    }
                }));

                ratingFixerButton.setOnAction((event) -> {
                    if(rateFixer != null) {
                        getData("Kurs: " + s.getCurrencyCode().getCurrencyCode() + " wobec: " + s.getRateFor() + " wynosi: " + rateFixer);
                    }else{
                        getData("Nie dysponujemy kursem walut \n " +s.getCurrencyCode().getCurrencyCode()
                                + " wobec: "+ s.getRateFor());
                    }
                });

                ratingNBPButton.setOnAction((event) -> {
                    if(!rateNBP.isNaN()){
                        getData("Kurs: " + s.getCurrencyCode().getCurrencyCode() + " Narodowego Banku Polskiego wobec PLN wynosi: " + rateNBP);
                    }else{
                        getData("W bazie danych NBP nie występuje waluta: "+ s.getCurrencyCode().getCurrencyCode());
                    }
                });



                descriptionButton.setOnAction((event) ->{
                    if (!wikiURL.isEmpty()){
                        webEngine.load(wikiURL);
                    }
                } );

                bottomPane.setRight(buttonBox);
                mainPane.setBottom(bottomPane);

                tab1.setContent(choicePane);
                tab2.setContent(mainPane);

                browserPane = new BorderPane();
                webView = new WebView();
                webEngine = webView.getEngine();


                browserPane.setCenter(webView);
                tab3.setContent(browserPane);

                scene = new Scene(tabPane, 500,500);

            }


            public void getData(String data){
                textArea.clear();
                textArea.appendText(data);
            }

            public String formatWeatherJSON(String weatherJSON){
                String formattedJSON = "";

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(weatherJSON);
                    if(!jsonObject.isNull("main")){
                        formattedJSON += "Kraj: " + s.getCountry() + "\n";
                        formattedJSON += "Miasto: " + s.getCity() + "\n";
                        formattedJSON += "Temperatura (stopnie celcjusza): " + jsonObject.getJSONObject("main").get("temp") + "\n";
                        formattedJSON += "Temperatura min: " + jsonObject.getJSONObject("main").get("temp_min") + "\n";
                        formattedJSON += "Temperatura max: " + jsonObject.getJSONObject("main").get("temp_max") + "\n";
                        formattedJSON += "Wilgotność: " + jsonObject.getJSONObject("main").get("humidity") + "\n";
                        formattedJSON += "Ciśnienie: " + jsonObject.getJSONObject("main").get("pressure");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

             return formattedJSON;

            }



    @Override
    public void start(Stage primaryStage) {

        Parameters parameters = getParameters();
        List<String> list = parameters.getRaw();



        s = new Service(list.get(0));
        weatherJSON = s.getWeather(list.get(1));
        rateFixer = s.getRateFor(list.get(2));
       rateNBP = s.getNBPRate();
        wikiURL = s.getWikiDescription(list.get(1));

        prepareScene(primaryStage);

        primaryStage.setTitle("City Info");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
