package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargePufferfishEntityModel;
import net.minecraft.client.render.entity.model.MediumPufferfishEntityModel;
import net.minecraft.client.render.entity.model.SmallPufferfishEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PufferfishEntityRenderer extends MobEntityRenderer<PufferfishEntity, EntityModel<PufferfishEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fish/pufferfish.png");
	private int modelSize = 3;
	private final EntityModel<PufferfishEntity> smallModel;
	private final EntityModel<PufferfishEntity> mediumModel;
	private final EntityModel<PufferfishEntity> largeModel = this.getModel();

	public PufferfishEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new LargePufferfishEntityModel<>(context.getPart(EntityModelLayers.PUFFERFISH_BIG)), 0.2F);
		this.mediumModel = new MediumPufferfishEntityModel<>(context.getPart(EntityModelLayers.PUFFERFISH_MEDIUM));
		this.smallModel = new SmallPufferfishEntityModel<>(context.getPart(EntityModelLayers.PUFFERFISH_SMALL));
	}

	public Identifier getTexture(PufferfishEntity pufferfishEntity) {
		return TEXTURE;
	}

	public void render(PufferfishEntity pufferfishEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
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
		super.render(pufferfishEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	protected void setupTransforms(PufferfishEntity pufferfishEntity, MatrixStack matrixStack, float f, float g, float h, float i) {
		matrixStack.translate(0.0F, MathHelper.cos(f * 0.05F) * 0.08F, 0.0F);
		super.setupTransforms(pufferfishEntity, matrixStack, f, g, h, i);
	}
}
