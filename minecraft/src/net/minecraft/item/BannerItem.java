package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public class BannerItem extends WallStandingBlockItem {
	public BannerItem(Block block, Block block2, Item.Settings settings) {
		super(block, block2, settings);
		Validate.isInstanceOf(AbstractBannerBlock.class, block);
		Validate.isInstanceOf(AbstractBannerBlock.class, block2);
	}

	@Environment(EnvType.CLIENT)
	public static void buildBannerTooltip(ItemStack itemStack, List<Component> list) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("BlockEntityTag");
		if (compoundTag != null && compoundTag.containsKey("Patterns")) {
			ListTag listTag = compoundTag.getList("Patterns", 10);

			for (int i = 0; i < listTag.size() && i < 6; i++) {
				CompoundTag compoundTag2 = listTag.getCompoundTag(i);
				DyeColor dyeColor = DyeColor.byId(compoundTag2.getInt("Color"));
				BannerPattern bannerPattern = BannerPattern.byId(compoundTag2.getString("Pattern"));
				if (bannerPattern != null) {
					list.add(new TranslatableComponent("block.minecraft.banner." + bannerPattern.getName() + '.' + dyeColor.getName()).applyFormat(ChatFormat.field_1080));
				}
			}
		}
	}

	public DyeColor getColor() {
		return ((AbstractBannerBlock)this.getBlock()).getColor();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<Component> list, TooltipContext tooltipContext) {
		buildBannerTooltip(itemStack, list);
	}
}
