package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ChatScreen extends Screen {
	private String field_2389 = "";
	private int messageHistorySize = -1;
	protected TextFieldWidget chatField;
	private String originalChatText = "";
	private CommandSuggestor commandSuggestor;

	public ChatScreen(String originalChatText) {
		super(NarratorManager.EMPTY);
		this.originalChatText = originalChatText;
	}

	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.messageHistorySize = this.client.inGameHud.getChatHud().getMessageHistory().size();
		this.chatField = new TextFieldWidget(this.textRenderer, 4, this.height - 12, this.width - 4, 12, new TranslatableText("chat.editBox")) {
			@Override
			protected MutableText getNarrationMessage() {
				return super.getNarrationMessage().append(ChatScreen.this.commandSuggestor.getNarration());
			}
		};
		this.chatField.setMaxLength(256);
		this.chatField.setDrawsBackground(false);
		this.chatField.setText(this.originalChatText);
		this.chatField.setChangedListener(this::onChatFieldUpdate);
		this.children.add(this.chatField);
		this.commandSuggestor = new CommandSuggestor(this.client, this, this.chatField, this.textRenderer, false, false, 1, 10, true, -805306368);
		this.commandSuggestor.refresh();
		this.setInitialFocus(this.chatField);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.chatField.getText();
		this.init(client, width, height);
		this.setText(string);
		this.commandSuggestor.refresh();
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
		this.client.inGameHud.getChatHud().resetScroll();
	}

	@Override
	public void tick() {
		this.chatField.tick();
	}

	private void onChatFieldUpdate(String chatText) {
		String string = this.chatField.getText();
		this.commandSuggestor.setWindowActive(!string.equals(this.originalChatText));
		this.commandSuggestor.refresh();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.commandSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (keyCode == 256) {
			this.client.openScreen(null);
			return true;
		} else if (keyCode == 257 || keyCode == 335) {
			String string = this.chatField.getText().trim();
			if (!string.isEmpty()) {
				this.sendMessage(string);
			}

			this.client.openScreen(null);
			return true;
		} else if (keyCode == 265) {
			this.setChatFromHistory(-1);
			return true;
		} else if (keyCode == 264) {
			this.setChatFromHistory(1);
			return true;
		} else if (keyCode == 266) {
			this.client.inGameHud.getChatHud().scroll((double)(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1));
			return true;
		} else if (keyCode == 267) {
			this.client.inGameHud.getChatHud().scroll((double)(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (amount > 1.0) {
			amount = 1.0;
		}

		if (amount < -1.0) {
			amount = -1.0;
		}

		if (this.commandSuggestor.mouseScrolled(amount)) {
			return true;
		} else {
			if (!hasShiftDown()) {
				amount *= 7.0;
			}

			this.client.inGameHud.getChatHud().scroll(amount);
			return true;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.commandSuggestor.mouseClicked((double)((int)mouseX), (double)((int)mouseY), button)) {
			return true;
		} else {
			if (button == 0) {
				ChatHud chatHud = this.client.inGameHud.getChatHud();
				if (chatHud.mouseClicked(mouseX, mouseY)) {
					return true;
				}

				Style style = chatHud.getText(mouseX, mouseY);
				if (style != null && this.handleTextClick(style)) {
					return true;
				}
			}

			return this.chatField.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	protected void insertText(String text, boolean override) {
		if (override) {
			this.chatField.setText(text);
		} else {
			this.chatField.write(text);
		}
	}

	public void setChatFromHistory(int i) {
		int j = this.messageHistorySize + i;
		int k = this.client.inGameHud.getChatHud().getMessageHistory().size();
		j = MathHelper.clamp(j, 0, k);
		if (j != this.messageHistorySize) {
			if (j == k) {
				this.messageHistorySize = k;
				this.chatField.setText(this.field_2389);
			} else {
				if (this.messageHistorySize == k) {
					this.field_2389 = this.chatField.getText();
				}

				this.chatField.setText((String)this.client.inGameHud.getChatHud().getMessageHistory().get(j));
				this.commandSuggestor.setWindowActive(false);
				this.messageHistorySize = j;
			}
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.setFocused(this.chatField);
		this.chatField.setTextFieldFocused(true);
		fill(matrices, 2, this.height - 14, this.width - 2, this.height - 2, this.client.options.getTextBackgroundColor(Integer.MIN_VALUE));
		this.chatField.render(matrices, mouseX, mouseY, delta);
		this.commandSuggestor.render(matrices, mouseX, mouseY);
		Style style = this.client.inGameHud.getChatHud().getText((double)mouseX, (double)mouseY);
		if (style != null && style.getHoverEvent() != null) {
			this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void setText(String text) {
		this.chatField.setText(text);
	}
}
