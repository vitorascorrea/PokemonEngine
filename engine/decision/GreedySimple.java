package engine.decision;

import engine.game.*;
import engine.pokemon.*;
import engine.tree.*;

public class GreedySimple implements DecisionMaker {
  public int findNextMove(State state, int user_team) {
    int opponent_team = user_team == 1 ? 2 : 1;
    Game game = new Game();
    Utils utils = new Utils();

    double best_score = -9999;
    int best_action = 0;

    for (int i = 0; i < state.avaliableActions(user_team).size(); i++) {
      double total_move_damage = 0;
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
        total_move_damage += user_team == 1 ? total_p1_hp - total_p2_hp : total_p2_hp - total_p1_hp;
      }
      if (total_move_damage > best_score) {
        best_score = total_move_damage;
        best_action = i;
      }
    }
    return best_action;
  }
}
