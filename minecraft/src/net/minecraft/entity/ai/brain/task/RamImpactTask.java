package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class RamImpactTask extends MultiTickTask<GoatEntity> {
	public static final int RUN_TIME = 200;
	public static final float SPEED_STRENGTH_MULTIPLIER = 1.65F;
	private final Function<GoatEntity, UniformIntProvider> cooldownRangeFactory;
	private final TargetPredicate targetPredicate;
	private final float speed;
	private final ToDoubleFunction<GoatEntity> strengthMultiplierFactory;
	private Vec3d direction;
	private final Function<GoatEntity, SoundEvent> impactSoundFactory;
	private final Function<GoatEntity, SoundEvent> hornBreakSoundFactory;

	public RamImpactTask(
		Function<GoatEntity, UniformIntProvider> cooldownRangeFactory,
		TargetPredicate targetPredicate,
		float speed,
		ToDoubleFunction<GoatEntity> strengthMultiplierFactory,
		Function<GoatEntity, SoundEvent> impactSoundFactory,
		Function<GoatEntity, SoundEvent> hornBreakSoundFactory
	) {
		super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryModuleState.VALUE_PRESENT), 200);
		this.cooldownRangeFactory = cooldownRangeFactory;
		this.targetPredicate = targetPredicate;
		this.speed = speed;
		this.strengthMultiplierFactory = strengthMultiplierFactory;
		this.impactSoundFactory = impactSoundFactory;
		this.hornBreakSoundFactory = hornBreakSoundFactory;
		this.direction = Vec3d.ZERO;
	}

	protected boolean shouldRun(ServerWorld serverWorld, GoatEntity goatEntity) {
		return goatEntity.getBrain().hasMemoryModule(MemoryModuleType.RAM_TARGET);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, GoatEntity goatEntity, long l) {
		return goatEntity.getBrain().hasMemoryModule(MemoryModuleType.RAM_TARGET);
	}

	protected void run(ServerWorld serverWorld, GoatEntity goatEntity, long l) {
		BlockPos blockPos = goatEntity.getBlockPos();
		Brain<?> brain = goatEntity.getBrain();
		Vec3d vec3d = (Vec3d)brain.getOptionalRegisteredMemory(MemoryModuleType.RAM_TARGET).get();
		this.direction = new Vec3d((double)blockPos.getX() - vec3d.getX(), 0.0, (double)blockPos.getZ() - vec3d.getZ()).normalize();
		brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed, 0));
	}

	protected void keepRunning(ServerWorld serverWorld, GoatEntity goatEntity, long l) {
		List<LivingEntity> list = serverWorld.getTargets(LivingEntity.class, this.targetPredicate, goatEntity, goatEntity.getBoundingBox());
		Brain<?> brain = goatEntity.getBrain();
		if (!list.isEmpty()) {
			LivingEntity livingEntity = (LivingEntity)list.get(0);
			livingEntity.damage(serverWorld.getDamageSources().mobAttackNoAggro(goatEntity), (float)goatEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
			int i = goatEntity.hasStatusEffect(StatusEffects.SPEED) ? goatEntity.getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1 : 0;
			int j = goatEntity.hasStatusEffect(StatusEffects.SLOWNESS) ? goatEntity.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier() + 1 : 0;
			float f = 0.25F * (float)(i - j);
			float g = MathHelper.clamp(goatEntity.getMovementSpeed() * 1.65F, 0.2F, 3.0F) + f;
			float h = livingEntity.blockedByShield(serverWorld.getDamageSources().mobAttack(goatEntity)) ? 0.5F : 1.0F;
			livingEntity.takeKnockback((double)(h * g) * this.strengthMultiplierFactory.applyAsDouble(goatEntity), this.direction.getX(), this.direction.getZ());
			this.finishRam(serverWorld, goatEntity);
			serverWorld.playSoundFromEntity(null, goatEntity, (SoundEvent)this.impactSoundFactory.apply(goatEntity), SoundCategory.NEUTRAL, 1.0F, 1.0F);
		} else if (this.shouldSnapHorn(serverWorld, goatEntity)) {
			serverWorld.playSoundFromEntity(null, goatEntity, (SoundEvent)this.impactSoundFactory.apply(goatEntity), SoundCategory.NEUTRAL, 1.0F, 1.0F);
			boolean bl = goatEntity.dropHorn();
			if (bl) {
				serverWorld.playSoundFromEntity(null, goatEntity, (SoundEvent)this.hornBreakSoundFactory.apply(goatEntity), SoundCategory.NEUTRAL, 1.0F, 1.0F);
			}

			this.finishRam(serverWorld, goatEntity);
		} else {
			Optional<WalkTarget> optional = brain.getOptionalRegisteredMemory(MemoryModuleType.WALK_TARGET);
			Optional<Vec3d> optional2 = brain.getOptionalRegisteredMemory(MemoryModuleType.RAM_TARGET);
			boolean bl2 = optional.isEmpty() || optional2.isEmpty() || ((WalkTarget)optional.get()).getLookTarget().getPos().isInRange((Position)optional2.get(), 0.25);
			if (bl2) {
				this.finishRam(serverWorld, goatEntity);
			}
		}
	}

	private boolean shouldSnapHorn(ServerWorld world, GoatEntity goat) {
		Vec3d vec3d = goat.getVelocity().multiply(1.0, 0.0, 1.0).normalize();
		BlockPos blockPos = BlockPos.ofFloored(goat.getPos().add(vec3d));
		return world.getBlockState(blockPos).isIn(BlockTags.SNAPS_GOAT_HORN) || world.getBlockState(blockPos.up()).isIn(BlockTags.SNAPS_GOAT_HORN);
	}

	protected void finishRam(ServerWorld world, GoatEntity goat) {
		world.sendEntityStatus(goat, EntityStatuses.FINISH_RAM);
		goat.getBrain().remember(MemoryModuleType.RAM_COOLDOWN_TICKS, ((UniformIntProvider)this.cooldownRangeFactory.apply(goat)).get(world.random));
		goat.getBrain().forget(MemoryModuleType.RAM_TARGET);
	}
}
