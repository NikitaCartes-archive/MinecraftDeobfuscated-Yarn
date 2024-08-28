package net.minecraft.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.NoteBlockInstrument;
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
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeyedValue;
import net.minecraft.registry.RegistryKeys;
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
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.explosion.Explosion;

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
 * @apiNote In vanilla subclasses, these methods are called either to do the
 * default behavior (e.g. {@code super.onUse(...)}). Because the methods are {@code protected},
 * you must use these methods via the corresponding method in {@link
 * AbstractBlockState}.
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
public abstract class AbstractBlock implements ToggleableFeature {
	protected static final Direction[] DIRECTIONS = new Direction[]{
		Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP
	};
	/**
	 * Whether this block can be walked on or through.
	 * 
	 * @see #getCollisionShape
	 */
	protected final boolean collidable;
	/**
	 * The blast resistance of the block.
	 * 
	 * @see Block#getBlastResistance
	 */
	protected final float resistance;
	/**
	 * Whether this block should tick when randomly selected when ticking the world. An example of this ticking is crop growth.
	 * 
	 * @see Block#hasRandomTicks
	 * @see net.minecraft.server.world.ServerWorld#tickChunk
	 */
	protected final boolean randomTicks;
	/**
	 * The collection of sounds played when breaking, stepping on, placing, hitting (with a projectile), or falling on this block.
	 * 
	 * @see #getSoundGroup
	 */
	protected final BlockSoundGroup soundGroup;
	/**
	 * A speed reduction applied to a {@link net.minecraft.entity.LivingEntity} that tries to move across this block.
	 * 
	 * @see Block#getSlipperiness
	 * @see net.minecraft.entity.LivingEntity#travel
	 */
	protected final float slipperiness;
	/**
	 * The multiplier applied to the velocity of an {@link net.minecraft.entity.Entity} when it walks on this block.
	 * 
	 * @see Block#getVelocityMultiplier
	 * @see net.minecraft.entity.Entity#getVelocityMultiplier
	 */
	protected final float velocityMultiplier;
	/**
	 * The multiplier applied to the velocity of a {@link net.minecraft.entity.LivingEntity} when it jumps off this block.
	 * 
	 * @see Block#getJumpVelocityMultiplier
	 * @see net.minecraft.entity.Entity#getJumpVelocityMultiplier
	 */
	protected final float jumpVelocityMultiplier;
	/**
	 * Whether this block's collision shape can change.
	 * 
	 * @see Block#hasDynamicBounds
	 */
	protected final boolean dynamicBounds;
	/**
	 * The set of {@link net.minecraft.resource.featuretoggle.FeatureFlag FeatureFlags} that are required for this block to work correctly.
	 * 
	 * @see net.minecraft.resource.featuretoggle.FeatureFlags
	 */
	protected final FeatureSet requiredFeatures;
	/**
	 * The {@link AbstractBlock.Settings} to apply to this block.
	 */
	protected final AbstractBlock.Settings settings;
	/**
	 * The {@link RegistryKey} of the loot table that determines what this block drops.
	 * 
	 * @see #getLootTableKey
	 * @see #getDroppedStacks
	 */
	protected final Optional<RegistryKey<LootTable>> lootTableKey;
	protected final String translationKey;

