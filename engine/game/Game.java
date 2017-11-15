package engine.game;

import engine.pokemon.*;
import engine.tree.*;

import java.util.Random;

public class Game {

  public void turn(State state, int action_p1_index, int action_p2_index, boolean simulating, int turn_count) {
    Move action_p1 = state.avaliableActions(1).get(action_p1_index);
    Move action_p2 = state.avaliableActions(2).get(action_p2_index);

    Pokemon active_pokemon_p1 = state.getActivePokemon(1);
    Pokemon active_pokemon_p2 = state.getActivePokemon(2);

    active_pokemon_p1.stats = buildCalcStats(active_pokemon_p1);
    active_pokemon_p2.stats = buildCalcStats(active_pokemon_p2);

    if (!simulating) {
      System.out.println();
      System.out.println("TURN #" + turn_count + "==================================");
      System.out.println("P1: " + active_pokemon_p1.name + " hp: " + active_pokemon_p1.hp + " - " + printStatus(active_pokemon_p1));
      System.out.println("P2: " + active_pokemon_p2.name + " hp: " + active_pokemon_p2.hp + " - " + printStatus(active_pokemon_p2));
      System.out.println("========================================");
    }

    if (priorityCheck(active_pokemon_p1.stats.spe, active_pokemon_p2.stats.spe, action_p1.priority, action_p2.priority)) {
      if(action_p1.category.equals("Switch")) {
        if(!simulating){
          System.out.println("P1: " + action_p1.name);
        }
        active_pokemon_p1 = state.switchActivePokemon(1, action_p1);
        active_pokemon_p1.stats = buildCalcStats(active_pokemon_p1);
      } else {
        actionCalc(state, 1, active_pokemon_p1, active_pokemon_p2, action_p1, simulating, false);
      }
      if (active_pokemon_p2.hp > 0.0) {
        if(action_p2.category.equals("Switch")) {
          if(!simulating){
            System.out.println("P2: " + action_p2.name);
          }
          active_pokemon_p2 = state.switchActivePokemon(2, action_p2);
          active_pokemon_p2.stats = buildCalcStats(active_pokemon_p2);
        } else {
          actionCalc(state, 2, active_pokemon_p2, active_pokemon_p1, action_p2, simulating, true);
        }
      }
    } else {
      if(action_p2.category.equals("Switch")) {
        if(!simulating){
          System.out.println("P2: " + action_p2.name);
        }
        active_pokemon_p2 = state.switchActivePokemon(2, action_p2);
        active_pokemon_p2.stats = buildCalcStats(active_pokemon_p2);
      } else {
        actionCalc(state, 2, active_pokemon_p2, active_pokemon_p1, action_p2, simulating, false);
      }
      if (active_pokemon_p1.hp > 0.0) {
        if(action_p1.category.equals("Switch")) {
          if(!simulating){
            System.out.println("P1: " + action_p1.name);
          }
          active_pokemon_p1 = state.switchActivePokemon(1, action_p1);
          active_pokemon_p1.stats = buildCalcStats(active_pokemon_p1);
        } else {
          actionCalc(state, 1, active_pokemon_p1, active_pokemon_p2, action_p1, simulating, true);
        }
      }
    }
    //End turn here
    endTurnStatusCheck(active_pokemon_p1, simulating, 1);
    endTurnStatusCheck(active_pokemon_p2, simulating, 2);
  }

  public void actionCalc(State state, int team, Pokemon offense_pokemon, Pokemon target_pokemon, Move action, boolean simulating, boolean is_second_move){
    //Here we check if the move will happen: checks for paralyze, flinch, etc
    if (startTurnStatusCheck(offense_pokemon, simulating, is_second_move, team)) {
      double damage = moveCalc(state, team, offense_pokemon, target_pokemon, action, simulating, is_second_move);
      target_pokemon.hp = target_pokemon.hp - ((damage * 100.0) / target_pokemon.stats.hp);
      action.pp = action.pp - 1;

      //Here we switch the move with struggle in case there is no pp left
      if (action.pp <= 0){
        action = state.replaceMoveWithStruggle();
      }

      if (!simulating) {
        System.out.println("P" + team + ": " + offense_pokemon.name + " used " + action.name + " (" + action.pp + " left)");
        System.out.println((damage * 100.0) / target_pokemon.stats.hp);
      }

      //Finally we check if the move caused a certain effect
      effectCalc(offense_pokemon, target_pokemon, action, simulating, team, damage);
    }
  }

