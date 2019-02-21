package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;

@Environment(EnvType.CLIENT)
public interface class_4002 {
	Sprite getSprite(int i, int j);

	Sprite getSprite(Random random);
}
