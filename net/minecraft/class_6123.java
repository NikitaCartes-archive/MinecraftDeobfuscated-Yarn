/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import net.minecraft.class_6120;
import net.minecraft.class_6121;
import net.minecraft.class_6122;
import net.minecraft.class_6124;
import net.minecraft.util.registry.Registry;

public interface class_6123<P extends class_6122> {
    public static final class_6123<class_6121> field_31541 = class_6123.method_35394("constant", class_6121.field_31537);
    public static final class_6123<class_6124> field_31542 = class_6123.method_35394("uniform", class_6124.field_31544);
    public static final class_6123<class_6120> field_31543 = class_6123.method_35394("biased_to_bottom", class_6120.field_31531);

    public Codec<P> codec();

    public static <P extends class_6122> class_6123<P> method_35394(String string, Codec<P> codec) {
        return Registry.register(Registry.HEIGHT_PROVIDER_TYPE, string, () -> codec);
    }
}

