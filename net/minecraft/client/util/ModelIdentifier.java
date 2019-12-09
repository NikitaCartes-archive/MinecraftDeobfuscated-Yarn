/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ModelIdentifier
extends Identifier {
    private final String variant;

    protected ModelIdentifier(String[] strings) {
        super(strings);
        this.variant = strings[2].toLowerCase(Locale.ROOT);
    }

    public ModelIdentifier(String string) {
        this(ModelIdentifier.split(string));
    }

    public ModelIdentifier(Identifier id, String variant) {
        this(id.toString(), variant);
    }

    public ModelIdentifier(String string, String string2) {
        this(ModelIdentifier.split(string + '#' + string2));
    }

    protected static String[] split(String id) {
        String[] strings = new String[]{null, id, ""};
        int i = id.indexOf(35);
        String string = id;
        if (i >= 0) {
            strings[2] = id.substring(i + 1, id.length());
            if (i > 1) {
                string = id.substring(0, i);
            }
        }
        System.arraycopy(Identifier.split(string, ':'), 0, strings, 0, 2);
        return strings;
    }

    public String getVariant() {
        return this.variant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ModelIdentifier && super.equals(o)) {
            ModelIdentifier modelIdentifier = (ModelIdentifier)o;
            return this.variant.equals(modelIdentifier.variant);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.variant.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + '#' + this.variant;
    }
}

