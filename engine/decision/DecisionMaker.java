package engine.decision;

import engine.game.*;
import engine.pokemon.*;
import engine.tree.*;

public interface DecisionMaker {
  public int findNextMove(State state, int user_team);
}
