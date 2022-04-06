package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.AbstractRandom;

@Environment(EnvType.CLIENT)
public interface SpriteProvider {
	Sprite getSprite(int i, int j);

	Sprite getSprite(AbstractRandom random);
}
