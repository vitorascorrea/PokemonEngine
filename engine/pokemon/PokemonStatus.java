package engine.pokemon;

import engine.game.*;
import engine.tree.*;
import java.io.*;

public class PokemonStatus implements Serializable {
  public int poison = 0;
  public int poison_count = 0;
  public int burn = 0;
  public int freeze = 0;
  public int sleep = 0;
  public int sleep_count = 0;
  public int paralyze = 0;
  public int confuse = 0;
  public int confuse_count = 0;
  public int flinch = 0;
}
