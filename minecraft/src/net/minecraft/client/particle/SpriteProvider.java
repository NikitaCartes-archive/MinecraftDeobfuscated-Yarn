package net.minecraft.client.particle;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;

@Environment(EnvType.CLIENT)
public interface SpriteProvider {
	Sprite getSprite(int i, int j);

	Sprite getSprite(Random random);
}
