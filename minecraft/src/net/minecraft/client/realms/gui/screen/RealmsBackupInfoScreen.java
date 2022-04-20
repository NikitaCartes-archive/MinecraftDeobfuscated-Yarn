package net.minecraft.client.realms.gui.screen;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RealmsBackupInfoScreen extends RealmsScreen {
	private static final Text UNKNOWN = Text.method_43470("UNKNOWN");
	private final Screen parent;
	final Backup backup;
	private RealmsBackupInfoScreen.BackupInfoList backupInfoList;

	public RealmsBackupInfoScreen(Screen parent, Backup backup) {
		super(Text.method_43470("Changes from last backup"));
		this.parent = parent;
		this.backup = backup;
	}

	@Override
	public void tick() {
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20, ScreenTexts.BACK, button -> this.client.setScreen(this.parent))
		);
		this.backupInfoList = new RealmsBackupInfoScreen.BackupInfoList(this.client);
		this.addSelectableChild(this.backupInfoList);
		this.focusOn(this.backupInfoList);
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.client.setScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.backupInfoList.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	Text checkForSpecificMetadata(String key, String value) {
		String string = key.toLowerCase(Locale.ROOT);
		if (string.contains("game") && string.contains("mode")) {
			return this.gameModeMetadata(value);
		} else {
			return (Text)(string.contains("game") && string.contains("difficulty") ? this.gameDifficultyMetadata(value) : Text.method_43470(value));
		}
	}

	private Text gameDifficultyMetadata(String value) {
		try {
			return ((Difficulty)RealmsSlotOptionsScreen.DIFFICULTIES.get(Integer.parseInt(value))).getTranslatableName();
		} catch (Exception var3) {
			return UNKNOWN;
		}
	}

	private Text gameModeMetadata(String value) {
		try {
			return ((GameMode)RealmsSlotOptionsScreen.GAME_MODES.get(Integer.parseInt(value))).getSimpleTranslatableName();
		} catch (Exception var3) {
			return UNKNOWN;
		}
	}

	@Environment(EnvType.CLIENT)
	class BackupInfoList extends AlwaysSelectedEntryListWidget<RealmsBackupInfoScreen.BackupInfoListEntry> {
		public BackupInfoList(MinecraftClient client) {
			super(client, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
			this.setRenderSelection(false);
			if (RealmsBackupInfoScreen.this.backup.changeList != null) {
				RealmsBackupInfoScreen.this.backup.changeList.forEach((key, value) -> this.addEntry(RealmsBackupInfoScreen.this.new BackupInfoListEntry(key, value)));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class BackupInfoListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmsBackupInfoScreen.BackupInfoListEntry> {
		private final String key;
		private final String value;

		public BackupInfoListEntry(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			TextRenderer textRenderer = RealmsBackupInfoScreen.this.client.textRenderer;
			DrawableHelper.drawStringWithShadow(matrices, textRenderer, this.key, x, y, 10526880);
			DrawableHelper.drawTextWithShadow(matrices, textRenderer, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), x, y + 12, 16777215);
		}

		@Override
		public Text getNarration() {
			return Text.method_43469("narrator.select", this.key + " " + this.value);
		}
	}
}
