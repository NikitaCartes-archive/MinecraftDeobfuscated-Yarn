package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
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
	public BlockEntityUpdateClientPacket toUpdatePacket() {
		return new BlockEntityUpdateClientPacket(this.pos, 11, this.toInitialChunkDataTag());
	}

	@Environment(EnvType.CLIENT)
	public DyeColor getColor() {
		if (this.color == null) {
			this.color = ((BedBlock)this.getCachedState().getBlock()).getColor();
		}

		return this.color;
	}

	public void setColor(DyeColor dyeColor) {
		this.color = dyeColor;
	}
}
