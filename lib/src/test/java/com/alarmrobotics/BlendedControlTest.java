package com.alarmrobotics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BlendedControlTest {
    static class ControlVector extends Blendable<ControlVector> {
        public final double fieldX;
        public final double fieldY;
        public final double robotX;
        public final double robotY;
        public final double rotation;

        public static ControlVector ZERO = new ControlVector(0, 0, 0, 0, 0);
        public static ControlVector ONE = new ControlVector(1, 1, 1, 1, 1);

        public ControlVector(
            double fieldX,
            double fieldY,
            double robotX,
            double robotY,
            double rotation
        ) {
            this.fieldX = fieldX;
            this.fieldY = fieldY;
            this.robotX = robotX;
            this.robotY = robotY;
            this.rotation = rotation;
        }

        @Override
        public ControlVector lerp(ControlVector other, double t) {
            var fieldX = Blendable.lerp(this.fieldX, other.fieldX, t);
            var fieldY = Blendable.lerp(this.fieldY, other.fieldY, t);
            var robotX = Blendable.lerp(this.robotX, other.robotX, t);
            var robotY = Blendable.lerp(this.robotY, other.robotY, t);
            var rotation = Blendable.lerp(this.rotation, other.rotation, t);

            return new ControlVector(
                fieldX,
                fieldY,
                robotX,
                robotY,
                rotation
            );
        }

        @Override
        public ControlVector add(ControlVector other) {
            var fieldX = this.fieldX + other.fieldX;
            var fieldY = this.fieldY + other.fieldY;
            var robotX = this.robotX + other.robotX;
            var robotY = this.robotY + other.robotY;
            var rotation = this.rotation + other.rotation;

            return new ControlVector(
                fieldX,
                fieldY,
                robotX,
                robotY,
                rotation
            );
        }

        @Override
        public ControlVector mul(ControlVector other) {
            var fieldX = this.fieldX * other.fieldX;
            var fieldY = this.fieldY * other.fieldY;
            var robotX = this.robotX * other.robotX;
            var robotY = this.robotY * other.robotY;
            var rotation = this.rotation * other.rotation;

            return new ControlVector(
                fieldX,
                fieldY,
                robotX,
                robotY,
                rotation
            );
        }
    }

    @Test
    public static void testBlend() {
        var blendedControl = new BlendedControl<ControlVector>();

        blendedControl.addInput(() -> {
            var control = new ControlVector(0.1, 0.2, 0.3, 0.4, 0.5);
            var influence = new ControlVector(0.5, 0.5, 0.5, 0.5, 0.5);
            return new BlendedInput<>(control, influence);
        });

        ControlVector output = blendedControl.calculate().get();
        assertEquals(output.fieldX, 0.05);
        assertEquals(output.fieldY, 0.10);
        assertEquals(output.robotX, 0.15);
        assertEquals(output.robotY, 0.20);
        assertEquals(output.rotation, 0.25);
        assertEquals(true, false);
    }
}
