package twittercorpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Andrew on 16/06/15.
 */
public class PriceLabelCorpusImpl implements PriceLabelCorpus {

    // *** Need to account for the fact that the twitter time stamps are in GMT and the chart time stamps are in London time ***
    // *** Run {TZDF} {39<GO} to convert to GMT and then download the price data ***

    private static final int MILLENIUM = 0;             // needs to be changed to 2000 if the input data date is of the form 23/11/15
    private Map<ZonedDateTime,PriceSnapshot> priceMap;
    private String fileName;

    // constructors

    public PriceLabelCorpusImpl(String fileName) {
        priceMap = new HashMap<>();
        this.fileName = fileName;
    }

    public PriceLabelCorpusImpl(Map<ZonedDateTime, PriceSnapshot> priceMap, String fileName) {
        this.priceMap = priceMap;
        this.fileName = fileName;
    }

    // getters and setters

    public Map<ZonedDateTime, PriceSnapshot> getPriceMap() {
        return priceMap;
    }

    public void setPriceMap(Map<ZonedDateTime, PriceSnapshot> priceMap) {
        this.priceMap = priceMap;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void extractPriceDataFromFile(String fileName){

        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                Scanner s = new Scanner(currentLine);
                String dateString = s.next().trim();
                String timeString = s.next().trim();
                double openPrice = s.nextDouble();
                double high = s.nextDouble();
                double low = s.nextDouble();
                double closePrice = s.nextDouble();
                double macdLevel = s.nextDouble();
                double sigLevel = s.nextDouble();
                double macdDirection = s.nextDouble();

                Scanner splitDate = new Scanner(dateString).useDelimiter("/");
                int day = splitDate.nextInt();
                int month = splitDate.nextInt();
                int year = splitDate.nextInt() + MILLENIUM;

                Scanner splitTime = new Scanner(timeString).useDelimiter(":");
                int hour =  splitTime.nextInt();
                int min =  splitTime.nextInt();

                // create new ZonedDateTime object for each row in the file

                LocalDateTime localTS = LocalDateTime.of(year, month, day, hour, min, 0);
                ZonedDateTime ts = ZonedDateTime.of(localTS, ZoneId.of("Europe/London"));

                // create new PriceShapshot for every row in the file

                PriceSnapshot pSnap = new BMWPriceSnapshot(ts,openPrice,macdDirection,closePrice);
                priceMap.put(ts,pSnap);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
