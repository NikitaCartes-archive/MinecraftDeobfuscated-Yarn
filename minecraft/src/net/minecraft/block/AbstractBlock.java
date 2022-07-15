package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

/**
 * An abstract class that defines some logic for {@link Block blocks}.
 * 
 * <p id="deprecated-methods">Deprecated methods in this class mean they
 * should only be called from the corresponding method in {@link
 * AbstractBlockState} or subclasses of this class. In vanilla subclasses,
 * these methods are called either to do the default behavior (e.g.
 * {@code super.onUse(...)}) or to delegate logic to other blocks (e.g.
 * {@link net.minecraft.block.StairsBlock#randomTick
 * StairsBlock#randomTick} calls {@code randomTick} of its base block).
 * It's fine to override them, as they are overridden by vanilla blocks.
 */
public abstract class AbstractBlock {
	protected static final Direction[] DIRECTIONS = new Direction[]{
		Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP
	};
	protected final Material material;
	protected final boolean collidable;
	protected final float resistance;
	protected final boolean randomTicks;
	protected final BlockSoundGroup soundGroup;
	protected final float slipperiness;
	protected final float velocityMultiplier;
	protected final float jumpVelocityMultiplier;
	protected final boolean dynamicBounds;
	protected final AbstractBlock.Settings settings;
	@Nullable
	protected Identifier lootTableId;

