package net.minecraft.entity.passive;

import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1354;
import net.minecraft.class_1358;
import net.minecraft.class_1361;
import net.minecraft.class_1363;
import net.minecraft.class_1364;
import net.minecraft.class_1365;
import net.minecraft.class_1370;
import net.minecraft.class_1377;
import net.minecraft.class_1388;
import net.minecraft.class_1390;
import net.minecraft.class_1394;
import net.minecraft.class_1655;
import net.minecraft.class_3545;
import net.minecraft.class_3730;
import net.minecraft.class_3758;
import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.entity.ai.goal.StayInsideGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.VillagerInteractionGoal;
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
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.particle.Particle;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sortme.Raid;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillageProperties;
import net.minecraft.village.Villager;
import net.minecraft.village.VillagerRecipe;
import net.minecraft.village.VillagerRecipeList;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VillagerEntity extends PassiveEntity implements class_3758, class_1655, Villager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final TrackedData<Integer> VILLAGER_TYPE = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private int field_7446;
	private boolean field_7457;
	private boolean field_7455;
	private VillageProperties properties;
	@Nullable
	private PlayerEntity field_7461;
	@Nullable
	private VillagerRecipeList recipeList;
	private int field_7444;
	private boolean field_7453;
	private boolean field_7452;
	private int field_7462;
	private String field_7454;
	private int field_7459;
	private int field_7460;
	private boolean field_7449;
	private boolean field_7447;
	private final BasicInventory inventory = new BasicInventory(new StringTextComponent("Items"), 8);
	private static final VillagerEntity.class_1652[][][][] field_7456 = new VillagerEntity.class_1652[][][][]{
		{
				{
						{
								new VillagerEntity.class_1647(Items.field_8861, new VillagerEntity.class_1653(18, 22)),
								new VillagerEntity.class_1647(Items.field_8567, new VillagerEntity.class_1653(15, 19)),
								new VillagerEntity.class_1647(Items.field_8179, new VillagerEntity.class_1653(15, 19)),
								new VillagerEntity.class_1651(Items.field_8229, new VillagerEntity.class_1653(-4, -2))
						},
						{
								new VillagerEntity.class_1647(Blocks.field_10261, new VillagerEntity.class_1653(8, 13)),
								new VillagerEntity.class_1651(Items.field_8741, new VillagerEntity.class_1653(-3, -2))
						},
						{
								new VillagerEntity.class_1647(Blocks.field_10545, new VillagerEntity.class_1653(7, 12)),
								new VillagerEntity.class_1651(Items.field_8279, new VillagerEntity.class_1653(-7, -5))
						},
						{
								new VillagerEntity.class_1651(Items.field_8423, new VillagerEntity.class_1653(-10, -6)),
								new VillagerEntity.class_1651(Blocks.field_10183, new VillagerEntity.class_1653(1, 1))
						}
				},
				{
						{
								new VillagerEntity.class_1647(Items.field_8276, new VillagerEntity.class_1653(15, 20)),
								new VillagerEntity.class_1647(Items.field_8713, new VillagerEntity.class_1653(16, 24)),
								new VillagerEntity.class_1650(Items.field_8429, new VillagerEntity.class_1653(6, 6), Items.field_8373, new VillagerEntity.class_1653(6, 6)),
								new VillagerEntity.class_1650(Items.field_8209, new VillagerEntity.class_1653(6, 6), Items.field_8509, new VillagerEntity.class_1653(6, 6))
						},
						{new VillagerEntity.class_1649(Items.field_8378, new VillagerEntity.class_1653(7, 8))}
				},
				{
						{
								new VillagerEntity.class_1647(Blocks.field_10446, new VillagerEntity.class_1653(16, 22)),
								new VillagerEntity.class_1651(Items.field_8868, new VillagerEntity.class_1653(3, 4))
						},
						{
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10446), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10095), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10215), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10294), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10490), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10028), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10459), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10423), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10222), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10619), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10259), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10514), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10113), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10170), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10314), new VillagerEntity.class_1653(1, 2)),
								new VillagerEntity.class_1651(new ItemStack(Blocks.field_10146), new VillagerEntity.class_1653(1, 2))
						}
				},
				{
						{
								new VillagerEntity.class_1647(Items.field_8276, new VillagerEntity.class_1653(15, 20)),
								new VillagerEntity.class_1651(Items.field_8107, new VillagerEntity.class_1653(-12, -8))
						},
						{
								new VillagerEntity.class_1651(Items.field_8102, new VillagerEntity.class_1653(2, 3)),
								new VillagerEntity.class_1650(Blocks.field_10255, new VillagerEntity.class_1653(10, 10), Items.field_8145, new VillagerEntity.class_1653(6, 10))
						}
				}
		},
		{
				{
						{new VillagerEntity.class_1647(Items.field_8407, new VillagerEntity.class_1653(24, 36)), new VillagerEntity.class_1648()},
						{
								new VillagerEntity.class_1647(Items.field_8529, new VillagerEntity.class_1653(8, 10)),
								new VillagerEntity.class_1651(Items.field_8251, new VillagerEntity.class_1653(10, 12)),
								new VillagerEntity.class_1651(Blocks.field_10504, new VillagerEntity.class_1653(3, 4))
						},
						{
								new VillagerEntity.class_1647(Items.field_8360, new VillagerEntity.class_1653(2, 2)),
								new VillagerEntity.class_1651(Items.field_8557, new VillagerEntity.class_1653(10, 12)),
								new VillagerEntity.class_1651(Blocks.field_10033, new VillagerEntity.class_1653(-5, -3))
						},
						{new VillagerEntity.class_1648()},
						{new VillagerEntity.class_1648()},
						{new VillagerEntity.class_1651(Items.field_8448, new VillagerEntity.class_1653(20, 22))}
				},
				{
						{new VillagerEntity.class_1647(Items.field_8407, new VillagerEntity.class_1653(24, 36))},
						{new VillagerEntity.class_1647(Items.field_8251, new VillagerEntity.class_1653(1, 1))},
						{new VillagerEntity.class_1651(Items.field_8895, new VillagerEntity.class_1653(7, 11))},
						{
								new VillagerEntity.class_1654(new VillagerEntity.class_1653(12, 20), "Monument", MapIcon.Direction.field_98),
								new VillagerEntity.class_1654(new VillagerEntity.class_1653(16, 28), "Mansion", MapIcon.Direction.field_88)
						}
				}
		},
		{
				{
						{
								new VillagerEntity.class_1647(Items.field_8511, new VillagerEntity.class_1653(36, 40)),
								new VillagerEntity.class_1647(Items.field_8695, new VillagerEntity.class_1653(8, 10))
						},
						{
								new VillagerEntity.class_1651(Items.field_8725, new VillagerEntity.class_1653(-4, -1)),
								new VillagerEntity.class_1651(new ItemStack(Items.field_8759), new VillagerEntity.class_1653(-2, -1))
						},
						{
								new VillagerEntity.class_1651(Items.field_8634, new VillagerEntity.class_1653(4, 7)),
								new VillagerEntity.class_1651(Blocks.field_10171, new VillagerEntity.class_1653(-3, -1))
						},
						{new VillagerEntity.class_1651(Items.field_8287, new VillagerEntity.class_1653(3, 11))}
				}
		},
		{
				{
						{
								new VillagerEntity.class_1647(Items.field_8713, new VillagerEntity.class_1653(16, 24)),
								new VillagerEntity.class_1651(Items.field_8743, new VillagerEntity.class_1653(4, 6))
						},
						{
								new VillagerEntity.class_1647(Items.field_8620, new VillagerEntity.class_1653(7, 9)),
								new VillagerEntity.class_1651(Items.field_8523, new VillagerEntity.class_1653(10, 14))
						},
						{
								new VillagerEntity.class_1647(Items.field_8477, new VillagerEntity.class_1653(3, 4)),
								new VillagerEntity.class_1649(Items.field_8058, new VillagerEntity.class_1653(16, 19))
						},
						{
								new VillagerEntity.class_1651(Items.field_8313, new VillagerEntity.class_1653(5, 7)),
								new VillagerEntity.class_1651(Items.field_8218, new VillagerEntity.class_1653(9, 11)),
								new VillagerEntity.class_1651(Items.field_8283, new VillagerEntity.class_1653(5, 7)),
								new VillagerEntity.class_1651(Items.field_8873, new VillagerEntity.class_1653(11, 15))
						}
				},
				{
						{
								new VillagerEntity.class_1647(Items.field_8713, new VillagerEntity.class_1653(16, 24)),
								new VillagerEntity.class_1651(Items.field_8475, new VillagerEntity.class_1653(6, 8))
						},
						{
								new VillagerEntity.class_1647(Items.field_8620, new VillagerEntity.class_1653(7, 9)),
								new VillagerEntity.class_1649(Items.field_8371, new VillagerEntity.class_1653(9, 10))
						},
						{
								new VillagerEntity.class_1647(Items.field_8477, new VillagerEntity.class_1653(3, 4)),
								new VillagerEntity.class_1649(Items.field_8802, new VillagerEntity.class_1653(12, 15)),
								new VillagerEntity.class_1649(Items.field_8556, new VillagerEntity.class_1653(9, 12))
						}
				},
				{
						{
								new VillagerEntity.class_1647(Items.field_8713, new VillagerEntity.class_1653(16, 24)),
								new VillagerEntity.class_1649(Items.field_8699, new VillagerEntity.class_1653(5, 7))
						},
						{
								new VillagerEntity.class_1647(Items.field_8620, new VillagerEntity.class_1653(7, 9)),
								new VillagerEntity.class_1649(Items.field_8403, new VillagerEntity.class_1653(9, 11))
						},
						{
								new VillagerEntity.class_1647(Items.field_8477, new VillagerEntity.class_1653(3, 4)),
								new VillagerEntity.class_1649(Items.field_8377, new VillagerEntity.class_1653(12, 15))
						}
				}
		},
		{
				{
						{
								new VillagerEntity.class_1647(Items.field_8389, new VillagerEntity.class_1653(14, 18)),
								new VillagerEntity.class_1647(Items.field_8726, new VillagerEntity.class_1653(14, 18))
						},
						{
								new VillagerEntity.class_1647(Items.field_8713, new VillagerEntity.class_1653(16, 24)),
								new VillagerEntity.class_1651(Items.field_8261, new VillagerEntity.class_1653(-7, -5)),
								new VillagerEntity.class_1651(Items.field_8544, new VillagerEntity.class_1653(-8, -6))
						}
				},
				{
						{
								new VillagerEntity.class_1647(Items.field_8745, new VillagerEntity.class_1653(9, 12)),
								new VillagerEntity.class_1651(Items.field_8570, new VillagerEntity.class_1653(2, 4))
						},
						{new VillagerEntity.class_1649(Items.field_8577, new VillagerEntity.class_1653(7, 12))},
						{new VillagerEntity.class_1651(Items.field_8175, new VillagerEntity.class_1653(8, 10))}
				}
		},
		{new VillagerEntity.class_1652[0][]}
	};

	public VillagerEntity(World world) {
		this(world, 0);
	}

	public VillagerEntity(World world, int i) {
		super(EntityType.VILLAGER, world);
		this.setVillagerType(i);
		this.setSize(0.6F, 1.95F);
		((EntityMobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		this.setCanPickUpLoot(true);
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
		this.goalSelector.add(6, new class_1363(this));
		this.goalSelector.add(7, new class_1388(this));
		this.goalSelector.add(9, new class_1358(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.add(9, new VillagerInteractionGoal(this));
		this.goalSelector.add(9, new class_1394(this, 0.6));
		this.goalSelector.add(10, new class_1361(this, MobEntity.class, 8.0F));
	}

	private void method_7230() {
		if (!this.field_7447) {
			this.field_7447 = true;
			if (this.isChild()) {
				this.goalSelector.add(8, new class_1377(this, 0.32));
			} else if (this.getVillagerType() == 0) {
				this.goalSelector.add(6, new class_1354(this, 0.6));
			}
		}
	}

	@Override
	protected void method_5619() {
		if (this.getVillagerType() == 0) {
			this.goalSelector.add(8, new class_1354(this, 0.6));
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
		if (--this.field_7446 <= 0) {
			BlockPos blockPos = new BlockPos(this);
			this.world.getVillageManager().addRecentVillagerPosition(blockPos);
			this.field_7446 = 70 + this.random.nextInt(50);
			this.properties = this.world.getVillageManager().getNearestVillage(blockPos, 32);
			if (this.properties == null) {
				this.setAiRangeUnlimited();
			} else {
				BlockPos blockPos2 = this.properties.getCenter();
				this.setAiHome(blockPos2, this.properties.getRadius());
				if (this.field_7449) {
					this.field_7449 = false;
					this.properties.method_6401(5);
				}
			}
		}

		if (!this.method_7235() && this.field_7444 > 0) {
			this.field_7444--;
			if (this.field_7444 <= 0) {
				if (this.field_7453) {
					for (VillagerRecipe villagerRecipe : this.recipeList) {
						if (villagerRecipe.isDisabled()) {
							villagerRecipe.increasedMaxUses(this.random.nextInt(6) + this.random.nextInt(6) + 2);
						}
					}

					this.method_7237();
					this.field_7453 = false;
					if (this.properties != null && this.field_7454 != null) {
						this.world.method_8421(this, (byte)14);
						this.properties.method_6393(this.field_7454, 1);
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
		} else if (itemStack.getItem() != Items.field_8086 && this.isValid() && !this.method_7235() && !this.isChild()) {
			if (this.recipeList == null) {
				this.method_7237();
			}

			if (hand == Hand.MAIN) {
				playerEntity.method_7281(Stats.field_15384);
			}

			if (!this.world.isRemote && !this.recipeList.isEmpty()) {
				if (this.properties != null && this.properties.method_16469() != null && this.properties.method_16469().method_16832()) {
					this.world.method_8421(this, (byte)42);
				} else {
					this.setCurrentCustomer(playerEntity);
					playerEntity.openVillagerGui(this);
				}
			} else if (this.recipeList.isEmpty()) {
				return super.interactMob(playerEntity, hand);
			}

			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(VILLAGER_TYPE, 0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Profession", this.getVillagerType());
		compoundTag.putInt("Riches", this.field_7462);
		compoundTag.putInt("Career", this.field_7459);
		compoundTag.putInt("CareerLevel", this.field_7460);
		compoundTag.putBoolean("Willing", this.field_7452);
		if (this.recipeList != null) {
			compoundTag.put("Offers", this.recipeList.deserialize());
		}

		ListTag listTag = new ListTag();

		for (int i = 0; i < this.inventory.getInvSize(); i++) {
			ItemStack itemStack = this.inventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				listTag.add((Tag)itemStack.toTag(new CompoundTag()));
			}
		}

		compoundTag.put("Inventory", listTag);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setVillagerType(compoundTag.getInt("Profession"));
		this.field_7462 = compoundTag.getInt("Riches");
		this.field_7459 = compoundTag.getInt("Career");
		this.field_7460 = compoundTag.getInt("CareerLevel");
		this.field_7452 = compoundTag.getBoolean("Willing");
		if (compoundTag.containsKey("Offers", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("Offers");
			this.recipeList = new VillagerRecipeList(compoundTag2);
		}

		ListTag listTag = compoundTag.getList("Inventory", 10);

		for (int i = 0; i < listTag.size(); i++) {
			ItemStack itemStack = ItemStack.fromTag(listTag.getCompoundTag(i));
			if (!itemStack.isEmpty()) {
				this.inventory.method_5491(itemStack);
			}
		}

		this.setCanPickUpLoot(true);
		this.method_7230();
	}

	@Override
	public boolean method_5974(double d) {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.method_7235() ? SoundEvents.field_14933 : SoundEvents.field_15175;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15139;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15225;
	}

	public void setVillagerType(int i) {
		this.dataTracker.set(VILLAGER_TYPE, i);
	}

	public int getVillagerType() {
		return Math.max(this.dataTracker.get(VILLAGER_TYPE) % 6, 0);
	}

	public boolean method_7241() {
		return this.field_7457;
	}

	public void method_7226(boolean bl) {
		this.field_7457 = bl;
	}

	public void method_7220(boolean bl) {
		this.field_7455 = bl;
	}

	public boolean method_7236() {
		return this.field_7455;
	}

	@Nullable
	@Override
	public VillageProperties method_7232() {
		return this.properties;
	}

	@Override
	public void setAttacker(@Nullable LivingEntity livingEntity) {
		super.setAttacker(livingEntity);
		if (this.properties != null && livingEntity != null) {
			this.properties.addAttacker(livingEntity);
			if (livingEntity instanceof PlayerEntity) {
				int i = -1;
				if (this.isChild()) {
					i = -3;
				}

				this.properties.method_6393(((PlayerEntity)livingEntity).getGameProfile().getName(), i);
				if (this.isValid()) {
					this.world.method_8421(this, (byte)13);
				}
			}
		}
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		if (this.properties != null) {
			Entity entity = damageSource.getAttacker();
			if (entity != null) {
				if (entity instanceof PlayerEntity) {
					this.properties.method_6393(((PlayerEntity)entity).getGameProfile().getName(), -2);
				} else if (entity instanceof Monster) {
					this.properties.onVillagerDeath();
				}
			} else {
				PlayerEntity playerEntity = this.world.getClosestPlayer(this, 16.0);
				if (playerEntity != null) {
					this.properties.onVillagerDeath();
				}
			}
		}

		super.onDeath(damageSource);
	}

	@Override
	public void setCurrentCustomer(@Nullable PlayerEntity playerEntity) {
		this.field_7461 = playerEntity;
	}

	@Nullable
	@Override
	public PlayerEntity getCurrentCustomer() {
		return this.field_7461;
	}

	public boolean method_7235() {
		return this.field_7461 != null;
	}

	public boolean method_7245(boolean bl) {
		if (!this.field_7452 && bl && this.method_7224()) {
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
					this.world.method_8421(this, (byte)18);
					this.field_7452 = true;
					break;
				}
			}
		}

		return this.field_7452;
	}

	public void method_7243(boolean bl) {
		this.field_7452 = bl;
	}

	@Override
	public void useRecipe(VillagerRecipe villagerRecipe) {
		villagerRecipe.use();
		this.field_6191 = -this.method_5970();
		this.playSoundAtEntity(SoundEvents.field_14815, this.getSoundVolume(), this.getSoundPitch());
		int i = 3 + this.random.nextInt(4);
		if (villagerRecipe.getUses() == 1 || this.random.nextInt(5) == 0) {
			this.field_7444 = 40;
			this.field_7453 = true;
			this.field_7452 = true;
			if (this.field_7461 != null) {
				this.field_7454 = this.field_7461.getGameProfile().getName();
			} else {
				this.field_7454 = null;
			}

			i += 5;
		}

		if (villagerRecipe.getBuyItem().getItem() == Items.field_8687) {
			this.field_7462 = this.field_7462 + villagerRecipe.getBuyItem().getAmount();
		}

		if (villagerRecipe.getRewardExp()) {
			this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y + 0.5, this.z, i));
		}

		if (this.field_7461 instanceof ServerPlayerEntity) {
			CriterionCriterions.VILLAGER_TRADE.handle((ServerPlayerEntity)this.field_7461, this, villagerRecipe.getSellItem());
		}
	}

	@Override
	public void onSellingItem(ItemStack itemStack) {
		if (!this.world.isRemote && this.field_6191 > -this.method_5970() + 20) {
			this.field_6191 = -this.method_5970();
			this.playSoundAtEntity(itemStack.isEmpty() ? SoundEvents.field_15008 : SoundEvents.field_14815, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Nullable
	@Override
	public VillagerRecipeList getRecipes(PlayerEntity playerEntity) {
		if (this.recipeList == null) {
			this.method_7237();
		}

		return this.recipeList;
	}

	private void method_7237() {
		VillagerEntity.class_1652[][][] lvs = field_7456[this.getVillagerType()];
		if (this.field_7459 != 0 && this.field_7460 != 0) {
			this.field_7460++;
		} else {
			this.field_7459 = this.random.nextInt(lvs.length) + 1;
			this.field_7460 = 1;
		}

		if (this.recipeList == null) {
			this.recipeList = new VillagerRecipeList();
		}

		int i = this.field_7459 - 1;
		int j = this.field_7460 - 1;
		if (i >= 0 && i < lvs.length) {
			VillagerEntity.class_1652[][] lvs2 = lvs[i];
			if (j >= 0 && j < lvs2.length) {
				VillagerEntity.class_1652[] lvs3 = lvs2[j];

				for (VillagerEntity.class_1652 lv : lvs3) {
					lv.method_7246(this, this.recipeList, this.random);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setRecipeList(@Nullable VillagerRecipeList villagerRecipeList) {
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
			if (this.recipeList == null) {
				this.method_7237();
			}

			String string = null;
			switch (this.getVillagerType()) {
				case 0:
					if (this.field_7459 == 1) {
						string = "farmer";
					} else if (this.field_7459 == 2) {
						string = "fisherman";
					} else if (this.field_7459 == 3) {
						string = "shepherd";
					} else if (this.field_7459 == 4) {
						string = "fletcher";
					}
					break;
				case 1:
					if (this.field_7459 == 1) {
						string = "librarian";
					} else if (this.field_7459 == 2) {
						string = "cartographer";
					}
					break;
				case 2:
					string = "cleric";
					break;
				case 3:
					if (this.field_7459 == 1) {
						string = "armorer";
					} else if (this.field_7459 == 2) {
						string = "weapon_smith";
					} else if (this.field_7459 == 3) {
						string = "tool_smith";
					}
					break;
				case 4:
					if (this.field_7459 == 1) {
						string = "butcher";
					} else if (this.field_7459 == 2) {
						string = "leatherworker";
					}
					break;
				case 5:
					string = "nitwit";
			}

			if (string != null) {
				TextComponent textComponent2 = new TranslatableTextComponent(this.getType().getTranslationKey() + '.' + string)
					.modifyStyle(style -> style.setHoverEvent(this.getComponentHoverEvent()).setInsertion(this.getUuidAsString()));
				if (abstractScoreboardTeam != null) {
					textComponent2.applyFormat(abstractScoreboardTeam.getColor());
				}

				return textComponent2;
			} else {
				return super.getDisplayName();
			}
		}
	}

	@Override
	public float getEyeHeight() {
		return this.isChild() ? 0.81F : 1.62F;
	}

	@Nullable
	@Override
	public Raid method_16461() {
		return this.properties != null ? this.properties.method_16469() : null;
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
	private void method_7233(Particle particle) {
		for (int i = 0; i < 5; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.method_8406(
					particle,
					this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					this.y + 1.0 + (double)(this.random.nextFloat() * this.height),
					this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					d,
					e,
					f
				);
		}
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, class_3730 arg, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		return this.method_7240(iWorld, localDifficulty, arg, entityData, compoundTag, true);
	}

	public EntityData method_7240(
		IWorld iWorld, LocalDifficulty localDifficulty, class_3730 arg, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag, boolean bl
	) {
		entityData = super.method_5943(iWorld, localDifficulty, arg, entityData, compoundTag);
		if (bl) {
			this.setVillagerType(iWorld.getRandom().nextInt(6));
		}

		this.method_7230();
		this.method_7237();
		return entityData;
	}

	public void method_7238() {
		this.field_7449 = true;
	}

	public VillagerEntity createChild(PassiveEntity passiveEntity) {
		VillagerEntity villagerEntity = new VillagerEntity(this.world);
		villagerEntity.method_5943(this.world, this.world.getLocalDifficulty(new BlockPos(villagerEntity)), class_3730.field_16466, null, null);
		return villagerEntity;
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return false;
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
		if (!this.world.isRemote && !this.invalid) {
			WitchEntity witchEntity = new WitchEntity(this.world);
			witchEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
			witchEntity.method_5943(this.world, this.world.getLocalDifficulty(new BlockPos(witchEntity)), class_3730.field_16468, null, null);
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
	protected void method_5949(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();
		Item item = itemStack.getItem();
		if (this.method_7227(item)) {
			ItemStack itemStack2 = this.inventory.method_5491(itemStack);
			if (itemStack2.isEmpty()) {
				itemEntity.invalidate();
			} else {
				itemStack.setAmount(itemStack2.getAmount());
			}
		}
	}

	private boolean method_7227(Item item) {
		return item == Items.field_8229
			|| item == Items.field_8567
			|| item == Items.field_8179
			|| item == Items.field_8861
			|| item == Items.field_8317
			|| item == Items.field_8186
			|| item == Items.field_8309;
	}

	public boolean method_7224() {
		return this.method_7228(1);
	}

	public boolean method_7234() {
		return this.method_7228(2);
	}

	public boolean method_7239() {
		boolean bl = this.getVillagerType() == 0;
		return bl ? !this.method_7228(5) : !this.method_7228(1);
	}

	private boolean method_7228(int i) {
		boolean bl = this.getVillagerType() == 0;

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

	public boolean method_7244() {
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

	static class class_1647 implements VillagerEntity.class_1652 {
		public Item field_7463;
		public VillagerEntity.class_1653 field_7464;

		public class_1647(ItemContainer itemContainer, VillagerEntity.class_1653 arg) {
			this.field_7463 = itemContainer.getItem();
			this.field_7464 = arg;
		}

		@Override
		public void method_7246(Villager villager, VillagerRecipeList villagerRecipeList, Random random) {
			ItemStack itemStack = new ItemStack(this.field_7463, this.field_7464 == null ? 1 : this.field_7464.method_7247(random));
			villagerRecipeList.add(new VillagerRecipe(itemStack, Items.field_8687));
		}
	}

	static class class_1648 implements VillagerEntity.class_1652 {
		public class_1648() {
		}

		@Override
		public void method_7246(Villager villager, VillagerRecipeList villagerRecipeList, Random random) {
			Enchantment enchantment = Registry.ENCHANTMENT.getRandom(random);
			int i = MathHelper.nextInt(random, enchantment.getMinimumLevel(), enchantment.getMaximumLevel());
			ItemStack itemStack = EnchantedBookItem.method_7808(new InfoEnchantment(enchantment, i));
			int j = 2 + random.nextInt(5 + i * 10) + 3 * i;
			if (enchantment.isLootOnly()) {
				j *= 2;
			}

			if (j > 64) {
				j = 64;
			}

			villagerRecipeList.add(new VillagerRecipe(new ItemStack(Items.field_8529), new ItemStack(Items.field_8687, j), itemStack));
		}
	}

	static class class_1649 implements VillagerEntity.class_1652 {
		public ItemStack field_7465;
		public VillagerEntity.class_1653 field_7466;

		public class_1649(Item item, VillagerEntity.class_1653 arg) {
			this.field_7465 = new ItemStack(item);
			this.field_7466 = arg;
		}

		@Override
		public void method_7246(Villager villager, VillagerRecipeList villagerRecipeList, Random random) {
			int i = 1;
			if (this.field_7466 != null) {
				i = this.field_7466.method_7247(random);
			}

			ItemStack itemStack = new ItemStack(Items.field_8687, i);
			ItemStack itemStack2 = EnchantmentHelper.method_8233(random, new ItemStack(this.field_7465.getItem()), 5 + random.nextInt(15), false);
			villagerRecipeList.add(new VillagerRecipe(itemStack, itemStack2));
		}
	}

	static class class_1650 implements VillagerEntity.class_1652 {
		public ItemStack field_7467;
		public VillagerEntity.class_1653 field_7469;
		public ItemStack field_7468;
		public VillagerEntity.class_1653 field_7470;

		public class_1650(ItemContainer itemContainer, VillagerEntity.class_1653 arg, Item item, VillagerEntity.class_1653 arg2) {
			this.field_7467 = new ItemStack(itemContainer);
			this.field_7469 = arg;
			this.field_7468 = new ItemStack(item);
			this.field_7470 = arg2;
		}

		@Override
		public void method_7246(Villager villager, VillagerRecipeList villagerRecipeList, Random random) {
			int i = this.field_7469.method_7247(random);
			int j = this.field_7470.method_7247(random);
			villagerRecipeList.add(
				new VillagerRecipe(new ItemStack(this.field_7467.getItem(), i), new ItemStack(Items.field_8687), new ItemStack(this.field_7468.getItem(), j))
			);
		}
	}

	static class class_1651 implements VillagerEntity.class_1652 {
		public ItemStack field_7471;
		public VillagerEntity.class_1653 field_7472;

		public class_1651(Block block, VillagerEntity.class_1653 arg) {
			this(new ItemStack(block), arg);
		}

		public class_1651(Item item, VillagerEntity.class_1653 arg) {
			this(new ItemStack(item), arg);
		}

		public class_1651(ItemStack itemStack, VillagerEntity.class_1653 arg) {
			this.field_7471 = itemStack;
			this.field_7472 = arg;
		}

		@Override
		public void method_7246(Villager villager, VillagerRecipeList villagerRecipeList, Random random) {
			int i = 1;
			if (this.field_7472 != null) {
				i = this.field_7472.method_7247(random);
			}

			ItemStack itemStack;
			ItemStack itemStack2;
			if (i < 0) {
				itemStack = new ItemStack(Items.field_8687);
				itemStack2 = new ItemStack(this.field_7471.getItem(), -i);
			} else {
				itemStack = new ItemStack(Items.field_8687, i);
				itemStack2 = new ItemStack(this.field_7471.getItem());
			}

			villagerRecipeList.add(new VillagerRecipe(itemStack, itemStack2));
		}
	}

	interface class_1652 {
		void method_7246(Villager villager, VillagerRecipeList villagerRecipeList, Random random);
	}

	static class class_1653 extends class_3545<Integer, Integer> {
		public class_1653(int i, int j) {
			super(i, j);
			if (j < i) {
				VillagerEntity.LOGGER.warn("PriceRange({}, {}) invalid, {} smaller than {}", i, j, j, i);
			}
		}

		public int method_7247(Random random) {
			return this.method_15442() >= this.method_15441()
				? this.method_15442()
				: this.method_15442() + random.nextInt(this.method_15441() - this.method_15442() + 1);
		}
	}

	static class class_1654 implements VillagerEntity.class_1652 {
		public VillagerEntity.class_1653 field_7475;
		public String field_7474;
		public MapIcon.Direction field_7473;

		public class_1654(VillagerEntity.class_1653 arg, String string, MapIcon.Direction direction) {
			this.field_7475 = arg;
			this.field_7474 = string;
			this.field_7473 = direction;
		}

		@Override
		public void method_7246(Villager villager, VillagerRecipeList villagerRecipeList, Random random) {
			int i = this.field_7475.method_7247(random);
			World world = villager.getVillagerWorld();
			BlockPos blockPos = world.method_8487(this.field_7474, villager.getVillagerPos(), 100, true);
			if (blockPos != null) {
				ItemStack itemStack = FilledMapItem.method_8005(world, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
				FilledMapItem.method_8002(world, itemStack);
				MapState.method_110(itemStack, blockPos, "+", this.field_7473);
				itemStack.setDisplayName(new TranslatableTextComponent("filled_map." + this.field_7474.toLowerCase(Locale.ROOT)));
				villagerRecipeList.add(new VillagerRecipe(new ItemStack(Items.field_8687, i), new ItemStack(Items.field_8251), itemStack));
			}
		}
	}
}
