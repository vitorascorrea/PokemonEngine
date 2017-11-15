package engine.decision;

import engine.game.*;
import engine.pokemon.*;
import engine.tree.*;

import java.util.Random;
import java.lang.*;

public class MCTS implements DecisionMaker {
  public int findNextMove(State state, int user_team) {
    int opponent_team = user_team == 1 ? 2 : 1;
    Game game = new Game();
    Utils utils = new Utils();
    State state_copy = (State) utils.deepClone(state);
    Node root = new Node(state_copy, null, -1, -1);
    Tree tree = new Tree(root);
    Random generator = new Random();
    long startTime = System.currentTimeMillis();
    while (false || (System.currentTimeMillis() - startTime) < 10000) {
      //Selection
      Node promising_node = root;

      while (promising_node.child_array.size() != 0) {
        int parent_visit = promising_node.visit_score;
        double max_uct = -999;
        Node max_uct_child = null;
        for (Node child : promising_node.child_array) {
          double child_uct = uctValue(parent_visit, child.win_score, child.visit_score);
          if (child_uct > max_uct) {
            max_uct = child_uct;
            max_uct_child = child;
          }
        }
        promising_node = max_uct_child;
      }

      //Expansion
      if (!promising_node.state.terminalState()) {
        for (int j = 0; j < promising_node.state.avaliableActions(user_team).size(); j++) {
          int opponent_action_index = generator.nextInt(promising_node.state.avaliableActions(opponent_team).size());
          Node new_node = null;
          if (user_team == 1) {
            game.turn(promising_node.state, j, opponent_action_index, true, 0);
            new_node = new Node(promising_node.state, promising_node, j, 0);
          } else {
            game.turn(promising_node.state, opponent_action_index, j, true, 0);
            new_node = new Node(promising_node.state, promising_node, 0, j);
          }
          promising_node.child_array.add(new_node);
        }
      }

      Node node_to_explore = promising_node;

      if (promising_node.child_array.size() > 0) node_to_explore = promising_node.getRandomChild();

      //Simulation
      Node temp_node = node_to_explore;
      while (!temp_node.state.terminalState()) {
        int p1_move = new Random().nextInt(temp_node.state.avaliableActions(1).size());
        int p2_move = new Random().nextInt(temp_node.state.avaliableActions(2).size());

        game.turn(temp_node.state, p1_move, p2_move, true, 0);

        if (temp_node.state.getActivePokemon(1).hp <= 0 && !temp_node.state.terminalState()) {
          int rand1 = new Random().nextInt(temp_node.state.avaliableActions(1).size());
          temp_node.state.switchActivePokemon(1, temp_node.state.avaliableActions(1).get(rand1));
        }

        if (temp_node.state.getActivePokemon(2).hp <= 0 && !temp_node.state.terminalState()) {
          int rand2 = new Random().nextInt(temp_node.state.avaliableActions(2).size());
          temp_node.state.switchActivePokemon(2, temp_node.state.avaliableActions(2).get(rand2));
        }
      }

      int playout_result = 0;
      if (user_team == 1 && temp_node.state.winnerTeam() == 1) playout_result = 1;
      if (user_team == 2 && temp_node.state.winnerTeam() == 2) playout_result = 1;

      //backpropagation
      while (temp_node != null) {
        temp_node.visit_score++;
        if (playout_result == 1) {
          temp_node.win_score++;
        }
        temp_node = temp_node.parent;
      }

    }

    if (user_team == 1)
      return root.getChildWithMaxScore().p1_index;
    else
      return root.getChildWithMaxScore().p2_index;
  }

  public double uctValue(int total_visit, int node_win_score, int node_visit_score) {
    if (node_visit_score == 0) return 99999;
    return ((double) node_win_score / (double) node_visit_score) + 1.41 * Math.sqrt(Math.log((double)total_visit) / (double)node_visit_score);
  }
}
