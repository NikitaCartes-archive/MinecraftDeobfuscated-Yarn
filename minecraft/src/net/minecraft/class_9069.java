package net.minecraft;

import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class class_9069 extends AnimalEntity {
	public static final float field_47778 = 0.6F;
	private static final int field_47782 = 5;
	private static final int field_47783 = 8;
	public static final int field_47779 = 60;
	private static final double field_47784 = 7.0;
	private static final TrackedData<class_9069.class_9070> field_47785 = DataTracker.registerData(class_9069.class, TrackedDataHandlerRegistry.field_47707);
	private long field_47786 = 0L;
	public final AnimationState field_47780 = new AnimationState();
	public final AnimationState field_47781 = new AnimationState();
	private int field_47787;

	public class_9069(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		this.getNavigation().setCanSwim(true);
		this.field_47787 = this.method_55719();
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return EntityType.ARMADILLO.create(world);
	}

	public static DefaultAttributeContainer.Builder method_55722() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.14);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_47785, class_9069.class_9070.IDLE);
	}

	public boolean method_55723() {
		return this.dataTracker.get(field_47785) != class_9069.class_9070.IDLE;
	}

	public boolean method_55711() {
		class_9069.class_9070 lv = this.method_55718();
		return lv == class_9069.class_9070.SCARED || lv == class_9069.class_9070.ROLLING && this.field_47786 > 5L;
	}

	public boolean method_55714() {
		return this.method_55718() == class_9069.class_9070.ROLLING && this.field_47786 > 8L;
	}

	private class_9069.class_9070 method_55718() {
		return this.dataTracker.get(field_47785);
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	public void method_55713(class_9069.class_9070 arg) {
		this.dataTracker.set(field_47785, arg);
	}

	private void method_55725(boolean bl) {
		this.method_55713(bl ? class_9069.class_9070.ROLLING : class_9069.class_9070.IDLE);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_47785.equals(data)) {
			this.field_47786 = 0L;
		}

		super.onTrackedDataSet(data);
	}

	@Override
	protected Brain.Profile<class_9069> createBrainProfile() {
		return class_9071.method_55728();
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return class_9071.method_55731(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	protected void mobTick() {
		this.getWorld().getProfiler().push("armadilloBrain");
		((Brain<class_9069>)this.brain).tick((ServerWorld)this.getWorld(), this);
		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("armadilloActivityUpdate");
		class_9071.method_55734(this);
		this.getWorld().getProfiler().pop();
		if (this.isAlive() && !this.isBaby() && --this.field_47787 <= 0) {
			this.playSound(SoundEvents.ENTITY_ARMADILLO_SCUTE_DROP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			this.dropItem(Items.ARMADILLO_SCUTE);
			this.emitGameEvent(GameEvent.ENTITY_PLACE);
			this.field_47787 = this.method_55719();
		}

		super.mobTick();
	}

	private int method_55719() {
		return this.random.nextInt(20 * TimeHelper.field_47726 * 5) + 20 * TimeHelper.field_47726 * 5;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getWorld().isClient()) {
			this.method_55720();
		}

		this.field_47786++;
	}

	@Override
	public float getScaleFactor() {
		return this.isBaby() ? 0.6F : 1.0F;
	}

	private void method_55720() {
		switch (this.method_55718()) {
			case IDLE:
				this.field_47780.stop();
				this.field_47781.stop();
				break;
			case SCARED:
				this.field_47780.startIfNotRunning(this.age);
				this.field_47781.stop();
				break;
			case ROLLING:
				this.field_47780.stop();
				this.field_47781.startIfNotRunning(this.age);
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return class_9071.field_47796.test(stack);
	}

	public boolean method_55721(LivingEntity livingEntity) {
		if (!new Box(this.getPos(), this.getPos()).expand(7.0).contains(livingEntity.getPos())) {
			return false;
		} else if (livingEntity.getType().isIn(EntityTypeTags.UNDEAD)) {
			return true;
		} else {
			if (livingEntity instanceof PlayerEntity playerEntity && (playerEntity.isSprinting() || playerEntity.hasVehicle())) {
				return true;
			}

			return false;
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putString("state", this.method_55718().asString());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.method_55713(class_9069.class_9070.method_55727(nbt.getString("state")));
	}

	public void method_55715() {
		if (!this.method_55723()) {
			this.method_55695();
			this.resetLoveTicks();
			this.emitGameEvent(GameEvent.ENTITY_ACTION);
			this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ARMADILLO_ROLL, this.getSoundCategory(), 1.0F, 1.0F);
			this.method_55725(true);
		}
	}

	public void method_55724(boolean bl) {
		if (this.method_55723()) {
			this.emitGameEvent(GameEvent.ENTITY_ACTION);
			if (!bl) {
				this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ARMADILLO_UNROLL, this.getSoundCategory(), 1.0F, 1.0F);
			}

			this.method_55725(false);
		}
	}

	@Override
	protected void applyDamage(DamageSource source, float amount) {
		this.method_55724(true);
		super.applyDamage(source, amount);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (this.getWorld().isClient) {
			boolean bl = itemStack.isOf(Items.BRUSH);
			return bl ? ActionResult.CONSUME : ActionResult.PASS;
		} else if (itemStack.isOf(Items.BRUSH)) {
			if (!player.getAbilities().creativeMode) {
				itemStack.damage(16, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
			}

			this.method_55716();
			return ActionResult.SUCCESS;
		} else {
			return super.interactMob(player, hand);
		}
	}

	public void method_55716() {
		this.dropStack(new ItemStack(Items.ARMADILLO_SCUTE));
		this.emitGameEvent(GameEvent.ENTITY_INTERACT);
		this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ARMADILLO_BRUSH, this.getSoundCategory(), 1.0F, 1.0F);
	}

	public boolean method_55717() {
		return !this.isPanicking() && !this.isInFluid() && !this.isLeashed();
	}

	@Override
	public void lovePlayer(@Nullable PlayerEntity player) {
		super.lovePlayer(player);
		this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ARMADILLO_EAT, this.getSoundCategory(), 1.0F, 1.0F);
	}

	@Override
	public boolean canEat() {
		return super.canEat() && !this.method_55723();
	}

	@Override
	public SoundEvent getEatSound(ItemStack stack) {
		return SoundEvents.ENTITY_ARMADILLO_EAT;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.method_55723() ? null : SoundEvents.ENTITY_ARMADILLO_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ARMADILLO_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ARMADILLO_HURT;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_ARMADILLO_STEP, 0.15F, 1.0F);
	}

	@Override
	protected BodyControl createBodyControl() {
		return new BodyControl(this) {
			@Override
			public void tick() {
				if (!class_9069.this.method_55723()) {
					super.tick();
				}
			}
		};
	}

	public static enum class_9070 implements StringIdentifiable {
		IDLE("idle"),
		ROLLING("rolling"),
		SCARED("scared");

		private static StringIdentifiable.EnumCodec<class_9069.class_9070> field_47794 = StringIdentifiable.createCodec(class_9069.class_9070::values);
		final String field_47793;

		private class_9070(String string2) {
			this.field_47793 = string2;
		}

		public static class_9069.class_9070 method_55727(String string) {
			return (class_9069.class_9070)field_47794.byId(string, IDLE);
		}

		@Override
		public String asString() {
			return this.field_47793;
		}
	}
}
