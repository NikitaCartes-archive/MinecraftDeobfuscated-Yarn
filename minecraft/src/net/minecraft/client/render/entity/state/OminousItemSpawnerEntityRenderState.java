package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class OminousItemSpawnerEntityRenderState extends EntityRenderState {
	@Nullable
	public BakedModel model;
	public ItemStack stack = ItemStack.EMPTY;
}