  public double moveCalc(State state, int team, Pokemon offense_pokemon, Pokemon target_pokemon, Move action, boolean simulating, boolean is_second_move) {
    double move_effectiviness = target_pokemon.getEffectiviness(action.type);
    double chance_result = chanceCalc(action.accuracy);
    double modifier_result = modifierCalc(1.0, 1.0, action.stab, move_effectiviness, (action.category == "Physical" ? true : false));

    if (action.category.equals("Physical")) {
      return damageCalc(100, action.power, offense_pokemon.stats.atk, target_pokemon.stats.def) * modifier_result * chance_result;
    } else if (action.category.equals("Special")) {
      return damageCalc(100, action.power, offense_pokemon.stats.sp_atk, target_pokemon.stats.sp_def) * modifier_result * chance_result;
    } else {
      return 0.0;
    }
  }

  public boolean startTurnStatusCheck(Pokemon active_pokemon, boolean simulating, boolean is_second_move, int team) {
    Random generator = new Random();
    double rand = generator.nextDouble();

    if (active_pokemon.status.paralyze == 1.0){
      if (rand <= 0.25) {
        if (!simulating) {
          System.out.println("P" + team + ": " + active_pokemon.name + " is paralyzed and cant move!");
        }
        return false;
      }
    } else if (active_pokemon.status.sleep == 1) {
      active_pokemon.status.sleep_count += 1;
      if (rand > 0.33 * active_pokemon.status.sleep_count){
        if (!simulating) {
          System.out.println("P" + team + ": " + active_pokemon.name + " is fast asleep!");
        }
        return false;
      } else {
        active_pokemon.status.sleep_count = 0;
        active_pokemon.status.sleep = 0;
        if (!simulating) {
          System.out.println("P" + team + ": " + active_pokemon.name + " woke up!");
        }
        return true;
      }
    } else if (active_pokemon.status.freeze == 1) {
      if (rand <= 0.2) {
        active_pokemon.status.freeze = 0;
        if (!simulating) {
          System.out.println("P" + team + ": " + active_pokemon.name + " thawed out!");
        }
        return true;
      } else {
        if (!simulating) {
          System.out.println("P" + team + ": " + active_pokemon.name + " is frozen and cant move!");
        }
        return false;
      }
    } else if (active_pokemon.status.confuse == 1) {
      active_pokemon.status.confuse_count = active_pokemon.status.confuse_count + 1;
      if (rand <= 0.33) {
        double confusion_damage = damageCalc(100, 40, active_pokemon.stats.atk, active_pokemon.stats.def);
        active_pokemon.hp = active_pokemon.hp - ((confusion_damage * 100) / active_pokemon.stats.hp);
        if (!simulating) {
          System.out.println("P" + team + ": " + active_pokemon.name + " hurt itself in its confusion!");
        }
        return false;
      } else if (active_pokemon.status.confuse_count == 4) {
        active_pokemon.status.confuse = 0;
        if (!simulating) {
          System.out.println("P" + team + ": " + active_pokemon.name + " snapped out of confusion!");
        }
        return true;
      }
    } else if (active_pokemon.status.flinch == 1 && is_second_move) {
      if (!simulating) {
        active_pokemon.status.flinch = 0;
        System.out.println("P" + team + ": " + active_pokemon.name + " flinched and cannot move!");
      }
      return false;
    }
    return true;
  }

  public void endTurnStatusCheck(Pokemon active_pokemon, boolean simulating, int team) {
    if (active_pokemon.status.poison == 1.0) {
      active_pokemon.hp = active_pokemon.hp - 12.5;
      if(!simulating) {
        System.out.println("P" + team + ": " + active_pokemon.name + " was hurt by the poison");
      }
    } else if (active_pokemon.status.poison == 2.0) {
      active_pokemon.status.poison_count = active_pokemon.status.poison_count + 1;
      active_pokemon.hp = active_pokemon.hp - (6.25 * (Math.pow(2.0, active_pokemon.status.poison_count)));
      if(!simulating) {
        System.out.println("P" + team + ": " + active_pokemon.name + " was hurt by the poison");
      }
    } else if (active_pokemon.status.burn == 1.0) {
      active_pokemon.hp = active_pokemon.hp - 6.25;
      if(!simulating) {
        System.out.println("P" + team + ": " + active_pokemon.name + " was hurt by the burn");
      }
    }
  }

