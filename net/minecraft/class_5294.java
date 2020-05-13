/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class class_5294 {
    private static final Map<DimensionType, class_5294> field_24609 = Maps.newHashMap();
    private final float[] field_24610 = new float[4];
    private final float field_24611;
    private final boolean field_24612;
    private final boolean field_24613;

    public class_5294(float f, boolean bl, boolean bl2) {
        this.field_24611 = f;
        this.field_24612 = bl;
        this.field_24613 = bl2;
    }

    public static class_5294 method_28111(DimensionType dimensionType) {
        return field_24609.getOrDefault(dimensionType, field_24609.get(DimensionType.OVERWORLD));
    }

    @Nullable
    public float[] method_28109(float f, float g) {
        float h = 0.4f;
        float i = MathHelper.cos(f * ((float)Math.PI * 2)) - 0.0f;
        float j = -0.0f;
        if (i >= -0.4f && i <= 0.4f) {
            float k = (i - -0.0f) / 0.4f * 0.5f + 0.5f;
            float l = 1.0f - (1.0f - MathHelper.sin(k * (float)Math.PI)) * 0.99f;
            l *= l;
            this.field_24610[0] = k * 0.3f + 0.7f;
            this.field_24610[1] = k * k * 0.7f + 0.2f;
            this.field_24610[2] = k * k * 0.0f + 0.2f;
            this.field_24610[3] = l;
            return this.field_24610;
        }
        return null;
    }

    public float method_28108() {
        return this.field_24611;
    }

    public boolean method_28113() {
        return this.field_24612;
    }

    public abstract Vec3d method_28112(Vec3d var1, float var2);

    public abstract boolean method_28110(int var1, int var2);

    public boolean method_28114() {
        return this.field_24613;
    }

    static {
        field_24609.put(DimensionType.OVERWORLD, new class_5297());
        field_24609.put(DimensionType.THE_NETHER, new class_5296());
        field_24609.put(DimensionType.THE_END, new class_5295());
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_5295
    extends class_5294 {
        public class_5295() {
            super(Float.NaN, false, false);
        }

        @Override
        public Vec3d method_28112(Vec3d vec3d, float f) {
            return vec3d.multiply(0.15f);
        }

        @Override
        public boolean method_28110(int i, int j) {
            return false;
        }

        @Override
        @Nullable
        public float[] method_28109(float f, float g) {
            return null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_5297
    extends class_5294 {
        public class_5297() {
            super(128.0f, true, true);
        }

        @Override
        public Vec3d method_28112(Vec3d vec3d, float f) {
            return vec3d.multiply(f * 0.94f + 0.06f, f * 0.94f + 0.06f, f * 0.91f + 0.09f);
        }

        @Override
        public boolean method_28110(int i, int j) {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_5296
    extends class_5294 {
        public class_5296() {
            super(Float.NaN, true, false);
        }

        @Override
        public Vec3d method_28112(Vec3d vec3d, float f) {
            return vec3d;
        }

        @Override
        public boolean method_28110(int i, int j) {
            return true;
        }
    }
}

