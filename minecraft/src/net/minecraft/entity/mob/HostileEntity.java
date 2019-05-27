package net.minecraft.entity.mob;

import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class HostileEntity extends MobEntityWithAi implements Monster {
	protected HostileEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.field_15251;
	}

	@Override
	public void tickMovement() {
		this.tickHandSwing();
		this.updateDespawnCounter();
		super.tickMovement();
	}

	protected void updateDespawnCounter() {
		float f = this.getBrightnessAtEyes();
		if (f > 0.5F) {
			this.despawnCounter += 2;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient && this.world.getDifficulty() == Difficulty.field_5801) {
			this.remove();
		}
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.field_14630;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.field_14836;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return this.isInvulnerableTo(damageSource) ? false : super.damage(damageSource, f);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14994;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14899;
	}

	@Override
	protected SoundEvent getFallSound(int i) {
		return i > 4 ? SoundEvents.field_15157 : SoundEvents.field_14754;
	}

	@Override
	public float getPathfindingFavor(BlockPos blockPos, ViewableWorld viewableWorld) {
		return 0.5F - viewableWorld.getBrightness(blockPos);
	}

	protected boolean checkLightLevelForSpawn() {
		BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
		if (this.world.getLightLevel(LightType.field_9284, blockPos) > this.random.nextInt(32)) {
			return false;
		} else {
			int i = this.world.isThundering() ? this.world.method_8603(blockPos, 10) : this.world.getLightLevel(blockPos);
			return i <= this.random.nextInt(8);
		}
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		return iWorld.getDifficulty() != Difficulty.field_5801 && this.checkLightLevelForSpawn() && super.canSpawn(iWorld, spawnType);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE);
	}

	@Override
	protected boolean canDropLootAndXp() {
		return true;
	}

	public boolean isAngryAt(PlayerEntity playerEntity) {
		return true;
	}

	@Override
	public ItemStack getArrowType(ItemStack itemStack) {
		if (itemStack.getItem() instanceof RangedWeaponItem) {
			Predicate<ItemStack> predicate = ((RangedWeaponItem)itemStack.getItem()).getHeldProjectiles();
			ItemStack itemStack2 = RangedWeaponItem.getHeldProjectile(this, predicate);
			return itemStack2.isEmpty() ? new ItemStack(Items.field_8107) : itemStack2;
		} else {
			return ItemStack.EMPTY;
		}
	}
}
