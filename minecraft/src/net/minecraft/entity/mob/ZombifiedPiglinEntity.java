package net.minecraft.entity.mob;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class ZombifiedPiglinEntity extends ZombieEntity {
	private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
	private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(
		ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.05, EntityAttributeModifier.Operation.ADDITION
	);
	private int anger;
	private int angrySoundDelay;
	private UUID angerTarget;

	public ZombifiedPiglinEntity(EntityType<? extends ZombifiedPiglinEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
	}

	@Override
	public void setAttacker(@Nullable LivingEntity attacker) {
		super.setAttacker(attacker);
		if (attacker != null) {
			this.angerTarget = attacker.getUuid();
		}
	}

	@Override
	protected void initCustomGoals() {
		this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
		this.targetSelector.add(1, new ZombifiedPiglinEntity.AvoidZombiesGoal(this));
		this.targetSelector.add(2, new ZombifiedPiglinEntity.FollowPlayerIfAngryGoal(this));
	}

	public static DefaultAttributeContainer.Builder createZombifiedPiglinAttributes() {
		return ZombieEntity.createZombieAttributes()
			.add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, 0.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
	}

	@Override
	protected boolean canConvertInWater() {
		return false;
	}

	@Override
	protected void mobTick() {
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		LivingEntity livingEntity = this.getAttacker();
		if (this.isAngry()) {
			if (!this.isBaby() && !entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
				entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
			}

			this.anger--;
			LivingEntity livingEntity2 = livingEntity != null ? livingEntity : this.getTarget();
			if (!this.isAngry() && livingEntity2 != null) {
				if (!this.canSee(livingEntity2)) {
					this.setAttacker(null);
					this.setTarget(null);
				} else {
					this.anger = this.getNewAngerDuration();
				}
			}
		} else if (entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
			entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
		}

		if (this.angrySoundDelay > 0 && --this.angrySoundDelay == 0) {
			this.playSound(
				SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.8F
			);
		}

		if (this.isAngry() && this.angerTarget != null && livingEntity == null) {
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.angerTarget);
			this.setAttacker(playerEntity);
			this.attackingPlayer = playerEntity;
			this.playerHitTimer = this.getLastAttackedTime();
		}

		super.mobTick();
	}

	public static boolean canSpawn(EntityType<ZombifiedPiglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getDifficulty() != Difficulty.PEACEFUL && world.getBlockState(pos.down()).getBlock() != Blocks.NETHER_WART_BLOCK;
	}

	@Override
	public boolean canSpawn(WorldView world) {
		return world.intersectsEntities(this) && !world.containsFluid(this.getBoundingBox());
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putShort("Anger", (short)this.anger);
		if (this.angerTarget != null) {
			tag.putUuid("HurtBy", this.angerTarget);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.anger = tag.getShort("Anger");
		if (tag.containsUuid("HurtBy")) {
			this.angerTarget = tag.getUuid("HurtBy");
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.angerTarget);
			this.setAttacker(playerEntity);
			if (playerEntity != null) {
				this.attackingPlayer = playerEntity;
				this.playerHitTimer = this.getLastAttackedTime();
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			Entity entity = source.getAttacker();
			if (entity instanceof PlayerEntity && !((PlayerEntity)entity).isCreative() && this.canSee(entity)) {
				this.getAngryAt((LivingEntity)entity);
			}

			return super.damage(source, amount);
		}
	}

	private boolean getAngryAt(LivingEntity entity) {
		this.anger = this.getNewAngerDuration();
		this.angrySoundDelay = this.random.nextInt(40);
		this.setAttacker(entity);
		return true;
	}

	private int getNewAngerDuration() {
		return 400 + this.random.nextInt(400);
	}

	private boolean isAngry() {
		return this.anger > 0;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_DEATH;
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}

	@Override
	protected void initAttributes() {
		this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0.0);
	}

	@Override
	public boolean isAngryAt(PlayerEntity player) {
		return this.isAngry();
	}

	static class AvoidZombiesGoal extends RevengeGoal {
		public AvoidZombiesGoal(ZombifiedPiglinEntity pigman) {
			super(pigman);
			this.setGroupRevenge(new Class[]{ZombieEntity.class});
		}

		@Override
		protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
			if (mob instanceof ZombifiedPiglinEntity && this.mob.canSee(target) && ((ZombifiedPiglinEntity)mob).getAngryAt(target)) {
				mob.setTarget(target);
			}
		}
	}

	static class FollowPlayerIfAngryGoal extends FollowTargetGoal<PlayerEntity> {
		public FollowPlayerIfAngryGoal(ZombifiedPiglinEntity pigman) {
			super(pigman, PlayerEntity.class, true);
		}

		@Override
		public boolean canStart() {
			return ((ZombifiedPiglinEntity)this.mob).isAngry() && super.canStart();
		}
	}
}
