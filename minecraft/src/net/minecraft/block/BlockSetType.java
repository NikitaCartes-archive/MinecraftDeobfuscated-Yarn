package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public record BlockSetType(
	String name,
	boolean canOpenByHand,
	BlockSoundGroup soundType,
	SoundEvent doorClose,
	SoundEvent doorOpen,
	SoundEvent trapdoorClose,
	SoundEvent trapdoorOpen,
	SoundEvent pressurePlateClickOff,
	SoundEvent pressurePlateClickOn,
	SoundEvent buttonClickOff,
	SoundEvent buttonClickOn
) {
	private static final Set<BlockSetType> VALUES = new ObjectArraySet<>();
	public static final BlockSetType IRON = register(
		new BlockSetType(
			"iron",
			false,
			BlockSoundGroup.METAL,
			SoundEvents.BLOCK_IRON_DOOR_CLOSE,
			SoundEvents.BLOCK_IRON_DOOR_OPEN,
			SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,
			SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN,
			SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF,
			SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF,
			SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON
		)
	);
	public static final BlockSetType GOLD = register(
		new BlockSetType(
			"gold",
			false,
			BlockSoundGroup.METAL,
			SoundEvents.BLOCK_IRON_DOOR_CLOSE,
			SoundEvents.BLOCK_IRON_DOOR_OPEN,
			SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,
			SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN,
			SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF,
			SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF,
			SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON
		)
	);
	public static final BlockSetType STONE = register(
		new BlockSetType(
			"stone",
			true,
			BlockSoundGroup.STONE,
			SoundEvents.BLOCK_IRON_DOOR_CLOSE,
			SoundEvents.BLOCK_IRON_DOOR_OPEN,
			SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,
			SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN,
			SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF,
			SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF,
			SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON
		)
	);
	public static final BlockSetType POLISHED_BLACKSTONE = register(
		new BlockSetType(
			"polished_blackstone",
			true,
			BlockSoundGroup.STONE,
			SoundEvents.BLOCK_IRON_DOOR_CLOSE,
			SoundEvents.BLOCK_IRON_DOOR_OPEN,
			SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,
			SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN,
			SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF,
			SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF,
			SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON
		)
	);
	public static final BlockSetType OAK = register(new BlockSetType("oak"));
	public static final BlockSetType SPRUCE = register(new BlockSetType("spruce"));
	public static final BlockSetType BIRCH = register(new BlockSetType("birch"));
	public static final BlockSetType ACACIA = register(new BlockSetType("acacia"));
	public static final BlockSetType CHERRY = register(
		new BlockSetType(
			"cherry",
			true,
			BlockSoundGroup.CHERRY_WOOD,
			SoundEvents.BLOCK_CHERRY_WOOD_DOOR_CLOSE,
			SoundEvents.BLOCK_CHERRY_WOOD_DOOR_OPEN,
			SoundEvents.BLOCK_CHERRY_WOOD_TRAPDOOR_CLOSE,
			SoundEvents.BLOCK_CHERRY_WOOD_TRAPDOOR_OPEN,
			SoundEvents.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF,
			SoundEvents.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_OFF,
			SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_ON
		)
	);
	public static final BlockSetType JUNGLE = register(new BlockSetType("jungle"));
	public static final BlockSetType DARK_OAK = register(new BlockSetType("dark_oak"));
	public static final BlockSetType CRIMSON = register(
		new BlockSetType(
			"crimson",
			true,
			BlockSoundGroup.NETHER_WOOD,
			SoundEvents.BLOCK_NETHER_WOOD_DOOR_CLOSE,
			SoundEvents.BLOCK_NETHER_WOOD_DOOR_OPEN,
			SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE,
			SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN,
			SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF,
			SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF,
			SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON
		)
	);
	public static final BlockSetType WARPED = register(
		new BlockSetType(
			"warped",
			true,
			BlockSoundGroup.NETHER_WOOD,
			SoundEvents.BLOCK_NETHER_WOOD_DOOR_CLOSE,
			SoundEvents.BLOCK_NETHER_WOOD_DOOR_OPEN,
			SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE,
			SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN,
			SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF,
			SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF,
			SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON
		)
	);
	public static final BlockSetType MANGROVE = register(new BlockSetType("mangrove"));
	public static final BlockSetType BAMBOO = register(
		new BlockSetType(
			"bamboo",
			true,
			BlockSoundGroup.BAMBOO_WOOD,
			SoundEvents.BLOCK_BAMBOO_WOOD_DOOR_CLOSE,
			SoundEvents.BLOCK_BAMBOO_WOOD_DOOR_OPEN,
			SoundEvents.BLOCK_BAMBOO_WOOD_TRAPDOOR_CLOSE,
			SoundEvents.BLOCK_BAMBOO_WOOD_TRAPDOOR_OPEN,
			SoundEvents.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF,
			SoundEvents.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF,
			SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON
		)
	);

	public BlockSetType(String name) {
		this(
			name,
			true,
			BlockSoundGroup.WOOD,
			SoundEvents.BLOCK_WOODEN_DOOR_CLOSE,
			SoundEvents.BLOCK_WOODEN_DOOR_OPEN,
			SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE,
			SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN,
			SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF,
			SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF,
			SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON
		);
	}

	private static BlockSetType register(BlockSetType blockSetType) {
		VALUES.add(blockSetType);
		return blockSetType;
	}

	public static Stream<BlockSetType> stream() {
		return VALUES.stream();
	}
}
