package net.minecraft.world.chunk;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class ChunkSection {
	public static final int field_31406 = 16;
	public static final int field_31407 = 16;
	public static final int field_31408 = 4096;
	public static final int field_34555 = 2;
	private short nonEmptyBlockCount;
	private short randomTickableBlockCount;
	private short nonEmptyFluidCount;
	private final PalettedContainer<BlockState> blockStateContainer;
	private ReadableContainer<RegistryEntry<Biome>> biomeContainer;

	public ChunkSection(PalettedContainer<BlockState> blockStateContainer, ReadableContainer<RegistryEntry<Biome>> biomeContainer) {
		this.blockStateContainer = blockStateContainer;
		this.biomeContainer = biomeContainer;
		this.calculateCounts();
	}

	public ChunkSection(Registry<Biome> biomeRegistry) {
		this.blockStateContainer = new PalettedContainer<>(Block.STATE_IDS, Blocks.AIR.getDefaultState(), PalettedContainer.PaletteProvider.BLOCK_STATE);
		this.biomeContainer = new PalettedContainer<>(
			biomeRegistry.getIndexedEntries(), biomeRegistry.entryOf(BiomeKeys.PLAINS), PalettedContainer.PaletteProvider.BIOME
		);
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

	public void calculateCounts() {
		class BlockStateCounter implements PalettedContainer.Counter<BlockState> {
			public int nonEmptyBlockCount;
			public int randomTickableBlockCount;
			public int nonEmptyFluidCount;

			public void accept(BlockState blockState, int i) {
				FluidState fluidState = blockState.getFluidState();
				if (!blockState.isAir()) {
					this.nonEmptyBlockCount += i;
					if (blockState.hasRandomTicks()) {
						this.randomTickableBlockCount += i;
					}
				}

				if (!fluidState.isEmpty()) {
					this.nonEmptyBlockCount += i;
					if (fluidState.hasRandomTicks()) {
						this.nonEmptyFluidCount += i;
					}
				}
			}
		}

		BlockStateCounter blockStateCounter = new BlockStateCounter();
		this.blockStateContainer.count(blockStateCounter);
		this.nonEmptyBlockCount = (short)blockStateCounter.nonEmptyBlockCount;
		this.randomTickableBlockCount = (short)blockStateCounter.randomTickableBlockCount;
		this.nonEmptyFluidCount = (short)blockStateCounter.nonEmptyFluidCount;
	}

	public PalettedContainer<BlockState> getBlockStateContainer() {
		return this.blockStateContainer;
	}

	public ReadableContainer<RegistryEntry<Biome>> getBiomeContainer() {
		return this.biomeContainer;
	}

	public void readDataPacket(PacketByteBuf buf) {
		this.nonEmptyBlockCount = buf.readShort();
		this.blockStateContainer.readPacket(buf);
		PalettedContainer<RegistryEntry<Biome>> palettedContainer = this.biomeContainer.slice();
		palettedContainer.readPacket(buf);
		this.biomeContainer = palettedContainer;
	}

	public void readBiomePacket(PacketByteBuf buf) {
		PalettedContainer<RegistryEntry<Biome>> palettedContainer = this.biomeContainer.slice();
		palettedContainer.readPacket(buf);
		this.biomeContainer = palettedContainer;
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

	public RegistryEntry<Biome> getBiome(int x, int y, int z) {
		return this.biomeContainer.get(x, y, z);
	}

	public void populateBiomes(BiomeSupplier biomeSupplier, MultiNoiseUtil.MultiNoiseSampler sampler, int x, int y, int z) {
		PalettedContainer<RegistryEntry<Biome>> palettedContainer = this.biomeContainer.slice();
		int i = 4;

		for (int j = 0; j < 4; j++) {
			for (int k = 0; k < 4; k++) {
				for (int l = 0; l < 4; l++) {
					palettedContainer.swapUnsafe(j, k, l, biomeSupplier.getBiome(x + j, y + k, z + l, sampler));
				}
			}
		}

		this.biomeContainer = palettedContainer;
	}
}
