package net.minecraft.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.EntityTextureOffset;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public abstract class Model {
	public float swingProgress;
	public boolean isRiding;
	public boolean isChild = true;
	public List<Cuboid> cuboids = Lists.<Cuboid>newArrayList();
	private final Map<String, EntityTextureOffset> textureMap = Maps.<String, EntityTextureOffset>newHashMap();
	public int textureWidth = 64;
	public int textureHeight = 32;

	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
	}

	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
	}

	public void animateModel(LivingEntity livingEntity, float f, float g, float h) {
	}

	public Cuboid getRandomBox(Random random) {
		return (Cuboid)this.cuboids.get(random.nextInt(this.cuboids.size()));
	}

	protected void setTextureOffset(String string, int i, int j) {
		this.textureMap.put(string, new EntityTextureOffset(i, j));
	}

	public EntityTextureOffset getTextureOffset(String string) {
		return (EntityTextureOffset)this.textureMap.get(string);
	}

	public static void copyAngles(Cuboid cuboid, Cuboid cuboid2) {
		cuboid2.pitch = cuboid.pitch;
		cuboid2.yaw = cuboid.yaw;
		cuboid2.roll = cuboid.roll;
		cuboid2.rotationPointX = cuboid.rotationPointX;
		cuboid2.rotationPointY = cuboid.rotationPointY;
		cuboid2.rotationPointZ = cuboid.rotationPointZ;
	}

	public void setAttributes(Model model) {
		this.swingProgress = model.swingProgress;
		this.isRiding = model.isRiding;
		this.isChild = model.isChild;
	}
}
