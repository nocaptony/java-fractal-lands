
// TreeLimb.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/*   The scene graph for a tree limb contains:

    startBG --->orientTG --->scaleTG ---> baseTG ---> cylinder
                         |
                         ---> endLimbTG [ --> leafBG --> ImagesCsSeries 
                                          --> leafBG --> ImagesCsSeries  ]


   startBG is a BranchGroup, and is linked to a 'parent' limb
   at run time. The link is made to the parent's endLimbTG node. Since the
   linkage is being carried out at execution time, we must use a BranchGroup.

   orientTG: contains the orientation of the limb (axis of rotation, the angle).
             This does not change during execution.

   scaleTG: used to scale the x-, y-, and z- lengths of the cylinder.
            We keep the x- and z- scaling the same (they represent the radius
            of the cylinder), while the y-axis is the length.

   baseTG: the (0,0,0) point for Java 3D's Cylinder is its middle, so baseTG lifts
           it up the y-axis by length/2, so (0,0,0) is now the center of its base.

   endLimbTG is an offset of _almost_ the cylinder's length. It is a little less than
   the length, so that child limbs will overlap the parent limb. This partly hides 
   any gaps between the limbs when a child limb is orientated at an extreme angle.

   endLimbTG is not attached to Cylinder since that would make it prone to scaling,
   which would also affect any child limbs attached to endLimbTG.

   If leaves are added to a limb, then _two_ branches are added to endLimbTG. Both
   branches start with a BranchGroup node since they are added at run time.

   The ImagesCsSeries nodes are 'screens' which show a picture of leaves. The pictures
   can be adjusted at run time, creating various animation effects, in this case the
   illusion of leaves growing.
*/

//CLASS ADDED BY TONY AND JANI TO MAKE TREES WORK


import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.*;
import com.sun.j3d.utils.geometry.Cylinder;


public class TreeLimb
{
  private static final double OVERLAP = 0.1;     
       // percentage overlap of children limbs with this limb

  // no. of steps to change the starting colour (green) to brown
  private final static int MAX_COLOUR_STEP = 15;
  
  // axis constants
  private final static int X_AXIS = 0;
  private final static int Y_AXIS = 1;
  private final static int Z_AXIS = 2;

  // colours for limb material
  private final static Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
  // private final static Color3f specular = new Color3f(1.0f, 1.0f, 1.0f);

  /* green is the starting colour for a limb, brown its final colour */
  private final static Color3f green = new Color3f(0.0f, 1.0f, 0.1f); 
  private final static Color3f brown = new Color3f(0.35f, 0.29f, 0.0f);

  // incremental change in terms of RGB to go from green to brown
  private float redShift = (brown.x - green.x)/((float) MAX_COLOUR_STEP);
  private float greenShift = (brown.y - green.y)/((float) MAX_COLOUR_STEP);
  private float blueShift = (brown.z - green.z)/((float) MAX_COLOUR_STEP);

  private int orientAxis;              // limb's axis of initial orientation
  private double orientAngle = 0;      // angle to orientation axis

  private float radius;                // original radius of the cylinder
  private float limbLen;               // original length of the cylinder

  private TreeLimb parent;

  private TransformGroup scaleTG;  
  private TransformGroup endLimbTG;
  private Material limbMaterial;     // stores the colour 

  private Transform3D currTrans, toMove;    // used for repeated calcs
  private Vector3d endPos;

  private Vector3d scaleLimb;   // for scaling the tree limb
  private Color3f currColour;   // starts as green
  private int colourStep = 0;
  private int age = 0;

  private int numChildren = 0;
  private ArrayList limbChildren;    // stores child TreeLimb objects
  private int level;           // this limb's level in the overall tree

  private boolean hasLeaves;
  private ImageCsSeries frontLeafShape, backLeafShape;   // screens for showing the leaves



  public TreeLimb(int axis, double angle, float rad, float len, 
                           TransformGroup startLimbTG, TreeLimb par)
  { 
    orientAxis = axis;
    orientAngle = angle;
    radius = rad;
    limbLen = len;
    parent = par;

    scaleLimb = new Vector3d(1,1,1); 
    currColour = new Color3f( green );

    limbChildren = new ArrayList();

    if (parent == null)
      level = 1;
    else
      level = parent.getLevel() + 1;

    hasLeaves = false;
    frontLeafShape = null; backLeafShape = null;

    currTrans = new Transform3D();
    toMove = new Transform3D();
    endPos = new Vector3d();

    buildSubgraph(startLimbTG);

    if (parent != null)
      parent.addChildLimb(this);     // tell the parent that there's a new child 
  }  // end of TreeLimb()



