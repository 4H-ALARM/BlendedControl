package com.alarmrobotics;

public class BlendedInput<T extends Blendable<T>> {
    private T control;
    private T coefficient;

    public BlendedInput(T control, T coefficient) {
        this.control = control;
        this.coefficient = coefficient;
    }

    /**
     * Returns the control point scaled field-by-field by the coefficient.
     */
    public T scaled() {
        return coefficient.mul(control);
    }
}
