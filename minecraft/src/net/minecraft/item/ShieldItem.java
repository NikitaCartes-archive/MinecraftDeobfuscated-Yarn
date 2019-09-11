package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ShieldItem extends Item {
	public ShieldItem(Item.Settings settings) {
		super(settings);
		this.addPropertyGetter(
			new Identifier("blocking"),
			(itemStack, world, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
		);
		DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
	}

	@Override
	public String getTranslationKey(ItemStack itemStack) {
		return itemStack.getSubTag("BlockEntityTag") != null ? this.getTranslationKey() + '.' + getColor(itemStack).getName() : super.getTranslationKey(itemStack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
		BannerItem.appendBannerTooltip(itemStack, list);
	}

	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.BLOCK;
	}

	@Override
	public int getMaxUseTime(ItemStack itemStack) {
		return 72000;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		playerEntity.setCurrentHand(hand);
		return TypedActionResult.method_22428(itemStack);
	}

	@Override
	public boolean canRepair(ItemStack itemStack, ItemStack itemStack2) {
		return ItemTags.PLANKS.contains(itemStack2.getItem()) || super.canRepair(itemStack, itemStack2);
	}

	public static DyeColor getColor(ItemStack itemStack) {
		return DyeColor.byId(itemStack.getOrCreateSubTag("BlockEntityTag").getInt("Base"));
	}
}
