package net.minecraft.entity.thrown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.HitResult;
import net.minecraft.world.World;

public class SnowballEntity extends ThrownEntity {
	public SnowballEntity(World world) {
		super(EntityType.SNOWBALL, world);
	}

	public SnowballEntity(World world, LivingEntity livingEntity) {
		super(EntityType.SNOWBALL, livingEntity, world);
	}

	public SnowballEntity(World world, double d, double e, double f) {
		super(EntityType.SNOWBALL, d, e, f, world);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 3) {
			for (int i = 0; i < 8; i++) {
				this.world.method_8406(ParticleTypes.field_11230, this.x, this.y, this.z, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		if (hitResult.entity != null) {
			int i = 0;
			if (hitResult.entity instanceof BlazeEntity) {
				i = 3;
			}

			hitResult.entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), (float)i);
		}

		if (!this.world.isRemote) {
			this.world.method_8421(this, (byte)3);
			this.invalidate();
		}
	}
}
