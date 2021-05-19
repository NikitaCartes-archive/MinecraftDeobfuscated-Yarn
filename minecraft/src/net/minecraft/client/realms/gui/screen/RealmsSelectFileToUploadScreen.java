package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RealmsSelectFileToUploadScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	static final Text worldLang = new TranslatableText("selectWorld.world");
	static final Text conversionLang = new TranslatableText("selectWorld.conversion");
	static final Text HARDCORE_TEXT = new TranslatableText("mco.upload.hardcore").formatted(Formatting.DARK_RED);
	static final Text CHEATS_TEXT = new TranslatableText("selectWorld.cheats");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private final RealmsResetWorldScreen parent;
	private final long worldId;
	private final int slotId;
	ButtonWidget uploadButton;
	List<LevelSummary> levelList = Lists.<LevelSummary>newArrayList();
	int selectedWorld = -1;
	RealmsSelectFileToUploadScreen.WorldSelectionList worldSelectionList;
	private final Runnable field_22717;

	public RealmsSelectFileToUploadScreen(long worldId, int slotId, RealmsResetWorldScreen parent, Runnable runnable) {
		super(new TranslatableText("mco.upload.select.world.title"));
		this.parent = parent;
		this.worldId = worldId;
		this.slotId = slotId;
		this.field_22717 = runnable;
	}

	private void loadLevelList() throws Exception {
		this.levelList = (List<LevelSummary>)this.client.getLevelStorage().getLevelList().stream().sorted((levelSummaryx, levelSummary2) -> {
			if (levelSummaryx.getLastPlayed() < levelSummary2.getLastPlayed()) {
				return 1;
			} else {
				return levelSummaryx.getLastPlayed() > levelSummary2.getLastPlayed() ? -1 : levelSummaryx.getName().compareTo(levelSummary2.getName());
			}
		}).collect(Collectors.toList());

		for (LevelSummary levelSummary : this.levelList) {
			this.worldSelectionList.addEntry(levelSummary);
		}
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.worldSelectionList = new RealmsSelectFileToUploadScreen.WorldSelectionList();

		try {
			this.loadLevelList();
		} catch (Exception var2) {
			LOGGER.error("Couldn't load level list", (Throwable)var2);
			this.client.openScreen(new RealmsGenericErrorScreen(new LiteralText("Unable to load worlds"), Text.of(var2.getMessage()), this.parent));
			return;
		}

		this.addSelectableChild(this.worldSelectionList);
		this.uploadButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 154, this.height - 32, 153, 20, new TranslatableText("mco.upload.button.name"), button -> this.upload())
		);
		this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 6, this.height - 32, 153, 20, ScreenTexts.BACK, button -> this.client.openScreen(this.parent)));
		this.method_37107(new RealmsLabel(new TranslatableText("mco.upload.select.world.subtitle"), this.width / 2, row(-1), 10526880));
		if (this.levelList.isEmpty()) {
			this.method_37107(new RealmsLabel(new TranslatableText("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, 16777215));
		}
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(this.getTitle(), this.narrateLabels());
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	private void upload() {
		if (this.selectedWorld != -1 && !((LevelSummary)this.levelList.get(this.selectedWorld)).isHardcore()) {
			LevelSummary levelSummary = (LevelSummary)this.levelList.get(this.selectedWorld);
			this.client.openScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.parent, levelSummary, this.field_22717));
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.worldSelectionList.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.client.openScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	static Text method_21400(LevelSummary levelSummary) {
		return levelSummary.getGameMode().getTranslatableName();
	}

	static String method_21404(LevelSummary levelSummary) {
		return DATE_FORMAT.format(new Date(levelSummary.getLastPlayed()));
	}

	@Environment(EnvType.CLIENT)
	class WorldListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmsSelectFileToUploadScreen.WorldListEntry> {
		private final LevelSummary field_22718;
		private final String field_26509;
		private final String field_26510;
		private final Text field_26511;

		public WorldListEntry(LevelSummary levelSummary) {
			this.field_22718 = levelSummary;
			this.field_26509 = levelSummary.getDisplayName();
			this.field_26510 = levelSummary.getName() + " (" + RealmsSelectFileToUploadScreen.method_21404(levelSummary) + ")";
			if (levelSummary.requiresConversion()) {
				this.field_26511 = RealmsSelectFileToUploadScreen.conversionLang;
			} else {
				Text text;
				if (levelSummary.isHardcore()) {
					text = RealmsSelectFileToUploadScreen.HARDCORE_TEXT;
				} else {
					text = RealmsSelectFileToUploadScreen.method_21400(levelSummary);
				}

				if (levelSummary.hasCheats()) {
					text = text.shallowCopy().append(", ").append(RealmsSelectFileToUploadScreen.CHEATS_TEXT);
				}

				this.field_26511 = text;
			}
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.renderItem(matrices, index, x, y);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			RealmsSelectFileToUploadScreen.this.worldSelectionList.setSelected(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.field_22718));
			return true;
		}

		protected void renderItem(MatrixStack matrices, int i, int j, int k) {
			String string;
			if (this.field_26509.isEmpty()) {
				string = RealmsSelectFileToUploadScreen.worldLang + " " + (i + 1);
			} else {
				string = this.field_26509;
			}

			RealmsSelectFileToUploadScreen.this.textRenderer.draw(matrices, string, (float)(j + 2), (float)(k + 1), 16777215);
			RealmsSelectFileToUploadScreen.this.textRenderer.draw(matrices, this.field_26510, (float)(j + 2), (float)(k + 12), 8421504);
			RealmsSelectFileToUploadScreen.this.textRenderer.draw(matrices, this.field_26511, (float)(j + 2), (float)(k + 12 + 10), 8421504);
		}

		@Override
		public Text method_37006() {
			Text text = ScreenTexts.joinLines(
				new LiteralText(this.field_22718.getDisplayName()),
				new LiteralText(RealmsSelectFileToUploadScreen.method_21404(this.field_22718)),
				RealmsSelectFileToUploadScreen.method_21400(this.field_22718)
			);
			return new TranslatableText("narrator.select", text);
		}
	}

	@Environment(EnvType.CLIENT)
	class WorldSelectionList extends RealmsObjectSelectionList<RealmsSelectFileToUploadScreen.WorldListEntry> {
		public WorldSelectionList() {
			super(
				RealmsSelectFileToUploadScreen.this.width,
				RealmsSelectFileToUploadScreen.this.height,
				RealmsSelectFileToUploadScreen.row(0),
				RealmsSelectFileToUploadScreen.this.height - 40,
				36
			);
		}

		public void addEntry(LevelSummary levelSummary) {
			this.addEntry(RealmsSelectFileToUploadScreen.this.new WorldListEntry(levelSummary));
		}

		@Override
		public int getMaxPosition() {
			return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
		}

		@Override
		public boolean isFocused() {
			return RealmsSelectFileToUploadScreen.this.getFocused() == this;
		}

		@Override
		public void renderBackground(MatrixStack matrices) {
			RealmsSelectFileToUploadScreen.this.renderBackground(matrices);
		}

		public void setSelected(@Nullable RealmsSelectFileToUploadScreen.WorldListEntry worldListEntry) {
			super.setSelected(worldListEntry);
			RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(worldListEntry);
			RealmsSelectFileToUploadScreen.this.uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0
				&& RealmsSelectFileToUploadScreen.this.selectedWorld < this.getEntryCount()
				&& !((LevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld)).isHardcore();
		}
	}
}
