package net.minecraft.client.util;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SelectionManager {
	private final Supplier<String> stringGetter;
	private final Consumer<String> stringSetter;
	private final Supplier<String> clipboardGetter;
	private final Consumer<String> clipboardSetter;
	private final Predicate<String> stringFilter;
	private int selectionStart;
	private int selectionEnd;

	public SelectionManager(
		Supplier<String> supplier, Consumer<String> consumer, Supplier<String> supplier2, Consumer<String> consumer2, Predicate<String> predicate
	) {
		this.stringGetter = supplier;
		this.stringSetter = consumer;
		this.clipboardGetter = supplier2;
		this.clipboardSetter = consumer2;
		this.stringFilter = predicate;
		this.moveCaretToEnd();
	}

	public static Supplier<String> makeClipboardGetter(MinecraftClient client) {
		return () -> getClipboard(client);
	}

	public static String getClipboard(MinecraftClient client) {
		return Formatting.strip(client.keyboard.getClipboard().replaceAll("\\r", ""));
	}

	public static Consumer<String> makeClipboardSetter(MinecraftClient client) {
		return string -> setClipboard(client, string);
	}

	public static void setClipboard(MinecraftClient client, String string) {
		client.keyboard.setClipboard(string);
	}

	public boolean insert(char c) {
		if (SharedConstants.isValidChar(c)) {
			this.insert((String)this.stringGetter.get(), Character.toString(c));
		}

		return true;
	}

	public boolean handleSpecialKey(int keyCode) {
		if (Screen.isSelectAll(keyCode)) {
			this.selectAll();
			return true;
		} else if (Screen.isCopy(keyCode)) {
			this.copy();
			return true;
		} else if (Screen.isPaste(keyCode)) {
			this.paste();
			return true;
		} else if (Screen.isCut(keyCode)) {
			this.cut();
			return true;
		} else if (keyCode == 259) {
			this.delete(-1);
			return true;
		} else {
			if (keyCode == 261) {
				this.delete(1);
			} else {
				if (keyCode == 263) {
					if (Screen.hasControlDown()) {
						this.moveCursorPastWord(-1, Screen.hasShiftDown());
					} else {
						this.moveCursor(-1, Screen.hasShiftDown());
					}

					return true;
				}

				if (keyCode == 262) {
					if (Screen.hasControlDown()) {
						this.moveCursorPastWord(1, Screen.hasShiftDown());
					} else {
						this.moveCursor(1, Screen.hasShiftDown());
					}

					return true;
				}

				if (keyCode == 268) {
					this.method_27553(Screen.hasShiftDown());
					return true;
				}

				if (keyCode == 269) {
					this.method_27558(Screen.hasShiftDown());
					return true;
				}
			}

			return false;
		}
	}

	private int method_27567(int i) {
		return MathHelper.clamp(i, 0, ((String)this.stringGetter.get()).length());
	}

	private void insert(String string, String insertion) {
		if (this.selectionEnd != this.selectionStart) {
			string = this.deleteSelectedText(string);
		}

		this.selectionStart = MathHelper.clamp(this.selectionStart, 0, string.length());
		String string2 = new StringBuilder(string).insert(this.selectionStart, insertion).toString();
		if (this.stringFilter.test(string2)) {
			this.stringSetter.accept(string2);
			this.selectionEnd = this.selectionStart = Math.min(string2.length(), this.selectionStart + insertion.length());
		}
	}

	public void insert(String string) {
		this.insert((String)this.stringGetter.get(), string);
	}

	private void updateSelectionRange(boolean shiftDown) {
		if (!shiftDown) {
			this.selectionEnd = this.selectionStart;
		}
	}

	public void moveCursor(int offset, boolean shiftDown) {
		this.selectionStart = Util.moveCursor((String)this.stringGetter.get(), this.selectionStart, offset);
		this.updateSelectionRange(shiftDown);
	}

	public void moveCursorPastWord(int offset, boolean shiftDown) {
		this.selectionStart = TextHandler.moveCursorByWords((String)this.stringGetter.get(), offset, this.selectionStart, true);
		this.updateSelectionRange(shiftDown);
	}

	public void delete(int cursorOffset) {
		String string = (String)this.stringGetter.get();
		if (!string.isEmpty()) {
			String string2;
			if (this.selectionEnd != this.selectionStart) {
				string2 = this.deleteSelectedText(string);
			} else {
				int i = Util.moveCursor(string, this.selectionStart, cursorOffset);
				int j = Math.min(i, this.selectionStart);
				int k = Math.max(i, this.selectionStart);
				string2 = new StringBuilder(string).delete(j, k).toString();
				if (cursorOffset < 0) {
					this.selectionEnd = this.selectionStart = j;
				}
			}

			this.stringSetter.accept(string2);
		}
	}

	public void cut() {
		String string = (String)this.stringGetter.get();
		this.clipboardSetter.accept(this.getSelectedText(string));
		this.stringSetter.accept(this.deleteSelectedText(string));
	}

	public void paste() {
		this.insert((String)this.stringGetter.get(), (String)this.clipboardGetter.get());
		this.selectionEnd = this.selectionStart;
	}

	public void copy() {
		this.clipboardSetter.accept(this.getSelectedText((String)this.stringGetter.get()));
	}

	public void selectAll() {
		this.selectionEnd = 0;
		this.selectionStart = ((String)this.stringGetter.get()).length();
	}

	private String getSelectedText(String string) {
		int i = Math.min(this.selectionStart, this.selectionEnd);
		int j = Math.max(this.selectionStart, this.selectionEnd);
		return string.substring(i, j);
	}

	private String deleteSelectedText(String string) {
		if (this.selectionEnd == this.selectionStart) {
			return string;
		} else {
			int i = Math.min(this.selectionStart, this.selectionEnd);
			int j = Math.max(this.selectionStart, this.selectionEnd);
			String string2 = string.substring(0, i) + string.substring(j);
			this.selectionEnd = this.selectionStart = i;
			return string2;
		}
	}

	private void method_27553(boolean bl) {
		this.selectionStart = 0;
		this.updateSelectionRange(bl);
	}

	public void moveCaretToEnd() {
		this.method_27558(false);
	}

	private void method_27558(boolean bl) {
		this.selectionStart = ((String)this.stringGetter.get()).length();
		this.updateSelectionRange(bl);
	}

	public int getSelectionStart() {
		return this.selectionStart;
	}

	public void method_27560(int i, boolean bl) {
		this.selectionStart = this.method_27567(i);
		this.updateSelectionRange(bl);
	}

	public int getSelectionEnd() {
		return this.selectionEnd;
	}

	public void method_27548(int i, int j) {
		int k = ((String)this.stringGetter.get()).length();
		this.selectionStart = MathHelper.clamp(i, 0, k);
		this.selectionEnd = MathHelper.clamp(j, 0, k);
	}

	public boolean method_27568() {
		return this.selectionStart != this.selectionEnd;
	}
}
