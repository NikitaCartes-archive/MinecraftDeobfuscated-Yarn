/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class class_5359 {
    public static final class_5359 field_25393 = new class_5359(ImmutableList.of("vanilla"), ImmutableList.of());
    public static final Codec<class_5359> field_25394 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.STRING.listOf().fieldOf("Enabled")).forGetter(arg -> arg.field_25395), ((MapCodec)Codec.STRING.listOf().fieldOf("Disabled")).forGetter(arg -> arg.field_25396)).apply((Applicative<class_5359, ?>)instance, class_5359::new));
    private final List<String> field_25395;
    private final List<String> field_25396;

    public class_5359(List<String> list, List<String> list2) {
        this.field_25395 = ImmutableList.copyOf(list);
        this.field_25396 = ImmutableList.copyOf(list2);
    }

    public List<String> method_29547() {
        return this.field_25395;
    }

    public List<String> method_29550() {
        return this.field_25396;
    }
}

