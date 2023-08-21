
// FractalLand3D.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* A fractal landscape is generated, made out of a mesh
   of textured squares. Squares at different heights are
   textured in different ways. 

   The bumpiness of the landscape is controlled by a flatness
   value input from the command line (or a default value can
   be used).

   The landscape is surrounded by dark blue walls.

   The user can 'walk' over the landscape using the
   similar left/right/front/back/turn/up/down moves
   as in the FPShooter3D example.
*/

import javax.swing.*;
import java.awt.*;

public class FractalLand3D extends JFrame {
  private static final long serialVersionUID = 1L;
  private static final double DEF_FLAT = 4; // makes a smooth-ish landscape
  private static final double MIN_FLAT = 1.6; // rough
  private static final double MAX_FLAT = 2.5; // very flat

  public FractalLand3D(String[] args) {
    super("3D Fractal Landscape");

    double flatness = processArgs(args);
    System.out.println("Flatness: " + flatness);

    WrapFractalLand3D w3d = new WrapFractalLand3D(flatness);

    Container c = getContentPane();
    c.setLayout(new BorderLayout());
    c.add(w3d, BorderLayout.CENTER);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setResizable(false); // fixed size display
    setVisible(true);
  } // end of FractalLand3D()

  private double processArgs(String[] args)
  // 0 or 1 argument is acceptable
  {
    double flatness = DEF_FLAT;
    if (args.length == 1)
      flatness = getFlatness(args[0]);
    else if (args.length > 1) {
      System.out.println("Usage: java FractalLand3D [<Flatness>]");
      System.exit(0);
    }
    return flatness;
  } // end of processArgs()

  private double getFlatness(String arg)
  // flatness must be a double within the range MIN_FLAT to MAX_FLAT
  {
    double flatness;
    try {
      flatness = Double.parseDouble(arg);
      if ((flatness < MIN_FLAT) || (flatness > MAX_FLAT)) {
        System.out.println("Flatness must be between " + MIN_FLAT + " and " + MAX_FLAT);
        flatness = DEF_FLAT;
      }
    } catch (NumberFormatException ex) {
      System.out.println("Incorrect format for Flatness double");
      flatness = DEF_FLAT;
    }
    return flatness;
  } // end of getFlatness()

  // -----------------------------------------

  public static void main(String[] args) {
    new FractalLand3D(args);
  }
} // end of FractalLand3D class
