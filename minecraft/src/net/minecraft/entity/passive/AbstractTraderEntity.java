package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Npc;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderRecipe;
import net.minecraft.village.TraderRecipeList;
import net.minecraft.village.Trades;
import net.minecraft.world.World;

public abstract class AbstractTraderEntity extends PassiveEntity implements Npc, Trader {
	@Nullable
	private PlayerEntity field_17722;
	@Nullable
	protected TraderRecipeList field_17721;
	private final BasicInventory inventory = new BasicInventory(8);

	public AbstractTraderEntity(EntityType<? extends AbstractTraderEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public int method_19269() {
		return 0;
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return this.isChild() ? 0.81F : 1.62F;
	}

	@Override
	public void setCurrentCustomer(@Nullable PlayerEntity playerEntity) {
		this.field_17722 = playerEntity;
	}

	@Nullable
	@Override
	public PlayerEntity getCurrentCustomer() {
		return this.field_17722;
	}

	public boolean hasCustomer() {
		return this.field_17722 != null;
	}

	@Override
	public TraderRecipeList method_8264() {
		if (this.field_17721 == null) {
			this.field_17721 = new TraderRecipeList();
			this.fillRecipes();
		}

		return this.field_17721;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_8261(@Nullable TraderRecipeList traderRecipeList) {
	}

	@Override
	public void method_19271(int i) {
	}

	@Override
	public void method_8262(TraderRecipe traderRecipe) {
		this.ambientSoundChance = -this.getMinAmbientSoundDelay();
		this.method_5783(this.method_18010(), this.getSoundVolume(), this.getSoundPitch());
		this.method_18008(traderRecipe);
		if (this.field_17722 instanceof ServerPlayerEntity) {
			Criterions.VILLAGER_TRADE.method_9146((ServerPlayerEntity)this.field_17722, this, traderRecipe.getModifiableSellItem());
		}
	}

	protected abstract void method_18008(TraderRecipe traderRecipe);

	@Override
	public boolean method_19270() {
		return true;
	}

	@Override
	public void onSellingItem(ItemStack itemStack) {
		if (!this.field_6002.isClient && this.ambientSoundChance > -this.getMinAmbientSoundDelay() + 20) {
			this.ambientSoundChance = -this.getMinAmbientSoundDelay();
			this.method_5783(this.method_18012(!itemStack.isEmpty()), this.getSoundVolume(), this.getSoundPitch());
		}
	}

	protected SoundEvent method_18010() {
		return SoundEvents.field_14815;
	}

	protected SoundEvent method_18012(boolean bl) {
		return bl ? SoundEvents.field_14815 : SoundEvents.field_15008;
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		TraderRecipeList traderRecipeList = this.method_8264();
		if (!traderRecipeList.isEmpty()) {
			compoundTag.method_10566("Offers", traderRecipeList.method_8268());
		}

		ListTag listTag = new ListTag();

		for (int i = 0; i < this.inventory.getInvSize(); i++) {
			ItemStack itemStack = this.inventory.method_5438(i);
			if (!itemStack.isEmpty()) {
				listTag.add(itemStack.method_7953(new CompoundTag()));
			}
		}

		compoundTag.method_10566("Inventory", listTag);
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("Offers", 10)) {
			this.field_17721 = new TraderRecipeList(compoundTag.getCompound("Offers"));
		}

		ListTag listTag = compoundTag.method_10554("Inventory", 10);

		for (int i = 0; i < listTag.size(); i++) {
			ItemStack itemStack = ItemStack.method_7915(listTag.getCompoundTag(i));
			if (!itemStack.isEmpty()) {
				this.inventory.method_5491(itemStack);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	protected void method_18007(ParticleParameters particleParameters) {
		for (int i = 0; i < 5; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.field_6002
				.method_8406(
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

	@Override
	public boolean method_5931(PlayerEntity playerEntity) {
		return false;
	}

	public BasicInventory getInventory() {
		return this.inventory;
	}

	@Override
	public boolean method_5758(int i, ItemStack itemStack) {
		if (super.method_5758(i, itemStack)) {
			return true;
		} else {
			int j = i - 300;
			if (j >= 0 && j < this.inventory.getInvSize()) {
				this.inventory.method_5447(j, itemStack);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public World method_8260() {
		return this.field_6002;
	}

	protected abstract void fillRecipes();

	protected void method_19170(TraderRecipeList traderRecipeList, Trades.Factory[] factorys, int i) {
		Set<Integer> set = Sets.<Integer>newHashSet();
		if (factorys.length > i) {
			while (set.size() < i) {
				set.add(this.random.nextInt(factorys.length));
			}
		} else {
			for (int j = 0; j < factorys.length; j++) {
				set.add(j);
			}
		}

		for (Integer integer : set) {
			Trades.Factory factory = factorys[integer];
			TraderRecipe traderRecipe = factory.method_7246(this, this.random);
			if (traderRecipe != null) {
				traderRecipeList.add(traderRecipe);
			}
		}
	}
}
