/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.root;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record MangroveRootPlacement(RegistryEntryList<Block> canGrowThrough, RegistryEntryList<Block> muddyRootsIn, BlockStateProvider muddyRootsProvider, int maxRootWidth, int maxRootLength, float randomSkewChance) {
    public static final Codec<MangroveRootPlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("can_grow_through")).forGetter(rootPlacement -> rootPlacement.canGrowThrough), ((MapCodec)RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("muddy_roots_in")).forGetter(rootPlacement -> rootPlacement.muddyRootsIn), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("muddy_roots_provider")).forGetter(rootPlacement -> rootPlacement.muddyRootsProvider), ((MapCodec)Codec.intRange(1, 12).fieldOf("max_root_width")).forGetter(rootPlacement -> rootPlacement.maxRootWidth), ((MapCodec)Codec.intRange(1, 64).fieldOf("max_root_length")).forGetter(rootPlacement -> rootPlacement.maxRootLength), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("random_skew_chance")).forGetter(rootPlacement -> Float.valueOf(rootPlacement.randomSkewChance))).apply((Applicative<MangroveRootPlacement, ?>)instance, MangroveRootPlacement::new));
}

