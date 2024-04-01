package net.minecraft.entity.mob;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class PoisonousPotatoZombieEntity extends ZombieEntity {
	public PoisonousPotatoZombieEntity(EntityType<? extends PoisonousPotatoZombieEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createPoisonousPotatoZombieAttributes() {
		return ZombieEntity.createZombieAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 8.0);
	}

	@Override
	protected void initGoals() {
		this.targetSelector.add(1, new ActiveTargetGoal(this, ZombieEntity.class, true, target -> !(target instanceof PoisonousPotatoZombieEntity)));
		this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true, target -> !this.isWearingPoisonousPotatoZombieHeadHat((PlayerEntity)target)));
		this.targetSelector.add(2, new RevengeGoal(this));
		this.initCustomGoals();
	}

	@Override
	protected void initCustomGoals() {
		this.goalSelector.add(2, new ZombieAttackGoal<>(this, 1.0, false));
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
	}

	private boolean isWearingPoisonousPotatoZombieHeadHat(PlayerEntity player) {
		ItemStack itemStack = player.getInventory().armor.get(3);
		return itemStack.isOf(Blocks.POISONOUS_POTATO_ZOMBIE_HEAD_HAT.asItem());
	}

	@Override
	public boolean onKilledOther(ServerWorld world, LivingEntity other) {
		boolean bl = super.onKilledOther(world, other);
		if (other instanceof ZombieEntity zombieEntity) {
			PoisonousPotatoZombieEntity poisonousPotatoZombieEntity = zombieEntity.convertTo(EntityType.POISONOUS_POTATO_ZOMBIE, false);
			if (poisonousPotatoZombieEntity != null) {
				poisonousPotatoZombieEntity.initialize(
					world, world.getLocalDifficulty(poisonousPotatoZombieEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true)
				);
				if (!this.isSilent()) {
					world.syncWorldEvent(null, 1051, this.getBlockPos(), 0);
				}

				bl = false;
			}
		}

		return bl;
	}

	@Override
	public boolean isPotato() {
		return true;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ZOMBIE_POTATO_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ZOMBIE_POTATO_DEATH;
	}

	@Override
	protected SoundEvent getStepSound() {
		return SoundEvents.ENTITY_ZOMBIE_POTATO_STEP;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ZOMBIE_POTATO_HURT;
	}

	@Override
	protected boolean canConvertInWater() {
		return false;
	}
}
