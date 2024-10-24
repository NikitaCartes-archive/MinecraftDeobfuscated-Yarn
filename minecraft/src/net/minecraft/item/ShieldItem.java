package net.minecraft.item;

import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ShieldItem extends Item {
	public static final int field_30918 = 5;
	public static final float MIN_DAMAGE_AMOUNT_TO_BREAK = 3.0F;

	public ShieldItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack) {
		DyeColor dyeColor = stack.get(DataComponentTypes.BASE_COLOR);
		return (Text)(dyeColor != null ? Text.translatable(this.translationKey + "." + dyeColor.getName()) : super.getName(stack));
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		BannerItem.appendBannerTooltip(stack, tooltip);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return 72000;
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		user.setCurrentHand(hand);
		return ActionResult.CONSUME;
	}
}
