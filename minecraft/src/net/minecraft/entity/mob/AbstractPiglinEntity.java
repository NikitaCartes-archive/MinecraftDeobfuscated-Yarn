package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public abstract class AbstractPiglinEntity extends HostileEntity {
	protected static final TrackedData<Boolean> IMMUNE_TO_ZOMBIFICATION = DataTracker.registerData(AbstractPiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final int TIME_TO_ZOMBIFY = 300;
	protected int timeInOverworld;

	public AbstractPiglinEntity(EntityType<? extends AbstractPiglinEntity> entityType, World world) {
		super(entityType, world);
		this.setCanPickUpLoot(true);
		this.setCanPathThroughDoors();
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
	}

	private void setCanPathThroughDoors() {
		if (NavigationConditions.hasMobNavigation(this)) {
			((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		}
	}

	protected abstract boolean canHunt();

	public void setImmuneToZombification(boolean immuneToZombification) {
		this.getDataTracker().set(IMMUNE_TO_ZOMBIFICATION, immuneToZombification);
	}

	protected boolean isImmuneToZombification() {
		return this.getDataTracker().get(IMMUNE_TO_ZOMBIFICATION);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(IMMUNE_TO_ZOMBIFICATION, false);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.isImmuneToZombification()) {
			nbt.putBoolean("IsImmuneToZombification", true);
		}

		nbt.putInt("TimeInOverworld", this.timeInOverworld);
	}

	@Override
	public double getHeightOffset() {
		return this.isBaby() ? -0.05 : -0.45;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setImmuneToZombification(nbt.getBoolean("IsImmuneToZombification"));
		this.timeInOverworld = nbt.getInt("TimeInOverworld");
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		if (this.shouldZombify()) {
			this.timeInOverworld++;
		} else {
			this.timeInOverworld = 0;
		}

		if (this.timeInOverworld > 300) {
			this.playZombificationSound();
			this.zombify((ServerWorld)this.world);
		}
	}

	public boolean shouldZombify() {
		return !this.world.getDimension().isPiglinSafe() && !this.isImmuneToZombification() && !this.isAiDisabled();
	}

	protected void zombify(ServerWorld world) {
		ZombifiedPiglinEntity zombifiedPiglinEntity = this.convertTo(EntityType.ZOMBIFIED_PIGLIN, true);
		if (zombifiedPiglinEntity != null) {
			zombifiedPiglinEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
		}
	}

	public boolean isAdult() {
		return !this.isBaby();
	}

	public abstract PiglinActivity getActivity();

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return (LivingEntity)this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
	}

	protected boolean isHoldingTool() {
		return this.getMainHandStack().getItem() instanceof ToolItem;
	}

	@Override
	public void playAmbientSound() {
		if (PiglinBrain.hasIdleActivity(this)) {
			super.playAmbientSound();
		}
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	protected abstract void playZombificationSound();
}
