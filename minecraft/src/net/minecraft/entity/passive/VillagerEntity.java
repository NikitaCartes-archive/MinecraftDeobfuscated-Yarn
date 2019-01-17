package net.minecraft.entity.passive;

import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.class_1358;
import net.minecraft.class_1361;
import net.minecraft.class_1364;
import net.minecraft.class_1365;
import net.minecraft.class_1370;
import net.minecraft.class_1390;
import net.minecraft.class_1394;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Npc;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.AcceptPoppyGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.entity.ai.goal.StayInsideGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.VillagerBreedGoal;
import net.minecraft.entity.ai.goal.VillagerFarmGoal;
import net.minecraft.entity.ai.goal.VillagerInteractionGoal;
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
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidVictim;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillageProperties;
import net.minecraft.village.Villager;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerRecipe;
import net.minecraft.village.VillagerRecipeList;
import net.minecraft.village.VillagerTrades;
import net.minecraft.village.VillagerType;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = VillagerDataContainer.class
	)})
public class VillagerEntity extends PassiveEntity implements RaidVictim, Npc, VillagerDataContainer, Villager {
	private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
	private int findVillageCountdown;
	private boolean inMating;
	private boolean staring;
	private VillageProperties village;
	@Nullable
	private PlayerEntity currentCustomer;
	@Nullable
	private VillagerRecipeList recipeList;
	private int unlockTradeCountdown;
	private boolean unlockTrade;
	private boolean willingToMate;
	private String customerName;
	private boolean recentlyRescued;
	private boolean goalsSet;
	private final BasicInventory inventory = new BasicInventory(8);

	public VillagerEntity(World world) {
		this(world, VillagerType.field_17073);
	}

	public VillagerEntity(World world, VillagerType villagerType) {
		super(EntityType.VILLAGER, world);
		((EntityMobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		this.setCanPickUpLoot(true);
		this.setVillagerData(this.getVillagerData().withProfession(Registry.VILLAGER_PROFESSION.getRandom(this.random)));
	}

	@Override
	protected void method_5959() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 0.6, 0.6));
		this.goalSelector.add(1, new FleeEntityGoal(this, EvokerEntity.class, 12.0F, 0.8, 0.8));
		this.goalSelector.add(1, new FleeEntityGoal(this, VindicatorEntity.class, 8.0F, 0.8, 0.8));
		this.goalSelector.add(1, new FleeEntityGoal(this, VexEntity.class, 8.0F, 0.6, 0.6));
		this.goalSelector.add(1, new FleeEntityGoal(this, PillagerEntity.class, 15.0F, 0.6, 0.6));
		this.goalSelector.add(1, new FleeEntityGoal(this, IllusionerEntity.class, 12.0F, 0.6, 0.6));
		this.goalSelector.add(1, new class_1390(this));
		this.goalSelector.add(1, new class_1364(this));
		this.goalSelector.add(2, new class_1365(this));
		this.goalSelector.add(3, new StayInsideGoal(this));
		this.goalSelector.add(4, new OpenDoorGoal(this, true));
		this.goalSelector.add(5, new class_1370(this, 0.6));
		this.goalSelector.add(6, new VillagerBreedGoal(this));
		this.goalSelector.add(7, new AcceptPoppyGoal(this));
		this.goalSelector.add(9, new class_1358(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.add(9, new VillagerInteractionGoal(this));
		this.goalSelector.add(9, new class_1394(this, 0.6));
		this.goalSelector.add(10, new class_1361(this, MobEntity.class, 8.0F));
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
				this.setAiRangeUnlimited();
			} else {
				BlockPos blockPos2 = this.village.getCenter();
				this.setAiHome(blockPos2, this.village.getRadius());
				if (this.recentlyRescued) {
					this.recentlyRescued = false;
					this.village.changeAllRatings(5);
				}
			}
		}

