package engine;

import engine.decision.*;
import engine.game.*;
import engine.pokemon.*;

import java.util.ArrayList;
import java.util.Random;

public class Main {
  public static void main(String[] args){
    DecisionMaker p1_decision_maker = new GreedySimple();
    DecisionMaker p2_decision_maker = new RandomDecision();
    runGame(p1_decision_maker, p2_decision_maker, false);
  }

  public static void runGame(DecisionMaker p1_decision_maker, DecisionMaker p2_decision_maker, boolean simulating) {
    Utils utils = new Utils();
    Game game = new Game();
    try {
      ArrayList<Pokemon> team_p1 = utils.getSampleTeam();
      ArrayList<Pokemon> team_p2 = utils.getSampleTeam();

      State state = new State(team_p1, team_p2);
      int count = 0;
      while (!state.terminalState()) {
        int p1_move = p1_decision_maker.findNextMove(state, 1);
        int p2_move = p2_decision_maker.findNextMove(state, 2);

        game.turn(state, p1_move, p2_move, simulating, count);

        if (state.getActivePokemon(1).hp <= 0 && !state.terminalState()) {
          int rand1 = new Random().nextInt(state.avaliableActions(1).size());
          if(!simulating) {
            System.out.println();
            System.out.println("P1: " + state.getActivePokemon(1).name + " fainted!");
            System.out.println("P1: " + state.avaliableActions(1).get(rand1).name);
            System.out.println();
          }
          state.switchActivePokemon(1, state.avaliableActions(1).get(rand1));

        }

        if (state.getActivePokemon(2).hp <= 0 && !state.terminalState()) {
          int rand2 = new Random().nextInt(state.avaliableActions(2).size());
          if(!simulating) {
            System.out.println();
            System.out.println("P2: " + state.getActivePokemon(2).name + " fainted!");
            System.out.println("P2: " + state.avaliableActions(2).get(rand2).name);
            System.out.println();
          }
          state.switchActivePokemon(2, state.avaliableActions(2).get(rand2));
        }

        count++;
      }
    System.out.println("P " + state.winnerTeam() + " won!");
    } catch (Exception e) {
      System.out.println("error");
    }
  }
}
