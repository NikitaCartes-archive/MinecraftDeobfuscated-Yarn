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
	public static final BlockSoundGroup LILY_PAD = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_15037, SoundEvents.field_14573, SoundEvents.field_15173, SoundEvents.field_14720, SoundEvents.field_14965
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
	public static final BlockSoundGroup HONEY = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21070, SoundEvents.field_21075, SoundEvents.field_21073, SoundEvents.field_21072, SoundEvents.field_21071
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
	public static final BlockSoundGroup STEM = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_15215, SoundEvents.field_15053, SoundEvents.field_17611, SoundEvents.field_14808, SoundEvents.field_14607
	);
	public static final BlockSoundGroup VINE = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_15037, SoundEvents.field_23061, SoundEvents.field_14653, SoundEvents.field_14720, SoundEvents.field_14965
	);
	public static final BlockSoundGroup NETHER_WART = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_17612, SoundEvents.field_14921, SoundEvents.field_17613, SoundEvents.field_14658, SoundEvents.field_14723
	);
	public static final BlockSoundGroup LANTERN = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_17745, SoundEvents.field_17744, SoundEvents.field_17743, SoundEvents.field_17742, SoundEvents.field_17746
	);
	public static final BlockSoundGroup NETHER_STEM = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21887, SoundEvents.field_21888, SoundEvents.field_21889, SoundEvents.field_21890, SoundEvents.field_21892
	);
	public static final BlockSoundGroup NYLIUM = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21893, SoundEvents.field_21894, SoundEvents.field_21895, SoundEvents.field_21896, SoundEvents.field_21897
	);
	public static final BlockSoundGroup FUNGUS = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21903, SoundEvents.field_21904, SoundEvents.field_21905, SoundEvents.field_21907, SoundEvents.field_21908
	);
	public static final BlockSoundGroup ROOTS = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21877, SoundEvents.field_21878, SoundEvents.field_21879, SoundEvents.field_21880, SoundEvents.field_21881
	);
	public static final BlockSoundGroup SHROOMLIGHT = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21931, SoundEvents.field_21932, SoundEvents.field_21933, SoundEvents.field_21934, SoundEvents.field_21935
	);
	public static final BlockSoundGroup WEEPING_VINES = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21909, SoundEvents.field_21910, SoundEvents.field_21911, SoundEvents.field_21912, SoundEvents.field_21913
	);
	public static final BlockSoundGroup WEEPING_VINES_LOW_PITCH = new BlockSoundGroup(
		1.0F, 0.5F, SoundEvents.field_21909, SoundEvents.field_21910, SoundEvents.field_21911, SoundEvents.field_21912, SoundEvents.field_21913
	);
	public static final BlockSoundGroup SOUL_SAND = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21949, SoundEvents.field_21950, SoundEvents.field_21951, SoundEvents.field_21942, SoundEvents.field_21943
	);
	public static final BlockSoundGroup SOUL_SOIL = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21944, SoundEvents.field_21945, SoundEvents.field_21946, SoundEvents.field_21947, SoundEvents.field_21948
	);
	public static final BlockSoundGroup BASALT = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21867, SoundEvents.field_21868, SoundEvents.field_21869, SoundEvents.field_21870, SoundEvents.field_21871
	);
	public static final BlockSoundGroup WART_BLOCK = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21914, SoundEvents.field_21915, SoundEvents.field_21916, SoundEvents.field_21917, SoundEvents.field_21918
	);
	public static final BlockSoundGroup NETHERRACK = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21924, SoundEvents.field_21925, SoundEvents.field_21926, SoundEvents.field_21927, SoundEvents.field_21928
	);
	public static final BlockSoundGroup NETHER_BRICKS = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21882, SoundEvents.field_21883, SoundEvents.field_21884, SoundEvents.field_21885, SoundEvents.field_21886
	);
	public static final BlockSoundGroup NETHER_SPROUTS = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21898, SoundEvents.field_21899, SoundEvents.field_21900, SoundEvents.field_21901, SoundEvents.field_21902
	);
	public static final BlockSoundGroup NETHER_ORE = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21937, SoundEvents.field_21941, SoundEvents.field_21940, SoundEvents.field_21939, SoundEvents.field_21938
	);
	public static final BlockSoundGroup BONE = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21872, SoundEvents.field_21876, SoundEvents.field_21875, SoundEvents.field_21874, SoundEvents.field_21873
	);
	public static final BlockSoundGroup NETHERITE = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21919, SoundEvents.field_21920, SoundEvents.field_21921, SoundEvents.field_21922, SoundEvents.field_21923
	);
	public static final BlockSoundGroup ANCIENT_DEBRIS = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_21891, SoundEvents.field_21906, SoundEvents.field_21929, SoundEvents.field_21930, SoundEvents.field_21936
	);
	public static final BlockSoundGroup LODESTONE = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_23194, SoundEvents.field_23195, SoundEvents.field_23196, SoundEvents.field_23197, SoundEvents.field_23198
	);
	public static final BlockSoundGroup CHAIN = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_24065, SoundEvents.field_24064, SoundEvents.field_24063, SoundEvents.field_24062, SoundEvents.field_24061
	);
	public static final BlockSoundGroup NETHER_GOLD_ORE = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_24071, SoundEvents.field_24075, SoundEvents.field_24074, SoundEvents.field_24073, SoundEvents.field_24072
	);
	public static final BlockSoundGroup GILDED_BLACKSTONE = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.field_24066, SoundEvents.field_24070, SoundEvents.field_24069, SoundEvents.field_24068, SoundEvents.field_24067
	);
	public final float volume;
	public final float pitch;
	private final SoundEvent breakSound;
	private final SoundEvent stepSound;
	private final SoundEvent placeSound;
	private final SoundEvent hitSound;
	private final SoundEvent fallSound;

	public BlockSoundGroup(
		float volume, float pitch, SoundEvent breakSound, SoundEvent stepSound, SoundEvent placeSound, SoundEvent hitSound, SoundEvent fallSound
	) {
		this.volume = volume;
		this.pitch = pitch;
		this.breakSound = breakSound;
		this.stepSound = stepSound;
		this.placeSound = placeSound;
		this.hitSound = hitSound;
		this.fallSound = fallSound;
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent getBreakSound() {
		return this.breakSound;
	}

	public SoundEvent getStepSound() {
		return this.stepSound;
	}

	public SoundEvent getPlaceSound() {
		return this.placeSound;
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent getHitSound() {
		return this.hitSound;
	}

	public SoundEvent getFallSound() {
		return this.fallSound;
	}
}
