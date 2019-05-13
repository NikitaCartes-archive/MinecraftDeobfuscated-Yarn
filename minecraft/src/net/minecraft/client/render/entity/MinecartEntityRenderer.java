package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class MinecartEntityRenderer<T extends AbstractMinecartEntity> extends EntityRenderer<T> {
	private static final Identifier SKIN = new Identifier("textures/entity/minecart.png");
	protected final EntityModel<T> model = new MinecartEntityModel<>();

	public MinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.7F;
	}

	public void method_4063(T abstractMinecartEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.bindEntityTexture(abstractMinecartEntity);
		long l = (long)abstractMinecartEntity.getEntityId() * 493286711L;
		l = l * l * 4392167121L + l * 98761L;
		float i = (((float)(l >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float j = (((float)(l >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float k = (((float)(l >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		GlStateManager.translatef(i, j, k);
		double m = MathHelper.lerp((double)h, abstractMinecartEntity.prevRenderX, abstractMinecartEntity.x);
		double n = MathHelper.lerp((double)h, abstractMinecartEntity.prevRenderY, abstractMinecartEntity.y);
		double o = MathHelper.lerp((double)h, abstractMinecartEntity.prevRenderZ, abstractMinecartEntity.z);
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

			d += vec3d.x - m;
			e += (vec3d2.y + vec3d3.y) / 2.0 - n;
			f += vec3d.z - o;
			Vec3d vec3d4 = vec3d3.add(-vec3d2.x, -vec3d2.y, -vec3d2.z);
			if (vec3d4.length() != 0.0) {
				vec3d4 = vec3d4.normalize();
				g = (float)(Math.atan2(vec3d4.z, vec3d4.x) * 180.0 / Math.PI);
				q = (float)(Math.atan(vec3d4.y) * 73.0);
			}
		}

		GlStateManager.translatef((float)d, (float)e + 0.375F, (float)f);
		GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-q, 0.0F, 0.0F, 1.0F);
		float r = (float)abstractMinecartEntity.getDamageWobbleTicks() - h;
		float s = abstractMinecartEntity.getDamageWobbleStrength() - h;
		if (s < 0.0F) {
			s = 0.0F;
		}

		if (r > 0.0F) {
			GlStateManager.rotatef(MathHelper.sin(r) * r * s / 10.0F * (float)abstractMinecartEntity.getDamageWobbleSide(), 1.0F, 0.0F, 0.0F);
		}

		int t = abstractMinecartEntity.getBlockOffset();
		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(abstractMinecartEntity));
		}

		BlockState blockState = abstractMinecartEntity.getContainedBlock();
		if (blockState.getRenderType() != BlockRenderType.field_11455) {
			GlStateManager.pushMatrix();
			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			float u = 0.75F;
			GlStateManager.scalef(0.75F, 0.75F, 0.75F);
			GlStateManager.translatef(-0.5F, (float)(t - 8) / 16.0F, 0.5F);
			this.renderBlock(abstractMinecartEntity, h, blockState);
			GlStateManager.popMatrix();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindEntityTexture(abstractMinecartEntity);
		}

		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
		this.model.render(abstractMinecartEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
		if (this.renderOutlines) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		super.render(abstractMinecartEntity, d, e, f, g, h);
	}

	protected Identifier method_4065(T abstractMinecartEntity) {
		return SKIN;
	}

	protected void renderBlock(T abstractMinecartEntity, float f, BlockState blockState) {
		GlStateManager.pushMatrix();
		MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState, abstractMinecartEntity.getBrightnessAtEyes());
		GlStateManager.popMatrix();
	}
}
