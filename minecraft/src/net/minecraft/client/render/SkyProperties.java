package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public abstract class SkyProperties {
	private static final Object2ObjectMap<RegistryKey<DimensionType>, SkyProperties> BY_DIMENSION_TYPE = Util.make(
		new Object2ObjectArrayMap<>(), object2ObjectArrayMap -> {
			SkyProperties.Overworld overworld = new SkyProperties.Overworld();
			object2ObjectArrayMap.defaultReturnValue(overworld);
			object2ObjectArrayMap.put(DimensionType.OVERWORLD_REGISTRY_KEY, overworld);
			object2ObjectArrayMap.put(DimensionType.THE_NETHER_REGISTRY_KEY, new SkyProperties.Nether());
			object2ObjectArrayMap.put(DimensionType.THE_END_REGISTRY_KEY, new SkyProperties.End());
		}
	);
	private final float[] rgba = new float[4];
	private final float cloudsHeight;
	private final boolean alternateSkyColor;
	private final boolean shouldRenderSky;

	public SkyProperties(float cloudsHeight, boolean alternateSkyColor, boolean shouldRenderSky) {
		this.cloudsHeight = cloudsHeight;
		this.alternateSkyColor = alternateSkyColor;
		this.shouldRenderSky = shouldRenderSky;
	}

	public static SkyProperties byDimensionType(Optional<RegistryKey<DimensionType>> optional) {
		return BY_DIMENSION_TYPE.get(optional.orElse(DimensionType.OVERWORLD_REGISTRY_KEY));
	}

	@Nullable
	public float[] getSkyColor(float skyAngle, float tickDelta) {
		float f = 0.4F;
		float g = MathHelper.cos(skyAngle * (float) (Math.PI * 2)) - 0.0F;
		float h = -0.0F;
		if (g >= -0.4F && g <= 0.4F) {
			float i = (g - -0.0F) / 0.4F * 0.5F + 0.5F;
			float j = 1.0F - (1.0F - MathHelper.sin(i * (float) Math.PI)) * 0.99F;
			j *= j;
			this.rgba[0] = i * 0.3F + 0.7F;
			this.rgba[1] = i * i * 0.7F + 0.2F;
			this.rgba[2] = i * i * 0.0F + 0.2F;
			this.rgba[3] = j;
			return this.rgba;
		} else {
			return null;
		}
	}

	public float getCloudsHeight() {
		return this.cloudsHeight;
	}

	public boolean isAlternateSkyColor() {
		return this.alternateSkyColor;
	}

	public abstract Vec3d adjustSkyColor(Vec3d color, float sunHeight);

	public abstract boolean useThickFog(int camX, int camY);

	public boolean shouldRenderSky() {
		return this.shouldRenderSky;
	}

	@Environment(EnvType.CLIENT)
	public static class End extends SkyProperties {
		public End() {
			super(Float.NaN, false, false);
		}

		@Override
		public Vec3d adjustSkyColor(Vec3d color, float sunHeight) {
			return color.multiply(0.15F);
		}

		@Override
		public boolean useThickFog(int camX, int camY) {
			return false;
		}

		@Nullable
		@Override
		public float[] getSkyColor(float skyAngle, float tickDelta) {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Nether extends SkyProperties {
		public Nether() {
			super(Float.NaN, true, false);
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

	@Environment(EnvType.CLIENT)
	public static class Overworld extends SkyProperties {
		public Overworld() {
			super(128.0F, true, true);
		}

		@Override
		public Vec3d adjustSkyColor(Vec3d color, float sunHeight) {
			return color.multiply((double)(sunHeight * 0.94F + 0.06F), (double)(sunHeight * 0.94F + 0.06F), (double)(sunHeight * 0.91F + 0.09F));
		}

		@Override
		public boolean useThickFog(int camX, int camY) {
			return false;
		}
	}
}
