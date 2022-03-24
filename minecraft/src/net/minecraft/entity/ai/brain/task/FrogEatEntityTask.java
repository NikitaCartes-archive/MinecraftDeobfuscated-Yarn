package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class FrogEatEntityTask extends Task<FrogEntity> {
	public static final int field_37479 = 100;
	public static final int field_37480 = 6;
	private static final float field_37481 = 1.75F;
	private static final float field_37482 = 0.75F;
	private int field_37483;
	private int field_37484;
	private final SoundEvent tongueSound;
	private final SoundEvent eatSound;
	private Vec3d targetPos;
	private boolean field_37488;
	private FrogEatEntityTask.Phase phase = FrogEatEntityTask.Phase.DONE;

	public FrogEatEntityTask(SoundEvent tongueSound, SoundEvent eatSound) {
		super(
			ImmutableMap.of(
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_PRESENT
			),
			100
		);
		this.tongueSound = tongueSound;
		this.eatSound = eatSound;
	}

	protected boolean shouldRun(ServerWorld serverWorld, FrogEntity frogEntity) {
		return super.shouldRun(serverWorld, frogEntity)
			&& FrogEntity.isValidFrogTarget((LivingEntity)frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get());
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
		return frogEntity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET) && this.phase != FrogEatEntityTask.Phase.DONE;
	}

	protected void run(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
		LivingEntity livingEntity = (LivingEntity)frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
		LookTargetUtil.lookAt(frogEntity, livingEntity);
		frogEntity.setFrogTarget(livingEntity);
		frogEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.getPos(), 2.0F, 0));
		this.field_37484 = 10;
		this.phase = FrogEatEntityTask.Phase.MOVE_TO_TARGET;
	}

	protected void finishRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
		frogEntity.setPose(EntityPose.STANDING);
		serverWorld.playSoundFromEntity(null, frogEntity, this.eatSound, SoundCategory.NEUTRAL, 2.0F, 1.0F);
		Optional<Entity> optional = frogEntity.getFrogTarget();
		if (optional.isPresent()) {
			Entity entity = (Entity)optional.get();
			if (this.field_37488 && entity.isAlive()) {
				entity.remove(Entity.RemovalReason.KILLED);
				ItemStack itemStack = createDroppedStack(frogEntity, entity);
				serverWorld.spawnEntity(new ItemEntity(serverWorld, this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), itemStack));
			}
		}

		frogEntity.clearFrogTarget();
		this.field_37488 = false;
	}

	private static ItemStack createDroppedStack(FrogEntity frog, Entity eatenEntity) {
		if (eatenEntity instanceof MagmaCubeEntity) {
			return new ItemStack(switch (frog.getVariant()) {
				case TEMPERATE -> Items.OCHRE_FROGLIGHT;
				case WARM -> Items.PEARLESCENT_FROGLIGHT;
				case COLD -> Items.VERDANT_FROGLIGHT;
			});
		} else {
			return new ItemStack(Items.SLIME_BALL);
		}
	}

	protected void keepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
		LivingEntity livingEntity = (LivingEntity)frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
		frogEntity.setFrogTarget(livingEntity);
		switch (this.phase) {
			case MOVE_TO_TARGET:
				if (livingEntity.distanceTo(frogEntity) < 1.75F) {
					serverWorld.playSoundFromEntity(null, frogEntity, this.tongueSound, SoundCategory.NEUTRAL, 2.0F, 1.0F);
					frogEntity.setPose(EntityPose.USING_TONGUE);
					livingEntity.setVelocity(livingEntity.getPos().relativize(frogEntity.getPos()).normalize().multiply(0.75));
					this.targetPos = livingEntity.getPos();
					this.field_37483 = 6;
					this.phase = FrogEatEntityTask.Phase.EAT_ANIMATION;
				} else if (this.field_37484 <= 0) {
					frogEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.getPos(), 2.0F, 0));
					this.field_37484 = 10;
				} else {
					this.field_37484--;
				}
				break;
			case EAT_ANIMATION:
				if (this.field_37483 <= 0) {
					this.field_37488 = true;
					this.phase = FrogEatEntityTask.Phase.DONE;
				} else {
					this.field_37483--;
				}
			case DONE:
		}
	}

	static enum Phase {
		MOVE_TO_TARGET,
		EAT_ANIMATION,
		DONE;
	}
}
