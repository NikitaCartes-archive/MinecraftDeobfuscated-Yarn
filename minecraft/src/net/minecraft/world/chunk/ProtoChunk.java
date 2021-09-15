package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProtoChunk extends Chunk {
	private static final Logger LOGGER = LogManager.getLogger();
	@Nullable
	private volatile LightingProvider lightingProvider;
	private volatile ChunkStatus status = ChunkStatus.EMPTY;
	private final List<NbtCompound> entities = Lists.<NbtCompound>newArrayList();
	private final List<BlockPos> lightSources = Lists.<BlockPos>newArrayList();
	private final Map<GenerationStep.Carver, BitSet> carvingMasks = new Object2ObjectArrayMap<>();

	public ProtoChunk(ChunkPos pos, UpgradeData upgradeData, HeightLimitView world, Registry<Biome> registry) {
		this(
			pos,
			upgradeData,
			null,
			new ChunkTickScheduler<>(block -> block == null || block.getDefaultState().isAir(), pos, world),
			new ChunkTickScheduler<>(fluid -> fluid == null || fluid == Fluids.EMPTY, pos, world),
			world,
			registry
		);
	}

	public ProtoChunk(
		ChunkPos pos,
		UpgradeData upgradeData,
		@Nullable ChunkSection[] chunkSections,
		ChunkTickScheduler<Block> blockTickScheduler,
		ChunkTickScheduler<Fluid> fluidTickScheduler,
		HeightLimitView world,
		Registry<Biome> registry
	) {
		super(pos, upgradeData, world, registry, 0L, chunkSections, blockTickScheduler, fluidTickScheduler);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		int i = pos.getY();
		if (this.isOutOfHeightLimit(i)) {
			return Blocks.VOID_AIR.getDefaultState();
		} else {
			ChunkSection chunkSection = this.getSection(this.getSectionIndex(i));
			return chunkSection.isEmpty() ? Blocks.AIR.getDefaultState() : chunkSection.getBlockState(pos.getX() & 15, i & 15, pos.getZ() & 15);
		}
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		int i = pos.getY();
		if (this.isOutOfHeightLimit(i)) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			ChunkSection chunkSection = this.getSection(this.getSectionIndex(i));
			return chunkSection.isEmpty() ? Fluids.EMPTY.getDefaultState() : chunkSection.getFluidState(pos.getX() & 15, i & 15, pos.getZ() & 15);
		}
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return this.lightSources.stream();
	}

	public ShortList[] getLightSourcesBySection() {
		ShortList[] shortLists = new ShortList[this.countVerticalSections()];

		for (BlockPos blockPos : this.lightSources) {
			Chunk.getList(shortLists, this.getSectionIndex(blockPos.getY())).add(getPackedSectionRelative(blockPos));
		}

		return shortLists;
	}

	public void addLightSource(short chunkSliceRel, int sectionY) {
		this.addLightSource(joinBlockPos(chunkSliceRel, this.sectionIndexToCoord(sectionY), this.field_34538));
	}

	public void addLightSource(BlockPos pos) {
		this.lightSources.add(pos.toImmutable());
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		if (j >= this.getBottomY() && j < this.getTopY()) {
			int l = this.getSectionIndex(j);
			if (this.field_34545[l].isEmpty() && state.isOf(Blocks.AIR)) {
				return state;
			} else {
				if (state.getLuminance() > 0) {
					this.lightSources.add(new BlockPos((i & 15) + this.getPos().getStartX(), j, (k & 15) + this.getPos().getStartZ()));
				}

				ChunkSection chunkSection = this.getSection(l);
				BlockState blockState = chunkSection.setBlockState(i & 15, j & 15, k & 15, state);
				if (this.status.isAtLeast(ChunkStatus.FEATURES)
					&& state != blockState
					&& (
						state.getOpacity(this, pos) != blockState.getOpacity(this, pos)
							|| state.getLuminance() != blockState.getLuminance()
							|| state.hasSidedTransparency()
							|| blockState.hasSidedTransparency()
					)) {
					this.lightingProvider.checkBlock(pos);
				}

				EnumSet<Heightmap.Type> enumSet = this.getStatus().getHeightmapTypes();
				EnumSet<Heightmap.Type> enumSet2 = null;

				for (Heightmap.Type type : enumSet) {
					Heightmap heightmap = (Heightmap)this.field_34541.get(type);
					if (heightmap == null) {
						if (enumSet2 == null) {
							enumSet2 = EnumSet.noneOf(Heightmap.Type.class);
						}

						enumSet2.add(type);
					}
				}

				if (enumSet2 != null) {
					Heightmap.populateHeightmaps(this, enumSet2);
				}

				for (Heightmap.Type typex : enumSet) {
					((Heightmap)this.field_34541.get(typex)).trackUpdate(i & 15, j, k & 15, state);
				}

				return blockState;
			}
		} else {
			return Blocks.VOID_AIR.getDefaultState();
		}
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity) {
		this.field_34543.put(blockEntity.getPos(), blockEntity);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return (BlockEntity)this.field_34543.get(pos);
	}

	public Map<BlockPos, BlockEntity> getBlockEntities() {
		return this.field_34543;
	}

	public void addEntity(NbtCompound entityTag) {
		this.entities.add(entityTag);
	}

	@Override
	public void addEntity(Entity entity) {
		if (!entity.hasVehicle()) {
			NbtCompound nbtCompound = new NbtCompound();
			entity.saveNbt(nbtCompound);
			this.addEntity(nbtCompound);
		}
	}

	public List<NbtCompound> getEntities() {
		return this.entities;
	}

	@Override
	public ChunkStatus getStatus() {
		return this.status;
	}

	public void setStatus(ChunkStatus status) {
		this.status = status;
		this.setShouldSave(true);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		if (!this.getStatus().isAtLeast(ChunkStatus.BIOMES)) {
			throw new IllegalStateException("Asking for biomes before we have biomes");
		} else {
			return super.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
		}
	}

	public static short getPackedSectionRelative(BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		int l = i & 15;
		int m = j & 15;
		int n = k & 15;
		return (short)(l | m << 4 | n << 8);
	}

	public static BlockPos joinBlockPos(short sectionRel, int sectionY, ChunkPos chunkPos) {
		int i = ChunkSectionPos.getOffsetPos(chunkPos.x, sectionRel & 15);
		int j = ChunkSectionPos.getOffsetPos(sectionY, sectionRel >>> 4 & 15);
		int k = ChunkSectionPos.getOffsetPos(chunkPos.z, sectionRel >>> 8 & 15);
		return new BlockPos(i, j, k);
	}

	@Override
	public void markBlockForPostProcessing(BlockPos pos) {
		if (!this.isOutOfHeightLimit(pos)) {
			Chunk.getList(this.field_34536, this.getSectionIndex(pos.getY())).add(getPackedSectionRelative(pos));
		}
	}

	@Override
	public void markBlockForPostProcessing(short packedPos, int index) {
		Chunk.getList(this.field_34536, index).add(packedPos);
	}

	public Map<BlockPos, NbtCompound> getBlockEntityNbts() {
		return Collections.unmodifiableMap(this.field_34542);
	}

	@Nullable
	@Override
	public NbtCompound getPackedBlockEntityNbt(BlockPos pos) {
		BlockEntity blockEntity = this.getBlockEntity(pos);
		return blockEntity != null ? blockEntity.createNbtWithIdentifyingData() : (NbtCompound)this.field_34542.get(pos);
	}

	@Override
	public void removeBlockEntity(BlockPos pos) {
		this.field_34543.remove(pos);
		this.field_34542.remove(pos);
	}

	@Nullable
	public BitSet getCarvingMask(GenerationStep.Carver carver) {
		return (BitSet)this.carvingMasks.get(carver);
	}

	public BitSet getOrCreateCarvingMask(GenerationStep.Carver carver) {
		return (BitSet)this.carvingMasks.computeIfAbsent(carver, carverx -> new BitSet(98304));
	}

	public void setCarvingMask(GenerationStep.Carver carver, BitSet mask) {
		this.carvingMasks.put(carver, mask);
	}

	public void setLightingProvider(LightingProvider lightingProvider) {
		this.lightingProvider = lightingProvider;
	}
}
