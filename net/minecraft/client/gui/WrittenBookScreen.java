/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.BookPageButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WrittenBookScreen
extends Screen {
    public static final Contents EMPTY_PROVIDER = new Contents(){

        @Override
        public int getLineCount() {
            return 0;
        }

        @Override
        public Component getLine(int i) {
            return new TextComponent("");
        }
    };
    public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
    private Contents contents;
    private int pageIndex;
    private List<Component> cachedPage = Collections.emptyList();
    private int cachedPageIndex = -1;
    private BookPageButtonWidget lastPageButton;
    private BookPageButtonWidget nextPageButton;
    private final boolean pageTurnSound;

    public WrittenBookScreen(Contents contents) {
        this(contents, true);
    }

    public WrittenBookScreen() {
        this(EMPTY_PROVIDER, false);
    }

    private WrittenBookScreen(Contents contents, boolean bl) {
        super(NarratorManager.field_18967);
        this.contents = contents;
        this.pageTurnSound = bl;
    }

    public void setPageProvider(Contents contents) {
        this.contents = contents;
        this.pageIndex = MathHelper.clamp(this.pageIndex, 0, contents.getLineCount());
        this.updatePageButtons();
        this.cachedPageIndex = -1;
    }

    public boolean setPage(int i) {
        int j = MathHelper.clamp(i, 0, this.contents.getLineCount() - 1);
        if (j != this.pageIndex) {
            this.pageIndex = j;
            this.updatePageButtons();
            this.cachedPageIndex = -1;
            return true;
        }
        return false;
    }

    protected boolean jumpToPage(int i) {
        return this.setPage(i);
    }

    @Override
    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
    }

    protected void addCloseButton() {
        this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.minecraft.openScreen(null)));
    }

    protected void addPageButtons() {
        int i = (this.width - 192) / 2;
        int j = 2;
        this.lastPageButton = this.addButton(new BookPageButtonWidget(i + 116, 159, true, buttonWidget -> this.goToNextPage(), this.pageTurnSound));
        this.nextPageButton = this.addButton(new BookPageButtonWidget(i + 43, 159, false, buttonWidget -> this.goToPreviousPage(), this.pageTurnSound));
        this.updatePageButtons();
    }

    private int getPageCount() {
        return this.contents.getLineCount();
    }

    protected void goToPreviousPage() {
        if (this.pageIndex > 0) {
            --this.pageIndex;
        }
        this.updatePageButtons();
    }

    protected void goToNextPage() {
        if (this.pageIndex < this.getPageCount() - 1) {
            ++this.pageIndex;
        }
        this.updatePageButtons();
    }

    private void updatePageButtons() {
        this.lastPageButton.visible = this.pageIndex < this.getPageCount() - 1;
        this.nextPageButton.visible = this.pageIndex > 0;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        switch (i) {
            case 266: {
                this.nextPageButton.onPress();
                return true;
            }
            case 267: {
                this.lastPageButton.onPress();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BOOK_TEXTURE);
        int k = (this.width - 192) / 2;
        int l = 2;
        this.blit(k, 2, 0, 0, 192, 192);
        String string = I18n.translate("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
        if (this.cachedPageIndex != this.pageIndex) {
            Component component = this.contents.getLineOrDefault(this.pageIndex);
            this.cachedPage = TextComponentUtil.wrapLines(component, 114, this.font, true, true);
        }
        this.cachedPageIndex = this.pageIndex;
        int m = this.getStringWidth(string);
        this.font.draw(string, k - m + 192 - 44, 18.0f, 0);
        int n = Math.min(128 / this.font.fontHeight, this.cachedPage.size());
        for (int o = 0; o < n; ++o) {
            Component component2 = this.cachedPage.get(o);
            this.font.draw(component2.getFormattedText(), k + 36, 32 + o * this.font.fontHeight, 0);
        }
        Component component3 = this.getLineAt(i, j);
        if (component3 != null) {
            this.renderComponentHoverEffect(component3, i, j);
        }
        super.render(i, j, f);
    }

    private int getStringWidth(String string) {
        return this.font.getStringWidth(this.font.isRightToLeft() ? this.font.mirror(string) : string);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        Component component;
        if (i == 0 && (component = this.getLineAt(d, e)) != null && this.handleComponentClicked(component)) {
            return true;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean handleComponentClicked(Component component) {
        ClickEvent clickEvent = component.getStyle().getClickEvent();
        if (clickEvent == null) {
            return false;
        }
        if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String string = clickEvent.getValue();
            try {
                int i = Integer.parseInt(string) - 1;
                return this.jumpToPage(i);
            } catch (Exception exception) {
                return false;
            }
        }
        boolean bl = super.handleComponentClicked(component);
        if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.minecraft.openScreen(null);
        }
        return bl;
    }

    @Nullable
    public Component getLineAt(double d, double e) {
        if (this.cachedPage == null) {
            return null;
        }
        int i = MathHelper.floor(d - (double)((this.width - 192) / 2) - 36.0);
        int j = MathHelper.floor(e - 2.0 - 30.0);
        if (i < 0 || j < 0) {
            return null;
        }
        int k = Math.min(128 / this.font.fontHeight, this.cachedPage.size());
        if (i <= 114 && j < this.minecraft.textRenderer.fontHeight * k + k) {
            int l = j / this.minecraft.textRenderer.fontHeight;
            if (l >= 0 && l < this.cachedPage.size()) {
                Component component = this.cachedPage.get(l);
                int m = 0;
                for (Component component2 : component) {
                    if (!(component2 instanceof TextComponent) || (m += this.minecraft.textRenderer.getStringWidth(component2.getFormattedText())) <= i) continue;
                    return component2;
                }
            }
            return null;
        }
        return null;
    }

    public static List<String> getLines(CompoundTag compoundTag) {
        ListTag listTag = compoundTag.getList("pages", 8).method_10612();
        ImmutableList.Builder builder = ImmutableList.builder();
        for (int i = 0; i < listTag.size(); ++i) {
            builder.add(listTag.getString(i));
        }
        return builder.build();
    }

    @Environment(value=EnvType.CLIENT)
    public static class WritableBookContents
    implements Contents {
        private final List<String> lines;

        public WritableBookContents(ItemStack itemStack) {
            this.lines = WritableBookContents.getLines(itemStack);
        }

        private static List<String> getLines(ItemStack itemStack) {
            CompoundTag compoundTag = itemStack.getTag();
            return compoundTag != null ? WrittenBookScreen.getLines(compoundTag) : ImmutableList.of();
        }

        @Override
        public int getLineCount() {
            return this.lines.size();
        }

        @Override
        public Component getLine(int i) {
            return new TextComponent(this.lines.get(i));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class WrittenBookContents
    implements Contents {
        private final List<String> lines;

        public WrittenBookContents(ItemStack itemStack) {
            this.lines = WrittenBookContents.getLines(itemStack);
        }

        private static List<String> getLines(ItemStack itemStack) {
            CompoundTag compoundTag = itemStack.getTag();
            if (compoundTag != null && WrittenBookItem.isValidBook(compoundTag)) {
                return WrittenBookScreen.getLines(compoundTag);
            }
            return ImmutableList.of(new TranslatableComponent("book.invalid.tag", new Object[0]).applyFormat(ChatFormat.DARK_RED).getFormattedText());
        }

        @Override
        public int getLineCount() {
            return this.lines.size();
        }

        @Override
        public Component getLine(int i) {
            String string = this.lines.get(i);
            try {
                Component component = Component.Serializer.fromJsonString(string);
                if (component != null) {
                    return component;
                }
            } catch (Exception exception) {
                // empty catch block
            }
            return new TextComponent(string);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Contents {
        public int getLineCount();

        public Component getLine(int var1);

        default public Component getLineOrDefault(int i) {
            if (i >= 0 && i < this.getLineCount()) {
                return this.getLine(i);
            }
            return new TextComponent("");
        }

        public static Contents create(ItemStack itemStack) {
            Item item = itemStack.getItem();
            if (item == Items.WRITTEN_BOOK) {
                return new WrittenBookContents(itemStack);
            }
            if (item == Items.WRITABLE_BOOK) {
                return new WritableBookContents(itemStack);
            }
            return EMPTY_PROVIDER;
        }
    }
}

