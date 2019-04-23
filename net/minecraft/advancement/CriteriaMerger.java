/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement;

import java.util.Collection;

public interface CriteriaMerger {
    public static final CriteriaMerger AND = collection -> {
        String[][] strings = new String[collection.size()][];
        int i = 0;
        for (String string : collection) {
            strings[i++] = new String[]{string};
        }
        return strings;
    };
    public static final CriteriaMerger OR = collection -> new String[][]{collection.toArray(new String[0])};

    public String[][] createRequirements(Collection<String> var1);
}

