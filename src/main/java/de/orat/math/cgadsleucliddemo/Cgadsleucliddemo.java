package de.orat.math.cgadsleucliddemo;

import de.orat.math.cgadsleucliddemo.gen.WrapperGen;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class Cgadsleucliddemo {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Point3d p = new Point3d(1,2,3);
        Vector3d ae = new Vector3d(1d,3d,3d);
        //Vector3d se = null;
        double[][] result = WrapperGen.INSTANCE.testIKPart1(p, ae);
        //double[][] result = WrapperGen.INSTANCE.testScalar();
    }
}
