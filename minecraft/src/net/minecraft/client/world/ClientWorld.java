package net.minecraft.client.world;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
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
import net.minecraft.client.audio.EntityTrackingSoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagManager;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

@Environment(EnvType.CLIENT)
public class ClientWorld extends World {
	private final List<Entity> globalEntities = Lists.<Entity>newArrayList();
	private final Int2ObjectMap<Entity> regularEntities = new Int2ObjectOpenHashMap<>();
	private final ClientPlayNetworkHandler netHandler;
	private final WorldRenderer worldRenderer;
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final List<AbstractClientPlayerEntity> players = Lists.<AbstractClientPlayerEntity>newArrayList();
	private int field_3732;
	private int field_3731;
	private int ticksSinceLightingClient = this.random.nextInt(12000);
	private Scoreboard scoreboard = new Scoreboard();
	private final Map<String, MapState> mapStates = Maps.<String, MapState>newHashMap();

	public ClientWorld(
		ClientPlayNetworkHandler clientPlayNetworkHandler,
		LevelInfo levelInfo,
		DimensionType dimensionType,
		Difficulty difficulty,
		Profiler profiler,
		WorldRenderer worldRenderer
	) {
		super(new LevelProperties(levelInfo, "MpServer"), dimensionType, (world, dimension) -> new ClientChunkManager((ClientWorld)world), profiler, true);
		this.netHandler = clientPlayNetworkHandler;
		this.worldRenderer = worldRenderer;
		this.getLevelProperties().setDifficulty(difficulty);
		this.setSpawnPos(new BlockPos(8, 64, 8));
		this.updateAmbientDarkness();
		this.initWeatherGradients();
	}

	public void method_8441(BooleanSupplier booleanSupplier) {
		this.getWorldBorder().update();
		this.tickTime();
		this.getProfiler().swap("blocks");
		this.chunkManager.tick(booleanSupplier);
		this.method_2939();
		this.getProfiler().pop();
	}

	public Iterable<Entity> getEntities() {
		return Iterables.concat(this.regularEntities.values(), this.globalEntities);
	}

	public void method_18116() {
		Profiler profiler = this.getProfiler();
		profiler.push("entities");
		profiler.push("global");

		for (int i = 0; i < this.globalEntities.size(); i++) {
			Entity entity = (Entity)this.globalEntities.get(i);
			this.method_18472(entityx -> {
				entityx.age++;
				entityx.update();
			}, entity);
			if (entity.invalid) {
				this.globalEntities.remove(i--);
			}
		}

		profiler.swap("regular");
		ObjectIterator<Entry<Entity>> objectIterator = this.regularEntities.int2ObjectEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<Entity> entry = (Entry<Entity>)objectIterator.next();
			Entity entity2 = (Entity)entry.getValue();
			if (!entity2.hasVehicle()) {
				profiler.push("tick");
				if (!entity2.invalid) {
					this.method_18472(this::method_18646, entity2);
				}

				profiler.pop();
				profiler.push("remove");
				if (entity2.invalid) {
					objectIterator.remove();
					this.method_18117(entity2);
				}

				profiler.pop();
			}
		}

