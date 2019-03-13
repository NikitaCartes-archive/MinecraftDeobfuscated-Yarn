package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class SpectatorMenu {
	private static final SpectatorMenuCommand field_3261 = new SpectatorMenu.CloseSpectatorMenuCommand();
	private static final SpectatorMenuCommand field_3262 = new SpectatorMenu.ChangePageSpectatorMenuCommand(-1, true);
	private static final SpectatorMenuCommand field_3256 = new SpectatorMenu.ChangePageSpectatorMenuCommand(1, true);
	private static final SpectatorMenuCommand field_3259 = new SpectatorMenu.ChangePageSpectatorMenuCommand(1, false);
	public static final SpectatorMenuCommand field_3260 = new SpectatorMenuCommand() {
		@Override
		public void use(SpectatorMenu spectatorMenu) {
		}

		@Override
		public TextComponent method_16892() {
			return new StringTextComponent("");
		}

		@Override
		public void renderIcon(float f, int i) {
		}

		@Override
		public boolean enabled() {
			return false;
		}
	};
	private final SpectatorMenuCloseCallback field_3255;
	private final List<SpectatorMenuState> stateStack = Lists.<SpectatorMenuState>newArrayList();
	private SpectatorMenuCommandGroup field_3258;
	private int selectedSlot = -1;
	private int page;

	public SpectatorMenu(SpectatorMenuCloseCallback spectatorMenuCloseCallback) {
		this.field_3258 = new RootSpectatorCommandGroup();
		this.field_3255 = spectatorMenuCloseCallback;
	}

	public SpectatorMenuCommand method_2777(int i) {
		int j = i + this.page * 6;
		if (this.page > 0 && i == 0) {
			return field_3262;
		} else if (i == 7) {
			return j < this.field_3258.getCommands().size() ? field_3256 : field_3259;
		} else if (i == 8) {
			return field_3261;
		} else {
			return j >= 0 && j < this.field_3258.getCommands().size()
				? MoreObjects.firstNonNull((SpectatorMenuCommand)this.field_3258.getCommands().get(j), field_3260)
				: field_3260;
		}
	}

	public List<SpectatorMenuCommand> getCommands() {
		List<SpectatorMenuCommand> list = Lists.<SpectatorMenuCommand>newArrayList();

		for (int i = 0; i <= 8; i++) {
			list.add(this.method_2777(i));
		}

		return list;
	}

	public SpectatorMenuCommand method_2774() {
		return this.method_2777(this.selectedSlot);
	}

	public SpectatorMenuCommandGroup method_2776() {
		return this.field_3258;
	}

	public void setSelectedSlot(int i) {
		SpectatorMenuCommand spectatorMenuCommand = this.method_2777(i);
		if (spectatorMenuCommand != field_3260) {
			if (this.selectedSlot == i && spectatorMenuCommand.enabled()) {
				spectatorMenuCommand.use(this);
			} else {
				this.selectedSlot = i;
			}
		}
	}

	public void close() {
		this.field_3255.close(this);
	}

	public int getSelectedSlot() {
		return this.selectedSlot;
	}

	public void method_2778(SpectatorMenuCommandGroup spectatorMenuCommandGroup) {
		this.stateStack.add(this.method_2772());
		this.field_3258 = spectatorMenuCommandGroup;
		this.selectedSlot = -1;
		this.page = 0;
	}

	public SpectatorMenuState method_2772() {
		return new SpectatorMenuState(this.field_3258, this.getCommands(), this.selectedSlot);
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
		public TextComponent method_16892() {
			return this.direction < 0 ? new TranslatableTextComponent("spectatorMenu.previous_page") : new TranslatableTextComponent("spectatorMenu.next_page");
		}

		@Override
		public void renderIcon(float f, int i) {
			MinecraftClient.getInstance().method_1531().method_4618(SpectatorHud.field_2199);
			if (this.direction < 0) {
				DrawableHelper.drawTexturedRect(0, 0, 144.0F, 0.0F, 16, 16, 256.0F, 256.0F);
			} else {
				DrawableHelper.drawTexturedRect(0, 0, 160.0F, 0.0F, 16, 16, 256.0F, 256.0F);
			}
		}

		@Override
		public boolean enabled() {
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
		public TextComponent method_16892() {
			return new TranslatableTextComponent("spectatorMenu.close");
		}

		@Override
		public void renderIcon(float f, int i) {
			MinecraftClient.getInstance().method_1531().method_4618(SpectatorHud.field_2199);
			DrawableHelper.drawTexturedRect(0, 0, 128.0F, 0.0F, 16, 16, 256.0F, 256.0F);
		}

		@Override
		public boolean enabled() {
			return true;
		}
	}
}
