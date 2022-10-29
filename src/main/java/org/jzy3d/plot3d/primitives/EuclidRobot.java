/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jzy3d.plot3d.primitives;

import de.orat.math.view.euclidview3d.ColladaLoader;
import de.orat.math.view.euclidview3d.test.robot.DH;
import static java.lang.Math.PI;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;

/**
 *
 * Exept for the Constructor, setData and addToChart the Transforming operations have to be executed after the init, 
 * because the Robot consists off multiple VBO objects, which can only be transfromed after the initialisation. 
 * 
 * @author Dominik Scharnagl
 */
public class EuclidRobot{
    
    private ArrayList<EuclidRobotPart> parts;
    private Chart chart = null;
    private Color boundingBoxColor = Color.RED;
    private ArrayList<DH> dhList;
    
    /**
     * Creat a Robot
     * @param chart the chart to which the robot should be added.
     */
    public EuclidRobot(Chart chart){
        super();
        this.chart = chart;
    }
    
    /** 
     * Set the Data for the Robot
     * @param componentsPaths the paths to the .dae Files
     */
    public void setData(List<String> componentsPaths){
        parts = new ArrayList<EuclidRobotPart>();
        dhList = new ArrayList<DH>();
        ColladaLoader loader = new ColladaLoader();
        for(String path: componentsPaths){
            parts.add(loader.getCOLLADA(path));
        }
    } 
    
    /**
     * Set the Data for the Robot
     * @param componentsPaths The path to the .dae Files
     * @param delta_theta_rad the delta theta in radiant for mDH
     * @param delta_alpha_rad the delta alpha in radiant for mDH
     * @param delta_d_m the delta d for mDH
     * @param delta_r_m the delta r for mDH
     */
     public void setData(List<String> componentsPaths, double[] delta_theta_rad, double[] delta_alpha_rad,double[] delta_d_m, double[] delta_r_m){
        parts = new ArrayList<EuclidRobotPart>();
        dhList = new ArrayList<DH>();
        ColladaLoader loader = new ColladaLoader();
        for(String path: componentsPaths){
            parts.add(loader.getCOLLADA(path));
        }
        double[] d_n_m_ = new double[]{0d, 162.5, 0d, 0d, 0, 99.7, 99.6};
        double[] a_n_m = new double[]{0d, 0d,  -425, -392.2, 0d, 0d, 0d};
        double[] alpha_n_rad = new double[]{0d, PI/2d, 0d, 0d, PI/2, -PI/2d, 0d};
        double[] d_m = new double[7];
        double[] r_m = new double[7];
        double[] alpha_rad = new double[7];
        double[] theta_rad = new double[7];
        for (int i=0;i<7;i++){
            d_m[i] = d_n_m_[i] + delta_d_m[i];
            r_m[i] = a_n_m[i] + delta_r_m[i];
            alpha_rad[i] = alpha_n_rad[i] + delta_alpha_rad[i];
            theta_rad[i] = delta_theta_rad[i];
        }
        /*
        TestRobot.DHAxes axes = determineDHAxesFromDH(theta_rad, alpha_rad, d_m, r_m);
        for(int i = 0; i < axes.x().length; i++){
            parts.get(i).setCoordCenter(new Coord3d(axes.c()[i].x, axes.c()[i].y, axes.c()[i].z));
            parts.get(i).setLocalVectorsystemX(new Coord3d(axes.x()[i].x, axes.x()[i].y, axes.x()[i].z));
            parts.get(i).setLocalVectorsystemZ(new Coord3d(axes.z()[i].x, axes.z()[i].y, axes.z()[i].z));
            System.out.println(parts.get(i).getCenter());
        }
        */
        for(int i = 0; i < theta_rad.length; i++){
            dhList.add(new DH(Math.toDegrees(theta_rad[i]), Math.toDegrees(alpha_rad[i]), d_m[i], r_m[i]));
        }
    } 
     
     /**
      * Set Data with Degrees
      * @param componentsPaths The path to the .dae Files
      * @param theta the theta of the DH
      * @param alpha the alpha of the DH
      * @param d the d of the DH
      * @param r the r of the DH 
      */
     public void setDataDegrees(List<String> componentsPaths, double[] theta, double[] alpha,double[] d, double[] r){
        parts = new ArrayList<EuclidRobotPart>();
        dhList = new ArrayList<DH>();
        ColladaLoader loader = new ColladaLoader();
        for(String path: componentsPaths){
            parts.add(loader.getCOLLADA(path));
        }
        for(int i = 0; i < theta.length; i++){
            dhList.add(new DH(theta[i], alpha[i], d[i], r[i]));
        }
    } 
    
    /**
     * Add the Robot to the Chart
     */
    public void addToChartParts(){
        for(EuclidRobotPart robotPart: parts){
            robotPart.drawRobotPart(chart);
        }
    }
    
    /**
     * Move the robot acording to its DH Paramteres
     */
    public void moveDH(){
        for(int i = 1; i < parts.size()-1; i++){
            for(int j = i; j < parts.size(); j++){
                translateD(i, j);
            }
            for(int j = i; j < parts.size(); j++){
                rotateTheta(i, j, j);
            }
            for(int j = i; j < parts.size(); j++){
                rotateAlpha(i, j);
            }
            for(int j = i+1; j < parts.size(); j++){
                translateR(i, j);
            }
        }
    }
    
