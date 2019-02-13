package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
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
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		if (this.fishType == EntityType.TROPICAL_FISH) {
			CompoundTag compoundTag = itemStack.getTag();
			if (compoundTag != null && compoundTag.containsKey("BucketVariantTag", 3)) {
				int i = compoundTag.getInt("BucketVariantTag");
				TextFormat[] textFormats = new TextFormat[]{TextFormat.field_1056, TextFormat.field_1080};
				String string = "color.minecraft." + TropicalFishEntity.getBaseDyeColor(i);
				String string2 = "color.minecraft." + TropicalFishEntity.getPatternDyeColor(i);

				for (int j = 0; j < TropicalFishEntity.COMMON_VARIANTS.length; j++) {
					if (i == TropicalFishEntity.COMMON_VARIANTS[j]) {
						list.add(new TranslatableTextComponent(TropicalFishEntity.getToolTipForVariant(j)).applyFormat(textFormats));
						return;
					}
				}

				list.add(new TranslatableTextComponent(TropicalFishEntity.getTranslationKey(i)).applyFormat(textFormats));
				TextComponent textComponent = new TranslatableTextComponent(string);
				if (!string.equals(string2)) {
					textComponent.append(", ").append(new TranslatableTextComponent(string2));
				}

				textComponent.applyFormat(textFormats);
				list.add(textComponent);
			}
		}
	}
}