	public AbstractBlock(AbstractBlock.Settings settings) {
		this.material = settings.material;
		this.collidable = settings.collidable;
		this.lootTableId = settings.lootTableId;
		this.resistance = settings.resistance;
		this.randomTicks = settings.randomTicks;
		this.soundGroup = settings.soundGroup;
		this.slipperiness = settings.slipperiness;
		this.velocityMultiplier = settings.velocityMultiplier;
		this.jumpVelocityMultiplier = settings.jumpVelocityMultiplier;
		this.dynamicBounds = settings.dynamicBounds;
		this.settings = settings;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#prepare(WorldAccess, BlockPos, int, int)} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#canPathfindThrough} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		switch (type) {
			case LAND:
				return !state.isFullCube(world, pos);
			case WATER:
				return world.getFluidState(pos).isIn(FluidTags.WATER);
			case AIR:
				return !state.isFullCube(world, pos);
			default:
				return false;
		}
	}

	/**
	 * Gets the possibly updated block state of this block when a neighboring block is updated.
	 * 
	 * @return the new state of this block
	 * 
	 * @deprecated Consider calling {@link AbstractBlockState#getStateForNeighborUpdate} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 * 
	 * @param state the state of this block
	 * @param direction the direction from this block to the neighbor
	 * @param neighborState the state of the updated neighbor block
	 * @param world the world
	 * @param pos the position of this block
	 * @param neighborPos the position of the neighbor block
	 */
	@Deprecated
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return state;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#isSideInvisible} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return false;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#neighborUpdate} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		DebugInfoSender.sendNeighborUpdate(world, pos);
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#onBlockAdded} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
	}

	/**
	 * Called in {@link net.minecraft.world.chunk.WorldChunk#setBlockState(BlockPos, BlockState, boolean)} if {@code newState} is different from {@code state}. Vanilla blocks perform removal cleanups here.
	 * 
	 * @deprecated Consider calling {@link AbstractBlockState#onStateReplaced} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
			world.removeBlockEntity(pos);
		}
	}

	/**
	 * Called when this block is used by a player.
	 * This, by default, is bound to using the right mouse button.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution when overriding this method.
	 * The logical side can be checked using {@link net.minecraft.world.World#isClient() world.isClient()}.
	 * 
	 * <p>If the action result is successful on a logical client, then the action will be sent to the logical server for processing.
	 * 
	 * @return an action result that specifies if using the block was successful.
	 * 
	 * @deprecated Consider calling {@link AbstractBlockState#onUse} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return ActionResult.PASS;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#onSyncedBlockEvent} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		return false;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getRenderType} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#hasSidedTransparency} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public boolean hasSidedTransparency(BlockState state) {
		return false;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#emitsRedstonePower} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public boolean emitsRedstonePower(BlockState state) {
		return false;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getPistonBehavior} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public PistonBehavior getPistonBehavior(BlockState state) {
		return this.material.getPistonBehavior();
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getFluidState} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return Fluids.EMPTY.getDefaultState();
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#hasComparatorOutput} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public boolean hasComparatorOutput(BlockState state) {
		return false;
	}

	public float getMaxHorizontalModelOffset() {
		return 0.25F;
	}

	public float getVerticalModelOffsetMultiplier() {
		return 0.2F;
	}

	/**
	 * Applies a block rotation to a block state.
	 * 
	 * <p>By default, this returns the provided block state.
	 * 
	 * @return the rotated block state
	 * 
	 * @deprecated Consider calling {@link AbstractBlockState#rotate} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#mirror} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#canReplace} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return this.material.isReplaceable() && (context.getStack().isEmpty() || !context.getStack().isOf(this.asItem()));
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#canBucketPlace} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public boolean canBucketPlace(BlockState state, Fluid fluid) {
		return this.material.isReplaceable() || !this.material.isSolid();
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getDroppedStacks} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		Identifier identifier = this.getLootTableId();
		if (identifier == LootTables.EMPTY) {
			return Collections.emptyList();
		} else {
			LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
			ServerWorld serverWorld = lootContext.getWorld();
			LootTable lootTable = serverWorld.getServer().getLootManager().getTable(identifier);
			return lootTable.generateLoot(lootContext);
		}
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getRenderingSeed} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public long getRenderingSeed(BlockState state, BlockPos pos) {
		return MathHelper.hashCode(pos);
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getCullingShape} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return state.getOutlineShape(world, pos);
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getSidesShape} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		return this.getCollisionShape(state, world, pos, ShapeContext.absent());
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getRaycastShape} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getOpacity} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
		if (state.isOpaqueFullCube(world, pos)) {
			return world.getMaxLightLevel();
		} else {
			return state.isTranslucent(world, pos) ? 0 : 1;
		}
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#createScreenHandlerFactory} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Nullable
	@Deprecated
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return null;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#canPlaceAt} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return true;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getAmbientOcclusionLightLevel} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return state.isFullCube(world, pos) ? 0.2F : 1.0F;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getComparatorOutput} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return 0;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getOutlineShape(BlockView, BlockPos, ShapeContext)} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.fullCube();
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getCollisionShape(BlockView, BlockPos, ShapeContext)} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.collidable ? state.getOutlineShape(world, pos) : VoxelShapes.empty();
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#isFullCube} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
		return Block.isShapeFullCube(state.getCollisionShape(world, pos));
	}

	@Deprecated
	public boolean isCullingShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
		return Block.isShapeFullCube(state.getCullingShape(world, pos));
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getCameraCollisionShape} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getCollisionShape(state, world, pos, context);
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#randomTick} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.scheduledTick(state, world, pos, random);
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#scheduledTick} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#calcBlockBreakingDelta} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		float f = state.getHardness(world, pos);
		if (f == -1.0F) {
			return 0.0F;
		} else {
			int i = player.canHarvest(state) ? 30 : 100;
			return player.getBlockBreakingSpeed(state) / f / (float)i;
		}
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#onStacksDropped} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, boolean dropExperience) {
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#onBlockBreakStart} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getWeakRedstonePower} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return 0;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#onEntityCollision} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#getStrongRedstonePower} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return 0;
	}

	public final Identifier getLootTableId() {
		if (this.lootTableId == null) {
			Identifier identifier = Registry.BLOCK.getId(this.asBlock());
			this.lootTableId = new Identifier(identifier.getNamespace(), "blocks/" + identifier.getPath());
		}

		return this.lootTableId;
	}

	/**
	 * @deprecated Consider calling {@link AbstractBlockState#onProjectileHit} instead. See <a href="#deprecated-methods">the class javadoc</a>.
	 */
	@Deprecated
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
	}

	public abstract Item asItem();

	protected abstract Block asBlock();

	public MapColor getDefaultMapColor() {
		return (MapColor)this.settings.mapColorProvider.apply(this.asBlock().getDefaultState());
	}

	public float getHardness() {
		return this.settings.hardness;
	}

	public abstract static class AbstractBlockState extends State<Block, BlockState> {
		private final int luminance;
		private final boolean hasSidedTransparency;
		private final boolean isAir;
		private final Material material;
		private final MapColor mapColor;
		private final float hardness;
		private final boolean toolRequired;
		private final boolean opaque;
		private final AbstractBlock.ContextPredicate solidBlockPredicate;
		private final AbstractBlock.ContextPredicate suffocationPredicate;
		private final AbstractBlock.ContextPredicate blockVisionPredicate;
		private final AbstractBlock.ContextPredicate postProcessPredicate;
		private final AbstractBlock.ContextPredicate emissiveLightingPredicate;
		private final AbstractBlock.OffsetType offsetType;
		@Nullable
		protected AbstractBlock.AbstractBlockState.ShapeCache shapeCache;

		protected AbstractBlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> codec) {
			super(block, propertyMap, codec);
			AbstractBlock.Settings settings = block.settings;
			this.luminance = settings.luminance.applyAsInt(this.asBlockState());
			this.hasSidedTransparency = block.hasSidedTransparency(this.asBlockState());
			this.isAir = settings.isAir;
			this.material = settings.material;
			this.mapColor = (MapColor)settings.mapColorProvider.apply(this.asBlockState());
			this.hardness = settings.hardness;
			this.toolRequired = settings.toolRequired;
			this.opaque = settings.opaque;
			this.solidBlockPredicate = settings.solidBlockPredicate;
			this.suffocationPredicate = settings.suffocationPredicate;
			this.blockVisionPredicate = settings.blockVisionPredicate;
			this.postProcessPredicate = settings.postProcessPredicate;
			this.emissiveLightingPredicate = settings.emissiveLightingPredicate;
			this.offsetType = (AbstractBlock.OffsetType)settings.offsetType.apply(this.asBlockState());
		}

		public void initShapeCache() {
			if (!this.getBlock().hasDynamicBounds()) {
				this.shapeCache = new AbstractBlock.AbstractBlockState.ShapeCache(this.asBlockState());
			}
		}

		public Block getBlock() {
			return this.owner;
		}

		public RegistryEntry<Block> getRegistryEntry() {
			return this.owner.getRegistryEntry();
		}

		public Material getMaterial() {
			return this.material;
		}

		public boolean allowsSpawning(BlockView world, BlockPos pos, EntityType<?> type) {
			return this.getBlock().settings.allowsSpawningPredicate.test(this.asBlockState(), world, pos, type);
		}

		public boolean isTranslucent(BlockView world, BlockPos pos) {
			return this.shapeCache != null ? this.shapeCache.translucent : this.getBlock().isTranslucent(this.asBlockState(), world, pos);
		}

		public int getOpacity(BlockView world, BlockPos pos) {
			return this.shapeCache != null ? this.shapeCache.lightSubtracted : this.getBlock().getOpacity(this.asBlockState(), world, pos);
		}

		public VoxelShape getCullingFace(BlockView world, BlockPos pos, Direction direction) {
			return this.shapeCache != null && this.shapeCache.extrudedFaces != null
				? this.shapeCache.extrudedFaces[direction.ordinal()]
				: VoxelShapes.extrudeFace(this.getCullingShape(world, pos), direction);
		}

		public VoxelShape getCullingShape(BlockView world, BlockPos pos) {
			return this.getBlock().getCullingShape(this.asBlockState(), world, pos);
		}

		public boolean exceedsCube() {
			return this.shapeCache == null || this.shapeCache.exceedsCube;
		}

		public boolean hasSidedTransparency() {
			return this.hasSidedTransparency;
		}

		/**
		 * {@return the light level emitted by this block state}
		 */
		public int getLuminance() {
			return this.luminance;
		}

		public boolean isAir() {
			return this.isAir;
		}

		public MapColor getMapColor(BlockView world, BlockPos pos) {
			return this.mapColor;
		}

		public BlockState rotate(BlockRotation rotation) {
			return this.getBlock().rotate(this.asBlockState(), rotation);
		}

		public BlockState mirror(BlockMirror mirror) {
			return this.getBlock().mirror(this.asBlockState(), mirror);
		}

		public BlockRenderType getRenderType() {
			return this.getBlock().getRenderType(this.asBlockState());
		}

		public boolean hasEmissiveLighting(BlockView world, BlockPos pos) {
			return this.emissiveLightingPredicate.test(this.asBlockState(), world, pos);
		}

		public float getAmbientOcclusionLightLevel(BlockView world, BlockPos pos) {
			return this.getBlock().getAmbientOcclusionLightLevel(this.asBlockState(), world, pos);
		}

		public boolean isSolidBlock(BlockView world, BlockPos pos) {
			return this.solidBlockPredicate.test(this.asBlockState(), world, pos);
		}

		public boolean emitsRedstonePower() {
			return this.getBlock().emitsRedstonePower(this.asBlockState());
		}

		public int getWeakRedstonePower(BlockView world, BlockPos pos, Direction direction) {
			return this.getBlock().getWeakRedstonePower(this.asBlockState(), world, pos, direction);
		}

		public boolean hasComparatorOutput() {
			return this.getBlock().hasComparatorOutput(this.asBlockState());
		}

		public int getComparatorOutput(World world, BlockPos pos) {
			return this.getBlock().getComparatorOutput(this.asBlockState(), world, pos);
		}

		public float getHardness(BlockView world, BlockPos pos) {
			return this.hardness;
		}

		public float calcBlockBreakingDelta(PlayerEntity player, BlockView world, BlockPos pos) {
			return this.getBlock().calcBlockBreakingDelta(this.asBlockState(), player, world, pos);
		}

		public int getStrongRedstonePower(BlockView world, BlockPos pos, Direction direction) {
			return this.getBlock().getStrongRedstonePower(this.asBlockState(), world, pos, direction);
		}

		public PistonBehavior getPistonBehavior() {
			return this.getBlock().getPistonBehavior(this.asBlockState());
		}

		public boolean isOpaqueFullCube(BlockView world, BlockPos pos) {
			if (this.shapeCache != null) {
				return this.shapeCache.fullOpaque;
			} else {
				BlockState blockState = this.asBlockState();
				return blockState.isOpaque() ? Block.isShapeFullCube(blockState.getCullingShape(world, pos)) : false;
			}
		}

		public boolean isOpaque() {
			return this.opaque;
		}

		public boolean isSideInvisible(BlockState state, Direction direction) {
			return this.getBlock().isSideInvisible(this.asBlockState(), state, direction);
		}

		public VoxelShape getOutlineShape(BlockView world, BlockPos pos) {
			return this.getOutlineShape(world, pos, ShapeContext.absent());
		}

		public VoxelShape getOutlineShape(BlockView world, BlockPos pos, ShapeContext context) {
			return this.getBlock().getOutlineShape(this.asBlockState(), world, pos, context);
		}

		public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
			return this.shapeCache != null ? this.shapeCache.collisionShape : this.getCollisionShape(world, pos, ShapeContext.absent());
		}

		public VoxelShape getCollisionShape(BlockView world, BlockPos pos, ShapeContext context) {
			return this.getBlock().getCollisionShape(this.asBlockState(), world, pos, context);
		}

		public VoxelShape getSidesShape(BlockView world, BlockPos pos) {
			return this.getBlock().getSidesShape(this.asBlockState(), world, pos);
		}

		public VoxelShape getCameraCollisionShape(BlockView world, BlockPos pos, ShapeContext context) {
			return this.getBlock().getCameraCollisionShape(this.asBlockState(), world, pos, context);
		}

		public VoxelShape getRaycastShape(BlockView world, BlockPos pos) {
			return this.getBlock().getRaycastShape(this.asBlockState(), world, pos);
		}

		public final boolean hasSolidTopSurface(BlockView world, BlockPos pos, Entity entity) {
			return this.isSolidSurface(world, pos, entity, Direction.UP);
		}

		public final boolean isSolidSurface(BlockView world, BlockPos pos, Entity entity, Direction direction) {
			return Block.isFaceFullSquare(this.getCollisionShape(world, pos, ShapeContext.of(entity)), direction);
		}

		public Vec3d getModelOffset(BlockView world, BlockPos pos) {
			if (this.offsetType == AbstractBlock.OffsetType.NONE) {
				return Vec3d.ZERO;
			} else {
				Block block = this.getBlock();
				long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
				float f = block.getMaxHorizontalModelOffset();
				double d = MathHelper.clamp(((double)((float)(l & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
				double e = this.offsetType == AbstractBlock.OffsetType.XYZ
					? ((double)((float)(l >> 4 & 15L) / 15.0F) - 1.0) * (double)block.getVerticalModelOffsetMultiplier()
					: 0.0;
				double g = MathHelper.clamp(((double)((float)(l >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
				return new Vec3d(d, e, g);
			}
		}

		public boolean onSyncedBlockEvent(World world, BlockPos pos, int type, int data) {
			return this.getBlock().onSyncedBlockEvent(this.asBlockState(), world, pos, type, data);
		}

		@Deprecated
		public void neighborUpdate(World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
			this.getBlock().neighborUpdate(this.asBlockState(), world, pos, sourceBlock, sourcePos, notify);
		}

		public final void updateNeighbors(WorldAccess world, BlockPos pos, int flags) {
			this.updateNeighbors(world, pos, flags, 512);
		}

		public final void updateNeighbors(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
			this.getBlock();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (Direction direction : AbstractBlock.DIRECTIONS) {
				mutable.set(pos, direction);
				world.replaceWithStateForNeighborUpdate(direction.getOpposite(), this.asBlockState(), mutable, pos, flags, maxUpdateDepth);
			}
		}

		public final void prepare(WorldAccess world, BlockPos pos, int flags) {
			this.prepare(world, pos, flags, 512);
		}

		public void prepare(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
			this.getBlock().prepare(this.asBlockState(), world, pos, flags, maxUpdateDepth);
		}

		public void onBlockAdded(World world, BlockPos pos, BlockState state, boolean notify) {
			this.getBlock().onBlockAdded(this.asBlockState(), world, pos, state, notify);
		}

		public void onStateReplaced(World world, BlockPos pos, BlockState state, boolean moved) {
			this.getBlock().onStateReplaced(this.asBlockState(), world, pos, state, moved);
		}

		public void scheduledTick(ServerWorld world, BlockPos pos, Random random) {
			this.getBlock().scheduledTick(this.asBlockState(), world, pos, random);
		}

		public void randomTick(ServerWorld world, BlockPos pos, Random random) {
			this.getBlock().randomTick(this.asBlockState(), world, pos, random);
		}

		public void onEntityCollision(World world, BlockPos pos, Entity entity) {
			this.getBlock().onEntityCollision(this.asBlockState(), world, pos, entity);
		}

		public void onStacksDropped(ServerWorld world, BlockPos pos, ItemStack stack, boolean dropExperience) {
			this.getBlock().onStacksDropped(this.asBlockState(), world, pos, stack, dropExperience);
		}

		public List<ItemStack> getDroppedStacks(LootContext.Builder builder) {
			return this.getBlock().getDroppedStacks(this.asBlockState(), builder);
		}

		public ActionResult onUse(World world, PlayerEntity player, Hand hand, BlockHitResult hit) {
			return this.getBlock().onUse(this.asBlockState(), world, hit.getBlockPos(), player, hand, hit);
		}

		public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
			this.getBlock().onBlockBreakStart(this.asBlockState(), world, pos, player);
		}

		public boolean shouldSuffocate(BlockView world, BlockPos pos) {
			return this.suffocationPredicate.test(this.asBlockState(), world, pos);
		}

		public boolean shouldBlockVision(BlockView world, BlockPos pos) {
			return this.blockVisionPredicate.test(this.asBlockState(), world, pos);
		}

		/**
		 * Gets the possibly updated block state of this block when a neighboring block is updated.
		 * 
		 * @return the new state of this block
		 * 
		 * @param direction the direction from this block to the neighbor
		 * @param neighborState the state of the updated neighbor block
		 * @param world the world
		 * @param pos the position of this block
		 * @param neighborPos the position of the neighbor block
		 */
		public BlockState getStateForNeighborUpdate(Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
			return this.getBlock().getStateForNeighborUpdate(this.asBlockState(), direction, neighborState, world, pos, neighborPos);
		}

		public boolean canPathfindThrough(BlockView world, BlockPos pos, NavigationType type) {
			return this.getBlock().canPathfindThrough(this.asBlockState(), world, pos, type);
		}

		public boolean canReplace(ItemPlacementContext context) {
			return this.getBlock().canReplace(this.asBlockState(), context);
		}

		public boolean canBucketPlace(Fluid fluid) {
			return this.getBlock().canBucketPlace(this.asBlockState(), fluid);
		}

		public boolean canPlaceAt(WorldView world, BlockPos pos) {
			return this.getBlock().canPlaceAt(this.asBlockState(), world, pos);
		}

		public boolean shouldPostProcess(BlockView world, BlockPos pos) {
			return this.postProcessPredicate.test(this.asBlockState(), world, pos);
		}

		@Nullable
		public NamedScreenHandlerFactory createScreenHandlerFactory(World world, BlockPos pos) {
			return this.getBlock().createScreenHandlerFactory(this.asBlockState(), world, pos);
		}

		public boolean isIn(TagKey<Block> tag) {
			return this.getBlock().getRegistryEntry().isIn(tag);
		}

		public boolean isIn(TagKey<Block> tag, Predicate<AbstractBlock.AbstractBlockState> predicate) {
			return this.isIn(tag) && predicate.test(this);
		}

		public boolean isIn(RegistryEntryList<Block> blocks) {
			return blocks.contains(this.getBlock().getRegistryEntry());
		}

		public Stream<TagKey<Block>> streamTags() {
			return this.getBlock().getRegistryEntry().streamTags();
		}

		public boolean hasBlockEntity() {
			return this.getBlock() instanceof BlockEntityProvider;
		}

		@Nullable
		public <T extends BlockEntity> BlockEntityTicker<T> getBlockEntityTicker(World world, BlockEntityType<T> blockEntityType) {
			return this.getBlock() instanceof BlockEntityProvider ? ((BlockEntityProvider)this.getBlock()).getTicker(world, this.asBlockState(), blockEntityType) : null;
		}

		public boolean isOf(Block block) {
			return this.getBlock() == block;
		}

		public FluidState getFluidState() {
			return this.getBlock().getFluidState(this.asBlockState());
		}

		public boolean hasRandomTicks() {
			return this.getBlock().hasRandomTicks(this.asBlockState());
		}

		public long getRenderingSeed(BlockPos pos) {
			return this.getBlock().getRenderingSeed(this.asBlockState(), pos);
		}

		public BlockSoundGroup getSoundGroup() {
			return this.getBlock().getSoundGroup(this.asBlockState());
		}

		public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
			this.getBlock().onProjectileHit(world, state, hit, projectile);
		}

		public boolean isSideSolidFullSquare(BlockView world, BlockPos pos, Direction direction) {
			return this.isSideSolid(world, pos, direction, SideShapeType.FULL);
		}

		public boolean isSideSolid(BlockView world, BlockPos pos, Direction direction, SideShapeType shapeType) {
			return this.shapeCache != null ? this.shapeCache.isSideSolid(direction, shapeType) : shapeType.matches(this.asBlockState(), world, pos, direction);
		}

		public boolean isFullCube(BlockView world, BlockPos pos) {
			return this.shapeCache != null ? this.shapeCache.isFullCube : this.getBlock().isShapeFullCube(this.asBlockState(), world, pos);
		}

		protected abstract BlockState asBlockState();

		public boolean isToolRequired() {
			return this.toolRequired;
		}

		public AbstractBlock.OffsetType getOffsetType() {
			return this.offsetType;
		}

		static final class ShapeCache {
			private static final Direction[] DIRECTIONS = Direction.values();
			private static final int SHAPE_TYPE_LENGTH = SideShapeType.values().length;
			protected final boolean fullOpaque;
			final boolean translucent;
			final int lightSubtracted;
			@Nullable
			final VoxelShape[] extrudedFaces;
			protected final VoxelShape collisionShape;
			protected final boolean exceedsCube;
			private final boolean[] solidSides;
			protected final boolean isFullCube;

			ShapeCache(BlockState state) {
				Block block = state.getBlock();
				this.fullOpaque = state.isOpaqueFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
				this.translucent = block.isTranslucent(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
				this.lightSubtracted = block.getOpacity(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
				if (!state.isOpaque()) {
					this.extrudedFaces = null;
				} else {
					this.extrudedFaces = new VoxelShape[DIRECTIONS.length];
					VoxelShape voxelShape = block.getCullingShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);

					for (Direction direction : DIRECTIONS) {
						this.extrudedFaces[direction.ordinal()] = VoxelShapes.extrudeFace(voxelShape, direction);
					}
				}

				this.collisionShape = block.getCollisionShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, ShapeContext.absent());
				if (!this.collisionShape.isEmpty() && state.getOffsetType() != AbstractBlock.OffsetType.NONE) {
					throw new IllegalStateException(
						String.format(
							Locale.ROOT, "%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.", Registry.BLOCK.getId(block)
						)
					);
				} else {
					this.exceedsCube = Arrays.stream(Direction.Axis.values())
						.anyMatch(axis -> this.collisionShape.getMin(axis) < 0.0 || this.collisionShape.getMax(axis) > 1.0);
					this.solidSides = new boolean[DIRECTIONS.length * SHAPE_TYPE_LENGTH];

					for (Direction direction2 : DIRECTIONS) {
						for (SideShapeType sideShapeType : SideShapeType.values()) {
							this.solidSides[indexSolidSide(direction2, sideShapeType)] = sideShapeType.matches(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction2);
						}
					}

					this.isFullCube = Block.isShapeFullCube(state.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
				}
			}

			public boolean isSideSolid(Direction direction, SideShapeType shapeType) {
				return this.solidSides[indexSolidSide(direction, shapeType)];
			}

			private static int indexSolidSide(Direction direction, SideShapeType shapeType) {
				return direction.ordinal() * SHAPE_TYPE_LENGTH + shapeType.ordinal();
			}
		}
	}

	public interface ContextPredicate {
		boolean test(BlockState state, BlockView world, BlockPos pos);
	}

	public static enum OffsetType {
		NONE,
		XZ,
		XYZ;
	}

	public static class Settings {
		Material material;
		Function<BlockState, MapColor> mapColorProvider;
		boolean collidable = true;
		BlockSoundGroup soundGroup = BlockSoundGroup.STONE;
		ToIntFunction<BlockState> luminance = state -> 0;
		float resistance;
		float hardness;
		boolean toolRequired;
		boolean randomTicks;
		float slipperiness = 0.6F;
		float velocityMultiplier = 1.0F;
		float jumpVelocityMultiplier = 1.0F;
		Identifier lootTableId;
		boolean opaque = true;
		boolean isAir;
		AbstractBlock.TypedContextPredicate<EntityType<?>> allowsSpawningPredicate = (state, world, pos, type) -> state.isSideSolidFullSquare(
					world, pos, Direction.UP
				)
				&& state.getLuminance() < 14;
		AbstractBlock.ContextPredicate solidBlockPredicate = (state, world, pos) -> state.getMaterial().blocksLight() && state.isFullCube(world, pos);
		AbstractBlock.ContextPredicate suffocationPredicate = (state, world, pos) -> this.material.blocksMovement() && state.isFullCube(world, pos);
		AbstractBlock.ContextPredicate blockVisionPredicate = this.suffocationPredicate;
		AbstractBlock.ContextPredicate postProcessPredicate = (state, world, pos) -> false;
		AbstractBlock.ContextPredicate emissiveLightingPredicate = (state, world, pos) -> false;
		boolean dynamicBounds;
		Function<BlockState, AbstractBlock.OffsetType> offsetType = state -> AbstractBlock.OffsetType.NONE;

		private Settings(Material material, MapColor mapColorProvider) {
			this(material, state -> mapColorProvider);
		}

		private Settings(Material material, Function<BlockState, MapColor> mapColorProvider) {
			this.material = material;
			this.mapColorProvider = mapColorProvider;
		}

		public static AbstractBlock.Settings of(Material material) {
			return of(material, material.getColor());
		}

		public static AbstractBlock.Settings of(Material material, DyeColor color) {
			return of(material, color.getMapColor());
		}

		public static AbstractBlock.Settings of(Material material, MapColor color) {
			return new AbstractBlock.Settings(material, color);
		}

		public static AbstractBlock.Settings of(Material material, Function<BlockState, MapColor> mapColor) {
			return new AbstractBlock.Settings(material, mapColor);
		}

		public static AbstractBlock.Settings copy(AbstractBlock block) {
			AbstractBlock.Settings settings = new AbstractBlock.Settings(block.material, block.settings.mapColorProvider);
			settings.material = block.settings.material;
			settings.hardness = block.settings.hardness;
			settings.resistance = block.settings.resistance;
			settings.collidable = block.settings.collidable;
			settings.randomTicks = block.settings.randomTicks;
			settings.luminance = block.settings.luminance;
			settings.mapColorProvider = block.settings.mapColorProvider;
			settings.soundGroup = block.settings.soundGroup;
			settings.slipperiness = block.settings.slipperiness;
			settings.velocityMultiplier = block.settings.velocityMultiplier;
			settings.dynamicBounds = block.settings.dynamicBounds;
			settings.opaque = block.settings.opaque;
			settings.isAir = block.settings.isAir;
			settings.toolRequired = block.settings.toolRequired;
			settings.offsetType = block.settings.offsetType;
			return settings;
		}

		/**
		 * Specifies that a block should have no collision bounds.
		 * 
		 * <p>This also marks a block as non-opaque.
		 */
		public AbstractBlock.Settings noCollision() {
			this.collidable = false;
			this.opaque = false;
			return this;
		}

		/**
		 * Specifies that a block should be non-opaque and light should be allowed to pass through.
		 */
		public AbstractBlock.Settings nonOpaque() {
			this.opaque = false;
			return this;
		}

		public AbstractBlock.Settings slipperiness(float slipperiness) {
			this.slipperiness = slipperiness;
			return this;
		}

		public AbstractBlock.Settings velocityMultiplier(float velocityMultiplier) {
			this.velocityMultiplier = velocityMultiplier;
			return this;
		}

		public AbstractBlock.Settings jumpVelocityMultiplier(float jumpVelocityMultiplier) {
			this.jumpVelocityMultiplier = jumpVelocityMultiplier;
			return this;
		}

		public AbstractBlock.Settings sounds(BlockSoundGroup soundGroup) {
			this.soundGroup = soundGroup;
			return this;
		}

		/**
		 * Specifies the light level emitted by a block.
		 * 
		 * @param luminance a per block state light level, with values between 0 and 15
		 */
		public AbstractBlock.Settings luminance(ToIntFunction<BlockState> luminance) {
			this.luminance = luminance;
			return this;
		}

		public AbstractBlock.Settings strength(float hardness, float resistance) {
			return this.hardness(hardness).resistance(resistance);
		}

		/**
		 * Specifies that a block is broken instantly.
		 */
		public AbstractBlock.Settings breakInstantly() {
			return this.strength(0.0F);
		}

		public AbstractBlock.Settings strength(float strength) {
			this.strength(strength, strength);
			return this;
		}

		public AbstractBlock.Settings ticksRandomly() {
			this.randomTicks = true;
			return this;
		}

		/**
		 * Specifies that a block's collision bounds can dynamically resize.
		 * By default, block collision bounds are cached for performance.
		 * By invoking this method, the game will not cache the block collision bounds and instead calculate the collision bounds when needed.
		 */
		public AbstractBlock.Settings dynamicBounds() {
			this.dynamicBounds = true;
			return this;
		}

		public AbstractBlock.Settings dropsNothing() {
			this.lootTableId = LootTables.EMPTY;
			return this;
		}

		/**
		 * Specifies that a block should drop the same items as a provided block.
		 * 
		 * @param source the block to copy item drops from
		 */
		public AbstractBlock.Settings dropsLike(Block source) {
			this.lootTableId = source.getLootTableId();
			return this;
		}

		public AbstractBlock.Settings air() {
			this.isAir = true;
			return this;
		}

		/**
		 * Specifies logic that calculates whether an entity can spawn on a block.
		 * 
		 * @param predicate the predicate used to calculate whether an entity can spawn on this block
		 */
		public AbstractBlock.Settings allowsSpawning(AbstractBlock.TypedContextPredicate<EntityType<?>> predicate) {
			this.allowsSpawningPredicate = predicate;
			return this;
		}

		public AbstractBlock.Settings solidBlock(AbstractBlock.ContextPredicate predicate) {
			this.solidBlockPredicate = predicate;
			return this;
		}

		/**
		 * Specifies logic that calculates whether an entity should suffocate if inside of a block.
		 */
		public AbstractBlock.Settings suffocates(AbstractBlock.ContextPredicate predicate) {
			this.suffocationPredicate = predicate;
			return this;
		}

		public AbstractBlock.Settings blockVision(AbstractBlock.ContextPredicate predicate) {
			this.blockVisionPredicate = predicate;
			return this;
		}

		public AbstractBlock.Settings postProcess(AbstractBlock.ContextPredicate predicate) {
			this.postProcessPredicate = predicate;
			return this;
		}

		public AbstractBlock.Settings emissiveLighting(AbstractBlock.ContextPredicate predicate) {
			this.emissiveLightingPredicate = predicate;
			return this;
		}

		public AbstractBlock.Settings requiresTool() {
			this.toolRequired = true;
			return this;
		}

		public AbstractBlock.Settings mapColor(MapColor color) {
			this.mapColorProvider = state -> color;
			return this;
		}

		public AbstractBlock.Settings hardness(float hardness) {
			this.hardness = hardness;
			return this;
		}

		public AbstractBlock.Settings resistance(float resistance) {
			this.resistance = Math.max(0.0F, resistance);
			return this;
		}

		public AbstractBlock.Settings offsetType(AbstractBlock.OffsetType offsetType) {
			return this.offsetType(state -> offsetType);
		}

		public AbstractBlock.Settings offsetType(Function<BlockState, AbstractBlock.OffsetType> offsetType) {
			this.offsetType = offsetType;
			return this;
		}
	}

	public interface TypedContextPredicate<A> {
		boolean test(BlockState state, BlockView world, BlockPos pos, A type);
	}
}
