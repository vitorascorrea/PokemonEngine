package engine.pokemon;

import engine.game.*;
import engine.tree.*;
import java.sql.*;
import java.io.*;

public class PokemonAttrs implements Serializable {
  public int hp;
  public int atk;
  public int def;
  public int sp_atk;
  public int sp_def;
  public int spe;

  public PokemonAttrs(ResultSet db_pokemon) {
    try {
      this.hp = db_pokemon.getInt("hp");
      this.atk = db_pokemon.getInt("atk");
      this.def = db_pokemon.getInt("def");
      this.sp_atk = db_pokemon.getInt("sp_atk");
      this.sp_def = db_pokemon.getInt("sp_def");
      this.spe = db_pokemon.getInt("spe");
    } catch (SQLException e) {

    }
  }
}
