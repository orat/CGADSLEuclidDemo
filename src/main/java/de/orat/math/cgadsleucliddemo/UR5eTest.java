package de.orat.math.cgadsleucliddemo;

import de.orat.math.cga.api.CGACircleIPNS;
import de.orat.math.cga.api.CGACircleOPNS;
import de.orat.math.cga.api.CGAEuclideanVector;
import de.orat.math.cga.api.CGAKVector;
import de.orat.math.cga.api.CGALineIPNS;
import de.orat.math.cga.api.CGALineOPNS;
import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAOrientedPointIPNS;
import de.orat.math.cga.api.CGAOrientedPointOPNS;
import de.orat.math.cga.api.CGAPlaneIPNS;
import de.orat.math.cga.api.CGAPlaneOPNS;
import de.orat.math.cga.api.CGAPointPairIPNS;
import de.orat.math.cga.api.CGAPointPairOPNS;
import de.orat.math.cga.api.CGARoundPointIPNS;
import de.orat.math.cga.api.CGARoundPointOPNS;
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
import javax.swing.SwingUtilities;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Tuple3d;
import org.jogamp.vecmath.Vector3d;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.EuclidRobot;
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
    public static float TANGENT_LENGTH = 0.1f;

    public static void main(String[] args) throws Exception {
        
        //System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        //GeometryViewCGA gv = new GeometryViewCGA();
        UR5eTest gv = new UR5eTest();
        AnalysisLauncher.open(gv);

        //Robots have to be rotated after initialisation.
        rotateRobotsCoordsystem();
        setRobotsDH();

        EuclidRobot robot = robotList.get(0); // letzter Eintrag aus der Tabelle
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
        
        /**
         * d4 := 0.1333
            d6 := 0.0996
            Pe := p+0.5p²εᵢ+ε₀
            t := p-(d6 ae)
            P5 := t+0.5t²εᵢ+ε₀
            Sc := P5-0.5d4²εᵢ
            K0 := ε₀+(Sc⋅ε₀)εᵢ
            C5k := Sc^K0
            Qc := (C5k⋅(P5^ε₁^ε₂^εᵢ))*
            Pe, P5, Sc, K0, C5k, Qc
         */
        Point3d p = new Point3d(0.2401191099971,-0.399999869993223,0.489999999997885);
        Vector3d ae = new Vector3d(0d,0d,-1d);
        Vector3d se = new Vector3d(0d,1d,0d);
        double[][] result = de.orat.math.cgadsleucliddemo.gen.WrapperGen.INSTANCE.ik(p, ae, se);
        
        CGARoundPointIPNS Pe = new CGARoundPointIPNS(result[0]);
        System.out.println(Pe.toString("Pe"));
        addCGAObject(Pe, "Pe");

        CGARoundPointIPNS P5e = new CGARoundPointIPNS(result[1]);
        System.out.println(P5e.toString("P5e"));
        addCGAObject(P5e, "P5e");
        
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
        
        CGARoundPointIPNS Pc = new CGARoundPointIPNS(result[7]);
        System.out.println(Pc.toString("Pc"));
        addCGAObject(Pc,"Pc");
        
        CGAMultivector Pce = new CGAMultivector(result[8]);
        System.out.println(Pce.toString("Pce"));
        
        CGAPlaneOPNS PIc = new CGAPlaneOPNS(result[9]); 
        System.out.println(PIc.toString("PIc"));
        //addCGAObject(PIc,"PIc");
        
        //CGAPlaneIPNS PIcp = new CGAPlaneIPNS(result[10]); 
        //System.out.println(PIc.toString("PIcp"));
        //addCGAObject(PIcp,"PIcp");
        
        CGAPlaneOPNS Pi56_o = new CGAPlaneOPNS(result[11]); 
        System.out.println(Pi56_o.toString("Pi56_o"));
        //addCGAObject(Pi56_o,"Pi56_o");
        
        //CGAMultivector n56 = new CGAMultivector(result[12]); 
        //System.out.println(n56.toString("n56"));
        
        CGAPlaneIPNS Pic_o = new CGAPlaneIPNS(result[13]); 
        System.out.println(PIc.toString("Pic_o"));
        //addCGAObject(Pic_o,"Pic_o");
        
        CGALineIPNS L_45 = new CGALineIPNS(result[14]); 
        System.out.println(PIc.toString("L_45"));
        //addCGAObject(L_45,"L_45");
        
        //CGASphereIPNS S_5 = new CGASphereIPNS(result[15]); 
        //System.out.println(S_5.toString("S_5"));
        //addCGAObject(S_5,"S_5");
        
        CGAPointPairOPNS Q_4 = new CGAPointPairOPNS(result[16]); 
        System.out.println(Q_4.toString("Q_4")); // grade-2 also opns
        //addCGAObject(Q_4,"Q_4");
        
        CGARoundPointIPNS P_4 = new CGARoundPointIPNS(result[17]);
        System.out.println(P_4.toString("P_4"));
        addCGAObject(P_4,"P_4");
        
        //S_4  := P_4 + (0.5d_4²εᵢ) // ipns 18
        CGASphereIPNS S_4 = new CGASphereIPNS(result[18]);
        System.out.println(S_4.toString("S_4")); 
        //addCGAObject(S_4, "S_4");
        
        //L_34 := P_4∧Pi_c*∧εᵢ // opns
        CGALineOPNS L_34 = new CGALineOPNS(result[19]); 
        System.out.println(L_34.toString("L_34"));
        addCGAObject(L_34,"L_34");
        
        // Q_3  := L_34⌊S_4 // opns
        CGAPointPairOPNS Q_3 = new CGAPointPairOPNS(result[20]); 
        System.out.println(Q_3.toString("Q_3")); // grade-2 also opns
        //addCGAObject(Q_3,"Q_3");
        
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
        CGARoundPointIPNS P_1 = new CGARoundPointIPNS(result[22]);
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
        
        
        // a_1, b_1, N_1
        //TODO
        
        
        // 2. joint (shoulder)
        
        // a_2 ok
        CGAEuclideanVector a_2 = new CGAEuclideanVector(result[34]);
        //  a_2 = (-0.9999999999999992*e3)
        System.out.println(a_2.toString("a_2"));
        
        // b_2 vermutlich ok
        CGAEuclideanVector b_2 = new CGAEuclideanVector(result[35]);
        // b_2 = (-0.31373406050916575*e1 + 0.2866963201085384*e2 + 0.0010767138214293195*e3)
        System.out.println(b_2.toString("b_2"));
        
        // N_2 vermutlich ok
        CGAMultivector N_2 = new CGAMultivector(result[36]);
        // N_2 = (-0.33004085614930245*e1^e3 + 0.3015977888722483*e2^e3)
        System.out.println(N_2.toString("N_2"));
        
        
        // 3. joint (elbow)
        
        // a_3 
        CGAEuclideanVector a_3 = new CGAEuclideanVector(result[37]);
        System.out.println(a_3.toString("a_3"));
        
        // b_3 
        CGAEuclideanVector b_3 = new CGAEuclideanVector(result[38]);
        System.out.println(b_3.toString("b_3"));
        
        // N_3 vermutlich ok
        CGAMultivector N_3 = new CGAMultivector(result[39]);
        System.out.println(N_3.toString("N_3"));
        
        
        // 4. joint (wrist-1)
        
        // a_4 
        CGAEuclideanVector a_4 = new CGAEuclideanVector(result[40]);
        System.out.println(a_4.toString("a_4"));
        
        // b_4
        CGAEuclideanVector b_4 = new CGAEuclideanVector(result[41]);
        System.out.println(b_4.toString("b_4"));
        
        // N_4 
        CGAMultivector N_4 = new CGAMultivector(result[42]);
        System.out.println(N_4.toString("N_4"));
        
        
        CGAOrientedPointIPNS op = new CGAOrientedPointIPNS(new Point3d(0.3,0,0), new Vector3d(0,0,1));
        addCGAObject(op,"op");
        
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

        // cga ipns objects
        if (m instanceof CGARoundPointIPNS roundPointIPNS){
            addPoint(roundPointIPNS.decompose(), label, true);
            return true;
        } else if (m instanceof CGALineIPNS lineIPNS){
            return addLine(lineIPNS.decomposeFlat(), label, true);
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
                
                addPointPair(parameters, label, true);
                        
                // scheint zum gleichen Ergebnis zu führen
                //iCGAPointPair.PointPair pp = pointPairIPNS.decomposePoints();
                //addPointPair(pp, label, true);
                //double r_ = pp.p1().distance(pp.p2())/2;
                //System.out.println("Visualize real point pair \""+label+"\"with r="+String.valueOf(r_));
                return true;
            }
            
        } else if (m instanceof CGASphereIPNS sphereIPNS){
            addSphere(sphereIPNS.decompose(), label, true);
            return true;
        } else if (m instanceof CGAPlaneIPNS planeIPNS){
            return addPlane(planeIPNS.decomposeFlat(), label, true, true, true);
        } else if (m instanceof CGAOrientedPointIPNS orientedPointIPNS){
            addOrientedPoint(orientedPointIPNS.decompose(), label, true);
            return true;
        } else if (m instanceof CGACircleIPNS circleIPNS){
            addCircle(circleIPNS.decompose(), label, true);
            return true;
        }
        //TODO
        // flat-point

        // cga opns objects
        if (m instanceof CGARoundPointOPNS roundPointOPNS){
            addPoint(roundPointOPNS.decompose(), label, false);
            return true;
        } else if (m instanceof CGALineOPNS lineOPNS){
            addLine(lineOPNS.decomposeFlat(), label, false);
            return true;
        } else if (m instanceof CGAPointPairOPNS pointPairOPNS){
            iCGATangentOrRound.EuclideanParameters parameters = pointPairOPNS.decompose();
            addPointPair(parameters, label, false);
            
            //iCGAPointPair.PointPair pp = pointPairOPNS.decomposePoints();
            //addPointPair(pp, label, false);
            return true;
        } else if (m instanceof CGASphereOPNS sphereOPNS){
            addSphere(sphereOPNS.decompose(), label, false);
            return true;
        } else if (m instanceof CGAPlaneOPNS planeOPNS){
            addPlane(planeOPNS.decomposeFlat(), label, false, true, true);
            return true;
        } else if (m instanceof CGACircleOPNS circleOPNS){
            addCircle(circleOPNS.decompose(), label, false);
            return true;
        } else if (m instanceof CGAOrientedPointOPNS orientedPointOPNS){
            addOrientedPoint(orientedPointOPNS.decompose(), label, false);
            return true;
        }
        //TODO
        // flat-point

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
            Point3d l = parameters.location();
            Vector3d att = parameters.attitude();
            //pp(tangendRound) "pp" loc=(0.10000000000000002, 0.20000000000000004, 0.0, att=(0.0, 0.0, 1.0), r2=0.03999999999999998
            System.out.println("pp(tangendRound) \""+label+"\" loc=("+String.valueOf(l.x)+", "+String.valueOf(l.y)+", "+String.valueOf(l.z)+
                      ", att=("+String.valueOf(att.x)+", "+String.valueOf(att.y)+", "+String.valueOf(att.z)+
                    "), r2="+String.valueOf(parameters.squaredSize()));
            
            Color color = COLOR_GRADE_3;
            if (!isIPNS) color = COLOR_GRADE_2;
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


        double[] delta_theta_rad = new double[]{0d,0d,0d,0d,0d,0d,0d};

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
        addRobotUR5e(pathList, delta_theta_rad);

        //addGeometricObjects();

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