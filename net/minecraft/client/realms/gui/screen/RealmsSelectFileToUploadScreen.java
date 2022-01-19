/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsResetWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsUploadScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsSelectFileToUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    static final Text WORLD_LANG = new TranslatableText("selectWorld.world");
    static final Text HARDCORE_TEXT = new TranslatableText("mco.upload.hardcore").formatted(Formatting.DARK_RED);
    static final Text CHEATS_TEXT = new TranslatableText("selectWorld.cheats");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private final RealmsResetWorldScreen parent;
    private final long worldId;
    private final int slotId;
    ButtonWidget uploadButton;
    List<LevelSummary> levelList = Lists.newArrayList();
    int selectedWorld = -1;
    WorldSelectionList worldSelectionList;
    private final Runnable onBack;

    public RealmsSelectFileToUploadScreen(long worldId, int slotId, RealmsResetWorldScreen parent, Runnable onBack) {
        super(new TranslatableText("mco.upload.select.world.title"));
        this.parent = parent;
        this.worldId = worldId;
        this.slotId = slotId;
        this.onBack = onBack;
    }

    private void loadLevelList() throws Exception {
        this.levelList = this.client.getLevelStorage().getLevelList().stream().filter(a -> !a.requiresConversion() && !a.isLocked()).sorted((a, b) -> {
            if (a.getLastPlayed() < b.getLastPlayed()) {
                return 1;
            }
            if (a.getLastPlayed() > b.getLastPlayed()) {
                return -1;
            }
            return a.getName().compareTo(b.getName());
        }).collect(Collectors.toList());
        for (LevelSummary levelSummary : this.levelList) {
            this.worldSelectionList.addEntry(levelSummary);
        }
    }

    @Override
    public void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.worldSelectionList = new WorldSelectionList();
        try {
            this.loadLevelList();
        } catch (Exception exception) {
            LOGGER.error("Couldn't load level list", exception);
            this.client.setScreen(new RealmsGenericErrorScreen(new LiteralText("Unable to load worlds"), Text.of(exception.getMessage()), this.parent));
            return;
        }
        this.addSelectableChild(this.worldSelectionList);
        this.uploadButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 154, this.height - 32, 153, 20, new TranslatableText("mco.upload.button.name"), button -> this.upload()));
        this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 6, this.height - 32, 153, 20, ScreenTexts.BACK, button -> this.client.setScreen(this.parent)));
        this.addLabel(new RealmsLabel(new TranslatableText("mco.upload.select.world.subtitle"), this.width / 2, RealmsSelectFileToUploadScreen.row(-1), 0xA0A0A0));
        if (this.levelList.isEmpty()) {
            this.addLabel(new RealmsLabel(new TranslatableText("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, 0xFFFFFF));
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
        if (this.selectedWorld != -1 && !this.levelList.get(this.selectedWorld).isHardcore()) {
            LevelSummary levelSummary = this.levelList.get(this.selectedWorld);
            this.client.setScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.parent, levelSummary, this.onBack));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.worldSelectionList.render(matrices, mouseX, mouseY, delta);
        RealmsSelectFileToUploadScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    static Text getGameModeName(LevelSummary summary) {
        return summary.getGameMode().getTranslatableName();
    }

    static String getLastPlayed(LevelSummary summary) {
        return DATE_FORMAT.format(new Date(summary.getLastPlayed()));
    }

    @Environment(value=EnvType.CLIENT)
    class WorldSelectionList
    extends RealmsObjectSelectionList<WorldListEntry> {
        public WorldSelectionList() {
            super(RealmsSelectFileToUploadScreen.this.width, RealmsSelectFileToUploadScreen.this.height, RealmsSelectFileToUploadScreen.row(0), RealmsSelectFileToUploadScreen.this.height - 40, 36);
        }

        public void addEntry(LevelSummary summary) {
            this.addEntry(new WorldListEntry(summary));
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

        @Override
        public void setSelected(@Nullable WorldListEntry worldListEntry) {
            super.setSelected(worldListEntry);
            RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(worldListEntry);
            RealmsSelectFileToUploadScreen.this.uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getEntryCount() && !RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld).isHardcore();
        }
    }

    @Environment(value=EnvType.CLIENT)
    class WorldListEntry
    extends AlwaysSelectedEntryListWidget.Entry<WorldListEntry> {
        private final LevelSummary summary;
        private final String displayName;
        private final String nameAndLastPlayed;
        private final Text details;

        public WorldListEntry(LevelSummary summary) {
            this.summary = summary;
            this.displayName = summary.getDisplayName();
            this.nameAndLastPlayed = summary.getName() + " (" + RealmsSelectFileToUploadScreen.getLastPlayed(summary) + ")";
            Text text = summary.isHardcore() ? HARDCORE_TEXT : RealmsSelectFileToUploadScreen.getGameModeName(summary);
            if (summary.hasCheats()) {
                text = text.shallowCopy().append(", ").append(CHEATS_TEXT);
            }
            this.details = text;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.renderItem(matrices, index, x, y);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            RealmsSelectFileToUploadScreen.this.worldSelectionList.setSelected(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.summary));
            return true;
        }

        protected void renderItem(MatrixStack matrices, int index, int x, int y) {
            Object string = this.displayName.isEmpty() ? WORLD_LANG + " " + (index + 1) : this.displayName;
            RealmsSelectFileToUploadScreen.this.textRenderer.draw(matrices, (String)string, (float)(x + 2), (float)(y + 1), 0xFFFFFF);
            RealmsSelectFileToUploadScreen.this.textRenderer.draw(matrices, this.nameAndLastPlayed, (float)(x + 2), (float)(y + 12), 0x808080);
            RealmsSelectFileToUploadScreen.this.textRenderer.draw(matrices, this.details, (float)(x + 2), (float)(y + 12 + 10), 0x808080);
        }

        @Override
        public Text getNarration() {
            Text text = ScreenTexts.joinLines(new LiteralText(this.summary.getDisplayName()), new LiteralText(RealmsSelectFileToUploadScreen.getLastPlayed(this.summary)), RealmsSelectFileToUploadScreen.getGameModeName(this.summary));
            return new TranslatableText("narrator.select", text);
        }
    }
}

