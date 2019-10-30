/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.ListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.PageTurnWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.packet.BookUpdateC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

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
    private int cursorIndex;
    private int highlightTo;
    private long lastClickTime;
    private int lastClickIndex = -1;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private ButtonWidget doneButton;
    private ButtonWidget signButton;
    private ButtonWidget finalizeButton;
    private ButtonWidget cancelButton;
    private final Hand hand;

    public BookEditScreen(PlayerEntity playerEntity, ItemStack itemStack, Hand hand) {
        super(NarratorManager.EMPTY);
        this.player = playerEntity;
        this.itemStack = itemStack;
        this.hand = hand;
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null) {
            ListTag listTag = compoundTag.getList("pages", 8).method_10612();
            for (int i = 0; i < listTag.size(); ++i) {
                this.pages.add(listTag.getString(i));
            }
        }
        if (this.pages.isEmpty()) {
            this.pages.add("");
        }
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
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.signButton = this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, I18n.translate("book.signButton", new Object[0]), buttonWidget -> {
            this.signing = true;
            this.updateButtons();
        }));
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> {
            this.minecraft.openScreen(null);
            this.finalizeBook(false);
        }));
        this.finalizeButton = this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, I18n.translate("book.finalizeButton", new Object[0]), buttonWidget -> {
            if (this.signing) {
                this.finalizeBook(true);
                this.minecraft.openScreen(null);
            }
        }));
        this.cancelButton = this.addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> {
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

    private String stripFromatting(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (c == '\u00a7' || c == '\u007f') continue;
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private void openPreviousPage() {
        if (this.currentPage > 0) {
            --this.currentPage;
            this.highlightTo = this.cursorIndex = 0;
        }
        this.updateButtons();
    }

    private void openNextPage() {
        if (this.currentPage < this.countPages() - 1) {
            ++this.currentPage;
            this.highlightTo = this.cursorIndex = 0;
        } else {
            this.appendNewPage();
            if (this.currentPage < this.countPages() - 1) {
                ++this.currentPage;
            }
            this.highlightTo = this.cursorIndex = 0;
        }
        this.updateButtons();
    }

    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
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

    private void finalizeBook(boolean bl) {
        if (!this.dirty) {
            return;
        }
        this.removeEmptyPages();
        ListTag listTag = new ListTag();
        this.pages.stream().map(StringTag::of).forEach(listTag::add);
        if (!this.pages.isEmpty()) {
            this.itemStack.putSubTag("pages", listTag);
        }
        if (bl) {
            this.itemStack.putSubTag("author", StringTag.of(this.player.getGameProfile().getName()));
            this.itemStack.putSubTag("title", StringTag.of(this.title.trim()));
        }
        this.minecraft.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(this.itemStack, bl, this.hand));
    }

    private void appendNewPage() {
        if (this.countPages() >= 100) {
            return;
        }
        this.pages.add("");
        this.dirty = true;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        if (this.signing) {
            return this.keyPressedSignMode(i, j, k);
        }
        return this.keyPressedEditMode(i, j, k);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (super.charTyped(c, i)) {
            return true;
        }
        if (this.signing) {
            if (this.title.length() < 16 && SharedConstants.isValidChar(c)) {
                this.title = this.title + Character.toString(c);
                this.updateButtons();
                this.dirty = true;
                return true;
            }
            return false;
        }
        if (SharedConstants.isValidChar(c)) {
            this.writeString(Character.toString(c));
            return true;
        }
        return false;
    }

    private boolean keyPressedEditMode(int i, int j, int k) {
        String string = this.getCurrentPageContent();
        if (Screen.isSelectAll(i)) {
            this.highlightTo = 0;
            this.cursorIndex = string.length();
            return true;
        }
        if (Screen.isCopy(i)) {
            this.minecraft.keyboard.setClipboard(this.getHighlightedText());
            return true;
        }
        if (Screen.isPaste(i)) {
            this.writeString(this.stripFromatting(Formatting.strip(this.minecraft.keyboard.getClipboard().replaceAll("\\r", ""))));
            this.highlightTo = this.cursorIndex;
            return true;
        }
        if (Screen.isCut(i)) {
            this.minecraft.keyboard.setClipboard(this.getHighlightedText());
            this.removeHighlightedText();
            return true;
        }
        switch (i) {
            case 259: {
                this.applyBackspaceKey(string);
                return true;
            }
            case 261: {
                this.applyDeleteKey(string);
                return true;
            }
            case 257: 
            case 335: {
                this.writeString("\n");
                return true;
            }
            case 263: {
                this.applyLeftArrowKey(string);
                return true;
            }
            case 262: {
                this.applyRightArrowKey(string);
                return true;
            }
            case 265: {
                this.applyUpArrowKey(string);
                return true;
            }
            case 264: {
                this.applyDownArrowKey(string);
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
                this.moveCursorToTop(string);
                return true;
            }
            case 269: {
                this.moveCursorToBottom(string);
                return true;
            }
        }
        return false;
    }

    private void applyBackspaceKey(String string) {
        if (!string.isEmpty()) {
            if (this.highlightTo != this.cursorIndex) {
                this.removeHighlightedText();
            } else if (this.cursorIndex > 0) {
                String string2 = new StringBuilder(string).deleteCharAt(Math.max(0, this.cursorIndex - 1)).toString();
                this.setPageContent(string2);
                this.highlightTo = this.cursorIndex = Math.max(0, this.cursorIndex - 1);
            }
        }
    }

    private void applyDeleteKey(String string) {
        if (!string.isEmpty()) {
            if (this.highlightTo != this.cursorIndex) {
                this.removeHighlightedText();
            } else if (this.cursorIndex < string.length()) {
                String string2 = new StringBuilder(string).deleteCharAt(Math.max(0, this.cursorIndex)).toString();
                this.setPageContent(string2);
            }
        }
    }

    private void applyLeftArrowKey(String string) {
        int i = this.font.isRightToLeft() ? 1 : -1;
        this.cursorIndex = Screen.hasControlDown() ? this.font.findWordEdge(string, i, this.cursorIndex, true) : Math.max(0, this.cursorIndex + i);
        if (!Screen.hasShiftDown()) {
            this.highlightTo = this.cursorIndex;
        }
    }

    private void applyRightArrowKey(String string) {
        int i = this.font.isRightToLeft() ? -1 : 1;
        this.cursorIndex = Screen.hasControlDown() ? this.font.findWordEdge(string, i, this.cursorIndex, true) : Math.min(string.length(), this.cursorIndex + i);
        if (!Screen.hasShiftDown()) {
            this.highlightTo = this.cursorIndex;
        }
    }

    private void applyUpArrowKey(String string) {
        if (!string.isEmpty()) {
            Position position = this.getCursorPositionForIndex(string, this.cursorIndex);
            if (position.y == 0) {
                this.cursorIndex = 0;
                if (!Screen.hasShiftDown()) {
                    this.highlightTo = this.cursorIndex;
                }
            } else {
                int i = this.getCharacterCountInFrontOfCursor(string, new Position(position.x + this.getCharWidthInString(string, this.cursorIndex) / 3, position.y - this.font.fontHeight));
                if (i >= 0) {
                    this.cursorIndex = i;
                    if (!Screen.hasShiftDown()) {
                        this.highlightTo = this.cursorIndex;
                    }
                }
            }
        }
    }

    private void applyDownArrowKey(String string) {
        if (!string.isEmpty()) {
            Position position = this.getCursorPositionForIndex(string, this.cursorIndex);
            int i = this.font.getStringBoundedHeight(string + "" + (Object)((Object)Formatting.BLACK) + "_", 114);
            if (position.y + this.font.fontHeight == i) {
                this.cursorIndex = string.length();
                if (!Screen.hasShiftDown()) {
                    this.highlightTo = this.cursorIndex;
                }
            } else {
                int j = this.getCharacterCountInFrontOfCursor(string, new Position(position.x + this.getCharWidthInString(string, this.cursorIndex) / 3, position.y + this.font.fontHeight));
                if (j >= 0) {
                    this.cursorIndex = j;
                    if (!Screen.hasShiftDown()) {
                        this.highlightTo = this.cursorIndex;
                    }
                }
            }
        }
    }

    private void moveCursorToTop(String string) {
        this.cursorIndex = this.getCharacterCountInFrontOfCursor(string, new Position(0, this.getCursorPositionForIndex(string, this.cursorIndex).y));
        if (!Screen.hasShiftDown()) {
            this.highlightTo = this.cursorIndex;
        }
    }

    private void moveCursorToBottom(String string) {
        this.cursorIndex = this.getCharacterCountInFrontOfCursor(string, new Position(113, this.getCursorPositionForIndex(string, this.cursorIndex).y));
        if (!Screen.hasShiftDown()) {
            this.highlightTo = this.cursorIndex;
        }
    }

    private void removeHighlightedText() {
        if (this.highlightTo == this.cursorIndex) {
            return;
        }
        String string = this.getCurrentPageContent();
        int i = Math.min(this.cursorIndex, this.highlightTo);
        int j = Math.max(this.cursorIndex, this.highlightTo);
        String string2 = string.substring(0, i) + string.substring(j);
        this.highlightTo = this.cursorIndex = i;
        this.setPageContent(string2);
    }

    private int getCharWidthInString(String string, int i) {
        return (int)this.font.getCharWidth(string.charAt(MathHelper.clamp(i, 0, string.length() - 1)));
    }

    private boolean keyPressedSignMode(int i, int j, int k) {
        switch (i) {
            case 259: {
                if (!this.title.isEmpty()) {
                    this.title = this.title.substring(0, this.title.length() - 1);
                    this.updateButtons();
                }
                return true;
            }
            case 257: 
            case 335: {
                if (!this.title.isEmpty()) {
                    this.finalizeBook(true);
                    this.minecraft.openScreen(null);
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

    private void setPageContent(String string) {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            this.pages.set(this.currentPage, string);
            this.dirty = true;
        }
    }

    private void writeString(String string) {
        if (this.highlightTo != this.cursorIndex) {
            this.removeHighlightedText();
        }
        String string2 = this.getCurrentPageContent();
        this.cursorIndex = MathHelper.clamp(this.cursorIndex, 0, string2.length());
        String string3 = new StringBuilder(string2).insert(this.cursorIndex, string).toString();
        int i = this.font.getStringBoundedHeight(string3 + "" + (Object)((Object)Formatting.BLACK) + "_", 114);
        if (i <= 128 && string3.length() < 1024) {
            this.setPageContent(string3);
            this.highlightTo = this.cursorIndex = Math.min(this.getCurrentPageContent().length(), this.cursorIndex + string.length());
        }
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.setFocused(null);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BookScreen.BOOK_TEXTURE);
        int k = (this.width - 192) / 2;
        int l = 2;
        this.blit(k, 2, 0, 0, 192, 192);
        if (this.signing) {
            String string = this.title;
            string = this.tickCounter / 6 % 2 == 0 ? string + "" + (Object)((Object)Formatting.BLACK) + "_" : string + "" + (Object)((Object)Formatting.GRAY) + "_";
            String string2 = I18n.translate("book.editTitle", new Object[0]);
            int m = this.getStringWidth(string2);
            this.font.draw(string2, k + 36 + (114 - m) / 2, 34.0f, 0);
            int n = this.getStringWidth(string);
            this.font.draw(string, k + 36 + (114 - n) / 2, 50.0f, 0);
            String string3 = I18n.translate("book.byAuthor", this.player.getName().getString());
            int o = this.getStringWidth(string3);
            this.font.draw((Object)((Object)Formatting.DARK_GRAY) + string3, k + 36 + (114 - o) / 2, 60.0f, 0);
            String string4 = I18n.translate("book.finalizeWarning", new Object[0]);
            this.font.drawTrimmed(string4, k + 36, 82, 114, 0);
        } else {
            String string = I18n.translate("book.pageIndicator", this.currentPage + 1, this.countPages());
            String string2 = this.getCurrentPageContent();
            int m = this.getStringWidth(string);
            this.font.draw(string, k - m + 192 - 44, 18.0f, 0);
            this.font.drawTrimmed(string2, k + 36, 32, 114, 0);
            this.drawHighlight(string2);
            if (this.tickCounter / 6 % 2 == 0) {
                Position position = this.getCursorPositionForIndex(string2, this.cursorIndex);
                if (this.font.isRightToLeft()) {
                    this.localizePosition(position);
                    position.x = position.x - 4;
                }
                this.translateRelativePositionToGlPosition(position);
                if (this.cursorIndex < string2.length()) {
                    DrawableHelper.fill(position.x, position.y - 1, position.x + 1, position.y + this.font.fontHeight, -16777216);
                } else {
                    this.font.draw("_", position.x, position.y, 0);
                }
            }
        }
        super.render(i, j, f);
    }

    private int getStringWidth(String string) {
        return this.font.getStringWidth(this.font.isRightToLeft() ? this.font.mirror(string) : string);
    }

    private int getCharacterCountForWidth(String string, int i) {
        return this.font.getCharacterCountForWidth(string, i);
    }

    private String getHighlightedText() {
        String string = this.getCurrentPageContent();
        int i = Math.min(this.cursorIndex, this.highlightTo);
        int j = Math.max(this.cursorIndex, this.highlightTo);
        return string.substring(i, j);
    }

    private void drawHighlight(String string) {
        if (this.highlightTo == this.cursorIndex) {
            return;
        }
        int i = Math.min(this.cursorIndex, this.highlightTo);
        int j = Math.max(this.cursorIndex, this.highlightTo);
        String string2 = string.substring(i, j);
        int k = this.font.findWordEdge(string, 1, j, true);
        String string3 = string.substring(i, k);
        Position position = this.getCursorPositionForIndex(string, i);
        Position position2 = new Position(position.x, position.y + this.font.fontHeight);
        while (!string2.isEmpty()) {
            int l = this.getCharacterCountForWidth(string3, 114 - position.x);
            if (string2.length() <= l) {
                position2.x = position.x + this.getStringWidth(string2);
                this.drawHighlightRect(position, position2);
                break;
            }
            l = Math.min(l, string2.length() - 1);
            String string4 = string2.substring(0, l);
            char c = string2.charAt(l);
            boolean bl = c == ' ' || c == '\n';
            string2 = Formatting.getFormatAtEnd(string4) + string2.substring(l + (bl ? 1 : 0));
            string3 = Formatting.getFormatAtEnd(string4) + string3.substring(l + (bl ? 1 : 0));
            position2.x = position.x + this.getStringWidth(string4 + " ");
            this.drawHighlightRect(position, position2);
            position.x = 0;
            position.y = position.y + this.font.fontHeight;
            position2.y = position2.y + this.font.fontHeight;
        }
    }

    private void drawHighlightRect(Position position, Position position2) {
        Position position3 = new Position(position.x, position.y);
        Position position4 = new Position(position2.x, position2.y);
        if (this.font.isRightToLeft()) {
            this.localizePosition(position3);
            this.localizePosition(position4);
            int i = position4.x;
            position4.x = position3.x;
            position3.x = i;
        }
        this.translateRelativePositionToGlPosition(position3);
        this.translateRelativePositionToGlPosition(position4);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder.begin(7, VertexFormats.POSITION);
        bufferBuilder.vertex(position3.x, position4.y, 0.0).next();
        bufferBuilder.vertex(position4.x, position4.y, 0.0).next();
        bufferBuilder.vertex(position4.x, position3.y, 0.0).next();
        bufferBuilder.vertex(position3.x, position3.y, 0.0).next();
        tessellator.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    private Position getCursorPositionForIndex(String string, int i) {
        Position position = new Position();
        int j = 0;
        int k = 0;
        String string2 = string;
        while (!string2.isEmpty()) {
            String string3;
            int l = this.getCharacterCountForWidth(string2, 114);
            if (string2.length() <= l) {
                string3 = string2.substring(0, Math.min(Math.max(i - k, 0), string2.length()));
                position.x = position.x + this.getStringWidth(string3);
                break;
            }
            string3 = string2.substring(0, l);
            char c = string2.charAt(l);
            boolean bl = c == ' ' || c == '\n';
            string2 = Formatting.getFormatAtEnd(string3) + string2.substring(l + (bl ? 1 : 0));
            if ((j += string3.length() + (bl ? 1 : 0)) - 1 >= i) {
                String string4 = string3.substring(0, Math.min(Math.max(i - k, 0), string3.length()));
                position.x = position.x + this.getStringWidth(string4);
                break;
            }
            position.y = position.y + this.font.fontHeight;
            k = j;
        }
        return position;
    }

    private void localizePosition(Position position) {
        if (this.font.isRightToLeft()) {
            position.x = 114 - position.x;
        }
    }

    private void translateGlPositionToRelativePosition(Position position) {
        position.x = position.x - (this.width - 192) / 2 - 36;
        position.y = position.y - 32;
    }

    private void translateRelativePositionToGlPosition(Position position) {
        position.x = position.x + (this.width - 192) / 2 + 36;
        position.y = position.y + 32;
    }

    private int getCharacterCountForStringWidth(String string, int i) {
        if (i < 0) {
            return 0;
        }
        float f = 0.0f;
        boolean bl = false;
        String string2 = string + " ";
        for (int j = 0; j < string2.length(); ++j) {
            char c = string2.charAt(j);
            float g = this.font.getCharWidth(c);
            if (c == '\u00a7' && j < string2.length() - 1) {
                if ((c = string2.charAt(++j)) == 'l' || c == 'L') {
                    bl = true;
                } else if (c == 'r' || c == 'R') {
                    bl = false;
                }
                g = 0.0f;
            }
            float h = f;
            f += g;
            if (bl && g > 0.0f) {
                f += 1.0f;
            }
            if (!((float)i >= h) || !((float)i < f)) continue;
            return j;
        }
        if ((float)i >= f) {
            return string2.length() - 1;
        }
        return -1;
    }

    private int getCharacterCountInFrontOfCursor(String string, Position position) {
        int i = 16 * this.font.fontHeight;
        if (position.y > i) {
            return -1;
        }
        int j = Integer.MIN_VALUE;
        int k = this.font.fontHeight;
        int l = 0;
        String string2 = string;
        while (!string2.isEmpty() && j < i) {
            int m = this.getCharacterCountForWidth(string2, 114);
            if (m < string2.length()) {
                String string3 = string2.substring(0, m);
                if (position.y >= j && position.y < k) {
                    int n = this.getCharacterCountForStringWidth(string3, position.x);
                    return n < 0 ? -1 : l + n;
                }
                char c = string2.charAt(m);
                boolean bl = c == ' ' || c == '\n';
                string2 = Formatting.getFormatAtEnd(string3) + string2.substring(m + (bl ? 1 : 0));
                l += string3.length() + (bl ? 1 : 0);
            } else if (position.y >= j && position.y < k) {
                int o = this.getCharacterCountForStringWidth(string2, position.x);
                return o < 0 ? -1 : l + o;
            }
            j = k;
            k += this.font.fontHeight;
        }
        return string.length();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (i == 0) {
            long l = Util.getMeasuringTimeMs();
            String string = this.getCurrentPageContent();
            if (!string.isEmpty()) {
                Position position = new Position((int)d, (int)e);
                this.translateGlPositionToRelativePosition(position);
                this.localizePosition(position);
                int j = this.getCharacterCountInFrontOfCursor(string, position);
                if (j >= 0) {
                    if (j == this.lastClickIndex && l - this.lastClickTime < 250L) {
                        if (this.highlightTo == this.cursorIndex) {
                            this.highlightTo = this.font.findWordEdge(string, -1, j, false);
                            this.cursorIndex = this.font.findWordEdge(string, 1, j, false);
                        } else {
                            this.highlightTo = 0;
                            this.cursorIndex = this.getCurrentPageContent().length();
                        }
                    } else {
                        this.cursorIndex = j;
                        if (!Screen.hasShiftDown()) {
                            this.highlightTo = this.cursorIndex;
                        }
                    }
                }
                this.lastClickIndex = j;
            }
            this.lastClickTime = l;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (i == 0 && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            String string = this.pages.get(this.currentPage);
            Position position = new Position((int)d, (int)e);
            this.translateGlPositionToRelativePosition(position);
            this.localizePosition(position);
            int j = this.getCharacterCountInFrontOfCursor(string, position);
            if (j >= 0) {
                this.cursorIndex = j;
            }
        }
        return super.mouseDragged(d, e, i, f, g);
    }

    @Environment(value=EnvType.CLIENT)
    class Position {
        private int x;
        private int y;

        Position() {
        }

        Position(int i, int j) {
            this.x = i;
            this.y = j;
        }
    }
}

