
// PointHeight.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/*  Read in a file of 4 coordinates defining a 1 by 1 square in the
    XZ plane, but with various heights (y-coords). The coords are
    labelled P, Q, R, S, starting at the bottom-left corner and
    going counter-clockwise, when viewed from above.

    Also read in a (x,z) pair that is located somewhere within the
    quad defined by the coords.

    Calculate the _two_ possible heights for that (x,z) coord, depending
    on how the quad is triangulated. 
    (There are two possible triangulations around the lines PR and SQ.)
*/

import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

import javax.vecmath.*;



public class PointHeight
{
  // used to say if the pt is above or below a triangulation line
  private static final int ABOVE = 0;
  private static final int BELOW = 1;

  private DecimalFormat df;    // for output formatting

  private Vector3d coordP, coordQ, coordR, coordS, pt;
  private int posSQ, posPR; 
   /* The pt can be above or below the two triangulation lines
      defined by SQ and PR. 
      posSQ and posPR can take the values ABOVE or BELOW.
   */


  public PointHeight(String fnm)
  {
    df = new DecimalFormat("0.###");  // 3 dp

    getFileCoords(fnm);

    if (inRange(pt)) {
      relativeToLines(pt);
      calcHeightSQ(posSQ, pt);   // pt relationship with SQ
      calcHeightPR(posPR, pt);   // pt relationship with PR
    }
    else
      System.out.println("Point outside area defined by PQRS");
  
  } // end of PointHeight()



  private void getFileCoords(String fnm)
  /* Obtain coords and point info from fnm, with the format:
        num num num     // P coord
        num num num     // Q coord
        num num num     // R coord
        num num num     // S coord
        num num         // (x,z) of point 
  */
  { try {
      BufferedReader br = new BufferedReader( new FileReader(fnm));
      coordP = getCoord('P', br.readLine() );   
      coordQ = getCoord('Q', br.readLine() );   
      coordR = getCoord('R', br.readLine() );   
      coordS = getCoord('S', br.readLine() );

      pt = getPoint( br.readLine() );

      br.close();
    } 
    catch (IOException e) 
    { System.out.println("Error reading coords file: " + fnm);
      System.exit(1);
    }
  }  // end of getFileCoords()



  private Vector3d getCoord(char ptCh, String line)
  // return (x,y,z)
  {
    double vals[] = new double[3];            // for the coords data
    vals[0] = 0; vals[1] = 0; vals[2] = 0;    // represents (x,y,z)

    StringTokenizer tokens = new StringTokenizer(line);
    String token;
    int count = 0;
    while (tokens.hasMoreTokens() && (count < 3)) {
      token = tokens.nextToken();
      try {
        vals[count] = Double.parseDouble(token);
        count++;
      }
      catch (NumberFormatException ex){ 
        System.out.println("Incorrect format for coord " + ptCh); 
        break;
      }
    }
    if (count != 3)
      System.out.println("Incorrect data for coord" + ptCh);

    Vector3d pt = new Vector3d( vals[0], vals[1], vals[2]);
    System.out.println(ptCh + " coord: (" + df.format(pt.x) +
				", " + df.format(pt.y) + ", " + df.format(pt.z) + ")" );
    return pt;
  }  // end of getCoord()



  private Vector3d getPoint(String line)
  // return (x,0,z); 0 is not used
  {
    double vals[] = new double[2];  // for the (x,z) data in the point
    vals[0] = 0; vals[1] = 0;

    StringTokenizer tokens = new StringTokenizer(line);
    String token;
    int count = 0;
    while (tokens.hasMoreTokens() && (count < 2)) {
      token = tokens.nextToken();
      try {
        vals[count] = Double.parseDouble(token);
        count++;
      }
      catch (NumberFormatException ex){ 
        System.out.println("Incorrect format for the point"); 
        break;
      }
    }
    if (count != 2)
      System.out.println("Incorrect data for the point");

    Vector3d pt = new Vector3d( vals[0], 0, vals[1]);  // dummy y- value
    System.out.println("Point: (" + df.format(pt.x) +
							 ", " + df.format(pt.z) + ")" );
    return pt;
  }  // end of getCoord()



