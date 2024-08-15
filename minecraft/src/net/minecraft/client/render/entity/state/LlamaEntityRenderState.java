package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.DyeColor;

@Environment(EnvType.CLIENT)
public class LlamaEntityRenderState extends LivingEntityRenderState {
	public LlamaEntity.Variant variant = LlamaEntity.Variant.CREAMY;
	public boolean hasChest;
	@Nullable
	public DyeColor carpetColor;
	public boolean trader;
}
