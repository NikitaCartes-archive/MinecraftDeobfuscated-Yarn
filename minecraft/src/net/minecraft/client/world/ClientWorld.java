package net.minecraft.client.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ColorResolver;

@Environment(EnvType.CLIENT)
public class ClientWorld extends World {
	private final Int2ObjectMap<Entity> regularEntities = new Int2ObjectOpenHashMap<>();
	private final ClientPlayNetworkHandler netHandler;
	private final WorldRenderer worldRenderer;
	private final ClientWorld.Properties clientWorldProperties;
	private final SkyProperties skyProperties;
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final List<AbstractClientPlayerEntity> players = Lists.<AbstractClientPlayerEntity>newArrayList();
	private Scoreboard scoreboard = new Scoreboard();
	private final Map<String, MapState> mapStates = Maps.<String, MapState>newHashMap();
	private int lightningTicksLeft;
	private final Object2ObjectArrayMap<ColorResolver, BiomeColorCache> colorCache = Util.make(new Object2ObjectArrayMap<>(3), object2ObjectArrayMap -> {
		object2ObjectArrayMap.put(BiomeColors.GRASS_COLOR, new BiomeColorCache());
		object2ObjectArrayMap.put(BiomeColors.FOLIAGE_COLOR, new BiomeColorCache());
		object2ObjectArrayMap.put(BiomeColors.WATER_COLOR, new BiomeColorCache());
	});
	private final ClientChunkManager chunkManager;

	public ClientWorld(
		ClientPlayNetworkHandler clientPlayNetworkHandler,
		ClientWorld.Properties properties,
		RegistryKey<World> registryKey,
		RegistryKey<DimensionType> registryKey2,
		DimensionType dimensionType,
		int i,
		Supplier<Profiler> supplier,
		WorldRenderer worldRenderer,
		boolean bl,
		long l
	) {
		super(properties, registryKey, registryKey2, dimensionType, supplier, true, bl, l);
		this.chunkManager = new ClientChunkManager(this, i);
		this.clientWorldProperties = properties;
		this.netHandler = clientPlayNetworkHandler;
		this.worldRenderer = worldRenderer;
		this.skyProperties = SkyProperties.byDimensionType(clientPlayNetworkHandler.method_29091().getDimensionTypeRegistry().getKey(dimensionType));
		this.setSpawnPos(new BlockPos(8, 64, 8));
		this.calculateAmbientDarkness();
		this.initWeatherGradients();
	}

	public SkyProperties getSkyProperties() {
		return this.skyProperties;
	}

	public void tick(BooleanSupplier shouldKeepTicking) {
		this.getWorldBorder().tick();
		this.tickTime();
		this.getProfiler().push("blocks");
		this.chunkManager.tick(shouldKeepTicking);
		this.getProfiler().pop();
	}

	private void tickTime() {
		this.method_29089(this.properties.getTime() + 1L);
		if (this.properties.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
			this.setTimeOfDay(this.properties.getTimeOfDay() + 1L);
		}
	}

	public void method_29089(long l) {
		this.clientWorldProperties.setTime(l);
	}

	public void setTimeOfDay(long l) {
		if (l < 0L) {
			l = -l;
			this.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
		} else {
			this.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, null);
		}

