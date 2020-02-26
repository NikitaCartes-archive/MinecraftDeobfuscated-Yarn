package net.minecraft.entity.thrown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SnowballEntity extends ThrownItemEntity {
	public SnowballEntity(EntityType<? extends SnowballEntity> entityType, World world) {
		super(entityType, world);
	}

	public SnowballEntity(World world, LivingEntity owner) {
		super(EntityType.SNOWBALL, owner, world);
	}

	public SnowballEntity(World world, double x, double y, double z) {
		super(EntityType.SNOWBALL, x, y, z, world);
	}

	@Override
	protected Item getDefaultItem() {
		return Items.SNOWBALL;
	}

	@Environment(EnvType.CLIENT)
	private ParticleEffect getParticleParameters() {
		ItemStack itemStack = this.getItem();
		return (ParticleEffect)(itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 3) {
			ParticleEffect particleEffect = this.getParticleParameters();

			for (int i = 0; i < 8; i++) {
				this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.ENTITY) {
			Entity entity = ((EntityHitResult)hitResult).getEntity();
			int i = entity instanceof BlazeEntity ? 3 : 0;
			entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), (float)i);
		} else if (type == HitResult.Type.BLOCK) {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			BlockState blockState = this.world.getBlockState(blockHitResult.getBlockPos());
			blockState.onProjectileHit(this.world, blockState, blockHitResult, this);
		}

		if (!this.world.isClient) {
			this.world.sendEntityStatus(this, (byte)3);
			this.remove();
		}
	}
}
