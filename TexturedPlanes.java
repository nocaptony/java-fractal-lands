
// TexturedPlanes.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* TexturedPlanes contains many quads, each within a certain height
   range. They will all be assigned the same texture.

   In order to obtain lighting effects, we must calculate
   the normals (and mix the texture with a Material). We
   also stripify to improve the performance.

   Very similar to TexturedPlanes in /Maze3D but now the normals
   are calculated using GeometryInfo and NormalGenerator. 

   Picking is enabled so that getLandHeight() from Landscape can 
   find the floor's height at any point.
*/

import java.util.ArrayList;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.*;
import com.sun.j3d.utils.picking.PickTool;


public class TexturedPlanes extends Shape3D 
{

  public TexturedPlanes(ArrayList coords, String fnm) 
  {
    System.out.println(fnm + "; numPoints: " + coords.size());
    createGeometry(coords);
    createAppearance(fnm);

	// set the picking capabilities so that intersection
    // coords can be extracted after the shape is picked
	PickTool.setCapabilities(this, PickTool.INTERSECT_COORD);
  } // end of TexturedPlanes()


  private void createGeometry(ArrayList coords)
  {
    int numPoints = coords.size();
    QuadArray plane = new QuadArray(numPoints, 
						GeometryArray.COORDINATES | 
						GeometryArray.TEXTURE_COORDINATE_2 |
						GeometryArray.NORMALS );

    // set coordinates
    Point3d[] points = new Point3d[numPoints];
    coords.toArray( points );

    // assign texture coords to each quad
    // counter-clockwise, from bottom left
    TexCoord2f[] tcoords = new TexCoord2f[numPoints];
    for(int i=0; i < numPoints; i=i+4) {
      tcoords[i] = new TexCoord2f(0.0f, 0.0f);   // for 1 point
      tcoords[i+1] = new TexCoord2f(1.0f, 0.0f);
      tcoords[i+2] = new TexCoord2f(1.0f, 1.0f);
      tcoords[i+3] = new TexCoord2f(0.0f, 1.0f);
    }

    // create geometryInfo
    GeometryInfo gi = new GeometryInfo(GeometryInfo.QUAD_ARRAY);
    gi.setCoordinates(points);
    gi.setTextureCoordinateParams(1, 2); // one set of 2D texels
    gi.setTextureCoordinates(0, tcoords);

    // calculate normals with very smooth edges
    NormalGenerator ng = new NormalGenerator();
    ng.setCreaseAngle( (float) Math.toRadians(150));   // default is 44
    ng.generateNormals(gi);

    // stripifier to use triangle strips
    Stripifier st = new Stripifier();
    st.stripify(gi);

    // extract and use GeometryArray
    setGeometry( gi.getGeometryArray() );
  }  // end of createGeometry()




  private void createAppearance(String fnm)
  // combine the texture with a lit white surface
  {
    Appearance app = new Appearance();

    // mix the texture and the material colour
    TextureAttributes ta = new TextureAttributes();
    ta.setTextureMode(TextureAttributes.MODULATE);
    app.setTextureAttributes(ta);

    // load and set the texture; generate mipmaps for it
    TextureLoader loader = new TextureLoader(fnm, 
							TextureLoader.GENERATE_MIPMAP, null);

    Texture2D texture = (Texture2D) loader.getTexture();
    texture.setMinFilter(Texture2D.MULTI_LEVEL_LINEAR);  // NICEST

    app.setTexture(texture);      // set the texture

    // set a default white material
    Material mat = new Material();
    mat.setLightingEnable(true);    // lighting switched on
    app.setMaterial(mat);

    setAppearance(app);
  }  // end of createAppearance()


} // end of TexturedPlanes class
