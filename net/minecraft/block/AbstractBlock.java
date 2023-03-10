/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
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
import net.minecraft.util.math.random.Random;
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
 * <strong>This class should not be extended directly. Extend {@link Block} instead.</strong>
 * Custom block behaviors are specified either through {@linkplain AbstractBlock.Settings
 * block settings} or by overriding methods in this class.
 * 
 * <p>Methods in this class may be executed during world generation if they take
 * {@link WorldAccess} as a parameter. In this case, a {@link net.minecraft.world.ChunkRegion}
 * is passed to the parameter, which is not a subclass of {@link World}.
 * 
 * <p id="deprecated-methods">Deprecated methods in this class mean they
 * should only be called from the corresponding method in {@link
 * AbstractBlockState} or subclasses of this class. <strong>Overriding the
 * methods is an expected usage and is not deprecated in any way.</strong>
 * 
 * @apiNote In vanilla subclasses, these methods are called either to do the
 * default behavior (e.g. {@code super.onUse(...)}) or to delegate logic to
 * other blocks (e.g. {@link net.minecraft.block.StairsBlock#randomTick
 * StairsBlock#randomTick} calls {@code randomTick} of its base block).
 * 
 * <p>Many methods of this class are called on both the logical client and logical server,
 * so take caution when using those methods. The logical side can be checked using
 * {@link World#isClient}.
 * 
 * <h2 id=quick-view>Quick view</h2>
 * <p><strong>Notes</strong>: "Tall or wide block" refers to a block that
 * has multiple parts, such as doors, sunflowers, or beds. "Neighboring
 * block" refers to blocks adjacent to a block on all 6 sides (but not
 * diagonally.)
 * 
 * <h3 id=placement>Placement related methods</h3>
 * <table>
 * <caption>Block placement related methods (sorted by execution order)</caption>
 * <thead>
 *     <tr>
 *         <th>Method</th>
 *         <th>Purpose</th>
 *         <th>Player/dispenser</th>
 *         <th>Falling block</th>
 *         <th>{@link World#setBlockState(BlockPos, BlockState) setBlockState} call</th>
 *     </tr>
 * </thead>
 * <tbody>
 *     <tr>
 *         <td>oldState.{@link #canReplace canReplace}</td>
 *         <td>Checking if the current block can be replaced</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>No</td>
 *     </tr>
 *     <tr>
 *         <td>newBlock.{@link Block#getPlacementState getPlacementState}</td>
 *         <td>Getting the placed state</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>No</td>
 *     </tr>
 *     <tr>
 *         <td>newState.{@link #canPlaceAt canPlaceAt}</td>
 *         <td>Checking the block's placement restriction</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>No</td>
 *     </tr>
 *     <tr>
 *         <td>oldState.{@link #onStateReplaced onStateReplaced}</td>
 *         <td>Dropping inventory, updating redstone circuit, etc</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *     </tr>
 *     <tr>
 *         <td>newState.{@link #onBlockAdded onBlockAdded}</td>
 *         <td>Activating redstone component, etc</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *     </tr>
 *     <tr>
 *         <td>neighborState.{@link #neighborUpdate neighborUpdate}</td>
 *         <td>Activating neighboring redstone component, etc</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *     </tr>
 *     <tr>
 *         <td>oldState.{@link #prepare prepare}</td>
 *         <td>Updating redstone wire connection</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *     </tr>
 *     <tr>
 *         <td>neighborState.{@link #getStateForNeighborUpdate getStateForNeighborUpdate}</td>
 *         <td>Checking the neighboring block's placement restriction, updating connection, etc</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *     </tr>
 *     <tr>
 *         <td>newState.{@link #prepare prepare}</td>
 *         <td>Updating redstone wire connection</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *     </tr>
 *     <tr>
 *         <td>newBlock.{@link Block#onPlaced onPlaced}</td>
 *         <td>Placing the other half of tall or wide block, setting block entity's custom name, etc</td>
 *         <td>Yes</td>
 *         <td>No</td>
 *         <td>No</td>
 *     </tr>
 * </tbody>
 * </table>
 * 
 * <h3 id=breaking>Breaking related methods</h3>
 * <table>
 * <caption>Block breaking related methods (sorted by execution order)</caption>
 * <thead>
 *     <tr>
 *         <th>Method</th>
 *         <th>Purpose</th>
 *         <th>Player mining</th>
 *         <th>Explosion</th>
 *         <th>{@link World#setBlockState(BlockPos, BlockState) setBlockState} / {@link net.minecraft.world.ModifiableWorld#removeBlock(BlockPos, boolean) removeBlock} call</th>
 *         <th>{@link net.minecraft.world.ModifiableWorld#breakBlock(BlockPos, boolean) breakBlock} call</th>
 *     </tr>
 * </thead>
 * <tbody>
 *     <tr>
 *         <td>state.{@link #onBlockBreakStart onBlockBreakStart}</td>
 *         <td>Doing something when player starts breaking a block</td>
 *         <td>Yes</td>
 *         <td>No</td>
 *         <td>No</td>
 *         <td>No</td>
 *     </tr>
 *     <tr>
 *         <td>state.{@link #calcBlockBreakingDelta calcBlockBreakingDelta}</td>
 *         <td>Calculating the player's mining speed</td>
 *         <td>Yes</td>
 *         <td>No</td>
 *         <td>No</td>
 *         <td>No</td>
 *     </tr>
 *     <tr>
 *         <td>block.{@link Block#onBreak onBreak}</td>
 *         <td>Spawning particles, breaking the other half of tall or wide block, etc</td>
 *         <td>Yes</td>
 *         <td>No</td>
 *         <td>No</td>
 *         <td>No</td>
 *     </tr>
 *     <tr>
 *         <td>state.{@link #onStateReplaced onStateReplaced}</td>
 *         <td>Dropping inventory, updating redstone circuit, etc</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *     </tr>
 *     <tr>
 *         <td>neighborState.{@link #neighborUpdate neighborUpdate}</td>
 *         <td>Activating neighboring redstone component, etc</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *     </tr>
 *     <tr>
 *         <td>state.{@link #prepare prepare}</td>
 *         <td>Updating redstone wire connection</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *     </tr>
 *     <tr>
 *         <td>neighborState.{@link #getStateForNeighborUpdate getStateForNeighborUpdate}</td>
 *         <td>Checking the neighboring block's placement restriction, updating connection, etc</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *     </tr>
 *     <tr>
 *         <td>block.{@link Block#onBroken onBroken}</td>
 *         <td>Unused in most cases</td>
 *         <td>Yes</td>
 *         <td>No</td>
 *         <td>No</td>
 *         <td>No</td>
 *     </tr>
 *     <tr>
 *         <td>block.{@link Block#afterBreak afterBreak}</td>
 *         <td>Dropping stacks, replacing the broken block with another block, etc</td>
 *         <td>Yes</td>
 *         <td>No</td>
 *         <td>No</td>
 *         <td>No</td>
 *     </tr>
 *     <tr>
 *         <td>state.{@link #getDroppedStacks getDroppedStacks}</td>
 *         <td>Supplying information to loot context builder</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes</td>
 *         <td>Yes<sup>1</sup></td>
 *     </tr>
 *     <tr>
 *         <td>state.{@link #onStacksDropped onStacksDropped}</td>
 *         <td>Dropping experience orbs</td>
 *         <td>Yes</td>
 *         <td>Yes<sup>2</sup></td>
 *         <td>Yes</td>
 *         <td>Yes<sup>1</sup></td>
 *     </tr>
 * </tbody>
 * </table>
 * 
 * <p>Notes:
 * <ol>
 * <li>Called before {@link #onStateReplaced onStateReplaced} in this case.</li>
 * <li>Called before {@link #getDroppedStacks getDroppedStacks} in this case.</li>
 * </ol>
 */
public abstract class AbstractBlock
implements ToggleableFeature {
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
    protected final FeatureSet requiredFeatures;
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
        this.requiredFeatures = settings.requiredFeatures;
        this.settings = settings;
    }

    /**
     * Called when the block state changes, before the {@linkplain #getStateForNeighborUpdate
     * neighbor-triggered state update} on the original block, and after the
     * neighbor-triggered state update on the replaced block.
     * 
     * <p>This method is called on both the logical client and logical server, so take caution
     * when overriding this method. The logical side can be checked using {@link
     * World#isClient}.
     * 
     * @apiNote This is used by {@link RedstoneWireBlock} to update connected redstone wire.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#prepare(WorldAccess, BlockPos, int, int)} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see #getStateForNeighborUpdate
     * @see #neighborUpdate
     */
    @Deprecated
    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
    }

    /**
     * {@return if an entity using navigation type {@code type} can navigate through this block}
     * 
     * @apiNote Subclasses may override this to prevent or restrict pathfinding through the
     * block. For example, {@link DoorBlock} restricts it to open doors only.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#canPathfindThrough} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
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
     * {@return the state of the block after a neighboring block's state change}
     * 
     * <p>Returning {@link Blocks#AIR} breaks the block. This is useful to implement supporting
     * block requirement for blocks (if used along with {@link #canPlaceAt}).
     * 
     * <p>Side effects like activating a redstone component (but not scheduling a tick)
     * should be performed in {@link #neighborUpdate} instead. If the block supports
     * waterlogging and currently has water, this method should be overridden to tick the
     * fluid at the block's position.
     * 
     * <p>This method is called on both the logical client and logical server, so take caution
     * when overriding this method. The logical side can be checked using {@link
     * World#isClient}. This is not called if {@link Block#FORCE_STATE} flag is set in the {@code
     * setBlockState} call.
     * 
     * <p>This method can be used for multiple purposes. Here are some examples:
     * <ul>
     * <li>{@link FenceBlock} uses it to update the fence's connection when a horizontally
     * neighboring block's state is changed.</li>
     * <li>{@link PlantBlock} uses it to break the plant if the state change causes it to
     * lose its supporting block.</li>
     * <li>{@link DoorBlock} uses it to copy the state of the other half of the door.</li>
     * <li>{@link SlabBlock} uses it to schedule the fluid to tick if waterlogged.</li>
     * <li>{@link SoulSandBlock} uses it to schedule the water block above to tick
     * so that it becomes a bubble column.</li>
     * <li>{@link FallingBlock} uses it to schedule the block to tick so that it can
     * fall if needed.</li>
     * </ul>
     * 
     * @deprecated Consider calling {@link AbstractBlockState#getStateForNeighborUpdate} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see #neighborUpdate
     * @see #prepare
     * @see #canPlaceAt
     * @see Block#FORCE_STATE
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
     * @deprecated Consider calling {@link AbstractBlockState#isSideInvisible} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return false;
    }

    /**
     * Called when a neighboring block is updated. This method should be overridden
     * to perform an action with a side effect, most notably an activation of a redstone
     * component. This can also be used to perform an action changing block states of
     * other blocks, such as {@link SpongeBlock} which absorbs water.
     * 
     * <p>To replace the state of the block itself, override {@link #getStateForNeighborUpdate}
     * instead.
     * 
     * <p>This method is called on both the logical client and logical server, so take caution
     * when overriding this method. The logical side can be checked using {@link
     * World#isClient}.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#neighborUpdate} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see #getStateForNeighborUpdate
     * @see World#isReceivingRedstonePower
     */
    @Deprecated
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        DebugInfoSender.sendNeighborUpdate(world, pos);
    }

    /**
     * Called server-side on the new block when the block state is changed. This includes block
     * placement. When overriding this method, {@link #getStateForNeighborUpdate} or {@link
     * #neighborUpdate} should also be overridden. The method is used in the following cases:
     * 
     * <ul>
     * <li>When activating a redstone component on placement (used along with {@link
     * #neighborUpdate}</li>
     * <li>When resetting a position-dependent state (see {@link TargetBlock})</li>
     * <li>When converting a block on placement (see {@link WetSpongeBlock})</li>
     * <li>When {@linkplain AbstractFireBlock fire} lights a portal</li>
     * </ul>
     * 
     * @deprecated Consider calling {@link AbstractBlockState#onBlockAdded} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see #onStateReplaced
     */
    @Deprecated
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
    }

    /**
     * Called server-side on the old block when the block state is changed. This includes block
     * removal. This is used to update neighboring blocks when an active redstone power source
     * is removed, or to drop the contents of an inventory. The check {@code
     * state.isOf(newState.getBlock())} can be used to see if the block was removed or not.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#onStateReplaced} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see net.minecraft.util.ItemScatterer#spawn(World, BlockPos, net.minecraft.inventory.Inventory)
     * @see #onBlockAdded
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
     * @deprecated Consider calling {@link AbstractBlockState#onUse} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    /**
     * Handles the block event, which is an event specific to a block with an integer ID and data.
     * 
     * @return whether the event was handled successfully
     * 
     * @deprecated Consider calling {@link AbstractBlockState#onSyncedBlockEvent} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see World#addSyncedBlockEvent
     */
    @Deprecated
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        return false;
    }

    /**
     * {@return the block's render type (invisible, animated, model)}
     * 
     * @apiNote {@link BlockWithEntity} overrides this to return {@link BlockRenderType#INVISIBLE};
     * therefore, custom blocks extending that class must override it again to render the block.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#getRenderType} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    /**
     * {@return whether the block's transparency depends on the side of the block, like slabs}
     * 
     * @deprecated Consider calling {@link AbstractBlockState#hasSidedTransparency} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public boolean hasSidedTransparency(BlockState state) {
        return false;
    }

    /**
     * {@return whether the block is capable of emitting redstone power}
     * 
     * <p>This does not return whether the block is currently emitting redstone power.
     * Use {@link World#isEmittingRedstonePower} in that case.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#emitsRedstonePower} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see World#isEmittingRedstonePower
     */
    @Deprecated
    public boolean emitsRedstonePower(BlockState state) {
        return false;
    }

    /**
     * {@return how the piston should handle the block}
     * 
     * <p>Notes on piston behavior:
     * <ul>
     * <li>{@link PistonBehavior#IGNORE} is ignored for blocks.</li>
     * <li>{@link PistonBehavior#DESTROY} and {@link PistonBehavior#PUSH_ONLY} causes pistons to
     * ignore the block entity restriction.</li>
     * <li>If the {@linkplain #getHardness hardness} equals {@code -1.0f} like bedrock, it blocks
     * the piston regardless of the return value.</li>
     * <li>Behavior of several unpushable blocks are hardcoded at {@link PistonBlock#isMovable}
     * instead.</li>
     * </ul>
     * 
     * @deprecated Consider calling {@link AbstractBlockState#getPistonBehavior} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see PistonBlock#isMovable
     */
    @Deprecated
    public PistonBehavior getPistonBehavior(BlockState state) {
        return this.material.getPistonBehavior();
    }

    /**
     * {@return the state's associated fluid state}
     * 
     * <p>{@linkplain Waterloggable Waterloggable blocks} must override this to return {@code Fluids.WATER.getStill(false)}
     * when waterlogged.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#getFluidState} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see net.minecraft.fluid.Fluids#WATER
     */
    @Deprecated
    public FluidState getFluidState(BlockState state) {
        return Fluids.EMPTY.getDefaultState();
    }

    /**
     * {@return whether the block can have a comparator output}
     * 
     * <p>This does not check the current comparator output of the block.
     * Use {@link #getComparatorOutput} in that case.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#hasComparatorOutput} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see #getComparatorOutput
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

    @Override
    public FeatureSet getRequiredFeatures() {
        return this.requiredFeatures;
    }

    /**
     * {@return {@code state} rotated by {@code rotation}}
     * 
     * <p>By default, this returns the provided block state.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#rotate} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state;
    }

    /**
     * {@return {@code state} mirrored by {@code mirror}}
     * 
     * <p>By default, this returns the provided block state.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#mirror} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state;
    }

    /**
     * {@return whether the item can replace the block}
     * 
     * <p>By default, this checks if the block's material allows replacing and whether the
     * item differs from the block's item. Items composed of multiple blocks, such as candles,
     * vines, or snow layers, should override this to implement additional checks.
     * 
     * <p>This method is called on both the logical client and logical server, so take caution
     * when overriding this method. The logical side can be checked using {@link
     * World#isClient}.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#canReplace} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see #canBucketPlace
     * @see Material#isReplaceable
     */
    @Deprecated
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return this.material.isReplaceable() && (context.getStack().isEmpty() || !context.getStack().isOf(this.asItem()));
    }

    /**
     * {@return whether a bucket can replace the block with the fluid}
     * 
     * <p>By default, this checks if the block's material allows replacing or is not solid.
     * Blocks intended to be unbreakable should override this to implement additional checks.
     * 
     * <p>This method is called on both the logical client and logical server, so take caution
     * when overriding this method. The logical side can be checked using {@link
     * World#isClient}.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#canBucketPlace} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see #canReplace
     * @see Material#isReplaceable
     */
    @Deprecated
    public boolean canBucketPlace(BlockState state, Fluid fluid) {
        return this.material.isReplaceable() || !this.material.isSolid();
    }

    /**
     * {@return the block's dropped item stacks}
     * 
     * <p>The default implementation uses loot tables. Blocks with custom drops <strong>should
     * not hardcode the drops</strong>; instead, make a new loot table. If the loot table
     * needs an additional context, override this method and modify {@code builder} before
     * calling {@code super.getDroppedStacks}. An example of this is {@link ShulkerBoxBlock}.
     * Note that to prevent item duplication, when appending item stacks to the builder,
     * {@link ItemStack#split} should be called.
     * 
     * <p>This method should not be used for dropping inventory contents ({@link
     * #onStateReplaced} should be used instead) or to drop experience orbs ({@link
     * #onStacksDropped} should be used instead).
     * 
     * @deprecated Consider calling {@link AbstractBlockState#getDroppedStacks} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see #onStateReplaced
     * @see #onStacksDropped
     * @see ItemStack#split
     * @see net.minecraft.loot.context.LootContextParameters
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
     * {@return the seed value for rendering}
     * 
     * <p>This is usually the hash code of {@code pos}. Tall or wide blocks (such as doors or
     * beds) should override this to make sure both parts of the block have the same seed.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#getRenderingSeed} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos);
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getCullingShape} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return state.getOutlineShape(world, pos);
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getSidesShape} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return this.getCollisionShape(state, world, pos, ShapeContext.absent());
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getRaycastShape} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getOpacity} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        if (state.isOpaqueFullCube(world, pos)) {
            return world.getMaxLightLevel();
        }
        return state.isTransparent(world, pos) ? 0 : 1;
    }

    /**
     * {@return the screen handler factory or {@code null} if screen handler cannot be created}
     * 
     * <p>This method should be overridden for blocks with screen handlers, such as anvils.
     * The created screen handler is usually passed to {@link PlayerEntity#openHandledScreen}.
     * See {@link AnvilBlock#createScreenHandlerFactory} for basic usage. {@link BlockWithEntity}
     * delegates this logic to the block entity implementing {@link
     * net.minecraft.screen.NamedScreenHandlerFactory}. For example, any {@link BlockWithEntity} whose block entity
     * extends {@link net.minecraft.block.entity.LockableContainerBlockEntity} needs to override
     * {@link net.minecraft.block.entity.LockableContainerBlockEntity#createScreenHandler}
     * instead of this method.
     * 
     * <p>This method is called on both the logical client and logical server, so take caution
     * when overriding this method. The logical side can be checked using {@link
     * World#isClient}.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#createScreenHandlerFactory} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see net.minecraft.screen.SimpleNamedScreenHandlerFactory
     * @see net.minecraft.block.entity.LockableContainerBlockEntity
     */
    @Nullable
    @Deprecated
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return null;
    }

    /**
     * {@return whether the block can be placed at {@code pos}}
     * 
     * <p>Blocks with supporting block requirements should override this method. Note that
     * this should also be checked manually during {@link #getStateForNeighborUpdate}
     * in order to break the block that lost its supporting block.
     * 
     * <p>This is only checked during {@linkplain net.minecraft.item.BlockItem#canPlace the
     * use of block items} or by endermen, falling blocks, etc that can place blocks. This
     * does not affect block state changes performed through {@link
     * World#setBlockState(BlockPos, BlockState)} call.
     * 
     * <p>This method is called on both the logical client and logical server, so take caution
     * when overriding this method. The logical side can be checked using {@link
     * World#isClient}.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#canPlaceAt} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see #getStateForNeighborUpdate
     */
    @Deprecated
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getAmbientOcclusionLightLevel} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return state.isFullCube(world, pos) ? 0.2f : 1.0f;
    }

    /**
     * {@return the comparator output of the block, from {@code 0} to {@code 15}}
     * 
     * <p>When overriding this, {@link #hasComparatorOutput} must also be overridden.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#getComparatorOutput} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see #hasComparatorOutput
     */
    @Deprecated
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 0;
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getOutlineShape(BlockView, BlockPos, ShapeContext)} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#getCollisionShape(BlockView, BlockPos, ShapeContext)} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.collidable ? state.getOutlineShape(world, pos) : VoxelShapes.empty();
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#isFullCube} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
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
     * @deprecated Consider calling {@link AbstractBlockState#getCameraCollisionShape} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getCollisionShape(state, world, pos, context);
    }

    /**
     * Called server-side when a block gets ticked randomly. This can be overridden to implement
     * various logics, most commonly plant growth. Default implementation calls
     * {@link #scheduledTick}. To control the rate of the action, use {@code random}.
     * 
     * <p>Random tick speed is controlled by the game rule {@link
     * net.minecraft.world.GameRules#RANDOM_TICK_SPEED randomTickSpeed} and can be disabled.
     * Only blocks within 128-block cylinder (i.e. ignoring Y coordinates) around players
     * receive random ticks.
     * 
     * <p>Blocks overriding this must use {@link AbstractBlock.Settings#ticksRandomly}
     * block settings.
     * 
     * <p>Here are some examples:
     * <ul>
     * <li>{@link SugarCaneBlock} uses this to grow sugar cane.</li>
     * <li>{@link OxidizableBlock} uses this to oxidize.</li>
     * <li>{@link NetherPortalBlock} uses this to spawn zombified piglins.</li>
     * <li>{@link LeavesBlock} uses this to decay when far from logs.</li>
     * </ul>
     * 
     * @deprecated Consider calling {@link AbstractBlockState#randomTick} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see CropBlock
     * @see #scheduledTick
     */
    @Deprecated
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.scheduledTick(state, world, pos, random);
    }

    /**
     * Called server-side when a block receives a scheduled tick. This can be used like a timer.
     * Scheduled ticks are added using {@link
     * WorldAccess#scheduleBlockTick(BlockPos, Block, int)}. Additionally, {@link
     * #randomTick} by default calls this method; override {@link #randomTick} to disable this
     * behavior.
     * 
     * <p>Scheduled ticks are often used inside {@link #getStateForNeighborUpdate}.
     * 
     * <p>Here are some examples:
     * <ul>
     * <li>{@link SugarCaneBlock} checks the placement requirement.</li>
     * <li>{@link DispenserBlock} dispenses its content.</li>
     * <li>{@link CommandBlock} executes its command.</li>
     * <li>{@link FrogspawnBlock} spawns a tadpole.</li>
     * <li>{@link SoulSandBlock} updates a bubble column.</li>
     * <li>{@link FallingBlock} tries to fall.</li>
     * </ul>
     * 
     * @deprecated Consider calling {@link AbstractBlockState#scheduledTick} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see WorldAccess#scheduleBlockTick(BlockPos, Block, int)
     * @see #getStateForNeighborUpdate
     * @see #randomTick
     */
    @Deprecated
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

    /**
     * @deprecated Consider calling {@link AbstractBlockState#calcBlockBreakingDelta} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
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
     * Called server-side when the stacks are dropped by mining or explosion. This is mostly
     * overridden to drop experience orbs. To change the dropped item stacks, use loot tables
     * or {@link #getDroppedStacks}. To drop inventory contents, use {@link #onStateReplaced}
     * instead.
     * 
     * <p>Experience orbs should only be dropped if {@code dropExperience} is {@code true}.
     * {@link Block#dropExperienceWhenMined} can be used to drop experience orbs.
     * {@link ExperienceDroppingBlock} provides the implementation for experience-dropping blocks.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#onStacksDropped} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see ExperienceDroppingBlock
     * @see Block#dropExperienceWhenMined
     * @see #getDroppedStacks
     * @see #onStateReplaced
     */
    @Deprecated
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
    }

    /**
     * Called when a player starts breaking the block (including when instant-mining).
     * 
     * <p>This method is called on both the logical client and logical server, so take caution
     * when overriding this method. The logical side can be checked using {@link
     * World#isClient}.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#onBlockBreakStart} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     */
    @Deprecated
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
    }

    /**
     * {@return the weak redstone power emitted from the block}
     * 
     * <p>When overriding this, make sure to also override {@link #emitsRedstonePower} to
     * return {@code true}.
     * 
     * <p>Weak redstone power is a power that cannot power a redstone wire when a solid block
     * is in between. For example, {@link RedstoneBlock} and {@link TargetBlock} emits weak
     * redstone power only. {@link LeverBlock} and {@link ButtonBlock} emits both
     * weak and strong redstone power depending on the direction.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#getWeakRedstonePower} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see #emitsRedstonePower
     * @see #getStrongRedstonePower
     * @see World#isReceivingRedstonePower
     */
    @Deprecated
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    /**
     * Called when the entity's collision box intersects the block. Therefore,
     * this method is not called for blocks with a collision; use {@link Block#onSteppedOn}
     * for those blocks.
     * 
     * <p>This method is called on both the logical client and logical server, so take caution
     * when overriding this method. The logical side can be checked using {@link
     * World#isClient}.
     * 
     * <p>Here are some examples:
     * <ul>
     * <li>{@link CactusBlock} damages the entity.</li>
     * <li>{@link AbstractPressurePlateBlock} triggers.</li>
     * <li>{@link CobwebBlock} slows the entity.</li>
     * <li>{@link EndPortalBlock} teleports the entity.</li>
     * <li>{@link HopperBlock} collects the item entity.</li>
     * </ul>
     * 
     * @deprecated Consider calling {@link AbstractBlockState#onEntityCollision} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see Block#onSteppedOn
     * @see #onProjectileHit
     */
    @Deprecated
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    }

    /**
     * {@return the strong redstone power emitted from the block}
     * 
     * <p>When overriding this, make sure to also override {@link #emitsRedstonePower} to
     * return {@code true}. {@link #getWeakRedstonePower} might also need to be overridden.
     * 
     * <p>Strong redstone power is a power that can power a redstone wire when a solid block
     * is in between. For example, {@link RedstoneBlock} and {@link TargetBlock} emits weak
     * redstone power only. {@link LeverBlock} and {@link ButtonBlock} emits both
     * weak and strong redstone power.
     * 
     * @deprecated Consider calling {@link AbstractBlockState#getStrongRedstonePower} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see #emitsRedstonePower
     * @see #getWeakRedstonePower
     * @see World#isReceivingRedstonePower
     */
    @Deprecated
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    public final Identifier getLootTableId() {
        if (this.lootTableId == null) {
            Identifier identifier = Registries.BLOCK.getId(this.asBlock());
            this.lootTableId = identifier.withPrefixedPath("blocks/");
        }
        return this.lootTableId;
    }

    /**
     * Called when a {@link ProjectileEntity} hits a block.
     * 
     * <p>This method is called on both the logical client and logical server, so take caution
     * when overriding this method. The logical side can be checked using {@link
     * World#isClient}.
     * 
     * <p>Here are some examples:
     * <ul>
     * <li>{@link TargetBlock} activates.</li>
     * <li>{@link BellBlock} rings.</li>
     * <li>{@link LightningRodBlock} spawns a lightning.</li>
     * <li>{@link AbstractCandleBlock} lights on fire when hit by a projectile on fire.</li>
     * </ul>
     * 
     * @deprecated Consider calling {@link AbstractBlockState#onProjectileHit} instead. See <a href="#deprecated-methods">why these methods are deprecated</a>.
     * 
     * @see ProjectileEntity#onBlockHit
     * @see #onEntityCollision
     */
    @Deprecated
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
    }

    /**
     * {@return the block's corresponding item}
     * 
     * <p>This is not affected by loot tables. Blocks without corresponding items,
     * such as piston head, will return {@link net.minecraft.item.Items#AIR}.
     * 
     * @see net.minecraft.item.BlockItem
     */
    public abstract Item asItem();

    /**
     * {@return the block as {@link Block}}
     * 
     * <p>This is used for casting purposes.
     */
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
        boolean blockBreakParticles = true;
        TypedContextPredicate<EntityType<?>> allowsSpawningPredicate = (state, world, pos, type) -> state.isSideSolidFullSquare(world, pos, Direction.UP) && state.getLuminance() < 14;
        ContextPredicate solidBlockPredicate = (state, world, pos) -> state.getMaterial().blocksLight() && state.isFullCube(world, pos);
        ContextPredicate suffocationPredicate;
        ContextPredicate blockVisionPredicate = this.suffocationPredicate = (state, world, pos) -> this.material.blocksMovement() && state.isFullCube(world, pos);
        ContextPredicate postProcessPredicate = (state, world, pos) -> false;
        ContextPredicate emissiveLightingPredicate = (state, world, pos) -> false;
        boolean dynamicBounds;
        FeatureSet requiredFeatures = FeatureFlags.VANILLA_FEATURES;
        Optional<Offsetter> offsetter = Optional.empty();

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
            settings.offsetter = block.settings.offsetter;
            settings.blockBreakParticles = block.settings.blockBreakParticles;
            settings.requiredFeatures = block.settings.requiredFeatures;
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

        public Settings offset(OffsetType offsetType) {
            switch (offsetType) {
                default: {
                    this.offsetter = Optional.empty();
                    break;
                }
                case XYZ: {
                    this.offsetter = Optional.of((state, world, pos) -> {
                        Block block = state.getBlock();
                        long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
                        double d = ((double)((float)(l >> 4 & 0xFL) / 15.0f) - 1.0) * (double)block.getVerticalModelOffsetMultiplier();
                        float f = block.getMaxHorizontalModelOffset();
                        double e = MathHelper.clamp(((double)((float)(l & 0xFL) / 15.0f) - 0.5) * 0.5, (double)(-f), (double)f);
                        double g = MathHelper.clamp(((double)((float)(l >> 8 & 0xFL) / 15.0f) - 0.5) * 0.5, (double)(-f), (double)f);
                        return new Vec3d(e, d, g);
                    });
                    break;
                }
                case XZ: {
                    this.offsetter = Optional.of((state, world, pos) -> {
                        Block block = state.getBlock();
                        long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
                        float f = block.getMaxHorizontalModelOffset();
                        double d = MathHelper.clamp(((double)((float)(l & 0xFL) / 15.0f) - 0.5) * 0.5, (double)(-f), (double)f);
                        double e = MathHelper.clamp(((double)((float)(l >> 8 & 0xFL) / 15.0f) - 0.5) * 0.5, (double)(-f), (double)f);
                        return new Vec3d(d, 0.0, e);
                    });
                }
            }
            return this;
        }

        public Settings noBlockBreakParticles() {
            this.blockBreakParticles = false;
            return this;
        }

        public Settings requires(FeatureFlag ... features) {
            this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(features);
            return this;
        }
    }

    public static interface TypedContextPredicate<A> {
        public boolean test(BlockState var1, BlockView var2, BlockPos var3, A var4);
    }

    public static interface Offsetter {
        public Vec3d evaluate(BlockState var1, BlockView var2, BlockPos var3);
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
        private final Optional<Offsetter> offsetter;
        private final boolean blockBreakParticles;
        @Nullable
        protected ShapeCache shapeCache;
        private FluidState fluidState = Fluids.EMPTY.getDefaultState();
        private boolean ticksRandomly;

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
            this.offsetter = settings.offsetter;
            this.blockBreakParticles = settings.blockBreakParticles;
        }

        public void initShapeCache() {
            this.fluidState = ((Block)this.owner).getFluidState(this.asBlockState());
            this.ticksRandomly = ((Block)this.owner).hasRandomTicks(this.asBlockState());
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

        public boolean isTransparent(BlockView world, BlockPos pos) {
            if (this.shapeCache != null) {
                return this.shapeCache.transparent;
            }
            return this.getBlock().isTransparent(this.asBlockState(), world, pos);
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
            return this.offsetter.map(offsetter -> offsetter.evaluate(this.asBlockState(), world, pos)).orElse(Vec3d.ZERO);
        }

        public boolean hasModelOffset() {
            return !this.offsetter.isEmpty();
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

        public void scheduledTick(ServerWorld world, BlockPos pos, Random random) {
            this.getBlock().scheduledTick(this.asBlockState(), world, pos, random);
        }

        public void randomTick(ServerWorld world, BlockPos pos, Random random) {
            this.getBlock().randomTick(this.asBlockState(), world, pos, random);
        }

        public void onEntityCollision(World world, BlockPos pos, Entity entity) {
            this.getBlock().onEntityCollision(this.asBlockState(), world, pos, entity);
        }

        public void onStacksDropped(ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
            this.getBlock().onStacksDropped(this.asBlockState(), world, pos, tool, dropExperience);
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

        public boolean isReplaceable() {
            return this.getMaterial().isReplaceable();
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
            return this.fluidState;
        }

        public boolean hasRandomTicks() {
            return this.ticksRandomly;
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

        public boolean hasBlockBreakParticles() {
            return this.blockBreakParticles;
        }

        static final class ShapeCache {
            private static final Direction[] DIRECTIONS = Direction.values();
            private static final int SHAPE_TYPE_LENGTH = SideShapeType.values().length;
            protected final boolean fullOpaque;
            final boolean transparent;
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
                this.transparent = block.isTransparent(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
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
                if (!this.collisionShape.isEmpty() && state.hasModelOffset()) {
                    throw new IllegalStateException(String.format(Locale.ROOT, "%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.", Registries.BLOCK.getId(block)));
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

