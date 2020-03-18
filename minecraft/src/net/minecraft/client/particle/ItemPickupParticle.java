package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ItemPickupParticle extends Particle {
	private final BufferBuilderStorage bufferBuilderStorage;
	private final Entity item;
	private final Entity picker;
	private int existingTicks;
	private final EntityRenderDispatcher entityRenderDispatcher;

	public ItemPickupParticle(EntityRenderDispatcher entityRenderDispatcher, BufferBuilderStorage bufferBuilderStorage, World world, Entity entity, Entity entity2) {
		this(entityRenderDispatcher, bufferBuilderStorage, world, entity, entity2, entity.getVelocity());
	}

	private ItemPickupParticle(
		EntityRenderDispatcher entityRenderDispatcher, BufferBuilderStorage bufferBuilderStorage, World world, Entity entity, Entity entity2, Vec3d vec3d
	) {
		super(world, entity.getX(), entity.getY(), entity.getZ(), vec3d.x, vec3d.y, vec3d.z);
		this.bufferBuilderStorage = bufferBuilderStorage;
		this.item = entity;
		this.picker = entity2;
		this.entityRenderDispatcher = entityRenderDispatcher;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		float f = ((float)this.existingTicks + tickDelta) / 3.0F;
		f *= f;
		double d = MathHelper.lerp((double)tickDelta, this.picker.lastRenderX, this.picker.getX());
		double e = MathHelper.lerp((double)tickDelta, this.picker.lastRenderY, this.picker.getY()) + 0.5;
		double g = MathHelper.lerp((double)tickDelta, this.picker.lastRenderZ, this.picker.getZ());
		double h = MathHelper.lerp((double)f, this.item.getX(), d);
		double i = MathHelper.lerp((double)f, this.item.getY(), e);
		double j = MathHelper.lerp((double)f, this.item.getZ(), g);
		VertexConsumerProvider.Immediate immediate = this.bufferBuilderStorage.getEntityVertexConsumers();
		Vec3d vec3d = camera.getPos();
		this.entityRenderDispatcher
			.render(
				this.item,
				h - vec3d.getX(),
				i - vec3d.getY(),
				j - vec3d.getZ(),
				this.item.yaw,
				tickDelta,
				new MatrixStack(),
				immediate,
				this.entityRenderDispatcher.getLight(this.item, tickDelta)
			);
		immediate.draw();
	}

	@Override
	public void tick() {
		this.existingTicks++;
		if (this.existingTicks == 3) {
			this.markDead();
		}
	}
}
