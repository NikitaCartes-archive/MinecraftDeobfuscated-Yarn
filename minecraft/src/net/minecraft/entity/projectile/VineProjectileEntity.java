package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VineProjectileEntity extends ExplosiveProjectileEntity {
	private static final ItemStack POISONOUS_POTATO = new ItemStack(Items.POISONOUS_POTATO);
	private static final TrackedData<Float> STRENGTH = DataTracker.registerData(VineProjectileEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private int lifetime = 60;

	public VineProjectileEntity(EntityType<? extends VineProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(STRENGTH, 5.0F);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("lifetime", this.lifetime);
		nbt.putFloat("strength", this.strength());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.lifetime = nbt.getInt("lifetime");
		this.setStrength(nbt.getFloat("strength"));
	}

	public void setStrength(float strength) {
		this.dataTracker.set(STRENGTH, strength);
	}

	public float strength() {
		return this.dataTracker.get(STRENGTH);
	}

	@Override
	public void tick() {
		if (!this.getWorld().isClient) {
			this.lifetime--;
			if (this.lifetime <= 0) {
				this.discard();
				return;
			}
		}

		HitResult hitResult = ProjectileUtil.getCollision(this, Entity::isAlive, this.getRaycastShapeType());
		this.onCollision(hitResult);
		Vec3d vec3d = this.getPos();
		Vec3d vec3d2 = this.getVelocity();
		Vec3d vec3d3 = vec3d.add(vec3d2);
		Vec3d vec3d4 = vec3d.add(vec3d2.multiply(0.5));
		float f = this.strength();
		if (this.random.nextFloat() < f / 2.0F) {
			this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, vec3d3.x, vec3d3.y, vec3d3.z, 0.0, 0.0, 0.0);
		}

		if (this.random.nextFloat() < f / 2.0F) {
			this.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, POISONOUS_POTATO), vec3d4.x, vec3d4.y, vec3d4.z, 0.0, 0.0, 0.0);
		}

		this.setPosition(vec3d3.x, vec3d3.y, vec3d3.z);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.ENTITY) {
			Entity entity = ((EntityHitResult)hitResult).getEntity();
			entity.damage(this.getWorld().getDamageSources().potatoMagic(), this.strength());
		} else if (type == HitResult.Type.BLOCK) {
			this.discard();
		}
	}
}
