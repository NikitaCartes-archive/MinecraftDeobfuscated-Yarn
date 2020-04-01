package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public interface BookContent {
	BookContent EMPTY = new BookContent() {
		@Override
		public int getPageCount() {
			return 0;
		}

		@Override
		public Text getPageUnchecked(int index) {
			return new LiteralText("");
		}
	};

	static List<String> fromTag(CompoundTag tag) {
		ListTag listTag = tag.getList("pages", 8).copy();
		Builder<String> builder = ImmutableList.builder();

		for (int i = 0; i < listTag.size(); i++) {
			builder.add(listTag.getString(i));
		}

		return builder.build();
	}

	int getPageCount();

	Text getPageUnchecked(int index);

	default Text getPage(int index) {
		return (Text)(index >= 0 && index < this.getPageCount() ? this.getPageUnchecked(index) : new LiteralText(""));
	}

	static BookContent fromStack(ItemStack stack) {
		Item item = stack.getItem();
		if (item == Items.WRITTEN_BOOK) {
			return new BookContent.WrittenBookContents(stack);
		} else {
			return (BookContent)(item == Items.WRITABLE_BOOK ? new BookContent.WritableBookContents(stack) : EMPTY);
		}
	}

	public static class WritableBookContents implements BookContent {
		private final List<String> pages;

		public WritableBookContents(ItemStack stack) {
			this.pages = getPages(stack);
		}

		private static List<String> getPages(ItemStack stack) {
			CompoundTag compoundTag = stack.getTag();
			return (List<String>)(compoundTag != null ? BookContent.fromTag(compoundTag) : ImmutableList.of());
		}

		@Override
		public int getPageCount() {
			return this.pages.size();
		}

		@Override
		public Text getPageUnchecked(int index) {
			return new LiteralText((String)this.pages.get(index));
		}
	}

	public static class WrittenBookContents implements BookContent {
		private final List<String> pages;

		public WrittenBookContents(ItemStack stack) {
			this.pages = getPages(stack);
		}

		private static List<String> getPages(ItemStack stack) {
			CompoundTag compoundTag = stack.getTag();
			return (List<String>)(compoundTag != null && WrittenBookItem.isValid(compoundTag)
				? BookContent.fromTag(compoundTag)
				: ImmutableList.of(new TranslatableText("book.invalid.tag").formatted(Formatting.DARK_RED).asFormattedString()));
		}

		@Override
		public int getPageCount() {
			return this.pages.size();
		}

		@Override
		public Text getPageUnchecked(int index) {
			String string = (String)this.pages.get(index);

			try {
				Text text = Text.Serializer.fromJson(string);
				if (text != null) {
					return text;
				}
			} catch (Exception var4) {
			}

			return new LiteralText(string);
		}
	}
}
