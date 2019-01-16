package net.minecraft.entity.mob;

import com.mojang.datafixers.Dynamic;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.datafixers.NbtOps;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerRecipeList;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = VillagerDataContainer.class
	)})
public class ZombieVillagerEntity extends ZombieEntity implements VillagerDataContainer {
	private static final TrackedData<Boolean> CONVERTING = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
	private int conversionTimer;
	private UUID converter;
	private CompoundTag offerData;

	public ZombieVillagerEntity(World world) {
		super(EntityType.ZOMBIE_VILLAGER, world);
		this.setVillagerData(this.getVillagerData().withProfession(Registry.VILLAGER_PROFESSION.getRandom(this.random)));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CONVERTING, false);
		this.dataTracker.startTracking(VILLAGER_DATA, new VillagerData(VillagerType.field_17073, VillagerProfession.field_17051, 1));
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
		if (!this.world.isClient && this.isConverting()) {
			int i = this.method_7194();
			this.conversionTimer -= i;
			if (this.conversionTimer <= 0) {
				this.finishConversion();
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

	protected void finishConversion() {
		VillagerEntity villagerEntity = new VillagerEntity(this.world);
		villagerEntity.setPositionAndAngles(this);
		villagerEntity.setVillagerData(this.getVillagerData());
		if (this.offerData != null) {
			villagerEntity.setRecipes(new VillagerRecipeList(this.offerData));
		}

		villagerEntity.prepareEntityData(this.world, this.world.getLocalDifficulty(new BlockPos(villagerEntity)), SpawnType.field_16468, null, null);
		villagerEntity.setRecentlyRescued();
		if (this.isChild()) {
			villagerEntity.setBreedingAge(-24000);
		}

		this.world.removeEntity(this);
		villagerEntity.setAiDisabled(this.isAiDisabled());
		if (this.hasCustomName()) {
			villagerEntity.setCustomName(this.getCustomName());
			villagerEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		this.world.spawnEntity(villagerEntity);
		if (this.converter != null) {
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.converter);
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.CURED_ZOMBIE_VILLAGER.handle((ServerPlayerEntity)playerEntity, this, villagerEntity);
			}
		}

		villagerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5916, 200, 0));
		this.world.fireWorldEvent(null, 1027, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
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
	public SoundEvent getSoundStep() {
		return SoundEvents.field_14841;
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}

	public void setOfferData(CompoundTag compoundTag) {
		this.offerData = compoundTag;
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
