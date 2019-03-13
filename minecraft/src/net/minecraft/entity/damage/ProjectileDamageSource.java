package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class ProjectileDamageSource extends EntityDamageSource {
	private final Entity field_5878;

	public ProjectileDamageSource(String string, Entity entity, @Nullable Entity entity2) {
		super(string, entity);
		this.field_5878 = entity2;
	}

	@Nullable
	@Override
	public Entity method_5526() {
		return this.field_5879;
	}

	@Nullable
	@Override
	public Entity method_5529() {
		return this.field_5878;
	}

	@Override
	public TextComponent method_5506(LivingEntity livingEntity) {
		TextComponent textComponent = this.field_5878 == null ? this.field_5879.method_5476() : this.field_5878.method_5476();
		ItemStack itemStack = this.field_5878 instanceof LivingEntity ? ((LivingEntity)this.field_5878).method_6047() : ItemStack.EMPTY;
		String string = "death.attack." + this.name;
		String string2 = string + ".item";
		return !itemStack.isEmpty() && itemStack.hasDisplayName()
			? new TranslatableTextComponent(string2, livingEntity.method_5476(), textComponent, itemStack.method_7954())
			: new TranslatableTextComponent(string, livingEntity.method_5476(), textComponent);
	}
}
