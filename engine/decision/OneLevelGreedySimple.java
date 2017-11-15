package engine.decision;

import engine.game.*;
import engine.pokemon.*;
import engine.tree.*;

import java.util.Random;

public class OneLevelGreedySimple implements DecisionMaker {
  public int findNextMove(State state, int user_team) {
    int opponent_team = user_team == 1 ? 2 : 1;
    Game game = new Game();
    Utils utils = new Utils();
    GreedySimple greedy_simple = new GreedySimple();
    int best_score = -999;
    int best_action = 0;

    for (int i = 0; i < state.avaliableActions(user_team).size(); i++) {
      int move_wins = 0;
      for (int j = 0; j < 5 ; j++) {
        State state_copy = (State) utils.deepClone(state);
        int opponent_action_index = greedy_simple.findNextMove(state_copy, opponent_team);

        if (user_team == 1)
          game.turn(state_copy, i, opponent_action_index, true, 0);
        else
          game.turn(state_copy, opponent_action_index, i, true, 0);

        if (state_copy.getActivePokemon(1).hp <= 0 && !state_copy.terminalState()) {
          int rand1 = new Random().nextInt(state_copy.avaliableActions(1).size());
          state_copy.switchActivePokemon(1, state_copy.avaliableActions(1).get(rand1));
        }

        if (state_copy.getActivePokemon(2).hp <= 0 && !state_copy.terminalState()) {
          int rand2 = new Random().nextInt(state_copy.avaliableActions(2).size());
          state_copy.switchActivePokemon(2, state_copy.avaliableActions(2).get(rand2));
        }

        while (!state_copy.terminalState()) {
          int p1_move = greedy_simple.findNextMove(state_copy, 1);
          int p2_move = greedy_simple.findNextMove(state_copy, 2);

          game.turn(state_copy, p1_move, p2_move, true, 0);

          if (state_copy.getActivePokemon(1).hp <= 0 && !state_copy.terminalState()) {
            int rand1 = new Random().nextInt(state_copy.avaliableActions(1).size());
            state_copy.switchActivePokemon(1, state_copy.avaliableActions(1).get(rand1));
          }

          if (state_copy.getActivePokemon(2).hp <= 0 && !state_copy.terminalState()) {
            int rand2 = new Random().nextInt(state_copy.avaliableActions(2).size());
            state_copy.switchActivePokemon(2, state_copy.avaliableActions(2).get(rand2));
          }
        }

        if (user_team == 1 && state_copy.winnerTeam() == 1) move_wins++;
        if (user_team == 2 && state_copy.winnerTeam() == 2) move_wins++;
      }
      if (move_wins > best_score) {
        best_score = move_wins;
        best_action = i;
      }
    }
    return best_action;
  }
}