	public AbstractBlock(AbstractBlock.Settings settings) {
		this.collidable = settings.collidable;
		this.lootTableKey = settings.getLootTableKey();
		this.translationKey = settings.getTranslationKey();
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

	public AbstractBlock.Settings getSettings() {
		return this.settings;
	}

	protected abstract MapCodec<? extends Block> getCodec();

	protected static <B extends Block> RecordCodecBuilder<B, AbstractBlock.Settings> createSettingsCodec() {
		return AbstractBlock.Settings.CODEC.fieldOf("properties").forGetter(AbstractBlock::getSettings);
	}

	public static <B extends Block> MapCodec<B> createCodec(Function<AbstractBlock.Settings, B> blockFromSettings) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(createSettingsCodec()).apply(instance, blockFromSettings));
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
	 * @see AbstractBlockState#prepare(WorldAccess, BlockPos, int, int)
	 * @see #getStateForNeighborUpdate
	 * @see #neighborUpdate
	 */
	protected void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
	}

	/**
	 * {@return if an entity using navigation type {@code type} can navigate through this block}
	 * 
	 * @apiNote Subclasses may override this to prevent or restrict pathfinding through the
	 * block. For example, {@link DoorBlock} restricts it to open doors only.
	 * 
	 * @see AbstractBlockState#canPathfindThrough
	 */
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		switch (type) {
			case LAND:
				return !state.isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
			case WATER:
				return state.getFluidState().isIn(FluidTags.WATER);
			case AIR:
				return !state.isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
			default:
				return false;
		}
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
	 * net.minecraft.world.World#isClient}. This is not called if {@link Block#FORCE_STATE} flag is set in the {@code
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
	 * @see AbstractBlockState#getStateForNeighborUpdate
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
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return state;
	}

	/**
	 * @see AbstractBlockState#isSideInvisible
	 */
	protected boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
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
	 * @see AbstractBlockState#neighborUpdate
	 * @see #getStateForNeighborUpdate
	 * @see net.minecraft.world.RedstoneView#isReceivingRedstonePower
	 */
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
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
	 * @see AbstractBlockState#onBlockAdded
	 * @see #onStateReplaced
	 */
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
	}

	/**
	 * Called server-side on the old block when the block state is changed. This includes block
	 * removal. This is used to update neighboring blocks when an active redstone power source
	 * is removed, or to drop the contents of an inventory. The check {@code
	 * state.isOf(newState.getBlock())} can be used to see if the block was removed or not.
	 * 
	 * <p>For most block entities, {@link net.minecraft.util.ItemScatterer#onStateReplaced}
	 * provides a good implementation of this method. Make sure to call {@code
	 * super.onStateReplaced(state, world, pos, newState, moved);} <strong>after</strong>
	 * invoking {@code ItemScatterer} methods.
	 * 
	 * @see AbstractBlockState#onStateReplaced
	 * 
	 * @see net.minecraft.util.ItemScatterer#onStateReplaced
	 * @see net.minecraft.util.ItemScatterer#spawn(World, BlockPos, net.minecraft.inventory.Inventory)
	 * @see #onBlockAdded
	 */
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
			world.removeBlockEntity(pos);
		}
	}

	protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
		if (!state.isAir() && explosion.getDestructionType() != Explosion.DestructionType.TRIGGER_BLOCK) {
			Block block = state.getBlock();
			boolean bl = explosion.getCausingEntity() instanceof PlayerEntity;
			if (block.shouldDropItemsOnExplosion(explosion)) {
				BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
				LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world)
					.add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
					.add(LootContextParameters.TOOL, ItemStack.EMPTY)
					.addOptional(LootContextParameters.BLOCK_ENTITY, blockEntity)
					.addOptional(LootContextParameters.THIS_ENTITY, explosion.getEntity());
				if (explosion.getDestructionType() == Explosion.DestructionType.DESTROY_WITH_DECAY) {
					builder.add(LootContextParameters.EXPLOSION_RADIUS, explosion.getPower());
				}

				state.onStacksDropped(world, pos, ItemStack.EMPTY, bl);
				state.getDroppedStacks(builder).forEach(stack -> stackMerger.accept(stack, pos));
			}

			world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
			block.onDestroyedByExplosion(world, pos, explosion);
		}
	}

	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		return ActionResult.PASS;
	}

	protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
	}

	/**
	 * Handles the block event, which is an event specific to a block with an integer ID and data.
	 * 
	 * @return whether the event was handled successfully
	 * 
	 * @see AbstractBlockState#onSyncedBlockEvent
	 * @see World#addSyncedBlockEvent
	 */
	protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		return false;
	}

	/**
	 * {@return the block's render type (invisible, animated, model)}
	 * 
	 * @apiNote {@link BlockWithEntity} overrides this to return {@link BlockRenderType#INVISIBLE};
	 * therefore, custom blocks extending that class must override it again to render the block.
	 * 
	 * @see AbstractBlockState#getRenderType
	 */
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	/**
	 * {@return whether the block's transparency depends on the side of the block, like slabs}
	 * 
	 * @see AbstractBlockState#hasSidedTransparency
	 */
	protected boolean hasSidedTransparency(BlockState state) {
		return false;
	}

	/**
	 * {@return whether the block is capable of emitting redstone power}
	 * 
	 * <p>This does not return whether the block is currently emitting redstone power.
	 * Use {@link World#isEmittingRedstonePower} in that case.
	 * 
	 * @see AbstractBlockState#emitsRedstonePower
	 * 
	 * @see World#isEmittingRedstonePower
	 */
	protected boolean emitsRedstonePower(BlockState state) {
		return false;
	}

	/**
	 * {@return the state's associated fluid state}
	 * 
	 * <p>{@linkplain Waterloggable Waterloggable blocks} must override this to return {@code Fluids.WATER.getStill(false)}
	 * when waterlogged.
	 * 
	 * @see AbstractBlockState#getFluidState
	 * @see net.minecraft.fluid.Fluids#WATER
	 */
	protected FluidState getFluidState(BlockState state) {
		return Fluids.EMPTY.getDefaultState();
	}

	/**
	 * {@return whether the block can have a comparator output}
	 * 
	 * <p>This does not check the current comparator output of the block.
	 * Use {@link #getComparatorOutput} in that case.
	 * 
	 * @see AbstractBlockState#hasComparatorOutput
	 * 
	 * @see #getComparatorOutput
	 */
	protected boolean hasComparatorOutput(BlockState state) {
		return false;
	}

	protected float getMaxHorizontalModelOffset() {
		return 0.25F;
	}

	protected float getVerticalModelOffsetMultiplier() {
		return 0.2F;
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
	 * @see AbstractBlockState#rotate
	 */
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state;
	}

	/**
	 * {@return {@code state} mirrored by {@code mirror}}
	 * 
	 * <p>By default, this returns the provided block state.
	 * 
	 * @see AbstractBlockState#mirror
	 */
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state;
	}

	/**
	 * {@return whether the item can replace the block}
	 * 
	 * <p>By default, this checks if the block allows replacing and whether the
	 * item differs from the block's item. Items composed of multiple blocks, such as candles,
	 * vines, or snow layers, should override this to implement additional checks.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 * 
	 * @see AbstractBlockState#canReplace
	 * @see #canBucketPlace
	 * @see AbstractBlockState#isReplaceable
	 */
	protected boolean canReplace(BlockState state, ItemPlacementContext context) {
		return state.isReplaceable() && (context.getStack().isEmpty() || !context.getStack().isOf(this.asItem()));
	}

	/**
	 * {@return whether a bucket can replace the block with the fluid}
	 * 
	 * <p>By default, this checks if the block allows replacing or is not solid.
	 * Blocks intended to be unbreakable should override this to implement additional checks.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 * 
	 * @see AbstractBlockState#canBucketPlace
	 * @see #canReplace
	 * @see AbstractBlockState#isReplaceable
	 */
	protected boolean canBucketPlace(BlockState state, Fluid fluid) {
		return state.isReplaceable() || !state.isSolid();
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
	 * @see AbstractBlockState#getDroppedStacks
	 * @see #onStateReplaced
	 * @see #onStacksDropped
	 * @see ItemStack#split
	 * @see net.minecraft.loot.context.LootContextParameters
	 */
	protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		if (this.lootTableKey.isEmpty()) {
			return Collections.emptyList();
		} else {
			LootContextParameterSet lootContextParameterSet = builder.add(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
			ServerWorld serverWorld = lootContextParameterSet.getWorld();
			LootTable lootTable = serverWorld.getServer().getReloadableRegistries().getLootTable((RegistryKey<LootTable>)this.lootTableKey.get());
			return lootTable.generateLoot(lootContextParameterSet);
		}
	}

	/**
	 * {@return the seed value for rendering}
	 * 
	 * <p>This is usually the hash code of {@code pos}. Tall or wide blocks (such as doors or
	 * beds) should override this to make sure both parts of the block have the same seed.
	 * 
	 * @see AbstractBlockState#getRenderingSeed
	 */
	protected long getRenderingSeed(BlockState state, BlockPos pos) {
		return MathHelper.hashCode(pos);
	}

	/**
	 * @see AbstractBlockState#getCullingShape
	 */
	protected VoxelShape getCullingShape(BlockState state) {
		return state.getOutlineShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
	}

	/**
	 * @see AbstractBlockState#getSidesShape
	 */
	protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		return this.getCollisionShape(state, world, pos, ShapeContext.absent());
	}

	/**
	 * @see AbstractBlockState#getRaycastShape
	 */
	protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	/**
	 * @see AbstractBlockState#getOpacity
	 */
	protected int getOpacity(BlockState state) {
		if (state.isOpaqueFullCube()) {
			return 15;
		} else {
			return state.isTransparent() ? 0 : 1;
		}
	}

	/**
	 * {@return the screen handler factory or {@code null} if screen handler cannot be created}
	 * 
	 * <p>This method should be overridden for blocks with screen handlers, such as anvils.
	 * The created screen handler is usually passed to {@link net.minecraft.entity.player.PlayerEntity#openHandledScreen}.
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
	 * @see AbstractBlockState#createScreenHandlerFactory
	 * @see net.minecraft.screen.SimpleNamedScreenHandlerFactory
	 * @see net.minecraft.block.entity.LockableContainerBlockEntity
	 */
	@Nullable
	protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
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
	 * net.minecraft.world.World#setBlockState(BlockPos, BlockState)} call.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * net.minecraft.world.World#isClient}.
	 * 
	 * @see AbstractBlockState#canPlaceAt
	 * @see #getStateForNeighborUpdate
	 */
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return true;
	}

	/**
	 * @see AbstractBlockState#getAmbientOcclusionLightLevel
	 */
	protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return state.isFullCube(world, pos) ? 0.2F : 1.0F;
	}

	/**
	 * {@return the comparator output of the block, from {@code 0} to {@code 15}}
	 * 
	 * <p>When overriding this, {@link #hasComparatorOutput} must also be overridden.
	 * 
	 * @see AbstractBlockState#getComparatorOutput
	 * @see #hasComparatorOutput
	 */
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return 0;
	}

	/**
	 * @see AbstractBlockState#getOutlineShape(BlockView, BlockPos, ShapeContext)
	 */
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.fullCube();
	}

	/**
	 * @see AbstractBlockState#getCollisionShape(BlockView, BlockPos, ShapeContext)
	 */
	protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.collidable ? state.getOutlineShape(world, pos) : VoxelShapes.empty();
	}

	/**
	 * @see AbstractBlockState#isFullCube
	 */
	protected boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
		return Block.isShapeFullCube(state.getCollisionShape(world, pos));
	}

	/**
	 * @see AbstractBlockState#getCameraCollisionShape
	 */
	protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getCollisionShape(state, world, pos, context);
	}

	/**
	 * Called server-side when a block gets ticked randomly. This can be overridden to implement
	 * various logics, most commonly plant growth. Default implementation does
	 * nothing. To control the rate of the action, use {@code random}.
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
	 * @see AbstractBlockState#randomTick
	 * 
	 * @see CropBlock
	 * @see #scheduledTick
	 */
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
	}

	/**
	 * Called server-side when a block receives a scheduled tick. This can be used like a timer.
	 * Scheduled ticks are added using {@link
	 * net.minecraft.world.WorldAccess#scheduleBlockTick(BlockPos, Block, int)}.
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
	 * @see AbstractBlockState#scheduledTick
	 * @see net.minecraft.world.WorldAccess#scheduleBlockTick(BlockPos, Block, int)
	 * @see #getStateForNeighborUpdate
	 * @see #randomTick
	 */
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
	}

	/**
	 * @see AbstractBlockState#calcBlockBreakingDelta
	 */
	protected float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		float f = state.getHardness(world, pos);
		if (f == -1.0F) {
			return 0.0F;
		} else {
			int i = player.canHarvest(state) ? 30 : 100;
			return player.getBlockBreakingSpeed(state) / f / (float)i;
		}
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
	 * @see AbstractBlockState#onStacksDropped
	 * @see ExperienceDroppingBlock
	 * @see Block#dropExperienceWhenMined
	 * @see #getDroppedStacks
	 * @see #onStateReplaced
	 */
	protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
	}

	/**
	 * Called when a player starts breaking the block (including when instant-mining).
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 * 
	 * @see AbstractBlockState#onBlockBreakStart
	 */
	protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
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
	 * @see AbstractBlockState#getWeakRedstonePower
	 * 
	 * @see #emitsRedstonePower
	 * @see #getStrongRedstonePower
	 * @see net.minecraft.world.RedstoneView#isReceivingRedstonePower
	 */
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
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
	 * @see AbstractBlockState#onEntityCollision
	 * @see Block#onSteppedOn
	 * @see #onProjectileHit
	 */
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
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
	 * @see AbstractBlockState#getStrongRedstonePower
	 * 
	 * @see #emitsRedstonePower
	 * @see #getWeakRedstonePower
	 * @see net.minecraft.world.RedstoneView#isReceivingRedstonePower
	 */
	protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return 0;
	}

	public final Optional<RegistryKey<LootTable>> getLootTableKey() {
		return this.lootTableKey;
	}

	public final String getTranslationKey() {
		return this.translationKey;
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
	 * @see AbstractBlockState#onProjectileHit
	 * @see ProjectileEntity#onBlockHit
	 * @see #onEntityCollision
	 */
	protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
	}

	protected boolean isTransparent(BlockState state) {
		return !Block.isShapeFullCube(state.getOutlineShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN)) && state.getFluidState().isEmpty();
	}

	protected boolean hasRandomTicks(BlockState state) {
		return this.randomTicks;
	}

	protected BlockSoundGroup getSoundGroup(BlockState state) {
		return this.soundGroup;
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
		return (MapColor)this.settings.mapColorProvider.apply(this.asBlock().getDefaultState());
	}

	public float getHardness() {
		return this.settings.hardness;
	}

	public abstract static class AbstractBlockState extends State<Block, BlockState> {
		private static final Direction[] DIRECTIONS = Direction.values();
		private static final VoxelShape[] EMPTY_CULLING_FACES = Util.make(new VoxelShape[DIRECTIONS.length], direction -> Arrays.fill(direction, VoxelShapes.empty()));
		private static final VoxelShape[] FULL_CULLING_FACES = Util.make(
			new VoxelShape[DIRECTIONS.length], direction -> Arrays.fill(direction, VoxelShapes.fullCube())
		);
		private final int luminance;
		private final boolean hasSidedTransparency;
		private final boolean isAir;
		private final boolean burnable;
		@Deprecated
		private final boolean liquid;
		@Deprecated
		private boolean solid;
		private final PistonBehavior pistonBehavior;
		private final MapColor mapColor;
		private final float hardness;
		private final boolean toolRequired;
		private final boolean opaque;
		private final AbstractBlock.ContextPredicate solidBlockPredicate;
		private final AbstractBlock.ContextPredicate suffocationPredicate;
		private final AbstractBlock.ContextPredicate blockVisionPredicate;
		private final AbstractBlock.ContextPredicate postProcessPredicate;
		private final AbstractBlock.ContextPredicate emissiveLightingPredicate;
		@Nullable
		private final AbstractBlock.Offsetter offsetter;
		private final boolean blockBreakParticles;
		private final NoteBlockInstrument instrument;
		private final boolean replaceable;
		@Nullable
		private AbstractBlock.AbstractBlockState.ShapeCache shapeCache;
		private FluidState fluidState = Fluids.EMPTY.getDefaultState();
		private boolean ticksRandomly;
		private boolean opaqueFullCube;
		private VoxelShape cullingShape;
		private VoxelShape[] cullingFaces;
		private boolean transparent;
		private int opacity;

		protected AbstractBlockState(Block block, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> codec) {
			super(block, propertyMap, codec);
			AbstractBlock.Settings settings = block.settings;
			this.luminance = settings.luminance.applyAsInt(this.asBlockState());
			this.hasSidedTransparency = block.hasSidedTransparency(this.asBlockState());
			this.isAir = settings.isAir;
			this.burnable = settings.burnable;
			this.liquid = settings.liquid;
			this.pistonBehavior = settings.pistonBehavior;
			this.mapColor = (MapColor)settings.mapColorProvider.apply(this.asBlockState());
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
			this.instrument = settings.instrument;
			this.replaceable = settings.replaceable;
		}

		private boolean shouldBeSolid() {
			if (this.owner.settings.forceSolid) {
				return true;
			} else if (this.owner.settings.forceNotSolid) {
				return false;
			} else if (this.shapeCache == null) {
				return false;
			} else {
				VoxelShape voxelShape = this.shapeCache.collisionShape;
				if (voxelShape.isEmpty()) {
					return false;
				} else {
					Box box = voxelShape.getBoundingBox();
					return box.getAverageSideLength() >= 0.7291666666666666 ? true : box.getLengthY() >= 1.0;
				}
			}
		}

		public void initShapeCache() {
			this.fluidState = this.owner.getFluidState(this.asBlockState());
			this.ticksRandomly = this.owner.hasRandomTicks(this.asBlockState());
			if (!this.getBlock().hasDynamicBounds()) {
				this.shapeCache = new AbstractBlock.AbstractBlockState.ShapeCache(this.asBlockState());
			}

			this.solid = this.shouldBeSolid();
			this.cullingShape = this.opaque ? this.owner.getCullingShape(this.asBlockState()) : VoxelShapes.empty();
			this.opaqueFullCube = Block.isShapeFullCube(this.cullingShape);
			if (this.cullingShape.isEmpty()) {
				this.cullingFaces = EMPTY_CULLING_FACES;
			} else if (this.opaqueFullCube) {
				this.cullingFaces = FULL_CULLING_FACES;
			} else {
				this.cullingFaces = new VoxelShape[DIRECTIONS.length];

				for (Direction direction : DIRECTIONS) {
					this.cullingFaces[direction.ordinal()] = this.cullingShape.getFace(direction);
				}
			}

			this.transparent = this.owner.isTransparent(this.asBlockState());
			this.opacity = this.owner.getOpacity(this.asBlockState());
		}

		public Block getBlock() {
			return this.owner;
		}

		public RegistryEntry<Block> getRegistryEntry() {
			return this.owner.getRegistryEntry();
		}

		@Deprecated
		public boolean blocksMovement() {
			Block block = this.getBlock();
			return block != Blocks.COBWEB && block != Blocks.BAMBOO_SAPLING && this.isSolid();
		}

		@Deprecated
		public boolean isSolid() {
			return this.solid;
		}

		public boolean allowsSpawning(BlockView world, BlockPos pos, EntityType<?> type) {
			return this.getBlock().settings.allowsSpawningPredicate.test(this.asBlockState(), world, pos, type);
		}

		public boolean isTransparent() {
			return this.transparent;
		}

		public int getOpacity() {
			return this.opacity;
		}

		public VoxelShape getCullingFace(Direction direction) {
			return this.cullingFaces[direction.ordinal()];
		}

		public VoxelShape getCullingShape() {
			return this.cullingShape;
		}

		public boolean exceedsCube() {
			return this.shapeCache == null || this.shapeCache.exceedsCube;
		}

		public boolean hasSidedTransparency() {
			return this.hasSidedTransparency;
		}

		/**
		 * {@return the light level emitted by this block state}
		 */
		public int getLuminance() {
			return this.luminance;
		}

		public boolean isAir() {
			return this.isAir;
		}

		public boolean isBurnable() {
			return this.burnable;
		}

		@Deprecated
		public boolean isLiquid() {
			return this.liquid;
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
			return this.pistonBehavior;
		}

		public boolean isOpaqueFullCube() {
			return this.opaqueFullCube;
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
			return this.shapeCache != null ? this.shapeCache.collisionShape : this.getCollisionShape(world, pos, ShapeContext.absent());
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

		public Vec3d getModelOffset(BlockPos pos) {
			AbstractBlock.Offsetter offsetter = this.offsetter;
			return offsetter != null ? offsetter.evaluate(this.asBlockState(), pos) : Vec3d.ZERO;
		}

		public boolean hasModelOffset() {
			return this.offsetter != null;
		}

		public boolean onSyncedBlockEvent(World world, BlockPos pos, int type, int data) {
			return this.getBlock().onSyncedBlockEvent(this.asBlockState(), world, pos, type, data);
		}

		public void neighborUpdate(World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
			DebugInfoSender.sendNeighborUpdate(world, pos);
			this.getBlock().neighborUpdate(this.asBlockState(), world, pos, sourceBlock, wireOrientation, notify);
		}

		public final void updateNeighbors(WorldAccess world, BlockPos pos, int flags) {
			this.updateNeighbors(world, pos, flags, 512);
		}

		public final void updateNeighbors(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (Direction direction : AbstractBlock.DIRECTIONS) {
				mutable.set(pos, direction);
				world.replaceWithStateForNeighborUpdate(direction.getOpposite(), mutable, pos, this.asBlockState(), flags, maxUpdateDepth);
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

		public void onExploded(ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
			this.getBlock().onExploded(this.asBlockState(), world, pos, explosion, stackMerger);
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

		public List<ItemStack> getDroppedStacks(LootContextParameterSet.Builder builder) {
			return this.getBlock().getDroppedStacks(this.asBlockState(), builder);
		}

		public ActionResult onUseWithItem(ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hit) {
			return this.getBlock().onUseWithItem(stack, this.asBlockState(), world, hit.getBlockPos(), player, hand, hit);
		}

		public ActionResult onUse(World world, PlayerEntity player, BlockHitResult hit) {
			return this.getBlock().onUse(this.asBlockState(), world, hit.getBlockPos(), player, hit);
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

		/**
		 * Gets the possibly updated block state of this block when a neighboring block is updated.
		 * 
		 * @return the new state of this block
		 * 
		 * @param neighborPos the position of the neighbor block
		 * @param neighborState the state of the updated neighbor block
		 * @param direction the direction from this block to the neighbor
		 * @param pos the position of this block
		 * @param world the world
		 */
		public BlockState getStateForNeighborUpdate(Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
			return this.getBlock().getStateForNeighborUpdate(this.asBlockState(), direction, neighborState, world, pos, neighborPos);
		}

		public boolean canPathfindThrough(NavigationType type) {
			return this.getBlock().canPathfindThrough(this.asBlockState(), type);
		}

		public boolean canReplace(ItemPlacementContext context) {
			return this.getBlock().canReplace(this.asBlockState(), context);
		}

		public boolean canBucketPlace(Fluid fluid) {
			return this.getBlock().canBucketPlace(this.asBlockState(), fluid);
		}

		public boolean isReplaceable() {
			return this.replaceable;
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

		public boolean isIn(TagKey<Block> tag, Predicate<AbstractBlock.AbstractBlockState> predicate) {
			return this.isIn(tag) && predicate.test(this);
		}

		public boolean isIn(RegistryEntryList<Block> blocks) {
			return blocks.contains(this.getBlock().getRegistryEntry());
		}

		public boolean isOf(RegistryEntry<Block> blockEntry) {
			return this.isOf(blockEntry.value());
		}

		public Stream<TagKey<Block>> streamTags() {
			return this.getBlock().getRegistryEntry().streamTags();
		}

		public boolean hasBlockEntity() {
			return this.getBlock() instanceof BlockEntityProvider;
		}

		@Nullable
		public <T extends BlockEntity> BlockEntityTicker<T> getBlockEntityTicker(World world, BlockEntityType<T> blockEntityType) {
			return this.getBlock() instanceof BlockEntityProvider ? ((BlockEntityProvider)this.getBlock()).getTicker(world, this.asBlockState(), blockEntityType) : null;
		}

		public boolean isOf(Block block) {
			return this.getBlock() == block;
		}

		public boolean matchesKey(RegistryKey<Block> key) {
			return this.getBlock().getRegistryEntry().matchesKey(key);
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
			return this.shapeCache != null ? this.shapeCache.isSideSolid(direction, shapeType) : shapeType.matches(this.asBlockState(), world, pos, direction);
		}

		public boolean isFullCube(BlockView world, BlockPos pos) {
			return this.shapeCache != null ? this.shapeCache.isFullCube : this.getBlock().isShapeFullCube(this.asBlockState(), world, pos);
		}

		protected abstract BlockState asBlockState();

		public boolean isToolRequired() {
			return this.toolRequired;
		}

		public boolean hasBlockBreakParticles() {
			return this.blockBreakParticles;
		}

		public NoteBlockInstrument getInstrument() {
			return this.instrument;
		}

		static final class ShapeCache {
			private static final Direction[] DIRECTIONS = Direction.values();
			private static final int SHAPE_TYPE_LENGTH = SideShapeType.values().length;
			protected final VoxelShape collisionShape;
			protected final boolean exceedsCube;
			private final boolean[] solidSides;
			protected final boolean isFullCube;

			ShapeCache(BlockState state) {
				Block block = state.getBlock();
				this.collisionShape = block.getCollisionShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, ShapeContext.absent());
				if (!this.collisionShape.isEmpty() && state.hasModelOffset()) {
					throw new IllegalStateException(
						String.format(
							Locale.ROOT, "%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.", Registries.BLOCK.getId(block)
						)
					);
				} else {
					this.exceedsCube = Arrays.stream(Direction.Axis.values())
						.anyMatch(axis -> this.collisionShape.getMin(axis) < 0.0 || this.collisionShape.getMax(axis) > 1.0);
					this.solidSides = new boolean[DIRECTIONS.length * SHAPE_TYPE_LENGTH];

					for (Direction direction : DIRECTIONS) {
						for (SideShapeType sideShapeType : SideShapeType.values()) {
							this.solidSides[indexSolidSide(direction, sideShapeType)] = sideShapeType.matches(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction);
						}
					}

					this.isFullCube = Block.isShapeFullCube(state.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
				}
			}

			public boolean isSideSolid(Direction direction, SideShapeType shapeType) {
				return this.solidSides[indexSolidSide(direction, shapeType)];
			}

			private static int indexSolidSide(Direction direction, SideShapeType shapeType) {
				return direction.ordinal() * SHAPE_TYPE_LENGTH + shapeType.ordinal();
			}
		}
	}

	@FunctionalInterface
	public interface ContextPredicate {
		boolean test(BlockState state, BlockView world, BlockPos pos);
	}

	public static enum OffsetType {
		NONE,
		XZ,
		XYZ;
	}

	@FunctionalInterface
	public interface Offsetter {
		Vec3d evaluate(BlockState state, BlockPos pos);
	}

	public static class Settings {
		public static final Codec<AbstractBlock.Settings> CODEC = Codec.unit((Supplier<AbstractBlock.Settings>)(() -> create()));
		Function<BlockState, MapColor> mapColorProvider = state -> MapColor.CLEAR;
		boolean collidable = true;
		BlockSoundGroup soundGroup = BlockSoundGroup.STONE;
		ToIntFunction<BlockState> luminance = state -> 0;
		float resistance;
		float hardness;
		boolean toolRequired;
		boolean randomTicks;
		float slipperiness = 0.6F;
		float velocityMultiplier = 1.0F;
		float jumpVelocityMultiplier = 1.0F;
		@Nullable
		private RegistryKey<Block> registryKey;
		private RegistryKeyedValue<Block, Optional<RegistryKey<LootTable>>> lootTable = registryKey -> Optional.of(
				RegistryKey.of(RegistryKeys.LOOT_TABLE, registryKey.getValue().withPrefixedPath("blocks/"))
			);
		private RegistryKeyedValue<Block, String> translationKey = registryKey -> Util.createTranslationKey("block", registryKey.getValue());
		boolean opaque = true;
		boolean isAir;
		boolean burnable;
		@Deprecated
		boolean liquid;
		@Deprecated
		boolean forceNotSolid;
		boolean forceSolid;
		PistonBehavior pistonBehavior = PistonBehavior.NORMAL;
		boolean blockBreakParticles = true;
		NoteBlockInstrument instrument = NoteBlockInstrument.HARP;
		boolean replaceable;
		AbstractBlock.TypedContextPredicate<EntityType<?>> allowsSpawningPredicate = (state, world, pos, type) -> state.isSideSolidFullSquare(
					world, pos, Direction.UP
				)
				&& state.getLuminance() < 14;
		AbstractBlock.ContextPredicate solidBlockPredicate = (state, world, pos) -> state.isFullCube(world, pos);
		AbstractBlock.ContextPredicate suffocationPredicate = (state, world, pos) -> state.blocksMovement() && state.isFullCube(world, pos);
		AbstractBlock.ContextPredicate blockVisionPredicate = this.suffocationPredicate;
		AbstractBlock.ContextPredicate postProcessPredicate = (state, world, pos) -> false;
		AbstractBlock.ContextPredicate emissiveLightingPredicate = (state, world, pos) -> false;
		boolean dynamicBounds;
		FeatureSet requiredFeatures = FeatureFlags.VANILLA_FEATURES;
		@Nullable
		AbstractBlock.Offsetter offsetter;

		private Settings() {
		}

		public static AbstractBlock.Settings create() {
			return new AbstractBlock.Settings();
		}

		public static AbstractBlock.Settings copy(AbstractBlock block) {
			AbstractBlock.Settings settings = copyShallow(block);
			AbstractBlock.Settings settings2 = block.settings;
			settings.jumpVelocityMultiplier = settings2.jumpVelocityMultiplier;
			settings.solidBlockPredicate = settings2.solidBlockPredicate;
			settings.allowsSpawningPredicate = settings2.allowsSpawningPredicate;
			settings.postProcessPredicate = settings2.postProcessPredicate;
			settings.suffocationPredicate = settings2.suffocationPredicate;
			settings.blockVisionPredicate = settings2.blockVisionPredicate;
			settings.lootTable = settings2.lootTable;
			settings.translationKey = settings2.translationKey;
			return settings;
		}

		@Deprecated
		public static AbstractBlock.Settings copyShallow(AbstractBlock block) {
			AbstractBlock.Settings settings = new AbstractBlock.Settings();
			AbstractBlock.Settings settings2 = block.settings;
			settings.hardness = settings2.hardness;
			settings.resistance = settings2.resistance;
			settings.collidable = settings2.collidable;
			settings.randomTicks = settings2.randomTicks;
			settings.luminance = settings2.luminance;
			settings.mapColorProvider = settings2.mapColorProvider;
			settings.soundGroup = settings2.soundGroup;
			settings.slipperiness = settings2.slipperiness;
			settings.velocityMultiplier = settings2.velocityMultiplier;
			settings.dynamicBounds = settings2.dynamicBounds;
			settings.opaque = settings2.opaque;
			settings.isAir = settings2.isAir;
			settings.burnable = settings2.burnable;
			settings.liquid = settings2.liquid;
			settings.forceNotSolid = settings2.forceNotSolid;
			settings.forceSolid = settings2.forceSolid;
			settings.pistonBehavior = settings2.pistonBehavior;
			settings.toolRequired = settings2.toolRequired;
			settings.offsetter = settings2.offsetter;
			settings.blockBreakParticles = settings2.blockBreakParticles;
			settings.requiredFeatures = settings2.requiredFeatures;
			settings.emissiveLightingPredicate = settings2.emissiveLightingPredicate;
			settings.instrument = settings2.instrument;
			settings.replaceable = settings2.replaceable;
			return settings;
		}

		public AbstractBlock.Settings mapColor(DyeColor color) {
			this.mapColorProvider = state -> color.getMapColor();
			return this;
		}

		public AbstractBlock.Settings mapColor(MapColor color) {
			this.mapColorProvider = state -> color;
			return this;
		}

		public AbstractBlock.Settings mapColor(Function<BlockState, MapColor> mapColorProvider) {
			this.mapColorProvider = mapColorProvider;
			return this;
		}

		/**
		 * Specifies that a block should have no collision bounds.
		 * 
		 * <p>This also marks a block as non-opaque.
		 */
		public AbstractBlock.Settings noCollision() {
			this.collidable = false;
			this.opaque = false;
			return this;
		}

		/**
		 * Specifies that a block should be non-opaque and light should be allowed to pass through.
		 */
		public AbstractBlock.Settings nonOpaque() {
			this.opaque = false;
			return this;
		}

		public AbstractBlock.Settings slipperiness(float slipperiness) {
			this.slipperiness = slipperiness;
			return this;
		}

		public AbstractBlock.Settings velocityMultiplier(float velocityMultiplier) {
			this.velocityMultiplier = velocityMultiplier;
			return this;
		}

		public AbstractBlock.Settings jumpVelocityMultiplier(float jumpVelocityMultiplier) {
			this.jumpVelocityMultiplier = jumpVelocityMultiplier;
			return this;
		}

		public AbstractBlock.Settings sounds(BlockSoundGroup soundGroup) {
			this.soundGroup = soundGroup;
			return this;
		}

		/**
		 * Specifies the light level emitted by a block.
		 * 
		 * @param luminance a per block state light level, with values between 0 and 15
		 */
		public AbstractBlock.Settings luminance(ToIntFunction<BlockState> luminance) {
			this.luminance = luminance;
			return this;
		}

		public AbstractBlock.Settings strength(float hardness, float resistance) {
			return this.hardness(hardness).resistance(resistance);
		}

		/**
		 * Specifies that a block is broken instantly.
		 */
		public AbstractBlock.Settings breakInstantly() {
			return this.strength(0.0F);
		}

		public AbstractBlock.Settings strength(float strength) {
			this.strength(strength, strength);
			return this;
		}

		public AbstractBlock.Settings ticksRandomly() {
			this.randomTicks = true;
			return this;
		}

		/**
		 * Specifies that a block's collision bounds can dynamically resize.
		 * By default, block collision bounds are cached for performance.
		 * By invoking this method, the game will not cache the block collision bounds and instead calculate the collision bounds when needed.
		 */
		public AbstractBlock.Settings dynamicBounds() {
			this.dynamicBounds = true;
			return this;
		}

		public AbstractBlock.Settings dropsNothing() {
			this.lootTable = RegistryKeyedValue.fixed(Optional.empty());
			return this;
		}

		public AbstractBlock.Settings lootTable(Optional<RegistryKey<LootTable>> lootTableKey) {
			this.lootTable = RegistryKeyedValue.fixed(lootTableKey);
			return this;
		}

		protected Optional<RegistryKey<LootTable>> getLootTableKey() {
			return this.lootTable.get((RegistryKey<Block>)Objects.requireNonNull(this.registryKey, "Block id not set"));
		}

		public AbstractBlock.Settings burnable() {
			this.burnable = true;
			return this;
		}

		public AbstractBlock.Settings liquid() {
			this.liquid = true;
			return this;
		}

		public AbstractBlock.Settings solid() {
			this.forceSolid = true;
			return this;
		}

		@Deprecated
		public AbstractBlock.Settings notSolid() {
			this.forceNotSolid = true;
			return this;
		}

		public AbstractBlock.Settings pistonBehavior(PistonBehavior pistonBehavior) {
			this.pistonBehavior = pistonBehavior;
			return this;
		}

		public AbstractBlock.Settings air() {
			this.isAir = true;
			return this;
		}

		/**
		 * Specifies logic that calculates whether an entity can spawn on a block.
		 * 
		 * @param predicate the predicate used to calculate whether an entity can spawn on this block
		 */
		public AbstractBlock.Settings allowsSpawning(AbstractBlock.TypedContextPredicate<EntityType<?>> predicate) {
			this.allowsSpawningPredicate = predicate;
			return this;
		}

		public AbstractBlock.Settings solidBlock(AbstractBlock.ContextPredicate predicate) {
			this.solidBlockPredicate = predicate;
			return this;
		}

		/**
		 * Specifies logic that calculates whether an entity should suffocate if inside of a block.
		 */
		public AbstractBlock.Settings suffocates(AbstractBlock.ContextPredicate predicate) {
			this.suffocationPredicate = predicate;
			return this;
		}

		public AbstractBlock.Settings blockVision(AbstractBlock.ContextPredicate predicate) {
			this.blockVisionPredicate = predicate;
			return this;
		}

		public AbstractBlock.Settings postProcess(AbstractBlock.ContextPredicate predicate) {
			this.postProcessPredicate = predicate;
			return this;
		}

		public AbstractBlock.Settings emissiveLighting(AbstractBlock.ContextPredicate predicate) {
			this.emissiveLightingPredicate = predicate;
			return this;
		}

		public AbstractBlock.Settings requiresTool() {
			this.toolRequired = true;
			return this;
		}

		public AbstractBlock.Settings hardness(float hardness) {
			this.hardness = hardness;
			return this;
		}

		public AbstractBlock.Settings resistance(float resistance) {
			this.resistance = Math.max(0.0F, resistance);
			return this;
		}

		public AbstractBlock.Settings offset(AbstractBlock.OffsetType offsetType) {
			this.offsetter = switch (offsetType) {
				case NONE -> null;
				case XZ -> (state, pos) -> {
				Block block = state.getBlock();
				long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
				float f = block.getMaxHorizontalModelOffset();
				double d = MathHelper.clamp(((double)((float)(l & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
				double e = MathHelper.clamp(((double)((float)(l >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
				return new Vec3d(d, 0.0, e);
			};
				case XYZ -> (state, pos) -> {
				Block block = state.getBlock();
				long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
				double d = ((double)((float)(l >> 4 & 15L) / 15.0F) - 1.0) * (double)block.getVerticalModelOffsetMultiplier();
				float f = block.getMaxHorizontalModelOffset();
				double e = MathHelper.clamp(((double)((float)(l & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
				double g = MathHelper.clamp(((double)((float)(l >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
				return new Vec3d(e, d, g);
			};
			};
			return this;
		}

		public AbstractBlock.Settings noBlockBreakParticles() {
			this.blockBreakParticles = false;
			return this;
		}

		public AbstractBlock.Settings requires(FeatureFlag... features) {
			this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(features);
			return this;
		}

		public AbstractBlock.Settings instrument(NoteBlockInstrument instrument) {
			this.instrument = instrument;
			return this;
		}

		public AbstractBlock.Settings replaceable() {
			this.replaceable = true;
			return this;
		}

		public AbstractBlock.Settings registryKey(RegistryKey<Block> registryKey) {
			this.registryKey = registryKey;
			return this;
		}

		public AbstractBlock.Settings overrideTranslationKey(String translationKey) {
			this.translationKey = RegistryKeyedValue.fixed(translationKey);
			return this;
		}

		protected String getTranslationKey() {
			return this.translationKey.get((RegistryKey<Block>)Objects.requireNonNull(this.registryKey, "Block id not set"));
		}
	}

	@FunctionalInterface
	public interface TypedContextPredicate<A> {
		boolean test(BlockState state, BlockView world, BlockPos pos, A type);
	}
}
