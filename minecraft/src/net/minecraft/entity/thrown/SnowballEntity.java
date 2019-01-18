package net.minecraft.entity.thrown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3857;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SnowballEntity extends class_3857 {
	public SnowballEntity(World world) {
		super(EntityType.SNOWBALL, world);
	}

	public SnowballEntity(World world, LivingEntity livingEntity) {
		super(EntityType.SNOWBALL, livingEntity, world);
	}

	public SnowballEntity(World world, double d, double e, double f) {
		super(EntityType.SNOWBALL, d, e, f, world);
	}

	@Override
	protected Item method_16942() {
		return Items.field_8543;
	}

	@Environment(EnvType.CLIENT)
	private ParticleParameters method_16939() {
		ItemStack itemStack = this.method_16943();
		return (ParticleParameters)(itemStack.isEmpty() ? ParticleTypes.field_11230 : new ItemStackParticleParameters(ParticleTypes.field_11218, itemStack));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 3) {
			ParticleParameters particleParameters = this.method_16939();

			for (int i = 0; i < 8; i++) {
				this.world.addParticle(particleParameters, this.x, this.y, this.z, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void method_7492(HitResult hitResult) {
		if (hitResult.getType() == HitResult.Type.ENTITY) {
			Entity entity = ((EntityHitResult)hitResult).getEntity();
			int i = entity instanceof BlazeEntity ? 3 : 0;
			entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), (float)i);
		}

		if (!this.world.isClient) {
			this.world.summonParticle(this, (byte)3);
			this.invalidate();
		}
	}
}
