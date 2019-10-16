package net.minecraft.entity.mob;

import com.mojang.datafixers.Dynamic;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ZombieVillagerEntity extends ZombieEntity implements VillagerDataContainer {
	private static final TrackedData<Boolean> CONVERTING = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
	private int conversionTimer;
	private UUID converter;
	private Tag field_20299;
	private CompoundTag offerData;
	private int xp;

	public ZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> entityType, World world) {
		super(entityType, world);
		this.setVillagerData(this.getVillagerData().withProfession(Registry.VILLAGER_PROFESSION.getRandom(this.random)));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CONVERTING, false);
		this.dataTracker.startTracking(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.put("VillagerData", this.getVillagerData().serialize(NbtOps.INSTANCE));
		if (this.offerData != null) {
			compoundTag.put("Offers", this.offerData);
		}

		if (this.field_20299 != null) {
			compoundTag.put("Gossips", this.field_20299);
		}

		compoundTag.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : -1);
		if (this.converter != null) {
			compoundTag.putUuid("ConversionPlayer", this.converter);
		}

		compoundTag.putInt("Xp", this.xp);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.contains("VillagerData", 10)) {
			this.setVillagerData(new VillagerData(new Dynamic<>(NbtOps.INSTANCE, compoundTag.get("VillagerData"))));
		}

		if (compoundTag.contains("Offers", 10)) {
			this.offerData = compoundTag.getCompound("Offers");
		}

		if (compoundTag.contains("Gossips", 10)) {
			this.field_20299 = compoundTag.getList("Gossips", 10);
		}

		if (compoundTag.contains("ConversionTime", 99) && compoundTag.getInt("ConversionTime") > -1) {
			this.setConverting(compoundTag.containsUuid("ConversionPlayer") ? compoundTag.getUuid("ConversionPlayer") : null, compoundTag.getInt("ConversionTime"));
		}

		if (compoundTag.contains("Xp", 3)) {
			this.xp = compoundTag.getInt("Xp");
		}
	}

	@Override
	public void tick() {
		if (!this.world.isClient && this.isAlive() && this.isConverting()) {
			int i = this.getConversionRate();
			this.conversionTimer -= i;
			if (this.conversionTimer <= 0) {
				this.finishConversion((ServerWorld)this.world);
			}
		}

		super.tick();
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.GOLDEN_APPLE && this.hasStatusEffect(StatusEffects.WEAKNESS)) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.decrement(1);
			}

			if (!this.world.isClient) {
				this.setConverting(playerEntity.getUuid(), this.random.nextInt(2401) + 3600);
				playerEntity.method_23667(hand, true);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean canConvertInWater() {
		return false;
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return !this.isConverting();
	}

	public boolean isConverting() {
		return this.getDataTracker().get(CONVERTING);
	}

	private void setConverting(@Nullable UUID uUID, int i) {
		this.converter = uUID;
		this.conversionTimer = i;
		this.getDataTracker().set(CONVERTING, true);
		this.tryRemoveStatusEffect(StatusEffects.WEAKNESS);
		this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, i, Math.min(this.world.getDifficulty().getId() - 1, 0)));
		this.world.sendEntityStatus(this, (byte)16);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 16) {
			if (!this.isSilent()) {
				this.world
					.playSound(
						this.getX(),
						this.method_23320(),
						this.getZ(),
						SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE,
						this.getSoundCategory(),
						1.0F + this.random.nextFloat(),
						this.random.nextFloat() * 0.7F + 0.3F,
						false
					);
			}
		} else {
			super.handleStatus(b);
		}
	}

	private void finishConversion(ServerWorld serverWorld) {
		VillagerEntity villagerEntity = EntityType.VILLAGER.create(serverWorld);
		villagerEntity.copyPositionAndRotation(this);
		villagerEntity.setVillagerData(this.getVillagerData());
		if (this.field_20299 != null) {
			villagerEntity.method_21650(this.field_20299);
		}

		if (this.offerData != null) {
			villagerEntity.setOffers(new TraderOfferList(this.offerData));
		}

		villagerEntity.setExperience(this.xp);
		villagerEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(new BlockPos(villagerEntity)), SpawnType.CONVERSION, null, null);
		if (this.isBaby()) {
			villagerEntity.setBreedingAge(-24000);
		}

		this.remove();
		villagerEntity.setAiDisabled(this.isAiDisabled());
		if (this.hasCustomName()) {
			villagerEntity.setCustomName(this.getCustomName());
			villagerEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		if (this.isPersistent()) {
			villagerEntity.setPersistent();
		}

		villagerEntity.setInvulnerable(this.isInvulnerable());
		serverWorld.spawnEntity(villagerEntity);
		if (this.converter != null) {
			PlayerEntity playerEntity = serverWorld.getPlayerByUuid(this.converter);
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayerEntity)playerEntity, this, villagerEntity);
				serverWorld.handleInteraction(EntityInteraction.ZOMBIE_VILLAGER_CURED, playerEntity, villagerEntity);
			}
		}

		villagerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
		serverWorld.playLevelEvent(null, 1027, new BlockPos(this), 0);
	}

	private int getConversionRate() {
		int i = 1;
		if (this.random.nextFloat() < 0.01F) {
			int j = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = (int)this.getX() - 4; k < (int)this.getX() + 4 && j < 14; k++) {
				for (int l = (int)this.getY() - 4; l < (int)this.getY() + 4 && j < 14; l++) {
					for (int m = (int)this.getZ() - 4; m < (int)this.getZ() + 4 && j < 14; m++) {
						Block block = this.world.getBlockState(mutable.set(k, l, m)).getBlock();
						if (block == Blocks.IRON_BARS || block instanceof BedBlock) {
							if (this.random.nextFloat() < 0.3F) {
								i++;
							}

							j++;
						}
					}
				}
			}
		}

		return i;
	}

	@Override
	protected float getSoundPitch() {
		return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.ENTITY_ZOMBIE_VILLAGER_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH;
	}

	@Override
	public SoundEvent getStepSound() {
		return SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP;
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}

	public void setOfferData(CompoundTag compoundTag) {
		this.offerData = compoundTag;
	}

	public void method_21649(Tag tag) {
		this.field_20299 = tag;
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(iWorld.getBiome(new BlockPos(this)))));
		return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	public void setVillagerData(VillagerData villagerData) {
		VillagerData villagerData2 = this.getVillagerData();
		if (villagerData2.getProfession() != villagerData.getProfession()) {
			this.offerData = null;
		}

		this.dataTracker.set(VILLAGER_DATA, villagerData);
	}

	@Override
	public VillagerData getVillagerData() {
		return this.dataTracker.get(VILLAGER_DATA);
	}

	public void setXp(int i) {
		this.xp = i;
	}
}
