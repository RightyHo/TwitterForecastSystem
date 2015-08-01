package twittercorpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Andrew on 16/06/15.
 */
public class PriceLabelCorpusImpl implements PriceLabelCorpus {

    // *** British Summer Time ended on 26 October 2014 begins again on 29 March 2015 ***
    // *** The data set we are using for this project runs from 1 January 2015 until 24 March 2015 during which time London will be using GMT ***

    private Map<ZonedDateTime,PriceSnapshot> priceMap;
    private String fileName;
    private ZoneOffset timeZone;
    private int millennium;

    // constructor

    public PriceLabelCorpusImpl(String fileName,ZoneOffset timeZone,int millennium) {
        priceMap = new HashMap<>();
        this.fileName = fileName;
        this.timeZone = timeZone;
        this.millennium = millennium;
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
                int year = splitDate.nextInt() + millennium;

                Scanner splitTime = new Scanner(timeString).useDelimiter(":");
                int hour =  splitTime.nextInt();
                int min =  splitTime.nextInt();

                // create new ZonedDateTime object for each row in the file

                LocalDateTime localTS = LocalDateTime.of(year, month, day, hour, min, 0);
                ZonedDateTime ts = ZonedDateTime.of(localTS,timeZone);

                // create new PriceShapshot for every row in the file

                PriceSnapshot pSnap = new BMWPriceSnapshot(ts,openPrice,macdDirection,closePrice);
                priceMap.put(ts,pSnap);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
