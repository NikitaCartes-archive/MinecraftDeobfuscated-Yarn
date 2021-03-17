package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public class BannerItem extends WallStandingBlockItem {
	public BannerItem(Block block, Block block2, Item.Settings settings) {
		super(block, block2, settings);
		Validate.isInstanceOf(AbstractBannerBlock.class, block);
		Validate.isInstanceOf(AbstractBannerBlock.class, block2);
	}

	@Environment(EnvType.CLIENT)
	public static void appendBannerTooltip(ItemStack stack, List<Text> tooltip) {
		NbtCompound nbtCompound = stack.getSubTag("BlockEntityTag");
		if (nbtCompound != null && nbtCompound.contains("Patterns")) {
			NbtList nbtList = nbtCompound.getList("Patterns", NbtTypeIds.COMPOUND);

			for (int i = 0; i < nbtList.size() && i < 6; i++) {
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				DyeColor dyeColor = DyeColor.byId(nbtCompound2.getInt("Color"));
				BannerPattern bannerPattern = BannerPattern.byId(nbtCompound2.getString("Pattern"));
				if (bannerPattern != null) {
					tooltip.add(new TranslatableText("block.minecraft.banner." + bannerPattern.getName() + '.' + dyeColor.getName()).formatted(Formatting.GRAY));
				}
			}
		}
	}

	public DyeColor getColor() {
		return ((AbstractBannerBlock)this.getBlock()).getColor();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		appendBannerTooltip(stack, tooltip);
	}
}
