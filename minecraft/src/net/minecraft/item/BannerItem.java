package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public class BannerItem extends WallStandingBlockItem {
	private static final String TRANSLATION_KEY_PREFIX = "block.minecraft.banner.";

	public BannerItem(Block block, Block block2, Item.Settings settings) {
		super(block, block2, settings);
		Validate.isInstanceOf(AbstractBannerBlock.class, block);
		Validate.isInstanceOf(AbstractBannerBlock.class, block2);
	}

	public static void appendBannerTooltip(ItemStack stack, List<Text> tooltip) {
		NbtCompound nbtCompound = stack.getSubTag("BlockEntityTag");
		if (nbtCompound != null && nbtCompound.contains("Patterns")) {
			NbtList nbtList = nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < nbtList.size() && i < 6; i++) {
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				DyeColor dyeColor = DyeColor.byId(nbtCompound2.getInt("Color"));
				BannerPattern bannerPattern = BannerPattern.byId(nbtCompound2.getString("Pattern"));
				if (bannerPattern != null) {
					tooltip.add(new TranslatableText("block.minecraft.banner." + bannerPattern.getName() + "." + dyeColor.getName()).formatted(Formatting.GRAY));
				}
			}
		}
	}

	public DyeColor getColor() {
		return ((AbstractBannerBlock)this.getBlock()).getColor();
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		appendBannerTooltip(stack, tooltip);
	}
}
