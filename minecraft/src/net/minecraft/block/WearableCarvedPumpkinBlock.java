package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;

public class WearableCarvedPumpkinBlock extends CarvedPumpkinBlock implements Equipment {
	public static final MapCodec<WearableCarvedPumpkinBlock> CODEC = createCodec(WearableCarvedPumpkinBlock::new);

	@Override
	public MapCodec<WearableCarvedPumpkinBlock> getCodec() {
		return CODEC;
	}

	protected WearableCarvedPumpkinBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public EquipmentSlot getSlotType() {
		return EquipmentSlot.HEAD;
	}
}
