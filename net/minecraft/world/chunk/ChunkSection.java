/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.TagHelper;
import net.minecraft.world.chunk.IdListPalette;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public class ChunkSection {
    private static final Palette<BlockState> palette = new IdListPalette<BlockState>(Block.STATE_IDS, Blocks.AIR.getDefaultState());
    private final int yOffset;
    private short nonEmptyBlockCount;
    private short randomTickableBlockCount;
    private short nonEmptyFluidCount;
    private final PalettedContainer<BlockState> container;

    public ChunkSection(int i) {
        this(i, 0, 0, 0);
    }

    public ChunkSection(int i, short s, short t, short u) {
        this.yOffset = i;
        this.nonEmptyBlockCount = s;
        this.randomTickableBlockCount = t;
        this.nonEmptyFluidCount = u;
        this.container = new PalettedContainer<BlockState>(palette, Block.STATE_IDS, TagHelper::deserializeBlockState, TagHelper::serializeBlockState, Blocks.AIR.getDefaultState());
    }

    public BlockState getBlockState(int i, int j, int k) {
        return this.container.get(i, j, k);
    }

    public FluidState getFluidState(int i, int j, int k) {
        return this.container.get(i, j, k).getFluidState();
    }

    public void lock() {
        this.container.lock();
    }

    public void unlock() {
        this.container.unlock();
    }

    public BlockState setBlockState(int i, int j, int k, BlockState blockState) {
        return this.setBlockState(i, j, k, blockState, true);
    }

    public BlockState setBlockState(int i, int j, int k, BlockState blockState, boolean bl) {
        BlockState blockState2 = bl ? this.container.setSync(i, j, k, blockState) : this.container.set(i, j, k, blockState);
        FluidState fluidState = blockState2.getFluidState();
        FluidState fluidState2 = blockState.getFluidState();
        if (!blockState2.isAir()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount - 1);
            if (blockState2.hasRandomTicks()) {
                this.randomTickableBlockCount = (short)(this.randomTickableBlockCount - 1);
            }
        }
        if (!fluidState.isEmpty()) {
            this.nonEmptyFluidCount = (short)(this.nonEmptyFluidCount - 1);
        }
        if (!blockState.isAir()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + 1);
            if (blockState.hasRandomTicks()) {
                this.randomTickableBlockCount = (short)(this.randomTickableBlockCount + 1);
            }
        }
        if (!fluidState2.isEmpty()) {
            this.nonEmptyFluidCount = (short)(this.nonEmptyFluidCount + 1);
        }
        return blockState2;
    }

    public boolean isEmpty() {
        return this.nonEmptyBlockCount == 0;
    }

    public static boolean isEmpty(@Nullable ChunkSection chunkSection) {
        return chunkSection == WorldChunk.EMPTY_SECTION || chunkSection.isEmpty();
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
        this.container.count((blockState, i) -> {
            FluidState fluidState = blockState.getFluidState();
            if (!blockState.isAir()) {
                this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + i);
                if (blockState.hasRandomTicks()) {
                    this.randomTickableBlockCount = (short)(this.randomTickableBlockCount + i);
                }
            }
            if (!fluidState.isEmpty()) {
                this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + i);
                if (fluidState.hasRandomTicks()) {
                    this.nonEmptyFluidCount = (short)(this.nonEmptyFluidCount + i);
                }
            }
        });
    }

    public PalettedContainer<BlockState> getContainer() {
        return this.container;
    }

    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf packetByteBuf) {
        this.nonEmptyBlockCount = packetByteBuf.readShort();
        this.container.fromPacket(packetByteBuf);
    }

    public void toPacket(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeShort(this.nonEmptyBlockCount);
        this.container.toPacket(packetByteBuf);
    }

    public int getPacketSize() {
        return 2 + this.container.getPacketSize();
    }

    public boolean method_19523(BlockState blockState) {
        return this.container.method_19526(blockState);
    }
}

