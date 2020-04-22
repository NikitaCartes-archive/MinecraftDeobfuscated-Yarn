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
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
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
    private class_5233 field_24268 = class_5233.method_27599();

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
        this.method_27577();
        this.client.keyboard.enableRepeatEvents(true);
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
        this.method_27577();
    }

    private void openNextPage() {
        if (this.currentPage < this.countPages() - 1) {
            ++this.currentPage;
            this.field_24269.method_27566();
        } else {
            this.appendNewPage();
            if (this.currentPage < this.countPages() - 1) {
                ++this.currentPage;
            }
            this.field_24269.method_27566();
        }
        this.updateButtons();
        this.method_27577();
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
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
        this.client.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(this.itemStack, signBook, this.hand));
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
            this.method_27577();
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
            this.method_27577();
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
        int k = this.method_27576().method_27601(j, i);
        this.field_24269.method_27560(k, Screen.hasShiftDown());
    }

    private void moveCursorToTop() {
        int i = this.field_24269.getSelectionStart();
        int j = this.method_27576().method_27600(i);
        this.field_24269.method_27560(j, Screen.hasShiftDown());
    }

    private void moveCursorToBottom() {
        class_5233 lv = this.method_27576();
        int i = this.field_24269.getSelectionStart();
        int j = lv.method_27604(i);
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
            this.method_27577();
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
            String string = this.title;
            string = this.tickCounter / 6 % 2 == 0 ? string + "" + (Object)((Object)Formatting.BLACK) + "_" : string + "" + (Object)((Object)Formatting.GRAY) + "_";
            String string2 = I18n.translate("book.editTitle", new Object[0]);
            int k = this.getStringWidth(string2);
            this.textRenderer.draw(matrices, string2, (float)(i + 36 + (114 - k) / 2), 34.0f, 0);
            int l = this.getStringWidth(string);
            this.textRenderer.draw(matrices, string, (float)(i + 36 + (114 - l) / 2), 50.0f, 0);
            String string3 = I18n.translate("book.byAuthor", this.player.getName().getString());
            int m = this.getStringWidth(string3);
            this.textRenderer.draw(matrices, (Object)((Object)Formatting.DARK_GRAY) + string3, (float)(i + 36 + (114 - m) / 2), 60.0f, 0);
            this.textRenderer.drawTrimmed(new TranslatableText("book.finalizeWarning"), i + 36, 82, 114, 0);
        } else {
            String string = I18n.translate("book.pageIndicator", this.currentPage + 1, this.countPages());
            int n = this.getStringWidth(string);
            this.textRenderer.draw(matrices, string, (float)(i - n + 192 - 44), 18.0f, 0);
            class_5233 lv = this.method_27576();
            for (Position position : lv.field_24276) {
                this.textRenderer.draw(matrices, position.field_24280, (float)position.x, (float)position.y, -16777216);
            }
            this.method_27588(lv.field_24277);
            this.method_27581(matrices, lv.field_24273, lv.field_24274);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void method_27581(MatrixStack matrixStack, class_5234 arg, boolean bl) {
        if (this.tickCounter / 6 % 2 == 0) {
            arg = this.method_27590(arg);
            if (!bl) {
                DrawableHelper.fill(matrixStack, arg.field_24281, arg.field_24282 - 1, arg.field_24281 + 1, arg.field_24282 + this.textRenderer.fontHeight, -16777216);
            } else {
                this.textRenderer.draw(matrixStack, "_", (float)arg.field_24281, (float)arg.field_24282, 0);
            }
        }
    }

    private int getStringWidth(String text) {
        return this.textRenderer.getWidth(this.textRenderer.isRightToLeft() ? this.textRenderer.mirror(text) : text);
    }

    private void method_27588(Rect2i[] rect2is) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder.begin(7, VertexFormats.POSITION);
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

    private class_5234 method_27582(class_5234 arg) {
        return new class_5234(arg.field_24281 - (this.width - 192) / 2 - 36, arg.field_24282 - 32);
    }

    private class_5234 method_27590(class_5234 arg) {
        return new class_5234(arg.field_24281 + (this.width - 192) / 2 + 36, arg.field_24282 + 32);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            long l = Util.getMeasuringTimeMs();
            class_5233 lv = this.method_27576();
            int i = lv.method_27602(this.textRenderer, this.method_27582(new class_5234((int)mouseX, (int)mouseY)));
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
                this.method_27577();
            }
            this.lastClickIndex = i;
            this.lastClickTime = l;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void method_27589(int i) {
        String string = this.getCurrentPageContent();
        this.field_24269.method_27548(TextHandler.moveCursorByWords(string, -1, i, false), TextHandler.moveCursorByWords(string, 1, i, false));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) {
            class_5233 lv = this.method_27576();
            int i = lv.method_27602(this.textRenderer, this.method_27582(new class_5234((int)mouseX, (int)mouseY)));
            this.field_24269.method_27560(i, true);
            this.method_27577();
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private class_5233 method_27576() {
        if (this.field_24268 == null) {
            this.field_24268 = this.method_27578();
        }
        return this.field_24268;
    }

    private void method_27577() {
        this.field_24268 = null;
    }

    private class_5233 method_27578() {
        int l;
        class_5234 lv;
        boolean bl;
        String string = this.getCurrentPageContent();
        if (string.isEmpty()) {
            return class_5233.field_24271;
        }
        String string2 = this.textRenderer.isRightToLeft() ? this.textRenderer.mirror(string) : string;
        int i2 = this.field_24269.getSelectionStart();
        int j2 = this.field_24269.getSelectionEnd();
        IntArrayList intList = new IntArrayList();
        ArrayList list = Lists.newArrayList();
        MutableInt mutableInt = new MutableInt();
        MutableBoolean mutableBoolean = new MutableBoolean();
        TextHandler textHandler = this.textRenderer.getTextHandler();
        textHandler.wrapLines(string2, 114, Style.EMPTY, true, (style, i, j) -> {
            int k = mutableInt.getAndIncrement();
            String string2 = string2.substring(i, j);
            mutableBoolean.setValue(string2.endsWith("\n"));
            String string3 = StringUtils.stripEnd(string2, " \n");
            int l = k * this.textRenderer.fontHeight;
            class_5234 lv = this.method_27590(new class_5234(0, l));
            intList.add(i);
            list.add(new Position(style, string3, lv.field_24281, lv.field_24282));
        });
        int[] is = intList.toIntArray();
        boolean bl2 = bl = i2 == string2.length();
        if (bl && mutableBoolean.isTrue()) {
            lv = new class_5234(0, list.size() * this.textRenderer.fontHeight);
        } else {
            int k = BookEditScreen.method_27591(is, i2);
            l = this.textRenderer.getWidth(string2.substring(is[k], i2));
            lv = new class_5234(l, k * this.textRenderer.fontHeight);
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
                list2.add(this.method_27585(string2, textHandler, l, m, p, q));
            } else {
                int p = n + 1 > is.length ? string2.length() : is[n + 1];
                list2.add(this.method_27585(string2, textHandler, l, p, n * this.textRenderer.fontHeight, is[n]));
                for (int q = n + 1; q < o; ++q) {
                    int r = q * this.textRenderer.fontHeight;
                    String string3 = string2.substring(is[q], is[q + 1]);
                    int s = (int)textHandler.getWidth(string3);
                    list2.add(this.method_27583(new class_5234(0, r), new class_5234(s, r + this.textRenderer.fontHeight)));
                }
                list2.add(this.method_27585(string2, textHandler, is[o], m, o * this.textRenderer.fontHeight, is[o]));
            }
        }
        return new class_5233(string2, lv, bl, is, list.toArray(new Position[0]), list2.toArray(new Rect2i[0]));
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
        class_5234 lv = new class_5234((int)textHandler.getWidth(string2), k);
        class_5234 lv2 = new class_5234((int)textHandler.getWidth(string3), k + this.textRenderer.fontHeight);
        return this.method_27583(lv, lv2);
    }

    private Rect2i method_27583(class_5234 arg, class_5234 arg2) {
        class_5234 lv = this.method_27590(arg);
        class_5234 lv2 = this.method_27590(arg2);
        int i = Math.min(lv.field_24281, lv2.field_24281);
        int j = Math.max(lv.field_24281, lv2.field_24281);
        int k = Math.min(lv.field_24282, lv2.field_24282);
        int l = Math.max(lv.field_24282, lv2.field_24282);
        return new Rect2i(i, k, j - i, l - k);
    }

    @Environment(value=EnvType.CLIENT)
    static class class_5233 {
        private static final class_5233 field_24271 = new class_5233("", new class_5234(0, 0), true, new int[]{0}, new Position[]{new Position(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
        private final String field_24272;
        private final class_5234 field_24273;
        private final boolean field_24274;
        private final int[] field_24275;
        private final Position[] field_24276;
        private final Rect2i[] field_24277;

        public class_5233(String string, class_5234 arg, boolean bl, int[] is, Position[] positions, Rect2i[] rect2is) {
            this.field_24272 = string;
            this.field_24273 = arg;
            this.field_24274 = bl;
            this.field_24275 = is;
            this.field_24276 = positions;
            this.field_24277 = rect2is;
        }

        public int method_27602(TextRenderer textRenderer, class_5234 arg) {
            int i = arg.field_24282 / textRenderer.fontHeight;
            if (i < 0) {
                return 0;
            }
            if (i >= this.field_24276.length) {
                return this.field_24272.length();
            }
            Position position = this.field_24276[i];
            return this.field_24275[i] + textRenderer.getTextHandler().getTrimmedLength(position.field_24279, arg.field_24281, position.field_24278);
        }

        public int method_27601(int i, int j) {
            int o;
            int k = BookEditScreen.method_27591(this.field_24275, i);
            int l = k + j;
            if (0 <= l && l < this.field_24275.length) {
                int m = i - this.field_24275[k];
                int n = this.field_24276[l].field_24279.length();
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
            return this.field_24275[j] + this.field_24276[j].field_24279.length();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Position {
        private final Style field_24278;
        private final String field_24279;
        private final Text field_24280;
        private final int x;
        private final int y;

        public Position(Style style, String string, int i, int j) {
            this.field_24278 = style;
            this.field_24279 = string;
            this.x = i;
            this.y = j;
            this.field_24280 = new LiteralText(string).setStyle(style);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class class_5234 {
        public final int field_24281;
        public final int field_24282;

        class_5234(int i, int j) {
            this.field_24281 = i;
            this.field_24282 = j;
        }
    }
}

