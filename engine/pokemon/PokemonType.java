package engine.pokemon;

import engine.game.*;
import engine.tree.*;

import java.sql.*;
import java.io.*;

public class PokemonType implements Serializable {
  public String name;
  public double psychic_attacking;
  public double bug_attacking;
  public double dark_attacking;
  public double dragon_attacking;
  public double electric_attacking;
  public double fairy_attacking;
  public double fighting_attacking;
  public double fire_attacking;
  public double flying_attacking;
  public double ghost_attacking;
  public double grass_attacking;
  public double ground_attacking;
  public double ice_attacking;
  public double normal_attacking;
  public double poison_attacking;
  public double rock_attacking;
  public double steel_attacking;
  public double water_attacking;

  public PokemonType(ResultSet db_pokemon) {
    try {
      this.name = db_pokemon.getString("p_type");
      this.psychic_attacking = db_pokemon.getDouble("psychic_attacking");
      this.bug_attacking = db_pokemon.getDouble("bug_attacking");
      this.dark_attacking = db_pokemon.getDouble("dark_attacking");
      this.dragon_attacking = db_pokemon.getDouble("dragon_attacking");
      this.electric_attacking = db_pokemon.getDouble("electric_attacking");
      this.fairy_attacking = db_pokemon.getDouble("fairy_attacking");
      this.fighting_attacking = db_pokemon.getDouble("fighting_attacking");
      this.fire_attacking = db_pokemon.getDouble("fire_attacking");
      this.flying_attacking = db_pokemon.getDouble("flying_attacking");
      this.ghost_attacking = db_pokemon.getDouble("ghost_attacking");
      this.grass_attacking = db_pokemon.getDouble("grass_attacking");
      this.ground_attacking = db_pokemon.getDouble("ground_attacking");
      this.ice_attacking = db_pokemon.getDouble("ice_attacking");
      this.normal_attacking = db_pokemon.getDouble("normal_attacking");
      this.poison_attacking = db_pokemon.getDouble("poison_attacking");
      this.rock_attacking = db_pokemon.getDouble("rock_attacking");
      this.steel_attacking = db_pokemon.getDouble("steel_attacking");
      this.water_attacking = db_pokemon.getDouble("water_attacking");
    } catch (SQLException e) {

    }
  }

}
