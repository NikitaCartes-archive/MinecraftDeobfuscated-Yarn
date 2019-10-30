package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;

public class BedBlockEntity extends BlockEntity {
	private DyeColor color;

	public BedBlockEntity() {
		super(BlockEntityType.BED);
	}

	public BedBlockEntity(DyeColor dyeColor) {
		this();
		this.setColor(dyeColor);
	}

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 11, this.toInitialChunkDataTag());
	}

	@Environment(EnvType.CLIENT)
	public DyeColor getColor() {
		if (this.color == null) {
			this.color = ((BedBlock)this.getCachedState().getBlock()).getColor();
		}

		return this.color;
	}

	public void setColor(DyeColor color) {
		this.color = color;
	}
}