		profiler.pop();
		this.method_18471();
		profiler.pop();
	}

	public void method_18646(Entity entity) {
		if (entity instanceof PlayerEntity || this.method_2935().isEntityInLoadedChunk(entity)) {
			entity.prevRenderX = entity.x;
			entity.prevRenderY = entity.y;
			entity.prevRenderZ = entity.z;
			entity.prevYaw = entity.yaw;
			entity.prevPitch = entity.pitch;
			if (entity.field_6016) {
				entity.age++;
				this.getProfiler().push((Supplier<String>)(() -> Registry.ENTITY_TYPE.getId(entity.getType()).toString()));
				entity.update();
				this.getProfiler().pop();
			}

			this.method_18648(entity);
			if (entity.field_6016) {
				for (Entity entity2 : entity.getPassengerList()) {
					this.method_18647(entity, entity2);
				}
			}
		}
	}

	public void method_18647(Entity entity, Entity entity2) {
		if (entity2.invalid || entity2.getRiddenEntity() != entity) {
			entity2.stopRiding();
		} else if (entity2 instanceof PlayerEntity || this.method_2935().isEntityInLoadedChunk(entity2)) {
			entity2.prevRenderX = entity2.x;
			entity2.prevRenderY = entity2.y;
			entity2.prevRenderZ = entity2.z;
			entity2.prevYaw = entity2.yaw;
			entity2.prevPitch = entity2.pitch;
			if (entity2.field_6016) {
				entity2.age++;
				entity2.updateRiding();
			}

			this.method_18648(entity2);
			if (entity2.field_6016) {
				for (Entity entity3 : entity2.getPassengerList()) {
					this.method_18647(entity2, entity3);
				}
			}
		}
	}

	public void method_18648(Entity entity) {
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

	public void method_18110(WorldChunk worldChunk) {
		this.field_18139.addAll(worldChunk.getBlockEntityMap().values());
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		return true;
	}

	private void method_2939() {
		int i = this.client.options.viewDistance;
		int j = MathHelper.floor(this.client.player.x / 16.0);
		int k = MathHelper.floor(this.client.player.z / 16.0);
		if (this.ticksSinceLightingClient > 0) {
			this.ticksSinceLightingClient--;
		} else {
			for (int l = 0; l < 10; l++) {
				int m = j + this.field_3732;
				int n = k + this.field_3731;
				int o = m * 16;
				int p = n * 16;
				this.field_3731++;
				if (this.field_3731 >= i) {
					this.field_3731 = -i;
					this.field_3732++;
					if (this.field_3732 >= i) {
						this.field_3732 = -i;
					}
				}

				WorldChunk worldChunk = this.method_8497(m, n);
				this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
				int q = this.lcgBlockSeed >> 2;
				int r = q & 15;
				int s = q >> 8 & 15;
				int t = q >> 16 & 0xFF;
				double d = this.client.player.squaredDistanceTo((double)r + 0.5, (double)t + 0.5, (double)s + 0.5);
				if (this.client.player != null && d > 4.0 && d < 256.0) {
					BlockPos blockPos = new BlockPos(r + o, t, s + p);
					BlockState blockState = worldChunk.getBlockState(blockPos);
					r += o;
					s += p;
					if (blockState.isAir() && this.getLightLevel(blockPos, 0) <= this.random.nextInt(8) && this.getLightLevel(LightType.SKY, blockPos) <= 0) {
						this.playSound(
							(double)r + 0.5,
							(double)t + 0.5,
							(double)s + 0.5,
							SoundEvents.field_14564,
							SoundCategory.field_15256,
							0.7F,
							0.8F + this.random.nextFloat() * 0.2F,
							false
						);
						this.ticksSinceLightingClient = this.random.nextInt(12000) + 6000;
					}
				}
			}
		}
	}

	public int method_18120() {
		return this.regularEntities.size();
	}

	public void method_18108(LightningEntity lightningEntity) {
		this.globalEntities.add(lightningEntity);
	}

	public void method_18107(int i, AbstractClientPlayerEntity abstractClientPlayerEntity) {
		this.method_18114(i, abstractClientPlayerEntity);
		this.players.add(abstractClientPlayerEntity);
	}

	public void method_2942(int i, Entity entity) {
		this.method_18114(i, entity);
	}

	private void method_18114(int i, Entity entity) {
		this.method_2945(i);
		this.regularEntities.put(i, entity);
		this.method_2935().method_2857(MathHelper.floor(entity.x / 16.0), MathHelper.floor(entity.z / 16.0), ChunkStatus.FULL, true).addEntity(entity);
	}

	public void method_2945(int i) {
		Entity entity = this.regularEntities.remove(i);
		if (entity != null) {
			entity.invalidate();
			this.method_18117(entity);
		}
	}

	private void method_18117(Entity entity) {
		entity.method_18375();
		if (entity.field_6016) {
			this.method_8497(entity.chunkX, entity.chunkZ).remove(entity);
		}

		this.players.remove(entity);
	}

	public void method_18115(WorldChunk worldChunk) {
		for (Entry<Entity> entry : this.regularEntities.int2ObjectEntrySet()) {
			Entity entity = (Entity)entry.getValue();
			int i = MathHelper.floor(entity.x / 16.0);
			int j = MathHelper.floor(entity.z / 16.0);
			if (i == worldChunk.getPos().x && j == worldChunk.getPos().z) {
				worldChunk.addEntity(entity);
			}
		}
	}

	@Nullable
	@Override
	public Entity getEntityById(int i) {
		return this.regularEntities.get(i);
	}

	public void method_2937(BlockPos blockPos, BlockState blockState) {
		this.setBlockState(blockPos, blockState, 19);
	}

	@Override
	public void disconnect() {
		this.netHandler.getClientConnection().disconnect(new TranslatableTextComponent("multiplayer.status.quitting"));
	}

	public void doRandomBlockDisplayTicks(int i, int j, int k) {
		int l = 32;
		Random random = new Random();
		ItemStack itemStack = this.client.player.getMainHandStack();
		boolean bl = this.client.interactionManager.getCurrentGameMode() == GameMode.field_9220
			&& !itemStack.isEmpty()
			&& itemStack.getItem() == Blocks.field_10499.getItem();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = 0; m < 667; m++) {
			this.randomBlockDisplayTick(i, j, k, 16, random, bl, mutable);
			this.randomBlockDisplayTick(i, j, k, 32, random, bl, mutable);
		}
	}

	public void randomBlockDisplayTick(int i, int j, int k, int l, Random random, boolean bl, BlockPos.Mutable mutable) {
		int m = i + this.random.nextInt(l) - this.random.nextInt(l);
		int n = j + this.random.nextInt(l) - this.random.nextInt(l);
		int o = k + this.random.nextInt(l) - this.random.nextInt(l);
		mutable.set(m, n, o);
		BlockState blockState = this.getBlockState(mutable);
		blockState.getBlock().randomDisplayTick(blockState, this, mutable, random);
		FluidState fluidState = this.getFluidState(mutable);
		if (!fluidState.isEmpty()) {
			fluidState.randomDisplayTick(this, mutable, random);
			ParticleParameters particleParameters = fluidState.getParticle();
			if (particleParameters != null && this.random.nextInt(10) == 0) {
				boolean bl2 = Block.isFaceFullSquare(blockState.getCollisionShape(this, mutable), Direction.DOWN);
				BlockPos blockPos = mutable.down();
				this.method_2938(blockPos, this.getBlockState(blockPos), particleParameters, bl2);
			}
		}

		if (bl && blockState.getBlock() == Blocks.field_10499) {
			this.addParticle(ParticleTypes.field_11235, (double)((float)m + 0.5F), (double)((float)n + 0.5F), (double)((float)o + 0.5F), 0.0, 0.0, 0.0);
		}
	}

	private void method_2938(BlockPos blockPos, BlockState blockState, ParticleParameters particleParameters, boolean bl) {
		if (blockState.getFluidState().isEmpty()) {
			VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos);
			double d = voxelShape.getMaximum(Direction.Axis.Y);
			if (d < 1.0) {
				if (bl) {
					this.method_2932(
						(double)blockPos.getX(),
						(double)(blockPos.getX() + 1),
						(double)blockPos.getZ(),
						(double)(blockPos.getZ() + 1),
						(double)(blockPos.getY() + 1) - 0.05,
						particleParameters
					);
				}
			} else if (!blockState.matches(BlockTags.field_15490)) {
				double e = voxelShape.getMinimum(Direction.Axis.Y);
				if (e > 0.0) {
					this.method_2948(blockPos, particleParameters, voxelShape, (double)blockPos.getY() + e - 0.05);
				} else {
					BlockPos blockPos2 = blockPos.down();
					BlockState blockState2 = this.getBlockState(blockPos2);
					VoxelShape voxelShape2 = blockState2.getCollisionShape(this, blockPos2);
					double f = voxelShape2.getMaximum(Direction.Axis.Y);
					if (f < 1.0 && blockState2.getFluidState().isEmpty()) {
						this.method_2948(blockPos, particleParameters, voxelShape, (double)blockPos.getY() - 0.05);
					}
				}
			}
		}
	}

	private void method_2948(BlockPos blockPos, ParticleParameters particleParameters, VoxelShape voxelShape, double d) {
		this.method_2932(
			(double)blockPos.getX() + voxelShape.getMinimum(Direction.Axis.X),
			(double)blockPos.getX() + voxelShape.getMaximum(Direction.Axis.X),
			(double)blockPos.getZ() + voxelShape.getMinimum(Direction.Axis.Z),
			(double)blockPos.getZ() + voxelShape.getMaximum(Direction.Axis.Z),
			d,
			particleParameters
		);
	}

	private void method_2932(double d, double e, double f, double g, double h, ParticleParameters particleParameters) {
		this.addParticle(particleParameters, MathHelper.lerp(this.random.nextDouble(), d, e), h, MathHelper.lerp(this.random.nextDouble(), f, g), 0.0, 0.0, 0.0);
	}

	public void method_2936() {
		ObjectIterator<Entry<Entity>> objectIterator = this.regularEntities.int2ObjectEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<Entity> entry = (Entry<Entity>)objectIterator.next();
			Entity entity = (Entity)entry.getValue();
			if (entity.invalid) {
				objectIterator.remove();
				this.method_18117(entity);
			}
		}
	}

	@Override
	public CrashReportSection addDetailsToCrashReport(CrashReport crashReport) {
		CrashReportSection crashReportSection = super.addDetailsToCrashReport(crashReport);
		crashReportSection.add("Server brand", (ICrashCallable<String>)(() -> this.client.player.getServerBrand()));
		crashReportSection.add(
			"Server type", (ICrashCallable<String>)(() -> this.client.getServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server")
		);
		return crashReportSection;
	}

	@Override
	public void playSound(@Nullable PlayerEntity playerEntity, double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h) {
		if (playerEntity == this.client.player) {
			this.playSound(d, e, f, soundEvent, soundCategory, g, h, false);
		}
	}

	@Override
	public void playSoundFromEntity(@Nullable PlayerEntity playerEntity, Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
		if (playerEntity == this.client.player) {
			this.client.getSoundLoader().play(new EntityTrackingSoundInstance(soundEvent, soundCategory, entity));
		}
	}

	public void playSound(BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g, boolean bl) {
		this.playSound((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, soundEvent, soundCategory, f, g, bl);
	}

	@Override
	public void playSound(double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h, boolean bl) {
		double i = this.client.getCameraEntity().squaredDistanceTo(d, e, f);
		PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(soundEvent, soundCategory, g, h, (float)d, (float)e, (float)f);
		if (bl && i > 100.0) {
			double j = Math.sqrt(i) / 40.0;
			this.client.getSoundLoader().play(positionedSoundInstance, (int)(j * 20.0));
		} else {
			this.client.getSoundLoader().play(positionedSoundInstance);
		}
	}

	@Override
	public void addFireworkParticle(double d, double e, double f, double g, double h, double i, @Nullable CompoundTag compoundTag) {
		this.client.particleManager.addParticle(new FireworksSparkParticle.create(this, d, e, f, g, h, i, this.client.particleManager, compoundTag));
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
	public void setTimeOfDay(long l) {
		if (l < 0L) {
			l = -l;
			this.getGameRules().put("doDaylightCycle", "false", null);
		} else {
			this.getGameRules().put("doDaylightCycle", "true", null);
		}

		super.setTimeOfDay(l);
	}

	@Override
	public TickScheduler<Block> getBlockTickScheduler() {
		return DummyClientTickScheduler.get();
	}

	@Override
	public TickScheduler<Fluid> getFluidTickScheduler() {
		return DummyClientTickScheduler.get();
	}

	public ClientChunkManager method_2935() {
		return (ClientChunkManager)super.getChunkManager();
	}

	@Nullable
	@Override
	public MapState getMapState(String string) {
		return (MapState)this.mapStates.get(string);
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
	public TagManager getTagManager() {
		return this.netHandler.getTagManager();
	}

	@Override
	public void updateListeners(BlockPos blockPos, BlockState blockState, BlockState blockState2, int i) {
		this.worldRenderer.method_8570(this, blockPos, blockState, blockState2, i);
	}

	@Override
	public void scheduleBlockRender(BlockPos blockPos) {
		this.worldRenderer.scheduleBlockRender(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	public void method_18113(int i, int j, int k) {
		this.worldRenderer.method_18145(i, j, k);
	}

	@Override
	public void setBlockBreakingProgress(int i, BlockPos blockPos, int j) {
		this.worldRenderer.setBlockBreakingProgress(i, blockPos, j);
	}

	@Override
	public void playGlobalEvent(int i, BlockPos blockPos, int j) {
		this.worldRenderer.playGlobalEvent(i, blockPos, j);
	}

	@Override
	public void playEvent(@Nullable PlayerEntity playerEntity, int i, BlockPos blockPos, int j) {
		try {
			this.worldRenderer.playLevelEvent(playerEntity, i, blockPos, j);
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Playing level event");
			CrashReportSection crashReportSection = crashReport.addElement("Level event being played");
			crashReportSection.add("Block coordinates", CrashReportSection.createPositionString(blockPos));
			crashReportSection.add("Event source", playerEntity);
			crashReportSection.add("Event type", i);
			crashReportSection.add("Event data", j);
			throw new CrashException(crashReport);
		}
	}

	@Override
	public void addParticle(ParticleParameters particleParameters, double d, double e, double f, double g, double h, double i) {
		this.worldRenderer.addParticle(particleParameters, particleParameters.getType().shouldAlwaysSpawn(), d, e, f, g, h, i);
	}

	@Override
	public void addParticle(ParticleParameters particleParameters, boolean bl, double d, double e, double f, double g, double h, double i) {
		this.worldRenderer.addParticle(particleParameters, particleParameters.getType().shouldAlwaysSpawn() || bl, d, e, f, g, h, i);
	}

	@Override
	public void addImportantParticle(ParticleParameters particleParameters, double d, double e, double f, double g, double h, double i) {
		this.worldRenderer.addParticle(particleParameters, false, true, d, e, f, g, h, i);
	}

	@Override
	public void addImportantParticle(ParticleParameters particleParameters, boolean bl, double d, double e, double f, double g, double h, double i) {
		this.worldRenderer.addParticle(particleParameters, particleParameters.getType().shouldAlwaysSpawn() || bl, true, d, e, f, g, h, i);
	}

	@Override
	public List<AbstractClientPlayerEntity> getPlayers() {
		return this.players;
	}
}
