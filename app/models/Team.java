package models;

/**
 * Created with IntelliJ IDEA.
 * User: fredkingham
 * Date: 24/01/2014
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
import siena.*;
import java.util.*;

public class Team extends Model {

    @NotNull
    @Id(Generator.NONE)
    public String name;

    @NotNull
    public String division;

    @NotNull
    public String link;

    static Query<Team> all() {
        return Model.all(Team.class);
    }

    public static int clear(){
        return all().delete();
    }

    public static List<Team> allTeams(){
        return all().fetch();
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", division='" + division + '\'' +
                '}';
    }

    public Team(String name, String division, String link){
        if(division == null){
            System.out.println("division = null for " + name);
        }
        if(link == null){
            System.out.println("link = null for " + name);
        }
        this.name = name;
        this.division = division;
        this.link = link;

    }

    public Team getOrCreate(){
        Team team = all().filter("name", name).get();
        System.out.println(link);
        if(team == null){
            team = new Team(name, division, link);
            team.insert();
        }
        return team;
    }

    /*
    * deletes all games that are not connected to teams
    * we're having to do an absurd amount locally becuase
    * Siena doesn't have the functionality (that I can find)
    */
    public static void deleteIfNotConnected(){
         List<Game> games = Game.allGames();
         List<Team> teams = new ArrayList<Team>();
         for(Game game : games){
             teams.add(game.homeTeam);
             teams.add(game.awayTeam);
         }

         List<Team> existing = all().fetch();
         existing.removeAll(teams);
         for(Team team: teams){
             team.delete();
         }
    }

    public static Team getByName(String name){
        return all().filter("name", name).get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        if (!super.equals(o)) return false;
        Team team = (Team) o;
        if (!name.equals(team.name)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
