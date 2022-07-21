package net.minecraft.client.realms;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Moved from RealmsUploadScreen.Unit in 20w10a.
 */
@Environment(EnvType.CLIENT)
public enum SizeUnit {
	B,
	KB,
	MB,
	GB;

	private static final int BASE = 1024;

	public static SizeUnit getLargestUnit(long bytes) {
		if (bytes < 1024L) {
			return B;
		} else {
			try {
				int i = (int)(Math.log((double)bytes) / Math.log(1024.0));
				String string = String.valueOf("KMGTPE".charAt(i - 1));
				return valueOf(string + "B");
			} catch (Exception var4) {
				return GB;
			}
		}
	}

	public static double convertToUnit(long bytes, SizeUnit unit) {
		return unit == B ? (double)bytes : (double)bytes / Math.pow(1024.0, (double)unit.ordinal());
	}

	public static String getUserFriendlyString(long bytes) {
		int i = 1024;
		if (bytes < 1024L) {
			return bytes + " B";
		} else {
			int j = (int)(Math.log((double)bytes) / Math.log(1024.0));
			String string = "KMGTPE".charAt(j - 1) + "";
			return String.format(Locale.ROOT, "%.1f %sB", (double)bytes / Math.pow(1024.0, (double)j), string);
		}
	}

	public static String humanReadableSize(long bytes, SizeUnit unit) {
		return String.format(Locale.ROOT, "%." + (unit == GB ? "1" : "0") + "f %s", convertToUnit(bytes, unit), unit.name());
	}
}
