package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;
import siena.Query;

public class Application extends Controller {

    @Before
    public static void setCORS()
    {
        Http.Response.current().accessControl("*", "GET,PUT,POST,DELETE", true);
    }

    public static void index() {
        render();
    }

    public static void teamName(String name){
        renderJSON(Team.getByName(name));
    }

    public static void team(){
        Scope.Params r = request.params;
        renderJSON(Team.allTeams());
    }

    public static void gameId(String id){
        renderJSON(Game.findById(Long.getLong(id)));
    }

    public static void game(){
        String teamName = request.params.get("team");
        if(teamName == null){
            renderJSON(Game.allGames());
        }
        else{
            renderJSON(Game.findByTeamName(teamName));
        }
    }

    public static void scrapeDivision(){
        String link = request.params.get("link");
        Scraper.scrapeDivisionalLinks(link);
    }

    public static void scrape(){
        Scraper scraper = new Scraper();
        scraper.scrape();
    }

    public static void clear(){
        Team.clear();
        Game.clear();
    }
}