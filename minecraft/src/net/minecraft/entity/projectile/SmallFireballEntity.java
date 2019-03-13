package net.minecraft.entity.projectile;

import net.minecraft.class_3855;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmallFireballEntity extends class_3855 {
	public SmallFireballEntity(EntityType<? extends SmallFireballEntity> entityType, World world) {
		super(entityType, world);
	}

	public SmallFireballEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.SMALL_FIREBALL, livingEntity, d, e, f, world);
	}

	public SmallFireballEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(EntityType.SMALL_FIREBALL, d, e, f, g, h, i, world);
	}

	@Override
	protected void method_7469(HitResult hitResult) {
		if (!this.field_6002.isClient) {
			if (hitResult.getType() == HitResult.Type.ENTITY) {
				Entity entity = ((EntityHitResult)hitResult).getEntity();
				if (!entity.isFireImmune()) {
					entity.setOnFireFor(5);
					boolean bl = entity.damage(DamageSource.method_5521(this, this.owner), 5.0F);
					if (bl) {
						this.method_5723(this.owner, entity);
					}
				}
			} else if (this.owner == null || !(this.owner instanceof MobEntity) || this.field_6002.getGameRules().getBoolean("mobGriefing")) {
				BlockHitResult blockHitResult = (BlockHitResult)hitResult;
				BlockPos blockPos = blockHitResult.method_17777().method_10093(blockHitResult.method_17780());
				if (this.field_6002.method_8623(blockPos)) {
					this.field_6002.method_8501(blockPos, Blocks.field_10036.method_9564());
				}
			}

			this.invalidate();
		}
	}

	@Override
	public boolean doesCollide() {
		return false;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return false;
	}
}
