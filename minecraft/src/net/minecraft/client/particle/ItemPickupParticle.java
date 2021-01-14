package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ItemPickupParticle extends Particle {
	private final BufferBuilderStorage bufferStorage;
	private final Entity itemEntity;
	private final Entity interactingEntity;
	private int ticksExisted;
	private final EntityRenderDispatcher dispatcher;

	public ItemPickupParticle(
		EntityRenderDispatcher dispatcher, BufferBuilderStorage bufferStorage, ClientWorld world, Entity itemEntity, Entity interactingEntity
	) {
		this(dispatcher, bufferStorage, world, itemEntity, interactingEntity, itemEntity.getVelocity());
	}

	private ItemPickupParticle(
		EntityRenderDispatcher dispatcher, BufferBuilderStorage bufferStorage, ClientWorld world, Entity itemEntity, Entity interactingEntity, Vec3d velocity
	) {
		super(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), velocity.x, velocity.y, velocity.z);
		this.bufferStorage = bufferStorage;
		this.itemEntity = this.method_29358(itemEntity);
		this.interactingEntity = interactingEntity;
		this.dispatcher = dispatcher;
	}

	private Entity method_29358(Entity entity) {
		return (Entity)(!(entity instanceof ItemEntity) ? entity : ((ItemEntity)entity).method_29271());
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		float f = ((float)this.ticksExisted + tickDelta) / 3.0F;
		f *= f;
		double d = MathHelper.lerp((double)tickDelta, this.interactingEntity.lastRenderX, this.interactingEntity.getX());
		double e = MathHelper.lerp((double)tickDelta, this.interactingEntity.lastRenderY, this.interactingEntity.getY()) + 0.5;
		double g = MathHelper.lerp((double)tickDelta, this.interactingEntity.lastRenderZ, this.interactingEntity.getZ());
		double h = MathHelper.lerp((double)f, this.itemEntity.getX(), d);
		double i = MathHelper.lerp((double)f, this.itemEntity.getY(), e);
		double j = MathHelper.lerp((double)f, this.itemEntity.getZ(), g);
		VertexConsumerProvider.Immediate immediate = this.bufferStorage.getEntityVertexConsumers();
		Vec3d vec3d = camera.getPos();
		this.dispatcher
			.render(
				this.itemEntity,
				h - vec3d.getX(),
				i - vec3d.getY(),
				j - vec3d.getZ(),
				this.itemEntity.yaw,
				tickDelta,
				new MatrixStack(),
				immediate,
				this.dispatcher.getLight(this.itemEntity, tickDelta)
			);
		immediate.draw();
	}

	@Override
	public void tick() {
		this.ticksExisted++;
		if (this.ticksExisted == 3) {
			this.markDead();
		}
	}
}
