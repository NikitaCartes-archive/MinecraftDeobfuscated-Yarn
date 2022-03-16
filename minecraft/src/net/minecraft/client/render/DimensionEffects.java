package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

@Environment(EnvType.CLIENT)
public abstract class DimensionEffects {
	private static final Object2ObjectMap<Identifier, DimensionEffects> BY_IDENTIFIER = Util.make(new Object2ObjectArrayMap<>(), map -> {
		DimensionEffects.Overworld overworld = new DimensionEffects.Overworld();
		map.defaultReturnValue(overworld);
		map.put(DimensionTypes.OVERWORLD_ID, overworld);
		map.put(DimensionTypes.THE_NETHER_ID, new DimensionEffects.Nether());
		map.put(DimensionTypes.THE_END_ID, new DimensionEffects.End());
	});
	private final float[] rgba = new float[4];
	private final float cloudsHeight;
	private final boolean alternateSkyColor;
	private final DimensionEffects.SkyType skyType;
	private final boolean brightenLighting;
	private final boolean darkened;

	public DimensionEffects(float cloudsHeight, boolean alternateSkyColor, DimensionEffects.SkyType skyType, boolean brightenLighting, boolean darkened) {
		this.cloudsHeight = cloudsHeight;
		this.alternateSkyColor = alternateSkyColor;
		this.skyType = skyType;
		this.brightenLighting = brightenLighting;
		this.darkened = darkened;
	}

	public static DimensionEffects byDimensionType(DimensionType dimensionType) {
		return BY_IDENTIFIER.get(dimensionType.getEffects());
	}

	/**
	 * {@return an RGBA fog color override based on the current sky angle, or {@code null} if fog color should not be overridden}
	 * This is used in vanilla to render sunset and sunrise fog.
	 */
	@Nullable
	public float[] getFogColorOverride(float skyAngle, float tickDelta) {
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

	/**
	 * Transforms the given fog color based on the current height of the sun. This is used in vanilla to darken
	 * fog during night.
	 */
	public abstract Vec3d adjustFogColor(Vec3d color, float sunHeight);

	public abstract boolean useThickFog(int camX, int camY);

	public DimensionEffects.SkyType getSkyType() {
		return this.skyType;
	}

	public boolean shouldBrightenLighting() {
		return this.brightenLighting;
	}

	public boolean isDarkened() {
		return this.darkened;
	}

	@Environment(EnvType.CLIENT)
	public static class End extends DimensionEffects {
		public End() {
			super(Float.NaN, false, DimensionEffects.SkyType.END, true, false);
		}

		@Override
		public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
			return color.multiply(0.15F);
		}

		@Override
		public boolean useThickFog(int camX, int camY) {
			return false;
		}

		@Nullable
		@Override
		public float[] getFogColorOverride(float skyAngle, float tickDelta) {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Nether extends DimensionEffects {
		public Nether() {
			super(Float.NaN, true, DimensionEffects.SkyType.NONE, false, true);
		}

		@Override
		public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
			return color;
		}

		@Override
		public boolean useThickFog(int camX, int camY) {
			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Overworld extends DimensionEffects {
		public static final int CLOUDS_HEIGHT = 192;

		public Overworld() {
			super(192.0F, true, DimensionEffects.SkyType.NORMAL, false, false);
		}

		@Override
		public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
			return color.multiply((double)(sunHeight * 0.94F + 0.06F), (double)(sunHeight * 0.94F + 0.06F), (double)(sunHeight * 0.91F + 0.09F));
		}

		@Override
		public boolean useThickFog(int camX, int camY) {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum SkyType {
		/**
		 * Signals the renderer not to render a sky.
		 */
		NONE,
		/**
		 * Signals the renderer to render a normal sky (as in the vanilla Overworld).
		 */
		NORMAL,
		/**
		 * Signals the renderer to draw the end sky box over the sky (as in the vanilla End).
		 */
		END;
	}
}
