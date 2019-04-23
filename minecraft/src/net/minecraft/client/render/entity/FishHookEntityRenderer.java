package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class FishHookEntityRenderer extends EntityRenderer<FishHookEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/fishing_hook.png");

	public FishHookEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_3974(FishHookEntity fishHookEntity, double d, double e, double f, float g, float h) {
		PlayerEntity playerEntity = fishHookEntity.getOwner();
		if (playerEntity != null && !this.field_4674) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d, (float)e, (float)f);
			GlStateManager.enableRescaleNormal();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			this.bindEntityTexture(fishHookEntity);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			float i = 1.0F;
			float j = 0.5F;
			float k = 0.5F;
			GlStateManager.rotatef(180.0F - this.renderManager.cameraYaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch, 1.0F, 0.0F, 0.0F);
			if (this.field_4674) {
				GlStateManager.enableColorMaterial();
				GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(fishHookEntity));
			}

			bufferBuilder.begin(7, VertexFormats.POSITION_UV_NORMAL);
			bufferBuilder.vertex(-0.5, -0.5, 0.0).texture(0.0, 1.0).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(0.5, -0.5, 0.0).texture(1.0, 1.0).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(0.5, 0.5, 0.0).texture(1.0, 0.0).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(-0.5, 0.5, 0.0).texture(0.0, 0.0).normal(0.0F, 1.0F, 0.0F).next();
			tessellator.draw();
			if (this.field_4674) {
				GlStateManager.tearDownSolidRenderingTextureCombine();
				GlStateManager.disableColorMaterial();
			}

			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			int l = playerEntity.getMainHand() == AbsoluteHand.field_6183 ? 1 : -1;
			ItemStack itemStack = playerEntity.getMainHandStack();
			if (itemStack.getItem() != Items.field_8378) {
				l = -l;
			}

			float m = playerEntity.getHandSwingProgress(h);
			float n = MathHelper.sin(MathHelper.sqrt(m) * (float) Math.PI);
			float o = MathHelper.lerp(h, playerEntity.field_6220, playerEntity.field_6283) * (float) (Math.PI / 180.0);
			double p = (double)MathHelper.sin(o);
			double q = (double)MathHelper.cos(o);
			double r = (double)l * 0.35;
			double s = 0.8;
			double t;
			double u;
			double v;
			double w;
			if ((this.renderManager.gameOptions == null || this.renderManager.gameOptions.perspective <= 0) && playerEntity == MinecraftClient.getInstance().player) {
				double x = this.renderManager.gameOptions.fov;
				x /= 100.0;
				Vec3d vec3d = new Vec3d((double)l * -0.36 * x, -0.045 * x, 0.4);
				vec3d = vec3d.rotateX(-MathHelper.lerp(h, playerEntity.prevPitch, playerEntity.pitch) * (float) (Math.PI / 180.0));
				vec3d = vec3d.rotateY(-MathHelper.lerp(h, playerEntity.prevYaw, playerEntity.yaw) * (float) (Math.PI / 180.0));
				vec3d = vec3d.rotateY(n * 0.5F);
				vec3d = vec3d.rotateX(-n * 0.7F);
				t = MathHelper.lerp((double)h, playerEntity.prevX, playerEntity.x) + vec3d.x;
				u = MathHelper.lerp((double)h, playerEntity.prevY, playerEntity.y) + vec3d.y;
				v = MathHelper.lerp((double)h, playerEntity.prevZ, playerEntity.z) + vec3d.z;
				w = (double)playerEntity.getStandingEyeHeight();
			} else {
				t = MathHelper.lerp((double)h, playerEntity.prevX, playerEntity.x) - q * r - p * 0.8;
				u = playerEntity.prevY + (double)playerEntity.getStandingEyeHeight() + (playerEntity.y - playerEntity.prevY) * (double)h - 0.45;
				v = MathHelper.lerp((double)h, playerEntity.prevZ, playerEntity.z) - p * r + q * 0.8;
				w = playerEntity.isInSneakingPose() ? -0.1875 : 0.0;
			}

			double x = MathHelper.lerp((double)h, fishHookEntity.prevX, fishHookEntity.x);
			double y = MathHelper.lerp((double)h, fishHookEntity.prevY, fishHookEntity.y) + 0.25;
			double z = MathHelper.lerp((double)h, fishHookEntity.prevZ, fishHookEntity.z);
			double aa = (double)((float)(t - x));
			double ab = (double)((float)(u - y)) + w;
			double ac = (double)((float)(v - z));
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
			int ad = 16;

			for (int ae = 0; ae <= 16; ae++) {
				float af = (float)ae / 16.0F;
				bufferBuilder.vertex(d + aa * (double)af, e + ab * (double)(af * af + af) * 0.5 + 0.25, f + ac * (double)af).color(0, 0, 0, 255).next();
			}

			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
			super.render(fishHookEntity, d, e, f, g, h);
		}
	}

	protected Identifier method_3975(FishHookEntity fishHookEntity) {
		return SKIN;
	}
}
