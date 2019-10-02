package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class CaveSpiderEntityRenderer extends SpiderEntityRenderer<CaveSpiderEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/spider/cave_spider.png");

	public CaveSpiderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 *= 0.7F;
	}

	protected void method_3886(CaveSpiderEntity caveSpiderEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(0.7F, 0.7F, 0.7F);
	}

	public Identifier method_3885(CaveSpiderEntity caveSpiderEntity) {
		return SKIN;
	}
}
