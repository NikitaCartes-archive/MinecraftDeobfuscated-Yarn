package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HuskEntity extends ZombieEntity {
	public HuskEntity(EntityType<? extends HuskEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		return super.method_5979(iWorld, spawnType) && (spawnType == SpawnType.field_16469 || iWorld.method_8311(new BlockPos(this)));
	}

	@Override
	protected boolean method_7216() {
		return false;
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14680;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15196;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14892;
	}

	@Override
	protected SoundEvent method_7207() {
		return SoundEvents.field_15046;
	}

	@Override
	public boolean attack(Entity entity) {
		boolean bl = super.attack(entity);
		if (bl && this.method_6047().isEmpty() && entity instanceof LivingEntity) {
			float f = this.field_6002.method_8404(new BlockPos(this)).getLocalDifficulty();
			((LivingEntity)entity).addPotionEffect(new StatusEffectInstance(StatusEffects.field_5903, 140 * (int)f));
		}

		return bl;
	}

	@Override
	protected boolean method_7209() {
		return true;
	}

	@Override
	protected void method_7218() {
		this.method_7200(EntityType.ZOMBIE);
		this.field_6002.method_8444(null, 1041, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
	}

	@Override
	protected ItemStack method_7215() {
		return ItemStack.EMPTY;
	}
}
