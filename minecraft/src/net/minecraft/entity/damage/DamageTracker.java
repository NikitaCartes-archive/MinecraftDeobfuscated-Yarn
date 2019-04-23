package net.minecraft.entity.damage;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.math.BlockPos;

public class DamageTracker {
	private final List<DamageRecord> recentDamage = Lists.<DamageRecord>newArrayList();
	private final LivingEntity entity;
	private int ageOnLastDamage;
	private int ageOnLastAttacked;
	private int ageOnLastUpdate;
	private boolean recentlyAttacked;
	private boolean hasDamage;
	private String fallDeathSuffix;

	public DamageTracker(LivingEntity livingEntity) {
		this.entity = livingEntity;
	}

	public void setFallDeathSuffix() {
		this.clearFallDeathSuffix();
		if (this.entity.isClimbing()) {
			Block block = this.entity.world.getBlockState(new BlockPos(this.entity.x, this.entity.getBoundingBox().minY, this.entity.z)).getBlock();
			if (block == Blocks.field_9983) {
				this.fallDeathSuffix = "ladder";
			} else if (block == Blocks.field_10597) {
				this.fallDeathSuffix = "vines";
			}
		} else if (this.entity.isInsideWater()) {
			this.fallDeathSuffix = "water";
		}
	}

	public void onDamage(DamageSource damageSource, float f, float g) {
		this.update();
		this.setFallDeathSuffix();
		DamageRecord damageRecord = new DamageRecord(damageSource, this.entity.age, f, g, this.fallDeathSuffix, this.entity.fallDistance);
		this.recentDamage.add(damageRecord);
		this.ageOnLastDamage = this.entity.age;
		this.hasDamage = true;
		if (damageRecord.isAttackerLiving() && !this.recentlyAttacked && this.entity.isAlive()) {
			this.recentlyAttacked = true;
			this.ageOnLastAttacked = this.entity.age;
			this.ageOnLastUpdate = this.ageOnLastAttacked;
			this.entity.method_6000();
		}
	}

	public Component getDeathMessage() {
		if (this.recentDamage.isEmpty()) {
			return new TranslatableComponent("death.attack.generic", this.entity.getDisplayName());
		} else {
			DamageRecord damageRecord = this.getBiggestFall();
			DamageRecord damageRecord2 = (DamageRecord)this.recentDamage.get(this.recentDamage.size() - 1);
			Component component = damageRecord2.getAttackerName();
			Entity entity = damageRecord2.getDamageSource().getAttacker();
			Component component3;
			if (damageRecord != null && damageRecord2.getDamageSource() == DamageSource.FALL) {
				Component component2 = damageRecord.getAttackerName();
				if (damageRecord.getDamageSource() == DamageSource.FALL || damageRecord.getDamageSource() == DamageSource.OUT_OF_WORLD) {
					component3 = new TranslatableComponent("death.fell.accident." + this.getFallDeathSuffix(damageRecord), this.entity.getDisplayName());
				} else if (component2 != null && (component == null || !component2.equals(component))) {
					Entity entity2 = damageRecord.getDamageSource().getAttacker();
					ItemStack itemStack = entity2 instanceof LivingEntity ? ((LivingEntity)entity2).getMainHandStack() : ItemStack.EMPTY;
					if (!itemStack.isEmpty() && itemStack.hasDisplayName()) {
						component3 = new TranslatableComponent("death.fell.assist.item", this.entity.getDisplayName(), component2, itemStack.toTextComponent());
					} else {
						component3 = new TranslatableComponent("death.fell.assist", this.entity.getDisplayName(), component2);
					}
				} else if (component != null) {
					ItemStack itemStack2 = entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandStack() : ItemStack.EMPTY;
					if (!itemStack2.isEmpty() && itemStack2.hasDisplayName()) {
						component3 = new TranslatableComponent("death.fell.finish.item", this.entity.getDisplayName(), component, itemStack2.toTextComponent());
					} else {
						component3 = new TranslatableComponent("death.fell.finish", this.entity.getDisplayName(), component);
					}
				} else {
					component3 = new TranslatableComponent("death.fell.killer", this.entity.getDisplayName());
				}
			} else {
				component3 = damageRecord2.getDamageSource().getDeathMessage(this.entity);
			}

			return component3;
		}
	}

	@Nullable
	public LivingEntity getBiggestAttacker() {
		LivingEntity livingEntity = null;
		PlayerEntity playerEntity = null;
		float f = 0.0F;
		float g = 0.0F;

		for (DamageRecord damageRecord : this.recentDamage) {
			if (damageRecord.getDamageSource().getAttacker() instanceof PlayerEntity && (playerEntity == null || damageRecord.getDamage() > g)) {
				g = damageRecord.getDamage();
				playerEntity = (PlayerEntity)damageRecord.getDamageSource().getAttacker();
			}

			if (damageRecord.getDamageSource().getAttacker() instanceof LivingEntity && (livingEntity == null || damageRecord.getDamage() > f)) {
				f = damageRecord.getDamage();
				livingEntity = (LivingEntity)damageRecord.getDamageSource().getAttacker();
			}
		}

		return (LivingEntity)(playerEntity != null && g >= f / 3.0F ? playerEntity : livingEntity);
	}

	@Nullable
	private DamageRecord getBiggestFall() {
		DamageRecord damageRecord = null;
		DamageRecord damageRecord2 = null;
		float f = 0.0F;
		float g = 0.0F;

		for (int i = 0; i < this.recentDamage.size(); i++) {
			DamageRecord damageRecord3 = (DamageRecord)this.recentDamage.get(i);
			DamageRecord damageRecord4 = i > 0 ? (DamageRecord)this.recentDamage.get(i - 1) : null;
			if ((damageRecord3.getDamageSource() == DamageSource.FALL || damageRecord3.getDamageSource() == DamageSource.OUT_OF_WORLD)
				&& damageRecord3.getFallDistance() > 0.0F
				&& (damageRecord == null || damageRecord3.getFallDistance() > g)) {
				if (i > 0) {
					damageRecord = damageRecord4;
				} else {
					damageRecord = damageRecord3;
				}

				g = damageRecord3.getFallDistance();
			}

			if (damageRecord3.getFallDeathSuffix() != null && (damageRecord2 == null || damageRecord3.getDamage() > f)) {
				damageRecord2 = damageRecord3;
				f = damageRecord3.getDamage();
			}
		}

		if (g > 5.0F && damageRecord != null) {
			return damageRecord;
		} else {
			return f > 5.0F && damageRecord2 != null ? damageRecord2 : null;
		}
	}

	private String getFallDeathSuffix(DamageRecord damageRecord) {
		return damageRecord.getFallDeathSuffix() == null ? "generic" : damageRecord.getFallDeathSuffix();
	}

	public int getTimeSinceLastAttack() {
		return this.recentlyAttacked ? this.entity.age - this.ageOnLastAttacked : this.ageOnLastUpdate - this.ageOnLastAttacked;
	}

	private void clearFallDeathSuffix() {
		this.fallDeathSuffix = null;
	}

	public void update() {
		int i = this.recentlyAttacked ? 300 : 100;
		if (this.hasDamage && (!this.entity.isAlive() || this.entity.age - this.ageOnLastDamage > i)) {
			boolean bl = this.recentlyAttacked;
			this.hasDamage = false;
			this.recentlyAttacked = false;
			this.ageOnLastUpdate = this.entity.age;
			if (bl) {
				this.entity.method_6044();
			}

			this.recentDamage.clear();
		}
	}

	public LivingEntity getEntity() {
		return this.entity;
	}
}
