package net.minecraft.world;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleParameters;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
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

public abstract class World implements ExtendedBlockView, IWorld, AutoCloseable {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final Direction[] field_9233 = Direction.values();
	public final List<BlockEntity> blockEntities = Lists.<BlockEntity>newArrayList();
	public final List<BlockEntity> tickingBlockEntities = Lists.<BlockEntity>newArrayList();
	protected final List<BlockEntity> pendingBlockEntities = Lists.<BlockEntity>newArrayList();
	protected final List<BlockEntity> field_18139 = Lists.<BlockEntity>newArrayList();
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
	public final Dimension field_9247;
	protected final ChunkManager field_9248;
	protected final LevelProperties field_9232;
	private final Profiler profiler;
	public final boolean isClient;
	protected boolean iteratingTickingBlockEntities;
	private final WorldBorder field_9223;

	protected World(
		LevelProperties levelProperties, DimensionType dimensionType, BiFunction<World, Dimension, ChunkManager> biFunction, Profiler profiler, boolean bl
	) {
		this.profiler = profiler;
		this.field_9232 = levelProperties;
		this.field_9247 = dimensionType.create(this);
		this.field_9248 = (ChunkManager)biFunction.apply(this, this.field_9247);
		this.isClient = bl;
		this.field_9223 = this.field_9247.createWorldBorder();
		this.thread = Thread.currentThread();
	}

	@Override
	public Biome method_8310(BlockPos blockPos) {
		ChunkManager chunkManager = this.method_8398();
		WorldChunk worldChunk = chunkManager.method_12126(blockPos.getX() >> 4, blockPos.getZ() >> 4, false);
		if (worldChunk != null) {
			return worldChunk.method_16552(blockPos);
		} else {
			ChunkGenerator<?> chunkGenerator = this.method_8398().getChunkGenerator();
			return chunkGenerator == null ? Biomes.field_9451 : chunkGenerator.getBiomeSource().method_8758(blockPos);
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
		this.method_8554(new BlockPos(8, 64, 8));
	}

	public BlockState method_8495(BlockPos blockPos) {
		BlockPos blockPos2 = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ());

		while (!this.method_8623(blockPos2.up())) {
			blockPos2 = blockPos2.up();
		}

		return this.method_8320(blockPos2);
	}

	public static boolean method_8558(BlockPos blockPos) {
		return !method_8518(blockPos) && blockPos.getX() >= -30000000 && blockPos.getZ() >= -30000000 && blockPos.getX() < 30000000 && blockPos.getZ() < 30000000;
	}

	public static boolean method_8518(BlockPos blockPos) {
		return isHeightInvalid(blockPos.getY());
	}

	public static boolean isHeightInvalid(int i) {
		return i < 0 || i >= 256;
	}

