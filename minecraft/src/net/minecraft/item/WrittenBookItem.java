package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.client.network.packet.GuiSlotUpdateClientPacket;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WrittenBookItem extends Item {
	public WrittenBookItem(Item.Settings settings) {
		super(settings);
	}

	public static boolean method_8053(@Nullable CompoundTag compoundTag) {
		if (!WritableBookItem.method_8047(compoundTag)) {
			return false;
		} else if (!compoundTag.containsKey("title", 8)) {
			return false;
		} else {
			String string = compoundTag.getString("title");
			return string.length() > 32 ? false : compoundTag.containsKey("author", 8);
		}
	}

	public static int getBookGeneration(ItemStack itemStack) {
		return itemStack.getTag().getInt("generation");
	}

	@Override
	public TextComponent getTranslatedNameTrimmed(ItemStack itemStack) {
		if (itemStack.hasTag()) {
			CompoundTag compoundTag = itemStack.getTag();
			String string = compoundTag.getString("title");
			if (!ChatUtil.isEmpty(string)) {
				return new StringTextComponent(string);
			}
		}

		return super.getTranslatedNameTrimmed(itemStack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addInformation(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipOptions tooltipOptions) {
		if (itemStack.hasTag()) {
			CompoundTag compoundTag = itemStack.getTag();
			String string = compoundTag.getString("author");
			if (!ChatUtil.isEmpty(string)) {
				list.add(new TranslatableTextComponent("book.byAuthor", string).applyFormat(TextFormat.GRAY));
			}

			list.add(new TranslatableTextComponent("book.generation." + compoundTag.getInt("generation")).applyFormat(TextFormat.GRAY));
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (!world.isRemote) {
			this.method_8054(itemStack, playerEntity);
		}

		playerEntity.openBookEditor(itemStack, hand);
		playerEntity.incrementStat(Stats.field_15372.method_14956(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}

	private void method_8054(ItemStack itemStack, PlayerEntity playerEntity) {
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null && !compoundTag.getBoolean("resolved")) {
			compoundTag.putBoolean("resolved", true);
			if (method_8053(compoundTag)) {
				ListTag listTag = compoundTag.getList("pages", 8);

				for (int i = 0; i < listTag.size(); i++) {
					String string = listTag.getString(i);

					TextComponent textComponent;
					try {
						textComponent = TextComponent.Serializer.fromLenientJsonString(string);
						textComponent = TextFormatter.method_10881(playerEntity.getCommandSource(), textComponent, playerEntity);
					} catch (Exception var9) {
						textComponent = new StringTextComponent(string);
					}

					listTag.set(i, (Tag)(new StringTag(TextComponent.Serializer.toJsonString(textComponent))));
				}

				compoundTag.put("pages", listTag);
				if (playerEntity instanceof ServerPlayerEntity && playerEntity.getMainHandStack() == itemStack) {
					Slot slot = playerEntity.container.getMatchingSlot(playerEntity.inventory, playerEntity.inventory.selectedSlot);
					((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(new GuiSlotUpdateClientPacket(0, slot.id, itemStack));
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasEnchantmentGlow(ItemStack itemStack) {
		return true;
	}
}
