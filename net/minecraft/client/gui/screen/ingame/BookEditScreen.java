/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Rect2i;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BookEditScreen
extends Screen {
    private static final Text field_25893 = new TranslatableText("book.editTitle");
    private static final Text field_25894 = new TranslatableText("book.finalizeWarning");
    private static final OrderedText field_25895 = OrderedText.styledString("_", Style.EMPTY.withColor(Formatting.BLACK));
    private static final OrderedText field_25896 = OrderedText.styledString("_", Style.EMPTY.withColor(Formatting.GRAY));
    private final PlayerEntity player;
    private final ItemStack itemStack;
    private boolean dirty;
    private boolean signing;
    private int tickCounter;
    private int currentPage;
    private final List<String> pages = Lists.newArrayList();
    private String title = "";
    private final SelectionManager field_24269 = new SelectionManager(this::getCurrentPageContent, this::setPageContent, this::method_27595, this::method_27584, string -> string.length() < 1024 && this.textRenderer.getStringBoundedHeight((String)string, 114) <= 128);
    private final SelectionManager field_24270 = new SelectionManager(() -> this.title, string -> {
        this.title = string;
    }, this::method_27595, this::method_27584, string -> string.length() < 16);
    private long lastClickTime;
    private int lastClickIndex = -1;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private ButtonWidget doneButton;
    private ButtonWidget signButton;
    private ButtonWidget finalizeButton;
    private ButtonWidget cancelButton;
    private final Hand hand;
    @Nullable
    private PageContent pageContent = PageContent.method_27599();
    private Text field_25891 = LiteralText.EMPTY;
    private final Text field_25892;

    public BookEditScreen(PlayerEntity playerEntity, ItemStack itemStack, Hand hand) {
        super(NarratorManager.EMPTY);
        this.player = playerEntity;
        this.itemStack = itemStack;
        this.hand = hand;
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null) {
            ListTag listTag = compoundTag.getList("pages", 8).copy();
            for (int i = 0; i < listTag.size(); ++i) {
                this.pages.add(listTag.getString(i));
            }
        }
        if (this.pages.isEmpty()) {
            this.pages.add("");
        }
        this.field_25892 = new TranslatableText("book.byAuthor", playerEntity.getName()).formatted(Formatting.DARK_GRAY);
    }

    private void method_27584(String string) {
        if (this.client != null) {
            SelectionManager.setClipboard(this.client, string);
        }
    }

    private String method_27595() {
        return this.client != null ? SelectionManager.getClipboard(this.client) : "";
    }

    private int countPages() {
        return this.pages.size();
    }

    @Override
    public void tick() {
        super.tick();
        ++this.tickCounter;
    }

    @Override
    protected void init() {
        this.invalidatePageContent();
        this.client.keyboard.setRepeatEvents(true);
        this.signButton = this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, new TranslatableText("book.signButton"), buttonWidget -> {
            this.signing = true;
            this.updateButtons();
        }));
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, ScreenTexts.DONE, buttonWidget -> {
            this.client.openScreen(null);
            this.finalizeBook(false);
        }));
        this.finalizeButton = this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, new TranslatableText("book.finalizeButton"), buttonWidget -> {
            if (this.signing) {
                this.finalizeBook(true);
                this.client.openScreen(null);
            }
        }));
        this.cancelButton = this.addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, ScreenTexts.CANCEL, buttonWidget -> {
            if (this.signing) {
                this.signing = false;
            }
            this.updateButtons();
        }));
        int i = (this.width - 192) / 2;
        int j = 2;
        this.nextPageButton = this.addButton(new PageTurnWidget(i + 116, 159, true, buttonWidget -> this.openNextPage(), true));
        this.previousPageButton = this.addButton(new PageTurnWidget(i + 43, 159, false, buttonWidget -> this.openPreviousPage(), true));
        this.updateButtons();
    }

    private void openPreviousPage() {
        if (this.currentPage > 0) {
            --this.currentPage;
        }
        this.updateButtons();
        this.method_27872();
    }

    private void openNextPage() {
        if (this.currentPage < this.countPages() - 1) {
            ++this.currentPage;
        } else {
            this.appendNewPage();
            if (this.currentPage < this.countPages() - 1) {
                ++this.currentPage;
            }
        }
        this.updateButtons();
        this.method_27872();
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    private void updateButtons() {
        this.previousPageButton.visible = !this.signing && this.currentPage > 0;
        this.nextPageButton.visible = !this.signing;
        this.doneButton.visible = !this.signing;
        this.signButton.visible = !this.signing;
        this.cancelButton.visible = this.signing;
        this.finalizeButton.visible = this.signing;
        this.finalizeButton.active = !this.title.trim().isEmpty();
    }

    private void removeEmptyPages() {
        ListIterator<String> listIterator = this.pages.listIterator(this.pages.size());
        while (listIterator.hasPrevious() && listIterator.previous().isEmpty()) {
            listIterator.remove();
        }
    }

    private void finalizeBook(boolean signBook) {
        if (!this.dirty) {
            return;
        }
        this.removeEmptyPages();
        ListTag listTag = new ListTag();
        this.pages.stream().map(StringTag::of).forEach(listTag::add);
        if (!this.pages.isEmpty()) {
            this.itemStack.putSubTag("pages", listTag);
        }
        if (signBook) {
            this.itemStack.putSubTag("author", StringTag.of(this.player.getGameProfile().getName()));
            this.itemStack.putSubTag("title", StringTag.of(this.title.trim()));
        }
        int i = this.hand == Hand.MAIN_HAND ? this.player.getInventory().selectedSlot : 40;
        this.client.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(this.itemStack, signBook, i));
    }

    private void appendNewPage() {
        if (this.countPages() >= 100) {
            return;
        }
        this.pages.add("");
        this.dirty = true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.signing) {
            return this.keyPressedSignMode(keyCode, scanCode, modifiers);
        }
        boolean bl = this.method_27592(keyCode, scanCode, modifiers);
        if (bl) {
            this.invalidatePageContent();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (super.charTyped(chr, keyCode)) {
            return true;
        }
        if (this.signing) {
            boolean bl = this.field_24270.insert(chr);
            if (bl) {
                this.updateButtons();
                this.dirty = true;
                return true;
            }
            return false;
        }
        if (SharedConstants.isValidChar(chr)) {
            this.field_24269.insert(Character.toString(chr));
            this.invalidatePageContent();
            return true;
        }
        return false;
    }

    private boolean method_27592(int i, int j, int k) {
        if (Screen.isSelectAll(i)) {
            this.field_24269.selectAll();
            return true;
        }
        if (Screen.isCopy(i)) {
            this.field_24269.copy();
            return true;
        }
        if (Screen.isPaste(i)) {
            this.field_24269.paste();
            return true;
        }
        if (Screen.isCut(i)) {
            this.field_24269.cut();
            return true;
        }
        switch (i) {
            case 259: {
                this.field_24269.delete(-1);
                return true;
            }
            case 261: {
                this.field_24269.delete(1);
                return true;
            }
            case 257: 
            case 335: {
                this.field_24269.insert("\n");
                return true;
            }
            case 263: {
                this.field_24269.moveCursor(-1, Screen.hasShiftDown());
                return true;
            }
            case 262: {
                this.field_24269.moveCursor(1, Screen.hasShiftDown());
                return true;
            }
            case 265: {
                this.method_27597();
                return true;
            }
            case 264: {
                this.method_27598();
                return true;
            }
            case 266: {
                this.previousPageButton.onPress();
                return true;
            }
            case 267: {
                this.nextPageButton.onPress();
                return true;
            }
            case 268: {
                this.moveCursorToTop();
                return true;
            }
            case 269: {
                this.moveCursorToBottom();
                return true;
            }
        }
        return false;
    }

    private void method_27597() {
        this.method_27580(-1);
    }

    private void method_27598() {
        this.method_27580(1);
    }

    private void method_27580(int i) {
        int j = this.field_24269.getSelectionStart();
        int k = this.getPageContent().method_27601(j, i);
        this.field_24269.method_27560(k, Screen.hasShiftDown());
    }

    private void moveCursorToTop() {
        int i = this.field_24269.getSelectionStart();
        int j = this.getPageContent().method_27600(i);
        this.field_24269.method_27560(j, Screen.hasShiftDown());
    }

    private void moveCursorToBottom() {
        PageContent pageContent = this.getPageContent();
        int i = this.field_24269.getSelectionStart();
        int j = pageContent.method_27604(i);
        this.field_24269.method_27560(j, Screen.hasShiftDown());
    }

    private boolean keyPressedSignMode(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case 259: {
                this.field_24270.delete(-1);
                this.updateButtons();
                this.dirty = true;
                return true;
            }
            case 257: 
            case 335: {
                if (!this.title.isEmpty()) {
                    this.finalizeBook(true);
                    this.client.openScreen(null);
                }
                return true;
            }
        }
        return false;
    }

    private String getCurrentPageContent() {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            return this.pages.get(this.currentPage);
        }
        return "";
    }

    private void setPageContent(String newContent) {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            this.pages.set(this.currentPage, newContent);
            this.dirty = true;
            this.invalidatePageContent();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.setFocused(null);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(BookScreen.BOOK_TEXTURE);
        int i = (this.width - 192) / 2;
        int j = 2;
        this.drawTexture(matrices, i, 2, 0, 0, 192, 192);
        if (this.signing) {
            boolean bl = this.tickCounter / 6 % 2 == 0;
            OrderedText orderedText = OrderedText.concat(OrderedText.styledString(this.title, Style.EMPTY), bl ? field_25895 : field_25896);
            int k = this.textRenderer.getWidth(field_25893);
            this.textRenderer.draw(matrices, field_25893, (float)(i + 36 + (114 - k) / 2), 34.0f, 0);
            int l = this.textRenderer.getWidth(orderedText);
            this.textRenderer.draw(matrices, orderedText, (float)(i + 36 + (114 - l) / 2), 50.0f, 0);
            int m = this.textRenderer.getWidth(this.field_25892);
            this.textRenderer.draw(matrices, this.field_25892, (float)(i + 36 + (114 - m) / 2), 60.0f, 0);
            this.textRenderer.drawTrimmed(field_25894, i + 36, 82, 114, 0);
        } else {
            int n = this.textRenderer.getWidth(this.field_25891);
            this.textRenderer.draw(matrices, this.field_25891, (float)(i - n + 192 - 44), 18.0f, 0);
            PageContent pageContent = this.getPageContent();
            for (Line line : pageContent.lines) {
                this.textRenderer.draw(matrices, line.text, (float)line.x, (float)line.y, -16777216);
            }
            this.method_27588(pageContent.field_24277);
            this.method_27581(matrices, pageContent.position, pageContent.field_24274);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void method_27581(MatrixStack matrixStack, Position position, boolean bl) {
        if (this.tickCounter / 6 % 2 == 0) {
            position = this.method_27590(position);
            if (!bl) {
                DrawableHelper.fill(matrixStack, position.x, position.y - 1, position.x + 1, position.y + this.textRenderer.fontHeight, -16777216);
            } else {
                this.textRenderer.draw(matrixStack, "_", (float)position.x, (float)position.y, 0);
            }
        }
    }

    private void method_27588(Rect2i[] rect2is) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        for (Rect2i rect2i : rect2is) {
            int i = rect2i.getX();
            int j = rect2i.getY();
            int k = i + rect2i.getWidth();
            int l = j + rect2i.getHeight();
            bufferBuilder.vertex(i, l, 0.0).next();
            bufferBuilder.vertex(k, l, 0.0).next();
            bufferBuilder.vertex(k, j, 0.0).next();
            bufferBuilder.vertex(i, j, 0.0).next();
        }
        tessellator.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    private Position method_27582(Position position) {
        return new Position(position.x - (this.width - 192) / 2 - 36, position.y - 32);
    }

    private Position method_27590(Position position) {
        return new Position(position.x + (this.width - 192) / 2 + 36, position.y + 32);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0) {
            long l = Util.getMeasuringTimeMs();
            PageContent pageContent = this.getPageContent();
            int i = pageContent.method_27602(this.textRenderer, this.method_27582(new Position((int)mouseX, (int)mouseY)));
            if (i >= 0) {
                if (i == this.lastClickIndex && l - this.lastClickTime < 250L) {
                    if (!this.field_24269.method_27568()) {
                        this.method_27589(i);
                    } else {
                        this.field_24269.selectAll();
                    }
                } else {
                    this.field_24269.method_27560(i, Screen.hasShiftDown());
                }
                this.invalidatePageContent();
            }
            this.lastClickIndex = i;
            this.lastClickTime = l;
        }
        return true;
    }

    private void method_27589(int i) {
        String string = this.getCurrentPageContent();
        this.field_24269.method_27548(TextHandler.moveCursorByWords(string, -1, i, false), TextHandler.moveCursorByWords(string, 1, i, false));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (button == 0) {
            PageContent pageContent = this.getPageContent();
            int i = pageContent.method_27602(this.textRenderer, this.method_27582(new Position((int)mouseX, (int)mouseY)));
            this.field_24269.method_27560(i, true);
            this.invalidatePageContent();
        }
        return true;
    }

    private PageContent getPageContent() {
        if (this.pageContent == null) {
            this.pageContent = this.createPageContent();
            this.field_25891 = new TranslatableText("book.pageIndicator", this.currentPage + 1, this.countPages());
        }
        return this.pageContent;
    }

    private void invalidatePageContent() {
        this.pageContent = null;
    }

    private void method_27872() {
        this.field_24269.moveCaretToEnd();
        this.invalidatePageContent();
    }

    private PageContent createPageContent() {
        int l;
        Position position;
        boolean bl;
        String string = this.getCurrentPageContent();
        if (string.isEmpty()) {
            return PageContent.EMPTY;
        }
        int i2 = this.field_24269.getSelectionStart();
        int j2 = this.field_24269.getSelectionEnd();
        IntArrayList intList = new IntArrayList();
        ArrayList list = Lists.newArrayList();
        MutableInt mutableInt = new MutableInt();
        MutableBoolean mutableBoolean = new MutableBoolean();
        TextHandler textHandler = this.textRenderer.getTextHandler();
        textHandler.wrapLines(string, 114, Style.EMPTY, true, (style, i, j) -> {
            int k = mutableInt.getAndIncrement();
            String string2 = string.substring(i, j);
            mutableBoolean.setValue(string2.endsWith("\n"));
            String string3 = StringUtils.stripEnd(string2, " \n");
            int l = k * this.textRenderer.fontHeight;
            Position position = this.method_27590(new Position(0, l));
            intList.add(i);
            list.add(new Line(style, string3, position.x, position.y));
        });
        int[] is = intList.toIntArray();
        boolean bl2 = bl = i2 == string.length();
        if (bl && mutableBoolean.isTrue()) {
            position = new Position(0, list.size() * this.textRenderer.fontHeight);
        } else {
            int k = BookEditScreen.method_27591(is, i2);
            l = this.textRenderer.getWidth(string.substring(is[k], i2));
            position = new Position(l, k * this.textRenderer.fontHeight);
        }
        ArrayList<Rect2i> list2 = Lists.newArrayList();
        if (i2 != j2) {
            int o;
            l = Math.min(i2, j2);
            int m = Math.max(i2, j2);
            int n = BookEditScreen.method_27591(is, l);
            if (n == (o = BookEditScreen.method_27591(is, m))) {
                int p = n * this.textRenderer.fontHeight;
                int q = is[n];
                list2.add(this.method_27585(string, textHandler, l, m, p, q));
            } else {
                int p = n + 1 > is.length ? string.length() : is[n + 1];
                list2.add(this.method_27585(string, textHandler, l, p, n * this.textRenderer.fontHeight, is[n]));
                for (int q = n + 1; q < o; ++q) {
                    int r = q * this.textRenderer.fontHeight;
                    String string2 = string.substring(is[q], is[q + 1]);
                    int s = (int)textHandler.getWidth(string2);
                    list2.add(this.method_27583(new Position(0, r), new Position(s, r + this.textRenderer.fontHeight)));
                }
                list2.add(this.method_27585(string, textHandler, is[o], m, o * this.textRenderer.fontHeight, is[o]));
            }
        }
        return new PageContent(string, position, bl, is, list.toArray(new Line[0]), list2.toArray(new Rect2i[0]));
    }

    private static int method_27591(int[] is, int i) {
        int j = Arrays.binarySearch(is, i);
        if (j < 0) {
            return -(j + 2);
        }
        return j;
    }

    private Rect2i method_27585(String string, TextHandler textHandler, int i, int j, int k, int l) {
        String string2 = string.substring(l, i);
        String string3 = string.substring(l, j);
        Position position = new Position((int)textHandler.getWidth(string2), k);
        Position position2 = new Position((int)textHandler.getWidth(string3), k + this.textRenderer.fontHeight);
        return this.method_27583(position, position2);
    }

    private Rect2i method_27583(Position position, Position position2) {
        Position position3 = this.method_27590(position);
        Position position4 = this.method_27590(position2);
        int i = Math.min(position3.x, position4.x);
        int j = Math.max(position3.x, position4.x);
        int k = Math.min(position3.y, position4.y);
        int l = Math.max(position3.y, position4.y);
        return new Rect2i(i, k, j - i, l - k);
    }

    @Environment(value=EnvType.CLIENT)
    static class PageContent {
        private static final PageContent EMPTY = new PageContent("", new Position(0, 0), true, new int[]{0}, new Line[]{new Line(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
        private final String pageContent;
        private final Position position;
        private final boolean field_24274;
        private final int[] field_24275;
        private final Line[] lines;
        private final Rect2i[] field_24277;

        public PageContent(String pageContent, Position position, boolean bl, int[] is, Line[] lines, Rect2i[] rect2is) {
            this.pageContent = pageContent;
            this.position = position;
            this.field_24274 = bl;
            this.field_24275 = is;
            this.lines = lines;
            this.field_24277 = rect2is;
        }

        public int method_27602(TextRenderer textRenderer, Position position) {
            int i = position.y / textRenderer.fontHeight;
            if (i < 0) {
                return 0;
            }
            if (i >= this.lines.length) {
                return this.pageContent.length();
            }
            Line line = this.lines[i];
            return this.field_24275[i] + textRenderer.getTextHandler().getTrimmedLength(line.content, position.x, line.style);
        }

        public int method_27601(int i, int j) {
            int o;
            int k = BookEditScreen.method_27591(this.field_24275, i);
            int l = k + j;
            if (0 <= l && l < this.field_24275.length) {
                int m = i - this.field_24275[k];
                int n = this.lines[l].content.length();
                o = this.field_24275[l] + Math.min(m, n);
            } else {
                o = i;
            }
            return o;
        }

        public int method_27600(int i) {
            int j = BookEditScreen.method_27591(this.field_24275, i);
            return this.field_24275[j];
        }

        public int method_27604(int i) {
            int j = BookEditScreen.method_27591(this.field_24275, i);
            return this.field_24275[j] + this.lines[j].content.length();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Line {
        private final Style style;
        private final String content;
        private final Text text;
        private final int x;
        private final int y;

        public Line(Style style, String content, int x, int y) {
            this.style = style;
            this.content = content;
            this.x = x;
            this.y = y;
            this.text = new LiteralText(content).setStyle(style);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Position {
        public final int x;
        public final int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}

