/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsResetWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsUploadScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsSelectFileToUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Text worldLang = new TranslatableText("selectWorld.world");
    private static final Text conversionLang = new TranslatableText("selectWorld.conversion");
    private static final Text field_26507 = new TranslatableText("mco.upload.hardcore").formatted(Formatting.DARK_RED);
    private static final Text field_26508 = new TranslatableText("selectWorld.cheats");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private final RealmsResetWorldScreen parent;
    private final long worldId;
    private final int slotId;
    private ButtonWidget uploadButton;
    private List<LevelSummary> levelList = Lists.newArrayList();
    private int selectedWorld = -1;
    private WorldSelectionList worldSelectionList;
    private RealmsLabel titleLabel;
    private RealmsLabel subtitleLabel;
    private RealmsLabel field_20063;
    private final Runnable field_22717;

    public RealmsSelectFileToUploadScreen(long worldId, int slotId, RealmsResetWorldScreen parent, Runnable runnable) {
        this.parent = parent;
        this.worldId = worldId;
        this.slotId = slotId;
        this.field_22717 = runnable;
    }

    private void loadLevelList() throws Exception {
        this.levelList = this.client.getLevelStorage().getLevelList().stream().sorted((levelSummary, levelSummary2) -> {
            if (levelSummary.getLastPlayed() < levelSummary2.getLastPlayed()) {
                return 1;
            }
            if (levelSummary.getLastPlayed() > levelSummary2.getLastPlayed()) {
                return -1;
            }
            return levelSummary.getName().compareTo(levelSummary2.getName());
        }).collect(Collectors.toList());
        for (LevelSummary levelSummary3 : this.levelList) {
            this.worldSelectionList.addEntry(levelSummary3);
        }
    }

    @Override
    public void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.worldSelectionList = new WorldSelectionList();
        try {
            this.loadLevelList();
        } catch (Exception exception) {
            LOGGER.error("Couldn't load level list", (Throwable)exception);
            this.client.openScreen(new RealmsGenericErrorScreen(new LiteralText("Unable to load worlds"), Text.of(exception.getMessage()), this.parent));
            return;
        }
        this.addChild(this.worldSelectionList);
        this.uploadButton = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 32, 153, 20, new TranslatableText("mco.upload.button.name"), buttonWidget -> this.upload()));
        this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
        this.addButton(new ButtonWidget(this.width / 2 + 6, this.height - 32, 153, 20, ScreenTexts.BACK, buttonWidget -> this.client.openScreen(this.parent)));
        this.titleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.upload.select.world.title"), this.width / 2, 13, 0xFFFFFF));
        this.subtitleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.upload.select.world.subtitle"), this.width / 2, RealmsSelectFileToUploadScreen.row(-1), 0xA0A0A0));
        this.field_20063 = this.levelList.isEmpty() ? this.addChild(new RealmsLabel(new TranslatableText("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, 0xFFFFFF)) : null;
        this.narrateLabels();
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    private void upload() {
        if (this.selectedWorld != -1 && !this.levelList.get(this.selectedWorld).isHardcore()) {
            LevelSummary levelSummary = this.levelList.get(this.selectedWorld);
            this.client.openScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.parent, levelSummary, this.field_22717));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.worldSelectionList.render(matrices, mouseX, mouseY, delta);
        this.titleLabel.render(this, matrices);
        this.subtitleLabel.render(this, matrices);
        if (this.field_20063 != null) {
            this.field_20063.render(this, matrices);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private static Text method_21400(LevelSummary levelSummary) {
        return levelSummary.getGameMode().getTranslatableName();
    }

    private static String method_21404(LevelSummary levelSummary) {
        return DATE_FORMAT.format(new Date(levelSummary.getLastPlayed()));
    }

    @Environment(value=EnvType.CLIENT)
    class WorldListEntry
    extends AlwaysSelectedEntryListWidget.Entry<WorldListEntry> {
        private final LevelSummary field_22718;
        private final String field_26509;
        private final String field_26510;
        private final Text field_26511;

        public WorldListEntry(LevelSummary levelSummary) {
            this.field_22718 = levelSummary;
            this.field_26509 = levelSummary.getDisplayName();
            this.field_26510 = levelSummary.getName() + " (" + RealmsSelectFileToUploadScreen.method_21404(levelSummary) + ")";
            if (levelSummary.requiresConversion()) {
                this.field_26511 = conversionLang;
            } else {
                Text text = levelSummary.isHardcore() ? field_26507 : RealmsSelectFileToUploadScreen.method_21400(levelSummary);
                if (levelSummary.hasCheats()) {
                    text = text.shallowCopy().append(", ").append(field_26508);
                }
                this.field_26511 = text;
            }
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.renderItem(matrices, this.field_22718, index, x, y);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            RealmsSelectFileToUploadScreen.this.worldSelectionList.setSelected(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.field_22718));
            return true;
        }

        protected void renderItem(MatrixStack matrixStack, LevelSummary levelSummary, int i, int j, int k) {
            String string = this.field_26509.isEmpty() ? worldLang + " " + (i + 1) : this.field_26509;
            RealmsSelectFileToUploadScreen.this.textRenderer.draw(matrixStack, string, (float)(j + 2), (float)(k + 1), 0xFFFFFF);
            RealmsSelectFileToUploadScreen.this.textRenderer.draw(matrixStack, this.field_26510, (float)(j + 2), (float)(k + 12), 0x808080);
            RealmsSelectFileToUploadScreen.this.textRenderer.draw(matrixStack, this.field_26511, (float)(j + 2), (float)(k + 12 + 10), 0x808080);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class WorldSelectionList
    extends RealmsObjectSelectionList<WorldListEntry> {
        public WorldSelectionList() {
            super(RealmsSelectFileToUploadScreen.this.width, RealmsSelectFileToUploadScreen.this.height, RealmsSelectFileToUploadScreen.row(0), RealmsSelectFileToUploadScreen.this.height - 40, 36);
        }

        public void addEntry(LevelSummary levelSummary) {
            this.addEntry(new WorldListEntry(levelSummary));
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
        public void setSelected(int index) {
            this.setSelectedItem(index);
            if (index != -1) {
                LevelSummary levelSummary = (LevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(index);
                String string = I18n.translate("narrator.select.list.position", index + 1, RealmsSelectFileToUploadScreen.this.levelList.size());
                String string2 = Realms.joinNarrations(Arrays.asList(levelSummary.getDisplayName(), RealmsSelectFileToUploadScreen.method_21404(levelSummary), RealmsSelectFileToUploadScreen.method_21400(levelSummary).getString(), string));
                Realms.narrateNow(I18n.translate("narrator.select", string2));
            }
        }

        @Override
        public void setSelected(@Nullable WorldListEntry worldListEntry) {
            super.setSelected(worldListEntry);
            RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(worldListEntry);
            ((RealmsSelectFileToUploadScreen)RealmsSelectFileToUploadScreen.this).uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount() && !((LevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld)).isHardcore();
        }
    }
}

