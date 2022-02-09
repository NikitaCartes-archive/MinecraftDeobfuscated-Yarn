/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;

public class MatchingBlockTagPredicate
extends OffsetPredicate {
    final TagKey<Block> tag;
    public static final Codec<MatchingBlockTagPredicate> CODEC = RecordCodecBuilder.create(instance -> MatchingBlockTagPredicate.registerOffsetField(instance).and(((MapCodec)TagKey.identifierCodec(Registry.BLOCK_KEY).fieldOf("tag")).forGetter(matchingBlockTagPredicate -> matchingBlockTagPredicate.tag)).apply((Applicative<MatchingBlockTagPredicate, ?>)instance, MatchingBlockTagPredicate::new));

    protected MatchingBlockTagPredicate(Vec3i offset, TagKey<Block> tag) {
        super(offset);
        this.tag = tag;
    }

    @Override
    protected boolean test(BlockState state) {
        return state.isIn(this.tag);
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.MATCHING_BLOCK_TAG;
    }
}

