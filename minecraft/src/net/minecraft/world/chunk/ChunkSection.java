package net.minecraft.world.chunk;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class ChunkSection {
	public static final int field_31406 = 16;
	public static final int field_31407 = 16;
	public static final int field_31408 = 4096;
	public static final int field_34555 = 2;
	private final int yOffset;
	private short nonEmptyBlockCount;
	private short randomTickableBlockCount;
	private short nonEmptyFluidCount;
	private final PalettedContainer<BlockState> blockStateContainer;
	private final PalettedContainer<Biome> biomeContainer;

	public ChunkSection(int i, PalettedContainer<BlockState> blockStateContainer, PalettedContainer<Biome> biomeContainer) {
		this.yOffset = blockCoordFromChunkCoord(i);
		this.blockStateContainer = blockStateContainer;
		this.biomeContainer = biomeContainer;
		this.calculateCounts();
	}

	public ChunkSection(int i, Registry<Biome> biomeRegistry) {
		this.yOffset = blockCoordFromChunkCoord(i);
		this.blockStateContainer = new PalettedContainer<>(Block.STATE_IDS, Blocks.AIR.getDefaultState(), PalettedContainer.PaletteProvider.BLOCK_STATE);
		this.biomeContainer = new PalettedContainer<>(biomeRegistry, biomeRegistry.getOrThrow(BiomeKeys.PLAINS), PalettedContainer.PaletteProvider.BIOME);
	}

	public static int blockCoordFromChunkCoord(int chunkPos) {
		return chunkPos << 4;
	}

	public BlockState getBlockState(int x, int y, int z) {
		return this.blockStateContainer.get(x, y, z);
	}

	public FluidState getFluidState(int x, int y, int z) {
		return this.blockStateContainer.get(x, y, z).getFluidState();
	}

	public void lock() {
		this.blockStateContainer.lock();
	}

	public void unlock() {
		this.blockStateContainer.unlock();
	}

	public BlockState setBlockState(int x, int y, int z, BlockState state) {
		return this.setBlockState(x, y, z, state, true);
	}

	public BlockState setBlockState(int x, int y, int z, BlockState state, boolean lock) {
		BlockState blockState;
		if (lock) {
			blockState = this.blockStateContainer.swap(x, y, z, state);
		} else {
			blockState = this.blockStateContainer.swapUnsafe(x, y, z, state);
		}

		FluidState fluidState = blockState.getFluidState();
		FluidState fluidState2 = state.getFluidState();
		if (!blockState.isAir()) {
			this.nonEmptyBlockCount--;
			if (blockState.hasRandomTicks()) {
				this.randomTickableBlockCount--;
			}
		}

		if (!fluidState.isEmpty()) {
			this.nonEmptyFluidCount--;
		}

		if (!state.isAir()) {
			this.nonEmptyBlockCount++;
			if (state.hasRandomTicks()) {
				this.randomTickableBlockCount++;
			}
		}

		if (!fluidState2.isEmpty()) {
			this.nonEmptyFluidCount++;
		}

		return blockState;
	}

	public boolean isEmpty() {
		return this.nonEmptyBlockCount == 0;
	}

	public boolean hasRandomTicks() {
		return this.hasRandomBlockTicks() || this.hasRandomFluidTicks();
	}

	public boolean hasRandomBlockTicks() {
		return this.randomTickableBlockCount > 0;
	}

	public boolean hasRandomFluidTicks() {
		return this.nonEmptyFluidCount > 0;
	}

	public int getYOffset() {
		return this.yOffset;
	}

	public void calculateCounts() {
		this.nonEmptyBlockCount = 0;
		this.randomTickableBlockCount = 0;
		this.nonEmptyFluidCount = 0;
		this.blockStateContainer.count((state, count) -> {
			FluidState fluidState = state.getFluidState();
			if (!state.isAir()) {
				this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + count);
				if (state.hasRandomTicks()) {
					this.randomTickableBlockCount = (short)(this.randomTickableBlockCount + count);
				}
			}

			if (!fluidState.isEmpty()) {
				this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + count);
				if (fluidState.hasRandomTicks()) {
					this.nonEmptyFluidCount = (short)(this.nonEmptyFluidCount + count);
				}
			}
		});
	}

	public PalettedContainer<BlockState> getBlockStateContainer() {
		return this.blockStateContainer;
	}

	public PalettedContainer<Biome> getBiomeContainer() {
		return this.biomeContainer;
	}

	public void fromPacket(PacketByteBuf buf) {
		this.nonEmptyBlockCount = buf.readShort();
		this.blockStateContainer.readPacket(buf);
		this.biomeContainer.readPacket(buf);
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeShort(this.nonEmptyBlockCount);
		this.blockStateContainer.writePacket(buf);
		this.biomeContainer.writePacket(buf);
	}

	public int getPacketSize() {
		return 2 + this.blockStateContainer.getPacketSize() + this.biomeContainer.getPacketSize();
	}

	public boolean hasAny(Predicate<BlockState> predicate) {
		return this.blockStateContainer.hasAny(predicate);
	}

	public Biome getBiome(int x, int y, int z) {
		return this.biomeContainer.get(x, y, z);
	}

	public void method_38291(BiomeSource source, MultiNoiseUtil.MultiNoiseSampler sampler, int x, int z) {
		PalettedContainer<Biome> palettedContainer = this.getBiomeContainer();
		palettedContainer.lock();

		try {
			int i = BiomeCoords.fromBlock(this.getYOffset());
			int j = 4;

			for (int k = 0; k < 4; k++) {
				for (int l = 0; l < 4; l++) {
					for (int m = 0; m < 4; m++) {
						palettedContainer.swapUnsafe(k, l, m, source.getBiome(x + k, i + l, z + m, sampler));
					}
				}
			}
		} finally {
			palettedContainer.unlock();
		}
	}
}
