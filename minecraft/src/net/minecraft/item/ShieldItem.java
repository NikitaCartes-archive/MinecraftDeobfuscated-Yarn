package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.block.BannerItem;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.TextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ShieldItem extends Item {
	public ShieldItem(Item.Settings settings) {
		super(settings);
		this.method_7863(
			new Identifier("blocking"),
			(itemStack, world, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.method_6030() == itemStack ? 1.0F : 0.0F
		);
		DispenserBlock.method_10009(this, ArmorItem.field_7879);
	}

	@Override
	public String method_7866(ItemStack itemStack) {
		return itemStack.method_7941("BlockEntityTag") != null ? this.getTranslationKey() + '.' + getColor(itemStack).getName() : super.method_7866(itemStack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		BannerItem.method_7705(itemStack, list);
	}

	@Override
	public UseAction method_7853(ItemStack itemStack) {
		return UseAction.field_8949;
	}

	@Override
	public int method_7881(ItemStack itemStack) {
		return 72000;
	}

	@Override
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		playerEntity.setCurrentHand(hand);
		return new TypedActionResult<>(ActionResult.field_5812, itemStack);
	}

	@Override
	public boolean method_7878(ItemStack itemStack, ItemStack itemStack2) {
		return ItemTags.field_15537.contains(itemStack2.getItem()) || super.method_7878(itemStack, itemStack2);
	}

	public static DyeColor getColor(ItemStack itemStack) {
		return DyeColor.byId(itemStack.method_7911("BlockEntityTag").getInt("Base"));
	}
}
