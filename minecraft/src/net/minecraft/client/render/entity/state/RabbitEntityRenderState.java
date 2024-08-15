package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.RabbitEntity;

@Environment(EnvType.CLIENT)
public class RabbitEntityRenderState extends LivingEntityRenderState {
	public float jumpProgress;
	public boolean isToast;
	public RabbitEntity.RabbitType type = RabbitEntity.RabbitType.BROWN;
}
