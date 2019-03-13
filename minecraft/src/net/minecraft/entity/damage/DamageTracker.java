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
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;

public class DamageTracker {
	private final List<DamageRecord> recentDamage = Lists.<DamageRecord>newArrayList();
	private final LivingEntity field_5877;
	private int ageOnLastDamage;
	private int ageOnLastAttacked;
	private int ageOnLastUpdate;
	private boolean recentlyAttacked;
	private boolean hasDamage;
	private String fallDeathSuffix;

	public DamageTracker(LivingEntity livingEntity) {
		this.field_5877 = livingEntity;
	}

	public void setFallDeathSuffix() {
		this.clearFallDeathSuffix();
		if (this.field_5877.canClimb()) {
			Block block = this.field_5877.field_6002.method_8320(new BlockPos(this.field_5877.x, this.field_5877.method_5829().minY, this.field_5877.z)).getBlock();
			if (block == Blocks.field_9983) {
				this.fallDeathSuffix = "ladder";
			} else if (block == Blocks.field_10597) {
				this.fallDeathSuffix = "vines";
			}
		} else if (this.field_5877.isInsideWater()) {
			this.fallDeathSuffix = "water";
		}
	}

	public void method_5547(DamageSource damageSource, float f, float g) {
		this.update();
		this.setFallDeathSuffix();
		DamageRecord damageRecord = new DamageRecord(damageSource, this.field_5877.age, f, g, this.fallDeathSuffix, this.field_5877.fallDistance);
		this.recentDamage.add(damageRecord);
		this.ageOnLastDamage = this.field_5877.age;
		this.hasDamage = true;
		if (damageRecord.isAttackerLiving() && !this.recentlyAttacked && this.field_5877.isValid()) {
			this.recentlyAttacked = true;
			this.ageOnLastAttacked = this.field_5877.age;
			this.ageOnLastUpdate = this.ageOnLastAttacked;
			this.field_5877.method_6000();
		}
	}

	public TextComponent method_5548() {
		if (this.recentDamage.isEmpty()) {
			return new TranslatableTextComponent("death.attack.generic", this.field_5877.method_5476());
		} else {
			DamageRecord damageRecord = this.getBiggestFall();
			DamageRecord damageRecord2 = (DamageRecord)this.recentDamage.get(this.recentDamage.size() - 1);
			TextComponent textComponent = damageRecord2.method_5498();
			Entity entity = damageRecord2.method_5499().method_5529();
			TextComponent textComponent3;
			if (damageRecord != null && damageRecord2.method_5499() == DamageSource.FALL) {
				TextComponent textComponent2 = damageRecord.method_5498();
				if (damageRecord.method_5499() == DamageSource.FALL || damageRecord.method_5499() == DamageSource.OUT_OF_WORLD) {
					textComponent3 = new TranslatableTextComponent("death.fell.accident." + this.getFallDeathSuffix(damageRecord), this.field_5877.method_5476());
				} else if (textComponent2 != null && (textComponent == null || !textComponent2.equals(textComponent))) {
					Entity entity2 = damageRecord.method_5499().method_5529();
					ItemStack itemStack = entity2 instanceof LivingEntity ? ((LivingEntity)entity2).method_6047() : ItemStack.EMPTY;
					if (!itemStack.isEmpty() && itemStack.hasDisplayName()) {
						textComponent3 = new TranslatableTextComponent("death.fell.assist.item", this.field_5877.method_5476(), textComponent2, itemStack.method_7954());
					} else {
						textComponent3 = new TranslatableTextComponent("death.fell.assist", this.field_5877.method_5476(), textComponent2);
					}
				} else if (textComponent != null) {
					ItemStack itemStack2 = entity instanceof LivingEntity ? ((LivingEntity)entity).method_6047() : ItemStack.EMPTY;
					if (!itemStack2.isEmpty() && itemStack2.hasDisplayName()) {
						textComponent3 = new TranslatableTextComponent("death.fell.finish.item", this.field_5877.method_5476(), textComponent, itemStack2.method_7954());
					} else {
						textComponent3 = new TranslatableTextComponent("death.fell.finish", this.field_5877.method_5476(), textComponent);
					}
				} else {
					textComponent3 = new TranslatableTextComponent("death.fell.killer", this.field_5877.method_5476());
				}
			} else {
				textComponent3 = damageRecord2.method_5499().method_5506(this.field_5877);
			}

			return textComponent3;
		}
	}

	@Nullable
	public LivingEntity method_5541() {
		LivingEntity livingEntity = null;
		PlayerEntity playerEntity = null;
		float f = 0.0F;
		float g = 0.0F;

		for (DamageRecord damageRecord : this.recentDamage) {
			if (damageRecord.method_5499().method_5529() instanceof PlayerEntity && (playerEntity == null || damageRecord.getDamage() > g)) {
				g = damageRecord.getDamage();
				playerEntity = (PlayerEntity)damageRecord.method_5499().method_5529();
			}

			if (damageRecord.method_5499().method_5529() instanceof LivingEntity && (livingEntity == null || damageRecord.getDamage() > f)) {
				f = damageRecord.getDamage();
				livingEntity = (LivingEntity)damageRecord.method_5499().method_5529();
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
			if ((damageRecord3.method_5499() == DamageSource.FALL || damageRecord3.method_5499() == DamageSource.OUT_OF_WORLD)
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
		return this.recentlyAttacked ? this.field_5877.age - this.ageOnLastAttacked : this.ageOnLastUpdate - this.ageOnLastAttacked;
	}

	private void clearFallDeathSuffix() {
		this.fallDeathSuffix = null;
	}

	public void update() {
		int i = this.recentlyAttacked ? 300 : 100;
		if (this.hasDamage && (!this.field_5877.isValid() || this.field_5877.age - this.ageOnLastDamage > i)) {
			boolean bl = this.recentlyAttacked;
			this.hasDamage = false;
			this.recentlyAttacked = false;
			this.ageOnLastUpdate = this.field_5877.age;
			if (bl) {
				this.field_5877.method_6044();
			}

			this.recentDamage.clear();
		}
	}

	public LivingEntity method_5540() {
		return this.field_5877;
	}
}
