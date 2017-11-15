package engine.tree;

import engine.game.*;
import engine.pokemon.*;

import java.util.ArrayList;
import java.util.Random;

public class Node {
  public State state;
  public Node parent;
  public int p1_index;
  public int p2_index;
  public ArrayList<Node> child_array;
  public int win_score;
  public int visit_score;

  public Node(State state, Node parent, int p1_index, int p2_index) {
    this.state = state;
    this.parent = parent;
    this.p1_index = p1_index;
    this.p2_index = p2_index;
    this.child_array = new ArrayList<Node>();
    this.win_score = 0;
    this.visit_score = 0;
  }

  public Node getRandomChild() {
    Random gen = new Random();
    return this.child_array.get(gen.nextInt(this.child_array.size()));
  }

  public Node getChildWithMaxScore() {
    int max_score = -999;
    Node max_score_child = null;
    for (Node child : this.child_array) {
      if (child.win_score > max_score) {
        max_score = child.win_score;
        max_score_child = child;
      }
    }
    return max_score_child;
  }


}
