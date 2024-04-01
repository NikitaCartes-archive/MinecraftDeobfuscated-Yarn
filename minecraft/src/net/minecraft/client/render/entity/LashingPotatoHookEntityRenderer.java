package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LashingPotatoHookEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class LashingPotatoHookEntityRenderer extends EntityRenderer<LashingPotatoHookEntity> {
	private final ItemRenderer itemRenderer;

	public LashingPotatoHookEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
	}

	public void render(
		LashingPotatoHookEntity lashingPotatoHookEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		PlayerEntity playerEntity = lashingPotatoHookEntity.getPlayer();
		if (playerEntity != null) {
			matrixStack.push();
			this.itemRenderer
				.renderItem(
					new ItemStack(Items.POISONOUS_POTATO),
					ModelTransformationMode.GROUND,
					i,
					OverlayTexture.DEFAULT_UV,
					matrixStack,
					vertexConsumerProvider,
					lashingPotatoHookEntity.getWorld(),
					lashingPotatoHookEntity.getId()
				);
			Vec3d vec3d = FishingBobberEntityRenderer.getPlayerHandPos(playerEntity, g, Items.LASHING_POTATO, this.dispatcher);
			Vec3d vec3d2 = new Vec3d(
				MathHelper.lerp((double)g, lashingPotatoHookEntity.prevX, lashingPotatoHookEntity.getX()),
				MathHelper.lerp((double)g, lashingPotatoHookEntity.prevY, lashingPotatoHookEntity.getY()) + (double)lashingPotatoHookEntity.getStandingEyeHeight(),
				MathHelper.lerp((double)g, lashingPotatoHookEntity.prevZ, lashingPotatoHookEntity.getZ())
			);
			float h = (float)lashingPotatoHookEntity.age + g;
			float j = h * 0.15F % 1.0F;
			Vec3d vec3d3 = vec3d.subtract(vec3d2);
			float k = (float)(vec3d3.length() + 0.1);
			vec3d3 = vec3d3.normalize();
			float l = (float)Math.acos(vec3d3.y);
			float m = (float)Math.atan2(vec3d3.z, vec3d3.x);
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((float) (Math.PI / 2) - m) * (180.0F / (float)Math.PI)));
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(l * (180.0F / (float)Math.PI)));
			float n = h * 0.05F * -1.5F;
			float o = 0.2F;
			float p = MathHelper.cos(n + (float) Math.PI) * 0.2F;
			float q = MathHelper.sin(n + (float) Math.PI) * 0.2F;
			float r = MathHelper.cos(n + 0.0F) * 0.2F;
			float s = MathHelper.sin(n + 0.0F) * 0.2F;
			float t = MathHelper.cos(n + (float) (Math.PI / 2)) * 0.2F;
			float u = MathHelper.sin(n + (float) (Math.PI / 2)) * 0.2F;
			float v = MathHelper.cos(n + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
			float w = MathHelper.sin(n + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
			float y = 0.0F;
			float z = 0.4999F;
			float aa = -1.0F + j;
			float ab = k * 2.5F + aa;
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(GuardianEntityRenderer.TOXIFIN_BEAM_TEXTURE));
			MatrixStack.Entry entry = matrixStack.peek();
			vertex(vertexConsumer, entry, p, k, q, 0.4999F, ab);
			vertex(vertexConsumer, entry, p, 0.0F, q, 0.4999F, aa);
			vertex(vertexConsumer, entry, r, 0.0F, s, 0.0F, aa);
			vertex(vertexConsumer, entry, r, k, s, 0.0F, ab);
			vertex(vertexConsumer, entry, t, k, u, 0.4999F, ab);
			vertex(vertexConsumer, entry, t, 0.0F, u, 0.4999F, aa);
			vertex(vertexConsumer, entry, v, 0.0F, w, 0.0F, aa);
			vertex(vertexConsumer, entry, v, k, w, 0.0F, ab);
			matrixStack.pop();
			super.render(lashingPotatoHookEntity, f, g, matrixStack, vertexConsumerProvider, i);
		}
	}

	private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, float z, float u, float v) {
		vertexConsumer.vertex(matrix, x, y, z)
			.color(128, 255, 128, 255)
			.texture(u, v)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
			.normal(0.0F, 1.0F, 0.0F)
			.next();
	}

	public Identifier getTexture(LashingPotatoHookEntity lashingPotatoHookEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}
