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
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class FishHookEntityRenderer extends EntityRenderer<FishHookEntity> {
	private static final Identifier TEX = new Identifier("textures/particle/particles.png");

	public FishHookEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_3974(FishHookEntity fishHookEntity, double d, double e, double f, float g, float h) {
		PlayerEntity playerEntity = fishHookEntity.getOwner();
		if (playerEntity != null && !this.renderOutlines) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d, (float)e, (float)f);
			GlStateManager.enableRescaleNormal();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			this.bindEntityTexture(fishHookEntity);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			int i = 1;
			int j = 2;
			float k = 0.03125F;
			float l = 0.0625F;
			float m = 0.0625F;
			float n = 0.09375F;
			float o = 1.0F;
			float p = 0.5F;
			float q = 0.5F;
			GlStateManager.rotatef(180.0F - this.renderManager.field_4679, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef((float)(this.renderManager.settings.field_1850 == 2 ? -1 : 1) * -this.renderManager.field_4677, 1.0F, 0.0F, 0.0F);
			if (this.renderOutlines) {
				GlStateManager.enableColorMaterial();
				GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(fishHookEntity));
			}

			bufferBuilder.begin(7, VertexFormats.POSITION_UV_NORMAL);
			bufferBuilder.vertex(-0.5, -0.5, 0.0).texture(0.03125, 0.09375).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(0.5, -0.5, 0.0).texture(0.0625, 0.09375).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(0.5, 0.5, 0.0).texture(0.0625, 0.0625).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(-0.5, 0.5, 0.0).texture(0.03125, 0.0625).normal(0.0F, 1.0F, 0.0F).next();
			tessellator.draw();
			if (this.renderOutlines) {
				GlStateManager.tearDownSolidRenderingTextureCombine();
				GlStateManager.disableColorMaterial();
			}

			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			int r = playerEntity.getMainHand() == OptionMainHand.field_6183 ? 1 : -1;
			ItemStack itemStack = playerEntity.getMainHandStack();
			if (itemStack.getItem() != Items.field_8378) {
				r = -r;
			}

			float s = playerEntity.method_6055(h);
			float t = MathHelper.sin(MathHelper.sqrt(s) * (float) Math.PI);
			float u = MathHelper.lerp(h, playerEntity.field_6220, playerEntity.field_6283) * (float) (Math.PI / 180.0);
			double v = (double)MathHelper.sin(u);
			double w = (double)MathHelper.cos(u);
			double x = (double)r * 0.35;
			double y = 0.8;
			double z;
			double aa;
			double ab;
			double ac;
			if ((this.renderManager.settings == null || this.renderManager.settings.field_1850 <= 0) && playerEntity == MinecraftClient.getInstance().player) {
				double ad = this.renderManager.settings.fov;
				ad /= 100.0;
				Vec3d vec3d = new Vec3d((double)r * -0.36 * ad, -0.045 * ad, 0.4);
				vec3d = vec3d.rotateX(-MathHelper.lerp(h, playerEntity.prevPitch, playerEntity.pitch) * (float) (Math.PI / 180.0));
				vec3d = vec3d.rotateY(-MathHelper.lerp(h, playerEntity.prevYaw, playerEntity.yaw) * (float) (Math.PI / 180.0));
				vec3d = vec3d.rotateY(t * 0.5F);
				vec3d = vec3d.rotateX(-t * 0.7F);
				z = MathHelper.lerp((double)h, playerEntity.prevX, playerEntity.x) + vec3d.x;
				aa = MathHelper.lerp((double)h, playerEntity.prevY, playerEntity.y) + vec3d.y;
				ab = MathHelper.lerp((double)h, playerEntity.prevZ, playerEntity.z) + vec3d.z;
				ac = (double)playerEntity.getEyeHeight();
			} else {
				z = MathHelper.lerp((double)h, playerEntity.prevX, playerEntity.x) - w * x - v * 0.8;
				aa = playerEntity.prevY + (double)playerEntity.getEyeHeight() + (playerEntity.y - playerEntity.prevY) * (double)h - 0.45;
				ab = MathHelper.lerp((double)h, playerEntity.prevZ, playerEntity.z) - v * x + w * 0.8;
				ac = playerEntity.isSneaking() ? -0.1875 : 0.0;
			}

			double ad = MathHelper.lerp((double)h, fishHookEntity.prevX, fishHookEntity.x);
			double ae = MathHelper.lerp((double)h, fishHookEntity.prevY, fishHookEntity.y) + 0.25;
			double af = MathHelper.lerp((double)h, fishHookEntity.prevZ, fishHookEntity.z);
			double ag = (double)((float)(z - ad));
			double ah = (double)((float)(aa - ae)) + ac;
			double ai = (double)((float)(ab - af));
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
			int aj = 16;

			for (int ak = 0; ak <= 16; ak++) {
				float al = (float)ak / 16.0F;
				bufferBuilder.vertex(d + ag * (double)al, e + ah * (double)(al * al + al) * 0.5 + 0.25, f + ai * (double)al).color(0, 0, 0, 255).next();
			}

			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
			super.render(fishHookEntity, d, e, f, g, h);
		}
	}

	protected Identifier getTexture(FishHookEntity fishHookEntity) {
		return TEX;
	}
}
