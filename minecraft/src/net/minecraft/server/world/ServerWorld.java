package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5362;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Npc;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.StructureManager;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.CsvWriter;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IdCountsState;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorld extends World implements ServerWorldAccess {
	public static final BlockPos field_25144 = new BlockPos(100, 50, 0);
	private static final Logger LOGGER = LogManager.getLogger();
	private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectLinkedOpenHashMap<>();
	private final Map<UUID, Entity> entitiesByUuid = Maps.<UUID, Entity>newHashMap();
	private final Queue<Entity> entitiesToLoad = Queues.<Entity>newArrayDeque();
	private final List<ServerPlayerEntity> players = Lists.<ServerPlayerEntity>newArrayList();
	private final ServerChunkManager serverChunkManager;
	boolean inEntityTick;
	private final MinecraftServer server;
	private final ServerWorldProperties field_24456;
	public boolean savingDisabled;
	private boolean allPlayersSleeping;
	private int idleTimeout;
	private final PortalForcer portalForcer;
	private final ServerTickScheduler<Block> blockTickScheduler = new ServerTickScheduler<>(
		this, block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, this::tickBlock
	);
	private final ServerTickScheduler<Fluid> fluidTickScheduler = new ServerTickScheduler<>(
		this, fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::getId, this::tickFluid
	);
	private final Set<EntityNavigation> entityNavigations = Sets.<EntityNavigation>newHashSet();
	protected final RaidManager raidManager;
	private final ObjectLinkedOpenHashSet<BlockEvent> syncedBlockEventQueue = new ObjectLinkedOpenHashSet<>();
	private boolean inBlockTick;
	private final List<Spawner> field_25141;
	@Nullable
	private final EnderDragonFight enderDragonFight;
	private final StructureAccessor structureAccessor;
	private final boolean field_25143;

	public ServerWorld(
		MinecraftServer server,
		Executor workerExecutor,
		LevelStorage.Session session,
		ServerWorldProperties properties,
		RegistryKey<World> registryKey,
		RegistryKey<DimensionType> registryKey2,
		DimensionType dimensionType,
		WorldGenerationProgressListener generationProgressListener,
		ChunkGenerator chunkGenerator,
		boolean bl,
		long l,
		List<Spawner> list,
		boolean bl2
	) {
		super(properties, registryKey, registryKey2, dimensionType, server::getProfiler, false, bl, l);
		this.field_25143 = bl2;
		this.server = server;
		this.field_25141 = list;
		this.field_24456 = properties;
		this.serverChunkManager = new ServerChunkManager(
			this,
			session,
			server.getDataFixer(),
			server.getStructureManager(),
			workerExecutor,
			chunkGenerator,
			server.getPlayerManager().getViewDistance(),
			server.syncChunkWrites(),
			generationProgressListener,
			() -> server.getWorld(World.OVERWORLD).getPersistentStateManager()
		);
		this.portalForcer = new PortalForcer(this);
		this.calculateAmbientDarkness();
		this.initWeatherGradients();
		this.getWorldBorder().setMaxWorldBorderRadius(server.getMaxWorldBorderRadius());
		this.raidManager = this.getPersistentStateManager().getOrCreate(() -> new RaidManager(this), RaidManager.nameFor(this.getDimension()));
		if (!server.isSinglePlayer()) {
			properties.setGameMode(server.getDefaultGameMode());
		}

		this.structureAccessor = new StructureAccessor(this, server.getSaveProperties().getGeneratorOptions());
		if (this.getDimension().hasEnderDragonFight()) {
			this.enderDragonFight = new EnderDragonFight(this, server.getSaveProperties().getGeneratorOptions().getSeed(), server.getSaveProperties().method_29036());
		} else {
			this.enderDragonFight = null;
		}
	}

	public void method_27910(int i, int j, boolean bl, boolean bl2) {
		this.field_24456.setClearWeatherTime(i);
		this.field_24456.setRainTime(j);
		this.field_24456.setThunderTime(j);
		this.field_24456.setRaining(bl);
		this.field_24456.setThundering(bl2);
	}

	@Override
	public Biome getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
		return this.getChunkManager().getChunkGenerator().getBiomeSource().getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
	}

	public StructureAccessor getStructureAccessor() {
		return this.structureAccessor;
	}

	public void tick(BooleanSupplier shouldKeepTicking) {
		Profiler profiler = this.getProfiler();
		this.inBlockTick = true;
		profiler.push("world border");
		this.getWorldBorder().tick();
		profiler.swap("weather");
		boolean bl = this.isRaining();
		if (this.getDimension().hasSkyLight()) {
			if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
				int i = this.field_24456.getClearWeatherTime();
				int j = this.field_24456.getThunderTime();
				int k = this.field_24456.getRainTime();
				boolean bl2 = this.properties.isThundering();
				boolean bl3 = this.properties.isRaining();
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

				this.field_24456.setThunderTime(j);
				this.field_24456.setRainTime(k);
				this.field_24456.setClearWeatherTime(i);
				this.field_24456.setThundering(bl2);
				this.field_24456.setRaining(bl3);
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

		if (this.rainGradientPrev != this.rainGradient) {
			this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(7, this.rainGradient), this.getRegistryKey());
		}

		if (this.thunderGradientPrev != this.thunderGradient) {
			this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(8, this.thunderGradient), this.getRegistryKey());
		}

		if (bl != this.isRaining()) {
			if (bl) {
				this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(2, 0.0F));
			} else {
				this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(1, 0.0F));
			}

			this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(7, this.rainGradient));
			this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(8, this.thunderGradient));
		}

		if (this.allPlayersSleeping
			&& this.players.stream().noneMatch(serverPlayerEntity -> !serverPlayerEntity.isSpectator() && !serverPlayerEntity.isSleepingLongEnough())) {
			this.allPlayersSleeping = false;
			if (this.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
				long l = this.properties.getTimeOfDay() + 24000L;
				this.method_29199(l - l % 24000L);
			}

			this.wakeSleepingPlayers();
			if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
				this.resetWeather();
			}
		}

		this.calculateAmbientDarkness();
		this.method_29203();
		profiler.swap("chunkSource");
		this.getChunkManager().tick(shouldKeepTicking);
		profiler.swap("tickPending");
		if (!this.isDebugWorld()) {
			this.blockTickScheduler.tick();
			this.fluidTickScheduler.tick();
		}

		profiler.swap("raid");
		this.raidManager.tick();
		profiler.swap("blockEvents");
		this.processSyncedBlockEvents();
		this.inBlockTick = false;
		profiler.swap("entities");
		boolean bl4 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
		if (bl4) {
			this.resetIdleTimeout();
		}

		if (bl4 || this.idleTimeout++ < 300) {
			if (this.enderDragonFight != null) {
				this.enderDragonFight.tick();
			}

			this.inEntityTick = true;
			ObjectIterator<Entry<Entity>> objectIterator = this.entitiesById.int2ObjectEntrySet().iterator();

			while (objectIterator.hasNext()) {
				Entry<Entity> entry = (Entry<Entity>)objectIterator.next();
				Entity entity = (Entity)entry.getValue();
				Entity entity2 = entity.getVehicle();
				if (!this.server.shouldSpawnAnimals() && (entity instanceof AnimalEntity || entity instanceof WaterCreatureEntity)) {
					entity.remove();
				}

				if (!this.server.shouldSpawnNpcs() && entity instanceof Npc) {
					entity.remove();
				}

				profiler.push("checkDespawn");
				if (!entity.removed) {
					entity.checkDespawn();
				}

				profiler.pop();
				if (entity2 != null) {
					if (!entity2.removed && entity2.hasPassenger(entity)) {
						continue;
					}

					entity.stopRiding();
				}

				profiler.push("tick");
				if (!entity.removed && !(entity instanceof EnderDragonPart)) {
					this.tickEntity(this::tickEntity, entity);
				}

				profiler.pop();
				profiler.push("remove");
				if (entity.removed) {
					this.removeEntityFromChunk(entity);
					objectIterator.remove();
					this.unloadEntity(entity);
				}

				profiler.pop();
			}

			this.inEntityTick = false;

			Entity entity3;
			while ((entity3 = (Entity)this.entitiesToLoad.poll()) != null) {
				this.loadEntityUnchecked(entity3);
			}

			this.tickBlockEntities();
		}

		profiler.pop();
	}

	protected void method_29203() {
		if (this.field_25143) {
			long l = this.properties.getTime() + 1L;
			this.field_24456.method_29034(l);
			this.field_24456.getScheduledEvents().processEvents(this.server, l);
			if (this.properties.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
				this.method_29199(this.properties.getTimeOfDay() + 1L);
			}
		}
	}

	public void method_29199(long l) {
		this.field_24456.method_29035(l);
	}

	public void method_29202(boolean bl, boolean bl2) {
		for (Spawner spawner : this.field_25141) {
			spawner.spawn(this, bl, bl2);
		}
	}

	private void wakeSleepingPlayers() {
		((List)this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList()))
			.forEach(serverPlayerEntity -> serverPlayerEntity.wakeUp(false, false));
	}

	public void tickChunk(WorldChunk chunk, int randomTickSpeed) {
		ChunkPos chunkPos = chunk.getPos();
		boolean bl = this.isRaining();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		Profiler profiler = this.getProfiler();
		profiler.push("thunder");
		if (bl && this.isThundering() && this.random.nextInt(100000) == 0) {
			BlockPos blockPos = this.getSurface(this.getRandomPosInChunk(i, 0, j, 15));
			if (this.hasRain(blockPos)) {
				LocalDifficulty localDifficulty = this.getLocalDifficulty(blockPos);
				boolean bl2 = this.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && this.random.nextDouble() < (double)localDifficulty.getLocalDifficulty() * 0.01;
				if (bl2) {
					SkeletonHorseEntity skeletonHorseEntity = EntityType.SKELETON_HORSE.create(this);
					skeletonHorseEntity.setTrapped(true);
					skeletonHorseEntity.setBreedingAge(0);
					skeletonHorseEntity.updatePosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
					this.spawnEntity(skeletonHorseEntity);
				}

				LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(this);
				lightningEntity.method_29495(Vec3d.ofBottomCenter(blockPos));
				lightningEntity.method_29498(bl2);
				this.spawnEntity(lightningEntity);
			}
		}

		profiler.swap("iceandsnow");
		if (this.random.nextInt(16) == 0) {
			BlockPos blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, this.getRandomPosInChunk(i, 0, j, 15));
			BlockPos blockPos2 = blockPos.down();
			Biome biome = this.getBiome(blockPos);
			if (biome.canSetIce(this, blockPos2)) {
				this.setBlockState(blockPos2, Blocks.ICE.getDefaultState());
			}

			if (bl && biome.canSetSnow(this, blockPos)) {
				this.setBlockState(blockPos, Blocks.SNOW.getDefaultState());
			}

			if (bl && this.getBiome(blockPos2).getPrecipitation() == Biome.Precipitation.RAIN) {
				this.getBlockState(blockPos2).getBlock().rainTick(this, blockPos2);
			}
		}

		profiler.swap("tickBlocks");
		if (randomTickSpeed > 0) {
			for (ChunkSection chunkSection : chunk.getSectionArray()) {
				if (chunkSection != WorldChunk.EMPTY_SECTION && chunkSection.hasRandomTicks()) {
					int k = chunkSection.getYOffset();

					for (int l = 0; l < randomTickSpeed; l++) {
						BlockPos blockPos3 = this.getRandomPosInChunk(i, k, j, 15);
						profiler.push("randomTick");
						BlockState blockState = chunkSection.getBlockState(blockPos3.getX() - i, blockPos3.getY() - k, blockPos3.getZ() - j);
						if (blockState.hasRandomTicks()) {
							blockState.randomTick(this, blockPos3, this.random);
						}

						FluidState fluidState = blockState.getFluidState();
						if (fluidState.hasRandomTicks()) {
							fluidState.onRandomTick(this, blockPos3, this.random);
						}

						profiler.pop();
					}
				}
			}
		}

		profiler.pop();
	}

	protected BlockPos getSurface(BlockPos pos) {
		BlockPos blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos);
		Box box = new Box(blockPos, new BlockPos(blockPos.getX(), this.getHeight(), blockPos.getZ())).expand(3.0);
		List<LivingEntity> list = this.getEntities(
			LivingEntity.class, box, livingEntity -> livingEntity != null && livingEntity.isAlive() && this.isSkyVisible(livingEntity.getBlockPos())
		);
		if (!list.isEmpty()) {
			return ((LivingEntity)list.get(this.random.nextInt(list.size()))).getBlockPos();
		} else {
			if (blockPos.getY() == -1) {
				blockPos = blockPos.up(2);
			}

			return blockPos;
		}
	}

	public boolean isInBlockTick() {
		return this.inBlockTick;
	}

	public void updateSleepingPlayers() {
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

	public ServerScoreboard getScoreboard() {
		return this.server.getScoreboard();
	}

	private void resetWeather() {
		this.field_24456.setRainTime(0);
		this.field_24456.setRaining(false);
		this.field_24456.setThunderTime(0);
		this.field_24456.setThundering(false);
	}

	public void resetIdleTimeout() {
		this.idleTimeout = 0;
	}

	private void tickFluid(ScheduledTick<Fluid> tick) {
		FluidState fluidState = this.getFluidState(tick.pos);
		if (fluidState.getFluid() == tick.getObject()) {
			fluidState.onScheduledTick(this, tick.pos);
		}
	}

	private void tickBlock(ScheduledTick<Block> tick) {
		BlockState blockState = this.getBlockState(tick.pos);
		if (blockState.isOf(tick.getObject())) {
			blockState.scheduledTick(this, tick.pos, this.random);
		}
	}

	public void tickEntity(Entity entity) {
		if (!(entity instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(entity)) {
			this.checkChunk(entity);
		} else {
			entity.resetPosition(entity.getX(), entity.getY(), entity.getZ());
			entity.prevYaw = entity.yaw;
			entity.prevPitch = entity.pitch;
			if (entity.updateNeeded) {
				entity.age++;
				Profiler profiler = this.getProfiler();
				profiler.push((Supplier<String>)(() -> Registry.ENTITY_TYPE.getId(entity.getType()).toString()));
				profiler.visit("tickNonPassenger");
				entity.tick();
				profiler.pop();
			}

			this.checkChunk(entity);
			if (entity.updateNeeded) {
				for (Entity entity2 : entity.getPassengerList()) {
					this.tickPassenger(entity, entity2);
				}
			}
		}
	}

	public void tickPassenger(Entity vehicle, Entity passenger) {
		if (passenger.removed || passenger.getVehicle() != vehicle) {
			passenger.stopRiding();
		} else if (passenger instanceof PlayerEntity || this.getChunkManager().shouldTickEntity(passenger)) {
			passenger.resetPosition(passenger.getX(), passenger.getY(), passenger.getZ());
			passenger.prevYaw = passenger.yaw;
			passenger.prevPitch = passenger.pitch;
			if (passenger.updateNeeded) {
				passenger.age++;
				Profiler profiler = this.getProfiler();
				profiler.push((Supplier<String>)(() -> Registry.ENTITY_TYPE.getId(passenger.getType()).toString()));
				profiler.visit("tickPassenger");
				passenger.tickRiding();
				profiler.pop();
			}

			this.checkChunk(passenger);
			if (passenger.updateNeeded) {
				for (Entity entity : passenger.getPassengerList()) {
					this.tickPassenger(passenger, entity);
				}
			}
		}
	}

	public void checkChunk(Entity entity) {
		if (entity.method_29240()) {
			this.getProfiler().push("chunkCheck");
			int i = MathHelper.floor(entity.getX() / 16.0);
			int j = MathHelper.floor(entity.getY() / 16.0);
			int k = MathHelper.floor(entity.getZ() / 16.0);
			if (!entity.updateNeeded || entity.chunkX != i || entity.chunkY != j || entity.chunkZ != k) {
				if (entity.updateNeeded && this.isChunkLoaded(entity.chunkX, entity.chunkZ)) {
					this.getChunk(entity.chunkX, entity.chunkZ).remove(entity, entity.chunkY);
				}

				if (!entity.teleportRequested() && !this.isChunkLoaded(i, k)) {
					if (entity.updateNeeded) {
						LOGGER.warn("Entity {} left loaded chunk area", entity);
					}

					entity.updateNeeded = false;
				} else {
					this.getChunk(i, k).addEntity(entity);
				}
			}

			this.getProfiler().pop();
		}
	}

	@Override
	public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
		return !this.server.isSpawnProtected(this, pos, player) && this.getWorldBorder().contains(pos);
	}

	public void save(@Nullable ProgressListener progressListener, boolean flush, boolean bl) {
		ServerChunkManager serverChunkManager = this.getChunkManager();
		if (!bl) {
			if (progressListener != null) {
				progressListener.method_15412(new TranslatableText("menu.savingLevel"));
			}

			this.saveLevel();
			if (progressListener != null) {
				progressListener.method_15414(new TranslatableText("menu.savingChunks"));
			}

			serverChunkManager.save(flush);
		}
	}

	private void saveLevel() {
		if (this.enderDragonFight != null) {
			this.server.getSaveProperties().method_29037(this.enderDragonFight.toTag());
		}

		this.getChunkManager().getPersistentStateManager().save();
	}

	public List<Entity> getEntities(@Nullable EntityType<?> entityType, Predicate<? super Entity> predicate) {
		List<Entity> list = Lists.<Entity>newArrayList();
		ServerChunkManager serverChunkManager = this.getChunkManager();

		for (Entity entity : this.entitiesById.values()) {
			if ((entityType == null || entity.getType() == entityType)
				&& serverChunkManager.isChunkLoaded(MathHelper.floor(entity.getX()) >> 4, MathHelper.floor(entity.getZ()) >> 4)
				&& predicate.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	public List<EnderDragonEntity> getAliveEnderDragons() {
		List<EnderDragonEntity> list = Lists.<EnderDragonEntity>newArrayList();

		for (Entity entity : this.entitiesById.values()) {
			if (entity instanceof EnderDragonEntity && entity.isAlive()) {
				list.add((EnderDragonEntity)entity);
			}
		}

		return list;
	}

	public List<ServerPlayerEntity> getPlayers(Predicate<? super ServerPlayerEntity> predicate) {
		List<ServerPlayerEntity> list = Lists.<ServerPlayerEntity>newArrayList();

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (predicate.test(serverPlayerEntity)) {
				list.add(serverPlayerEntity);
			}
		}

		return list;
	}

	@Nullable
	public ServerPlayerEntity getRandomAlivePlayer() {
		List<ServerPlayerEntity> list = this.getPlayers(LivingEntity::isAlive);
		return list.isEmpty() ? null : (ServerPlayerEntity)list.get(this.random.nextInt(list.size()));
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		return this.addEntity(entity);
	}

	public boolean tryLoadEntity(Entity entity) {
		return this.addEntity(entity);
	}

	public void onDimensionChanged(Entity entity) {
		boolean bl = entity.teleporting;
		entity.teleporting = true;
		this.tryLoadEntity(entity);
		entity.teleporting = bl;
		this.checkChunk(entity);
	}

	public void onPlayerTeleport(ServerPlayerEntity player) {
		this.addPlayer(player);
		this.checkChunk(player);
	}

	public void onPlayerChangeDimension(ServerPlayerEntity player) {
		this.addPlayer(player);
		this.checkChunk(player);
	}

	public void onPlayerConnected(ServerPlayerEntity player) {
		this.addPlayer(player);
	}

	public void onPlayerRespawned(ServerPlayerEntity player) {
		this.addPlayer(player);
	}

	private void addPlayer(ServerPlayerEntity player) {
		Entity entity = (Entity)this.entitiesByUuid.get(player.getUuid());
		if (entity != null) {
			LOGGER.warn("Force-added player with duplicate UUID {}", player.getUuid().toString());
			entity.detach();
			this.removePlayer((ServerPlayerEntity)entity);
		}

		this.players.add(player);
		this.updateSleepingPlayers();
		Chunk chunk = this.getChunk(MathHelper.floor(player.getX() / 16.0), MathHelper.floor(player.getZ() / 16.0), ChunkStatus.FULL, true);
		if (chunk instanceof WorldChunk) {
			chunk.addEntity(player);
		}

		this.loadEntityUnchecked(player);
	}

	private boolean addEntity(Entity entity) {
		if (entity.removed) {
			LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getId(entity.getType()));
			return false;
		} else if (this.checkUuid(entity)) {
			return false;
		} else {
			Chunk chunk = this.getChunk(MathHelper.floor(entity.getX() / 16.0), MathHelper.floor(entity.getZ() / 16.0), ChunkStatus.FULL, entity.teleporting);
			if (!(chunk instanceof WorldChunk)) {
				return false;
			} else {
				chunk.addEntity(entity);
				this.loadEntityUnchecked(entity);
				return true;
			}
		}
	}

	public boolean loadEntity(Entity entity) {
		if (this.checkUuid(entity)) {
			return false;
		} else {
			this.loadEntityUnchecked(entity);
			return true;
		}
	}

	private boolean checkUuid(Entity entity) {
		Entity entity2 = (Entity)this.entitiesByUuid.get(entity.getUuid());
		if (entity2 == null) {
			return false;
		} else {
			LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getId(entity2.getType()), entity.getUuid().toString());
			return true;
		}
	}

	public void unloadEntities(WorldChunk chunk) {
		this.unloadedBlockEntities.addAll(chunk.getBlockEntities().values());
		TypeFilterableList[] var2 = chunk.getEntitySectionArray();
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; var4++) {
			for (Entity entity : var2[var4]) {
				if (!(entity instanceof ServerPlayerEntity)) {
					if (this.inEntityTick) {
						throw (IllegalStateException)Util.throwOrPause(new IllegalStateException("Removing entity while ticking!"));
					}

					this.entitiesById.remove(entity.getEntityId());
					this.unloadEntity(entity);
				}
			}
		}
	}

	public void unloadEntity(Entity entity) {
		if (entity instanceof EnderDragonEntity) {
			for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).getBodyParts()) {
				enderDragonPart.remove();
			}
		}

		this.entitiesByUuid.remove(entity.getUuid());
		this.getChunkManager().unloadEntity(entity);
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
			this.players.remove(serverPlayerEntity);
		}

		this.getScoreboard().resetEntityScore(entity);
		if (entity instanceof MobEntity) {
			this.entityNavigations.remove(((MobEntity)entity).getNavigation());
		}
	}

	private void loadEntityUnchecked(Entity entity) {
		if (this.inEntityTick) {
			this.entitiesToLoad.add(entity);
		} else {
			this.entitiesById.put(entity.getEntityId(), entity);
			if (entity instanceof EnderDragonEntity) {
				for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).getBodyParts()) {
					this.entitiesById.put(enderDragonPart.getEntityId(), enderDragonPart);
				}
			}

			this.entitiesByUuid.put(entity.getUuid(), entity);
			this.getChunkManager().loadEntity(entity);
			if (entity instanceof MobEntity) {
				this.entityNavigations.add(((MobEntity)entity).getNavigation());
			}
		}
	}

	public void removeEntity(Entity entity) {
		if (this.inEntityTick) {
			throw (IllegalStateException)Util.throwOrPause(new IllegalStateException("Removing entity while ticking!"));
		} else {
			this.removeEntityFromChunk(entity);
			this.entitiesById.remove(entity.getEntityId());
			this.unloadEntity(entity);
		}
	}

	private void removeEntityFromChunk(Entity entity) {
		Chunk chunk = this.getChunk(entity.chunkX, entity.chunkZ, ChunkStatus.FULL, false);
		if (chunk instanceof WorldChunk) {
			((WorldChunk)chunk).remove(entity);
		}
	}

	public void removePlayer(ServerPlayerEntity player) {
		player.remove();
		this.removeEntity(player);
		this.updateSleepingPlayers();
	}

	@Override
	public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
		for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
			if (serverPlayerEntity != null && serverPlayerEntity.world == this && serverPlayerEntity.getEntityId() != entityId) {
				double d = (double)pos.getX() - serverPlayerEntity.getX();
				double e = (double)pos.getY() - serverPlayerEntity.getY();
				double f = (double)pos.getZ() - serverPlayerEntity.getZ();
				if (d * d + e * e + f * f < 1024.0) {
					serverPlayerEntity.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(entityId, pos, progress));
				}
			}
		}
	}

	@Override
	public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
		this.server
			.getPlayerManager()
			.sendToAround(
				player, x, y, z, volume > 1.0F ? (double)(16.0F * volume) : 16.0, this.getRegistryKey(), new PlaySoundS2CPacket(sound, category, x, y, z, volume, pitch)
			);
	}

	@Override
	public void playSoundFromEntity(@Nullable PlayerEntity player, Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
		this.server
			.getPlayerManager()
			.sendToAround(
				player,
				entity.getX(),
				entity.getY(),
				entity.getZ(),
				volume > 1.0F ? (double)(16.0F * volume) : 16.0,
				this.getRegistryKey(),
				new PlaySoundFromEntityS2CPacket(sound, category, entity, volume, pitch)
			);
	}

	@Override
	public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
		this.server.getPlayerManager().sendToAll(new WorldEventS2CPacket(eventId, pos, data, true));
	}

	@Override
	public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {
		this.server
			.getPlayerManager()
			.sendToAround(
				player, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 64.0, this.getRegistryKey(), new WorldEventS2CPacket(eventId, pos, data, false)
			);
	}

	@Override
	public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
		this.getChunkManager().markForUpdate(pos);
		VoxelShape voxelShape = oldState.getCollisionShape(this, pos);
		VoxelShape voxelShape2 = newState.getCollisionShape(this, pos);
		if (VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.NOT_SAME)) {
			for (EntityNavigation entityNavigation : this.entityNavigations) {
				if (!entityNavigation.shouldRecalculatePath()) {
					entityNavigation.onBlockChanged(pos);
				}
			}
		}
	}

	@Override
	public void sendEntityStatus(Entity entity, byte status) {
		this.getChunkManager().sendToNearbyPlayers(entity, new EntityStatusS2CPacket(entity, status));
	}

	public ServerChunkManager getChunkManager() {
		return this.serverChunkManager;
	}

	@Override
	public Explosion createExplosion(
		@Nullable Entity entity,
		@Nullable DamageSource damageSource,
		@Nullable class_5362 arg,
		double d,
		double e,
		double f,
		float g,
		boolean bl,
		Explosion.DestructionType destructionType
	) {
		Explosion explosion = new Explosion(this, entity, damageSource, arg, d, e, f, g, bl, destructionType);
		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(false);
		if (destructionType == Explosion.DestructionType.NONE) {
			explosion.clearAffectedBlocks();
		}

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (serverPlayerEntity.squaredDistanceTo(d, e, f) < 4096.0) {
				serverPlayerEntity.networkHandler
					.sendPacket(new ExplosionS2CPacket(d, e, f, g, explosion.getAffectedBlocks(), (Vec3d)explosion.getAffectedPlayers().get(serverPlayerEntity)));
			}
		}

		return explosion;
	}

	@Override
	public void addSyncedBlockEvent(BlockPos pos, Block block, int type, int data) {
		this.syncedBlockEventQueue.add(new BlockEvent(pos, block, type, data));
	}

	private void processSyncedBlockEvents() {
		while (!this.syncedBlockEventQueue.isEmpty()) {
			BlockEvent blockEvent = this.syncedBlockEventQueue.removeFirst();
			if (this.processBlockEvent(blockEvent)) {
				this.server
					.getPlayerManager()
					.sendToAround(
						null,
						(double)blockEvent.getPos().getX(),
						(double)blockEvent.getPos().getY(),
						(double)blockEvent.getPos().getZ(),
						64.0,
						this.getRegistryKey(),
						new BlockEventS2CPacket(blockEvent.getPos(), blockEvent.getBlock(), blockEvent.getType(), blockEvent.getData())
					);
			}
		}
	}

	private boolean processBlockEvent(BlockEvent event) {
		BlockState blockState = this.getBlockState(event.getPos());
		return blockState.isOf(event.getBlock()) ? blockState.onSyncedBlockEvent(this, event.getPos(), event.getType(), event.getData()) : false;
	}

	public ServerTickScheduler<Block> getBlockTickScheduler() {
		return this.blockTickScheduler;
	}

	public ServerTickScheduler<Fluid> getFluidTickScheduler() {
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
		return this.server.getStructureManager();
	}

	public <T extends ParticleEffect> int spawnParticles(
		T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed
	) {
		ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(particle, false, x, y, z, (float)deltaX, (float)deltaY, (float)deltaZ, (float)speed, count);
		int i = 0;

		for (int j = 0; j < this.players.size(); j++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(j);
			if (this.sendToPlayerIfNearby(serverPlayerEntity, false, x, y, z, particleS2CPacket)) {
				i++;
			}
		}

		return i;
	}

	public <T extends ParticleEffect> boolean spawnParticles(
		ServerPlayerEntity viewer, T particle, boolean force, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed
	) {
		Packet<?> packet = new ParticleS2CPacket(particle, force, x, y, z, (float)deltaX, (float)deltaY, (float)deltaZ, (float)speed, count);
		return this.sendToPlayerIfNearby(viewer, force, x, y, z, packet);
	}

	private boolean sendToPlayerIfNearby(ServerPlayerEntity player, boolean force, double x, double y, double z, Packet<?> packet) {
		if (player.getServerWorld() != this) {
			return false;
		} else {
			BlockPos blockPos = player.getBlockPos();
			if (blockPos.isWithinDistance(new Vec3d(x, y, z), force ? 512.0 : 32.0)) {
				player.networkHandler.sendPacket(packet);
				return true;
			} else {
				return false;
			}
		}
	}

	@Nullable
	@Override
	public Entity getEntityById(int id) {
		return this.entitiesById.get(id);
	}

	@Nullable
	public Entity getEntity(UUID uUID) {
		return (Entity)this.entitiesByUuid.get(uUID);
	}

	@Nullable
	public BlockPos locateStructure(StructureFeature<?> structureFeature, BlockPos blockPos, int i, boolean bl) {
		return !this.server.getSaveProperties().getGeneratorOptions().shouldGenerateStructures()
			? null
			: this.getChunkManager().getChunkGenerator().locateStructure(this, structureFeature, blockPos, i, bl);
	}

	@Nullable
	public BlockPos locateBiome(Biome biome, BlockPos blockPos, int i, int j) {
		return this.getChunkManager()
			.getChunkGenerator()
			.getBiomeSource()
			.method_24385(blockPos.getX(), blockPos.getY(), blockPos.getZ(), i, j, ImmutableList.of(biome), this.random, true);
	}

	@Override
	public RecipeManager getRecipeManager() {
		return this.server.getRecipeManager();
	}

	@Override
	public RegistryTagManager getTagManager() {
		return this.server.getTagManager();
	}

	@Override
	public boolean isSavingDisabled() {
		return this.savingDisabled;
	}

	public PersistentStateManager getPersistentStateManager() {
		return this.getChunkManager().getPersistentStateManager();
	}

	@Nullable
	@Override
	public MapState getMapState(String id) {
		return this.getServer().getWorld(World.OVERWORLD).getPersistentStateManager().get(() -> new MapState(id), id);
	}

	@Override
	public void putMapState(MapState mapState) {
		this.getServer().getWorld(World.OVERWORLD).getPersistentStateManager().set(mapState);
	}

	@Override
	public int getNextMapId() {
		return this.getServer().getWorld(World.OVERWORLD).getPersistentStateManager().<IdCountsState>getOrCreate(IdCountsState::new, "idcounts").getNextMapId();
	}

	public void setSpawnPos(BlockPos blockPos) {
		ChunkPos chunkPos = new ChunkPos(new BlockPos(this.properties.getSpawnX(), 0, this.properties.getSpawnZ()));
		this.properties.setSpawnPos(blockPos);
		this.getChunkManager().removeTicket(ChunkTicketType.START, chunkPos, 11, Unit.INSTANCE);
		this.getChunkManager().addTicket(ChunkTicketType.START, new ChunkPos(blockPos), 11, Unit.INSTANCE);
		this.getServer().getPlayerManager().sendToAll(new PlayerSpawnPositionS2CPacket(blockPos));
	}

	public BlockPos getSpawnPos() {
		BlockPos blockPos = new BlockPos(this.properties.getSpawnX(), this.properties.getSpawnY(), this.properties.getSpawnZ());
		if (!this.getWorldBorder().contains(blockPos)) {
			blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
		}

		return blockPos;
	}

	public LongSet getForcedChunks() {
		ForcedChunkState forcedChunkState = this.getPersistentStateManager().get(ForcedChunkState::new, "chunks");
		return (LongSet)(forcedChunkState != null ? LongSets.unmodifiable(forcedChunkState.getChunks()) : LongSets.EMPTY_SET);
	}

	public boolean setChunkForced(int x, int z, boolean forced) {
		ForcedChunkState forcedChunkState = this.getPersistentStateManager().getOrCreate(ForcedChunkState::new, "chunks");
		ChunkPos chunkPos = new ChunkPos(x, z);
		long l = chunkPos.toLong();
		boolean bl;
		if (forced) {
			bl = forcedChunkState.getChunks().add(l);
			if (bl) {
				this.getChunk(x, z);
			}
		} else {
			bl = forcedChunkState.getChunks().remove(l);
		}

		forcedChunkState.setDirty(bl);
		if (bl) {
			this.getChunkManager().setChunkForced(chunkPos, forced);
		}

		return bl;
	}

	@Override
	public List<ServerPlayerEntity> getPlayers() {
		return this.players;
	}

	@Override
	public void onBlockChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock) {
		Optional<PointOfInterestType> optional = PointOfInterestType.from(oldBlock);
		Optional<PointOfInterestType> optional2 = PointOfInterestType.from(newBlock);
		if (!Objects.equals(optional, optional2)) {
			BlockPos blockPos = pos.toImmutable();
			optional.ifPresent(pointOfInterestType -> this.getServer().execute(() -> {
					this.getPointOfInterestStorage().remove(blockPos);
					DebugInfoSender.sendPoiRemoval(this, blockPos);
				}));
			optional2.ifPresent(pointOfInterestType -> this.getServer().execute(() -> {
					this.getPointOfInterestStorage().add(blockPos, pointOfInterestType);
					DebugInfoSender.sendPoiAddition(this, blockPos);
				}));
		}
	}

	public PointOfInterestStorage getPointOfInterestStorage() {
		return this.getChunkManager().getPointOfInterestStorage();
	}

	public boolean isNearOccupiedPointOfInterest(BlockPos pos) {
		return this.isNearOccupiedPointOfInterest(pos, 1);
	}

	public boolean isNearOccupiedPointOfInterest(ChunkSectionPos sectionPos) {
		return this.isNearOccupiedPointOfInterest(sectionPos.getCenterPos());
	}

	public boolean isNearOccupiedPointOfInterest(BlockPos pos, int maxDistance) {
		return maxDistance > 6 ? false : this.getOccupiedPointOfInterestDistance(ChunkSectionPos.from(pos)) <= maxDistance;
	}

	public int getOccupiedPointOfInterestDistance(ChunkSectionPos pos) {
		return this.getPointOfInterestStorage().getDistanceFromNearestOccupied(pos);
	}

	public RaidManager getRaidManager() {
		return this.raidManager;
	}

	@Nullable
	public Raid getRaidAt(BlockPos pos) {
		return this.raidManager.getRaidAt(pos, 9216);
	}

	public boolean hasRaidAt(BlockPos pos) {
		return this.getRaidAt(pos) != null;
	}

	public void handleInteraction(EntityInteraction interaction, Entity entity, InteractionObserver observer) {
		observer.onInteractionWith(interaction, entity);
	}

	public void dump(Path path) throws IOException {
		ThreadedAnvilChunkStorage threadedAnvilChunkStorage = this.getChunkManager().threadedAnvilChunkStorage;
		Writer writer = Files.newBufferedWriter(path.resolve("stats.txt"));
		Throwable path2 = null;

		try {
			writer.write(String.format("spawning_chunks: %d\n", threadedAnvilChunkStorage.getTicketManager().getSpawningChunkCount()));
			SpawnHelper.Info info = this.getChunkManager().getSpawnInfo();
			if (info != null) {
				for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<SpawnGroup> entry : info.getGroupToCount().object2IntEntrySet()) {
					writer.write(String.format("spawn_count.%s: %d\n", ((SpawnGroup)entry.getKey()).getName(), entry.getIntValue()));
				}
			}

			writer.write(String.format("entities: %d\n", this.entitiesById.size()));
			writer.write(String.format("block_entities: %d\n", this.blockEntities.size()));
			writer.write(String.format("block_ticks: %d\n", this.getBlockTickScheduler().getTicks()));
			writer.write(String.format("fluid_ticks: %d\n", this.getFluidTickScheduler().getTicks()));
			writer.write("distance_manager: " + threadedAnvilChunkStorage.getTicketManager().toDumpString() + "\n");
			writer.write(String.format("pending_tasks: %d\n", this.getChunkManager().getPendingTasks()));
		} catch (Throwable var121) {
			path2 = var121;
			throw var121;
		} finally {
			if (writer != null) {
				if (path2 != null) {
					try {
						writer.close();
					} catch (Throwable var112) {
						path2.addSuppressed(var112);
					}
				} else {
					writer.close();
				}
			}
		}

		CrashReport crashReport = new CrashReport("Level dump", new Exception("dummy"));
		this.addDetailsToCrashReport(crashReport);
		Writer writer2 = Files.newBufferedWriter(path.resolve("example_crash.txt"));
		Throwable var126 = null;

		try {
			writer2.write(crashReport.asString());
		} catch (Throwable var116) {
			var126 = var116;
			throw var116;
		} finally {
			if (writer2 != null) {
				if (var126 != null) {
					try {
						writer2.close();
					} catch (Throwable var111) {
						var126.addSuppressed(var111);
					}
				} else {
					writer2.close();
				}
			}
		}

		Path path2x = path.resolve("chunks.csv");
		Writer writer3 = Files.newBufferedWriter(path2x);
		Throwable var129 = null;

		try {
			threadedAnvilChunkStorage.dump(writer3);
		} catch (Throwable var115) {
			var129 = var115;
			throw var115;
		} finally {
			if (writer3 != null) {
				if (var129 != null) {
					try {
						writer3.close();
					} catch (Throwable var110) {
						var129.addSuppressed(var110);
					}
				} else {
					writer3.close();
				}
			}
		}

		Path path3 = path.resolve("entities.csv");
		Writer writer4 = Files.newBufferedWriter(path3);
		Throwable var132 = null;

		try {
			dumpEntities(writer4, this.entitiesById.values());
		} catch (Throwable var114) {
			var132 = var114;
			throw var114;
		} finally {
			if (writer4 != null) {
				if (var132 != null) {
					try {
						writer4.close();
					} catch (Throwable var109) {
						var132.addSuppressed(var109);
					}
				} else {
					writer4.close();
				}
			}
		}

		Path path4 = path.resolve("block_entities.csv");
		Writer writer5 = Files.newBufferedWriter(path4);
		Throwable var8 = null;

		try {
			this.dumpBlockEntities(writer5);
		} catch (Throwable var113) {
			var8 = var113;
			throw var113;
		} finally {
			if (writer5 != null) {
				if (var8 != null) {
					try {
						writer5.close();
					} catch (Throwable var108) {
						var8.addSuppressed(var108);
					}
				} else {
					writer5.close();
				}
			}
		}
	}

	private static void dumpEntities(Writer writer, Iterable<Entity> entities) throws IOException {
		CsvWriter csvWriter = CsvWriter.makeHeader()
			.addColumn("x")
			.addColumn("y")
			.addColumn("z")
			.addColumn("uuid")
			.addColumn("type")
			.addColumn("alive")
			.addColumn("display_name")
			.addColumn("custom_name")
			.startBody(writer);

		for (Entity entity : entities) {
			Text text = entity.getCustomName();
			Text text2 = entity.getDisplayName();
			csvWriter.printRow(
				entity.getX(),
				entity.getY(),
				entity.getZ(),
				entity.getUuid(),
				Registry.ENTITY_TYPE.getId(entity.getType()),
				entity.isAlive(),
				text2.getString(),
				text != null ? text.getString() : null
			);
		}
	}

	private void dumpBlockEntities(Writer writer) throws IOException {
		CsvWriter csvWriter = CsvWriter.makeHeader().addColumn("x").addColumn("y").addColumn("z").addColumn("type").startBody(writer);

		for (BlockEntity blockEntity : this.blockEntities) {
			BlockPos blockPos = blockEntity.getPos();
			csvWriter.printRow(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType()));
		}
	}

	@VisibleForTesting
	public void clearUpdatesInArea(BlockBox box) {
		this.syncedBlockEventQueue.removeIf(blockEvent -> box.contains(blockEvent.getPos()));
	}

	@Override
	public void updateNeighbors(BlockPos pos, Block block) {
		if (!this.isDebugWorld()) {
			this.updateNeighborsAlways(pos, block);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getBrightness(Direction direction, boolean shaded) {
		return 1.0F;
	}

	public Iterable<Entity> iterateEntities() {
		return Iterables.unmodifiableIterable(this.entitiesById.values());
	}

	public String toString() {
		return "ServerLevel[" + this.field_24456.getLevelName() + "]";
	}

	public boolean method_28125() {
		return this.server.getSaveProperties().getGeneratorOptions().isFlatWorld();
	}

	@Override
	public long getSeed() {
		return this.server.getSaveProperties().getGeneratorOptions().getSeed();
	}

	@Nullable
	public EnderDragonFight method_29198() {
		return this.enderDragonFight;
	}

	public static void method_29200(ServerWorld serverWorld) {
		BlockPos blockPos = field_25144;
		int i = blockPos.getX();
		int j = blockPos.getY() - 2;
		int k = blockPos.getZ();
		BlockPos.iterate(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach(blockPosx -> serverWorld.setBlockState(blockPosx, Blocks.AIR.getDefaultState()));
		BlockPos.iterate(i - 2, j, k - 2, i + 2, j, k + 2).forEach(blockPosx -> serverWorld.setBlockState(blockPosx, Blocks.OBSIDIAN.getDefaultState()));
	}
}
