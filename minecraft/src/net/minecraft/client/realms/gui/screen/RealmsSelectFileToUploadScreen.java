package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsSelectFileToUploadScreen extends RealmsScreen {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Text TITLE = Text.translatable("mco.upload.select.world.title");
	private static final Text LOADING_ERROR_TEXT = Text.translatable("selectWorld.unable_to_load");
	static final Text WORLD_LANG = Text.translatable("selectWorld.world");
	static final Text HARDCORE_TEXT = Text.translatable("mco.upload.hardcore").styled(style -> style.withColor(-65536));
	static final Text CHEATS_TEXT = Text.translatable("selectWorld.cheats");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private final RealmsCreateWorldScreen parent;
	private final long worldId;
	private final int slotId;
	ButtonWidget uploadButton;
	List<LevelSummary> levelList = Lists.<LevelSummary>newArrayList();
	int selectedWorld = -1;
	RealmsSelectFileToUploadScreen.WorldSelectionList worldSelectionList;

	public RealmsSelectFileToUploadScreen(long worldId, int slotId, RealmsCreateWorldScreen parent) {
		super(TITLE);
		this.parent = parent;
		this.worldId = worldId;
		this.slotId = slotId;
	}

	private void loadLevelList() throws Exception {
		LevelStorage.LevelList levelList = this.client.getLevelStorage().getLevelList();
		this.levelList = (List<LevelSummary>)((List)this.client.getLevelStorage().loadSummaries(levelList).join())
			.stream()
			.filter(a -> !a.requiresConversion() && !a.isLocked())
			.collect(Collectors.toList());

		for (LevelSummary levelSummary : this.levelList) {
			this.worldSelectionList.addEntry(levelSummary);
		}
	}

	@Override
	public void init() {
		this.worldSelectionList = new RealmsSelectFileToUploadScreen.WorldSelectionList();

		try {
			this.loadLevelList();
		} catch (Exception var2) {
			LOGGER.error("Couldn't load level list", (Throwable)var2);
			this.client.setScreen(new RealmsGenericErrorScreen(LOADING_ERROR_TEXT, Text.of(var2.getMessage()), this.parent));
			return;
		}

		this.addSelectableChild(this.worldSelectionList);
		this.uploadButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.upload.button.name"), button -> this.upload())
				.dimensions(this.width / 2 - 154, this.height - 32, 153, 20)
				.build()
		);
		this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 6, this.height - 32, 153, 20).build()
		);
		this.addLabel(new RealmsLabel(Text.translatable("mco.upload.select.world.subtitle"), this.width / 2, row(-1), -6250336));
		if (this.levelList.isEmpty()) {
			this.addLabel(new RealmsLabel(Text.translatable("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, -1));
		}
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(this.getTitle(), this.narrateLabels());
	}

	private void upload() {
		if (this.selectedWorld != -1 && !((LevelSummary)this.levelList.get(this.selectedWorld)).isHardcore()) {
			LevelSummary levelSummary = (LevelSummary)this.levelList.get(this.selectedWorld);
			this.client.setScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.parent, levelSummary));
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.worldSelectionList.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 13, -1);
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

	static Text getGameModeName(LevelSummary summary) {
		return summary.getGameMode().getTranslatableName();
	}

	static String getLastPlayed(LevelSummary summary) {
		return DATE_FORMAT.format(new Date(summary.getLastPlayed()));
	}

	@Environment(EnvType.CLIENT)
	class WorldListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmsSelectFileToUploadScreen.WorldListEntry> {
		private final LevelSummary summary;
		private final String displayName;
		private final Text nameAndLastPlayed;
		private final Text details;

		public WorldListEntry(LevelSummary summary) {
			this.summary = summary;
			this.displayName = summary.getDisplayName();
			this.nameAndLastPlayed = Text.translatable("mco.upload.entry.id", summary.getName(), RealmsSelectFileToUploadScreen.getLastPlayed(summary));
			Text text;
			if (summary.isHardcore()) {
				text = RealmsSelectFileToUploadScreen.HARDCORE_TEXT;
			} else {
				text = RealmsSelectFileToUploadScreen.getGameModeName(summary);
			}

			if (summary.hasCheats()) {
				text = Text.translatable("mco.upload.entry.cheats", text.getString(), RealmsSelectFileToUploadScreen.CHEATS_TEXT);
			}

			this.details = text;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.renderItem(context, index, x, y);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			RealmsSelectFileToUploadScreen.this.worldSelectionList.setSelected(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.summary));
			return true;
		}

		protected void renderItem(DrawContext context, int index, int x, int y) {
			String string;
			if (this.displayName.isEmpty()) {
				string = RealmsSelectFileToUploadScreen.WORLD_LANG + " " + (index + 1);
			} else {
				string = this.displayName;
			}

			context.drawText(RealmsSelectFileToUploadScreen.this.textRenderer, string, x + 2, y + 1, 16777215, false);
			context.drawText(RealmsSelectFileToUploadScreen.this.textRenderer, this.nameAndLastPlayed, x + 2, y + 12, -8355712, false);
			context.drawText(RealmsSelectFileToUploadScreen.this.textRenderer, this.details, x + 2, y + 12 + 10, -8355712, false);
		}

		@Override
		public Text getNarration() {
			Text text = ScreenTexts.joinLines(
				Text.literal(this.summary.getDisplayName()),
				Text.literal(RealmsSelectFileToUploadScreen.getLastPlayed(this.summary)),
				RealmsSelectFileToUploadScreen.getGameModeName(this.summary)
			);
			return Text.translatable("narrator.select", text);
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

		public void addEntry(LevelSummary summary) {
			this.addEntry(RealmsSelectFileToUploadScreen.this.new WorldListEntry(summary));
		}

		@Override
		public int getMaxPosition() {
			return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
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
