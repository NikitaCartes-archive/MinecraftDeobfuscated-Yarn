package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BoggedEntity extends AbstractSkeletonEntity implements Shearable {
	private static final int HARD_ATTACK_INTERVAL = 50;
	private static final int REGULAR_ATTACK_INTERVAL = 70;
	private static final TrackedData<Boolean> SHEARED = DataTracker.registerData(BoggedEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final String SHEARED_KEY = "sheared";

	public static DefaultAttributeContainer.Builder createBoggedAttributes() {
		return AbstractSkeletonEntity.createAbstractSkeletonAttributes().add(EntityAttributes.MAX_HEALTH, 16.0);
	}

	public BoggedEntity(EntityType<? extends BoggedEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(SHEARED, false);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("sheared", this.isSheared());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setSheared(nbt.getBoolean("sheared"));
	}

	public boolean isSheared() {
		return this.dataTracker.get(SHEARED);
	}

	public void setSheared(boolean sheared) {
		this.dataTracker.set(SHEARED, sheared);
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
			if (this.getWorld() instanceof ServerWorld serverWorld) {
				this.sheared(serverWorld, SoundCategory.PLAYERS, itemStack);
				this.emitGameEvent(GameEvent.SHEAR, player);
				itemStack.damage(1, player, getSlotForHand(hand));
			}

			return ActionResult.SUCCESS;
		} else {
			return super.interactMob(player, hand);
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_BOGGED_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_BOGGED_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BOGGED_DEATH;
	}

	@Override
	protected SoundEvent getStepSound() {
		return SoundEvents.ENTITY_BOGGED_STEP;
	}

	@Override
	protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom) {
		PersistentProjectileEntity persistentProjectileEntity = super.createArrowProjectile(arrow, damageModifier, shotFrom);
		if (persistentProjectileEntity instanceof ArrowEntity arrowEntity) {
			arrowEntity.addEffect(new StatusEffectInstance(StatusEffects.POISON, 100));
		}

		return persistentProjectileEntity;
	}

	@Override
	protected int getHardAttackInterval() {
		return 50;
	}

	@Override
	protected int getRegularAttackInterval() {
		return 70;
	}

	@Override
	public void sheared(ServerWorld world, SoundCategory shearedSoundCategory, ItemStack shears) {
		world.playSoundFromEntity(null, this, SoundEvents.ENTITY_BOGGED_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
		this.dropShearedItems(world, shears);
		this.setSheared(true);
	}

	private void dropShearedItems(ServerWorld world, ItemStack shears) {
		this.forEachShearedItem(world, LootTables.BOGGED_SHEARING, shears, (worldx, stack) -> this.dropStack(worldx, stack, this.getHeight()));
	}

	@Override
	public boolean isShearable() {
		return !this.isSheared() && this.isAlive();
	}
}
