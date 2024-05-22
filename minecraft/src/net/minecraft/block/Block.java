package net.minecraft.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.slf4j.Logger;

/**
 * A block is a voxel in a {@linkplain World world}. {@link AbstractBlock},
 * this class, and its subclasses define all logic for those voxels.
 * See the documentation on {@link AbstractBlock} for instructions on overriding
 * methods.
 * 
 * <p>There is exactly one instance for every type of block. Every stone
 * block for example in a world shares the same block instance. Each block
 * instance is registered under {@link net.minecraft.registry.Registries#BLOCK}.
 * See {@link Blocks} for examples of block instances.
 * 
 * <p>An item corresponding to a block is not automatically created. You
 * may create your own {@link net.minecraft.item.BlockItem} and register it
 * under {@link net.minecraft.registry.Registries#ITEM}.
 * 
 * <p>The translation key for the block name is determined by {@link
 * #getTranslationKey}.
 * 
 * <p>In the world, the actual voxels are not stored as blocks, but as
 * {@linkplain BlockState block states}. The possible states of the block
 * are defined by {@link #appendProperties}.
 * 
 * @see AbstractBlock
 * @see BlockState
 */
public class Block extends AbstractBlock implements ItemConvertible {
	public static final MapCodec<Block> CODEC = createCodec(Block::new);
	private static final Logger LOGGER = LogUtils.getLogger();
	private final RegistryEntry.Reference<Block> registryEntry = Registries.BLOCK.createEntry(this);
	public static final IdList<BlockState> STATE_IDS = new IdList<>();
	private static final LoadingCache<VoxelShape, Boolean> FULL_CUBE_SHAPE_CACHE = CacheBuilder.newBuilder()
		.maximumSize(512L)
		.weakKeys()
		.build(new CacheLoader<VoxelShape, Boolean>() {
			public Boolean load(VoxelShape voxelShape) {
				return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), voxelShape, BooleanBiFunction.NOT_SAME);
			}
		});
	/**
	 * Sends a neighbor update event to surrounding blocks.
	 */
	public static final int NOTIFY_NEIGHBORS = 1;
	/**
	 * Notifies listeners and clients who need to react when the block changes.
	 */
	public static final int NOTIFY_LISTENERS = 2;
	/**
	 * Used in conjunction with {@link #NOTIFY_LISTENERS} to suppress the render pass on clients.
	 */
	public static final int NO_REDRAW = 4;
	/**
	 * Forces a synchronous redraw on clients.
	 */
	public static final int REDRAW_ON_MAIN_THREAD = 8;
	/**
	 * Bypass virtual block state changes and forces the passed state to be stored as-is.
	 */
	public static final int FORCE_STATE = 16;
	/**
	 * Prevents the previous block (container) from dropping items when destroyed.
	 */
	public static final int SKIP_DROPS = 32;
	/**
	 * Signals that the current block is being moved to a different location, usually because of a piston.
	 */
	public static final int MOVED = 64;
	public static final int field_31035 = 4;
	/**
	 * The default setBlockState behavior. Same as {@code NOTIFY_NEIGHBORS | NOTIFY_LISTENERS}.
	 */
	public static final int NOTIFY_ALL = 3;
	/**
	 * Notifies neighbors and listeners, and forces a redraw on clients. Same as {@code NOTIFY_ALL | REDRAW_ON_MAIN_THREAD}
	 */
	public static final int NOTIFY_ALL_AND_REDRAW = 11;
	public static final float field_31023 = -1.0F;
	public static final float field_31024 = 0.0F;
	public static final int field_31025 = 512;
	protected final StateManager<Block, BlockState> stateManager;
	private BlockState defaultState;
	@Nullable
	private String translationKey;
	@Nullable
	private Item cachedItem;
	private static final int field_31026 = 2048;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> FACE_CULL_MAP = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(2048, 0.25F) {
			@Override
			protected void rehash(int newN) {
			}
		};
		object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteLinkedOpenHashMap;
	});

	@Override
	protected MapCodec<? extends Block> getCodec() {
		return CODEC;
	}

	public static int getRawIdFromState(@Nullable BlockState state) {
		if (state == null) {
			return 0;
		} else {
			int i = STATE_IDS.getRawId(state);
			return i == -1 ? 0 : i;
		}
	}

	public static BlockState getStateFromRawId(int stateId) {
		BlockState blockState = STATE_IDS.get(stateId);
		return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
	}

	public static Block getBlockFromItem(@Nullable Item item) {
		return item instanceof BlockItem ? ((BlockItem)item).getBlock() : Blocks.AIR;
	}

	/**
	 * Pushes entities standing on a block up before changing the block to taller ones.
	 * Without calling this, entities can fall through the block. This only needs to be called
	 * if the original block's height is smaller than 1 block.
	 * 
	 * @return the passed new block state
	 */
	public static BlockState pushEntitiesUpBeforeBlockChange(BlockState from, BlockState to, WorldAccess world, BlockPos pos) {
		VoxelShape voxelShape = VoxelShapes.combine(from.getCollisionShape(world, pos), to.getCollisionShape(world, pos), BooleanBiFunction.ONLY_SECOND)
			.offset((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		if (voxelShape.isEmpty()) {
			return to;
		} else {
			for (Entity entity : world.getOtherEntities(null, voxelShape.getBoundingBox())) {
				double d = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entity.getBoundingBox().offset(0.0, 1.0, 0.0), List.of(voxelShape), -1.0);
				entity.requestTeleportOffset(0.0, 1.0 + d, 0.0);
			}

			return to;
		}
	}

	public static VoxelShape createCuboidShape(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return VoxelShapes.cuboid(minX / 16.0, minY / 16.0, minZ / 16.0, maxX / 16.0, maxY / 16.0, maxZ / 16.0);
	}

	public static BlockState postProcessState(BlockState state, WorldAccess world, BlockPos pos) {
		BlockState blockState = state;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : DIRECTIONS) {
			mutable.set(pos, direction);
			blockState = blockState.getStateForNeighborUpdate(direction, world.getBlockState(mutable), world, pos, mutable);
		}

		return blockState;
	}

	/**
	 * Replaces the {@code state} with the {@code newState} at the {@code pos}.
	 * 
	 * <p>If the two state objects are identical, this method does nothing.
	 * 
	 * <p>If the new state {@linkplain BlockState#isAir() is air},
	 * breaks the block at the position instead.
	 * 
	 * @param newState the new block state
	 * @param world the world
	 * @param pos the position of the replaced block state
	 * @param flags the bitwise flags for {@link net.minecraft.world.ModifiableWorld#setBlockState(BlockPos, BlockState, int, int)}
	 * @param state the existing block state
	 */
	public static void replace(BlockState state, BlockState newState, WorldAccess world, BlockPos pos, int flags) {
		replace(state, newState, world, pos, flags, 512);
	}

	/**
	 * Replaces the {@code state} with the {@code newState} at the {@code pos}.
	 * 
	 * <p>If the two state objects are identical, this method does nothing.
	 * 
	 * <p>If the new state {@linkplain BlockState#isAir() is air},
	 * breaks the block at the position instead.
	 * 
	 * @param pos the position of the replaced block state
	 * @param flags the bitwise flags for {@link net.minecraft.world.ModifiableWorld#setBlockState(BlockPos, BlockState, int, int)}
	 * @param maxUpdateDepth the limit for the cascading block updates
	 * @param state the existing block state
	 * @param newState the new block state
	 * @param world the world
	 */
	public static void replace(BlockState state, BlockState newState, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
		if (newState != state) {
			if (newState.isAir()) {
				if (!world.isClient()) {
					world.breakBlock(pos, (flags & SKIP_DROPS) == 0, null, maxUpdateDepth);
				}
			} else {
				world.setBlockState(pos, newState, flags & ~SKIP_DROPS, maxUpdateDepth);
			}
		}
	}

	public Block(AbstractBlock.Settings settings) {
		super(settings);
		StateManager.Builder<Block, BlockState> builder = new StateManager.Builder<>(this);
		this.appendProperties(builder);
		this.stateManager = builder.build(Block::getDefaultState, BlockState::new);
		this.setDefaultState(this.stateManager.getDefaultState());
		if (SharedConstants.isDevelopment) {
			String string = this.getClass().getSimpleName();
			if (!string.endsWith("Block")) {
				LOGGER.error("Block classes should end with Block and {} doesn't.", string);
			}
		}
	}

	public static boolean cannotConnect(BlockState state) {
		return state.getBlock() instanceof LeavesBlock
			|| state.isOf(Blocks.BARRIER)
			|| state.isOf(Blocks.CARVED_PUMPKIN)
			|| state.isOf(Blocks.JACK_O_LANTERN)
			|| state.isOf(Blocks.MELON)
			|| state.isOf(Blocks.PUMPKIN)
			|| state.isIn(BlockTags.SHULKER_BOXES);
	}

	public static boolean shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos otherPos) {
		BlockState blockState = world.getBlockState(otherPos);
		if (state.isSideInvisible(blockState, side)) {
			return false;
		} else if (blockState.isOpaque()) {
			Block.NeighborGroup neighborGroup = new Block.NeighborGroup(state, blockState, side);
			Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = (Object2ByteLinkedOpenHashMap<Block.NeighborGroup>)FACE_CULL_MAP.get();
			byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
			if (b != 127) {
				return b != 0;
			} else {
				VoxelShape voxelShape = state.getCullingFace(world, pos, side);
				if (voxelShape.isEmpty()) {
					return true;
				} else {
					VoxelShape voxelShape2 = blockState.getCullingFace(world, otherPos, side.getOpposite());
					boolean bl = VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.ONLY_FIRST);
					if (object2ByteLinkedOpenHashMap.size() == 2048) {
						object2ByteLinkedOpenHashMap.removeLastByte();
					}

					object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
					return bl;
				}
			}
		} else {
			return true;
		}
	}

	public static boolean hasTopRim(BlockView world, BlockPos pos) {
		return world.getBlockState(pos).isSideSolid(world, pos, Direction.UP, SideShapeType.RIGID);
	}

	public static boolean sideCoversSmallSquare(WorldView world, BlockPos pos, Direction side) {
		BlockState blockState = world.getBlockState(pos);
		return side == Direction.DOWN && blockState.isIn(BlockTags.UNSTABLE_BOTTOM_CENTER) ? false : blockState.isSideSolid(world, pos, side, SideShapeType.CENTER);
	}

	public static boolean isFaceFullSquare(VoxelShape shape, Direction side) {
		VoxelShape voxelShape = shape.getFace(side);
		return isShapeFullCube(voxelShape);
	}

	public static boolean isShapeFullCube(VoxelShape shape) {
		return FULL_CUBE_SHAPE_CACHE.getUnchecked(shape);
	}

	/**
	 * Called randomly on the client. Blocks may override this to spawn particles.
	 * Unlike {@link AbstractBlock#randomTick} this is not affected by a game rule.
	 */
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
	}

	/**
	 * Called after a player breaks a block and the block is removed from the world.
	 * Explosions do not trigger this.
	 * 
	 * <p>In most cases, {@link AbstractBlock#onStateReplaced} or {@link
	 * AbstractBlock#onStacksDropped} should be used instead. Note that they are called
	 * when blocks are broken by explosions as well as players breaking them.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 * 
	 * @see AbstractBlock#onStateReplaced
	 * @see AbstractBlock#onStacksDropped
	 * @see #onBreak
	 */
	public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
	}

	public static List<ItemStack> getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity) {
		LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world)
			.add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
			.add(LootContextParameters.TOOL, ItemStack.EMPTY)
			.addOptional(LootContextParameters.BLOCK_ENTITY, blockEntity);
		return state.getDroppedStacks(builder);
	}

	public static List<ItemStack> getDroppedStacks(
		BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack
	) {
		LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world)
			.add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
			.add(LootContextParameters.TOOL, stack)
			.addOptional(LootContextParameters.THIS_ENTITY, entity)
			.addOptional(LootContextParameters.BLOCK_ENTITY, blockEntity);
		return state.getDroppedStacks(builder);
	}

	public static void dropStacks(BlockState state, World world, BlockPos pos) {
		if (world instanceof ServerWorld) {
			getDroppedStacks(state, (ServerWorld)world, pos, null).forEach(stack -> dropStack(world, pos, stack));
			state.onStacksDropped((ServerWorld)world, pos, ItemStack.EMPTY, true);
		}
	}

	public static void dropStacks(BlockState state, WorldAccess world, BlockPos pos, @Nullable BlockEntity blockEntity) {
		if (world instanceof ServerWorld) {
			getDroppedStacks(state, (ServerWorld)world, pos, blockEntity).forEach(stack -> dropStack((ServerWorld)world, pos, stack));
			state.onStacksDropped((ServerWorld)world, pos, ItemStack.EMPTY, true);
		}
	}

	public static void dropStacks(BlockState state, World world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool) {
		if (world instanceof ServerWorld) {
			getDroppedStacks(state, (ServerWorld)world, pos, blockEntity, entity, tool).forEach(stack -> dropStack(world, pos, stack));
			state.onStacksDropped((ServerWorld)world, pos, tool, true);
		}
	}

	public static void dropStack(World world, BlockPos pos, ItemStack stack) {
		double d = (double)EntityType.ITEM.getHeight() / 2.0;
		double e = (double)pos.getX() + 0.5 + MathHelper.nextDouble(world.random, -0.25, 0.25);
		double f = (double)pos.getY() + 0.5 + MathHelper.nextDouble(world.random, -0.25, 0.25) - d;
		double g = (double)pos.getZ() + 0.5 + MathHelper.nextDouble(world.random, -0.25, 0.25);
		dropStack(world, () -> new ItemEntity(world, e, f, g, stack), stack);
	}

	public static void dropStack(World world, BlockPos pos, Direction direction, ItemStack stack) {
		int i = direction.getOffsetX();
		int j = direction.getOffsetY();
		int k = direction.getOffsetZ();
		double d = (double)EntityType.ITEM.getWidth() / 2.0;
		double e = (double)EntityType.ITEM.getHeight() / 2.0;
		double f = (double)pos.getX() + 0.5 + (i == 0 ? MathHelper.nextDouble(world.random, -0.25, 0.25) : (double)i * (0.5 + d));
		double g = (double)pos.getY() + 0.5 + (j == 0 ? MathHelper.nextDouble(world.random, -0.25, 0.25) : (double)j * (0.5 + e)) - e;
		double h = (double)pos.getZ() + 0.5 + (k == 0 ? MathHelper.nextDouble(world.random, -0.25, 0.25) : (double)k * (0.5 + d));
		double l = i == 0 ? MathHelper.nextDouble(world.random, -0.1, 0.1) : (double)i * 0.1;
		double m = j == 0 ? MathHelper.nextDouble(world.random, 0.0, 0.1) : (double)j * 0.1 + 0.1;
		double n = k == 0 ? MathHelper.nextDouble(world.random, -0.1, 0.1) : (double)k * 0.1;
		dropStack(world, () -> new ItemEntity(world, f, g, h, stack, l, m, n), stack);
	}

	private static void dropStack(World world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack) {
		if (!world.isClient && !stack.isEmpty() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
			ItemEntity itemEntity = (ItemEntity)itemEntitySupplier.get();
			itemEntity.setToDefaultPickupDelay();
			world.spawnEntity(itemEntity);
		}
	}

	/**
	 * Drops experience orbs. This should be called inside {@link AbstractBlock#onStacksDropped}
	 * after {@code dropExperience} check. This does not drop experience orbs if {@link
	 * net.minecraft.world.GameRules#DO_TILE_DROPS doTileDrops} is turned off. For blocks that do
	 * not drop experience when mined with Silk Touch, consider calling {@link
	 * #dropExperienceWhenMined} instead.
	 * 
	 * @see AbstractBlock#onStacksDropped
	 * @see #dropExperienceWhenMined
	 */
	protected void dropExperience(ServerWorld world, BlockPos pos, int size) {
		if (world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
			ExperienceOrbEntity.spawn(world, Vec3d.ofCenter(pos), size);
		}
	}

	public float getBlastResistance() {
		return this.resistance;
	}

	/**
	 * Called when this block is destroyed by an explosion.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 */
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
	}

	/**
	 * Called when an entity steps on this block.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 */
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState();
	}

	/**
	 * Called server-side when the block is broken by the player using correct tool.
	 * This is called after {@link #onBroken} but has the tool requirement.
	 * By default, this increments {@link net.minecraft.stat.Stats#MINED}, adds exhaustion
	 * to the player, and drops the block's item stacks.
	 * 
	 * <p>Subclasses should override this if breaking the block causes another block to
	 * be placed (like {@link IceBlock}) or if the block can break multiple times
	 * (like {@link TurtleEggBlock}). {@link BeehiveBlock} uses this to anger the bees if
	 * the hive is mined without silk touch.
	 * 
	 * @see #onBreak
	 * @see #onBroken
	 * @see AbstractBlock#onStacksDropped
	 * @see AbstractBlock#onStateReplaced
	 */
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		player.incrementStat(Stats.MINED.getOrCreateStat(this));
		player.addExhaustion(0.005F);
		dropStacks(state, world, pos, blockEntity, player, tool);
	}

	/**
	 * Called when the player placed the block.
	 * 
	 * <p>Tall or wide blocks (such as doors or beds) should override this to place
	 * the other half of the block. Blocks with block entities can use this to copy the
	 * data from the item stack, such as the custom name.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 * 
	 * @see AbstractBlock#onBlockAdded
	 */
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
	}

	public boolean canMobSpawnInside(BlockState state) {
		return !state.isSolid() && !state.isLiquid();
	}

	public MutableText getName() {
		return Text.translatable(this.getTranslationKey());
	}

	/**
	 * {@return the translation key for the name of this block}
	 * 
	 * <p>By default, it returns {@code block.namespace.path} where {@code
	 * namespace} and {@code path} are of the identifier used for registering
	 * this block, but {@code /} in {@code path} is replaced with {@code .}.
	 * If the block is not registered, it returns {@code block.unregistered_sadface}.
	 */
	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("block", Registries.BLOCK.getId(this));
		}

		return this.translationKey;
	}

	/**
	 * Called when the entity lands on the block.
	 * 
	 * <p>Default implementation deals fall damage to the entity. Blocks that increase or
	 * reduce fall damage (like {@link HayBlock}) should override this. {@link FarmlandBlock}
	 * overrides this method to convert the block to dirt.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 */
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		entity.handleFallDamage(fallDistance, 1.0F, entity.getDamageSources().fall());
	}

	/**
	 * Called after the entity lands on the block.
	 * 
	 * <p>Default implementation resets the entity's vertical velocity. Blocks that cause
	 * entities to jump (such as {@link SlimeBlock}) should override this.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 */
	public void onEntityLand(BlockView world, Entity entity) {
		entity.setVelocity(entity.getVelocity().multiply(1.0, 0.0, 1.0));
	}

	/**
	 * {@return the new item stack when using pick block functionality}
	 * 
	 * <p>Pick block is available via middle-clicking by default. Blocks without the
	 * corresponding {@link net.minecraft.item.BlockItem}, such as crops, should
	 * override this method to return the correct item stack.
	 */
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return new ItemStack(this);
	}

	public float getSlipperiness() {
		return this.slipperiness;
	}

	public float getVelocityMultiplier() {
		return this.velocityMultiplier;
	}

	public float getJumpVelocityMultiplier() {
		return this.jumpVelocityMultiplier;
	}

	protected void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state) {
		world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, pos, getRawIdFromState(state));
	}

	/**
	 * Called when a player breaks a block before the block is removed from the world.
	 * Explosions do not trigger this.
	 * 
	 * <p>Default implementation spawns block breaking particles, angers piglins, and
	 * emits game events. Tall or wide blocks such as doors or beds should override this
	 * to break the other part (along with {@link AbstractBlock#getStateForNeighborUpdate}.)
	 * 
	 * <p>In most cases, {@link AbstractBlock#onStateReplaced} or {@link
	 * AbstractBlock#onStacksDropped} should be used instead. Note that they are called
	 * when blocks are broken by explosions as well as players breaking them.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 * 
	 * @see AbstractBlock#onStateReplaced
	 * @see AbstractBlock#onStacksDropped
	 * @see #onBroken
	 */
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		this.spawnBreakParticles(world, player, pos, state);
		if (state.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
			PiglinBrain.onGuardedBlockInteracted(player, false);
		}

		world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, state));
		return state;
	}

	/**
	 * Called randomly server-side on blocks with unobstructed sky access when it is
	 * raining or snowing. Like random ticks, only blocks within 128-block cylinder
	 * (i.e. ignoring Y coordinates) around players receive precipitation ticks. However,
	 * precipitation ticks are unaffected by the {@link
	 * net.minecraft.world.GameRules#RANDOM_TICK_SPEED randomTickSpeed} game rule, and {@link
	 * AbstractBlock.Settings#ticksRandomly} block setting is not required.
	 * 
	 * <p>{@link LeveledCauldronBlock} uses this to fill the cauldron.
	 * 
	 * @param precipitation the precipitation (snow or rain), including snow
	 * observable on high altitude
	 */
	public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
	}

	/**
	 * {@return whether an explosion can drop the block as an item}
	 * 
	 * <p>This should be overridden if an explosion affects the block in other ways,
	 * like {@link TntBlock} that triggers the chain reaction. This should not consider
	 * the randomness, since it is defined in the loot table.
	 * 
	 * @see net.minecraft.loot.condition.SurvivesExplosionLootCondition
	 */
	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return true;
	}

	/**
	 * Appends block state properties to this block. To use this, override and call {@link
	 * StateManager.Builder#add} inside the method. See {@link
	 * net.minecraft.state.property.Properties} for the list of pre-defined properties.
	 */
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
	}

	public StateManager<Block, BlockState> getStateManager() {
		return this.stateManager;
	}

	/**
	 * Sets the default state of the block. This should be called inside
	 * the block's constructor to override the default state chosen by the
	 * state manager.
	 */
	protected final void setDefaultState(BlockState state) {
		this.defaultState = state;
	}

	public final BlockState getDefaultState() {
		return this.defaultState;
	}

	/**
	 * Gets a block state with all properties that both this block and the source block state have.
	 */
	public final BlockState getStateWithProperties(BlockState state) {
		BlockState blockState = this.getDefaultState();

		for (Property<?> property : state.getBlock().getStateManager().getProperties()) {
			if (blockState.contains(property)) {
				blockState = copyProperty(state, blockState, property);
			}
		}

		return blockState;
	}

	private static <T extends Comparable<T>> BlockState copyProperty(BlockState source, BlockState target, Property<T> property) {
		return target.with(property, source.get(property));
	}

	@Override
	public Item asItem() {
		if (this.cachedItem == null) {
			this.cachedItem = Item.fromBlock(this);
		}

		return this.cachedItem;
	}

	public boolean hasDynamicBounds() {
		return this.dynamicBounds;
	}

	public String toString() {
		return "Block{" + Registries.BLOCK.getId(this) + "}";
	}

	/**
	 * Appends tooltips to a stack of this block's corresponding {@linkplain
	 * net.minecraft.item.BlockItem block item}. Used by shulker boxes.
	 * 
	 * @see Item#appendTooltip
	 */
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
	}

	@Override
	protected Block asBlock() {
		return this;
	}

	protected ImmutableMap<BlockState, VoxelShape> getShapesForStates(Function<BlockState, VoxelShape> stateToShape) {
		return (ImmutableMap<BlockState, VoxelShape>)this.stateManager.getStates().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), stateToShape));
	}

	@Deprecated
	public RegistryEntry.Reference<Block> getRegistryEntry() {
		return this.registryEntry;
	}

	/**
	 * Drops experience orbs. This should be called inside {@link AbstractBlock#onStacksDropped}
	 * after {@code dropExperience} check. This does not drop experience orbs if {@code tool}
	 * is enchanted with silk touch or if {@link net.minecraft.world.GameRules#DO_TILE_DROPS doTileDrops}
	 * is turned off.
	 * 
	 * @see AbstractBlock#onStacksDropped
	 * @see #dropExperience
	 * 
	 * @param tool the tool used to break the block, or {@link ItemStack#EMPTY} for explosions
	 */
	protected void dropExperienceWhenMined(ServerWorld world, BlockPos pos, ItemStack tool, IntProvider experience) {
		int i = EnchantmentHelper.getBlockExperience(world, tool, experience.get(world.getRandom()));
		if (i > 0) {
			this.dropExperience(world, pos, i);
		}
	}

	public static final class NeighborGroup {
		private final BlockState self;
		private final BlockState other;
		private final Direction facing;

		public NeighborGroup(BlockState self, BlockState other, Direction facing) {
			this.self = self;
			this.other = other;
			this.facing = facing;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else {
				return !(o instanceof Block.NeighborGroup neighborGroup)
					? false
					: this.self == neighborGroup.self && this.other == neighborGroup.other && this.facing == neighborGroup.facing;
			}
		}

		public int hashCode() {
			int i = this.self.hashCode();
			i = 31 * i + this.other.hashCode();
			return 31 * i + this.facing.hashCode();
		}
	}
}
