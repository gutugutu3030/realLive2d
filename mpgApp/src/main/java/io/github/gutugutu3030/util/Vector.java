package io.github.gutugutu3030.util;

public class Vector {

    /** The x component of the vector. */
    public double x;

    /** The y component of the vector. */
    public double y;

    /** The z component of the vector. */
    public double z;

    /** Array so that this can be temporarily used in an array context */
    protected double[] array;

    /**
     * Constructor for an empty vector: x, y, and z are set to 0.
     */
    public Vector() {
    }

    /**
     * Constructor for a 3D vector.
     * 
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the y coordinate.
     */
    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructor for a 2D vector: z coordinate is set to 0.
     * 
     * @param x the x coordinate.
     * @param y the y coordinate.
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    /**
     * Set x, y, and z coordinates.
     * 
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordinate.
     */
    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Set x, y, and z coordinates from a Vector3D object.
     * 
     * @param v the Vector object to be copied
     */
    public void set(Vector v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    /**
     * Set the x, y (and maybe z) coordinates using a double[] array as the source.
     * 
     * @param source array to copy from
     */
    public void set(double[] source) {
        if (source.length >= 2) {
            x = source[0];
            y = source[1];
        }
        if (source.length >= 3) {
            z = source[2];
        }
    }

    /**
     * Get a copy of this vector.
     */
    public Vector get() {
        return new Vector(x, y, z);
    }

    public double[] get(double[] target) {
        if (target == null) {
            return new double[] { x, y, z };
        }
        if (target.length >= 2) {
            target[0] = x;
            target[1] = y;
        }
        if (target.length >= 3) {
            target[2] = z;
        }
        return target;
    }

    /**
     * Calculate the magnitude (length) of the vector
     * 
     * @return the magnitude of the vector
     */
    public double mag() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Add a vector to this vector
     * 
     * @param v the vector to be added
     */
    public void add(Vector v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    public void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    /**
     * Add two vectors
     * 
     * @param v1 a vector
     * @param v2 another vector
     * @return a new vector that is the sum of v1 and v2
     */
    static public Vector add(Vector v1, Vector v2) {
        return add(v1, v2, null);
    }

    /**
     * Add two vectors into a target vector
     * 
     * @param v1     a vector
     * @param v2     another vector
     * @param target the target vector (if null, a new vector will be created)
     * @return a new vector that is the sum of v1 and v2
     */
    static public Vector add(Vector v1, Vector v2, Vector target) {
        if (target == null) {
            target = new Vector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
        } else {
            target.set(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
        }
        return target;
    }

    /**
     * Subtract a vector from this vector
     * 
     * @param v the vector to be subtracted
     */
    public void sub(Vector v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
    }

    public void sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    /**
     * Subtract one vector from another
     * 
     * @param v1 a vector
     * @param v2 another vector
     * @return a new vector that is v1 - v2
     */
    static public Vector sub(Vector v1, Vector v2) {
        return sub(v1, v2, null);
    }

    static public Vector sub(Vector v1, Vector v2, Vector target) {
        if (target == null) {
            target = new Vector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
        } else {
            target.set(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
        }
        return target;
    }

    /**
     * Multiply this vector by a scalar
     * 
     * @param n the value to multiply by
     */
    public void mult(double n) {
        x *= n;
        y *= n;
        z *= n;
    }

    /**
     * Multiply a vector by a scalar
     * 
     * @param v a vector
     * @param n scalar
     * @return a new vector that is v1 * n
     */
    static public Vector mult(Vector v, double n) {
        return mult(v, n, null);
    }

    /**
     * Multiply a vector by a scalar, and write the result into a target Vector.
     * 
     * @param v      a vector
     * @param n      scalar
     * @param target Vector to store the result
     * @return the target vector, now set to v1 * n
     */
    static public Vector mult(Vector v, double n, Vector target) {
        if (target == null) {
            target = new Vector(v.x * n, v.y * n, v.z * n);
        } else {
            target.set(v.x * n, v.y * n, v.z * n);
        }
        return target;
    }

    /**
     * Multiply each element of one vector by the elements of another vector.
     * 
     * @param v the vector to multiply by
     */
    public void mult(Vector v) {
        x *= v.x;
        y *= v.y;
        z *= v.z;
    }

    /**
     * Multiply each element of one vector by the individual elements of another
     * vector, and return the result as a new Vector.
     */
    static public Vector mult(Vector v1, Vector v2) {
        return mult(v1, v2, null);
    }

    /**
     * Multiply each element of one vector by the individual elements of another
     * vector, and write the result into a target vector.
     * 
     * @param v1     the first vector
     * @param v2     the second vector
     * @param target Vector to store the result
     */
    static public Vector mult(Vector v1, Vector v2, Vector target) {
        if (target == null) {
            target = new Vector(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
        } else {
            target.set(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
        }
        return target;
    }

    /**
     * Divide this vector by a scalar
     * 
     * @param n the value to divide by
     */
    public void div(double n) {
        x /= n;
        y /= n;
        z /= n;
    }

    /**
     * Divide a vector by a scalar and return the result in a new vector.
     * 
     * @param v a vector
     * @param n scalar
     * @return a new vector that is v1 / n
     */
    static public Vector div(Vector v, double n) {
        return div(v, n, null);
    }

    static public Vector div(Vector v, double n, Vector target) {
        if (target == null) {
            target = new Vector(v.x / n, v.y / n, v.z / n);
        } else {
            target.set(v.x / n, v.y / n, v.z / n);
        }
        return target;
    }

    /**
     * Divide each element of one vector by the elements of another vector.
     */
    public void div(Vector v) {
        x /= v.x;
        y /= v.y;
        z /= v.z;
    }

    /**
     * Multiply each element of one vector by the individual elements of another
     * vector, and return the result as a new Vector.
     */
    static public Vector div(Vector v1, Vector v2) {
        return div(v1, v2, null);
    }

    /**
     * Divide each element of one vector by the individual elements of another
     * vector, and write the result into a target vector.
     * 
     * @param v1     the first vector
     * @param v2     the second vector
     * @param target Vector to store the result
     */
    static public Vector div(Vector v1, Vector v2, Vector target) {
        if (target == null) {
            target = new Vector(v1.x / v2.x, v1.y / v2.y, v1.z / v2.z);
        } else {
            target.set(v1.x / v2.x, v1.y / v2.y, v1.z / v2.z);
        }
        return target;
    }

    /**
     * Calculate the Euclidean distance between two points (considering a point as a
     * vector object)
     * 
     * @param v another vector
     * @return the Euclidean distance between
     */
    public double dist(Vector v) {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Calculate the Euclidean distance between two points (considering a point as a
     * vector object)
     * 
     * @param v1 a vector
     * @param v2 another vector
     * @return the Euclidean distance between v1 and v2
     */
    static public double dist(Vector v1, Vector v2) {
        double dx = v1.x - v2.x;
        double dy = v1.y - v2.y;
        double dz = v1.z - v2.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Calculate the dot product with another vector
     * 
     * @return the dot product
     */
    public double dot(Vector v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public double dot(double x, double y, double z) {
        return this.x * x + this.y * y + this.z * z;
    }

    static public double dot(Vector v1, Vector v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    /**
     * Return a vector composed of the cross product between this and another.
     */
    public Vector cross(Vector v) {
        return cross(v, null);
    }

    /**
     * Perform cross product between this and another vector, and store the result
     * in 'target'. If target is null, a new vector is created.
     */
    public Vector cross(Vector v, Vector target) {
        double crossX = y * v.z - v.y * z;
        double crossY = z * v.x - v.z * x;
        double crossZ = x * v.y - v.x * y;

        if (target == null) {
            target = new Vector(crossX, crossY, crossZ);
        } else {
            target.set(crossX, crossY, crossZ);
        }
        return target;
    }

    static public Vector cross(Vector v1, Vector v2, Vector target) {
        double crossX = v1.y * v2.z - v2.y * v1.z;
        double crossY = v1.z * v2.x - v2.z * v1.x;
        double crossZ = v1.x * v2.y - v2.x * v1.y;

        if (target == null) {
            target = new Vector(crossX, crossY, crossZ);
        } else {
            target.set(crossX, crossY, crossZ);
        }
        return target;
    }

    /**
     * Normalize the vector to length 1 (make it a unit vector)
     */
    public void normalize() {
        double m = mag();
        if (m != 0 && m != 1) {
            div(m);
        }
    }

    /**
     * Normalize this vector, storing the result in another vector.
     * 
     * @param target Set to null to create a new vector
     * @return a new vector (if target was null), or target
     */
    public Vector normalize(Vector target) {
        if (target == null) {
            target = new Vector();
        }
        double m = mag();
        if (m > 0) {
            target.set(x / m, y / m, z / m);
        } else {
            target.set(x, y, z);
        }
        return target;
    }

    /**
     * Limit the magnitude of this vector
     * 
     * @param max the maximum length to limit this vector
     */
    public void limit(double max) {
        if (mag() > max) {
            normalize();
            mult(max);
        }
    }

    /**
     * Calculate the angle of rotation for this vector (only 2D vectors)
     * 
     * @return the angle of rotation
     */
    public double heading2D() {
        double angle = Math.atan2(-y, x);
        return -1 * angle;
    }

    /**
     * Calculate the angle between two vectors, using the dot product
     * 
     * @param v1 a vector
     * @param v2 another vector
     * @return the angle between the vectors
     */
    static public double angleBetween(Vector v1, Vector v2) {
        double dot = v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
        double v1mag = Math.sqrt(v1.x * v1.x + v1.y * v1.y + v1.z * v1.z);
        double v2mag = Math.sqrt(v2.x * v2.x + v2.y * v2.y + v2.z * v2.z);
        return Math.acos(dot / (v1mag * v2mag));
    }

    public String toString() {
        return "[ " + x + ", " + y + ", " + z + " ]";
    }

    /**
     * Return a representation of this vector as a double array. This is only for
     * temporary use. If used in any other fashion, the contents should be copied by
     * using the get() command to copy into your own array.
     */
    public double[] array() {
        if (array == null) {
            array = new double[3];
        }
        array[0] = x;
        array[1] = y;
        array[2] = z;
        return array;
    }
}
