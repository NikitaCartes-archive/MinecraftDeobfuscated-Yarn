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
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.IdList;
import net.minecraft.util.Identifier;
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
import net.minecraft.world.GameRules;
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

public class Block implements ItemConvertible {
	protected static final Logger LOGGER = LogManager.getLogger();
	public static final IdList<BlockState> STATE_IDS = new IdList<>();
	private static final Direction[] FACINGS = new Direction[]{
		Direction.field_11039, Direction.field_11034, Direction.field_11043, Direction.field_11035, Direction.field_11033, Direction.field_11036
	};
	private static final LoadingCache<VoxelShape, Boolean> FULL_CUBE_SHAPE_CACHE = CacheBuilder.newBuilder()
		.maximumSize(512L)
		.weakKeys()
		.build(new CacheLoader<VoxelShape, Boolean>() {
			public Boolean method_20516(VoxelShape voxelShape) {
				return !VoxelShapes.method_1074(VoxelShapes.method_1077(), voxelShape, BooleanBiFunction.NOT_SAME);
			}
		});
	private static final VoxelShape field_18966 = VoxelShapes.method_1072(
		VoxelShapes.method_1077(), method_9541(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST
	);
	private static final VoxelShape field_19061 = method_9541(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);
	protected final int lightLevel;
	protected final float hardness;
	protected final float resistance;
	protected final boolean randomTicks;
	protected final BlockSoundGroup field_10643;
	protected final Material field_10635;
	protected final MaterialColor field_10639;
	private final float slipperiness;
	protected final StateFactory<Block, BlockState> field_10647;
	private BlockState field_10646;
	protected final boolean collidable;
	private final boolean dynamicBounds;
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

	public static int method_9507(@Nullable BlockState blockState) {
		if (blockState == null) {
			return 0;
		} else {
			int i = STATE_IDS.getId(blockState);
			return i == -1 ? 0 : i;
		}
	}

	public static BlockState method_9531(int i) {
		BlockState blockState = STATE_IDS.get(i);
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
			entity.requestTeleport(entity.x, entity.y + 1.0 + d, entity.z);
		}

		return blockState2;
	}

	public static VoxelShape method_9541(double d, double e, double f, double g, double h, double i) {
		return VoxelShapes.method_1081(d / 16.0, e / 16.0, f / 16.0, g / 16.0, h / 16.0, i / 16.0);
	}

