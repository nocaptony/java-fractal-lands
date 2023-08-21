
// ColouredPlane.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* ColouredPlane creates a single QuadArray made of 4 points,
   along with a specified colour and normal. The normal is
   used with all the points.
*/


import javax.media.j3d.*;
import javax.vecmath.*;


public class ColouredPlane extends Shape3D 
{
  private static final int NUM_VERTS = 4;

  public ColouredPlane(Point3d p1, Point3d p2, Point3d p3, Point3d p4,
								Vector3f normVec, Color3f col) 
  { createGeometry(p1, p2, p3, p4, normVec);
    createAppearance(col);
  } // end of ColouredPlane()


  private void createGeometry(Point3d p1, Point3d p2, Point3d p3, Point3d p4,
										Vector3f normVec)
  {
    QuadArray plane = new QuadArray(NUM_VERTS, 
			GeometryArray.COORDINATES | GeometryArray.NORMALS );

    // counter-clockwise point specification
    plane.setCoordinate(0, p1);
    plane.setCoordinate(1, p2);
    plane.setCoordinate(2, p3);
    plane.setCoordinate(3, p4);

    Vector3f[] norms = new Vector3f[NUM_VERTS];
    for (int i=0; i < NUM_VERTS; i++)
       norms[i] = normVec;   // same normal for all points
    plane.setNormals(0, norms);

    setGeometry(plane);
  }  // end of createGeometry()


  private void createAppearance(Color3f col)
  {
    Appearance app = new Appearance();

    // mateial with lighting effects
    Material mat = new Material();
    mat.setDiffuseColor(col); 
    mat.setLightingEnable(true);

    app.setMaterial(mat);
    setAppearance(app);
  }  // end of createAppearance()


} // end of ColouredPlane class
