package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EntityTextureOffset {
	public final int offsetX;
	public final int offsetY;

	public EntityTextureOffset(int i, int j) {
		this.offsetX = i;
		this.offsetY = j;
	}
}
