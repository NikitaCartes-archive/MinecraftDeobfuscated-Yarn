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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WrittenBookItem extends Item {
	public WrittenBookItem(Item.Settings settings) {
		super(settings);
	}

	public static boolean isValid(@Nullable CompoundTag compoundTag) {
		if (!WritableBookItem.isValid(compoundTag)) {
			return false;
		} else if (!compoundTag.containsKey("title", 8)) {
			return false;
		} else {
			String string = compoundTag.getString("title");
			return string.length() > 32 ? false : compoundTag.containsKey("author", 8);
		}
	}

	public static int getGeneration(ItemStack itemStack) {
		return itemStack.getTag().getInt("generation");
	}

	public static int getPageCount(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		return compoundTag != null ? compoundTag.getList("pages", 8).size() : 0;
	}

	@Override
	public Text getName(ItemStack itemStack) {
		if (itemStack.hasTag()) {
			CompoundTag compoundTag = itemStack.getTag();
			String string = compoundTag.getString("title");
			if (!ChatUtil.isEmpty(string)) {
				return new LiteralText(string);
			}
		}

		return super.getName(itemStack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
		if (itemStack.hasTag()) {
			CompoundTag compoundTag = itemStack.getTag();
			String string = compoundTag.getString("author");
			if (!ChatUtil.isEmpty(string)) {
				list.add(new TranslatableText("book.byAuthor", string).formatted(Formatting.GRAY));
			}

			list.add(new TranslatableText("book.generation." + compoundTag.getInt("generation")).formatted(Formatting.GRAY));
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == Blocks.LECTERN) {
			return LecternBlock.putBookIfAbsent(world, blockPos, blockState, itemUsageContext.getStack()) ? ActionResult.SUCCESS : ActionResult.PASS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		playerEntity.openEditBookScreen(itemStack, hand);
		playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}

	public static boolean resolve(ItemStack itemStack, @Nullable ServerCommandSource serverCommandSource, @Nullable PlayerEntity playerEntity) {
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null && !compoundTag.getBoolean("resolved")) {
			compoundTag.putBoolean("resolved", true);
			if (!isValid(compoundTag)) {
				return false;
			} else {
				ListTag listTag = compoundTag.getList("pages", 8);

				for (int i = 0; i < listTag.size(); i++) {
					String string = listTag.getString(i);

					Text text;
					try {
						text = Text.Serializer.fromLenientJson(string);
						text = Texts.parse(serverCommandSource, text, playerEntity, 0);
					} catch (Exception var9) {
						text = new LiteralText(string);
					}

					listTag.method_10606(i, new StringTag(Text.Serializer.toJson(text)));
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
