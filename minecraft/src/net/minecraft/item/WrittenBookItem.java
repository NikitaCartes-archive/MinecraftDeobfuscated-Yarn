package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.StringHelper;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WrittenBookItem extends Item {
	public static final int MAX_TITLE_EDIT_LENGTH = 16;
	public static final int MAX_TITLE_VIEW_LENGTH = 32;
	public static final int MAX_PAGE_EDIT_LENGTH = 1024;
	public static final int MAX_PAGE_VIEW_LENGTH = 32767;
	public static final int MAX_PAGES = 100;
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
		NbtCompound nbtCompound = stack.getNbt();
		return nbtCompound != null ? nbtCompound.getInt("generation") : 0;
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
			if (!StringHelper.isEmpty(string)) {
				return Text.literal(string);
			}
		}

		return super.getName(stack);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (stack.hasNbt()) {
			NbtCompound nbtCompound = stack.getNbt();
			String string = nbtCompound.getString("author");
			if (!StringHelper.isEmpty(string)) {
				tooltip.add(Text.translatable("book.byAuthor", string).formatted(Formatting.GRAY));
			}

			tooltip.add(Text.translatable("book.generation." + nbtCompound.getInt("generation")).formatted(Formatting.GRAY));
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
				NbtList nbtList2 = new NbtList();

				for (int i = 0; i < nbtList.size(); i++) {
					String string = textToJson(commandSource, player, nbtList.getString(i));
					if (string.length() > 32767) {
						return false;
					}

					nbtList2.add(i, (NbtElement)NbtString.of(string));
				}

				if (nbtCompound.contains("filtered_pages", NbtElement.COMPOUND_TYPE)) {
					NbtCompound nbtCompound2 = nbtCompound.getCompound("filtered_pages");
					NbtCompound nbtCompound3 = new NbtCompound();

					for (String string2 : nbtCompound2.getKeys()) {
						String string3 = textToJson(commandSource, player, nbtCompound2.getString(string2));
						if (string3.length() > 32767) {
							return false;
						}

						nbtCompound3.putString(string2, string3);
					}

					nbtCompound.put("filtered_pages", nbtCompound3);
				}

				nbtCompound.put("pages", nbtList2);
				return true;
			}
		} else {
			return false;
		}
	}

	private static String textToJson(@Nullable ServerCommandSource commandSource, @Nullable PlayerEntity player, String text) {
		Text text2;
		try {
			text2 = Text.Serialization.fromLenientJson(text);
			text2 = Texts.parse(commandSource, text2, player, 0);
		} catch (Exception var5) {
			text2 = Text.literal(text);
		}

		return Text.Serialization.toJsonString(text2);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return true;
	}
}
