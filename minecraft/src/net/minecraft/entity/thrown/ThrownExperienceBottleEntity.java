package net.minecraft.entity.thrown;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThrownExperienceBottleEntity extends ThrownItemEntity {
	public ThrownExperienceBottleEntity(EntityType<? extends ThrownExperienceBottleEntity> entityType, World world) {
		super(entityType, world);
	}

	public ThrownExperienceBottleEntity(World world, LivingEntity livingEntity) {
		super(EntityType.field_6064, livingEntity, world);
	}

	public ThrownExperienceBottleEntity(World world, double d, double e, double f) {
		super(EntityType.field_6064, d, e, f, world);
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
	protected void method_7492(HitResult hitResult) {
		if (!this.field_6002.isClient) {
			this.field_6002.playLevelEvent(2002, new BlockPos(this), PotionUtil.getColor(Potions.field_8991));
			int i = 3 + this.field_6002.random.nextInt(5) + this.field_6002.random.nextInt(5);

			while (i > 0) {
				int j = ExperienceOrbEntity.roundToOrbSize(i);
				i -= j;
				this.field_6002.spawnEntity(new ExperienceOrbEntity(this.field_6002, this.x, this.y, this.z, j));
			}

			this.remove();
		}
	}
}
