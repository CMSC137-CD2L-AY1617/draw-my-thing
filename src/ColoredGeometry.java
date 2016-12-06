import java.awt.Color;
import java.awt.Shape;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

/*
 * represents:
 * a previously drawn line or shape,
 * its respective color, and
 * its type
 */

public class ColoredGeometry implements Serializable {

  private Shape shape;
  private Geometry type;
  private Color color;
  private static final long serialVersionUID = 1L;

  ColoredGeometry(Shape s, Color c, Geometry geom){
    shape = s;
    type = geom;
    color = c;
  }

  public Shape getShape(){
    return shape;
  }

  public Color getColor(){
    return color;
  }

  public Geometry getGeometry(){
    return type;
  }

  public static byte[] getByteArray(ColoredGeometry drawing){
    ByteArrayOutputStream byteArr = null;
    ObjectOutputStream obj = null;

    try{
      byteArr = new ByteArrayOutputStream();
      obj = new ObjectOutputStream(byteArr);

      obj.flush();
      obj.writeObject(drawing);
      obj.flush();
    } catch(IOException e){
      e.printStackTrace();
    }

    return byteArr.toByteArray();

  }



  public static ColoredGeometry getObject(byte[] object){
    ColoredGeometry drawing = null;
    ByteArrayInputStream byteArr = null;
    ObjectInputStream obj = null;

    try{
      byteArr = new ByteArrayInputStream(object);
      obj = new ObjectInputStream(byteArr);

      drawing = (ColoredGeometry)obj.readObject();
      obj.close();
      byteArr.close();
    } catch(EOFException eof){
      eof.printStackTrace();
    } catch(StreamCorruptedException sce){
      sce.printStackTrace();
    } catch(IOException e){
      e.printStackTrace();
    } catch(ClassNotFoundException cnfe){
      cnfe.printStackTrace();
    }

    return drawing;

  }

  private void writeObject(ObjectOutputStream stream){
    try{
      stream.writeObject(shape);
      stream.writeObject(type);
      stream.writeObject(color);
    } catch(IOException e){
      e.printStackTrace();
    }
  }

  private void readObject(ObjectInputStream stream){
    try{
      shape = (Shape)stream.readObject();
      type = (Geometry)stream.readObject();
      color = (Color)stream.readObject();
    } catch(IOException e){
      e.printStackTrace();
    } catch(ClassNotFoundException cnfe){
      cnfe.printStackTrace();
    }
  }

  public String toString(){
    String str = this.getShape()+Server.DELIMITER+
                 this.getGeometry()+Server.DELIMITER+
                 this.getColor().getRGB()+Server.DELIMITER;

    return str;
  }

}

/*
 * sources:
 *
 * Serialization
 * http://www.javaworld.com/article/2077539/learn-java/java-tip-40--object-transport-via-datagram-packets.html
 */
