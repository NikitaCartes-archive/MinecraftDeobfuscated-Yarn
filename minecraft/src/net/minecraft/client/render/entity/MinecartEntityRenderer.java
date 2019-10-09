package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class MinecartEntityRenderer<T extends AbstractMinecartEntity> extends EntityRenderer<T> {
	private static final Identifier SKIN = new Identifier("textures/entity/minecart.png");
	protected final EntityModel<T> model = new MinecartEntityModel<>();

	public MinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.7F;
	}

	public void method_4063(
		T abstractMinecartEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		super.render(abstractMinecartEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
		matrixStack.push();
		long l = (long)abstractMinecartEntity.getEntityId() * 493286711L;
		l = l * l * 4392167121L + l * 98761L;
		float i = (((float)(l >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float j = (((float)(l >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float k = (((float)(l >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		matrixStack.translate((double)i, (double)j, (double)k);
		double m = MathHelper.lerp((double)h, abstractMinecartEntity.prevRenderX, abstractMinecartEntity.getX());
		double n = MathHelper.lerp((double)h, abstractMinecartEntity.prevRenderY, abstractMinecartEntity.getY());
		double o = MathHelper.lerp((double)h, abstractMinecartEntity.prevRenderZ, abstractMinecartEntity.getZ());
		double p = 0.3F;
		Vec3d vec3d = abstractMinecartEntity.method_7508(m, n, o);
		float q = MathHelper.lerp(h, abstractMinecartEntity.prevPitch, abstractMinecartEntity.pitch);
		if (vec3d != null) {
			Vec3d vec3d2 = abstractMinecartEntity.method_7505(m, n, o, 0.3F);
			Vec3d vec3d3 = abstractMinecartEntity.method_7505(m, n, o, -0.3F);
			if (vec3d2 == null) {
				vec3d2 = vec3d;
			}

			if (vec3d3 == null) {
				vec3d3 = vec3d;
			}

			matrixStack.translate(vec3d.x - m, (vec3d2.y + vec3d3.y) / 2.0 - n, vec3d.z - o);
			Vec3d vec3d4 = vec3d3.add(-vec3d2.x, -vec3d2.y, -vec3d2.z);
			if (vec3d4.length() != 0.0) {
				vec3d4 = vec3d4.normalize();
				g = (float)(Math.atan2(vec3d4.z, vec3d4.x) * 180.0 / Math.PI);
				q = (float)(Math.atan(vec3d4.y) * 73.0);
			}
		}

		matrixStack.translate(0.0, 0.375, 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F - g));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(-q));
		float r = (float)abstractMinecartEntity.getDamageWobbleTicks() - h;
		float s = abstractMinecartEntity.getDamageWobbleStrength() - h;
		if (s < 0.0F) {
			s = 0.0F;
		}

		if (r > 0.0F) {
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(MathHelper.sin(r) * r * s / 10.0F * (float)abstractMinecartEntity.getDamageWobbleSide()));
		}

		int t = abstractMinecartEntity.getBlockOffset();
		int u = abstractMinecartEntity.getLightmapCoordinates();
		BlockState blockState = abstractMinecartEntity.getContainedBlock();
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			matrixStack.push();
			float v = 0.75F;
			matrixStack.scale(0.75F, 0.75F, 0.75F);
			matrixStack.translate(-0.5, (double)((float)(t - 8) / 16.0F), 0.5);
			this.renderBlock(abstractMinecartEntity, h, blockState, matrixStack, layeredVertexConsumerStorage, u);
			matrixStack.pop();
		}

		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		this.model.setAngles(abstractMinecartEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(this.model.method_23500(this.method_4065(abstractMinecartEntity)));
		this.model.renderItem(matrixStack, vertexConsumer, u, OverlayTexture.field_21444, 1.0F, 1.0F, 1.0F);
		matrixStack.pop();
	}

	public Identifier method_4065(T abstractMinecartEntity) {
		return SKIN;
	}

	protected void renderBlock(
		T abstractMinecartEntity, float f, BlockState blockState, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i
	) {
		MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState, matrixStack, layeredVertexConsumerStorage, i, OverlayTexture.field_21444);
	}
}
