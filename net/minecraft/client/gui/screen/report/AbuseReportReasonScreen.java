/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.report;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.report.AbuseReportReason;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AbuseReportReasonScreen
extends Screen {
    private static final String ABOUT_JAVA_REPORTING_URL = "https://aka.ms/aboutjavareporting";
    private static final Text TITLE_TEXT = Text.translatable("gui.abuseReport.reason.title");
    private static final Text DESCRIPTION_TEXT = Text.translatable("gui.abuseReport.reason.description");
    private static final Text READ_INFO_TEXT = Text.translatable("gui.chatReport.read_info");
    private static final int REASON_LIST_BOTTOM_MARGIN = 95;
    private static final int DONE_BUTTON_WIDTH = 150;
    private static final int DONE_BUTTON_HEIGHT = 20;
    private static final int SCREEN_WIDTH = 320;
    private static final int TOP_MARGIN = 4;
    @Nullable
    private final Screen parent;
    @Nullable
    private ReasonListWidget reasonList;
    @Nullable
    AbuseReportReason reason;
    private final Consumer<AbuseReportReason> reasonConsumer;

    public AbuseReportReasonScreen(@Nullable Screen parent, @Nullable AbuseReportReason reason, Consumer<AbuseReportReason> reasonConsumer) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.reason = reason;
        this.reasonConsumer = reasonConsumer;
    }

    @Override
    protected void init() {
        this.reasonList = new ReasonListWidget(this.client);
        this.reasonList.setRenderBackground(false);
        this.addSelectableChild(this.reasonList);
        ReasonListWidget.ReasonEntry reasonEntry = Util.map(this.reason, this.reasonList::getEntry);
        this.reasonList.setSelected(reasonEntry);
        int i = this.width / 2 - 150 - 5;
        this.addDrawableChild(ButtonWidget.createBuilder(READ_INFO_TEXT, button -> this.client.setScreen(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getOperatingSystem().open(ABOUT_JAVA_REPORTING_URL);
            }
            this.client.setScreen(this);
        }, ABOUT_JAVA_REPORTING_URL, true))).setPositionAndSize(i, this.getDoneButtonY(), 150, 20).build());
        int j = this.width / 2 + 5;
        this.addDrawableChild(ButtonWidget.createBuilder(ScreenTexts.DONE, button -> {
            ReasonListWidget.ReasonEntry reasonEntry = (ReasonListWidget.ReasonEntry)this.reasonList.getSelectedOrNull();
            if (reasonEntry != null) {
                this.reasonConsumer.accept(reasonEntry.getReason());
            }
            this.client.setScreen(this.parent);
        }).setPositionAndSize(j, this.getDoneButtonY(), 150, 20).build());
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.reasonList.render(matrices, mouseX, mouseY, delta);
        AbuseReportReasonScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        AbuseReportReasonScreen.fill(matrices, this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), 0x7F000000);
        AbuseReportReasonScreen.drawTextWithShadow(matrices, this.textRenderer, DESCRIPTION_TEXT, this.getLeft() + 4, this.getTop() + 4, -8421505);
        ReasonListWidget.ReasonEntry reasonEntry = (ReasonListWidget.ReasonEntry)this.reasonList.getSelectedOrNull();
        if (reasonEntry != null) {
            int i = this.getLeft() + 4 + 16;
            int j = this.getRight() - 4;
            int k = this.getTop() + 4 + this.textRenderer.fontHeight + 2;
            int l = this.getBottom() - 4;
            int m = j - i;
            int n = l - k;
            int o = this.textRenderer.getWrappedLinesHeight(reasonEntry.reason.getDescription(), m);
            this.textRenderer.drawTrimmed(reasonEntry.reason.getDescription(), i, k + (n - o) / 2, m, -1);
        }
    }

    private int getDoneButtonY() {
        return this.height - 20 - 4;
    }

    private int getLeft() {
        return (this.width - 320) / 2;
    }

    private int getRight() {
        return (this.width + 320) / 2;
    }

    private int getTop() {
        return this.height - 95 + 4;
    }

    private int getBottom() {
        return this.getDoneButtonY() - 4;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Environment(value=EnvType.CLIENT)
    public class ReasonListWidget
    extends AlwaysSelectedEntryListWidget<ReasonEntry> {
        public ReasonListWidget(MinecraftClient client) {
            super(client, AbuseReportReasonScreen.this.width, AbuseReportReasonScreen.this.height, 40, AbuseReportReasonScreen.this.height - 95, 18);
            for (AbuseReportReason abuseReportReason : AbuseReportReason.values()) {
                if (!abuseReportReason.isReportable()) continue;
                this.addEntry(new ReasonEntry(abuseReportReason));
            }
        }

        @Nullable
        public ReasonEntry getEntry(AbuseReportReason reason) {
            return this.children().stream().filter(entry -> entry.reason == reason).findFirst().orElse(null);
        }

        @Override
        public int getRowWidth() {
            return 320;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.getRowRight() - 2;
        }

        @Override
        protected boolean isFocused() {
            return AbuseReportReasonScreen.this.getFocused() == this;
        }

        @Override
        public void setSelected(@Nullable ReasonEntry reasonEntry) {
            super.setSelected(reasonEntry);
            AbuseReportReasonScreen.this.reason = reasonEntry != null ? reasonEntry.getReason() : null;
        }

        @Environment(value=EnvType.CLIENT)
        public class ReasonEntry
        extends AlwaysSelectedEntryListWidget.Entry<ReasonEntry> {
            final AbuseReportReason reason;

            public ReasonEntry(AbuseReportReason reason) {
                this.reason = reason;
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                int i = x + 1;
                int j = y + (entryHeight - ((AbuseReportReasonScreen)AbuseReportReasonScreen.this).textRenderer.fontHeight) / 2 + 1;
                DrawableHelper.drawTextWithShadow(matrices, AbuseReportReasonScreen.this.textRenderer, this.reason.getText(), i, j, -1);
            }

            @Override
            public Text getNarration() {
                return Text.translatable("gui.abuseReport.reason.narration", this.reason.getText(), this.reason.getDescription());
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    ReasonListWidget.this.setSelected(this);
                    return true;
                }
                return false;
            }

            public AbuseReportReason getReason() {
                return this.reason;
            }
        }
    }
}

