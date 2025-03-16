package com.alarmrobotics;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A BlendedControl represents the merging of many input components
 * to calculate a final output value. This was designed for FIRST
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
 * 
 * I highly recommend this video by Freya Holmer called "The Continuity
 * of Splines": https://www.youtube.com/watch?v=jvPPXbo87ds
 * 
 * In that video Freya does an excellent job at explaining how interpolation
 * works, and just after the first few minutes we learn that a linear
 * interpolation is simply a way of taking two points in a space and
 * calculating points on the line between them, using a "t-value" from 0 to 1
 * representing how far between the input points to calculate.
 * 
 * The linear interpolation of two points is typically written like this:
 *
 * double lerp(double p1, double p2, double t) {
 *     return ((1 - t) * p1) + (t * p2);
 * }
 * 
 * - The (1 - t) * p1 will evaluate to p1 at t=0 or evaluate to 0 when t=1.
 * - The t * p2 will evalutate to 0 when t=0 or evalutate to p2 when t=1.
 * - For some value of t between 0 and 1, the returned value will lie on the
 *   line segment between p1 and p2.
 * 
 * After reading to this point and watching "The Continuity of Splines",
 * you should hopefully have a good idea about how interpolation works. Now,
 * lets introduce two twists that we'll use in this library to help us control
 * robots:
 * 
 * - We can generalize this to include more points than just p1 and p2, and
 * - We can use points in any arbitrary number space, meaning we can effectively
 *   apply this strategy to classes with robot control variables (such as
 *   swerve speeds, rotation, arm positions, etc).
 * 
 * # Generalizing with More Points
 * 
 * The key observation needed for generalizing interpolation into blending is
 * that we can relabel our coefficients t and (1 - t) as t1 and t2 respectively.
 * This way, we don't have two coefficients tied together under one variable, we
 * just say we have new and independent variables for each coefficient we need.
 * 
 * We can create a list of control points (p1, p2, p3, ...), then match them with
 * a list of coefficients (t1, t2, t3, ...). When any coefficient is zero, it
 * cancels out the corresponding control point, and when a coefficient is one, it
 * gives the corresponding control point full representation.
 * 
 * The formula for blending an arbitrary set of control points given a corresponding
 * set of coefficients is the following:
 * 
 * output = (t1 * p1) + (t2 * p2) + (t3 * p3) + ...
 * 
 * Note that using high coefficients (over 1) can scale the control points beyond their
 * original range.
 * 
 * # Using Arbitrary Number Spaces
 * 
 * When I say "number space", what I really mean is "how many numbers are we using
 * to control the system". For example, a 2D vector can be represented with two
 * decimal numbers (doubles) and can be drawn on a 2D coordinate plane. A 3D vector
 * can be represented with three doubles and drawn on a 3D number space. We can
 * actually continue adding numbers, it just becomes hard to visualize them
 * geometrically. It may be more useful to think about the operations that we need
 * to apply to our control points in order to allow us to blend them together with
 * the formula from the last section:
 * 
 * output = (t1 * p1) + (t2 * p2) + (t3 * p3) + ...
 * 
 * - We need to be able to add two points in the same space together, and
 * - We need to be able to multiply two points in the same space together
 * 
 * For the purposes of this library, coefficients use the same type as the control
 * points that they scale. All operations (add, multiply) need to be defined
 * field-by-field. So for a 2D vector, addition of p1 and p2 expands to
 * p1.x + p2.x and p1.y + p2.y. For types with more fields, define the operations
 * between the respective fields.
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
