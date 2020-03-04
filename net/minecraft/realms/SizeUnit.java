/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Moved from RealmsUploadScreen.Unit in 20w10a.
 */
@Environment(value=EnvType.CLIENT)
public enum SizeUnit {
    B,
    KB,
    MB,
    GB;


    public static SizeUnit getLargestUnit(long bytes) {
        if (bytes < 1024L) {
            return B;
        }
        try {
            int i = (int)(Math.log(bytes) / Math.log(1024.0));
            String string = String.valueOf("KMGTPE".charAt(i - 1));
            return SizeUnit.valueOf(string + "B");
        } catch (Exception exception) {
            return GB;
        }
    }

    public static double convertToUnit(long bytes, SizeUnit unit) {
        if (unit == B) {
            return bytes;
        }
        return (double)bytes / Math.pow(1024.0, unit.ordinal());
    }

    public static String getUserFriendlyString(long bytes) {
        int i = 1024;
        if (bytes < 1024L) {
            return bytes + " B";
        }
        int j = (int)(Math.log(bytes) / Math.log(1024.0));
        String string = "KMGTPE".charAt(j - 1) + "";
        return String.format(Locale.ROOT, "%.1f %sB", (double)bytes / Math.pow(1024.0, j), string);
    }

    public static String humanReadableSize(long bytes, SizeUnit unit) {
        return String.format("%." + (unit == GB ? "1" : "0") + "f %s", SizeUnit.convertToUnit(bytes, unit), unit.name());
    }
}

