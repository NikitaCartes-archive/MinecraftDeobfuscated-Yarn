package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Villager;
import net.minecraft.village.VillagerRecipe;
import net.minecraft.village.VillagerRecipeList;
import net.minecraft.world.World;

public abstract class AbstractVillagerEntity extends PassiveEntity implements Npc, Villager {
	@Nullable
	private PlayerEntity customer;
	@Nullable
	protected VillagerRecipeList recipes;
	private final BasicInventory field_17723 = new BasicInventory(8);

	public AbstractVillagerEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public float getEyeHeight() {
		return this.isChild() ? 0.81F : 1.62F;
	}

	@Override
	public void setCurrentCustomer(@Nullable PlayerEntity playerEntity) {
		this.customer = playerEntity;
	}

	@Nullable
	@Override
	public PlayerEntity getCurrentCustomer() {
		return this.customer;
	}

	public boolean hasCustomer() {
		return this.customer != null;
	}

	@Override
	public VillagerRecipeList getRecipes() {
		if (this.recipes == null) {
			this.recipes = new VillagerRecipeList();
			this.method_7237();
		}

		return this.recipes;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setServerRecipes(@Nullable VillagerRecipeList villagerRecipeList) {
	}

	@Override
	public void useRecipe(VillagerRecipe villagerRecipe) {
		villagerRecipe.use();
		this.field_6191 = -this.getMinAmbientSoundDelay();
		this.playSound(this.method_18010(), this.getSoundVolume(), this.getSoundPitch());
		this.method_18008(villagerRecipe);
		if (this.customer instanceof ServerPlayerEntity) {
			Criterions.VILLAGER_TRADE.handle((ServerPlayerEntity)this.customer, this, villagerRecipe.getSellItem());
		}
	}

	protected abstract void method_18008(VillagerRecipe villagerRecipe);

	@Override
	public void onSellingItem(ItemStack itemStack) {
		if (!this.world.isClient && this.field_6191 > -this.getMinAmbientSoundDelay() + 20) {
			this.field_6191 = -this.getMinAmbientSoundDelay();
			this.playSound(this.method_18012(!itemStack.isEmpty()), this.getSoundVolume(), this.getSoundPitch());
		}
	}

	protected SoundEvent method_18010() {
		return SoundEvents.field_14815;
	}

	protected SoundEvent method_18012(boolean bl) {
		return bl ? SoundEvents.field_14815 : SoundEvents.field_15008;
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		VillagerRecipeList villagerRecipeList = this.getRecipes();
		if (!villagerRecipeList.isEmpty()) {
			compoundTag.put("Offers", villagerRecipeList.deserialize());
		}

		ListTag listTag = new ListTag();

		for (int i = 0; i < this.field_17723.getInvSize(); i++) {
			ItemStack itemStack = this.field_17723.getInvStack(i);
			if (!itemStack.isEmpty()) {
				listTag.add(itemStack.toTag(new CompoundTag()));
			}
		}

		compoundTag.put("Inventory", listTag);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("Offers", 10)) {
			this.recipes = new VillagerRecipeList(compoundTag.getCompound("Offers"));
		}

		ListTag listTag = compoundTag.getList("Inventory", 10);

		for (int i = 0; i < listTag.size(); i++) {
			ItemStack itemStack = ItemStack.fromTag(listTag.getCompoundTag(i));
			if (!itemStack.isEmpty()) {
				this.field_17723.add(itemStack);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	protected void method_18007(ParticleParameters particleParameters) {
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

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return false;
	}

	public BasicInventory method_18011() {
		return this.field_17723;
	}

	@Override
	public boolean method_5758(int i, ItemStack itemStack) {
		if (super.method_5758(i, itemStack)) {
			return true;
		} else {
			int j = i - 300;
			if (j >= 0 && j < this.field_17723.getInvSize()) {
				this.field_17723.setInvStack(j, itemStack);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public World getVillagerWorld() {
		return this.world;
	}

	@Override
	public BlockPos getVillagerPos() {
		return new BlockPos(this);
	}

	protected abstract void method_7237();
}
