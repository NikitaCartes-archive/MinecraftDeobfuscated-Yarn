/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class class_5313 {
    public static final Codec<class_5313> field_24913 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("distance")).forGetter(class_5313::method_28799), ((MapCodec)Codec.INT.fieldOf("spread")).forGetter(class_5313::method_28801), ((MapCodec)Codec.INT.fieldOf("count")).forGetter(class_5313::method_28802)).apply((Applicative<class_5313, ?>)instance, class_5313::new));
    private final int field_24914;
    private final int field_24915;
    private final int field_24916;

    public class_5313(int i, int j, int k) {
        this.field_24914 = i;
        this.field_24915 = j;
        this.field_24916 = k;
    }

    public int method_28799() {
        return this.field_24914;
    }

    public int method_28801() {
        return this.field_24915;
    }

    public int method_28802() {
        return this.field_24916;
    }
}

