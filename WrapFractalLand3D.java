
// WrapFractalLand3D.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* A fractal landscape is generated, made out of a mesh
   of textured squares. Squares at different heights are
   textured in different ways.

   The landscape is surrounded by blue walls, and is poorly lit to
   suggest evening; the sky is a dark blue. LinearFog obscures
   the distance.

   The user can 'walk' over the landscape using the
   similar left/right/front/back/turn/up/down moves
   as in the FPShooter3D example.
*/


import javax.swing.*;
import java.awt.*;

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.image.TextureLoader;

import java.util.Random;



public class WrapFractalLand3D extends JPanel
/* Holds the 3D fractal landscape in a Swing container. */
{
  private final static int PWIDTH = 512;   // size of panel
  private final static int PHEIGHT = 512; 

  private static final int BOUNDSIZE = 200;  // larger than world

  private Color3f skyColour = new Color3f(0.17f, 0.07f, 0.45f);
     // used for the Background and LinearFog nodes

  private final static int X_AXIS = 0;
  private final static int Y_AXIS = 1;
  private final static int Z_AXIS = 2;

  private SimpleUniverse su;
  private BranchGroup sceneBG;
  private BoundingSphere bounds;   // for environment nodes

  private Landscape land;   // creates the floor and walls


  public WrapFractalLand3D(double flatness)
  {
    setLayout( new BorderLayout() );
    setOpaque( false );
    setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

    GraphicsConfiguration config =
					SimpleUniverse.getPreferredConfiguration();
    Canvas3D canvas3D = new Canvas3D(config);
    add("Center", canvas3D);
    canvas3D.setFocusable(true);   
    canvas3D.requestFocus();
    su = new SimpleUniverse(canvas3D);

    createSceneGraph(flatness);
    createUserControls();

    su.addBranchGraph( sceneBG );
  } // end of WrapFractalLand3D()


  void createSceneGraph(double flatness) 
  // initilise the scene
  { 
    sceneBG = new BranchGroup();
    bounds = new BoundingSphere(new Point3d(0,0,0), BOUNDSIZE);

    lightScene();     // add the lights
    addBackground();  // add the sky
//    addFog();         // add the fog; comment this line out to switch off fog
    

    // create the landscape: the floor and walls
    land = new Landscape(flatness);
    sceneBG.addChild( land.getLandBG() );   

    growTrees();
    otherTree();
    redFlower();
    sakura();
    trees();
    growBush();
    tonyAndBeifang();

    sceneBG.compile();   // fix the scene
  } // end of createScene()


  private void lightScene()
  // one directional light
  { Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    Vector3f lightDir = new Vector3f(1.0f, -1.0f, -0.8f); // upper left
    DirectionalLight light1 = 
            new DirectionalLight(white, lightDir);
    light1.setInfluencingBounds(bounds);
    sceneBG.addChild(light1);
  }  // end of lightScene()


  private void addBackground()
  // An early evening sky
  { Background back = new Background();
    back.setApplicationBounds( bounds );
    back.setColor( skyColour );   // darkish blue
    sceneBG.addChild( back );
  }  // end of addBackground()

  
  private void addFog()
  // linear fog
  { LinearFog fogLinear = new LinearFog( skyColour, 15.0f, 30.0f);
    fogLinear.setInfluencingBounds( bounds );  // same as background
    sceneBG.addChild( fogLinear );
  }  // end of addFog()


  // ---------------------- user controls --------------------------


  private void createUserControls()
  /* Adjust the clip distances and set up the KeyBehaviour.
     The behaviour initialises the viewpoint at the origin on
     the XZ plane.
  */
  {
    // original clips are 10 and 0.1; keep ratio between 100-1000
    View view = su.getViewer().getView();
    view.setBackClipDistance(20);      // can see a long way
    view.setFrontClipDistance(0.05);   // can see close things

    ViewingPlatform vp = su.getViewingPlatform();
    TransformGroup steerTG = vp.getViewPlatformTransform();

    // set up keyboard controls (and position viewpoint)
    KeyBehavior keybeh = new KeyBehavior(land, steerTG);
    keybeh.setSchedulingBounds(bounds);
    vp.setViewPlatformBehavior(keybeh);
  } // end of createUserControls()

  // ---------------------- grow trees -----------------
  //Created by Tony and Jani

  private void growTrees()
  /*
   * Position three trees and create the behaviour that controls their growth.
   */
  {
    // starting position for the first tree: (0,0,-5)
    /*
     * Transform3D t3d = new Transform3D(); t3d.set( new Vector3f(0,2.3f,-5));
     * TransformGroup tg0 = new TransformGroup(t3d); sceneBG.addChild(tg0); //
     * create the tree TreeLimb t0 = new TreeLimb(Z_AXIS, 0, 0.05f, 0.5f, tg0,
     * null);
     * 
     * // second tree t3d.set( new Vector3f(-5,0,5)); TransformGroup tg1 = new
     * TransformGroup(t3d); sceneBG.addChild(tg1); TreeLimb t1 = new
     * TreeLimb(Y_AXIS, 45, 0.05f, 0.5f, tg1, null);
     * 
     * // third tree t3d.set( new Vector3f(5,0,5)); TransformGroup tg2 = new
     * TransformGroup(t3d); sceneBG.addChild(tg2); TreeLimb t2 = new
     * TreeLimb(Y_AXIS, -60, 0.05f, 0.5f, tg2, null);
     * 
     * // fourth tree t3d.set( new Vector3f(-9,0,-9)); TransformGroup tg3 = new
     * TransformGroup(t3d); sceneBG.addChild(tg3); TreeLimb t3 = new
     * TreeLimb(Y_AXIS, 30, 0.05f, 0.5f, tg3, null);
     * 
     * // 5th tree t3d.set( new Vector3f(9,0,-9)); TransformGroup tg4 = new
     * TransformGroup(t3d); sceneBG.addChild(tg4); TreeLimb t4 = new
     * TreeLimb(Y_AXIS, -30, 0.05f, 0.5f, tg4, null);
     */

    for (int i = 0; i < 50; i++) {
      createTree();
    }

    // load the leaf images used by all the trees
    ImageComponent2D[] leafIms = loadImages("images/leaf", 6);

    // the behaviour that manages the growing of the trees
    GrowthBehavior grower = new GrowthBehavior(leafIms);
    grower.setSchedulingBounds(bounds);

    // add the trees to GrowthBehavior
    for (int i = 0; i < 30; i++) {
      grower.addLimb(createTree());
    }
    sceneBG.addChild(grower);
  } // end of growTrees()
  
  private void trees(){
      
      ImageComponent2D[] im = loadTrees("images/cactus");
      GrowthBehavior grower = new GrowthBehavior(im);
    grower.setSchedulingBounds(bounds);

    // add the trees to GrowthBehavior
    for (int i = 0; i < 10; i++) {
      grower.addLimb(createCactus());
    }
    sceneBG.addChild(grower);
      
      
  }
  
  private void sakura() {

    ImageComponent2D[] im = loadTrees("images/sakura");
    GrowthBehavior grower = new GrowthBehavior(im);
    grower.setSchedulingBounds(bounds);

    // add the trees to GrowthBehavior
    for (int i = 0; i < 40; i++) {
      grower.addLimb(createTree());
    }
    sceneBG.addChild(grower);

  }

  private void tonyAndBeifang() {

    ImageComponent2D[] im = loadTrees("images/tonyandbeifang");
    GrowthBehavior grower = new GrowthBehavior(im);
    grower.setSchedulingBounds(bounds);

    // add the trees to GrowthBehavior
    for (int i = 0; i < 1; i++) {
      grower.addLimb(createProf());
    }
    sceneBG.addChild(grower);

  }
  
  private void redFlower() {

    ImageComponent2D[] im = loadTrees("images/redflower");
    GrowthBehavior grower = new GrowthBehavior(im);
    grower.setSchedulingBounds(bounds);

    // add the trees to GrowthBehavior
    for (int i = 0; i < 35; i++) {
      grower.addLimb(createTree());
    }
    sceneBG.addChild(grower);

  }

  private void otherTree() {

    ImageComponent2D[] im = loadTrees("images/otherLeaf");
    GrowthBehavior grower = new GrowthBehavior(im);
    grower.setSchedulingBounds(bounds);

    // add the trees to GrowthBehavior
    for (int i = 0; i < 35; i++) {
      grower.addLimb(createTree());
    }
    sceneBG.addChild(grower);

  }

  private void growBush() {

    ImageComponent2D[] im = loadTrees("images/bush");
    GrowthBehavior grower = new GrowthBehavior(im);
    grower.setSchedulingBounds(bounds);

    // add the trees to GrowthBehavior
    for (int i = 0; i < 30; i++) {
      grower.addLimb(createBush());
    }
    sceneBG.addChild(grower);

  }


  
  private TreeLimb createTree() {
    Transform3D t3d = new Transform3D();
    Random rand = new Random();
    int r = rand.nextInt(30+30)-30;
    int r1 = rand.nextInt(30+30)-30;
    t3d.set(new Vector3f(r, 2f, r1));
    TransformGroup tg0 = new TransformGroup(t3d);
    sceneBG.addChild(tg0);
    TreeLimb t0 = new TreeLimb(Z_AXIS, 0, 0.05f, 0.5f, tg0, null);
    return t0;
  }

  private TreeLimb createCactus() {
    Transform3D t3d = new Transform3D();
    Random rand = new Random();
    int r = rand.nextInt(30 + 30) - 30;
    int r1 = rand.nextInt(30 + 30) - 30;
    t3d.set(new Vector3f(r, -2f, r1));
    TransformGroup tg0 = new TransformGroup(t3d);
    sceneBG.addChild(tg0);
    TreeLimb t0 = new TreeLimb(Z_AXIS, 0, 0.05f, 0.5f, tg0, null);
    return t0;
  }

  private TreeLimb createBush() {
    Transform3D t3d = new Transform3D();
    Random rand = new Random();
    int r = rand.nextInt(30 + 30) - 30;
    int r1 = rand.nextInt(30 + 30) - 30;
    t3d.set(new Vector3f(r, -2f, r1));
    TransformGroup tg0 = new TransformGroup(t3d);
    sceneBG.addChild(tg0);
    TreeLimb t0 = new TreeLimb(Z_AXIS, 0, 0.05f, 0.5f, tg0, null);
    return t0;
  }

  private TreeLimb createProf() {
    Transform3D t3d = new Transform3D();
    Random rand = new Random();
    int r = rand.nextInt(30 + 30) - 30;
    int r1 = rand.nextInt(30 + 30) - 30;
    t3d.set(new Vector3f(r, -2f, r1));
    TransformGroup tg0 = new TransformGroup(t3d);
    sceneBG.addChild(tg0);
    TreeLimb t0 = new TreeLimb(Z_AXIS, 0, 0.05f, 0.5f, tg0, null);
    return t0;
  }
  
  private ImageComponent2D[] loadTrees(String fNm){
      String filename;
      TextureLoader loader;
      ImageComponent2D[] im = new ImageComponent2D[1];
      filename = new String(fNm+".gif");
      loader = new TextureLoader(filename,null);
      im[0]=loader.getImage();
      if (im[0] == null){
        System.out.println("Load failed for texture in : " + filename);}
      im[0].setCapability(ImageComponent2D.ALLOW_SIZE_READ);
      return im;
  }

  private ImageComponent2D[] loadImages(String fNms, int numIms)
  /*
   * Load the leaf images: they all start with fNms, and there are numIms of them.
   */
  {
    String filename;
    TextureLoader loader;
    ImageComponent2D[] ims = new ImageComponent2D[numIms];
    System.out.println("Loading " + numIms + " textures from " + fNms);
    for (int i = 0; i < numIms; i++) {
      filename = new String(fNms + i + ".gif");
      loader = new TextureLoader(filename, null);
      ims[i] = loader.getImage();
      if (ims[i] == null)
        System.out.println("Load failed for texture in : " + filename);
      ims[i].setCapability(ImageComponent2D.ALLOW_SIZE_READ);
    }
    return ims;
  } // end of loadImages()



private ImageComponent2D[] forButterfly(String fNms, int numIms)
  /*
   * Load the leaf images: they all start with fNms, and there are numIms of them.
   */
  {
    String filename;
    TextureLoader loader;
    ImageComponent2D[] ims = new ImageComponent2D[numIms];
    System.out.println("Loading " + numIms + " textures from " + fNms);
    for (int i = 0; i < numIms-1; i++) {
      filename = new String(fNms + i + ".gif");
      loader = new TextureLoader(filename, null);
      ims[i] = loader.getImage();
      if (ims[i] == null)
        System.out.println("Load failed for texture in : " + filename);
      ims[i].setCapability(ImageComponent2D.ALLOW_SIZE_READ);
    }
    filename = new String("butterfly.gif");
    loader = new TextureLoader(filename, null);
    ims[numIms-1] = loader.getImage();
    if (ims[numIms-1] == null)
      System.out.println("Load failed for texture in : " + filename);
    ims[numIms-1].setCapability(ImageComponent2D.ALLOW_SIZE_READ);
    return ims;
  }
}
// end of WrapFractalLand3D class