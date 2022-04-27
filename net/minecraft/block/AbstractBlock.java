/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

/**
 * An abstract class that defines some logic for {@link Block blocks}.
 * 
 * <p id="deprecated-methods">Deprecated methods in this class mean they
 * should only be called from the corresponding method in {@link
 * AbstractBlockState} or subclasses of this class. In vanilla subclasses,
 * these methods are called either to do the default behavior (e.g.
 * {@code super.onUse(...)}) or to delegate logic to other blocks (e.g.
 * {@link net.minecraft.block.StairsBlock#randomTick
 * StairsBlock#randomTick} calls {@code randomTick} of its base block).
 * It's fine to override them, as they are overridden by vanilla blocks.
 */
public abstract class AbstractBlock {
    protected static final Direction[] DIRECTIONS = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
    protected final Material material;
    protected final boolean collidable;
    protected final float resistance;
    protected final boolean randomTicks;
    protected final BlockSoundGroup soundGroup;
    protected final float slipperiness;
    protected final float velocityMultiplier;
    protected final float jumpVelocityMultiplier;
    protected final boolean dynamicBounds;
    protected final Settings settings;
    @Nullable
    protected Identifier lootTableId;

    public AbstractBlock(Settings settings) {
        this.material = settings.material;
        this.collidable = settings.collidable;
        this.lootTableId = settings.lootTableId;
        this.resistance = settings.resistance;
        this.randomTicks = settings.randomTicks;
        this.soundGroup = settings.soundGroup;
        this.slipperiness = settings.slipperiness;
        this.velocityMultiplier = settings.velocityMultiplier;
        this.jumpVelocityMultiplier = settings.jumpVelocityMultiplier;
        this.dynamicBounds = settings.dynamicBounds;
        this.settings = settings;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#prepare(WorldAccess, BlockPos, int, int)} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#canPathfindThrough} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        switch (type) {
            case LAND: {
                return !state.isFullCube(world, pos);
            }
            case WATER: {
                return world.getFluidState(pos).isIn(FluidTags.WATER);
            }
            case AIR: {
                return !state.isFullCube(world, pos);
            }
        }
        return false;
    }

    /**
     * Gets the possibly updated block state of this block when a neighboring block is updated.
     * 
     * @return the new state of this block
     * 
     * @deprecated Consider calling {@link AbstractBlockState#getStateForNeighborUpdate} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     * 
     * @param neighborState the state of the updated neighbor block
     * @param direction the direction from this block to the neighbor
     * @param state the state of this block
     * @param neighborPos the position of the neighbor block
     * @param pos the position of this block
     * @param world the world
     */
    @Deprecated
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#isSideInvisible} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return false;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#neighborUpdate} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        DebugInfoSender.sendNeighborUpdate(world, pos);
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#onBlockAdded} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
    }

    /**
     * Called in {@link net.minecraft.world.chunk.WorldChunk#setBlockState(BlockPos, BlockState, boolean)} if {@code newState} is different from {@code state}. Vanilla blocks perform removal cleanups here.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#onStateReplaced} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
            world.removeBlockEntity(pos);
        }
    }

    /**
     * Called when this block is used by a player.
     * This, by default, is bound to using the right mouse button.
     * 
     * <p>This method is called on both the logical client and logical server, so take caution when overriding this method.
     * The logical side can be checked using {@link net.minecraft.world.World#isClient() world.isClient()}.
     * 
     * <p>If the action result is successful on a logical client, then the action will be sent to the logical server for processing.
     * 
     * @return an action result that specifies if using the block was successful.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#onUse} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#onSyncedBlockEvent} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        return false;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getRenderType} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#hasSidedTransparency} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public boolean hasSidedTransparency(BlockState state) {
        return false;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#emitsRedstonePower} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public boolean emitsRedstonePower(BlockState state) {
        return false;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getPistonBehavior} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public PistonBehavior getPistonBehavior(BlockState state) {
        return this.material.getPistonBehavior();
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getFluidState} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public FluidState getFluidState(BlockState state) {
        return Fluids.EMPTY.getDefaultState();
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#hasComparatorOutput} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public boolean hasComparatorOutput(BlockState state) {
        return false;
    }

    public float getMaxHorizontalModelOffset() {
        return 0.25f;
    }

    public float getVerticalModelOffsetMultiplier() {
        return 0.2f;
    }

    /**
     * Applies a block rotation to a block state.
     * 
     * <p>By default, this returns the provided block state.
     * 
     * @return the rotated block state
     * 
     * @deprecated Consider calling {@link AbstractBlockState#rotate} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#mirror} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#canReplace} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return this.material.isReplaceable() && (context.getStack().isEmpty() || !context.getStack().isOf(this.asItem()));
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#canBucketPlace} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public boolean canBucketPlace(BlockState state, Fluid fluid) {
        return this.material.isReplaceable() || !this.material.isSolid();
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getDroppedStacks} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        Identifier identifier = this.getLootTableId();
        if (identifier == LootTables.EMPTY) {
            return Collections.emptyList();
        }
        LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
        ServerWorld serverWorld = lootContext.getWorld();
        LootTable lootTable = serverWorld.getServer().getLootManager().getTable(identifier);
        return lootTable.generateLoot(lootContext);
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getRenderingSeed} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos);
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getCullingShape} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return state.getOutlineShape(world, pos);
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getSidesShape} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return this.getCollisionShape(state, world, pos, ShapeContext.absent());
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getRaycastShape} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getOpacity} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        if (state.isOpaqueFullCube(world, pos)) {
            return world.getMaxLightLevel();
        }
        return state.isTranslucent(world, pos) ? 0 : 1;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#createScreenHandlerFactory} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Nullable
    @Deprecated
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return null;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#canPlaceAt} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getAmbientOcclusionLightLevel} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return state.isFullCube(world, pos) ? 0.2f : 1.0f;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getComparatorOutput} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 0;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getOutlineShape(BlockView, BlockPos, ShapeContext)} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getCollisionShape(BlockView, BlockPos, ShapeContext)} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.collidable ? state.getOutlineShape(world, pos) : VoxelShapes.empty();
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#isFullCube} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return Block.isShapeFullCube(state.getCollisionShape(world, pos));
    }

    @Deprecated
    public boolean isCullingShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return Block.isShapeFullCube(state.getCullingShape(world, pos));
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getCameraCollisionShape} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getCollisionShape(state, world, pos, context);
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#randomTick} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, AbstractRandom random) {
        this.scheduledTick(state, world, pos, random);
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#scheduledTick} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, AbstractRandom random) {
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#calcBlockBreakingDelta} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        float f = state.getHardness(world, pos);
        if (f == -1.0f) {
            return 0.0f;
        }
        int i = player.canHarvest(state) ? 30 : 100;
        return player.getBlockBreakingSpeed(state) / f / (float)i;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#onStacksDropped} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, boolean dropExperience) {
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#onBlockBreakStart} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getWeakRedstonePower} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#onEntityCollision} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getStrongRedstonePower} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    public final Identifier getLootTableId() {
        if (this.lootTableId == null) {
            Identifier identifier = Registry.BLOCK.getId(this.asBlock());
            this.lootTableId = new Identifier(identifier.getNamespace(), "blocks/" + identifier.getPath());
        }
        return this.lootTableId;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#onProjectileHit} instead. See <a href="#deprecated-methods">the class javadoc</a>.
     */
    @Deprecated
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
    }

    public abstract Item asItem();

    protected abstract Block asBlock();

    public MapColor getDefaultMapColor() {
        return this.settings.mapColorProvider.apply(this.asBlock().getDefaultState());
    }

    public float getHardness() {
        return this.settings.hardness;
    }

    public static class Settings {
        Material material;
        Function<BlockState, MapColor> mapColorProvider;
        boolean collidable = true;
        BlockSoundGroup soundGroup = BlockSoundGroup.STONE;
        ToIntFunction<BlockState> luminance = state -> 0;
        float resistance;
        float hardness;
        boolean toolRequired;
        boolean randomTicks;
        float slipperiness = 0.6f;
        float velocityMultiplier = 1.0f;
        float jumpVelocityMultiplier = 1.0f;
        Identifier lootTableId;
        boolean opaque = true;
        boolean isAir;
        TypedContextPredicate<EntityType<?>> allowsSpawningPredicate = (state, world, pos, type) -> state.isSideSolidFullSquare(world, pos, Direction.UP) && state.getLuminance() < 14;
        ContextPredicate solidBlockPredicate = (state, world, pos) -> state.getMaterial().blocksLight() && state.isFullCube(world, pos);
        ContextPredicate suffocationPredicate;
        ContextPredicate blockVisionPredicate = this.suffocationPredicate = (state, world, pos) -> this.material.blocksMovement() && state.isFullCube(world, pos);
        ContextPredicate postProcessPredicate = (state, world, pos) -> false;
        ContextPredicate emissiveLightingPredicate = (state, world, pos) -> false;
        boolean dynamicBounds;
        Function<BlockState, OffsetType> offsetType = state -> OffsetType.NONE;

        private Settings(Material material, MapColor mapColorProvider) {
            this(material, (BlockState state) -> mapColorProvider);
        }

        private Settings(Material material, Function<BlockState, MapColor> mapColorProvider) {
            this.material = material;
            this.mapColorProvider = mapColorProvider;
        }

        public static Settings of(Material material) {
            return Settings.of(material, material.getColor());
        }

        public static Settings of(Material material, DyeColor color) {
            return Settings.of(material, color.getMapColor());
        }

        public static Settings of(Material material, MapColor color) {
            return new Settings(material, color);
        }

        public static Settings of(Material material, Function<BlockState, MapColor> mapColor) {
            return new Settings(material, mapColor);
        }

        public static Settings copy(AbstractBlock block) {
            Settings settings = new Settings(block.material, block.settings.mapColorProvider);
            settings.material = block.settings.material;
            settings.hardness = block.settings.hardness;
            settings.resistance = block.settings.resistance;
            settings.collidable = block.settings.collidable;
            settings.randomTicks = block.settings.randomTicks;
            settings.luminance = block.settings.luminance;
            settings.mapColorProvider = block.settings.mapColorProvider;
            settings.soundGroup = block.settings.soundGroup;
            settings.slipperiness = block.settings.slipperiness;
            settings.velocityMultiplier = block.settings.velocityMultiplier;
            settings.dynamicBounds = block.settings.dynamicBounds;
            settings.opaque = block.settings.opaque;
            settings.isAir = block.settings.isAir;
            settings.toolRequired = block.settings.toolRequired;
            settings.offsetType = block.settings.offsetType;
            return settings;
        }

        public Settings noCollision() {
            this.collidable = false;
            this.opaque = false;
            return this;
        }

        public Settings nonOpaque() {
            this.opaque = false;
            return this;
        }

        public Settings slipperiness(float slipperiness) {
            this.slipperiness = slipperiness;
            return this;
        }

        public Settings velocityMultiplier(float velocityMultiplier) {
            this.velocityMultiplier = velocityMultiplier;
            return this;
        }

        public Settings jumpVelocityMultiplier(float jumpVelocityMultiplier) {
            this.jumpVelocityMultiplier = jumpVelocityMultiplier;
            return this;
        }

        public Settings sounds(BlockSoundGroup soundGroup) {
            this.soundGroup = soundGroup;
            return this;
        }

        public Settings luminance(ToIntFunction<BlockState> luminance) {
            this.luminance = luminance;
            return this;
        }

        public Settings strength(float hardness, float resistance) {
            return this.hardness(hardness).resistance(resistance);
        }

        public Settings breakInstantly() {
            return this.strength(0.0f);
        }

        public Settings strength(float strength) {
            this.strength(strength, strength);
            return this;
        }

        public Settings ticksRandomly() {
            this.randomTicks = true;
            return this;
        }

        public Settings dynamicBounds() {
            this.dynamicBounds = true;
            return this;
        }

        public Settings dropsNothing() {
            this.lootTableId = LootTables.EMPTY;
            return this;
        }

        public Settings dropsLike(Block source) {
            this.lootTableId = source.getLootTableId();
            return this;
        }

        public Settings air() {
            this.isAir = true;
            return this;
        }

        public Settings allowsSpawning(TypedContextPredicate<EntityType<?>> predicate) {
            this.allowsSpawningPredicate = predicate;
            return this;
        }

        public Settings solidBlock(ContextPredicate predicate) {
            this.solidBlockPredicate = predicate;
            return this;
        }

        public Settings suffocates(ContextPredicate predicate) {
            this.suffocationPredicate = predicate;
            return this;
        }

        public Settings blockVision(ContextPredicate predicate) {
            this.blockVisionPredicate = predicate;
            return this;
        }

        public Settings postProcess(ContextPredicate predicate) {
            this.postProcessPredicate = predicate;
            return this;
        }

        public Settings emissiveLighting(ContextPredicate predicate) {
            this.emissiveLightingPredicate = predicate;
            return this;
        }

        public Settings requiresTool() {
            this.toolRequired = true;
            return this;
        }

        public Settings mapColor(MapColor color) {
            this.mapColorProvider = state -> color;
            return this;
        }

        public Settings hardness(float hardness) {
            this.hardness = hardness;
            return this;
        }

        public Settings resistance(float resistance) {
            this.resistance = Math.max(0.0f, resistance);
            return this;
        }

        public Settings offsetType(OffsetType offsetType) {
            return this.offsetType((BlockState state) -> offsetType);
        }

        public Settings offsetType(Function<BlockState, OffsetType> offsetType) {
            this.offsetType = offsetType;
            return this;
        }
    }

    public static interface TypedContextPredicate<A> {
        public boolean test(BlockState var1, BlockView var2, BlockPos var3, A var4);
    }

    public static interface ContextPredicate {
        public boolean test(BlockState var1, BlockView var2, BlockPos var3);
    }

    public static abstract class AbstractBlockState
    extends State<Block, BlockState> {
        private final int luminance;
        private final boolean hasSidedTransparency;
        private final boolean isAir;
        private final Material material;
        private final MapColor mapColor;
        private final float hardness;
        private final boolean toolRequired;
        private final boolean opaque;
        private final ContextPredicate solidBlockPredicate;
        private final ContextPredicate suffocationPredicate;
        private final ContextPredicate blockVisionPredicate;
        private final ContextPredicate postProcessPredicate;
        private final ContextPredicate emissiveLightingPredicate;
        private final OffsetType offsetType;
        @Nullable
        protected ShapeCache shapeCache;

        protected AbstractBlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> codec) {
            super(block, propertyMap, codec);
            Settings settings = block.settings;
            this.luminance = settings.luminance.applyAsInt(this.asBlockState());
            this.hasSidedTransparency = block.hasSidedTransparency(this.asBlockState());
            this.isAir = settings.isAir;
            this.material = settings.material;
            this.mapColor = settings.mapColorProvider.apply(this.asBlockState());
            this.hardness = settings.hardness;
            this.toolRequired = settings.toolRequired;
            this.opaque = settings.opaque;
            this.solidBlockPredicate = settings.solidBlockPredicate;
            this.suffocationPredicate = settings.suffocationPredicate;
            this.blockVisionPredicate = settings.blockVisionPredicate;
            this.postProcessPredicate = settings.postProcessPredicate;
            this.emissiveLightingPredicate = settings.emissiveLightingPredicate;
            this.offsetType = settings.offsetType.apply(this.asBlockState());
        }

        public void initShapeCache() {
            if (!this.getBlock().hasDynamicBounds()) {
                this.shapeCache = new ShapeCache(this.asBlockState());
            }
        }

        public Block getBlock() {
            return (Block)this.owner;
        }

        public RegistryEntry<Block> getRegistryEntry() {
            return ((Block)this.owner).getRegistryEntry();
        }

        public Material getMaterial() {
            return this.material;
        }

        public boolean allowsSpawning(BlockView world, BlockPos pos, EntityType<?> type) {
            return this.getBlock().settings.allowsSpawningPredicate.test(this.asBlockState(), world, pos, type);
        }

        public boolean isTranslucent(BlockView world, BlockPos pos) {
            if (this.shapeCache != null) {
                return this.shapeCache.translucent;
            }
            return this.getBlock().isTranslucent(this.asBlockState(), world, pos);
        }

        public int getOpacity(BlockView world, BlockPos pos) {
            if (this.shapeCache != null) {
                return this.shapeCache.lightSubtracted;
            }
            return this.getBlock().getOpacity(this.asBlockState(), world, pos);
        }

        public VoxelShape getCullingFace(BlockView world, BlockPos pos, Direction direction) {
            if (this.shapeCache != null && this.shapeCache.extrudedFaces != null) {
                return this.shapeCache.extrudedFaces[direction.ordinal()];
            }
            return VoxelShapes.extrudeFace(this.getCullingShape(world, pos), direction);
        }

        public VoxelShape getCullingShape(BlockView world, BlockPos pos) {
            return this.getBlock().getCullingShape(this.asBlockState(), world, pos);
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
            return this.isAir;
        }

        public MapColor getMapColor(BlockView world, BlockPos pos) {
            return this.mapColor;
        }

        public BlockState rotate(BlockRotation rotation) {
            return this.getBlock().rotate(this.asBlockState(), rotation);
        }

        public BlockState mirror(BlockMirror mirror) {
            return this.getBlock().mirror(this.asBlockState(), mirror);
        }

        public BlockRenderType getRenderType() {
            return this.getBlock().getRenderType(this.asBlockState());
        }

        public boolean hasEmissiveLighting(BlockView world, BlockPos pos) {
            return this.emissiveLightingPredicate.test(this.asBlockState(), world, pos);
        }

        public float getAmbientOcclusionLightLevel(BlockView world, BlockPos pos) {
            return this.getBlock().getAmbientOcclusionLightLevel(this.asBlockState(), world, pos);
        }

        public boolean isSolidBlock(BlockView world, BlockPos pos) {
            return this.solidBlockPredicate.test(this.asBlockState(), world, pos);
        }

        public boolean emitsRedstonePower() {
            return this.getBlock().emitsRedstonePower(this.asBlockState());
        }

        public int getWeakRedstonePower(BlockView world, BlockPos pos, Direction direction) {
            return this.getBlock().getWeakRedstonePower(this.asBlockState(), world, pos, direction);
        }

        public boolean hasComparatorOutput() {
            return this.getBlock().hasComparatorOutput(this.asBlockState());
        }

        public int getComparatorOutput(World world, BlockPos pos) {
            return this.getBlock().getComparatorOutput(this.asBlockState(), world, pos);
        }

        public float getHardness(BlockView world, BlockPos pos) {
            return this.hardness;
        }

        public float calcBlockBreakingDelta(PlayerEntity player, BlockView world, BlockPos pos) {
            return this.getBlock().calcBlockBreakingDelta(this.asBlockState(), player, world, pos);
        }

        public int getStrongRedstonePower(BlockView world, BlockPos pos, Direction direction) {
            return this.getBlock().getStrongRedstonePower(this.asBlockState(), world, pos, direction);
        }

        public PistonBehavior getPistonBehavior() {
            return this.getBlock().getPistonBehavior(this.asBlockState());
        }

        public boolean isOpaqueFullCube(BlockView world, BlockPos pos) {
            if (this.shapeCache != null) {
                return this.shapeCache.fullOpaque;
            }
            BlockState blockState = this.asBlockState();
            if (blockState.isOpaque()) {
                return Block.isShapeFullCube(blockState.getCullingShape(world, pos));
            }
            return false;
        }

        public boolean isOpaque() {
            return this.opaque;
        }

        public boolean isSideInvisible(BlockState state, Direction direction) {
            return this.getBlock().isSideInvisible(this.asBlockState(), state, direction);
        }

        public VoxelShape getOutlineShape(BlockView world, BlockPos pos) {
            return this.getOutlineShape(world, pos, ShapeContext.absent());
        }

        public VoxelShape getOutlineShape(BlockView world, BlockPos pos, ShapeContext context) {
            return this.getBlock().getOutlineShape(this.asBlockState(), world, pos, context);
        }

        public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
            if (this.shapeCache != null) {
                return this.shapeCache.collisionShape;
            }
            return this.getCollisionShape(world, pos, ShapeContext.absent());
        }

        public VoxelShape getCollisionShape(BlockView world, BlockPos pos, ShapeContext context) {
            return this.getBlock().getCollisionShape(this.asBlockState(), world, pos, context);
        }

        public VoxelShape getSidesShape(BlockView world, BlockPos pos) {
            return this.getBlock().getSidesShape(this.asBlockState(), world, pos);
        }

        public VoxelShape getCameraCollisionShape(BlockView world, BlockPos pos, ShapeContext context) {
            return this.getBlock().getCameraCollisionShape(this.asBlockState(), world, pos, context);
        }

        public VoxelShape getRaycastShape(BlockView world, BlockPos pos) {
            return this.getBlock().getRaycastShape(this.asBlockState(), world, pos);
        }

        public final boolean hasSolidTopSurface(BlockView world, BlockPos pos, Entity entity) {
            return this.isSolidSurface(world, pos, entity, Direction.UP);
        }

        public final boolean isSolidSurface(BlockView world, BlockPos pos, Entity entity, Direction direction) {
            return Block.isFaceFullSquare(this.getCollisionShape(world, pos, ShapeContext.of(entity)), direction);
        }

        public Vec3d getModelOffset(BlockView world, BlockPos pos) {
            if (this.offsetType == OffsetType.NONE) {
                return Vec3d.ZERO;
            }
            Block block = this.getBlock();
            long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
            float f = block.getMaxHorizontalModelOffset();
            double d = MathHelper.clamp(((double)((float)(l & 0xFL) / 15.0f) - 0.5) * 0.5, (double)(-f), (double)f);
            double e = this.offsetType == OffsetType.XYZ ? ((double)((float)(l >> 4 & 0xFL) / 15.0f) - 1.0) * (double)block.getVerticalModelOffsetMultiplier() : 0.0;
            double g = MathHelper.clamp(((double)((float)(l >> 8 & 0xFL) / 15.0f) - 0.5) * 0.5, (double)(-f), (double)f);
            return new Vec3d(d, e, g);
        }

        public boolean onSyncedBlockEvent(World world, BlockPos pos, int type, int data) {
            return this.getBlock().onSyncedBlockEvent(this.asBlockState(), world, pos, type, data);
        }

        @Deprecated
        public void neighborUpdate(World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
            this.getBlock().neighborUpdate(this.asBlockState(), world, pos, sourceBlock, sourcePos, notify);
        }

        public final void updateNeighbors(WorldAccess world, BlockPos pos, int flags) {
            this.updateNeighbors(world, pos, flags, 512);
        }

        public final void updateNeighbors(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
            this.getBlock();
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (Direction direction : DIRECTIONS) {
                mutable.set((Vec3i)pos, direction);
                world.replaceWithStateForNeighborUpdate(direction.getOpposite(), this.asBlockState(), mutable, pos, flags, maxUpdateDepth);
            }
        }

        public final void prepare(WorldAccess world, BlockPos pos, int flags) {
            this.prepare(world, pos, flags, 512);
        }

        public void prepare(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
            this.getBlock().prepare(this.asBlockState(), world, pos, flags, maxUpdateDepth);
        }

        public void onBlockAdded(World world, BlockPos pos, BlockState state, boolean notify) {
            this.getBlock().onBlockAdded(this.asBlockState(), world, pos, state, notify);
        }

        public void onStateReplaced(World world, BlockPos pos, BlockState state, boolean moved) {
            this.getBlock().onStateReplaced(this.asBlockState(), world, pos, state, moved);
        }

        public void scheduledTick(ServerWorld world, BlockPos pos, AbstractRandom random) {
            this.getBlock().scheduledTick(this.asBlockState(), world, pos, random);
        }

        public void randomTick(ServerWorld world, BlockPos pos, AbstractRandom random) {
            this.getBlock().randomTick(this.asBlockState(), world, pos, random);
        }

        public void onEntityCollision(World world, BlockPos pos, Entity entity) {
            this.getBlock().onEntityCollision(this.asBlockState(), world, pos, entity);
        }

        public void onStacksDropped(ServerWorld world, BlockPos pos, ItemStack stack, boolean dropExperience) {
            this.getBlock().onStacksDropped(this.asBlockState(), world, pos, stack, dropExperience);
        }

        public List<ItemStack> getDroppedStacks(LootContext.Builder builder) {
            return this.getBlock().getDroppedStacks(this.asBlockState(), builder);
        }

        public ActionResult onUse(World world, PlayerEntity player, Hand hand, BlockHitResult hit) {
            return this.getBlock().onUse(this.asBlockState(), world, hit.getBlockPos(), player, hand, hit);
        }

        public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
            this.getBlock().onBlockBreakStart(this.asBlockState(), world, pos, player);
        }

        public boolean shouldSuffocate(BlockView world, BlockPos pos) {
            return this.suffocationPredicate.test(this.asBlockState(), world, pos);
        }

        public boolean shouldBlockVision(BlockView world, BlockPos pos) {
            return this.blockVisionPredicate.test(this.asBlockState(), world, pos);
        }

        public BlockState getStateForNeighborUpdate(Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
            return this.getBlock().getStateForNeighborUpdate(this.asBlockState(), direction, neighborState, world, pos, neighborPos);
        }

        public boolean canPathfindThrough(BlockView world, BlockPos pos, NavigationType type) {
            return this.getBlock().canPathfindThrough(this.asBlockState(), world, pos, type);
        }

        public boolean canReplace(ItemPlacementContext context) {
            return this.getBlock().canReplace(this.asBlockState(), context);
        }

        public boolean canBucketPlace(Fluid fluid) {
            return this.getBlock().canBucketPlace(this.asBlockState(), fluid);
        }

        public boolean canPlaceAt(WorldView world, BlockPos pos) {
            return this.getBlock().canPlaceAt(this.asBlockState(), world, pos);
        }

        public boolean shouldPostProcess(BlockView world, BlockPos pos) {
            return this.postProcessPredicate.test(this.asBlockState(), world, pos);
        }

        @Nullable
        public NamedScreenHandlerFactory createScreenHandlerFactory(World world, BlockPos pos) {
            return this.getBlock().createScreenHandlerFactory(this.asBlockState(), world, pos);
        }

        public boolean isIn(TagKey<Block> tag) {
            return this.getBlock().getRegistryEntry().isIn(tag);
        }

        public boolean isIn(TagKey<Block> tag, Predicate<AbstractBlockState> predicate) {
            return this.isIn(tag) && predicate.test(this);
        }

        public boolean isIn(RegistryEntryList<Block> blocks) {
            return blocks.contains(this.getBlock().getRegistryEntry());
        }

        public Stream<TagKey<Block>> streamTags() {
            return this.getBlock().getRegistryEntry().streamTags();
        }

        public boolean hasBlockEntity() {
            return this.getBlock() instanceof BlockEntityProvider;
        }

        @Nullable
        public <T extends BlockEntity> BlockEntityTicker<T> getBlockEntityTicker(World world, BlockEntityType<T> blockEntityType) {
            if (this.getBlock() instanceof BlockEntityProvider) {
                return ((BlockEntityProvider)((Object)this.getBlock())).getTicker(world, this.asBlockState(), blockEntityType);
            }
            return null;
        }

        public boolean isOf(Block block) {
            return this.getBlock() == block;
        }

        public FluidState getFluidState() {
            return this.getBlock().getFluidState(this.asBlockState());
        }

        public boolean hasRandomTicks() {
            return this.getBlock().hasRandomTicks(this.asBlockState());
        }

        public long getRenderingSeed(BlockPos pos) {
            return this.getBlock().getRenderingSeed(this.asBlockState(), pos);
        }

        public BlockSoundGroup getSoundGroup() {
            return this.getBlock().getSoundGroup(this.asBlockState());
        }

        public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
            this.getBlock().onProjectileHit(world, state, hit, projectile);
        }

        public boolean isSideSolidFullSquare(BlockView world, BlockPos pos, Direction direction) {
            return this.isSideSolid(world, pos, direction, SideShapeType.FULL);
        }

        public boolean isSideSolid(BlockView world, BlockPos pos, Direction direction, SideShapeType shapeType) {
            if (this.shapeCache != null) {
                return this.shapeCache.isSideSolid(direction, shapeType);
            }
            return shapeType.matches(this.asBlockState(), world, pos, direction);
        }

        public boolean isFullCube(BlockView world, BlockPos pos) {
            if (this.shapeCache != null) {
                return this.shapeCache.isFullCube;
            }
            return this.getBlock().isShapeFullCube(this.asBlockState(), world, pos);
        }

        protected abstract BlockState asBlockState();

        public boolean isToolRequired() {
            return this.toolRequired;
        }

        public OffsetType getOffsetType() {
            return this.offsetType;
        }

        static final class ShapeCache {
            private static final Direction[] DIRECTIONS = Direction.values();
            private static final int SHAPE_TYPE_LENGTH = SideShapeType.values().length;
            protected final boolean fullOpaque;
            final boolean translucent;
            final int lightSubtracted;
            @Nullable
            final VoxelShape[] extrudedFaces;
            protected final VoxelShape collisionShape;
            protected final boolean exceedsCube;
            private final boolean[] solidSides;
            protected final boolean isFullCube;

            ShapeCache(BlockState state) {
                Block block = state.getBlock();
                this.fullOpaque = state.isOpaqueFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                this.translucent = block.isTranslucent(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                this.lightSubtracted = block.getOpacity(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                if (!state.isOpaque()) {
                    this.extrudedFaces = null;
                } else {
                    this.extrudedFaces = new VoxelShape[DIRECTIONS.length];
                    VoxelShape voxelShape = block.getCullingShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                    Direction[] directionArray = DIRECTIONS;
                    int n = directionArray.length;
                    for (int i = 0; i < n; ++i) {
                        Direction direction = directionArray[i];
                        this.extrudedFaces[direction.ordinal()] = VoxelShapes.extrudeFace(voxelShape, direction);
                    }
                }
                this.collisionShape = block.getCollisionShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, ShapeContext.absent());
                if (!this.collisionShape.isEmpty() && state.getOffsetType() != OffsetType.NONE) {
                    throw new IllegalStateException(String.format("%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.", Registry.BLOCK.getId(block)));
                }
                this.exceedsCube = Arrays.stream(Direction.Axis.values()).anyMatch(axis -> this.collisionShape.getMin((Direction.Axis)axis) < 0.0 || this.collisionShape.getMax((Direction.Axis)axis) > 1.0);
                this.solidSides = new boolean[DIRECTIONS.length * SHAPE_TYPE_LENGTH];
                for (Direction direction2 : DIRECTIONS) {
                    for (SideShapeType sideShapeType : SideShapeType.values()) {
                        this.solidSides[ShapeCache.indexSolidSide((Direction)direction2, (SideShapeType)sideShapeType)] = sideShapeType.matches(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction2);
                    }
                }
                this.isFullCube = Block.isShapeFullCube(state.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
            }

            public boolean isSideSolid(Direction direction, SideShapeType shapeType) {
                return this.solidSides[ShapeCache.indexSolidSide(direction, shapeType)];
            }

            private static int indexSolidSide(Direction direction, SideShapeType shapeType) {
                return direction.ordinal() * SHAPE_TYPE_LENGTH + shapeType.ordinal();
            }
        }
    }

    public static enum OffsetType {
        NONE,
        XZ,
        XYZ;

    }
}

