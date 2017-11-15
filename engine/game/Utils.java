package engine.game;

import engine.pokemon.*;
import engine.tree.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import java.nio.file.Paths;
import java.sql.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Utils {

  public Connection getConnection() {
    try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
			return null;
		}

		Connection connection = null;

		try {
			connection = DriverManager.getConnection("jdbc:postgresql://localhost/pokemon", "vitor", "root");
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}

    return connection;
  }

  public ResultSet getPokemon(String pokemon_name) {
    Connection connection = getConnection();
    try {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(
      "SELECT p.name AS p_name, p.pokemon_identification, p.hp, p.atk, p.def, p.sp_atk, p.sp_def, p.spe, t.type AS p_type, t.psychic_attacking, t.bug_attacking, t.dark_attacking, t.dragon_attacking, t.electric_attacking, t.fairy_attacking, t.fighting_attacking, t.fire_attacking, t.flying_attacking, t.ghost_attacking, t.grass_attacking, t.ground_attacking, t.ice_attacking, t.normal_attacking, t.poison_attacking, t.rock_attacking, t.steel_attacking, t.water_attacking FROM pokemons AS p INNER JOIN types AS t ON t.id = p.type WHERE name = '" + pokemon_name + "'");
      connection.close();
      return rs;
    } catch (SQLException e) {
      return null;
    }
  }

  public ResultSet getMove(String move_name) {
    Connection connection = getConnection();
    try {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(
      "SELECT moves.name AS m_name," +
      "moves.category," +
      "moves.power," +
      "moves.pp," +
      "moves.accuracy," +
      "move_types.type AS m_type," +
      "moves.priority," +
      "moves.effect_prob," +
      "moves.can_poison," +
      "moves.can_burn," +
      "moves.can_freeze," +
      "moves.can_sleep," +
      "moves.can_paralyze," +
      "moves.can_flinch," +
      "moves.can_confuse," +
      "moves.raise_user_atk," +
      "moves.raise_user_def," +
      "moves.raise_user_sp_atk," +
      "moves.raise_user_sp_def," +
      "moves.raise_user_spe," +
      "moves.restore_immediate_hp," +
      "moves.cause_recoil " +
      "FROM moves " +
      "INNER JOIN move_types ON moves.type = move_types.id " +
      "WHERE name = '" + move_name.replaceAll("'", "''") + "'");
      connection.close();
      return rs;
    } catch (SQLException e) {
      System.out.println(e);
      return null;
    }
  }

  public ResultSet getMoveByIdentification(String move_identification) {
    Connection connection = getConnection();
    try {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(
      "SELECT moves.name AS m_name," +
      "moves.category," +
      "moves.power," +
      "moves.pp," +
      "moves.accuracy," +
      "move_types.type AS m_type," +
      "moves.priority," +
      "moves.effect_prob," +
      "moves.can_poison," +
      "moves.can_burn," +
      "moves.can_freeze," +
      "moves.can_sleep," +
      "moves.can_paralyze," +
      "moves.can_flinch," +
      "moves.can_confuse," +
      "moves.raise_user_atk," +
      "moves.raise_user_def," +
      "moves.raise_user_sp_atk," +
      "moves.raise_user_sp_def," +
      "moves.raise_user_spe," +
      "moves.restore_immediate_hp," +
      "moves.cause_recoil" +
      "FROM moves" +
      "INNER JOIN move_types ON moves.type = move_types.id" +
      "WHERE move_identification = '" + move_identification + "'");
      return rs;
    } catch (SQLException e) {
      return null;
    }
  }

  public ArrayList<Pokemon> getSampleTeam() {
    JSONParser parser = new JSONParser();
    try {
      String path = Paths.get(".").toAbsolutePath().normalize().toString() + "/factory-sets.json";
      JSONObject json_obj = (JSONObject) parser.parse(new FileReader(path));

      ArrayList<Pokemon> team = new ArrayList<Pokemon>();
      String[] tiers = {"Uber", "OU", "UU", "RU", "NU", "PU"};

      while (team.size() < 6) {
        int rnd = new Random().nextInt(tiers.length);
        JSONObject random_pokemon = getRandomPokemon(tiers[rnd], json_obj);
        team.add(convertJsonToPokemon(random_pokemon));
      }
      team.get(0).active = true;

      return team;

    } catch (FileNotFoundException fe) {
      fe.printStackTrace();
    } catch(Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public JSONObject getRandomPokemon(String tier, JSONObject json_obj) {
    for (Object key : json_obj.keySet()) {
      //based on you key types
      String key_str = (String) key;
      if (key_str.equals(tier)) {
        JSONObject keyvalue = (JSONObject) json_obj.get(key_str);
        Random generator = new Random();
        Object[] values = keyvalue.values().toArray();
        JSONArray random_pokemon_sets = (JSONArray)((JSONObject)values[generator.nextInt(values.length)]).get("sets");
        return (JSONObject)random_pokemon_sets.get(generator.nextInt(random_pokemon_sets.size()));
      }
    }
    return null;
  }

  public Pokemon convertJsonToPokemon(JSONObject json_pokemon) {
    int[] evs = {
      ((Long)((JSONObject)json_pokemon.get("evs")).get("hp")).intValue(),
      ((Long)((JSONObject)json_pokemon.get("evs")).get("atk")).intValue(),
      ((Long)((JSONObject)json_pokemon.get("evs")).get("def")).intValue(),
      ((Long)((JSONObject)json_pokemon.get("evs")).get("spa")).intValue(),
      ((Long)((JSONObject)json_pokemon.get("evs")).get("spd")).intValue(),
      ((Long)((JSONObject)json_pokemon.get("evs")).get("spe")).intValue()
    };
    String[] moves = new String[4];
    for (int i = 0; i < ((JSONArray)json_pokemon.get("moves")).size(); i++) {
      JSONArray possible_moves = (JSONArray)((JSONArray)json_pokemon.get("moves")).get(i);
      if (possible_moves.size() > 1) {
        Random rand = new Random();
        moves[i] = (String) possible_moves.get(rand.nextInt(possible_moves.size()));
      } else {
        moves[i] = (String) possible_moves.get(0);
      }
    }

    Pokemon pokemon = new Pokemon(getPokemon((String)json_pokemon.get("species")), (String)json_pokemon.get("ability"), (String)json_pokemon.get("item"), evs, moves, false);

    return pokemon;
  }

  public Object deepClone(Object object) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(object);
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      return ois.readObject();
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  // public void printJsonObject(JSONObject json_obj, JSONArray jsonArr) {
  //   if (json_obj != null) {
  //     for (Object key : json_obj.keySet()) {
  //       //based on you key types
  //       String keyStr = (String) key;
  //       Object keyvalue = json_obj.get(keyStr);
  //
  //       if(key.equals("species")) {
  //         System.out.println("==============");
  //       }
  //
  //       //Print key and value
  //       if(key.equals("evs") || key.equals("species") || key.equals("moves") || key.equals("species") || key.equals("ability") || key.equals("item")) {
  //         System.out.println("key: "+ keyStr + " value: " + keyvalue);
  //       }
  //
  //       //for nested objects iteration if required
  //       if (keyvalue instanceof JSONObject) {
  //         printJsonObject((JSONObject) keyvalue, null);
  //       } else if (keyvalue instanceof JSONArray) {
  //         printJsonObject(null, (JSONArray) keyvalue);
  //       }
  //     }
  //   } else if (jsonArr != null) {
  //     for (int i = 0; i < jsonArr.size(); i++) {
  //       if (jsonArr.get(i) instanceof JSONObject) {
  //         printJsonObject((JSONObject) jsonArr.get(i), null);
  //       } else if (jsonArr.get(i) instanceof JSONArray) {
  //         printJsonObject(null, (JSONArray) jsonArr.get(i));
  //       }
  //     }
  //   }
  //
  // }

}
