package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.TagHelper;

public class ChunkSection {
	private static final Palette<BlockState> field_12879 = new IdListPalette<>(Block.STATE_IDS, Blocks.field_10124.method_9564());
	private final int yOffset;
	private short nonEmptyBlockCount;
	private short randomTickableBlockCount;
	private short nonEmptyFluidCount;
	private final PalettedContainer<BlockState> field_12878;

	public ChunkSection(int i) {
		this(i, (short)0, (short)0, (short)0);
	}

	public ChunkSection(int i, short s, short t, short u) {
		this.yOffset = i;
		this.nonEmptyBlockCount = s;
		this.randomTickableBlockCount = t;
		this.nonEmptyFluidCount = u;
		this.field_12878 = new PalettedContainer<>(
			field_12879, Block.STATE_IDS, TagHelper::deserializeBlockState, TagHelper::serializeBlockState, Blocks.field_10124.method_9564()
		);
	}

	public BlockState getBlockState(int i, int j, int k) {
		return this.field_12878.get(i, j, k);
	}

	public FluidState method_12255(int i, int j, int k) {
		return this.field_12878.get(i, j, k).method_11618();
	}

	public void lock() {
		this.field_12878.lock();
	}

	public void unlock() {
		this.field_12878.unlock();
	}

	public BlockState setBlockState(int i, int j, int k, BlockState blockState) {
		return this.setBlockState(i, j, k, blockState, true);
	}

	public BlockState setBlockState(int i, int j, int k, BlockState blockState, boolean bl) {
		BlockState blockState2;
		if (bl) {
			blockState2 = this.field_12878.setSync(i, j, k, blockState);
		} else {
			blockState2 = this.field_12878.set(i, j, k, blockState);
		}

		FluidState fluidState = blockState2.method_11618();
		FluidState fluidState2 = blockState.method_11618();
		if (!blockState2.isAir()) {
			this.nonEmptyBlockCount--;
			if (blockState2.hasRandomTicks()) {
				this.randomTickableBlockCount--;
			}
		}

		if (!fluidState.isEmpty()) {
			this.nonEmptyFluidCount--;
		}

		if (!blockState.isAir()) {
			this.nonEmptyBlockCount++;
			if (blockState.hasRandomTicks()) {
				this.randomTickableBlockCount++;
			}
		}

		if (!fluidState2.isEmpty()) {
			this.nonEmptyFluidCount++;
		}

		return blockState2;
	}

	public boolean isEmpty() {
		return this.nonEmptyBlockCount == 0;
	}

	public static boolean isEmpty(@Nullable ChunkSection chunkSection) {
		return chunkSection == WorldChunk.field_12852 || chunkSection.isEmpty();
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

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					BlockState blockState = this.getBlockState(i, j, k);
					FluidState fluidState = this.method_12255(i, j, k);
					if (!blockState.isAir()) {
						this.nonEmptyBlockCount++;
						if (blockState.hasRandomTicks()) {
							this.randomTickableBlockCount++;
						}
					}

					if (!fluidState.isEmpty()) {
						this.nonEmptyBlockCount++;
						if (fluidState.hasRandomTicks()) {
							this.nonEmptyFluidCount++;
						}
					}
				}
			}
		}
	}

	public PalettedContainer<BlockState> method_12265() {
		return this.field_12878;
	}

	@Environment(EnvType.CLIENT)
	public void fromPacket(PacketByteBuf packetByteBuf) {
		this.nonEmptyBlockCount = packetByteBuf.readShort();
		this.field_12878.fromPacket(packetByteBuf);
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeShort(this.nonEmptyBlockCount);
		this.field_12878.toPacket(packetByteBuf);
	}

	public int getPacketSize() {
		return 2 + this.field_12878.getPacketSize();
	}

	public boolean method_19523(BlockState blockState) {
		return this.field_12878.method_19526(blockState);
	}
}
