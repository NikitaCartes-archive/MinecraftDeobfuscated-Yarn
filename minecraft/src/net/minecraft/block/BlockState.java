package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.AbstractState;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class BlockState extends AbstractState<Block, BlockState> implements State<BlockState> {
	@Nullable
	private BlockState.ShapeCache shapeCache;
	private final int luminance;
	private final boolean hasSidedTransparency;

	public BlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertyMap) {
		super(block, propertyMap);
		this.luminance = block.getLuminance(this);
		this.hasSidedTransparency = block.hasSidedTransparency(this);
	}

	public void initShapeCache() {
		if (!this.getBlock().hasDynamicBounds()) {
			this.shapeCache = new BlockState.ShapeCache(this);
		}
	}

	public Block getBlock() {
		return this.owner;
	}

	public Material getMaterial() {
		return this.getBlock().getMaterial(this);
	}

	public boolean allowsSpawning(BlockView world, BlockPos pos, EntityType<?> type) {
		return this.getBlock().allowsSpawning(this, world, pos, type);
	}

	public boolean isTranslucent(BlockView world, BlockPos pos) {
		return this.shapeCache != null ? this.shapeCache.translucent : this.getBlock().isTranslucent(this, world, pos);
	}

	public int getOpacity(BlockView world, BlockPos pos) {
		return this.shapeCache != null ? this.shapeCache.lightSubtracted : this.getBlock().getOpacity(this, world, pos);
	}

	public VoxelShape getCullingFace(BlockView world, BlockPos pos, Direction facing) {
		return this.shapeCache != null && this.shapeCache.extrudedFaces != null
			? this.shapeCache.extrudedFaces[facing.ordinal()]
			: VoxelShapes.extrudeFace(this.getCullingShape(world, pos), facing);
	}

	public boolean exceedsCube() {
		return this.shapeCache == null || this.shapeCache.exceedsCube;
	}

	public boolean hasSidedTransparency() {
		return this.hasSidedTransparency;
	}

	public int getLuminance() {
		return this.luminance;
	}

	public boolean isAir() {
		return this.getBlock().isAir(this);
	}

	public MaterialColor getTopMaterialColor(BlockView world, BlockPos pos) {
		return this.getBlock().getMapColor(this, world, pos);
	}

	public BlockState rotate(BlockRotation rotation) {
		return this.getBlock().rotate(this, rotation);
	}

	public BlockState mirror(BlockMirror mirror) {
		return this.getBlock().mirror(this, mirror);
	}

	public BlockRenderType getRenderType() {
		return this.getBlock().getRenderType(this);
	}

	@Environment(EnvType.CLIENT)
	public boolean hasEmissiveLighting() {
		return this.getBlock().hasEmissiveLighting(this);
	}

	@Environment(EnvType.CLIENT)
	public float getAmbientOcclusionLightLevel(BlockView world, BlockPos pos) {
		return this.getBlock().getAmbientOcclusionLightLevel(this, world, pos);
	}

	public boolean isSimpleFullBlock(BlockView world, BlockPos pos) {
		return this.getBlock().isSimpleFullBlock(this, world, pos);
	}

	public boolean emitsRedstonePower() {
		return this.getBlock().emitsRedstonePower(this);
	}

	public int getWeakRedstonePower(BlockView world, BlockPos pos, Direction facing) {
		return this.getBlock().getWeakRedstonePower(this, world, pos, facing);
	}

	public boolean hasComparatorOutput() {
		return this.getBlock().hasComparatorOutput(this);
	}

	public int getComparatorOutput(World world, BlockPos pos) {
		return this.getBlock().getComparatorOutput(this, world, pos);
	}

	public float getHardness(BlockView world, BlockPos pos) {
		return this.getBlock().getHardness(this, world, pos);
	}

	public float calcBlockBreakingDelta(PlayerEntity player, BlockView world, BlockPos pos) {
		return this.getBlock().calcBlockBreakingDelta(this, player, world, pos);
	}

	public int getStrongRedstonePower(BlockView world, BlockPos pos, Direction facing) {
		return this.getBlock().getStrongRedstonePower(this, world, pos, facing);
	}

	public PistonBehavior getPistonBehavior() {
		return this.getBlock().getPistonBehavior(this);
	}

	public boolean isFullOpaque(BlockView world, BlockPos pos) {
		return this.shapeCache != null ? this.shapeCache.fullOpaque : this.getBlock().isFullOpaque(this, world, pos);
	}

	public boolean isOpaque() {
		return this.shapeCache != null ? this.shapeCache.opaque : this.getBlock().isOpaque(this);
	}

	@Environment(EnvType.CLIENT)
	public boolean isSideInvisible(BlockState neighbor, Direction facing) {
		return this.getBlock().isSideInvisible(this, neighbor, facing);
	}

	public VoxelShape getOutlineShape(BlockView world, BlockPos pos) {
		return this.getOutlineShape(world, pos, EntityContext.absent());
	}

	public VoxelShape getOutlineShape(BlockView world, BlockPos pos, EntityContext context) {
		return this.getBlock().getOutlineShape(this, world, pos, context);
	}

	public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
		return this.shapeCache != null ? this.shapeCache.collisionShape : this.getCollisionShape(world, pos, EntityContext.absent());
	}

	public VoxelShape getCollisionShape(BlockView world, BlockPos pos, EntityContext context) {
		return this.getBlock().getCollisionShape(this, world, pos, context);
	}

	public VoxelShape method_25962(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_25959(this, blockView, blockPos);
	}

	public VoxelShape getCullingShape(BlockView world, BlockPos pos) {
		return this.getBlock().getCullingShape(this, world, pos);
	}

	public VoxelShape getRayTraceShape(BlockView world, BlockPos pos) {
		return this.getBlock().getRayTraceShape(this, world, pos);
	}

	public final boolean hasSolidTopSurface(BlockView world, BlockPos pos, Entity entity) {
		return this.isSideOpaque(world, pos, entity, Direction.UP);
	}

	public final boolean isSideOpaque(BlockView world, BlockPos pos, Entity entity, Direction side) {
		return Block.isFaceFullSquare(this.getCollisionShape(world, pos, EntityContext.of(entity)), side);
	}

	public Vec3d getOffsetPos(BlockView world, BlockPos pos) {
		return this.getBlock().getOffsetPos(this, world, pos);
	}

	public boolean onBlockAction(World world, BlockPos pos, int type, int data) {
		return this.getBlock().onBlockAction(this, world, pos, type, data);
	}

	public void neighborUpdate(World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
		this.getBlock().neighborUpdate(this, world, pos, neighborBlock, neighborPos, moved);
	}

	public void updateNeighborStates(IWorld world, BlockPos pos, int flags) {
		this.getBlock().updateNeighborStates(this, world, pos, flags);
	}

	public void method_11637(IWorld world, BlockPos pos, int flags) {
		this.getBlock().method_9517(this, world, pos, flags);
	}

	public void onBlockAdded(World world, BlockPos pos, BlockState oldState, boolean moved) {
		this.getBlock().onBlockAdded(this, world, pos, oldState, moved);
	}

	public void onBlockRemoved(World world, BlockPos pos, BlockState newState, boolean moved) {
		this.getBlock().onBlockRemoved(this, world, pos, newState, moved);
	}

	public void scheduledTick(ServerWorld world, BlockPos pos, Random random) {
		this.getBlock().scheduledTick(this, world, pos, random);
	}

	public void randomTick(ServerWorld world, BlockPos pos, Random random) {
		this.getBlock().randomTick(this, world, pos, random);
	}

	public void onEntityCollision(World world, BlockPos pos, Entity entity) {
		this.getBlock().onEntityCollision(this, world, pos, entity);
	}

	public void onStacksDropped(World world, BlockPos pos, ItemStack stack) {
		this.getBlock().onStacksDropped(this, world, pos, stack);
	}

	public List<ItemStack> getDroppedStacks(LootContext.Builder builder) {
		return this.getBlock().getDroppedStacks(this, builder);
	}

	public ActionResult onUse(World world, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return this.getBlock().onUse(this, world, hit.getBlockPos(), player, hand, hit);
	}

	public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
		this.getBlock().onBlockBreakStart(this, world, pos, player);
	}

	public boolean canSuffocate(BlockView world, BlockPos pos) {
		return this.getBlock().canSuffocate(this, world, pos);
	}

	@Environment(EnvType.CLIENT)
	public boolean hasInWallOverlay(BlockView world, BlockPos pos) {
		return this.getBlock().hasInWallOverlay(this, world, pos);
	}

	public BlockState getStateForNeighborUpdate(Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return this.getBlock().getStateForNeighborUpdate(this, facing, neighborState, world, pos, neighborPos);
	}

	public boolean canPathfindThrough(BlockView world, BlockPos pos, NavigationType env) {
		return this.getBlock().canPathfindThrough(this, world, pos, env);
	}

	public boolean canReplace(ItemPlacementContext ctx) {
		return this.getBlock().canReplace(this, ctx);
	}

	public boolean canBucketPlace(Fluid fluid) {
		return this.getBlock().canBucketPlace(this, fluid);
	}

	public boolean canPlaceAt(WorldView world, BlockPos pos) {
		return this.getBlock().canPlaceAt(this, world, pos);
	}

	public boolean shouldPostProcess(BlockView world, BlockPos pos) {
		return this.getBlock().shouldPostProcess(this, world, pos);
	}

	@Nullable
	public NamedScreenHandlerFactory createScreenHandlerFactory(World world, BlockPos pos) {
		return this.getBlock().createScreenHandlerFactory(this, world, pos);
	}

	public boolean matches(Tag<Block> tag) {
		return this.getBlock().isIn(tag);
	}

	public FluidState getFluidState() {
		return this.getBlock().getFluidState(this);
	}

	public boolean hasRandomTicks() {
		return this.getBlock().hasRandomTicks(this);
	}

	@Environment(EnvType.CLIENT)
	public long getRenderingSeed(BlockPos pos) {
		return this.getBlock().getRenderingSeed(this, pos);
	}

	public BlockSoundGroup getSoundGroup() {
		return this.getBlock().getSoundGroup(this);
	}

	public void onProjectileHit(World world, BlockState state, BlockHitResult hitResult, Projectile projectile) {
		this.getBlock().onProjectileHit(world, state, hitResult, projectile);
	}

	public boolean isSideSolidFullSquare(BlockView world, BlockPos pos, Direction direction) {
		return this.shapeCache != null ? this.shapeCache.solidFullSquare[direction.ordinal()] : Block.isSideSolidFullSquare(this, world, pos, direction);
	}

	public boolean isFullCube(BlockView world, BlockPos pos) {
		return this.shapeCache != null ? this.shapeCache.isFullCube : Block.isShapeFullCube(this.getCollisionShape(world, pos));
	}

	public static <T> Dynamic<T> serialize(DynamicOps<T> ops, BlockState state) {
		ImmutableMap<Property<?>, Comparable<?>> immutableMap = state.getEntries();
		T object;
		if (immutableMap.isEmpty()) {
			object = ops.createMap(ImmutableMap.of(ops.createString("Name"), ops.createString(Registry.BLOCK.getId(state.getBlock()).toString())));
		} else {
			object = ops.createMap(
				ImmutableMap.of(
					ops.createString("Name"),
					ops.createString(Registry.BLOCK.getId(state.getBlock()).toString()),
					ops.createString("Properties"),
					ops.createMap(
						(Map<T, T>)immutableMap.entrySet()
							.stream()
							.map(
								entry -> Pair.of(
										ops.createString(((Property)entry.getKey()).getName()),
										ops.createString(State.nameValue((Property<T>)entry.getKey(), (Comparable<?>)entry.getValue()))
									)
							)
							.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
					)
				)
			);
		}

		return new Dynamic<>(ops, object);
	}

	public static <T> BlockState deserialize(Dynamic<T> dynamic) {
		Block block = Registry.BLOCK.get(new Identifier((String)dynamic.getElement("Name").flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:air")));
		Map<String, String> map = dynamic.get("Properties").asMap(dynamicx -> dynamicx.asString(""), dynamicx -> dynamicx.asString(""));
		BlockState blockState = block.getDefaultState();
		StateManager<Block, BlockState> stateManager = block.getStateManager();

		for (Entry<String, String> entry : map.entrySet()) {
			String string = (String)entry.getKey();
			Property<?> property = stateManager.getProperty(string);
			if (property != null) {
				blockState = State.tryRead(blockState, property, string, dynamic.toString(), (String)entry.getValue());
			}
		}

		return blockState;
	}

	static final class ShapeCache {
		private static final Direction[] DIRECTIONS = Direction.values();
		private final boolean opaque;
		private final boolean fullOpaque;
		private final boolean translucent;
		private final int lightSubtracted;
		private final VoxelShape[] extrudedFaces;
		private final VoxelShape collisionShape;
		private final boolean exceedsCube;
		private final boolean[] solidFullSquare;
		private final boolean isFullCube;

		private ShapeCache(BlockState state) {
			Block block = state.getBlock();
			this.opaque = block.isOpaque(state);
			this.fullOpaque = block.isFullOpaque(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
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

			this.collisionShape = block.getCollisionShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, EntityContext.absent());
			this.exceedsCube = Arrays.stream(Direction.Axis.values())
				.anyMatch(axis -> this.collisionShape.getMinimum(axis) < 0.0 || this.collisionShape.getMaximum(axis) > 1.0);
			this.solidFullSquare = new boolean[6];

			for (Direction direction2 : DIRECTIONS) {
				this.solidFullSquare[direction2.ordinal()] = Block.isSideSolidFullSquare(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction2);
			}

			this.isFullCube = Block.isShapeFullCube(state.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
		}
	}
}
