/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TextFieldWidget
extends AbstractButtonWidget
implements Drawable,
Element {
    private final TextRenderer textRenderer;
    private String text = "";
    private int maxLength = 32;
    private int focusedTicks;
    private boolean focused = true;
    private boolean focusUnlocked = true;
    private boolean editable = true;
    private boolean selecting;
    private int firstCharacter;
    private int selectionStart;
    private int selectionEnd;
    private int editableColor = 0xE0E0E0;
    private int uneditableColor = 0x707070;
    private String suggestion;
    private Consumer<String> changedListener;
    private Predicate<String> textPredicate = Predicates.alwaysTrue();
    private BiFunction<String, Integer, String> renderTextProvider = (string, integer) -> string;

    public TextFieldWidget(TextRenderer textRenderer, int i, int j, int k, int l, String string) {
        this(textRenderer, i, j, k, l, null, string);
    }

    public TextFieldWidget(TextRenderer textRenderer, int i, int j, int k, int l, @Nullable TextFieldWidget textFieldWidget, String string2) {
        super(i, j, k, l, string2);
        this.textRenderer = textRenderer;
        if (textFieldWidget != null) {
            this.setText(textFieldWidget.getText());
        }
    }

    public void setChangedListener(Consumer<String> consumer) {
        this.changedListener = consumer;
    }

    public void setRenderTextProvider(BiFunction<String, Integer, String> biFunction) {
        this.renderTextProvider = biFunction;
    }

    public void tick() {
        ++this.focusedTicks;
    }

    @Override
    protected String getNarrationMessage() {
        String string = this.getMessage();
        if (string.isEmpty()) {
            return "";
        }
        return I18n.translate("gui.narrate.editBox", string, this.text);
    }

    public void setText(String string) {
        if (!this.textPredicate.test(string)) {
            return;
        }
        this.text = string.length() > this.maxLength ? string.substring(0, this.maxLength) : string;
        this.setCursorToEnd();
        this.setSelectionEnd(this.selectionStart);
        this.onChanged(string);
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i = this.selectionStart < this.selectionEnd ? this.selectionStart : this.selectionEnd;
        int j = this.selectionStart < this.selectionEnd ? this.selectionEnd : this.selectionStart;
        return this.text.substring(i, j);
    }

    public void setTextPredicate(Predicate<String> predicate) {
        this.textPredicate = predicate;
    }

    public void write(String string) {
        int l;
        String string2 = "";
        String string3 = SharedConstants.stripInvalidChars(string);
        int i = this.selectionStart < this.selectionEnd ? this.selectionStart : this.selectionEnd;
        int j = this.selectionStart < this.selectionEnd ? this.selectionEnd : this.selectionStart;
        int k = this.maxLength - this.text.length() - (i - j);
        if (!this.text.isEmpty()) {
            string2 = string2 + this.text.substring(0, i);
        }
        if (k < string3.length()) {
            string2 = string2 + string3.substring(0, k);
            l = k;
        } else {
            string2 = string2 + string3;
            l = string3.length();
        }
        if (!this.text.isEmpty() && j < this.text.length()) {
            string2 = string2 + this.text.substring(j);
        }
        if (!this.textPredicate.test(string2)) {
            return;
        }
        this.text = string2;
        this.setSelectionStart(i + l);
        this.setSelectionEnd(this.selectionStart);
        this.onChanged(this.text);
    }

    private void onChanged(String string) {
        if (this.changedListener != null) {
            this.changedListener.accept(string);
        }
        this.nextNarration = Util.getMeasuringTimeMs() + 500L;
    }

    private void erase(int i) {
        if (Screen.hasControlDown()) {
            this.eraseWords(i);
        } else {
            this.eraseCharacters(i);
        }
    }

    public void eraseWords(int i) {
        if (this.text.isEmpty()) {
            return;
        }
        if (this.selectionEnd != this.selectionStart) {
            this.write("");
            return;
        }
        this.eraseCharacters(this.getWordSkipPosition(i) - this.selectionStart);
    }

    public void eraseCharacters(int i) {
        if (this.text.isEmpty()) {
            return;
        }
        if (this.selectionEnd != this.selectionStart) {
            this.write("");
            return;
        }
        boolean bl = i < 0;
        int j = bl ? this.selectionStart + i : this.selectionStart;
        int k = bl ? this.selectionStart : this.selectionStart + i;
        String string = "";
        if (j >= 0) {
            string = this.text.substring(0, j);
        }
        if (k < this.text.length()) {
            string = string + this.text.substring(k);
        }
        if (!this.textPredicate.test(string)) {
            return;
        }
        this.text = string;
        if (bl) {
            this.moveCursor(i);
        }
        this.onChanged(this.text);
    }

    public int getWordSkipPosition(int i) {
        return this.getWordSkipPosition(i, this.getCursor());
    }

    private int getWordSkipPosition(int i, int j) {
        return this.getWordSkipPosition(i, j, true);
    }

    private int getWordSkipPosition(int i, int j, boolean bl) {
        int k = j;
        boolean bl2 = i < 0;
        int l = Math.abs(i);
        for (int m = 0; m < l; ++m) {
            if (bl2) {
                while (bl && k > 0 && this.text.charAt(k - 1) == ' ') {
                    --k;
                }
                while (k > 0 && this.text.charAt(k - 1) != ' ') {
                    --k;
                }
                continue;
            }
            int n = this.text.length();
            if ((k = this.text.indexOf(32, k)) == -1) {
                k = n;
                continue;
            }
            while (bl && k < n && this.text.charAt(k) == ' ') {
                ++k;
            }
        }
        return k;
    }

    public void moveCursor(int i) {
        this.setCursor(this.selectionStart + i);
    }

    public void setCursor(int i) {
        this.setSelectionStart(i);
        if (!this.selecting) {
            this.setSelectionEnd(this.selectionStart);
        }
        this.onChanged(this.text);
    }

    public void setSelectionStart(int i) {
        this.selectionStart = MathHelper.clamp(i, 0, this.text.length());
    }

    public void setCursorToStart() {
        this.setCursor(0);
    }

    public void setCursorToEnd() {
        this.setCursor(this.text.length());
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (!this.isActive()) {
            return false;
        }
        this.selecting = Screen.hasShiftDown();
        if (Screen.isSelectAll(i)) {
            this.setCursorToEnd();
            this.setSelectionEnd(0);
            return true;
        }
        if (Screen.isCopy(i)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            return true;
        }
        if (Screen.isPaste(i)) {
            if (this.editable) {
                this.write(MinecraftClient.getInstance().keyboard.getClipboard());
            }
            return true;
        }
        if (Screen.isCut(i)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            if (this.editable) {
                this.write("");
            }
            return true;
        }
        switch (i) {
            case 263: {
                if (Screen.hasControlDown()) {
                    this.setCursor(this.getWordSkipPosition(-1));
                } else {
                    this.moveCursor(-1);
                }
                return true;
            }
            case 262: {
                if (Screen.hasControlDown()) {
                    this.setCursor(this.getWordSkipPosition(1));
                } else {
                    this.moveCursor(1);
                }
                return true;
            }
            case 259: {
                if (this.editable) {
                    this.selecting = false;
                    this.erase(-1);
                    this.selecting = Screen.hasShiftDown();
                }
                return true;
            }
            case 261: {
                if (this.editable) {
                    this.selecting = false;
                    this.erase(1);
                    this.selecting = Screen.hasShiftDown();
                }
                return true;
            }
            case 268: {
                this.setCursorToStart();
                return true;
            }
            case 269: {
                this.setCursorToEnd();
                return true;
            }
        }
        return false;
    }

    public boolean isActive() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (!this.isActive()) {
            return false;
        }
        if (SharedConstants.isValidChar(c)) {
            if (this.editable) {
                this.write(Character.toString(c));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        boolean bl;
        if (!this.isVisible()) {
            return false;
        }
        boolean bl2 = bl = d >= (double)this.x && d < (double)(this.x + this.width) && e >= (double)this.y && e < (double)(this.y + this.height);
        if (this.focusUnlocked) {
            this.setSelected(bl);
        }
        if (this.isFocused() && bl && i == 0) {
            int j = MathHelper.floor(d) - this.x;
            if (this.focused) {
                j -= 4;
            }
            String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacter), this.getInnerWidth());
            this.setCursor(this.textRenderer.trimToWidth(string, j).length() + this.firstCharacter);
            return true;
        }
        return false;
    }

    public void setSelected(boolean bl) {
        super.setFocused(bl);
    }

    @Override
    public void renderButton(int i, int j, float f) {
        if (!this.isVisible()) {
            return;
        }
        if (this.hasBorder()) {
            TextFieldWidget.fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
            TextFieldWidget.fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
        }
        int k = this.editable ? this.editableColor : this.uneditableColor;
        int l = this.selectionStart - this.firstCharacter;
        int m = this.selectionEnd - this.firstCharacter;
        String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacter), this.getInnerWidth());
        boolean bl = l >= 0 && l <= string.length();
        boolean bl2 = this.isFocused() && this.focusedTicks / 6 % 2 == 0 && bl;
        int n = this.focused ? this.x + 4 : this.x;
        int o = this.focused ? this.y + (this.height - 8) / 2 : this.y;
        int p = n;
        if (m > string.length()) {
            m = string.length();
        }
        if (!string.isEmpty()) {
            String string2 = bl ? string.substring(0, l) : string;
            p = this.textRenderer.drawWithShadow(this.renderTextProvider.apply(string2, this.firstCharacter), p, o, k);
        }
        boolean bl3 = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
        int q = p;
        if (!bl) {
            q = l > 0 ? n + this.width : n;
        } else if (bl3) {
            --q;
            --p;
        }
        if (!string.isEmpty() && bl && l < string.length()) {
            this.textRenderer.drawWithShadow(this.renderTextProvider.apply(string.substring(l), this.selectionStart), p, o, k);
        }
        if (!bl3 && this.suggestion != null) {
            this.textRenderer.drawWithShadow(this.suggestion, q - 1, o, -8355712);
        }
        if (bl2) {
            if (bl3) {
                DrawableHelper.fill(q, o - 1, q + 1, o + 1 + this.textRenderer.fontHeight, -3092272);
            } else {
                this.textRenderer.drawWithShadow("_", q, o, k);
            }
        }
        if (m != l) {
            int r = n + this.textRenderer.getStringWidth(string.substring(0, m));
            this.drawSelectionHighlight(q, o - 1, r - 1, o + 1 + this.textRenderer.fontHeight);
        }
    }

    private void drawSelectionHighlight(int i, int j, int k, int l) {
        int m;
        if (i < k) {
            m = i;
            i = k;
            k = m;
        }
        if (j < l) {
            m = j;
            j = l;
            l = m;
        }
        if (k > this.x + this.width) {
            k = this.x + this.width;
        }
        if (i > this.x + this.width) {
            i = this.x + this.width;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder.begin(7, VertexFormats.POSITION);
        bufferBuilder.vertex(i, l, 0.0).next();
        bufferBuilder.vertex(k, l, 0.0).next();
        bufferBuilder.vertex(k, j, 0.0).next();
        bufferBuilder.vertex(i, j, 0.0).next();
        tessellator.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    public void setMaxLength(int i) {
        this.maxLength = i;
        if (this.text.length() > i) {
            this.text = this.text.substring(0, i);
            this.onChanged(this.text);
        }
    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursor() {
        return this.selectionStart;
    }

    private boolean hasBorder() {
        return this.focused;
    }

    public void setHasBorder(boolean bl) {
        this.focused = bl;
    }

    public void setEditableColor(int i) {
        this.editableColor = i;
    }

    public void setUneditableColor(int i) {
        this.uneditableColor = i;
    }

    @Override
    public boolean changeFocus(boolean bl) {
        if (!this.visible || !this.editable) {
            return false;
        }
        return super.changeFocus(bl);
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return this.visible && d >= (double)this.x && d < (double)(this.x + this.width) && e >= (double)this.y && e < (double)(this.y + this.height);
    }

    @Override
    protected void onFocusedChanged(boolean bl) {
        if (bl) {
            this.focusedTicks = 0;
        }
    }

    private boolean isEditable() {
        return this.editable;
    }

    public void setEditable(boolean bl) {
        this.editable = bl;
    }

    public int getInnerWidth() {
        return this.hasBorder() ? this.width - 8 : this.width;
    }

    public void setSelectionEnd(int i) {
        int j = this.text.length();
        this.selectionEnd = MathHelper.clamp(i, 0, j);
        if (this.textRenderer != null) {
            if (this.firstCharacter > j) {
                this.firstCharacter = j;
            }
            int k = this.getInnerWidth();
            String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacter), k);
            int l = string.length() + this.firstCharacter;
            if (this.selectionEnd == this.firstCharacter) {
                this.firstCharacter -= this.textRenderer.trimToWidth(this.text, k, true).length();
            }
            if (this.selectionEnd > l) {
                this.firstCharacter += this.selectionEnd - l;
            } else if (this.selectionEnd <= this.firstCharacter) {
                this.firstCharacter -= this.firstCharacter - this.selectionEnd;
            }
            this.firstCharacter = MathHelper.clamp(this.firstCharacter, 0, j);
        }
    }

    public void setFocusUnlocked(boolean bl) {
        this.focusUnlocked = bl;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean bl) {
        this.visible = bl;
    }

    public void setSuggestion(@Nullable String string) {
        this.suggestion = string;
    }

    public int getCharacterX(int i) {
        if (i > this.text.length()) {
            return this.x;
        }
        return this.x + this.textRenderer.getStringWidth(this.text.substring(0, i));
    }

    public void setX(int i) {
        this.x = i;
    }
}