		if (!this.isTrading() && this.unlockTradeCountdown > 0) {
			this.unlockTradeCountdown--;
			if (this.unlockTradeCountdown <= 0) {
				if (this.unlockTrade) {
					for (VillagerRecipe villagerRecipe : this.getRecipes()) {
						if (villagerRecipe.isDisabled()) {
							villagerRecipe.increasedMaxUses(this.random.nextInt(6) + this.random.nextInt(6) + 2);
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
		} else if (itemStack.getItem() != Items.field_8086 && this.isValid() && !this.isTrading() && !this.isChild()) {
			if (hand == Hand.MAIN) {
				playerEntity.increaseStat(Stats.field_15384);
			}

			if (this.getRecipes().isEmpty()) {
				return super.interactMob(playerEntity, hand);
			} else {
				if (!this.world.isClient && !this.recipeList.isEmpty()) {
					if (this.village != null && this.village.getRaid() != null && this.village.getRaid().isOnGoing()) {
						this.world.summonParticle(this, (byte)42);
					} else {
						this.setCurrentCustomer(playerEntity);
						this.method_17449(playerEntity, this.getDisplayName());
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
		VillagerRecipeList villagerRecipeList = this.getRecipes();
		if (!villagerRecipeList.isEmpty()) {
			compoundTag.put("Offers", villagerRecipeList.deserialize());
		}

		ListTag listTag = new ListTag();

		for (int i = 0; i < this.inventory.getInvSize(); i++) {
			ItemStack itemStack = this.inventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				listTag.add(itemStack.toTag(new CompoundTag()));
			}
		}

		compoundTag.put("Inventory", listTag);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("VillagerData", 10)) {
			this.setVillagerData(new VillagerData(new Dynamic<>(NbtOps.INSTANCE, compoundTag.getTag("VillagerData"))));
		}

		if (compoundTag.containsKey("Offers", 10)) {
			this.recipeList = new VillagerRecipeList(compoundTag.getCompound("Offers"));
		}

		this.willingToMate = compoundTag.getBoolean("Willing");
		ListTag listTag = compoundTag.getList("Inventory", 10);

		for (int i = 0; i < listTag.size(); i++) {
			ItemStack itemStack = ItemStack.fromTag(listTag.getCompoundTag(i));
			if (!itemStack.isEmpty()) {
				this.inventory.add(itemStack);
			}
		}

		this.setCanPickUpLoot(true);
		this.setSpecificGoals();
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isTrading() ? SoundEvents.field_14933 : SoundEvents.field_15175;
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
			this.recipeList = null;
		}

		this.dataTracker.set(VILLAGER_DATA, villagerData);
	}

	@Override
	public VillagerData getVillagerData() {
		return this.dataTracker.get(VILLAGER_DATA);
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
				PlayerEntity playerEntity = this.world.getClosestPlayer(this, 16.0);
				if (playerEntity != null) {
					this.village.onVillagerDeath();
				}
			}
		}

		super.onDeath(damageSource);
	}

	@Override
	public void setCurrentCustomer(@Nullable PlayerEntity playerEntity) {
		this.currentCustomer = playerEntity;
	}

	@Nullable
	@Override
	public PlayerEntity getCurrentCustomer() {
		return this.currentCustomer;
	}

	public boolean isTrading() {
		return this.currentCustomer != null;
	}

	public boolean isWillingToMate(boolean bl) {
		if (!this.willingToMate && bl && this.hasFoodForWilling()) {
			boolean bl2 = false;

			for (int i = 0; i < this.inventory.getInvSize(); i++) {
				ItemStack itemStack = this.inventory.getInvStack(i);
				if (!itemStack.isEmpty()) {
					if (itemStack.getItem() == Items.field_8229 && itemStack.getAmount() >= 3) {
						bl2 = true;
						this.inventory.takeInvStack(i, 3);
					} else if ((itemStack.getItem() == Items.field_8567 || itemStack.getItem() == Items.field_8179) && itemStack.getAmount() >= 12) {
						bl2 = true;
						this.inventory.takeInvStack(i, 12);
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

	@Override
	public void useRecipe(VillagerRecipe villagerRecipe) {
		villagerRecipe.use();
		this.field_6191 = -this.method_5970();
		this.playSound(SoundEvents.field_14815, this.getSoundVolume(), this.getSoundPitch());
		int i = 3 + this.random.nextInt(4);
		if (villagerRecipe.getUses() == 1 || this.random.nextInt(5) == 0) {
			this.unlockTradeCountdown = 40;
			this.unlockTrade = true;
			this.willingToMate = true;
			this.customerName = this.currentCustomer == null ? null : this.currentCustomer.getGameProfile().getName();
			i += 5;
		}

		if (villagerRecipe.getRewardExp()) {
			this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y + 0.5, this.z, i));
		}

		if (this.currentCustomer instanceof ServerPlayerEntity) {
			Criterions.VILLAGER_TRADE.handle((ServerPlayerEntity)this.currentCustomer, this, villagerRecipe.getSellItem());
		}
	}

	@Override
	public void onSellingItem(ItemStack itemStack) {
		if (!this.world.isClient && this.field_6191 > -this.method_5970() + 20) {
			this.field_6191 = -this.method_5970();
			this.playSound(itemStack.isEmpty() ? SoundEvents.field_15008 : SoundEvents.field_14815, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Override
	public VillagerRecipeList getRecipes() {
		if (this.recipeList == null) {
			this.recipeList = new VillagerRecipeList();
			this.addTrades();
		}

		return this.recipeList;
	}

	public void setRecipes(VillagerRecipeList villagerRecipeList) {
		this.recipeList = villagerRecipeList;
	}

	private void levelUp() {
		this.setVillagerData(this.getVillagerData().withLevel(this.getVillagerData().getLevel() + 1));
		this.addTrades();
	}

	private void addTrades() {
		VillagerData villagerData = this.getVillagerData();
		Int2ObjectMap<VillagerTrades.Factory[]> int2ObjectMap = (Int2ObjectMap<VillagerTrades.Factory[]>)VillagerTrades.PROFESSION_TO_LEVELED_TRADE
			.get(villagerData.getProfession());
		if (int2ObjectMap != null && !int2ObjectMap.isEmpty()) {
			VillagerTrades.Factory[] factorys = int2ObjectMap.get(villagerData.getLevel());
			if (factorys != null) {
				VillagerRecipeList villagerRecipeList = this.getRecipes();

				for (VillagerTrades.Factory factory : factorys) {
					VillagerRecipe villagerRecipe = factory.create(this, this.random);
					if (villagerRecipe != null) {
						villagerRecipeList.add(villagerRecipe);
					}
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setServerRecipes(@Nullable VillagerRecipeList villagerRecipeList) {
	}

	@Override
	public World getVillagerWorld() {
		return this.world;
	}

	@Override
	public BlockPos getVillagerPos() {
		return new BlockPos(this);
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

	@Override
	public float getEyeHeight() {
		return this.isChild() ? 0.81F : 1.62F;
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
			this.method_7233(ParticleTypes.field_11201);
		} else if (b == 13) {
			this.method_7233(ParticleTypes.field_11231);
		} else if (b == 14) {
			this.method_7233(ParticleTypes.field_11211);
		} else if (b == 42) {
			this.method_7233(ParticleTypes.field_11202);
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	private void method_7233(ParticleParameters particleParameters) {
		for (int i = 0; i < 5; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.addParticle(
					particleParameters,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 1.0 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					d,
					e,
					f
				);
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

	public VillagerEntity createChild(PassiveEntity passiveEntity) {
		double d = this.random.nextDouble();
		VillagerType villagerType;
		if (d < 0.5) {
			villagerType = VillagerType.forBiome(this.world.getBiome(new BlockPos(this)));
		} else if (d < 0.75) {
			villagerType = this.getVillagerData().getType();
		} else {
			villagerType = ((VillagerEntity)passiveEntity).getVillagerData().getType();
		}

		VillagerEntity villagerEntity = new VillagerEntity(this.world, villagerType);
		villagerEntity.prepareEntityData(this.world, this.world.getLocalDifficulty(new BlockPos(villagerEntity)), SpawnType.field_16466, null, null);
		return villagerEntity;
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return false;
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
		if (!this.world.isClient && !this.invalid) {
			WitchEntity witchEntity = new WitchEntity(this.world);
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

	public BasicInventory getInventory() {
		return this.inventory;
	}

	@Override
	protected void pickupItem(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();
		Item item = itemStack.getItem();
		if (this.canPickUp(item)) {
			ItemStack itemStack2 = this.inventory.add(itemStack);
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

	public boolean method_7234() {
		return this.hasEnoughFood(2);
	}

	public boolean canBreed() {
		boolean bl = this.getVillagerData().getProfession() == VillagerProfession.field_17056;
		return bl ? !this.hasEnoughFood(5) : !this.hasEnoughFood(1);
	}

	private boolean hasEnoughFood(int i) {
		boolean bl = this.getVillagerData().getProfession() == VillagerProfession.field_17056;

		for (int j = 0; j < this.inventory.getInvSize(); j++) {
			ItemStack itemStack = this.inventory.getInvStack(j);
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
		for (int i = 0; i < this.inventory.getInvSize(); i++) {
			Item item = this.inventory.getInvStack(i).getItem();
			if (item == Items.field_8317 || item == Items.field_8567 || item == Items.field_8179 || item == Items.field_8309) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean method_5758(int i, ItemStack itemStack) {
		if (super.method_5758(i, itemStack)) {
			return true;
		} else {
			int j = i - 300;
			if (j >= 0 && j < this.inventory.getInvSize()) {
				this.inventory.setInvStack(j, itemStack);
				return true;
			} else {
				return false;
			}
		}
	}
}
