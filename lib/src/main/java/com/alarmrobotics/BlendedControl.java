package com.alarmrobotics;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A BlendedControl is a data structure that takes a list of input-gathering
 * functions (suppliers) in order to blend those inputs together and calculate
 * a final output.
 */
public class BlendedControl<T extends Blendable<T>> {
    private ArrayList<Supplier<BlendedInput<T>>> inputs = new ArrayList<>();

    /**
     * Create a new BlendedControl with zero inputs.
     */
    public BlendedControl() {}

    /**
     * Add an input supplier. This will be called once per frame to collect
     * the control points and coefficients.
     */
    public void addInput(Supplier<BlendedInput<T>> input) {
        inputs.add(input);
    }

    /**
     * Add an input supplier. This method returns "this" BlendedControl to
     * allow for fluent method chaining.
     */
    public BlendedControl<T> withInput(Supplier<BlendedInput<T>> input) {
        addInput(input);
        return this;
    }

    /**
     * Calculates one final output value which is the blended combination of
     * all inputs this frame. This value should be used to assign robot outputs
     * such as drive speeds or subsystem positions.
     */
    public Optional<T> calculate() {
        return inputs.stream()
            // Get the current values from the Supplier
            .map((it) -> it.get())
            // Get the scaled output from the BlendedInput
            .map((it) -> it.scaled())
            // Add each Blendable
            .reduce((a, b) -> a.add(b));
    }
}
