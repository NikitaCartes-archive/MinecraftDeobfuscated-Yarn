package net.minecraft.util.math;

/**
 * Contains color-related helper methods.
 */
public class ColorHelper {
	/**
	 * Contains color-related helper methods that use ARGB colors represented
	 * as {@code 0xAARRGGBB}.
	 */
	public static class Argb {
		/**
		 * {@return the alpha value of {@code argb}}
		 * 
		 * <p>The returned value is between {@code 0} and {@code 255} (both inclusive).
		 */
		public static int getAlpha(int argb) {
			return argb >>> 24;
		}

		/**
		 * {@return the red value of {@code argb}}
		 * 
		 * <p>The returned value is between {@code 0} and {@code 255} (both inclusive).
		 */
		public static int getRed(int argb) {
			return argb >> 16 & 0xFF;
		}

		/**
		 * {@return the green value of {@code argb}}
		 * 
		 * <p>The returned value is between {@code 0} and {@code 255} (both inclusive).
		 */
		public static int getGreen(int argb) {
			return argb >> 8 & 0xFF;
		}

		/**
		 * {@return the blue value of {@code argb}}
		 * 
		 * <p>The returned value is between {@code 0} and {@code 255} (both inclusive).
		 */
		public static int getBlue(int argb) {
			return argb & 0xFF;
		}

		/**
		 * {@return the ARGB color value from its components}
		 */
		public static int getArgb(int alpha, int red, int green, int blue) {
			return alpha << 24 | red << 16 | green << 8 | blue;
		}

		public static int mixColor(int first, int second) {
			return getArgb(
				getAlpha(first) * getAlpha(second) / 255,
				getRed(first) * getRed(second) / 255,
				getGreen(first) * getGreen(second) / 255,
				getBlue(first) * getBlue(second) / 255
			);
		}
	}
}
