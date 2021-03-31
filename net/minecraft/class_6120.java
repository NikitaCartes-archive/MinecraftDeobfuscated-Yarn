/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_6122;
import net.minecraft.class_6123;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_6120
extends class_6122 {
    public static final Codec<class_6120> field_31531 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)YOffset.OFFSET_CODEC.fieldOf("min_inclusive")).forGetter(arg -> arg.field_31533), ((MapCodec)YOffset.OFFSET_CODEC.fieldOf("max_inclusive")).forGetter(arg -> arg.field_31534), Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("inner", 1).forGetter(arg -> arg.field_31535)).apply((Applicative<class_6120, ?>)instance, class_6120::new)).comapFlatMap(DataResult::success, Function.identity());
    private static final Logger field_31532 = LogManager.getLogger();
    private final YOffset field_31533;
    private final YOffset field_31534;
    private final int field_31535;

    private class_6120(YOffset yOffset, YOffset yOffset2, int i) {
        this.field_31533 = yOffset;
        this.field_31534 = yOffset2;
        this.field_31535 = i;
    }

    public static class_6120 method_35377(YOffset yOffset, YOffset yOffset2, int i) {
        return new class_6120(yOffset, yOffset2, i);
    }

    @Override
    public int method_35391(Random random, HeightContext heightContext) {
        int i = this.field_31533.getY(heightContext);
        int j = this.field_31534.getY(heightContext);
        if (j - i - this.field_31535 + 1 <= 0) {
            field_31532.warn("Empty height range: {}", (Object)this);
            return i;
        }
        int k = random.nextInt(j - i - this.field_31535 + 1);
        return random.nextInt(k + this.field_31535) + i;
    }

    @Override
    public class_6123<?> method_35388() {
        return class_6123.field_31543;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        class_6120 lv = (class_6120)object;
        return this.field_31533.equals(lv.field_31533) && this.field_31534.equals(this.field_31534) && this.field_31535 == lv.field_31535;
    }

    public int hashCode() {
        return Objects.hash(this.field_31533, this.field_31534);
    }

    public String toString() {
        return "biased[" + this.field_31533 + '-' + this.field_31534 + " inner: " + this.field_31535 + "]";
    }
}

