package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;

public class BellBlockEntity extends BlockEntity implements Tickable {
	private long lastRingTime;
	public int ringTicks;
	public boolean ringing;
	public Direction lastSideHit;
	private List<LivingEntity> hearingEntities;
	private boolean resonating;
	private int resonateTime;

	public BellBlockEntity() {
		super(BlockEntityType.BELL);
	}

	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		if (type == 1) {
			this.notifyMemoriesOfBell();
			this.resonateTime = 0;
			this.lastSideHit = Direction.byId(data);
			this.ringTicks = 0;
			this.ringing = true;
			return true;
		} else {
			return super.onSyncedBlockEvent(type, data);
		}
	}

	@Override
	public void tick() {
		if (this.ringing) {
			this.ringTicks++;
		}

		if (this.ringTicks >= 50) {
			this.ringing = false;
			this.ringTicks = 0;
		}

		if (this.ringTicks >= 5 && this.resonateTime == 0 && this.raidersHearBell()) {
			this.resonating = true;
			this.playResonateSound();
		}

		if (this.resonating) {
			if (this.resonateTime < 40) {
				this.resonateTime++;
			} else {
				this.applyGlowToRaiders(this.world);
				this.applyParticlesToRaiders(this.world);
				this.resonating = false;
			}
		}
	}

	private void playResonateSound() {
		this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	public void activate(Direction direction) {
		BlockPos blockPos = this.getPos();
		this.lastSideHit = direction;
		if (this.ringing) {
			this.ringTicks = 0;
		} else {
			this.ringing = true;
		}

		this.world.addSyncedBlockEvent(blockPos, this.getCachedState().getBlock(), 1, direction.getId());
	}

	private void notifyMemoriesOfBell() {
		BlockPos blockPos = this.getPos();
		if (this.world.getTime() > this.lastRingTime + 60L || this.hearingEntities == null) {
			this.lastRingTime = this.world.getTime();
			Box box = new Box(blockPos).expand(48.0);
			this.hearingEntities = this.world.getNonSpectatingEntities(LivingEntity.class, box);
		}

		if (!this.world.isClient) {
			for (LivingEntity livingEntity : this.hearingEntities) {
				if (livingEntity.isAlive() && !livingEntity.removed && blockPos.isWithinDistance(livingEntity.getPos(), 32.0)) {
					livingEntity.getBrain().remember(MemoryModuleType.HEARD_BELL_TIME, this.world.getTime());
				}
			}
		}
	}

	private boolean raidersHearBell() {
		BlockPos blockPos = this.getPos();

		for (LivingEntity livingEntity : this.hearingEntities) {
			if (livingEntity.isAlive()
				&& !livingEntity.removed
				&& blockPos.isWithinDistance(livingEntity.getPos(), 32.0)
				&& livingEntity.getType().isIn(EntityTypeTags.RAIDERS)) {
				return true;
			}
		}

		return false;
	}

	private void applyGlowToRaiders(World world) {
		if (!world.isClient) {
			this.hearingEntities.stream().filter(this::isRaiderEntity).forEach(this::applyGlowToEntity);
		}
	}

	private void applyParticlesToRaiders(World world) {
		if (world.isClient) {
			BlockPos blockPos = this.getPos();
			MutableInt mutableInt = new MutableInt(16700985);
			int i = (int)this.hearingEntities.stream().filter(livingEntity -> blockPos.isWithinDistance(livingEntity.getPos(), 48.0)).count();
			this.hearingEntities
				.stream()
				.filter(this::isRaiderEntity)
				.forEach(
					livingEntity -> {
						float f = 1.0F;
						float g = MathHelper.sqrt(
							(livingEntity.getX() - (double)blockPos.getX()) * (livingEntity.getX() - (double)blockPos.getX())
								+ (livingEntity.getZ() - (double)blockPos.getZ()) * (livingEntity.getZ() - (double)blockPos.getZ())
						);
						double d = (double)((float)blockPos.getX() + 0.5F) + (double)(1.0F / g) * (livingEntity.getX() - (double)blockPos.getX());
						double e = (double)((float)blockPos.getZ() + 0.5F) + (double)(1.0F / g) * (livingEntity.getZ() - (double)blockPos.getZ());
						int j = MathHelper.clamp((i - 21) / -2, 3, 15);

						for (int k = 0; k < j; k++) {
							int l = mutableInt.addAndGet(5);
							double h = (double)BackgroundHelper.ColorMixer.getRed(l) / 255.0;
							double m = (double)BackgroundHelper.ColorMixer.getGreen(l) / 255.0;
							double n = (double)BackgroundHelper.ColorMixer.getBlue(l) / 255.0;
							world.addParticle(ParticleTypes.ENTITY_EFFECT, d, (double)((float)blockPos.getY() + 0.5F), e, h, m, n);
						}
					}
				);
		}
	}

	private boolean isRaiderEntity(LivingEntity entity) {
		return entity.isAlive() && !entity.removed && this.getPos().isWithinDistance(entity.getPos(), 48.0) && entity.getType().isIn(EntityTypeTags.RAIDERS);
	}

	private void applyGlowToEntity(LivingEntity entity) {
		entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60));
	}
}
