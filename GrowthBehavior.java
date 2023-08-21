
// GrowthBehavior.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* GrowthBehaviour is a timed-based Behavior which is triggered
   every TIME_DELAY milliseconds.

   It calls applyRulesToLimbs() which iterates through an 
   ArrayList of TreeLimb objects, applying a series of 'rules'
   to each one.

   The rules are encoded as if-tests in applyRules(). Each if-test
   states how a tree limb will change if the limb matches its 
   conditions
*/

//CLASS ADDED BY TONY AND JANI TO MAKE TREES WORK

import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.*;


public class GrowthBehavior extends Behavior
{
  private final static int TIME_DELAY = 1000;  //ms

  // axis constants
  private final static int X_AXIS = 0;
  private final static int Y_AXIS = 1;
  private final static int Z_AXIS = 2;


  private WakeupCondition timeOut;
  private ArrayList treeLimbs;           // of TreeLimb objects
  private ImageComponent2D[] leafIms;    // a sequence of leaf images



  public GrowthBehavior(ImageComponent2D[] lfIms)
  { 
    timeOut = new WakeupOnElapsedTime(TIME_DELAY);
    treeLimbs = new ArrayList();
    leafIms = lfIms;
  }


  public void addLimb(TreeLimb limb)
  {  treeLimbs.add(limb);  }


  public void initialize()
  { wakeupOn( timeOut );
  }

  public void processStimulus( Enumeration criteria )
  { // ignore criteria
    applyRulesToLimbs();
    wakeupOn( timeOut );
  } // end of processStimulus()


  private void applyRulesToLimbs()
  /* Apply the rules to each tree limb.
     The ArrayList may increase in size during the for-loop
     since a rule in applyRules() may trigger the creation of a
     new limb which must be stored in the list.
  */
  {
    TreeLimb limb;
    for(int i=0; i < treeLimbs.size(); i++) {
      limb = (TreeLimb) treeLimbs.get(i);
      applyRules(limb);
      limb.incrAge();   // a limb gets older after each iteration
    }
  }  // end of applyRulesToLimbs()


  private void applyRules(TreeLimb limb)
  // Apply rules to the tree limb.
  {
    // get longer
    if ((limb.getLength() < 1.0f) && !limb.hasLeaves())
      limb.scaleLength(1.1f);

    // get thicker
    if ((limb.getRadius() <= (-0.05f*limb.getLevel()+0.25f))&& !limb.hasLeaves())
      limb.scaleRadius(1.05f);

    // get more brown
    limb.stepToBrown();

    // spawn some child limbs
    int axis;
    if ((limb.getAge() == 5) && (treeLimbs.size() <= 256) && !limb.hasLeaves() &&   
        (limb.getLevel() < 10)) {
      axis = (Math.random() < 0.5) ? Z_AXIS : X_AXIS;
      if (Math.random() < 0.85)
        makeChild(axis, randomRange(10,30), 0.05f, 0.5f, limb);

      axis = (Math.random() < 0.5) ? Z_AXIS : X_AXIS;
      if (Math.random() < 0.85)
        makeChild(axis, randomRange(-30,-10), 0.05f, 0.5f, limb);
    }

    // start some leaves
    if ( (limb.getLevel() > 3) && (Math.random() < 0.08) && 
         (limb.getNumChildren() == 0) && !limb.hasLeaves() )
      makeLeaves(limb);

    // grow the leaves
    if (limb.getAge()%10 == 0)
      limb.showNextLeaf();

    // turn the base limb into a 'blue bucket'
    if ((limb.getAge() == 100) && (limb.getLevel() == 1)) {
      limb.setRadius( 2.0f*limb.getRadius());
      // limb.setLength( 2.0f*limb.getLength());
      limb.setCurrColour( new Color3f(0.0f, 0.0f, 1.0f));
    }

  }  // end of applyRules()



  private void makeChild(int axis, double angle, float rad, float len, TreeLimb par)
  { 
    TransformGroup startLimbTG = par.getEndLimbTG();
    TreeLimb child = new TreeLimb(axis, angle, rad, len, startLimbTG, par);
    treeLimbs.add(child);   // extend ArrayList
  } // end of makeChild()



  private void makeLeaves(TreeLimb limb)
  /* Leaves are represented by _two_ ImageCsSeries screens. One will rotate
     about a point in front of the leaves, the other about a point behind the
     leaves, which creates a convincing 'mass' of leaves. */
  {
    ImageCsSeries frontLeafShape = new ImageCsSeries(0.5f, 2.0f, leafIms);
    ImageCsSeries backLeafShape = new ImageCsSeries(-0.5f, 2.0f, leafIms);

    limb.addLeaves(frontLeafShape, backLeafShape);
  }  // end of makeLeaves()


  private double randomRange(double min, double max)
  // return a random number in the range min-max
  {  return (Math.random()*(max - min)) + min;  }

}  // end of GrowthBehavior class
