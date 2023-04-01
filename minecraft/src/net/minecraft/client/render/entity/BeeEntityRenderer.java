package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_8293;
import net.minecraft.class_8464;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LunarWorldView;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class BeeEntityRenderer extends MobEntityRenderer<BeeEntity, BeeEntityModel<BeeEntity>> {
	public static final class_8464.class_8465[] field_44407 = class_8464.method_51053(2);
	private static final Identifier ANGRY_TEXTURE = new Identifier("textures/entity/bee/bee_angry.png");
	private static final Identifier ANGRY_NECTAR_TEXTURE = new Identifier("textures/entity/bee/bee_angry_nectar.png");
	private static final Identifier PASSIVE_TEXTURE = new Identifier("textures/entity/bee/bee.png");
	private static final Identifier NECTAR_TEXTURE = new Identifier("textures/entity/bee/bee_nectar.png");

	public BeeEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new BeeEntityModel<>(context.getPart(EntityModelLayers.BEE)), 0.4F);
	}

	public static boolean method_51037(LunarWorldView lunarWorldView) {
		long l = lunarWorldView.getLunarTime() % 24000L;
		return l >= 13000L && l < 23000L;
	}

	public void render(BeeEntity beeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if (class_8293.field_43553.method_50116() && class_8293.field_43661.method_50116() && method_51037(beeEntity.world)) {
			int j = ColorHelper.Argb.getArgb(32, 0, 192, 255);
			float h = 1.5F + MathHelper.cos(this.getAnimationProgress(beeEntity, g) * 2.0F) / 32.0F;
			matrixStack.push();
			matrixStack.scale(h, h, h);
			Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
			class_8464.method_51054(field_44407, matrix4f, vertexConsumerProvider, j);
			matrixStack.pop();
		}

		super.render(beeEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(BeeEntity beeEntity) {
		if (beeEntity.hasAngerTime()) {
			return beeEntity.hasNectar() ? ANGRY_NECTAR_TEXTURE : ANGRY_TEXTURE;
		} else {
			return beeEntity.hasNectar() ? NECTAR_TEXTURE : PASSIVE_TEXTURE;
		}
	}
}
