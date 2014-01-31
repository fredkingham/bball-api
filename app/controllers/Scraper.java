package controllers;

import models.Game;
import models.Team;
import org.apache.commons.lang.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;


import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;


/**
 * Created with IntelliJ IDEA.
 * User: fredkingham
 * Date: 25/01/2014
 * Time: 23:08
 * To change this template use File | Settings | File Templates.
 */
public class Scraper {

    private String route = "http://www.gbwba.org.uk/gbwba/";
    private String league = route + "index.cfm/the-league/";

    public void scrape(){
        Queue queue = QueueFactory.getDefaultQueue();

        try{
            Document doc = Jsoup.connect(league).get();
            List<String> links = getDivisionLinks(doc);
            Game.DeleteOverAYearOld();
            Team.deleteIfNotConnected();

            for(String link : links){
                queue.add(withUrl("/scrapeDivision").param("link", league + link));
            }
        }
        catch(IOException e){
            Logger.error("unable to find scrape details");
            Logger.error(e.toString());
        }
    }

    public static void scrapeDivisionalLinks(String link){
        Scraper scraper = new Scraper();
        scraper.addDivisionLinks(link);
    }

    public void addDivisionLinks(String link){
        List<Game> games = new ArrayList<Game>();
        Document resultsDocument = getResultsDocument(link);
        games.addAll(getTableData(resultsDocument, false));
        Document fixturesDocument = getFixturesDocument(link);
        games.addAll(getTableData(fixturesDocument, true));
        Game.updateGames(games);
    }

    private Document getResultsDocument(String link){
        try{

            Document doc = Jsoup.connect(link).get();
            String resultLink = league + doc.getElementsMatchingOwnText("^Results$").first().attr("href");
            return Jsoup.connect(resultLink).get();
        }
        catch(IOException e){
            Logger.error("unable to find results Document");
            Logger.error(e.toString());
        }

        return null;
    }

    private Document getFixturesDocument(String link){
        try{

            Document doc = Jsoup.connect(link).get();
            String resultLink =  league + doc.getElementsMatchingOwnText("^Fixtures").first().attr("href");
            return Jsoup.connect(resultLink).get();
        }
        catch(IOException e){
            Logger.error("unable to get the fixtures document");
            Logger.error(e.toString());
        }

        return null;
    }

    private List<Game> getTableData(Document doc, Boolean isFixtures){
        String tableName = isFixtures ? "leagueManager_fixturesTable" : "leagueManager_divisionTable";
        Elements tables = doc.getElementsByClass(tableName);
        List<Game> games = new ArrayList<Game>();

        for(Element tableResults : tables){
            String divisionName = getDivisionName(tableResults);
            Elements odds = tableResults.getElementsByClass("odd");
            Elements evens = tableResults.getElementsByClass("even");

            for(Element odd : odds){
                if(isFixtures){
                    games.add(extractFixturesFromTable(odd, divisionName));
                }
                else{
                    games.add(extractResultsFromTable(odd, divisionName));
                }
            }

            for(Element even : evens){
                if(isFixtures){
                    games.add(extractFixturesFromTable(even, divisionName));
                }
                else{
                    games.add(extractResultsFromTable(even, divisionName));
                }
            }
        }

        return games;

    }

    private String getDivisionName(Element table){
            Element th = table.getElementsByTag("th").first();
            return th.text().replaceAll(" Results", "").replaceAll(" Fixtures", "").trim();
    }

    private Date getDate(String potentialDate) throws ParseException{
          return DateUtils.parseDate(potentialDate, new String[]{"dd-MMMM-yyyy"});
    }

    private Team getTeam(Element element, String division){
           String teamName = element.text();
           String teamLink = league + element.getElementsByTag("a").first().attr("href");
           return new Team(teamName, division, teamLink);
    }

    private ArrayList<String> getDivisionLinks(Document doc){
        Element leagueDivisions = doc.getElementById("content").getElementsByClass("leagueManager_leagueSelector").first();
        Elements divisions = leagueDivisions.getElementsByClass("navSecondary").first().getElementsByTag("li");
        ArrayList<String> links = new ArrayList();
        for(Element division : divisions){
            String name = division.text();
            Element anchor = division.getElementsByTag("a").first();
            links.add(anchor.attr("href"));

        }

        return links;
    }

    private Integer[] getScores(Element td) throws Exception{
        String pattern = "(\\d+) - (\\d+)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(td.text());
        if(m.matches()){
            return new Integer[]{Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))};
        }

        throw new Exception("boo");
    }

    /*
    * td [0]
     */
    private Game extractResultsFromTable(Element tableRow, String divisionName){
         Date d = null;
         Integer[] score = null;
         Elements tds = tableRow.getElementsByTag("td");

         try{
             d = getDate(tds.first().text());
         }
         catch(ParseException e){
             Logger.error("unable to get the date");
             Logger.error(e.toString());
         }

         Team homeTeam = getTeam(tds.get(1), divisionName);

         try{
             score = getScores(tds.get(2));
         }
         catch(Exception e){
             Logger.error("unable to get score");
             Logger.error(e.toString());
         }

         Team awayTeam = getTeam(tds.get(3), divisionName);
         return new Game(d,  homeTeam, awayTeam, score[0], score[1]);
    }

    private Game extractFixturesFromTable(Element tableRow, String divisionName){
        Date d = null;
        Elements tds = tableRow.getElementsByTag("td");

        try{
            d = getDate(tds.first().text());
        }
        catch(ParseException e){
            Logger.error("unable to get score");
            Logger.error(e.toString());
        }


        Team homeTeam = getTeam(tds.get(1), divisionName);

        Team awayTeam = getTeam(tds.get(2), divisionName);
        return new Game(d,  homeTeam, awayTeam);
    }
}
