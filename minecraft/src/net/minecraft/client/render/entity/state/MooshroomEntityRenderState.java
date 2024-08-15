package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.MooshroomEntity;

@Environment(EnvType.CLIENT)
public class MooshroomEntityRenderState extends LivingEntityRenderState {
	public MooshroomEntity.Type type = MooshroomEntity.Type.RED;
}