    /**
     * Rotate a part with the angle theta
     * @param i the DH parameter part in the list
     * @param j the object which will be rotatet
     */
    private void rotateTheta(int i, int j, int center){
        if(dhList.get(i).getTheta() != 0){
            Coord3d z = parts.get(i-1).getLocalVectorsystemZ();
            z = new Coord3d(Math.round(z.x), Math.round(z.y), Math.round(z.z));
            Coord3d around = z;
            parts.get(j).rotateAroundVector2((float) dhList.get(i).getTheta(), around, parts.get(center).getCenter());
            Coord3d x = parts.get(j).getLocalVectorsystemX();
            Coord3d newX = x.rotate((float) dhList.get(i).getTheta(),around);
            parts.get(j).setLocalVectorsystemX(newX);
            Coord3d newZ = parts.get(j).getLocalVectorsystemZ();
            newZ = newZ.rotate((float) dhList.get(i).getTheta(),around);
            parts.get(j).setLocalVectorsystemZ(newZ);
            Coord3d newCenter = parts.get(j).getCenter();
            if(j != i){
                //Hier richtige Center rotation ala rotateAroundVector2 hinzufügen
                //newCenter = newCenter.rotate((float) dhList.get(i).getTheta(),around);
                parts.get(j).rotateCenter((float) dhList.get(i).getTheta(), around, parts.get(center).getCenter());
            }
        }
    }
    
    /**
     * Translate a part along D
     * @param i the DH D Parameter in the list
     * @param j the object which will be translated
     */
    private void translateD(int i, int j){
        if((float) dhList.get(i).getD() != 0){
            Coord3d c = parts.get(j).getCenter();
            Coord3d z = parts.get(i-1).getLocalVectorsystemZ();
            z = new Coord3d(Math.round(z.x), Math.round(z.y), Math.round(z.z));
            float d = (float) dhList.get(i).getD();
            parts.get(j).translateAlongVector(d, z);
            parts.get(j).setCoordCenter(new Coord3d(c.x + d * z.x, c.y + d * z.y, c.z + d * z.z));
        }
    }
    
    /**
     * Translate a part along R
     * @param i the DH R Parameter in the list
     * @param j the object which will be translated
     */
    private void translateR(int i, int j){
        if((float) dhList.get(i).getR() != 0){
            Coord3d c = parts.get(j).getCenter();
            Coord3d x = parts.get(i).getLocalVectorsystemX();
            x = new Coord3d(Math.round(x.x), Math.round(x.y), Math.round(x.z));
            float r = (float) dhList.get(i).getR();
            parts.get(j).translateAlongVector(r, x);
            parts.get(j).setCoordCenter(new Coord3d(c.x + r * x.x, c.y + r * x.y, c.z + r * x.z));
        }
    }
    
    /**
     * Rotate a part with the angle alpha
     * @param i the DH parameter part in the list
     * @param j the object which will be rotatet
     */
    private void rotateAlpha(int i, int j){
         if((float) dhList.get(i).getAlpha() != 0){
            Coord3d newZ = parts.get(j).getLocalVectorsystemZ();
            float alpha = (float) dhList.get(i).getAlpha();
            if(alpha < 0){
                alpha = 360 + alpha;
            }
            Coord3d x = parts.get(i).getLocalVectorsystemX();
            x = new Coord3d(Math.round(x.x), Math.round(x.y), Math.round(x.z));
            newZ = newZ.rotate(alpha, x);
            parts.get(j).setLocalVectorsystemZ(newZ);
         }
    }
    
    /**
     * Rotate the Coordinate System to have the Z-Vector up top.
     */
    public void rotateCoordSystem(){
        for(int i = 0; i < parts.size(); i++){
            parts.get(i).rotateAroundVector(90, new Coord3d(1,0,0));
            parts.get(i).setLocalVectorsystemZ(new Coord3d(0,0,1));
            if(i == 2 || i == 3){
                parts.get(i).rotateAroundVector(90, new Coord3d(0,-1,0));
            }
            if(i==4 || i == 5){
                parts.get(i).rotateAroundVector(180, new Coord3d(0,1,0));
            }
            if(i == 5){
                parts.get(i).translateAlongVector(100 , new Coord3d(0,0,1));
            }
            if(i >= 5){
                parts.get(i).translateAlongVector(125, new Coord3d(0,-1,0));
            }
        }
    }
    
    public void setTheta(int axis, float theta){
       DH oldDH = dhList.get(axis);
       float oldTheta = (float) oldDH.getTheta();
       float rotateTheta = theta - oldTheta;
       if(rotateTheta < 0){
           rotateTheta = 360 + rotateTheta;
       }
       DH newDh = new DH(rotateTheta, oldDH.getAlpha(), oldDH.getD(), oldDH.getR());
       dhList.set(axis, newDh);
           for(int j = axis; j < parts.size(); j++){
                rotateTheta(axis, j, axis);
            }
       newDh = new DH(theta, oldDH.getAlpha(), oldDH.getD(), oldDH.getR());
       dhList.set(axis, newDh);
    }
    
    /**
     * Print out the lokal coordinate systems of the parts
     */
    public void getCoordsys(){
        for(int i = 0; i < parts.size(); i++){
            System.out.println(i + " -X: " + parts.get(i).getLocalVectorsystemX() + " - Z:" +  parts.get(i).getLocalVectorsystemZ());
        }
    }
 
    /**
     * Returns the Chart on which the Robot is located
     * @return the Chart
     */
    public Chart getChart(){
        return this.chart;
    }
    
    /**
     * Set the BoundingBox Color for the Robot
     * @param color The Color of the BoundingBox
     */
    public void setBoundingBoxColor(Color color){
        for(EuclidRobotPart part: parts){
            part.setBoundingBoxColor(color);
        }
    }
    
    /**
     * Return the DH Values for the parts.
     * @return the DH Values as a list
     */
    public List<DH> getDHs(){
        return this.dhList;
    }
    
}
