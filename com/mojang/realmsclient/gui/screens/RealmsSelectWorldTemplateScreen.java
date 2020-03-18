/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsScreenWithCallback;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.TextRenderingUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsSelectWorldTemplateScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier LINK_ICONS = new Identifier("realms", "textures/gui/realms/link_icons.png");
    private static final Identifier TRAILER_ICONS = new Identifier("realms", "textures/gui/realms/trailer_icons.png");
    private static final Identifier SLOT_FRAME = new Identifier("realms", "textures/gui/realms/slot_frame.png");
    private final RealmsScreenWithCallback lastScreen;
    private WorldTemplateObjectSelectionList templateList;
    private int selectedTemplate = -1;
    private String title;
    private ButtonWidget selectButton;
    private ButtonWidget trailerButton;
    private ButtonWidget publisherButton;
    private String toolTip;
    private String currentLink;
    private final RealmsServer.WorldType worldType;
    private int clicks;
    private String warning;
    private String warningURL;
    private boolean displayWarning;
    private boolean hoverWarning;
    private List<TextRenderingUtils.Line> noTemplatesMessage;

    public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback callback, RealmsServer.WorldType worldType) {
        this(callback, worldType, null);
    }

    public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback callback, RealmsServer.WorldType worldType, @Nullable WorldTemplatePaginatedList list) {
        this.lastScreen = callback;
        this.worldType = worldType;
        if (list == null) {
            this.templateList = new WorldTemplateObjectSelectionList();
            this.setPagination(new WorldTemplatePaginatedList(10));
        } else {
            this.templateList = new WorldTemplateObjectSelectionList(Lists.newArrayList(list.templates));
            this.setPagination(list);
        }
        this.title = I18n.translate("mco.template.title", new Object[0]);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWarning(String warning) {
        this.warning = warning;
        this.displayWarning = true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.hoverWarning && this.warningURL != null) {
            Util.getOperatingSystem().open("https://beta.minecraft.net/realms/adventure-maps-in-1-9");
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.templateList = new WorldTemplateObjectSelectionList(this.templateList.getValues());
        this.trailerButton = this.addButton(new ButtonWidget(this.width / 2 - 206, this.height - 32, 100, 20, I18n.translate("mco.template.button.trailer", new Object[0]), buttonWidget -> this.onTrailer()));
        this.selectButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 32, 100, 20, I18n.translate("mco.template.button.select", new Object[0]), buttonWidget -> this.selectTemplate()));
        String string = this.worldType == RealmsServer.WorldType.MINIGAME ? "gui.cancel" : "gui.back";
        ButtonWidget buttonWidget2 = new ButtonWidget(this.width / 2 + 6, this.height - 32, 100, 20, I18n.translate(string, new Object[0]), buttonWidget -> this.backButtonClicked());
        this.addButton(buttonWidget2);
        this.publisherButton = this.addButton(new ButtonWidget(this.width / 2 + 112, this.height - 32, 100, 20, I18n.translate("mco.template.button.publisher", new Object[0]), buttonWidget -> this.onPublish()));
        this.selectButton.active = false;
        this.trailerButton.visible = false;
        this.publisherButton.visible = false;
        this.addChild(this.templateList);
        this.focusOn(this.templateList);
        Realms.narrateNow(Stream.of(this.title, this.warning).filter(Objects::nonNull).collect(Collectors.toList()));
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
        return this.selectedTemplate != -1 && !this.method_21434().link.isEmpty();
    }

    private WorldTemplate method_21434() {
        return this.templateList.getItem(this.selectedTemplate);
    }

    private boolean shouldTrailerBeVisible() {
        return this.selectedTemplate != -1 && !this.method_21434().trailer.isEmpty();
    }

    @Override
    public void tick() {
        super.tick();
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void backButtonClicked() {
        this.lastScreen.callback(null);
        this.client.openScreen(this.lastScreen);
    }

    private void selectTemplate() {
        if (this.method_25247()) {
            this.lastScreen.callback(this.method_21434());
        }
    }

    private boolean method_25247() {
        return this.selectedTemplate >= 0 && this.selectedTemplate < this.templateList.getItemCount();
    }

    private void onTrailer() {
        if (this.method_25247()) {
            WorldTemplate worldTemplate = this.method_21434();
            if (!"".equals(worldTemplate.trailer)) {
                Util.getOperatingSystem().open(worldTemplate.trailer);
            }
        }
    }

    private void onPublish() {
        if (this.method_25247()) {
            WorldTemplate worldTemplate = this.method_21434();
            if (!"".equals(worldTemplate.link)) {
                Util.getOperatingSystem().open(worldTemplate.link);
            }
        }
    }

    private void setPagination(final WorldTemplatePaginatedList worldTemplatePaginatedList) {
        new Thread("realms-template-fetcher"){

            @Override
            public void run() {
                WorldTemplatePaginatedList worldTemplatePaginatedList2 = worldTemplatePaginatedList;
                RealmsClient realmsClient = RealmsClient.createRealmsClient();
                while (worldTemplatePaginatedList2 != null) {
                    Either either = RealmsSelectWorldTemplateScreen.this.method_21416(worldTemplatePaginatedList2, realmsClient);
                    worldTemplatePaginatedList2 = RealmsSelectWorldTemplateScreen.this.client.submit(() -> {
                        if (either.right().isPresent()) {
                            LOGGER.error("Couldn't fetch templates: {}", either.right().get());
                            if (RealmsSelectWorldTemplateScreen.this.templateList.isEmpty()) {
                                RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(I18n.translate("mco.template.select.failure", new Object[0]), new TextRenderingUtils.LineSegment[0]);
                            }
                            return null;
                        }
                        WorldTemplatePaginatedList worldTemplatePaginatedList2 = (WorldTemplatePaginatedList)either.left().get();
                        for (WorldTemplate worldTemplate : worldTemplatePaginatedList2.templates) {
                            RealmsSelectWorldTemplateScreen.this.templateList.addEntry(worldTemplate);
                        }
                        if (worldTemplatePaginatedList2.templates.isEmpty()) {
                            if (RealmsSelectWorldTemplateScreen.this.templateList.isEmpty()) {
                                String string = I18n.translate("mco.template.select.none", "%link");
                                TextRenderingUtils.LineSegment lineSegment = TextRenderingUtils.LineSegment.link(I18n.translate("mco.template.select.none.linkTitle", new Object[0]), "https://minecraft.net/realms/content-creator/");
                                RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(string, lineSegment);
                            }
                            return null;
                        }
                        return worldTemplatePaginatedList2;
                    }).join();
                }
            }
        }.start();
    }

    private Either<WorldTemplatePaginatedList, String> method_21416(WorldTemplatePaginatedList worldTemplatePaginatedList, RealmsClient realmsClient) {
        try {
            return Either.left(realmsClient.fetchWorldTemplates(worldTemplatePaginatedList.page + 1, worldTemplatePaginatedList.size, this.worldType));
        } catch (RealmsServiceException realmsServiceException) {
            return Either.right(realmsServiceException.getMessage());
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.toolTip = null;
        this.currentLink = null;
        this.hoverWarning = false;
        this.renderBackground();
        this.templateList.render(mouseX, mouseY, delta);
        if (this.noTemplatesMessage != null) {
            this.method_21414(mouseX, mouseY, this.noTemplatesMessage);
        }
        this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 13, 0xFFFFFF);
        if (this.displayWarning) {
            int k;
            int i;
            String[] strings = this.warning.split("\\\\n");
            for (i = 0; i < strings.length; ++i) {
                int j = this.textRenderer.getStringWidth(strings[i]);
                k = this.width / 2 - j / 2;
                int l = RealmsSelectWorldTemplateScreen.row(-1 + i);
                if (mouseX < k || mouseX > k + j || mouseY < l || mouseY > l + this.textRenderer.fontHeight) continue;
                this.hoverWarning = true;
            }
            for (i = 0; i < strings.length; ++i) {
                String string = strings[i];
                k = 0xA0A0A0;
                if (this.warningURL != null) {
                    if (this.hoverWarning) {
                        k = 7107012;
                        string = (Object)((Object)Formatting.STRIKETHROUGH) + string;
                    } else {
                        k = 0x3366BB;
                    }
                }
                this.drawCenteredString(this.textRenderer, string, this.width / 2, RealmsSelectWorldTemplateScreen.row(-1 + i), k);
            }
        }
        super.render(mouseX, mouseY, delta);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, mouseX, mouseY);
        }
    }

    private void method_21414(int i, int j, List<TextRenderingUtils.Line> list) {
        for (int k = 0; k < list.size(); ++k) {
            TextRenderingUtils.Line line = list.get(k);
            int l = RealmsSelectWorldTemplateScreen.row(4 + k);
            int m = line.segments.stream().mapToInt(lineSegment -> this.textRenderer.getStringWidth(lineSegment.renderedText())).sum();
            int n = this.width / 2 - m / 2;
            for (TextRenderingUtils.LineSegment lineSegment2 : line.segments) {
                int o = lineSegment2.isLink() ? 0x3366BB : 0xFFFFFF;
                int p = this.textRenderer.drawWithShadow(lineSegment2.renderedText(), n, l, o);
                if (lineSegment2.isLink() && i > n && i < p && j > l - 3 && j < l + 8) {
                    this.toolTip = lineSegment2.getLinkUrl();
                    this.currentLink = lineSegment2.getLinkUrl();
                }
                n = p;
            }
        }
    }

    protected void renderMousehoverTooltip(String msg, int x, int y) {
        if (msg == null) {
            return;
        }
        int i = x + 12;
        int j = y - 12;
        int k = this.textRenderer.getStringWidth(msg);
        this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
        this.textRenderer.drawWithShadow(msg, i, j, 0xFFFFFF);
    }

    @Environment(value=EnvType.CLIENT)
    class WorldTemplateObjectSelectionListEntry
    extends AlwaysSelectedEntryListWidget.Entry<WorldTemplateObjectSelectionListEntry> {
        private final WorldTemplate mTemplate;

        public WorldTemplateObjectSelectionListEntry(WorldTemplate template) {
            this.mTemplate = template;
        }

        @Override
        public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
            this.renderWorldTemplateItem(this.mTemplate, x, y, mouseX, mouseY);
        }

        private void renderWorldTemplateItem(WorldTemplate worldTemplate, int x, int y, int mouseX, int mouseY) {
            int i = x + 45 + 20;
            RealmsSelectWorldTemplateScreen.this.textRenderer.draw(worldTemplate.name, i, y + 2, 0xFFFFFF);
            RealmsSelectWorldTemplateScreen.this.textRenderer.draw(worldTemplate.author, i, y + 15, 0x6C6C6C);
            RealmsSelectWorldTemplateScreen.this.textRenderer.draw(worldTemplate.version, i + 227 - RealmsSelectWorldTemplateScreen.this.textRenderer.getStringWidth(worldTemplate.version), y + 1, 0x6C6C6C);
            if (!("".equals(worldTemplate.link) && "".equals(worldTemplate.trailer) && "".equals(worldTemplate.recommendedPlayers))) {
                this.drawIcons(i - 1, y + 25, mouseX, mouseY, worldTemplate.link, worldTemplate.trailer, worldTemplate.recommendedPlayers);
            }
            this.drawImage(x, y + 1, mouseX, mouseY, worldTemplate);
        }

        private void drawImage(int x, int y, int xm, int ym, WorldTemplate worldTemplate) {
            RealmsTextureManager.bindWorldTemplate(worldTemplate.id, worldTemplate.image);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(x + 1, y + 1, 0.0f, 0.0f, 38, 38, 38, 38);
            RealmsSelectWorldTemplateScreen.this.client.getTextureManager().bindTexture(SLOT_FRAME);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(x, y, 0.0f, 0.0f, 40, 40, 40, 40);
        }

        private void drawIcons(int x, int y, int xm, int ym, String link, String trailerLink, String recommendedPlayers) {
            if (!"".equals(recommendedPlayers)) {
                RealmsSelectWorldTemplateScreen.this.textRenderer.draw(recommendedPlayers, x, y + 4, 0x4C4C4C);
            }
            int i = "".equals(recommendedPlayers) ? 0 : RealmsSelectWorldTemplateScreen.this.textRenderer.getStringWidth(recommendedPlayers) + 2;
            boolean bl = false;
            boolean bl2 = false;
            if (xm >= x + i && xm <= x + i + 32 && ym >= y && ym <= y + 15 && ym < RealmsSelectWorldTemplateScreen.this.height - 15 && ym > 32) {
                if (xm <= x + 15 + i && xm > i) {
                    if ("".equals(link)) {
                        bl2 = true;
                    } else {
                        bl = true;
                    }
                } else if (!"".equals(link)) {
                    bl2 = true;
                }
            }
            if (!"".equals(link)) {
                RealmsSelectWorldTemplateScreen.this.client.getTextureManager().bindTexture(LINK_ICONS);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.pushMatrix();
                RenderSystem.scalef(1.0f, 1.0f, 1.0f);
                float f = bl ? 15.0f : 0.0f;
                DrawableHelper.drawTexture(x + i, y, f, 0.0f, 15, 15, 30, 15);
                RenderSystem.popMatrix();
            }
            if (!"".equals(trailerLink)) {
                RealmsSelectWorldTemplateScreen.this.client.getTextureManager().bindTexture(TRAILER_ICONS);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.pushMatrix();
                RenderSystem.scalef(1.0f, 1.0f, 1.0f);
                int j = x + i + ("".equals(link) ? 0 : 17);
                float g = bl2 ? 15.0f : 0.0f;
                DrawableHelper.drawTexture(j, y, g, 0.0f, 15, 15, 30, 15);
                RenderSystem.popMatrix();
            }
            if (bl && !"".equals(link)) {
                RealmsSelectWorldTemplateScreen.this.toolTip = I18n.translate("mco.template.info.tooltip", new Object[0]);
                RealmsSelectWorldTemplateScreen.this.currentLink = link;
            } else if (bl2 && !"".equals(trailerLink)) {
                RealmsSelectWorldTemplateScreen.this.toolTip = I18n.translate("mco.template.trailer.tooltip", new Object[0]);
                RealmsSelectWorldTemplateScreen.this.currentLink = trailerLink;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class WorldTemplateObjectSelectionList
    extends RealmsObjectSelectionList<WorldTemplateObjectSelectionListEntry> {
        public WorldTemplateObjectSelectionList() {
            this(Collections.emptyList());
        }

        public WorldTemplateObjectSelectionList(Iterable<WorldTemplate> templates) {
            super(RealmsSelectWorldTemplateScreen.this.width, RealmsSelectWorldTemplateScreen.this.height, RealmsSelectWorldTemplateScreen.this.displayWarning ? RealmsSelectWorldTemplateScreen.row(1) : 32, RealmsSelectWorldTemplateScreen.this.height - 40, 46);
            templates.forEach(this::addEntry);
        }

        public void addEntry(WorldTemplate template) {
            this.addEntry(new WorldTemplateObjectSelectionListEntry(template));
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
                if (mouseX >= (double)i && mouseX < (double)this.getScrollbarPositionX() && k >= 0 && j >= 0 && k < this.getItemCount()) {
                    this.setSelected(k);
                    this.itemClicked(j, k, mouseX, mouseY, this.width);
                    if (k >= RealmsSelectWorldTemplateScreen.this.templateList.getItemCount()) {
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
                String string = I18n.translate("narrator.select.list.position", index + 1, RealmsSelectWorldTemplateScreen.this.templateList.getItemCount());
                String string2 = I18n.translate("mco.template.select.narrate.version", worldTemplate.version);
                String string3 = I18n.translate("mco.template.select.narrate.authors", worldTemplate.author);
                String string4 = Realms.joinNarrations(Arrays.asList(worldTemplate.name, string3, worldTemplate.recommendedPlayers, string2, string));
                Realms.narrateNow(I18n.translate("narrator.select", string4));
            }
        }

        @Override
        public void setSelected(@Nullable WorldTemplateObjectSelectionListEntry worldTemplateObjectSelectionListEntry) {
            super.setSelected(worldTemplateObjectSelectionListEntry);
            RealmsSelectWorldTemplateScreen.this.selectedTemplate = this.children().indexOf(worldTemplateObjectSelectionListEntry);
            RealmsSelectWorldTemplateScreen.this.updateButtonStates();
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 46;
        }

        @Override
        public int getRowWidth() {
            return 300;
        }

        @Override
        public void renderBackground() {
            RealmsSelectWorldTemplateScreen.this.renderBackground();
        }

        @Override
        public boolean isFocused() {
            return RealmsSelectWorldTemplateScreen.this.getFocused() == this;
        }

        public boolean isEmpty() {
            return this.getItemCount() == 0;
        }

        public WorldTemplate getItem(int index) {
            return ((WorldTemplateObjectSelectionListEntry)this.children().get(index)).mTemplate;
        }

        public List<WorldTemplate> getValues() {
            return this.children().stream().map(worldTemplateObjectSelectionListEntry -> ((WorldTemplateObjectSelectionListEntry)worldTemplateObjectSelectionListEntry).mTemplate).collect(Collectors.toList());
        }
    }
}

