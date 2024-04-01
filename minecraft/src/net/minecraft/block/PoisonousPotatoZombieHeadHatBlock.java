package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;

public class PoisonousPotatoZombieHeadHatBlock extends PoisonousPotatoZombieHeadBlock implements Equipment {
	public static final MapCodec<PoisonousPotatoZombieHeadHatBlock> CODEC = createCodec(PoisonousPotatoZombieHeadHatBlock::new);

	@Override
	public MapCodec<PoisonousPotatoZombieHeadHatBlock> getCodec() {
		return CODEC;
	}

	protected PoisonousPotatoZombieHeadHatBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public EquipmentSlot getSlotType() {
		return EquipmentSlot.HEAD;
	}
}