		this.clientWorldProperties.setTimeOfDay(l);
	}

	public Iterable<Entity> getEntities() {
		return this.regularEntities.values();
	}

	public void tickEntities() {
		Profiler profiler = this.getProfiler();
		profiler.push("entities");
		ObjectIterator<Entry<Entity>> objectIterator = this.regularEntities.int2ObjectEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<Entity> entry = (Entry<Entity>)objectIterator.next();
			Entity entity = (Entity)entry.getValue();
			if (!entity.hasVehicle()) {
				profiler.push("tick");
				if (!entity.removed) {
					this.tickEntity(this::tickEntity, entity);
				}

				profiler.pop();
				profiler.push("remove");
				if (entity.removed) {
					objectIterator.remove();
					this.finishRemovingEntity(entity);
				}

				profiler.pop();
			}
		}

		this.tickBlockEntities();
		profiler.pop();
	}

	public void tickEntity(Entity entity) {
		if (!(entity instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(entity)) {
			this.checkChunk(entity);
		} else {
			entity.resetPosition(entity.getX(), entity.getY(), entity.getZ());
			entity.prevYaw = entity.yaw;
			entity.prevPitch = entity.pitch;
			if (entity.updateNeeded || entity.isSpectator()) {
				entity.age++;
				this.getProfiler().push((Supplier<String>)(() -> Registry.ENTITY_TYPE.getId(entity.getType()).toString()));
				entity.tick();
				this.getProfiler().pop();
			}

			this.checkChunk(entity);
			if (entity.updateNeeded) {
				for (Entity entity2 : entity.getPassengerList()) {
					this.tickPassenger(entity, entity2);
				}
			}
		}
	}

	public void tickPassenger(Entity entity, Entity passenger) {
		if (passenger.removed || passenger.getVehicle() != entity) {
			passenger.stopRiding();
		} else if (passenger instanceof PlayerEntity || this.getChunkManager().shouldTickEntity(passenger)) {
			passenger.resetPosition(passenger.getX(), passenger.getY(), passenger.getZ());
			passenger.prevYaw = passenger.yaw;
			passenger.prevPitch = passenger.pitch;
			if (passenger.updateNeeded) {
				passenger.age++;
				passenger.tickRiding();
			}

			this.checkChunk(passenger);
			if (passenger.updateNeeded) {
				for (Entity entity2 : passenger.getPassengerList()) {
					this.tickPassenger(passenger, entity2);
				}
			}
		}
	}

	private void checkChunk(Entity entity) {
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

	public void unloadBlockEntities(WorldChunk chunk) {
		this.unloadedBlockEntities.addAll(chunk.getBlockEntities().values());
		this.chunkManager.getLightingProvider().setLightEnabled(chunk.getPos(), false);
	}

	public void resetChunkColor(int i, int j) {
		this.colorCache.forEach((colorResolver, biomeColorCache) -> biomeColorCache.reset(i, j));
	}

	public void reloadColor() {
		this.colorCache.forEach((colorResolver, biomeColorCache) -> biomeColorCache.reset());
	}

	@Override
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		return true;
	}

	public int getRegularEntityCount() {
		return this.regularEntities.size();
	}

	public void addPlayer(int id, AbstractClientPlayerEntity player) {
		this.addEntityPrivate(id, player);
		this.players.add(player);
	}

	public void addEntity(int id, Entity entity) {
		this.addEntityPrivate(id, entity);
	}

	private void addEntityPrivate(int id, Entity entity) {
		this.removeEntity(id);
		this.regularEntities.put(id, entity);
		this.getChunkManager().getChunk(MathHelper.floor(entity.getX() / 16.0), MathHelper.floor(entity.getZ() / 16.0), ChunkStatus.FULL, true).addEntity(entity);
	}

	public void removeEntity(int entityId) {
		Entity entity = this.regularEntities.remove(entityId);
		if (entity != null) {
			entity.remove();
			this.finishRemovingEntity(entity);
		}
	}

	private void finishRemovingEntity(Entity entity) {
		entity.detach();
		if (entity.updateNeeded) {
			this.getChunk(entity.chunkX, entity.chunkZ).remove(entity);
		}

		this.players.remove(entity);
	}

	public void addEntitiesToChunk(WorldChunk chunk) {
		for (Entry<Entity> entry : this.regularEntities.int2ObjectEntrySet()) {
			Entity entity = (Entity)entry.getValue();
			int i = MathHelper.floor(entity.getX() / 16.0);
			int j = MathHelper.floor(entity.getZ() / 16.0);
			if (i == chunk.getPos().x && j == chunk.getPos().z) {
				chunk.addEntity(entity);
			}
		}
	}

	@Nullable
	@Override
	public Entity getEntityById(int id) {
		return this.regularEntities.get(id);
	}

	public void setBlockStateWithoutNeighborUpdates(BlockPos pos, BlockState state) {
		this.setBlockState(pos, state, 19);
	}

	@Override
	public void disconnect() {
		this.netHandler.getConnection().disconnect(new TranslatableText("multiplayer.status.quitting"));
	}

	public void doRandomBlockDisplayTicks(int xCenter, int yCenter, int zCenter) {
		int i = 32;
		Random random = new Random();
		boolean bl = false;
		if (this.client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
			for (ItemStack itemStack : this.client.player.getItemsHand()) {
				if (itemStack.getItem() == Blocks.BARRIER.asItem()) {
					bl = true;
					break;
				}
			}
		}

		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < 667; j++) {
			this.randomBlockDisplayTick(xCenter, yCenter, zCenter, 16, random, bl, mutable);
			this.randomBlockDisplayTick(xCenter, yCenter, zCenter, 32, random, bl, mutable);
		}
	}

	public void randomBlockDisplayTick(int xCenter, int yCenter, int zCenter, int radius, Random random, boolean spawnBarrierParticles, BlockPos.Mutable pos) {
		int i = xCenter + this.random.nextInt(radius) - this.random.nextInt(radius);
		int j = yCenter + this.random.nextInt(radius) - this.random.nextInt(radius);
		int k = zCenter + this.random.nextInt(radius) - this.random.nextInt(radius);
		pos.set(i, j, k);
		BlockState blockState = this.getBlockState(pos);
		blockState.getBlock().randomDisplayTick(blockState, this, pos, random);
		FluidState fluidState = this.getFluidState(pos);
		if (!fluidState.isEmpty()) {
			fluidState.randomDisplayTick(this, pos, random);
			ParticleEffect particleEffect = fluidState.getParticle();
			if (particleEffect != null && this.random.nextInt(10) == 0) {
				boolean bl = blockState.isSideSolidFullSquare(this, pos, Direction.DOWN);
				BlockPos blockPos = pos.down();
				this.addParticle(blockPos, this.getBlockState(blockPos), particleEffect, bl);
			}
		}

		if (spawnBarrierParticles && blockState.isOf(Blocks.BARRIER)) {
			this.addParticle(ParticleTypes.BARRIER, (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, 0.0, 0.0, 0.0);
		}

		if (!blockState.isFullCube(this, pos)) {
			this.getBiome(pos)
				.getParticleConfig()
				.ifPresent(
					biomeParticleConfig -> {
						if (biomeParticleConfig.shouldAddParticle(this.random)) {
							this.addParticle(
								biomeParticleConfig.getParticleType(),
								(double)pos.getX() + this.random.nextDouble(),
								(double)pos.getY() + this.random.nextDouble(),
								(double)pos.getZ() + this.random.nextDouble(),
								0.0,
								0.0,
								0.0
							);
						}
					}
				);
		}
	}

	private void addParticle(BlockPos pos, BlockState state, ParticleEffect parameters, boolean bl) {
		if (state.getFluidState().isEmpty()) {
			VoxelShape voxelShape = state.getCollisionShape(this, pos);
			double d = voxelShape.getMax(Direction.Axis.Y);
			if (d < 1.0) {
				if (bl) {
					this.addParticle((double)pos.getX(), (double)(pos.getX() + 1), (double)pos.getZ(), (double)(pos.getZ() + 1), (double)(pos.getY() + 1) - 0.05, parameters);
				}
			} else if (!state.isIn(BlockTags.IMPERMEABLE)) {
				double e = voxelShape.getMin(Direction.Axis.Y);
				if (e > 0.0) {
					this.addParticle(pos, parameters, voxelShape, (double)pos.getY() + e - 0.05);
				} else {
					BlockPos blockPos = pos.down();
					BlockState blockState = this.getBlockState(blockPos);
					VoxelShape voxelShape2 = blockState.getCollisionShape(this, blockPos);
					double f = voxelShape2.getMax(Direction.Axis.Y);
					if (f < 1.0 && blockState.getFluidState().isEmpty()) {
						this.addParticle(pos, parameters, voxelShape, (double)pos.getY() - 0.05);
					}
				}
			}
		}
	}

	private void addParticle(BlockPos pos, ParticleEffect parameters, VoxelShape shape, double y) {
		this.addParticle(
			(double)pos.getX() + shape.getMin(Direction.Axis.X),
			(double)pos.getX() + shape.getMax(Direction.Axis.X),
			(double)pos.getZ() + shape.getMin(Direction.Axis.Z),
			(double)pos.getZ() + shape.getMax(Direction.Axis.Z),
			y,
			parameters
		);
	}

	private void addParticle(double minX, double maxX, double minZ, double maxZ, double y, ParticleEffect parameters) {
		this.addParticle(parameters, MathHelper.lerp(this.random.nextDouble(), minX, maxX), y, MathHelper.lerp(this.random.nextDouble(), minZ, maxZ), 0.0, 0.0, 0.0);
	}

	public void finishRemovingEntities() {
		ObjectIterator<Entry<Entity>> objectIterator = this.regularEntities.int2ObjectEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<Entity> entry = (Entry<Entity>)objectIterator.next();
			Entity entity = (Entity)entry.getValue();
			if (entity.removed) {
				objectIterator.remove();
				this.finishRemovingEntity(entity);
			}
		}
	}

	@Override
	public CrashReportSection addDetailsToCrashReport(CrashReport report) {
		CrashReportSection crashReportSection = super.addDetailsToCrashReport(report);
		crashReportSection.add("Server brand", (CrashCallable<String>)(() -> this.client.player.getServerBrand()));
		crashReportSection.add(
			"Server type", (CrashCallable<String>)(() -> this.client.getServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server")
		);
		return crashReportSection;
	}

	@Override
	public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
		if (player == this.client.player) {
			this.playSound(x, y, z, sound, category, volume, pitch, false);
		}
	}

	@Override
	public void playSoundFromEntity(@Nullable PlayerEntity player, Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
		if (player == this.client.player) {
			this.client.getSoundManager().play(new EntityTrackingSoundInstance(sound, category, entity));
		}
	}

	public void playSound(BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
		this.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, sound, category, volume, pitch, useDistance);
	}

	@Override
	public void playSound(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean bl) {
		double d = this.client.gameRenderer.getCamera().getPos().squaredDistanceTo(x, y, z);
		PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(sound, category, volume, pitch, x, y, z);
		if (bl && d > 100.0) {
			double e = Math.sqrt(d) / 40.0;
			this.client.getSoundManager().play(positionedSoundInstance, (int)(e * 20.0));
		} else {
			this.client.getSoundManager().play(positionedSoundInstance);
		}
	}

	@Override
	public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Nullable CompoundTag tag) {
		this.client
			.particleManager
			.addParticle(new FireworksSparkParticle.FireworkParticle(this, x, y, z, velocityX, velocityY, velocityZ, this.client.particleManager, tag));
	}

	@Override
	public void sendPacket(Packet<?> packet) {
		this.netHandler.sendPacket(packet);
	}

	@Override
	public RecipeManager getRecipeManager() {
		return this.netHandler.getRecipeManager();
	}

	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}

	@Override
	public TickScheduler<Block> getBlockTickScheduler() {
		return DummyClientTickScheduler.get();
	}

	@Override
	public TickScheduler<Fluid> getFluidTickScheduler() {
		return DummyClientTickScheduler.get();
	}

	public ClientChunkManager getChunkManager() {
		return this.chunkManager;
	}

	@Nullable
	@Override
	public MapState getMapState(String id) {
		return (MapState)this.mapStates.get(id);
	}

	@Override
	public void putMapState(MapState mapState) {
		this.mapStates.put(mapState.getId(), mapState);
	}

	@Override
	public int getNextMapId() {
		return 0;
	}

	@Override
	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	@Override
	public RegistryTagManager getTagManager() {
		return this.netHandler.getTagManager();
	}

	@Override
	public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
		this.worldRenderer.updateBlock(this, pos, oldState, newState, flags);
	}

	@Override
	public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {
		this.worldRenderer.scheduleBlockRerenderIfNeeded(pos, old, updated);
	}

	public void scheduleBlockRenders(int x, int y, int z) {
		this.worldRenderer.scheduleBlockRenders(x, y, z);
	}

	@Override
	public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
		this.worldRenderer.setBlockBreakingInfo(entityId, pos, progress);
	}

	@Override
	public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
		this.worldRenderer.processGlobalEvent(eventId, pos, data);
	}

	@Override
	public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {
		try {
			this.worldRenderer.processWorldEvent(player, eventId, pos, data);
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Playing level event");
			CrashReportSection crashReportSection = crashReport.addElement("Level event being played");
			crashReportSection.add("Block coordinates", CrashReportSection.createPositionString(pos));
			crashReportSection.add("Event source", player);
			crashReportSection.add("Event type", eventId);
			crashReportSection.add("Event data", data);
			throw new CrashException(crashReport);
		}
	}

	@Override
	public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
	}

	@Override
	public void addParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn() || alwaysSpawn, x, y, z, velocityX, velocityY, velocityZ);
	}

	@Override
	public void addImportantParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		this.worldRenderer.addParticle(parameters, false, true, x, y, z, velocityX, velocityY, velocityZ);
	}

	@Override
	public void addImportantParticle(
		ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn() || alwaysSpawn, true, x, y, z, velocityX, velocityY, velocityZ);
	}

	@Override
	public List<AbstractClientPlayerEntity> getPlayers() {
		return this.players;
	}

	@Override
	public Biome getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
		return Biomes.PLAINS;
	}

	public float method_23783(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.2F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0F) / 16.0));
		return h * 0.8F + 0.2F;
	}

	public Vec3d method_23777(BlockPos blockPos, float f) {
		float g = this.getSkyAngle(f);
		float h = MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		Biome biome = this.getBiome(blockPos);
		int i = biome.getSkyColor();
		float j = (float)(i >> 16 & 0xFF) / 255.0F;
		float k = (float)(i >> 8 & 0xFF) / 255.0F;
		float l = (float)(i & 0xFF) / 255.0F;
		j *= h;
		k *= h;
		l *= h;
		float m = this.getRainGradient(f);
		if (m > 0.0F) {
			float n = (j * 0.3F + k * 0.59F + l * 0.11F) * 0.6F;
			float o = 1.0F - m * 0.75F;
			j = j * o + n * (1.0F - o);
			k = k * o + n * (1.0F - o);
			l = l * o + n * (1.0F - o);
		}

		float n = this.getThunderGradient(f);
		if (n > 0.0F) {
			float o = (j * 0.3F + k * 0.59F + l * 0.11F) * 0.2F;
			float p = 1.0F - n * 0.75F;
			j = j * p + o * (1.0F - p);
			k = k * p + o * (1.0F - p);
			l = l * p + o * (1.0F - p);
		}

		if (this.lightningTicksLeft > 0) {
			float o = (float)this.lightningTicksLeft - f;
			if (o > 1.0F) {
				o = 1.0F;
			}

			o *= 0.45F;
			j = j * (1.0F - o) + 0.8F * o;
			k = k * (1.0F - o) + 0.8F * o;
			l = l * (1.0F - o) + 1.0F * o;
		}

		return new Vec3d((double)j, (double)k, (double)l);
	}

	public Vec3d getCloudsColor(float tickDelta) {
		float f = this.getSkyAngle(tickDelta);
		float g = MathHelper.cos(f * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		float h = 1.0F;
		float i = 1.0F;
		float j = 1.0F;
		float k = this.getRainGradient(tickDelta);
		if (k > 0.0F) {
			float l = (h * 0.3F + i * 0.59F + j * 0.11F) * 0.6F;
			float m = 1.0F - k * 0.95F;
			h = h * m + l * (1.0F - m);
			i = i * m + l * (1.0F - m);
			j = j * m + l * (1.0F - m);
		}

		h *= g * 0.9F + 0.1F;
		i *= g * 0.9F + 0.1F;
		j *= g * 0.85F + 0.15F;
		float l = this.getThunderGradient(tickDelta);
		if (l > 0.0F) {
			float m = (h * 0.3F + i * 0.59F + j * 0.11F) * 0.2F;
			float n = 1.0F - l * 0.95F;
			h = h * n + m * (1.0F - n);
			i = i * n + m * (1.0F - n);
			j = j * n + m * (1.0F - n);
		}

		return new Vec3d((double)h, (double)i, (double)j);
	}

	public float method_23787(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.25F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		return h * h * 0.5F;
	}

	public int getLightningTicksLeft() {
		return this.lightningTicksLeft;
	}

	@Override
	public void setLightningTicksLeft(int lightningTicksLeft) {
		this.lightningTicksLeft = lightningTicksLeft;
	}

	@Override
	public float getBrightness(Direction direction, boolean shaded) {
		boolean bl = this.getSkyProperties().isDarkened();
		if (!shaded) {
			return bl ? 0.9F : 1.0F;
		} else {
			switch (direction) {
				case DOWN:
					return bl ? 0.9F : 0.5F;
				case UP:
					return bl ? 0.9F : 1.0F;
				case NORTH:
				case SOUTH:
					return 0.8F;
				case WEST:
				case EAST:
					return 0.6F;
				default:
					return 1.0F;
			}
		}
	}

	@Override
	public int getColor(BlockPos pos, ColorResolver colorResolver) {
		BiomeColorCache biomeColorCache = this.colorCache.get(colorResolver);
		return biomeColorCache.getBiomeColor(pos, () -> this.calculateColor(pos, colorResolver));
	}

	public int calculateColor(BlockPos pos, ColorResolver colorResolver) {
		int i = MinecraftClient.getInstance().options.biomeBlendRadius;
		if (i == 0) {
			return colorResolver.getColor(this.getBiome(pos), (double)pos.getX(), (double)pos.getZ());
		} else {
			int j = (i * 2 + 1) * (i * 2 + 1);
			int k = 0;
			int l = 0;
			int m = 0;
			CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(pos.getX() - i, pos.getY(), pos.getZ() - i, pos.getX() + i, pos.getY(), pos.getZ() + i);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			while (cuboidBlockIterator.step()) {
				mutable.set(cuboidBlockIterator.getX(), cuboidBlockIterator.getY(), cuboidBlockIterator.getZ());
				int n = colorResolver.getColor(this.getBiome(mutable), (double)mutable.getX(), (double)mutable.getZ());
				k += (n & 0xFF0000) >> 16;
				l += (n & 0xFF00) >> 8;
				m += n & 0xFF;
			}

			return (k / j & 0xFF) << 16 | (l / j & 0xFF) << 8 | m / j & 0xFF;
		}
	}

	public BlockPos getSpawnPos() {
		BlockPos blockPos = new BlockPos(this.properties.getSpawnX(), this.properties.getSpawnY(), this.properties.getSpawnZ());
		if (!this.getWorldBorder().contains(blockPos)) {
			blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
		}

		return blockPos;
	}

	public void setSpawnPos(BlockPos pos) {
		this.properties.setSpawnPos(pos);
	}

	public String toString() {
		return "ClientLevel";
	}

	public ClientWorld.Properties getLevelProperties() {
		return this.clientWorldProperties;
	}

	@Environment(EnvType.CLIENT)
	public static class Properties implements MutableWorldProperties {
		private final boolean hardcore;
		private final GameRules gameRules;
		private final boolean flatWorld;
		private int spawnX;
		private int spawnY;
		private int spawnZ;
		private long time;
		private long timeOfDay;
		private boolean raining;
		private Difficulty difficulty;
		private boolean difficultyLocked;

		public Properties(Difficulty difficulty, boolean hardcore, boolean flatWorld) {
			this.difficulty = difficulty;
			this.hardcore = hardcore;
			this.flatWorld = flatWorld;
			this.gameRules = new GameRules();
		}

		@Override
		public int getSpawnX() {
			return this.spawnX;
		}

		@Override
		public int getSpawnY() {
			return this.spawnY;
		}

		@Override
		public int getSpawnZ() {
			return this.spawnZ;
		}

		@Override
		public long getTime() {
			return this.time;
		}

		@Override
		public long getTimeOfDay() {
			return this.timeOfDay;
		}

		@Override
		public void setSpawnX(int spawnX) {
			this.spawnX = spawnX;
		}

		@Override
		public void setSpawnY(int spawnY) {
			this.spawnY = spawnY;
		}

		@Override
		public void setSpawnZ(int spawnZ) {
			this.spawnZ = spawnZ;
		}

		public void setTime(long difficulty) {
			this.time = difficulty;
		}

		public void setTimeOfDay(long time) {
			this.timeOfDay = time;
		}

		@Override
		public void setSpawnPos(BlockPos pos) {
			this.spawnX = pos.getX();
			this.spawnY = pos.getY();
			this.spawnZ = pos.getZ();
		}

		@Override
		public boolean isThundering() {
			return false;
		}

		@Override
		public boolean isRaining() {
			return this.raining;
		}

		@Override
		public void setRaining(boolean raining) {
			this.raining = raining;
		}

		@Override
		public boolean isHardcore() {
			return this.hardcore;
		}

		@Override
		public GameRules getGameRules() {
			return this.gameRules;
		}

		@Override
		public Difficulty getDifficulty() {
			return this.difficulty;
		}

		@Override
		public boolean isDifficultyLocked() {
			return this.difficultyLocked;
		}

		@Override
		public void populateCrashReport(CrashReportSection reportSection) {
			MutableWorldProperties.super.populateCrashReport(reportSection);
		}

		public void setDifficulty(Difficulty difficulty) {
			this.difficulty = difficulty;
		}

		public void setDifficultyLocked(boolean difficultyLocked) {
			this.difficultyLocked = difficultyLocked;
		}

		public double getSkyDarknessHeight() {
			return this.flatWorld ? 0.0 : 63.0;
		}

		public double getHorizonShadingRatio() {
			return this.flatWorld ? 1.0 : 0.03125;
		}
	}
}
