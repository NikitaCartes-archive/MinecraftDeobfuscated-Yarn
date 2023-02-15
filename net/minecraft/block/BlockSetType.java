/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public record BlockSetType(String name, BlockSoundGroup soundType, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn, SoundEvent buttonClickOff, SoundEvent buttonClickOn) {
    private static final Set<BlockSetType> VALUES = new ObjectArraySet<BlockSetType>();
    public static final BlockSetType IRON = BlockSetType.register(new BlockSetType("iron", BlockSoundGroup.METAL, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    public static final BlockSetType GOLD = BlockSetType.register(new BlockSetType("gold", BlockSoundGroup.METAL, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    public static final BlockSetType STONE = BlockSetType.register(new BlockSetType("stone", BlockSoundGroup.STONE, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    public static final BlockSetType POLISHED_BLACKSTONE = BlockSetType.register(new BlockSetType("polished_blackstone", BlockSoundGroup.STONE, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    public static final BlockSetType OAK = BlockSetType.register(new BlockSetType("oak"));
    public static final BlockSetType SPRUCE = BlockSetType.register(new BlockSetType("spruce"));
    public static final BlockSetType BIRCH = BlockSetType.register(new BlockSetType("birch"));
    public static final BlockSetType ACACIA = BlockSetType.register(new BlockSetType("acacia"));
    public static final BlockSetType CHERRY = BlockSetType.register(new BlockSetType("cherry", BlockSoundGroup.CHERRY_WOOD, SoundEvents.BLOCK_CHERRY_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_CHERRY_WOOD_DOOR_OPEN, SoundEvents.BLOCK_CHERRY_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_CHERRY_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_ON));
    public static final BlockSetType JUNGLE = BlockSetType.register(new BlockSetType("jungle"));
    public static final BlockSetType DARK_OAK = BlockSetType.register(new BlockSetType("dark_oak"));
    public static final BlockSetType CRIMSON = BlockSetType.register(new BlockSetType("crimson", BlockSoundGroup.NETHER_WOOD, SoundEvents.BLOCK_NETHER_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_DOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON));
    public static final BlockSetType WARPED = BlockSetType.register(new BlockSetType("warped", BlockSoundGroup.NETHER_WOOD, SoundEvents.BLOCK_NETHER_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_DOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON));
    public static final BlockSetType MANGROVE = BlockSetType.register(new BlockSetType("mangrove"));
    public static final BlockSetType BAMBOO = BlockSetType.register(new BlockSetType("bamboo", BlockSoundGroup.BAMBOO_WOOD, SoundEvents.BLOCK_BAMBOO_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_BAMBOO_WOOD_DOOR_OPEN, SoundEvents.BLOCK_BAMBOO_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_BAMBOO_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON));

    public BlockSetType(String name) {
        this(name, BlockSoundGroup.WOOD, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON);
    }

    private static BlockSetType register(BlockSetType blockSetType) {
        VALUES.add(blockSetType);
        return blockSetType;
    }

    public static Stream<BlockSetType> stream() {
        return VALUES.stream();
    }
}

