/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class BlockFilterDecoratorConfig
implements DecoratorConfig {
    public static final Codec<BlockFilterDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockPredicate.BASE_CODEC.fieldOf("predicate")).forGetter(blockFilterDecoratorConfig -> blockFilterDecoratorConfig.predicate)).apply((Applicative<BlockFilterDecoratorConfig, ?>)instance, BlockFilterDecoratorConfig::new));
    private final BlockPredicate predicate;

    public BlockFilterDecoratorConfig(BlockPredicate predicate) {
        this.predicate = predicate;
    }

    public BlockPredicate getPredicate() {
        return this.predicate;
    }
}

