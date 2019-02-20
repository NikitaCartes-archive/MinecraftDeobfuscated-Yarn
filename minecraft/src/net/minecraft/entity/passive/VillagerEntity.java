package net.minecraft.entity.passive;

import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.class_1358;
import net.minecraft.class_1364;
import net.minecraft.class_1365;
import net.minecraft.class_1370;
import net.minecraft.class_1390;
import net.minecraft.class_1394;
import net.minecraft.class_4051;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.AcceptPoppyGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.entity.ai.goal.StayInsideGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.VillagerBreedGoal;
import net.minecraft.entity.ai.goal.VillagerBreedingGoal;
import net.minecraft.entity.ai.goal.VillagerFarmGoal;
import net.minecraft.entity.ai.goal.VillagerStareGoal;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidVictim;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TraderRecipe;
import net.minecraft.village.TraderRecipeList;
import net.minecraft.village.Trades;
import net.minecraft.village.VillageProperties;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = VillagerDataContainer.class
	)})
public class VillagerEntity extends AbstractTraderEntity implements RaidVictim, VillagerDataContainer {
	private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
	private static final class_4051 field_18133 = new class_4051().method_18418(16.0);
	private int findVillageCountdown;
	private int unlockTradeCountdown;
	private boolean unlockTrade;
	private String customerName;
	private boolean willingToMate;
	private boolean inMating;
	private boolean staring;
	private VillageProperties village;
	private boolean recentlyRescued;
	private boolean goalsSet;