  public void effectCalc(Pokemon offense_pokemon, Pokemon target_pokemon, Move action, boolean simulating, int team, double damage) {
    Random generator = new Random();
    double rand = generator.nextDouble();

    if (action.cause_recoil > 0) {
      if (action.name.equals("Struggle")) {
        offense_pokemon.hp = offense_pokemon.hp - (offense_pokemon.stats.hp/4.0);
      } else {
        offense_pokemon.hp = offense_pokemon.hp - (((damage/3.0) * 100.0) / offense_pokemon.stats.hp);
      }
      if (!simulating) System.out.println("P" + team + ": " + offense_pokemon.name + " was hurt by recoil");
    }

    if (action.effect_prob > 0) {
      boolean move_effect_chance = (rand * 100.0) <= action.effect_prob ? true : false;
      if(move_effect_chance) {
        if (noCurrentStatus(target_pokemon)) {
          if (action.can_poison == 1.0 && target_pokemon.type.poison_attacking > 0) {
            target_pokemon.status.poison = 1;
            if (!simulating) {
              System.out.println("P" + team + ": " + target_pokemon.name + " was poisoned");
            }
          }
          if (action.can_poison == 2.0 && target_pokemon.type.poison_attacking > 0) {
            target_pokemon.status.poison = 2;
            if (!simulating) {
              System.out.println("P" + team + ": " + target_pokemon.name + " was badly poisoned");
            }
          }
          if (action.can_burn == 1.0 && !target_pokemon.type.name.equals("fire")) {
            target_pokemon.status.burn = 1;
            if (!simulating) {
              System.out.println("P" + team + ": " + target_pokemon.name + " was burned");
            }
          }
          if (action.can_freeze == 1.0 && !target_pokemon.type.name.equals("ice")) {
            target_pokemon.status.freeze = 1;
            if (!simulating) {
              System.out.println("P" + team + ": " + target_pokemon.name + " was frozen solid");
            }
          }
          if (action.can_paralyze == 1.0) {
            target_pokemon.status.paralyze = 1;
            if (!simulating) {
              System.out.println("P" + team + ": " + target_pokemon.name + " was paralyzed");
            }
          }
          if (action.can_sleep == 1.0) {
            target_pokemon.status.sleep = 1;
            if (!simulating) {
              System.out.println("P" + team + ": " + target_pokemon.name + " was put to sleep");
            }
          }
          if (action.can_flinch == 1.0) {
            target_pokemon.status.flinch = 1;
            if (!simulating) {
              System.out.println("P" + team + ": " + target_pokemon.name + " flinched!");
            }
          }
          if (action.can_confuse == 1.0) {
            target_pokemon.status.confuse = 1;
            if (!simulating) {
              System.out.println("P" + team + ": " + target_pokemon.name + " was confused");
            }
          }
        }
        if (action.raise_user_atk > 0) {
          offense_pokemon.modifiers.atk = increaseModifier(action.raise_user_atk, offense_pokemon.modifiers.atk);
        }
        if (action.raise_user_def > 0) {
          offense_pokemon.modifiers.def = increaseModifier(action.raise_user_def, offense_pokemon.modifiers.def);
        }
        if (action.raise_user_sp_atk > 0) {
          offense_pokemon.modifiers.sp_atk = increaseModifier(action.raise_user_sp_atk, offense_pokemon.modifiers.sp_atk);
        }
        if (action.raise_user_sp_def > 0) {
          offense_pokemon.modifiers.sp_def = increaseModifier(action.raise_user_sp_def, offense_pokemon.modifiers.sp_def);
        }
        if (action.raise_user_spe > 0) {
        offense_pokemon.modifiers.spe = increaseModifier(action.raise_user_spe, offense_pokemon.modifiers.spe);
      }
        if (action.restore_immediate_hp > 0) {
          if (offense_pokemon.hp + (action.restore_immediate_hp * 100.0) <= 100.0) {
            offense_pokemon.hp = offense_pokemon.hp + (action.restore_immediate_hp * 100.0);
          } else {
            offense_pokemon.hp = 100.0;
          }
          if (action.name.equals("Rest")) {
            if (!simulating) {
              System.out.println("P" + team + ": " + offense_pokemon.name + " fell asleep!");
            }
            offense_pokemon.status.sleep = 1;
            offense_pokemon.status.sleep_count = 0;
            offense_pokemon.status.poison = 0;
            offense_pokemon.status.poison_count = 0;
            offense_pokemon.status.burn = 0;
            offense_pokemon.status.paralyze = 0;
            offense_pokemon.status.freeze = 0;
          }
        }
      }
    }
  }

  public boolean noCurrentStatus(Pokemon pokemon) {
    return pokemon.status.poison == 0.0 && pokemon.status.burn == 0.0 && pokemon.status.sleep == 0.0 && pokemon.status.paralyze == 0.0 && pokemon.status.freeze == 0.0;
  }