  private void buildSubgraph(TransformGroup startLimbTG)
  /* Create the scene graph detailed in the comments above.
     startLimbTG is the parent's endLimbTG. */
  { 
    BranchGroup startBG = new BranchGroup();

    // set the limb's orientation
    TransformGroup orientTG = new TransformGroup();
    if (orientAngle != 0) {
      Transform3D trans = new Transform3D();
      if (orientAxis == X_AXIS)    
        trans.rotX( Math.toRadians(orientAngle));
      else if (orientAxis == Y_AXIS)
         trans.rotY( Math.toRadians(orientAngle));
      else    // must be z-axis
        trans.rotZ( Math.toRadians(orientAngle));
      orientTG.setTransform(trans);
    }

    // scaling node
    scaleTG = new TransformGroup();
    scaleTG.setCapability( TransformGroup.ALLOW_TRANSFORM_READ);
    scaleTG.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE);  // can alter

    // limb subgraph's sequence of TGs
    startBG.addChild(orientTG);
    orientTG.addChild(scaleTG);
    scaleTG.addChild( makeLimb() );

    TransformGroup endLimbTG = locateEndLimb();
    orientTG.addChild(endLimbTG);

    startBG.compile();

    startLimbTG.addChild(startBG);   // connect limb to parent's endLimbTG
  } // end of buildSubgraph()


  private TransformGroup makeLimb()
  // a green cylinder whose base is at (0,0,0)
  {
    // fix limb's start position
    TransformGroup baseTG = new TransformGroup();
    Transform3D trans1 = new Transform3D();
    trans1.setTranslation( new Vector3d(0, limbLen/2, 0) );   // move up length/2
    baseTG.setTransform(trans1);

    Appearance app = new Appearance();
    limbMaterial = new Material(black, black, green, brown, 50.f);  // specular, 100
    limbMaterial.setCapability( Material.ALLOW_COMPONENT_READ);
    limbMaterial.setCapability( Material.ALLOW_COMPONENT_WRITE);
              // can change colours; only the diffuse colour will be altered

    limbMaterial.setLightingEnable(true);

    app.setMaterial( limbMaterial );
    Cylinder cyl = new Cylinder( radius, limbLen, app);

    baseTG.addChild( cyl );
    return baseTG;
  }  // end of makeLimb()



  private TransformGroup locateEndLimb()
  {  
    // fix limb's end position, and store in endLimbTG
    endLimbTG = new TransformGroup();
    endLimbTG.setCapability( TransformGroup.ALLOW_CHILDREN_EXTEND );
    endLimbTG.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
    endLimbTG.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
 
    Transform3D trans2 = new Transform3D();   
    trans2.setTranslation(new Vector3d(0, limbLen*(1.0-OVERLAP), 0));
    /* The end position is just short of the actual length of the
       limb so that any child limbs will be placed so they overlap
       with this one. */   
    endLimbTG.setTransform(trans2);

    return endLimbTG;
  }  // end of locateEndLimb()


  // --------------------- public methods -----------------

  public TransformGroup getEndLimbTG()
  {  return endLimbTG;  }

  public void incrAge()
  { age++;  }

  public int getAge()
  {  return age;  }

  public int getLevel()
  {  return level;  }

  
  // ----------- scale get/set methods using length and radius ------------

  public double getScaleLength()
  {  return scaleLimb.y;  }

  public double getScaleRadius()
  {  return scaleLimb.x; }     // the scale values for x and z stay the same

  public void scaleLength(double yChange)
  { scaleLimb.y *= yChange;  
    applyScale();
  }

  public void scaleRadius(double radChange)
  { scaleLimb.x *= radChange;
    scaleLimb.z *= radChange;
    applyScale();
  }


  private void applyScale()
  { 
    moveEndLimbTG( scaleLimb.y);

    scaleTG.getTransform(currTrans);
    currTrans.setScale(scaleLimb);
    scaleTG.setTransform(currTrans);
  }  // applyScale()


  private void moveEndLimbTG( double yScale)
  /* Since endLimbTG is not attached to Cylinder, it will not automatically
     be adjusted when the cylinder is scaled (e.g. lengthened, shortened). This
     means that we must calculate the new position for endLimbTG ourselves.

     yScale is the amount that the Cylinder is about to be scaled, which
     must be applied to the y- value in endLimbTG
  */
  {
    endLimbTG.getTransform( currTrans );
    currTrans.get( endPos );         // current position of endLimbTG
    double currLimbLen = endPos.y;    
      // current y- position, the length of the cylinder including scaling

    double changedLen = ((double) limbLen*(1.0-OVERLAP) * yScale) - currLimbLen;
      // changedLen is the change in the y- value after scaling has been applied 

    endPos.set(0, changedLen, 0);       // use endPos to store the length change
    toMove.setTranslation( endPos );    // overwrite previous trans
    currTrans.mul(toMove);              
    endLimbTG.setTransform(currTrans);  // move endLimbTG
  }  // end of moveEndLimbTG() 



  // --------------- scaling using the radius and length values ----------

  /* Any methods using the cylinder's current radius or length must first apply 
     the current scaling values (stored in scaleLimb) to the cylinder's original
     radius and limbLen values. 

     No changes are every made to radius or limbLen, only to the scaling stored
     in scaleTG.
  */


  public float getRadius()
  {  return (radius * (float) scaleLimb.x);  }    // we could use scaleLimb.z instead

  public void setRadius(float newRadius)
  // change the cylinder's radius to newRadius (by changing the scaling)
  {
    double scaledRadius = ((double) radius) * scaleLimb.x;
    double radChange = ((double) newRadius) / scaledRadius;
    scaleRadius(radChange);
  }  // end of setRadius()


  public float getLength()
  {  return (limbLen * (float) scaleLimb.y);  }


  public void setLength(float newLimbLen)
  // change the cylinder's length to newLimbLen (by changing the scaling)
  { double scaledLimbLen = ((double) limbLen) * scaleLimb.y;
    double lenChange = ((double) newLimbLen) / scaledLimbLen;
    scaleLength( lenChange );
  }  // end of setLimbLen()


  // -------------- colour get / set methods ---------------------

  public Color3f getCurrColour()
  {  return currColour;  }


  public void setCurrColour(Color3f c)
  // Change the limb's colour to c.
  { currColour.x = c.x;
    currColour.y = c.y;
    currColour.z = c.z;
    limbMaterial.setDiffuseColor( currColour );
  }

  public void stepToBrown()
  // Incrementally change the limb's colour from green to brown
  {
    if (colourStep <= MAX_COLOUR_STEP) {
      currColour.x += redShift;
      currColour.y += greenShift;
      currColour.z += blueShift;
      limbMaterial.setDiffuseColor( currColour );
      colourStep++;
    }
  }  // end of stepToBrown


  
  // -------------------- parent and children methods ---------------

  public TreeLimb getParent()
  { return parent; }

  public int getNumChildren()
  {  return numChildren;  }

  public void addChildLimb(TreeLimb child)
  { limbChildren.add(child);
    numChildren++;
  }

  public ArrayList getLimbChildren()
  { return limbChildren; }


  // -------------------- leaves related methods -------------------


  public boolean hasLeaves()
  {  return hasLeaves;  }


  public void addLeaves(ImageCsSeries fls, ImageCsSeries bls)
  // Leaves are represented by two ImageCsSeries 'screens'
  {
    if (!hasLeaves) {
      frontLeafShape = fls;
      backLeafShape = bls;

      // add the screens to endLimbTG, via BranchGroups
      BranchGroup leafBG1 = new BranchGroup();
      leafBG1.addChild(frontLeafShape);
      endLimbTG.addChild(leafBG1);

      BranchGroup leafBG2 = new BranchGroup();
      leafBG2.addChild(backLeafShape);
      endLimbTG.addChild(leafBG2);

      hasLeaves = true;
    }
  } // end of addLeaves()


  public void showLeaf(int i)
  // show the i th leaf image
  { if (hasLeaves) {
      frontLeafShape.showImage(i);  
      backLeafShape.showImage(i);  
    }
  }

  public void showNextLeaf()
  // show the next leaf image
  { if (hasLeaves) {
      frontLeafShape.showNext(); 
      backLeafShape.showNext(); 
    } 
  }

  public void showPrevLeaf()
  // show the previous leaf image
  { if (hasLeaves) {
      frontLeafShape.showPrev();
      backLeafShape.showPrev();
    }  
  }

}  // end of TreeLimb class
