package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
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
	private BlockState.ShapeCache shapeCache = null;
	private final int luminance;
	private final boolean field_16554;

	public BlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
		super(block, immutableMap);
		this.luminance = block.getLuminance(this);
		this.field_16554 = block.method_9526(this);
	}

	public void initShapeCache() {
		if (!this.getBlock().isPistonExtension()) {
			this.shapeCache = new BlockState.ShapeCache(this);
		}
	}

	public Block getBlock() {
		return this.owner;
	}

	public Material getMaterial() {
		return this.getBlock().getMaterial(this);
	}

	public boolean allowsSpawning(Entity entity) {
		return this.getBlock().allowsSpawning(this, entity);
	}

	public boolean method_11623(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.field_16556 : this.getBlock().method_9579(this, blockView, blockPos);
	}

	public int method_11581(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.field_16555 : this.getBlock().method_9505(this, blockView, blockPos);
	}

	@Environment(EnvType.CLIENT)
	public VoxelShape method_16384(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.shapeCache != null && this.shapeCache.shapes != null
			? this.shapeCache.shapes[direction.ordinal()]
			: VoxelShapes.method_16344(this.method_11615(blockView, blockPos), direction);
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

	public boolean method_11593(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9599(this, blockView, blockPos);
	}

	public MaterialColor getMaterialColor(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().getMaterialColor(this, blockView, blockPos);
	}

	public BlockState applyRotation(Rotation rotation) {
		return this.getBlock().applyRotation(this, rotation);
	}

	public BlockState applyMirror(Mirror mirror) {
		return this.getBlock().applyMirror(this, mirror);
	}

	public boolean method_11604(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9555(this, blockView, blockPos);
	}

	@Environment(EnvType.CLIENT)
	public boolean hasBlockEntityBreakingRender() {
		return this.getBlock().hasBlockEntityBreakingRender(this);
	}

	public RenderTypeBlock getRenderType() {
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

	public boolean blocksLight(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_16361(this, blockView, blockPos);
	}

	public boolean isSimpleFullBlock(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().isSimpleFullBlock(this, blockView, blockPos);
	}

	public boolean emitsRedstonePower() {
		return this.getBlock().emitsRedstonePower(this);
	}

	public int method_11597(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.getBlock().method_9524(this, blockView, blockPos, direction);
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

	public int method_11577(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.getBlock().method_9603(this, blockView, blockPos, direction);
	}

	public PistonBehavior getPistonBehavior() {
		return this.getBlock().getPistonBehavior(this);
	}

	public boolean method_11598(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.field_16557 : this.getBlock().method_9557(this, blockView, blockPos);
	}

	public boolean isFullBoundsCubeForCulling() {
		return this.shapeCache != null ? this.shapeCache.cull : this.getBlock().isFullBoundsCubeForCulling(this);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11592(BlockState blockState, Direction direction) {
		return this.getBlock().method_9522(this, blockState, direction);
	}

	public VoxelShape getBoundingShape(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().getBoundingShape(this, blockView, blockPos);
	}

	public VoxelShape method_11628(BlockView blockView, BlockPos blockPos) {
		return this.method_16337(blockView, blockPos, VerticalEntityPosition.minValue());
	}

	public VoxelShape method_16337(BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.getBlock().method_9549(this, blockView, blockPos, verticalEntityPosition);
	}

	public VoxelShape method_11615(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9571(this, blockView, blockPos);
	}

	public VoxelShape getRayTraceShape(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().getRayTraceShape(this, blockView, blockPos);
	}

	public boolean hasSolidTopSurface(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().hasSolidTopSurface(this, blockView, blockPos);
	}

	public Vec3d getOffsetPos(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().getOffsetPos(this, blockView, blockPos);
	}

	public boolean onBlockAction(World world, BlockPos blockPos, int i, int j) {
		return this.getBlock().onBlockAction(this, world, blockPos, i, j);
	}

	public void neighborUpdate(World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		this.getBlock().neighborUpdate(this, world, blockPos, block, blockPos2);
	}

	public void method_11635(IWorld iWorld, BlockPos blockPos, int i) {
		this.getBlock().method_9528(this, iWorld, blockPos, i);
	}

	public void method_11637(IWorld iWorld, BlockPos blockPos, int i) {
		this.getBlock().method_9517(this, iWorld, blockPos, i);
	}

	public void onBlockAdded(World world, BlockPos blockPos, BlockState blockState) {
		this.getBlock().onBlockAdded(this, world, blockPos, blockState);
	}

	public void onBlockRemoved(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		this.getBlock().onBlockRemoved(this, world, blockPos, blockState, bl);
	}

	public void scheduledTick(World world, BlockPos blockPos, Random random) {
		this.getBlock().scheduledTick(this, world, blockPos, random);
	}

	public void randomTick(World world, BlockPos blockPos, Random random) {
		this.getBlock().randomTick(this, world, blockPos, random);
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

	public boolean method_11629(World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h) {
		return this.getBlock().method_9534(this, world, blockPos, playerEntity, hand, direction, f, g, h);
	}

	public void onBlockBreakStart(World world, BlockPos blockPos, PlayerEntity playerEntity) {
		this.getBlock().onBlockBreakStart(this, world, blockPos, playerEntity);
	}

	public boolean canSuffocate(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().canSuffocate(this, blockView, blockPos);
	}

	public BlockState method_11578(Direction direction, BlockState blockState, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return this.getBlock().method_9559(this, direction, blockState, iWorld, blockPos, blockPos2);
	}

	public boolean canPlaceAtSide(BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return this.getBlock().canPlaceAtSide(this, blockView, blockPos, placementEnvironment);
	}

	public boolean method_11587(ItemPlacementContext itemPlacementContext) {
		return this.getBlock().method_9616(this, itemPlacementContext);
	}

	public boolean canPlaceAt(ViewableWorld viewableWorld, BlockPos blockPos) {
		return this.getBlock().canPlaceAt(this, viewableWorld, blockPos);
	}

	public boolean method_11601(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9552(this, blockView, blockPos);
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
	public long getPosRandom(BlockPos blockPos) {
		return this.getBlock().getPosRandom(this, blockPos);
	}

	public BlockSoundGroup getSoundGroup() {
		return this.getBlock().getSoundGroup(this);
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
		Optional<Map<Dynamic<T>, Dynamic<T>>> optional = dynamic.get("Properties").flatMap(Dynamic::getMapValues);
		BlockState blockState = block.getDefaultState();
		if (optional.isPresent()) {
			StateFactory<Block, BlockState> stateFactory = block.getStateFactory();

			for (Entry<Dynamic<T>, Dynamic<T>> entry : ((Map)optional.get()).entrySet()) {
				String string = (String)((Dynamic)entry.getKey()).getStringValue().orElse("");
				Property<?> property = stateFactory.getProperty(string);
				if (property != null) {
					blockState = PropertyContainer.deserialize(
						blockState, property, string, dynamic.toString(), (String)((Dynamic)entry.getValue()).getStringValue().orElse("")
					);
				}
			}
		}

		return blockState;
	}

	static final class ShapeCache {
		private static final Direction[] field_16559 = Direction.values();
		private final boolean cull;
		private final boolean field_16557;
		private final boolean field_16556;
		private final int field_16555;
		private final VoxelShape[] shapes;

		private ShapeCache(BlockState blockState) {
			Block block = blockState.getBlock();
			this.cull = block.isFullBoundsCubeForCulling(blockState);
			this.field_16557 = block.method_9557(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);
			this.field_16556 = block.method_9579(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);
			this.field_16555 = block.method_9505(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);
			if (!blockState.isFullBoundsCubeForCulling()) {
				this.shapes = null;
			} else {
				this.shapes = new VoxelShape[field_16559.length];
				VoxelShape voxelShape = block.method_9571(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);

				for (Direction direction : field_16559) {
					this.shapes[direction.ordinal()] = VoxelShapes.method_16344(voxelShape, direction);
				}
			}
		}
	}
}
