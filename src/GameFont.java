import java.awt.Font;
import java.awt.Color;

public final class GameFont {

  private static final String location = "../assets/fonts/ordinary_guy/ordinaryguyDEMO.otf";

  public static final float size = 80f;

  public static final Font font = CustomFont.createCustomFont(location, size);

  public static final Color color = Palette.CHALK;

}

/*
 * http://www.dafont.com/ordinary-guy.font
 */
