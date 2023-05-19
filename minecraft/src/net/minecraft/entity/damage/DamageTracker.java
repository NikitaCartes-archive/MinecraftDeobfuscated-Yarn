package net.minecraft.entity.damage;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.class_8572;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class DamageTracker {
	public static final int DAMAGE_COOLDOWN = 100;
	public static final int ATTACK_DAMAGE_COOLDOWN = 300;
	private static final Style INTENTIONAL_GAME_DESIGN_ISSUE_LINK_STYLE = Style.EMPTY
		.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723"))
		.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("MCPE-28723")));
	private final List<DamageRecord> recentDamage = Lists.<DamageRecord>newArrayList();
	private final LivingEntity entity;
	private int ageOnLastDamage;
	private int ageOnLastAttacked;
	private int ageOnLastUpdate;
	private boolean recentlyAttacked;
	private boolean hasDamage;

	public DamageTracker(LivingEntity entity) {
		this.entity = entity;
	}

	public void onDamage(DamageSource damageSource, float originalHealth) {
		this.update();
		class_8572 lv = class_8572.method_52195(this.entity);
		DamageRecord damageRecord = new DamageRecord(damageSource, originalHealth, lv, this.entity.fallDistance);
		this.recentDamage.add(damageRecord);
		this.ageOnLastDamage = this.entity.age;
		this.hasDamage = true;
		if (!this.recentlyAttacked && this.entity.isAlive() && method_52191(damageSource)) {
			this.recentlyAttacked = true;
			this.ageOnLastAttacked = this.entity.age;
			this.ageOnLastUpdate = this.ageOnLastAttacked;
			this.entity.enterCombat();
		}
	}

	private static boolean method_52191(DamageSource damageSource) {
		return damageSource.getAttacker() instanceof LivingEntity;
	}

	private Text method_52193(Entity entity, Text text, String string, String string2) {
		ItemStack itemStack = entity instanceof LivingEntity livingEntity ? livingEntity.getMainHandStack() : ItemStack.EMPTY;
		return !itemStack.isEmpty() && itemStack.hasCustomName()
			? Text.translatable(string, this.entity.getDisplayName(), text, itemStack.toHoverableText())
			: Text.translatable(string2, this.entity.getDisplayName(), text);
	}

	private Text method_52190(DamageRecord damageRecord, @Nullable Entity entity) {
		DamageSource damageSource = damageRecord.damageSource();
		if (!damageSource.isIn(DamageTypeTags.IS_FALL) && !damageSource.isIn(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL)) {
			Text text = method_52192(entity);
			Entity entity2 = damageSource.getAttacker();
			Text text2 = method_52192(entity2);
			if (text2 != null && !text2.equals(text)) {
				return this.method_52193(entity2, text2, "death.fell.assist.item", "death.fell.assist");
			} else {
				return (Text)(text != null
					? this.method_52193(entity, text, "death.fell.finish.item", "death.fell.finish")
					: Text.translatable("death.fell.killer", this.entity.getDisplayName()));
			}
		} else {
			class_8572 lv = (class_8572)Objects.requireNonNullElse(damageRecord.fallLocation(), class_8572.GENERIC);
			return Text.translatable(lv.method_52194(), this.entity.getDisplayName());
		}
	}

	@Nullable
	private static Text method_52192(@Nullable Entity entity) {
		return entity == null ? null : entity.getDisplayName();
	}

	public Text getDeathMessage() {
		if (this.recentDamage.isEmpty()) {
			return Text.translatable("death.attack.generic", this.entity.getDisplayName());
		} else {
			DamageRecord damageRecord = (DamageRecord)this.recentDamage.get(this.recentDamage.size() - 1);
			DamageSource damageSource = damageRecord.damageSource();
			DamageRecord damageRecord2 = this.getBiggestFall();
			DeathMessageType deathMessageType = damageSource.getType().deathMessageType();
			if (deathMessageType == DeathMessageType.FALL_VARIANTS && damageRecord2 != null) {
				return this.method_52190(damageRecord2, damageSource.getAttacker());
			} else if (deathMessageType == DeathMessageType.INTENTIONAL_GAME_DESIGN) {
				String string = "death.attack." + damageSource.getName();
				Text text = Texts.bracketed(Text.translatable(string + ".link")).fillStyle(INTENTIONAL_GAME_DESIGN_ISSUE_LINK_STYLE);
				return Text.translatable(string + ".message", this.entity.getDisplayName(), text);
			} else {
				return damageSource.getDeathMessage(this.entity);
			}
		}
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
			DamageSource damageSource = damageRecord3.damageSource();
			boolean bl = damageSource.isIn(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL);
			float h = bl ? Float.MAX_VALUE : damageRecord3.fallDistance();
			if ((damageSource.isIn(DamageTypeTags.IS_FALL) || bl) && h > 0.0F && (damageRecord == null || h > g)) {
				if (i > 0) {
					damageRecord = damageRecord4;
				} else {
					damageRecord = damageRecord3;
				}

				g = h;
			}

			if (damageRecord3.fallLocation() != null && (damageRecord2 == null || damageRecord3.damage() > f)) {
				damageRecord2 = damageRecord3;
				f = damageRecord3.damage();
			}
		}

		if (g > 5.0F && damageRecord != null) {
			return damageRecord;
		} else {
			return f > 5.0F && damageRecord2 != null ? damageRecord2 : null;
		}
	}

	public int getTimeSinceLastAttack() {
		return this.recentlyAttacked ? this.entity.age - this.ageOnLastAttacked : this.ageOnLastUpdate - this.ageOnLastAttacked;
	}

	public void update() {
		int i = this.recentlyAttacked ? 300 : 100;
		if (this.hasDamage && (!this.entity.isAlive() || this.entity.age - this.ageOnLastDamage > i)) {
			boolean bl = this.recentlyAttacked;
			this.hasDamage = false;
			this.recentlyAttacked = false;
			this.ageOnLastUpdate = this.entity.age;
			if (bl) {
				this.entity.endCombat();
			}

			this.recentDamage.clear();
		}
	}
}
