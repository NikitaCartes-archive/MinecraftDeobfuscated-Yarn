package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public class ThrownPotionEntityRenderer extends SnowballEntityRenderer<ThrownPotionEntity> {
	public ThrownPotionEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher, Items.field_8574, itemRenderer);
	}

	public ItemStack getItemStack(ThrownPotionEntity thrownPotionEntity) {
		return thrownPotionEntity.getItemStack();
	}
}
