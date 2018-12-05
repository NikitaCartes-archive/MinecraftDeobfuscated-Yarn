package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3730;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
	private final EntityType<?> field_7991;

	public FishBucketItem(EntityType<?> entityType, Fluid fluid, Item.Settings settings) {
		super(fluid, settings);
		this.field_7991 = entityType;
	}

	@Override
	public void method_7728(World world, ItemStack itemStack, BlockPos blockPos) {
		if (!world.isRemote) {
			this.method_7824(world, itemStack, blockPos);
		}
	}

	@Override
	protected void method_7727(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos) {
		iWorld.playSound(playerEntity, blockPos, SoundEvents.field_14912, SoundCategory.field_15254, 1.0F, 1.0F);
	}

	private void method_7824(World world, ItemStack itemStack, BlockPos blockPos) {
		Entity entity = this.field_7991.spawnFromItemStack(world, itemStack, null, blockPos, class_3730.field_16473, true, false);
		if (entity != null) {
			((FishEntity)entity).method_6454(true);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addInformation(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipOptions tooltipOptions) {
		if (this.field_7991 == EntityType.TROPICAL_FISH) {
			CompoundTag compoundTag = itemStack.getTag();
			if (compoundTag != null && compoundTag.containsKey("BucketVariantTag", 3)) {
				int i = compoundTag.getInt("BucketVariantTag");
				TextFormat[] textFormats = new TextFormat[]{TextFormat.ITALIC, TextFormat.GRAY};
				String string = "color.minecraft." + TropicalFishEntity.method_6652(i);
				String string2 = "color.minecraft." + TropicalFishEntity.method_6651(i);

				for (int j = 0; j < TropicalFishEntity.field_6879.length; j++) {
					if (i == TropicalFishEntity.field_6879[j]) {
						list.add(new TranslatableTextComponent(TropicalFishEntity.method_6649(j)).applyFormat(textFormats));
						return;
					}
				}

				list.add(new TranslatableTextComponent(TropicalFishEntity.method_6657(i)).applyFormat(textFormats));
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
