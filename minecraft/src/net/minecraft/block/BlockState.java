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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;

public class BlockState extends AbstractPropertyContainer<Block, BlockState> implements PropertyContainer<BlockState> {
	@Nullable
	private BlockState.ShapeCache shapeCache;
	private final int luminance;
	private final boolean field_16554;

	public BlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
		super(block, immutableMap);
		this.luminance = block.getLuminance(this);
		this.field_16554 = block.method_9526(this);
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

	public boolean allowsSpawning(BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return this.getBlock().allowsSpawning(this, blockView, blockPos, entityType);
	}

	public boolean isTranslucent(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.field_16556 : this.getBlock().isTranslucent(this, blockView, blockPos);
	}

	public int getLightSubtracted(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.field_16555 : this.getBlock().getLightSubtracted(this, blockView, blockPos);
	}

	@Environment(EnvType.CLIENT)
	public VoxelShape getCullShape(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.shapeCache != null && this.shapeCache.shapes != null
			? this.shapeCache.shapes[direction.ordinal()]
			: VoxelShapes.method_16344(this.method_11615(blockView, blockPos), direction);
	}

	public boolean method_17900() {
		return this.shapeCache == null || this.shapeCache.field_17651;
	}

	public boolean method_16386() {
		return this.field_16554;
	}

	public int getLuminance() {
		return this.luminance;
	}

	public boolean isAir() {
		return this.getBlock().isAir(this);
	}

	public MaterialColor getTopMaterialColor(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().getMapColor(this, blockView, blockPos);
	}

	public BlockState rotate(Rotation rotation) {
		return this.getBlock().rotate(this, rotation);
	}

	public BlockState mirror(Mirror mirror) {
		return this.getBlock().mirror(this, mirror);
	}

	@Environment(EnvType.CLIENT)
	public boolean hasBlockEntityBreakingRender() {
		return this.getBlock().hasBlockEntityBreakingRender(this);
	}

	public BlockRenderType getRenderType() {
		return this.getBlock().getRenderType(this);
	}

	@Environment(EnvType.CLIENT)
	public int getBlockBrightness(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		return this.getBlock().getBlockBrightness(this, extendedBlockView, blockPos);
	}

	@Environment(EnvType.CLIENT)
	public float getAmbientOcclusionLightLevel(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().getAmbientOcclusionLightLevel(this, blockView, blockPos);
	}

	public boolean isSimpleFullBlock(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().isSimpleFullBlock(this, blockView, blockPos);
	}

	public boolean emitsRedstonePower() {
		return this.getBlock().emitsRedstonePower(this);
	}

	public int getWeakRedstonePower(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.getBlock().getWeakRedstonePower(this, blockView, blockPos, direction);
	}

	public boolean hasComparatorOutput() {
		return this.getBlock().hasComparatorOutput(this);
	}

	public int getComparatorOutput(World world, BlockPos blockPos) {
		return this.getBlock().getComparatorOutput(this, world, blockPos);
	}

	public float getHardness(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().getHardness(this, blockView, blockPos);
	}

	public float calcBlockBreakingDelta(PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		return this.getBlock().calcBlockBreakingDelta(this, playerEntity, blockView, blockPos);
	}

	public int getStrongRedstonePower(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.getBlock().getStrongRedstonePower(this, blockView, blockPos, direction);
	}

	public PistonBehavior getPistonBehavior() {
		return this.getBlock().getPistonBehavior(this);
	}

	public boolean isFullOpaque(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.fullOpaque : this.getBlock().isFullOpaque(this, blockView, blockPos);
	}

	public boolean isFullBoundsCubeForCulling() {
		return this.shapeCache != null ? this.shapeCache.cull : this.getBlock().isFullBoundsCubeForCulling(this);
	}

	@Environment(EnvType.CLIENT)
	public boolean skipRenderingSide(BlockState blockState, Direction direction) {
		return this.getBlock().skipRenderingSide(this, blockState, direction);
	}

