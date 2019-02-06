package net.minecraft.client.world;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
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
import net.minecraft.util.Tickable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
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
	private final List<BlockEntity> field_17779 = Lists.<BlockEntity>newArrayList();
	private final ClientPlayNetworkHandler netHandler;
	private final WorldRenderer worldRenderer;
	private final MinecraftClient client = MinecraftClient.getInstance();
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

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		super.tick(booleanSupplier);
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
			this.method_18111(entityx -> {
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
					this.method_18111(this::tickEntity, entity2);
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
		this.method_18118();
		profiler.pop();
	}

	public void method_18118() {
		Profiler profiler = this.getProfiler();
		profiler.push("blockEntities");
		if (!this.field_17779.isEmpty()) {
			this.tickingBlockEntities.removeAll(this.field_17779);
			this.blockEntities.removeAll(this.field_17779);
			this.field_17779.clear();
		}

		this.iteratingTickingBlockEntities = true;
		Iterator<BlockEntity> iterator = this.tickingBlockEntities.iterator();

		while (iterator.hasNext()) {
			BlockEntity blockEntity = (BlockEntity)iterator.next();
			if (!blockEntity.isInvalid() && blockEntity.hasWorld()) {
				BlockPos blockPos = blockEntity.getPos();
				if (this.isBlockLoaded(blockPos) && this.getWorldBorder().contains(blockPos)) {
					try {
						profiler.push((Supplier<String>)(() -> String.valueOf(BlockEntityType.getId(blockEntity.getType()))));
						((Tickable)blockEntity).tick();
						profiler.pop();
					} catch (Throwable var8) {
						CrashReport crashReport = CrashReport.create(var8, "Ticking block entity");
						CrashReportSection crashReportSection = crashReport.addElement("Block entity being ticked");
						blockEntity.populateCrashReport(crashReportSection);
						throw new CrashException(crashReport);
					}
				}
			}

			if (blockEntity.isInvalid()) {
				iterator.remove();
				this.blockEntities.remove(blockEntity);
				if (this.isBlockLoaded(blockEntity.getPos())) {
					this.getWorldChunk(blockEntity.getPos()).removeBlockEntity(blockEntity.getPos());
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
						this.addBlockEntity(blockEntity2);
					}

					if (this.isBlockLoaded(blockEntity2.getPos())) {
						WorldChunk worldChunk = this.getWorldChunk(blockEntity2.getPos());
						BlockState blockState = worldChunk.getBlockState(blockEntity2.getPos());
						worldChunk.setBlockEntity(blockEntity2.getPos(), blockEntity2);
						this.updateListeners(blockEntity2.getPos(), blockState, blockState, 3);
					}
				}
			}

			this.pendingBlockEntities.clear();
		}

		profiler.pop();
	}

	public void method_18111(Consumer<Entity> consumer, Entity entity) {
		try {
			consumer.accept(entity);
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Ticking entity");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being ticked");
			entity.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public void method_18110(WorldChunk worldChunk) {
		worldChunk.setLoadedToWorld(false);
		this.field_17779.addAll(worldChunk.getBlockEntityMap().values());
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

				WorldChunk worldChunk = this.getWorldChunk(m, n);
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
					if (blockState.isAir() && this.getLightLevel(blockPos, 0) <= this.random.nextInt(8) && this.getLightLevel(LightType.SKY_LIGHT, blockPos) <= 0) {
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
		this.getChunkProvider().getChunk(MathHelper.floor(entity.x / 16.0), MathHelper.floor(entity.z / 16.0), ChunkStatus.FULL, true).addEntity(entity);
	}

	public void method_2945(int i) {
		Entity entity = this.regularEntities.remove(i);
		if (entity != null) {
			entity.invalidate();
			this.method_18117(entity);
		}
	}

	private void method_18117(Entity entity) {
		if (entity.hasPassengers()) {
			entity.removeAllPassengers();
		}

		if (entity.hasVehicle()) {
			entity.stopRiding();
		}

		if (entity.field_6016) {
			this.getWorldChunk(entity.chunkX, entity.chunkZ).remove(entity);
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

	@Override
	protected void updateWeather() {
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
			ParticleParameters particleParameters = fluidState.method_15766();
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

	public ClientChunkManager getChunkProvider() {
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
}
