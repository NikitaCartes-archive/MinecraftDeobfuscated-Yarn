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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TraderRecipeList;
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
	private CompoundTag offerData;

	public ZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> entityType, World world) {
		super(entityType, world);
		this.setVillagerData(this.getVillagerData().withProfession(Registry.VILLAGER_PROFESSION.getRandom(this.random)));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CONVERTING, false);
		this.dataTracker.startTracking(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.field_17051, 1));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.put("VillagerData", this.getVillagerData().serialize(NbtOps.INSTANCE));
		if (this.offerData != null) {
			compoundTag.put("Offers", this.offerData);
		}

		compoundTag.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : -1);
		if (this.converter != null) {
			compoundTag.putUuid("ConversionPlayer", this.converter);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("VillagerData", 10)) {
			this.setVillagerData(new VillagerData(new Dynamic<>(NbtOps.INSTANCE, compoundTag.getTag("VillagerData"))));
		}

		if (compoundTag.containsKey("Offers", 10)) {
			this.offerData = compoundTag.getCompound("Offers");
		}

		if (compoundTag.containsKey("ConversionTime", 99) && compoundTag.getInt("ConversionTime") > -1) {
			this.setConverting(compoundTag.hasUuid("ConversionPlayer") ? compoundTag.getUuid("ConversionPlayer") : null, compoundTag.getInt("ConversionTime"));
		}
	}

	@Override
	public void update() {
		if (!this.world.isClient && this.isValid() && this.isConverting()) {
			int i = this.method_7194();
			this.conversionTimer -= i;
			if (this.conversionTimer <= 0) {
				this.finishConversion((ServerWorld)this.world);
			}
		}

		super.update();
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8463 && this.hasPotionEffect(StatusEffects.field_5911)) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.subtractAmount(1);
			}

			if (!this.world.isClient) {
				this.setConverting(playerEntity.getUuid(), this.random.nextInt(2401) + 3600);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean method_7209() {
		return false;
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return !this.isConverting();
	}

	public boolean isConverting() {
		return this.getDataTracker().get(CONVERTING);
	}

	protected void setConverting(@Nullable UUID uUID, int i) {
		this.converter = uUID;
		this.conversionTimer = i;
		this.getDataTracker().set(CONVERTING, true);
		this.removeStatusEffect(StatusEffects.field_5911);
		this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5910, i, Math.min(this.world.getDifficulty().getId() - 1, 0)));
		this.world.summonParticle(this, (byte)16);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 16) {
			if (!this.isSilent()) {
				this.world
					.playSound(
						this.x + 0.5,
						this.y + 0.5,
						this.z + 0.5,
						SoundEvents.field_14905,
						this.getSoundCategory(),
						1.0F + this.random.nextFloat(),
						this.random.nextFloat() * 0.7F + 0.3F,
						false
					);
			}
		} else {
			super.method_5711(b);
		}
	}

	protected void finishConversion(ServerWorld serverWorld) {
		VillagerEntity villagerEntity = EntityType.VILLAGER.create(serverWorld);
		villagerEntity.setPositionAndAngles(this);
		villagerEntity.setVillagerData(this.getVillagerData());
		if (this.offerData != null) {
			villagerEntity.setRecipes(new TraderRecipeList(this.offerData));
		}

		villagerEntity.prepareEntityData(serverWorld, serverWorld.getLocalDifficulty(new BlockPos(villagerEntity)), SpawnType.field_16468, null, null);
		if (this.isChild()) {
			villagerEntity.setBreedingAge(-24000);
		}

		this.invalidate();
		villagerEntity.setAiDisabled(this.isAiDisabled());
		if (this.hasCustomName()) {
			villagerEntity.setCustomName(this.getCustomName());
			villagerEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		serverWorld.spawnEntity(villagerEntity);
		if (this.converter != null) {
			PlayerEntity playerEntity = serverWorld.method_18470(this.converter);
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.CURED_ZOMBIE_VILLAGER.handle((ServerPlayerEntity)playerEntity, this, villagerEntity);
				serverWorld.handleInteraction(EntityInteraction.field_18474, playerEntity, villagerEntity);
			}
		}

		villagerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5916, 200, 0));
		serverWorld.playEvent(null, 1027, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
	}

	protected int method_7194() {
		int i = 1;
		if (this.random.nextFloat() < 0.01F) {
			int j = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = (int)this.x - 4; k < (int)this.x + 4 && j < 14; k++) {
				for (int l = (int)this.y - 4; l < (int)this.y + 4 && j < 14; l++) {
					for (int m = (int)this.z - 4; m < (int)this.z + 4 && j < 14; m++) {
						Block block = this.world.getBlockState(mutable.set(k, l, m)).getBlock();
						if (block == Blocks.field_10576 || block instanceof BedBlock) {
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
		return this.isChild() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.field_15056;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14728;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.field_14996;
	}

	@Override
	public SoundEvent getStepSound() {
		return SoundEvents.field_14841;
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}

	public void setOfferData(CompoundTag compoundTag) {
		this.offerData = compoundTag;
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		if (spawnType == SpawnType.field_16462 || spawnType == SpawnType.field_16465 || spawnType == SpawnType.field_16469) {
			this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(iWorld.getBiome(new BlockPos(this)))));
		}

		return super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
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
}
