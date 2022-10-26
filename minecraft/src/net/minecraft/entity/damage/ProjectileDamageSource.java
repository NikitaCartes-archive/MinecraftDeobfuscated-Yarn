package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ProjectileDamageSource extends EntityDamageSource {
	@Nullable
	private final Entity attacker;

	public ProjectileDamageSource(String name, Entity projectile, @Nullable Entity attacker) {
		super(name, projectile);
		this.attacker = attacker;
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
	public Text getDeathMessage(LivingEntity entity) {
		Text text = this.attacker == null ? this.source.getDisplayName() : this.attacker.getDisplayName();
		ItemStack itemStack = this.attacker instanceof LivingEntity livingEntity ? livingEntity.getMainHandStack() : ItemStack.EMPTY;
		String string = "death.attack." + this.name;
		if (!itemStack.isEmpty() && itemStack.hasCustomName()) {
			String string2 = string + ".item";
			return Text.translatable(string2, entity.getDisplayName(), text, itemStack.toHoverableText());
		} else {
			return Text.translatable(string, entity.getDisplayName(), text);
		}
	}
}
