package engine.pokemon;

import engine.game.*;
import engine.tree.*;
import java.sql.*;
import java.io.*;
import java.util.ArrayList;

public class Pokemon implements Serializable {
  public String name;
  public String pokemon_identification;
  public PokemonAttrs attrs;
  public PokemonType type;
  public PokemonStatus status;
  public PokemonModifiers modifiers;
  public PokemonStats stats;
  public String ability;
  public String item;
  public int[] evs;
  public ArrayList<Move> moves;
  public boolean active;
  public double hp = 100.0;
  public int level = 100;

  public Pokemon(ResultSet db_pokemon, String ability, String item, int[] evs, String[] moves, boolean active) {
    try {
      while (db_pokemon.next()) {
        this.name = db_pokemon.getString("p_name");
        this.pokemon_identification = db_pokemon.getString("pokemon_identification");
        this.attrs = new PokemonAttrs(db_pokemon);
        this.type = new PokemonType(db_pokemon);
        this.status = new PokemonStatus();
        this.modifiers = new PokemonModifiers();
        this.ability = ability;
        this.item = item;
        this.evs = evs;
        this.active = active;
        this.moves = new ArrayList<Move>();
        for (String move : moves) {
          this.moves.add(new Move(new Utils().getMove(move)));
        }
        this.moves = setStab(this.moves);
      }
    } catch (SQLException e) {

    }
  }

  public ArrayList<Move> setStab (ArrayList<Move> moves) {
    for (Move move : moves) {
      if (this.type.name.contains(move.type)) {
        move.stab = true;
      } else {
        move.stab = false;
      }
    }
    return moves;
  }

  public double getEffectiviness(String m_type) {
    switch (m_type) {
      case "psychic":
        return this.type.psychic_attacking;
      case "bug":
        return this.type.bug_attacking;
      case "dark":
        return this.type.dark_attacking;
      case "dragon":
        return this.type.dragon_attacking;
      case "electric":
        return this.type.electric_attacking;
      case "fairy":
        return this.type.fairy_attacking;
      case "fighting":
        return this.type.fighting_attacking;
      case "fire":
        return this.type.fire_attacking;
      case "flying":
        return this.type.flying_attacking;
      case "ghost":
        return this.type.ghost_attacking;
      case "grass":
        return this.type.grass_attacking;
      case "ground":
        if (this.ability.equals("Levitate")) {
          return 0;
        } else {
          return this.type.ground_attacking;
        }
      case "ice":
        return this.type.ice_attacking;
      case "normal":
        return this.type.normal_attacking;
      case "poison":
        return this.type.poison_attacking;
      case "rock":
        return this.type.rock_attacking;
      case "steel":
        return this.type.steel_attacking;
      case "water":
        return this.type.water_attacking;
    }
    return 0;
  }

}
