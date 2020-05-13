package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FishBucketItem extends BucketItem {
	private final EntityType<?> fishType;

	public FishBucketItem(EntityType<?> type, Fluid fluid, Item.Settings settings) {
		super(fluid, settings);
		this.fishType = type;
	}

	@Override
	public void onEmptied(World world, ItemStack stack, BlockPos pos) {
		if (!world.isClient) {
			this.spawnFish(world, stack, pos);
		}
	}

	@Override
	protected void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
		world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
	}

	private void spawnFish(World world, ItemStack stack, BlockPos pos) {
		Entity entity = this.fishType.spawnFromItemStack(world, stack, null, pos, SpawnReason.BUCKET, true, false);
		if (entity != null) {
			((FishEntity)entity).setFromBucket(true);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (this.fishType == EntityType.TROPICAL_FISH) {
			CompoundTag compoundTag = stack.getTag();
			if (compoundTag != null && compoundTag.contains("BucketVariantTag", 3)) {
				int i = compoundTag.getInt("BucketVariantTag");
				Formatting[] formattings = new Formatting[]{Formatting.ITALIC, Formatting.GRAY};
				String string = "color.minecraft." + TropicalFishEntity.getBaseDyeColor(i);
				String string2 = "color.minecraft." + TropicalFishEntity.getPatternDyeColor(i);

				for (int j = 0; j < TropicalFishEntity.COMMON_VARIANTS.length; j++) {
					if (i == TropicalFishEntity.COMMON_VARIANTS[j]) {
						tooltip.add(new TranslatableText(TropicalFishEntity.getToolTipForVariant(j)).formatted(formattings));
						return;
					}
				}

				tooltip.add(new TranslatableText(TropicalFishEntity.getTranslationKey(i)).formatted(formattings));
				MutableText mutableText = new TranslatableText(string);
				if (!string.equals(string2)) {
					mutableText.append(", ").append(new TranslatableText(string2));
				}

				mutableText.formatted(formattings);
				tooltip.add(mutableText);
			}
		}
	}
}