	public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world) {
		this(entityType, world, VillagerType.field_17073);
	}

	public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world, VillagerType villagerType) {
		super(entityType, world);
		((EntityMobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		this.setCanPickUpLoot(true);
		this.setVillagerData(this.getVillagerData().withProfession(Registry.VILLAGER_PROFESSION.getRandom(this.random)));
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 0.6, 0.6));
		this.goalSelector.add(1, new FleeEntityGoal(this, EvokerEntity.class, 12.0F, 0.8, 0.8));
		this.goalSelector.add(1, new FleeEntityGoal(this, VindicatorEntity.class, 8.0F, 0.8, 0.8));
		this.goalSelector.add(1, new FleeEntityGoal(this, VexEntity.class, 8.0F, 0.6, 0.6));
		this.goalSelector.add(1, new FleeEntityGoal(this, PillagerEntity.class, 15.0F, 0.6, 0.6));
		this.goalSelector.add(1, new FleeEntityGoal(this, IllusionerEntity.class, 12.0F, 0.6, 0.6));
		this.goalSelector.add(1, new FleeEntityGoal(this, RavagerEntity.class, 12.0F, 0.8, 0.8));
		this.goalSelector.add(1, new class_1390(this));
		this.goalSelector.add(1, new class_1364(this));
		this.goalSelector.add(2, new class_1365(this));
		this.goalSelector.add(3, new StayInsideGoal(this));
		this.goalSelector.add(4, new OpenDoorGoal(this, true));
		this.goalSelector.add(5, new class_1370(this, 0.6));
		this.goalSelector.add(6, new VillagerBreedGoal(this));
		this.goalSelector.add(7, new AcceptPoppyGoal(this));
		this.goalSelector.add(9, new class_1358(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.add(9, new VillagerBreedingGoal(this));
		this.goalSelector.add(9, new class_1394(this, 0.6));
		this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
	}

	private void setSpecificGoals() {
		if (!this.goalsSet) {
			this.goalsSet = true;
			if (this.isChild()) {
				this.goalSelector.add(8, new VillagerStareGoal(this, 0.32));
			} else if (this.getVillagerData().getProfession() == VillagerProfession.field_17056) {
				this.goalSelector.add(6, new VillagerFarmGoal(this, 0.6));
			}
		}
	}

	@Override
	protected void method_5619() {
		if (this.getVillagerData().getProfession() == VillagerProfession.field_17056) {
			this.goalSelector.add(8, new VillagerFarmGoal(this, 0.6));
		}

		super.method_5619();
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
	}

	@Override
	protected void mobTick() {
		if (--this.findVillageCountdown <= 0) {
			BlockPos blockPos = new BlockPos(this);
			this.world.getVillageManager().addRecentVillagerPosition(blockPos);
			this.findVillageCountdown = 70 + this.random.nextInt(50);
			this.village = this.world.getVillageManager().getNearestVillage(blockPos, 32);
			if (this.village == null) {
				this.method_18409();
			} else {
				BlockPos blockPos2 = this.village.getCenter();
				this.method_18408(blockPos2, this.village.getRadius());
				if (this.recentlyRescued) {
					this.recentlyRescued = false;
					this.village.changeAllRatings(5);
				}
			}
		}

		if (!this.hasCustomer() && this.unlockTradeCountdown > 0) {
			this.unlockTradeCountdown--;
			if (this.unlockTradeCountdown <= 0) {
				if (this.unlockTrade) {
					for (TraderRecipe traderRecipe : this.getRecipes()) {
						if (traderRecipe.isDisabled()) {
							traderRecipe.increasedMaxUses(this.random.nextInt(6) + this.random.nextInt(6) + 2);
						}
					}

					this.levelUp();
					this.unlockTrade = false;
					if (this.village != null && this.customerName != null) {
						this.world.summonParticle(this, (byte)14);
						this.village.changeRating(this.customerName, 1);
					}
				}

				this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5924, 200, 0));
			}
		}

		super.mobTick();
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
				if (!this.world.isClient && !this.recipes.isEmpty()) {
					if (this.village != null && this.village.getRaid() != null && this.village.getRaid().isOnGoing()) {
						this.world.summonParticle(this, (byte)42);
					} else {
						this.setCurrentCustomer(playerEntity);
						this.sendRecipes(playerEntity, this.getDisplayName());
					}
				}

				return true;
			}
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(VILLAGER_DATA, new VillagerData(VillagerType.field_17073, VillagerProfession.field_17051, 1));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.put("VillagerData", this.getVillagerData().serialize(NbtOps.INSTANCE));
		compoundTag.putBoolean("Willing", this.willingToMate);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("VillagerData", 10)) {
			this.setVillagerData(new VillagerData(new Dynamic<>(NbtOps.INSTANCE, compoundTag.getTag("VillagerData"))));
		}

		if (compoundTag.containsKey("Offers", 10)) {
			this.recipes = new TraderRecipeList(compoundTag.getCompound("Offers"));
		}

		this.willingToMate = compoundTag.getBoolean("Willing");
		this.setCanPickUpLoot(true);
		this.setSpecificGoals();
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.hasCustomer() ? SoundEvents.field_14933 : SoundEvents.field_15175;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15139;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15225;
	}

	public void setVillagerData(VillagerData villagerData) {
		VillagerData villagerData2 = this.getVillagerData();
		if (villagerData2.getProfession() != villagerData.getProfession()) {
			this.recipes = null;
		}

		this.dataTracker.set(VILLAGER_DATA, villagerData);
	}

	@Override
	public VillagerData getVillagerData() {
		return this.dataTracker.get(VILLAGER_DATA);
	}

	@Override
	protected void afterUsing(TraderRecipe traderRecipe) {
		int i = 3 + this.random.nextInt(4);
		if (traderRecipe.getUses() == 1 || this.random.nextInt(5) == 0) {
			this.unlockTradeCountdown = 40;
			this.unlockTrade = true;
			this.willingToMate = true;
			this.customerName = this.getCurrentCustomer() == null ? null : this.getCurrentCustomer().getGameProfile().getName();
			i += 5;
		}

		if (traderRecipe.getRewardExp()) {
			this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y + 0.5, this.z, i));
		}
	}

	public boolean isInMating() {
		return this.inMating;
	}

	public void setInMating(boolean bl) {
		this.inMating = bl;
	}

	public void setStaring(boolean bl) {
		this.staring = bl;
	}

	public boolean isStaring() {
		return this.staring;
	}

	@Nullable
	@Override
	public VillageProperties getVillage() {
		return this.village;
	}

	@Override
	public void setAttacker(@Nullable LivingEntity livingEntity) {
		super.setAttacker(livingEntity);
		if (this.village != null && livingEntity != null) {
			this.village.addAttacker(livingEntity);
			if (livingEntity instanceof PlayerEntity) {
				int i = -1;
				if (this.isChild()) {
					i = -3;
				}

				this.village.changeRating(((PlayerEntity)livingEntity).getGameProfile().getName(), i);
				if (this.isValid()) {
					this.world.summonParticle(this, (byte)13);
				}
			}
		}
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		if (this.village != null) {
			Entity entity = damageSource.getAttacker();
			if (entity != null) {
				if (entity instanceof PlayerEntity) {
					this.village.changeRating(((PlayerEntity)entity).getGameProfile().getName(), -2);
				} else if (entity instanceof Monster) {
					this.village.onVillagerDeath();
				}
			} else {
				PlayerEntity playerEntity = this.world.method_18462(field_18133, this);
				if (playerEntity != null) {
					this.village.onVillagerDeath();
				}
			}
		}

		super.onDeath(damageSource);
	}

	public boolean isWillingToMate(boolean bl) {
		if (!this.willingToMate && bl && this.hasFoodForWilling()) {
			boolean bl2 = false;

			for (int i = 0; i < this.getInventory().getInvSize(); i++) {
				ItemStack itemStack = this.getInventory().getInvStack(i);
				if (!itemStack.isEmpty()) {
					if (itemStack.getItem() == Items.field_8229 && itemStack.getAmount() >= 3) {
						bl2 = true;
						this.getInventory().takeInvStack(i, 3);
					} else if ((itemStack.getItem() == Items.field_8567 || itemStack.getItem() == Items.field_8179) && itemStack.getAmount() >= 12) {
						bl2 = true;
						this.getInventory().takeInvStack(i, 12);
					}
				}

				if (bl2) {
					this.world.summonParticle(this, (byte)18);
					this.willingToMate = true;
					break;
				}
			}
		}

		return this.willingToMate;
	}

	public void setWillingToMate(boolean bl) {
		this.willingToMate = bl;
	}

	public void setRecipes(TraderRecipeList traderRecipeList) {
		this.recipes = traderRecipeList;
	}

	private void levelUp() {
		this.setVillagerData(this.getVillagerData().withLevel(this.getVillagerData().getLevel() + 1));
		this.fillRecipes();
	}

	@Override
	public TextComponent getDisplayName() {
		AbstractScoreboardTeam abstractScoreboardTeam = this.getScoreboardTeam();
		TextComponent textComponent = this.getCustomName();
		if (textComponent != null) {
			return ScoreboardTeam.method_1142(abstractScoreboardTeam, textComponent)
				.modifyStyle(style -> style.setHoverEvent(this.getComponentHoverEvent()).setInsertion(this.getUuidAsString()));
		} else {
			VillagerProfession villagerProfession = this.getVillagerData().getProfession();
			TextComponent textComponent2 = new TranslatableTextComponent(
					this.getType().getTranslationKey() + '.' + Registry.VILLAGER_PROFESSION.getId(villagerProfession).getPath()
				)
				.modifyStyle(style -> style.setHoverEvent(this.getComponentHoverEvent()).setInsertion(this.getUuidAsString()));
			if (abstractScoreboardTeam != null) {
				textComponent2.applyFormat(abstractScoreboardTeam.getColor());
			}

			return textComponent2;
		}
	}

	@Nullable
	@Override
	public Raid getRaid() {
		return this.village != null ? this.village.getRaid() : null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 12) {
			this.method_18007(ParticleTypes.field_11201);
		} else if (b == 13) {
			this.method_18007(ParticleTypes.field_11231);
		} else if (b == 14) {
			this.method_18007(ParticleTypes.field_11211);
		} else if (b == 42) {
			this.method_18007(ParticleTypes.field_11202);
		} else {
			super.method_5711(b);
		}
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.setSpecificGoals();
		if (spawnType == SpawnType.field_16466) {
			this.setVillagerData(this.getVillagerData().withProfession(Registry.VILLAGER_PROFESSION.getRandom(iWorld.getRandom())));
		}

		if (spawnType == SpawnType.field_16462 || spawnType == SpawnType.field_16465 || spawnType == SpawnType.field_16469) {
			this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(iWorld.getBiome(new BlockPos(this)))));
		}

		return super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	public void setRecentlyRescued() {
		this.recentlyRescued = true;
	}

	public VillagerEntity method_7225(PassiveEntity passiveEntity) {
		double d = this.random.nextDouble();
		VillagerType villagerType;
		if (d < 0.5) {
			villagerType = VillagerType.forBiome(this.world.getBiome(new BlockPos(this)));
		} else if (d < 0.75) {
			villagerType = this.getVillagerData().getType();
		} else {
			villagerType = ((VillagerEntity)passiveEntity).getVillagerData().getType();
		}

		VillagerEntity villagerEntity = new VillagerEntity(EntityType.VILLAGER, this.world, villagerType);
		villagerEntity.prepareEntityData(this.world, this.world.getLocalDifficulty(new BlockPos(villagerEntity)), SpawnType.field_16466, null, null);
		return villagerEntity;
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
		if (!this.world.isClient && !this.invalid) {
			WitchEntity witchEntity = EntityType.WITCH.create(this.world);
			witchEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
			witchEntity.prepareEntityData(this.world, this.world.getLocalDifficulty(new BlockPos(witchEntity)), SpawnType.field_16468, null, null);
			witchEntity.setAiDisabled(this.isAiDisabled());
			if (this.hasCustomName()) {
				witchEntity.setCustomName(this.getCustomName());
				witchEntity.setCustomNameVisible(this.isCustomNameVisible());
			}

			this.world.spawnEntity(witchEntity);
			this.invalidate();
		}
	}

	@Override
	protected void pickupItem(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();
		Item item = itemStack.getItem();
		if (this.canPickUp(item)) {
			ItemStack itemStack2 = this.getInventory().add(itemStack);
			if (itemStack2.isEmpty()) {
				itemEntity.invalidate();
			} else {
				itemStack.setAmount(itemStack2.getAmount());
			}
		}
	}

	private boolean canPickUp(Item item) {
		return item == Items.field_8229
			|| item == Items.field_8567
			|| item == Items.field_8179
			|| item == Items.field_8861
			|| item == Items.field_8317
			|| item == Items.field_8186
			|| item == Items.field_8309;
	}

	public boolean hasFoodForWilling() {
		return this.hasEnoughFood(1);
	}

	public boolean wantsToStartBreeding() {
		return this.hasEnoughFood(2);
	}

	public boolean canBreed() {
		boolean bl = this.getVillagerData().getProfession() == VillagerProfession.field_17056;
		return bl ? !this.hasEnoughFood(5) : !this.hasEnoughFood(1);
	}

	private boolean hasEnoughFood(int i) {
		boolean bl = this.getVillagerData().getProfession() == VillagerProfession.field_17056;

		for (int j = 0; j < this.getInventory().getInvSize(); j++) {
			ItemStack itemStack = this.getInventory().getInvStack(j);
			Item item = itemStack.getItem();
			int k = itemStack.getAmount();
			if (item == Items.field_8229 && k >= 3 * i
				|| item == Items.field_8567 && k >= 12 * i
				|| item == Items.field_8179 && k >= 12 * i
				|| item == Items.field_8186 && k >= 12 * i) {
				return true;
			}

			if (bl && item == Items.field_8861 && k >= 9 * i) {
				return true;
			}
		}

		return false;
	}

	public boolean hasSeed() {
		for (int i = 0; i < this.getInventory().getInvSize(); i++) {
			Item item = this.getInventory().getInvStack(i).getItem();
			if (item == Items.field_8317 || item == Items.field_8567 || item == Items.field_8179 || item == Items.field_8309) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void fillRecipes() {
		VillagerData villagerData = this.getVillagerData();
		Int2ObjectMap<Trades.Factory[]> int2ObjectMap = (Int2ObjectMap<Trades.Factory[]>)Trades.PROFESSION_TO_LEVELED_TRADE.get(villagerData.getProfession());
		if (int2ObjectMap != null && !int2ObjectMap.isEmpty()) {
			Trades.Factory[] factorys = int2ObjectMap.get(villagerData.getLevel());
			if (factorys != null) {
				TraderRecipeList traderRecipeList = this.getRecipes();

				for (Trades.Factory factory : factorys) {
					TraderRecipe traderRecipe = factory.create(this, this.random);
					if (traderRecipe != null) {
						traderRecipeList.add(traderRecipe);
					}
				}
			}
		}
	}
}
