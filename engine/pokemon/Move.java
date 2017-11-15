package engine.pokemon;

import engine.game.*;
import engine.tree.*;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Move implements Serializable {
  public String name;
  public String category;
  public String type;
  public boolean stab;
  public Pokemon switch_option;
  public int power;
  public int pp;
  public int accuracy;
  public int priority;
  public int effect_prob;
  public int can_poison;
  public int can_burn;
  public int can_freeze;
  public int can_sleep;
  public int can_paralyze;
  public int can_flinch;
  public int can_confuse;
  public int raise_user_atk;
  public int raise_user_def;
  public int raise_user_sp_atk;
  public int raise_user_sp_def;
  public int raise_user_spe;
  public double restore_immediate_hp;
  public int cause_recoil;

  public Move(ResultSet db_move) {
    try {
      while(db_move.next()) {
        this.name = db_move.getString("m_name");
        this.category = db_move.getString("category");
        this.type = db_move.getString("m_type");
        this.power = db_move.getInt("power");
        this.pp = db_move.getInt("pp");
        this.accuracy = db_move.getInt("accuracy");
        this.priority = db_move.getInt("priority");
        this.effect_prob = db_move.getInt("effect_prob");
        this.can_poison = db_move.getInt("can_poison");
        this.can_burn = db_move.getInt("can_burn");
        this.can_freeze = db_move.getInt("can_freeze");
        this.can_sleep = db_move.getInt("can_sleep");
        this.can_paralyze = db_move.getInt("can_paralyze");
        this.can_flinch = db_move.getInt("can_flinch");
        this.can_confuse = db_move.getInt("can_confuse");
        this.raise_user_atk = db_move.getInt("raise_user_atk");
        this.raise_user_def = db_move.getInt("raise_user_def");
        this.raise_user_sp_atk = db_move.getInt("raise_user_sp_atk");
        this.raise_user_sp_def = db_move.getInt("raise_user_sp_def");
        this.raise_user_spe = db_move.getInt("raise_user_spe");
        this.restore_immediate_hp = db_move.getFloat("restore_immediate_hp");
        this.cause_recoil = db_move.getInt("cause_recoil");
      }
    } catch (SQLException e) {

    }
  }

  public Move(Pokemon pokemon) {
    this.switch_option = pokemon;
    this.category = "Switch";
    this.name = "Switch to " + pokemon.name;
  }
}
