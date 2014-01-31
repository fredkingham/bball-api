package models;

import org.apache.commons.lang.time.DateUtils;
import siena.*;
import java.util.*;
import play.Logger;

public class Game extends Model {
    @Id(Generator.AUTO_INCREMENT)
    public Long id;


    @Index("team_index")
    @Column("homeTeam")
    @NotNull
    public Team homeTeam;

    private Integer homeTeamScore;

    private Integer awayTeamScore;

    @Index("team_index")
    @Column("awayTeam")
    @NotNull
    public Team awayTeam;

    @NotNull
    public Date matchDate;


    public Game(Date matchDate, Team homeTeam, Team awayTeam){
        this.matchDate = matchDate;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public Game(Date matchDate, Team homeTeam, Team awayTeam, Integer homeTeamScore, Integer awayTeamScore){
        this.matchDate = matchDate;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamScore = homeTeamScore;
        this.awayTeamScore = awayTeamScore;
    }

    static Query<Game> all() {
        return Model.all(Game.class);
    }

    public static List<Game> findByTeamName(String teamName){
        Team team = Team.getByName(teamName);
        List<Game> result = all().filter("awayTeam", team).fetch();
        result.addAll(all().filter("homeTeam", team).fetch());
        return result;
    }

    public static Game findById(Long id) {
        return all().filter("id", id).get();
    }

    public static void DeleteOverAYearOld(){
        Date d = DateUtils.addYears(new Date(), -1);

        /*
        * we could do an order by with Siena, I just don't quite trust Siena enough...
         */
        for(Game game : all().fetch()){
            if(game.matchDate.before(d)){
                game.delete();
            }
        }
    }

    public static void updateGames(List<Game>newGames){
        List<Game>existingGames = all().fetch();

        Map<Game, Game> existingGamesMap = new HashMap();

        for(Game existingGame : existingGames){
            existingGamesMap.put(existingGame, existingGame);
        }

        for(Game newGame : newGames){
            if(existingGamesMap.containsKey(newGame)){
                  if(newGame.awayTeamScore != null && newGame.homeTeamScore != null){
                      Game existingGame = existingGamesMap.get(newGame);
                      existingGame.homeTeamScore = newGame.homeTeamScore;
                      existingGame.awayTeamScore = newGame.awayTeamScore;
                      existingGame.update();
                  }
            }
            else{
                  insertGame(newGame);
            }
        }
    }

    public static void insertGame(Game game){
        game.awayTeam.getOrCreate();
        game.homeTeam.getOrCreate();
        game.insert();
    }


    public static List<Game> allGames(){
        return all().fetch();
    }

    public static int clear(){
        return all().delete();
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", homeTeam=" + homeTeam +
                ", homeTeamScore=" + homeTeamScore +
                ", awayTeamScore=" + awayTeamScore +
                ", awayTeam=" + awayTeam +
                ", matchDate=" + matchDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;
        if (!super.equals(o)) return false;

        Game game = (Game) o;

        if (!awayTeam.equals(game.awayTeam)) return false;
        if (!homeTeam.equals(game.homeTeam)) return false;
        if (!matchDate.equals(game.matchDate)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + homeTeam.hashCode();
        result = 31 * result + awayTeam.hashCode();
        result = 31 * result + matchDate.hashCode();
        return result;
    }
}