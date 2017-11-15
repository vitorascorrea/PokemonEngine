package engine.decision;

import engine.game.*;
import engine.pokemon.*;
import engine.tree.*;

import java.util.Random;

public class RandomDecision implements DecisionMaker {
  public int findNextMove(State state, int team) {
    return new Random().nextInt(state.avaliableActions(team).size());
  }
}
