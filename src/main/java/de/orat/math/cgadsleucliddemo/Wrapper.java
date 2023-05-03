package de.orat.math.cgadsleucliddemo;

import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

public interface Wrapper {

    // Kommentarzeilen gibts noch nicht, daher im folgenden zu entfernen
    //TODO
    @CGA(
    """
    a2 := -0.425
    a3 := -0.3922
    d1 := 0.1625
    d4 := 0.1333
    d5 := 0.0997
    d6 := 0.0996

    // position of the end-effector and joint 5
    Pe := p+0.5p²εᵢ+ε₀

    P5e := p-d6 ae
    P5 := P5e+0.5P5e²εᵢ+ε₀
    //P5 = createPoint(px - d6 * ae1, py - d6 * ae2, pz - d6 * ae3)

    // sphere around P5
    Sc := P5-0.5d4²εᵢ

    // sphere around the origin
    K0 := ε₀+(Sc⋅ε₀)εᵢ
    // intersection of Sc and K0
    C5k := Sc^K0
    // intersection of C5k and the horizontal plane through P5
    Qc := (C5k⋅(P5^ε₁^ε₂^εᵢ))*
    // point Pc with an offset d4 from P5
    Pc := ExtractFirstPoint(Qc)
    // plane through joints 1, 2, 3 and 4
    PIc := (ε₀^ε₃^Pc^εᵢ)*
    // finding P3 and P4
    // plane parallel to PIc that contains P4 and P5
    PIc_parallel := PIc + (P5⋅PIc)εᵢ // eq. 47
    PI56_orthogonal := ((P5^Pe)*^εᵢ)* // eq. 48 l.1
    n56_orthogonal  := -((PI56_orthogonal*⋅ε₀)⋅εᵢ)/abs((PI56_orthogonal*⋅ε₀)⋅εᵢ) // eq. 48, l. 2
    PIc_orthogonal := (P5^n56_orthogonal^εᵢ)*
    L45 := PIc_parallel^PIc_orthogonal
    S5  := P5-(0.5d5²εᵢ)
    Q4  := (L45⋅S5*)*
    P4  := ExtractFirstPoint(Q4)

    // point P3
    S4  := P4 + (0.5d4²εᵢ)
    L34 := (P4^PIc^εᵢ)*
    Q3  := (S4⋅L34*)*
    P3  := ExtractFirstPoint(Q3)

    // finding P1 and P2
    P1 := createPoint(0, 0, d1)
    S1 := P1-0.5a2²εᵢ
    S3 := P3+0.5a3²εᵢ
    C2 := S1^S3
    Q2 := (C2*⋅PIc)*
    P2 := ExtractFirstPoint(Q2)


    // finding the joint angles

    L01 := (ε₀^ε₃^εᵢ)*
    L12 := (P1^P2^εᵢ)*
    L23 := (P2^P3^εᵢ)*

    P0 := ε₀ 

    a1 := ε₂
    b1 := -PIc
    N1 := ε₁^ε₂

    a2 := (L01*⋅ε₀)⋅εᵢ
    b2 := (L12*⋅ε₀)⋅εᵢ
    N2 := -(PIc*⋅ε₀)⋅εᵢ

    a3 := (L12*⋅ε₀)⋅εᵢ
    b3 := (L23*⋅ε₀)⋅εᵢ
    N3 := -(PIc*⋅ε₀)⋅εᵢ

    a4 := (L23*⋅ε₀)⋅εᵢ
    b4 := (L45*⋅ε₀)⋅εᵢ
    N4 := -(PIc*⋅ε₀)⋅εᵢ
  
    a5 := Pc;
    b5 := -ae
    N5 := (-L45^ε₀)⋅εᵢ
   
    a6 := (L45*⋅ε₀)⋅εᵢ
    b6 := -se
    N6 := -ae (ε₃^ε₂^ε₁)
  
    """)
    public double[] ikVisu(Point3d p_euclidean_vector, Vector3d ae_euclidean_vector, 
            Vector3d se_euclidean_vector);
    
