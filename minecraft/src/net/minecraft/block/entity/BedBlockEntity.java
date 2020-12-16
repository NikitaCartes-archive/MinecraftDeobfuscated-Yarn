package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class BedBlockEntity extends BlockEntity {
	private DyeColor color;

	public BedBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.BED, pos, state);
		this.color = ((BedBlock)state.getBlock()).getColor();
	}

	public BedBlockEntity(BlockPos pos, BlockState state, DyeColor color) {
		super(BlockEntityType.BED, pos, state);
		this.color = color;
	}

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 11, this.toInitialChunkDataTag());
	}

	@Environment(EnvType.CLIENT)
	public DyeColor getColor() {
		return this.color;
	}

	@Environment(EnvType.CLIENT)
	public void setColor(DyeColor color) {
		this.color = color;
	}
}
