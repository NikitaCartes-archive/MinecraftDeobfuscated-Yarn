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
import net.minecraft.class_4209;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemProvider;
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
import net.minecraft.util.IdList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.hit.BlockHitResult;
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
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block implements ItemProvider {
	protected static final Logger LOGGER = LogManager.getLogger();
	public static final IdList<BlockState> field_10651 = new IdList<>();
	private static final Direction[] field_10644 = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
	protected final int lightLevel;
	protected final float hardness;
	protected final float resistance;
	protected final boolean randomTicks;
	protected final BlockSoundGroup field_10643;
	protected final Material field_10635;
	protected final MaterialColor field_10639;
	private final float friction;
	protected final StateFactory<Block, BlockState> field_10647;
	private BlockState field_10646;
	protected final boolean collidable;
	private final boolean dynamicBounds;
	@Nullable
	private Identifier field_10636;
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

	public static int method_9507(@Nullable BlockState blockState) {
		if (blockState == null) {
			return 0;
		} else {
			int i = field_10651.getId(blockState);
			return i == -1 ? 0 : i;
		}
	}

	public static BlockState method_9531(int i) {
		BlockState blockState = field_10651.get(i);
		return blockState == null ? Blocks.field_10124.method_9564() : blockState;
	}

	public static Block getBlockFromItem(@Nullable Item item) {
		return item instanceof BlockItem ? ((BlockItem)item).method_7711() : Blocks.field_10124;
	}

	public static BlockState method_9582(BlockState blockState, BlockState blockState2, World world, BlockPos blockPos) {
		VoxelShape voxelShape = VoxelShapes.method_1082(
				blockState.method_11628(world, blockPos), blockState2.method_11628(world, blockPos), BooleanBiFunction.ONLY_SECOND
			)
			.offset((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());

		for (Entity entity : world.method_8335(null, voxelShape.getBoundingBox())) {
			double d = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entity.method_5829().offset(0.0, 1.0, 0.0), Stream.of(voxelShape), -1.0);
			entity.method_5859(entity.x, entity.y + 1.0 + d, entity.z);
		}

		return blockState2;
	}

	public static VoxelShape method_9541(double d, double e, double f, double g, double h, double i) {
		return VoxelShapes.method_1081(d / 16.0, e / 16.0, f / 16.0, g / 16.0, h / 16.0, i / 16.0);
	}

	@Deprecated
	public boolean method_9523(BlockState blockState, Entity entity) {
		return true;
	}

	@Deprecated
	public boolean method_9500(BlockState blockState) {
		return false;
	}

	@Deprecated
	public int method_9593(BlockState blockState) {
		return this.lightLevel;
	}

	@Deprecated
	public Material method_9597(BlockState blockState) {
		return this.field_10635;
	}

	@Deprecated
	public MaterialColor method_9602(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.field_10639;
	}

	@Deprecated
	public void method_9528(BlockState blockState, IWorld iWorld, BlockPos blockPos, int i) {
		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : field_10644) {
				pooledMutable.method_10114(blockPos).method_10118(direction);
				BlockState blockState2 = iWorld.method_8320(pooledMutable);
				BlockState blockState3 = blockState2.method_11578(direction.getOpposite(), blockState, iWorld, pooledMutable, blockPos);
				method_9611(blockState2, blockState3, iWorld, pooledMutable, i);
			}
		}
	}

	public boolean method_9525(Tag<Block> tag) {
		return tag.contains(this);
	}

	public static BlockState method_9510(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		BlockState blockState2 = blockState;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : field_10644) {
			mutable.method_10101(blockPos).method_10098(direction);
			blockState2 = blockState2.method_11578(direction, iWorld.method_8320(mutable), iWorld, blockPos, mutable);
		}

		return blockState2;
	}

	public static void method_9611(BlockState blockState, BlockState blockState2, IWorld iWorld, BlockPos blockPos, int i) {
		if (blockState2 != blockState) {
			if (blockState2.isAir()) {
				if (!iWorld.isClient()) {
					iWorld.method_8651(blockPos, (i & 32) == 0);
				}
			} else {
				iWorld.method_8652(blockPos, blockState2, i & -33);
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
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState;
	}

	@Deprecated
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		return blockState;
	}

	public Block(Block.Settings settings) {
		StateFactory.Builder<Block, BlockState> builder = new StateFactory.Builder<>(this);
		this.method_9515(builder);
		this.field_10635 = settings.field_10668;
		this.field_10639 = settings.field_10662;
		this.collidable = settings.collidable;
		this.field_10643 = settings.field_10665;
		this.lightLevel = settings.luminance;
		this.resistance = settings.resistance;
		this.hardness = settings.hardness;
		this.randomTicks = settings.randomTicks;
		this.friction = settings.friction;
		this.dynamicBounds = settings.dynamicBounds;
		this.field_10636 = settings.field_10666;
		this.field_10647 = builder.method_11668(BlockState::new);
		this.method_9590(this.field_10647.method_11664());
	}

	protected static boolean method_9553(Block block) {
		return block instanceof ShulkerBoxBlock
			|| block instanceof LeavesBlock
			|| block.method_9525(BlockTags.field_15487)
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
	public boolean method_9521(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.method_11620().method_15804() && blockState.method_11604(blockView, blockPos) && !blockState.emitsRedstonePower();
	}

	@Deprecated
	public boolean method_16362(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.field_10635.suffocates() && blockState.method_11604(blockView, blockPos);
	}

	public final boolean method_9555(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.method_11603(blockView, blockPos);
	}

	@Deprecated
	public boolean method_9561(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.method_11620().method_15804() && blockState.method_11604(blockView, blockPos);
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public boolean method_9589(BlockState blockState) {
		return false;
	}

	@Deprecated
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return !method_9614(blockState.method_11628(blockView, blockPos));
			case field_48:
				return blockView.method_8316(blockPos).method_15767(FluidTags.field_15517);
			case field_51:
				return !method_9614(blockState.method_11628(blockView, blockPos));
			default:
				return false;
		}
	}

	@Deprecated
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Deprecated
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		return this.field_10635.isReplaceable() && (itemPlacementContext.getItemStack().isEmpty() || itemPlacementContext.getItemStack().getItem() != this.getItem());
	}

	@Deprecated
	public float method_9537(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.hardness;
	}

	public boolean method_9542(BlockState blockState) {
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
	public int method_9546(BlockState blockState, ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		return extendedBlockView.method_8313(blockPos, blockState.getLuminance());
	}

	@Environment(EnvType.CLIENT)
	public static boolean method_9607(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.method_10093(direction);
		BlockState blockState2 = blockView.method_8320(blockPos2);
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
				boolean bl = VoxelShapes.method_1074(voxelShape, voxelShape2, BooleanBiFunction.ONLY_FIRST);
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
	public boolean method_9601(BlockState blockState) {
		return this.collidable && this.getRenderLayer() == BlockRenderLayer.SOLID;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public boolean method_9522(BlockState blockState, BlockState blockState2, Direction direction) {
		return false;
	}

	@Deprecated
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return VoxelShapes.method_1077();
	}

	@Deprecated
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.collidable ? blockState.method_17770(blockView, blockPos) : VoxelShapes.method_1073();
	}

	@Deprecated
	public VoxelShape method_9571(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.method_17770(blockView, blockPos);
	}

	@Deprecated
	public VoxelShape method_9584(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return VoxelShapes.method_1073();
	}

	public static boolean method_9501(VoxelShape voxelShape, Direction direction) {
		VoxelShape voxelShape2 = voxelShape.method_1098(direction);
		return method_9614(voxelShape2);
	}

	public static boolean method_9614(VoxelShape voxelShape) {
		return !VoxelShapes.method_1074(VoxelShapes.method_1077(), voxelShape, BooleanBiFunction.NOT_SAME);
	}

	@Deprecated
	public final boolean method_9557(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.isFullBoundsCubeForCulling() ? method_9614(blockState.method_11615(blockView, blockPos)) : false;
	}

	public boolean method_9579(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !method_9614(blockState.method_17770(blockView, blockPos)) && blockState.method_11618().isEmpty();
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

	@Deprecated
	public void method_9514(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.method_9588(blockState, world, blockPos, random);
	}

	@Deprecated
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
	}

	@Environment(EnvType.CLIENT)
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
	}

	public void method_9585(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
	}

	@Deprecated
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		class_4209.method_19472(world, blockPos);
	}

	public int getTickRate(ViewableWorld viewableWorld) {
		return 10;
	}

	@Nullable
	@Deprecated
	public NameableContainerProvider method_17454(BlockState blockState, World world, BlockPos blockPos) {
		return null;
	}

	@Deprecated
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
	}

	@Deprecated
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (this.hasBlockEntity() && blockState.getBlock() != blockState2.getBlock()) {
			world.method_8544(blockPos);
		}
	}

	@Deprecated
	public float method_9594(BlockState blockState, PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		float f = blockState.method_11579(blockView, blockPos);
		if (f == -1.0F) {
			return 0.0F;
		} else {
			int i = playerEntity.method_7305(blockState) ? 30 : 100;
			return playerEntity.method_7351(blockState) / f / (float)i;
		}
	}

	@Deprecated
	public void method_9565(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
	}

	public Identifier method_9580() {
		if (this.field_10636 == null) {
			Identifier identifier = Registry.BLOCK.method_10221(this);
			this.field_10636 = new Identifier(identifier.getNamespace(), "blocks/" + identifier.getPath());
		}

		return this.field_10636;
	}

	@Deprecated
	public List<ItemStack> method_9560(BlockState blockState, LootContext.Builder builder) {
		Identifier identifier = this.method_9580();
		if (identifier == LootTables.field_844) {
			return Collections.emptyList();
		} else {
			LootContext lootContext = builder.method_312(LootContextParameters.field_1224, blockState).method_309(LootContextTypes.BLOCK);
			ServerWorld serverWorld = lootContext.method_299();
			LootSupplier lootSupplier = serverWorld.getServer().getLootManager().method_367(identifier);
			return lootSupplier.getDrops(lootContext);
		}
	}

	public static List<ItemStack> method_9562(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
		LootContext.Builder builder = new LootContext.Builder(serverWorld)
			.setRandom(serverWorld.random)
			.method_312(LootContextParameters.field_1232, blockPos)
			.method_312(LootContextParameters.field_1229, ItemStack.EMPTY)
			.method_306(LootContextParameters.field_1228, blockEntity);
		return blockState.method_11612(builder);
	}

	public static List<ItemStack> method_9609(
		BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack itemStack
	) {
		LootContext.Builder builder = new LootContext.Builder(serverWorld)
			.setRandom(serverWorld.random)
			.method_312(LootContextParameters.field_1232, blockPos)
			.method_312(LootContextParameters.field_1229, itemStack)
			.method_312(LootContextParameters.field_1226, entity)
			.method_306(LootContextParameters.field_1228, blockEntity);
		return blockState.method_11612(builder);
	}

	public static void method_9566(BlockState blockState, LootContext.Builder builder) {
		ServerWorld serverWorld = builder.method_313();
		BlockPos blockPos = builder.method_308(LootContextParameters.field_1232);
		blockState.method_11612(builder).forEach(itemStack -> method_9577(serverWorld, blockPos, itemStack));
		blockState.method_11595(serverWorld, blockPos, ItemStack.EMPTY);
	}

	public static void method_9497(BlockState blockState, World world, BlockPos blockPos) {
		if (world instanceof ServerWorld) {
			method_9562(blockState, (ServerWorld)world, blockPos, null).forEach(itemStack -> method_9577(world, blockPos, itemStack));
		}

		blockState.method_11595(world, blockPos, ItemStack.EMPTY);
	}

	public static void method_9610(BlockState blockState, World world, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
		if (world instanceof ServerWorld) {
			method_9562(blockState, (ServerWorld)world, blockPos, blockEntity).forEach(itemStack -> method_9577(world, blockPos, itemStack));
		}

		blockState.method_11595(world, blockPos, ItemStack.EMPTY);
	}

	public static void method_9511(BlockState blockState, World world, BlockPos blockPos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack itemStack) {
		if (world instanceof ServerWorld) {
			method_9609(blockState, (ServerWorld)world, blockPos, blockEntity, entity, itemStack).forEach(itemStackx -> method_9577(world, blockPos, itemStackx));
		}

		blockState.method_11595(world, blockPos, itemStack);
	}

	public static void method_9577(World world, BlockPos blockPos, ItemStack itemStack) {
		if (!world.isClient && !itemStack.isEmpty() && world.getGameRules().getBoolean("doTileDrops")) {
			float f = 0.5F;
			double d = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			double e = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			double g = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			ItemEntity itemEntity = new ItemEntity(world, (double)blockPos.getX() + d, (double)blockPos.getY() + e, (double)blockPos.getZ() + g, itemStack);
			itemEntity.setToDefaultPickupDelay();
			world.spawnEntity(itemEntity);
		}
	}

	protected void method_9583(World world, BlockPos blockPos, int i) {
		if (!world.isClient && world.getGameRules().getBoolean("doTileDrops")) {
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

	public void method_9586(World world, BlockPos blockPos, Explosion explosion) {
	}

	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Deprecated
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return true;
	}

	@Deprecated
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		return false;
	}

	public void method_9591(World world, BlockPos blockPos, Entity entity) {
	}

	@Nullable
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564();
	}

	@Deprecated
	public void method_9606(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
	}

	@Deprecated
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return 0;
	}

	@Deprecated
	public boolean method_9506(BlockState blockState) {
		return false;
	}

	@Deprecated
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
	}

	@Deprecated
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return 0;
	}

	public void method_9556(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		playerEntity.method_7259(Stats.field_15427.getOrCreateStat(this));
		playerEntity.addExhaustion(0.005F);
		method_9511(blockState, world, blockPos, blockEntity, playerEntity, itemStack);
	}

	public void method_9567(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
	}

	public boolean canMobSpawnInside() {
		return !this.field_10635.method_15799() && !this.field_10635.isLiquid();
	}

	@Environment(EnvType.CLIENT)
	public TextComponent method_9518() {
		return new TranslatableTextComponent(this.getTranslationKey());
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = SystemUtil.method_646("block", Registry.BLOCK.method_10221(this));
		}

		return this.translationKey;
	}

	@Deprecated
	public boolean method_9592(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
		return false;
	}

	@Deprecated
	public PistonBehavior method_9527(BlockState blockState) {
		return this.field_10635.method_15798();
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public float method_9575(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.method_11603(blockView, blockPos) ? 0.2F : 1.0F;
	}

	public void method_9554(World world, BlockPos blockPos, Entity entity, float f) {
		entity.handleFallDamage(f, 1.0F);
	}

	public void onEntityLand(BlockView blockView, Entity entity) {
		entity.method_18799(entity.method_18798().multiply(1.0, 0.0, 1.0));
	}

	@Environment(EnvType.CLIENT)
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this);
	}

	public void method_9578(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		defaultedList.add(new ItemStack(this));
	}

	@Deprecated
	public FluidState method_9545(BlockState blockState) {
		return Fluids.EMPTY.method_15785();
	}

	public float getFrictionCoefficient() {
		return this.friction;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public long method_9535(BlockState blockState, BlockPos blockPos) {
		return MathHelper.hashCode(blockPos);
	}

	public void method_19286(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
	}

	public void method_9576(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		world.method_8444(playerEntity, 2001, blockPos, method_9507(blockState));
	}

	public void method_9504(World world, BlockPos blockPos) {
	}

	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return true;
	}

	@Deprecated
	public boolean method_9498(BlockState blockState) {
		return false;
	}

	@Deprecated
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		return 0;
	}

	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
	}

	public StateFactory<Block, BlockState> method_9595() {
		return this.field_10647;
	}

	protected final void method_9590(BlockState blockState) {
		this.field_10646 = blockState;
	}

	public final BlockState method_9564() {
		return this.field_10646;
	}

	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.NONE;
	}

	@Deprecated
	public Vec3d method_9540(BlockState blockState, BlockView blockView, BlockPos blockPos) {
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

	public BlockSoundGroup method_9573(BlockState blockState) {
		return this.field_10643;
	}

	@Override
	public Item getItem() {
		if (this.cachedItem == null) {
			this.cachedItem = Item.method_7867(this);
		}

		return this.cachedItem;
	}

	public boolean hasDynamicBounds() {
		return this.dynamicBounds;
	}

	public String toString() {
		return "Block{" + Registry.BLOCK.method_10221(this) + "}";
	}

	@Environment(EnvType.CLIENT)
	public void buildTooltip(ItemStack itemStack, @Nullable BlockView blockView, List<TextComponent> list, TooltipContext tooltipContext) {
	}

	public static boolean isNaturalStone(Block block) {
		return block == Blocks.field_10340 || block == Blocks.field_10474 || block == Blocks.field_10508 || block == Blocks.field_10115;
	}

	public static boolean isNaturalDirt(Block block) {
		return block == Blocks.field_10566 || block == Blocks.field_10253 || block == Blocks.field_10520;
	}

	public static final class NeighborGroup {
		private final BlockState field_10652;
		private final BlockState field_10654;
		private final Direction field_10653;

		public NeighborGroup(BlockState blockState, BlockState blockState2, Direction direction) {
			this.field_10652 = blockState;
			this.field_10654 = blockState2;
			this.field_10653 = direction;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof Block.NeighborGroup)) {
				return false;
			} else {
				Block.NeighborGroup neighborGroup = (Block.NeighborGroup)object;
				return this.field_10652 == neighborGroup.field_10652 && this.field_10654 == neighborGroup.field_10654 && this.field_10653 == neighborGroup.field_10653;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.field_10652, this.field_10654, this.field_10653});
		}
	}

	public static enum OffsetType {
		NONE,
		XZ,
		XYZ;
	}

	public static class Settings {
		private Material field_10668;
		private MaterialColor field_10662;
		private boolean collidable = true;
		private BlockSoundGroup field_10665 = BlockSoundGroup.STONE;
		private int luminance;
		private float resistance;
		private float hardness;
		private boolean randomTicks;
		private float friction = 0.6F;
		private Identifier field_10666;
		private boolean dynamicBounds;

		private Settings(Material material, MaterialColor materialColor) {
			this.field_10668 = material;
			this.field_10662 = materialColor;
		}

		public static Block.Settings method_9637(Material material) {
			return method_9639(material, material.method_15803());
		}

		public static Block.Settings method_9617(Material material, DyeColor dyeColor) {
			return method_9639(material, dyeColor.method_7794());
		}

		public static Block.Settings method_9639(Material material, MaterialColor materialColor) {
			return new Block.Settings(material, materialColor);
		}

		public static Block.Settings copy(Block block) {
			Block.Settings settings = new Block.Settings(block.field_10635, block.field_10639);
			settings.field_10668 = block.field_10635;
			settings.hardness = block.hardness;
			settings.resistance = block.resistance;
			settings.collidable = block.collidable;
			settings.randomTicks = block.randomTicks;
			settings.luminance = block.lightLevel;
			settings.field_10662 = block.field_10639;
			settings.field_10665 = block.field_10643;
			settings.friction = block.getFrictionCoefficient();
			settings.dynamicBounds = block.dynamicBounds;
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

		protected Block.Settings method_9626(BlockSoundGroup blockSoundGroup) {
			this.field_10665 = blockSoundGroup;
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

		protected Block.Settings hasDynamicBounds() {
			this.dynamicBounds = true;
			return this;
		}

		protected Block.Settings dropsNothing() {
			this.field_10666 = LootTables.field_844;
			return this;
		}

		public Block.Settings dropsLike(Block block) {
			this.field_10666 = block.method_9580();
			return this;
		}
	}
}