	public VoxelShape getOutlineShape(BlockView blockView, BlockPos blockPos) {
		return this.getOutlineShape(blockView, blockPos, VerticalEntityPosition.minValue());
	}

	public VoxelShape getOutlineShape(BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.getBlock().getOutlineShape(this, blockView, blockPos, verticalEntityPosition);
	}

	public VoxelShape getCollisionShape(BlockView blockView, BlockPos blockPos) {
		return this.getCollisionShape(blockView, blockPos, VerticalEntityPosition.minValue());
	}

	public VoxelShape getCollisionShape(BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.getBlock().getCollisionShape(this, blockView, blockPos, verticalEntityPosition);
	}

	public VoxelShape method_11615(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9571(this, blockView, blockPos);
	}

	public VoxelShape getRayTraceShape(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().getRayTraceShape(this, blockView, blockPos);
	}

	public final boolean hasSolidTopSurface(BlockView blockView, BlockPos blockPos, Entity entity) {
		return Block.isFaceFullSquare(this.getCollisionShape(blockView, blockPos, VerticalEntityPosition.fromEntity(entity)), Direction.UP);
	}

	public Vec3d getOffsetPos(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().getOffsetPos(this, blockView, blockPos);
	}

	public boolean onBlockAction(World world, BlockPos blockPos, int i, int j) {
		return this.getBlock().onBlockAction(this, world, blockPos, i, j);
	}

	public void neighborUpdate(World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		this.getBlock().neighborUpdate(this, world, blockPos, block, blockPos2, bl);
	}

	public void updateNeighborStates(IWorld iWorld, BlockPos blockPos, int i) {
		this.getBlock().updateNeighborStates(this, iWorld, blockPos, i);
	}

	public void method_11637(IWorld iWorld, BlockPos blockPos, int i) {
		this.getBlock().method_9517(this, iWorld, blockPos, i);
	}

	public void onBlockAdded(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		this.getBlock().onBlockAdded(this, world, blockPos, blockState, bl);
	}

	public void onBlockRemoved(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		this.getBlock().onBlockRemoved(this, world, blockPos, blockState, bl);
	}

	public void scheduledTick(World world, BlockPos blockPos, Random random) {
		this.getBlock().onScheduledTick(this, world, blockPos, random);
	}

	public void onRandomTick(World world, BlockPos blockPos, Random random) {
		this.getBlock().onRandomTick(this, world, blockPos, random);
	}

	public void onEntityCollision(World world, BlockPos blockPos, Entity entity) {
		this.getBlock().onEntityCollision(this, world, blockPos, entity);
	}

	public void onStacksDropped(World world, BlockPos blockPos, ItemStack itemStack) {
		this.getBlock().onStacksDropped(this, world, blockPos, itemStack);
	}

	public List<ItemStack> getDroppedStacks(LootContext.Builder builder) {
		return this.getBlock().getDroppedStacks(this, builder);
	}

	public boolean activate(World world, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		return this.getBlock().activate(this, world, blockHitResult.getBlockPos(), playerEntity, hand, blockHitResult);
	}

	public void onBlockBreakStart(World world, BlockPos blockPos, PlayerEntity playerEntity) {
		this.getBlock().onBlockBreakStart(this, world, blockPos, playerEntity);
	}

	public boolean canSuffocate(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().canSuffocate(this, blockView, blockPos);
	}

	public BlockState getStateForNeighborUpdate(Direction direction, BlockState blockState, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return this.getBlock().getStateForNeighborUpdate(this, direction, blockState, iWorld, blockPos, blockPos2);
	}

	public boolean canPlaceAtSide(BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return this.getBlock().canPlaceAtSide(this, blockView, blockPos, blockPlacementEnvironment);
	}

	public boolean canReplace(ItemPlacementContext itemPlacementContext) {
		return this.getBlock().canReplace(this, itemPlacementContext);
	}

