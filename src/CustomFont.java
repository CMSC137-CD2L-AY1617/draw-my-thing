import java.io.File;
import java.io.IOException;
import java.awt.GraphicsEnvironment;
import java.awt.FontFormatException;
import java.awt.Font;

public final class CustomFont {

  public static Font createCustomFont(String font_location, float font_size){

    //Initialize default font
    Font font = new Font("Courier New", 1, 25);

    try {
      //create the font, specify size
      font = Font.createFont(Font.TRUETYPE_FONT, new File(font_location)).deriveFont(font_size);

      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      //register the font
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(font_location)));
    } catch (IOException e) {
        e.printStackTrace();
        System.exit(-1);
    } catch(FontFormatException e) {
        e.printStackTrace();
        System.exit(-1);
    }

    return font;

  }
}

/*
 * sources:
 *
 * [CMSC 170] unused custom text panel for TicTacToe
 *
 * on custom font:
 *
 * answered Jul 7 '13 at 18:25 Phloo
 * http://stackoverflow.com/questions/5652344/how-can-i-use-a-custom-font-in-java/17514968#17514968
 */
