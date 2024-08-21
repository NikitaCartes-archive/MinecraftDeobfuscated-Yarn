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
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.session.report.AbuseReportReason;
import net.minecraft.client.session.report.AbuseReportType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Nullables;
import net.minecraft.util.Urls;

@Environment(EnvType.CLIENT)
public class AbuseReportReasonScreen extends Screen {
	private static final Text TITLE_TEXT = Text.translatable("gui.abuseReport.reason.title");
	private static final Text DESCRIPTION_TEXT = Text.translatable("gui.abuseReport.reason.description");
	private static final Text READ_INFO_TEXT = Text.translatable("gui.abuseReport.read_info");
	private static final int field_49546 = 320;
	private static final int field_49547 = 62;
	private static final int TOP_MARGIN = 4;
	@Nullable
	private final Screen parent;
	@Nullable
	private AbuseReportReasonScreen.ReasonListWidget reasonList;
	@Nullable
	AbuseReportReason reason;
	private final Consumer<AbuseReportReason> reasonConsumer;
	final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	final AbuseReportType reportType;

	public AbuseReportReasonScreen(
		@Nullable Screen parent, @Nullable AbuseReportReason reason, AbuseReportType reportType, Consumer<AbuseReportReason> reasonConsumer
	) {
		super(TITLE_TEXT);
		this.parent = parent;
		this.reason = reason;
		this.reasonConsumer = reasonConsumer;
		this.reportType = reportType;
	}

	@Override
	protected void init() {
		this.layout.addHeader(TITLE_TEXT, this.textRenderer);
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(DirectionalLayoutWidget.vertical().spacing(4));
		this.reasonList = directionalLayoutWidget.add(new AbuseReportReasonScreen.ReasonListWidget(this.client));
		AbuseReportReasonScreen.ReasonListWidget.ReasonEntry reasonEntry = Nullables.map(this.reason, this.reasonList::getEntry);
		this.reasonList.setSelected(reasonEntry);
		directionalLayoutWidget.add(EmptyWidget.ofHeight(this.getHeight()));
		DirectionalLayoutWidget directionalLayoutWidget2 = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
		directionalLayoutWidget2.add(ButtonWidget.builder(READ_INFO_TEXT, ConfirmLinkScreen.opening(this, Urls.ABOUT_JAVA_REPORTING)).build());
		directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.DONE, button -> {
			AbuseReportReasonScreen.ReasonListWidget.ReasonEntry reasonEntryx = this.reasonList.getSelectedOrNull();
			if (reasonEntryx != null) {
				this.reasonConsumer.accept(reasonEntryx.getReason());
			}

			this.client.setScreen(this.parent);
		}).build());
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
		if (this.reasonList != null) {
			this.reasonList.position(this.width, this.getReasonListHeight(), this.layout.getHeaderHeight());
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.fill(this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), -16777216);
		context.drawBorder(this.getLeft(), this.getTop(), this.getWidth(), this.getHeight(), Colors.WHITE);
		context.drawTextWithShadow(this.textRenderer, DESCRIPTION_TEXT, this.getLeft() + 4, this.getTop() + 4, Colors.WHITE);
		AbuseReportReasonScreen.ReasonListWidget.ReasonEntry reasonEntry = this.reasonList.getSelectedOrNull();
		if (reasonEntry != null) {
			int i = this.getLeft() + 4 + 16;
			int j = this.getRight() - 4;
			int k = this.getTop() + 4 + 9 + 2;
			int l = this.getBottom() - 4;
			int m = j - i;
			int n = l - k;
			int o = this.textRenderer.getWrappedLinesHeight(reasonEntry.reason.getDescription(), m);
			context.drawTextWrapped(this.textRenderer, reasonEntry.reason.getDescription(), i, k + (n - o) / 2, m, Colors.WHITE);
		}
	}

	private int getLeft() {
		return (this.width - 320) / 2;
	}

	private int getRight() {
		return (this.width + 320) / 2;
	}

	private int getTop() {
		return this.getBottom() - this.getHeight();
	}

	private int getBottom() {
		return this.height - this.layout.getFooterHeight() - 4;
	}

	private int getWidth() {
		return 320;
	}

	private int getHeight() {
		return 62;
	}

	int getReasonListHeight() {
		return this.layout.getContentHeight() - this.getHeight() - 8;
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Environment(EnvType.CLIENT)
	public class ReasonListWidget extends AlwaysSelectedEntryListWidget<AbuseReportReasonScreen.ReasonListWidget.ReasonEntry> {
		public ReasonListWidget(final MinecraftClient client) {
			super(
				client, AbuseReportReasonScreen.this.width, AbuseReportReasonScreen.this.getReasonListHeight(), AbuseReportReasonScreen.this.layout.getHeaderHeight(), 18
			);

			for (AbuseReportReason abuseReportReason : AbuseReportReason.values()) {
				if (!AbuseReportReason.getExcludedReasonsForType(AbuseReportReasonScreen.this.reportType).contains(abuseReportReason)) {
					this.addEntry(new AbuseReportReasonScreen.ReasonListWidget.ReasonEntry(abuseReportReason));
				}
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

		public void setSelected(@Nullable AbuseReportReasonScreen.ReasonListWidget.ReasonEntry reasonEntry) {
			super.setSelected(reasonEntry);
			AbuseReportReasonScreen.this.reason = reasonEntry != null ? reasonEntry.getReason() : null;
		}

		@Environment(EnvType.CLIENT)
		public class ReasonEntry extends AlwaysSelectedEntryListWidget.Entry<AbuseReportReasonScreen.ReasonListWidget.ReasonEntry> {
			final AbuseReportReason reason;

			public ReasonEntry(final AbuseReportReason reason) {
				this.reason = reason;
			}

			@Override
			public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				int i = x + 1;
				int j = y + (entryHeight - 9) / 2 + 1;
				context.drawTextWithShadow(AbuseReportReasonScreen.this.textRenderer, this.reason.getText(), i, j, Colors.WHITE);
			}

			@Override
			public Text getNarration() {
				return Text.translatable("gui.abuseReport.reason.narration", this.reason.getText(), this.reason.getDescription());
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				ReasonListWidget.this.setSelected(this);
				return super.mouseClicked(mouseX, mouseY, button);
			}

			public AbuseReportReason getReason() {
				return this.reason;
			}
		}
	}
}
