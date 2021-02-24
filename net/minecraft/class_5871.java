/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.class_5872;
import net.minecraft.world.gen.ProbabilityConfig;

public class class_5871
extends ProbabilityConfig {
    public static final Codec<class_5871> field_29054 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).forGetter(arg -> Float.valueOf(arg.probability)), class_5872.field_29056.optionalFieldOf("debug_settings", class_5872.field_29055).forGetter(class_5871::method_33969)).apply((Applicative<class_5871, ?>)instance, class_5871::new));
    private final class_5872 field_29053;

    public class_5871(float f, class_5872 arg) {
        super(f);
        this.field_29053 = arg;
    }

    public class_5871(float f) {
        this(f, class_5872.field_29055);
    }

    public class_5872 method_33969() {
        return this.field_29053;
    }
}

