/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BookScreen
extends Screen {
    public static final Contents EMPTY_PROVIDER = new Contents(){

        @Override
        public int getPageCount() {
            return 0;
        }

        @Override
        public StringRenderable getPageUnchecked(int index) {
            return StringRenderable.EMPTY;
        }
    };
    public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
    private Contents contents;
    private int pageIndex;
    private List<StringRenderable> cachedPage = Collections.emptyList();
    private int cachedPageIndex = -1;
    private StringRenderable field_25897 = StringRenderable.EMPTY;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private final boolean pageTurnSound;

    public BookScreen(Contents pageProvider) {
        this(pageProvider, true);
    }

    public BookScreen() {
        this(EMPTY_PROVIDER, false);
    }

    private BookScreen(Contents contents, boolean playPageTurnSound) {
        super(NarratorManager.EMPTY);
        this.contents = contents;
        this.pageTurnSound = playPageTurnSound;
    }

    public void setPageProvider(Contents pageProvider) {
        this.contents = pageProvider;
        this.pageIndex = MathHelper.clamp(this.pageIndex, 0, pageProvider.getPageCount());
        this.updatePageButtons();
        this.cachedPageIndex = -1;
    }

    public boolean setPage(int index) {
        int i = MathHelper.clamp(index, 0, this.contents.getPageCount() - 1);
        if (i != this.pageIndex) {
            this.pageIndex = i;
            this.updatePageButtons();
            this.cachedPageIndex = -1;
            return true;
        }
        return false;
    }

    protected boolean jumpToPage(int page) {
        return this.setPage(page);
    }

    @Override
    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
    }

    protected void addCloseButton() {
        this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, ScreenTexts.DONE, buttonWidget -> this.client.openScreen(null)));
    }

    protected void addPageButtons() {
        int i = (this.width - 192) / 2;
        int j = 2;
        this.nextPageButton = this.addButton(new PageTurnWidget(i + 116, 159, true, buttonWidget -> this.goToNextPage(), this.pageTurnSound));
        this.previousPageButton = this.addButton(new PageTurnWidget(i + 43, 159, false, buttonWidget -> this.goToPreviousPage(), this.pageTurnSound));
        this.updatePageButtons();
    }

    private int getPageCount() {
        return this.contents.getPageCount();
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
        this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
        this.previousPageButton.visible = this.pageIndex > 0;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case 266: {
                this.previousPageButton.onPress();
                return true;
            }
            case 267: {
                this.nextPageButton.onPress();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(BOOK_TEXTURE);
        int i = (this.width - 192) / 2;
        int j = 2;
        this.drawTexture(matrices, i, 2, 0, 0, 192, 192);
        if (this.cachedPageIndex != this.pageIndex) {
            StringRenderable stringRenderable = this.contents.getPage(this.pageIndex);
            this.cachedPage = this.textRenderer.getTextHandler().wrapLines(stringRenderable, 114, Style.EMPTY);
            this.field_25897 = new TranslatableText("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
        }
        this.cachedPageIndex = this.pageIndex;
        int k = this.textRenderer.getWidth(this.field_25897);
        this.textRenderer.draw(matrices, this.field_25897, (float)(i - k + 192 - 44), 18.0f, 0);
        int l = Math.min(128 / this.textRenderer.fontHeight, this.cachedPage.size());
        for (int m = 0; m < l; ++m) {
            StringRenderable stringRenderable2 = this.cachedPage.get(m);
            this.textRenderer.draw(matrices, stringRenderable2, (float)(i + 36), (float)(32 + m * this.textRenderer.fontHeight), 0);
        }
        Style style = this.getTextAt(mouseX, mouseY);
        if (style != null) {
            this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Style style;
        if (button == 0 && (style = this.getTextAt(mouseX, mouseY)) != null && this.handleTextClick(style)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean handleTextClick(Style style) {
        ClickEvent clickEvent = style.getClickEvent();
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
        boolean bl = super.handleTextClick(style);
        if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.client.openScreen(null);
        }
        return bl;
    }

    @Nullable
    public Style getTextAt(double x, double y) {
        if (this.cachedPage == null) {
            return null;
        }
        int i = MathHelper.floor(x - (double)((this.width - 192) / 2) - 36.0);
        int j = MathHelper.floor(y - 2.0 - 30.0);
        if (i < 0 || j < 0) {
            return null;
        }
        int k = Math.min(128 / this.textRenderer.fontHeight, this.cachedPage.size());
        if (i <= 114 && j < this.client.textRenderer.fontHeight * k + k) {
            int l = j / this.client.textRenderer.fontHeight;
            if (l >= 0 && l < this.cachedPage.size()) {
                StringRenderable stringRenderable = this.cachedPage.get(l);
                return this.client.textRenderer.getTextHandler().trimToWidth(stringRenderable, i);
            }
            return null;
        }
        return null;
    }

    public static List<String> readPages(CompoundTag tag) {
        ListTag listTag = tag.getList("pages", 8).copy();
        ImmutableList.Builder builder = ImmutableList.builder();
        for (int i = 0; i < listTag.size(); ++i) {
            builder.add(listTag.getString(i));
        }
        return builder.build();
    }

    @Environment(value=EnvType.CLIENT)
    public static class WritableBookContents
    implements Contents {
        private final List<String> pages;

        public WritableBookContents(ItemStack stack) {
            this.pages = WritableBookContents.getPages(stack);
        }

        private static List<String> getPages(ItemStack stack) {
            CompoundTag compoundTag = stack.getTag();
            return compoundTag != null ? BookScreen.readPages(compoundTag) : ImmutableList.of();
        }

        @Override
        public int getPageCount() {
            return this.pages.size();
        }

        @Override
        public StringRenderable getPageUnchecked(int index) {
            return StringRenderable.plain(this.pages.get(index));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class WrittenBookContents
    implements Contents {
        private final List<String> pages;

        public WrittenBookContents(ItemStack stack) {
            this.pages = WrittenBookContents.getPages(stack);
        }

        private static List<String> getPages(ItemStack stack) {
            CompoundTag compoundTag = stack.getTag();
            if (compoundTag != null && WrittenBookItem.isValid(compoundTag)) {
                return BookScreen.readPages(compoundTag);
            }
            return ImmutableList.of(Text.Serializer.toJson(new TranslatableText("book.invalid.tag").formatted(Formatting.DARK_RED)));
        }

        @Override
        public int getPageCount() {
            return this.pages.size();
        }

        @Override
        public StringRenderable getPageUnchecked(int index) {
            String string = this.pages.get(index);
            try {
                MutableText stringRenderable = Text.Serializer.fromJson(string);
                if (stringRenderable != null) {
                    return stringRenderable;
                }
            } catch (Exception exception) {
                // empty catch block
            }
            return StringRenderable.plain(string);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Contents {
        public int getPageCount();

        public StringRenderable getPageUnchecked(int var1);

        default public StringRenderable getPage(int index) {
            if (index >= 0 && index < this.getPageCount()) {
                return this.getPageUnchecked(index);
            }
            return StringRenderable.EMPTY;
        }

        public static Contents create(ItemStack stack) {
            Item item = stack.getItem();
            if (item == Items.WRITTEN_BOOK) {
                return new WrittenBookContents(stack);
            }
            if (item == Items.WRITABLE_BOOK) {
                return new WritableBookContents(stack);
            }
            return EMPTY_PROVIDER;
        }
    }
}

