package net.minecraft.entity.projectile;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class FireballEntity extends AbstractFireballEntity {
	private int explosionPower = 1;

	public FireballEntity(EntityType<? extends FireballEntity> entityType, World world) {
		super(entityType, world);
	}

	public FireballEntity(World world, LivingEntity owner, Vec3d velocity, int explosionPower) {
		super(EntityType.FIREBALL, owner, velocity, world);
		this.explosionPower = explosionPower;
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.getWorld().isClient) {
			boolean bl = this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
			this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, bl, World.ExplosionSourceType.MOB);
			this.discard();
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			Entity var6 = entityHitResult.getEntity();
			Entity entity2 = this.getOwner();
			DamageSource damageSource = this.getDamageSources().fireball(this, entity2);
			var6.damage(damageSource, 6.0F);
			EnchantmentHelper.onTargetDamaged(serverWorld, var6, damageSource);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putByte("ExplosionPower", (byte)this.explosionPower);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("ExplosionPower", NbtElement.NUMBER_TYPE)) {
			this.explosionPower = nbt.getByte("ExplosionPower");
		}
	}
}
