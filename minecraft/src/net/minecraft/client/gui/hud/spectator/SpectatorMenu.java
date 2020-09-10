package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class SpectatorMenu {
	private static final SpectatorMenuCommand CLOSE_COMMAND = new SpectatorMenu.CloseSpectatorMenuCommand();
	private static final SpectatorMenuCommand PREVIOUS_PAGE_COMMAND = new SpectatorMenu.ChangePageSpectatorMenuCommand(-1, true);
	private static final SpectatorMenuCommand NEXT_PAGE_COMMAND = new SpectatorMenu.ChangePageSpectatorMenuCommand(1, true);
	private static final SpectatorMenuCommand DISABLED_NEXT_PAGE_COMMAND = new SpectatorMenu.ChangePageSpectatorMenuCommand(1, false);
	private static final Text field_26613 = new TranslatableText("spectatorMenu.close");
	private static final Text field_26614 = new TranslatableText("spectatorMenu.previous_page");
	private static final Text field_26615 = new TranslatableText("spectatorMenu.next_page");
	public static final SpectatorMenuCommand BLANK_COMMAND = new SpectatorMenuCommand() {
		@Override
		public void use(SpectatorMenu menu) {
		}

		@Override
		public Text getName() {
			return LiteralText.EMPTY;
		}

		@Override
		public void renderIcon(MatrixStack matrixStack, float f, int i) {
		}

		@Override
		public boolean isEnabled() {
			return false;
		}
	};
	private final SpectatorMenuCloseCallback closeCallback;
	private SpectatorMenuCommandGroup currentGroup;
	private int selectedSlot = -1;
	private int page;

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
		return new SpectatorMenuState(this.currentGroup, this.getCommands(), this.selectedSlot);
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
			return this.direction < 0 ? SpectatorMenu.field_26614 : SpectatorMenu.field_26615;
		}

		@Override
		public void renderIcon(MatrixStack matrixStack, float f, int i) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEXTURE);
			if (this.direction < 0) {
				DrawableHelper.drawTexture(matrixStack, 0, 0, 144.0F, 0.0F, 16, 16, 256, 256);
			} else {
				DrawableHelper.drawTexture(matrixStack, 0, 0, 160.0F, 0.0F, 16, 16, 256, 256);
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
		public void use(SpectatorMenu menu) {
			menu.close();
		}

		@Override
		public Text getName() {
			return SpectatorMenu.field_26613;
		}

		@Override
		public void renderIcon(MatrixStack matrixStack, float f, int i) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEXTURE);
			DrawableHelper.drawTexture(matrixStack, 0, 0, 128.0F, 0.0F, 16, 16, 256, 256);
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}
}
