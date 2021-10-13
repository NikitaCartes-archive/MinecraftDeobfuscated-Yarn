/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.function.Supplier;
import org.apache.commons.lang3.ObjectUtils;

public record ModStatus(Confidence confidence, String description) {
    /**
     * {@return the modification status determined by the brand and whether the
     * class is signed or not}
     */
    public static ModStatus check(String vanillaBrand, Supplier<String> brandSupplier, String environment, Class<?> clazz) {
        String string = brandSupplier.get();
        if (!vanillaBrand.equals(string)) {
            return new ModStatus(Confidence.DEFINITELY, environment + " brand changed to '" + string + "'");
        }
        if (clazz.getSigners() == null) {
            return new ModStatus(Confidence.VERY_LIKELY, environment + " jar signature invalidated");
        }
        return new ModStatus(Confidence.PROBABLY_NOT, environment + " jar signature and brand is untouched");
    }

    public boolean isModded() {
        return this.confidence.modded;
    }

    /**
     * {@return the combined modification status with the higher confidence}
     * 
     * @apiNote This is used to check if either the client or the integrated server is modded.
     */
    public ModStatus combine(ModStatus brand) {
        return new ModStatus((Confidence)((Object)ObjectUtils.max((Comparable[])new Confidence[]{this.confidence, brand.confidence})), this.description + "; " + brand.description);
    }

    public String getMessage() {
        return this.confidence.description + " " + this.description;
    }

    public static enum Confidence {
        PROBABLY_NOT("Probably not.", false),
        VERY_LIKELY("Very likely;", true),
        DEFINITELY("Definitely;", true);

        final String description;
        final boolean modded;

        private Confidence(String description, boolean modded) {
            this.description = description;
            this.modded = modded;
        }
    }
}

