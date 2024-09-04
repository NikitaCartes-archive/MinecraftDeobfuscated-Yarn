package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class LlamaEntityRenderState extends LivingEntityRenderState {
	public LlamaEntity.Variant variant = LlamaEntity.Variant.CREAMY;
	public boolean hasChest;
	public ItemStack bodyArmor = ItemStack.EMPTY;
	public boolean trader;
}
