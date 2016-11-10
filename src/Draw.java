import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Draw extends JPanel implements ActionListener {

  Color selectedColor = Color.BLACK;
  String shapeType = "";
  ArrayList<Point> pointList = new ArrayList<Point>();
  // int xDragged, yDragged, xPressed, yPressed;

  public static void main(String[] args) {
    new Draw();
  }

  public void setSelectedColor(Color color){
    selectedColor = color;
    // System.out.println("yeah");
  }

  public Color getSelectedColor(){

    // System.out.println("whooo");
    return selectedColor;
  }

  public Draw() {
    // add check box group
    ButtonGroup cbg = new ButtonGroup();
    JRadioButton lineButton = new JRadioButton("Line");
    JRadioButton ovalButton = new JRadioButton("Oval");
    JRadioButton rectangleButton = new JRadioButton("Rectangle");
    JRadioButton freeDrawButton = new JRadioButton("FreeDraw");
    JRadioButton mediumBrushButton = new JRadioButton("MediumBrush");
    JRadioButton largeBrushButton = new JRadioButton("LargeBrush");
    // JButton clearButton = new JButton("Clear");

    cbg.add(lineButton);
    cbg.add(ovalButton);
    cbg.add(rectangleButton);
    cbg.add(freeDrawButton);
    cbg.add(mediumBrushButton);
    cbg.add(largeBrushButton);
    // cbg.add(clearButton);

    lineButton.addActionListener(this);
    ovalButton.addActionListener(this);
    rectangleButton.addActionListener(this);
    freeDrawButton.addActionListener(this);
    mediumBrushButton.addActionListener(this);
    largeBrushButton.addActionListener(this);
    // clearButton.addActionListener(this);

    // lineButton.setSelected(true); shapeType = "Line";
    // ovalButton.setSelected(true); shapeType = "Oval";
    // rectangleButton.setSelected(true); shapeType = "Rectangle";
    // freeDrawButton.setSelected(true); shapeType = "FreeDraw";
    // mediumBrushButton.setSelected(true); shapeType = "MediumBrush";
    largeBrushButton.setSelected(true); shapeType = "LargeBrush";

    JPanel radioPanel = new JPanel(new FlowLayout());

    radioPanel.add(lineButton);
    radioPanel.add(ovalButton);
    radioPanel.add(rectangleButton);
    radioPanel.add(freeDrawButton);
    radioPanel.add(mediumBrushButton);
    radioPanel.add(largeBrushButton);
    // radioPanel.add(clearButton);

    // this.addMouseListener(this);
    this.setLayout(new BorderLayout());
    this.add(radioPanel, BorderLayout.NORTH);
    this.add(new PaintSurface(), BorderLayout.CENTER);

    // this.setSize(600, 300);
    // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // this.setVisible(true);
  }

  public void actionPerformed(ActionEvent ae) {
    shapeType = ae.getActionCommand().toString();
    // System.out.println(shapeType);
  }

  private class PaintSurface extends JComponent {
    LinkedList<ColoredGeometry> shapes = new LinkedList<ColoredGeometry>();
    LinkedList<ColoredGeometry> lines = new LinkedList<ColoredGeometry>();
    LinkedList<ColoredGeometry> mediumBrushes = new LinkedList<ColoredGeometry>();
    LinkedList<ColoredGeometry> largeBrushes = new LinkedList<ColoredGeometry>();

    Point startDrag, endDrag;

    public PaintSurface() {
      this.addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
          // System.out.println(e.getX()+" "+e.getY());
          startDrag = new Point(e.getX(), e.getY());
          endDrag = startDrag;

          if(shapeType.compareTo("FreeDraw")==0 || shapeType.contains("Brush")){
            pointList.add(startDrag);
            // xPressed = startDrag.x;
            // yPressed = startDrag.y;
            // System.out.println("xPressed: "+xPressed+" ,yPressed: "+yPressed);
            // xDragged=xPressed;
            // yDragged=yPressed;
          }
          repaint();
        }

        public void mouseReleased(MouseEvent e) {
          // System.out.println(e.getX()+" "+e.getY());
          Shape r = null;

          if(shapeType.compareTo("Rectangle")==0){
            r = makeRectangle(startDrag.x, startDrag.y, e.getX(), e.getY());
          }
          else if(shapeType.compareTo("Line")==0){
            r = makeLine(startDrag.x, startDrag.y, e.getX(), e.getY());
          }
          else if(shapeType.compareTo("Oval")==0){
            r = makeEllipse(startDrag.x, startDrag.y, e.getX(), e.getY());
          }
          else if(shapeType.compareTo("FreeDraw")==0 || shapeType.contains("Brush")){
            r = makeFreeLine(pointList);
            // xDragged = null;
            // yDragged = null;
            // xPressed = null;
            // yPressed = null;
          }

          if(shapeType.compareTo("FreeDraw")!=0 && !shapeType.contains("Brush")){
            // shapes.add(r);
            // shapes.put(r,getSelectedColor());
            shapes.add(new ColoredGeometry(r, getSelectedColor()));
          }
          else{
            if(shapeType.compareTo("FreeDraw")==0){
              // lines.add(r);
              // lines.put(r,getSelectedColor());
              lines.add(new ColoredGeometry(r, getSelectedColor()));
            }
            else if(shapeType.compareTo("MediumBrush")==0){
              // mediumBrushes.add(r);
              // mediumBrushes.put(r,getSelectedColor());
              mediumBrushes.add(new ColoredGeometry(r, getSelectedColor()));
            }
            else if(shapeType.compareTo("LargeBrush")==0){
              // largeBrushes.add(r);
              // largeBrushes.put(r,getSelectedColor());
              largeBrushes.add(new ColoredGeometry(r, getSelectedColor()));
            }
            pointList.clear();
          }

          startDrag = null;
          endDrag = null;
          repaint();


        }
      });

      this.addMouseMotionListener(new MouseMotionAdapter() {
        public void mouseDragged(MouseEvent e) {
          endDrag = new Point(e.getX(), e.getY());
          if(shapeType.compareTo("FreeDraw")==0 || shapeType.contains("Brush")){
            pointList.add(endDrag);

            Graphics g = getGraphics();

            g.drawLine(endDrag.x, endDrag.y, endDrag.x, endDrag.y);

            // xDragged = endDrag.x;
            // yDragged = endDrag.y;
          }


          // System.out.println(endDrag.x+" "+endDrag.y);
          // System.out.println(startDrag.x+" "+startDrag.y);
          // System.out.println();

          repaint();
        }
      });
    }

    private void paintBackground(Graphics2D g2){
      g2.setPaint(Color.LIGHT_GRAY);
      for (int i = 0; i < getSize().width; i += 10) {
        Shape line = new Line2D.Float(i, 0, i, getSize().height);
        g2.draw(line);
      }

      for (int i = 0; i < getSize().height; i += 10) {
        Shape line = new Line2D.Float(0, i, getSize().width, i);
        g2.draw(line);
      }


    }

    public void paint(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;

      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      paintBackground(g2);
      Color[] colors = { Color.YELLOW, Color.MAGENTA, Color.CYAN , Color.RED, Color.BLUE, Color.PINK};
      int colorIndex = 0;

      if(shapeType.compareTo("MediumBrush")==0){
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      }
      else if(shapeType.compareTo("LargeBrush")==0){
        g2.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      }
      else{
        g2.setStroke(new BasicStroke(BasicStroke.CAP_SQUARE));
      }

      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

      for (ColoredGeometry entry : shapes){
        // System.out.println(entry.getKey() + "/" + entry.getValue());
        g2.setPaint(entry.getValue());
        g2.draw(entry.getKey());
      }

      // for (Entry<ColoredGeometry> s : shapes) {
      //   // g2.setPaint(Color.BLACK);
      //   g2.setPaint(s.key);
      //   g2.draw(s.value);
      //   // g2.setPaint(colors[(colorIndex++) % 6]);
      //   // g2.fill(s);
      // }

      for (ColoredGeometry entry : lines){
        // System.out.println(entry.getKey() + "/" + entry.getValue());
        g2.setPaint(entry.getValue());
        g2.draw(entry.getKey());
      }

      // for (Entry<ColoredGeometry> l : lines) {
      //   // g2.setPaint(Color.BLACK);
      //   g2.setPaint(l.key);
      //   g2.draw(l.value);
      //   // g2.setPaint(colors[(colorIndex++) % 6]);
      //   // g2.fill(l);
      // }

      for (ColoredGeometry entry : mediumBrushes){
        // System.out.println(entry.getKey() + "/" + entry.getValue());
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setPaint(entry.getValue());
        g2.draw(entry.getKey());
      }

      // for (Entry<ColoredGeometry> mdBrush : mediumBrushes) {
      //   g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      //   // g2.setPaint(Color.BLACK);
      //   g2.setPaint(mdBrush.key);
      //   g2.draw(mdBrush.value);
      //   // g2.setPaint(colors[(colorIndex++) % 6]);
      //   // g2.fill(l);
      // }

      for (ColoredGeometry entry : largeBrushes){
        // System.out.println(entry.getKey() + "/" + entry.getValue());
        g2.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setPaint(entry.getValue());
        g2.draw(entry.getKey());
      }

      // for (Entry<ColoredGeometry> lgBrush : largeBrushes) {
      //   g2.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      //   // g2.setPaint(Color.BLACK);
      //   g2.setPaint(lgBrush.key);
      //   g2.draw(lgBrush.value);
      //   // g2.setPaint(colors[(colorIndex++) % 6]);
      //   // g2.fill(l);
      // }

      // still dragging
      if (startDrag != null && endDrag != null) {
        g2.setPaint(Color.LIGHT_GRAY);

        if(shapeType.compareTo("MediumBrush")==0){
          g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }
        else if(shapeType.compareTo("LargeBrush")==0){
          g2.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }
        else{
          g2.setStroke(new BasicStroke(BasicStroke.CAP_SQUARE));
        }

          // System.out.println(startDrag.x+" "+startDrag.x);
          // System.out.println(endDrag.x+" "+endDrag.x);

        Shape r = null;

        if(shapeType.compareTo("Rectangle")==0){
          r = makeRectangle(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
        }
        else if(shapeType.compareTo("Line")==0){
          r = makeLine(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
        }
        else if(shapeType.compareTo("Oval")==0){
          r = makeEllipse(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
        }

        if(shapeType.compareTo("FreeDraw")==0 || shapeType.contains("Brush")){
          r = makeFreeLine(pointList);
        }

        g2.draw(r);

      }
    }

    private LinkedList<ColoredGeometry> reverseLinkedList(LinkedList<ColoredGeometry> list){

      LinkedList<ColoredGeometry> reverse = new LinkedList<ColoredGeometry>();

      for(int i=list.size(); i>0; i--){
        reverse.add(list.get(i-1));
      }

      return reverse;
    }

    private Line2D.Float makeLine(int x1, int y1, int x2, int y2) {
      return new Line2D.Float(x1, y1, x2, y2);
    }

    private GeneralPath makeFreeLine(ArrayList<Point> pointList) {

        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, pointList.size());

        path.moveTo(pointList.get(0).x, pointList.get(0).y);

        for (int i = 1; i < pointList.size(); i++){
          Point p = pointList.get(i);
          path.lineTo(p.x, p.y);
        }

      return path;
    }

    private Ellipse2D.Float makeEllipse(int x1, int y1, int x2, int y2) {
      return new Ellipse2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    private Rectangle2D.Float makeRectangle(int x1, int y1, int x2, int y2) {
      return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
    }
  }
}

/*
 * sources:
 *
 * tutorial for drawing square/rectangles on mouse drag;
 * modified to include ability to draw circle/oval and line
 * further modified to include freehand lines / brushes
 * http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/Mousedraganddraw.htm
 *
 * guide on drawing freehand lines on mousedrag [Able Johnson's]
 * http://stackoverflow.com/questions/6039831/freehand-drawline-using-mouse-events
 *
 * java general path usage example, to be used with the freehand lines tutorial
 * https://kodejava.org/how-do-i-draw-a-generalpath-in-java-2d/
 *
 */
