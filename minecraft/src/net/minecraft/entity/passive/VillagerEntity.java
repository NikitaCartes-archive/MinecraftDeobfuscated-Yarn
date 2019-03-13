package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4094;
import net.minecraft.class_4095;
import net.minecraft.class_4129;
import net.minecraft.class_4136;
import net.minecraft.class_4139;
import net.minecraft.class_4140;
import net.minecraft.class_4141;
import net.minecraft.class_4148;
import net.minecraft.class_4149;
import net.minecraft.class_4151;
import net.minecraft.class_4168;
import net.minecraft.class_4170;
import net.minecraft.class_4208;
import net.minecraft.class_4209;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TraderRecipe;
import net.minecraft.village.TraderRecipeList;
import net.minecraft.village.Trades;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class VillagerEntity extends AbstractTraderEntity implements class_4094, VillagerDataContainer {
	private static final TrackedData<VillagerData> field_7445 = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
	public static final Map<Item, Integer> field_18526 = ImmutableMap.of(Items.field_8229, 4, Items.field_8567, 1, Items.field_8179, 1, Items.field_8186, 1);
	private static final Set<Item> field_18527 = ImmutableSet.of(
		Items.field_8229, Items.field_8567, Items.field_8179, Items.field_8861, Items.field_8317, Items.field_8186, Items.field_8309
	);
	private int field_18528;
	private boolean field_18529;
	@Nullable
	private PlayerEntity field_18530;
	@Nullable
	private UUID field_18531;
	private long field_18532 = Long.MIN_VALUE;
	private byte field_18533;
	private final class_4136 field_18534 = new class_4136();
	private long field_18535;
	private int field_18536;
	private long field_18537 = 0L;
	private static final ImmutableList<class_4140<?>> field_18538 = ImmutableList.of(
		class_4140.field_18438,
		class_4140.field_18439,
		class_4140.field_18440,
		class_4140.field_18441,
		class_4140.field_18442,
		class_4140.field_18443,
		class_4140.field_18444,
		class_4140.field_18445,
		class_4140.field_18446,
		class_4140.field_18447,
		class_4140.field_18448,
		class_4140.field_18449,
		class_4140.field_18450,
		class_4140.field_18451,
		class_4140.field_18452,
		class_4140.field_18453
	);
	private static final ImmutableList<class_4149<? extends class_4148<? super VillagerEntity>>> field_18539 = ImmutableList.of(
		class_4149.field_18466, class_4149.field_18467, class_4149.field_18468, class_4149.field_18469, class_4149.field_18470
	);

	public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world) {
		this(entityType, world, VillagerType.field_17073);
	}

	public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world, VillagerType villagerType) {
		super(entityType, world);
		((EntityMobNavigation)this.method_5942()).setCanPathThroughDoors(true);
		this.setCanPickUpLoot(true);
		this.method_7221(this.getVillagerData().method_16921(VillagerProfession.field_17051));
		this.field_18321 = this.method_18867(new Dynamic<>(NbtOps.INSTANCE, new CompoundTag()));
	}

	@Override
	public class_4095<VillagerEntity> method_18868() {
		return (class_4095<VillagerEntity>)super.method_18868();
	}

	@Override
	protected class_4095<?> method_18867(Dynamic<?> dynamic) {
		class_4095<VillagerEntity> lv = new class_4095<>(field_18538, field_18539, dynamic);
		this.method_19174(lv);
		return lv;
	}

	public void method_19179(ServerWorld serverWorld) {
		class_4095<VillagerEntity> lv = this.method_18868();
		lv.method_18900(serverWorld, this);
		this.field_18321 = lv.method_18911();
		this.method_19174(this.method_18868());
	}

	private void method_19174(class_4095<VillagerEntity> arg) {
		VillagerProfession villagerProfession = this.getVillagerData().method_16924();
		float f = (float)this.method_5996(EntityAttributes.MOVEMENT_SPEED).getValue();
		if (this.isChild()) {
			arg.method_18884(class_4170.field_18605);
		} else {
			arg.method_18884(class_4170.field_18606);
			arg.method_18882(
				class_4168.field_18596, class_4129.method_19021(villagerProfession, f), ImmutableSet.of(Pair.of(class_4140.field_18439, class_4141.field_18456))
			);
		}

		arg.method_18881(class_4168.field_18594, class_4129.method_19020(villagerProfession, f));
		arg.method_18882(
			class_4168.field_18598, class_4129.method_19023(villagerProfession, f), ImmutableSet.of(Pair.of(class_4140.field_18440, class_4141.field_18456))
		);
		arg.method_18882(
			class_4168.field_18597, class_4129.method_19022(villagerProfession, f), ImmutableSet.of(Pair.of(class_4140.field_18438, class_4141.field_18456))
		);
		arg.method_18881(class_4168.field_18595, class_4129.method_19024(villagerProfession, f));
		arg.method_18881(class_4168.field_18599, class_4129.method_19025(villagerProfession, f));
		arg.method_18890(ImmutableSet.of(class_4168.field_18594));
		arg.method_18897(class_4168.field_18595);
		arg.method_18871(this.field_6002.getTimeOfDay());
	}

	@Override
	protected void method_5619() {
		super.method_5619();
		if (this.field_6002 instanceof ServerWorld) {
			this.method_19179((ServerWorld)this.field_6002);
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.method_5996(EntityAttributes.FOLLOW_RANGE).setBaseValue(48.0);
	}

	@Override
	protected void mobTick() {
		this.field_6002.getProfiler().push("brain");
		this.method_18868().method_18891((ServerWorld)this.field_6002, this);
		this.field_6002.getProfiler().pop();
		if (!this.hasCustomer() && this.field_18528 > 0) {
			this.field_18528--;
			if (this.field_18528 <= 0) {
				if (this.field_18529) {
					this.levelUp();
					this.field_18529 = false;
				}

				this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5924, 200, 0));
			}
		}

		if (this.field_18530 != null && this.field_6002 instanceof ServerWorld) {
			((ServerWorld)this.field_6002).method_19496(class_4151.field_18478, this.field_18530, this);
			this.field_18530 = null;
		}

		super.mobTick();
	}

	public void method_19181() {
		this.setCurrentCustomer(null);
		this.method_19187();
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
				if (this.field_6002 instanceof ServerWorld && !this.field_17721.isEmpty()) {
					if (((ServerWorld)this.field_6002).method_19503(new BlockPos(this))) {
						this.field_6002.summonParticle(this, (byte)42);
					} else {
						this.method_19191(playerEntity);
					}
				}

				return true;
			}
		} else {
			return super.method_5992(playerEntity, hand);
		}
	}

	private void method_19191(PlayerEntity playerEntity) {
		this.method_19192(playerEntity);
		this.setCurrentCustomer(playerEntity);
		this.method_17449(playerEntity, this.method_5476(), this.getVillagerData().getLevel());
	}

	public void method_19182() {
		for (TraderRecipe traderRecipe : this.method_8264()) {
			traderRecipe.method_19274();
			traderRecipe.method_19275();
		}

		this.field_18537 = this.field_6002.getTimeOfDay() % 24000L;
	}

	private void method_19192(PlayerEntity playerEntity) {
		int i = this.field_18534.method_19073(playerEntity.getUuid(), arg -> arg != class_4139.field_18429);
		if (i != 0) {
			for (TraderRecipe traderRecipe : this.method_8264()) {
				traderRecipe.increasedMaxUses(-MathHelper.floor((float)i * traderRecipe.method_19278()));
			}
		}
	}

	private void method_19187() {
		for (TraderRecipe traderRecipe : this.method_8264()) {
			traderRecipe.method_19276();
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7445, new VillagerData(VillagerType.field_17073, VillagerProfession.field_17051, 1));
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.method_10566("VillagerData", this.getVillagerData().serialize(NbtOps.INSTANCE));
		compoundTag.putByte("FoodLevel", this.field_18533);
		compoundTag.method_10566("Gossips", this.field_18534.method_19067(NbtOps.INSTANCE).getValue());
		compoundTag.putInt("Xp", this.field_18536);
		compoundTag.putLong("LastRestock", this.field_18537);
		if (this.field_18531 != null) {
			compoundTag.putUuid("BuddyGolem", this.field_18531);
		}
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("VillagerData", 10)) {
			this.method_7221(new VillagerData(new Dynamic<>(NbtOps.INSTANCE, compoundTag.method_10580("VillagerData"))));
		}

		if (compoundTag.containsKey("Offers", 10)) {
			this.field_17721 = new TraderRecipeList(compoundTag.getCompound("Offers"));
		}

		if (compoundTag.containsKey("FoodLevel", 1)) {
			this.field_18533 = compoundTag.getByte("FoodLevel");
		}

		ListTag listTag = compoundTag.method_10554("Gossips", 10);
		this.field_18534.method_19066(new Dynamic<>(NbtOps.INSTANCE, listTag));
		if (compoundTag.containsKey("Xp", 3)) {
			this.field_18536 = compoundTag.getInt("Xp");
		} else {
			int i = this.getVillagerData().getLevel();
			if (VillagerData.method_19196(i)) {
				this.field_18536 = VillagerData.method_19194(i);
			}
		}

		this.field_18537 = compoundTag.getLong("LastRestock");
		if (compoundTag.hasUuid("BuddyGolem")) {
			this.field_18531 = compoundTag.getUuid("BuddyGolem");
		}

		this.setCanPickUpLoot(true);
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return false;
	}

	@Override
	protected SoundEvent method_5994() {
		return this.hasCustomer() ? SoundEvents.field_14933 : SoundEvents.field_15175;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15139;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15225;
	}

	public void method_19183() {
		SoundEvent soundEvent = this.getVillagerData().method_16924().method_19198().method_19166();
		if (soundEvent != null) {
			this.method_5783(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	public void method_7221(VillagerData villagerData) {
		VillagerData villagerData2 = this.getVillagerData();
		if (villagerData2.method_16924() != villagerData.method_16924()) {
			this.field_17721 = null;
		}

		this.field_6011.set(field_7445, villagerData);
	}

	@Override
	public VillagerData getVillagerData() {
		return this.field_6011.get(field_7445);
	}

	@Override
	protected void method_18008(TraderRecipe traderRecipe) {
		int i = 3 + this.random.nextInt(4);
		this.field_18536 = this.field_18536 + traderRecipe.method_19279();
		this.field_18530 = this.getCurrentCustomer();
		if (this.method_19188()) {
			this.field_18528 = 40;
			this.field_18529 = true;
			i += 5;
		}

		if (traderRecipe.getRewardExp()) {
			this.field_6002.spawnEntity(new ExperienceOrbEntity(this.field_6002, this.x, this.y + 0.5, this.z, i));
		}
	}

	@Override
	public void setAttacker(@Nullable LivingEntity livingEntity) {
		if (livingEntity != null && this.field_6002 instanceof ServerWorld) {
			((ServerWorld)this.field_6002).method_19496(class_4151.field_18476, livingEntity, this);
		}

		super.setAttacker(livingEntity);
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		this.method_19176(class_4140.field_18438);
		this.method_19176(class_4140.field_18439);
		this.method_19176(class_4140.field_18440);
		super.onDeath(damageSource);
	}

	private void method_19176(class_4140<class_4208> arg) {
		if (this.field_6002 instanceof ServerWorld) {
			MinecraftServer minecraftServer = ((ServerWorld)this.field_6002).getServer();
			this.field_18321.method_18904(arg).ifPresent(argx -> minecraftServer.method_3847(argx.method_19442()).method_19494().method_19129(argx.method_19446()));
		}
	}

	public boolean method_19184() {
		return this.field_18533 + this.method_19189() >= 12 && this.getBreedingAge() == 0;
	}

	public void method_19185() {
		if (this.field_18533 < 12 && this.method_19189() != 0) {
			Set<Item> set = field_18526.keySet();

			for (int i = 0; i < this.getInventory().getInvSize(); i++) {
				ItemStack itemStack = this.getInventory().method_5438(i);
				Item item = itemStack.getItem();
				if (!itemStack.isEmpty() || set.contains(item)) {
					int j = itemStack.getAmount();

					for (int k = j; k > 0; k--) {
						this.field_18533 = (byte)(this.field_18533 + (Integer)field_18526.get(item));
						this.getInventory().method_5434(i, 1);
						if (this.field_18533 >= 12) {
							return;
						}
					}
				}
			}
		}
	}

	public void method_19193(int i) {
		this.field_18533 = (byte)(this.field_18533 - i);
	}

	public void method_16917(TraderRecipeList traderRecipeList) {
		this.field_17721 = traderRecipeList;
	}

	private boolean method_19188() {
		int i = this.getVillagerData().getLevel();
		return VillagerData.method_19196(i) && this.field_18536 >= VillagerData.method_19195(i);
	}

	private void levelUp() {
		this.method_7221(this.getVillagerData().withLevel(this.getVillagerData().getLevel() + 1));
		this.fillRecipes();
	}

	@Override
	public TextComponent method_5476() {
		AbstractScoreboardTeam abstractScoreboardTeam = this.method_5781();
		TextComponent textComponent = this.method_5797();
		if (textComponent != null) {
			return ScoreboardTeam.method_1142(abstractScoreboardTeam, textComponent)
				.modifyStyle(style -> style.setHoverEvent(this.method_5769()).setInsertion(this.getUuidAsString()));
		} else {
			VillagerProfession villagerProfession = this.getVillagerData().method_16924();
			TextComponent textComponent2 = new TranslatableTextComponent(
					this.method_5864().getTranslationKey() + '.' + Registry.VILLAGER_PROFESSION.method_10221(villagerProfession).getPath()
				)
				.modifyStyle(style -> style.setHoverEvent(this.method_5769()).setInsertion(this.getUuidAsString()));
			if (abstractScoreboardTeam != null) {
				textComponent2.applyFormat(abstractScoreboardTeam.getColor());
			}

			return textComponent2;
		}
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
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		if (spawnType == SpawnType.field_16466) {
			this.method_7221(this.getVillagerData().method_16921(VillagerProfession.field_17051));
		}

		if (spawnType == SpawnType.field_16462 || spawnType == SpawnType.field_16465 || spawnType == SpawnType.field_16469) {
			this.method_7221(this.getVillagerData().method_16922(VillagerType.method_16930(iWorld.method_8310(new BlockPos(this)))));
		}

		return super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	public VillagerEntity method_7225(PassiveEntity passiveEntity) {
		double d = this.random.nextDouble();
		VillagerType villagerType;
		if (d < 0.5) {
			villagerType = VillagerType.method_16930(this.field_6002.method_8310(new BlockPos(this)));
		} else if (d < 0.75) {
			villagerType = this.getVillagerData().method_16919();
		} else {
			villagerType = ((VillagerEntity)passiveEntity).getVillagerData().method_16919();
		}

		VillagerEntity villagerEntity = new VillagerEntity(EntityType.VILLAGER, this.field_6002, villagerType);
		villagerEntity.method_5943(this.field_6002, this.field_6002.method_8404(new BlockPos(villagerEntity)), SpawnType.field_16466, null, null);
		return villagerEntity;
	}

	@Override
	public void method_5800(LightningEntity lightningEntity) {
		WitchEntity witchEntity = EntityType.WITCH.method_5883(this.field_6002);
		witchEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
		witchEntity.method_5943(this.field_6002, this.field_6002.method_8404(new BlockPos(witchEntity)), SpawnType.field_16468, null, null);
		witchEntity.setAiDisabled(this.isAiDisabled());
		if (this.hasCustomName()) {
			witchEntity.method_5665(this.method_5797());
			witchEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		this.field_6002.spawnEntity(witchEntity);
		this.invalidate();
	}

	@Override
	protected void method_5949(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.method_6983();
		Item item = itemStack.getItem();
		VillagerProfession villagerProfession = this.getVillagerData().method_16924();
		if (field_18527.contains(item) || villagerProfession.method_19199().contains(item)) {
			if (villagerProfession == VillagerProfession.field_17056 && item == Items.field_8861) {
				int i = itemStack.getAmount() / 3;
				if (i > 0) {
					ItemStack itemStack2 = this.getInventory().method_5491(new ItemStack(Items.field_8229, i));
					itemStack.subtractAmount(i * 3);
					if (!itemStack2.isEmpty()) {
						this.method_5699(itemStack2, 0.5F);
					}
				}
			}

			ItemStack itemStack3 = this.getInventory().method_5491(itemStack);
			if (itemStack3.isEmpty()) {
				itemEntity.invalidate();
			} else {
				itemStack.setAmount(itemStack3.getAmount());
			}
		}
	}

	public boolean wantsToStartBreeding() {
		return this.method_19189() >= 24;
	}

	public boolean canBreed() {
		boolean bl = this.getVillagerData().method_16924() == VillagerProfession.field_17056;
		int i = this.method_19189();
		return bl ? i < 60 : i < 12;
	}

	private int method_19189() {
		BasicInventory basicInventory = this.getInventory();
		return field_18526.entrySet().stream().mapToInt(entry -> basicInventory.method_18861((Item)entry.getKey()) * (Integer)entry.getValue()).sum();
	}

	@Override
	protected void fillRecipes() {
		VillagerData villagerData = this.getVillagerData();
		Int2ObjectMap<Trades.Factory[]> int2ObjectMap = (Int2ObjectMap<Trades.Factory[]>)Trades.PROFESSION_TO_LEVELED_TRADE.get(villagerData.method_16924());
		if (int2ObjectMap != null && !int2ObjectMap.isEmpty()) {
			Trades.Factory[] factorys = int2ObjectMap.get(villagerData.getLevel());
			if (factorys != null) {
				TraderRecipeList traderRecipeList = this.method_8264();
				this.method_19170(traderRecipeList, factorys, 2);
			}
		}
	}

	public void method_19177(VillagerEntity villagerEntity, long l) {
		if ((l < this.field_18535 || l >= this.field_18535 + 1200L) && (l < villagerEntity.field_18535 || l >= villagerEntity.field_18535 + 1200L)) {
			if (this.method_19173(this) || this.method_19171(l)) {
				this.field_18534.method_19072(this.getUuid(), class_4139.field_18429, class_4139.field_18429.field_18432);
			}

			this.field_18534.method_19061(villagerEntity.field_18534, this.random, 10);
			this.field_18535 = l;
			villagerEntity.field_18535 = l;
			if (this.field_18534.method_19062(class_4139.field_18429) >= 10L) {
				BoundingBox boundingBox = this.method_5829().stretch(80.0, 80.0, 80.0);
				List<VillagerEntity> list = this.field_6002.method_8390(VillagerEntity.class, boundingBox, this::method_19173);
				if (list.size() >= 10) {
					IronGolemEntity ironGolemEntity = this.method_19190();
					if (ironGolemEntity != null) {
						UUID uUID = ironGolemEntity.getUuid();

						for (VillagerEntity villagerEntity2 : list) {
							for (VillagerEntity villagerEntity3 : list) {
								villagerEntity2.field_18534.method_19072(villagerEntity3.getUuid(), class_4139.field_18429, -class_4139.field_18429.field_18432);
							}

							villagerEntity2.field_18531 = uUID;
						}
					}
				}
			}
		}
	}

	private boolean method_19173(Entity entity) {
		return this.field_18534.method_19073(entity.getUuid(), arg -> arg == class_4139.field_18429) > 30;
	}

	private boolean method_19171(long l) {
		if (this.field_18531 == null) {
			return true;
		} else {
			if (this.field_18532 < l + 1200L) {
				this.field_18532 = l + 1200L;
				Entity entity = ((ServerWorld)this.field_6002).getEntity(this.field_18531);
				if (entity == null || !entity.isValid() || this.squaredDistanceTo(entity) > 80.0) {
					this.field_18531 = null;
					return true;
				}
			}

			return false;
		}
	}

	@Nullable
	private IronGolemEntity method_19190() {
		BlockPos blockPos = new BlockPos(this);

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(this.field_6002.random.nextInt(16) - 8, this.field_6002.random.nextInt(6) - 3, this.field_6002.random.nextInt(16) - 8);
			IronGolemEntity ironGolemEntity = EntityType.IRON_GOLEM.method_5888(this.field_6002, null, null, null, blockPos2, SpawnType.field_16471, false, false);
			if (ironGolemEntity != null) {
				if (ironGolemEntity.method_5979(this.field_6002, SpawnType.field_16471) && ironGolemEntity.method_5957(this.field_6002)) {
					this.field_6002.spawnEntity(ironGolemEntity);
					return ironGolemEntity;
				}

				ironGolemEntity.invalidate();
			}
		}

		return null;
	}

	@Override
	public void method_18870(class_4151 arg, Entity entity) {
		if (arg == class_4151.field_18474) {
			this.field_18534.method_19072(entity.getUuid(), class_4139.field_18427, 25);
		} else if (arg == class_4151.field_18478) {
			this.field_18534.method_19072(entity.getUuid(), class_4139.field_18428, 2);
		} else if (arg == class_4151.field_18476) {
			this.field_18534.method_19072(entity.getUuid(), class_4139.field_18425, 25);
		} else if (arg == class_4151.field_18477) {
			this.field_18534.method_19072(entity.getUuid(), class_4139.field_18424, 25);
		}
	}

	@Override
	public int method_19269() {
		return this.field_18536;
	}

	public long method_19186() {
		return this.field_18537;
	}

	@Override
	protected void method_18409() {
		super.method_18409();
		class_4209.method_19468(this.field_6002, this, this.field_18321);
	}
}
