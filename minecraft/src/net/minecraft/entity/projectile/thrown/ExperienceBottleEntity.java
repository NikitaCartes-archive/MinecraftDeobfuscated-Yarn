package net.minecraft.entity.projectile.thrown;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ExperienceBottleEntity extends ThrownItemEntity {
	public ExperienceBottleEntity(EntityType<? extends ExperienceBottleEntity> entityType, World world) {
		super(entityType, world);
	}

	public ExperienceBottleEntity(World world, LivingEntity owner) {
		super(EntityType.field_6064, owner, world);
	}

	public ExperienceBottleEntity(World world, double x, double y, double z) {
		super(EntityType.field_6064, x, y, z, world);
	}

	@Override
	protected Item getDefaultItem() {
		return Items.field_8287;
	}

	@Override
	protected float getGravity() {
		return 0.07F;
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.world.isClient) {
			this.world.syncWorldEvent(2002, this.getBlockPos(), PotionUtil.getColor(Potions.field_8991));
			int i = 3 + this.world.random.nextInt(5) + this.world.random.nextInt(5);

			while (i > 0) {
				int j = ExperienceOrbEntity.roundToOrbSize(i);
				i -= j;
				this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY(), this.getZ(), j));
			}

			this.remove();
		}
	}
}
