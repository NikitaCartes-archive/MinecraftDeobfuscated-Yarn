package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.slf4j.Logger;

/**
 * Represents a type of {@linkplain BlockEntity block entities}.
 * There is one instance of block entity for each placed block entity; this class
 * represents the type of the placed block entities, like chests or furnaces.
 * 
 * <p>Block entity types are pre-defined and registered in {@link
 * net.minecraft.registry.Registries#BLOCK_ENTITY_TYPE}. To create a block
 * entity type, the {@linkplain BlockEntityType.Builder#create builder} should be used.
 * 
 * <p>Blocks that have corresponding block entities must implement {@link
 * net.minecraft.block.BlockEntityProvider} and list it in the builder of the block
 * entity type. Multiple blocks or block states can be associated with a single block
 * entity type.
 * 
 * @see BlockEntity
 * @see net.minecraft.block.BlockEntityProvider
 */
public class BlockEntityType<T extends BlockEntity> {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final BlockEntityType<FurnaceBlockEntity> FURNACE = create("furnace", FurnaceBlockEntity::new, Blocks.FURNACE);
	public static final BlockEntityType<ChestBlockEntity> CHEST = create("chest", ChestBlockEntity::new, Blocks.CHEST);
	public static final BlockEntityType<TrappedChestBlockEntity> TRAPPED_CHEST = create("trapped_chest", TrappedChestBlockEntity::new, Blocks.TRAPPED_CHEST);
	public static final BlockEntityType<EnderChestBlockEntity> ENDER_CHEST = create("ender_chest", EnderChestBlockEntity::new, Blocks.ENDER_CHEST);
	public static final BlockEntityType<JukeboxBlockEntity> JUKEBOX = create("jukebox", JukeboxBlockEntity::new, Blocks.JUKEBOX);
	public static final BlockEntityType<DispenserBlockEntity> DISPENSER = create("dispenser", DispenserBlockEntity::new, Blocks.DISPENSER);
	public static final BlockEntityType<DropperBlockEntity> DROPPER = create("dropper", DropperBlockEntity::new, Blocks.DROPPER);
	public static final BlockEntityType<SignBlockEntity> SIGN = create(
		"sign",
		SignBlockEntity::new,
		Blocks.OAK_SIGN,
		Blocks.SPRUCE_SIGN,
		Blocks.BIRCH_SIGN,
		Blocks.ACACIA_SIGN,
		Blocks.CHERRY_SIGN,
		Blocks.JUNGLE_SIGN,
		Blocks.DARK_OAK_SIGN,
		Blocks.OAK_WALL_SIGN,
		Blocks.SPRUCE_WALL_SIGN,
		Blocks.BIRCH_WALL_SIGN,
		Blocks.ACACIA_WALL_SIGN,
		Blocks.CHERRY_WALL_SIGN,
		Blocks.JUNGLE_WALL_SIGN,
		Blocks.DARK_OAK_WALL_SIGN,
		Blocks.CRIMSON_SIGN,
		Blocks.CRIMSON_WALL_SIGN,
		Blocks.WARPED_SIGN,
		Blocks.WARPED_WALL_SIGN,
		Blocks.MANGROVE_SIGN,
		Blocks.MANGROVE_WALL_SIGN,
		Blocks.BAMBOO_SIGN,
		Blocks.BAMBOO_WALL_SIGN
	);
	public static final BlockEntityType<HangingSignBlockEntity> HANGING_SIGN = create(
		"hanging_sign",
		HangingSignBlockEntity::new,
		Blocks.OAK_HANGING_SIGN,
		Blocks.SPRUCE_HANGING_SIGN,
		Blocks.BIRCH_HANGING_SIGN,
		Blocks.ACACIA_HANGING_SIGN,
		Blocks.CHERRY_HANGING_SIGN,
		Blocks.JUNGLE_HANGING_SIGN,
		Blocks.DARK_OAK_HANGING_SIGN,
		Blocks.CRIMSON_HANGING_SIGN,
		Blocks.WARPED_HANGING_SIGN,
		Blocks.MANGROVE_HANGING_SIGN,
		Blocks.BAMBOO_HANGING_SIGN,
		Blocks.OAK_WALL_HANGING_SIGN,
		Blocks.SPRUCE_WALL_HANGING_SIGN,
		Blocks.BIRCH_WALL_HANGING_SIGN,
		Blocks.ACACIA_WALL_HANGING_SIGN,
		Blocks.CHERRY_WALL_HANGING_SIGN,
		Blocks.JUNGLE_WALL_HANGING_SIGN,
		Blocks.DARK_OAK_WALL_HANGING_SIGN,
		Blocks.CRIMSON_WALL_HANGING_SIGN,
		Blocks.WARPED_WALL_HANGING_SIGN,
		Blocks.MANGROVE_WALL_HANGING_SIGN,
		Blocks.BAMBOO_WALL_HANGING_SIGN
	);
	public static final BlockEntityType<MobSpawnerBlockEntity> MOB_SPAWNER = create("mob_spawner", MobSpawnerBlockEntity::new, Blocks.SPAWNER);
	public static final BlockEntityType<PistonBlockEntity> PISTON = create("piston", PistonBlockEntity::new, Blocks.MOVING_PISTON);
	public static final BlockEntityType<BrewingStandBlockEntity> BREWING_STAND = create("brewing_stand", BrewingStandBlockEntity::new, Blocks.BREWING_STAND);
	public static final BlockEntityType<EnchantingTableBlockEntity> ENCHANTING_TABLE = create(
		"enchanting_table", EnchantingTableBlockEntity::new, Blocks.ENCHANTING_TABLE
	);
	public static final BlockEntityType<EndPortalBlockEntity> END_PORTAL = create("end_portal", EndPortalBlockEntity::new, Blocks.END_PORTAL);
	public static final BlockEntityType<BeaconBlockEntity> BEACON = create("beacon", BeaconBlockEntity::new, Blocks.BEACON);
	public static final BlockEntityType<SkullBlockEntity> SKULL = create(
		"skull",
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
		Blocks.PLAYER_WALL_HEAD,
		Blocks.PIGLIN_HEAD,
		Blocks.PIGLIN_WALL_HEAD
	);
	public static final BlockEntityType<DaylightDetectorBlockEntity> DAYLIGHT_DETECTOR = create(
		"daylight_detector", DaylightDetectorBlockEntity::new, Blocks.DAYLIGHT_DETECTOR
	);
	public static final BlockEntityType<HopperBlockEntity> HOPPER = create("hopper", HopperBlockEntity::new, Blocks.HOPPER);
	public static final BlockEntityType<ComparatorBlockEntity> COMPARATOR = create("comparator", ComparatorBlockEntity::new, Blocks.COMPARATOR);
	public static final BlockEntityType<BannerBlockEntity> BANNER = create(
		"banner",
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
	);
	public static final BlockEntityType<StructureBlockBlockEntity> STRUCTURE_BLOCK = create(
		"structure_block", StructureBlockBlockEntity::new, Blocks.STRUCTURE_BLOCK
	);
	public static final BlockEntityType<EndGatewayBlockEntity> END_GATEWAY = create("end_gateway", EndGatewayBlockEntity::new, Blocks.END_GATEWAY);
	public static final BlockEntityType<CommandBlockBlockEntity> COMMAND_BLOCK = create(
		"command_block", CommandBlockBlockEntity::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK
	);
	public static final BlockEntityType<ShulkerBoxBlockEntity> SHULKER_BOX = create(
		"shulker_box",
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
	);
	public static final BlockEntityType<BedBlockEntity> BED = create(
		"bed",
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
	);
	public static final BlockEntityType<ConduitBlockEntity> CONDUIT = create("conduit", ConduitBlockEntity::new, Blocks.CONDUIT);
	public static final BlockEntityType<BarrelBlockEntity> BARREL = create("barrel", BarrelBlockEntity::new, Blocks.BARREL);
	public static final BlockEntityType<SmokerBlockEntity> SMOKER = create("smoker", SmokerBlockEntity::new, Blocks.SMOKER);
	public static final BlockEntityType<BlastFurnaceBlockEntity> BLAST_FURNACE = create("blast_furnace", BlastFurnaceBlockEntity::new, Blocks.BLAST_FURNACE);
	public static final BlockEntityType<LecternBlockEntity> LECTERN = create("lectern", LecternBlockEntity::new, Blocks.LECTERN);
	public static final BlockEntityType<BellBlockEntity> BELL = create("bell", BellBlockEntity::new, Blocks.BELL);
	public static final BlockEntityType<JigsawBlockEntity> JIGSAW = create("jigsaw", JigsawBlockEntity::new, Blocks.JIGSAW);
	public static final BlockEntityType<CampfireBlockEntity> CAMPFIRE = create("campfire", CampfireBlockEntity::new, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
	public static final BlockEntityType<BeehiveBlockEntity> BEEHIVE = create("beehive", BeehiveBlockEntity::new, Blocks.BEE_NEST, Blocks.BEEHIVE);
	public static final BlockEntityType<SculkSensorBlockEntity> SCULK_SENSOR = create("sculk_sensor", SculkSensorBlockEntity::new, Blocks.SCULK_SENSOR);
	public static final BlockEntityType<CalibratedSculkSensorBlockEntity> CALIBRATED_SCULK_SENSOR = create(
		"calibrated_sculk_sensor", CalibratedSculkSensorBlockEntity::new, Blocks.CALIBRATED_SCULK_SENSOR
	);
	public static final BlockEntityType<SculkCatalystBlockEntity> SCULK_CATALYST = create("sculk_catalyst", SculkCatalystBlockEntity::new, Blocks.SCULK_CATALYST);
	public static final BlockEntityType<SculkShriekerBlockEntity> SCULK_SHRIEKER = create("sculk_shrieker", SculkShriekerBlockEntity::new, Blocks.SCULK_SHRIEKER);
	public static final BlockEntityType<ChiseledBookshelfBlockEntity> CHISELED_BOOKSHELF = create(
		"chiseled_bookshelf", ChiseledBookshelfBlockEntity::new, Blocks.CHISELED_BOOKSHELF
	);
	public static final BlockEntityType<BrushableBlockEntity> BRUSHABLE_BLOCK = create(
		"brushable_block", BrushableBlockEntity::new, Blocks.SUSPICIOUS_SAND, Blocks.SUSPICIOUS_GRAVEL
	);
	public static final BlockEntityType<DecoratedPotBlockEntity> DECORATED_POT = create("decorated_pot", DecoratedPotBlockEntity::new, Blocks.DECORATED_POT);
	public static final BlockEntityType<CrafterBlockEntity> CRAFTER = create("crafter", CrafterBlockEntity::new, Blocks.CRAFTER);
	public static final BlockEntityType<TrialSpawnerBlockEntity> TRIAL_SPAWNER = create("trial_spawner", TrialSpawnerBlockEntity::new, Blocks.TRIAL_SPAWNER);
	public static final BlockEntityType<VaultBlockEntity> VAULT = create("vault", VaultBlockEntity::new, Blocks.VAULT);
	private final BlockEntityType.BlockEntityFactory<? extends T> factory;
	private final Set<Block> blocks;
	private final RegistryEntry.Reference<BlockEntityType<?>> registryEntry = Registries.BLOCK_ENTITY_TYPE.createEntry(this);

	/**
	 * {@return the block entity type's ID, or {@code null} if it is unregistered}
	 * 
	 * <p>This should never return {@code null} under normal circumstances.
	 */
	@Nullable
	public static Identifier getId(BlockEntityType<?> type) {
		return Registries.BLOCK_ENTITY_TYPE.getId(type);
	}

	private static <T extends BlockEntity> BlockEntityType<T> create(
		String id, BlockEntityType.BlockEntityFactory<? extends T> blockEntityFactory, Block... blocks
	) {
		if (blocks.length == 0) {
			LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", id);
		}

		Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, new BlockEntityType<>(blockEntityFactory, Set.of(blocks)));
	}

