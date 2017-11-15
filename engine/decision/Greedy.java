package engine.decision;

import engine.game.*;
import engine.pokemon.*;
import engine.tree.*;

import java.io.*;

public class Greedy implements DecisionMaker {
  public int findNextMove(State state, int user_team) {
    int opponent_team = user_team == 1 ? 2 : 1;
    Game game = new Game();
    Utils utils = new Utils();
    double best_opponent_score = -9999;
    int best_opponent_action = 0;

    //First we find the best greedy action our opponent can take
    for (int i = 0; i < state.avaliableActions(user_team).size(); i++) {
      for (int j = 0; j < state.avaliableActions(opponent_team).size(); j++) {
        State state_copy = (State) utils.deepClone(state);
        if (user_team == 1)
          game.turn(state_copy, i, j, true, 0);
        else
          game.turn(state_copy, j, i, true, 0);

        double total_p1_hp = 0;
        for (Pokemon p1 : state_copy.team_p1) {
          total_p1_hp += p1.hp;
        }

        double total_p2_hp = 0;
        for (Pokemon p2 : state_copy.team_p2) {
          total_p2_hp += p2.hp;
        }

        double diff_between_hps = user_team == 1 ? total_p2_hp - total_p1_hp : total_p1_hp - total_p2_hp;

        if (diff_between_hps > best_opponent_score) {
          best_opponent_score = diff_between_hps;
          best_opponent_action = j;
        }
      }
    }

    double best_user_score = -9999;
    int best_user_action = 0;

    //Now based on the best opponent move, we select the best user move
    for (int i = 0; i < state.avaliableActions(user_team).size(); i++) {
      State state_copy = (State) utils.deepClone(state);
      if (user_team == 1)
        game.turn(state_copy, i, best_opponent_action, true, 0);
      else
        game.turn(state_copy, best_opponent_action, i, true, 0);

      double total_p1_hp = 0;
      for (Pokemon p1 : state_copy.team_p1) {
        total_p1_hp += p1.hp;
      }

      double total_p2_hp = 0;
      for (Pokemon p2 : state_copy.team_p2) {
        total_p2_hp += p2.hp;
      }

      double diff_between_hps = user_team == 1 ? total_p1_hp - total_p2_hp : total_p2_hp - total_p1_hp;

      if (diff_between_hps > best_user_score) {
        best_user_score = diff_between_hps;
        best_user_action = i;
      }
    }
    return best_user_action;
  }
}
