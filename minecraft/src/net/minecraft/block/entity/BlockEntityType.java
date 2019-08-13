package net.minecraft.block.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.datafixers.Schemas;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockEntityType<T extends BlockEntity> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final BlockEntityType<FurnaceBlockEntity> field_11903 = create(
		"furnace", BlockEntityType.Builder.create(FurnaceBlockEntity::new, Blocks.field_10181)
	);
	public static final BlockEntityType<ChestBlockEntity> field_11914 = create("chest", BlockEntityType.Builder.create(ChestBlockEntity::new, Blocks.field_10034));
	public static final BlockEntityType<TrappedChestBlockEntity> field_11891 = create(
		"trapped_chest", BlockEntityType.Builder.create(TrappedChestBlockEntity::new, Blocks.field_10380)
	);
	public static final BlockEntityType<EnderChestBlockEntity> field_11901 = create(
		"ender_chest", BlockEntityType.Builder.create(EnderChestBlockEntity::new, Blocks.field_10443)
	);
	public static final BlockEntityType<JukeboxBlockEntity> field_11907 = create(
		"jukebox", BlockEntityType.Builder.create(JukeboxBlockEntity::new, Blocks.field_10223)
	);
	public static final BlockEntityType<DispenserBlockEntity> field_11887 = create(
		"dispenser", BlockEntityType.Builder.create(DispenserBlockEntity::new, Blocks.field_10200)
	);
	public static final BlockEntityType<DropperBlockEntity> field_11899 = create(
		"dropper", BlockEntityType.Builder.create(DropperBlockEntity::new, Blocks.field_10228)
	);
	public static final BlockEntityType<SignBlockEntity> field_11911 = create(
		"sign",
		BlockEntityType.Builder.create(
			SignBlockEntity::new,
			Blocks.field_10121,
			Blocks.field_10411,
			Blocks.field_10231,
			Blocks.field_10284,
			Blocks.field_10544,
			Blocks.field_10330,
			Blocks.field_10187,
			Blocks.field_10088,
			Blocks.field_10391,
			Blocks.field_10401,
			Blocks.field_10587,
			Blocks.field_10265
		)
	);
	public static final BlockEntityType<MobSpawnerBlockEntity> field_11889 = create(
		"mob_spawner", BlockEntityType.Builder.create(MobSpawnerBlockEntity::new, Blocks.field_10260)
	);
	public static final BlockEntityType<PistonBlockEntity> field_11897 = create(
		"piston", BlockEntityType.Builder.create(PistonBlockEntity::new, Blocks.field_10008)
	);
	public static final BlockEntityType<BrewingStandBlockEntity> field_11894 = create(
		"brewing_stand", BlockEntityType.Builder.create(BrewingStandBlockEntity::new, Blocks.field_10333)
	);
	public static final BlockEntityType<EnchantingTableBlockEntity> field_11912 = create(
		"enchanting_table", BlockEntityType.Builder.create(EnchantingTableBlockEntity::new, Blocks.field_10485)
	);
	public static final BlockEntityType<EndPortalBlockEntity> field_11898 = create(
		"end_portal", BlockEntityType.Builder.create(EndPortalBlockEntity::new, Blocks.field_10027)
	);
	public static final BlockEntityType<BeaconBlockEntity> field_11890 = create(
		"beacon", BlockEntityType.Builder.create(BeaconBlockEntity::new, Blocks.field_10327)
	);
	public static final BlockEntityType<SkullBlockEntity> field_11913 = create(
		"skull",
		BlockEntityType.Builder.create(
			SkullBlockEntity::new,
			Blocks.field_10481,
			Blocks.field_10388,
			Blocks.field_10042,
			Blocks.field_10509,
			Blocks.field_10337,
			Blocks.field_10472,
			Blocks.field_10241,
			Blocks.field_10581,
			Blocks.field_10177,
			Blocks.field_10101,
			Blocks.field_10432,
			Blocks.field_10208
		)
	);
	public static final BlockEntityType<DaylightDetectorBlockEntity> field_11900 = create(
		"daylight_detector", BlockEntityType.Builder.create(DaylightDetectorBlockEntity::new, Blocks.field_10429)
	);
	public static final BlockEntityType<HopperBlockEntity> field_11888 = create(
		"hopper", BlockEntityType.Builder.create(HopperBlockEntity::new, Blocks.field_10312)
	);
	public static final BlockEntityType<ComparatorBlockEntity> field_11908 = create(
		"comparator", BlockEntityType.Builder.create(ComparatorBlockEntity::new, Blocks.field_10377)
	);
	public static final BlockEntityType<BannerBlockEntity> field_11905 = create(
		"banner",
		BlockEntityType.Builder.create(
			BannerBlockEntity::new,
			Blocks.field_10154,
			Blocks.field_10045,
			Blocks.field_10438,
			Blocks.field_10452,
			Blocks.field_10547,
			Blocks.field_10229,
			Blocks.field_10612,
			Blocks.field_10185,
			Blocks.field_9985,
			Blocks.field_10165,
			Blocks.field_10368,
			Blocks.field_10281,
			Blocks.field_10602,
			Blocks.field_10198,
			Blocks.field_10406,
			Blocks.field_10062,
			Blocks.field_10202,
			Blocks.field_10599,
			Blocks.field_10274,
			Blocks.field_10050,
			Blocks.field_10139,
			Blocks.field_10318,
			Blocks.field_10531,
			Blocks.field_10267,
			Blocks.field_10604,
			Blocks.field_10372,
			Blocks.field_10054,
			Blocks.field_10067,
			Blocks.field_10370,
			Blocks.field_10594,
			Blocks.field_10279,
			Blocks.field_10537
		)
	);
	public static final BlockEntityType<StructureBlockBlockEntity> field_11895 = create(
		"structure_block", BlockEntityType.Builder.create(StructureBlockBlockEntity::new, Blocks.field_10465)
	);
	public static final BlockEntityType<EndGatewayBlockEntity> field_11906 = create(
		"end_gateway", BlockEntityType.Builder.create(EndGatewayBlockEntity::new, Blocks.field_10613)
	);
	public static final BlockEntityType<CommandBlockBlockEntity> field_11904 = create(
		"command_block", BlockEntityType.Builder.create(CommandBlockBlockEntity::new, Blocks.field_10525, Blocks.field_10395, Blocks.field_10263)
	);
	public static final BlockEntityType<ShulkerBoxBlockEntity> field_11896 = create(
		"shulker_box",
		BlockEntityType.Builder.create(
			ShulkerBoxBlockEntity::new,
			Blocks.field_10603,
			Blocks.field_10371,
			Blocks.field_10605,
			Blocks.field_10373,
			Blocks.field_10532,
			Blocks.field_10140,
			Blocks.field_10055,
			Blocks.field_10203,
			Blocks.field_10320,
			Blocks.field_10275,
			Blocks.field_10063,
			Blocks.field_10407,
			Blocks.field_10051,
			Blocks.field_10268,
			Blocks.field_10068,
			Blocks.field_10199,
			Blocks.field_10600
		)
	);
	public static final BlockEntityType<BedBlockEntity> field_11910 = create(
		"bed",
		BlockEntityType.Builder.create(
			BedBlockEntity::new,
			Blocks.field_10069,
			Blocks.field_10461,
			Blocks.field_10527,
			Blocks.field_10288,
			Blocks.field_10109,
			Blocks.field_10141,
			Blocks.field_10561,
			Blocks.field_10621,
			Blocks.field_10326,
			Blocks.field_10180,
			Blocks.field_10230,
			Blocks.field_10410,
			Blocks.field_10610,
			Blocks.field_10019,
			Blocks.field_10120,
			Blocks.field_10356
		)
	);
	public static final BlockEntityType<ConduitBlockEntity> field_11902 = create(
		"conduit", BlockEntityType.Builder.create(ConduitBlockEntity::new, Blocks.field_10502)
	);
	public static final BlockEntityType<BarrelBlockEntity> field_16411 = create(
		"barrel", BlockEntityType.Builder.create(BarrelBlockEntity::new, Blocks.field_16328)
	);
	public static final BlockEntityType<SmokerBlockEntity> field_16414 = create(
		"smoker", BlockEntityType.Builder.create(SmokerBlockEntity::new, Blocks.field_16334)
	);
	public static final BlockEntityType<BlastFurnaceBlockEntity> field_16415 = create(
		"blast_furnace", BlockEntityType.Builder.create(BlastFurnaceBlockEntity::new, Blocks.field_16333)
	);
	public static final BlockEntityType<LecternBlockEntity> field_16412 = create(
		"lectern", BlockEntityType.Builder.create(LecternBlockEntity::new, Blocks.field_16330)
	);
	public static final BlockEntityType<BellBlockEntity> field_16413 = create("bell", BlockEntityType.Builder.create(BellBlockEntity::new, Blocks.field_16332));
	public static final BlockEntityType<JigsawBlockEntity> field_16549 = create(
		"jigsaw", BlockEntityType.Builder.create(JigsawBlockEntity::new, Blocks.field_16540)
	);
	public static final BlockEntityType<CampfireBlockEntity> field_17380 = create(
		"campfire", BlockEntityType.Builder.create(CampfireBlockEntity::new, Blocks.field_17350)
	);
	private final Supplier<? extends T> supplier;
	private final Set<Block> blocks;
	private final Type<?> type;

	@Nullable
	public static Identifier getId(BlockEntityType<?> blockEntityType) {
		return Registry.BLOCK_ENTITY.getId(blockEntityType);
	}

	private static <T extends BlockEntity> BlockEntityType<T> create(String string, BlockEntityType.Builder<T> builder) {
		Type<?> type = null;

		try {
			type = Schemas.getFixer()
				.getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
				.getChoiceType(TypeReferences.BLOCK_ENTITY, string);
		} catch (IllegalStateException var4) {
			if (SharedConstants.isDevelopment) {
				throw var4;
			}

			LOGGER.warn("No data fixer registered for block entity {}", string);
		}

		if (builder.blocks.isEmpty()) {
			LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", string);
		}

		return Registry.register(Registry.BLOCK_ENTITY, string, builder.build(type));
	}

	public BlockEntityType(Supplier<? extends T> supplier, Set<Block> set, Type<?> type) {
		this.supplier = supplier;
		this.blocks = set;
		this.type = type;
	}

	@Nullable
	public T instantiate() {
		return (T)this.supplier.get();
	}

	public boolean supports(Block block) {
		return this.blocks.contains(block);
	}

	public static final class Builder<T extends BlockEntity> {
		private final Supplier<? extends T> supplier;
		private final Set<Block> blocks;

		private Builder(Supplier<? extends T> supplier, Set<Block> set) {
			this.supplier = supplier;
			this.blocks = set;
		}

		public static <T extends BlockEntity> BlockEntityType.Builder<T> create(Supplier<? extends T> supplier, Block... blocks) {
			return new BlockEntityType.Builder<>(supplier, ImmutableSet.copyOf(blocks));
		}

		public BlockEntityType<T> build(Type<?> type) {
			return new BlockEntityType<>(this.supplier, this.blocks, type);
		}
	}
}
