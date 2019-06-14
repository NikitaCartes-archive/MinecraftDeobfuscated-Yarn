package net.minecraft.client.particle;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;

@Environment(EnvType.CLIENT)
public interface SpriteProvider {
	Sprite method_18138(int i, int j);

	Sprite method_18139(Random random);
}
