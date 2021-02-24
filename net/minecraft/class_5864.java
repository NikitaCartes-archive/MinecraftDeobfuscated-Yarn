/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import net.minecraft.class_5861;
import net.minecraft.class_5862;
import net.minecraft.class_5863;
import net.minecraft.class_5865;
import net.minecraft.class_5866;
import net.minecraft.util.registry.Registry;

public interface class_5864<P extends class_5863> {
    public static final class_5864<class_5862> field_29008 = class_5864.method_33925("constant", class_5862.field_29004);
    public static final class_5864<class_5866> field_29009 = class_5864.method_33925("uniform", class_5866.field_29016);
    public static final class_5864<class_5861> field_29010 = class_5864.method_33925("clamped_normal", class_5861.field_28998);
    public static final class_5864<class_5865> field_29011 = class_5864.method_33925("trapezoid", class_5865.field_29012);

    public Codec<P> codec();

    public static <P extends class_5863> class_5864<P> method_33925(String string, Codec<P> codec) {
        return Registry.register(Registry.field_29076, string, () -> codec);
    }
}

