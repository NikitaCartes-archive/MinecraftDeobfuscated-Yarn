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
import net.minecraft.nbt.Tag;
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

	public static boolean isValid(@Nullable CompoundTag tag) {
		if (!WritableBookItem.isValid(tag)) {
			return false;
		} else if (!tag.contains("title", 8)) {
			return false;
		} else {
			String string = tag.getString("title");
			return string.length() > 32 ? false : tag.contains("author", 8);
		}
	}

	public static int getGeneration(ItemStack stack) {
		return stack.getTag().getInt("generation");
	}

	public static int getPageCount(ItemStack stack) {
		CompoundTag compoundTag = stack.getTag();
		return compoundTag != null ? compoundTag.getList("pages", 8).size() : 0;
	}

	@Override
	public Text getName(ItemStack stack) {
		CompoundTag compoundTag = stack.getTag();
		if (compoundTag != null) {
			String string = compoundTag.getString("title");
			if (!ChatUtil.isEmpty(string)) {
				return new LiteralText(string);
			}
		}

		return super.getName(stack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (stack.hasTag()) {
			CompoundTag compoundTag = stack.getTag();
			String string = compoundTag.getString("author");
			if (!ChatUtil.isEmpty(string)) {
				tooltip.add(new TranslatableText("book.byAuthor", string).formatted(Formatting.GRAY));
			}

			tooltip.add(new TranslatableText("book.generation." + compoundTag.getInt("generation")).formatted(Formatting.GRAY));
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.isOf(Blocks.LECTERN)) {
			return LecternBlock.putBookIfAbsent(context.getPlayer(), world, blockPos, blockState, context.getStack())
				? ActionResult.success(world.isClient)
				: ActionResult.PASS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		user.openEditBookScreen(itemStack, hand);
		user.incrementStat(Stats.USED.getOrCreateStat(this));
		return TypedActionResult.success(itemStack, world.isClient());
	}

	public static boolean resolve(ItemStack book, @Nullable ServerCommandSource commandSource, @Nullable PlayerEntity player) {
		CompoundTag compoundTag = book.getTag();
		if (compoundTag != null && !compoundTag.getBoolean("resolved")) {
			compoundTag.putBoolean("resolved", true);
			if (!isValid(compoundTag)) {
				return false;
			} else {
				ListTag listTag = compoundTag.getList("pages", 8);

				for (int i = 0; i < listTag.size(); i++) {
					listTag.set(i, (Tag)StringTag.of(method_33826(commandSource, player, listTag.getString(i))));
				}

				if (compoundTag.contains("filtered_pages", 10)) {
					CompoundTag compoundTag2 = compoundTag.getCompound("filtered_pages");

					for (String string : compoundTag2.getKeys()) {
						compoundTag2.putString(string, method_33826(commandSource, player, compoundTag2.getString(string)));
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	private static String method_33826(@Nullable ServerCommandSource serverCommandSource, @Nullable PlayerEntity playerEntity, String string) {
		Text text;
		try {
			text = Text.Serializer.fromLenientJson(string);
			text = Texts.parse(serverCommandSource, text, playerEntity, 0);
		} catch (Exception var5) {
			text = new LiteralText(string);
		}

		return Text.Serializer.toJson(text);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return true;
	}
}
