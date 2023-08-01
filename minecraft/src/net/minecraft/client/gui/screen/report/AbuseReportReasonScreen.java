package net.minecraft.client.gui.screen.report;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.report.AbuseReportReason;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Nullables;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class AbuseReportReasonScreen extends Screen {
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
	private AbuseReportReasonScreen.ReasonListWidget reasonList;
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
		this.reasonList = new AbuseReportReasonScreen.ReasonListWidget(this.client);
		this.addSelectableChild(this.reasonList);
		AbuseReportReasonScreen.ReasonListWidget.ReasonEntry reasonEntry = Nullables.map(this.reason, this.reasonList::getEntry);
		this.reasonList.setSelected(reasonEntry);
		int i = this.width / 2 - 150 - 5;
		this.addDrawableChild(ButtonWidget.builder(READ_INFO_TEXT, button -> this.client.setScreen(new ConfirmLinkScreen(confirmed -> {
				if (confirmed) {
					Util.getOperatingSystem().open("https://aka.ms/aboutjavareporting");
				}

				this.client.setScreen(this);
			}, "https://aka.ms/aboutjavareporting", true))).dimensions(i, this.getDoneButtonY(), 150, 20).build());
		int j = this.width / 2 + 5;
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
			AbuseReportReasonScreen.ReasonListWidget.ReasonEntry reasonEntryx = this.reasonList.getSelectedOrNull();
			if (reasonEntryx != null) {
				this.reasonConsumer.accept(reasonEntryx.getReason());
			}

			this.client.setScreen(this.parent);
		}).dimensions(j, this.getDoneButtonY(), 150, 20).build());
		super.init();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.reasonList.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 16, 16777215);
		context.fill(this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), 2130706432);
		context.drawTextWithShadow(this.textRenderer, DESCRIPTION_TEXT, this.getLeft() + 4, this.getTop() + 4, -8421505);
		AbuseReportReasonScreen.ReasonListWidget.ReasonEntry reasonEntry = this.reasonList.getSelectedOrNull();
		if (reasonEntry != null) {
			int i = this.getLeft() + 4 + 16;
			int j = this.getRight() - 4;
			int k = this.getTop() + 4 + 9 + 2;
			int l = this.getBottom() - 4;
			int m = j - i;
			int n = l - k;
			int o = this.textRenderer.getWrappedLinesHeight(reasonEntry.reason.getDescription(), m);
			context.drawTextWrapped(this.textRenderer, reasonEntry.reason.getDescription(), i, k + (n - o) / 2, m, -1);
		}
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(context);
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

	@Environment(EnvType.CLIENT)
	public class ReasonListWidget extends AlwaysSelectedEntryListWidget<AbuseReportReasonScreen.ReasonListWidget.ReasonEntry> {
		public ReasonListWidget(MinecraftClient client) {
			super(client, AbuseReportReasonScreen.this.width, AbuseReportReasonScreen.this.height, 40, AbuseReportReasonScreen.this.height - 95, 18);

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
			return 320;
		}

		@Override
		protected int getScrollbarPositionX() {
			return this.getRowRight() - 2;
		}

		public void setSelected(@Nullable AbuseReportReasonScreen.ReasonListWidget.ReasonEntry reasonEntry) {
			super.setSelected(reasonEntry);
			AbuseReportReasonScreen.this.reason = reasonEntry != null ? reasonEntry.getReason() : null;
		}

		@Environment(EnvType.CLIENT)
		public class ReasonEntry extends AlwaysSelectedEntryListWidget.Entry<AbuseReportReasonScreen.ReasonListWidget.ReasonEntry> {
			final AbuseReportReason reason;

			public ReasonEntry(AbuseReportReason reason) {
				this.reason = reason;
			}

			@Override
			public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				int i = x + 1;
				int j = y + (entryHeight - 9) / 2 + 1;
				context.drawTextWithShadow(AbuseReportReasonScreen.this.textRenderer, this.reason.getText(), i, j, -1);
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
