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
		this.luminance = block.method_9593(this);
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

	public Material method_11620() {
		return this.getBlock().method_9597(this);
	}

	public boolean allowsSpawning(Entity entity) {
		return this.getBlock().method_9523(this, entity);
	}

	public boolean method_11623(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.field_16556 : this.getBlock().method_9579(this, blockView, blockPos);
	}

	public int method_11581(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.field_16555 : this.getBlock().method_9505(this, blockView, blockPos);
	}

	@Environment(EnvType.CLIENT)
	public VoxelShape method_16384(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.shapeCache != null && this.shapeCache.field_16560 != null
			? this.shapeCache.field_16560[direction.ordinal()]
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
		return this.getBlock().method_9500(this);
	}

	public boolean method_11593(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9599(this, blockView, blockPos);
	}

	public MaterialColor method_11625(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9602(this, blockView, blockPos);
	}

	public BlockState rotate(Rotation rotation) {
		return this.getBlock().method_9598(this, rotation);
	}

	public BlockState mirror(Mirror mirror) {
		return this.getBlock().method_9569(this, mirror);
	}

	public boolean method_11604(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9555(this, blockView, blockPos);
	}

	@Environment(EnvType.CLIENT)
	public boolean hasBlockEntityBreakingRender() {
		return this.getBlock().method_9589(this);
	}

	public BlockRenderType getRenderType() {
		return this.getBlock().method_9604(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11632(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		return this.getBlock().method_9546(this, extendedBlockView, blockPos);
	}

	@Environment(EnvType.CLIENT)
	public float method_11596(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9575(this, blockView, blockPos);
	}

	public boolean method_11603(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_16361(this, blockView, blockPos);
	}

	public boolean method_11621(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9521(this, blockView, blockPos);
	}

	public boolean emitsRedstonePower() {
		return this.getBlock().method_9506(this);
	}

	public int method_11597(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.getBlock().method_9524(this, blockView, blockPos, direction);
	}

	public boolean hasComparatorOutput() {
		return this.getBlock().method_9498(this);
	}

	public int method_11627(World world, BlockPos blockPos) {
		return this.getBlock().method_9572(this, world, blockPos);
	}

	public float method_11579(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9537(this, blockView, blockPos);
	}

	public float method_11589(PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9594(this, playerEntity, blockView, blockPos);
	}

	public int method_11577(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.getBlock().method_9603(this, blockView, blockPos, direction);
	}

	public PistonBehavior method_11586() {
		return this.getBlock().method_9527(this);
	}

	public boolean method_11598(BlockView blockView, BlockPos blockPos) {
		return this.shapeCache != null ? this.shapeCache.fullOpaque : this.getBlock().method_9557(this, blockView, blockPos);
	}

	public boolean isFullBoundsCubeForCulling() {
		return this.shapeCache != null ? this.shapeCache.cull : this.getBlock().method_9601(this);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11592(BlockState blockState, Direction direction) {
		return this.getBlock().method_9522(this, blockState, direction);
	}

	public VoxelShape method_17770(BlockView blockView, BlockPos blockPos) {
		return this.method_11606(blockView, blockPos, VerticalEntityPosition.minValue());
	}

	public VoxelShape method_11606(BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.getBlock().method_9530(this, blockView, blockPos, verticalEntityPosition);
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

	public VoxelShape method_11607(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9584(this, blockView, blockPos);
	}

	public boolean method_11631(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9561(this, blockView, blockPos);
	}

	public Vec3d method_11599(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9540(this, blockView, blockPos);
	}

	public boolean method_11583(World world, BlockPos blockPos, int i, int j) {
		return this.getBlock().method_9592(this, world, blockPos, i, j);
	}

	public void method_11622(World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		this.getBlock().method_9612(this, world, blockPos, block, blockPos2);
	}

	public void method_11635(IWorld iWorld, BlockPos blockPos, int i) {
		this.getBlock().method_9528(this, iWorld, blockPos, i);
	}

	public void method_11637(IWorld iWorld, BlockPos blockPos, int i) {
		this.getBlock().method_9517(this, iWorld, blockPos, i);
	}

	public void method_11580(World world, BlockPos blockPos, BlockState blockState) {
		this.getBlock().method_9615(this, world, blockPos, blockState);
	}

	public void method_11600(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		this.getBlock().method_9536(this, world, blockPos, blockState, bl);
	}

	public void method_11585(World world, BlockPos blockPos, Random random) {
		this.getBlock().method_9588(this, world, blockPos, random);
	}

	public void method_11624(World world, BlockPos blockPos, Random random) {
		this.getBlock().method_9514(this, world, blockPos, random);
	}

	public void method_11613(World world, BlockPos blockPos, Entity entity) {
		this.getBlock().method_9548(this, world, blockPos, entity);
	}

	public void method_11595(World world, BlockPos blockPos, ItemStack itemStack) {
		this.getBlock().method_9565(this, world, blockPos, itemStack);
	}

	public List<ItemStack> method_11612(LootContext.Builder builder) {
		return this.getBlock().method_9560(this, builder);
	}

	public boolean method_11629(World world, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		return this.getBlock().method_9534(this, world, blockHitResult.method_17777(), playerEntity, hand, blockHitResult);
	}

	public void method_11636(World world, BlockPos blockPos, PlayerEntity playerEntity) {
		this.getBlock().method_9606(this, world, blockPos, playerEntity);
	}

	public boolean method_11582(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_16362(this, blockView, blockPos);
	}

	public BlockState method_11578(Direction direction, BlockState blockState, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return this.getBlock().method_9559(this, direction, blockState, iWorld, blockPos, blockPos2);
	}

	public boolean method_11609(BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return this.getBlock().method_9516(this, blockView, blockPos, blockPlacementEnvironment);
	}

	public boolean method_11587(ItemPlacementContext itemPlacementContext) {
		return this.getBlock().method_9616(this, itemPlacementContext);
	}

	public boolean method_11591(ViewableWorld viewableWorld, BlockPos blockPos) {
		return this.getBlock().method_9558(this, viewableWorld, blockPos);
	}

	public boolean method_11601(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_9552(this, blockView, blockPos);
	}

	@Nullable
	public NameableContainerProvider method_17526(World world, BlockPos blockPos) {
		return this.getBlock().method_17454(this, world, blockPos);
	}

	public boolean method_11602(Tag<Block> tag) {
		return this.getBlock().method_9525(tag);
	}

	public FluidState method_11618() {
		return this.getBlock().method_9545(this);
	}

	public boolean hasRandomTicks() {
		return this.getBlock().method_9542(this);
	}

	@Environment(EnvType.CLIENT)
	public long method_11617(BlockPos blockPos) {
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
				ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.BLOCK.method_10221(blockState.getBlock()).toString()))
			);
		} else {
			object = dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("Name"),
					dynamicOps.createString(Registry.BLOCK.method_10221(blockState.getBlock()).toString()),
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
		Block block = Registry.BLOCK
			.method_10223(new Identifier((String)dynamic.getElement("Name").flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:air")));
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
		private static final Direction[] field_16559 = Direction.values();
		private final boolean cull;
		private final boolean fullOpaque;
		private final boolean field_16556;
		private final int field_16555;
		private final VoxelShape[] field_16560;
		private final boolean field_17651;

		private ShapeCache(BlockState blockState) {
			Block block = blockState.getBlock();
			this.cull = block.method_9601(blockState);
			this.fullOpaque = block.method_9557(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);
			this.field_16556 = block.method_9579(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);
			this.field_16555 = block.method_9505(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);
			if (!blockState.isFullBoundsCubeForCulling()) {
				this.field_16560 = null;
			} else {
				this.field_16560 = new VoxelShape[field_16559.length];
				VoxelShape voxelShape = block.method_9571(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN);

				for (Direction direction : field_16559) {
					this.field_16560[direction.ordinal()] = VoxelShapes.method_16344(voxelShape, direction);
				}
			}

			VoxelShape voxelShape = block.method_9549(blockState, EmptyBlockView.field_12294, BlockPos.ORIGIN, VerticalEntityPosition.minValue());
			this.field_17651 = Arrays.stream(Direction.Axis.values()).anyMatch(axis -> voxelShape.getMinimum(axis) < 0.0 || voxelShape.getMaximum(axis) > 1.0);
		}
	}
}