  private boolean inRange(Vector3d pt)
  // is pt within the quad defined by ABCD?
  {
    if ((pt.x < coordS.x) || (pt.x > coordQ.x) || 
        (pt.z < coordS.z) || (pt.z > coordQ.z))
      return false;
    return true;
  }  // end of inRange()



  private void relativeToLines(Vector3d pt)
  /* Work out if the pt is above or below the two triangulation
     lines DB and AC.
  */
  {
    // test of SQ:  z = x + (S.z-S.x)
    if (pt.z > (pt.x + coordS.z - coordS.x))
      posSQ = ABOVE;
    else
      posSQ = BELOW;

    // test of PR:  z = -x + (P.z+P.x)
    if (pt.z > (-pt.x + coordP.z + coordP.x))
      posPR = ABOVE;
    else
      posPR = BELOW;

    String relSQ = (posSQ == ABOVE) ? "Above " : "Below ";
    String relPR = (posPR == ABOVE) ? "Above " : "Below ";
    System.out.println("Line Relationships: " +
							relSQ + "SQ;  " + relPR + "PR");
  } // end of relativeToLines()



  private void calcHeightSQ(int linePos, Vector3d pt)
  /* Calculate the two vectors u, v that define the
     triangle which uses the SQ triangulation line
   
     pt is ABOVE / BELOW the line SQ, as recorded in linePos.
  */
  {
    Vector3d u = new Vector3d();
    Vector3d v = new Vector3d();

    if (linePos == BELOW) {
      System.out.println("\nSQ Triangle SQR");
      u.set( 1, coordQ.y-coordS.y, 1);    // Q-S
      v.set( 1, coordR.y-coordS.y, 0);    // R-S
      vecsToHeight(u, v, coordS);
    }
    else if (linePos == ABOVE) {
      System.out.println("\nSQ Triangle SPQ");
      u.set( 1, coordQ.y-coordP.y, 0);     // Q-P
      v.set( 0, coordS.y-coordP.y, -1);    // S-P
      vecsToHeight(u, v, coordP);
    }
    else
      System.out.println("calcHeightSQ() error");
  }  // end of calcHeightSQ()



  private void calcHeightPR(int linePos, Vector3d pt)
  /* Calculate the two vectors u, v that define the
     triangle which uses the PR triangulation line
   
     pt is ABOVE / BELOW the line PR, as recorded in linePos.
  */
  {
    Vector3d u = new Vector3d();
    Vector3d v = new Vector3d();

    if (linePos == BELOW) {
      System.out.println("\nPR Triangle PRS");
      u.set( 1, coordR.y-coordP.y, -1);     // R-P
      v.set( 0, coordS.y-coordP.y, -1);     // S-P
      vecsToHeight(u, v, coordP);
    }
    else if (linePos == ABOVE) {
      System.out.println("\nPR Triangle RPQ");
      u.set( 1, coordQ.y-coordP.y, 0);     // Q-P
      v.set( 1, coordR.y-coordP.y, -1);    // R-P
      vecsToHeight(u, v, coordP);
    }
    else
      System.out.println("calcHeightPR() error");
  }  // end of calcHeightPR()



  private void vecsToHeight(Vector3d u, Vector3d v, Vector3d pt)
  /* Calculate the normal to the two vectors u and v.

     Calculate the distance to the origin by using the dot product
     on the normal and any point covered by the triangle.

     The normal is (A, B, C) and distance is D.
     These define the equation for the triangle:
            (A * x) + (B * y) + (C * z) = D

     Use this equation to calculate the y value for the pt at (x,z)
  */
  {
    Vector3d normal = new Vector3d();
    normal.cross(u, v);
    normal.normalize();
    double dist = normal.dot(pt);

    System.out.println("A: "+ df.format(normal.x) + ", B: " +
			df.format(normal.y) + ", C: " + df.format(normal.z) +
			", D: " + df.format(dist) );    // Ax + By + Cz = D

    double height = (dist - (normal.x * pt.x) - (normal.z * pt.z)) / normal.y;
    System.out.println("Height for pt: " + df.format(height) );
  }  // end of vecsToHeight()



  // ---------------------- main() -----------------------

  public static void main(String args[])
  {
    if (args.length != 1) {
      System.out.println( "Usage: java PointHeight <file>");
      System.exit(0);
    }
    new PointHeight(args[0]);
  } // end of main()



} // end of PointHeight class