/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BookScreen
extends Screen {
    public static final int field_32328 = 16;
    public static final int field_32329 = 36;
    public static final int field_32330 = 30;
    public static final Contents EMPTY_PROVIDER = new Contents(){

        @Override
        public int getPageCount() {
            return 0;
        }

        @Override
        public StringVisitable getPageUnchecked(int index) {
            return StringVisitable.EMPTY;
        }
    };
    public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
    protected static final int MAX_TEXT_WIDTH = 114;
    protected static final int MAX_TEXT_HEIGHT = 128;
    protected static final int WIDTH = 192;
    protected static final int HEIGHT = 192;
    private Contents contents;
    private int pageIndex;
    private List<OrderedText> cachedPage = Collections.emptyList();
    private int cachedPageIndex = -1;
    private Text pageIndexText = ScreenTexts.EMPTY;
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
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.client.setScreen(null)).dimensions(this.width / 2 - 100, 196, 200, 20).build());
    }

    protected void addPageButtons() {
        int i = (this.width - 192) / 2;
        int j = 2;
        this.nextPageButton = this.addDrawableChild(new PageTurnWidget(i + 116, 159, true, button -> this.goToNextPage(), this.pageTurnSound));
        this.previousPageButton = this.addDrawableChild(new PageTurnWidget(i + 43, 159, false, button -> this.goToPreviousPage(), this.pageTurnSound));
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
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, BOOK_TEXTURE);
        int i = (this.width - 192) / 2;
        int j = 2;
        this.drawTexture(matrices, i, 2, 0, 0, 192, 192);
        if (this.cachedPageIndex != this.pageIndex) {
            StringVisitable stringVisitable = this.contents.getPage(this.pageIndex);
            this.cachedPage = this.textRenderer.wrapLines(stringVisitable, 114);
            this.pageIndexText = Text.translatable("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
        }
        this.cachedPageIndex = this.pageIndex;
        int k = this.textRenderer.getWidth(this.pageIndexText);
        this.textRenderer.draw(matrices, this.pageIndexText, (float)(i - k + 192 - 44), 18.0f, 0);
        int l = Math.min(128 / this.textRenderer.fontHeight, this.cachedPage.size());
        for (int m = 0; m < l; ++m) {
            OrderedText orderedText = this.cachedPage.get(m);
            this.textRenderer.draw(matrices, orderedText, (float)(i + 36), (float)(32 + m * this.textRenderer.fontHeight), 0);
        }
        Style style = this.getTextStyleAt(mouseX, mouseY);
        if (style != null) {
            this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Style style;
        if (button == 0 && (style = this.getTextStyleAt(mouseX, mouseY)) != null && this.handleTextClick(style)) {
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
            this.closeScreen();
        }
        return bl;
    }

    protected void closeScreen() {
        this.client.setScreen(null);
    }

    @Nullable
    public Style getTextStyleAt(double x, double y) {
        if (this.cachedPage.isEmpty()) {
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
                OrderedText orderedText = this.cachedPage.get(l);
                return this.client.textRenderer.getTextHandler().getStyleAt(orderedText, i);
            }
            return null;
        }
        return null;
    }

    static List<String> readPages(NbtCompound nbt) {
        ImmutableList.Builder builder = ImmutableList.builder();
        BookScreen.filterPages(nbt, builder::add);
        return builder.build();
    }

    public static void filterPages(NbtCompound nbt, Consumer<String> pageConsumer) {
        IntFunction<String> intFunction;
        NbtList nbtList = nbt.getList("pages", NbtElement.STRING_TYPE).copy();
        if (MinecraftClient.getInstance().shouldFilterText() && nbt.contains("filtered_pages", NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbtCompound = nbt.getCompound("filtered_pages");
            intFunction = page -> {
                String string = String.valueOf(page);
                return nbtCompound.contains(string) ? nbtCompound.getString(string) : nbtList.getString(page);
            };
        } else {
            intFunction = nbtList::getString;
        }
        for (int i = 0; i < nbtList.size(); ++i) {
            pageConsumer.accept(intFunction.apply(i));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Contents {
        public int getPageCount();

        public StringVisitable getPageUnchecked(int var1);

        default public StringVisitable getPage(int index) {
            if (index >= 0 && index < this.getPageCount()) {
                return this.getPageUnchecked(index);
            }
            return StringVisitable.EMPTY;
        }

        public static Contents create(ItemStack stack) {
            if (stack.isOf(Items.WRITTEN_BOOK)) {
                return new WrittenBookContents(stack);
            }
            if (stack.isOf(Items.WRITABLE_BOOK)) {
                return new WritableBookContents(stack);
            }
            return EMPTY_PROVIDER;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class WritableBookContents
    implements Contents {
        private final List<String> pages;

        public WritableBookContents(ItemStack stack) {
            this.pages = WritableBookContents.getPages(stack);
        }

        private static List<String> getPages(ItemStack stack) {
            NbtCompound nbtCompound = stack.getNbt();
            return nbtCompound != null ? BookScreen.readPages(nbtCompound) : ImmutableList.of();
        }

        @Override
        public int getPageCount() {
            return this.pages.size();
        }

        @Override
        public StringVisitable getPageUnchecked(int index) {
            return StringVisitable.plain(this.pages.get(index));
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
            NbtCompound nbtCompound = stack.getNbt();
            if (nbtCompound != null && WrittenBookItem.isValid(nbtCompound)) {
                return BookScreen.readPages(nbtCompound);
            }
            return ImmutableList.of(Text.Serializer.toJson(Text.translatable("book.invalid.tag").formatted(Formatting.DARK_RED)));
        }

        @Override
        public int getPageCount() {
            return this.pages.size();
        }

        @Override
        public StringVisitable getPageUnchecked(int index) {
            String string = this.pages.get(index);
            try {
                MutableText stringVisitable = Text.Serializer.fromJson(string);
                if (stringVisitable != null) {
                    return stringVisitable;
                }
            } catch (Exception exception) {
                // empty catch block
            }
            return StringVisitable.plain(string);
        }
    }
}

