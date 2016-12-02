import java.awt.Font;
import java.awt.Color;

public final class ScoreFont {

  private static final String location = "../assets/fonts/press_start_2p/PressStart2P.ttf";

  public static final float size = 15f;

  public static final Font font = CustomFont.createCustomFont(location, size);

  public static final Color color = Palette.CHEESE_GREASE;

  public static final int padding = Math.round(size/3);

}

/*
 * http://www.dafont.com/scoreboard.font
 */
