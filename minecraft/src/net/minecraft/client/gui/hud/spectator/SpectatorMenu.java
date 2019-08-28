package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class SpectatorMenu {
	private static final SpectatorMenuCommand CLOSE_COMMAND = new SpectatorMenu.CloseSpectatorMenuCommand();
	private static final SpectatorMenuCommand PREVIOUS_PAGE_COMMAND = new SpectatorMenu.ChangePageSpectatorMenuCommand(-1, true);
	private static final SpectatorMenuCommand NEXT_PAGE_COMMAND = new SpectatorMenu.ChangePageSpectatorMenuCommand(1, true);
	private static final SpectatorMenuCommand DISABLED_NEXT_PAGE_COMMAND = new SpectatorMenu.ChangePageSpectatorMenuCommand(1, false);
	public static final SpectatorMenuCommand BLANK_COMMAND = new SpectatorMenuCommand() {
		@Override
		public void use(SpectatorMenu spectatorMenu) {
		}

		@Override
		public Text getName() {
			return new LiteralText("");
		}

		@Override
		public void renderIcon(float f, int i) {
		}

		@Override
		public boolean isEnabled() {
			return false;
		}
	};
	private final SpectatorMenuCloseCallback closeCallback;
	private final List<SpectatorMenuState> stateStack = Lists.<SpectatorMenuState>newArrayList();
	private SpectatorMenuCommandGroup currentGroup;
	private int selectedSlot = -1;
	private int page;

	public SpectatorMenu(SpectatorMenuCloseCallback spectatorMenuCloseCallback) {
		this.currentGroup = new RootSpectatorCommandGroup();
		this.closeCallback = spectatorMenuCloseCallback;
	}

	public SpectatorMenuCommand getCommand(int i) {
		int j = i + this.page * 6;
		if (this.page > 0 && i == 0) {
			return PREVIOUS_PAGE_COMMAND;
		} else if (i == 7) {
			return j < this.currentGroup.getCommands().size() ? NEXT_PAGE_COMMAND : DISABLED_NEXT_PAGE_COMMAND;
		} else if (i == 8) {
			return CLOSE_COMMAND;
		} else {
			return j >= 0 && j < this.currentGroup.getCommands().size()
				? MoreObjects.firstNonNull((SpectatorMenuCommand)this.currentGroup.getCommands().get(j), BLANK_COMMAND)
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

	public void useCommand(int i) {
		SpectatorMenuCommand spectatorMenuCommand = this.getCommand(i);
		if (spectatorMenuCommand != BLANK_COMMAND) {
			if (this.selectedSlot == i && spectatorMenuCommand.isEnabled()) {
				spectatorMenuCommand.use(this);
			} else {
				this.selectedSlot = i;
			}
		}
	}

	public void close() {
		this.closeCallback.close(this);
	}

	public int getSelectedSlot() {
		return this.selectedSlot;
	}

	public void selectElement(SpectatorMenuCommandGroup spectatorMenuCommandGroup) {
		this.stateStack.add(this.getCurrentState());
		this.currentGroup = spectatorMenuCommandGroup;
		this.selectedSlot = -1;
		this.page = 0;
	}

	public SpectatorMenuState getCurrentState() {
		return new SpectatorMenuState(this.currentGroup, this.getCommands(), this.selectedSlot);
	}

	@Environment(EnvType.CLIENT)
	static class ChangePageSpectatorMenuCommand implements SpectatorMenuCommand {
		private final int direction;
		private final boolean enabled;

		public ChangePageSpectatorMenuCommand(int i, boolean bl) {
			this.direction = i;
			this.enabled = bl;
		}

		@Override
		public void use(SpectatorMenu spectatorMenu) {
			spectatorMenu.page = spectatorMenu.page + this.direction;
		}

		@Override
		public Text getName() {
			return this.direction < 0 ? new TranslatableText("spectatorMenu.previous_page") : new TranslatableText("spectatorMenu.next_page");
		}

		@Override
		public void renderIcon(float f, int i) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
			if (this.direction < 0) {
				DrawableHelper.blit(0, 0, 144.0F, 0.0F, 16, 16, 256, 256);
			} else {
				DrawableHelper.blit(0, 0, 160.0F, 0.0F, 16, 16, 256, 256);
			}
		}

		@Override
		public boolean isEnabled() {
			return this.enabled;
		}
	}

	@Environment(EnvType.CLIENT)
	static class CloseSpectatorMenuCommand implements SpectatorMenuCommand {
		private CloseSpectatorMenuCommand() {
		}

		@Override
		public void use(SpectatorMenu spectatorMenu) {
			spectatorMenu.close();
		}

		@Override
		public Text getName() {
			return new TranslatableText("spectatorMenu.close");
		}

		@Override
		public void renderIcon(float f, int i) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
			DrawableHelper.blit(0, 0, 128.0F, 0.0F, 16, 16, 256, 256);
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}
}
