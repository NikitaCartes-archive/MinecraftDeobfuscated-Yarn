package net.minecraft.util.math;

import net.minecraft.util.Colors;

/**
 * Contains color-related helper methods that mostly use ARGB colors represented as {@code 0xAARRGGBB}.
 */
public class ColorHelper {
	/**
	 * @return the alpha value of {@code argb}
	 * 
	 * <p>The returned value is between {@code 0} and {@code 255} (both inclusive).
	 */
	public static int getAlpha(int argb) {
		return argb >>> 24;
	}

	/**
	 * @return the red value of {@code argb}
	 * 
	 * <p>The returned value is between {@code 0} and {@code 255} (both inclusive).
	 */
	public static int getRed(int argb) {
		return argb >> 16 & 0xFF;
	}

	/**
	 * @return the green value of {@code argb}
	 * 
	 * <p>The returned value is between {@code 0} and {@code 255} (both inclusive).
	 */
	public static int getGreen(int argb) {
		return argb >> 8 & 0xFF;
	}

	/**
	 * @return the blue value of {@code argb}
	 * 
	 * <p>The returned value is between {@code 0} and {@code 255} (both inclusive).
	 */
	public static int getBlue(int argb) {
		return argb & 0xFF;
	}

	/**
	 * @return the ARGB color value from its components
	 */
	public static int getArgb(int alpha, int red, int green, int blue) {
		return alpha << 24 | red << 16 | green << 8 | blue;
	}

	/**
	 * @return the full alpha ARGB color value from its components
	 */
	public static int getArgb(int red, int green, int blue) {
		return getArgb(255, red, green, blue);
	}

	public static int getArgb(Vec3d rgb) {
		return getArgb(channelFromFloat((float)rgb.getX()), channelFromFloat((float)rgb.getY()), channelFromFloat((float)rgb.getZ()));
	}

	public static int mix(int first, int second) {
		if (first == Colors.WHITE) {
			return second;
		} else {
			return second == Colors.WHITE
				? first
				: getArgb(
					getAlpha(first) * getAlpha(second) / 255,
					getRed(first) * getRed(second) / 255,
					getGreen(first) * getGreen(second) / 255,
					getBlue(first) * getBlue(second) / 255
				);
		}
	}

	public static int scaleRgb(int argb, float scale) {
		return scaleRgb(argb, scale, scale, scale);
	}

	public static int scaleRgb(int argb, float redScale, float greenScale, float blueScale) {
		return getArgb(
			getAlpha(argb),
			Math.clamp((long)((int)((float)getRed(argb) * redScale)), 0, 255),
			Math.clamp((long)((int)((float)getGreen(argb) * greenScale)), 0, 255),
			Math.clamp((long)((int)((float)getBlue(argb) * blueScale)), 0, 255)
		);
	}

	public static int scaleRgb(int argb, int scale) {
		return getArgb(
			getAlpha(argb),
			Math.clamp((long)getRed(argb) * (long)scale / 255L, 0, 255),
			Math.clamp((long)getGreen(argb) * (long)scale / 255L, 0, 255),
			Math.clamp((long)getBlue(argb) * (long)scale / 255L, 0, 255)
		);
	}

	public static int grayscale(int argb) {
		int i = (int)((float)getRed(argb) * 0.3F + (float)getGreen(argb) * 0.59F + (float)getBlue(argb) * 0.11F);
		return getArgb(i, i, i);
	}

	public static int lerp(float delta, int start, int end) {
		int i = MathHelper.lerp(delta, getAlpha(start), getAlpha(end));
		int j = MathHelper.lerp(delta, getRed(start), getRed(end));
		int k = MathHelper.lerp(delta, getGreen(start), getGreen(end));
		int l = MathHelper.lerp(delta, getBlue(start), getBlue(end));
		return getArgb(i, j, k, l);
	}

	public static int fullAlpha(int argb) {
		return argb | Colors.BLACK;
	}

	public static int zeroAlpha(int argb) {
		return argb & 16777215;
	}

	public static int withAlpha(int alpha, int rgb) {
		return alpha << 24 | rgb & 16777215;
	}

	public static int getWhite(float alpha) {
		return channelFromFloat(alpha) << 24 | 16777215;
	}

	public static int fromFloats(float alpha, float red, float green, float blue) {
		return getArgb(channelFromFloat(alpha), channelFromFloat(red), channelFromFloat(green), channelFromFloat(blue));
	}

	public static int average(int first, int second) {
		return getArgb(
			(getAlpha(first) + getAlpha(second)) / 2,
			(getRed(first) + getRed(second)) / 2,
			(getGreen(first) + getGreen(second)) / 2,
			(getBlue(first) + getBlue(second)) / 2
		);
	}

	public static int channelFromFloat(float value) {
		return MathHelper.floor(value * 255.0F);
	}

	public static float floatFromChannel(int channel) {
		return (float)channel / 255.0F;
	}

	public static int toAbgr(int argb) {
		return argb & Colors.GREEN | (argb & 0xFF0000) >> 16 | (argb & 0xFF) << 16;
	}

	public static int fromAbgr(int abgr) {
		return toAbgr(abgr);
	}
}
