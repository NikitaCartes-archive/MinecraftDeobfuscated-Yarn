/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.heightprovider;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProviderType;

public class WeightedListHeightProvider
extends HeightProvider {
    public static final Codec<WeightedListHeightProvider> WEIGHTED_LIST_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)DataPool.createCodec(HeightProvider.CODEC).fieldOf("distribution")).forGetter(weightedListHeightProvider -> weightedListHeightProvider.weightedList)).apply((Applicative<WeightedListHeightProvider, ?>)instance, WeightedListHeightProvider::new));
    private final DataPool<HeightProvider> weightedList;

    public WeightedListHeightProvider(DataPool<HeightProvider> weightedList) {
        this.weightedList = weightedList;
    }

    @Override
    public int get(AbstractRandom abstractRandom, HeightContext context) {
        return this.weightedList.getDataOrEmpty(abstractRandom).orElseThrow(IllegalStateException::new).get(abstractRandom, context);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.WEIGHTED_LIST;
    }
}

