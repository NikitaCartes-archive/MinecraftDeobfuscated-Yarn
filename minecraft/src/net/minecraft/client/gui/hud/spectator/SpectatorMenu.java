package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpectatorMenu {
	static final Identifier CLOSE_TEXTURE = Identifier.ofVanilla("spectator/close");
	static final Identifier SCROLL_LEFT_TEXTURE = Identifier.ofVanilla("spectator/scroll_left");
	static final Identifier SCROLL_RIGHT_TEXTURE = Identifier.ofVanilla("spectator/scroll_right");
	private static final SpectatorMenuCommand CLOSE_COMMAND = new SpectatorMenu.CloseSpectatorMenuCommand();
	private static final SpectatorMenuCommand PREVIOUS_PAGE_COMMAND = new SpectatorMenu.ChangePageSpectatorMenuCommand(-1, true);
	private static final SpectatorMenuCommand NEXT_PAGE_COMMAND = new SpectatorMenu.ChangePageSpectatorMenuCommand(1, true);
	private static final SpectatorMenuCommand DISABLED_NEXT_PAGE_COMMAND = new SpectatorMenu.ChangePageSpectatorMenuCommand(1, false);
	private static final int CLOSE_SLOT = 8;
	static final Text CLOSE_TEXT = Text.translatable("spectatorMenu.close");
	static final Text PREVIOUS_PAGE_TEXT = Text.translatable("spectatorMenu.previous_page");
	static final Text NEXT_PAGE_TEXT = Text.translatable("spectatorMenu.next_page");
	public static final SpectatorMenuCommand BLANK_COMMAND = new SpectatorMenuCommand() {
		@Override
		public void use(SpectatorMenu menu) {
		}

		@Override
		public Text getName() {
			return ScreenTexts.EMPTY;
		}

		@Override
		public void renderIcon(DrawContext context, float brightness, int alpha) {
		}

		@Override
		public boolean isEnabled() {
			return false;
		}
	};
	private final SpectatorMenuCloseCallback closeCallback;
	private SpectatorMenuCommandGroup currentGroup;
	private int selectedSlot = -1;
	int page;

	public SpectatorMenu(SpectatorMenuCloseCallback closeCallback) {
		this.currentGroup = new RootSpectatorCommandGroup();
		this.closeCallback = closeCallback;
	}

	public SpectatorMenuCommand getCommand(int slot) {
		int i = slot + this.page * 6;
		if (this.page > 0 && slot == 0) {
			return PREVIOUS_PAGE_COMMAND;
		} else if (slot == 7) {
			return i < this.currentGroup.getCommands().size() ? NEXT_PAGE_COMMAND : DISABLED_NEXT_PAGE_COMMAND;
		} else if (slot == 8) {
			return CLOSE_COMMAND;
		} else {
			return i >= 0 && i < this.currentGroup.getCommands().size()
				? MoreObjects.firstNonNull((SpectatorMenuCommand)this.currentGroup.getCommands().get(i), BLANK_COMMAND)
				: BLANK_COMMAND;
		}
	}

	public List<SpectatorMenuCommand> getCommands() {
		List<SpectatorMenuCommand> list = Lists.<SpectatorMenuCommand>newArrayList();

		for (int i = 0; i <= 8; i++) {
			list.add(this.getCommand(i));
		}

		return list;
	}

	public SpectatorMenuCommand getSelectedCommand() {
		return this.getCommand(this.selectedSlot);
	}

	public SpectatorMenuCommandGroup getCurrentGroup() {
		return this.currentGroup;
	}

	public void useCommand(int slot) {
		SpectatorMenuCommand spectatorMenuCommand = this.getCommand(slot);
		if (spectatorMenuCommand != BLANK_COMMAND) {
			if (this.selectedSlot == slot && spectatorMenuCommand.isEnabled()) {
				spectatorMenuCommand.use(this);
			} else {
				this.selectedSlot = slot;
			}
		}
	}

	public void close() {
		this.closeCallback.close(this);
	}

	public int getSelectedSlot() {
		return this.selectedSlot;
	}

	public void selectElement(SpectatorMenuCommandGroup group) {
		this.currentGroup = group;
		this.selectedSlot = -1;
		this.page = 0;
	}

	public SpectatorMenuState getCurrentState() {
		return new SpectatorMenuState(this.getCommands(), this.selectedSlot);
	}

	@Environment(EnvType.CLIENT)
	static class ChangePageSpectatorMenuCommand implements SpectatorMenuCommand {
		private final int direction;
		private final boolean enabled;

		public ChangePageSpectatorMenuCommand(int direction, boolean enabled) {
			this.direction = direction;
			this.enabled = enabled;
		}

		@Override
		public void use(SpectatorMenu menu) {
			menu.page = menu.page + this.direction;
		}

		@Override
		public Text getName() {
			return this.direction < 0 ? SpectatorMenu.PREVIOUS_PAGE_TEXT : SpectatorMenu.NEXT_PAGE_TEXT;
		}

		@Override
		public void renderIcon(DrawContext context, float brightness, int alpha) {
			if (this.direction < 0) {
				context.drawGuiTexture(SpectatorMenu.SCROLL_LEFT_TEXTURE, 0, 0, 16, 16);
			} else {
				context.drawGuiTexture(SpectatorMenu.SCROLL_RIGHT_TEXTURE, 0, 0, 16, 16);
			}
		}

		@Override
		public boolean isEnabled() {
			return this.enabled;
		}
	}

	@Environment(EnvType.CLIENT)
	static class CloseSpectatorMenuCommand implements SpectatorMenuCommand {
		@Override
		public void use(SpectatorMenu menu) {
			menu.close();
		}

		@Override
		public Text getName() {
			return SpectatorMenu.CLOSE_TEXT;
		}

		@Override
		public void renderIcon(DrawContext context, float brightness, int alpha) {
			context.drawGuiTexture(SpectatorMenu.CLOSE_TEXTURE, 0, 0, 16, 16);
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}
}
