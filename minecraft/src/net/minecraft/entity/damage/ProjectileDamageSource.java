package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

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
	public TextComponent getDeathMessage(LivingEntity livingEntity) {
		TextComponent textComponent = this.attacker == null ? this.source.getDisplayName() : this.attacker.getDisplayName();
		ItemStack itemStack = this.attacker instanceof LivingEntity ? ((LivingEntity)this.attacker).getMainHandStack() : ItemStack.EMPTY;
		String string = "death.attack." + this.name;
		String string2 = string + ".item";
		return !itemStack.isEmpty() && itemStack.hasDisplayName()
			? new TranslatableTextComponent(string2, livingEntity.getDisplayName(), textComponent, itemStack.toTextComponent())
			: new TranslatableTextComponent(string, livingEntity.getDisplayName(), textComponent);
	}
}
