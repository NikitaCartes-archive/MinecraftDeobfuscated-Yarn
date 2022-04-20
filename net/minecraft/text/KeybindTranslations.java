/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.text.Text;

public class KeybindTranslations {
    static Function<String, Supplier<Text>> FACTORY = string -> () -> Text.literal(string);

    public static void setFactory(Function<String, Supplier<Text>> factory) {
        FACTORY = factory;
    }
}

