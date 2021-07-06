package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
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
	public static final int field_30929 = 16;
	public static final int MAX_TITLE_LENGTH = 32;
	public static final int field_30931 = 1024;
	public static final int field_30932 = 32767;
	public static final int field_30933 = 100;
	public static final int field_30934 = 2;
	public static final String TITLE_KEY = "title";
	public static final String FILTERED_TITLE_KEY = "filtered_title";
	public static final String AUTHOR_KEY = "author";
	public static final String PAGES_KEY = "pages";
	public static final String FILTERED_PAGES_KEY = "filtered_pages";
	public static final String GENERATION_KEY = "generation";
	public static final String RESOLVED_KEY = "resolved";

	public WrittenBookItem(Item.Settings settings) {
		super(settings);
	}

	public static boolean isValid(@Nullable NbtCompound nbt) {
		if (!WritableBookItem.isValid(nbt)) {
			return false;
		} else if (!nbt.contains("title", NbtElement.STRING_TYPE)) {
			return false;
		} else {
			String string = nbt.getString("title");
			return string.length() > 32 ? false : nbt.contains("author", NbtElement.STRING_TYPE);
		}
	}

	public static int getGeneration(ItemStack stack) {
		return stack.getNbt().getInt("generation");
	}

	public static int getPageCount(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		return nbtCompound != null ? nbtCompound.getList("pages", NbtElement.STRING_TYPE).size() : 0;
	}

	@Override
	public Text getName(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null) {
			String string = nbtCompound.getString("title");
			if (!ChatUtil.isEmpty(string)) {
				return new LiteralText(string);
			}
		}

		return super.getName(stack);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (stack.hasNbt()) {
			NbtCompound nbtCompound = stack.getNbt();
			String string = nbtCompound.getString("author");
			if (!ChatUtil.isEmpty(string)) {
				tooltip.add(new TranslatableText("book.byAuthor", string).formatted(Formatting.GRAY));
			}

			tooltip.add(new TranslatableText("book.generation." + nbtCompound.getInt("generation")).formatted(Formatting.GRAY));
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
		user.useBook(itemStack, hand);
		user.incrementStat(Stats.USED.getOrCreateStat(this));
		return TypedActionResult.success(itemStack, world.isClient());
	}

	public static boolean resolve(ItemStack book, @Nullable ServerCommandSource commandSource, @Nullable PlayerEntity player) {
		NbtCompound nbtCompound = book.getNbt();
		if (nbtCompound != null && !nbtCompound.getBoolean("resolved")) {
			nbtCompound.putBoolean("resolved", true);
			if (!isValid(nbtCompound)) {
				return false;
			} else {
				NbtList nbtList = nbtCompound.getList("pages", NbtElement.STRING_TYPE);

				for (int i = 0; i < nbtList.size(); i++) {
					nbtList.set(i, (NbtElement)NbtString.of(method_33826(commandSource, player, nbtList.getString(i))));
				}

				if (nbtCompound.contains("filtered_pages", NbtElement.COMPOUND_TYPE)) {
					NbtCompound nbtCompound2 = nbtCompound.getCompound("filtered_pages");

					for (String string : nbtCompound2.getKeys()) {
						nbtCompound2.putString(string, method_33826(commandSource, player, nbtCompound2.getString(string)));
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
