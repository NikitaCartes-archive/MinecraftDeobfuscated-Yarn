package net.minecraft.server.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1419;
import net.minecraft.class_4094;
import net.minecraft.class_4151;
import net.minecraft.class_4153;
import net.minecraft.class_4158;
import net.minecraft.class_4209;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.BlockActionS2CPacket;
import net.minecraft.client.network.packet.BlockBreakingProgressS2CPacket;
import net.minecraft.client.network.packet.EntitySpawnGlobalS2CPacket;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.client.network.packet.ExplosionS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.client.network.packet.ParticleS2CPacket;
import net.minecraft.client.network.packet.PlaySoundFromEntityS2CPacket;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Npc;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ChunkTicketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.StructureManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagManager;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.TypeFilterableList;
import net.minecraft.util.Void;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.IdCountsState;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.SessionLockException;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorld extends World {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<Entity> globalEntities = Lists.<Entity>newArrayList();
	private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectOpenHashMap<>();
	private final Map<UUID, Entity> entitiesByUuid = Maps.<UUID, Entity>newHashMap();
	private final Queue<Entity> field_18260 = Queues.<Entity>newArrayDeque();
	private final List<ServerPlayerEntity> players = Lists.<ServerPlayerEntity>newArrayList();
	boolean field_18264;
	private final MinecraftServer server;
	private final WorldSaveHandler worldSaveHandler;
	public boolean savingDisabled;
	private boolean allPlayersSleeping;
	private int idleTimeout;
	private final PortalForcer portalForcer;
	private final ServerTickScheduler<Block> blockTickScheduler = new ServerTickScheduler<>(
		this, block -> block == null || block.method_9564().isAir(), Registry.BLOCK::method_10221, Registry.BLOCK::method_10223, this::method_14189
	);
	private final ServerTickScheduler<Fluid> fluidTickScheduler = new ServerTickScheduler<>(
		this, fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::method_10221, Registry.FLUID::method_10223, this::method_14171
	);
	private final Set<EntityNavigation> field_18262 = Sets.<EntityNavigation>newHashSet();
	protected final RaidManager field_18811;
	protected final class_1419 field_13958 = new class_1419(this);
	private final ObjectLinkedOpenHashSet<BlockAction> pendingBlockActions = new ObjectLinkedOpenHashSet<>();
	private boolean insideTick;
	@Nullable
	private final WanderingTraderManager wanderingTraderManager;

	public ServerWorld(
		MinecraftServer minecraftServer,
		Executor executor,
		WorldSaveHandler worldSaveHandler,
		LevelProperties levelProperties,
		DimensionType dimensionType,
		Profiler profiler,
		WorldGenerationProgressListener worldGenerationProgressListener
	) {
		super(
			levelProperties,
			dimensionType,
			(world, dimension) -> new ServerChunkManager(
					(ServerWorld)world,
					worldSaveHandler.getWorldDir(),
					worldSaveHandler.getDataFixer(),
					worldSaveHandler.getStructureManager(),
					executor,
					dimension.createChunkGenerator(),
					minecraftServer.method_3760().getViewDistance(),
					minecraftServer.method_3760().getViewDistance() - 2,
					worldGenerationProgressListener,
					() -> minecraftServer.method_3847(DimensionType.field_13072).getPersistentStateManager()
				),
			profiler,
			false
		);
		this.worldSaveHandler = worldSaveHandler;
		this.server = minecraftServer;
		this.portalForcer = new PortalForcer(this);
		this.calculateAmbientDarkness();
		this.initWeatherGradients();
		this.method_8621().setMaxWorldBorderRadius(minecraftServer.getMaxWorldBorderRadius());
		this.field_18811 = this.getPersistentStateManager().getOrCreate(() -> new RaidManager(this), RaidManager.method_16533(this.field_9247));
		if (!minecraftServer.isSinglePlayer()) {
			this.method_8401().setGameMode(minecraftServer.getDefaultGameMode());
		}

		this.wanderingTraderManager = this.field_9247.method_12460() == DimensionType.field_13072 ? new WanderingTraderManager(this) : null;
	}

	public void method_18765(BooleanSupplier booleanSupplier) {
		Profiler profiler = this.getProfiler();
		this.insideTick = true;
		profiler.push("world border");
		this.method_8621().update();
		profiler.swap("weather");
		boolean bl = this.isRaining();
		if (this.field_9247.hasSkyLight()) {
			if (this.getGameRules().getBoolean("doWeatherCycle")) {
				int i = this.field_9232.getClearWeatherTime();
				int j = this.field_9232.getThunderTime();
				int k = this.field_9232.getRainTime();
				boolean bl2 = this.field_9232.isThundering();
				boolean bl3 = this.field_9232.isRaining();
				if (i > 0) {
					i--;
					j = bl2 ? 0 : 1;
					k = bl3 ? 0 : 1;
					bl2 = false;
					bl3 = false;
				} else {
					if (j > 0) {
						if (--j == 0) {
							bl2 = !bl2;
						}
					} else if (bl2) {
						j = this.random.nextInt(12000) + 3600;
					} else {
						j = this.random.nextInt(168000) + 12000;
					}

					if (k > 0) {
						if (--k == 0) {
							bl3 = !bl3;
						}
					} else if (bl3) {
						k = this.random.nextInt(12000) + 12000;
					} else {
						k = this.random.nextInt(168000) + 12000;
					}
				}

				this.field_9232.setThunderTime(j);
				this.field_9232.setRainTime(k);
				this.field_9232.setClearWeatherTime(i);
				this.field_9232.setThundering(bl2);
				this.field_9232.setRaining(bl3);
			}

			this.thunderGradientPrev = this.thunderGradient;
			if (this.field_9232.isThundering()) {
				this.thunderGradient = (float)((double)this.thunderGradient + 0.01);
			} else {
				this.thunderGradient = (float)((double)this.thunderGradient - 0.01);
			}

			this.thunderGradient = MathHelper.clamp(this.thunderGradient, 0.0F, 1.0F);
			this.rainGradientPrev = this.rainGradient;
			if (this.field_9232.isRaining()) {
				this.rainGradient = (float)((double)this.rainGradient + 0.01);
			} else {
				this.rainGradient = (float)((double)this.rainGradient - 0.01);
			}

			this.rainGradient = MathHelper.clamp(this.rainGradient, 0.0F, 1.0F);
		}

		if (this.rainGradientPrev != this.rainGradient) {
			this.server.method_3760().sendToDimension(new GameStateChangeS2CPacket(7, this.rainGradient), this.field_9247.method_12460());
		}

		if (this.thunderGradientPrev != this.thunderGradient) {
			this.server.method_3760().sendToDimension(new GameStateChangeS2CPacket(8, this.thunderGradient), this.field_9247.method_12460());
		}

		if (bl != this.isRaining()) {
			if (bl) {
				this.server.method_3760().sendToAll(new GameStateChangeS2CPacket(2, 0.0F));
			} else {
				this.server.method_3760().sendToAll(new GameStateChangeS2CPacket(1, 0.0F));
			}

			this.server.method_3760().sendToAll(new GameStateChangeS2CPacket(7, this.rainGradient));
			this.server.method_3760().sendToAll(new GameStateChangeS2CPacket(8, this.thunderGradient));
		}

		if (this.method_8401().isHardcore() && this.getDifficulty() != Difficulty.HARD) {
			this.method_8401().setDifficulty(Difficulty.HARD);
		}

		if (this.allPlayersSleeping
			&& this.players.stream().noneMatch(serverPlayerEntity -> !serverPlayerEntity.isSpectator() && !serverPlayerEntity.isSleepingLongEnough())) {
			this.allPlayersSleeping = false;
			if (this.getGameRules().getBoolean("doDaylightCycle")) {
				long l = this.field_9232.getTimeOfDay() + 24000L;
				this.setTimeOfDay(l - l % 24000L);
			}

			this.players.stream().filter(LivingEntity::isSleeping).forEach(serverPlayerEntity -> serverPlayerEntity.wakeUp(false, false, true));
			if (this.getGameRules().getBoolean("doWeatherCycle")) {
				this.resetWeather();
			}
		}

		this.calculateAmbientDarkness();
		this.tickTime();
		profiler.swap("chunkSource");
		this.method_14178().tick(booleanSupplier);
		profiler.swap("tickPending");
		if (this.field_9232.getGeneratorType() != LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.blockTickScheduler.tick();
			this.fluidTickScheduler.tick();
		}

		profiler.swap("village");
		this.field_13958.method_6445();
		profiler.swap("portalForcer");
		this.portalForcer.tick(this.getTime());
		profiler.swap("raid");
		this.field_18811.tick();
		if (this.wanderingTraderManager != null) {
			this.wanderingTraderManager.tick();
		}

		profiler.swap("blockEvents");
		this.sendBlockActions();
		this.insideTick = false;
		profiler.swap("entities");
		boolean bl4 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
		if (bl4) {
			this.resetIdleTimeout();
		}

		if (bl4 || this.idleTimeout++ < 300) {
			this.field_9247.update();
			profiler.push("global");

			for (int j = 0; j < this.globalEntities.size(); j++) {
				Entity entity = (Entity)this.globalEntities.get(j);
				this.method_18472(entityx -> {
					entityx.age++;
					entityx.update();
				}, entity);
				if (entity.invalid) {
					this.globalEntities.remove(j--);
				}
			}

			profiler.swap("regular");
			this.field_18264 = true;
			ObjectIterator<Entry<Entity>> objectIterator = this.entitiesById.int2ObjectEntrySet().iterator();

			while (objectIterator.hasNext()) {
				Entry<Entity> entry = (Entry<Entity>)objectIterator.next();
				Entity entity2 = (Entity)entry.getValue();
				Entity entity3 = entity2.getRiddenEntity();
				if (!this.server.shouldSpawnAnimals() && (entity2 instanceof AnimalEntity || entity2 instanceof WaterCreatureEntity)) {
					entity2.invalidate();
				}

				if (!this.server.shouldSpawnNpcs() && entity2 instanceof Npc) {
					entity2.invalidate();
				}

				if (entity3 != null) {
					if (!entity3.invalid && entity3.hasPassenger(entity2)) {
						continue;
					}

					entity2.stopRiding();
				}

				profiler.push("tick");
				if (!entity2.invalid && !(entity2 instanceof EnderDragonPart)) {
					this.method_18472(this::method_18762, entity2);
				}

				profiler.pop();
				profiler.push("remove");
				if (entity2.invalid) {
					this.method_18780(entity2);
					objectIterator.remove();
					this.method_18772(entity2);
				}

				profiler.pop();
			}

			this.field_18264 = false;

			Entity entity;
			while ((entity = (Entity)this.field_18260.poll()) != null) {
				this.method_18778(entity);
			}

			profiler.pop();
			this.method_18471();
		}

		profiler.pop();
	}

	public void tickChunk(WorldChunk worldChunk, int i) {
		ChunkPos chunkPos = worldChunk.getPos();
		boolean bl = this.isRaining();
		int j = chunkPos.getStartX();
		int k = chunkPos.getStartZ();
		Profiler profiler = this.getProfiler();
		profiler.push("thunder");
		if (bl && this.isThundering() && this.random.nextInt(100000) == 0) {
			BlockPos blockPos = this.method_18210(this.method_8536(j, 0, k, 15));
			if (this.method_8520(blockPos)) {
				LocalDifficulty localDifficulty = this.method_8404(blockPos);
				boolean bl2 = this.getGameRules().getBoolean("doMobSpawning") && this.random.nextDouble() < (double)localDifficulty.getLocalDifficulty() * 0.01;
				if (bl2) {
					SkeletonHorseEntity skeletonHorseEntity = EntityType.SKELETON_HORSE.method_5883(this);
					skeletonHorseEntity.method_6813(true);
					skeletonHorseEntity.setBreedingAge(0);
					skeletonHorseEntity.setPosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
					this.spawnEntity(skeletonHorseEntity);
				}

				this.addLightning(new LightningEntity(this, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, bl2));
			}
		}

		profiler.swap("iceandsnow");
		if (this.random.nextInt(16) == 0) {
			BlockPos blockPos = this.method_8598(Heightmap.Type.MOTION_BLOCKING, this.method_8536(j, 0, k, 15));
			BlockPos blockPos2 = blockPos.down();
			Biome biome = this.method_8310(blockPos);
			if (biome.method_8705(this, blockPos2)) {
				this.method_8501(blockPos2, Blocks.field_10295.method_9564());
			}

			if (bl && biome.method_8696(this, blockPos)) {
				this.method_8501(blockPos, Blocks.field_10477.method_9564());
			}

			if (bl && this.method_8310(blockPos2).getPrecipitation() == Biome.Precipitation.RAIN) {
				this.method_8320(blockPos2).getBlock().method_9504(this, blockPos2);
			}
		}

		profiler.swap("tickBlocks");
		if (i > 0) {
			for (ChunkSection chunkSection : worldChunk.method_12006()) {
				if (chunkSection != WorldChunk.field_12852 && chunkSection.hasRandomTicks()) {
					int l = chunkSection.getYOffset();

					for (int m = 0; m < i; m++) {
						BlockPos blockPos3 = this.method_8536(j, l, k, 15);
						profiler.push("randomTick");
						BlockState blockState = chunkSection.getBlockState(blockPos3.getX() - j, blockPos3.getY() - l, blockPos3.getZ() - k);
						if (blockState.hasRandomTicks()) {
							blockState.method_11624(this, blockPos3, this.random);
						}

						FluidState fluidState = chunkSection.method_12255(blockPos3.getX() - j, blockPos3.getY() - l, blockPos3.getZ() - k);
						if (fluidState.hasRandomTicks()) {
							fluidState.method_15757(this, blockPos3, this.random);
						}

						profiler.pop();
					}
				}
			}
		}

		profiler.pop();
	}

	protected BlockPos method_18210(BlockPos blockPos) {
		BlockPos blockPos2 = this.method_8598(Heightmap.Type.MOTION_BLOCKING, blockPos);
		BoundingBox boundingBox = new BoundingBox(blockPos2, new BlockPos(blockPos2.getX(), this.getHeight(), blockPos2.getZ())).expand(3.0);
		List<LivingEntity> list = this.method_8390(
			LivingEntity.class, boundingBox, livingEntity -> livingEntity != null && livingEntity.isValid() && this.method_8311(livingEntity.method_5704())
		);
		if (!list.isEmpty()) {
			return ((LivingEntity)list.get(this.random.nextInt(list.size()))).method_5704();
		} else {
			if (blockPos2.getY() == -1) {
				blockPos2 = blockPos2.up(2);
			}

			return blockPos2;
		}
	}

	public boolean isInsideTick() {
		return this.insideTick;
	}

	public void updatePlayersSleeping() {
		this.allPlayersSleeping = false;
		if (!this.players.isEmpty()) {
			int i = 0;
			int j = 0;

			for (ServerPlayerEntity serverPlayerEntity : this.players) {
				if (serverPlayerEntity.isSpectator()) {
					i++;
				} else if (serverPlayerEntity.isSleeping()) {
					j++;
				}
			}

			this.allPlayersSleeping = j > 0 && j >= this.players.size() - i;
		}
	}

	public ServerScoreboard method_14170() {
		return this.server.method_3845();
	}

	private void resetWeather() {
		this.field_9232.setRainTime(0);
		this.field_9232.setRaining(false);
		this.field_9232.setThunderTime(0);
		this.field_9232.setThundering(false);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setDefaultSpawnClient() {
		if (this.field_9232.getSpawnY() <= 0) {
			this.field_9232.setSpawnY(this.getSeaLevel() + 1);
		}

		int i = this.field_9232.getSpawnX();
		int j = this.field_9232.getSpawnZ();
		int k = 0;

		while (this.method_8495(new BlockPos(i, 0, j)).isAir()) {
			i += this.random.nextInt(8) - this.random.nextInt(8);
			j += this.random.nextInt(8) - this.random.nextInt(8);
			if (++k == 10000) {
				break;
			}
		}

		this.field_9232.setSpawnX(i);
		this.field_9232.setSpawnZ(j);
	}

	public void resetIdleTimeout() {
		this.idleTimeout = 0;
	}

	private void method_14171(ScheduledTick<Fluid> scheduledTick) {
		FluidState fluidState = this.method_8316(scheduledTick.field_9322);
		if (fluidState.getFluid() == scheduledTick.getObject()) {
			fluidState.method_15770(this, scheduledTick.field_9322);
		}
	}

	private void method_14189(ScheduledTick<Block> scheduledTick) {
		BlockState blockState = this.method_8320(scheduledTick.field_9322);
		if (blockState.getBlock() == scheduledTick.getObject()) {
			blockState.method_11585(this, scheduledTick.field_9322, this.random);
		}
	}

	public void method_18762(Entity entity) {
		if (entity instanceof PlayerEntity || this.method_14178().isEntityInLoadedChunk(entity)) {
			entity.prevRenderX = entity.x;
			entity.prevRenderY = entity.y;
			entity.prevRenderZ = entity.z;
			entity.prevYaw = entity.yaw;
			entity.prevPitch = entity.pitch;
			if (entity.field_6016) {
				entity.age++;
				this.getProfiler().push((Supplier<String>)(() -> Registry.ENTITY_TYPE.method_10221(entity.method_5864()).toString()));
				entity.update();
				this.getProfiler().pop();
			}

			this.method_18767(entity);
			if (entity.field_6016) {
				for (Entity entity2 : entity.getPassengerList()) {
					this.method_18763(entity, entity2);
				}
			}
		}
	}

	public void method_18763(Entity entity, Entity entity2) {
		if (entity2.invalid || entity2.getRiddenEntity() != entity) {
			entity2.stopRiding();
		} else if (entity2 instanceof PlayerEntity || this.method_14178().isEntityInLoadedChunk(entity2)) {
			entity2.prevRenderX = entity2.x;
			entity2.prevRenderY = entity2.y;
			entity2.prevRenderZ = entity2.z;
			entity2.prevYaw = entity2.yaw;
			entity2.prevPitch = entity2.pitch;
			if (entity2.field_6016) {
				entity2.age++;
				entity2.updateRiding();
			}

			this.method_18767(entity2);
			if (entity2.field_6016) {
				for (Entity entity3 : entity2.getPassengerList()) {
					this.method_18763(entity2, entity3);
				}
			}
		}
	}

	public void method_18767(Entity entity) {
		this.getProfiler().push("chunkCheck");
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.y / 16.0);
		int k = MathHelper.floor(entity.z / 16.0);
		if (!entity.field_6016 || entity.chunkX != i || entity.chunkY != j || entity.chunkZ != k) {
			if (entity.field_6016 && this.isChunkLoaded(entity.chunkX, entity.chunkZ)) {
				this.method_8497(entity.chunkX, entity.chunkZ).remove(entity, entity.chunkY);
			}

			if (!entity.method_5754() && !this.isChunkLoaded(i, k)) {
				entity.field_6016 = false;
			} else {
				this.method_8497(i, k).addEntity(entity);
			}
		}

		this.getProfiler().pop();
	}

	@Override
	public boolean method_8505(PlayerEntity playerEntity, BlockPos blockPos) {
		return !this.server.isSpawnProtected(this, blockPos, playerEntity) && this.method_8621().method_11952(blockPos);
	}

	public void init(LevelInfo levelInfo) {
		if (!this.field_9247.canPlayersSleep()) {
			this.field_9232.method_187(BlockPos.ORIGIN.up(this.field_9248.getChunkGenerator().getSpawnHeight()));
		} else if (this.field_9232.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.field_9232.method_187(BlockPos.ORIGIN.up());
		} else {
			BiomeSource biomeSource = this.field_9248.getChunkGenerator().getBiomeSource();
			List<Biome> list = biomeSource.getSpawnBiomes();
			Random random = new Random(this.getSeed());
			BlockPos blockPos = biomeSource.method_8762(0, 0, 256, list, random);
			ChunkPos chunkPos = blockPos == null ? new ChunkPos(0, 0) : new ChunkPos(blockPos);
			if (blockPos == null) {
				LOGGER.warn("Unable to find spawn biome");
			}

			boolean bl = false;

			for (Block block : BlockTags.field_15478.values()) {
				if (biomeSource.getTopMaterials().contains(block.method_9564())) {
					bl = true;
					break;
				}
			}

			this.field_9232.method_187(chunkPos.method_8323().add(8, this.field_9248.getChunkGenerator().getSpawnHeight(), 8));
			int i = 0;
			int j = 0;
			int k = 0;
			int l = -1;
			int m = 32;

			for (int n = 0; n < 1024; n++) {
				if (i > -16 && i <= 16 && j > -16 && j <= 16) {
					BlockPos blockPos2 = this.field_9247.method_12452(new ChunkPos(chunkPos.x + i, chunkPos.z + j), bl);
					if (blockPos2 != null) {
						this.field_9232.method_187(blockPos2);
						break;
					}
				}

				if (i == j || i < 0 && i == -j || i > 0 && i == 1 - j) {
					int o = k;
					k = -l;
					l = o;
				}

				i += k;
				j += l;
			}

			if (levelInfo.hasBonusChest()) {
				this.placeBonusChest();
			}
		}
	}

	protected void placeBonusChest() {
		BonusChestFeature bonusChestFeature = Feature.BONUS_CHEST;

		for (int i = 0; i < 10; i++) {
			int j = this.field_9232.getSpawnX() + this.random.nextInt(6) - this.random.nextInt(6);
			int k = this.field_9232.getSpawnZ() + this.random.nextInt(6) - this.random.nextInt(6);
			BlockPos blockPos = this.method_8598(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(j, 0, k)).up();
			if (bonusChestFeature.method_12817(
				this, (ChunkGenerator<? extends ChunkGeneratorConfig>)this.field_9248.getChunkGenerator(), this.random, blockPos, FeatureConfig.field_13603
			)) {
				break;
			}
		}
	}

	@Nullable
	public BlockPos getForcedSpawnPoint() {
		return this.field_9247.method_12466();
	}

	public void method_14176(@Nullable ProgressListener progressListener, boolean bl, boolean bl2) throws SessionLockException {
		ServerChunkManager serverChunkManager = this.method_14178();
		if (!bl2) {
			if (progressListener != null) {
				progressListener.method_15412(new TranslatableTextComponent("menu.savingLevel"));
			}

			this.saveLevel();
			if (progressListener != null) {
				progressListener.method_15414(new TranslatableTextComponent("menu.savingChunks"));
			}

			serverChunkManager.save(bl);
		}
	}

	protected void saveLevel() throws SessionLockException {
		this.checkSessionLock();
		this.field_9247.saveWorldData();
		this.method_14178().getPersistentStateManager().save();
	}

	public List<Entity> method_18198(@Nullable EntityType<?> entityType, Predicate<? super Entity> predicate) {
		List<Entity> list = Lists.<Entity>newArrayList();

		for (Entity entity : this.entitiesById.values()) {
			if ((entityType == null || entity.method_5864() == entityType) && predicate.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	public List<EnderDragonEntity> method_18776() {
		List<EnderDragonEntity> list = Lists.<EnderDragonEntity>newArrayList();

		for (Entity entity : this.entitiesById.values()) {
			if (entity instanceof EnderDragonEntity && entity.isValid()) {
				list.add((EnderDragonEntity)entity);
			}
		}

		return list;
	}

	public List<ServerPlayerEntity> method_18766(Predicate<? super ServerPlayerEntity> predicate) {
		List<ServerPlayerEntity> list = Lists.<ServerPlayerEntity>newArrayList();

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (predicate.test(serverPlayerEntity)) {
				list.add(serverPlayerEntity);
			}
		}

		return list;
	}

	@Nullable
	public ServerPlayerEntity method_18779() {
		List<ServerPlayerEntity> list = this.method_18766(LivingEntity::isValid);
		return list.isEmpty() ? null : (ServerPlayerEntity)list.get(this.random.nextInt(list.size()));
	}

	public Object2IntMap<EntityCategory> method_18219() {
		Object2IntMap<EntityCategory> object2IntMap = new Object2IntOpenHashMap<>();

		for (Entity entity : this.entitiesById.values()) {
			if (!(entity instanceof MobEntity) || !((MobEntity)entity).isPersistent()) {
				EntityCategory entityCategory = entity.method_5864().method_5891();
				if (entityCategory != EntityCategory.field_17715) {
					object2IntMap.computeInt(entityCategory, (entityCategoryx, integer) -> 1 + (integer == null ? 0 : integer));
				}
			}
		}

		return object2IntMap;
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		return this.method_14175(entity);
	}

	public boolean method_18768(Entity entity) {
		return this.method_14175(entity);
	}

	public void method_18769(Entity entity) {
		boolean bl = entity.teleporting;
		entity.teleporting = true;
		this.method_18768(entity);
		entity.teleporting = bl;
		this.method_18767(entity);
	}

	public void method_18207(ServerPlayerEntity serverPlayerEntity) {
		this.method_18771(serverPlayerEntity);
		this.method_18767(serverPlayerEntity);
	}

	public void method_18211(ServerPlayerEntity serverPlayerEntity) {
		this.method_18771(serverPlayerEntity);
		this.method_18767(serverPlayerEntity);
	}

	public void method_18213(ServerPlayerEntity serverPlayerEntity) {
		this.method_18771(serverPlayerEntity);
	}

	public void method_18215(ServerPlayerEntity serverPlayerEntity) {
		this.method_18771(serverPlayerEntity);
	}

	private void method_18771(ServerPlayerEntity serverPlayerEntity) {
		Entity entity = (Entity)this.entitiesByUuid.get(serverPlayerEntity.getUuid());
		if (entity != null) {
			LOGGER.warn("Force-added player with duplicate UUID {}", serverPlayerEntity.getUuid().toString());
			entity.method_18375();
			this.method_18770((ServerPlayerEntity)entity);
		}

		this.players.add(serverPlayerEntity);
		this.updatePlayersSleeping();
		Chunk chunk = this.method_8402(MathHelper.floor(serverPlayerEntity.x / 16.0), MathHelper.floor(serverPlayerEntity.z / 16.0), ChunkStatus.FULL, true);
		if (chunk instanceof WorldChunk) {
			chunk.addEntity(serverPlayerEntity);
		}

		this.method_18778(serverPlayerEntity);
	}

	private boolean method_14175(Entity entity) {
		if (entity.invalid) {
			LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.method_5890(entity.method_5864()));
			return false;
		} else if (this.method_18777(entity)) {
			return false;
		} else {
			Chunk chunk = this.method_8402(MathHelper.floor(entity.x / 16.0), MathHelper.floor(entity.z / 16.0), ChunkStatus.FULL, entity.teleporting);
			if (!(chunk instanceof WorldChunk)) {
				return false;
			} else {
				chunk.addEntity(entity);
				this.method_18778(entity);
				return true;
			}
		}
	}

	public void method_18214(Entity entity) {
		if (!this.method_18777(entity)) {
			this.method_18778(entity);
		}
	}

	private boolean method_18777(Entity entity) {
		Entity entity2 = (Entity)this.entitiesByUuid.get(entity.getUuid());
		if (entity2 == null) {
			return false;
		} else {
			LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.method_5890(entity2.method_5864()), entity.getUuid().toString());
			return true;
		}
	}

	public void method_18764(WorldChunk worldChunk) {
		this.field_18139.addAll(worldChunk.getBlockEntityMap().values());
		TypeFilterableList[] var2 = worldChunk.method_12215();
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; var4++) {
			for (Entity entity : var2[var4]) {
				if (!(entity instanceof ServerPlayerEntity)) {
					if (this.field_18264) {
						throw new IllegalStateException("Removing entity while ticking!");
					}

					this.entitiesById.remove(entity.getEntityId());
					this.method_18772(entity);
				}
			}
		}
	}

	public void method_18772(Entity entity) {
		if (entity instanceof EnderDragonEntity) {
			for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).method_5690()) {
				enderDragonPart.invalidate();
			}
		}

		this.entitiesByUuid.remove(entity.getUuid());
		this.method_14178().method_18753(entity);
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
			this.players.remove(serverPlayerEntity);
		}

		this.method_14170().resetEntityScore(entity);
		if (entity instanceof MobEntity) {
			this.field_18262.remove(((MobEntity)entity).method_5942());
		}
	}

	private void method_18778(Entity entity) {
		if (this.field_18264) {
			this.field_18260.add(entity);
		} else {
			this.entitiesById.put(entity.getEntityId(), entity);
			if (entity instanceof EnderDragonEntity) {
				for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).method_5690()) {
					this.entitiesById.put(enderDragonPart.getEntityId(), enderDragonPart);
				}
			}

			this.entitiesByUuid.put(entity.getUuid(), entity);
			this.method_14178().method_18755(entity);
			if (entity instanceof MobEntity) {
				this.field_18262.add(((MobEntity)entity).method_5942());
			}
		}
	}

	public void method_18774(Entity entity) {
		if (this.field_18264) {
			throw new IllegalStateException("Removing entity while ticking!");
		} else {
			this.method_18780(entity);
			this.entitiesById.remove(entity.getEntityId());
			this.method_18772(entity);
		}
	}

	private void method_18780(Entity entity) {
		Chunk chunk = this.method_8402(entity.chunkX, entity.chunkZ, ChunkStatus.FULL, false);
		if (chunk instanceof WorldChunk) {
			((WorldChunk)chunk).remove(entity);
		}
	}

	public void method_18770(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.invalidate();
		this.updatePlayersSleeping();
		this.method_18774(serverPlayerEntity);
	}

	public void addLightning(LightningEntity lightningEntity) {
		this.globalEntities.add(lightningEntity);
		this.server
			.method_3760()
			.sendToAround(
				null, lightningEntity.x, lightningEntity.y, lightningEntity.z, 512.0, this.field_9247.method_12460(), new EntitySpawnGlobalS2CPacket(lightningEntity)
			);
	}

	@Override
	public void method_8517(int i, BlockPos blockPos, int j) {
		for (ServerPlayerEntity serverPlayerEntity : this.server.method_3760().getPlayerList()) {
			if (serverPlayerEntity != null && serverPlayerEntity.field_6002 == this && serverPlayerEntity.getEntityId() != i) {
				double d = (double)blockPos.getX() - serverPlayerEntity.x;
				double e = (double)blockPos.getY() - serverPlayerEntity.y;
				double f = (double)blockPos.getZ() - serverPlayerEntity.z;
				if (d * d + e * e + f * f < 1024.0) {
					serverPlayerEntity.field_13987.sendPacket(new BlockBreakingProgressS2CPacket(i, blockPos, j));
				}
			}
		}
	}

	@Override
	public void method_8465(
		@Nullable PlayerEntity playerEntity, double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h
	) {
		this.server
			.method_3760()
			.sendToAround(
				playerEntity,
				d,
				e,
				f,
				g > 1.0F ? (double)(16.0F * g) : 16.0,
				this.field_9247.method_12460(),
				new PlaySoundS2CPacket(soundEvent, soundCategory, d, e, f, g, h)
			);
	}

	@Override
	public void method_8449(@Nullable PlayerEntity playerEntity, Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
		this.server
			.method_3760()
			.sendToAround(
				playerEntity,
				entity.x,
				entity.y,
				entity.z,
				f > 1.0F ? (double)(16.0F * f) : 16.0,
				this.field_9247.method_12460(),
				new PlaySoundFromEntityS2CPacket(soundEvent, soundCategory, entity, f, g)
			);
	}

	@Override
	public void method_8474(int i, BlockPos blockPos, int j) {
		this.server.method_3760().sendToAll(new WorldEventS2CPacket(i, blockPos, j, true));
	}

	@Override
	public void method_8444(@Nullable PlayerEntity playerEntity, int i, BlockPos blockPos, int j) {
		this.server
			.method_3760()
			.sendToAround(
				playerEntity,
				(double)blockPos.getX(),
				(double)blockPos.getY(),
				(double)blockPos.getZ(),
				64.0,
				this.field_9247.method_12460(),
				new WorldEventS2CPacket(i, blockPos, j, false)
			);
	}

	@Override
	public void method_8413(BlockPos blockPos, BlockState blockState, BlockState blockState2, int i) {
		this.method_14178().markForUpdate(blockPos);
		VoxelShape voxelShape = blockState.method_11628(this, blockPos);
		VoxelShape voxelShape2 = blockState2.method_11628(this, blockPos);
		if (VoxelShapes.method_1074(voxelShape, voxelShape2, BooleanBiFunction.NOT_SAME)) {
			for (EntityNavigation entityNavigation : this.field_18262) {
				if (!entityNavigation.shouldRecalculatePath()) {
					entityNavigation.method_18053(blockPos);
				}
			}
		}
	}

	@Override
	public void summonParticle(Entity entity, byte b) {
		this.method_14178().method_18751(entity, new EntityStatusS2CPacket(entity, b));
	}

	public ServerChunkManager method_14178() {
		return (ServerChunkManager)super.method_8398();
	}

	@Environment(EnvType.CLIENT)
	public CompletableFuture<WorldChunk> getChunkSyncIfServerThread(int i, int j, boolean bl) {
		return this.method_14178()
			.getChunkSyncIfServerThread(i, j, ChunkStatus.FULL, bl)
			.thenApply(either -> either.map(chunk -> (WorldChunk)chunk, unloaded -> null));
	}

	@Override
	public Explosion createExplosion(
		@Nullable Entity entity, DamageSource damageSource, double d, double e, double f, float g, boolean bl, Explosion.class_4179 arg
	) {
		Explosion explosion = new Explosion(this, entity, d, e, f, g, bl, arg);
		if (damageSource != null) {
			explosion.setDamageSource(damageSource);
		}

		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(false);
		if (arg == Explosion.class_4179.field_18685) {
			explosion.clearAffectedBlocks();
		}

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (serverPlayerEntity.squaredDistanceTo(d, e, f) < 4096.0) {
				serverPlayerEntity.field_13987
					.sendPacket(new ExplosionS2CPacket(d, e, f, g, explosion.getAffectedBlocks(), (Vec3d)explosion.getAffectedPlayers().get(serverPlayerEntity)));
			}
		}

		return explosion;
	}

	@Override
	public void method_8427(BlockPos blockPos, Block block, int i, int j) {
		this.pendingBlockActions.add(new BlockAction(blockPos, block, i, j));
	}

	private void sendBlockActions() {
		while (!this.pendingBlockActions.isEmpty()) {
			BlockAction blockAction = this.pendingBlockActions.removeFirst();
			if (this.method_14174(blockAction)) {
				this.server
					.method_3760()
					.sendToAround(
						null,
						(double)blockAction.method_8306().getX(),
						(double)blockAction.method_8306().getY(),
						(double)blockAction.method_8306().getZ(),
						64.0,
						this.field_9247.method_12460(),
						new BlockActionS2CPacket(blockAction.method_8306(), blockAction.method_8309(), blockAction.getType(), blockAction.getData())
					);
			}
		}
	}

	private boolean method_14174(BlockAction blockAction) {
		BlockState blockState = this.method_8320(blockAction.method_8306());
		return blockState.getBlock() == blockAction.method_8309()
			? blockState.method_11583(this, blockAction.method_8306(), blockAction.getType(), blockAction.getData())
			: false;
	}

	@Override
	public void close() throws IOException {
		this.field_9248.close();
		super.close();
	}

	public ServerTickScheduler<Block> method_14196() {
		return this.blockTickScheduler;
	}

	public ServerTickScheduler<Fluid> method_14179() {
		return this.fluidTickScheduler;
	}

	@Nonnull
	@Override
	public MinecraftServer getServer() {
		return this.server;
	}

	public PortalForcer getPortalForcer() {
		return this.portalForcer;
	}

	public StructureManager getStructureManager() {
		return this.worldSaveHandler.getStructureManager();
	}

	public <T extends ParticleParameters> int method_14199(T particleParameters, double d, double e, double f, int i, double g, double h, double j, double k) {
		ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(
			particleParameters, false, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i
		);
		int l = 0;

		for (int m = 0; m < this.players.size(); m++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(m);
			if (this.method_14191(serverPlayerEntity, false, d, e, f, particleS2CPacket)) {
				l++;
			}
		}

		return l;
	}

	public <T extends ParticleParameters> boolean method_14166(
		ServerPlayerEntity serverPlayerEntity, T particleParameters, boolean bl, double d, double e, double f, int i, double g, double h, double j, double k
	) {
		Packet<?> packet = new ParticleS2CPacket(particleParameters, bl, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i);
		return this.method_14191(serverPlayerEntity, bl, d, e, f, packet);
	}

	private boolean method_14191(ServerPlayerEntity serverPlayerEntity, boolean bl, double d, double e, double f, Packet<?> packet) {
		if (serverPlayerEntity.getServerWorld() != this) {
			return false;
		} else {
			BlockPos blockPos = serverPlayerEntity.method_5704();
			double g = blockPos.squaredDistanceTo(d, e, f);
			if (!(g <= 1024.0) && (!bl || !(g <= 262144.0))) {
				return false;
			} else {
				serverPlayerEntity.field_13987.sendPacket(packet);
				return true;
			}
		}
	}

	@Nullable
	@Override
	public Entity getEntityById(int i) {
		return this.entitiesById.get(i);
	}

	@Nullable
	public Entity getEntity(UUID uUID) {
		return (Entity)this.entitiesByUuid.get(uUID);
	}

	@Nullable
	@Override
	public BlockPos method_8487(String string, BlockPos blockPos, int i, boolean bl) {
		return this.method_14178().getChunkGenerator().method_12103(this, string, blockPos, i, bl);
	}

	@Override
	public RecipeManager getRecipeManager() {
		return this.server.getRecipeManager();
	}

	@Override
	public TagManager method_8514() {
		return this.server.method_3801();
	}

	@Override
	public void setTime(long l) {
		super.setTime(l);
		this.field_9232.method_143().processEvents(this.server, l);
	}

	@Override
	public boolean isSavingDisabled() {
		return this.savingDisabled;
	}

	public void checkSessionLock() throws SessionLockException {
		this.worldSaveHandler.checkSessionLock();
	}

	public WorldSaveHandler getSaveHandler() {
		return this.worldSaveHandler;
	}

	public PersistentStateManager getPersistentStateManager() {
		return this.method_14178().getPersistentStateManager();
	}

	@Nullable
	@Override
	public MapState method_17891(String string) {
		return this.getServer().method_3847(DimensionType.field_13072).getPersistentStateManager().get(() -> new MapState(string), string);
	}

	@Override
	public void method_17890(MapState mapState) {
		this.getServer().method_3847(DimensionType.field_13072).getPersistentStateManager().set(mapState);
	}

	@Override
	public int getNextMapId() {
		return this.getServer()
			.method_3847(DimensionType.field_13072)
			.getPersistentStateManager()
			.<IdCountsState>getOrCreate(IdCountsState::new, "idcounts")
			.getNextMapId();
	}

	@Override
	public void method_8554(BlockPos blockPos) {
		ChunkPos chunkPos = new ChunkPos(new BlockPos(this.field_9232.getSpawnX(), 0, this.field_9232.getSpawnZ()));
		super.method_8554(blockPos);
		this.method_14178().method_17300(ChunkTicketType.START, chunkPos, 11, Void.INSTANCE);
		this.method_14178().method_17297(ChunkTicketType.START, new ChunkPos(blockPos), 11, Void.INSTANCE);
	}

	public LongSet getForcedChunks() {
		ForcedChunkState forcedChunkState = this.getPersistentStateManager().get(ForcedChunkState::new, "chunks");
		return (LongSet)(forcedChunkState != null ? LongSets.unmodifiable(forcedChunkState.getChunks()) : LongSets.EMPTY_SET);
	}

	public boolean setChunkForced(int i, int j, boolean bl) {
		ForcedChunkState forcedChunkState = this.getPersistentStateManager().getOrCreate(ForcedChunkState::new, "chunks");
		ChunkPos chunkPos = new ChunkPos(i, j);
		long l = chunkPos.toLong();
		boolean bl2;
		if (bl) {
			bl2 = forcedChunkState.getChunks().add(l);
			if (bl2) {
				this.method_8497(i, j);
			}
		} else {
			bl2 = forcedChunkState.getChunks().remove(l);
		}

		forcedChunkState.setDirty(bl2);
		if (bl2) {
			this.method_14178().setChunkForced(chunkPos, bl);
		}

		return bl2;
	}

	@Override
	public List<ServerPlayerEntity> getPlayers() {
		return this.players;
	}

	@Override
	public void method_19282(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		BlockPos blockPos2 = blockPos.toImmutable();
		if (blockState.method_11602(BlockTags.field_18847) && !BedBlock.method_19284(blockState)) {
			this.getServer().execute(() -> {
				this.method_19494().method_19112(blockPos2);
				class_4209.method_19473(this, blockPos2, blockState, false);
			});
		}

		if (blockState2.method_11602(BlockTags.field_18847) && !BedBlock.method_19284(blockState2)) {
			this.getServer().execute(() -> {
				this.method_19494().method_19115(blockPos2, class_4158.method_19163(blockState2.getBlock()));
				class_4209.method_19473(this, blockPos2, blockState2, true);
			});
		}
	}

	public class_4153 method_19494() {
		return this.method_14178().method_19493();
	}

	public boolean method_19500(BlockPos blockPos) {
		return this.method_19497(blockPos, 1);
	}

	public boolean method_19497(BlockPos blockPos, int i) {
		return i > 4 ? false : this.method_19498(ChunkSectionPos.from(blockPos)) <= i;
	}

	public int method_19498(ChunkSectionPos chunkSectionPos) {
		return this.method_19494().method_19118(chunkSectionPos);
	}

	public RaidManager method_19495() {
		return this.field_18811;
	}

	@Nullable
	public Raid method_19502(BlockPos blockPos) {
		return this.field_18811.method_19209(blockPos);
	}

	public boolean method_19503(BlockPos blockPos) {
		return this.method_19502(blockPos) != null;
	}

	public void method_19496(class_4151 arg, Entity entity, class_4094 arg2) {
		arg2.method_18870(arg, entity);
	}
}
