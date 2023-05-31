package de.orat.math.cgadsleucliddemo;

import de.orat.math.cga.api.CGACircleIPNS;
import de.orat.math.cga.api.CGACircleOPNS;
import de.orat.math.cga.api.CGAEuclideanVector;
import de.orat.math.cga.api.CGAKVector;
import de.orat.math.cga.api.CGALineIPNS;
import de.orat.math.cga.api.CGALineOPNS;
import de.orat.math.cga.api.CGAMultivector;
import static de.orat.math.cga.api.CGAMultivector.I3;
import de.orat.math.cga.api.CGAOrientedPointIPNS;
import de.orat.math.cga.api.CGAOrientedPointOPNS;
import de.orat.math.cga.api.CGAPlaneIPNS;
import de.orat.math.cga.api.CGAPlaneOPNS;
import de.orat.math.cga.api.CGAPointPairIPNS;
import de.orat.math.cga.api.CGAPointPairOPNS;
import de.orat.math.cga.api.CGARoundPointIPNS;
import de.orat.math.cga.api.CGARoundPointOPNS;
import de.orat.math.cga.api.CGAScalarOPNS;
import de.orat.math.cga.api.CGASphereIPNS;
import de.orat.math.cga.api.CGASphereOPNS;
import de.orat.math.cga.api.iCGAFlat;
import de.orat.math.cga.api.iCGAPointPair;
import de.orat.math.cga.api.iCGATangentOrRound;
import de.orat.math.cga.api.iCGATangentOrRound.EuclideanParameters;
import de.orat.math.view.euclidview3d.GeometryView3d;
import de.orat.math.view.euclidview3d.ObjectLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Tuple3d;
import org.jogamp.vecmath.Vector3d;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.EuclidRobot;
import org.jzy3d.plot3d.primitives.RobotType;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 *
 * TODO
 * - Größe der Punkte etc. sinnvoll kontrollieren
 * - Visualisierung-Volumen an Punkten, Kugeln, Kreisen orientieren und start-endpunkte
 *   von Linien sowie betreffende Parameter von Ebenen entsprechend automatisch anpassen
 *
 * TODO
 * imaginäre circles, point-pairs, spheres: dashed
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class UR5eTest extends GeometryView3d {

    public static Color COLOR_GRADE_1 = Color.RED;    // ipns: sphere, planes, round points
    public static Color COLOR_GRADE_2 = Color.GREEN;  // ipns: lines, ipns circle, oriented points; opns: flat-points, point-pairs
    public static Color COLOR_GRADE_3 = Color.BLUE;   // ipns: point-pairs, (tangent vector), flat-points; opns:circle, line, oriented-points
    public static Color COLOR_GRADE_4 = Color.YELLOW; // (ipns euclidean vector), opns: plane, round-point, spheres

    //TODO
    // nur als Faktoren verwenden und skalieren auf Basis des angezeigten Volumens
    public static float POINT_RADIUS = 0.01f; // in m
    public static float LINE_RADIUS = 0.005f; // in m
    public static float TANGENT_LENGTH = 0.1f*3f; // testweise *3 damit trotz Roboter sichtbar

    public static void main(String[] args) throws Exception {
        
        //System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        //GeometryViewCGA gv = new GeometryViewCGA();
        UR5eTest gv = new UR5eTest();
        AnalysisLauncher.open(gv);

        //Robots have to be rotated after initialisation.
        rotateRobotsCoordsystem();
        setRobotsDH();

        EuclidRobot robot = robotList.get(0); // letzter Eintrag aus der Tabelle
        // das ist die Reihenfolge von Base zu TCP, die so bestimmte TCP-Ausrichtung
        // in der Visualisierung ist gleich der Berechnung in der Excel-Tabelle von UR
        robot.setTheta(1, -42.29444839527154F,true);
        robot.setTheta(2, -83.92752302017205F,true);
        robot.setTheta(3, -95.53404922699703F,true);
        robot.setTheta(4, -90.56670900909727F,true);
        robot.setTheta(5, 90.21354316792402F,true);
        robot.setTheta(6, -132.37747769068068F,true);
        
        robot.getCoordCenters();

        gv.addGeometricObjects();
        gv.updateChessFloor(true, CHESS_FLOOR_WIDTH);
    }

    void addGeometricObjectsFromCSVFile(String fileName){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            String input;
            while((input=reader.readLine())!=null){
                String[] inputArray = input.trim().split(",");
                String multivectorType = inputArray[0];
                String multivectorName = inputArray[1];

                double[] multivectorComponents = new double[32];
                for(int i = 2; i <= 33; i++){
                    // multivectorComponents[i-2] = Double.valueOf(inputArray[i]);
                    multivectorComponents[i-2] = Double.parseDouble(inputArray[i]);
                }

                switch (multivectorType) {
                    case "point" -> {
                        CGARoundPointIPNS point = new CGARoundPointIPNS(CGAMultivector.fromGaalop(multivectorComponents));
                        addCGAObject(point, multivectorName);
                    }
                    case "sphereIPNS" -> {
                        CGASphereIPNS sphereIPNS = new CGASphereIPNS(CGAMultivector.fromGaalop(multivectorComponents));
                        addCGAObject(sphereIPNS, multivectorName);
                    }
                    case "sphereOPNS" -> {
                        CGASphereOPNS sphereOPNS = new CGASphereOPNS(CGAMultivector.fromGaalop(multivectorComponents));
                        addCGAObject(sphereOPNS, multivectorName);
                    }
                    case "circleIPNS" -> {
                        CGACircleIPNS circleIPNS = new CGACircleIPNS(CGAMultivector.fromGaalop(multivectorComponents));
                        addCGAObject(circleIPNS, multivectorName);
                    }
                    case "ppIPNS" -> {
                        CGAPointPairIPNS pointPairIPNS = new CGAPointPairIPNS(CGAMultivector.fromGaalop(multivectorComponents));
                        addCGAObject(pointPairIPNS, multivectorName);
                    }
                    case "ppOPNS" -> {
                        CGAPointPairOPNS pointPairIPNS = new CGAPointPairOPNS(CGAMultivector.fromGaalop(multivectorComponents));
                        addCGAObject(pointPairIPNS, multivectorName);
                    }
                    case "planeIPNS" -> {
                        CGAPlaneIPNS planeIPNS = new CGAPlaneIPNS(CGAMultivector.fromGaalop(multivectorComponents));
                        System.out.println("ipns-plane: "+planeIPNS.toString(multivectorName));
                        addCGAObject(planeIPNS, multivectorName);
                    }
                    case "planeOPNS" -> {
                        CGAPlaneOPNS planeOPNS = new CGAPlaneOPNS(CGAMultivector.fromGaalop(multivectorComponents));
                        System.out.println("opns-plane: "+planeOPNS.toString(multivectorName));
                        addCGAObject(planeOPNS, multivectorName);
                    }
                    case "lineIPNS" -> {
                        CGALineIPNS lineIPNS = new CGALineIPNS(CGAMultivector.fromGaalop(multivectorComponents));
                        System.out.println("ipns-line: "+lineIPNS.toString(multivectorName));
                        addCGAObject(lineIPNS, multivectorName);
                    }
                    case "lineOPNS" -> {
                        CGALineOPNS lineOPNS = new CGALineOPNS(CGAMultivector.fromGaalop(multivectorComponents));
                        System.out.println("opns-line: "+lineOPNS.toString(multivectorName));
                        addCGAObject(lineOPNS, multivectorName);
                    }
                    default -> {
                    }
                }
            }
        } catch (IOException e){
        
        }
    }
    
    void addGeometricObjects()  {

        //addGeometricObjectsFromCSVFile("data/multivectors.csv");

        //Vector3d dir = new Vector3d(1,1,0);
        //dir.scale(200);
        //addArrow(new Point3d(200,0,-50), dir , 
        //                 LINE_RADIUS*2*1000, Color.BLUE, "test-array");
        
        // test points visualisation
        //Point3d center = new Point3d(1,2,3);
        //CGARoundPointIPNS p = new CGARoundPointIPNS(center);
        //gv.addCGAObject(p, "p");

        // TCP
        // point in ipns representation, gaalop ordered values, Punkt Endeffektor
        /*double[] values = new double[]{0, 0.24011911, -0.39999987, -0.49, 0.22887854, 1,
                                       0,0,0,0,0,0,
                                       0,0,0,0,0,0,
                                       0,0,0,0,0,0,
                                       0,0,0,0,0,0,
                                       0,0};
        CGARoundPointIPNS tcp = new CGARoundPointIPNS(CGAMultivector.fromGaalop(values));
        System.out.println(tcp.toString("tcp"));
        addCGAObject(tcp, "tcp");

        // Punkt P5 (letztes Gelenk)
        values = new double[]{
                    0,          0.24011911, -0.39999987, -0.3904,      0.18503462,  1,
                    0,          0,          0,          0,          0,          0,
                    0,          0,          0,          0,          0,          0,
                    0,          0,          0,          0,          0,          0,
                    0,          0,          0,          0,          0,          0,
                    0,          0
        };
        CGARoundPointIPNS p5 = new CGARoundPointIPNS(CGAMultivector.fromGaalop(values));
        System.out.println(p5.toString("p5"));
        addCGAObject(p5, "p5");


        // spheres

        // test sphere um P5 visualisation
        values = new double[]{0, 0.24011911, -0.39999987, -0.3904, 0.17615018,  1,
                                0,         0,         0,         0,         0,         0,
                                0,         0,         0,         0,         0,         0,
                                0,         0,         0,         0,         0,         0,
                                0,         0,         0 ,        0,         0,         0,
                                0,         0,       };
        CGASphereIPNS sp5 = new CGASphereIPNS(CGAMultivector.fromGaalop(values));
        System.out.println(sp5.toString("sp5"));
        //addCGAObject(sp5, "sp5");


        // Kugel um den Ursprung
        /*values = new double[]{0, 0, 0, 0, -0.17615018,  1,
                                0,         0,         0,         0,         0,         0,
                                0,         0,         0,         0,         0,         0,
                                0,         0,         0,         0,         0,         0,
                                0,         0,         0 ,        0,         0,         0,
                                0,         0,       };
        CGASphereIPNS so = new CGASphereIPNS(CGAMultivector.fromGaalop(values));
        System.out.println(so.toString("so"));
        addCGAObject(so, "so");
        */

        // circle visualisation
                // FIXME scheint gekippt im Raum zu stehen
        /*values = new double[]{0,          0,         0,       0,         0,         0,
                    0,          0,         -0.04229702,  0.24011911,  0, 0.07046005,
                   -0.39999987,  0.06876903, -0.3904,     0.35230035,  0,          0,
                    0,          0,          0,          0,          0,          0,
                    0,          0,          0,          0,          0,          0,
                    0,          0       };
        CGACircleIPNS c = new CGACircleIPNS(CGAMultivector.fromGaalop(values));
        System.out.println(c.toString("c"));
        addCGAObject(c, "c");

        // testweise circle parameter ausgeben
        EuclideanParameters parameters = c.decompose();
        System.out.println("radius*radius= "+String.valueOf(parameters.squaredSize()));
        System.out.println("radius= "+String.valueOf(Math.sqrt(Math.abs(parameters.squaredSize()))));
        Vector3d n = parameters.attitude();
        System.out.println("normal= "+String.valueOf(n.x)+", "+String.valueOf(n.y)+", "+String.valueOf(n.z));
        Point3d loc = parameters.location();
        System.out.println("location= "+String.valueOf(loc.x)+", "+String.valueOf(loc.y)+", "+String.valueOf(loc.z));

        */
        
        // circle funktioniert test in x,y und z-Richtung den Normalenvektor des Kreises
        // auch der radius verändert sich nicht wenn der kreis gekippt wird
        /*Vector3d normal = new Vector3d(0,0,1);
        float radius = 0.3f;
        Point3d loc = new Point3d(0.1,-0.2, 0.1);
        CGACircleIPNS cc = new CGACircleIPNS(loc, normal, radius);
        //addCGAObject(cc,"cc-test");
        Vector3d normal2 = new Vector3d(1,0,1);
        CGACircleIPNS cc2 = new CGACircleIPNS(loc, normal2, radius);*/
        //addCGAObject(cc2,"cc-test2");
        
        // circle ok
        //loc.scale(1000); radius *=1000;
        //addCircle(loc, normal, radius, Color.BLUE, "test circle", false);

      
        // opns circle auf Basis von 2 opns Kugeln ist korrekt inclusive dem radius
        /*CGASphereOPNS s1 = new CGASphereOPNS(new Point3d(0.1,0,0), 0.2);
        System.out.println(s1.toString("s1"));
        CGASphereOPNS s2 = new CGASphereOPNS(new Point3d(-0.1,0,0), 0.2);
        System.out.println(s2.toString("s2"));
        CGACircleOPNS c12 = new CGACircleOPNS(s1.vee(s2));
        System.out.println(c12.toString("c12"));
        System.out.println("radius(c12)="+String.valueOf(Math.sqrt(Math.abs(c12.squaredSize()))));*/
        //addCGAObject(c12,"c12");
        
        // ipns circle auf Basis von 2 ipns Kugeln ist korrekt inclusive dem radius
        /*CGASphereIPNS s1_ = new CGASphereIPNS(new Point3d(0.1,0,0), 0.2);
        System.out.println(s1_.toString("s1_"));
        CGASphereIPNS s2_ = new CGASphereIPNS(new Point3d(-0.1,0,0), 0.2);
        System.out.println(s2_.toString("s2_"));
        CGACircleIPNS c12_ = new CGACircleIPNS(s1_.op(s2_));
        System.out.println(c12_.toString("c12_"));
        System.out.println("radius(c12_)="+String.valueOf(Math.sqrt(Math.abs(c12_.squaredSize()))));*/
        //addCGAObject(c12_,"c12_");
        
        //CGAPlaneOPNS pl_opns = new CGAPlaneOPNS(new Point3d(0,0,0), new Vector3d(0,0,1));
        //System.out.println(pl_opns.toString("pl_opns"));
        //addCGAObject(pl_opns,"pl_opns");
        
        //pp_ = (0)
        //CGAPointPairIPNS pp_ = new CGAPointPairIPNS(c12_.rc(pl_opns).dual());
        //CGAPointPairIPNS pp_ = new CGAPointPairIPNS(c12_.lc(pl_opns).dual());
        //System.out.println(pp_.toString("pp_"));
        //addCGAObject(pp_,"pp_");
        
        
        // circle
        /*Point3d c = new Point3d(0,0,0);
        Vector3d n = new Vector3d(0,1,1);
        double r = 0.2;
        CGACircleIPNS c12_1 = new CGACircleIPNS(c, n, r);
        addCGAObject(c12_1,"c12_1");
        CGAPointPairIPNS pp_1 = new CGAPointPairIPNS(c12_1.lc(pl_opns).dual());*/
        //addCGAObject(pp_1,"pp_1");
        
        // der grosse circle wird reproduziert, aber das Punkte-paar liegt da auch nicht drauf
        // der geladene circle hat die gleichen n,r,c aber wird kleiner dargestellt
        //FIXME
        // Add circle "C5k" at (232.57132751836434mm,-387.4264766874263mm, 571.066812241607mm) with radius 131.18823008190301"[mm] and n= (0.3193691047523477,-0.5320176323422736, 0.7841942449461139) 
        //Point3d c = new Point3d(0,0,0);//new Point3d(232.57132751836434/1000, -387.4264766874263/1000, 571.066812241607/1000);
        /*double r = 131.18823008190301/1000;
        Vector3d n = new Vector3d(0.3193691047523477,-0.5320176323422736, 0.7841942449461139);
        n.normalize(); // hat nichts gebracht
        CGACircleIPNS c5kt = new CGACircleIPNS(c, n, r);
        addCGAObject(c5kt,"c5kt");*/
        
        // dieser Kreis wird kleiner dargestellt
        /*n.negate();
        CGACircleIPNS c5ktt = new CGACircleIPNS(c, n, r);
        addCGAObject(c5ktt,"c5ktt");*/
        
        // point-pair

        // funktioniert, also auch der bestimmte Radius stimmt
        /*Point3d pp1 = new Point3d(0.1,0.2,0.2);
        Point3d pp2 = new Point3d(0.1,0.2,-0.2);
        //CGAPointPairIPNS pp = (new CGAPointPairOPNS(pp1,pp2)).dual();
        CGAPointPairOPNS pp = new CGAPointPairOPNS(pp1,pp2);
        addCGAObject(pp,"pp-ipns");*/
        

        // Points

        // Punkt P_c:
        /*double[] values = new double[]{ 0.00000000e+00, -3.30040856e-01,  3.01597789e-01,  3.90400000e-01,
            -1.76150176e-01, -1.00000000e+00,  0.00000000e+00,  0.00000000e+00,
             0.00000000e+00,  0.00000000e+00,  0.00000000e+00,  0.00000000e+00,
             0.00000000e+00,  0.00000000e+00,  0.00000000e+00,  0.00000000e+00,
             0.00000000e+00,  2.77555756e-17,  0.00000000e+00,  0.00000000e+00,
             0.00000000e+00,  0.00000000e+00,  0.00000000e+00,  0.00000000e+00,
             0.00000000e+00,  0.00000000e+00,  0.00000000e+00,  0.00000000e+00,
             0.00000000e+00,  0.00000000e+00,  0.00000000e+00,  0.00000000e+00};
        CGARoundPointIPNS P_c = new CGARoundPointIPNS(CGAMultivector.fromGaalop(values));
        System.out.println(P_c.toString("P_c"));
        addCGAObject(P_c, "P_c");*/


        // plane

        //Ebene PI_c:
        /*values = new double[]{0,         -0.30159779, -0.33004086,  0,          0,          0,
                                0,          0,          0,          0,          0,          0,
                                0,          0,          0,          0,          0,          0,
                                0,          0,          0,          0,          0,          0,
                                0,          0,          0,          0,          0,          0,
                                0,          0 };
        CGAPlaneIPNS PI_c = new CGAPlaneIPNS(CGAMultivector.fromGaalop(values));
        System.out.println(PI_c.toString("PI_c"));
        addCGAObject(PI_c, "PI_c");*/

        // test Ebene selbst erzeugt
        // yz-Ebene
        // funktioniert
        //CGAPlaneIPNS pl = new CGAPlaneIPNS(new Vector3d(1,0,0),0d);
        //System.out.println(pl.toString("pl"));
        //addCGAObject(pl, "pl");

        // test opns plane
        /*CGARoundPointIPNS p1 = new CGARoundPointIPNS(new Point3d(0,0,0));
        CGARoundPointIPNS p2 = new CGARoundPointIPNS(new Point3d(0,0,10));
        CGARoundPointIPNS p3 = new CGARoundPointIPNS(new Point3d(5,10,10));
        CGAPlaneOPNS pl = new CGAPlaneOPNS(p1,p2,p3);
        System.out.println(pl.toString("pl"));
        addCGAObject(pl, "pl");*/

                // funktioniert
                //boolean res = addPlaneDeprecated(new Point3d(0,0,0), new Vector3d(0,0,500), new Vector3d(0,500,500),
                //                  Color.BLUE, "plane");

                //boolean res = addPlaneDeprecated(new Point3d(0,0,-300), new Vector3d(0,0,1), Color.BLUE, "test_plane");

                // test line
                //addLine(new Vector3d(0d,0d,-1d), new Point3d(3d,0d,3d), Color.CYAN, 0.2f, 10, "ClipLinie");

        /*Point3d start = new Point3d(300,-300,-300);
        Vector3d att = new Vector3d(100,100,100);
        CGALineIPNS line = new CGALineIPNS(start, att);
        String label = "line";
        boolean result = addCGAObject(line, label);
        Point3d start2 = new Point3d(300,100,100);
        CGALineIPNS line2 = new CGALineIPNS(start2, att);
        String label2 = "line2";
        result = addCGAObject(line2, label2);*/

        // funktioniert
        //CGACircleIPNS _c = new CGACircleIPNS(CGAMultivector.fromGaalop(testCreateGaalopExampleCircle()));
        //System.out.println(_c.toString("_c"));
        //addCGAObject(_c, "_c");
        
        
        Point3d p = new Point3d(0.2401191099971,-0.399999869993223,0.489999999997885);
        Vector3d ae = new Vector3d(0d,0d,-1d);
        Vector3d se = new Vector3d(0d,1d,0d);
        boolean k_ud = true; // elbow down
        boolean k_fn = true; // wrist flip
        boolean k_lr = true; // wrist left
        double[][] result = de.orat.math.cgadsleucliddemo.gen.WrapperGen.INSTANCE.ik(p, ae, se, k_ud, k_fn, k_lr);
        
        CGARoundPointIPNS Pe = new CGARoundPointIPNS(result[0]);
        System.out.println(Pe.toString("Pe"));
        addCGAObject(Pe, "Pe");

        CGARoundPointIPNS P_5 = new CGARoundPointIPNS(result[1]);
        System.out.println(P_5.toString("P_5"));
        addCGAObject(P_5, "P_5");
        
        CGASphereIPNS Sc = new CGASphereIPNS(result[2]);
        // Kugel um P5
        //addCGAObject(Sc, "Sc");
        
        CGASphereIPNS K0 = new CGASphereIPNS(result[3]);
        // Kugel um den Ursprung
        //addCGAObject(K0, "K0");
        
        CGACircleIPNS C5k = new CGACircleIPNS(result[4]);
        //addCGAObject(C5k, "C5k");
        //System.out.println("r(C5k)="+String.valueOf(Math.sqrt(C5k.squaredSize())*1000));
        
        CGAPlaneOPNS Pl = new CGAPlaneOPNS(result[5]); // opns-plane ist grade 4
        System.out.println(Pl.toString("Pl"));
        //addCGAObject(Pl, "Pl");
        
        CGAPointPairOPNS Qc = new CGAPointPairOPNS(result[6]);
        //System.out.println(Qc.toString("Qc"));
        //System.out.println("squaredWeight(Qc)="+String.valueOf(Math.sqrt(Qc.squaredWeight())));
        //System.out.println("r(Qc)="+String.valueOf(Math.sqrt(Qc.squaredSize())*1000));
        //addCGAObject(Qc,"Qc");
        
        // ok
        CGARoundPointIPNS P_c = new CGARoundPointIPNS(result[7]);
        System.out.println(P_c.toString("P_c"));
        addCGAObject(P_c,"P_c");
        
        CGAEuclideanVector P_ce = new CGAEuclideanVector(result[8]);
        System.out.println(P_ce.toString("P_ce"));
        
        CGAPlaneOPNS Pi_c = new CGAPlaneOPNS(result[9]); 
        System.out.println(Pi_c.toString("Pi_c"));
        addCGAObject(Pi_c,"Pi_c");
        
        //CGAPlaneIPNS PIcp = new CGAPlaneIPNS(result[10]); 
        //System.out.println(Pi_c.toString("PIcp"));
        //addCGAObject(PIcp,"PIcp");
        
        CGAPlaneOPNS Pi_56_o = new CGAPlaneOPNS(result[11]); 
        System.out.println(Pi_56_o.toString("Pi_56_o"));
        //addCGAObject(Pi_56_o,"Pi_56_o");
        
        //CGAMultivector n56 = new CGAMultivector(result[12]); 
        //System.out.println(n56.toString("n56"));
        
        CGAPlaneIPNS Pi_c_o = new CGAPlaneIPNS(result[13]); 
        System.out.println(Pi_c.toString("Pi_c_o"));
        //addCGAObject(Pi_c_o,"Pi_c_o");
        
        CGALineIPNS L_45 = new CGALineIPNS(result[14]); 
        System.out.println(Pi_c.toString("L_45"));
        //addCGAObject(L_45,"L_45");
        
        //CGASphereIPNS S_5 = new CGASphereIPNS(result[15]); 
        //System.out.println(S_5.toString("S_5"));
        //addCGAObject(S_5,"S_5");
        
        CGAPointPairOPNS Q_4 = new CGAPointPairOPNS(result[16]); 
        System.out.println(Q_4.toString("Q_4")); // grade-2 also opns
        //addCGAObject(Q_4,"Q_4");
        
        CGARoundPointIPNS P_4 = new CGARoundPointIPNS(result[17]);
        System.out.println(P_4.toString("P_4"));
        addCGAObject(P_4,"P_4",null);
        
        //S_4  := P_4 + (0.5d_4²εᵢ) // ipns 18
        CGASphereIPNS S_4 = new CGASphereIPNS(result[18]);
        System.out.println(S_4.toString("S_4")); 
        //addCGAObject(S_4, "S_4");
        
        //L_34 := P_4∧Pi_c*∧εᵢ // opns ok
        CGALineOPNS L_34 = new CGALineOPNS(result[19]); 
        System.out.println(L_34.toString("L_34"));
        addCGAObject(L_34,"L_34",null);
        
        // Q_3  := L_34⌊S_4 // opns
        CGAPointPairOPNS Q_3 = new CGAPointPairOPNS(result[20]); 
        System.out.println(Q_3.toString("Q_3")); // grade-2 also opns
        addCGAObject(Q_3,"Q_3");
        
        //CGAScalarOPNS s = new CGAScalarOPNS(Q_3.sqr());
        //System.out.println(s.toString("s"));
        
        // testweise P_3 direkt bestimmen
        // P_3 := (Q_3+sqrt(Q_3²))/(-εᵢ⌋Q_3) 
        // geht so nicht: verwendet norm()
        //CGAMultivector P_3_test = Q_3.add(
        //        new CGAScalarOPNS(Math.sqrt(Math.abs(new CGAScalarOPNS(Q_3.sqr()).decomposeScalar())))).
        //        div(inf.negate().lc(Q_3));
        //System.out.println(P_3_test.toString("P_3_test"));
         
        //PointPair pp = Q_3.decomposePoints();
        //Point3d p1 = pp.p1();
        //Point3d p2 = pp.p2();
        //System.out.println("P_3_test: "+toString("p1", p1)+", "+toString("p2", p2));
        
        // ok
        //pp = Q_3.decomposePoints2();
        //p1 = pp.p1();
        //p2 = pp.p2();
        //System.out.println("P_3_test2: "+toString("p1", p1)+", "+toString("p2", p2));
       
        //P_3 := (Q_3-norm(Q_3))/(-εᵢ⌋Q_3) // ipns
        CGARoundPointIPNS P_3 = new CGARoundPointIPNS(result[21]);
        System.out.println(P_3.toString("P_3")); //FIXME kaputt
        addCGAObject(P_3,"P_3");
        
        // P_1 := d_1ε₃+0.5d_1²εᵢ+ε₀ //P_1 := createPoint(0, 0, d1)
        CGARoundPointIPNS P_1 = new CGARoundPointIPNS(result[22]); // ok
        System.out.println(P_1.toString("P_1")); 
        //addCGAObject(P_1,"P_1");

        // S_1 := P_1-0.5a_2²εᵢ
        CGASphereIPNS S_1 = new CGASphereIPNS(result[23]);
        System.out.println(S_1.toString("S_1")); 
        //addCGAObject(S_1, "S_1");
        
        //S_3
        CGASphereIPNS S_3 = new CGASphereIPNS(result[24]);
        System.out.println(S_3.toString("S_3")); 
        //addCGAObject(S_3, "S_3");
        
        //C_2
        CGACircleIPNS C_2 = new CGACircleIPNS(result[25]);
        System.out.println(C_2.toString("C_2")); 
        addCGAObject(C_2, "C_2");
        
        // Q_2
        CGAPointPairOPNS Q_2 = new CGAPointPairOPNS(result[26]); 
        System.out.println(Q_2.toString("Q_2")); 
        addCGAObject(Q_2,"Q_2");
        
        //P_2
        CGARoundPointIPNS P_2 = new CGARoundPointIPNS(result[27]);
        System.out.println(P_2.toString("P_2")); 
        //addCGAObject(P_2,"P_2");
        
        
        // L_01
        CGALineOPNS L_01 = new CGALineOPNS(result[28]); 
        System.out.println(L_34.toString("L_01"));
        addCGAObject(L_01,"L_01");
        
        // L_12
        CGALineOPNS L_12 = new CGALineOPNS(result[29]); 
        System.out.println(L_12.toString("L_12"));
        addCGAObject(L_12,"L_12");
        
        // L_23
        CGALineOPNS L_23 = new CGALineOPNS(result[30]); 
        System.out.println(L_23.toString("L_23"));
        addCGAObject(L_23,"L_23");
        
        
        // 1. joint (base)

        // a_1 ok
        CGAEuclideanVector a_1 = new CGAEuclideanVector(result[31]);
        System.out.println(a_1.toString("a_1")); 
        CGAOrientedPointIPNS a_1_op = new CGAOrientedPointIPNS(P_1.location(), a_1.direction());
        addCGAObject(a_1_op,"a1-op");
        
        // b_1 ok
        CGAEuclideanVector b_1 = new CGAEuclideanVector(result[32]);
        System.out.println(b_1.toString("b_1"));
        CGAOrientedPointIPNS b_1_op = new CGAOrientedPointIPNS(P_1.location(), b_1.direction());
        addCGAObject(b_1_op,"b1-op", Color.ORANGE);
        
        // N_1 
        //CGAMultivector N_1 = new CGAMultivector(result[33]);
        CGAEuclideanVector N_1 = new CGAEuclideanVector(new CGAMultivector(result[33]).normalize().div(I3)); // normalized and euclidean-Dual
        System.out.println(N_1.toString("N_1"));
        CGAOrientedPointIPNS N_1_op = new CGAOrientedPointIPNS(P_1.location(), N_1.direction());
        addCGAObject(N_1_op,"N1-op", Color.CYAN);
        
        
        // 2. joint (shoulder)
        
        // a_2 um 90Grad falsch?
        CGAEuclideanVector a_2 = new CGAEuclideanVector(result[34]);
        System.out.println(a_2.toString("a_2")); 
        CGAOrientedPointIPNS a_2_op = new CGAOrientedPointIPNS(P_2.location(), a_2.direction());
        addCGAObject(a_2_op,"a2-op");
        
        // b_2 vermutlich ok
        CGAEuclideanVector b_2 = new CGAEuclideanVector(result[35]);
        // b_2 = (-0.31373406050916575*e1 + 0.2866963201085384*e2 + 0.0010767138214293195*e3)
        System.out.println(b_2.toString("b_2"));
        CGAOrientedPointIPNS b_2_op = new CGAOrientedPointIPNS(P_2.location(), b_2.direction());
        addCGAObject(b_2_op,"b2-op", Color.ORANGE);
        
        // N_2 vermutlich ok
        //CGAMultivector N_2 = new CGAMultivector(result[36]);
        CGAEuclideanVector N_2 = new CGAEuclideanVector(new CGAMultivector(result[36]).normalize().div(I3)); // normalized and euclidean-Dual
        System.out.println(N_2.toString("N_2"));
        CGAOrientedPointIPNS N_2_op = new CGAOrientedPointIPNS(P_2.location(), N_2.direction());
        addCGAObject(N_2_op,"N2-op", Color.CYAN);
        
        // 3. joint (elbow)
        
        // a_3 ok
        CGAEuclideanVector a_3 = new CGAEuclideanVector(result[37]);
        System.out.println(a_3.toString("a_3"));
        CGAOrientedPointIPNS a_3_op = new CGAOrientedPointIPNS(P_3.location(), a_3.direction());
        addCGAObject(a_3_op,"a3-op");
        
        // b_3 ok
        CGAEuclideanVector b_3 = new CGAEuclideanVector(result[38]);
        System.out.println(b_3.toString("b_3"));
        CGAOrientedPointIPNS b_3_op = new CGAOrientedPointIPNS(P_3.location(), b_3.direction());
        addCGAObject(b_3_op,"b3-op", Color.ORANGE);
        
        // N_3 vermutlich ok
        //CGAMultivector N_3 = new CGAMultivector(result[39]);
        //System.out.println(N_3.toString("N_3"));
        CGAEuclideanVector N_3 = new CGAEuclideanVector(new CGAMultivector(result[39]).normalize().div(I3)); // normalized and euclidean-Dual
        System.out.println(N_3.toString("N_3"));
        CGAOrientedPointIPNS N_3_op = new CGAOrientedPointIPNS(P_3.location(), N_3.direction());
        addCGAObject(N_3_op,"N3-op", Color.CYAN);
        
        
        // 4. joint (wrist-1)
        
        // a_4 um 90 Grad falsch, oder B_4 um 90Grad falsch
        CGAEuclideanVector a_4 = new CGAEuclideanVector(result[40]);
        System.out.println(a_4.toString("a_4"));
        CGAOrientedPointIPNS a_4_op = new CGAOrientedPointIPNS(P_4.location(), a_4.direction());
        addCGAObject(a_4_op,"a4-op");
        
        // b_4
        //FIXME
        // decompose location schlägt fehl
        //CGAEuclideanVector b_4 = new CGAEuclideanVector(result[41]);
        //System.out.println(b_4.toString("b_4"));
        //CGAOrientedPointIPNS b_4_op = new CGAOrientedPointIPNS(P_4.location(), b_4.direction());
        //addCGAObject(b_4_op,"b4-op", Color.ORANGE);
        
        // N_4 
        //CGAMultivector N_4 = new CGAMultivector(result[42]);
        //System.out.println(N_4.toString("N_4"));
        CGAEuclideanVector N_4 = new CGAEuclideanVector(new CGAMultivector(result[42]).normalize().div(I3)); // normalized and euclidean-Dual
        System.out.println(N_4.toString("N_4"));
        CGAOrientedPointIPNS N_4_op = new CGAOrientedPointIPNS(P_4.location(), N_4.direction());
        addCGAObject(N_4_op,"N4-op", Color.CYAN);
         
        // 5. joint (wrist-2)
        
        // a_5
        CGAEuclideanVector a_5 = new CGAEuclideanVector(result[43]);
        System.out.println(a_5.toString("a_5"));
        CGAOrientedPointIPNS a_5_op = new CGAOrientedPointIPNS(P_5.location(), a_5.direction());
        addCGAObject(a_5_op,"a5-op");
        
        // b_5
        CGAEuclideanVector b_5 = new CGAEuclideanVector(result[44]);
        System.out.println(b_5.toString("b_5"));
        CGAOrientedPointIPNS b_5_op = new CGAOrientedPointIPNS(P_5.location(), b_5.direction());
        addCGAObject(b_5_op,"b5-op", Color.ORANGE);
        
        // N_5
        //CGAMultivector N_5 = new CGAMultivector(result[45]);
        //System.out.println(N_5.toString("N_5"));
        CGAEuclideanVector N_5 = new CGAEuclideanVector(new CGAMultivector(result[45]).normalize().div(I3)); // normalized and euclidean-Dual
        System.out.println(N_5.toString("N_5"));
        CGAOrientedPointIPNS N_5_op = new CGAOrientedPointIPNS(P_5.location(), N_5.direction());
        addCGAObject(N_5_op,"N5-op", Color.CYAN);
        
        // 6. joint (TCP)
        
        // a_6
        CGAEuclideanVector a_6 = new CGAEuclideanVector(result[46]);
        System.out.println(a_6.toString("a_5"));
        CGAOrientedPointIPNS a_6_op = new CGAOrientedPointIPNS(Pe.location(), a_6.direction());
        //addCGAObject(a_6_op,"a6-op");
        
        // b_6
        CGAEuclideanVector b_6 = new CGAEuclideanVector(result[47]);
        System.out.println(b_6.toString("b_6"));
        CGAOrientedPointIPNS b_6_op = new CGAOrientedPointIPNS(Pe.location(), b_6.direction());
        addCGAObject(b_6_op,"b6-op", Color.ORANGE);
        
        // N_6
        CGAEuclideanVector N_6 = new CGAEuclideanVector(new CGAMultivector(result[48]).normalize().div(I3)); // normalized and euclidean-Dual
        System.out.println(N_6.toString("N_6"));
        CGAOrientedPointIPNS N_6_op = new CGAOrientedPointIPNS(Pe.location(), N_6.direction());
        addCGAObject(N_6_op,"N6-op", Color.CYAN);
        
        
        // Angles
       
         
        CGAScalarOPNS theta_1 = new CGAScalarOPNS(result[49]);
        System.out.println(theta_1.toString("theta_1 (-42.29444839527154)"));
        
        CGAScalarOPNS theta_2 = new CGAScalarOPNS(result[50]);
        // theta_2 = (101.89769531137071)
        System.out.println(theta_2.toString("theta_2 (-83.92752302017205)"));
        
        CGAScalarOPNS theta_3 = new CGAScalarOPNS(result[51]);
        System.out.println(theta_3.toString("theta_3 (-95.53404922699703)"));
        CGAScalarOPNS theta_4 = new CGAScalarOPNS(result[52]);
        System.out.println(theta_4.toString("theta_4 (-90.56670900909727)"));
        
        
        /*CGAScalarOPNS x_5 = new CGAScalarOPNS(result[53]);
        System.out.println(x_5.toString("x_5"));
        CGAScalarOPNS y_5 = new CGAScalarOPNS(result[54]);
        System.out.println(y_5.toString("y_5"));*/
         
        CGAScalarOPNS theta_5 = new CGAScalarOPNS(result[55]);
        System.out.println(theta_5.toString("theta_5 (90.21354316792402)"));
        CGAScalarOPNS theta_6 = new CGAScalarOPNS(result[56]);
        System.out.println(theta_6.toString("theta_6 (-132.37747769068068)"));
        
        //CGAOrientedPointIPNS op = new CGAOrientedPointIPNS(new Point3d(0.3,0,0), new Vector3d(0,0,1));
        //addCGAObject(op,"op");
        
        
        //CGAOrientedPointOPNS op = new CGAOrientedPointOPNS(new Point3d(0.3,0,0), new Vector3d(0,0,1));
        //addCGAObject(op,"op");
        
        // alternativ Punktepaar direkt bestimmen
        // liefert das gleiche Ergebnis
        /*CGAPointPairIPNS Qc_ = new CGAPointPairIPNS(C5k.lc(Pl).dual());
        System.out.println("squaredWeight(Qc_)="+String.valueOf(Math.sqrt(Qc_.squaredWeight())));
        System.out.println(Qc_.toString("Qc_"));
        System.out.println("r(Qc_)="+String.valueOf(Math.sqrt(Qc_.squaredSize())*1000));*/
        //addCGAObject(Qc_,"Qc_");
        
        // alternativ Punktepaar direkt ohne inneres Produkt bestimmen
        /*CGAPointPairIPNS Qc__ = new CGAPointPairIPNS(C5k.op(Pl.dual()));
        System.out.println("r(Qc__)="+String.valueOf(Math.sqrt(Qc__.squaredSize())*1000));*/
        //addCGAObject(Qc__,"Qc__");
    }

    /**
     * Add cga object into the visualization.
     *
     * @param values tuple of 32 values representing the multivector
     * @param fromGaalop set to true, if the values are in order of usage in Gaalop
     * @param isIPNS set to true, if an CGA object should be created in inner product null space representation
     * @param label label of the cga object shown in the visualisation
     */
    public void addCGAObject(double[] values, boolean fromGaalop, boolean isIPNS, String label){
        if (values.length != 32) throw new IllegalArgumentException("The given double[] is not of length 32 but \""+
                        String.valueOf(values.length)+"\"!");
        double[] useValues = values;
        if (fromGaalop){
            useValues = CGAMultivector.fromGaalop(values);
        }
        //TODO
        // CGAMultivector.create() is incomplete
        addCGAObject(CGAKVector.create(values, isIPNS), label);
    }

    /**
     * Add cga object into the visualization.
     *
     * @param m cga multivector
     * @param label label
     * @return true if the given object can be visualized, false if it is outside 
     * the axis-aligned bounding box and this box is not extended for the given object
     * @throws IllegalArgumentException if multivector is no visualizable type
     */
    public boolean addCGAObject(CGAKVector m, String label){
         return addCGAObject(m, label, null);
    }
    public boolean addCGAObject(CGAKVector m, String label, Color color){

        // cga ipns objects
        if (m instanceof CGARoundPointIPNS roundPointIPNS){
            if (color == null){
                addPoint(roundPointIPNS.decompose(), label, true);
            } else {
                addPoint(roundPointIPNS.decompose(), label, color);
            }
            return true;
        } else if (m instanceof CGALineIPNS lineIPNS){
            if (color == null){
                return addLine(lineIPNS.decomposeFlat(), label, true);
            } else {
                return addLine(lineIPNS.decomposeFlat(), label, color);
            }
        } else if (m instanceof CGAPointPairIPNS pointPairIPNS){
            //addPointPair(m.decomposeTangentOrRound(), label, true);
            
            EuclideanParameters parameters = pointPairIPNS.decompose();
            double r2 = parameters.squaredSize();
            //double r2 = pointPairIPNS.squaredSize();
            if (r2 < 0){
                //FIXME
                // decomposition schlägt fehl
                // show imaginary point pairs as circles
                //CGACircleIPNS circle = new CGACircleIPNS(pointPairIPNS);
                //addCircle(pointPairIPNS.decomposeTangentOrRound(), label, true);
                //return true;
                System.out.println("Visualize imaginary point pair \""+label+"\" failed!");
                return false;
            // tangent vector
            } else if (r2 == 0){
                System.out.println("CGA-Object \""+label+"\" is a tangent vector - not yet supported!");
                return false;

            // real point pair only?
            //FIXME
            } else {
                //ddPointPair(cGAPointPairIPNS.decomposePoints(), label, true);
                //iCGATangentOrRound.EuclideanParameters parameters = pointPairIPNS.decompose();
                Point3d loc = parameters.location();
                System.out.println("pp \""+label+"\" loc=("+String.valueOf(loc.x)+", "+String.valueOf(loc.y)+", "+String.valueOf(loc.z)+
                        " r2="+String.valueOf(parameters.squaredSize()));
                
                if (color == null){
                    addPointPair(parameters, label, true);
                } else {
                     addPointPair(parameters, label, color);
                }  
                // scheint zum gleichen Ergebnis zu führen
                //iCGAPointPair.PointPair pp = pointPairIPNS.decomposePoints();
                //addPointPair(pp, label, true);
                //double r_ = pp.p1().distance(pp.p2())/2;
                //System.out.println("Visualize real point pair \""+label+"\"with r="+String.valueOf(r_));
                return true;
            }
            
        } else if (m instanceof CGASphereIPNS sphereIPNS){
            if (color == null){
                addSphere(sphereIPNS.decompose(), label, true);
            } else {
                addSphere(sphereIPNS.decompose(), label, color);
            }
            return true;
        } else if (m instanceof CGAPlaneIPNS planeIPNS){
            if (color == null){
                return addPlane(planeIPNS.decomposeFlat(), label, true, true, true);
            } else {
                return addPlane(planeIPNS.decomposeFlat(), label, color, true, true);
            }
        } else if (m instanceof CGAOrientedPointIPNS orientedPointIPNS){
            if (color == null){
                addOrientedPoint(orientedPointIPNS.decompose(), label, true);
            } else {
                addOrientedPoint(orientedPointIPNS.decompose(), label, color);
            }
            return true;
        } else if (m instanceof CGACircleIPNS circleIPNS){
            if (color == null){
                addCircle(circleIPNS.decompose(), label, true);
            } else {
                addCircle(circleIPNS.decompose(), label, color);
            }
            return true;
        }
        //TODO
        // flat-point

        // cga opns objects
        if (m instanceof CGARoundPointOPNS roundPointOPNS){
            if (color == null){
                addPoint(roundPointOPNS.decompose(), label, false);
            } else {
                addPoint(roundPointOPNS.decompose(), label, color);
            }
            return true;
        } else if (m instanceof CGALineOPNS lineOPNS){
            if (color == null){
                addLine(lineOPNS.decomposeFlat(), label, false);
            } else {
                addLine(lineOPNS.decomposeFlat(), label, color);
            }
            return true;
        } else if (m instanceof CGAPointPairOPNS pointPairOPNS){
            iCGATangentOrRound.EuclideanParameters parameters = pointPairOPNS.decompose();
            
            if (color == null){
                addPointPair(parameters, label, false);
            } else {
                addPointPair(parameters, label, color);
            }
            //iCGAPointPair.PointPair pp = pointPairOPNS.decomposePoints();
            //addPointPair(pp, label, false);
            return true;
        } else if (m instanceof CGASphereOPNS sphereOPNS){
            if (color == null){
                addSphere(sphereOPNS.decompose(), label, false);
            } else {
                addSphere(sphereOPNS.decompose(), label, color);
            }
            return true;
        } else if (m instanceof CGAPlaneOPNS planeOPNS){
            if (color == null){
                addPlane(planeOPNS.decomposeFlat(), label, false, true, true);
            } else {
                addPlane(planeOPNS.decomposeFlat(), label, color, true, true);
            }
            return true;
        } else if (m instanceof CGACircleOPNS circleOPNS){
            if (color == null){
                addCircle(circleOPNS.decompose(), label, false);
            } else {
                 addCircle(circleOPNS.decompose(), label, color);
            }
            return true;
        } else if (m instanceof CGAOrientedPointOPNS orientedPointOPNS){
            if (color == null){
                addOrientedPoint(orientedPointOPNS.decompose(), label, false);
            } else {
                addOrientedPoint(orientedPointOPNS.decompose(), label, color);
            }
            return true;
        }
        //TODO
        // flat-point als Würfel darstellen

        throw new IllegalArgumentException("\""+m.toString("")+"\" has unknown type!");
    }


    // grade 1 multivectors

    /**
     * Add a point to the 3d view.
     *
     * @param parameters unit is [m]
     * @param isIPNS
     * @param label label or null if no label needed
     */
    public void addPoint(iCGATangentOrRound.EuclideanParameters parameters, String label, boolean isIPNS){
        Color color = COLOR_GRADE_1;
        if (!isIPNS) color = COLOR_GRADE_4;
        addPoint(parameters, label, color);
    }
    
    public void addPoint(iCGATangentOrRound.EuclideanParameters parameters, String label, Color color){
        
        if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        Point3d location = parameters.location();
        System.out.println("Add point \""+label+"\" at ("+String.valueOf(location.x)+","+
                        String.valueOf(location.y)+", "+String.valueOf(location.z)+")!");
        location.scale(1000d);
        addPoint(location, color, POINT_RADIUS*2*1000, label);
    }

    /**
     * Add a sphere to the 3d view.
     *
     * @param parameters unit is [m]
     * @param label
     * @param isIPNS
     */
    public void addSphere(iCGATangentOrRound.EuclideanParameters parameters,
                                              String label, boolean isIPNS){
            Color color = COLOR_GRADE_1;
            if (!isIPNS) color = COLOR_GRADE_4;
            addSphere(parameters, label, color);
    }
    public void addSphere(iCGATangentOrRound.EuclideanParameters parameters,
                                              String label, Color color){
        
        if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        Point3d location = parameters.location();
        location.scale(1000d);
        boolean imaginary = false;
        if (parameters.squaredSize() < 0){
            imaginary = true;
        }
        //TODO
        // Farbe ändern für imaginäre Kugeln
        double radius = Math.sqrt(Math.abs(parameters.squaredSize()));
        radius *= 1000;
        System.out.println("Add sphere \""+label+"\" at ("+String.valueOf(location.x)+"mm,"+
                        String.valueOf(location.y)+"mm, "+String.valueOf(location.z)+"mm) with radius "+
                        String.valueOf(radius)+"mm!");

        addSphere(location, radius, color, label);
    }

    /**
     * Add a plane to the 3d view.
     *
     * @param parameters unit is [m]
     * @param label
     * @param isIPNS
     * @param showPolygon 
     * @param showNormal 
     * @return true, if the plane is visible in the current bounding box
     */
    public boolean addPlane(iCGAFlat.EuclideanParameters parameters, String label,
                     boolean isIPNS, boolean showPolygon, boolean showNormal){
        Color color = COLOR_GRADE_1;
        if (!isIPNS) color = COLOR_GRADE_4;
        return addPlane(parameters, label, color, showPolygon, showNormal);
    }
    public boolean addPlane(iCGAFlat.EuclideanParameters parameters, String label,
                     Color color, boolean showPolygon, boolean showNormal){
        
        if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        Point3d location = parameters.location();
        location.scale(1000d);
        Vector3d a = parameters.attitude();
        System.out.println("plane "+label+" a=("+String.valueOf(a.x)+", "+String.valueOf(a.y)+", "+String.valueOf(a.z)+
                "), o=("+String.valueOf(location.x)+", "+String.valueOf(location.y)+", "+String.valueOf(location.z)+")");
        boolean result = true;
        if (showPolygon){
            result = addPlane(location, a, color, label);
        }
        // scheint zum Absturz zu führen
        /*if (result && showNormal){
            addArrow(location, a, TANGENT_LENGTH, 
                         LINE_RADIUS*1000, color, label);
        }*/
        return result;
    }
    
    // grade 2

    /**
     * Add a line to the 3d view.
     *
     * @param parameters, unit is [m]
     * @param isIPNS
     * @param label
     * @return true if the line is inside the bounding box and therefore visible
     */
    public boolean addLine(iCGAFlat.EuclideanParameters parameters, String label, boolean isIPNS){
        Color color = COLOR_GRADE_2;
        if (!isIPNS) color = COLOR_GRADE_3;
        return addLine(parameters, label, color);
    }
    public boolean addLine(iCGAFlat.EuclideanParameters parameters, String label, Color color){
        
        if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        Point3d location = parameters.location();
        location.scale(1000d);
        Vector3d a = parameters.attitude();
        System.out.println("add line \""+label+"\" at ("+String.valueOf(location.x)+", "+String.valueOf(location.y)+
                        ", "+String.valueOf(location.z)+") with a=("+String.valueOf(a.x)+", "+String.valueOf(a.y)+", "+
                        String.valueOf(a.z)+")");
        return addLine(location, a, color, LINE_RADIUS*1000,  label);
    }
    /**
     * Add oriented-point visualized as point and arrow.
     * 
     * @param parameters, unit is [m]
     * @param label
     * @param isIPNS 
     */
    public void addOrientedPoint(iCGATangentOrRound.EuclideanParameters parameters, 
                                                String label, boolean isIPNS){
       Color color = COLOR_GRADE_2;
       if (!isIPNS) color = COLOR_GRADE_3;
       addOrientedPoint(parameters, label, color);
    }
    public void addOrientedPoint(iCGATangentOrRound.EuclideanParameters parameters, 
                                                String label, Color color){
       if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
       
    // location
       Point3d location = parameters.location();
       location.scale(1000d);
       
       // orientation
       Vector3d direction = parameters.attitude();
       //FIXME soll die length von direction der length der attitude, also dem weight
       // des cga-Objekts entsprechen?
       direction.normalize();
       direction.scale(TANGENT_LENGTH*1000/2d);
       
       // point
       addPoint(location, color, POINT_RADIUS*2*1000, label);
       
       // arrow
       Point3d location2 = new Point3d(location);
       location2.sub(direction);
       direction.scale(2d);
       addArrow(location2, direction, 
                        LINE_RADIUS*1000, color, null);
    }

    /**
     * Add a circle to the 3d view.
     *
     * @param parameters units in [m]
     * @param label name of the circle shown in the visualisation
     * @param isIPNS true, if circle is given in inner-product-null-space representation
     */
    public void addCircle(iCGATangentOrRound.EuclideanParameters parameters,
                                              String label, boolean isIPNS){
        Color color = COLOR_GRADE_2;
        if (!isIPNS) color = COLOR_GRADE_3;
        addCircle(parameters, label, color);
    }
    public void addCircle(iCGATangentOrRound.EuclideanParameters parameters,
                                              String label, Color color){
        
        boolean isImaginary = false;
        double r2 = parameters.squaredSize();
        if (r2 <0) {
            isImaginary = true;
            System.out.println("Circle \""+label+"\" is imaginary!");
            r2 = -r2;
        }
        double r = Math.sqrt(r2)*1000;
        Point3d location = parameters.location();
        location.scale(1000d);
        Vector3d direction = parameters.attitude();
        
        System.out.println("Add circle \""+label+"\" at ("+String.valueOf(location.x)+"mm,"+
                        String.valueOf(location.y)+"mm, "+String.valueOf(location.z)+"mm) with radius "+
                        String.valueOf(r)+"\"[mm] and n= ("+String.valueOf(direction.x)+","+
                        String.valueOf(direction.y)+", "+String.valueOf(direction.z)+") ");
        addCircle(location, direction, (float) r, color, label, isImaginary);
    }


    // grade 3

    /**
     * Add a point-pair to the 3d view.
     *
     * No imaginary point-pairs, because these are ipns circles.
     *
     * @param pp unit in [m]
     * @param label
     * @param isIPNS true, if ipns representation
     */
    public void addPointPair(iCGAPointPair.PointPair pp, String label, boolean isIPNS){
            Color color = COLOR_GRADE_3;
            if (!isIPNS) color = COLOR_GRADE_2;
            Point3d[] points = new Point3d[]{pp.p1(), pp.p2()};
            points[0].scale(1000d);
            points[1].scale(1000d);
            addPointPair(points[0], points[1], label, color, color, LINE_RADIUS*1000, POINT_RADIUS*2*1000);
    }

    /**
     * Add a point-pair to the 3d view.
     *
     * Because parameters are decomposed the point-pair can not be imaginary.
     *
     * @param parameters unit in [m]
     * @param label
     * @param isIPNS true, if ipns represenation
     */
    public void addPointPair(iCGATangentOrRound.EuclideanParameters parameters,
                                                     String label, boolean isIPNS){
        Color color = COLOR_GRADE_3;
        if (!isIPNS) color = COLOR_GRADE_2;
        addPointPair(parameters, label, color);
    }
    public void addPointPair(iCGATangentOrRound.EuclideanParameters parameters,
                                                     String label, Color color){    
        Point3d l = parameters.location();
        Vector3d att = parameters.attitude();
        System.out.println("pp(tangendRound) \""+label+"\" loc=("+String.valueOf(l.x)+", "+String.valueOf(l.y)+", "+String.valueOf(l.z)+
                  ", att=("+String.valueOf(att.x)+", "+String.valueOf(att.y)+", "+String.valueOf(att.z)+
                "), r2="+String.valueOf(parameters.squaredSize()));
        Point3d[] points = decomposePointPair(parameters);
        System.out.println("pp \""+label+"\" p1=("+String.valueOf(points[0].x)+", "+String.valueOf(points[0].y)+", "+String.valueOf(points[0].z)+
                    ", p2=("+String.valueOf(points[1].x)+", "+String.valueOf(points[1].y)+", "+String.valueOf(points[1].z)+")");

        points[0].scale(1000d);
        points[1].scale(1000d);
        addPointPair(points[0], points[1], label, color, color, LINE_RADIUS*1000, POINT_RADIUS*2*1000);
     
    }
    
    /**
     * Add a tangent to the 3d view.
     *
     * @param parameters
     * @param label
     * @param isIPNS
     */
    public void addTangentVector(iCGATangentOrRound.EuclideanParameters parameters,
                                                             String label, boolean isIPNS){
            Color color = COLOR_GRADE_3;
            if (!isIPNS) color = COLOR_GRADE_2;
            Vector3d dir = new Vector3d(parameters.attitude());
            dir.normalize();
            dir.scale(TANGENT_LENGTH);
            addArrow(parameters.location(), dir,
                            LINE_RADIUS*1000, color, label);
    }


    // helper methods

    private static double signedRadius(double squaredRadius){
        double r = Math.sqrt(Math.abs(squaredRadius));
        if (squaredRadius < 0) r = -r;
        return r;
    }

    /**
     * Decompose euclidean parameters of a point-pair into two points.
     *
     * Implementation based on determination of location and squared-size.<p>
     *
     * TODO sollte das nicht in CGAPointPair... verschoeben werden?
     * Nein, ich brauche das ja ausserhalb der CGAAPI, aber wohin dann damit?<p>
     * 
     * @param parameters
     * @return the two decomposed points in Point3d[2]
     */
    private static Point3d[] decomposePointPair(iCGATangentOrRound.EuclideanParameters parameters){
        Point3d c = parameters.location();
        double r = Math.sqrt(Math.abs(parameters.squaredSize()));
        Vector3d v = parameters.attitude();
        v.normalize();
        v.scale(r); 
        Point3d[] result = new Point3d[2];
        result[0] = new Point3d(c);
        result[0].add(v);
        result[1] = new Point3d(c);
        result[1].sub(v);
        return result;
    }

    /**
     * This method must be invoked outside the EVT, because long running file
     * loading can be included. It is invoked via AnalysisLauncher.open().
     *
     * @throws Exception
     */
    @Override
    public void init() throws Exception {

        if (SwingUtilities.isEventDispatchThread()){
                System.out.println("init() invoked inside the EVT!");
                throw new RuntimeException("GeometryViewCGA.init() is invoked inside the EDT!");
        }

        Quality q = Quality.Advanced();
        q.setDepthActivated(true);
        //q.setAlphaActivated(false);
        q.setAnimated(false);
        q.setHiDPIEnabled(true);
        q.setDisableDepthBufferWhenAlpha(false);
        q.setPreserveViewportSize(true);
        //chart = initializeChart(q);

        chart = new Chart(this.getFactory(), q);

        //chart = myfactory.newChart(q);
        chart.getView().setSquared(false);
        chart.getView().setBackgroundColor(Color.WHITE);
        chart.getView().getAxis().getLayout().setMainColor(Color.BLACK);

        //Set up ObjectLoader and Mouse
        colladaLoader = ObjectLoader.getLoader();

        //Add the ChessFloor and set size
        /*this.setUpChessFloor(100f);
        chart.getScene().getGraph().addGraphListener(() -> {
            updateChessFloor(true, CHESS_FLOOR_WIDTH);
        });*/

        //setUpPickingSupport();

        //Light light = chart.addLightOnCamera();

        //addSkeleton("data/golembones/golembones.obj");


        //addPoint(new Point3d(1,1,1), Color.BLUE, 0.6f, "Point1");
        //addSphere(new Point3d(20,20,20), 10, Color.ORANGE, "Sphere1");

        //addPlane(new Point3d(5d,5d,5d), new Vector3d(0d,0d,5d), new Vector3d(5d,0d,0d), Color.RED, "Plane1");

        //addArrow(new Point3d(0d, 0d, 0d), new Vector3d(0d,0d,2d), 3f, 0.5f, Color.CYAN, "Arrow1");

        //addLabel(new Point3d(10d, 10d, 10d), "Label", Color.BLACK);
        //addCircle(new Point3d(20,20,20), new Vector3d(0,0,1),10,Color.RED, "Circle");

        //addLine(new Vector3d(0d,0d,-1d), new Point3d(3d,0d,3d), Color.CYAN, 0.2f, 10, "ClipLinie");

        //addPlane(new Point3d(0,1,5), new Vector3d(0,-10,0), new Vector3d(-10,0,0), Color.ORANGE, "ClipPlane");
        //addPoint(new Point3d(0,0,0), Color.BLUE, 0.6f, "Point1");
        //addPoint(new Point3d(1,10,1), Color.BLUE, 0.6f, "Point3");
        //addPoint(new Point3d(20,20,20), Color.BLUE, 0.6f, "Point2");
        //addPlane(new Point3d(5d,5d,5d), new Vector3d(0d,0d,5d), new Vector3d(5d,0d,0d), Color.RED, "Plane1");
        //addLine(new Vector3d(0d,0d,-1d), new Point3d(3d,0d,3d), Color.CYAN, 0.2f, 10, "ClipLinie");
        //addArrow(new Point3d(7d, 7d, 7d), new Vector3d(0d,0d,2d), 3f, 0.5f, Color.CYAN, "Arrow1");


        // test


        ArrayList<String> pathList = new ArrayList<>();
        pathList.add("data/objfiles/base.dae");
        pathList.add("data/objfiles/shoulder.dae");
        pathList.add("data/objfiles/upperarm.dae");
        pathList.add("data/objfiles/forearm.dae");
        pathList.add("data/objfiles/wrist1.dae");
        pathList.add("data/objfiles/wrist2.dae");
        pathList.add("data/objfiles/wrist3.dae");

        // Unklar, ob dies draw-Aufrufe auslöst und damit Codes in den EDT
        // ausgelagert werden müssen
        
        //FIXME intern werden hier deltas für d,r und alpha parameter angenommen
        addRobotUR5e(pathList, true);

        //addGeometricObjects();

    }

     /**
     * Add a UR5e Robot.
     * 
     * @param paths the paths to the robot parts
     * @param delta_theta_rad the angle of the single parts
     */
    public void addRobotUR5e(List<String> paths, boolean nominal){ 
        double[] delta_theta_rad = new double[]{0d,0d,0d,0d,0d,0d,0d};
        
        double[] delta_a_m;
        double[] delta_d_m;
        double[] delta_alpha_rad;
        
        if (nominal){
            delta_a_m = new double[]{0,0,0,0,0,0,0};
            delta_d_m = new double[]{0,0,0,0,0,0,0};
            delta_alpha_rad = new double[]{0,0,0,0,0,0,0};
        } else {
            delta_a_m = new double[]{0d, 0.000156734465764371306, 0.109039760794650886, 0.00135049423466820917, 0.30167176077633267e-05, 8.98147062591837358e-05, 0};
            delta_d_m = new double[]{0d, -7.63582045015809285e-05, 136.026368377065324, -130.146527922606964, 0.12049886607637639, -0.13561334270734671, -0.000218168195914358876};
            delta_alpha_rad= new double[]{0d, -0.000849612070594307767, 0.00209120614311242205, 0.0044565542371754396, -0.000376815598678081898, 0.000480742313784698894, 0};
        }
        
        EuclidRobot robot = new EuclidRobot(chart, RobotType.UR5e);
        robot.setDataWithUR5eDHDeltas(paths, delta_theta_rad, delta_alpha_rad, delta_d_m, delta_a_m);
        robotList.add(robot);
        robot.addToChartParts();
    }
    
    // https://gaalopweb.esa.informatik.tu-darmstadt.de/gaalopweb/res/python/input?sample=threespheres
    private static double[] testCreateGaalopExampleCircle(){
            double a1=0; double a2=0; double a3=0;
            double b1=0; double b2=0.4; double b3=0;
            double c1=0; double c2=0.45; double c3=0.2;
            double d14=0.5; double d24=0.4; double d34=0.3;
            double[] x1 = new double[32];//np.zeros(32)
            x1[4] = (a1 * a1 + a2 * a2 + a3 * a3) / 2.0;// # ep
            double[] x2 = new double[32];//np.zeros(32)
            x2[4] = (b1 * b1 + b2 * b2 + b3 * b3) / 2.0;// # ep
            double[] S1 = new double[32];//np.zeros(32)
            S1[4] = x1[4] - (d14 * d14) / 2.0;// # ep
            double[] S2 = new double[32];//np.zeros(32)
            S2[4] = x2[4] - (d24 * d24) / 2.0;// # ep
            double[] c = new double[32];//np.zeros(32)
            c[6] = a1 * b2 + (-(a2 * b1));// # e1 ^ e2
            c[7] = a1 * b3 + (-(a3 * b1));// # e1 ^ e3
            c[8] = a1 * S2[4] + (-(S1[4] * b1)) + -0.5 * (a1 + (-b1));// # e1 ^ ep
            c[9] = a1 * S2[4] + (-(S1[4] * b1)) + (a1 + (-b1)) / 2.0;// # e1 ^ em
            c[10] = a2 * b3 + (-(a3 * b2));// # e2 ^ e3;
            c[11] = a2 * S2[4] + (-(S1[4] * b2)) + -0.5 * (a2 + (-b2));// # e2 ^ ep
            c[12] = a2 * S2[4] + (-(S1[4] * b2)) + (a2 + (-b2)) / 2.0;// # e2 ^ em
            c[13] = a3 * S2[4] + (-(S1[4] * b3)) + -0.5 * (a3 + (-b3));// # e3 ^ ep
            c[14] = a3 * S2[4] + (-(S1[4] * b3)) + (a3 + (-b3)) / 2.0;// # e3 ^ em
            c[15] = S1[4] + (-S2[4]);// # ep ^ em
            return c;
    }
    
    public static String toString(String name, Tuple3d value){
        return name+" = ("+String.valueOf(value.x)+","+String.valueOf(value.y)+","+String.valueOf(value.z)+")";
    }
    public static String toString(String name, double value){
        return name+" = "+String.valueOf(value);
    }
}