package net.minecraft.world.chunk;

import java.util.function.Predicate;
import net.minecraft.class_7522;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
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
	private class_7522<RegistryEntry<Biome>> biomeContainer;

	public ChunkSection(int chunkPos, PalettedContainer<BlockState> blockStateContainer, class_7522<RegistryEntry<Biome>> arg) {
		this.yOffset = blockCoordFromChunkCoord(chunkPos);
		this.blockStateContainer = blockStateContainer;
		this.biomeContainer = arg;
		this.calculateCounts();
	}

	public ChunkSection(int chunkPos, Registry<Biome> biomeRegistry) {
		this.yOffset = blockCoordFromChunkCoord(chunkPos);
		this.blockStateContainer = new PalettedContainer<>(Block.STATE_IDS, Blocks.AIR.getDefaultState(), PalettedContainer.PaletteProvider.BLOCK_STATE);
		this.biomeContainer = new PalettedContainer<>(
			biomeRegistry.getIndexedEntries(), biomeRegistry.entryOf(BiomeKeys.PLAINS), PalettedContainer.PaletteProvider.BIOME
		);
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
		class class_6869 implements PalettedContainer.Counter<BlockState> {
			public int field_36408;
			public int field_36409;
			public int field_36410;

			public void accept(BlockState blockState, int i) {
				FluidState fluidState = blockState.getFluidState();
				if (!blockState.isAir()) {
					this.field_36408 += i;
					if (blockState.hasRandomTicks()) {
						this.field_36409 += i;
					}
				}

				if (!fluidState.isEmpty()) {
					this.field_36408 += i;
					if (fluidState.hasRandomTicks()) {
						this.field_36410 += i;
					}
				}
			}
		}

		class_6869 lv = new class_6869();
		this.blockStateContainer.count(lv);
		this.nonEmptyBlockCount = (short)lv.field_36408;
		this.randomTickableBlockCount = (short)lv.field_36409;
		this.nonEmptyFluidCount = (short)lv.field_36410;
	}

	public PalettedContainer<BlockState> getBlockStateContainer() {
		return this.blockStateContainer;
	}

	public class_7522<RegistryEntry<Biome>> getBiomeContainer() {
		return this.biomeContainer;
	}

	public void fromPacket(PacketByteBuf buf) {
		this.nonEmptyBlockCount = buf.readShort();
		this.blockStateContainer.readPacket(buf);
		PalettedContainer<RegistryEntry<Biome>> palettedContainer = this.biomeContainer.method_44350();
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

	public void populateBiomes(BiomeSupplier biomeSupplier, MultiNoiseUtil.MultiNoiseSampler sampler, int x, int z) {
		PalettedContainer<RegistryEntry<Biome>> palettedContainer = this.biomeContainer.method_44350();
		int i = BiomeCoords.fromBlock(this.getYOffset());
		int j = 4;

		for (int k = 0; k < 4; k++) {
			for (int l = 0; l < 4; l++) {
				for (int m = 0; m < 4; m++) {
					palettedContainer.swapUnsafe(k, l, m, biomeSupplier.getBiome(x + k, i + l, z + m, sampler));
				}
			}
		}

		this.biomeContainer = palettedContainer;
	}
}
