package net.minecraft.world;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.block.ChainRestrictedNeighborUpdater;
import net.minecraft.world.block.NeighborUpdater;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public abstract class World implements WorldAccess, AutoCloseable {
	public static final Codec<RegistryKey<World>> CODEC = RegistryKey.createCodec(Registry.WORLD_KEY);
	public static final RegistryKey<World> OVERWORLD = RegistryKey.of(Registry.WORLD_KEY, new Identifier("overworld"));
	public static final RegistryKey<World> NETHER = RegistryKey.of(Registry.WORLD_KEY, new Identifier("the_nether"));
	public static final RegistryKey<World> END = RegistryKey.of(Registry.WORLD_KEY, new Identifier("the_end"));
	public static final int HORIZONTAL_LIMIT = 30000000;
	public static final int MAX_UPDATE_DEPTH = 512;
	public static final int field_30967 = 32;
	private static final Direction[] DIRECTIONS = Direction.values();
	public static final int field_30968 = 15;
	public static final int field_30969 = 24000;
	public static final int MAX_Y = 20000000;
	public static final int MIN_Y = -20000000;
	protected final List<BlockEntityTickInvoker> blockEntityTickers = Lists.<BlockEntityTickInvoker>newArrayList();
	protected final NeighborUpdater neighborUpdater;
	private final List<BlockEntityTickInvoker> pendingBlockEntityTickers = Lists.<BlockEntityTickInvoker>newArrayList();
	private boolean iteratingTickingBlockEntities;
	private final Thread thread;
	private final boolean debugWorld;
	private int ambientDarkness;
	protected int lcgBlockSeed = AbstractRandom.createAtomic().nextInt();
	protected final int lcgBlockSeedIncrement = 1013904223;
	protected float rainGradientPrev;
	protected float rainGradient;
	protected float thunderGradientPrev;
	protected float thunderGradient;
	public final AbstractRandom random = AbstractRandom.createAtomic();
	@Deprecated
	private final AbstractRandom blockingRandom = AbstractRandom.createBlocking();
	final DimensionType dimension;
	private final RegistryEntry<DimensionType> dimensionEntry;
	protected final MutableWorldProperties properties;
	private final Supplier<Profiler> profiler;
	public final boolean isClient;
	private final WorldBorder border;
	private final BiomeAccess biomeAccess;
	private final RegistryKey<World> registryKey;
	private long tickOrder;

	protected World(
		MutableWorldProperties properties,
		RegistryKey<World> registryRef,
		RegistryEntry<DimensionType> dimension,
		Supplier<Profiler> supplier,
		boolean isClient,
		boolean debugWorld,
		long seed,
		int maxChainedNeighborUpdates
	) {
		this.profiler = supplier;
		this.properties = properties;
		this.dimensionEntry = dimension;
		this.dimension = dimension.value();
		this.registryKey = registryRef;
		this.isClient = isClient;
		if (this.dimension.coordinateScale() != 1.0) {
			this.border = new WorldBorder() {
				@Override
				public double getCenterX() {
					return super.getCenterX() / World.this.dimension.coordinateScale();
				}

				@Override
				public double getCenterZ() {
					return super.getCenterZ() / World.this.dimension.coordinateScale();
				}
			};
		} else {
			this.border = new WorldBorder();
		}

		this.thread = Thread.currentThread();
		this.biomeAccess = new BiomeAccess(this, seed);
		this.debugWorld = debugWorld;
		this.neighborUpdater = new ChainRestrictedNeighborUpdater(this, maxChainedNeighborUpdates);
	}

	@Override
	public boolean isClient() {
		return this.isClient;
	}

	@Nullable
	@Override
	public MinecraftServer getServer() {
		return null;
	}

	/**
	 * {@return whether the position is inside the build limit}
	 * 
	 * @implNote In addition to the height limit, the position's X and Z
	 * coordinates must be greater than or equal to {@code -30_000_000}
	 * and less than {@code 30_000_000}.
	 * 
	 * @apiNote This method should be used for block placement. If the
	 * action involves a player interaction, additionally check for
	 * {@link #canPlayerModifyAt} (which checks the spawn protection and world border).
	 * 
	 * @see #isValid
	 * @see #canPlayerModifyAt
	 */
	public boolean isInBuildLimit(BlockPos pos) {
		return !this.isOutOfHeightLimit(pos) && isValidHorizontally(pos);
	}

	/**
	 * {@return whether the position is valid}
	 * 
	 * @implNote The position is considered valid if the X and Z
	 * coordinates are greater than or equal to {@code -30_000_000} and less than
	 * {@code 30_000_000}, and the Y coordinate is greater or equal to
	 * {@code -20_000_000} and less than {@code 20_000_000}.
	 * 
	 * @apiNote This method should be used for teleportation. To test for
	 * block positions, use {@link #isInBuildLimit} (which checks the height
	 * limit), and if the action involves a player interaction, additionally
	 * check for {@link #canPlayerModifyAt} (which checks the spawn protection and world border).
	 * 
	 * @see #isInBuildLimit
	 * @see #canPlayerModifyAt
	 */
	public static boolean isValid(BlockPos pos) {
		return !isInvalidVertically(pos.getY()) && isValidHorizontally(pos);
	}

	private static boolean isValidHorizontally(BlockPos pos) {
		return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000;
	}

	private static boolean isInvalidVertically(int y) {
		return y < -20000000 || y >= 20000000;
	}

	/**
	 * {@return the chunk that contains {@code pos}}
	 */
	public WorldChunk getWorldChunk(BlockPos pos) {
		return this.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
	}

	public WorldChunk getChunk(int i, int j) {
		return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL);
	}

	@Nullable
	@Override
	public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
		Chunk chunk = this.getChunkManager().getChunk(chunkX, chunkZ, leastStatus, create);
		if (chunk == null && create) {
			throw new IllegalStateException("Should always be able to create a chunk!");
		} else {
			return chunk;
		}
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
		return this.setBlockState(pos, state, flags, 512);
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
		if (this.isOutOfHeightLimit(pos)) {
			return false;
		} else if (!this.isClient && this.isDebugWorld()) {
			return false;
		} else {
			WorldChunk worldChunk = this.getWorldChunk(pos);
			Block block = state.getBlock();
			BlockState blockState = worldChunk.setBlockState(pos, state, (flags & Block.MOVED) != 0);
			if (blockState == null) {
				return false;
			} else {
				BlockState blockState2 = this.getBlockState(pos);
				if ((flags & Block.SKIP_LIGHTING_UPDATES) == 0
					&& blockState2 != blockState
					&& (
						blockState2.getOpacity(this, pos) != blockState.getOpacity(this, pos)
							|| blockState2.getLuminance() != blockState.getLuminance()
							|| blockState2.hasSidedTransparency()
							|| blockState.hasSidedTransparency()
					)) {
					this.getProfiler().push("queueCheckLight");
					this.getChunkManager().getLightingProvider().checkBlock(pos);
					this.getProfiler().pop();
				}

				if (blockState2 == state) {
					if (blockState != blockState2) {
						this.scheduleBlockRerenderIfNeeded(pos, blockState, blockState2);
					}

					if ((flags & Block.NOTIFY_LISTENERS) != 0
						&& (!this.isClient || (flags & Block.NO_REDRAW) == 0)
						&& (this.isClient || worldChunk.getLevelType() != null && worldChunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
						this.updateListeners(pos, blockState, state, flags);
					}

					if ((flags & Block.NOTIFY_NEIGHBORS) != 0) {
						this.updateNeighbors(pos, blockState.getBlock());
						if (!this.isClient && state.hasComparatorOutput()) {
							this.updateComparators(pos, block);
						}
					}

					if ((flags & Block.FORCE_STATE) == 0 && maxUpdateDepth > 0) {
						int i = flags & ~(Block.NOTIFY_NEIGHBORS | Block.SKIP_DROPS);
						blockState.prepare(this, pos, i, maxUpdateDepth - 1);
						state.updateNeighbors(this, pos, i, maxUpdateDepth - 1);
						state.prepare(this, pos, i, maxUpdateDepth - 1);
					}

					this.onBlockChanged(pos, blockState, blockState2);
				}

				return true;
			}
		}
	}

	/**
	 * Called when a block state changed.
	 * 
	 * @apiNote To implement logic for specific type of blocks, override
	 * {@link net.minecraft.block.AbstractBlock#onStateReplaced} instead.
	 */
	public void onBlockChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock) {
	}

	@Override
	public boolean removeBlock(BlockPos pos, boolean move) {
		FluidState fluidState = this.getFluidState(pos);
		return this.setBlockState(pos, fluidState.getBlockState(), Block.NOTIFY_ALL | (move ? Block.MOVED : 0));
	}

	@Override
	public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
		BlockState blockState = this.getBlockState(pos);
		if (blockState.isAir()) {
			return false;
		} else {
			FluidState fluidState = this.getFluidState(pos);
			if (!(blockState.getBlock() instanceof AbstractFireBlock)) {
				this.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(blockState));
			}

			if (drop) {
				BlockEntity blockEntity = blockState.hasBlockEntity() ? this.getBlockEntity(pos) : null;
				Block.dropStacks(blockState, this, pos, blockEntity, breakingEntity, ItemStack.EMPTY);
			}

			boolean bl = this.setBlockState(pos, fluidState.getBlockState(), Block.NOTIFY_ALL, maxUpdateDepth);
			if (bl) {
				this.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(breakingEntity, blockState));
			}

			return bl;
		}
	}

	public void addBlockBreakParticles(BlockPos pos, BlockState state) {
	}

	public boolean setBlockState(BlockPos pos, BlockState state) {
		return this.setBlockState(pos, state, Block.NOTIFY_ALL);
	}

	public abstract void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags);

	public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {
	}

	/**
	 * Emits a neighbor update to all 6 neighboring blocks of {@code pos}.
	 * 
	 * @see #updateNeighborsExcept(BlockPos, Block, Direction)
	 */
	public void updateNeighborsAlways(BlockPos pos, Block sourceBlock) {
	}

	/**
	 * Emits a neighbor update to neighboring blocks of {@code pos}, except
	 * for the one in {@code direction} direction.
	 * 
	 * @see #updateNeighborsAlways(BlockPos, Block)
	 */
	public void updateNeighborsExcept(BlockPos pos, Block sourceBlock, Direction direction) {
	}

	/**
	 * Triggers a neighbor update originating from {@code sourcePos} at
	 * {@code pos}.
	 * 
	 * @see #updateNeighborsAlways(BlockPos, Block)
	 */
	public void updateNeighbor(BlockPos pos, Block sourceBlock, BlockPos sourcePos) {
	}

	public void updateNeighbor(BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
	}

	@Override
	public void replaceWithStateForNeighborUpdate(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth) {
		this.neighborUpdater.replaceWithStateForNeighborUpdate(direction, neighborState, pos, neighborPos, flags, maxUpdateDepth);
	}

	@Override
	public int getTopY(Heightmap.Type heightmap, int x, int z) {
		int i;
		if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
			if (this.isChunkLoaded(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z))) {
				i = this.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z)).sampleHeightmap(heightmap, x & 15, z & 15) + 1;
			} else {
				i = this.getBottomY();
			}
		} else {
			i = this.getSeaLevel() + 1;
		}

		return i;
	}

	@Override
	public LightingProvider getLightingProvider() {
		return this.getChunkManager().getLightingProvider();
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		if (this.isOutOfHeightLimit(pos)) {
			return Blocks.VOID_AIR.getDefaultState();
		} else {
			WorldChunk worldChunk = this.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
			return worldChunk.getBlockState(pos);
		}
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		if (this.isOutOfHeightLimit(pos)) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			WorldChunk worldChunk = this.getWorldChunk(pos);
			return worldChunk.getFluidState(pos);
		}
	}

	public boolean isDay() {
		return !this.getDimension().hasFixedTime() && this.ambientDarkness < 4;
	}

	public boolean isNight() {
		return !this.getDimension().hasFixedTime() && !this.isDay();
	}

	@Override
	public void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
		this.playSound(player, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, sound, category, volume, pitch);
	}

	/**
	 * @param except the player that should not receive the sound, or {@code null}
	 */
	public abstract void playSound(
		@Nullable PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, long seed
	);

	/**
	 * @param except the player that should not receive the sound, or {@code null}
	 */
	public abstract void playSoundFromEntity(
		@Nullable PlayerEntity except, Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch, long seed
	);

	public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
		this.playSound(player, x, y, z, sound, category, volume, pitch, this.blockingRandom.nextLong());
	}

	public void playSoundFromEntity(@Nullable PlayerEntity player, Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
		this.playSoundFromEntity(player, entity, sound, category, volume, pitch, this.blockingRandom.nextLong());
	}

	public void playSound(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
	}

	@Override
	public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
	}

	public void addParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
	}

	public void addImportantParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
	}

	public void addImportantParticle(
		ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
	}

	public float getSkyAngleRadians(float tickDelta) {
		float f = this.getSkyAngle(tickDelta);
		return f * (float) (Math.PI * 2);
	}

	public void addBlockEntityTicker(BlockEntityTickInvoker ticker) {
		(this.iteratingTickingBlockEntities ? this.pendingBlockEntityTickers : this.blockEntityTickers).add(ticker);
	}

	protected void tickBlockEntities() {
		Profiler profiler = this.getProfiler();
		profiler.push("blockEntities");
		this.iteratingTickingBlockEntities = true;
		if (!this.pendingBlockEntityTickers.isEmpty()) {
			this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
			this.pendingBlockEntityTickers.clear();
		}

		Iterator<BlockEntityTickInvoker> iterator = this.blockEntityTickers.iterator();

		while (iterator.hasNext()) {
			BlockEntityTickInvoker blockEntityTickInvoker = (BlockEntityTickInvoker)iterator.next();
			if (blockEntityTickInvoker.isRemoved()) {
				iterator.remove();
			} else if (this.shouldTickBlockPos(blockEntityTickInvoker.getPos())) {
				blockEntityTickInvoker.tick();
			}
		}

		this.iteratingTickingBlockEntities = false;
		profiler.pop();
	}

	public <T extends Entity> void tickEntity(Consumer<T> tickConsumer, T entity) {
		try {
			tickConsumer.accept(entity);
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Ticking entity");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being ticked");
			entity.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public boolean shouldUpdatePostDeath(Entity entity) {
		return true;
	}

	/**
	 * {@return whether the blocks in the specified chunk should get ticked}
	 */
	public boolean shouldTickBlocksInChunk(long chunkPos) {
		return true;
	}

	public boolean shouldTickBlockPos(BlockPos pos) {
		return this.shouldTickBlocksInChunk(ChunkPos.toLong(pos));
	}

	/**
	 * Creates an explosion without creating fire.
	 * 
	 * @see #createExplosion(Entity, DamageSource, ExplosionBehavior, double, double, double, float, boolean, Explosion.DestructionType)
	 */
	public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, Explosion.DestructionType destructionType) {
		return this.createExplosion(entity, null, null, x, y, z, power, false, destructionType);
	}

	/**
	 * Creates an explosion.
	 * 
	 * @see #createExplosion(Entity, DamageSource, ExplosionBehavior, double, double, double, float, boolean, Explosion.DestructionType)
	 */
	public Explosion createExplosion(
		@Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType
	) {
		return this.createExplosion(entity, null, null, x, y, z, power, createFire, destructionType);
	}

	/**
	 * Creates an explosion.
	 * 
	 * @param entity the entity that exploded (like TNT) or {@code null} to indicate no entity exploded
	 * @param damageSource the custom damage source, or {@code null} to use the default
	 * ({@link DamageSource#explosion(Explosion)})
	 * @param behavior the explosion behavior, or {@code null} to use the default
	 * @param createFire whether the explosion should create fire
	 * @param destructionType the destruction type of the explosion
	 */
	public Explosion createExplosion(
		@Nullable Entity entity,
		@Nullable DamageSource damageSource,
		@Nullable ExplosionBehavior behavior,
		double x,
		double y,
		double z,
		float power,
		boolean createFire,
		Explosion.DestructionType destructionType
	) {
		Explosion explosion = new Explosion(this, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(true);
		return explosion;
	}

	public abstract String asString();

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		if (this.isOutOfHeightLimit(pos)) {
			return null;
		} else {
			return !this.isClient && Thread.currentThread() != this.thread ? null : this.getWorldChunk(pos).getBlockEntity(pos, WorldChunk.CreationType.IMMEDIATE);
		}
	}

	public void addBlockEntity(BlockEntity blockEntity) {
		BlockPos blockPos = blockEntity.getPos();
		if (!this.isOutOfHeightLimit(blockPos)) {
			this.getWorldChunk(blockPos).addBlockEntity(blockEntity);
		}
	}

	public void removeBlockEntity(BlockPos pos) {
		if (!this.isOutOfHeightLimit(pos)) {
			this.getWorldChunk(pos).removeBlockEntity(pos);
		}
	}

	public boolean canSetBlock(BlockPos pos) {
		return this.isOutOfHeightLimit(pos)
			? false
			: this.getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
	}

	public boolean isDirectionSolid(BlockPos pos, Entity entity, Direction direction) {
		if (this.isOutOfHeightLimit(pos)) {
			return false;
		} else {
			Chunk chunk = this.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
			return chunk == null ? false : chunk.getBlockState(pos).isSolidSurface(this, pos, entity, direction);
		}
	}

	public boolean isTopSolid(BlockPos pos, Entity entity) {
		return this.isDirectionSolid(pos, entity, Direction.UP);
	}

	public void calculateAmbientDarkness() {
		double d = 1.0 - (double)(this.getRainGradient(1.0F) * 5.0F) / 16.0;
		double e = 1.0 - (double)(this.getThunderGradient(1.0F) * 5.0F) / 16.0;
		double f = 0.5 + 2.0 * MathHelper.clamp((double)MathHelper.cos(this.getSkyAngle(1.0F) * (float) (Math.PI * 2)), -0.25, 0.25);
		this.ambientDarkness = (int)((1.0 - f * d * e) * 11.0);
	}

	/**
	 * Sets whether monsters or animals can spawn.
	 */
	public void setMobSpawnOptions(boolean spawnMonsters, boolean spawnAnimals) {
		this.getChunkManager().setMobSpawnOptions(spawnMonsters, spawnAnimals);
	}

	public BlockPos getSpawnPos() {
		BlockPos blockPos = new BlockPos(this.properties.getSpawnX(), this.properties.getSpawnY(), this.properties.getSpawnZ());
		if (!this.getWorldBorder().contains(blockPos)) {
			blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
		}

		return blockPos;
	}

	public float getSpawnAngle() {
		return this.properties.getSpawnAngle();
	}

	protected void initWeatherGradients() {
		if (this.properties.isRaining()) {
			this.rainGradient = 1.0F;
			if (this.properties.isThundering()) {
				this.thunderGradient = 1.0F;
			}
		}
	}

	public void close() throws IOException {
		this.getChunkManager().close();
	}

	@Nullable
	@Override
	public BlockView getChunkAsView(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
	}

	@Override
	public List<Entity> getOtherEntities(@Nullable Entity except, Box box, Predicate<? super Entity> predicate) {
		this.getProfiler().visit("getEntities");
		List<Entity> list = Lists.<Entity>newArrayList();
		this.getEntityLookup().forEachIntersects(box, entity -> {
			if (entity != except && predicate.test(entity)) {
				list.add(entity);
			}

			if (entity instanceof EnderDragonEntity) {
				for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).getBodyParts()) {
					if (entity != except && predicate.test(enderDragonPart)) {
						list.add(enderDragonPart);
					}
				}
			}
		});
		return list;
	}

	@Override
	public <T extends Entity> List<T> getEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate) {
		this.getProfiler().visit("getEntities");
		List<T> list = Lists.<T>newArrayList();
		this.getEntityLookup().forEachIntersects(filter, box, entity -> {
			if (predicate.test(entity)) {
				list.add(entity);
			}

			if (entity instanceof EnderDragonEntity enderDragonEntity) {
				for (EnderDragonPart enderDragonPart : enderDragonEntity.getBodyParts()) {
					T entity2 = filter.downcast(enderDragonPart);
					if (entity2 != null && predicate.test(entity2)) {
						list.add(entity2);
					}
				}
			}
		});
		return list;
	}

	/**
	 * {@return the entity using the entity ID, or {@code null} if none was found}
	 * 
	 * <p>Entity ID is ephemeral and changes after server restart. Use the UUID
	 * for persistent storage instead.
	 * 
	 * @see net.minecraft.server.world.ServerWorld#getEntity
	 */
	@Nullable
	public abstract Entity getEntityById(int id);

	public void markDirty(BlockPos pos) {
		if (this.isChunkLoaded(pos)) {
			this.getWorldChunk(pos).setNeedsSaving(true);
		}
	}

	@Override
	public int getSeaLevel() {
		return 63;
	}

	public int getReceivedStrongRedstonePower(BlockPos pos) {
		int i = 0;
		i = Math.max(i, this.getStrongRedstonePower(pos.down(), Direction.DOWN));
		if (i >= 15) {
			return i;
		} else {
			i = Math.max(i, this.getStrongRedstonePower(pos.up(), Direction.UP));
			if (i >= 15) {
				return i;
			} else {
				i = Math.max(i, this.getStrongRedstonePower(pos.north(), Direction.NORTH));
				if (i >= 15) {
					return i;
				} else {
					i = Math.max(i, this.getStrongRedstonePower(pos.south(), Direction.SOUTH));
					if (i >= 15) {
						return i;
					} else {
						i = Math.max(i, this.getStrongRedstonePower(pos.west(), Direction.WEST));
						if (i >= 15) {
							return i;
						} else {
							i = Math.max(i, this.getStrongRedstonePower(pos.east(), Direction.EAST));
							return i >= 15 ? i : i;
						}
					}
				}
			}
		}
	}

	public boolean isEmittingRedstonePower(BlockPos pos, Direction direction) {
		return this.getEmittedRedstonePower(pos, direction) > 0;
	}

	public int getEmittedRedstonePower(BlockPos pos, Direction direction) {
		BlockState blockState = this.getBlockState(pos);
		int i = blockState.getWeakRedstonePower(this, pos, direction);
		return blockState.isSolidBlock(this, pos) ? Math.max(i, this.getReceivedStrongRedstonePower(pos)) : i;
	}

	public boolean isReceivingRedstonePower(BlockPos pos) {
		if (this.getEmittedRedstonePower(pos.down(), Direction.DOWN) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(pos.up(), Direction.UP) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(pos.north(), Direction.NORTH) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(pos.south(), Direction.SOUTH) > 0) {
			return true;
		} else {
			return this.getEmittedRedstonePower(pos.west(), Direction.WEST) > 0 ? true : this.getEmittedRedstonePower(pos.east(), Direction.EAST) > 0;
		}
	}

	public int getReceivedRedstonePower(BlockPos pos) {
		int i = 0;

		for (Direction direction : DIRECTIONS) {
			int j = this.getEmittedRedstonePower(pos.offset(direction), direction);
			if (j >= 15) {
				return 15;
			}

			if (j > i) {
				i = j;
			}
		}

		return i;
	}

	public void disconnect() {
	}

	/**
	 * {@return the time}
	 * 
	 * <p>Time is used to track scheduled ticks and cannot be modified or frozen.
	 * 
	 * @see WorldProperties#getTime
	 */
	public long getTime() {
		return this.properties.getTime();
	}

	/**
	 * {@return the time of day}
	 * 
	 * <p>Time of day is different to "time", which is incremented on every tick and
	 * cannot be modified; Time of day affects the day-night cycle, can be changed using
	 * {@link net.minecraft.server.command.TimeCommand /time command}, and can be frozen
	 * if {@link GameRules#DO_DAYLIGHT_CYCLE doDaylightCycle} gamerule is turned off.
	 * Time is used to track scheduled ticks and cannot be modified or frozen.
	 * 
	 * @see WorldProperties#getTimeOfDay
	 * @see net.minecraft.server.world.ServerWorld#setTimeOfDay
	 */
	public long getTimeOfDay() {
		return this.properties.getTimeOfDay();
	}

	/**
	 * {@return whether {@code player} can modify blocks at {@code pos}}
	 * 
	 * @implNote This checks the spawn protection and the world border.
	 * 
	 * @see #isInBuildLimit
	 * @see #isValid
	 */
	public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
		return true;
	}

	/**
	 * Sends the entity status to nearby players.
	 * 
	 * @see net.minecraft.entity.EntityStatuses
	 */
	public void sendEntityStatus(Entity entity, byte status) {
	}

	public void addSyncedBlockEvent(BlockPos pos, Block block, int type, int data) {
		this.getBlockState(pos).onSyncedBlockEvent(this, pos, type, data);
	}

	@Override
	public WorldProperties getLevelProperties() {
		return this.properties;
	}

	public GameRules getGameRules() {
		return this.properties.getGameRules();
	}

	public float getThunderGradient(float delta) {
		return MathHelper.lerp(delta, this.thunderGradientPrev, this.thunderGradient) * this.getRainGradient(delta);
	}

	public void setThunderGradient(float thunderGradient) {
		float f = MathHelper.clamp(thunderGradient, 0.0F, 1.0F);
		this.thunderGradientPrev = f;
		this.thunderGradient = f;
	}

	public float getRainGradient(float delta) {
		return MathHelper.lerp(delta, this.rainGradientPrev, this.rainGradient);
	}

	public void setRainGradient(float rainGradient) {
		float f = MathHelper.clamp(rainGradient, 0.0F, 1.0F);
		this.rainGradientPrev = f;
		this.rainGradient = f;
	}

	public boolean isThundering() {
		return this.getDimension().hasSkyLight() && !this.getDimension().hasCeiling() ? (double)this.getThunderGradient(1.0F) > 0.9 : false;
	}

	/**
	 * {@return whether it is raining}
	 * 
	 * @see #hasRain
	 */
	public boolean isRaining() {
		return (double)this.getRainGradient(1.0F) > 0.2;
	}

	/**
	 * {@return whether it can rain at {@code pos}}
	 * 
	 * @implNote This returns {@code true} if a rain is ongoing, the biome
	 * and the position allows it to rain, and there are no blocks above the position.
	 * 
	 * @see #isRaining
	 */
	public boolean hasRain(BlockPos pos) {
		if (!this.isRaining()) {
			return false;
		} else if (!this.isSkyVisible(pos)) {
			return false;
		} else if (this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
			return false;
		} else {
			Biome biome = this.getBiome(pos).value();
			return biome.getPrecipitation() == Biome.Precipitation.RAIN && biome.doesNotSnow(pos);
		}
	}

	/**
	 * {@return whether the biome at {@code pos} has high humidity}
	 * 
	 * <p>Humidity affects the chance of fire spreading.
	 */
	public boolean hasHighHumidity(BlockPos pos) {
		Biome biome = this.getBiome(pos).value();
		return biome.hasHighHumidity();
	}

	@Nullable
	public abstract MapState getMapState(String id);

	public abstract void putMapState(String id, MapState state);

	public abstract int getNextMapId();

	public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
	}

	public CrashReportSection addDetailsToCrashReport(CrashReport report) {
		CrashReportSection crashReportSection = report.addElement("Affected level", 1);
		crashReportSection.add("All players", (CrashCallable<String>)(() -> this.getPlayers().size() + " total; " + this.getPlayers()));
		crashReportSection.add("Chunk stats", this.getChunkManager()::getDebugString);
		crashReportSection.add("Level dimension", (CrashCallable<String>)(() -> this.getRegistryKey().getValue().toString()));

		try {
			this.properties.populateCrashReport(crashReportSection, this);
		} catch (Throwable var4) {
			crashReportSection.add("Level Data Unobtainable", var4);
		}

		return crashReportSection;
	}

	public abstract void setBlockBreakingInfo(int entityId, BlockPos pos, int progress);

	public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Nullable NbtCompound nbt) {
	}

	public abstract Scoreboard getScoreboard();

	public void updateComparators(BlockPos pos, Block block) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			if (this.isChunkLoaded(blockPos)) {
				BlockState blockState = this.getBlockState(blockPos);
				if (blockState.isOf(Blocks.COMPARATOR)) {
					this.updateNeighbor(blockState, blockPos, block, pos, false);
				} else if (blockState.isSolidBlock(this, blockPos)) {
					blockPos = blockPos.offset(direction);
					blockState = this.getBlockState(blockPos);
					if (blockState.isOf(Blocks.COMPARATOR)) {
						this.updateNeighbor(blockState, blockPos, block, pos, false);
					}
				}
			}
		}
	}

	@Override
	public LocalDifficulty getLocalDifficulty(BlockPos pos) {
		long l = 0L;
		float f = 0.0F;
		if (this.isChunkLoaded(pos)) {
			f = this.getMoonSize();
			l = this.getWorldChunk(pos).getInhabitedTime();
		}

		return new LocalDifficulty(this.getDifficulty(), this.getTimeOfDay(), l, f);
	}

	@Override
	public int getAmbientDarkness() {
		return this.ambientDarkness;
	}

	public void setLightningTicksLeft(int lightningTicksLeft) {
	}

	@Override
	public WorldBorder getWorldBorder() {
		return this.border;
	}

	public void sendPacket(Packet<?> packet) {
		throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
	}

	@Override
	public DimensionType getDimension() {
		return this.dimension;
	}

	public RegistryEntry<DimensionType> getDimensionEntry() {
		return this.dimensionEntry;
	}

	public RegistryKey<World> getRegistryKey() {
		return this.registryKey;
	}

	@Override
	public AbstractRandom getRandom() {
		return this.random;
	}

	@Override
	public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
		return state.test(this.getBlockState(pos));
	}

	@Override
	public boolean testFluidState(BlockPos pos, Predicate<FluidState> state) {
		return state.test(this.getFluidState(pos));
	}

	public abstract RecipeManager getRecipeManager();

	public BlockPos getRandomPosInChunk(int x, int y, int z, int i) {
		this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
		int j = this.lcgBlockSeed >> 2;
		return new BlockPos(x + (j & 15), y + (j >> 16 & i), z + (j >> 8 & 15));
	}

	public boolean isSavingDisabled() {
		return false;
	}

	public Profiler getProfiler() {
		return (Profiler)this.profiler.get();
	}

	public Supplier<Profiler> getProfilerSupplier() {
		return this.profiler;
	}

	@Override
	public BiomeAccess getBiomeAccess() {
		return this.biomeAccess;
	}

	/**
	 * Checks if this world is a debug world.
	 * 
	 * <p>Debug worlds are not modifiable. They have a barrier layer at y=60,
	 * and lists all possible block states in game at y=70, helpful for finding
	 * block rendering and model errors.
	 * 
	 * @see net.minecraft.world.gen.chunk.DebugChunkGenerator
	 */
	public final boolean isDebugWorld() {
		return this.debugWorld;
	}

	protected abstract EntityLookup<Entity> getEntityLookup();

	@Override
	public long getTickOrder() {
		return this.tickOrder++;
	}
}
