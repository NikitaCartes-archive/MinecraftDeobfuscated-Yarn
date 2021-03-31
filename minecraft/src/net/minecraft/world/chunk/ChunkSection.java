package net.minecraft.world.chunk;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;

public class ChunkSection {
	public static final int field_31406 = 16;
	public static final int field_31407 = 16;
	public static final int field_31408 = 4096;
	private static final Palette<BlockState> PALETTE = new IdListPalette<>(Block.STATE_IDS, Blocks.AIR.getDefaultState());
	private final int yOffset;
	private short nonEmptyBlockCount;
	private short randomTickableBlockCount;
	private short nonEmptyFluidCount;
	private final PalettedContainer<BlockState> container;

	public ChunkSection(int yOffset) {
		this(yOffset, (short)0, (short)0, (short)0);
	}

	public ChunkSection(int yOffset, short nonEmptyBlockCount, short randomTickableBlockCount, short nonEmptyFluidCount) {
		this.yOffset = blockCoordFromChunkCoord(yOffset);
		this.nonEmptyBlockCount = nonEmptyBlockCount;
		this.randomTickableBlockCount = randomTickableBlockCount;
		this.nonEmptyFluidCount = nonEmptyFluidCount;
		this.container = new PalettedContainer<>(PALETTE, Block.STATE_IDS, NbtHelper::toBlockState, NbtHelper::fromBlockState, Blocks.AIR.getDefaultState());
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

	public static boolean isEmpty(@Nullable ChunkSection section) {
		return section == WorldChunk.EMPTY_SECTION || section.isEmpty();
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

	public void fromPacket(PacketByteBuf buf) {
		this.nonEmptyBlockCount = buf.readShort();
		this.container.fromPacket(buf);
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeShort(this.nonEmptyBlockCount);
		this.container.toPacket(buf);
	}

	public int getPacketSize() {
		return 2 + this.container.getPacketSize();
	}

	public boolean hasAny(Predicate<BlockState> predicate) {
		return this.container.hasAny(predicate);
	}
}
