package net.minecraft.world;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagManager;
import net.minecraft.util.MaterialPredicate;
import net.minecraft.util.Tickable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class World implements ExtendedBlockView, EntityView, IWorld, AutoCloseable {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final Direction[] DIRECTIONS = Direction.values();
	public final List<BlockEntity> blockEntities = Lists.<BlockEntity>newArrayList();
	public final List<BlockEntity> tickingBlockEntities = Lists.<BlockEntity>newArrayList();
	protected final List<BlockEntity> pendingBlockEntities = Lists.<BlockEntity>newArrayList();
	public final List<PlayerEntity> players = Lists.<PlayerEntity>newArrayList();
	private final long unusedWhite = 16777215L;
	private final Thread thread;
	private int ambientDarkness;
	protected int lcgBlockSeed = new Random().nextInt();
	protected final int unusedIncrement = 1013904223;
	protected float rainGradientPrev;
	protected float rainGradient;
	protected float thunderGradientPrev;
	protected float thunderGradient;
	private int ticksSinceLightning;
	public final Random random = new Random();
	public final Dimension dimension;
	protected final ChunkManager chunkManager;
	protected final LevelProperties properties;
	protected WorldVillageManager villageManager;
	protected RaidManager raidManager;
	private final Profiler profiler;
	public final boolean isClient;
	protected boolean iteratingTickingBlockEntities;
	private final WorldBorder border;
	private final WanderingTraderManager wanderingTraderManager;

	protected World(
		LevelProperties levelProperties, DimensionType dimensionType, BiFunction<World, Dimension, ChunkManager> biFunction, Profiler profiler, boolean bl
	) {
		this.profiler = profiler;
		this.properties = levelProperties;
		this.dimension = dimensionType.create(this);
		this.chunkManager = (ChunkManager)biFunction.apply(this, this.dimension);
		this.isClient = bl;
		this.border = this.dimension.createWorldBorder();
		this.thread = Thread.currentThread();
		this.wanderingTraderManager = dimensionType == DimensionType.field_13072 ? new WanderingTraderManager(this) : null;
	}

	@Override
	public Biome getBiome(BlockPos blockPos) {
		ChunkManager chunkManager = this.getChunkManager();
		WorldChunk worldChunk = chunkManager.getWorldChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, false);
		if (worldChunk != null) {
			return worldChunk.getBiome(blockPos);
		} else {
			ChunkGenerator<?> chunkGenerator = this.getChunkManager().getChunkGenerator();
			return chunkGenerator == null ? Biomes.field_9451 : chunkGenerator.getBiomeSource().getBiome(blockPos);
		}
	}

	@Override
	public boolean isClient() {
		return this.isClient;
	}

	@Nullable
	public MinecraftServer getServer() {
		return null;
	}

	@Environment(EnvType.CLIENT)
	public void setDefaultSpawnClient() {
		this.setSpawnPos(new BlockPos(8, 64, 8));
	}

	public BlockState getTopNonAirState(BlockPos blockPos) {
		BlockPos blockPos2 = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ());

		while (!this.isAir(blockPos2.up())) {
			blockPos2 = blockPos2.up();
		}

		return this.getBlockState(blockPos2);
	}

	public static boolean isValid(BlockPos blockPos) {
		return !isHeightInvalid(blockPos) && blockPos.getX() >= -30000000 && blockPos.getZ() >= -30000000 && blockPos.getX() < 30000000 && blockPos.getZ() < 30000000;
	}

	public static boolean isHeightInvalid(BlockPos blockPos) {
		return isHeightInvalid(blockPos.getY());
	}

	public static boolean isHeightInvalid(int i) {
		return i < 0 || i >= 256;
	}

	public WorldChunk getWorldChunk(BlockPos blockPos) {
		return this.getWorldChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	public WorldChunk getWorldChunk(int i, int j) {
		return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL);
	}

	@Override
	public Chunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		Chunk chunk = this.chunkManager.getChunkSync(i, j, chunkStatus, bl);
		if (chunk == null && bl) {
			throw new IllegalStateException("Should always be able to create a chunk!");
		} else {
			return chunk;
		}
	}

	@Override
	public boolean setBlockState(BlockPos blockPos, BlockState blockState, int i) {
		if (isHeightInvalid(blockPos)) {
			return false;
		} else if (!this.isClient && this.properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			return false;
		} else {
			WorldChunk worldChunk = this.getWorldChunk(blockPos);
			Block block = blockState.getBlock();
			BlockState blockState2 = worldChunk.setBlockState(blockPos, blockState, (i & 64) != 0);
			if (blockState2 == null) {
				return false;
			} else {
				BlockState blockState3 = this.getBlockState(blockPos);
				if (blockState3 != blockState2
					&& (
						blockState3.getLightSubtracted(this, blockPos) != blockState2.getLightSubtracted(this, blockPos)
							|| blockState3.getLuminance() != blockState2.getLuminance()
							|| blockState3.method_16386()
							|| blockState2.method_16386()
					)) {
					this.profiler.push("queueCheckLight");
					this.getChunkManager().getLightingProvider().enqueueLightUpdate(blockPos);
					this.profiler.pop();
				}

				if (blockState3 == blockState) {
					if (blockState2 != blockState3) {
						this.scheduleBlockRender(blockPos);
					}

					if ((i & 2) != 0
						&& (!this.isClient || (i & 4) == 0)
						&& (this.isClient || worldChunk.getLevelType() != null && worldChunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
						this.updateListeners(blockPos, blockState2, blockState, i);
					}

					if (!this.isClient && (i & 1) != 0) {
						this.updateNeighbors(blockPos, blockState2.getBlock());
						if (blockState.hasComparatorOutput()) {
							this.updateHorizontalAdjacent(blockPos, block);
						}
					}

					if ((i & 16) == 0) {
						int j = i & -2;
						blockState2.method_11637(this, blockPos, j);
						blockState.updateNeighborStates(this, blockPos, j);
						blockState.method_11637(this, blockPos, j);
					}
				}

				return true;
			}
		}
	}

	@Override
	public boolean clearBlockState(BlockPos blockPos) {
		FluidState fluidState = this.getFluidState(blockPos);
		return this.setBlockState(blockPos, fluidState.getBlockState(), 3);
	}

	@Override
	public boolean breakBlock(BlockPos blockPos, boolean bl) {
		BlockState blockState = this.getBlockState(blockPos);
		if (blockState.isAir()) {
			return false;
		} else {
			FluidState fluidState = this.getFluidState(blockPos);
			this.playEvent(2001, blockPos, Block.getRawIdFromState(blockState));
			if (bl) {
				BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? this.getBlockEntity(blockPos) : null;
				Block.dropStacks(blockState, this, blockPos, blockEntity);
			}

			return this.setBlockState(blockPos, fluidState.getBlockState(), 3);
		}
	}

	public boolean setBlockState(BlockPos blockPos, BlockState blockState) {
		return this.setBlockState(blockPos, blockState, 3);
	}

	public abstract void updateListeners(BlockPos blockPos, BlockState blockState, BlockState blockState2, int i);

	@Override
	public void updateNeighbors(BlockPos blockPos, Block block) {
		if (this.properties.getGeneratorType() != LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.updateNeighborsAlways(blockPos, block);
		}
	}

	public void scheduleBlockRender(BlockPos blockPos) {
	}

	public void updateNeighborsAlways(BlockPos blockPos, Block block) {
		this.updateNeighbor(blockPos.west(), block, blockPos);
		this.updateNeighbor(blockPos.east(), block, blockPos);
		this.updateNeighbor(blockPos.down(), block, blockPos);
		this.updateNeighbor(blockPos.up(), block, blockPos);
		this.updateNeighbor(blockPos.north(), block, blockPos);
		this.updateNeighbor(blockPos.south(), block, blockPos);
	}

	public void updateNeighborsExcept(BlockPos blockPos, Block block, Direction direction) {
		if (direction != Direction.WEST) {
			this.updateNeighbor(blockPos.west(), block, blockPos);
		}

		if (direction != Direction.EAST) {
			this.updateNeighbor(blockPos.east(), block, blockPos);
		}

		if (direction != Direction.DOWN) {
			this.updateNeighbor(blockPos.down(), block, blockPos);
		}

		if (direction != Direction.UP) {
			this.updateNeighbor(blockPos.up(), block, blockPos);
		}

		if (direction != Direction.NORTH) {
			this.updateNeighbor(blockPos.north(), block, blockPos);
		}

		if (direction != Direction.SOUTH) {
			this.updateNeighbor(blockPos.south(), block, blockPos);
		}
	}

	public void updateNeighbor(BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!this.isClient) {
			BlockState blockState = this.getBlockState(blockPos);

			try {
				blockState.neighborUpdate(this, blockPos, block, blockPos2);
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Exception while updating neighbours");
				CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
				crashReportSection.add("Source block type", (ICrashCallable<String>)(() -> {
					try {
						return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(block), block.getTranslationKey(), block.getClass().getCanonicalName());
					} catch (Throwable var2) {
						return "ID #" + Registry.BLOCK.getId(block);
					}
				}));
				CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
				throw new CrashException(crashReport);
			}
		}
	}

	@Override
	public int getLightLevel(BlockPos blockPos, int i) {
		if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
			return 15;
		} else if (blockPos.getY() < 0) {
			return 0;
		} else {
			if (blockPos.getY() >= 256) {
				blockPos = new BlockPos(blockPos.getX(), 255, blockPos.getZ());
			}

			return this.getWorldChunk(blockPos).getLightLevel(blockPos, i);
		}
	}

	@Override
	public int getTop(Heightmap.Type type, int i, int j) {
		int k;
		if (i >= -30000000 && j >= -30000000 && i < 30000000 && j < 30000000) {
			if (this.isChunkLoaded(i >> 4, j >> 4)) {
				k = this.getWorldChunk(i >> 4, j >> 4).sampleHeightmap(type, i & 15, j & 15) + 1;
			} else {
				k = 0;
			}
		} else {
			k = this.getSeaLevel() + 1;
		}

		return k;
	}

	@Override
	public int getLightLevel(LightType lightType, BlockPos blockPos) {
		return this.getChunkManager().getLightingProvider().get(lightType).getLightLevel(blockPos);
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		if (isHeightInvalid(blockPos)) {
			return Blocks.field_10243.getDefaultState();
		} else {
			WorldChunk worldChunk = this.getWorldChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
			return worldChunk.getBlockState(blockPos);
		}
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		if (isHeightInvalid(blockPos)) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			WorldChunk worldChunk = this.getWorldChunk(blockPos);
			return worldChunk.getFluidState(blockPos);
		}
	}

	public boolean isDaylight() {
		return this.ambientDarkness < 4;
	}

	@Override
	public void playSound(@Nullable PlayerEntity playerEntity, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
		this.playSound(playerEntity, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, soundEvent, soundCategory, f, g);
	}

	public abstract void playSound(
		@Nullable PlayerEntity playerEntity, double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h
	);

	public abstract void playSoundFromEntity(
		@Nullable PlayerEntity playerEntity, Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g
	);

	public void playSound(double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h, boolean bl) {
	}

	@Override
	public void addParticle(ParticleParameters particleParameters, double d, double e, double f, double g, double h, double i) {
	}

	@Environment(EnvType.CLIENT)
	public void addParticle(ParticleParameters particleParameters, boolean bl, double d, double e, double f, double g, double h, double i) {
	}

	public void addImportantParticle(ParticleParameters particleParameters, double d, double e, double f, double g, double h, double i) {
	}

	public void addImportantParticle(ParticleParameters particleParameters, boolean bl, double d, double e, double f, double g, double h, double i) {
	}

	public int calculateAmbientDarkness(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0F) / 16.0));
		h = 1.0F - h;
		return (int)(h * 11.0F);
	}

	@Environment(EnvType.CLIENT)
	public float getAmbientLight(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.2F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0F) / 16.0));
		return h * 0.8F + 0.2F;
	}

	@Environment(EnvType.CLIENT)
	public Vec3d getSkyColor(Entity entity, float f) {
		float g = this.getSkyAngle(f);
		float h = MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		int i = MathHelper.floor(entity.x);
		int j = MathHelper.floor(entity.y);
		int k = MathHelper.floor(entity.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		Biome biome = this.getBiome(blockPos);
		float l = biome.getTemperature(blockPos);
		int m = biome.getSkyColor(l);
		float n = (float)(m >> 16 & 0xFF) / 255.0F;
		float o = (float)(m >> 8 & 0xFF) / 255.0F;
		float p = (float)(m & 0xFF) / 255.0F;
		n *= h;
		o *= h;
		p *= h;
		float q = this.getRainGradient(f);
		if (q > 0.0F) {
			float r = (n * 0.3F + o * 0.59F + p * 0.11F) * 0.6F;
			float s = 1.0F - q * 0.75F;
			n = n * s + r * (1.0F - s);
			o = o * s + r * (1.0F - s);
			p = p * s + r * (1.0F - s);
		}

		float r = this.getThunderGradient(f);
		if (r > 0.0F) {
			float s = (n * 0.3F + o * 0.59F + p * 0.11F) * 0.2F;
			float t = 1.0F - r * 0.75F;
			n = n * t + s * (1.0F - t);
			o = o * t + s * (1.0F - t);
			p = p * t + s * (1.0F - t);
		}

		if (this.ticksSinceLightning > 0) {
			float s = (float)this.ticksSinceLightning - f;
			if (s > 1.0F) {
				s = 1.0F;
			}

			s *= 0.45F;
			n = n * (1.0F - s) + 0.8F * s;
			o = o * (1.0F - s) + 0.8F * s;
			p = p * (1.0F - s) + 1.0F * s;
		}

		return new Vec3d((double)n, (double)o, (double)p);
	}

	public float method_8442(float f) {
		float g = this.getSkyAngle(f);
		return g * (float) (Math.PI * 2);
	}

	@Environment(EnvType.CLIENT)
	public Vec3d getCloudColor(float f) {
		float g = this.getSkyAngle(f);
		float h = MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		float i = 1.0F;
		float j = 1.0F;
		float k = 1.0F;
		float l = this.getRainGradient(f);
		if (l > 0.0F) {
			float m = (i * 0.3F + j * 0.59F + k * 0.11F) * 0.6F;
			float n = 1.0F - l * 0.95F;
			i = i * n + m * (1.0F - n);
			j = j * n + m * (1.0F - n);
			k = k * n + m * (1.0F - n);
		}

		i *= h * 0.9F + 0.1F;
		j *= h * 0.9F + 0.1F;
		k *= h * 0.85F + 0.15F;
		float m = this.getThunderGradient(f);
		if (m > 0.0F) {
			float n = (i * 0.3F + j * 0.59F + k * 0.11F) * 0.2F;
			float o = 1.0F - m * 0.95F;
			i = i * o + n * (1.0F - o);
			j = j * o + n * (1.0F - o);
			k = k * o + n * (1.0F - o);
		}

		return new Vec3d((double)i, (double)j, (double)k);
	}

	@Environment(EnvType.CLIENT)
	public Vec3d getFogColor(float f) {
		float g = this.getSkyAngle(f);
		return this.dimension.getFogColor(g, f);
	}

	@Environment(EnvType.CLIENT)
	public float getStarsBrightness(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.25F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		return h * h * 0.5F;
	}

	public boolean addBlockEntity(BlockEntity blockEntity) {
		boolean bl = this.blockEntities.add(blockEntity);
		if (bl && blockEntity instanceof Tickable) {
			this.tickingBlockEntities.add(blockEntity);
		}

		if (this.isClient) {
			BlockPos blockPos = blockEntity.getPos();
			BlockState blockState = this.getBlockState(blockPos);
			this.updateListeners(blockPos, blockState, blockState, 2);
		}

		return bl;
	}

	public void addBlockEntities(Collection<BlockEntity> collection) {
		if (this.iteratingTickingBlockEntities) {
			this.pendingBlockEntities.addAll(collection);
		} else {
			for (BlockEntity blockEntity : collection) {
				this.addBlockEntity(blockEntity);
			}
		}
	}

	public void tickEntity(Entity entity) {
		if (entity instanceof PlayerEntity || this.getChunkManager().isEntityInLoadedChunk(entity)) {
			entity.prevRenderX = entity.x;
			entity.prevRenderY = entity.y;
			entity.prevRenderZ = entity.z;
			entity.prevYaw = entity.yaw;
			entity.prevPitch = entity.pitch;
			if (entity.field_6016) {
				entity.age++;
				if (entity.hasVehicle()) {
					entity.updateRiding();
				} else {
					this.profiler.push((Supplier<String>)(() -> Registry.ENTITY_TYPE.getId(entity.getType()).toString()));
					entity.update();
					this.profiler.pop();
				}
			}

			this.updateChunkEntities(entity);
			if (entity.field_6016) {
				for (Entity entity2 : entity.getPassengerList()) {
					if (!entity2.invalid && entity2.getRiddenEntity() == entity) {
						this.tickEntity(entity2);
					} else {
						entity2.stopRiding();
					}
				}
			}
		}
	}

	public void updateChunkEntities(Entity entity) {
		this.profiler.push("chunkCheck");
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.y / 16.0);
		int k = MathHelper.floor(entity.z / 16.0);
		if (!entity.field_6016 || entity.chunkX != i || entity.chunkY != j || entity.chunkZ != k) {
			if (entity.field_6016 && this.isChunkLoaded(entity.chunkX, entity.chunkZ)) {
				this.getWorldChunk(entity.chunkX, entity.chunkZ).remove(entity, entity.chunkY);
			}

			if (!entity.method_5754() && !this.isChunkLoaded(i, k)) {
				entity.field_6016 = false;
			} else {
				this.getWorldChunk(i, k).addEntity(entity);
			}
		}

		this.profiler.pop();
	}

	@Override
	public boolean method_8611(@Nullable Entity entity, VoxelShape voxelShape) {
		return EntityView.super.method_8611(entity, voxelShape);
	}

	public boolean isAreaNotEmpty(BoundingBox boundingBox) {
		int i = MathHelper.floor(boundingBox.minX);
		int j = MathHelper.ceil(boundingBox.maxX);
		int k = MathHelper.floor(boundingBox.minY);
		int l = MathHelper.ceil(boundingBox.maxY);
		int m = MathHelper.floor(boundingBox.minZ);
		int n = MathHelper.ceil(boundingBox.maxZ);

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						BlockState blockState = this.getBlockState(pooledMutable.set(o, p, q));
						if (!blockState.isAir()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	public boolean doesAreaContainFireSource(BoundingBox boundingBox) {
		int i = MathHelper.floor(boundingBox.minX);
		int j = MathHelper.ceil(boundingBox.maxX);
		int k = MathHelper.floor(boundingBox.minY);
		int l = MathHelper.ceil(boundingBox.maxY);
		int m = MathHelper.floor(boundingBox.minZ);
		int n = MathHelper.ceil(boundingBox.maxZ);
		if (this.isAreaLoaded(i, k, m, j, l, n)) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int o = i; o < j; o++) {
					for (int p = k; p < l; p++) {
						for (int q = m; q < n; q++) {
							Block block = this.getBlockState(pooledMutable.set(o, p, q)).getBlock();
							if (block == Blocks.field_10036 || block == Blocks.field_10164) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	@Nullable
	public BlockState getBlockState(BoundingBox boundingBox, Block block) {
		int i = MathHelper.floor(boundingBox.minX);
		int j = MathHelper.ceil(boundingBox.maxX);
		int k = MathHelper.floor(boundingBox.minY);
		int l = MathHelper.ceil(boundingBox.maxY);
		int m = MathHelper.floor(boundingBox.minZ);
		int n = MathHelper.ceil(boundingBox.maxZ);
		if (this.isAreaLoaded(i, k, m, j, l, n)) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int o = i; o < j; o++) {
					for (int p = k; p < l; p++) {
						for (int q = m; q < n; q++) {
							BlockState blockState = this.getBlockState(pooledMutable.set(o, p, q));
							if (blockState.getBlock() == block) {
								return blockState;
							}
						}
					}
				}

				return null;
			}
		} else {
			return null;
		}
	}

	public boolean containsBlockWithMaterial(BoundingBox boundingBox, Material material) {
		int i = MathHelper.floor(boundingBox.minX);
		int j = MathHelper.ceil(boundingBox.maxX);
		int k = MathHelper.floor(boundingBox.minY);
		int l = MathHelper.ceil(boundingBox.maxY);
		int m = MathHelper.floor(boundingBox.minZ);
		int n = MathHelper.ceil(boundingBox.maxZ);
		MaterialPredicate materialPredicate = MaterialPredicate.create(material);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o < j; o++) {
			for (int p = k; p < l; p++) {
				for (int q = m; q < n; q++) {
					if (materialPredicate.method_11745(this.getBlockState(mutable.set(o, p, q)))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public Explosion createExplosion(@Nullable Entity entity, double d, double e, double f, float g, boolean bl) {
		return this.createExplosion(entity, null, d, e, f, g, false, bl);
	}

	public Explosion createExplosion(@Nullable Entity entity, double d, double e, double f, float g, boolean bl, boolean bl2) {
		return this.createExplosion(entity, null, d, e, f, g, bl, bl2);
	}

	public Explosion createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, double d, double e, double f, float g, boolean bl, boolean bl2) {
		Explosion explosion = new Explosion(this, entity, d, e, f, g, bl, bl2);
		if (damageSource != null) {
			explosion.setDamageSource(damageSource);
		}

		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(true);
		return explosion;
	}

	public boolean method_8506(@Nullable PlayerEntity playerEntity, BlockPos blockPos, Direction direction) {
		blockPos = blockPos.offset(direction);
		if (this.getBlockState(blockPos).getBlock() == Blocks.field_10036) {
			this.playEvent(playerEntity, 1009, blockPos, 0);
			this.clearBlockState(blockPos);
			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public String getChunkProviderStatus() {
		return this.chunkManager.getStatus();
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		if (isHeightInvalid(blockPos)) {
			return null;
		} else if (!this.isClient && Thread.currentThread() != this.thread) {
			return null;
		} else {
			BlockEntity blockEntity = null;
			if (this.iteratingTickingBlockEntities) {
				blockEntity = this.getPendingBlockEntity(blockPos);
			}

			if (blockEntity == null) {
				blockEntity = this.getWorldChunk(blockPos).getBlockEntity(blockPos, WorldChunk.CreationType.field_12860);
			}

			if (blockEntity == null) {
				blockEntity = this.getPendingBlockEntity(blockPos);
			}

			return blockEntity;
		}
	}

	@Nullable
	private BlockEntity getPendingBlockEntity(BlockPos blockPos) {
		for (int i = 0; i < this.pendingBlockEntities.size(); i++) {
			BlockEntity blockEntity = (BlockEntity)this.pendingBlockEntities.get(i);
			if (!blockEntity.isInvalid() && blockEntity.getPos().equals(blockPos)) {
				return blockEntity;
			}
		}

		return null;
	}

	public void setBlockEntity(BlockPos blockPos, @Nullable BlockEntity blockEntity) {
		if (!isHeightInvalid(blockPos)) {
			if (blockEntity != null && !blockEntity.isInvalid()) {
				if (this.iteratingTickingBlockEntities) {
					blockEntity.setPos(blockPos);
					Iterator<BlockEntity> iterator = this.pendingBlockEntities.iterator();

					while (iterator.hasNext()) {
						BlockEntity blockEntity2 = (BlockEntity)iterator.next();
						if (blockEntity2.getPos().equals(blockPos)) {
							blockEntity2.invalidate();
							iterator.remove();
						}
					}

					this.pendingBlockEntities.add(blockEntity);
				} else {
					this.getWorldChunk(blockPos).setBlockEntity(blockPos, blockEntity);
					this.addBlockEntity(blockEntity);
				}
			}
		}
	}

	public void removeBlockEntity(BlockPos blockPos) {
		BlockEntity blockEntity = this.getBlockEntity(blockPos);
		if (blockEntity != null && this.iteratingTickingBlockEntities) {
			blockEntity.invalidate();
			this.pendingBlockEntities.remove(blockEntity);
		} else {
			if (blockEntity != null) {
				this.pendingBlockEntities.remove(blockEntity);
				this.blockEntities.remove(blockEntity);
				this.tickingBlockEntities.remove(blockEntity);
			}

			this.getWorldChunk(blockPos).removeBlockEntity(blockPos);
		}
	}

	public boolean isHeightValidAndBlockLoaded(BlockPos blockPos) {
		return isHeightInvalid(blockPos) ? false : this.chunkManager.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	public boolean doesBlockHaveSolidTopSurface(BlockPos blockPos) {
		return this.isHeightValidAndBlockLoaded(blockPos) && this.getBlockState(blockPos).hasSolidTopSurface(this, blockPos);
	}

	public void updateAmbientDarkness() {
		int i = this.calculateAmbientDarkness(1.0F);
		if (i != this.ambientDarkness) {
			this.ambientDarkness = i;
		}
	}

	public void setMobSpawnOptions(boolean bl, boolean bl2) {
		this.getChunkManager().setMobSpawnOptions(bl, bl2);
	}

	public void tick(BooleanSupplier booleanSupplier) {
		this.border.update();
		if (!this.isClient() && this.wanderingTraderManager != null) {
			this.wanderingTraderManager.method_18015();
		}

		this.updateWeather();
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
		this.chunkManager.close();
	}

	protected void updateWeather() {
		if (this.dimension.hasSkyLight()) {
			if (!this.isClient) {
				boolean bl = this.getGameRules().getBoolean("doWeatherCycle");
				if (bl) {
					int i = this.properties.getClearWeatherTime();
					if (i > 0) {
						this.properties.setClearWeatherTime(--i);
						this.properties.setThunderTime(this.properties.isThundering() ? 1 : 2);
						this.properties.setRainTime(this.properties.isRaining() ? 1 : 2);
					}

					int j = this.properties.getThunderTime();
					if (j <= 0) {
						if (this.properties.isThundering()) {
							this.properties.setThunderTime(this.random.nextInt(12000) + 3600);
						} else {
							this.properties.setThunderTime(this.random.nextInt(168000) + 12000);
						}
					} else {
						this.properties.setThunderTime(--j);
						if (j <= 0) {
							this.properties.setThundering(!this.properties.isThundering());
						}
					}

					int k = this.properties.getRainTime();
					if (k <= 0) {
						if (this.properties.isRaining()) {
							this.properties.setRainTime(this.random.nextInt(12000) + 12000);
						} else {
							this.properties.setRainTime(this.random.nextInt(168000) + 12000);
						}
					} else {
						this.properties.setRainTime(--k);
						if (k <= 0) {
							this.properties.setRaining(!this.properties.isRaining());
						}
					}
				}

				this.thunderGradientPrev = this.thunderGradient;
				if (this.properties.isThundering()) {
					this.thunderGradient = (float)((double)this.thunderGradient + 0.01);
				} else {
					this.thunderGradient = (float)((double)this.thunderGradient - 0.01);
				}

				this.thunderGradient = MathHelper.clamp(this.thunderGradient, 0.0F, 1.0F);
				this.rainGradientPrev = this.rainGradient;
				if (this.properties.isRaining()) {
					this.rainGradient = (float)((double)this.rainGradient + 0.01);
				} else {
					this.rainGradient = (float)((double)this.rainGradient - 0.01);
				}

				this.rainGradient = MathHelper.clamp(this.rainGradient, 0.0F, 1.0F);
			}
		}
	}

	@Override
	public Stream<VoxelShape> getCollidingEntityBoundingBoxesForEntity(@Nullable Entity entity, VoxelShape voxelShape, Set<Entity> set) {
		return EntityView.super.getCollidingEntityBoundingBoxesForEntity(entity, voxelShape, set);
	}

	@Override
	public List<Entity> getEntities(@Nullable Entity entity, BoundingBox boundingBox, @Nullable Predicate<? super Entity> predicate) {
		List<Entity> list = Lists.<Entity>newArrayList();
		int i = MathHelper.floor((boundingBox.minX - 2.0) / 16.0);
		int j = MathHelper.floor((boundingBox.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((boundingBox.minZ - 2.0) / 16.0);
		int l = MathHelper.floor((boundingBox.maxZ + 2.0) / 16.0);

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				if (this.isChunkLoaded(m, n)) {
					this.getWorldChunk(m, n).appendEntities(entity, boundingBox, list, predicate);
				}
			}
		}

		return list;
	}

	public List<Entity> method_18023(@Nullable EntityType<?> entityType, BoundingBox boundingBox, Predicate<? super Entity> predicate) {
		int i = MathHelper.floor((boundingBox.minX - 2.0) / 16.0);
		int j = MathHelper.ceil((boundingBox.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((boundingBox.minZ - 2.0) / 16.0);
		int l = MathHelper.ceil((boundingBox.maxZ + 2.0) / 16.0);
		List<Entity> list = Lists.<Entity>newArrayList();

		for (int m = i; m < j; m++) {
			for (int n = k; n < l; n++) {
				WorldChunk worldChunk = this.getChunkManager().getWorldChunk(m, n, false);
				if (worldChunk != null) {
					worldChunk.method_18029(entityType, boundingBox, list, predicate);
				}
			}
		}

		return list;
	}

	public <T extends Entity> List<T> getPlayers(Class<? extends T> class_, Predicate<? super T> predicate) {
		List<T> list = Lists.<T>newArrayList();

		for (Entity entity : this.players) {
			if (class_.isAssignableFrom(entity.getClass()) && predicate.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	@Override
	public <T extends Entity> List<T> getEntities(Class<? extends T> class_, BoundingBox boundingBox, @Nullable Predicate<? super T> predicate) {
		int i = MathHelper.floor((boundingBox.minX - 2.0) / 16.0);
		int j = MathHelper.ceil((boundingBox.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((boundingBox.minZ - 2.0) / 16.0);
		int l = MathHelper.ceil((boundingBox.maxZ + 2.0) / 16.0);
		List<T> list = Lists.<T>newArrayList();

		for (int m = i; m < j; m++) {
			for (int n = k; n < l; n++) {
				WorldChunk worldChunk = this.getChunkManager().getWorldChunk(m, n, false);
				if (worldChunk != null) {
					worldChunk.appendEntities(class_, boundingBox, list, predicate);
				}
			}
		}

		return list;
	}

	@Nullable
	public <T extends Entity> T getClosestVisibleEntityTo(Class<? extends T> class_, BoundingBox boundingBox, T entity) {
		List<T> list = this.getVisibleEntities(class_, boundingBox);
		T entity2 = null;
		double d = Double.MAX_VALUE;

		for (int i = 0; i < list.size(); i++) {
			T entity3 = (T)list.get(i);
			if (entity3 != entity && EntityPredicates.EXCEPT_SPECTATOR.test(entity3)) {
				double e = entity.squaredDistanceTo(entity3);
				if (!(e > d)) {
					entity2 = entity3;
					d = e;
				}
			}
		}

		return entity2;
	}

	@Nullable
	public abstract Entity getEntityById(int i);

	public void markDirty(BlockPos blockPos, BlockEntity blockEntity) {
		if (this.isBlockLoaded(blockPos)) {
			this.getWorldChunk(blockPos).markDirty();
		}
	}

	@Override
	public int getSeaLevel() {
		return 63;
	}

	@Override
	public World getWorld() {
		return this;
	}

	@Override
	public int getEmittedStrongRedstonePower(BlockPos blockPos, Direction direction) {
		return this.getBlockState(blockPos).getStrongRedstonePower(this, blockPos, direction);
	}

	public LevelGeneratorType getGeneratorType() {
		return this.properties.getGeneratorType();
	}

	public int getReceivedStrongRedstonePower(BlockPos blockPos) {
		int i = 0;
		i = Math.max(i, this.getEmittedStrongRedstonePower(blockPos.down(), Direction.DOWN));
		if (i >= 15) {
			return i;
		} else {
			i = Math.max(i, this.getEmittedStrongRedstonePower(blockPos.up(), Direction.UP));
			if (i >= 15) {
				return i;
			} else {
				i = Math.max(i, this.getEmittedStrongRedstonePower(blockPos.north(), Direction.NORTH));
				if (i >= 15) {
					return i;
				} else {
					i = Math.max(i, this.getEmittedStrongRedstonePower(blockPos.south(), Direction.SOUTH));
					if (i >= 15) {
						return i;
					} else {
						i = Math.max(i, this.getEmittedStrongRedstonePower(blockPos.west(), Direction.WEST));
						if (i >= 15) {
							return i;
						} else {
							i = Math.max(i, this.getEmittedStrongRedstonePower(blockPos.east(), Direction.EAST));
							return i >= 15 ? i : i;
						}
					}
				}
			}
		}
	}

	public boolean isEmittingRedstonePower(BlockPos blockPos, Direction direction) {
		return this.getEmittedRedstonePower(blockPos, direction) > 0;
	}

	public int getEmittedRedstonePower(BlockPos blockPos, Direction direction) {
		BlockState blockState = this.getBlockState(blockPos);
		return blockState.isSimpleFullBlock(this, blockPos)
			? this.getReceivedStrongRedstonePower(blockPos)
			: blockState.getWeakRedstonePower(this, blockPos, direction);
	}

	public boolean isReceivingRedstonePower(BlockPos blockPos) {
		if (this.getEmittedRedstonePower(blockPos.down(), Direction.DOWN) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(blockPos.up(), Direction.UP) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(blockPos.north(), Direction.NORTH) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(blockPos.south(), Direction.SOUTH) > 0) {
			return true;
		} else {
			return this.getEmittedRedstonePower(blockPos.west(), Direction.WEST) > 0 ? true : this.getEmittedRedstonePower(blockPos.east(), Direction.EAST) > 0;
		}
	}

	public int getReceivedRedstonePower(BlockPos blockPos) {
		int i = 0;

		for (Direction direction : DIRECTIONS) {
			int j = this.getEmittedRedstonePower(blockPos.offset(direction), direction);
			if (j >= 15) {
				return 15;
			}

			if (j > i) {
				i = j;
			}
		}

		return i;
	}

	@Nullable
	@Override
	public PlayerEntity getClosestPlayer(double d, double e, double f, double g, Predicate<Entity> predicate) {
		double h = -1.0;
		PlayerEntity playerEntity = null;

		for (int i = 0; i < this.players.size(); i++) {
			PlayerEntity playerEntity2 = (PlayerEntity)this.players.get(i);
			if (predicate.test(playerEntity2)) {
				double j = playerEntity2.squaredDistanceTo(d, e, f);
				if ((g < 0.0 || j < g * g) && (h == -1.0 || j < h)) {
					h = j;
					playerEntity = playerEntity2;
				}
			}
		}

		return playerEntity;
	}

	public boolean containsVisiblePlayer(double d, double e, double f, double g) {
		for (int i = 0; i < this.players.size(); i++) {
			PlayerEntity playerEntity = (PlayerEntity)this.players.get(i);
			if (EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity)) {
				double h = playerEntity.squaredDistanceTo(d, e, f);
				if (g < 0.0 || h < g * g) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean findVisiblePlayer(double d, double e, double f, double g) {
		for (PlayerEntity playerEntity : this.players) {
			if (EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity) && EntityPredicates.VALID_ENTITY_LIVING.test(playerEntity)) {
				double h = playerEntity.squaredDistanceTo(d, e, f);
				if (g < 0.0 || h < g * g) {
					return true;
				}
			}
		}

		return false;
	}

	@Nullable
	public PlayerEntity findClosestVisiblePlayer(double d, double e, double f) {
		double g = -1.0;
		PlayerEntity playerEntity = null;

		for (int i = 0; i < this.players.size(); i++) {
			PlayerEntity playerEntity2 = (PlayerEntity)this.players.get(i);
			if (EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity2)) {
				double h = playerEntity2.squaredDistanceTo(d, playerEntity2.y, e);
				if ((f < 0.0 || h < f * f) && (g == -1.0 || h < g)) {
					g = h;
					playerEntity = playerEntity2;
				}
			}
		}

		return playerEntity;
	}

	@Nullable
	public PlayerEntity findMobAttackTarget(Entity entity, double d, double e) {
		return this.findMobAttackTarget(entity.x, entity.y, entity.z, d, e, null, null);
	}

	@Nullable
	public PlayerEntity findMobAttackTarget(BlockPos blockPos, double d, double e) {
		return this.findMobAttackTarget(
			(double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F), d, e, null, null
		);
	}

	@Nullable
	public PlayerEntity findMobAttackTarget(
		double d, double e, double f, double g, double h, @Nullable Function<PlayerEntity, Double> function, @Nullable Predicate<PlayerEntity> predicate
	) {
		double i = -1.0;
		PlayerEntity playerEntity = null;

		for (int j = 0; j < this.players.size(); j++) {
			PlayerEntity playerEntity2 = (PlayerEntity)this.players.get(j);
			if (!playerEntity2.abilities.invulnerable && playerEntity2.isValid() && !playerEntity2.isSpectator() && (predicate == null || predicate.test(playerEntity2))
				)
			 {
				double k = playerEntity2.squaredDistanceTo(d, playerEntity2.y, f);
				double l = g;
				if (playerEntity2.isSneaking()) {
					l = g * 0.8F;
				}

				if (playerEntity2.isInvisible()) {
					float m = playerEntity2.getWornArmorRatio();
					if (m < 0.1F) {
						m = 0.1F;
					}

					l *= (double)(0.7F * m);
				}

				if (function != null) {
					l *= MoreObjects.firstNonNull(function.apply(playerEntity2), 1.0);
				}

				if ((h < 0.0 || Math.abs(playerEntity2.y - e) < h * h) && (g < 0.0 || k < l * l) && (i == -1.0 || k < i)) {
					i = k;
					playerEntity = playerEntity2;
				}
			}
		}

		return playerEntity;
	}

	@Nullable
	public PlayerEntity getPlayerByName(String string) {
		for (int i = 0; i < this.players.size(); i++) {
			PlayerEntity playerEntity = (PlayerEntity)this.players.get(i);
			if (string.equals(playerEntity.getName().getString())) {
				return playerEntity;
			}
		}

		return null;
	}

	@Nullable
	public PlayerEntity getPlayerByUuid(UUID uUID) {
		for (int i = 0; i < this.players.size(); i++) {
			PlayerEntity playerEntity = (PlayerEntity)this.players.get(i);
			if (uUID.equals(playerEntity.getUuid())) {
				return playerEntity;
			}
		}

		return null;
	}

	@Nullable
	public PlayerEntity getRandomPlayer() {
		List<PlayerEntity> list = this.getPlayers(PlayerEntity.class, playerEntity -> playerEntity.isValid());
		return list.isEmpty() ? null : (PlayerEntity)list.get(this.random.nextInt(list.size()));
	}

	@Environment(EnvType.CLIENT)
	public void disconnect() {
	}

	public void setTime(long l) {
		this.properties.setTime(l);
	}

	@Override
	public long getSeed() {
		return this.properties.getSeed();
	}

	public long getTime() {
		return this.properties.getTime();
	}

	public long getTimeOfDay() {
		return this.properties.getTimeOfDay();
	}

	public void setTimeOfDay(long l) {
		this.properties.setTimeOfDay(l);
	}

	protected void tickTime() {
		this.setTime(this.properties.getTime() + 1L);
		if (this.properties.getGameRules().getBoolean("doDaylightCycle")) {
			this.setTimeOfDay(this.properties.getTimeOfDay() + 1L);
		}
	}

	@Override
	public BlockPos getSpawnPos() {
		BlockPos blockPos = new BlockPos(this.properties.getSpawnX(), this.properties.getSpawnY(), this.properties.getSpawnZ());
		if (!this.getWorldBorder().contains(blockPos)) {
			blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
		}

		return blockPos;
	}

	public void setSpawnPos(BlockPos blockPos) {
		this.properties.setSpawnPos(blockPos);
	}

	public boolean canPlayerModifyAt(PlayerEntity playerEntity, BlockPos blockPos) {
		return true;
	}

	public void summonParticle(Entity entity, byte b) {
	}

	@Override
	public ChunkManager getChunkManager() {
		return this.chunkManager;
	}

	public void addBlockAction(BlockPos blockPos, Block block, int i, int j) {
		this.getBlockState(blockPos).onBlockAction(this, blockPos, i, j);
	}

	@Override
	public LevelProperties getLevelProperties() {
		return this.properties;
	}

	public GameRules getGameRules() {
		return this.properties.getGameRules();
	}

	public float getThunderGradient(float f) {
		return MathHelper.lerp(f, this.thunderGradientPrev, this.thunderGradient) * this.getRainGradient(f);
	}

	@Environment(EnvType.CLIENT)
	public void setThunderGradient(float f) {
		this.thunderGradientPrev = f;
		this.thunderGradient = f;
	}

	public float getRainGradient(float f) {
		return MathHelper.lerp(f, this.rainGradientPrev, this.rainGradient);
	}

	@Environment(EnvType.CLIENT)
	public void setRainGradient(float f) {
		this.rainGradientPrev = f;
		this.rainGradient = f;
	}

	public boolean isThundering() {
		return this.dimension.hasSkyLight() && !this.dimension.isNether() ? (double)this.getThunderGradient(1.0F) > 0.9 : false;
	}

	public boolean isRaining() {
		return (double)this.getRainGradient(1.0F) > 0.2;
	}

	public boolean hasRain(BlockPos blockPos) {
		if (!this.isRaining()) {
			return false;
		} else if (!this.isSkyVisible(blockPos)) {
			return false;
		} else {
			return this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()
				? false
				: this.getBiome(blockPos).getPrecipitation() == Biome.Precipitation.RAIN;
		}
	}

	public boolean hasHighHumidity(BlockPos blockPos) {
		Biome biome = this.getBiome(blockPos);
		return biome.hasHighHumidity();
	}

	@Nullable
	public abstract MapState getMapState(String string);

	public abstract void putMapState(MapState mapState);

	public abstract int getNextMapId();

	public void playGlobalEvent(int i, BlockPos blockPos, int j) {
	}

	public void playEvent(int i, BlockPos blockPos, int j) {
		this.playEvent(null, i, blockPos, j);
	}

	public abstract void playEvent(@Nullable PlayerEntity playerEntity, int i, BlockPos blockPos, int j);

	public int getEffectiveHeight() {
		return this.dimension.isNether() ? 128 : 256;
	}

	@Environment(EnvType.CLIENT)
	public double getHorizonHeight() {
		return this.properties.getGeneratorType() == LevelGeneratorType.FLAT ? 0.0 : 63.0;
	}

	public CrashReportSection addDetailsToCrashReport(CrashReport crashReport) {
		CrashReportSection crashReportSection = crashReport.addElement("Affected level", 1);
		crashReportSection.add("Level name", this.properties == null ? "????" : this.properties.getLevelName());
		crashReportSection.add("All players", (ICrashCallable<String>)(() -> this.players.size() + " total; " + this.players));
		crashReportSection.add("Chunk stats", (ICrashCallable<String>)(() -> this.chunkManager.getStatus()));

		try {
			this.properties.populateCrashReport(crashReportSection);
		} catch (Throwable var4) {
			crashReportSection.add("Level Data Unobtainable", var4);
		}

		return crashReportSection;
	}

	public abstract void setBlockBreakingProgress(int i, BlockPos blockPos, int j);

	@Environment(EnvType.CLIENT)
	public void addFireworkParticle(double d, double e, double f, double g, double h, double i, @Nullable CompoundTag compoundTag) {
	}

	public abstract Scoreboard getScoreboard();

	public void updateHorizontalAdjacent(BlockPos blockPos, Block block) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			if (this.isBlockLoaded(blockPos2)) {
				BlockState blockState = this.getBlockState(blockPos2);
				if (blockState.getBlock() == Blocks.field_10377) {
					blockState.neighborUpdate(this, blockPos2, block, blockPos);
				} else if (blockState.isSimpleFullBlock(this, blockPos2)) {
					blockPos2 = blockPos2.offset(direction);
					blockState = this.getBlockState(blockPos2);
					if (blockState.getBlock() == Blocks.field_10377) {
						blockState.neighborUpdate(this, blockPos2, block, blockPos);
					}
				}
			}
		}
	}

	@Override
	public LocalDifficulty getLocalDifficulty(BlockPos blockPos) {
		long l = 0L;
		float f = 0.0F;
		if (this.isBlockLoaded(blockPos)) {
			f = this.method_8391();
			l = this.getWorldChunk(blockPos).getInhabitedTime();
		}

		return new LocalDifficulty(this.getDifficulty(), this.getTimeOfDay(), l, f);
	}

	@Override
	public int getAmbientDarkness() {
		return this.ambientDarkness;
	}

	public void setAmbientDarkness(int i) {
		this.ambientDarkness = i;
	}

	@Environment(EnvType.CLIENT)
	public int getTicksSinceLightning() {
		return this.ticksSinceLightning;
	}

	public void setTicksSinceLightning(int i) {
		this.ticksSinceLightning = i;
	}

	public WorldVillageManager getVillageManager() {
		return this.villageManager;
	}

	public RaidManager getRaidManager() {
		return this.raidManager;
	}

	@Override
	public WorldBorder getWorldBorder() {
		return this.border;
	}

	public void sendPacket(Packet<?> packet) {
		throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
	}

	@Nullable
	public BlockPos locateStructure(String string, BlockPos blockPos, int i, boolean bl) {
		return null;
	}

	@Override
	public Dimension getDimension() {
		return this.dimension;
	}

	@Override
	public Random getRandom() {
		return this.random;
	}

	@Override
	public boolean test(BlockPos blockPos, Predicate<BlockState> predicate) {
		return predicate.test(this.getBlockState(blockPos));
	}

	public abstract RecipeManager getRecipeManager();

	public abstract TagManager getTagManager();

	public BlockPos getRandomPosInChunk(int i, int j, int k, int l) {
		this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
		int m = this.lcgBlockSeed >> 2;
		return new BlockPos(i + (m & 15), j + (m >> 16 & l), k + (m >> 8 & 15));
	}

	public boolean isSavingDisabled() {
		return false;
	}

	public Profiler getProfiler() {
		return this.profiler;
	}

	@Override
	public BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos) {
		return new BlockPos(blockPos.getX(), this.getTop(type, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
	}
}
