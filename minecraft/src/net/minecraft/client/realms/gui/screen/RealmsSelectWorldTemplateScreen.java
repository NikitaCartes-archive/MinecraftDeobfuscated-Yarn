package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsSelectWorldTemplateScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier LINK_ICONS = new Identifier("realms", "textures/gui/realms/link_icons.png");
	private static final Identifier TRAILER_ICONS = new Identifier("realms", "textures/gui/realms/trailer_icons.png");
	private static final Identifier SLOT_FRAME = new Identifier("realms", "textures/gui/realms/slot_frame.png");
	private static final Text INFO_TOOLTIP = new TranslatableText("mco.template.info.tooltip");
	private static final Text TRAILER_TOOLTIP = new TranslatableText("mco.template.trailer.tooltip");
	private final Consumer<WorldTemplate> callback;
	private RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList templateList;
	private int selectedTemplate = -1;
	private Text title;
	private ButtonWidget selectButton;
	private ButtonWidget trailerButton;
	private ButtonWidget publisherButton;
	@Nullable
	private Text toolTip;
	private String currentLink;
	private final RealmsServer.WorldType worldType;
	private int clicks;
	@Nullable
	private Text[] warning;
	private String warningURL;
	private boolean displayWarning;
	private boolean hoverWarning;
	@Nullable
	private List<TextRenderingUtils.Line> noTemplatesMessage;

	public RealmsSelectWorldTemplateScreen(Consumer<WorldTemplate> callback, RealmsServer.WorldType worldType) {
		this(callback, worldType, null);
	}

	public RealmsSelectWorldTemplateScreen(Consumer<WorldTemplate> callback, RealmsServer.WorldType worldType, @Nullable WorldTemplatePaginatedList list) {
		this.callback = callback;
		this.worldType = worldType;
		if (list == null) {
			this.templateList = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList();
			this.setPagination(new WorldTemplatePaginatedList(10));
		} else {
			this.templateList = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(Lists.<WorldTemplate>newArrayList(list.templates));
			this.setPagination(list);
		}

		this.title = new TranslatableText("mco.template.title");
	}

	public void setTitle(Text title) {
		this.title = title;
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
		this.trailerButton = this.addButton(
			new ButtonWidget(this.width / 2 - 206, this.height - 32, 100, 20, new TranslatableText("mco.template.button.trailer"), buttonWidgetx -> this.onTrailer())
		);
		this.selectButton = this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height - 32, 100, 20, new TranslatableText("mco.template.button.select"), buttonWidgetx -> this.selectTemplate())
		);
		Text text = this.worldType == RealmsServer.WorldType.MINIGAME ? ScreenTexts.CANCEL : ScreenTexts.BACK;
		ButtonWidget buttonWidget = new ButtonWidget(this.width / 2 + 6, this.height - 32, 100, 20, text, buttonWidgetx -> this.onClose());
		this.addButton(buttonWidget);
		this.publisherButton = this.addButton(
			new ButtonWidget(this.width / 2 + 112, this.height - 32, 100, 20, new TranslatableText("mco.template.button.publisher"), buttonWidgetx -> this.onPublish())
		);
		this.selectButton.active = false;
		this.trailerButton.visible = false;
		this.publisherButton.visible = false;
		this.addChild(this.templateList);
		this.focusOn(this.templateList);
		Stream<Text> stream = Stream.of(this.title);
		if (this.warning != null) {
			stream = Stream.concat(Stream.of(this.warning), stream);
		}

		Realms.narrateNow((Iterable<String>)stream.filter(Objects::nonNull).map(Text::getString).collect(Collectors.toList()));
	}

	private void updateButtonStates() {
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
	public void onClose() {
		this.callback.accept(null);
	}

	private void selectTemplate() {
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

	private void setPagination(WorldTemplatePaginatedList worldTemplatePaginatedList) {
		(new Thread("realms-template-fetcher") {
				public void run() {
					WorldTemplatePaginatedList worldTemplatePaginatedList = worldTemplatePaginatedList;
					RealmsClient realmsClient = RealmsClient.createRealmsClient();

					while (worldTemplatePaginatedList != null) {
						Either<WorldTemplatePaginatedList, String> either = RealmsSelectWorldTemplateScreen.this.method_21416(worldTemplatePaginatedList, realmsClient);
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
										WorldTemplatePaginatedList worldTemplatePaginatedListxxx = (WorldTemplatePaginatedList)either.left().get();

										for (WorldTemplate worldTemplate : worldTemplatePaginatedListxxx.templates) {
											RealmsSelectWorldTemplateScreen.this.templateList.addEntry(worldTemplate);
										}

										if (worldTemplatePaginatedListxxx.templates.isEmpty()) {
											if (RealmsSelectWorldTemplateScreen.this.templateList.isEmpty()) {
												String string = I18n.translate("mco.template.select.none", "%link");
												TextRenderingUtils.LineSegment lineSegment = TextRenderingUtils.LineSegment.link(
													I18n.translate("mco.template.select.none.linkTitle"), "https://aka.ms/MinecraftRealmsContentCreator"
												);
												RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(string, lineSegment);
											}

											return null;
										} else {
											return worldTemplatePaginatedListxxx;
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

	private Either<WorldTemplatePaginatedList, String> method_21416(WorldTemplatePaginatedList worldTemplatePaginatedList, RealmsClient realmsClient) {
		try {
			return Either.left(realmsClient.fetchWorldTemplates(worldTemplatePaginatedList.page + 1, worldTemplatePaginatedList.size, this.worldType));
		} catch (RealmsServiceException var4) {
			return Either.right(var4.getMessage());
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.toolTip = null;
		this.currentLink = null;
		this.hoverWarning = false;
		this.renderBackground(matrices);
		this.templateList.render(matrices, mouseX, mouseY, delta);
		if (this.noTemplatesMessage != null) {
			this.method_21414(matrices, mouseX, mouseY, this.noTemplatesMessage);
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
						text = text.shallowCopy().formatted(Formatting.STRIKETHROUGH);
					} else {
						k = 3368635;
					}
				}

				drawCenteredText(matrices, this.textRenderer, text, this.width / 2, row(-1 + ix), k);
			}
		}

		super.render(matrices, mouseX, mouseY, delta);
		this.renderMousehoverTooltip(matrices, this.toolTip, mouseX, mouseY);
	}

	private void method_21414(MatrixStack matrixStack, int i, int j, List<TextRenderingUtils.Line> list) {
		for (int k = 0; k < list.size(); k++) {
			TextRenderingUtils.Line line = (TextRenderingUtils.Line)list.get(k);
			int l = row(4 + k);
			int m = line.segments.stream().mapToInt(lineSegmentx -> this.textRenderer.getWidth(lineSegmentx.renderedText())).sum();
			int n = this.width / 2 - m / 2;

			for (TextRenderingUtils.LineSegment lineSegment : line.segments) {
				int o = lineSegment.isLink() ? 3368635 : 16777215;
				int p = this.textRenderer.drawWithShadow(matrixStack, lineSegment.renderedText(), (float)n, (float)l, o);
				if (lineSegment.isLink() && i > n && i < p && j > l - 3 && j < l + 8) {
					this.toolTip = new LiteralText(lineSegment.getLinkUrl());
					this.currentLink = lineSegment.getLinkUrl();
				}

				n = p;
			}
		}
	}

	protected void renderMousehoverTooltip(MatrixStack matrices, @Nullable Text text, int i, int j) {
		if (text != null) {
			int k = i + 12;
			int l = j - 12;
			int m = this.textRenderer.getWidth(text);
			this.fillGradient(matrices, k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
			this.textRenderer.drawWithShadow(matrices, text, (float)k, (float)l, 16777215);
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

					RealmsSelectWorldTemplateScreen.this.clicks = RealmsSelectWorldTemplateScreen.this.clicks + 7;
					if (RealmsSelectWorldTemplateScreen.this.clicks >= 10) {
						RealmsSelectWorldTemplateScreen.this.selectTemplate();
					}

					return true;
				}
			}

			return super.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public void setSelected(int index) {
			this.setSelectedItem(index);
			if (index != -1) {
				WorldTemplate worldTemplate = RealmsSelectWorldTemplateScreen.this.templateList.getItem(index);
				String string = I18n.translate("narrator.select.list.position", index + 1, RealmsSelectWorldTemplateScreen.this.templateList.getEntryCount());
				String string2 = I18n.translate("mco.template.select.narrate.version", worldTemplate.version);
				String string3 = I18n.translate("mco.template.select.narrate.authors", worldTemplate.author);
				String string4 = Realms.joinNarrations(Arrays.asList(worldTemplate.name, string3, worldTemplate.recommendedPlayers, string2, string));
				Realms.narrateNow(I18n.translate("narrator.select", string4));
			}
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
			return (List<WorldTemplate>)this.children()
				.stream()
				.map(worldTemplateObjectSelectionListEntry -> worldTemplateObjectSelectionListEntry.mTemplate)
				.collect(Collectors.toList());
		}
	}

	@Environment(EnvType.CLIENT)
	class WorldTemplateObjectSelectionListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry> {
		private final WorldTemplate mTemplate;

		public WorldTemplateObjectSelectionListEntry(WorldTemplate template) {
			this.mTemplate = template;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.renderWorldTemplateItem(matrices, this.mTemplate, x, y, mouseX, mouseY);
		}

		private void renderWorldTemplateItem(MatrixStack matrices, WorldTemplate worldTemplate, int i, int j, int k, int l) {
			int m = i + 45 + 20;
			RealmsSelectWorldTemplateScreen.this.textRenderer.draw(matrices, worldTemplate.name, (float)m, (float)(j + 2), 16777215);
			RealmsSelectWorldTemplateScreen.this.textRenderer.draw(matrices, worldTemplate.author, (float)m, (float)(j + 15), 7105644);
			RealmsSelectWorldTemplateScreen.this.textRenderer
				.draw(
					matrices,
					worldTemplate.version,
					(float)(m + 227 - RealmsSelectWorldTemplateScreen.this.textRenderer.getWidth(worldTemplate.version)),
					(float)(j + 1),
					7105644
				);
			if (!"".equals(worldTemplate.link) || !"".equals(worldTemplate.trailer) || !"".equals(worldTemplate.recommendedPlayers)) {
				this.drawIcons(matrices, m - 1, j + 25, k, l, worldTemplate.link, worldTemplate.trailer, worldTemplate.recommendedPlayers);
			}

			this.drawImage(matrices, i, j + 1, k, l, worldTemplate);
		}

		private void drawImage(MatrixStack matrices, int y, int xm, int ym, int i, WorldTemplate worldTemplate) {
			RealmsTextureManager.bindWorldTemplate(worldTemplate.id, worldTemplate.image);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(matrices, y + 1, xm + 1, 0.0F, 0.0F, 38, 38, 38, 38);
			RealmsSelectWorldTemplateScreen.this.client.getTextureManager().bindTexture(RealmsSelectWorldTemplateScreen.SLOT_FRAME);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(matrices, y, xm, 0.0F, 0.0F, 40, 40, 40, 40);
		}

		private void drawIcons(MatrixStack matrices, int i, int j, int k, int l, String string, String string2, String string3) {
			if (!"".equals(string3)) {
				RealmsSelectWorldTemplateScreen.this.textRenderer.draw(matrices, string3, (float)i, (float)(j + 4), 5000268);
			}

			int m = "".equals(string3) ? 0 : RealmsSelectWorldTemplateScreen.this.textRenderer.getWidth(string3) + 2;
			boolean bl = false;
			boolean bl2 = false;
			boolean bl3 = "".equals(string);
			if (k >= i + m && k <= i + m + 32 && l >= j && l <= j + 15 && l < RealmsSelectWorldTemplateScreen.this.height - 15 && l > 32) {
				if (k <= i + 15 + m && k > m) {
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
				RealmsSelectWorldTemplateScreen.this.client.getTextureManager().bindTexture(RealmsSelectWorldTemplateScreen.LINK_ICONS);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.pushMatrix();
				RenderSystem.scalef(1.0F, 1.0F, 1.0F);
				float f = bl ? 15.0F : 0.0F;
				DrawableHelper.drawTexture(matrices, i + m, j, f, 0.0F, 15, 15, 30, 15);
				RenderSystem.popMatrix();
			}

			if (!"".equals(string2)) {
				RealmsSelectWorldTemplateScreen.this.client.getTextureManager().bindTexture(RealmsSelectWorldTemplateScreen.TRAILER_ICONS);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.pushMatrix();
				RenderSystem.scalef(1.0F, 1.0F, 1.0F);
				int n = i + m + (bl3 ? 0 : 17);
				float g = bl2 ? 15.0F : 0.0F;
				DrawableHelper.drawTexture(matrices, n, j, g, 0.0F, 15, 15, 30, 15);
				RenderSystem.popMatrix();
			}

			if (bl) {
				RealmsSelectWorldTemplateScreen.this.toolTip = RealmsSelectWorldTemplateScreen.INFO_TOOLTIP;
				RealmsSelectWorldTemplateScreen.this.currentLink = string;
			} else if (bl2 && !"".equals(string2)) {
				RealmsSelectWorldTemplateScreen.this.toolTip = RealmsSelectWorldTemplateScreen.TRAILER_TOOLTIP;
				RealmsSelectWorldTemplateScreen.this.currentLink = string2;
			}
		}
	}
}
