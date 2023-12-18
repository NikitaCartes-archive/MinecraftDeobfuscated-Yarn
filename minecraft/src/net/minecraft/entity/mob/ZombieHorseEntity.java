package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ZombieHorseEntity extends AbstractHorseEntity {
	private static final EntityDimensions BABY_BASE_DIMENSIONS = EntityType.ZOMBIE_HORSE
		.getDimensions()
		.withAttachments(EntityAttachments.builder().add(EntityAttachmentType.PASSENGER, 0.0F, EntityType.ZOMBIE_HORSE.getHeight() - 0.03125F, 0.0F))
		.scaled(0.5F);

	public ZombieHorseEntity(EntityType<? extends ZombieHorseEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createZombieHorseAttributes() {
		return createBaseHorseAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F);
	}

	public static boolean canSpawn(EntityType<? extends AnimalEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
		return !SpawnReason.isAnySpawner(reason)
			? AnimalEntity.isValidNaturalSpawn(type, world, reason, pos, random)
			: SpawnReason.isTrialSpawner(reason) || isLightLevelValidForNaturalSpawn(world, pos);
	}

	@Override
	protected void initAttributes(Random random) {
		this.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(getChildJumpStrengthBonus(random::nextDouble));
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ZOMBIE_HORSE_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ZOMBIE_HORSE_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ZOMBIE_HORSE_HURT;
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return EntityType.ZOMBIE_HORSE.create(world);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		return !this.isTame() ? ActionResult.PASS : super.interactMob(player, hand);
	}

	@Override
	protected void initCustomGoals() {
	}

	@Override
	public EntityDimensions getBaseDimensions(EntityPose pose) {
		return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
	}
}
