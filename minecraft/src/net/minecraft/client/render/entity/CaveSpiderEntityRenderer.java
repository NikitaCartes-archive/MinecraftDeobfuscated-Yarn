package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CaveSpiderEntityRenderer extends SpiderEntityRenderer<CaveSpiderEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/spider/cave_spider.png");

	public CaveSpiderEntityRenderer(class_5617.class_5618 arg) {
		super(arg, EntityModelLayers.CAVE_SPIDER);
		this.shadowRadius *= 0.7F;
	}

	protected void scale(CaveSpiderEntity caveSpiderEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(0.7F, 0.7F, 0.7F);
	}

	public Identifier getTexture(CaveSpiderEntity caveSpiderEntity) {
		return TEXTURE;
	}
}
