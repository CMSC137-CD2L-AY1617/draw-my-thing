/*
 * wrapper class representing a previously drawn line or shape and its respective color
 */

import java.awt.Color;
import java.awt.Shape;

public class ColoredGeometry {

  private Shape shape;
  private Color color;

  ColoredGeometry(Shape s, Color c){
    shape = s;
    color = c;
  }

  public Shape getKey(){
    return shape;
  }

  public Color getValue(){
    return color;
  }

}