	public WorldChunk method_8500(BlockPos blockPos) {
		return this.method_8497(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	public WorldChunk method_8497(int i, int j) {
		return (WorldChunk)this.method_16956(i, j, ChunkStatus.FULL);
	}

	@Override
	public Chunk method_8402(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		Chunk chunk = this.field_9248.method_12121(i, j, chunkStatus, bl);
		if (chunk == null && bl) {
			throw new IllegalStateException("Should always be able to create a chunk!");
		} else {
			return chunk;
		}
	}

	@Override
	public boolean method_8652(BlockPos blockPos, BlockState blockState, int i) {
		if (method_8518(blockPos)) {
			return false;
		} else if (!this.isClient && this.field_9232.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			return false;
		} else {
			WorldChunk worldChunk = this.method_8500(blockPos);
			Block block = blockState.getBlock();
			BlockState blockState2 = worldChunk.method_12010(blockPos, blockState, (i & 64) != 0);
			if (blockState2 == null) {
				return false;
			} else {
				BlockState blockState3 = this.method_8320(blockPos);
				if (blockState3 != blockState2
					&& (
						blockState3.method_11581(this, blockPos) != blockState2.method_11581(this, blockPos)
							|| blockState3.getLuminance() != blockState2.getLuminance()
							|| blockState3.method_16386()
							|| blockState2.method_16386()
					)) {
					this.profiler.push("queueCheckLight");
					this.method_8398().method_12130().method_15559(blockPos);
					this.profiler.pop();
				}

				if (blockState3 == blockState) {
					if (blockState2 != blockState3) {
						this.method_16109(blockPos);
					}

					if ((i & 2) != 0
						&& (!this.isClient || (i & 4) == 0)
						&& (this.isClient || worldChunk.method_12225() != null && worldChunk.method_12225().isAfter(ChunkHolder.LevelType.TICKING))) {
						this.method_8413(blockPos, blockState2, blockState, i);
					}

					if (!this.isClient && (i & 1) != 0) {
						this.method_8408(blockPos, blockState2.getBlock());
						if (blockState.hasComparatorOutput()) {
							this.method_8455(blockPos, block);
						}
					}

					if ((i & 16) == 0) {
						int j = i & -2;
						blockState2.method_11637(this, blockPos, j);
						blockState.method_11635(this, blockPos, j);
						blockState.method_11637(this, blockPos, j);
					}

					this.method_19282(blockPos, blockState2, blockState3);
				}

				return true;
			}
		}
	}

	public void method_19282(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
	}

	@Override
	public boolean method_8650(BlockPos blockPos) {
		FluidState fluidState = this.method_8316(blockPos);
		return this.method_8652(blockPos, fluidState.getBlockState(), 3);
	}

	@Override
	public boolean method_8651(BlockPos blockPos, boolean bl) {
		BlockState blockState = this.method_8320(blockPos);
		if (blockState.isAir()) {
			return false;
		} else {
			FluidState fluidState = this.method_8316(blockPos);
			this.method_8535(2001, blockPos, Block.method_9507(blockState));
			if (bl) {
				BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? this.method_8321(blockPos) : null;
				Block.method_9610(blockState, this, blockPos, blockEntity);
			}

			return this.method_8652(blockPos, fluidState.getBlockState(), 3);
		}
	}

	public boolean method_8501(BlockPos blockPos, BlockState blockState) {
		return this.method_8652(blockPos, blockState, 3);
	}

	public abstract void method_8413(BlockPos blockPos, BlockState blockState, BlockState blockState2, int i);

	@Override
	public void method_8408(BlockPos blockPos, Block block) {
		if (this.field_9232.getGeneratorType() != LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.method_8452(blockPos, block);
		}
	}

	public void method_16109(BlockPos blockPos) {
	}

	public void method_8452(BlockPos blockPos, Block block) {
		this.method_8492(blockPos.west(), block, blockPos);
		this.method_8492(blockPos.east(), block, blockPos);
		this.method_8492(blockPos.down(), block, blockPos);
		this.method_8492(blockPos.up(), block, blockPos);
		this.method_8492(blockPos.north(), block, blockPos);
		this.method_8492(blockPos.south(), block, blockPos);
	}

	public void method_8508(BlockPos blockPos, Block block, Direction direction) {
		if (direction != Direction.WEST) {
			this.method_8492(blockPos.west(), block, blockPos);
		}

		if (direction != Direction.EAST) {
			this.method_8492(blockPos.east(), block, blockPos);
		}

		if (direction != Direction.DOWN) {
			this.method_8492(blockPos.down(), block, blockPos);
		}

		if (direction != Direction.UP) {
			this.method_8492(blockPos.up(), block, blockPos);
		}

		if (direction != Direction.NORTH) {
			this.method_8492(blockPos.north(), block, blockPos);
		}

		if (direction != Direction.SOUTH) {
			this.method_8492(blockPos.south(), block, blockPos);
		}
	}

	public void method_8492(BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!this.isClient) {
			BlockState blockState = this.method_8320(blockPos);

			try {
				blockState.method_11622(this, blockPos, block, blockPos2);
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Exception while updating neighbours");
				CrashReportSection crashReportSection = crashReport.method_562("Block being updated");
				crashReportSection.method_577("Source block type", () -> {
					try {
						return String.format("ID #%s (%s // %s)", Registry.BLOCK.method_10221(block), block.getTranslationKey(), block.getClass().getCanonicalName());
					} catch (Throwable var2) {
						return "ID #" + Registry.BLOCK.method_10221(block);
					}
				});
				CrashReportSection.method_586(crashReportSection, blockPos, blockState);
				throw new CrashException(crashReport);
			}
		}
	}

	@Override
	public int method_8624(BlockPos blockPos, int i) {
		if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
			return 15;
		} else if (blockPos.getY() < 0) {
			return 0;
		} else {
			if (blockPos.getY() >= 256) {
				blockPos = new BlockPos(blockPos.getX(), 255, blockPos.getZ());
			}

			return this.method_8500(blockPos).method_12233(blockPos, i);
		}
	}

