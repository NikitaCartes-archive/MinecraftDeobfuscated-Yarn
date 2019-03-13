package net.minecraft.entity.mob;

import com.mojang.datafixers.Dynamic;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4151;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.entity.EntityData;
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
	private static final TrackedData<Boolean> field_7423 = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<VillagerData> field_7420 = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
	private int conversionTimer;
	private UUID converter;
	private CompoundTag field_17047;

	public ZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> entityType, World world) {
		super(entityType, world);
		this.method_7195(this.getVillagerData().method_16921(Registry.VILLAGER_PROFESSION.getRandom(this.random)));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7423, false);
		this.field_6011.startTracking(field_7420, new VillagerData(VillagerType.field_17073, VillagerProfession.field_17051, 1));
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.method_10566("VillagerData", this.getVillagerData().serialize(NbtOps.INSTANCE));
		if (this.field_17047 != null) {
			compoundTag.method_10566("Offers", this.field_17047);
		}

		compoundTag.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : -1);
		if (this.converter != null) {
			compoundTag.putUuid("ConversionPlayer", this.converter);
		}
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("VillagerData", 10)) {
			this.method_7195(new VillagerData(new Dynamic<>(NbtOps.INSTANCE, compoundTag.method_10580("VillagerData"))));
		}

		if (compoundTag.containsKey("Offers", 10)) {
			this.field_17047 = compoundTag.getCompound("Offers");
		}

		if (compoundTag.containsKey("ConversionTime", 99) && compoundTag.getInt("ConversionTime") > -1) {
			this.setConverting(compoundTag.hasUuid("ConversionPlayer") ? compoundTag.getUuid("ConversionPlayer") : null, compoundTag.getInt("ConversionTime"));
		}
	}

	@Override
	public void update() {
		if (!this.field_6002.isClient && this.isValid() && this.isConverting()) {
			int i = this.method_7194();
			this.conversionTimer -= i;
			if (this.conversionTimer <= 0) {
				this.method_7197((ServerWorld)this.field_6002);
			}
		}

		super.update();
	}

	@Override
	public boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (itemStack.getItem() == Items.field_8463 && this.hasPotionEffect(StatusEffects.field_5911)) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.subtractAmount(1);
			}

			if (!this.field_6002.isClient) {
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
		return this.method_5841().get(field_7423);
	}

	protected void setConverting(@Nullable UUID uUID, int i) {
		this.converter = uUID;
		this.conversionTimer = i;
		this.method_5841().set(field_7423, true);
		this.removeStatusEffect(StatusEffects.field_5911);
		this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5910, i, Math.min(this.field_6002.getDifficulty().getId() - 1, 0)));
		this.field_6002.summonParticle(this, (byte)16);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 16) {
			if (!this.isSilent()) {
				this.field_6002
					.method_8486(
						this.x + 0.5,
						this.y + 0.5,
						this.z + 0.5,
						SoundEvents.field_14905,
						this.method_5634(),
						1.0F + this.random.nextFloat(),
						this.random.nextFloat() * 0.7F + 0.3F,
						false
					);
			}
		} else {
			super.method_5711(b);
		}
	}

	protected void method_7197(ServerWorld serverWorld) {
		VillagerEntity villagerEntity = EntityType.VILLAGER.method_5883(serverWorld);
		villagerEntity.setPositionAndAngles(this);
		villagerEntity.method_7221(this.getVillagerData());
		if (this.field_17047 != null) {
			villagerEntity.method_16917(new TraderRecipeList(this.field_17047));
		}

		villagerEntity.method_5943(serverWorld, serverWorld.method_8404(new BlockPos(villagerEntity)), SpawnType.field_16468, null, null);
		if (this.isChild()) {
			villagerEntity.setBreedingAge(-24000);
		}

		this.invalidate();
		villagerEntity.setAiDisabled(this.isAiDisabled());
		if (this.hasCustomName()) {
			villagerEntity.method_5665(this.method_5797());
			villagerEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		serverWorld.spawnEntity(villagerEntity);
		if (this.converter != null) {
			PlayerEntity playerEntity = serverWorld.method_18470(this.converter);
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.CURED_ZOMBIE_VILLAGER.method_8831((ServerPlayerEntity)playerEntity, this, villagerEntity);
				serverWorld.method_19496(class_4151.field_18474, playerEntity, villagerEntity);
			}
		}

		villagerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5916, 200, 0));
		serverWorld.method_8444(null, 1027, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
	}

	protected int method_7194() {
		int i = 1;
		if (this.random.nextFloat() < 0.01F) {
			int j = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = (int)this.x - 4; k < (int)this.x + 4 && j < 14; k++) {
				for (int l = (int)this.y - 4; l < (int)this.y + 4 && j < 14; l++) {
					for (int m = (int)this.z - 4; m < (int)this.z + 4 && j < 14; m++) {
						Block block = this.field_6002.method_8320(mutable.set(k, l, m)).getBlock();
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
	public SoundEvent method_5994() {
		return SoundEvents.field_15056;
	}

	@Override
	public SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14728;
	}

	@Override
	public SoundEvent method_6002() {
		return SoundEvents.field_14996;
	}

	@Override
	public SoundEvent method_7207() {
		return SoundEvents.field_14841;
	}

	@Override
	protected ItemStack method_7215() {
		return ItemStack.EMPTY;
	}

	public void method_16916(CompoundTag compoundTag) {
		this.field_17047 = compoundTag;
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		if (spawnType == SpawnType.field_16462 || spawnType == SpawnType.field_16465 || spawnType == SpawnType.field_16469) {
			this.method_7195(this.getVillagerData().method_16922(VillagerType.method_16930(iWorld.method_8310(new BlockPos(this)))));
		}

		return super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	public void method_7195(VillagerData villagerData) {
		VillagerData villagerData2 = this.getVillagerData();
		if (villagerData2.method_16924() != villagerData.method_16924()) {
			this.field_17047 = null;
		}

		this.field_6011.set(field_7420, villagerData);
	}

	@Override
	public VillagerData getVillagerData() {
		return this.field_6011.get(field_7420);
	}
}
