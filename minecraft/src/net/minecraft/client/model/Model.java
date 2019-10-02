package net.minecraft.client.model;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Model implements Consumer<ModelPart> {
	public int textureWidth = 64;
	public int textureHeight = 32;

	public void onPartAdded(ModelPart modelPart) {
	}
}
