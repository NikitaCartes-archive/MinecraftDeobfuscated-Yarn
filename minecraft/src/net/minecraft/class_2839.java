package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2839 implements Chunk {
	private static final Logger field_12920 = LogManager.getLogger();
	private final ChunkPos field_12928;
	private boolean field_12924;
	private Biome[] field_12913;
	@Nullable
	private ChunkManager field_12910;
	private final Map<Heightmap.Type, Heightmap> field_12912 = Maps.newEnumMap(Heightmap.Type.class);
	private volatile ChunkStatus field_12918 = ChunkStatus.field_12798;
	private final Map<BlockPos, BlockEntity> field_12917 = Maps.<BlockPos, BlockEntity>newHashMap();
	private final Map<BlockPos, CompoundTag> field_12927 = Maps.<BlockPos, CompoundTag>newHashMap();
	private final ChunkSection[] field_12909 = new ChunkSection[16];
	private final List<CompoundTag> field_12929 = Lists.<CompoundTag>newArrayList();
	private final List<BlockPos> field_12919 = Lists.<BlockPos>newArrayList();
	private final ShortList[] field_12921 = new ShortList[16];
	private final Map<String, class_3449> field_12915 = Maps.<String, class_3449>newHashMap();
	private final Map<String, LongSet> field_12930 = Maps.<String, LongSet>newHashMap();
	private final class_2843 field_12916;
	private final ChunkTickScheduler<Block> field_12911;
	private final ChunkTickScheduler<Fluid> field_12923;
	private long field_12925;
	private final Map<GenerationStep.Carver, BitSet> field_12926 = Maps.<GenerationStep.Carver, BitSet>newHashMap();
	private boolean field_12914;

	public class_2839(
		int i, int j, class_2843 arg, ChunkSection[] chunkSections, ChunkTickScheduler<Block> chunkTickScheduler, ChunkTickScheduler<Fluid> chunkTickScheduler2
	) {
		this(new ChunkPos(i, j), arg, chunkTickScheduler, chunkTickScheduler2);
		if (this.field_12909.length != chunkSections.length) {
			field_12920.warn("Could not set level chunk sections, array length is {} instead of {}", chunkSections.length, this.field_12909.length);
		} else {
			System.arraycopy(chunkSections, 0, this.field_12909, 0, this.field_12909.length);
		}
	}

	public class_2839(ChunkPos chunkPos, class_2843 arg) {
		this(
			chunkPos,
			arg,
			new ChunkTickScheduler<>(block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, chunkPos),
			new ChunkTickScheduler<>(fluid -> fluid == null || fluid == Fluids.field_15906, Registry.FLUID::getId, Registry.FLUID::get, chunkPos)
		);
	}

	public class_2839(ChunkPos chunkPos, class_2843 arg, ChunkTickScheduler<Block> chunkTickScheduler, ChunkTickScheduler<Fluid> chunkTickScheduler2) {
		this.field_12928 = chunkPos;
		this.field_12916 = arg;
		this.field_12911 = chunkTickScheduler;
		this.field_12923 = chunkTickScheduler2;
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		if (World.isHeightInvaid(blockPos)) {
			return Blocks.field_10243.getDefaultState();
		} else {
			return this.getSectionArray()[blockPos.getY() >> 4] == WorldChunk.EMPTY_SECTION
				? Blocks.field_10124.getDefaultState()
				: this.getSectionArray()[blockPos.getY() >> 4].getBlockState(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
		}
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		return j >= 0 && j < 256 && this.field_12909[j >> 4] != WorldChunk.EMPTY_SECTION
			? this.field_12909[j >> 4].getFluidState(i & 15, j & 15, k & 15)
			: Fluids.field_15906.getDefaultState();
	}

	@Override
	public int getLuminance(BlockPos blockPos) {
		return this.field_12910 == null ? 0 : this.getBlockState(blockPos).getLuminance();
	}

	@Override
	public Stream<BlockPos> method_12018() {
		return this.field_12919.stream();
	}

	public ShortList[] method_12296() {
		ShortList[] shortLists = new ShortList[16];

		for (BlockPos blockPos : this.field_12919) {
			Chunk.getListFromArray(shortLists, blockPos.getY() >> 4).add(getPackedChunkSliceRelative(blockPos));
		}

		return shortLists;
	}

	public void method_12304(short s, int i) {
		this.method_12315(joinBlockPos(s, i, this.field_12928));
	}

	public void method_12315(BlockPos blockPos) {
		this.field_12919.add(blockPos.toImmutable());
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos blockPos, BlockState blockState, boolean bl) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		if (j >= 0 && j < 256) {
			if (this.field_12909[j >> 4] == WorldChunk.EMPTY_SECTION && blockState.getBlock() == Blocks.field_10124) {
				return blockState;
			} else {
				if (blockState.getLuminance() > 0) {
					this.field_12919.add(new BlockPos((i & 15) + this.getPos().getXStart(), j, (k & 15) + this.getPos().getZStart()));
				}

				ChunkSection chunkSection = this.method_16679(j >> 4);
				BlockState blockState2 = chunkSection.method_16675(i & 15, j & 15, k & 15, blockState);
				if (!this.getStatus().method_12160()) {
					Heightmap heightmap = (Heightmap)this.field_12912.get(Heightmap.Type.OCEAN_FLOOR_WG);
					Heightmap heightmap2 = (Heightmap)this.field_12912.get(Heightmap.Type.WORLD_SURFACE_WG);
					if (heightmap == null || heightmap2 == null) {
						EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);
						if (heightmap == null) {
							enumSet.add(Heightmap.Type.OCEAN_FLOOR_WG);
						}

						if (heightmap2 == null) {
							enumSet.add(Heightmap.Type.WORLD_SURFACE_WG);
						}

						Heightmap.method_16684(this, enumSet);
						heightmap = (Heightmap)this.field_12912.get(Heightmap.Type.OCEAN_FLOOR_WG);
						heightmap2 = (Heightmap)this.field_12912.get(Heightmap.Type.WORLD_SURFACE_WG);
					}

					heightmap.method_12597(i & 15, j, k & 15, blockState);
					heightmap2.method_12597(i & 15, j, k & 15, blockState);
				} else {
					Heightmap heightmap = (Heightmap)this.field_12912.get(Heightmap.Type.MOTION_BLOCKING);
					Heightmap heightmap2 = (Heightmap)this.field_12912.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
					Heightmap heightmap3 = (Heightmap)this.field_12912.get(Heightmap.Type.OCEAN_FLOOR);
					Heightmap heightmap4 = (Heightmap)this.field_12912.get(Heightmap.Type.WORLD_SURFACE);
					if (heightmap == null || heightmap2 == null || heightmap3 == null || heightmap4 == null) {
						EnumSet<Heightmap.Type> enumSet2 = EnumSet.noneOf(Heightmap.Type.class);
						if (heightmap == null) {
							enumSet2.add(Heightmap.Type.MOTION_BLOCKING);
						}

						if (heightmap2 == null) {
							enumSet2.add(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
						}

						if (heightmap3 == null) {
							enumSet2.add(Heightmap.Type.OCEAN_FLOOR);
						}

						if (heightmap4 == null) {
							enumSet2.add(Heightmap.Type.WORLD_SURFACE);
						}

						Heightmap.method_16684(this, enumSet2);
						heightmap = (Heightmap)this.field_12912.get(Heightmap.Type.MOTION_BLOCKING);
						heightmap2 = (Heightmap)this.field_12912.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
						heightmap3 = (Heightmap)this.field_12912.get(Heightmap.Type.OCEAN_FLOOR);
						heightmap4 = (Heightmap)this.field_12912.get(Heightmap.Type.WORLD_SURFACE);
					}

					heightmap.method_12597(i & 15, j, k & 15, blockState);
					heightmap2.method_12597(i & 15, j, k & 15, blockState);
					heightmap3.method_12597(i & 15, j, k & 15, blockState);
					heightmap4.method_12597(i & 15, j, k & 15, blockState);
				}

				return blockState2;
			}
		} else {
			return Blocks.field_10243.getDefaultState();
		}
	}

	public ChunkSection method_16679(int i) {
		if (this.field_12909[i] == WorldChunk.EMPTY_SECTION) {
			this.field_12909[i] = new ChunkSection(i << 4);
		}

		return this.field_12909[i];
	}

	@Override
	public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
		blockEntity.setPos(blockPos);
		this.field_12917.put(blockPos, blockEntity);
	}

	@Override
	public Set<BlockPos> method_12021() {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet(this.field_12927.keySet());
		set.addAll(this.field_12917.keySet());
		return set;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		return (BlockEntity)this.field_12917.get(blockPos);
	}

	public Map<BlockPos, BlockEntity> method_12309() {
		return this.field_12917;
	}

	public void method_12302(CompoundTag compoundTag) {
		this.field_12929.add(compoundTag);
	}

	@Override
	public void addEntity(Entity entity) {
		CompoundTag compoundTag = new CompoundTag();
		entity.saveToTag(compoundTag);
		this.method_12302(compoundTag);
	}

	public List<CompoundTag> method_12295() {
		return this.field_12929;
	}

	@Override
	public void setBiomes(Biome[] biomes) {
		this.field_12913 = biomes;
	}

	@Override
	public Biome[] getBiomeArray() {
		return this.field_12913;
	}

	@Override
	public void method_12008(boolean bl) {
		this.field_12924 = bl;
	}

	@Override
	public boolean method_12044() {
		return this.field_12924;
	}

	@Override
	public ChunkStatus getStatus() {
		return this.field_12918;
	}

	public void method_12308(ChunkStatus chunkStatus) {
		this.field_12918 = chunkStatus;
		this.method_12008(true);
	}

	@Override
	public ChunkSection[] getSectionArray() {
		return this.field_12909;
	}

	@Nullable
	@Override
	public LightingProvider method_12023() {
		return this.field_12910 == null ? null : this.field_12910.getLightingProvider();
	}

	@Override
	public Collection<Entry<Heightmap.Type, Heightmap>> method_12011() {
		return Collections.unmodifiableSet(this.field_12912.entrySet());
	}

	@Override
	public void method_12037(Heightmap.Type type, long[] ls) {
		this.getHeightmap(type).fromLongArray(ls);
	}

	@Override
	public Heightmap getHeightmap(Heightmap.Type type) {
		return (Heightmap)this.field_12912.computeIfAbsent(type, typex -> new Heightmap(this, typex));
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int i, int j) {
		Heightmap heightmap = (Heightmap)this.field_12912.get(type);
		if (heightmap == null) {
			Heightmap.method_16684(this, EnumSet.of(type));
			heightmap = (Heightmap)this.field_12912.get(type);
		}

		return heightmap.method_12603(i & 15, j & 15) - 1;
	}

	@Override
	public ChunkPos getPos() {
		return this.field_12928;
	}

	@Override
	public void method_12043(long l) {
	}

	@Nullable
	@Override
	public class_3449 method_12181(String string) {
		return (class_3449)this.field_12915.get(string);
	}

	@Override
	public void method_12184(String string, class_3449 arg) {
		this.field_12915.put(string, arg);
		this.field_12924 = true;
	}

	@Override
	public Map<String, class_3449> method_12016() {
		return Collections.unmodifiableMap(this.field_12915);
	}

	@Override
	public void method_12034(Map<String, class_3449> map) {
		this.field_12915.clear();
		this.field_12915.putAll(map);
		this.field_12924 = true;
	}

	@Override
	public LongSet method_12180(String string) {
		return (LongSet)this.field_12930.computeIfAbsent(string, stringx -> new LongOpenHashSet());
	}

	@Override
	public void method_12182(String string, long l) {
		((LongSet)this.field_12930.computeIfAbsent(string, stringx -> new LongOpenHashSet())).add(l);
		this.field_12924 = true;
	}

	@Override
	public Map<String, LongSet> method_12179() {
		return Collections.unmodifiableMap(this.field_12930);
	}

	@Override
	public void method_12183(Map<String, LongSet> map) {
		this.field_12930.clear();
		this.field_12930.putAll(map);
		this.field_12924 = true;
	}

	public static short getPackedChunkSliceRelative(BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		int l = i & 15;
		int m = j & 15;
		int n = k & 15;
		return (short)(l | m << 4 | n << 8);
	}

	public static BlockPos joinBlockPos(short s, int i, ChunkPos chunkPos) {
		int j = (s & 15) + (chunkPos.x << 4);
		int k = (s >>> 4 & 15) + (i << 4);
		int l = (s >>> 8 & 15) + (chunkPos.z << 4);
		return new BlockPos(j, k, l);
	}

	@Override
	public void markBlockForPostProcessing(BlockPos blockPos) {
		if (!World.isHeightInvaid(blockPos)) {
			Chunk.getListFromArray(this.field_12921, blockPos.getY() >> 4).add(getPackedChunkSliceRelative(blockPos));
		}
	}

	@Override
	public ShortList[] method_12012() {
		return this.field_12921;
	}

	@Override
	public void method_12029(short s, int i) {
		Chunk.getListFromArray(this.field_12921, i).add(s);
	}

	public ChunkTickScheduler<Block> method_12303() {
		return this.field_12911;
	}

	public ChunkTickScheduler<Fluid> method_12313() {
		return this.field_12923;
	}

	@Override
	public class_2843 method_12003() {
		return this.field_12916;
	}

	@Override
	public void method_12028(long l) {
		this.field_12925 = l;
	}

	@Override
	public long method_12033() {
		return this.field_12925;
	}

	@Override
	public void addPendingBlockEntityTag(CompoundTag compoundTag) {
		this.field_12927.put(new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")), compoundTag);
	}

	public Map<BlockPos, CompoundTag> method_12316() {
		return Collections.unmodifiableMap(this.field_12927);
	}

	@Override
	public CompoundTag method_12024(BlockPos blockPos) {
		return (CompoundTag)this.field_12927.get(blockPos);
	}

	@Override
	public void removeBlockEntity(BlockPos blockPos) {
		this.field_12917.remove(blockPos);
		this.field_12927.remove(blockPos);
	}

	@Override
	public BitSet method_12025(GenerationStep.Carver carver) {
		return (BitSet)this.field_12926.computeIfAbsent(carver, carverx -> new BitSet(65536));
	}

	public void method_12307(GenerationStep.Carver carver, BitSet bitSet) {
		this.field_12926.put(carver, bitSet);
	}

	@Override
	public void method_12027(ChunkManager chunkManager) {
		this.field_12910 = chunkManager;
	}

	@Override
	public boolean method_12038() {
		return this.field_12914;
	}

	@Override
	public void method_12020(boolean bl) {
		this.field_12914 = bl;
	}
}
