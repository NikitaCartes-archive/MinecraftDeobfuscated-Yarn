package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.Vec3d;

public class EntityDamageSource extends DamageSource {
	@Nullable
	protected final Entity field_5879;
	private boolean field_5880;

	public EntityDamageSource(String string, @Nullable Entity entity) {
		super(string);
		this.field_5879 = entity;
	}

	public EntityDamageSource method_5550() {
		this.field_5880 = true;
		return this;
	}

	public boolean method_5549() {
		return this.field_5880;
	}

	@Nullable
	@Override
	public Entity method_5529() {
		return this.field_5879;
	}

	@Override
	public TextComponent method_5506(LivingEntity livingEntity) {
		ItemStack itemStack = this.field_5879 instanceof LivingEntity ? ((LivingEntity)this.field_5879).method_6047() : ItemStack.EMPTY;
		String string = "death.attack." + this.name;
		return !itemStack.isEmpty() && itemStack.hasDisplayName()
			? new TranslatableTextComponent(string + ".item", livingEntity.method_5476(), this.field_5879.method_5476(), itemStack.method_7954())
			: new TranslatableTextComponent(string, livingEntity.method_5476(), this.field_5879.method_5476());
	}

	@Override
	public boolean isScaledWithDifficulty() {
		return this.field_5879 != null && this.field_5879 instanceof LivingEntity && !(this.field_5879 instanceof PlayerEntity);
	}

	@Nullable
	@Override
	public Vec3d method_5510() {
		return new Vec3d(this.field_5879.x, this.field_5879.y, this.field_5879.z);
	}
}
