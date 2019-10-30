package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ProjectileDamageSource extends EntityDamageSource {
	private final Entity attacker;

	public ProjectileDamageSource(String name, Entity projectile, @Nullable Entity entity) {
		super(name, projectile);
		this.attacker = entity;
	}

	@Nullable
	@Override
	public Entity getSource() {
		return this.source;
	}

	@Nullable
	@Override
	public Entity getAttacker() {
		return this.attacker;
	}

	@Override
	public Text getDeathMessage(LivingEntity livingEntity) {
		Text text = this.attacker == null ? this.source.getDisplayName() : this.attacker.getDisplayName();
		ItemStack itemStack = this.attacker instanceof LivingEntity ? ((LivingEntity)this.attacker).getMainHandStack() : ItemStack.EMPTY;
		String string = "death.attack." + this.name;
		String string2 = string + ".item";
		return !itemStack.isEmpty() && itemStack.hasCustomName()
			? new TranslatableText(string2, livingEntity.getDisplayName(), text, itemStack.toHoverableText())
			: new TranslatableText(string, livingEntity.getDisplayName(), text);
	}
}
