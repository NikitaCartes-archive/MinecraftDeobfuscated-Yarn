package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FishBucketItem extends BucketItem {
	private final EntityType<?> fishType;

	public FishBucketItem(EntityType<?> entityType, Fluid fluid, Item.Settings settings) {
		super(fluid, settings);
		this.fishType = entityType;
	}

	@Override
	public void onEmptied(World world, ItemStack itemStack, BlockPos blockPos) {
		if (!world.isClient) {
			this.spawnFish(world, itemStack, blockPos);
		}
	}

	@Override
	protected void playEmptyingSound(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos) {
		iWorld.playSound(playerEntity, blockPos, SoundEvents.field_14912, SoundCategory.field_15254, 1.0F, 1.0F);
	}

	private void spawnFish(World world, ItemStack itemStack, BlockPos blockPos) {
		Entity entity = this.fishType.spawnFromItemStack(world, itemStack, null, blockPos, SpawnType.field_16473, true, false);
		if (entity != null) {
			((FishEntity)entity).setFromBucket(true);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<Component> list, TooltipContext tooltipContext) {
		if (this.fishType == EntityType.field_6111) {
			CompoundTag compoundTag = itemStack.getTag();
			if (compoundTag != null && compoundTag.containsKey("BucketVariantTag", 3)) {
				int i = compoundTag.getInt("BucketVariantTag");
				ChatFormat[] chatFormats = new ChatFormat[]{ChatFormat.field_1056, ChatFormat.field_1080};
				String string = "color.minecraft." + TropicalFishEntity.getBaseDyeColor(i);
				String string2 = "color.minecraft." + TropicalFishEntity.getPatternDyeColor(i);

				for (int j = 0; j < TropicalFishEntity.COMMON_VARIANTS.length; j++) {
					if (i == TropicalFishEntity.COMMON_VARIANTS[j]) {
						list.add(new TranslatableComponent(TropicalFishEntity.getToolTipForVariant(j)).applyFormat(chatFormats));
						return;
					}
				}

				list.add(new TranslatableComponent(TropicalFishEntity.getTranslationKey(i)).applyFormat(chatFormats));
				Component component = new TranslatableComponent(string);
				if (!string.equals(string2)) {
					component.append(", ").append(new TranslatableComponent(string2));
				}

				component.applyFormat(chatFormats);
				list.add(component);
			}
		}
	}
}
