package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ProjectileDamageSource extends EntityDamageSource {
	private final Entity attacker;

	public ProjectileDamageSource(String string, Entity entity, @Nullable Entity entity2) {
		super(string, entity);
		this.attacker = entity2;
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
	public Text method_5506(LivingEntity livingEntity) {
		Text text = this.attacker == null ? this.source.method_5476() : this.attacker.method_5476();
		ItemStack itemStack = this.attacker instanceof LivingEntity ? ((LivingEntity)this.attacker).getMainHandStack() : ItemStack.EMPTY;
		String string = "death.attack." + this.name;
		String string2 = string + ".item";
		return !itemStack.isEmpty() && itemStack.hasCustomName()
			? new TranslatableText(string2, livingEntity.method_5476(), text, itemStack.method_7954())
			: new TranslatableText(string, livingEntity.method_5476(), text);
	}
}
