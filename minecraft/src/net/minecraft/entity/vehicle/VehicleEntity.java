package net.minecraft.entity.vehicle;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public abstract class VehicleEntity extends Entity {
	protected static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(VehicleEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(VehicleEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(VehicleEntity.class, TrackedDataHandlerRegistry.FLOAT);

	public VehicleEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.getWorld().isClient || this.isRemoved()) {
			return true;
		} else if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.setDamageWobbleSide(-this.getDamageWobbleSide());
			this.setDamageWobbleTicks(10);
			this.scheduleVelocityUpdate();
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0F);
			this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
			boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode;
			if ((bl || !(this.getDamageWobbleStrength() > 40.0F)) && !this.shouldAlwaysKill(source)) {
				if (bl) {
					this.discard();
				}
			} else {
				this.killAndDropSelf(source);
			}

			return true;
		}
	}

	boolean shouldAlwaysKill(DamageSource source) {
		return false;
	}

	public void killAndDropItem(Item selfAsItem) {
		this.kill();
		if (this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			ItemStack itemStack = new ItemStack(selfAsItem);
			itemStack.set(DataComponentTypes.CUSTOM_NAME, this.getCustomName());
			this.dropStack(itemStack);
		}
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(DAMAGE_WOBBLE_TICKS, 0);
		builder.add(DAMAGE_WOBBLE_SIDE, 1);
		builder.add(DAMAGE_WOBBLE_STRENGTH, 0.0F);
	}

	public void setDamageWobbleTicks(int damageWobbleTicks) {
		this.dataTracker.set(DAMAGE_WOBBLE_TICKS, damageWobbleTicks);
	}

	public void setDamageWobbleSide(int damageWobbleSide) {
		this.dataTracker.set(DAMAGE_WOBBLE_SIDE, damageWobbleSide);
	}

	public void setDamageWobbleStrength(float damageWobbleStrength) {
		this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, damageWobbleStrength);
	}

	public float getDamageWobbleStrength() {
		return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
	}

	public int getDamageWobbleTicks() {
		return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
	}

	public int getDamageWobbleSide() {
		return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
	}

	protected void killAndDropSelf(DamageSource source) {
		this.killAndDropItem(this.asItem());
	}

	abstract Item asItem();
}
