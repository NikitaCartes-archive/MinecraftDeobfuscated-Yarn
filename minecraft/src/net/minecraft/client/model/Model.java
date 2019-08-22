package net.minecraft.client.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Model {
	public final List<ModelPart> cuboidList = Lists.<ModelPart>newArrayList();
	public int textureWidth = 64;
	public int textureHeight = 32;

	public ModelPart getRandomCuboid(Random random) {
		return (ModelPart)this.cuboidList.get(random.nextInt(this.cuboidList.size()));
	}
}
