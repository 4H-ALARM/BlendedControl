package com.alarmrobotics;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A BlendedControl represents the merging of many input components
 * to calculate a final output component. This was designed for FIRST
 * Robotics Competition (FRC), but ultimately it's just a math library
 * and could be used for anything.
 * 
 * Here's a quick overview of how to think about and use this library:
 * 
 * Firstly, the primitives of this library are the Blendable interface,
 * the BlendedInput class, and this BlendedControl class.
 * 
 * The Blendable interface should be implemented by you for a chosen
 * class type that will represent all of the control values for your system
 * or robot. I typically name this class something like ControlVector
 * (for an example, see the BlendedControlTest file in the test directory).
 * 
 * A Blendable type is one that can "add" with another instance of itself,
 * "multiply" with another instance of itself, and "lerp" (or linearly-
 * interpolate) with another instance of itself. Each of these functions
 * should be defined point-wise. For example, in the add function, for
 * every field in your class you should just add this.field + other.field.
 * The same applies for multiplication and lerping, just do it field by field.
 */
public class BlendedControl<T extends Blendable<T>> {
    private ArrayList<Supplier<BlendedInput<T>>> inputs = new ArrayList<>();

    public BlendedControl() {}

    public void addInput(Supplier<BlendedInput<T>> input) {
        inputs.add(input);
    }

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
