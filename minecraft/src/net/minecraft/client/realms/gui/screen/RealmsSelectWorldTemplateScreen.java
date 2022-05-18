package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.realms.util.TextRenderingUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsSelectWorldTemplateScreen extends RealmsScreen {
	static final Logger LOGGER = LogUtils.getLogger();
	static final Identifier LINK_ICONS = new Identifier("realms", "textures/gui/realms/link_icons.png");
	static final Identifier TRAILER_ICONS = new Identifier("realms", "textures/gui/realms/trailer_icons.png");
	static final Identifier SLOT_FRAME = new Identifier("realms", "textures/gui/realms/slot_frame.png");
	static final Text INFO_TOOLTIP = Text.translatable("mco.template.info.tooltip");
	static final Text TRAILER_TOOLTIP = Text.translatable("mco.template.trailer.tooltip");
	private final Consumer<WorldTemplate> callback;
	RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList templateList;
	int selectedTemplate = -1;
	private ButtonWidget selectButton;
	private ButtonWidget trailerButton;
	private ButtonWidget publisherButton;
	@Nullable
	Text tooltip;
	@Nullable
	String currentLink;
	private final RealmsServer.WorldType worldType;
	int clicks;
	@Nullable
	private Text[] warning;
	private String warningURL;
	boolean displayWarning;
	private boolean hoverWarning;
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
		this.displayWarning = true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.hoverWarning && this.warningURL != null) {
			Util.getOperatingSystem().open("https://www.minecraft.net/realms/adventure-maps-in-1-9");
			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.templateList = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(this.templateList.getValues());
		this.trailerButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 206, this.height - 32, 100, 20, Text.translatable("mco.template.button.trailer"), button -> this.onTrailer())
		);
		this.selectButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 100, this.height - 32, 100, 20, Text.translatable("mco.template.button.select"), button -> this.selectTemplate())
		);
		Text text = this.worldType == RealmsServer.WorldType.MINIGAME ? ScreenTexts.CANCEL : ScreenTexts.BACK;
		ButtonWidget buttonWidget = new ButtonWidget(this.width / 2 + 6, this.height - 32, 100, 20, text, button -> this.close());
		this.addDrawableChild(buttonWidget);
		this.publisherButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 + 112, this.height - 32, 100, 20, Text.translatable("mco.template.button.publisher"), button -> this.onPublish())
		);
		this.selectButton.active = false;
		this.trailerButton.visible = false;
		this.publisherButton.visible = false;
		this.addSelectableChild(this.templateList);
		this.focusOn(this.templateList);
	}

	@Override
	public Text getNarratedTitle() {
		List<Text> list = Lists.<Text>newArrayListWithCapacity(2);
		if (this.title != null) {
			list.add(this.title);
		}

		if (this.warning != null) {
			list.addAll(Arrays.asList(this.warning));
		}

		return ScreenTexts.joinLines(list);
	}

	void updateButtonStates() {
		this.publisherButton.visible = this.shouldPublisherBeVisible();
		this.trailerButton.visible = this.shouldTrailerBeVisible();
		this.selectButton.active = this.shouldSelectButtonBeActive();
	}

	private boolean shouldSelectButtonBeActive() {
		return this.selectedTemplate != -1;
	}

	private boolean shouldPublisherBeVisible() {
		return this.selectedTemplate != -1 && !this.getSelectedTemplate().link.isEmpty();
	}

	private WorldTemplate getSelectedTemplate() {
		return this.templateList.getItem(this.selectedTemplate);
	}

	private boolean shouldTrailerBeVisible() {
		return this.selectedTemplate != -1 && !this.getSelectedTemplate().trailer.isEmpty();
	}

	@Override
	public void tick() {
		super.tick();
		this.clicks--;
		if (this.clicks < 0) {
			this.clicks = 0;
		}
	}

	@Override
	public void close() {
		this.callback.accept(null);
	}

	void selectTemplate() {
		if (this.isSelectionValid()) {
			this.callback.accept(this.getSelectedTemplate());
		}
	}

	private boolean isSelectionValid() {
		return this.selectedTemplate >= 0 && this.selectedTemplate < this.templateList.getEntryCount();
	}

	private void onTrailer() {
		if (this.isSelectionValid()) {
			WorldTemplate worldTemplate = this.getSelectedTemplate();
			if (!"".equals(worldTemplate.trailer)) {
				Util.getOperatingSystem().open(worldTemplate.trailer);
			}
		}
	}

	private void onPublish() {
		if (this.isSelectionValid()) {
			WorldTemplate worldTemplate = this.getSelectedTemplate();
			if (!"".equals(worldTemplate.link)) {
				Util.getOperatingSystem().open(worldTemplate.link);
			}
		}
	}

	private void setPagination(WorldTemplatePaginatedList templateList) {
		(new Thread("realms-template-fetcher") {
				public void run() {
					WorldTemplatePaginatedList worldTemplatePaginatedList = templateList;
					RealmsClient realmsClient = RealmsClient.createRealmsClient();

					while (worldTemplatePaginatedList != null) {
						Either<WorldTemplatePaginatedList, String> either = RealmsSelectWorldTemplateScreen.this.fetchWorldTemplates(worldTemplatePaginatedList, realmsClient);
						worldTemplatePaginatedList = (WorldTemplatePaginatedList)RealmsSelectWorldTemplateScreen.this.client
							.submit(
								() -> {
									if (either.right().isPresent()) {
										RealmsSelectWorldTemplateScreen.LOGGER.error("Couldn't fetch templates: {}", either.right().get());
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

	Either<WorldTemplatePaginatedList, String> fetchWorldTemplates(WorldTemplatePaginatedList templateList, RealmsClient realms) {
		try {
			return Either.left(realms.fetchWorldTemplates(templateList.page + 1, templateList.size, this.worldType));
		} catch (RealmsServiceException var4) {
			return Either.right(var4.getMessage());
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.tooltip = null;
		this.currentLink = null;
		this.hoverWarning = false;
		this.renderBackground(matrices);
		this.templateList.render(matrices, mouseX, mouseY, delta);
		if (this.noTemplatesMessage != null) {
			this.renderMessages(matrices, mouseX, mouseY, this.noTemplatesMessage);
		}

		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);
		if (this.displayWarning) {
			Text[] texts = this.warning;

			for (int i = 0; i < texts.length; i++) {
				int j = this.textRenderer.getWidth(texts[i]);
				int k = this.width / 2 - j / 2;
				int l = row(-1 + i);
				if (mouseX >= k && mouseX <= k + j && mouseY >= l && mouseY <= l + 9) {
					this.hoverWarning = true;
				}
			}

			for (int ix = 0; ix < texts.length; ix++) {
				Text text = texts[ix];
				int k = 10526880;
				if (this.warningURL != null) {
					if (this.hoverWarning) {
						k = 7107012;
						text = text.copy().formatted(Formatting.STRIKETHROUGH);
					} else {
						k = 3368635;
					}
				}

				drawCenteredText(matrices, this.textRenderer, text, this.width / 2, row(-1 + ix), k);
			}
		}

		super.render(matrices, mouseX, mouseY, delta);
		this.renderMousehoverTooltip(matrices, this.tooltip, mouseX, mouseY);
	}

	private void renderMessages(MatrixStack matrices, int x, int y, List<TextRenderingUtils.Line> messages) {
		for (int i = 0; i < messages.size(); i++) {
			TextRenderingUtils.Line line = (TextRenderingUtils.Line)messages.get(i);
			int j = row(4 + i);
			int k = line.segments.stream().mapToInt(segment -> this.textRenderer.getWidth(segment.renderedText())).sum();
			int l = this.width / 2 - k / 2;

			for (TextRenderingUtils.LineSegment lineSegment : line.segments) {
				int m = lineSegment.isLink() ? 3368635 : 16777215;
				int n = this.textRenderer.drawWithShadow(matrices, lineSegment.renderedText(), (float)l, (float)j, m);
				if (lineSegment.isLink() && x > l && x < n && y > j - 3 && y < j + 8) {
					this.tooltip = Text.literal(lineSegment.getLinkUrl());
					this.currentLink = lineSegment.getLinkUrl();
				}

				l = n;
			}
		}
	}

	protected void renderMousehoverTooltip(MatrixStack matrices, @Nullable Text tooltip, int mouseX, int mouseY) {
		if (tooltip != null) {
			int i = mouseX + 12;
			int j = mouseY - 12;
			int k = this.textRenderer.getWidth(tooltip);
			this.fillGradient(matrices, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
			this.textRenderer.drawWithShadow(matrices, tooltip, (float)i, (float)j, 16777215);
		}
	}

	@Environment(EnvType.CLIENT)
	class WorldTemplateObjectSelectionList extends RealmsObjectSelectionList<RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry> {
		public WorldTemplateObjectSelectionList() {
			this(Collections.emptyList());
		}

		public WorldTemplateObjectSelectionList(Iterable<WorldTemplate> templates) {
			super(
				RealmsSelectWorldTemplateScreen.this.width,
				RealmsSelectWorldTemplateScreen.this.height,
				RealmsSelectWorldTemplateScreen.this.displayWarning ? RealmsSelectWorldTemplateScreen.row(1) : 32,
				RealmsSelectWorldTemplateScreen.this.height - 40,
				46
			);
			templates.forEach(this::addEntry);
		}

		public void addEntry(WorldTemplate template) {
			this.addEntry(RealmsSelectWorldTemplateScreen.this.new WorldTemplateObjectSelectionListEntry(template));
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 0 && mouseY >= (double)this.top && mouseY <= (double)this.bottom) {
				int i = this.width / 2 - 150;
				if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
					Util.getOperatingSystem().open(RealmsSelectWorldTemplateScreen.this.currentLink);
				}

				int j = (int)Math.floor(mouseY - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
				int k = j / this.itemHeight;
				if (mouseX >= (double)i && mouseX < (double)this.getScrollbarPositionX() && k >= 0 && j >= 0 && k < this.getEntryCount()) {
					this.setSelected(k);
					this.itemClicked(j, k, mouseX, mouseY, this.width);
					if (k >= RealmsSelectWorldTemplateScreen.this.templateList.getEntryCount()) {
						return super.mouseClicked(mouseX, mouseY, button);
					}

					RealmsSelectWorldTemplateScreen.this.clicks += 7;
					if (RealmsSelectWorldTemplateScreen.this.clicks >= 10) {
						RealmsSelectWorldTemplateScreen.this.selectTemplate();
					}

					return true;
				}
			}

			return super.mouseClicked(mouseX, mouseY, button);
		}

		public void setSelected(@Nullable RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry worldTemplateObjectSelectionListEntry) {
			super.setSelected(worldTemplateObjectSelectionListEntry);
			RealmsSelectWorldTemplateScreen.this.selectedTemplate = this.children().indexOf(worldTemplateObjectSelectionListEntry);
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

		@Override
		public void renderBackground(MatrixStack matrices) {
			RealmsSelectWorldTemplateScreen.this.renderBackground(matrices);
		}

		@Override
		public boolean isFocused() {
			return RealmsSelectWorldTemplateScreen.this.getFocused() == this;
		}

		public boolean isEmpty() {
			return this.getEntryCount() == 0;
		}

		public WorldTemplate getItem(int index) {
			return ((RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry)this.children().get(index)).mTemplate;
		}

		public List<WorldTemplate> getValues() {
			return (List<WorldTemplate>)this.children().stream().map(child -> child.mTemplate).collect(Collectors.toList());
		}
	}

	@Environment(EnvType.CLIENT)
	class WorldTemplateObjectSelectionListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry> {
		final WorldTemplate mTemplate;

		public WorldTemplateObjectSelectionListEntry(WorldTemplate template) {
			this.mTemplate = template;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.renderWorldTemplateItem(matrices, this.mTemplate, x, y, mouseX, mouseY);
		}

		private void renderWorldTemplateItem(MatrixStack matrices, WorldTemplate template, int x, int y, int mouseX, int mouseY) {
			int i = x + 45 + 20;
			RealmsSelectWorldTemplateScreen.this.textRenderer.draw(matrices, template.name, (float)i, (float)(y + 2), 16777215);
			RealmsSelectWorldTemplateScreen.this.textRenderer.draw(matrices, template.author, (float)i, (float)(y + 15), 7105644);
			RealmsSelectWorldTemplateScreen.this.textRenderer
				.draw(matrices, template.version, (float)(i + 227 - RealmsSelectWorldTemplateScreen.this.textRenderer.getWidth(template.version)), (float)(y + 1), 7105644);
			if (!"".equals(template.link) || !"".equals(template.trailer) || !"".equals(template.recommendedPlayers)) {
				this.drawIcons(matrices, i - 1, y + 25, mouseX, mouseY, template.link, template.trailer, template.recommendedPlayers);
			}

			this.drawImage(matrices, x, y + 1, mouseX, mouseY, template);
		}

		private void drawImage(MatrixStack matrices, int x, int y, int mouseX, int mouseY, WorldTemplate template) {
			RealmsTextureManager.bindWorldTemplate(template.id, template.image);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(matrices, x + 1, y + 1, 0.0F, 0.0F, 38, 38, 38, 38);
			RenderSystem.setShaderTexture(0, RealmsSelectWorldTemplateScreen.SLOT_FRAME);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 40, 40, 40, 40);
		}

		private void drawIcons(MatrixStack matrices, int x, int y, int mouseX, int mouseY, String link, String trailer, String recommendedPlayers) {
			if (!"".equals(recommendedPlayers)) {
				RealmsSelectWorldTemplateScreen.this.textRenderer.draw(matrices, recommendedPlayers, (float)x, (float)(y + 4), 5000268);
			}

			int i = "".equals(recommendedPlayers) ? 0 : RealmsSelectWorldTemplateScreen.this.textRenderer.getWidth(recommendedPlayers) + 2;
			boolean bl = false;
			boolean bl2 = false;
			boolean bl3 = "".equals(link);
			if (mouseX >= x + i && mouseX <= x + i + 32 && mouseY >= y && mouseY <= y + 15 && mouseY < RealmsSelectWorldTemplateScreen.this.height - 15 && mouseY > 32) {
				if (mouseX <= x + 15 + i && mouseX > i) {
					if (bl3) {
						bl2 = true;
					} else {
						bl = true;
					}
				} else if (!bl3) {
					bl2 = true;
				}
			}

			if (!bl3) {
				RenderSystem.setShaderTexture(0, RealmsSelectWorldTemplateScreen.LINK_ICONS);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				float f = bl ? 15.0F : 0.0F;
				DrawableHelper.drawTexture(matrices, x + i, y, f, 0.0F, 15, 15, 30, 15);
			}

			if (!"".equals(trailer)) {
				RenderSystem.setShaderTexture(0, RealmsSelectWorldTemplateScreen.TRAILER_ICONS);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				int j = x + i + (bl3 ? 0 : 17);
				float g = bl2 ? 15.0F : 0.0F;
				DrawableHelper.drawTexture(matrices, j, y, g, 0.0F, 15, 15, 30, 15);
			}

			if (bl) {
				RealmsSelectWorldTemplateScreen.this.tooltip = RealmsSelectWorldTemplateScreen.INFO_TOOLTIP;
				RealmsSelectWorldTemplateScreen.this.currentLink = link;
			} else if (bl2 && !"".equals(trailer)) {
				RealmsSelectWorldTemplateScreen.this.tooltip = RealmsSelectWorldTemplateScreen.TRAILER_TOOLTIP;
				RealmsSelectWorldTemplateScreen.this.currentLink = trailer;
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