	public boolean canPlaceAt(ViewableWorld viewableWorld, BlockPos blockPos) {
		return this.getBlock().canPlaceAt(this, viewableWorld, blockPos);
	}

	public boolean shouldPostProcess(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().shouldPostProcess(this, blockView, blockPos);
	}

	@Nullable
	public NameableContainerProvider createContainerProvider(World world, BlockPos blockPos) {
		return this.getBlock().createContainerProvider(this, world, blockPos);
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
	public long getRenderingSeed(BlockPos blockPos) {
		return this.getBlock().getRenderingSeed(this, blockPos);
	}

	public BlockSoundGroup getSoundGroup() {
		return this.getBlock().getSoundGroup(this);
	}

	public void onProjectileHit(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
		this.getBlock().onProjectileHit(world, blockState, blockHitResult, entity);
	}

	public static <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps, BlockState blockState) {
		ImmutableMap<Property<?>, Comparable<?>> immutableMap = blockState.getEntries();
		T object;
		if (immutableMap.isEmpty()) {
			object = dynamicOps.createMap(
				ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.BLOCK.getId(blockState.getBlock()).toString()))
			);
		} else {
			object = dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("Name"),
					dynamicOps.createString(Registry.BLOCK.getId(blockState.getBlock()).toString()),
					dynamicOps.createString("Properties"),
					dynamicOps.createMap(
						(Map<T, T>)immutableMap.entrySet()
							.stream()
							.map(
								entry -> Pair.of(
										dynamicOps.createString(((Property)entry.getKey()).getName()),
										dynamicOps.createString(PropertyContainer.getValueAsString((Property<T>)entry.getKey(), (Comparable<?>)entry.getValue()))
									)
							)
							.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
					)
				)
			);
		}

		return new Dynamic<>(dynamicOps, object);
	}

	public static <T> BlockState deserialize(Dynamic<T> dynamic) {
		Block block = Registry.BLOCK.get(new Identifier((String)dynamic.getElement("Name").flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:air")));
		Map<String, String> map = dynamic.get("Properties").asMap(dynamicx -> dynamicx.asString(""), dynamicx -> dynamicx.asString(""));
		BlockState blockState = block.getDefaultState();
		StateFactory<Block, BlockState> stateFactory = block.getStateFactory();

		for (Entry<String, String> entry : map.entrySet()) {
			String string = (String)entry.getKey();
			Property<?> property = stateFactory.getProperty(string);
			if (property != null) {
				blockState = PropertyContainer.deserialize(blockState, property, string, dynamic.toString(), (String)entry.getValue());
			}
		}

		return blockState;
	}

	static final class ShapeCache {
		private static final Direction[] DIRECTIONS = Direction.values();
		private final boolean cull;
		private final boolean fullOpaque;
		private final boolean field_16556;
		private final int field_16555;
		private final VoxelShape[] shapes;
		private final boolean field_17651;

		private ShapeCache(BlockState blockState) {
			Block block = blockState.getBlock();
			this.cull = block.isFullBoundsCubeForCulling(blockState);
			this.fullOpaque = block.isFullOpaque(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);
			this.field_16556 = block.isTranslucent(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);
			this.field_16555 = block.getLightSubtracted(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);
			if (!blockState.isFullBoundsCubeForCulling()) {
				this.shapes = null;
			} else {
				this.shapes = new VoxelShape[DIRECTIONS.length];
				VoxelShape voxelShape = block.method_9571(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);

				for (Direction direction : DIRECTIONS) {
					this.shapes[direction.ordinal()] = VoxelShapes.method_16344(voxelShape, direction);
				}
			}

			VoxelShape voxelShape = block.getCollisionShape(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN, VerticalEntityPosition.minValue());
			this.field_17651 = Arrays.stream(Direction.Axis.values()).anyMatch(axis -> voxelShape.getMinimum(axis) < 0.0 || voxelShape.getMaximum(axis) > 1.0);
		}
	}
}
