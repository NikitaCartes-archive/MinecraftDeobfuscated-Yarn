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
	private boolean thorns;

	public EntityDamageSource(String name, @Nullable Entity entity) {
		super(name);
		this.source = entity;
	}

	public EntityDamageSource setThorns() {
		this.thorns = true;
		return this;
	}

	public boolean isThorns() {
		return this.thorns;
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
	public Vec3d getPosition() {
		return this.source != null ? this.source.getPos() : null;
	}

	@Override
	public String toString() {
		return "EntityDamageSource (" + this.source + ")";
	}
}
