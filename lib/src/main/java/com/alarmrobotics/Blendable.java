package com.alarmrobotics;

public interface Blendable<T extends Blendable<T>> {
    /**
     * Linearly interpolate (lerp) each field of this T with the corresponding fields of the other T.
     */
    T lerp(T other, double t);

    /**
     * Add each field of this T to the corresponding fields of the other T.
     */
    T add(T other);

    /**
     * Multiply each field of this T with the correspondig fields of the other T.
     */
    T mul(T other);

    /**
     * Linearly interpolate (lerp) between the first and second value.
     */
    static double lerp(double first, double second, double t) {
        return ((1 - t) * first) + (t * second);
    }
}
