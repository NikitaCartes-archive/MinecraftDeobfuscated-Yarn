package net.minecraft.item;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class GlowInkSacItem extends Item implements SignChangingItem {
	public GlowInkSacItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean useOnSign(World world, SignBlockEntity signBlockEntity, boolean front, PlayerEntity player) {
		if (signBlockEntity.changeText(text -> text.withGlowing(true), front)) {
			world.playSound(null, signBlockEntity.getPos(), SoundEvents.ITEM_GLOW_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return true;
		} else {
			return false;
		}
	}
}
