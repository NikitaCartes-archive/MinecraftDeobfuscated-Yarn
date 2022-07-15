/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.language;

import com.mojang.bridge.game.Language;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class LanguageDefinition
implements Language,
Comparable<LanguageDefinition> {
    private final String code;
    private final String region;
    private final String name;
    private final boolean rightToLeft;

    public LanguageDefinition(String code, String region, String name, boolean rightToLeft) {
        this.code = code;
        this.region = region;
        this.name = name;
        this.rightToLeft = rightToLeft;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getRegion() {
        return this.region;
    }

    public boolean isRightToLeft() {
        return this.rightToLeft;
    }

    public String toString() {
        return String.format(Locale.ROOT, "%s (%s)", this.name, this.region);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LanguageDefinition)) {
            return false;
        }
        return this.code.equals(((LanguageDefinition)o).code);
    }

    public int hashCode() {
        return this.code.hashCode();
    }

    @Override
    public int compareTo(LanguageDefinition languageDefinition) {
        return this.code.compareTo(languageDefinition.code);
    }

    @Override
    public /* synthetic */ int compareTo(Object other) {
        return this.compareTo((LanguageDefinition)other);
    }
}

