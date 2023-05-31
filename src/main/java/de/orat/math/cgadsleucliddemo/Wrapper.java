package de.orat.math.cgadsleucliddemo;

import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import de.dhbw.rahmlab.geomalgelang.api.annotation.CGAPATH;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

public interface Wrapper {

    @CGA(
        """
        d4 := 0.1333
        d6 := 0.0996
        d4,d6
        """)
    public double[][] testScalar();
    
    /**
     * Inverse Kinematics.
     * 
     * @param p_euclidean_vector
     * @param ae_euclidean_vector
     * @param se_euclidean_vector
     * @param ud_bool elbow up/down
     * @param fn_bool wrist flip/no flip
     * @param lr_bool wrist left/right
     * @return 
     */
    @CGAPATH
    double[][] ik(Point3d p_euclidean_vector, Vector3d ae_euclidean_vector, Vector3d se_euclidean_vector,
                  boolean ud_bool, boolean fn_bool, boolean lr_bool);
    
    @CGAPATH
    double[] ikangles(Point3d PiC_round_point_ipns, Point3d P1_round_point_ipns, Point3d P2_round_point_ipns,
            Vector3d ae_euclidean_vector, Vector3d se_euclidean_vector);
}
