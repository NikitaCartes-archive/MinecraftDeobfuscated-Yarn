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
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
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

	public boolean allowsSpawning(BlockView view, BlockPos pos, EntityType<?> type) {
		return this.getBlock().allowsSpawning(this, view, pos, type);
	}

	public boolean isTranslucent(BlockView view, BlockPos pos) {
		return this.shapeCache != null ? this.shapeCache.translucent : this.getBlock().isTranslucent(this, view, pos);
	}

	public int getOpacity(BlockView view, BlockPos pos) {
		return this.shapeCache != null ? this.shapeCache.lightSubtracted : this.getBlock().getOpacity(this, view, pos);
	}

	public VoxelShape getCullingShape(BlockView view, BlockPos pos, Direction facing) {
		return this.shapeCache != null && this.shapeCache.shapes != null
			? this.shapeCache.shapes[facing.ordinal()]
			: VoxelShapes.slice(this.getCullingShape(view, pos), facing);
	}

	public boolean method_17900() {
		return this.shapeCache == null || this.shapeCache.field_17651;
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

	public MaterialColor getTopMaterialColor(BlockView view, BlockPos pos) {
		return this.getBlock().getMapColor(this, view, pos);
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
	public float getAmbientOcclusionLightLevel(BlockView view, BlockPos pos) {
		return this.getBlock().getAmbientOcclusionLightLevel(this, view, pos);
	}

	public boolean isSimpleFullBlock(BlockView view, BlockPos pos) {
		return this.getBlock().isSimpleFullBlock(this, view, pos);
	}

	public boolean emitsRedstonePower() {
		return this.getBlock().emitsRedstonePower(this);
	}

	public int getWeakRedstonePower(BlockView view, BlockPos pos, Direction facing) {
		return this.getBlock().getWeakRedstonePower(this, view, pos, facing);
	}

	public boolean hasComparatorOutput() {
		return this.getBlock().hasComparatorOutput(this);
	}

	public int getComparatorOutput(World world, BlockPos pos) {
		return this.getBlock().getComparatorOutput(this, world, pos);
	}

	public float getHardness(BlockView view, BlockPos pos) {
		return this.getBlock().getHardness(this, view, pos);
	}

	public float calcBlockBreakingDelta(PlayerEntity player, BlockView view, BlockPos pos) {
		return this.getBlock().calcBlockBreakingDelta(this, player, view, pos);
	}

	public int getStrongRedstonePower(BlockView view, BlockPos pos, Direction facing) {
		return this.getBlock().getStrongRedstonePower(this, view, pos, facing);
	}

	public PistonBehavior getPistonBehavior() {
		return this.getBlock().getPistonBehavior(this);
	}

	public boolean isFullOpaque(BlockView view, BlockPos pos) {
		return this.shapeCache != null ? this.shapeCache.fullOpaque : this.getBlock().isFullOpaque(this, view, pos);
	}

	public boolean isOpaque() {
		return this.shapeCache != null ? this.shapeCache.opaque : this.getBlock().isOpaque(this);
	}

	@Environment(EnvType.CLIENT)
	public boolean isSideInvisible(BlockState neighbor, Direction facing) {
		return this.getBlock().isSideInvisible(this, neighbor, facing);
	}

	public VoxelShape getOutlineShape(BlockView view, BlockPos pos) {
		return this.getOutlineShape(view, pos, EntityContext.absent());
	}

	public VoxelShape getOutlineShape(BlockView view, BlockPos pos, EntityContext entityContext) {
		return this.getBlock().getOutlineShape(this, view, pos, entityContext);
	}

	public VoxelShape getCollisionShape(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.collisionShape : this.getCollisionShape(blockView, blockPos, EntityContext.absent());
	}

	public VoxelShape getCollisionShape(BlockView view, BlockPos pos, EntityContext ePos) {
		return this.getBlock().getCollisionShape(this, view, pos, ePos);
	}

	public VoxelShape getCullingShape(BlockView view, BlockPos pos) {
		return this.getBlock().getCullingShape(this, view, pos);
	}

	public VoxelShape getRayTraceShape(BlockView view, BlockPos pos) {
		return this.getBlock().getRayTraceShape(this, view, pos);
	}

	public final boolean hasSolidTopSurface(BlockView view, BlockPos pos, Entity entity) {
		return Block.isFaceFullSquare(this.getCollisionShape(view, pos, EntityContext.of(entity)), Direction.UP);
	}

	public Vec3d getOffsetPos(BlockView view, BlockPos pos) {
		return this.getBlock().getOffsetPos(this, view, pos);
	}

	public boolean onBlockAction(World world, BlockPos pos, int type, int data) {
		return this.getBlock().onBlockAction(this, world, pos, type, data);
	}

	public void neighborUpdate(World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean bl) {
		this.getBlock().neighborUpdate(this, world, pos, neighborBlock, neighborPos, bl);
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

	public boolean canSuffocate(BlockView view, BlockPos pos) {
		return this.getBlock().canSuffocate(this, view, pos);
	}

	public BlockState getStateForNeighborUpdate(Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return this.getBlock().getStateForNeighborUpdate(this, facing, neighborState, world, pos, neighborPos);
	}

	public boolean canPlaceAtSide(BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
		return this.getBlock().canPlaceAtSide(this, view, pos, env);
	}

	public boolean canReplace(ItemPlacementContext ctx) {
		return this.getBlock().canReplace(this, ctx);
	}

	public boolean canBucketPlace(Fluid fluid) {
		return this.getBlock().canBucketPlace(this, fluid);
	}

	public boolean canPlaceAt(WorldView worldView, BlockPos pos) {
		return this.getBlock().canPlaceAt(this, worldView, pos);
	}

	public boolean shouldPostProcess(BlockView view, BlockPos pos) {
		return this.getBlock().shouldPostProcess(this, view, pos);
	}

	@Nullable
	public NameableContainerProvider createContainerProvider(World world, BlockPos pos) {
		return this.getBlock().createContainerProvider(this, world, pos);
	}

	public boolean matches(Tag<Block> tag) {
		return this.getBlock().matches(tag);
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

	public void onProjectileHit(World world, BlockState state, BlockHitResult hitResult, Entity projectile) {
		this.getBlock().onProjectileHit(world, state, hitResult, projectile);
	}

	public boolean isSideSolidFullSquare(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.shapeCache != null ? this.shapeCache.solidFullSquare[direction.ordinal()] : Block.isSideSolidFullSquare(this, blockView, blockPos, direction);
	}

	public boolean isFullCube(BlockView world, BlockPos pos) {
		return this.shapeCache != null ? this.shapeCache.field_20337 : Block.isShapeFullCube(this.getCollisionShape(world, pos));
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
		StateManager<Block, BlockState> stateManager = block.getStateFactory();

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
		private final VoxelShape[] shapes;
		private final VoxelShape collisionShape;
		private final boolean field_17651;
		private final boolean[] solidFullSquare;
		private final boolean field_20337;

		private ShapeCache(BlockState state) {
			Block block = state.getBlock();
			this.opaque = block.isOpaque(state);
			this.fullOpaque = block.isFullOpaque(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
			this.translucent = block.isTranslucent(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
			this.lightSubtracted = block.getOpacity(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
			if (!state.isOpaque()) {
				this.shapes = null;
			} else {
				this.shapes = new VoxelShape[DIRECTIONS.length];
				VoxelShape voxelShape = block.getCullingShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);

				for (Direction direction : DIRECTIONS) {
					this.shapes[direction.ordinal()] = VoxelShapes.slice(voxelShape, direction);
				}
			}

			this.collisionShape = block.getCollisionShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, EntityContext.absent());
			this.field_17651 = Arrays.stream(Direction.Axis.values())
				.anyMatch(axis -> this.collisionShape.getMinimum(axis) < 0.0 || this.collisionShape.getMaximum(axis) > 1.0);
			this.solidFullSquare = new boolean[6];

			for (Direction direction2 : DIRECTIONS) {
				this.solidFullSquare[direction2.ordinal()] = Block.isSideSolidFullSquare(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction2);
			}

			this.field_20337 = Block.isShapeFullCube(state.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
		}
	}
}
