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
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
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
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;

public class BlockState extends AbstractPropertyContainer<Block, BlockState> implements PropertyContainer<BlockState> {
	@Nullable
	private BlockState.ShapeCache shapeCache;
	private final int luminance;
	private final boolean hasSidedTransparency;

	public BlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
		super(block, immutableMap);
		this.luminance = block.method_9593(this);
		this.hasSidedTransparency = block.method_9526(this);
	}

	public void initShapeCache() {
		if (!this.getBlock().hasDynamicBounds()) {
			this.shapeCache = new BlockState.ShapeCache(this);
		}
	}

	public Block getBlock() {
		return this.owner;
	}

	public Material method_11620() {
		return this.getBlock().method_9597(this);
	}

	public boolean allowsSpawning(BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return this.getBlock().method_9523(this, blockView, blockPos, entityType);
	}

	public boolean isTranslucent(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.translucent : this.getBlock().method_9579(this, blockView, blockPos);
	}

	public int getLightSubtracted(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.lightSubtracted : this.getBlock().method_9505(this, blockView, blockPos);
	}

	public VoxelShape method_16384(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.shapeCache != null && this.shapeCache.field_16560 != null
			? this.shapeCache.field_16560[direction.ordinal()]
			: VoxelShapes.method_16344(this.method_11615(blockView, blockPos), direction);
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
		return this.getBlock().method_9500(this);
	}

	public MaterialColor method_11625(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9602(this, blockView, blockPos);
	}

	public BlockState rotate(BlockRotation blockRotation) {
		return this.getBlock().method_9598(this, blockRotation);
	}

	public BlockState mirror(BlockMirror blockMirror) {
		return this.getBlock().method_9569(this, blockMirror);
	}

	@Environment(EnvType.CLIENT)
	public boolean hasBlockEntityBreakingRender() {
		return this.getBlock().method_9589(this);
	}

	public BlockRenderType getRenderType() {
		return this.getBlock().method_9604(this);
	}

	@Environment(EnvType.CLIENT)
	public int getBlockBrightness(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		return this.getBlock().method_9546(this, extendedBlockView, blockPos);
	}

	@Environment(EnvType.CLIENT)
	public float getAmbientOcclusionLightLevel(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9575(this, blockView, blockPos);
	}

	public boolean isSimpleFullBlock(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9521(this, blockView, blockPos);
	}

	public boolean emitsRedstonePower() {
		return this.getBlock().method_9506(this);
	}

	public int getWeakRedstonePower(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.getBlock().method_9524(this, blockView, blockPos, direction);
	}

	public boolean hasComparatorOutput() {
		return this.getBlock().method_9498(this);
	}

	public int getComparatorOutput(World world, BlockPos blockPos) {
		return this.getBlock().method_9572(this, world, blockPos);
	}

	public float getHardness(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9537(this, blockView, blockPos);
	}

	public float calcBlockBreakingDelta(PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9594(this, playerEntity, blockView, blockPos);
	}

	public int getStrongRedstonePower(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.getBlock().method_9603(this, blockView, blockPos, direction);
	}

	public PistonBehavior method_11586() {
		return this.getBlock().method_9527(this);
	}

	public boolean isFullOpaque(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.fullOpaque : this.getBlock().method_9557(this, blockView, blockPos);
	}

	public boolean isOpaque() {
		return this.shapeCache != null ? this.shapeCache.opaque : this.getBlock().method_9601(this);
	}

	@Environment(EnvType.CLIENT)
	public boolean isSideInvisible(BlockState blockState, Direction direction) {
		return this.getBlock().method_9522(this, blockState, direction);
	}

	public VoxelShape method_17770(BlockView blockView, BlockPos blockPos) {
		return this.method_11606(blockView, blockPos, EntityContext.absent());
	}

	public VoxelShape method_11606(BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return this.getBlock().method_9530(this, blockView, blockPos, entityContext);
	}

	public VoxelShape method_11628(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.field_19360 : this.method_16337(blockView, blockPos, EntityContext.absent());
	}

	public VoxelShape method_16337(BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return this.getBlock().method_9549(this, blockView, blockPos, entityContext);
	}

	public VoxelShape method_11615(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9571(this, blockView, blockPos);
	}

	public VoxelShape method_11607(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9584(this, blockView, blockPos);
	}

	public final boolean hasSolidTopSurface(BlockView blockView, BlockPos blockPos, Entity entity) {
		return Block.method_9501(this.method_16337(blockView, blockPos, EntityContext.of(entity)), Direction.field_11036);
	}

	public Vec3d method_11599(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9540(this, blockView, blockPos);
	}

	public boolean onBlockAction(World world, BlockPos blockPos, int i, int j) {
		return this.getBlock().method_9592(this, world, blockPos, i, j);
	}

	public void neighborUpdate(World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		this.getBlock().method_9612(this, world, blockPos, block, blockPos2, bl);
	}

	public void updateNeighborStates(IWorld iWorld, BlockPos blockPos, int i) {
		this.getBlock().method_9528(this, iWorld, blockPos, i);
	}

	public void method_11637(IWorld iWorld, BlockPos blockPos, int i) {
		this.getBlock().method_9517(this, iWorld, blockPos, i);
	}

	public void onBlockAdded(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		this.getBlock().method_9615(this, world, blockPos, blockState, bl);
	}

	public void onBlockRemoved(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		this.getBlock().method_9536(this, world, blockPos, blockState, bl);
	}

	public void scheduledTick(World world, BlockPos blockPos, Random random) {
		this.getBlock().method_9588(this, world, blockPos, random);
	}

	public void onRandomTick(World world, BlockPos blockPos, Random random) {
		this.getBlock().method_9514(this, world, blockPos, random);
	}

	public void onEntityCollision(World world, BlockPos blockPos, Entity entity) {
		this.getBlock().method_9548(this, world, blockPos, entity);
	}

	public void onStacksDropped(World world, BlockPos blockPos, ItemStack itemStack) {
		this.getBlock().method_9565(this, world, blockPos, itemStack);
	}

	public List<ItemStack> method_11612(LootContext.Builder builder) {
		return this.getBlock().method_9560(this, builder);
	}

	public boolean method_11629(World world, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		return this.getBlock().method_9534(this, world, blockHitResult.getBlockPos(), playerEntity, hand, blockHitResult);
	}

	public void onBlockBreakStart(World world, BlockPos blockPos, PlayerEntity playerEntity) {
		this.getBlock().method_9606(this, world, blockPos, playerEntity);
	}

	public boolean canSuffocate(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_16362(this, blockView, blockPos);
	}

	public BlockState getStateForNeighborUpdate(Direction direction, BlockState blockState, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return this.getBlock().method_9559(this, direction, blockState, iWorld, blockPos, blockPos2);
	}

	public boolean method_11609(BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return this.getBlock().method_9516(this, blockView, blockPos, blockPlacementEnvironment);
	}

	public boolean canReplace(ItemPlacementContext itemPlacementContext) {
		return this.getBlock().method_9616(this, itemPlacementContext);
	}

	public boolean canPlaceAt(ViewableWorld viewableWorld, BlockPos blockPos) {
		return this.getBlock().method_9558(this, viewableWorld, blockPos);
	}

	public boolean shouldPostProcess(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9552(this, blockView, blockPos);
	}

	@Nullable
	public NameableContainerProvider createContainerProvider(World world, BlockPos blockPos) {
		return this.getBlock().method_17454(this, world, blockPos);
	}

	public boolean matches(Tag<Block> tag) {
		return this.getBlock().matches(tag);
	}

	public FluidState method_11618() {
		return this.getBlock().method_9545(this);
	}

	public boolean hasRandomTicks() {
		return this.getBlock().method_9542(this);
	}

	@Environment(EnvType.CLIENT)
	public long getRenderingSeed(BlockPos blockPos) {
		return this.getBlock().method_9535(this, blockPos);
	}

	public BlockSoundGroup getSoundGroup() {
		return this.getBlock().method_9573(this);
	}

	public void method_19287(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
		this.getBlock().method_19286(world, blockState, blockHitResult, entity);
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
										dynamicOps.createString(PropertyContainer.method_16551((Property<T>)entry.getKey(), (Comparable<?>)entry.getValue()))
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
		BlockState blockState = block.method_9564();
		StateFactory<Block, BlockState> stateFactory = block.method_9595();

		for (Entry<String, String> entry : map.entrySet()) {
			String string = (String)entry.getKey();
			Property<?> property = stateFactory.method_11663(string);
			if (property != null) {
				blockState = PropertyContainer.method_11655(blockState, property, string, dynamic.toString(), (String)entry.getValue());
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
		private final VoxelShape[] field_16560;
		private final VoxelShape field_19360;
		private final boolean field_17651;

		private ShapeCache(BlockState blockState) {
			Block block = blockState.getBlock();
			this.opaque = block.method_9601(blockState);
			this.fullOpaque = block.method_9557(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);
			this.translucent = block.method_9579(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);
			this.lightSubtracted = block.method_9505(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);
			if (!blockState.isOpaque()) {
				this.field_16560 = null;
			} else {
				this.field_16560 = new VoxelShape[DIRECTIONS.length];
				VoxelShape voxelShape = block.method_9571(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);

				for (Direction direction : DIRECTIONS) {
					this.field_16560[direction.ordinal()] = VoxelShapes.method_16344(voxelShape, direction);
				}
			}

			this.field_19360 = block.method_9549(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN, EntityContext.absent());
			this.field_17651 = Arrays.stream(Direction.Axis.values())
				.anyMatch(axis -> this.field_19360.getMinimum(axis) < 0.0 || this.field_19360.getMaximum(axis) > 1.0);
		}
	}
}
