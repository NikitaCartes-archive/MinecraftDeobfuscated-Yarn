package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.command.ServerCommandSource;
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
import net.minecraft.util.math.BlockPos;
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
		return itemStack.method_7969().getInt("generation");
	}

	public static int getPageCount(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.method_7969();
		return compoundTag != null ? compoundTag.method_10554("pages", 8).size() : 0;
	}

	@Override
	public TextComponent method_7864(ItemStack itemStack) {
		if (itemStack.hasTag()) {
			CompoundTag compoundTag = itemStack.method_7969();
			String string = compoundTag.getString("title");
			if (!ChatUtil.isEmpty(string)) {
				return new StringTextComponent(string);
			}
		}

		return super.method_7864(itemStack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		if (itemStack.hasTag()) {
			CompoundTag compoundTag = itemStack.method_7969();
			String string = compoundTag.getString("author");
			if (!ChatUtil.isEmpty(string)) {
				list.add(new TranslatableTextComponent("book.byAuthor", string).applyFormat(TextFormat.field_1080));
			}

			list.add(new TranslatableTextComponent("book.generation." + compoundTag.getInt("generation")).applyFormat(TextFormat.field_1080));
		}
	}

	@Override
	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.method_8045();
		BlockPos blockPos = itemUsageContext.method_8037();
		BlockState blockState = world.method_8320(blockPos);
		if (blockState.getBlock() == Blocks.field_16330) {
			return LecternBlock.method_17472(world, blockPos, blockState, itemUsageContext.getItemStack()) ? ActionResult.field_5812 : ActionResult.PASS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		playerEntity.method_7315(itemStack, hand);
		playerEntity.method_7259(Stats.field_15372.getOrCreateStat(this));
		return new TypedActionResult<>(ActionResult.field_5812, itemStack);
	}

	public static boolean method_8054(ItemStack itemStack, @Nullable ServerCommandSource serverCommandSource, @Nullable PlayerEntity playerEntity) {
		CompoundTag compoundTag = itemStack.method_7969();
		if (compoundTag != null && !compoundTag.getBoolean("resolved")) {
			compoundTag.putBoolean("resolved", true);
			if (!method_8053(compoundTag)) {
				return false;
			} else {
				ListTag listTag = compoundTag.method_10554("pages", 8);

				for (int i = 0; i < listTag.size(); i++) {
					String string = listTag.getString(i);

					TextComponent textComponent;
					try {
						textComponent = TextComponent.Serializer.fromLenientJsonString(string);
						textComponent = TextFormatter.method_10881(serverCommandSource, textComponent, playerEntity);
					} catch (Exception var9) {
						textComponent = new StringTextComponent(string);
					}

					listTag.method_10606(i, new StringTag(TextComponent.Serializer.toJsonString(textComponent)));
				}

				compoundTag.method_10566("pages", listTag);
				return true;
			}
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_7886(ItemStack itemStack) {
		return true;
	}
}
