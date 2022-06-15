package net.minecraft.client.gui.screen.abusereport;

import java.util.function.Consumer;
import javax.annotation.Nullable;
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

@Environment(EnvType.CLIENT)
public class AbuseReportReasonScreen extends Screen {
	private static final Text TITLE_TEXT = Text.translatable("gui.abuseReport.reason.title");
	private static final Text DESCRIPTION_TEXT = Text.translatable("gui.abuseReport.reason.description");
	private static final int REASON_LIST_BOTTOM_MARGIN = 80;
	@Nullable
	private final Screen parent;
	@Nullable
	private AbuseReportReasonScreen.ReasonListWidget reasonList;
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
		this.reasonList = new AbuseReportReasonScreen.ReasonListWidget(this.client);
		this.reasonList.setRenderBackground(false);
		this.addSelectableChild(this.reasonList);
		AbuseReportReasonScreen.ReasonListWidget.ReasonEntry reasonEntry = Util.map(this.reason, this.reasonList::getEntry);
		this.reasonList.setSelected(reasonEntry);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 32, 150, 20, ScreenTexts.DONE, buttonWidget -> {
			AbuseReportReasonScreen.ReasonListWidget.ReasonEntry reasonEntryx = this.reasonList.getSelectedOrNull();
			if (reasonEntryx != null) {
				this.reasonConsumer.accept(reasonEntryx.getReason());
			}

			this.client.setScreen(this.parent);
		}));
		super.init();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.reasonList.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 16, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
		int i = this.height - 80;
		int j = this.height - 35;
		int k = this.width / 2 - 160;
		int l = this.width / 2 + 160;
		fill(matrices, k, i, l, j, 2130706432);
		drawTextWithShadow(matrices, this.textRenderer, DESCRIPTION_TEXT, k + 2, i + 2, -8421505);
		AbuseReportReasonScreen.ReasonListWidget.ReasonEntry reasonEntry = this.reasonList.getSelectedOrNull();
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

	@Environment(EnvType.CLIENT)
	public class ReasonListWidget extends AlwaysSelectedEntryListWidget<AbuseReportReasonScreen.ReasonListWidget.ReasonEntry> {
		public ReasonListWidget(MinecraftClient client) {
			super(client, AbuseReportReasonScreen.this.width, AbuseReportReasonScreen.this.height, 40, AbuseReportReasonScreen.this.height - 80, 18);

			for (AbuseReportReason abuseReportReason : AbuseReportReason.values()) {
				this.addEntry(new AbuseReportReasonScreen.ReasonListWidget.ReasonEntry(abuseReportReason));
			}
		}

		@Nullable
		public AbuseReportReasonScreen.ReasonListWidget.ReasonEntry getEntry(AbuseReportReason reason) {
			return (AbuseReportReasonScreen.ReasonListWidget.ReasonEntry)this.children().stream().filter(entry -> entry.reason == reason).findFirst().orElse(null);
		}

		@Override
		public int getRowWidth() {
			return 280;
		}

		@Environment(EnvType.CLIENT)
		public class ReasonEntry extends AlwaysSelectedEntryListWidget.Entry<AbuseReportReasonScreen.ReasonListWidget.ReasonEntry> {
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
				} else {
					return false;
				}
			}

			public AbuseReportReason getReason() {
				return this.reason;
			}
		}
	}
}
