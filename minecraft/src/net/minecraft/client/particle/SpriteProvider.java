package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public interface SpriteProvider {
	Sprite getSprite(int age, int maxAge);

	Sprite getSprite(Random random);
}
