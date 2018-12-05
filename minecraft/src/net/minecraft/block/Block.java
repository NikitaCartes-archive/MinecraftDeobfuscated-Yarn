package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemContainer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.HitResult;
import net.minecraft.util.IdList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextTypes;
import net.minecraft.world.loot.context.Parameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block implements ItemContainer {
	protected static final Logger LOGGER = LogManager.getLogger();
	public static final IdList<BlockState> STATE_IDS = new IdList<>();
	private static final Direction[] field_10644 = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
	protected final int lightLevel;
	protected final float hardness;
	protected final float resistance;
	protected final boolean randomTicks;
	protected final BlockSoundGroup soundGroup;
	protected final Material material;
	protected final MaterialColor materialColor;
	private final float friction;
	protected final StateFactory<Block, BlockState> stateFactory;
	private BlockState defaultState;
	protected final boolean collidable;
	private final boolean pistonExtension;
	@Nullable
	private Identifier dropTableId;
	@Nullable
	private String translationKey;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> FACE_CULL_MAP = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(200) {
			@Override
			protected void rehash(int i) {
			}
		};
		object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteLinkedOpenHashMap;
	});

	public static int getRawIdFromState(@Nullable BlockState blockState) {
		if (blockState == null) {
			return 0;
		} else {
			int i = STATE_IDS.getId(blockState);
			return i == -1 ? 0 : i;
		}
	}

	public static BlockState getStateFromRawId(int i) {
		BlockState blockState = STATE_IDS.getInt(i);
		return blockState == null ? Blocks.field_10124.getDefaultState() : blockState;
	}

	public static Block getBlockFromItem(@Nullable Item item) {
		return item instanceof BlockItem ? ((BlockItem)item).getBlock() : Blocks.field_10124;
	}

	public static BlockState method_9582(BlockState blockState, BlockState blockState2, World world, BlockPos blockPos) {
		VoxelShape voxelShape = VoxelShapes.method_1082(
				blockState.method_11628(world, blockPos), blockState2.method_11628(world, blockPos), BooleanBiFunction.ONLY_SECOND
			)
			.method_1096((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());

		for (Entity entity : world.getVisibleEntities(null, voxelShape.getBoundingBox())) {
			double d = VoxelShapes.method_1085(Direction.Axis.Y, entity.getBoundingBox().offset(0.0, 1.0, 0.0), Stream.of(voxelShape), -1.0);
			entity.method_5859(entity.x, entity.y + 1.0 + d, entity.z);
		}

		return blockState2;
	}

	public static VoxelShape createCubeShape(double d, double e, double f, double g, double h, double i) {
		return VoxelShapes.cube(d / 16.0, e / 16.0, f / 16.0, g / 16.0, h / 16.0, i / 16.0);
	}

	@Deprecated
	public boolean allowsSpawning(BlockState blockState, Entity entity) {
		return true;
	}

	@Deprecated
	public boolean isAir(BlockState blockState) {
		return false;
	}

	@Deprecated
	public int getLuminance(BlockState blockState) {
		return this.lightLevel;
	}

	@Deprecated
	public Material getMaterial(BlockState blockState) {
		return this.material;
	}

	@Deprecated
	public MaterialColor getMaterialColor(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.materialColor;
	}

	@Deprecated
	public void method_9528(BlockState blockState, IWorld iWorld, BlockPos blockPos, int i) {
		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : field_10644) {
				pooledMutable.set(blockPos).method_10118(direction);
				BlockState blockState2 = iWorld.getBlockState(pooledMutable);
				BlockState blockState3 = blockState2.method_11578(direction.getOpposite(), blockState, iWorld, pooledMutable, blockPos);
				replaceBlock(blockState2, blockState3, iWorld, pooledMutable, i);
			}
		}
	}

	public boolean matches(Tag<Block> tag) {
		return tag.contains(this);
	}

	public static BlockState getRenderingState(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		BlockState blockState2 = blockState;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : field_10644) {
			mutable.set(blockPos).method_10098(direction);
			blockState2 = blockState2.method_11578(direction, iWorld.getBlockState(mutable), iWorld, blockPos, mutable);
		}

		return blockState2;
	}

	public static void replaceBlock(BlockState blockState, BlockState blockState2, IWorld iWorld, BlockPos blockPos, int i) {
		if (blockState2 != blockState) {
			if (blockState2.isAir()) {
				if (!iWorld.isRemote()) {
					iWorld.breakBlock(blockPos, (i & 32) == 0);
				}
			} else {
				iWorld.setBlockState(blockPos, blockState2, i & -33);
			}
		}
	}

	@Deprecated
	public void method_9517(BlockState blockState, IWorld iWorld, BlockPos blockPos, int i) {
	}

	@Deprecated
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return blockState;
	}

	@Deprecated
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState;
	}

	@Deprecated
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState;
	}

	public Block(Block.Settings settings) {
		StateFactory.Builder<Block, BlockState> builder = new StateFactory.Builder<>(this);
		this.appendProperties(builder);
		this.material = settings.material;
		this.materialColor = settings.materialColor;
		this.collidable = settings.collidable;
		this.soundGroup = settings.soundGroup;
		this.lightLevel = settings.luminance;
		this.resistance = settings.resistance;
		this.hardness = settings.hardness;
		this.randomTicks = settings.randomTicks;
		this.friction = settings.friction;
		this.pistonExtension = settings.pistonExtension;
		this.dropTableId = settings.dropTableId;
		this.stateFactory = builder.build(BlockState::new);
		this.setDefaultState(this.stateFactory.getDefaultState());
	}

	protected static boolean method_9553(Block block) {
		return block instanceof ShulkerBoxBlock
			|| block instanceof LeavesBlock
			|| block.matches(BlockTags.field_15487)
			|| block instanceof StainedGlassBlock
			|| block == Blocks.field_10327
			|| block == Blocks.field_10593
			|| block == Blocks.field_10033
			|| block == Blocks.field_10171
			|| block == Blocks.field_10295
			|| block == Blocks.field_10174
			|| block == Blocks.field_10502;
	}

	public static boolean method_9581(Block block) {
		return method_9553(block) || block == Blocks.field_10560 || block == Blocks.field_10615 || block == Blocks.field_10379;
	}

	@Deprecated
	public final boolean method_16361(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return method_9614(blockState.method_11628(blockView, blockPos));
	}

	@Deprecated
	public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getMaterial().method_15804() && blockState.method_11604(blockView, blockPos) && !blockState.emitsRedstonePower();
	}

	@Deprecated
	public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.material.suffocates() && blockState.method_11604(blockView, blockPos);
	}

	public final boolean method_9555(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.blocksLight(blockView, blockPos);
	}

	@Deprecated
	public boolean hasSolidTopSurface(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getMaterial().method_15804() && blockState.method_11604(blockView, blockPos);
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public boolean hasBlockEntityBreakingRender(BlockState blockState) {
		return false;
	}

	@Deprecated
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		switch (placementEnvironment) {
			case field_50:
				return !method_9614(blockState.method_11628(blockView, blockPos));
			case field_48:
				return blockView.getFluidState(blockPos).matches(FluidTags.field_15517);
			case field_51:
				return !method_9614(blockState.method_11628(blockView, blockPos));
			default:
				return false;
		}
	}

	@Deprecated
	public RenderTypeBlock getRenderType(BlockState blockState) {
		return RenderTypeBlock.MODEL;
	}

	@Deprecated
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		return this.material.method_15800() && itemPlacementContext.getItemStack().getItem() != this.getItem();
	}

	@Deprecated
	public float getHardness(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.hardness;
	}

	public boolean hasRandomTicks(BlockState blockState) {
		return this.randomTicks;
	}

	public boolean hasBlockEntity() {
		return this instanceof BlockEntityProvider;
	}

	@Deprecated
	public boolean method_9552(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public int getBlockBrightness(BlockState blockState, ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		return extendedBlockView.getLightmapIndex(blockPos, blockState.getLuminance());
	}

	@Environment(EnvType.CLIENT)
	public static boolean method_9607(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.method_10093(direction);
		BlockState blockState2 = blockView.getBlockState(blockPos2);
		if (blockState.method_11592(blockState2, direction)) {
			return false;
		} else if (blockState2.isFullBoundsCubeForCulling()) {
			Block.NeighborGroup neighborGroup = new Block.NeighborGroup(blockState, blockState2, direction);
			Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = (Object2ByteLinkedOpenHashMap<Block.NeighborGroup>)FACE_CULL_MAP.get();
			byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
			if (b != 127) {
				return b != 0;
			} else {
				VoxelShape voxelShape = blockState.method_16384(blockView, blockPos, direction);
				VoxelShape voxelShape2 = blockState2.method_16384(blockView, blockPos2, direction.getOpposite());
				boolean bl = VoxelShapes.compareShapes(voxelShape, voxelShape2, BooleanBiFunction.ONLY_FIRST);
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
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return this.collidable && this.getRenderLayer() == BlockRenderLayer.SOLID;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public boolean method_9522(BlockState blockState, BlockState blockState2, Direction direction) {
		return false;
	}

	@Deprecated
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return VoxelShapes.fullCube();
	}

	@Deprecated
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.collidable ? blockState.getBoundingShape(blockView, blockPos) : VoxelShapes.empty();
	}

	@Deprecated
	public VoxelShape method_9571(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getBoundingShape(blockView, blockPos);
	}

	@Deprecated
	public VoxelShape getRayTraceShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return VoxelShapes.empty();
	}

	public static boolean method_9501(VoxelShape voxelShape, Direction direction) {
		VoxelShape voxelShape2 = voxelShape.method_1098(direction);
		return method_9614(voxelShape2);
	}

	public static boolean method_9614(VoxelShape voxelShape) {
		return !VoxelShapes.compareShapes(VoxelShapes.fullCube(), voxelShape, BooleanBiFunction.NOT_SAME);
	}

	@Deprecated
	public final boolean method_9557(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		boolean bl = blockState.isFullBoundsCubeForCulling();
		VoxelShape voxelShape = bl ? blockState.method_11615(blockView, blockPos) : VoxelShapes.empty();
		return method_9614(voxelShape);
	}

	public boolean method_9579(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !method_9614(blockState.getBoundingShape(blockView, blockPos)) && blockState.getFluidState().isEmpty();
	}

	@Deprecated
	public int method_9505(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if (blockState.method_11598(blockView, blockPos)) {
			return blockView.getMaxLightLevel();
		} else {
			return blockState.method_11623(blockView, blockPos) ? 0 : 1;
		}
	}

	@Deprecated
	public boolean method_9526(BlockState blockState) {
		return false;
	}

	@Deprecated
	public final boolean method_9599(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !blockState.method_11598(blockView, blockPos) && blockState.method_11581(blockView, blockPos) == blockView.getMaxLightLevel();
	}

	public boolean canCollideWith(BlockState blockState) {
		return this.canCollideWith();
	}

	public boolean canCollideWith() {
		return true;
	}

	@Deprecated
	public void randomTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.scheduledTick(blockState, world, blockPos, random);
	}

	@Deprecated
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
	}

	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
	}

	public void onBroken(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
	}

	@Deprecated
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
	}

	public int getTickRate(ViewableWorld viewableWorld) {
		return 10;
	}

	@Deprecated
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
	}

	@Deprecated
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
	}

	@Deprecated
	public float calcBlockBreakingDelta(BlockState blockState, PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		float f = blockState.getHardness(blockView, blockPos);
		if (f == -1.0F) {
			return 0.0F;
		} else {
			int i = playerEntity.isUsingEffectiveTool(blockState) ? 30 : 100;
			return playerEntity.getBlockBreakingSpeed(blockState) / f / (float)i;
		}
	}

	@Deprecated
	public void onStacksDropped(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
	}

	public Identifier getDropTableId() {
		if (this.dropTableId == null) {
			Identifier identifier = Registry.BLOCK.getId(this);
			this.dropTableId = new Identifier(identifier.getNamespace(), "blocks/" + identifier.getPath());
		}

		return this.dropTableId;
	}

	@Deprecated
	public List<ItemStack> getDroppedStacks(BlockState blockState, LootContext.Builder builder) {
		Identifier identifier = this.getDropTableId();
		if (identifier == LootTables.EMPTY) {
			return Collections.emptyList();
		} else {
			LootContext lootContext = builder.put(Parameters.field_1224, blockState).build(LootContextTypes.BLOCK);
			ServerWorld serverWorld = lootContext.getWorld();
			LootSupplier lootSupplier = serverWorld.getServer().getLootManager().getSupplier(identifier);
			return lootSupplier.getDrops(lootContext);
		}
	}

	public static List<ItemStack> getDroppedStacks(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
		LootContext.Builder builder = new LootContext.Builder(serverWorld)
			.setRandom(serverWorld.random)
			.put(Parameters.field_1232, blockPos)
			.put(Parameters.field_1229, ItemStack.EMPTY)
			.putNullable(Parameters.field_1228, blockEntity);
		return blockState.getDroppedStacks(builder);
	}

	public static List<ItemStack> getDroppedStacks(
		BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack itemStack
	) {
		LootContext.Builder builder = new LootContext.Builder(serverWorld)
			.setRandom(serverWorld.random)
			.put(Parameters.field_1232, blockPos)
			.put(Parameters.field_1229, itemStack)
			.put(Parameters.field_1226, entity)
			.putNullable(Parameters.field_1228, blockEntity);
		return blockState.getDroppedStacks(builder);
	}

	public static void dropStacks(BlockState blockState, LootContext.Builder builder) {
		ServerWorld serverWorld = builder.getWorld();
		BlockPos blockPos = builder.get(Parameters.field_1232);
		blockState.getDroppedStacks(builder).forEach(itemStack -> dropStack(serverWorld, blockPos, itemStack));
		blockState.onStacksDropped(serverWorld, blockPos, ItemStack.EMPTY);
	}

	public static void dropStacks(BlockState blockState, World world, BlockPos blockPos) {
		if (world instanceof ServerWorld) {
			getDroppedStacks(blockState, (ServerWorld)world, blockPos, null).forEach(itemStack -> dropStack(world, blockPos, itemStack));
		}

		blockState.onStacksDropped(world, blockPos, ItemStack.EMPTY);
	}

	public static void dropStacks(BlockState blockState, World world, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
		if (world instanceof ServerWorld) {
			getDroppedStacks(blockState, (ServerWorld)world, blockPos, blockEntity).forEach(itemStack -> dropStack(world, blockPos, itemStack));
		}

		blockState.onStacksDropped(world, blockPos, ItemStack.EMPTY);
	}

	public static void dropStacks(BlockState blockState, World world, BlockPos blockPos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack itemStack) {
		if (world instanceof ServerWorld) {
			getDroppedStacks(blockState, (ServerWorld)world, blockPos, blockEntity, entity, itemStack).forEach(itemStackx -> dropStack(world, blockPos, itemStackx));
		}

		blockState.onStacksDropped(world, blockPos, itemStack);
	}

	public static void dropStack(World world, BlockPos blockPos, ItemStack itemStack) {
		if (!world.isRemote && !itemStack.isEmpty() && world.getGameRules().getBoolean("doTileDrops")) {
			float f = 0.5F;
			double d = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			double e = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			double g = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			ItemEntity itemEntity = new ItemEntity(world, (double)blockPos.getX() + d, (double)blockPos.getY() + e, (double)blockPos.getZ() + g, itemStack);
			itemEntity.setPickupDelayDefault();
			world.spawnEntity(itemEntity);
		}
	}

	protected void dropExperience(World world, BlockPos blockPos, int i) {
		if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops")) {
			while (i > 0) {
				int j = ExperienceOrbEntity.roundToOrbSize(i);
				i -= j;
				world.spawnEntity(new ExperienceOrbEntity(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, j));
			}
		}
	}

	public float getBlastResistance() {
		return this.resistance;
	}

	@Nullable
	public static HitResult rayTrace(BlockState blockState, World world, BlockPos blockPos, Vec3d vec3d, Vec3d vec3d2) {
		HitResult hitResult = blockState.getBoundingShape(world, blockPos).rayTrace(vec3d, vec3d2, blockPos);
		if (hitResult != null) {
			HitResult hitResult2 = blockState.getRayTraceShape(world, blockPos).rayTrace(vec3d, vec3d2, blockPos);
			if (hitResult2 != null && hitResult2.pos.subtract(vec3d).lengthSquared() < hitResult.pos.subtract(vec3d).lengthSquared()) {
				hitResult.field_1327 = hitResult2.field_1327;
			}
		}

		return hitResult;
	}

	public void onDestroyedByExplosion(World world, BlockPos blockPos, Explosion explosion) {
	}

	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Deprecated
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return true;
	}

	@Deprecated
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		return false;
	}

	public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState();
	}

	@Deprecated
	public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
	}

	@Deprecated
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return 0;
	}

	@Deprecated
	public boolean emitsRedstonePower(BlockState blockState) {
		return false;
	}

	@Deprecated
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
	}

	@Deprecated
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return 0;
	}

	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		playerEntity.incrementStat(Stats.field_15427.method_14956(this));
		playerEntity.addExhaustion(0.005F);
		dropStacks(blockState, world, blockPos, blockEntity, playerEntity, itemStack);
	}

	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
	}

	public boolean canMobSpawnInside() {
		return !this.material.method_15799() && !this.material.method_15797();
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getTextComponent() {
		return new TranslatableTextComponent(this.getTranslationKey());
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = SystemUtil.createTranslationKey("block", Registry.BLOCK.getId(this));
		}

		return this.translationKey;
	}

	@Deprecated
	public boolean onBlockAction(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
		return false;
	}

	@Deprecated
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return this.material.getPistonBehavior();
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public float getAmbientOcclusionLightLevel(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.blocksLight(blockView, blockPos) ? 0.2F : 1.0F;
	}

	public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
		entity.handleFallDamage(f, 1.0F);
	}

	public void onEntityLand(BlockView blockView, Entity entity) {
		entity.velocityY = 0.0;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this);
	}

	public void addStacksForDisplay(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		defaultedList.add(new ItemStack(this));
	}

	@Deprecated
	public FluidState getFluidState(BlockState blockState) {
		return Fluids.field_15906.getDefaultState();
	}

	public float getFrictionCoefficient() {
		return this.friction;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public long getPosRandom(BlockState blockState, BlockPos blockPos) {
		return MathHelper.hashCode(blockPos);
	}

	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		world.fireWorldEvent(playerEntity, 2001, blockPos, getRawIdFromState(blockState));
	}

	public void onRainTick(World world, BlockPos blockPos) {
	}

	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return true;
	}

	@Deprecated
	public boolean hasComparatorOutput(BlockState blockState) {
		return false;
	}

	@Deprecated
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return 0;
	}

	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
	}

	public StateFactory<Block, BlockState> getStateFactory() {
		return this.stateFactory;
	}

	protected final void setDefaultState(BlockState blockState) {
		this.defaultState = blockState;
	}

	public final BlockState getDefaultState() {
		return this.defaultState;
	}

	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.NONE;
	}

	@Deprecated
	public Vec3d getOffsetPos(BlockState blockState, BlockView blockView, BlockPos blockPos) {
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

	public BlockSoundGroup getSoundGroup(BlockState blockState) {
		return this.soundGroup;
	}

	@Override
	public Item getItem() {
		return Item.getItemFromBlock(this);
	}

	public boolean isPistonExtension() {
		return this.pistonExtension;
	}

	public String toString() {
		return "Block{" + Registry.BLOCK.getId(this) + "}";
	}

	@Environment(EnvType.CLIENT)
	public void addInformation(ItemStack itemStack, @Nullable BlockView blockView, List<TextComponent> list, TooltipOptions tooltipOptions) {
	}

	public static boolean isNaturalStone(Block block) {
		return block == Blocks.field_10340 || block == Blocks.field_10474 || block == Blocks.field_10508 || block == Blocks.field_10115;
	}

	public static boolean isNaturalDirt(Block block) {
		return block == Blocks.field_10566 || block == Blocks.field_10253 || block == Blocks.field_10520;
	}

	public static final class NeighborGroup {
		private final BlockState self;
		private final BlockState other;
		private final Direction field_10653;

		public NeighborGroup(BlockState blockState, BlockState blockState2, Direction direction) {
			this.self = blockState;
			this.other = blockState2;
			this.field_10653 = direction;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof Block.NeighborGroup)) {
				return false;
			} else {
				Block.NeighborGroup neighborGroup = (Block.NeighborGroup)object;
				return this.self == neighborGroup.self && this.other == neighborGroup.other && this.field_10653 == neighborGroup.field_10653;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.self, this.other, this.field_10653});
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
		private float friction = 0.6F;
		private Identifier dropTableId;
		private boolean pistonExtension;

		private Settings(Material material, MaterialColor materialColor) {
			this.material = material;
			this.materialColor = materialColor;
		}

		public static Block.Settings of(Material material) {
			return of(material, material.getColor());
		}

		public static Block.Settings of(Material material, DyeColor dyeColor) {
			return of(material, dyeColor.getMaterialColor());
		}

		public static Block.Settings of(Material material, MaterialColor materialColor) {
			return new Block.Settings(material, materialColor);
		}

		public static Block.Settings copy(Block block) {
			Block.Settings settings = new Block.Settings(block.material, block.materialColor);
			settings.material = block.material;
			settings.hardness = block.hardness;
			settings.resistance = block.resistance;
			settings.collidable = block.collidable;
			settings.randomTicks = block.randomTicks;
			settings.luminance = block.lightLevel;
			settings.materialColor = block.materialColor;
			settings.soundGroup = block.soundGroup;
			settings.friction = block.getFrictionCoefficient();
			settings.pistonExtension = block.pistonExtension;
			return settings;
		}

		public Block.Settings noCollision() {
			this.collidable = false;
			return this;
		}

		public Block.Settings friction(float f) {
			this.friction = f;
			return this;
		}

		protected Block.Settings sounds(BlockSoundGroup blockSoundGroup) {
			this.soundGroup = blockSoundGroup;
			return this;
		}

		protected Block.Settings lightLevel(int i) {
			this.luminance = i;
			return this;
		}

		public Block.Settings strength(float f, float g) {
			this.hardness = f;
			this.resistance = Math.max(0.0F, g);
			return this;
		}

		protected Block.Settings breakInstantly() {
			return this.strength(0.0F);
		}

		protected Block.Settings strength(float f) {
			this.strength(f, f);
			return this;
		}

		protected Block.Settings ticksRandomly() {
			this.randomTicks = true;
			return this;
		}

		protected Block.Settings isPistonExtension() {
			this.pistonExtension = true;
			return this;
		}

		protected Block.Settings dropsNothing() {
			this.dropTableId = LootTables.EMPTY;
			return this;
		}

		public Block.Settings dropsLike(Block block) {
			this.dropTableId = block.getDropTableId();
			return this;
		}
	}
}