	private BlockEntityType(BlockEntityType.BlockEntityFactory<? extends T> factory, Set<Block> blocks) {
		this.factory = factory;
		this.blocks = blocks;
	}

	/**
	 * {@return a new instance of the block entity}
	 * 
	 * @see BlockEntityType.BlockEntityFactory
	 */
	@Nullable
	public T instantiate(BlockPos pos, BlockState state) {
		return (T)this.factory.create(pos, state);
	}

	/**
	 * {@return whether the block entity type supports {@code state}}
	 * 
	 * <p>The block, not the block state, determines the corresponding block entity type;
	 * therefore, for states of the same block, the return value is the same.
	 */
	public boolean supports(BlockState state) {
		return this.blocks.contains(state.getBlock());
	}

	@Deprecated
	public RegistryEntry.Reference<BlockEntityType<?>> getRegistryEntry() {
		return this.registryEntry;
	}

	/**
	 * {@return the block entity instance of this type at {@code pos}, or {@code null} if
	 * no such block entity exists}
	 * 
	 * @see BlockView#getBlockEntity
	 */
	@Nullable
	public T get(BlockView world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return (T)(blockEntity != null && blockEntity.getType() == this ? blockEntity : null);
	}

	/**
	 * A functional interface for a factory that creates a new block entity
	 * instance. This is usually not implemented directly; the block entity class's
	 * constructor (such as {@code MyBlockEntity::MyBlockEntity}) can be used as the
	 * implementation.
	 */
	@FunctionalInterface
	interface BlockEntityFactory<T extends BlockEntity> {
		T create(BlockPos pos, BlockState state);
	}
}
