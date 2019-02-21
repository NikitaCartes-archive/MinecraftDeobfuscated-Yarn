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

	public static boolean isValidBook(@Nullable CompoundTag compoundTag) {
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

	public static int getPageCount(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		return compoundTag != null ? compoundTag.getList("pages", 8).size() : 0;
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
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		if (itemStack.hasTag()) {
			CompoundTag compoundTag = itemStack.getTag();
			String string = compoundTag.getString("author");
			if (!ChatUtil.isEmpty(string)) {
				list.add(new TranslatableTextComponent("book.byAuthor", string).applyFormat(TextFormat.field_1080));
			}

			list.add(new TranslatableTextComponent("book.generation." + compoundTag.getInt("generation")).applyFormat(TextFormat.field_1080));
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == Blocks.field_16330) {
			return LecternBlock.putBookIfAbsent(world, blockPos, blockState, itemUsageContext.getItemStack()) ? ActionResult.field_5812 : ActionResult.PASS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		playerEntity.openEditBookScreen(itemStack, hand);
		playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
		return new TypedActionResult<>(ActionResult.field_5812, itemStack);
	}

	public static boolean resolve(ItemStack itemStack, @Nullable ServerCommandSource serverCommandSource, @Nullable PlayerEntity playerEntity) {
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null && !compoundTag.getBoolean("resolved")) {
			compoundTag.putBoolean("resolved", true);
			if (!isValidBook(compoundTag)) {
				return false;
			} else {
				ListTag listTag = compoundTag.getList("pages", 8);

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

				compoundTag.put("pages", listTag);
				return true;
			}
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasEnchantmentGlint(ItemStack itemStack) {
		return true;
	}
}
