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
	private static final int field_32656 = 3;
	private final BufferBuilderStorage bufferStorage;
	private final Entity itemEntity;
	private final Entity interactingEntity;
	private int ticksExisted;
	private final EntityRenderDispatcher dispatcher;
	private double targetX;
	private double targetY;
	private double targetZ;
	private double lastTargetX;
	private double lastTargetY;
	private double lastTargetZ;

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
		this.itemEntity = this.getOrCopy(itemEntity);
		this.interactingEntity = interactingEntity;
		this.dispatcher = dispatcher;
		this.updateTargetPos();
		this.updateLastTargetPos();
	}

	private Entity getOrCopy(Entity entity) {
		return (Entity)(!(entity instanceof ItemEntity) ? entity : ((ItemEntity)entity).copy());
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		float f = ((float)this.ticksExisted + tickDelta) / 3.0F;
		f *= f;
		double d = MathHelper.lerp((double)tickDelta, this.lastTargetX, this.targetX);
		double e = MathHelper.lerp((double)tickDelta, this.lastTargetY, this.targetY);
		double g = MathHelper.lerp((double)tickDelta, this.lastTargetZ, this.targetZ);
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
				this.itemEntity.getYaw(),
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

		this.updateLastTargetPos();
		this.updateTargetPos();
	}

	private void updateTargetPos() {
		this.targetX = this.interactingEntity.getX();
		this.targetY = (this.interactingEntity.getY() + this.interactingEntity.getEyeY()) / 2.0;
		this.targetZ = this.interactingEntity.getZ();
	}

	private void updateLastTargetPos() {
		this.lastTargetX = this.targetX;
		this.lastTargetY = this.targetY;
		this.lastTargetZ = this.targetZ;
	}
}
