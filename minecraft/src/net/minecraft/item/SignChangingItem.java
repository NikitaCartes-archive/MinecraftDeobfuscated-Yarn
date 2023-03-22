package net.minecraft.item;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface SignChangingItem {
	boolean useOnSign(World world, SignBlockEntity signBlockEntity, boolean front, PlayerEntity player);

	default boolean canUseOnSignText(SignText signText, PlayerEntity player) {
		return signText.hasText(player);
	}
}
