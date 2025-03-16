package com.alarmrobotics;

public class BlendedInput<T extends Blendable<T>> {
    private T control;
    private T influence;

    public BlendedInput(T control, T influence) {
        this.control = control;
        this.influence = influence;
    }

    /**
     * Returns the control object scaled field-wise by the influence object.
     * 
     * Imagine "influence" is a number between 0 and 1 which is multiplied
     * against the control object.
     */
    public T scaled() {
        return influence.mul(control);
    }
}