  public String printStatus(Pokemon active_pokemon) {
    String status_string = "";
    if (active_pokemon.status.poison == 1.0)
      status_string = status_string + " psn ";
    else if (active_pokemon.status.poison == 2.0)
      status_string = status_string + " tox ";
    else if (active_pokemon.status.burn == 1.0)
      status_string = status_string + " brn ";
    else if (active_pokemon.status.paralyze == 1.0)
      status_string = status_string + " prz ";
    else if (active_pokemon.status.freeze == 1.0)
      status_string = status_string + " frz ";
    else if (active_pokemon.status.sleep == 1.0)
      status_string = status_string + " slp ";
    else if (active_pokemon.status.confuse == 1.0)
      status_string = status_string + " confuse ";
    return status_string;
  }

  public double increaseModifier(double value, double current_modifier) {
    if (current_modifier + value <= 6.0)
      return current_modifier + value;
    else
      return current_modifier;
  }

  public PokemonStats buildCalcStats(Pokemon pokemon) {
    if (pokemon.stats != null) {
      return pokemon.stats;
    } else if (pokemon.evs != null) {
      PokemonStats stats = new PokemonStats();
      stats.hp = statCalc(true, pokemon.attrs.hp, pokemon.level, pokemon.evs[0], 0.0);
      stats.atk = statCalc(false, pokemon.attrs.atk, pokemon.level, pokemon.evs[1], pokemon.modifiers.atk);
      stats.def = statCalc(false, pokemon.attrs.def, pokemon.level, pokemon.evs[2], pokemon.modifiers.def);
      stats.sp_atk = statCalc(false, pokemon.attrs.sp_atk, pokemon.level, pokemon.evs[3], pokemon.modifiers.sp_atk);
      stats.sp_def = statCalc(false, pokemon.attrs.sp_def, pokemon.level, pokemon.evs[4], pokemon.modifiers.sp_def);
      stats.spe = statCalc(false, pokemon.attrs.spe, pokemon.level, pokemon.evs[5], pokemon.modifiers.spe);
      if (pokemon.status.paralyze == 1) {
        pokemon.stats.spe = pokemon.stats.spe * 0.5;
      }
      return stats;
    } else {
      PokemonStats stats = new PokemonStats();
      stats.hp = statCalc(true, pokemon.attrs.hp, pokemon.level, 252, 0.0);
      stats.atk = statCalc(false, pokemon.attrs.atk, pokemon.level, 252, pokemon.modifiers.atk);
      stats.def = statCalc(false, pokemon.attrs.def, pokemon.level, 252, pokemon.modifiers.def);
      stats.sp_atk = statCalc(false, pokemon.attrs.sp_atk, pokemon.level, 252, pokemon.modifiers.sp_atk);
      stats.sp_def = statCalc(false, pokemon.attrs.sp_def, pokemon.level, 252, pokemon.modifiers.sp_def);
      stats.spe = statCalc(false, pokemon.attrs.spe, pokemon.level, 252, pokemon.modifiers.spe);
      if (pokemon.status.paralyze == 1) {
        pokemon.stats.spe = pokemon.stats.spe * 0.5;
      }
      return stats;
    }
  }

  public double damageCalc(int p1_level, int move_power, double p1_atk, double p2_def) {
    return ((((((2*p1_level)/5) + 2) * move_power * (p1_atk/p2_def))/50) + 2);
  }

  public double modifierCalc(double targets, double weather, boolean stab, double move_effectiviness, boolean burn) {
    Random generator = new Random();
    double roll = generator.nextDouble()*(1.0 - 0.85) + 0.85;
    return targets * weather * (stab ? 1.5 : 1.0) * move_effectiviness * (burn ? 0.5 : 1) * roll;
  }

  public double chanceCalc(int move_accuracy){
    Random generator = new Random();
    double hit = (move_accuracy == 1 ? 1.0 : (generator.nextDouble() * 100 > move_accuracy ? 0 : 1.0));
    double critical = generator.nextDouble() < 0.0625 ? 2.0 : 1.0;
    return hit * critical;
  }

  public double statCalc(boolean is_hp, int base_stat, int level, int ev, double modifier) {
    if (is_hp) {
      return (((2 * base_stat + 31 + (ev/4)) * level) / 100) + level + 10;
    } else {
      return ((((2 * base_stat + 31 + (ev/4)) * level) / 100) + 5) * (modifier >= 0 ? ((modifier + 2)/2) : (2/(modifier + 2)));
    }
  }

  public boolean priorityCheck(double p1_speed, double p2_speed, int p1_priority, int p2_priority) {
    if (p1_priority > p2_priority)
      return true;
    else if (p1_priority == p2_priority)
      return p1_speed >= p2_speed;
    else
      return false;
  }

}
