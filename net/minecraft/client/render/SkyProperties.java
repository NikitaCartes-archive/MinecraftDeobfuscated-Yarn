/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class SkyProperties {
    private static final Object2ObjectMap<RegistryKey<DimensionType>, SkyProperties> BY_DIMENSION_TYPE = Util.make(new Object2ObjectArrayMap(), object2ObjectArrayMap -> {
        Overworld overworld = new Overworld();
        object2ObjectArrayMap.defaultReturnValue(overworld);
        object2ObjectArrayMap.put(DimensionType.OVERWORLD_REGISTRY_KEY, overworld);
        object2ObjectArrayMap.put(DimensionType.THE_NETHER_REGISTRY_KEY, new Nether());
        object2ObjectArrayMap.put(DimensionType.THE_END_REGISTRY_KEY, new End());
    });
    private final float[] rgba = new float[4];
    private final float cloudsHeight;
    private final boolean alternateSkyColor;
    private final class_5401 field_25637;
    private final boolean shouldRenderSky;
    private final boolean field_25638;

    public SkyProperties(float cloudsHeight, boolean alternateSkyColor, class_5401 arg, boolean bl, boolean bl2) {
        this.cloudsHeight = cloudsHeight;
        this.alternateSkyColor = alternateSkyColor;
        this.field_25637 = arg;
        this.shouldRenderSky = bl;
        this.field_25638 = bl2;
    }

    public static SkyProperties byDimensionType(Optional<RegistryKey<DimensionType>> optional) {
        return (SkyProperties)BY_DIMENSION_TYPE.get(optional.orElse(DimensionType.OVERWORLD_REGISTRY_KEY));
    }

    @Nullable
    public float[] getSkyColor(float skyAngle, float tickDelta) {
        float f = 0.4f;
        float g = MathHelper.cos(skyAngle * ((float)Math.PI * 2)) - 0.0f;
        float h = -0.0f;
        if (g >= -0.4f && g <= 0.4f) {
            float i = (g - -0.0f) / 0.4f * 0.5f + 0.5f;
            float j = 1.0f - (1.0f - MathHelper.sin(i * (float)Math.PI)) * 0.99f;
            j *= j;
            this.rgba[0] = i * 0.3f + 0.7f;
            this.rgba[1] = i * i * 0.7f + 0.2f;
            this.rgba[2] = i * i * 0.0f + 0.2f;
            this.rgba[3] = j;
            return this.rgba;
        }
        return null;
    }

    public float getCloudsHeight() {
        return this.cloudsHeight;
    }

    public boolean isAlternateSkyColor() {
        return this.alternateSkyColor;
    }

    public abstract Vec3d adjustSkyColor(Vec3d var1, float var2);

    public abstract boolean useThickFog(int var1, int var2);

    public class_5401 method_29992() {
        return this.field_25637;
    }

    public boolean shouldRenderSky() {
        return this.shouldRenderSky;
    }

    public boolean method_29993() {
        return this.field_25638;
    }

    @Environment(value=EnvType.CLIENT)
    public static class End
    extends SkyProperties {
        public End() {
            super(Float.NaN, false, class_5401.field_25641, true, false);
        }

        @Override
        public Vec3d adjustSkyColor(Vec3d color, float sunHeight) {
            return color.multiply(0.15f);
        }

        @Override
        public boolean useThickFog(int camX, int camY) {
            return false;
        }

        @Override
        @Nullable
        public float[] getSkyColor(float skyAngle, float tickDelta) {
            return null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Overworld
    extends SkyProperties {
        public Overworld() {
            super(128.0f, true, class_5401.field_25640, false, false);
        }

        @Override
        public Vec3d adjustSkyColor(Vec3d color, float sunHeight) {
            return color.multiply(sunHeight * 0.94f + 0.06f, sunHeight * 0.94f + 0.06f, sunHeight * 0.91f + 0.09f);
        }

        @Override
        public boolean useThickFog(int camX, int camY) {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Nether
    extends SkyProperties {
        public Nether() {
            super(Float.NaN, true, class_5401.field_25639, false, true);
        }

        @Override
        public Vec3d adjustSkyColor(Vec3d color, float sunHeight) {
            return color;
        }

        @Override
        public boolean useThickFog(int camX, int camY) {
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum class_5401 {
        field_25639,
        field_25640,
        field_25641;

    }
}