	@Override
	public int method_8589(Heightmap.Type type, int i, int j) {
		int k;
		if (i >= -30000000 && j >= -30000000 && i < 30000000 && j < 30000000) {
			if (this.isChunkLoaded(i >> 4, j >> 4)) {
				k = this.method_8497(i >> 4, j >> 4).method_12005(type, i & 15, j & 15) + 1;
			} else {
				k = 0;
			}
		} else {
			k = this.getSeaLevel() + 1;
		}

		return k;
	}

	@Override
	public int method_8314(LightType lightType, BlockPos blockPos) {
		return this.method_8398().method_12130().get(lightType).method_15543(blockPos);
	}

	@Override
	public BlockState method_8320(BlockPos blockPos) {
		if (method_8518(blockPos)) {
			return Blocks.field_10243.method_9564();
		} else {
			WorldChunk worldChunk = this.method_8497(blockPos.getX() >> 4, blockPos.getZ() >> 4);
			return worldChunk.method_8320(blockPos);
		}
	}

	@Override
	public FluidState method_8316(BlockPos blockPos) {
		if (method_8518(blockPos)) {
			return Fluids.EMPTY.method_15785();
		} else {
			WorldChunk worldChunk = this.method_8500(blockPos);
			return worldChunk.method_8316(blockPos);
		}
	}

	public boolean isDaylight() {
		return this.ambientDarkness < 4;
	}

	@Override
	public void method_8396(@Nullable PlayerEntity playerEntity, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
		this.method_8465(playerEntity, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, soundEvent, soundCategory, f, g);
	}

	public abstract void method_8465(
		@Nullable PlayerEntity playerEntity, double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h
	);

	public abstract void method_8449(@Nullable PlayerEntity playerEntity, Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g);

	public void method_8486(double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h, boolean bl) {
	}

	@Override
	public void method_8406(ParticleParameters particleParameters, double d, double e, double f, double g, double h, double i) {
	}

	@Environment(EnvType.CLIENT)
	public void method_8466(ParticleParameters particleParameters, boolean bl, double d, double e, double f, double g, double h, double i) {
	}

	public void method_8494(ParticleParameters particleParameters, double d, double e, double f, double g, double h, double i) {
	}

