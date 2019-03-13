package net.minecraft.entity.passive;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.class_1358;
import net.minecraft.class_1390;
import net.minecraft.class_1394;
import net.minecraft.class_3993;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtCustomerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TraderRecipe;
import net.minecraft.village.TraderRecipeList;
import net.minecraft.village.Trades;
import net.minecraft.world.World;

public class WanderingTraderEntity extends AbstractTraderEntity {
	@Nullable
	private BlockPos field_17758;
	private int despawnDelay;

	public WanderingTraderEntity(EntityType<? extends WanderingTraderEntity> entityType, World world) {
		super(entityType, world);
		this.teleporting = true;
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(0, new SwimGoal(this));
		this.field_6201
			.add(
				0,
				new class_3993<>(
					this,
					PotionUtil.setPotion(new ItemStack(Items.field_8574), Potions.field_8997),
					SoundEvents.field_18315,
					wanderingTraderEntity -> !this.field_6002.isDaylight() && !wanderingTraderEntity.isInvisible()
				)
			);
		this.field_6201
			.add(
				0,
				new class_3993<>(
					this,
					new ItemStack(Items.field_8103),
					SoundEvents.field_18314,
					wanderingTraderEntity -> this.field_6002.isDaylight() && wanderingTraderEntity.isInvisible()
				)
			);
		this.field_6201.add(1, new class_1390(this));
		this.field_6201.add(1, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 0.5, 0.5));
		this.field_6201.add(1, new FleeEntityGoal(this, EvokerEntity.class, 12.0F, 0.5, 0.5));
		this.field_6201.add(1, new FleeEntityGoal(this, VindicatorEntity.class, 8.0F, 0.5, 0.5));
		this.field_6201.add(1, new FleeEntityGoal(this, VexEntity.class, 8.0F, 0.5, 0.5));
		this.field_6201.add(1, new FleeEntityGoal(this, PillagerEntity.class, 15.0F, 0.5, 0.5));
		this.field_6201.add(1, new FleeEntityGoal(this, IllusionerEntity.class, 12.0F, 0.5, 0.5));
		this.field_6201.add(1, new EscapeDangerGoal(this, 0.5));
		this.field_6201.add(1, new LookAtCustomerGoal(this));
		this.field_6201.add(2, new WanderingTraderEntity.WanderToTargetGoal(this, 2.0, 0.35));
		this.field_6201.add(8, new class_1394(this, 0.35));
		this.field_6201.add(9, new class_1358(this, PlayerEntity.class, 3.0F, 1.0F));
		this.field_6201.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return null;
	}

	@Override
	public boolean method_19270() {
		return false;
	}

	@Override
	public boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		boolean bl = itemStack.getItem() == Items.field_8448;
		if (bl) {
			itemStack.interactWithEntity(playerEntity, this, hand);
			return true;
		} else if (itemStack.getItem() != Items.field_8086 && this.isValid() && !this.hasCustomer() && !this.isChild()) {
			if (hand == Hand.MAIN) {
				playerEntity.method_7281(Stats.field_15384);
			}

			if (this.method_8264().isEmpty()) {
				return super.method_5992(playerEntity, hand);
			} else {
				if (!this.field_6002.isClient) {
					this.setCurrentCustomer(playerEntity);
					this.method_17449(playerEntity, this.method_5476(), 1);
				}

				return true;
			}
		} else {
			return super.method_5992(playerEntity, hand);
		}
	}

	@Override
	protected void fillRecipes() {
		Trades.Factory[] factorys = Trades.WANDERING_TRADER_TRADES.get(1);
		Trades.Factory[] factorys2 = Trades.WANDERING_TRADER_TRADES.get(2);
		if (factorys != null && factorys2 != null) {
			TraderRecipeList traderRecipeList = this.method_8264();
			this.method_19170(traderRecipeList, factorys, 5);
			int i = this.random.nextInt(factorys2.length);
			Trades.Factory factory = factorys2[i];
			TraderRecipe traderRecipe = factory.method_7246(this, this.random);
			if (traderRecipe != null) {
				traderRecipeList.add(traderRecipe);
			}
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("DespawnDelay", this.despawnDelay);
		if (this.field_17758 != null) {
			compoundTag.method_10566("WanderTarget", TagHelper.serializeBlockPos(this.field_17758));
		}
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("DespawnDelay", 99)) {
			this.despawnDelay = compoundTag.getInt("DespawnDelay");
		}

		if (compoundTag.containsKey("WanderTarget")) {
			this.field_17758 = TagHelper.deserializeBlockPos(compoundTag.getCompound("WanderTarget"));
		}
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return false;
	}

	@Override
	protected void method_18008(TraderRecipe traderRecipe) {
		if (traderRecipe.getRewardExp()) {
			int i = 3 + this.random.nextInt(4);
			this.field_6002.spawnEntity(new ExperienceOrbEntity(this.field_6002, this.x, this.y + 0.5, this.z, i));
		}
	}

	@Override
	protected SoundEvent method_5994() {
		return this.hasCustomer() ? SoundEvents.field_17751 : SoundEvents.field_17747;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_17749;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_17748;
	}

	@Override
	protected SoundEvent method_18807(ItemStack itemStack) {
		Item item = itemStack.getItem();
		return item == Items.field_8103 ? SoundEvents.field_18316 : SoundEvents.field_18313;
	}

	@Override
	protected SoundEvent method_18012(boolean bl) {
		return bl ? SoundEvents.field_17752 : SoundEvents.field_17750;
	}

	@Override
	protected SoundEvent method_18010() {
		return SoundEvents.field_17752;
	}

	public void setDespawnDelay(int i) {
		this.despawnDelay = i;
	}

	public int getDespawnDelay() {
		return this.despawnDelay;
	}

	@Override
	public void update() {
		super.update();
		if (this.despawnDelay > 0 && !this.hasCustomer() && --this.despawnDelay == 0) {
			this.invalidate();
		}
	}

	public void method_18069(@Nullable BlockPos blockPos) {
		this.field_17758 = blockPos;
	}

	@Nullable
	private BlockPos method_18065() {
		return this.field_17758;
	}

	class WanderToTargetGoal extends Goal {
		final WanderingTraderEntity field_17759;
		final double proximityDistance;
		final double speed;

		WanderToTargetGoal(WanderingTraderEntity wanderingTraderEntity2, double d, double e) {
			this.field_17759 = wanderingTraderEntity2;
			this.proximityDistance = d;
			this.speed = e;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
		}

		@Override
		public void onRemove() {
			this.field_17759.method_18069(null);
			WanderingTraderEntity.this.field_6189.stop();
		}

		@Override
		public boolean canStart() {
			BlockPos blockPos = this.field_17759.method_18065();
			return blockPos != null && this.method_18070(blockPos, this.proximityDistance);
		}

		@Override
		public void tick() {
			BlockPos blockPos = this.field_17759.method_18065();
			if (blockPos != null && WanderingTraderEntity.this.field_6189.isIdle()) {
				if (this.method_18070(blockPos, 10.0)) {
					Vec3d vec3d = new Vec3d(
							(double)blockPos.getX() - this.field_17759.x, (double)blockPos.getY() - this.field_17759.y, (double)blockPos.getZ() - this.field_17759.z
						)
						.normalize();
					Vec3d vec3d2 = vec3d.multiply(10.0).add(this.field_17759.x, this.field_17759.y, this.field_17759.z);
					WanderingTraderEntity.this.field_6189.startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
				} else {
					WanderingTraderEntity.this.field_6189.startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), this.speed);
				}
			}
		}

		private boolean method_18070(BlockPos blockPos, double d) {
			return this.field_17759.method_5831(blockPos) > d * d;
		}
	}
}
