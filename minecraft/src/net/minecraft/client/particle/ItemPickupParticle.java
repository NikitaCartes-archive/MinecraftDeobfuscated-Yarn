package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LayeredBufferBuilderStorage;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ItemPickupParticle extends Particle {
	private final LayeredBufferBuilderStorage field_20944;
	private final Entity field_3823;
	private final Entity field_3821;
	private int field_3826;
	private final EntityRenderDispatcher field_3824;

	public ItemPickupParticle(
		EntityRenderDispatcher entityRenderDispatcher, LayeredBufferBuilderStorage layeredBufferBuilderStorage, World world, Entity entity, Entity entity2
	) {
		this(entityRenderDispatcher, layeredBufferBuilderStorage, world, entity, entity2, entity.getVelocity());
	}

	private ItemPickupParticle(
		EntityRenderDispatcher entityRenderDispatcher,
		LayeredBufferBuilderStorage layeredBufferBuilderStorage,
		World world,
		Entity entity,
		Entity entity2,
		Vec3d vec3d
	) {
		super(world, entity.getX(), entity.getY(), entity.getZ(), vec3d.x, vec3d.y, vec3d.z);
		this.field_20944 = layeredBufferBuilderStorage;
		this.field_3823 = entity;
		this.field_3821 = entity2;
		this.field_3824 = entityRenderDispatcher;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.field_3826 + f) / 3.0F;
		l *= l;
		double d = MathHelper.lerp((double)f, this.field_3821.prevRenderX, this.field_3821.getX());
		double e = MathHelper.lerp((double)f, this.field_3821.prevRenderY, this.field_3821.getY()) + 0.5;
		double m = MathHelper.lerp((double)f, this.field_3821.prevRenderZ, this.field_3821.getZ());
		double n = MathHelper.lerp((double)l, this.field_3823.getX(), d);
		double o = MathHelper.lerp((double)l, this.field_3823.getY(), e);
		double p = MathHelper.lerp((double)l, this.field_3823.getZ(), m);
		LayeredVertexConsumerStorage.class_4598 lv = this.field_20944.method_23000();
		this.field_3824.render(this.field_3823, n - cameraX, o - cameraY, p - cameraZ, this.field_3823.yaw, f, new MatrixStack(), lv);
		lv.method_22993();
	}

	@Override
	public void tick() {
		this.field_3826++;
		if (this.field_3826 == 3) {
			this.markDead();
		}
	}
}
