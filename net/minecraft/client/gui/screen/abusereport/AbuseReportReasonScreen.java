/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.abusereport;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.abusereport.AbuseReportReason;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AbuseReportReasonScreen
extends Screen {
    private static final Text TITLE_TEXT = Text.translatable("gui.abuseReport.reason.title");
    private static final Text DESCRIPTION_TEXT = Text.translatable("gui.abuseReport.reason.description");
    private static final int REASON_LIST_BOTTOM_MARGIN = 80;
    @Nullable
    private final Screen parent;
    @Nullable
    private ReasonListWidget reasonList;
    @Nullable
    private final AbuseReportReason reason;
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
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 32, 150, 20, ScreenTexts.DONE, buttonWidget -> {
            ReasonListWidget.ReasonEntry reasonEntry = (ReasonListWidget.ReasonEntry)this.reasonList.getSelectedOrNull();
            if (reasonEntry != null) {
                this.reasonConsumer.accept(reasonEntry.getReason());
            }
            this.client.setScreen(this.parent);
        }));
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.reasonList.render(matrices, mouseX, mouseY, delta);
        AbuseReportReasonScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        int i = this.height - 80;
        int j = this.height - 35;
        int k = this.width / 2 - 160;
        int l = this.width / 2 + 160;
        AbuseReportReasonScreen.fill(matrices, k, i, l, j, 0x7F000000);
        AbuseReportReasonScreen.drawTextWithShadow(matrices, this.textRenderer, DESCRIPTION_TEXT, k + 2, i + 2, -8421505);
        ReasonListWidget.ReasonEntry reasonEntry = (ReasonListWidget.ReasonEntry)this.reasonList.getSelectedOrNull();
        if (reasonEntry != null) {
            int m = this.textRenderer.getWrappedLinesHeight(reasonEntry.reason.getDescription(), 280);
            int n = j - i + 10;
            this.textRenderer.drawTrimmed(reasonEntry.reason.getDescription(), k + 20, i + (n - m) / 2, l - k - 40, -1);
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Environment(value=EnvType.CLIENT)
    public class ReasonListWidget
    extends AlwaysSelectedEntryListWidget<ReasonEntry> {
        public ReasonListWidget(MinecraftClient client) {
            super(client, AbuseReportReasonScreen.this.width, AbuseReportReasonScreen.this.height, 40, AbuseReportReasonScreen.this.height - 80, 18);
            for (AbuseReportReason abuseReportReason : AbuseReportReason.values()) {
                this.addEntry(new ReasonEntry(abuseReportReason));
            }
        }

        @Nullable
        public ReasonEntry getEntry(AbuseReportReason reason) {
            return this.children().stream().filter(entry -> entry.reason == reason).findFirst().orElse(null);
        }

        @Override
        public int getRowWidth() {
            return 280;
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
                DrawableHelper.drawTextWithShadow(matrices, AbuseReportReasonScreen.this.textRenderer, this.reason.getText(), x, y + 1, -1);
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