	public void method_17452(ParticleParameters particleParameters, boolean bl, double d, double e, double f, double g, double h, double i) {
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
	public Vec3d method_8548(BlockPos blockPos, float f) {
		float g = this.getSkyAngle(f);
		float h = MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		Biome biome = this.method_8310(blockPos);
		float i = biome.method_8707(blockPos);
		int j = biome.getSkyColor(i);
		float k = (float)(j >> 16 & 0xFF) / 255.0F;
		float l = (float)(j >> 8 & 0xFF) / 255.0F;
		float m = (float)(j & 0xFF) / 255.0F;
		k *= h;
		l *= h;
		m *= h;
		float n = this.getRainGradient(f);
		if (n > 0.0F) {
			float o = (k * 0.3F + l * 0.59F + m * 0.11F) * 0.6F;
			float p = 1.0F - n * 0.75F;
			k = k * p + o * (1.0F - p);
			l = l * p + o * (1.0F - p);
			m = m * p + o * (1.0F - p);
		}

		float o = this.getThunderGradient(f);
		if (o > 0.0F) {
			float p = (k * 0.3F + l * 0.59F + m * 0.11F) * 0.2F;
			float q = 1.0F - o * 0.75F;
			k = k * q + p * (1.0F - q);
			l = l * q + p * (1.0F - q);
			m = m * q + p * (1.0F - q);
		}

		if (this.ticksSinceLightning > 0) {
			float p = (float)this.ticksSinceLightning - f;
			if (p > 1.0F) {
				p = 1.0F;
			}

			p *= 0.45F;
			k = k * (1.0F - p) + 0.8F * p;
			l = l * (1.0F - p) + 0.8F * p;
			m = m * (1.0F - p) + 1.0F * p;
		}

		return new Vec3d((double)k, (double)l, (double)m);
	}

	public float method_8442(float f) {
		float g = this.getSkyAngle(f);
		return g * (float) (Math.PI * 2);
	}

	@Environment(EnvType.CLIENT)
	public Vec3d method_8423(float f) {
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
	public Vec3d method_8464(float f) {
		float g = this.getSkyAngle(f);
		return this.field_9247.method_12445(g, f);
	}

	@Environment(EnvType.CLIENT)
	public float getStarsBrightness(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.25F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		return h * h * 0.5F;
	}

	public boolean method_8438(BlockEntity blockEntity) {
		boolean bl = this.blockEntities.add(blockEntity);
		if (bl && blockEntity instanceof Tickable) {
			this.tickingBlockEntities.add(blockEntity);
		}

		if (this.isClient) {
			BlockPos blockPos = blockEntity.method_11016();
			BlockState blockState = this.method_8320(blockPos);
			this.method_8413(blockPos, blockState, blockState, 2);
		}

		return bl;
	}

	public void addBlockEntities(Collection<BlockEntity> collection) {
		if (this.iteratingTickingBlockEntities) {
			this.pendingBlockEntities.addAll(collection);
		} else {
			for (BlockEntity blockEntity : collection) {
				this.method_8438(blockEntity);
			}
		}
	}

	public void method_18471() {
		Profiler profiler = this.getProfiler();
		profiler.push("blockEntities");
		if (!this.field_18139.isEmpty()) {
			this.tickingBlockEntities.removeAll(this.field_18139);
			this.blockEntities.removeAll(this.field_18139);
			this.field_18139.clear();
		}

		this.iteratingTickingBlockEntities = true;
		Iterator<BlockEntity> iterator = this.tickingBlockEntities.iterator();

		while (iterator.hasNext()) {
			BlockEntity blockEntity = (BlockEntity)iterator.next();
			if (!blockEntity.isInvalid() && blockEntity.hasWorld()) {
				BlockPos blockPos = blockEntity.method_11016();
				if (this.method_8591(blockPos) && this.method_8621().method_11952(blockPos)) {
					try {
						profiler.push((Supplier<String>)(() -> String.valueOf(BlockEntityType.method_11033(blockEntity.method_11017()))));
						((Tickable)blockEntity).tick();
						profiler.pop();
					} catch (Throwable var8) {
						CrashReport crashReport = CrashReport.create(var8, "Ticking block entity");
						CrashReportSection crashReportSection = crashReport.method_562("Block entity being ticked");
						blockEntity.method_11003(crashReportSection);
						throw new CrashException(crashReport);
					}
				}
			}

			if (blockEntity.isInvalid()) {
				iterator.remove();
				this.blockEntities.remove(blockEntity);
				if (this.method_8591(blockEntity.method_11016())) {
					this.method_8500(blockEntity.method_11016()).method_12041(blockEntity.method_11016());
				}
			}
		}

		this.iteratingTickingBlockEntities = false;
		profiler.swap("pendingBlockEntities");
		if (!this.pendingBlockEntities.isEmpty()) {
			for (int i = 0; i < this.pendingBlockEntities.size(); i++) {
				BlockEntity blockEntity2 = (BlockEntity)this.pendingBlockEntities.get(i);
				if (!blockEntity2.isInvalid()) {
					if (!this.blockEntities.contains(blockEntity2)) {
						this.method_8438(blockEntity2);
					}

					if (this.method_8591(blockEntity2.method_11016())) {
						WorldChunk worldChunk = this.method_8500(blockEntity2.method_11016());
						BlockState blockState = worldChunk.method_8320(blockEntity2.method_11016());
						worldChunk.method_12007(blockEntity2.method_11016(), blockEntity2);
						this.method_8413(blockEntity2.method_11016(), blockState, blockState, 3);
					}
				}
			}

			this.pendingBlockEntities.clear();
		}

		profiler.pop();
	}

	public void method_18472(Consumer<Entity> consumer, Entity entity) {
		try {
			consumer.accept(entity);
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Ticking entity");
			CrashReportSection crashReportSection = crashReport.method_562("Entity being ticked");
			entity.method_5819(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public boolean method_8534(BoundingBox boundingBox) {
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
						BlockState blockState = this.method_8320(pooledMutable.method_10113(o, p, q));
						if (!blockState.isAir()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	public boolean method_8425(BoundingBox boundingBox) {
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
							Block block = this.method_8320(pooledMutable.method_10113(o, p, q)).getBlock();
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
	public BlockState method_8475(BoundingBox boundingBox, Block block) {
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
							BlockState blockState = this.method_8320(pooledMutable.method_10113(o, p, q));
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

	public boolean method_8422(BoundingBox boundingBox, Material material) {
		int i = MathHelper.floor(boundingBox.minX);
		int j = MathHelper.ceil(boundingBox.maxX);
		int k = MathHelper.floor(boundingBox.minY);
		int l = MathHelper.ceil(boundingBox.maxY);
		int m = MathHelper.floor(boundingBox.minZ);
		int n = MathHelper.ceil(boundingBox.maxZ);
		MaterialPredicate materialPredicate = MaterialPredicate.method_11746(material);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o < j; o++) {
			for (int p = k; p < l; p++) {
				for (int q = m; q < n; q++) {
					if (materialPredicate.method_11745(this.method_8320(mutable.set(o, p, q)))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public Explosion createExplosion(@Nullable Entity entity, double d, double e, double f, float g, Explosion.class_4179 arg) {
		return this.createExplosion(entity, null, d, e, f, g, false, arg);
	}

	public Explosion createExplosion(@Nullable Entity entity, double d, double e, double f, float g, boolean bl, Explosion.class_4179 arg) {
		return this.createExplosion(entity, null, d, e, f, g, bl, arg);
	}

	public Explosion createExplosion(
		@Nullable Entity entity, @Nullable DamageSource damageSource, double d, double e, double f, float g, boolean bl, Explosion.class_4179 arg
	) {
		Explosion explosion = new Explosion(this, entity, d, e, f, g, bl, arg);
		if (damageSource != null) {
			explosion.setDamageSource(damageSource);
		}

		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(true);
		return explosion;
	}

	public boolean method_8506(@Nullable PlayerEntity playerEntity, BlockPos blockPos, Direction direction) {
		blockPos = blockPos.method_10093(direction);
		if (this.method_8320(blockPos).getBlock() == Blocks.field_10036) {
			this.method_8444(playerEntity, 1009, blockPos, 0);
			this.method_8650(blockPos);
			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public String getChunkProviderStatus() {
		return this.field_9248.getStatus();
	}

	@Nullable
	@Override
	public BlockEntity method_8321(BlockPos blockPos) {
		if (method_8518(blockPos)) {
			return null;
		} else if (!this.isClient && Thread.currentThread() != this.thread) {
			return null;
		} else {
			BlockEntity blockEntity = null;
			if (this.iteratingTickingBlockEntities) {
				blockEntity = this.method_8426(blockPos);
			}

			if (blockEntity == null) {
				blockEntity = this.method_8500(blockPos).method_12201(blockPos, WorldChunk.CreationType.field_12860);
			}

			if (blockEntity == null) {
				blockEntity = this.method_8426(blockPos);
			}

			return blockEntity;
		}
	}

	@Nullable
	private BlockEntity method_8426(BlockPos blockPos) {
		for (int i = 0; i < this.pendingBlockEntities.size(); i++) {
			BlockEntity blockEntity = (BlockEntity)this.pendingBlockEntities.get(i);
			if (!blockEntity.isInvalid() && blockEntity.method_11016().equals(blockPos)) {
				return blockEntity;
			}
		}

		return null;
	}

	public void method_8526(BlockPos blockPos, @Nullable BlockEntity blockEntity) {
		if (!method_8518(blockPos)) {
			if (blockEntity != null && !blockEntity.isInvalid()) {
				if (this.iteratingTickingBlockEntities) {
					blockEntity.method_10998(blockPos);
					Iterator<BlockEntity> iterator = this.pendingBlockEntities.iterator();

					while (iterator.hasNext()) {
						BlockEntity blockEntity2 = (BlockEntity)iterator.next();
						if (blockEntity2.method_11016().equals(blockPos)) {
							blockEntity2.invalidate();
							iterator.remove();
						}
					}

					this.pendingBlockEntities.add(blockEntity);
				} else {
					this.method_8500(blockPos).method_12007(blockPos, blockEntity);
					this.method_8438(blockEntity);
				}
			}
		}
	}

	public void method_8544(BlockPos blockPos) {
		BlockEntity blockEntity = this.method_8321(blockPos);
		if (blockEntity != null && this.iteratingTickingBlockEntities) {
			blockEntity.invalidate();
			this.pendingBlockEntities.remove(blockEntity);
		} else {
			if (blockEntity != null) {
				this.pendingBlockEntities.remove(blockEntity);
				this.blockEntities.remove(blockEntity);
				this.tickingBlockEntities.remove(blockEntity);
			}

			this.method_8500(blockPos).method_12041(blockPos);
		}
	}

	public boolean method_8477(BlockPos blockPos) {
		return method_8518(blockPos) ? false : this.field_9248.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	public boolean method_8515(BlockPos blockPos) {
		return this.method_8477(blockPos) && this.method_8320(blockPos).method_11631(this, blockPos);
	}

	public void calculateAmbientDarkness() {
		double d = 1.0 - (double)(this.getRainGradient(1.0F) * 5.0F) / 16.0;
		double e = 1.0 - (double)(this.getThunderGradient(1.0F) * 5.0F) / 16.0;
		double f = 0.5 + 2.0 * MathHelper.clamp((double)MathHelper.cos(this.getSkyAngle(1.0F) * (float) (Math.PI * 2)), -0.25, 0.25);
		this.ambientDarkness = (int)((1.0 - f * d * e) * 11.0);
	}

	public void setMobSpawnOptions(boolean bl, boolean bl2) {
		this.method_8398().setMobSpawnOptions(bl, bl2);
	}

	protected void initWeatherGradients() {
		if (this.field_9232.isRaining()) {
			this.rainGradient = 1.0F;
			if (this.field_9232.isThundering()) {
				this.thunderGradient = 1.0F;
			}
		}
	}

	public void close() throws IOException {
		this.field_9248.close();
	}

	@Override
	public List<Entity> method_8333(@Nullable Entity entity, BoundingBox boundingBox, @Nullable Predicate<? super Entity> predicate) {
		List<Entity> list = Lists.<Entity>newArrayList();
		int i = MathHelper.floor((boundingBox.minX - 2.0) / 16.0);
		int j = MathHelper.floor((boundingBox.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((boundingBox.minZ - 2.0) / 16.0);
		int l = MathHelper.floor((boundingBox.maxZ + 2.0) / 16.0);

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				if (this.isChunkLoaded(m, n)) {
					this.method_8497(m, n).method_12205(entity, boundingBox, list, predicate);
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
				WorldChunk worldChunk = this.method_8398().method_12126(m, n, false);
				if (worldChunk != null) {
					worldChunk.method_18029(entityType, boundingBox, list, predicate);
				}
			}
		}

		return list;
	}

	@Override
	public <T extends Entity> List<T> method_8390(Class<? extends T> class_, BoundingBox boundingBox, @Nullable Predicate<? super T> predicate) {
		int i = MathHelper.floor((boundingBox.minX - 2.0) / 16.0);
		int j = MathHelper.ceil((boundingBox.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((boundingBox.minZ - 2.0) / 16.0);
		int l = MathHelper.ceil((boundingBox.maxZ + 2.0) / 16.0);
		List<T> list = Lists.<T>newArrayList();

		for (int m = i; m < j; m++) {
			for (int n = k; n < l; n++) {
				WorldChunk worldChunk = this.method_8398().method_12126(m, n, false);
				if (worldChunk != null) {
					worldChunk.method_12210(class_, boundingBox, list, predicate);
				}
			}
		}

		return list;
	}

	@Nullable
	public abstract Entity getEntityById(int i);

	public void method_8524(BlockPos blockPos, BlockEntity blockEntity) {
		if (this.method_8591(blockPos)) {
			this.method_8500(blockPos).markDirty();
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
	public int method_8596(BlockPos blockPos, Direction direction) {
		return this.method_8320(blockPos).method_11577(this, blockPos, direction);
	}

	public LevelGeneratorType method_8527() {
		return this.field_9232.getGeneratorType();
	}

	public int method_8488(BlockPos blockPos) {
		int i = 0;
		i = Math.max(i, this.method_8596(blockPos.down(), Direction.DOWN));
		if (i >= 15) {
			return i;
		} else {
			i = Math.max(i, this.method_8596(blockPos.up(), Direction.UP));
			if (i >= 15) {
				return i;
			} else {
				i = Math.max(i, this.method_8596(blockPos.north(), Direction.NORTH));
				if (i >= 15) {
					return i;
				} else {
					i = Math.max(i, this.method_8596(blockPos.south(), Direction.SOUTH));
					if (i >= 15) {
						return i;
					} else {
						i = Math.max(i, this.method_8596(blockPos.west(), Direction.WEST));
						if (i >= 15) {
							return i;
						} else {
							i = Math.max(i, this.method_8596(blockPos.east(), Direction.EAST));
							return i >= 15 ? i : i;
						}
					}
				}
			}
		}
	}

	public boolean method_8459(BlockPos blockPos, Direction direction) {
		return this.method_8499(blockPos, direction) > 0;
	}

	public int method_8499(BlockPos blockPos, Direction direction) {
		BlockState blockState = this.method_8320(blockPos);
		return blockState.method_11621(this, blockPos) ? this.method_8488(blockPos) : blockState.method_11597(this, blockPos, direction);
	}

	public boolean method_8479(BlockPos blockPos) {
		if (this.method_8499(blockPos.down(), Direction.DOWN) > 0) {
			return true;
		} else if (this.method_8499(blockPos.up(), Direction.UP) > 0) {
			return true;
		} else if (this.method_8499(blockPos.north(), Direction.NORTH) > 0) {
			return true;
		} else if (this.method_8499(blockPos.south(), Direction.SOUTH) > 0) {
			return true;
		} else {
			return this.method_8499(blockPos.west(), Direction.WEST) > 0 ? true : this.method_8499(blockPos.east(), Direction.EAST) > 0;
		}
	}

	public int method_8482(BlockPos blockPos) {
		int i = 0;

		for (Direction direction : field_9233) {
			int j = this.method_8499(blockPos.method_10093(direction), direction);
			if (j >= 15) {
				return 15;
			}

			if (j > i) {
				i = j;
			}
		}

		return i;
	}

	@Environment(EnvType.CLIENT)
	public void disconnect() {
	}

	public void setTime(long l) {
		this.field_9232.setTime(l);
	}

	@Override
	public long getSeed() {
		return this.field_9232.getSeed();
	}

	public long getTime() {
		return this.field_9232.getTime();
	}

	public long getTimeOfDay() {
		return this.field_9232.getTimeOfDay();
	}

	public void setTimeOfDay(long l) {
		this.field_9232.setTimeOfDay(l);
	}

	protected void tickTime() {
		this.setTime(this.field_9232.getTime() + 1L);
		if (this.field_9232.getGameRules().getBoolean("doDaylightCycle")) {
			this.setTimeOfDay(this.field_9232.getTimeOfDay() + 1L);
		}
	}

	@Override
	public BlockPos method_8395() {
		BlockPos blockPos = new BlockPos(this.field_9232.getSpawnX(), this.field_9232.getSpawnY(), this.field_9232.getSpawnZ());
		if (!this.method_8621().method_11952(blockPos)) {
			blockPos = this.method_8598(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.method_8621().getCenterX(), 0.0, this.method_8621().getCenterZ()));
		}

		return blockPos;
	}

	public void method_8554(BlockPos blockPos) {
		this.field_9232.method_187(blockPos);
	}

	public boolean method_8505(PlayerEntity playerEntity, BlockPos blockPos) {
		return true;
	}

	public void summonParticle(Entity entity, byte b) {
	}

	@Override
	public ChunkManager method_8398() {
		return this.field_9248;
	}

	public void method_8427(BlockPos blockPos, Block block, int i, int j) {
		this.method_8320(blockPos).method_11583(this, blockPos, i, j);
	}

	@Override
	public LevelProperties method_8401() {
		return this.field_9232;
	}

	public GameRules getGameRules() {
		return this.field_9232.getGameRules();
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
		return this.field_9247.hasSkyLight() && !this.field_9247.isNether() ? (double)this.getThunderGradient(1.0F) > 0.9 : false;
	}

	public boolean isRaining() {
		return (double)this.getRainGradient(1.0F) > 0.2;
	}

	public boolean method_8520(BlockPos blockPos) {
		if (!this.isRaining()) {
			return false;
		} else if (!this.method_8311(blockPos)) {
			return false;
		} else {
			return this.method_8598(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()
				? false
				: this.method_8310(blockPos).getPrecipitation() == Biome.Precipitation.RAIN;
		}
	}

	public boolean method_8480(BlockPos blockPos) {
		Biome biome = this.method_8310(blockPos);
		return biome.hasHighHumidity();
	}

	@Nullable
	public abstract MapState method_17891(String string);

	public abstract void method_17890(MapState mapState);

	public abstract int getNextMapId();

	public void method_8474(int i, BlockPos blockPos, int j) {
	}

	public void method_8535(int i, BlockPos blockPos, int j) {
		this.method_8444(null, i, blockPos, j);
	}

	public abstract void method_8444(@Nullable PlayerEntity playerEntity, int i, BlockPos blockPos, int j);

	public int getEffectiveHeight() {
		return this.field_9247.isNether() ? 128 : 256;
	}

	@Environment(EnvType.CLIENT)
	public double getHorizonHeight() {
		return this.field_9232.getGeneratorType() == LevelGeneratorType.FLAT ? 0.0 : 63.0;
	}

	public CrashReportSection method_8538(CrashReport crashReport) {
		CrashReportSection crashReportSection = crashReport.method_556("Affected level", 1);
		crashReportSection.add("Level name", this.field_9232 == null ? "????" : this.field_9232.getLevelName());
		crashReportSection.method_577("All players", () -> this.getPlayers().size() + " total; " + this.getPlayers());
		crashReportSection.method_577("Chunk stats", () -> this.field_9248.getStatus());

		try {
			this.field_9232.method_151(crashReportSection);
		} catch (Throwable var4) {
			crashReportSection.add("Level Data Unobtainable", var4);
		}

		return crashReportSection;
	}

	public abstract void method_8517(int i, BlockPos blockPos, int j);

	@Environment(EnvType.CLIENT)
	public void method_8547(double d, double e, double f, double g, double h, double i, @Nullable CompoundTag compoundTag) {
	}

	public abstract Scoreboard method_8428();

	public void method_8455(BlockPos blockPos, Block block) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.method_10093(direction);
			if (this.method_8591(blockPos2)) {
				BlockState blockState = this.method_8320(blockPos2);
				if (blockState.getBlock() == Blocks.field_10377) {
					blockState.method_11622(this, blockPos2, block, blockPos);
				} else if (blockState.method_11621(this, blockPos2)) {
					blockPos2 = blockPos2.method_10093(direction);
					blockState = this.method_8320(blockPos2);
					if (blockState.getBlock() == Blocks.field_10377) {
						blockState.method_11622(this, blockPos2, block, blockPos);
					}
				}
			}
		}
	}

	@Override
	public LocalDifficulty method_8404(BlockPos blockPos) {
		long l = 0L;
		float f = 0.0F;
		if (this.method_8591(blockPos)) {
			f = this.method_8391();
			l = this.method_8500(blockPos).getInhabitedTime();
		}

		return new LocalDifficulty(this.getDifficulty(), this.getTimeOfDay(), l, f);
	}

	@Override
	public int getAmbientDarkness() {
		return this.ambientDarkness;
	}

	@Environment(EnvType.CLIENT)
	public int getTicksSinceLightning() {
		return this.ticksSinceLightning;
	}

	public void setTicksSinceLightning(int i) {
		this.ticksSinceLightning = i;
	}

	@Override
	public WorldBorder method_8621() {
		return this.field_9223;
	}

	public void method_8522(Packet<?> packet) {
		throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
	}

	@Nullable
	public BlockPos method_8487(String string, BlockPos blockPos, int i, boolean bl) {
		return null;
	}

	@Override
	public Dimension method_8597() {
		return this.field_9247;
	}

	@Override
	public Random getRandom() {
		return this.random;
	}

	@Override
	public boolean method_16358(BlockPos blockPos, Predicate<BlockState> predicate) {
		return predicate.test(this.method_8320(blockPos));
	}

	public abstract RecipeManager getRecipeManager();

	public abstract TagManager method_8514();

	public BlockPos method_8536(int i, int j, int k, int l) {
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
	public BlockPos method_8598(Heightmap.Type type, BlockPos blockPos) {
		return new BlockPos(blockPos.getX(), this.method_8589(type, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
	}
}
