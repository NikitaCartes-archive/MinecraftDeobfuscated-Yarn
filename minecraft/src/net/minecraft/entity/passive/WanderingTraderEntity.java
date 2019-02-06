package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_1358;
import net.minecraft.class_1364;
import net.minecraft.class_1390;
import net.minecraft.class_1394;
import net.minecraft.class_3993;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
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
import net.minecraft.village.VillagerRecipe;
import net.minecraft.village.VillagerRecipeList;
import net.minecraft.village.VillagerTrades;
import net.minecraft.world.World;

public class WanderingTraderEntity extends AbstractVillagerEntity {
	@Nullable
	private BlockPos field_17758;
	private int field_17725;

	public WanderingTraderEntity(World world) {
		super(EntityType.field_17713, world);
		this.teleporting = true;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector
			.add(
				0,
				new class_3993<>(
					this,
					PotionUtil.setPotion(new ItemStack(Items.field_8574), Potions.field_8997),
					wanderingTraderEntity -> !this.world.isDaylight() && !wanderingTraderEntity.isInvisible()
				)
			);
		this.goalSelector
			.add(0, new class_3993<>(this, new ItemStack(Items.field_8103), wanderingTraderEntity -> this.world.isDaylight() && wanderingTraderEntity.isInvisible()));
		this.goalSelector.add(1, new class_1390(this));
		this.goalSelector.add(1, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 0.5, 0.5));
		this.goalSelector.add(1, new FleeEntityGoal(this, EvokerEntity.class, 12.0F, 0.5, 0.5));
		this.goalSelector.add(1, new FleeEntityGoal(this, VindicatorEntity.class, 8.0F, 0.5, 0.5));
		this.goalSelector.add(1, new FleeEntityGoal(this, VexEntity.class, 8.0F, 0.5, 0.5));
		this.goalSelector.add(1, new FleeEntityGoal(this, PillagerEntity.class, 15.0F, 0.5, 0.5));
		this.goalSelector.add(1, new FleeEntityGoal(this, IllusionerEntity.class, 12.0F, 0.5, 0.5));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 0.5));
		this.goalSelector.add(1, new class_1364(this));
		this.goalSelector.add(2, new WanderingTraderEntity.class_3994(this, 2.0, 0.35));
		this.goalSelector.add(8, new class_1394(this, 0.35));
		this.goalSelector.add(9, new class_1358(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return null;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		boolean bl = itemStack.getItem() == Items.field_8448;
		if (bl) {
			itemStack.interactWithEntity(playerEntity, this, hand);
			return true;
		} else if (itemStack.getItem() != Items.field_8086 && this.isValid() && !this.hasCustomer() && !this.isChild()) {
			if (hand == Hand.MAIN) {
				playerEntity.increaseStat(Stats.field_15384);
			}

			if (this.getRecipes().isEmpty()) {
				return super.interactMob(playerEntity, hand);
			} else {
				if (!this.world.isClient) {
					this.setCurrentCustomer(playerEntity);
					this.method_17449(playerEntity, this.getDisplayName());
				}

				return true;
			}
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	@Override
	protected void method_7237() {
		VillagerTrades.Factory[] factorys = VillagerTrades.WANDERING_TRADER_TRADES.get(1);
		VillagerTrades.Factory[] factorys2 = VillagerTrades.WANDERING_TRADER_TRADES.get(2);
		if (factorys != null && factorys2 != null) {
			Set<Integer> set = Sets.<Integer>newHashSet();
			if (factorys.length > 5) {
				while (set.size() < 5) {
					set.add(this.random.nextInt(factorys.length));
				}
			} else {
				for (int i = 0; i < factorys.length; i++) {
					set.add(i);
				}
			}

			VillagerRecipeList villagerRecipeList = this.getRecipes();

			for (Integer integer : set) {
				VillagerTrades.Factory factory = factorys[integer];
				VillagerRecipe villagerRecipe = factory.create(this, this.random);
				if (villagerRecipe != null) {
					villagerRecipeList.add(villagerRecipe);
				}
			}

			int j = this.random.nextInt(factorys2.length);
			VillagerTrades.Factory factory2 = factorys2[j];
			VillagerRecipe villagerRecipe2 = factory2.create(this, this.random);
			if (villagerRecipe2 != null) {
				villagerRecipeList.add(villagerRecipe2);
			}
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("DespawnDelay", this.field_17725);
		if (this.field_17758 != null) {
			compoundTag.put("WanderTarget", TagHelper.serializeBlockPos(this.field_17758));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("DespawnDelay", 99)) {
			this.field_17725 = compoundTag.getInt("DespawnDelay");
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
	protected void method_18008(VillagerRecipe villagerRecipe) {
		if (villagerRecipe.getRewardExp()) {
			int i = 3 + this.random.nextInt(4);
			this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y + 0.5, this.z, i));
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.hasCustomer() ? SoundEvents.field_17751 : SoundEvents.field_17747;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_17749;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_17748;
	}

	@Override
	protected SoundEvent method_18012(boolean bl) {
		return bl ? SoundEvents.field_17752 : SoundEvents.field_17750;
	}

	@Override
	protected SoundEvent method_18010() {
		return SoundEvents.field_17752;
	}

	public void method_18013(int i) {
		this.field_17725 = i;
	}

	public int method_18014() {
		return this.field_17725;
	}

	@Override
	public void update() {
		super.update();
		if (this.field_17725 > 0 && !this.hasCustomer() && --this.field_17725 == 0) {
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

	class class_3994 extends Goal {
		final WanderingTraderEntity field_17759;
		final double field_17760;
		final double field_17761;

		class_3994(WanderingTraderEntity wanderingTraderEntity2, double d, double e) {
			this.field_17759 = wanderingTraderEntity2;
			this.field_17760 = d;
			this.field_17761 = e;
			this.setControlBits(1);
		}

		@Override
		public void onRemove() {
			this.field_17759.method_18069(null);
			WanderingTraderEntity.this.navigation.stop();
		}

		@Override
		public boolean canStart() {
			BlockPos blockPos = this.field_17759.method_18065();
			return blockPos != null && this.method_18070(blockPos, this.field_17760);
		}

		@Override
		public void tick() {
			BlockPos blockPos = this.field_17759.method_18065();
			if (blockPos != null && WanderingTraderEntity.this.navigation.isIdle()) {
				if (this.method_18070(blockPos, 10.0)) {
					Vec3d vec3d = new Vec3d(
							(double)blockPos.getX() - this.field_17759.x, (double)blockPos.getY() - this.field_17759.y, (double)blockPos.getZ() - this.field_17759.z
						)
						.normalize();
					Vec3d vec3d2 = vec3d.multiply(10.0).add(this.field_17759.x, this.field_17759.y, this.field_17759.z);
					WanderingTraderEntity.this.navigation.startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.field_17761);
				} else {
					WanderingTraderEntity.this.navigation.startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), this.field_17761);
				}
			}
		}

		private boolean method_18070(BlockPos blockPos, double d) {
			return this.field_17759.squaredDistanceTo(blockPos) > d * d;
		}
	}
}
