package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.Cracks;

@Environment(EnvType.CLIENT)
public class IronGolemEntityRenderState extends LivingEntityRenderState {
	public float attackTicksLeft;
	public int lookingAtVillagerTicks;
	public Cracks.CrackLevel crackLevel = Cracks.CrackLevel.NONE;
}
