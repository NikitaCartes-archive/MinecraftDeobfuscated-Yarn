package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class RamImpactTask<E extends PathAwareEntity> extends Task<E> {
	public static final int RUN_TIME = 200;
	public static final float SPEED_STRENGTH_MULTIPLIER = 1.65F;
	private final Function<E, UniformIntProvider> cooldownRangeFactory;
	private final TargetPredicate targetPredicate;
	private final ToIntFunction<E> damage;
	private final float speed;
	private final ToDoubleFunction<E> strengthMultiplierFactory;
	private Vec3d direction;
	private final Function<E, SoundEvent> soundFactory;

	public RamImpactTask(
		Function<E, UniformIntProvider> cooldownRangeFactory,
		TargetPredicate targetPredicate,
		ToIntFunction<E> damage,
		float speed,
		ToDoubleFunction<E> strengthMultiplierFactory,
		Function<E, SoundEvent> soundFactory
	) {
		super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryModuleState.VALUE_PRESENT), 200);
		this.cooldownRangeFactory = cooldownRangeFactory;
		this.targetPredicate = targetPredicate;
		this.damage = damage;
		this.speed = speed;
		this.strengthMultiplierFactory = strengthMultiplierFactory;
		this.soundFactory = soundFactory;
		this.direction = Vec3d.ZERO;
	}

	protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		return pathAwareEntity.getBrain().hasMemoryModule(MemoryModuleType.RAM_TARGET);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		return pathAwareEntity.getBrain().hasMemoryModule(MemoryModuleType.RAM_TARGET);
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		BlockPos blockPos = pathAwareEntity.getBlockPos();
		Brain<?> brain = pathAwareEntity.getBrain();
		Vec3d vec3d = (Vec3d)brain.getOptionalMemory(MemoryModuleType.RAM_TARGET).get();
		this.direction = new Vec3d((double)blockPos.getX() - vec3d.getX(), 0.0, (double)blockPos.getZ() - vec3d.getZ()).normalize();
		brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed, 0));
	}

	protected void keepRunning(ServerWorld serverWorld, E pathAwareEntity, long l) {
		List<LivingEntity> list = serverWorld.getTargets(LivingEntity.class, this.targetPredicate, pathAwareEntity, pathAwareEntity.getBoundingBox());
		Brain<?> brain = pathAwareEntity.getBrain();
		if (!list.isEmpty()) {
			LivingEntity livingEntity = (LivingEntity)list.get(0);
			livingEntity.damage(DamageSource.mob(pathAwareEntity).method_37353(), (float)this.damage.applyAsInt(pathAwareEntity));
			float f = livingEntity.blockedByShield(DamageSource.mob(pathAwareEntity)) ? 0.5F : 1.0F;
			float g = MathHelper.clamp(pathAwareEntity.getMovementSpeed() * 1.65F, 0.2F, 3.0F);
			livingEntity.takeKnockback((double)(f * g) * this.strengthMultiplierFactory.applyAsDouble(pathAwareEntity), this.direction.getX(), this.direction.getZ());
			this.finishRam(serverWorld, pathAwareEntity);
			serverWorld.playSoundFromEntity(null, pathAwareEntity, (SoundEvent)this.soundFactory.apply(pathAwareEntity), SoundCategory.HOSTILE, 1.0F, 1.0F);
		} else {
			Optional<WalkTarget> optional = brain.getOptionalMemory(MemoryModuleType.WALK_TARGET);
			Optional<Vec3d> optional2 = brain.getOptionalMemory(MemoryModuleType.RAM_TARGET);
			boolean bl = !optional.isPresent()
				|| !optional2.isPresent()
				|| ((WalkTarget)optional.get()).getLookTarget().getPos().distanceTo((Vec3d)optional2.get()) < 0.25;
			if (bl) {
				this.finishRam(serverWorld, pathAwareEntity);
			}
		}
	}

	protected void finishRam(ServerWorld world, E entity) {
		world.sendEntityStatus(entity, EntityStatuses.FINISH_RAM);
		entity.getBrain().remember(MemoryModuleType.RAM_COOLDOWN_TICKS, ((UniformIntProvider)this.cooldownRangeFactory.apply(entity)).get(world.random));
		entity.getBrain().forget(MemoryModuleType.RAM_TARGET);
	}
}
