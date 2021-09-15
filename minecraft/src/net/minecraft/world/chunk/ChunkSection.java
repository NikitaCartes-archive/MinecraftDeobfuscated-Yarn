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
	private final PalettedContainer<BlockState> container;
	private final PalettedContainer<Biome> field_34556;

	public ChunkSection(int i, PalettedContainer<BlockState> palettedContainer, PalettedContainer<Biome> palettedContainer2) {
		this.yOffset = blockCoordFromChunkCoord(i);
		this.container = palettedContainer;
		this.field_34556 = palettedContainer2;
		this.calculateCounts();
	}

	public ChunkSection(int i, Registry<Biome> registry) {
		this.yOffset = blockCoordFromChunkCoord(i);
		this.container = new PalettedContainer<>(Block.STATE_IDS, Blocks.AIR.getDefaultState(), PalettedContainer.class_6563.field_34569);
		this.field_34556 = new PalettedContainer<>(registry, registry.getOrThrow(BiomeKeys.PLAINS), PalettedContainer.class_6563.field_34570);
	}

	public static int blockCoordFromChunkCoord(int chunkPos) {
		return chunkPos << 4;
	}

	public BlockState getBlockState(int x, int y, int z) {
		return this.container.get(x, y, z);
	}

	public FluidState getFluidState(int x, int y, int z) {
		return this.container.get(x, y, z).getFluidState();
	}

	public void lock() {
		this.container.lock();
	}

	public void unlock() {
		this.container.unlock();
	}

	public BlockState setBlockState(int x, int y, int z, BlockState state) {
		return this.setBlockState(x, y, z, state, true);
	}

	public BlockState setBlockState(int x, int y, int z, BlockState state, boolean lock) {
		BlockState blockState;
		if (lock) {
			blockState = this.container.setSync(x, y, z, state);
		} else {
			blockState = this.container.set(x, y, z, state);
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
		this.container.count((state, count) -> {
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

	public PalettedContainer<BlockState> getContainer() {
		return this.container;
	}

	public PalettedContainer<Biome> method_38294() {
		return this.field_34556;
	}

	public void fromPacket(PacketByteBuf buf) {
		this.nonEmptyBlockCount = buf.readShort();
		this.container.fromPacket(buf);
		this.field_34556.fromPacket(buf);
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeShort(this.nonEmptyBlockCount);
		this.container.toPacket(buf);
		this.field_34556.toPacket(buf);
	}

	public int getPacketSize() {
		return 2 + this.container.getPacketSize() + this.field_34556.getPacketSize();
	}

	public boolean hasAny(Predicate<BlockState> predicate) {
		return this.container.hasAny(predicate);
	}

	public Biome method_38293(int i, int j, int k) {
		return this.field_34556.get(i, j, k);
	}

	public void method_38291(BiomeSource biomeSource, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler, int i, int j) {
		PalettedContainer<Biome> palettedContainer = this.method_38294();
		palettedContainer.lock();

		try {
			int k = BiomeCoords.fromBlock(this.getYOffset());
			int l = 4;

			for (int m = 0; m < 4; m++) {
				for (int n = 0; n < 4; n++) {
					for (int o = 0; o < 4; o++) {
						palettedContainer.set(m, n, o, biomeSource.method_38109(i + m, k + n, j + o, multiNoiseSampler));
					}
				}
			}
		} finally {
			palettedContainer.unlock();
		}
	}
}
