/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.BlockSetType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public record WoodType(String name, BlockSetType setType, BlockSoundGroup soundType, BlockSoundGroup hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
    private static final Set<WoodType> VALUES = new ObjectArraySet<WoodType>();
    public static final WoodType OAK = WoodType.register(new WoodType("oak", BlockSetType.OAK));
    public static final WoodType SPRUCE = WoodType.register(new WoodType("spruce", BlockSetType.SPRUCE));
    public static final WoodType BIRCH = WoodType.register(new WoodType("birch", BlockSetType.BIRCH));
    public static final WoodType ACACIA = WoodType.register(new WoodType("acacia", BlockSetType.ACACIA));
    public static final WoodType CHERRY = WoodType.register(new WoodType("cherry", BlockSetType.CHERRY, BlockSoundGroup.CHERRY_WOOD, BlockSoundGroup.CHERRY_WOOD_HANGING_SIGN, SoundEvents.BLOCK_CHERRY_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_CHERRY_WOOD_FENCE_GATE_OPEN));
    public static final WoodType JUNGLE = WoodType.register(new WoodType("jungle", BlockSetType.JUNGLE));
    public static final WoodType DARK_OAK = WoodType.register(new WoodType("dark_oak", BlockSetType.DARK_OAK));
    public static final WoodType CRIMSON = WoodType.register(new WoodType("crimson", BlockSetType.CRIMSON, BlockSoundGroup.NETHER_WOOD, BlockSoundGroup.NETHER_WOOD_HANGING_SIGN, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_OPEN));
    public static final WoodType WARPED = WoodType.register(new WoodType("warped", BlockSetType.WARPED, BlockSoundGroup.NETHER_WOOD, BlockSoundGroup.NETHER_WOOD_HANGING_SIGN, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_OPEN));
    public static final WoodType MANGROVE = WoodType.register(new WoodType("mangrove", BlockSetType.MANGROVE));
    public static final WoodType BAMBOO = WoodType.register(new WoodType("bamboo", BlockSetType.BAMBOO, BlockSoundGroup.BAMBOO_WOOD, BlockSoundGroup.BAMBOO_WOOD_HANGING_SIGN, SoundEvents.BLOCK_BAMBOO_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_BAMBOO_WOOD_FENCE_GATE_OPEN));

    public WoodType(String name, BlockSetType setType) {
        this(name, setType, BlockSoundGroup.WOOD, BlockSoundGroup.HANGING_SIGN, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundEvents.BLOCK_FENCE_GATE_OPEN);
    }

    private static WoodType register(WoodType type) {
        VALUES.add(type);
        return type;
    }

    public static Stream<WoodType> stream() {
        return VALUES.stream();
    }
}

