package de.orat.math.cgadsleucliddemo;

import de.orat.math.cgadsleucliddemo.gen.WrapperGen;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

/**
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class Cgadsleucliddemo {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Point3d p = null;
        Vector3d ae = null;
        Vector3d se = null;
        double[] result = WrapperGen.INSTANCE.ik(p, ae, se);
    }
}
