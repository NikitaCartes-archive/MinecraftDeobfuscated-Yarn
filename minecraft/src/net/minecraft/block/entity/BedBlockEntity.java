package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.util.DyeColor;

public class BedBlockEntity extends BlockEntity {
	private DyeColor field_11869;

	public BedBlockEntity() {
		super(BlockEntityType.BED);
	}

	public BedBlockEntity(DyeColor dyeColor) {
		this();
		this.method_11019(dyeColor);
	}

	@Override
	public BlockEntityUpdateClientPacket toUpdatePacket() {
		return new BlockEntityUpdateClientPacket(this.pos, 11, this.toInitialChunkDataTag());
	}

	@Environment(EnvType.CLIENT)
	public DyeColor method_11018() {
		if (this.field_11869 == null) {
			this.field_11869 = ((BedBlock)this.getCachedState().getBlock()).getColor();
		}

		return this.field_11869;
	}

	public void method_11019(DyeColor dyeColor) {
		this.field_11869 = dyeColor;
	}
}
