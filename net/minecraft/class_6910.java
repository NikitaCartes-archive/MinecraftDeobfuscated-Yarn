/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.function.Function;
import net.minecraft.class_6916;
import net.minecraft.world.gen.chunk.Blender;

public interface class_6910 {
    public double method_40464(class_6912 var1);

    public void method_40470(double[] var1, class_6911 var2);

    public class_6910 method_40469(class_6915 var1);

    public double minValue();

    public double maxValue();

    default public class_6910 method_40468(double d, double e) {
        return new class_6916.class_6922(this, d, e);
    }

    default public class_6910 method_40471() {
        return class_6916.method_40490(this, class_6916.class_6925.class_6926.ABS);
    }

    default public class_6910 method_40472() {
        return class_6916.method_40490(this, class_6916.class_6925.class_6926.SQUARE);
    }

    default public class_6910 method_40473() {
        return class_6916.method_40490(this, class_6916.class_6925.class_6926.CUBE);
    }

    default public class_6910 method_40474() {
        return class_6916.method_40490(this, class_6916.class_6925.class_6926.HALF_NEGATIVE);
    }

    default public class_6910 method_40475() {
        return class_6916.method_40490(this, class_6916.class_6925.class_6926.QUARTER_NEGATIVE);
    }

    default public class_6910 method_40476() {
        return class_6916.method_40490(this, class_6916.class_6925.class_6926.SQUEEZE);
    }

    public record class_6914(int blockX, int blockY, int blockZ) implements class_6912
    {
    }

    public static interface class_6912 {
        public int blockX();

        public int blockY();

        public int blockZ();

        default public Blender getBlender() {
            return Blender.getNoBlending();
        }
    }

    public static interface class_6913
    extends class_6910 {
        @Override
        default public void method_40470(double[] ds, class_6911 arg) {
            arg.method_40478(ds, this);
        }

        @Override
        default public class_6910 method_40469(class_6915 arg) {
            return (class_6910)arg.apply(this);
        }
    }

    public static interface class_6915
    extends Function<class_6910, class_6910> {
    }

    public static interface class_6911 {
        public class_6912 method_40477(int var1);

        public void method_40478(double[] var1, class_6910 var2);
    }
}

