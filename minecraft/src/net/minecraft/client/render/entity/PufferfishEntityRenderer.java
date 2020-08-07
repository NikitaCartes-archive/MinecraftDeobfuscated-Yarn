package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.LargePufferfishEntityModel;
import net.minecraft.client.render.entity.model.MediumPufferfishEntityModel;
import net.minecraft.client.render.entity.model.SmallPufferfishEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PufferfishEntityRenderer extends MobEntityRenderer<PufferfishEntity, EntityModel<PufferfishEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/fish/pufferfish.png");
	private int modelSize;
	private final SmallPufferfishEntityModel<PufferfishEntity> smallModel = new SmallPufferfishEntityModel<>();
	private final MediumPufferfishEntityModel<PufferfishEntity> mediumModel = new MediumPufferfishEntityModel<>();
	private final LargePufferfishEntityModel<PufferfishEntity> largeModel = new LargePufferfishEntityModel<>();

	public PufferfishEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new LargePufferfishEntityModel<>(), 0.2F);
		this.modelSize = 3;
	}

	public Identifier method_4096(PufferfishEntity pufferfishEntity) {
		return TEXTURE;
	}

	public void method_4094(PufferfishEntity pufferfishEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		int j = pufferfishEntity.getPuffState();
		if (j != this.modelSize) {
			if (j == 0) {
				this.model = this.smallModel;
			} else if (j == 1) {
				this.model = this.mediumModel;
			} else {
				this.model = this.largeModel;
			}
		}

		this.modelSize = j;
		this.shadowRadius = 0.1F + 0.1F * (float)j;
		super.method_4072(pufferfishEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	protected void method_4095(PufferfishEntity pufferfishEntity, MatrixStack matrixStack, float f, float g, float h) {
		matrixStack.translate(0.0, (double)(MathHelper.cos(f * 0.05F) * 0.08F), 0.0);
		super.setupTransforms(pufferfishEntity, matrixStack, f, g, h);
	}
}
