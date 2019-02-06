package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3999;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ItemPickupParticle extends Particle {
	private final Entity field_3823;
	private final Entity field_3821;
	private int field_3826;
	private final int field_3825;
	private final float field_3822;
	private final EntityRenderDispatcher field_3824 = MinecraftClient.getInstance().getEntityRenderManager();

	public ItemPickupParticle(World world, Entity entity, Entity entity2, float f) {
		super(world, entity.x, entity.y, entity.z, entity.velocityX, entity.velocityY, entity.velocityZ);
		this.field_3823 = entity;
		this.field_3821 = entity2;
		this.field_3825 = 3;
		this.field_3822 = f;
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17831;
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.field_3826 + f) / (float)this.field_3825;
		l *= l;
		double d = this.field_3823.x;
		double e = this.field_3823.y;
		double m = this.field_3823.z;
		double n = MathHelper.lerp((double)f, this.field_3821.prevRenderX, this.field_3821.x);
		double o = MathHelper.lerp((double)f, this.field_3821.prevRenderY, this.field_3821.y) + (double)this.field_3822;
		double p = MathHelper.lerp((double)f, this.field_3821.prevRenderZ, this.field_3821.z);
		double q = MathHelper.lerp((double)l, d, n);
		double r = MathHelper.lerp((double)l, e, o);
		double s = MathHelper.lerp((double)l, m, p);
		int t = this.getColorMultiplier(f);
		int u = t % 65536;
		int v = t / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)u, (float)v);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		q -= cameraX;
		r -= cameraY;
		s -= cameraZ;
		GlStateManager.enableLighting();
		this.field_3824.render(this.field_3823, q, r, s, this.field_3823.yaw, f, false);
	}

	@Override
	public void update() {
		this.field_3826++;
		if (this.field_3826 == this.field_3825) {
			this.markDead();
		}
	}
}
