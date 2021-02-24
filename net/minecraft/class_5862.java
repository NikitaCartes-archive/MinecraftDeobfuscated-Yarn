/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.class_5863;
import net.minecraft.class_5864;

public class class_5862
extends class_5863 {
    public static class_5862 field_29003 = class_5862.method_33908(0.0f);
    public static final Codec<class_5862> field_29004 = Codec.either(Codec.FLOAT, RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.FLOAT.fieldOf("value")).forGetter(arg -> Float.valueOf(arg.field_29005))).apply((Applicative<class_5862, ?>)instance, class_5862::new))).xmap(either -> either.map(class_5862::method_33908, arg -> arg), arg -> Either.left(Float.valueOf(arg.field_29005)));
    private float field_29005;

    public static class_5862 method_33908(float f) {
        if (f == 0.0f) {
            return field_29003;
        }
        return new class_5862(f);
    }

    private class_5862(float f) {
        this.field_29005 = f;
    }

    public float method_33914() {
        return this.field_29005;
    }

    @Override
    public float method_33920(Random random) {
        return this.field_29005;
    }

    @Override
    public float method_33915() {
        return this.field_29005;
    }

    @Override
    public float method_33921() {
        return this.field_29005 + 1.0f;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        class_5862 lv = (class_5862)object;
        return this.field_29005 == lv.field_29005;
    }

    public int hashCode() {
        return Float.hashCode(this.field_29005);
    }

    @Override
    public class_5864<?> method_33923() {
        return class_5864.field_29008;
    }

    public String toString() {
        return Float.toString(this.field_29005);
    }
}

