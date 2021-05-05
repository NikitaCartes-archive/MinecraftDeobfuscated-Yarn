package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
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

public class RamTask<E extends PathAwareEntity> extends Task<E> {
	public static final int field_33474 = 200;
	public static final float field_33475 = 1.65F;
	private final Function<E, UniformIntProvider> field_33476;
	private final TargetPredicate field_33477;
	private final ToIntFunction<E> field_33478;
	private final float field_33479;
	private final ToDoubleFunction<E> field_33480;
	private Vec3d field_33481;
	private final Function<E, SoundEvent> field_33482;

	public RamTask(
		Function<E, UniformIntProvider> function,
		TargetPredicate targetPredicate,
		ToIntFunction<E> toIntFunction,
		float f,
		ToDoubleFunction<E> toDoubleFunction,
		Function<E, SoundEvent> function2
	) {
		super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryModuleState.VALUE_PRESENT), 200);
		this.field_33476 = function;
		this.field_33477 = targetPredicate;
		this.field_33478 = toIntFunction;
		this.field_33479 = f;
		this.field_33480 = toDoubleFunction;
		this.field_33482 = function2;
		this.field_33481 = Vec3d.ZERO;
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
		this.field_33481 = new Vec3d((double)blockPos.getX() - vec3d.getX(), 0.0, (double)blockPos.getZ() - vec3d.getZ()).normalize();
		brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.field_33479, 0));
	}

	protected void keepRunning(ServerWorld serverWorld, E pathAwareEntity, long l) {
		List<LivingEntity> list = serverWorld.getTargets(LivingEntity.class, this.field_33477, pathAwareEntity, pathAwareEntity.getBoundingBox());
		Brain<?> brain = pathAwareEntity.getBrain();
		if (!list.isEmpty()) {
			LivingEntity livingEntity = (LivingEntity)list.get(0);
			livingEntity.damage(DamageSource.mob(pathAwareEntity), (float)this.field_33478.applyAsInt(pathAwareEntity));
			float f = livingEntity.blockedByShield(DamageSource.mob(pathAwareEntity)) ? 0.5F : 1.0F;
			float g = MathHelper.clamp(pathAwareEntity.getMovementSpeed() * 1.65F, 0.2F, 3.0F);
			livingEntity.takeKnockback((double)(f * g) * this.field_33480.applyAsDouble(pathAwareEntity), this.field_33481.getX(), this.field_33481.getZ());
			this.method_36279(serverWorld, pathAwareEntity);
			serverWorld.playSoundFromEntity(null, pathAwareEntity, (SoundEvent)this.field_33482.apply(pathAwareEntity), SoundCategory.HOSTILE, 1.0F, 1.0F);
		} else {
			Optional<WalkTarget> optional = brain.getOptionalMemory(MemoryModuleType.WALK_TARGET);
			Optional<Vec3d> optional2 = brain.getOptionalMemory(MemoryModuleType.RAM_TARGET);
			boolean bl = !optional.isPresent()
				|| !optional2.isPresent()
				|| ((WalkTarget)optional.get()).getLookTarget().getPos().distanceTo((Vec3d)optional2.get()) < 0.25;
			if (bl) {
				this.method_36279(serverWorld, pathAwareEntity);
			}
		}
	}

	protected void method_36279(ServerWorld serverWorld, E pathAwareEntity) {
		serverWorld.sendEntityStatus(pathAwareEntity, (byte)59);
		pathAwareEntity.getBrain()
			.remember(MemoryModuleType.RAM_COOLDOWN_TICKS, ((UniformIntProvider)this.field_33476.apply(pathAwareEntity)).get(serverWorld.random));
		pathAwareEntity.getBrain().forget(MemoryModuleType.RAM_TARGET);
	}
}
