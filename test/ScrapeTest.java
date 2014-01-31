/**
 * Created with IntelliJ IDEA.
 * User: fredkingham
 * Date: 26/01/2014
 * Time: 19:31
 * To change this template use File | Settings | File Templates.
 */
import controllers.Scraper;
import org.jsoup.nodes.Document;
import play.test.*;
import org.junit.*;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class ScrapeTest extends UnitTest {

//    /*
//    * presumes that yml is stored with html as the key
//     */
//    public Document getMockDocument(String fileName){
//        String html =  (String)Fixtures.loadYamlAsMap("resultsScreen.yml").get("html");
//        Document doc = new Document("");
//        doc.html(html);
//        return doc;
//    }
//
//    @Test
//    public void testDate(){
//        String potentialDate1 = "16-November-2013";
//        String potentialDate2 = "02-May-2013";
//        Scraper scrape = new Scraper();
//        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
//
//        try{
//            Date d = scrape.getDate(potentialDate1);
//            assertEquals("Nov 16, 2013", df.format(d));
//        }
//        catch(Exception e){
//            fail();
//        }
//
//        try{
//            Date d = scrape.getDate(potentialDate2);
//            assertEquals("May 2, 2013", df.format(d));
//        }
//        catch(Exception e){
//            fail();
//        }
//    }
//
//    @Test
//    public void testgetDivisionLinks(){
//        Document doc = getMockDocument("homeScreen.yml");
//        Scraper scraper = new Scraper();
//        scraper.getDivisionLinks(doc);
//        ArrayList<String> results = scraper.getDivisionLinks(doc);
//        for(String result : results){
//            System.out.println(result);
//        }
//
//        String[] correctArray = new String[]{
//                "./?v=ll&LeagueId=102",
//                "./?v=ll&LeagueId=103",
//                "./?v=ll&LeagueId=104",
//                "./?v=ll&LeagueId=105",
//                "./?v=ll&LeagueId=106",
//                "./?v=ll&LeagueId=107",
//                "./?v=ll&LeagueId=108"};
//
//        assertEquals(Arrays.asList(correctArray), results);
//    }
//
//    @Test
//    public void testGetDivisionNames(){
//        Document doc = getMockDocument("divisionScreen.yml");
//        Scraper scraper = new Scraper();
//        scraper.getDivisionName(doc);
//
//    }
//
//    @Test
//    public void testGetResults(){
//        Document doc = getMockDocument("resultsScreen.yml");
//        Scraper scraper = new Scraper();
//        scraper.getTableData(doc);
//        assertEquals(1, 1);
//    }



}
