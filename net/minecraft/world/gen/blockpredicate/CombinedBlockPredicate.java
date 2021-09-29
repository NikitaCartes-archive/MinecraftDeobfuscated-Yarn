/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;

abstract class CombinedBlockPredicate
implements BlockPredicate {
    protected final List<BlockPredicate> predicates;

    protected CombinedBlockPredicate(List<BlockPredicate> predicates) {
        this.predicates = predicates;
    }

    public static <T extends CombinedBlockPredicate> Codec<T> buildCodec(Function<List<BlockPredicate>, T> function) {
        return RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockPredicate.BASE_CODEC.listOf().fieldOf("predicates")).forGetter(combinedBlockPredicate -> combinedBlockPredicate.predicates)).apply((Applicative<CombinedBlockPredicate, ?>)instance, function));
    }
}

