package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.realms.util.TextRenderingUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsSelectWorldTemplateScreen extends RealmsScreen {
	static final Logger LOGGER = LogUtils.getLogger();
	static final Identifier SLOT_FRAME_TEXTURE = new Identifier("widget/slot_frame");
	private static final Text SELECT_TEXT = Text.translatable("mco.template.button.select");
	private static final Text TRAILER_TEXT = Text.translatable("mco.template.button.trailer");
	private static final Text PUBLISHER_TEXT = Text.translatable("mco.template.button.publisher");
	private static final int field_45974 = 100;
	private static final int field_45975 = 10;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	final Consumer<WorldTemplate> callback;
	RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList templateList;
	private final RealmsServer.WorldType worldType;
	private ButtonWidget selectButton;
	private ButtonWidget trailerButton;
	private ButtonWidget publisherButton;
	@Nullable
	WorldTemplate selectedTemplate = null;
	@Nullable
	String currentLink;
	@Nullable
	private Text[] warning;
	@Nullable
	List<TextRenderingUtils.Line> noTemplatesMessage;

	public RealmsSelectWorldTemplateScreen(Text title, Consumer<WorldTemplate> callback, RealmsServer.WorldType worldType) {
		this(title, callback, worldType, null);
	}

	public RealmsSelectWorldTemplateScreen(
		Text title, Consumer<WorldTemplate> callback, RealmsServer.WorldType worldType, @Nullable WorldTemplatePaginatedList templateList
	) {
		super(title);
		this.callback = callback;
		this.worldType = worldType;
		if (templateList == null) {
			this.templateList = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList();
			this.setPagination(new WorldTemplatePaginatedList(10));
		} else {
			this.templateList = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(Lists.<WorldTemplate>newArrayList(templateList.templates));
			this.setPagination(templateList);
		}
	}

	public void setWarning(Text... warning) {
		this.warning = warning;
	}

	@Override
	public void init() {
		this.layout.addHeader(this.title, this.textRenderer);
		this.templateList = this.layout.addBody(new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(this.templateList.getValues()));
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(10));
		directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
		this.trailerButton = directionalLayoutWidget.add(ButtonWidget.builder(TRAILER_TEXT, button -> this.onTrailer()).width(100).build());
		this.selectButton = directionalLayoutWidget.add(ButtonWidget.builder(SELECT_TEXT, button -> this.selectTemplate()).width(100).build());
		directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).width(100).build());
		this.publisherButton = directionalLayoutWidget.add(ButtonWidget.builder(PUBLISHER_TEXT, button -> this.onPublish()).width(100).build());
		this.updateButtonStates();
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		this.templateList.setDimensions(this.width, this.height - this.layout.getFooterHeight() - this.getTemplateListTop());
		this.layout.refreshPositions();
	}

	@Override
	public Text getNarratedTitle() {
		List<Text> list = Lists.<Text>newArrayListWithCapacity(2);
		list.add(this.title);
		if (this.warning != null) {
			list.addAll(Arrays.asList(this.warning));
		}

		return ScreenTexts.joinLines(list);
	}

	void updateButtonStates() {
		this.publisherButton.visible = this.selectedTemplate != null && !this.selectedTemplate.link.isEmpty();
		this.trailerButton.visible = this.selectedTemplate != null && !this.selectedTemplate.trailer.isEmpty();
		this.selectButton.active = this.selectedTemplate != null;
	}

	@Override
	public void close() {
		this.callback.accept(null);
	}

	private void selectTemplate() {
		if (this.selectedTemplate != null) {
			this.callback.accept(this.selectedTemplate);
		}
	}

	private void onTrailer() {
		if (this.selectedTemplate != null && !this.selectedTemplate.trailer.isBlank()) {
			ConfirmLinkScreen.open(this, this.selectedTemplate.trailer);
		}
	}

	private void onPublish() {
		if (this.selectedTemplate != null && !this.selectedTemplate.link.isBlank()) {
			ConfirmLinkScreen.open(this, this.selectedTemplate.link);
		}
	}

	private void setPagination(WorldTemplatePaginatedList templateList) {
		(new Thread("realms-template-fetcher") {
				public void run() {
					WorldTemplatePaginatedList worldTemplatePaginatedList = templateList;
					RealmsClient realmsClient = RealmsClient.create();
	
					while (worldTemplatePaginatedList != null) {
						Either<WorldTemplatePaginatedList, Exception> either = RealmsSelectWorldTemplateScreen.this.fetchWorldTemplates(worldTemplatePaginatedList, realmsClient);
						worldTemplatePaginatedList = (WorldTemplatePaginatedList)RealmsSelectWorldTemplateScreen.this.client
							.submit(
								() -> {
									if (either.right().isPresent()) {
										RealmsSelectWorldTemplateScreen.LOGGER.error("Couldn't fetch templates", (Throwable)either.right().get());
										if (RealmsSelectWorldTemplateScreen.this.templateList.isEmpty()) {
											RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(I18n.translate("mco.template.select.failure"));
										}
			
										return null;
									} else {
										WorldTemplatePaginatedList worldTemplatePaginatedListx = (WorldTemplatePaginatedList)either.left().get();
			
										for (WorldTemplate worldTemplate : worldTemplatePaginatedListx.templates) {
											RealmsSelectWorldTemplateScreen.this.templateList.addEntry(worldTemplate);
										}
			
										if (worldTemplatePaginatedListx.templates.isEmpty()) {
											if (RealmsSelectWorldTemplateScreen.this.templateList.isEmpty()) {
												String string = I18n.translate("mco.template.select.none", "%link");
												TextRenderingUtils.LineSegment lineSegment = TextRenderingUtils.LineSegment.link(
													I18n.translate("mco.template.select.none.linkTitle"), "https://aka.ms/MinecraftRealmsContentCreator"
												);
												RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(string, lineSegment);
											}
			
											return null;
										} else {
											return worldTemplatePaginatedListx;
										}
									}
								}
							)
							.join();
					}
				}
			})
			.start();
	}

	Either<WorldTemplatePaginatedList, Exception> fetchWorldTemplates(WorldTemplatePaginatedList templateList, RealmsClient realms) {
		try {
			return Either.left(realms.fetchWorldTemplates(templateList.page + 1, templateList.size, this.worldType));
		} catch (RealmsServiceException var4) {
			return Either.right(var4);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.currentLink = null;
		if (this.noTemplatesMessage != null) {
			this.renderMessages(context, mouseX, mouseY, this.noTemplatesMessage);
		}

		if (this.warning != null) {
			for (int i = 0; i < this.warning.length; i++) {
				Text text = this.warning[i];
				context.drawCenteredTextWithShadow(this.textRenderer, text, this.width / 2, row(-1 + i), Colors.LIGHT_GRAY);
			}
		}
	}

	private void renderMessages(DrawContext context, int x, int y, List<TextRenderingUtils.Line> messages) {
		for (int i = 0; i < messages.size(); i++) {
			TextRenderingUtils.Line line = (TextRenderingUtils.Line)messages.get(i);
			int j = row(4 + i);
			int k = line.segments.stream().mapToInt(segment -> this.textRenderer.getWidth(segment.renderedText())).sum();
			int l = this.width / 2 - k / 2;

			for (TextRenderingUtils.LineSegment lineSegment : line.segments) {
				int m = lineSegment.isLink() ? 3368635 : Colors.WHITE;
				int n = context.drawTextWithShadow(this.textRenderer, lineSegment.renderedText(), l, j, m);
				if (lineSegment.isLink() && x > l && x < n && y > j - 3 && y < j + 8) {
					this.setTooltip(Text.literal(lineSegment.getLinkUrl()));
					this.currentLink = lineSegment.getLinkUrl();
				}

				l = n;
			}
		}
	}

	int getTemplateListTop() {
		return this.warning != null ? row(1) : 33;
	}

	@Environment(EnvType.CLIENT)
	class WorldTemplateObjectSelectionList extends RealmsObjectSelectionList<RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry> {
		public WorldTemplateObjectSelectionList() {
			this(Collections.emptyList());
		}

		public WorldTemplateObjectSelectionList(final Iterable<WorldTemplate> templates) {
			super(
				RealmsSelectWorldTemplateScreen.this.width,
				RealmsSelectWorldTemplateScreen.this.height - 33 - RealmsSelectWorldTemplateScreen.this.getTemplateListTop(),
				RealmsSelectWorldTemplateScreen.this.getTemplateListTop(),
				46
			);
			templates.forEach(this::addEntry);
		}

		public void addEntry(WorldTemplate template) {
			this.addEntry(RealmsSelectWorldTemplateScreen.this.new WorldTemplateObjectSelectionListEntry(template));
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
				ConfirmLinkScreen.open(RealmsSelectWorldTemplateScreen.this, RealmsSelectWorldTemplateScreen.this.currentLink);
				return true;
			} else {
				return super.mouseClicked(mouseX, mouseY, button);
			}
		}

		public void setSelected(@Nullable RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry worldTemplateObjectSelectionListEntry) {
			super.setSelected(worldTemplateObjectSelectionListEntry);
			RealmsSelectWorldTemplateScreen.this.selectedTemplate = worldTemplateObjectSelectionListEntry == null
				? null
				: worldTemplateObjectSelectionListEntry.mTemplate;
			RealmsSelectWorldTemplateScreen.this.updateButtonStates();
		}

		@Override
		public int getMaxPosition() {
			return this.getEntryCount() * 46;
		}

		@Override
		public int getRowWidth() {
			return 300;
		}

		public boolean isEmpty() {
			return this.getEntryCount() == 0;
		}

		public List<WorldTemplate> getValues() {
			return (List<WorldTemplate>)this.children().stream().map(child -> child.mTemplate).collect(Collectors.toList());
		}
	}

	@Environment(EnvType.CLIENT)
	class WorldTemplateObjectSelectionListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry> {
		private static final ButtonTextures LINK_TEXTURES = new ButtonTextures(new Identifier("icon/link"), new Identifier("icon/link_highlighted"));
		private static final ButtonTextures VIDEO_LINK_TEXTURES = new ButtonTextures(new Identifier("icon/video_link"), new Identifier("icon/video_link_highlighted"));
		private static final Text INFO_TOOLTIP_TEXT = Text.translatable("mco.template.info.tooltip");
		private static final Text TRAILER_TOOLTIP_TEXT = Text.translatable("mco.template.trailer.tooltip");
		public final WorldTemplate mTemplate;
		private long prevClickTime;
		@Nullable
		private TexturedButtonWidget infoButton;
		@Nullable
		private TexturedButtonWidget trailerButton;

		public WorldTemplateObjectSelectionListEntry(final WorldTemplate template) {
			this.mTemplate = template;
			if (!template.link.isBlank()) {
				this.infoButton = new TexturedButtonWidget(
					15, 15, LINK_TEXTURES, ConfirmLinkScreen.opening(RealmsSelectWorldTemplateScreen.this, template.link), INFO_TOOLTIP_TEXT
				);
				this.infoButton.setTooltip(Tooltip.of(INFO_TOOLTIP_TEXT));
			}

			if (!template.trailer.isBlank()) {
				this.trailerButton = new TexturedButtonWidget(
					15, 15, VIDEO_LINK_TEXTURES, ConfirmLinkScreen.opening(RealmsSelectWorldTemplateScreen.this, template.trailer), TRAILER_TOOLTIP_TEXT
				);
				this.trailerButton.setTooltip(Tooltip.of(TRAILER_TOOLTIP_TEXT));
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			RealmsSelectWorldTemplateScreen.this.selectedTemplate = this.mTemplate;
			RealmsSelectWorldTemplateScreen.this.updateButtonStates();
			if (Util.getMeasuringTimeMs() - this.prevClickTime < 250L && this.isFocused()) {
				RealmsSelectWorldTemplateScreen.this.callback.accept(this.mTemplate);
			}

			this.prevClickTime = Util.getMeasuringTimeMs();
			if (this.infoButton != null) {
				this.infoButton.mouseClicked(mouseX, mouseY, button);
			}

			if (this.trailerButton != null) {
				this.trailerButton.mouseClicked(mouseX, mouseY, button);
			}

			return super.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			context.drawTexture(RealmsTextureManager.getTextureId(this.mTemplate.id, this.mTemplate.image), x + 1, y + 1 + 1, 0.0F, 0.0F, 38, 38, 38, 38);
			context.drawGuiTexture(RealmsSelectWorldTemplateScreen.SLOT_FRAME_TEXTURE, x, y + 1, 40, 40);
			int i = 5;
			int j = RealmsSelectWorldTemplateScreen.this.textRenderer.getWidth(this.mTemplate.version);
			if (this.infoButton != null) {
				this.infoButton.setPosition(x + entryWidth - j - this.infoButton.getWidth() - 10, y);
				this.infoButton.render(context, mouseX, mouseY, tickDelta);
			}

			if (this.trailerButton != null) {
				this.trailerButton.setPosition(x + entryWidth - j - this.trailerButton.getWidth() * 2 - 15, y);
				this.trailerButton.render(context, mouseX, mouseY, tickDelta);
			}

			int k = x + 45 + 20;
			int l = y + 5;
			context.drawText(RealmsSelectWorldTemplateScreen.this.textRenderer, this.mTemplate.name, k, l, Colors.WHITE, false);
			context.drawText(RealmsSelectWorldTemplateScreen.this.textRenderer, this.mTemplate.version, x + entryWidth - j - 5, l, 7105644, false);
			context.drawText(RealmsSelectWorldTemplateScreen.this.textRenderer, this.mTemplate.author, k, l + 9 + 5, Colors.LIGHT_GRAY, false);
			if (!this.mTemplate.recommendedPlayers.isBlank()) {
				context.drawText(RealmsSelectWorldTemplateScreen.this.textRenderer, this.mTemplate.recommendedPlayers, k, y + entryHeight - 9 / 2 - 5, 5000268, false);
			}
		}

		@Override
		public Text getNarration() {
			Text text = ScreenTexts.joinLines(
				Text.literal(this.mTemplate.name),
				Text.translatable("mco.template.select.narrate.authors", this.mTemplate.author),
				Text.literal(this.mTemplate.recommendedPlayers),
				Text.translatable("mco.template.select.narrate.version", this.mTemplate.version)
			);
			return Text.translatable("narrator.select", text);
		}
	}
}
