package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;

public class EntityDamageSource extends DamageSource {
	@Nullable
	protected final Entity source;
	private boolean field_5880;

	public EntityDamageSource(String string, @Nullable Entity entity) {
		super(string);
		this.source = entity;
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
	public Entity getAttacker() {
		return this.source;
	}

	@Override
	public Text getDeathMessage(LivingEntity livingEntity) {
		ItemStack itemStack = this.source instanceof LivingEntity ? ((LivingEntity)this.source).getMainHandStack() : ItemStack.EMPTY;
		String string = "death.attack." + this.name;
		return !itemStack.isEmpty() && itemStack.hasCustomName()
			? new TranslatableText(string + ".item", livingEntity.getDisplayName(), this.source.getDisplayName(), itemStack.toHoverableText())
			: new TranslatableText(string, livingEntity.getDisplayName(), this.source.getDisplayName());
	}

	@Override
	public boolean isScaledWithDifficulty() {
		return this.source != null && this.source instanceof LivingEntity && !(this.source instanceof PlayerEntity);
	}

	@Nullable
	@Override
	public Vec3d method_5510() {
		return new Vec3d(this.source.x, this.source.y, this.source.z);
	}
}
