package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
		this(world, entity, entity2, f, entity.getVelocity());
	}

	private ItemPickupParticle(World world, Entity entity, Entity entity2, float f, Vec3d vec3d) {
		super(world, entity.x, entity.y, entity.z, vec3d.x, vec3d.y, vec3d.z);
		this.field_3823 = entity;
		this.field_3821 = entity2;
		this.field_3825 = 3;
		this.field_3822 = f;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Camera camera, float tickDelta, float f, float g, float h, float i, float j) {
		float k = ((float)this.field_3826 + tickDelta) / (float)this.field_3825;
		k *= k;
		double d = this.field_3823.x;
		double e = this.field_3823.y;
		double l = this.field_3823.z;
		double m = MathHelper.lerp((double)tickDelta, this.field_3821.lastRenderX, this.field_3821.x);
		double n = MathHelper.lerp((double)tickDelta, this.field_3821.lastRenderY, this.field_3821.y) + (double)this.field_3822;
		double o = MathHelper.lerp((double)tickDelta, this.field_3821.lastRenderZ, this.field_3821.z);
		double p = MathHelper.lerp((double)k, d, m);
		double q = MathHelper.lerp((double)k, e, n);
		double r = MathHelper.lerp((double)k, l, o);
		int s = this.getColorMultiplier(tickDelta);
		int t = s % 65536;
		int u = s / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)t, (float)u);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		p -= cameraX;
		q -= cameraY;
		r -= cameraZ;
		GlStateManager.enableLighting();
		this.field_3824.render(this.field_3823, p, q, r, this.field_3823.yaw, tickDelta, false);
	}

	@Override
	public void tick() {
		this.field_3826++;
		if (this.field_3826 == this.field_3825) {
			this.markDead();
		}
	}
}
