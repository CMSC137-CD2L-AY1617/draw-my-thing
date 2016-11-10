import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.FlowLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class DrawPanel extends JPanel implements ActionListener {

  private Color selectedColor = Color.BLACK;
  private Geometry selectedTool = Geometry.PENCIL;
  private ArrayList<Point> pointList = new ArrayList<Point>();

  private static final float MEDIUM_BRUSH_SIZE = 5.0f;
  private static final float LARGE_BRUSH_SIZE = 10.0f;

  private static final BasicStroke LARGE_BRUSH_STROKE = new BasicStroke(LARGE_BRUSH_SIZE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  private static final BasicStroke MEDIUM_BRUSH_STROKE = new BasicStroke(MEDIUM_BRUSH_SIZE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  private static final BasicStroke DEFAULT_STROKE = new BasicStroke(BasicStroke.CAP_SQUARE);

  public void setSelectedColor(Color color){

    selectedColor = color;

  }

  public Color getSelectedColor(){

    return selectedColor;

  }

  public DrawPanel() {

    // add tools as a group
    ButtonGroup tools = new ButtonGroup();
    JRadioButton lineButton = new JRadioButton("Line");
    JRadioButton ovalButton = new JRadioButton("Ellipse");
    JRadioButton rectangleButton = new JRadioButton("Rectangle");
    JRadioButton pencilButton = new JRadioButton("Pencil", true);
    JRadioButton mediumBrushButton = new JRadioButton("Medium Brush");
    JRadioButton largeBrushButton = new JRadioButton("Large Brush");

    tools.add(lineButton);
    tools.add(ovalButton);
    tools.add(rectangleButton);
    tools.add(pencilButton);
    tools.add(mediumBrushButton);
    tools.add(largeBrushButton);

    lineButton.addActionListener(this);
    ovalButton.addActionListener(this);
    rectangleButton.addActionListener(this);
    pencilButton.addActionListener(this);
    mediumBrushButton.addActionListener(this);
    largeBrushButton.addActionListener(this);

    JPanel toolPanel = new JPanel(new FlowLayout());

    for (Enumeration<AbstractButton> e = tools.getElements(); e.hasMoreElements();){

      e.nextElement().setBackground(Palette.POINTS_OF_LIGHT);
    }

    toolPanel.setBackground(Palette.POINTS_OF_LIGHT);
    toolPanel.add(lineButton);
    toolPanel.add(ovalButton);
    toolPanel.add(rectangleButton);
    toolPanel.add(pencilButton);
    toolPanel.add(mediumBrushButton);
    toolPanel.add(largeBrushButton);

    this.setLayout(new BorderLayout());
    this.add(toolPanel, BorderLayout.SOUTH);
    this.add(new DrawSurface(), BorderLayout.CENTER);

  }

  public void actionPerformed(ActionEvent ae) {

    String selected = ae.getActionCommand().toString();
    selected = selected.toUpperCase();
    selected = selected.replace(" ", "_");

    selectedTool = Geometry.valueOf(selected);

  }

  private class DrawSurface extends JComponent {
    LinkedList<ColoredGeometry> all_shapes = new LinkedList<ColoredGeometry>();
    LinkedList<ColoredGeometry> shapes = new LinkedList<ColoredGeometry>();
    LinkedList<ColoredGeometry> lines = new LinkedList<ColoredGeometry>();
    LinkedList<ColoredGeometry> mediumBrushes = new LinkedList<ColoredGeometry>();
    LinkedList<ColoredGeometry> largeBrushes = new LinkedList<ColoredGeometry>();

    Point startDrag, endDrag;

    public DrawSurface() {
      this.addMouseListener(new MouseAdapter() {

        public void mousePressed(MouseEvent e) {
          startDrag = new Point(e.getX(), e.getY());
          endDrag = startDrag;

          if(isFreeDraw()){
            pointList.add(startDrag);
          }

          repaint();
        }

        public void mouseReleased(MouseEvent e) {
          Shape r = null;

          if(selectedTool == Geometry.RECTANGLE){
            r = makeRectangle(startDrag.x, startDrag.y, e.getX(), e.getY());
          }
          else if(selectedTool == Geometry.LINE){
            r = makeLine(startDrag.x, startDrag.y, e.getX(), e.getY());
          }
          else if(selectedTool == Geometry.ELLIPSE){
            r = makeEllipse(startDrag.x, startDrag.y, e.getX(), e.getY());
          }
          else if(isFreeDraw()){
            r = makeFreeLine(pointList);
          }

          all_shapes.add(new ColoredGeometry(r, getSelectedColor(), selectedTool));

          if(isFreeDraw()){
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

          if(isFreeDraw()){
            pointList.add(endDrag);

            Graphics g = getGraphics();
            g.drawLine(endDrag.x, endDrag.y, endDrag.x, endDrag.y);
          }

          repaint();
        }
      });
    }

    private boolean isFreeDraw(){
      return (selectedTool == Geometry.PENCIL ||
              selectedTool.name().contains("BRUSH") );
    }

    private Graphics2D setStrokeToUse(Graphics2D g2, Geometry tool){

      g2.setStroke(DEFAULT_STROKE);

      if(tool == Geometry.MEDIUM_BRUSH){
        g2.setStroke(MEDIUM_BRUSH_STROKE);
      }
      else if(tool == Geometry.LARGE_BRUSH){
        g2.setStroke(LARGE_BRUSH_STROKE);
      }

      return g2;
    }

    // draw grid
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

      // set stroke
      g2 = setStrokeToUse(g2, selectedTool);

      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

      for (ColoredGeometry entry : all_shapes){
        g2 = setStrokeToUse(g2, entry.getGeometry());
        g2.setPaint(entry.getColor());
        g2.draw(entry.getShape());
      }

      // still dragging
      if (startDrag != null && endDrag != null) {
        g2.setPaint(Color.LIGHT_GRAY);

        g2 = setStrokeToUse(g2, selectedTool);

        Shape r = null;

        if(selectedTool == Geometry.RECTANGLE){
          r = makeRectangle(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
        }
        else if(selectedTool == Geometry.LINE){
          r = makeLine(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
        }
        else if(selectedTool == Geometry.ELLIPSE){
          r = makeEllipse(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
        }

        if(isFreeDraw()){
          r = makeFreeLine(pointList);
        }

        g2.draw(r);

      }
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
 * guide on drawing freehand lines on mousedrag
 * http://stackoverflow.com/questions/6039831/freehand-drawline-using-mouse-events/13709256#13709256
 *
 * java general path usage example, to be used with the freehand lines tutorial
 * https://kodejava.org/how-do-i-draw-a-generalpath-in-java-2d/
 *
 * accessing enum name
 * https://dzone.com/articles/enum-tricks-dynamic-enums
 * http://stackoverflow.com/questions/6667243/using-enum-values-as-string-literals/6667365#6667365
 */
