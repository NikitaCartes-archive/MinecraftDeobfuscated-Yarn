package net.minecraft.entity.mob;

import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;

public abstract class HostileEntity extends PathAwareEntity implements Monster {
	protected HostileEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
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
	protected boolean isDisallowedInPeaceful() {
		return true;
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_HOSTILE_SWIM;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_HOSTILE_SPLASH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_HOSTILE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_HOSTILE_DEATH;
	}

	@Override
	public LivingEntity.FallSounds getFallSounds() {
		return new LivingEntity.FallSounds(SoundEvents.ENTITY_HOSTILE_SMALL_FALL, SoundEvents.ENTITY_HOSTILE_BIG_FALL);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return -world.getPhototaxisFavor(pos);
	}

	public static boolean isSpawnDark(ServerWorldAccess world, BlockPos pos, Random random) {
		if (world.getLightLevel(LightType.SKY, pos) > random.nextInt(32)) {
			return false;
		} else {
			DimensionType dimensionType = world.getDimension();
			int i = dimensionType.monsterSpawnBlockLightLimit();
			if (i < 15 && world.getLightLevel(LightType.BLOCK, pos) > i) {
				return false;
			} else {
				int j = world.toServerWorld().isThundering() ? world.getLightLevel(pos, 10) : world.getLightLevel(pos);
				return j <= dimensionType.monsterSpawnLightTest().get(random);
			}
		}
	}

	public static boolean canSpawnInDark(EntityType<? extends HostileEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getDifficulty() != Difficulty.PEACEFUL
			&& (SpawnReason.isTrialSpawner(spawnReason) || isSpawnDark(world, pos, random))
			&& canMobSpawn(type, world, spawnReason, pos, random);
	}

	public static boolean canSpawnIgnoreLightLevel(
		EntityType<? extends HostileEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random
	) {
		return world.getDifficulty() != Difficulty.PEACEFUL && canMobSpawn(type, world, spawnReason, pos, random);
	}

	public static DefaultAttributeContainer.Builder createHostileAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE);
	}

	@Override
	public boolean shouldDropXp() {
		return true;
	}

	@Override
	protected boolean shouldDropLoot() {
		return true;
	}

	public boolean isAngryAt(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack getProjectileType(ItemStack stack) {
		if (stack.getItem() instanceof RangedWeaponItem) {
			Predicate<ItemStack> predicate = ((RangedWeaponItem)stack.getItem()).getHeldProjectiles();
			ItemStack itemStack = RangedWeaponItem.getHeldProjectile(this, predicate);
			return itemStack.isEmpty() ? new ItemStack(Items.ARROW) : itemStack;
		} else {
			return ItemStack.EMPTY;
		}
	}
}
