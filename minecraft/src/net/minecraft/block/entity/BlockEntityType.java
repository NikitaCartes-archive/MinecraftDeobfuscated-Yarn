package net.minecraft.block.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockEntityType<T extends BlockEntity> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final BlockEntityType<FurnaceBlockEntity> FURNACE = create("furnace", BlockEntityType.Builder.create(FurnaceBlockEntity::new, Blocks.FURNACE));
	public static final BlockEntityType<ChestBlockEntity> CHEST = create("chest", BlockEntityType.Builder.create(ChestBlockEntity::new, Blocks.CHEST));
	public static final BlockEntityType<TrappedChestBlockEntity> TRAPPED_CHEST = create(
		"trapped_chest", BlockEntityType.Builder.create(TrappedChestBlockEntity::new, Blocks.TRAPPED_CHEST)
	);
	public static final BlockEntityType<EnderChestBlockEntity> ENDER_CHEST = create(
		"ender_chest", BlockEntityType.Builder.create(EnderChestBlockEntity::new, Blocks.ENDER_CHEST)
	);
	public static final BlockEntityType<JukeboxBlockEntity> JUKEBOX = create("jukebox", BlockEntityType.Builder.create(JukeboxBlockEntity::new, Blocks.JUKEBOX));
	public static final BlockEntityType<DispenserBlockEntity> DISPENSER = create(
		"dispenser", BlockEntityType.Builder.create(DispenserBlockEntity::new, Blocks.DISPENSER)
	);
	public static final BlockEntityType<DropperBlockEntity> DROPPER = create("dropper", BlockEntityType.Builder.create(DropperBlockEntity::new, Blocks.DROPPER));
	public static final BlockEntityType<SignBlockEntity> SIGN = create(
		"sign",
		BlockEntityType.Builder.create(
			SignBlockEntity::new,
			Blocks.OAK_SIGN,
			Blocks.SPRUCE_SIGN,
			Blocks.BIRCH_SIGN,
			Blocks.ACACIA_SIGN,
			Blocks.JUNGLE_SIGN,
			Blocks.DARK_OAK_SIGN,
			Blocks.OAK_WALL_SIGN,
			Blocks.SPRUCE_WALL_SIGN,
			Blocks.BIRCH_WALL_SIGN,
			Blocks.ACACIA_WALL_SIGN,
			Blocks.JUNGLE_WALL_SIGN,
			Blocks.DARK_OAK_WALL_SIGN,
			Blocks.CRIMSON_SIGN,
			Blocks.CRIMSON_WALL_SIGN,
			Blocks.WARPED_SIGN,
			Blocks.WARPED_WALL_SIGN
		)
	);
	public static final BlockEntityType<MobSpawnerBlockEntity> MOB_SPAWNER = create(
		"mob_spawner", BlockEntityType.Builder.create(MobSpawnerBlockEntity::new, Blocks.SPAWNER)
	);
	public static final BlockEntityType<PistonBlockEntity> PISTON = create("piston", BlockEntityType.Builder.create(PistonBlockEntity::new, Blocks.MOVING_PISTON));
	public static final BlockEntityType<BrewingStandBlockEntity> BREWING_STAND = create(
		"brewing_stand", BlockEntityType.Builder.create(BrewingStandBlockEntity::new, Blocks.BREWING_STAND)
	);
	public static final BlockEntityType<EnchantingTableBlockEntity> ENCHANTING_TABLE = create(
		"enchanting_table", BlockEntityType.Builder.create(EnchantingTableBlockEntity::new, Blocks.ENCHANTING_TABLE)
	);
	public static final BlockEntityType<EndPortalBlockEntity> END_PORTAL = create(
		"end_portal", BlockEntityType.Builder.create(EndPortalBlockEntity::new, Blocks.END_PORTAL)
	);
	public static final BlockEntityType<BeaconBlockEntity> BEACON = create("beacon", BlockEntityType.Builder.create(BeaconBlockEntity::new, Blocks.BEACON));
	public static final BlockEntityType<SkullBlockEntity> SKULL = create(
		"skull",
		BlockEntityType.Builder.create(
			SkullBlockEntity::new,
			Blocks.SKELETON_SKULL,
			Blocks.SKELETON_WALL_SKULL,
			Blocks.CREEPER_HEAD,
			Blocks.CREEPER_WALL_HEAD,
			Blocks.DRAGON_HEAD,
			Blocks.DRAGON_WALL_HEAD,
			Blocks.ZOMBIE_HEAD,
			Blocks.ZOMBIE_WALL_HEAD,
			Blocks.WITHER_SKELETON_SKULL,
			Blocks.WITHER_SKELETON_WALL_SKULL,
			Blocks.PLAYER_HEAD,
			Blocks.PLAYER_WALL_HEAD
		)
	);
	public static final BlockEntityType<DaylightDetectorBlockEntity> DAYLIGHT_DETECTOR = create(
		"daylight_detector", BlockEntityType.Builder.create(DaylightDetectorBlockEntity::new, Blocks.DAYLIGHT_DETECTOR)
	);
	public static final BlockEntityType<HopperBlockEntity> HOPPER = create("hopper", BlockEntityType.Builder.create(HopperBlockEntity::new, Blocks.HOPPER));
	public static final BlockEntityType<ComparatorBlockEntity> COMPARATOR = create(
		"comparator", BlockEntityType.Builder.create(ComparatorBlockEntity::new, Blocks.COMPARATOR)
	);
	public static final BlockEntityType<BannerBlockEntity> BANNER = create(
		"banner",
		BlockEntityType.Builder.create(
			BannerBlockEntity::new,
			Blocks.WHITE_BANNER,
			Blocks.ORANGE_BANNER,
			Blocks.MAGENTA_BANNER,
			Blocks.LIGHT_BLUE_BANNER,
			Blocks.YELLOW_BANNER,
			Blocks.LIME_BANNER,
			Blocks.PINK_BANNER,
			Blocks.GRAY_BANNER,
			Blocks.LIGHT_GRAY_BANNER,
			Blocks.CYAN_BANNER,
			Blocks.PURPLE_BANNER,
			Blocks.BLUE_BANNER,
			Blocks.BROWN_BANNER,
			Blocks.GREEN_BANNER,
			Blocks.RED_BANNER,
			Blocks.BLACK_BANNER,
			Blocks.WHITE_WALL_BANNER,
			Blocks.ORANGE_WALL_BANNER,
			Blocks.MAGENTA_WALL_BANNER,
			Blocks.LIGHT_BLUE_WALL_BANNER,
			Blocks.YELLOW_WALL_BANNER,
			Blocks.LIME_WALL_BANNER,
			Blocks.PINK_WALL_BANNER,
			Blocks.GRAY_WALL_BANNER,
			Blocks.LIGHT_GRAY_WALL_BANNER,
			Blocks.CYAN_WALL_BANNER,
			Blocks.PURPLE_WALL_BANNER,
			Blocks.BLUE_WALL_BANNER,
			Blocks.BROWN_WALL_BANNER,
			Blocks.GREEN_WALL_BANNER,
			Blocks.RED_WALL_BANNER,
			Blocks.BLACK_WALL_BANNER
		)
	);
	public static final BlockEntityType<StructureBlockBlockEntity> STRUCTURE_BLOCK = create(
		"structure_block", BlockEntityType.Builder.create(StructureBlockBlockEntity::new, Blocks.STRUCTURE_BLOCK)
	);
	public static final BlockEntityType<EndGatewayBlockEntity> END_GATEWAY = create(
		"end_gateway", BlockEntityType.Builder.create(EndGatewayBlockEntity::new, Blocks.END_GATEWAY)
	);
	public static final BlockEntityType<CommandBlockBlockEntity> COMMAND_BLOCK = create(
		"command_block",
		BlockEntityType.Builder.create(CommandBlockBlockEntity::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK)
	);
	public static final BlockEntityType<ShulkerBoxBlockEntity> SHULKER_BOX = create(
		"shulker_box",
		BlockEntityType.Builder.create(
			ShulkerBoxBlockEntity::new,
			Blocks.SHULKER_BOX,
			Blocks.BLACK_SHULKER_BOX,
			Blocks.BLUE_SHULKER_BOX,
			Blocks.BROWN_SHULKER_BOX,
			Blocks.CYAN_SHULKER_BOX,
			Blocks.GRAY_SHULKER_BOX,
			Blocks.GREEN_SHULKER_BOX,
			Blocks.LIGHT_BLUE_SHULKER_BOX,
			Blocks.LIGHT_GRAY_SHULKER_BOX,
			Blocks.LIME_SHULKER_BOX,
			Blocks.MAGENTA_SHULKER_BOX,
			Blocks.ORANGE_SHULKER_BOX,
			Blocks.PINK_SHULKER_BOX,
			Blocks.PURPLE_SHULKER_BOX,
			Blocks.RED_SHULKER_BOX,
			Blocks.WHITE_SHULKER_BOX,
			Blocks.YELLOW_SHULKER_BOX
		)
	);
	public static final BlockEntityType<BedBlockEntity> BED = create(
		"bed",
		BlockEntityType.Builder.create(
			BedBlockEntity::new,
			Blocks.RED_BED,
			Blocks.BLACK_BED,
			Blocks.BLUE_BED,
			Blocks.BROWN_BED,
			Blocks.CYAN_BED,
			Blocks.GRAY_BED,
			Blocks.GREEN_BED,
			Blocks.LIGHT_BLUE_BED,
			Blocks.LIGHT_GRAY_BED,
			Blocks.LIME_BED,
			Blocks.MAGENTA_BED,
			Blocks.ORANGE_BED,
			Blocks.PINK_BED,
			Blocks.PURPLE_BED,
			Blocks.WHITE_BED,
			Blocks.YELLOW_BED
		)
	);
	public static final BlockEntityType<ConduitBlockEntity> CONDUIT = create("conduit", BlockEntityType.Builder.create(ConduitBlockEntity::new, Blocks.CONDUIT));
	public static final BlockEntityType<BarrelBlockEntity> BARREL = create("barrel", BlockEntityType.Builder.create(BarrelBlockEntity::new, Blocks.BARREL));
	public static final BlockEntityType<SmokerBlockEntity> SMOKER = create("smoker", BlockEntityType.Builder.create(SmokerBlockEntity::new, Blocks.SMOKER));
	public static final BlockEntityType<BlastFurnaceBlockEntity> BLAST_FURNACE = create(
		"blast_furnace", BlockEntityType.Builder.create(BlastFurnaceBlockEntity::new, Blocks.BLAST_FURNACE)
	);
	public static final BlockEntityType<LecternBlockEntity> LECTERN = create("lectern", BlockEntityType.Builder.create(LecternBlockEntity::new, Blocks.LECTERN));
	public static final BlockEntityType<BellBlockEntity> BELL = create("bell", BlockEntityType.Builder.create(BellBlockEntity::new, Blocks.BELL));
	public static final BlockEntityType<JigsawBlockEntity> JIGSAW = create("jigsaw", BlockEntityType.Builder.create(JigsawBlockEntity::new, Blocks.JIGSAW));
	public static final BlockEntityType<CampfireBlockEntity> CAMPFIRE = create(
		"campfire", BlockEntityType.Builder.create(CampfireBlockEntity::new, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE)
	);
	public static final BlockEntityType<BeehiveBlockEntity> BEEHIVE = create(
		"beehive", BlockEntityType.Builder.create(BeehiveBlockEntity::new, Blocks.BEE_NEST, Blocks.BEEHIVE)
	);
	public static final BlockEntityType<SculkSensorBlockEntity> SCULK_SENSOR = create(
		"sculk_sensor", BlockEntityType.Builder.create(SculkSensorBlockEntity::new, Blocks.SCULK_SENSOR)
	);
	private final BlockEntityType.BlockEntityFactory<? extends T> factory;
	private final Set<Block> blocks;
	private final Type<?> type;

	@Nullable
	public static Identifier getId(BlockEntityType<?> type) {
		return Registry.BLOCK_ENTITY_TYPE.getId(type);
	}

	private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.Builder<T> builder) {
		if (builder.blocks.isEmpty()) {
			LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", id);
		}

		Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, id, builder.build(type));
	}

	public BlockEntityType(BlockEntityType.BlockEntityFactory<? extends T> factory, Set<Block> blocks, Type<?> type) {
		this.factory = factory;
		this.blocks = blocks;
		this.type = type;
	}

	@Nullable
	public T instantiate(BlockPos pos, BlockState state) {
		return (T)this.factory.create(pos, state);
	}

	public boolean supports(BlockState state) {
		return this.blocks.contains(state.getBlock());
	}

	@Nullable
	public T get(BlockView world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return (T)(blockEntity != null && blockEntity.getType() == this ? blockEntity : null);
	}

	@FunctionalInterface
	interface BlockEntityFactory<T extends BlockEntity> {
		T create(BlockPos pos, BlockState state);
	}

	public static final class Builder<T extends BlockEntity> {
		private final BlockEntityType.BlockEntityFactory<? extends T> factory;
		final Set<Block> blocks;

		private Builder(BlockEntityType.BlockEntityFactory<? extends T> factory, Set<Block> blocks) {
			this.factory = factory;
			this.blocks = blocks;
		}

		public static <T extends BlockEntity> BlockEntityType.Builder<T> create(BlockEntityType.BlockEntityFactory<? extends T> factory, Block... blocks) {
			return new BlockEntityType.Builder<>(factory, ImmutableSet.copyOf(blocks));
		}

		public BlockEntityType<T> build(Type<?> type) {
			return new BlockEntityType<>(this.factory, this.blocks, type);
		}
	}
}
