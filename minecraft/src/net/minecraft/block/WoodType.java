package net.minecraft.block;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public record WoodType(
	String name, BlockSetType setType, BlockSoundGroup soundType, BlockSoundGroup hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen
) {
	private static final Map<String, WoodType> VALUES = new Object2ObjectArrayMap<>();
	public static final Codec<WoodType> CODEC = Codec.stringResolver(WoodType::name, VALUES::get);
	public static final WoodType OAK = register(new WoodType("oak", BlockSetType.OAK));
	public static final WoodType SPRUCE = register(new WoodType("spruce", BlockSetType.SPRUCE));
	public static final WoodType BIRCH = register(new WoodType("birch", BlockSetType.BIRCH));
	public static final WoodType ACACIA = register(new WoodType("acacia", BlockSetType.ACACIA));
	public static final WoodType CHERRY = register(
		new WoodType(
			"cherry",
			BlockSetType.CHERRY,
			BlockSoundGroup.CHERRY_WOOD,
			BlockSoundGroup.CHERRY_WOOD_HANGING_SIGN,
			SoundEvents.BLOCK_CHERRY_WOOD_FENCE_GATE_CLOSE,
			SoundEvents.BLOCK_CHERRY_WOOD_FENCE_GATE_OPEN
		)
	);
	public static final WoodType JUNGLE = register(new WoodType("jungle", BlockSetType.JUNGLE));
	public static final WoodType DARK_OAK = register(new WoodType("dark_oak", BlockSetType.DARK_OAK));
	public static final WoodType CRIMSON = register(
		new WoodType(
			"crimson",
			BlockSetType.CRIMSON,
			BlockSoundGroup.NETHER_WOOD,
			BlockSoundGroup.NETHER_WOOD_HANGING_SIGN,
			SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE,
			SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_OPEN
		)
	);
	public static final WoodType WARPED = register(
		new WoodType(
			"warped",
			BlockSetType.WARPED,
			BlockSoundGroup.NETHER_WOOD,
			BlockSoundGroup.NETHER_WOOD_HANGING_SIGN,
			SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE,
			SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_OPEN
		)
	);
	public static final WoodType MANGROVE = register(new WoodType("mangrove", BlockSetType.MANGROVE));
	public static final WoodType BAMBOO = register(
		new WoodType(
			"bamboo",
			BlockSetType.BAMBOO,
			BlockSoundGroup.BAMBOO_WOOD,
			BlockSoundGroup.BAMBOO_WOOD_HANGING_SIGN,
			SoundEvents.BLOCK_BAMBOO_WOOD_FENCE_GATE_CLOSE,
			SoundEvents.BLOCK_BAMBOO_WOOD_FENCE_GATE_OPEN
		)
	);

	public WoodType(String name, BlockSetType setType) {
		this(name, setType, BlockSoundGroup.WOOD, BlockSoundGroup.HANGING_SIGN, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundEvents.BLOCK_FENCE_GATE_OPEN);
	}

	private static WoodType register(WoodType type) {
		VALUES.put(type.name(), type);
		return type;
	}

	public static Stream<WoodType> stream() {
		return VALUES.values().stream();
	}
}
