package net.minecraft.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.IdList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block implements ItemConvertible {
	protected static final Logger LOGGER = LogManager.getLogger();
	public static final IdList<BlockState> STATE_IDS = new IdList<>();
	private static final Direction[] FACINGS = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
	private static final LoadingCache<VoxelShape, Boolean> FULL_CUBE_SHAPE_CACHE = CacheBuilder.newBuilder()
		.maximumSize(512L)
		.weakKeys()
		.build(new CacheLoader<VoxelShape, Boolean>() {
			public Boolean method_20516(VoxelShape voxelShape) {
				return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), voxelShape, BooleanBiFunction.NOT_SAME);
			}
		});
	private static final VoxelShape SOLID_MEDIUM_SQUARE_SHAPE = VoxelShapes.combineAndSimplify(
		VoxelShapes.fullCube(), createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST
	);
	private static final VoxelShape SOLID_SMALL_SQUARE_SHAPE = createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);
	protected final int lightLevel;
	protected final float hardness;
	protected final float resistance;
	protected final boolean randomTicks;
	protected final BlockSoundGroup soundGroup;
	protected final Material material;
	protected final MaterialColor materialColor;
	private final float slipperiness;
	private final float field_21207;
	private final float field_21208;
	protected final StateManager<Block, BlockState> stateManager;
	private BlockState defaultState;
	protected final boolean collidable;
	private final boolean dynamicBounds;
	private final boolean opaque;
	@Nullable
	private Identifier dropTableId;
	@Nullable
	private String translationKey;
	@Nullable
	private Item cachedItem;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> FACE_CULL_MAP = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(200) {
			@Override
			protected void rehash(int i) {
			}
		};
		object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteLinkedOpenHashMap;
	});

	public static int getRawIdFromState(@Nullable BlockState state) {
		if (state == null) {
			return 0;
		} else {
			int i = STATE_IDS.getId(state);
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

	public static BlockState pushEntitiesUpBeforeBlockChange(BlockState from, BlockState to, World world, BlockPos pos) {
		VoxelShape voxelShape = VoxelShapes.combine(from.getCollisionShape(world, pos), to.getCollisionShape(world, pos), BooleanBiFunction.ONLY_SECOND)
			.offset((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());

		for (Entity entity : world.getEntities(null, voxelShape.getBoundingBox())) {
			double d = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entity.getBoundingBox().offset(0.0, 1.0, 0.0), Stream.of(voxelShape), -1.0);
			entity.requestTeleport(entity.getX(), entity.getY() + 1.0 + d, entity.getZ());
		}

		return to;
	}

	public static VoxelShape createCuboidShape(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		return VoxelShapes.cuboid(xMin / 16.0, yMin / 16.0, zMin / 16.0, xMax / 16.0, yMax / 16.0, zMax / 16.0);
	}

	@Deprecated
	public boolean allowsSpawning(BlockState state, BlockView view, BlockPos pos, EntityType<?> type) {
		return state.isSideSolidFullSquare(view, pos, Direction.UP) && this.lightLevel < 14;
	}

	@Deprecated
	public boolean isAir(BlockState state) {
		return false;
	}

	@Deprecated
	public int getLuminance(BlockState state) {
		return this.lightLevel;
	}

	@Deprecated
	public Material getMaterial(BlockState state) {
		return this.material;
	}

	@Deprecated
	public MaterialColor getMapColor(BlockState state, BlockView view, BlockPos pos) {
		return this.materialColor;
	}

	@Deprecated
	public void updateNeighborStates(BlockState state, IWorld world, BlockPos pos, int flags) {
		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : FACINGS) {
				pooledMutable.method_10114(pos).method_10118(direction);
				BlockState blockState = world.getBlockState(pooledMutable);
				BlockState blockState2 = blockState.getStateForNeighborUpdate(direction.getOpposite(), state, world, pooledMutable, pos);
				replaceBlock(blockState, blockState2, world, pooledMutable, flags);
			}
		}
	}

	public boolean matches(Tag<Block> tag) {
		return tag.contains(this);
	}

	public static BlockState getRenderingState(BlockState state, IWorld world, BlockPos pos) {
		BlockState blockState = state;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : FACINGS) {
			mutable.set(pos).setOffset(direction);
			blockState = blockState.getStateForNeighborUpdate(direction, world.getBlockState(mutable), world, pos, mutable);
		}

		return blockState;
	}

	public static void replaceBlock(BlockState state, BlockState newState, IWorld world, BlockPos pos, int flags) {
		if (newState != state) {
			if (newState.isAir()) {
				if (!world.isClient()) {
					world.breakBlock(pos, (flags & 32) == 0);
				}
			} else {
				world.setBlockState(pos, newState, flags & -33);
			}
		}
	}

	@Deprecated
	public void method_9517(BlockState state, IWorld world, BlockPos pos, int flags) {
	}

	@Deprecated
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return state;
	}

	@Deprecated
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state;
	}

	@Deprecated
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state;
	}

	public Block(Block.Settings settings) {
		StateManager.Builder<Block, BlockState> builder = new StateManager.Builder<>(this);
		this.appendProperties(builder);
		this.material = settings.material;
		this.materialColor = settings.materialColor;
		this.collidable = settings.collidable;
		this.soundGroup = settings.soundGroup;
		this.lightLevel = settings.luminance;
		this.resistance = settings.resistance;
		this.hardness = settings.hardness;
		this.randomTicks = settings.randomTicks;
		this.slipperiness = settings.slipperiness;
		this.field_21207 = settings.field_21209;
		this.field_21208 = settings.field_21210;
		this.dynamicBounds = settings.dynamicBounds;
		this.dropTableId = settings.dropTableId;
		this.opaque = settings.opaque;
		this.stateManager = builder.build(BlockState::new);
		this.setDefaultState(this.stateManager.getDefaultState());
	}

	public static boolean canConnect(Block block) {
		return block instanceof LeavesBlock
			|| block == Blocks.BARRIER
			|| block == Blocks.CARVED_PUMPKIN
			|| block == Blocks.JACK_O_LANTERN
			|| block == Blocks.MELON
			|| block == Blocks.PUMPKIN
			|| block.matches(BlockTags.SHULKER_BOXES);
	}

	@Deprecated
	public boolean isSimpleFullBlock(BlockState state, BlockView view, BlockPos pos) {
		return state.getMaterial().blocksLight() && state.isFullCube(view, pos) && !state.emitsRedstonePower();
	}

	@Deprecated
	public boolean canSuffocate(BlockState state, BlockView view, BlockPos pos) {
		return this.material.blocksMovement() && state.isFullCube(view, pos);
	}

	@Deprecated
	public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
		switch (env) {
			case LAND:
				return !world.isFullCube(view, pos);
			case WATER:
				return view.getFluidState(pos).matches(FluidTags.WATER);
			case AIR:
				return !world.isFullCube(view, pos);
			default:
				return false;
		}
	}

	@Deprecated
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Deprecated
	public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
		return this.material.isReplaceable() && (ctx.getStack().isEmpty() || ctx.getStack().getItem() != this.asItem());
	}

	@Deprecated
	public boolean canBucketPlace(BlockState state, Fluid fluid) {
		return this.material.isReplaceable() || !this.material.isSolid();
	}

	@Deprecated
	public float getHardness(BlockState state, BlockView world, BlockPos pos) {
		return this.hardness;
	}

	public boolean hasRandomTicks(BlockState state) {
		return this.randomTicks;
	}

	public boolean hasBlockEntity() {
		return this instanceof BlockEntityProvider;
	}

	@Deprecated
	public boolean shouldPostProcess(BlockState state, BlockView view, BlockPos pos) {
		return false;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public boolean hasEmissiveLighting(BlockState blockState) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public static boolean shouldDrawSide(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		BlockPos blockPos = pos.offset(facing);
		BlockState blockState = view.getBlockState(blockPos);
		if (state.isSideInvisible(blockState, facing)) {
			return false;
		} else if (blockState.isOpaque()) {
			Block.NeighborGroup neighborGroup = new Block.NeighborGroup(state, blockState, facing);
			Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = (Object2ByteLinkedOpenHashMap<Block.NeighborGroup>)FACE_CULL_MAP.get();
			byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
			if (b != 127) {
				return b != 0;
			} else {
				VoxelShape voxelShape = state.getCullingFace(view, pos, facing);
				VoxelShape voxelShape2 = blockState.getCullingFace(view, blockPos, facing.getOpposite());
				boolean bl = VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.ONLY_FIRST);
				if (object2ByteLinkedOpenHashMap.size() == 200) {
					object2ByteLinkedOpenHashMap.removeLastByte();
				}

				object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
				return bl;
			}
		} else {
			return true;
		}
	}

	@Deprecated
	public final boolean isOpaque(BlockState blockState) {
		return this.opaque;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState neighbor, Direction facing) {
		return false;
	}

	@Deprecated
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		return VoxelShapes.fullCube();
	}

	@Deprecated
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		return this.collidable ? state.getOutlineShape(view, pos) : VoxelShapes.empty();
	}

	@Deprecated
	public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
		return state.getOutlineShape(view, pos);
	}

	@Deprecated
	public VoxelShape getRayTraceShape(BlockState state, BlockView view, BlockPos pos) {
		return VoxelShapes.empty();
	}

	public static boolean topCoversMediumSquare(BlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return !blockState.matches(BlockTags.LEAVES)
			&& !VoxelShapes.matchesAnywhere(blockState.getCollisionShape(world, pos).getFace(Direction.UP), SOLID_MEDIUM_SQUARE_SHAPE, BooleanBiFunction.ONLY_SECOND);
	}

	public static boolean sideCoversSmallSquare(WorldView world, BlockPos pos, Direction side) {
		BlockState blockState = world.getBlockState(pos);
		return !blockState.matches(BlockTags.LEAVES)
			&& !VoxelShapes.matchesAnywhere(blockState.getCollisionShape(world, pos).getFace(side), SOLID_SMALL_SQUARE_SHAPE, BooleanBiFunction.ONLY_SECOND);
	}

	public static boolean isSideSolidFullSquare(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return !state.matches(BlockTags.LEAVES) && isFaceFullSquare(state.getCollisionShape(world, pos), side);
	}

	public static boolean isFaceFullSquare(VoxelShape shape, Direction side) {
		VoxelShape voxelShape = shape.getFace(side);
		return isShapeFullCube(voxelShape);
	}

	public static boolean isShapeFullCube(VoxelShape shape) {
		return FULL_CUBE_SHAPE_CACHE.getUnchecked(shape);
	}

	@Deprecated
	public final boolean isFullOpaque(BlockState state, BlockView view, BlockPos pos) {
		return state.isOpaque() ? isShapeFullCube(state.getCullingShape(view, pos)) : false;
	}

	public boolean isTranslucent(BlockState state, BlockView view, BlockPos pos) {
		return !isShapeFullCube(state.getOutlineShape(view, pos)) && state.getFluidState().isEmpty();
	}

	@Deprecated
	public int getOpacity(BlockState state, BlockView view, BlockPos pos) {
		if (state.isFullOpaque(view, pos)) {
			return view.getMaxLightLevel();
		} else {
			return state.isTranslucent(view, pos) ? 0 : 1;
		}
	}

	@Deprecated
	public boolean hasSidedTransparency(BlockState state) {
		return false;
	}

	@Deprecated
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.scheduledTick(state, world, pos, random);
	}

	@Deprecated
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
	}

	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
	}

	public void onBroken(IWorld world, BlockPos pos, BlockState state) {
	}

	@Deprecated
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		DebugRendererInfoManager.sendNeighborUpdate(world, pos);
	}

	public int getTickRate(WorldView worldView) {
		return 10;
	}

	@Nullable
	@Deprecated
	public NameableContainerProvider createContainerProvider(BlockState state, World world, BlockPos pos) {
		return null;
	}

	@Deprecated
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
	}

	@Deprecated
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (this.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
			world.removeBlockEntity(pos);
		}
	}

	@Deprecated
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		float f = state.getHardness(world, pos);
		if (f == -1.0F) {
			return 0.0F;
		} else {
			int i = player.isUsingEffectiveTool(state) ? 30 : 100;
			return player.getBlockBreakingSpeed(state) / f / (float)i;
		}
	}

	@Deprecated
	public void onStacksDropped(BlockState state, World world, BlockPos pos, ItemStack stack) {
	}

	public Identifier getDropTableId() {
		if (this.dropTableId == null) {
			Identifier identifier = Registry.BLOCK.getId(this);
			this.dropTableId = new Identifier(identifier.getNamespace(), "blocks/" + identifier.getPath());
		}

		return this.dropTableId;
	}

	@Deprecated
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		Identifier identifier = this.getDropTableId();
		if (identifier == LootTables.EMPTY) {
			return Collections.emptyList();
		} else {
			LootContext lootContext = builder.put(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
			ServerWorld serverWorld = lootContext.getWorld();
			LootTable lootTable = serverWorld.getServer().getLootManager().getSupplier(identifier);
			return lootTable.getDrops(lootContext);
		}
	}

	public static List<ItemStack> getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity) {
		LootContext.Builder builder = new LootContext.Builder(world)
			.setRandom(world.random)
			.put(LootContextParameters.POSITION, pos)
			.put(LootContextParameters.TOOL, ItemStack.EMPTY)
			.putNullable(LootContextParameters.BLOCK_ENTITY, blockEntity);
		return state.getDroppedStacks(builder);
	}

	public static List<ItemStack> getDroppedStacks(
		BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack
	) {
		LootContext.Builder builder = new LootContext.Builder(world)
			.setRandom(world.random)
			.put(LootContextParameters.POSITION, pos)
			.put(LootContextParameters.TOOL, stack)
			.putNullable(LootContextParameters.THIS_ENTITY, entity)
			.putNullable(LootContextParameters.BLOCK_ENTITY, blockEntity);
		return state.getDroppedStacks(builder);
	}

	public static void dropStacks(BlockState state, LootContext.Builder builder) {
		ServerWorld serverWorld = builder.getWorld();
		BlockPos blockPos = builder.get(LootContextParameters.POSITION);
		state.getDroppedStacks(builder).forEach(itemStack -> dropStack(serverWorld, blockPos, itemStack));
		state.onStacksDropped(serverWorld, blockPos, ItemStack.EMPTY);
	}

	public static void dropStacks(BlockState state, World world, BlockPos pos) {
		if (world instanceof ServerWorld) {
			getDroppedStacks(state, (ServerWorld)world, pos, null).forEach(itemStack -> dropStack(world, pos, itemStack));
		}

		state.onStacksDropped(world, pos, ItemStack.EMPTY);
	}

	public static void dropStacks(BlockState state, World world, BlockPos pos, @Nullable BlockEntity blockEntity) {
		if (world instanceof ServerWorld) {
			getDroppedStacks(state, (ServerWorld)world, pos, blockEntity).forEach(itemStack -> dropStack(world, pos, itemStack));
		}

		state.onStacksDropped(world, pos, ItemStack.EMPTY);
	}

	public static void dropStacks(BlockState state, World world, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack) {
		if (world instanceof ServerWorld) {
			getDroppedStacks(state, (ServerWorld)world, pos, blockEntity, entity, stack).forEach(itemStack -> dropStack(world, pos, itemStack));
		}

		state.onStacksDropped(world, pos, stack);
	}

	public static void dropStack(World world, BlockPos pos, ItemStack stack) {
		if (!world.isClient && !stack.isEmpty() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
			float f = 0.5F;
			double d = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			double e = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			double g = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, stack);
			itemEntity.setToDefaultPickupDelay();
			world.spawnEntity(itemEntity);
		}
	}

	protected void dropExperience(World world, BlockPos pos, int size) {
		if (!world.isClient && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
			while (size > 0) {
				int i = ExperienceOrbEntity.roundToOrbSize(size);
				size -= i;
				world.spawnEntity(new ExperienceOrbEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, i));
			}
		}
	}

	public float getBlastResistance() {
		return this.resistance;
	}

	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
	}

	@Deprecated
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return true;
	}

	@Deprecated
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return ActionResult.PASS;
	}

	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState();
	}

	@Deprecated
	public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
	}

	@Deprecated
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return 0;
	}

	@Deprecated
	public boolean emitsRedstonePower(BlockState state) {
		return false;
	}

	@Deprecated
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
	}

	@Deprecated
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return 0;
	}

	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		player.incrementStat(Stats.MINED.getOrCreateStat(this));
		player.addExhaustion(0.005F);
		dropStacks(state, world, pos, blockEntity, player, stack);
	}

	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
	}

	public boolean canMobSpawnInside() {
		return !this.material.isSolid() && !this.material.isLiquid();
	}

	@Environment(EnvType.CLIENT)
	public Text getName() {
		return new TranslatableText(this.getTranslationKey());
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("block", Registry.BLOCK.getId(this));
		}

		return this.translationKey;
	}

	@Deprecated
	public boolean onBlockAction(BlockState state, World world, BlockPos pos, int type, int data) {
		return false;
	}

	@Deprecated
	public PistonBehavior getPistonBehavior(BlockState state) {
		return this.material.getPistonBehavior();
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView view, BlockPos pos) {
		return state.isFullCube(view, pos) ? 0.2F : 1.0F;
	}

	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		entity.handleFallDamage(distance, 1.0F);
	}

	public void onEntityLand(BlockView world, Entity entity) {
		entity.setVelocity(entity.getVelocity().multiply(1.0, 0.0, 1.0));
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(this);
	}

	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> list) {
		list.add(new ItemStack(this));
	}

	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return Fluids.EMPTY.getDefaultState();
	}

	public float getSlipperiness() {
		return this.slipperiness;
	}

	public float method_23349() {
		return this.field_21207;
	}

	public float method_23350() {
		return this.field_21208;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public long getRenderingSeed(BlockState state, BlockPos pos) {
		return MathHelper.hashCode(pos);
	}

	public void onProjectileHit(World world, BlockState state, BlockHitResult hitResult, Entity entity) {
	}

	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		world.playLevelEvent(player, 2001, pos, getRawIdFromState(state));
	}

	public void rainTick(World world, BlockPos pos) {
	}

	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return true;
	}

	@Deprecated
	public boolean hasComparatorOutput(BlockState state) {
		return false;
	}

	@Deprecated
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return 0;
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
	}

	public StateManager<Block, BlockState> getStateManager() {
		return this.stateManager;
	}

	protected final void setDefaultState(BlockState state) {
		this.defaultState = state;
	}

	public final BlockState getDefaultState() {
		return this.defaultState;
	}

	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.NONE;
	}

	@Deprecated
	public Vec3d getOffsetPos(BlockState state, BlockView view, BlockPos blockPos) {
		Block.OffsetType offsetType = this.getOffsetType();
		if (offsetType == Block.OffsetType.NONE) {
			return Vec3d.ZERO;
		} else {
			long l = MathHelper.hashCode(blockPos.getX(), 0, blockPos.getZ());
			return new Vec3d(
				((double)((float)(l & 15L) / 15.0F) - 0.5) * 0.5,
				offsetType == Block.OffsetType.XYZ ? ((double)((float)(l >> 4 & 15L) / 15.0F) - 1.0) * 0.2 : 0.0,
				((double)((float)(l >> 8 & 15L) / 15.0F) - 0.5) * 0.5
			);
		}
	}

	public BlockSoundGroup getSoundGroup(BlockState state) {
		return this.soundGroup;
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
		return "Block{" + Registry.BLOCK.getId(this) + "}";
	}

	@Environment(EnvType.CLIENT)
	public void buildTooltip(ItemStack stack, @Nullable BlockView view, List<Text> tooltip, TooltipContext options) {
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
			} else if (!(o instanceof Block.NeighborGroup)) {
				return false;
			} else {
				Block.NeighborGroup neighborGroup = (Block.NeighborGroup)o;
				return this.self == neighborGroup.self && this.other == neighborGroup.other && this.facing == neighborGroup.facing;
			}
		}

		public int hashCode() {
			int i = this.self.hashCode();
			i = 31 * i + this.other.hashCode();
			return 31 * i + this.facing.hashCode();
		}
	}

	public static enum OffsetType {
		NONE,
		XZ,
		XYZ;
	}

	public static class Settings {
		private Material material;
		private MaterialColor materialColor;
		private boolean collidable = true;
		private BlockSoundGroup soundGroup = BlockSoundGroup.STONE;
		private int luminance;
		private float resistance;
		private float hardness;
		private boolean randomTicks;
		private float slipperiness = 0.6F;
		private float field_21209 = 1.0F;
		private float field_21210 = 1.0F;
		private Identifier dropTableId;
		private boolean opaque = true;
		private boolean dynamicBounds;

		private Settings(Material material, MaterialColor materialColor) {
			this.material = material;
			this.materialColor = materialColor;
		}

		public static Block.Settings of(Material material) {
			return of(material, material.getColor());
		}

		public static Block.Settings of(Material material, DyeColor color) {
			return of(material, color.getMaterialColor());
		}

		public static Block.Settings of(Material material, MaterialColor color) {
			return new Block.Settings(material, color);
		}

		public static Block.Settings copy(Block source) {
			Block.Settings settings = new Block.Settings(source.material, source.materialColor);
			settings.material = source.material;
			settings.hardness = source.hardness;
			settings.resistance = source.resistance;
			settings.collidable = source.collidable;
			settings.randomTicks = source.randomTicks;
			settings.luminance = source.lightLevel;
			settings.materialColor = source.materialColor;
			settings.soundGroup = source.soundGroup;
			settings.slipperiness = source.getSlipperiness();
			settings.field_21209 = source.method_23349();
			settings.dynamicBounds = source.dynamicBounds;
			settings.opaque = source.opaque;
			return settings;
		}

		public Block.Settings noCollision() {
			this.collidable = false;
			this.opaque = false;
			return this;
		}

		public Block.Settings nonOpaque() {
			this.opaque = false;
			return this;
		}

		public Block.Settings slipperiness(float slipperiness) {
			this.slipperiness = slipperiness;
			return this;
		}

		public Block.Settings method_23351(float f) {
			this.field_21209 = f;
			return this;
		}

		public Block.Settings method_23352(float f) {
			this.field_21210 = f;
			return this;
		}

		protected Block.Settings sounds(BlockSoundGroup soundGroup) {
			this.soundGroup = soundGroup;
			return this;
		}

		protected Block.Settings lightLevel(int luminance) {
			this.luminance = luminance;
			return this;
		}

		public Block.Settings strength(float hardness, float resistance) {
			this.hardness = hardness;
			this.resistance = Math.max(0.0F, resistance);
			return this;
		}

		protected Block.Settings breakInstantly() {
			return this.strength(0.0F);
		}

		protected Block.Settings strength(float strength) {
			this.strength(strength, strength);
			return this;
		}

		protected Block.Settings ticksRandomly() {
			this.randomTicks = true;
			return this;
		}

		protected Block.Settings hasDynamicBounds() {
			this.dynamicBounds = true;
			return this;
		}

		protected Block.Settings dropsNothing() {
			this.dropTableId = LootTables.EMPTY;
			return this;
		}

		public Block.Settings dropsLike(Block source) {
			this.dropTableId = source.getDropTableId();
			return this;
		}
	}
}
