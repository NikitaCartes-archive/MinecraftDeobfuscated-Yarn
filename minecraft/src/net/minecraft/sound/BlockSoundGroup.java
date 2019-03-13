package net.minecraft.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BlockSoundGroup {
	public static final BlockSoundGroup WOOD = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_15215, SoundEvents.field_15053, SoundEvents.field_14718, SoundEvents.field_14808, SoundEvents.field_14607
	);
	public static final BlockSoundGroup GRAVEL = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_15211, SoundEvents.field_14798, SoundEvents.field_14609, SoundEvents.field_14697, SoundEvents.field_15156
	);
	public static final BlockSoundGroup GRASS = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_15037, SoundEvents.field_14573, SoundEvents.field_14653, SoundEvents.field_14720, SoundEvents.field_14965
	);
	public static final BlockSoundGroup STONE = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_15026, SoundEvents.field_14921, SoundEvents.field_14574, SoundEvents.field_14658, SoundEvents.field_14723
	);
	public static final BlockSoundGroup METAL = new BlockSoundGroup(
		1.0F, 1.5F, SoundEvents.field_15044, SoundEvents.field_14924, SoundEvents.field_15167, SoundEvents.field_14557, SoundEvents.field_15142
	);
	public static final BlockSoundGroup GLASS = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_15081, SoundEvents.field_14769, SoundEvents.field_14843, SoundEvents.field_14583, SoundEvents.field_14666
	);
	public static final BlockSoundGroup WOOL = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_14983, SoundEvents.field_15181, SoundEvents.field_15226, SoundEvents.field_14628, SoundEvents.field_15048
	);
	public static final BlockSoundGroup SAND = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_15074, SoundEvents.field_14677, SoundEvents.field_15144, SoundEvents.field_15221, SoundEvents.field_14943
	);
	public static final BlockSoundGroup SNOW = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_15165, SoundEvents.field_15060, SoundEvents.field_14945, SoundEvents.field_15040, SoundEvents.field_15092
	);
	public static final BlockSoundGroup LADDER = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_14546, SoundEvents.field_14948, SoundEvents.field_14853, SoundEvents.field_14775, SoundEvents.field_14646
	);
	public static final BlockSoundGroup ANVIL = new BlockSoundGroup(
		0.3F, 1.0F, SoundEvents.field_14542, SoundEvents.field_14695, SoundEvents.field_14785, SoundEvents.field_14927, SoundEvents.field_14727
	);
	public static final BlockSoundGroup SLIME = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_15194, SoundEvents.field_15180, SoundEvents.field_14788, SoundEvents.field_14640, SoundEvents.field_14560
	);
	public static final BlockSoundGroup WET_GRASS = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_15120, SoundEvents.field_14774, SoundEvents.field_15025, SoundEvents.field_14953, SoundEvents.field_15207
	);
	public static final BlockSoundGroup CORAL = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_14804, SoundEvents.field_14935, SoundEvents.field_15087, SoundEvents.field_14672, SoundEvents.field_14551
	);
	public static final BlockSoundGroup BAMBOO = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_14740, SoundEvents.field_14635, SoundEvents.field_14719, SoundEvents.field_14811, SoundEvents.field_14906
	);
	public static final BlockSoundGroup BAMBOO_SAPLING = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_14629, SoundEvents.field_14635, SoundEvents.field_15125, SoundEvents.field_15227, SoundEvents.field_14906
	);
	public static final BlockSoundGroup SCAFFOLDING = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_16509, SoundEvents.field_16508, SoundEvents.field_16507, SoundEvents.field_16506, SoundEvents.field_16510
	);
	public static final BlockSoundGroup SWEET_BERRY_BUSH = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_17615, SoundEvents.field_14573, SoundEvents.field_17616, SoundEvents.field_14720, SoundEvents.field_14965
	);
	public static final BlockSoundGroup CROP = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_17610, SoundEvents.field_14573, SoundEvents.field_17611, SoundEvents.field_14720, SoundEvents.field_14965
	);
	public static final BlockSoundGroup NETHER_WART = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_17612, SoundEvents.field_14921, SoundEvents.field_17613, SoundEvents.field_14658, SoundEvents.field_14723
	);
	public static final BlockSoundGroup LANTERN = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_17745, SoundEvents.field_17744, SoundEvents.field_17743, SoundEvents.field_17742, SoundEvents.field_17746
	);
	public final float volume;
	public final float pitch;
	private final SoundEvent field_11546;
	private final SoundEvent field_11527;
	private final SoundEvent field_11536;
	private final SoundEvent field_11530;
	private final SoundEvent field_11541;

	public BlockSoundGroup(float f, float g, SoundEvent soundEvent, SoundEvent soundEvent2, SoundEvent soundEvent3, SoundEvent soundEvent4, SoundEvent soundEvent5) {
		this.volume = f;
		this.pitch = g;
		this.field_11546 = soundEvent;
		this.field_11527 = soundEvent2;
		this.field_11536 = soundEvent3;
		this.field_11530 = soundEvent4;
		this.field_11541 = soundEvent5;
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent method_10595() {
		return this.field_11546;
	}

	public SoundEvent method_10594() {
		return this.field_11527;
	}

	public SoundEvent method_10598() {
		return this.field_11536;
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent method_10596() {
		return this.field_11530;
	}

	public SoundEvent method_10593() {
		return this.field_11541;
	}
}
