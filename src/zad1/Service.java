/**
 *
 *  @author Strzelecki Karol S17435
 *
 */

package zad1;


import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public class Service {

    private final String[] NBP_BASE_URL = {"http://www.nbp.pl/kursy/kursya.html", "http://www.nbp.pl/kursy/kursyb.html"};


    private String country;
    private String isoCode;
    private Currency currencyCode;
    private String city;
    private String rateFor;


    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getRateFor() {
        return rateFor;
    }

    public Currency getCurrencyCode() {
        return currencyCode;
    }

    public Service(String country) {
        this.country = country;
        this.isoCode = getISOCode(country);
        this.currencyCode = getCurrency(isoCode);

    }


    public String getWeather(String city){
        this.city = city;
        String JSON = null;

          OpenWeatherMap owm = new OpenWeatherMap(OpenWeatherMap.Units.METRIC, "30c1d478db2dc63bee176476d3c54923");

        try {
            CurrentWeather currentWeather = owm.currentWeatherByCityName(city, isoCode);

            if (currentWeather.hasBaseStation()){
                JSON = currentWeather.getRawResponse();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return JSON;
    }



    public Double getRateFor(String ex_currencyCode) {

        rateFor = ex_currencyCode;

        String uri = "https://api.exchangeratesapi.io/latest?symbols=" + ex_currencyCode + "&base=" + currencyCode.getCurrencyCode();



        HttpGet httpGet = new HttpGet(uri);
        Double exchange_rate = null;



        try {
            CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            HttpEntity httpEntity1 = closeableHttpResponse.getEntity();

            JSONObject jsonObject = new JSONObject(EntityUtils.toString(httpEntity1));


           if(!jsonObject.isNull("rates")){

               exchange_rate = jsonObject.getJSONObject("rates").getDouble(ex_currencyCode);

        }





        }catch(IOException | JSONException | ParseException e){
            e.printStackTrace();
        }


        return exchange_rate;
    }




    public Double getNBPRate(){

        Double nbpRate = null;

        if(currencyCode.getCurrencyCode().equals("PLN")){
            nbpRate = 1.0;
            return nbpRate;
        }else{
            nbpRate = getNBPRating();


            return nbpRate;
        }


    }


    public Double getNBPRating(){

        Double nbpRating ;

       for (String uri : NBP_BASE_URL) {
          String xmlUri = getXMLNBPurl(uri);
          Map<String, Double> NBPmap = XMLtoMapConverter(xmlUri);


          if(NBPmap.containsKey(currencyCode.getCurrencyCode())) {
              nbpRating = NBPmap.get(currencyCode.getCurrencyCode());


              return nbpRating;
          }

        }


        return null;

    }


    public Map<String, Double> XMLtoMapConverter(String uri){
        Map<String, Double> tmpMap = new HashMap<String, Double>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(uri);

            document.getDocumentElement().normalize();
            NodeList pozycjaNodeList = document.getElementsByTagName("pozycja");

            for(int i = 0; i<pozycjaNodeList.getLength(); i++){
                Node pozycja = pozycjaNodeList.item(i);

                if(pozycja.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element)pozycja;

                    String currencyCode = element.getElementsByTagName("kod_waluty").item(0).getTextContent();
                    Double averageRating = Double.parseDouble(element.getElementsByTagName("kurs_sredni").item(0).getTextContent().replace(',','.'));
                    tmpMap.put(currencyCode, averageRating);

                }


            }



        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return tmpMap;
    }





    public String getXMLNBPurl(String uri){
        String link = new String();

        try {
            org.jsoup.nodes.Document document = Jsoup.connect(uri).get();
            link = document.select("a[href]").attr("abs:href");

        } catch (IOException e) {
            e.printStackTrace();
        }



        return link;

    }







    private String getISOCode(String country){
        Map<String, String> countries = new HashMap<>();
        for (String iso : Locale.getISOCountries()){
            Locale locale = new Locale("en", iso);
            countries.put(locale.getDisplayCountry(new Locale("en")), iso);
        }

        String isoCode = countries.get(country);

        return isoCode;
    }





    private Currency getCurrency (String isoCode){
        Currency currencyCode = Currency.getInstance(new Locale("en", isoCode));


        return currencyCode;


    }

    private void getCityByCountry(){

    }




    public String getWikiDescription(String city){
        String citynamePL = city;
        if(citynamePL != null){
            return "https://en.wikipedia.org/wiki/" + citynamePL;
        }

        return citynamePL;
    }



}
