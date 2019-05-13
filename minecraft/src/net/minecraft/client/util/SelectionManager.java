package net.minecraft.client.util;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SelectionManager {
	private final MinecraftClient client;
	private final TextRenderer fontRenderer;
	private final Supplier<String> stringSupplier;
	private final Consumer<String> stringConsumer;
	private final int field_16455;
	private int selectionStart;
	private int selectionEnd;

	public SelectionManager(MinecraftClient minecraftClient, Supplier<String> supplier, Consumer<String> consumer, int i) {
		this.client = minecraftClient;
		this.fontRenderer = minecraftClient.textRenderer;
		this.stringSupplier = supplier;
		this.stringConsumer = consumer;
		this.field_16455 = i;
		this.moveCaretToEnd();
	}

	public boolean insert(char c) {
		if (SharedConstants.isValidChar(c)) {
			this.insert(Character.toString(c));
		}

		return true;
	}

	private void insert(String string) {
		if (this.selectionEnd != this.selectionStart) {
			this.deleteSelectedText();
		}

		String string2 = (String)this.stringSupplier.get();
		this.selectionStart = MathHelper.clamp(this.selectionStart, 0, string2.length());
		String string3 = new StringBuilder(string2).insert(this.selectionStart, string).toString();
		if (this.fontRenderer.getStringWidth(string3) <= this.field_16455) {
			this.stringConsumer.accept(string3);
			this.selectionEnd = this.selectionStart = Math.min(string3.length(), this.selectionStart + string.length());
		}
	}

	public boolean handleSpecialKey(int i) {
		String string = (String)this.stringSupplier.get();
		if (Screen.isSelectAll(i)) {
			this.selectionEnd = 0;
			this.selectionStart = string.length();
			return true;
		} else if (Screen.isCopy(i)) {
			this.client.keyboard.setClipboard(this.getSelectedText());
			return true;
		} else if (Screen.isPaste(i)) {
			this.insert(SharedConstants.stripInvalidChars(ChatFormat.stripFormatting(this.client.keyboard.getClipboard().replaceAll("\\r", ""))));
			this.selectionEnd = this.selectionStart;
			return true;
		} else if (Screen.isCut(i)) {
			this.client.keyboard.setClipboard(this.getSelectedText());
			this.deleteSelectedText();
			return true;
		} else if (i == 259) {
			if (!string.isEmpty()) {
				if (this.selectionEnd != this.selectionStart) {
					this.deleteSelectedText();
				} else if (this.selectionStart > 0) {
					string = new StringBuilder(string).deleteCharAt(Math.max(0, this.selectionStart - 1)).toString();
					this.selectionEnd = this.selectionStart = Math.max(0, this.selectionStart - 1);
					this.stringConsumer.accept(string);
				}
			}

			return true;
		} else if (i == 261) {
			if (!string.isEmpty()) {
				if (this.selectionEnd != this.selectionStart) {
					this.deleteSelectedText();
				} else if (this.selectionStart < string.length()) {
					string = new StringBuilder(string).deleteCharAt(Math.max(0, this.selectionStart)).toString();
					this.stringConsumer.accept(string);
				}
			}

			return true;
		} else if (i == 263) {
			int j = this.fontRenderer.isRightToLeft() ? 1 : -1;
			if (Screen.hasControlDown()) {
				this.selectionStart = this.fontRenderer.findWordEdge(string, j, this.selectionStart, true);
			} else {
				this.selectionStart = Math.max(0, Math.min(string.length(), this.selectionStart + j));
			}

			if (!Screen.hasShiftDown()) {
				this.selectionEnd = this.selectionStart;
			}

			return true;
		} else if (i == 262) {
			int jx = this.fontRenderer.isRightToLeft() ? -1 : 1;
			if (Screen.hasControlDown()) {
				this.selectionStart = this.fontRenderer.findWordEdge(string, jx, this.selectionStart, true);
			} else {
				this.selectionStart = Math.max(0, Math.min(string.length(), this.selectionStart + jx));
			}

			if (!Screen.hasShiftDown()) {
				this.selectionEnd = this.selectionStart;
			}

			return true;
		} else if (i == 268) {
			this.selectionStart = 0;
			if (!Screen.hasShiftDown()) {
				this.selectionEnd = this.selectionStart;
			}

			return true;
		} else if (i == 269) {
			this.selectionStart = ((String)this.stringSupplier.get()).length();
			if (!Screen.hasShiftDown()) {
				this.selectionEnd = this.selectionStart;
			}

			return true;
		} else {
			return false;
		}
	}

	private String getSelectedText() {
		String string = (String)this.stringSupplier.get();
		int i = Math.min(this.selectionStart, this.selectionEnd);
		int j = Math.max(this.selectionStart, this.selectionEnd);
		return string.substring(i, j);
	}

	private void deleteSelectedText() {
		if (this.selectionEnd != this.selectionStart) {
			String string = (String)this.stringSupplier.get();
			int i = Math.min(this.selectionStart, this.selectionEnd);
			int j = Math.max(this.selectionStart, this.selectionEnd);
			String string2 = string.substring(0, i) + string.substring(j);
			this.selectionStart = i;
			this.selectionEnd = this.selectionStart;
			this.stringConsumer.accept(string2);
		}
	}

	public void moveCaretToEnd() {
		this.selectionEnd = this.selectionStart = ((String)this.stringSupplier.get()).length();
	}

	public int getSelectionStart() {
		return this.selectionStart;
	}

	public int getSelectionEnd() {
		return this.selectionEnd;
	}
}
