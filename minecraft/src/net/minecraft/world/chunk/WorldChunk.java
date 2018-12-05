package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2839;
import net.minecraft.class_2843;
import net.minecraft.class_3449;
import net.minecraft.class_3509;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.DummyClientTickScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerChunkManagerEntry;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportElement;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkSaveHandlerImpl;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldChunk implements Chunk {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ChunkSection EMPTY_SECTION = null;
	private final ChunkSection[] sections = new ChunkSection[16];
	private final Biome[] biomeArray;
	private final Map<BlockPos, CompoundTag> pendingBlockEntityTags = Maps.<BlockPos, CompoundTag>newHashMap();
	private boolean loadedToWorld;
	private final World world;
	private final Map<Heightmap.Type, Heightmap> field_12853 = Maps.newEnumMap(Heightmap.Type.class);
	private final class_2843 field_12849;
	private final Map<BlockPos, BlockEntity> blockEntityMap = Maps.<BlockPos, BlockEntity>newHashMap();
	private final class_3509<Entity>[] entitySections;
	private final Map<String, class_3449> field_12838 = Maps.<String, class_3449>newHashMap();
	private final Map<String, LongSet> field_12845 = Maps.<String, LongSet>newHashMap();
	private final ShortList[] field_12836 = new ShortList[16];
	private final TickScheduler<Block> field_12841;
	private final TickScheduler<Fluid> field_12857;
	private boolean field_12837;
	private long field_12844;
	private boolean dirty;
	private long field_12843;
	private Supplier<ServerChunkManagerEntry.class_3194> field_12856;
	private Consumer<WorldChunk> field_12850;
	private final ChunkPos pos;
	private boolean field_12847;

	@Environment(EnvType.CLIENT)
	public WorldChunk(World world, int i, int j, Biome[] biomes) {
		this(world, i, j, biomes, class_2843.field_12950, DummyClientTickScheduler.get(), DummyClientTickScheduler.get(), 0L, null);
	}

	public WorldChunk(
		World world,
		int i,
		int j,
		Biome[] biomes,
		class_2843 arg,
		TickScheduler<Block> tickScheduler,
		TickScheduler<Fluid> tickScheduler2,
		long l,
		@Nullable Consumer<WorldChunk> consumer
	) {
		this(world, i, j, biomes, arg, tickScheduler, tickScheduler2, l, null, consumer);
	}

	public WorldChunk(
		World world,
		int i,
		int j,
		Biome[] biomes,
		class_2843 arg,
		TickScheduler<Block> tickScheduler,
		TickScheduler<Fluid> tickScheduler2,
		long l,
		@Nullable ChunkSection[] chunkSections,
		@Nullable Consumer<WorldChunk> consumer
	) {
		this.entitySections = new class_3509[16];
		this.world = world;
		this.pos = new ChunkPos(i, j);
		this.field_12849 = arg;

		for (Heightmap.Type type : Heightmap.Type.values()) {
			if (type.method_16136()) {
				this.field_12853.put(type, new Heightmap(this, type));
			}
		}

		for (int k = 0; k < this.entitySections.length; k++) {
			this.entitySections[k] = new class_3509<>(Entity.class);
		}

		this.biomeArray = biomes;
		this.field_12841 = tickScheduler;
		this.field_12857 = tickScheduler2;
		this.field_12843 = l;
		this.field_12850 = consumer;
		if (chunkSections != null) {
			if (this.sections.length == chunkSections.length) {
				System.arraycopy(chunkSections, 0, this.sections, 0, this.sections.length);
			} else {
				LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", chunkSections.length, this.sections.length);
			}
		}
	}

	public WorldChunk(World world, class_2839 arg, int i, int j) {
		this(world, i, j, arg.getBiomeArray(), arg.method_12003(), arg.method_12303(), arg.method_12313(), arg.method_12033(), null);

		for (int k = 0; k < this.sections.length; k++) {
			this.sections[k] = arg.getSectionArray()[k];
		}

		for (CompoundTag compoundTag : arg.method_12295()) {
			ChunkSaveHandlerImpl.method_12383(compoundTag, world, this);
		}

		for (BlockEntity blockEntity : arg.method_12309().values()) {
			this.addBlockEntity(blockEntity);
		}

		this.pendingBlockEntityTags.putAll(arg.method_12316());

		for (int k = 0; k < arg.method_12012().length; k++) {
			this.field_12836[k] = arg.method_12012()[k];
		}

		this.method_12034(arg.method_12016());
		this.method_12183(arg.method_12179());

		for (Entry<Heightmap.Type, Heightmap> entry : arg.method_12011()) {
			if (((Heightmap.Type)entry.getKey()).method_16136()) {
				this.getHeightmap((Heightmap.Type)entry.getKey()).fromLongArray(((Heightmap)entry.getValue()).asLongArray());
			}
		}

		this.dirty = true;
	}

	@Override
	public Heightmap getHeightmap(Heightmap.Type type) {
		return (Heightmap)this.field_12853.computeIfAbsent(type, typex -> new Heightmap(this, typex));
	}

	@Override
	public Set<BlockPos> method_12021() {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet(this.pendingBlockEntityTags.keySet());
		set.addAll(this.blockEntityMap.keySet());
		return set;
	}

	@Override
	public ChunkSection[] getSectionArray() {
		return this.sections;
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		if (this.world.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			BlockState blockState = null;
			if (j == 60) {
				blockState = Blocks.field_10499.getDefaultState();
			}

			if (j == 70) {
				blockState = DebugChunkGenerator.method_12578(i, k);
			}

			return blockState == null ? Blocks.field_10124.getDefaultState() : blockState;
		} else {
			try {
				if (j >= 0 && j >> 4 < this.sections.length) {
					ChunkSection chunkSection = this.sections[j >> 4];
					if (chunkSection != EMPTY_SECTION) {
						return chunkSection.getBlockState(i & 15, j & 15, k & 15);
					}
				}

				return Blocks.field_10124.getDefaultState();
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Getting block state");
				CrashReportElement crashReportElement = crashReport.addElement("Block being got");
				crashReportElement.add("Location", (ICrashCallable<String>)(() -> CrashReportElement.createPositionString(i, j, k)));
				throw new CrashException(crashReport);
			}
		}
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		return this.getFluidState(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	public FluidState getFluidState(int i, int j, int k) {
		try {
			if (j >= 0 && j >> 4 < this.sections.length) {
				ChunkSection chunkSection = this.sections[j >> 4];
				if (chunkSection != EMPTY_SECTION) {
					return chunkSection.getFluidState(i & 15, j & 15, k & 15);
				}
			}

			return Fluids.field_15906.getDefaultState();
		} catch (Throwable var7) {
			CrashReport crashReport = CrashReport.create(var7, "Getting fluid state");
			CrashReportElement crashReportElement = crashReport.addElement("Block being got");
			crashReportElement.add("Location", (ICrashCallable<String>)(() -> CrashReportElement.createPositionString(i, j, k)));
			throw new CrashException(crashReport);
		}
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos blockPos, BlockState blockState, boolean bl) {
		int i = blockPos.getX() & 15;
		int j = blockPos.getY();
		int k = blockPos.getZ() & 15;
		ChunkSection chunkSection = this.sections[j >> 4];
		if (chunkSection == EMPTY_SECTION) {
			if (blockState.isAir()) {
				return null;
			}

			chunkSection = new ChunkSection(j >> 4 << 4);
			this.sections[j >> 4] = chunkSection;
		}

		boolean bl2 = chunkSection.isEmpty();
		BlockState blockState2 = chunkSection.method_16675(i, j & 15, k, blockState);
		if (blockState2 == blockState) {
			return null;
		} else {
			Block block = blockState.getBlock();
			Block block2 = blockState2.getBlock();
			((Heightmap)this.field_12853.get(Heightmap.Type.MOTION_BLOCKING)).method_12597(i, j, k, blockState);
			((Heightmap)this.field_12853.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)).method_12597(i, j, k, blockState);
			((Heightmap)this.field_12853.get(Heightmap.Type.OCEAN_FLOOR)).method_12597(i, j, k, blockState);
			((Heightmap)this.field_12853.get(Heightmap.Type.WORLD_SURFACE)).method_12597(i, j, k, blockState);
			boolean bl3 = chunkSection.isEmpty();
			if (bl2 != bl3) {
				this.world.getChunkManager().getLightingProvider().method_15552(blockPos, bl3);
			}

			if (!this.world.isRemote) {
				blockState2.onBlockRemoved(this.world, blockPos, blockState, bl);
			} else if (block2 != block && block2 instanceof BlockEntityProvider) {
				this.world.removeBlockEntity(blockPos);
			}

			if (chunkSection.getBlockState(i, j & 15, k).getBlock() != block) {
				return null;
			} else {
				if (block2 instanceof BlockEntityProvider) {
					BlockEntity blockEntity = this.getBlockEntity(blockPos, WorldChunk.AccessType.GET);
					if (blockEntity != null) {
						blockEntity.resetBlock();
					}
				}

				if (!this.world.isRemote) {
					blockState.onBlockAdded(this.world, blockPos, blockState2);
				}

				if (block instanceof BlockEntityProvider) {
					BlockEntity blockEntity = this.getBlockEntity(blockPos, WorldChunk.AccessType.GET);
					if (blockEntity == null) {
						blockEntity = ((BlockEntityProvider)block).createBlockEntity(this.world);
						this.world.setBlockEntity(blockPos, blockEntity);
					} else {
						blockEntity.resetBlock();
					}
				}

				this.dirty = true;
				return blockState2;
			}
		}
	}

	@Nullable
	@Override
	public LightingProvider method_12023() {
		return this.world.getChunkManager().getLightingProvider();
	}

	@Override
	public int getLuminance(BlockPos blockPos) {
		return this.getBlockState(blockPos).getLuminance();
	}

	public int method_12233(BlockPos blockPos, int i) {
		return this.method_12035(blockPos, i, this.world.getDimension().method_12451());
	}

	@Override
	public void addEntity(Entity entity) {
		this.field_12837 = true;
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.z / 16.0);
		if (i != this.pos.x || j != this.pos.z) {
			LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.pos.x, this.pos.z, entity);
			entity.invalidate();
		}

		int k = MathHelper.floor(entity.y / 16.0);
		if (k < 0) {
			k = 0;
		}

		if (k >= this.entitySections.length) {
			k = this.entitySections.length - 1;
		}

		entity.field_6016 = true;
		entity.chunkX = this.pos.x;
		entity.chunkY = k;
		entity.chunkZ = this.pos.z;
		this.entitySections[k].add(entity);
	}

	@Override
	public void method_12037(Heightmap.Type type, long[] ls) {
		((Heightmap)this.field_12853.get(type)).fromLongArray(ls);
	}

	public void remove(Entity entity) {
		this.remove(entity, entity.chunkY);
	}

	public void remove(Entity entity, int i) {
		if (i < 0) {
			i = 0;
		}

		if (i >= this.entitySections.length) {
			i = this.entitySections.length - 1;
		}

		this.entitySections[i].remove(entity);
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int i, int j) {
		return ((Heightmap)this.field_12853.get(type)).method_12603(i & 15, j & 15) - 1;
	}

	@Nullable
	private BlockEntity createBlockEntity(BlockPos blockPos) {
		BlockState blockState = this.getBlockState(blockPos);
		Block block = blockState.getBlock();
		return !block.hasBlockEntity() ? null : ((BlockEntityProvider)block).createBlockEntity(this.world);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		return this.getBlockEntity(blockPos, WorldChunk.AccessType.GET);
	}

	@Nullable
	public BlockEntity getBlockEntity(BlockPos blockPos, WorldChunk.AccessType accessType) {
		BlockEntity blockEntity = (BlockEntity)this.blockEntityMap.get(blockPos);
		if (blockEntity == null) {
			CompoundTag compoundTag = (CompoundTag)this.pendingBlockEntityTags.remove(blockPos);
			if (compoundTag != null) {
				BlockEntity blockEntity2 = this.loadBlockEntity(blockPos, compoundTag);
				if (blockEntity2 != null) {
					return blockEntity2;
				}
			}
		}

		if (blockEntity == null) {
			if (accessType == WorldChunk.AccessType.CREATE) {
				blockEntity = this.createBlockEntity(blockPos);
				this.world.setBlockEntity(blockPos, blockEntity);
			}
		} else if (blockEntity.isInvalid()) {
			this.blockEntityMap.remove(blockPos);
			return null;
		}

		return blockEntity;
	}

	public void addBlockEntity(BlockEntity blockEntity) {
		this.setBlockEntity(blockEntity.getPos(), blockEntity);
		if (this.loadedToWorld) {
			this.world.addBlockEntity(blockEntity);
		}
	}

	@Override
	public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
		blockEntity.setWorld(this.world);
		blockEntity.setPos(blockPos);
		if (this.getBlockState(blockPos).getBlock() instanceof BlockEntityProvider) {
			if (this.blockEntityMap.containsKey(blockPos)) {
				((BlockEntity)this.blockEntityMap.get(blockPos)).invalidate();
			}

			blockEntity.validate();
			this.blockEntityMap.put(blockPos.toImmutable(), blockEntity);
		}
	}

	@Override
	public void addPendingBlockEntityTag(CompoundTag compoundTag) {
		this.pendingBlockEntityTags.put(new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")), compoundTag);
	}

	@Override
	public void removeBlockEntity(BlockPos blockPos) {
		if (this.loadedToWorld) {
			BlockEntity blockEntity = (BlockEntity)this.blockEntityMap.remove(blockPos);
			if (blockEntity != null) {
				blockEntity.invalidate();
			}
		}
	}

	public void loadToWorld() {
		if (this.field_12850 != null) {
			this.field_12850.accept(this);
			this.field_12850 = null;
		}

		this.loadedToWorld = true;
		this.world.addBlockEntities(this.blockEntityMap.values());

		for (class_3509<Entity> lv : this.entitySections) {
			this.world.loadEntities(lv.stream().filter(entity -> !(entity instanceof PlayerEntity)));
		}
	}

	public void unloadFromWorld() {
		this.loadedToWorld = false;

		for (BlockEntity blockEntity : this.blockEntityMap.values()) {
			this.world.unloadBlockEntity(blockEntity);
		}

		for (class_3509<Entity> lv : this.entitySections) {
			this.world.unloadEntities(lv);
		}
	}

	public void markDirty() {
		this.dirty = true;
	}

	public void appendEntities(@Nullable Entity entity, BoundingBox boundingBox, List<Entity> list, Predicate<? super Entity> predicate) {
		int i = MathHelper.floor((boundingBox.minY - 2.0) / 16.0);
		int j = MathHelper.floor((boundingBox.maxY + 2.0) / 16.0);
		i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
		j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

		for (int k = i; k <= j; k++) {
			if (!this.entitySections[k].isEmpty()) {
				for (Entity entity2 : this.entitySections[k]) {
					if (entity2.getBoundingBox().intersects(boundingBox) && entity2 != entity) {
						if (predicate == null || predicate.test(entity2)) {
							list.add(entity2);
						}

						Entity[] entitys = entity2.getParts();
						if (entitys != null) {
							for (Entity entity3 : entitys) {
								if (entity3 != entity && entity3.getBoundingBox().intersects(boundingBox) && (predicate == null || predicate.test(entity3))) {
									list.add(entity3);
								}
							}
						}
					}
				}
			}
		}
	}

	public <T extends Entity> void appendEntities(Class<? extends T> class_, BoundingBox boundingBox, List<T> list, @Nullable Predicate<? super T> predicate) {
		int i = MathHelper.floor((boundingBox.minY - 2.0) / 16.0);
		int j = MathHelper.floor((boundingBox.maxY + 2.0) / 16.0);
		i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
		j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

		for (int k = i; k <= j; k++) {
			for (T entity : this.entitySections[k].method_15216(class_)) {
				if (entity.getBoundingBox().intersects(boundingBox) && (predicate == null || predicate.test(entity))) {
					list.add(entity);
				}
			}
		}
	}

	public boolean method_12223() {
		return false;
	}

	@Override
	public ChunkPos getPos() {
		return this.pos;
	}

	public boolean method_12228(int i, int j) {
		if (i < 0) {
			i = 0;
		}

		if (j >= 256) {
			j = 255;
		}

		for (int k = i; k <= j; k += 16) {
			ChunkSection chunkSection = this.sections[k >> 4];
			if (chunkSection != EMPTY_SECTION && !chunkSection.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Environment(EnvType.CLIENT)
	public void method_12224(PacketByteBuf packetByteBuf, CompoundTag compoundTag, int i, boolean bl) {
		if (bl) {
			this.blockEntityMap.clear();
		} else {
			Iterator<BlockPos> iterator = this.blockEntityMap.keySet().iterator();

			while (iterator.hasNext()) {
				BlockPos blockPos = (BlockPos)iterator.next();
				int j = blockPos.getY() >> 4;
				if ((i & 1 << j) != 0) {
					iterator.remove();
				}
			}
		}

		for (int k = 0; k < this.sections.length; k++) {
			ChunkSection chunkSection = this.sections[k];
			if ((i & 1 << k) == 0) {
				if (bl && chunkSection != EMPTY_SECTION) {
					this.sections[k] = EMPTY_SECTION;
				}
			} else {
				if (chunkSection == EMPTY_SECTION) {
					chunkSection = new ChunkSection(k << 4);
					this.sections[k] = chunkSection;
				}

				chunkSection.fromPacket(packetByteBuf);
			}
		}

		if (bl) {
			for (int kx = 0; kx < this.biomeArray.length; kx++) {
				this.biomeArray[kx] = Registry.BIOME.getInt(packetByteBuf.readInt());
			}
		}

		for (Heightmap.Type type : Heightmap.Type.values()) {
			String string = type.getName();
			if (compoundTag.containsKey(string, 12)) {
				this.method_12037(type, compoundTag.getLongArray(string));
			}
		}

		for (BlockEntity blockEntity : this.blockEntityMap.values()) {
			blockEntity.resetBlock();
		}
	}

	@Override
	public Biome[] getBiomeArray() {
		return this.biomeArray;
	}

	public boolean isLoadedToWorld() {
		return this.loadedToWorld;
	}

	@Environment(EnvType.CLIENT)
	public void setLoadedToWorld(boolean bl) {
		this.loadedToWorld = bl;
	}

	public World getWorld() {
		return this.world;
	}

	@Override
	public Collection<Entry<Heightmap.Type, Heightmap>> method_12011() {
		return Collections.unmodifiableSet(this.field_12853.entrySet());
	}

	public Map<BlockPos, BlockEntity> getBlockEntityMap() {
		return this.blockEntityMap;
	}

	public class_3509<Entity>[] getEntitySectionArray() {
		return this.entitySections;
	}

	@Override
	public CompoundTag method_12024(BlockPos blockPos) {
		return (CompoundTag)this.pendingBlockEntityTags.get(blockPos);
	}

	@Override
	public Stream<BlockPos> method_12018() {
		return StreamSupport.stream(
				BlockPos.Mutable.iterateBoxPositions(this.pos.getXStart(), 0, this.pos.getZStart(), this.pos.getXEnd(), 255, this.pos.getZEnd()).spliterator(), false
			)
			.filter(blockPos -> this.getBlockState(blockPos).getLuminance() != 0);
	}

	@Override
	public TickScheduler<Block> method_12013() {
		return this.field_12841;
	}

	@Override
	public TickScheduler<Fluid> method_12014() {
		return this.field_12857;
	}

	@Override
	public void method_12008(boolean bl) {
		this.dirty = bl;
	}

	@Override
	public boolean method_12044() {
		return this.dirty || this.field_12837 && this.world.getTime() != this.field_12844;
	}

	public void method_12232(boolean bl) {
		this.field_12837 = bl;
	}

	@Override
	public void method_12043(long l) {
		this.field_12844 = l;
	}

	@Nullable
	@Override
	public class_3449 method_12181(String string) {
		return (class_3449)this.field_12838.get(string);
	}

	@Override
	public void method_12184(String string, class_3449 arg) {
		this.field_12838.put(string, arg);
	}

	@Override
	public Map<String, class_3449> method_12016() {
		return this.field_12838;
	}

	@Override
	public void method_12034(Map<String, class_3449> map) {
		this.field_12838.clear();
		this.field_12838.putAll(map);
	}

	@Override
	public LongSet method_12180(String string) {
		return (LongSet)this.field_12845.computeIfAbsent(string, stringx -> new LongOpenHashSet());
	}

	@Override
	public void method_12182(String string, long l) {
		((LongSet)this.field_12845.computeIfAbsent(string, stringx -> new LongOpenHashSet())).add(l);
	}

	@Override
	public Map<String, LongSet> method_12179() {
		return this.field_12845;
	}

	@Override
	public void method_12183(Map<String, LongSet> map) {
		this.field_12845.clear();
		this.field_12845.putAll(map);
	}

	@Override
	public long method_12033() {
		return this.field_12843;
	}

	@Override
	public void method_12028(long l) {
		this.field_12843 = l;
	}

	public void method_12221() {
		ChunkPos chunkPos = this.getPos();

		for (int i = 0; i < this.field_12836.length; i++) {
			if (this.field_12836[i] != null) {
				for (Short short_ : this.field_12836[i]) {
					BlockPos blockPos = class_2839.joinBlockPos(short_, i, chunkPos);
					BlockState blockState = this.getBlockState(blockPos);
					BlockState blockState2 = Block.getRenderingState(blockState, this.world, blockPos);
					this.world.setBlockState(blockPos, blockState2, 20);
				}

				this.field_12836[i].clear();
			}
		}

		if (this.field_12841 instanceof ChunkTickScheduler) {
			((ChunkTickScheduler)this.field_12841).tick(this.world.getBlockTickScheduler(), blockPosx -> this.getBlockState(blockPosx).getBlock());
		}

		if (this.field_12857 instanceof ChunkTickScheduler) {
			((ChunkTickScheduler)this.field_12857).tick(this.world.getFluidTickScheduler(), blockPosx -> this.getFluidState(blockPosx).getFluid());
		}

		for (BlockPos blockPos2 : new HashSet(this.pendingBlockEntityTags.keySet())) {
			this.getBlockEntity(blockPos2);
		}

		this.pendingBlockEntityTags.clear();
		this.field_12849.method_12356(this);
	}

	@Nullable
	private BlockEntity loadBlockEntity(BlockPos blockPos, CompoundTag compoundTag) {
		BlockEntity blockEntity;
		if ("DUMMY".equals(compoundTag.getString("id"))) {
			Block block = this.getBlockState(blockPos).getBlock();
			if (block instanceof BlockEntityProvider) {
				blockEntity = ((BlockEntityProvider)block).createBlockEntity(this.world);
			} else {
				blockEntity = null;
				LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", blockPos, this.getBlockState(blockPos));
			}
		} else {
			blockEntity = BlockEntity.createFromTag(compoundTag);
		}

		if (blockEntity != null) {
			blockEntity.setPos(blockPos);
			this.addBlockEntity(blockEntity);
		} else {
			LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", this.getBlockState(blockPos), blockPos);
		}

		return blockEntity;
	}

	@Override
	public class_2843 method_12003() {
		return this.field_12849;
	}

	@Override
	public ShortList[] method_12012() {
		return this.field_12836;
	}

	@Override
	public ChunkStatus getStatus() {
		return ChunkStatus.field_12803;
	}

	public ServerChunkManagerEntry.class_3194 method_12225() {
		return this.field_12856 == null ? ServerChunkManagerEntry.class_3194.field_13876 : (ServerChunkManagerEntry.class_3194)this.field_12856.get();
	}

	public void method_12207(Supplier<ServerChunkManagerEntry.class_3194> supplier) {
		this.field_12856 = supplier;
	}

	@Override
	public void method_12027(ChunkManager chunkManager) {
	}

	@Override
	public boolean method_12038() {
		return this.field_12847;
	}

	@Override
	public void method_12020(boolean bl) {
		this.field_12847 = bl;
	}

	public static enum AccessType {
		CREATE,
		field_12861,
		GET;
	}
}