    // Kommentarzeilen gibts noch nicht, daher im folgenden zu entfernen
    //TODO
    @CGA(
    """
    // dh parameters UR5e
    a2 := -0.425
    a3 := -0.3922
    d1 := 0.1625
    d4 := 0.1333
    d5 := 0.0997
    d6 := 0.0996

    // position of the end-effector and joint 5
    Pe := p+0.5p²εᵢ+ε₀

    P5e := p-d6 ae
    P5 := P5e+0.5P5e²εᵢ+ε₀
    //P5 = createPoint(px - d6 * ae1, py - d6 * ae2, pz - d6 * ae3)

    // sphere around P5
    Sc := P5-0.5d4²εᵢ

    // sphere around the origin
    K0 := ε₀+(Sc⋅ε₀)εᵢ
    // intersection of Sc and K0
    C5k := Sc^K0
    // intersection of C5k and the horizontal plane through P5
    Qc := (C5k⋅(P5^ε₁^ε₂^εᵢ))*
    // point Pc with an offset d4 from P5
    Pc := ExtractFirstPoint(Qc)
    // plane through joints 1, 2, 3 and 4
    PIc := (ε₀^ε₃^Pc^εᵢ)*
    // finding P3 and P4
    // plane parallel to PIc that contains P4 and P5
    PIc_parallel := PIc + (P5⋅PIc)εᵢ // eq. 47
    PI56_orthogonal := ((P5^Pe)*^εᵢ)* // eq. 48 l.1
    n56_orthogonal  := -((PI56_orthogonal*⋅ε₀)⋅εᵢ)/abs((PI56_orthogonal*⋅ε₀)⋅εᵢ) // eq. 48, l. 2
    PIc_orthogonal := (P5^n56_orthogonal^εᵢ)*
    L45 := PIc_parallel^PIc_orthogonal
    S5  := P5-(0.5d5²εᵢ)
    Q4  := (L45⋅S5*)*
    P4  := ExtractFirstPoint(Q4)

    // point P3
    S4  := P4 + (0.5d4²εᵢ)
    L34 := (P4^PIc^εᵢ)*
    Q3  := (S4⋅L34*)*
    P3  := ExtractFirstPoint(Q3)

    // finding P1 and P2
    P1 := createPoint(0, 0, d1)
    S1 := P1-0.5a2²εᵢ
    S3 := P3+0.5a3²εᵢ
    C2 := S1^S3
    Q2 := (C2*⋅PIc)*
    P2 := ExtractFirstPoint(Q2)


    // finding the joint angles

    L01 := (ε₀^ε₃^εᵢ)*
    L12 := (P1^P2^εᵢ)*
    L23 := (P2^P3^εᵢ)*

    P0 := ε₀ //createPoint(0,0,0);

    a1 := ε₂
    b1 := -PIc
    N1 := ε₁^ε₂
    x1 := (a1^b1)/N1
    y1 := a1⋅b1;

    a2 := (L01*⋅ε₀)⋅εᵢ
    b2 := (L12*⋅ε₀)⋅εᵢ
    N2 := -(PIc*⋅ε₀)⋅εᵢ
    x2 := (a2^b2)/N2
    y2 := a2⋅b2

    a3 := (L12*⋅ε₀)⋅εᵢ
    b3 := (L23*⋅ε₀)⋅εᵢ
    N3 := -(PIc*⋅ε₀)⋅εᵢ
    x3 := (a3^b3)/N3
    y3 := a3⋅b3

    a4 := (L23*⋅ε₀)⋅εᵢ
    b4 := (L45*⋅ε₀)⋅εᵢ
    N4 := -(PIc*⋅ε₀)⋅εᵢ
    x4 := (a4^b4)/N4
    y4 := a4⋅b4

    a5 := Pc;
    b5 := -ae
    N5 := (-L45^ε₀)⋅εᵢ
    x5 := (a5∧b5)/N5
    y5 := a5⋅b5

    a6 := (L45*⋅ε₀)⋅εᵢ
    b6 := -se
    N6 := -ae (ε₃^ε₂^ε₁)
    x6 := (a6∧b6)/N6
    y6 := a6⋅b6
    atan2(y1,x1), atan2(y2,x2), atan2(y3,x3), atan2(y4, x4), atan2(y5, y5), atan2(y6,x6)
    """)
    public double[] ik(Point3d p_euclidean_vector, Vector3d ae_euclidean_vector, 
            Vector3d se_euclidean_vector);
    
    @CGA(
        """
        d4 := 0.1333
        d6 := 0.0996
        d4,d6
        """)
    public double[][] testScalar();
    
    @CGA(
    """
    d4 := 0.1333
    d6 := 0.0996
    Pe := p+0.5p²εᵢ+ε₀
    P5e := p-d6 ae
    P5 := P5e+0.5P5e²εᵢ+ε₀
    Sc := P5-0.5d4²εᵢ
    K0 := ε₀+(Sc⋅ε₀)εᵢ
    C5k := Sc^K0
    Qc := (C5k⋅(P5^ε₁^ε₂^εᵢ))*
    Pe, P5e, Sc, K0, C5k, Qc
    """)
    public double[][] testIKPart1(Point3d p_euclidean_vector, Vector3d ae_euclidean_vector/*, 
                Vector3d se_euclidean_vector*/);
    
    //Pc := ExtractFirstPoint(Qc)
    //PIc := (ε₀^ε₃^Pc^εᵢ)*
}
