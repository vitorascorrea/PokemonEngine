package engine.game;

import engine.pokemon.*;
import engine.tree.*;

import java.util.ArrayList;
import java.io.*;

public class State implements Serializable {
  public ArrayList<Pokemon> team_p1;
  public ArrayList<Pokemon> team_p2;

  public State(ArrayList<Pokemon> team_p1, ArrayList<Pokemon> team_p2) {
    this.team_p1 = team_p1;
    this.team_p2 = team_p2;
  }

  public Pokemon getActivePokemon(int team) {
    if (team == 1) {
      for (Pokemon pokemon : this.team_p1) {
        if (pokemon.active) {
          return pokemon;
        }
      }
    } else {
      for (Pokemon pokemon : this.team_p2) {
        if (pokemon.active) {
          return pokemon;
        }
      }
    }
    return null;
  }

  public boolean terminalState() {
    boolean team_p1_alive = false;
    boolean team_p2_alive = false;
    for (Pokemon pokemon : this.team_p1) {
      if (pokemon.hp > 0) {
        team_p1_alive = true;
        break;
      }
    }
    for (Pokemon pokemon : this.team_p2) {
      if (pokemon.hp > 0) {
        team_p2_alive = true;
        break;
      }
    }
    return !team_p1_alive || !team_p2_alive;
  }

  public int winnerTeam() {
    if (terminalState()) {
      boolean team_p1_alive = false;
      for (Pokemon pokemon : this.team_p1) {
        if (pokemon.hp > 0) {
          team_p1_alive = true;
          break;
        }
      }
      return team_p1_alive ? 1 : 2;
    } else {
      return -1;
    }
  }

  public ArrayList<Move> avaliableActions(int team) {
    Pokemon team_active_pokemon = this.getActivePokemon(team);
    ArrayList<Move> avaliable_actions = new ArrayList<Move>();
    if (team_active_pokemon != null && team_active_pokemon.hp > 0) {
      avaliable_actions.addAll(team_active_pokemon.moves);
      avaliable_actions.addAll(this.getSwitchActions(team));
      return avaliable_actions;
    } else {
      avaliable_actions.addAll(this.getSwitchActions(team));
      return avaliable_actions;
    }
  }

  public ArrayList<Move> getSwitchActions(int team) {
    ArrayList<Move> switch_actions = new ArrayList<Move>();
    if (team == 1) {
      for(Pokemon pokemon : this.team_p1) {
        if (pokemon.hp > 0 && !pokemon.active) {
          switch_actions.add(new Move(pokemon));
        }
      }
    } else {
      for(Pokemon pokemon : this.team_p2) {
        if (pokemon.hp > 0 && !pokemon.active) {
          switch_actions.add(new Move(pokemon));
        }
      }
    }
    return switch_actions;
  }

  public Pokemon switchActivePokemon(int team, Move switch_move) {
    Pokemon current_active_pokemon = getActivePokemon(team);
    current_active_pokemon.active = false;
    switch_move.switch_option.active = true;
    return switch_move.switch_option;
  }

  public Move replaceMoveWithStruggle() {
    Utils utils = new Utils();
    return new Move(utils.getMove("Struggle"));
  }

}