	@Deprecated
	public boolean method_9523(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return method_20045(blockState, blockView, blockPos, Direction.field_11036) && this.lightLevel < 14;
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
			for (Direction direction : FACINGS) {
				pooledMutable.method_10114(blockPos).method_10118(direction);
				BlockState blockState2 = iWorld.method_8320(pooledMutable);
				BlockState blockState3 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), blockState, iWorld, pooledMutable, blockPos);
				method_9611(blockState2, blockState3, iWorld, pooledMutable, i);
			}
		}
	}

	public boolean matches(Tag<Block> tag) {
		return tag.contains(this);
	}

	public static BlockState method_9510(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		BlockState blockState2 = blockState;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : FACINGS) {
			mutable.set(blockPos).setOffset(direction);
			blockState2 = blockState2.getStateForNeighborUpdate(direction, iWorld.method_8320(mutable), iWorld, blockPos, mutable);
		}

		return blockState2;
	}

	public static void method_9611(BlockState blockState, BlockState blockState2, IWorld iWorld, BlockPos blockPos, int i) {
		if (blockState2 != blockState) {
			if (blockState2.isAir()) {
				if (!iWorld.isClient()) {
					iWorld.breakBlock(blockPos, (i & 32) == 0);
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
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState;
	}

	@Deprecated
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState;
	}

	public Block(Block.Settings settings) {
		StateFactory.Builder<Block, BlockState> builder = new StateFactory.Builder<>(this);
		this.appendProperties(builder);
		this.field_10635 = settings.field_10668;
		this.field_10639 = settings.field_10662;
		this.collidable = settings.collidable;
		this.field_10643 = settings.field_10665;
		this.lightLevel = settings.luminance;
		this.resistance = settings.resistance;
		this.hardness = settings.hardness;
		this.randomTicks = settings.randomTicks;
		this.slipperiness = settings.slipperiness;
		this.dynamicBounds = settings.dynamicBounds;
		this.dropTableId = settings.dropTableId;
		this.field_10647 = builder.build(BlockState::new);
		this.method_9590(this.field_10647.method_11664());
	}

	public static boolean canConnect(Block block) {
		return block instanceof LeavesBlock
			|| block == Blocks.field_10499
			|| block == Blocks.field_10147
			|| block == Blocks.field_10009
			|| block == Blocks.field_10545
			|| block == Blocks.field_10261;
	}

	@Deprecated
	public boolean method_9521(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.method_11620().blocksLight() && method_9614(blockState.method_11628(blockView, blockPos)) && !blockState.emitsRedstonePower();
	}

	@Deprecated
	public boolean method_16362(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.field_10635.blocksMovement() && method_9614(blockState.method_11628(blockView, blockPos));
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
				return blockView.method_8316(blockPos).matches(FluidTags.field_15517);
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
		return this.field_10635.isReplaceable() && (itemPlacementContext.getStack().isEmpty() || itemPlacementContext.getStack().getItem() != this.asItem());
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
		return extendedBlockView.getLightmapIndex(blockPos, blockState.getLuminance());
	}

	@Environment(EnvType.CLIENT)
	public static boolean method_9607(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState2 = blockView.method_8320(blockPos2);
		if (blockState.isSideInvisible(blockState2, direction)) {
			return false;
		} else if (blockState2.isOpaque()) {
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
		return this.collidable && this.getRenderLayer() == BlockRenderLayer.field_9178;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public boolean method_9522(BlockState blockState, BlockState blockState2, Direction direction) {
		return false;
	}

	@Deprecated
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return VoxelShapes.method_1077();
	}

	@Deprecated
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
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

	public static boolean isSolidMediumSquare(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.method_8320(blockPos);
		return !blockState.matches(BlockTags.field_15503)
			&& !VoxelShapes.method_1074(blockState.method_11628(blockView, blockPos).getFace(Direction.field_11036), field_18966, BooleanBiFunction.ONLY_SECOND);
	}

	public static boolean isSolidSmallSquare(ViewableWorld viewableWorld, BlockPos blockPos, Direction direction) {
		BlockState blockState = viewableWorld.method_8320(blockPos);
		return !blockState.matches(BlockTags.field_15503)
			&& !VoxelShapes.method_1074(blockState.method_11628(viewableWorld, blockPos).getFace(direction), field_19061, BooleanBiFunction.ONLY_SECOND);
	}

	public static boolean method_20045(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return !blockState.matches(BlockTags.field_15503) && method_9501(blockState.method_11628(blockView, blockPos), direction);
	}

	public static boolean method_9501(VoxelShape voxelShape, Direction direction) {
		VoxelShape voxelShape2 = voxelShape.getFace(direction);
		return method_9614(voxelShape2);
	}

	public static boolean method_9614(VoxelShape voxelShape) {
		return FULL_CUBE_SHAPE_CACHE.getUnchecked(voxelShape);
	}

	@Deprecated
	public final boolean method_9557(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.isOpaque() ? method_9614(blockState.method_11615(blockView, blockPos)) : false;
	}

	public boolean method_9579(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !method_9614(blockState.method_17770(blockView, blockPos)) && blockState.method_11618().isEmpty();
	}

	@Deprecated
	public int method_9505(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if (blockState.isFullOpaque(blockView, blockPos)) {
			return blockView.getMaxLightLevel();
		} else {
			return blockState.isTranslucent(blockView, blockPos) ? 0 : 1;
		}
	}

	@Deprecated
	public boolean method_9526(BlockState blockState) {
		return false;
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
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		DebugRendererInfoManager.sendNeighborUpdate(world, blockPos);
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
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
	}

	@Deprecated
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (this.hasBlockEntity() && blockState.getBlock() != blockState2.getBlock()) {
			world.removeBlockEntity(blockPos);
		}
	}

	@Deprecated
	public float method_9594(BlockState blockState, PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		float f = blockState.getHardness(blockView, blockPos);
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

	public Identifier getDropTableId() {
		if (this.dropTableId == null) {
			Identifier identifier = Registry.BLOCK.getId(this);
			this.dropTableId = new Identifier(identifier.getNamespace(), "blocks/" + identifier.getPath());
		}

		return this.dropTableId;
	}

	@Deprecated
	public List<ItemStack> method_9560(BlockState blockState, LootContext.Builder builder) {
		Identifier identifier = this.getDropTableId();
		if (identifier == LootTables.EMPTY) {
			return Collections.emptyList();
		} else {
			LootContext lootContext = builder.method_312(LootContextParameters.field_1224, blockState).method_309(LootContextTypes.field_1172);
			ServerWorld serverWorld = lootContext.getWorld();
			LootSupplier lootSupplier = serverWorld.getServer().getLootManager().getSupplier(identifier);
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
		ServerWorld serverWorld = builder.getWorld();
		BlockPos blockPos = builder.method_308(LootContextParameters.field_1232);
		blockState.method_11612(builder).forEach(itemStack -> dropStack(serverWorld, blockPos, itemStack));
		blockState.onStacksDropped(serverWorld, blockPos, ItemStack.EMPTY);
	}

	public static void method_9497(BlockState blockState, World world, BlockPos blockPos) {
		if (world instanceof ServerWorld) {
			method_9562(blockState, (ServerWorld)world, blockPos, null).forEach(itemStack -> dropStack(world, blockPos, itemStack));
		}

		blockState.onStacksDropped(world, blockPos, ItemStack.EMPTY);
	}

	public static void method_9610(BlockState blockState, World world, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
		if (world instanceof ServerWorld) {
			method_9562(blockState, (ServerWorld)world, blockPos, blockEntity).forEach(itemStack -> dropStack(world, blockPos, itemStack));
		}

		blockState.onStacksDropped(world, blockPos, ItemStack.EMPTY);
	}

	public static void method_9511(BlockState blockState, World world, BlockPos blockPos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack itemStack) {
		if (world instanceof ServerWorld) {
			method_9609(blockState, (ServerWorld)world, blockPos, blockEntity, entity, itemStack).forEach(itemStackx -> dropStack(world, blockPos, itemStackx));
		}

		blockState.onStacksDropped(world, blockPos, itemStack);
	}

	public static void dropStack(World world, BlockPos blockPos, ItemStack itemStack) {
		if (!world.isClient && !itemStack.isEmpty() && world.getGameRules().getBoolean(GameRules.field_19392)) {
			float f = 0.5F;
			double d = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			double e = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			double g = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			ItemEntity itemEntity = new ItemEntity(world, (double)blockPos.getX() + d, (double)blockPos.getY() + e, (double)blockPos.getZ() + g, itemStack);
			itemEntity.setToDefaultPickupDelay();
			world.spawnEntity(itemEntity);
		}
	}

	protected void dropExperience(World world, BlockPos blockPos, int i) {
		if (!world.isClient && world.getGameRules().getBoolean(GameRules.field_19392)) {
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

	public void onDestroyedByExplosion(World world, BlockPos blockPos, Explosion explosion) {
	}

	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9178;
	}

	@Deprecated
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return true;
	}

	@Deprecated
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		return false;
	}

	public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
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
		playerEntity.incrementStat(Stats.field_15427.getOrCreateStat(this));
		playerEntity.addExhaustion(0.005F);
		method_9511(blockState, world, blockPos, blockEntity, playerEntity, itemStack);
	}

	public void method_9567(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
	}

	public boolean canMobSpawnInside() {
		return !this.field_10635.isSolid() && !this.field_10635.isLiquid();
	}

	@Environment(EnvType.CLIENT)
	public Text getName() {
		return new TranslatableText(this.getTranslationKey());
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = SystemUtil.createTranslationKey("block", Registry.BLOCK.getId(this));
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
		return method_9614(blockState.method_11628(blockView, blockPos)) ? 0.2F : 1.0F;
	}

	public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
		entity.handleFallDamage(f, 1.0F);
	}

	public void onEntityLand(BlockView blockView, Entity entity) {
		entity.method_18799(entity.method_18798().multiply(1.0, 0.0, 1.0));
	}

	@Environment(EnvType.CLIENT)
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this);
	}

	public void addStacksForDisplay(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		defaultedList.add(new ItemStack(this));
	}

	@Deprecated
	public FluidState method_9545(BlockState blockState) {
		return Fluids.field_15906.method_15785();
	}

	public float getSlipperiness() {
		return this.slipperiness;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public long method_9535(BlockState blockState, BlockPos blockPos) {
		return MathHelper.hashCode(blockPos);
	}

	public void method_19286(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
	}

	public void method_9576(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		world.playLevelEvent(playerEntity, 2001, blockPos, method_9507(blockState));
	}

	public void onRainTick(World world, BlockPos blockPos) {
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

	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
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
		return Block.OffsetType.field_10656;
	}

	@Deprecated
	public Vec3d method_9540(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Block.OffsetType offsetType = this.getOffsetType();
		if (offsetType == Block.OffsetType.field_10656) {
			return Vec3d.ZERO;
		} else {
			long l = MathHelper.hashCode(blockPos.getX(), 0, blockPos.getZ());
			return new Vec3d(
				((double)((float)(l & 15L) / 15.0F) - 0.5) * 0.5,
				offsetType == Block.OffsetType.field_10655 ? ((double)((float)(l >> 4 & 15L) / 15.0F) - 1.0) * 0.2 : 0.0,
				((double)((float)(l >> 8 & 15L) / 15.0F) - 0.5) * 0.5
			);
		}
	}

	public BlockSoundGroup method_9573(BlockState blockState) {
		return this.field_10643;
	}

	@Override
	public Item asItem() {
		if (this.cachedItem == null) {
			this.cachedItem = Item.method_7867(this);
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
	public void buildTooltip(ItemStack itemStack, @Nullable BlockView blockView, List<Text> list, TooltipContext tooltipContext) {
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
		private final Direction facing;

		public NeighborGroup(BlockState blockState, BlockState blockState2, Direction direction) {
			this.field_10652 = blockState;
			this.field_10654 = blockState2;
			this.facing = direction;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof Block.NeighborGroup)) {
				return false;
			} else {
				Block.NeighborGroup neighborGroup = (Block.NeighborGroup)object;
				return this.field_10652 == neighborGroup.field_10652 && this.field_10654 == neighborGroup.field_10654 && this.facing == neighborGroup.facing;
			}
		}

		public int hashCode() {
			int i = this.field_10652.hashCode();
			i = 31 * i + this.field_10654.hashCode();
			return 31 * i + this.facing.hashCode();
		}
	}

	public static enum OffsetType {
		field_10656,
		field_10657,
		field_10655;
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
		private float slipperiness = 0.6F;
		private Identifier dropTableId;
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
			settings.slipperiness = block.getSlipperiness();
			settings.dynamicBounds = block.dynamicBounds;
			return settings;
		}

		public Block.Settings noCollision() {
			this.collidable = false;
			return this;
		}

		public Block.Settings slipperiness(float f) {
			this.slipperiness = f;
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
			this.dropTableId = LootTables.EMPTY;
			return this;
		}

		public Block.Settings dropsLike(Block block) {
			this.dropTableId = block.getDropTableId();
			return this;
		}
	}
}
