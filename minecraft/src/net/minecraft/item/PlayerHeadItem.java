package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

public class PlayerHeadItem extends VerticallyAttachableBlockItem {
	public PlayerHeadItem(Block block, Block wallBlock, Item.Settings settings) {
		super(block, wallBlock, settings, Direction.DOWN);
	}

	@Override
	public Text getName(ItemStack stack) {
		ProfileComponent profileComponent = stack.get(DataComponentTypes.PROFILE);
		return (Text)(profileComponent != null && profileComponent.name().isPresent()
			? Text.translatable(this.getTranslationKey() + ".named", profileComponent.name().get())
			: super.getName(stack));
	}

	@Override
	public void postProcessComponents(ItemStack stack) {
		ProfileComponent profileComponent = stack.get(DataComponentTypes.PROFILE);
		if (profileComponent != null && !profileComponent.isCompleted()) {
			profileComponent.getFuture().thenAcceptAsync(profile -> stack.set(DataComponentTypes.PROFILE, profile), SkullBlockEntity.EXECUTOR);
		}
	}
}
