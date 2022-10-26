/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CursorMovement;
import net.minecraft.text.Style;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;

/**
 * A multiline edit box with support for basic keyboard shortcuts.
 * This should not be used directly; {@link net.minecraft.client.gui.widget.EditBoxWidget}
 * provides the GUI for the edit box.
 */
@Environment(value=EnvType.CLIENT)
public class EditBox {
    /**
     * A constant denoting that the edit box accepts unlimited amount of text. Edit box
     * widgets with such edit boxes do not show the current text length indicator.
     */
    public static final int UNLIMITED_LENGTH = Integer.MAX_VALUE;
    private static final int CURSOR_WIDTH = 2;
    private final TextRenderer textRenderer;
    private final List<Substring> lines = Lists.newArrayList();
    private String text;
    private int cursor;
    private int selectionEnd;
    private boolean selecting;
    private int maxLength = Integer.MAX_VALUE;
    private final int width;
    private Consumer<String> changeListener = text -> {};
    private Runnable cursorChangeListener = () -> {};

    public EditBox(TextRenderer textRenderer, int width) {
        this.textRenderer = textRenderer;
        this.width = width;
        this.setText("");
    }

    /**
     * {@return the maximum length of the edit box text in characters}
     * 
     * <p>If this equals {@link #UNLIMITED_LENGTH}, the edit box does not have a
     * length limit. Edit box widgets with such edit boxes do not show the current
     * text length indicator.
     * 
     * @see #setMaxLength
     * @see #hasMaxLength
     */
    public int getMaxLength() {
        return this.maxLength;
    }

    /**
     * Sets the maximum length of the edit box text in characters.
     * 
     * <p>If {@code maxLength} equals {@link #UNLIMITED_LENGTH}, the edit box does not
     * have a length limit. Edit box widgets with such edit boxes do not show the current
     * text length indicator.
     * 
     * @throws IllegalArgumentException if {@code maxLength} is negative
     * @see #getMaxLength
     * @see #hasMaxLength
     */
    public void setMaxLength(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("Character limit cannot be negative");
        }
        this.maxLength = maxLength;
    }

    /**
     * {@return whether the edit box has a maximum length limit}
     * 
     * <p>Edit box widgets with edit boxes without a length limit do not
     * show the current text length indicator.
     * 
     * @see #getMaxLength
     * @see #setMaxLength
     */
    public boolean hasMaxLength() {
        return this.maxLength != Integer.MAX_VALUE;
    }

    /**
     * Sets the change listener that is called every time the text changes.
     * 
     * @param changeListener the listener that takes the new text of the edit box
     */
    public void setChangeListener(Consumer<String> changeListener) {
        this.changeListener = changeListener;
    }

    /**
     * Sets the cursor change listener that is called every time the cursor position changes.
     */
    public void setCursorChangeListener(Runnable cursorChangeListener) {
        this.cursorChangeListener = cursorChangeListener;
    }

    /**
     * Sets the text of the edit box and moves the cursor to the end of the edit box.
     */
    public void setText(String text) {
        this.text = this.truncateForReplacement(text);
        this.selectionEnd = this.cursor = this.text.length();
        this.onChange();
    }

    /**
     * {@return the text of the edit box}
     */
    public String getText() {
        return this.text;
    }

    /**
     * Replaces the current selection with {@code string}. If there is no
     * selection, this inserts the string at the cursor position. This removes
     * {@linkplain net.minecraft.SharedConstants#isValidChar invalid characters} and truncates
     * the passed string if necessary.
     */
    public void replaceSelection(String string) {
        if (string.isEmpty() && !this.hasSelection()) {
            return;
        }
        String string2 = this.truncate(SharedConstants.stripInvalidChars(string, true));
        Substring substring = this.getSelection();
        this.text = new StringBuilder(this.text).replace(substring.beginIndex, substring.endIndex, string2).toString();
        this.selectionEnd = this.cursor = substring.beginIndex + string2.length();
        this.onChange();
    }

    /**
     * Deletes the selected text, or {@code offset} characters of text from the cursor position
     * if there is no selection. If the offset is negative, the characters before the cursor
     * will be removed, and vice versa.
     */
    public void delete(int offset) {
        if (!this.hasSelection()) {
            this.selectionEnd = MathHelper.clamp(this.cursor + offset, 0, this.text.length());
        }
        this.replaceSelection("");
    }

    /**
     * {@return the cursor position}
     */
    public int getCursor() {
        return this.cursor;
    }

    /**
     * Sets whether the edit box is currently selecting.
     * 
     * <p>If using the widget, this is done by dragging or holding down Shift and clicking.
     */
    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    /**
     * {@return the current selection}
     */
    public Substring getSelection() {
        return new Substring(Math.min(this.selectionEnd, this.cursor), Math.max(this.selectionEnd, this.cursor));
    }

    /**
     * {@return the number of total lines in the edit box}
     */
    public int getLineCount() {
        return this.lines.size();
    }

    /**
     * {@return the line index that the cursor is located at}
     */
    public int getCurrentLineIndex() {
        for (int i = 0; i < this.lines.size(); ++i) {
            Substring substring = this.lines.get(i);
            if (this.cursor < substring.beginIndex || this.cursor > substring.endIndex) continue;
            return i;
        }
        return -1;
    }

    /**
     * {@return the line with index {@code index}}
     */
    public Substring getLine(int index) {
        return this.lines.get(MathHelper.clamp(index, 0, this.lines.size() - 1));
    }

    /**
     * Moves the cursor by {@code amount} characters.
     * 
     * @apiNote See {@link CursorMovement} for the types of the movement.
     */
    public void moveCursor(CursorMovement movement, int amount) {
        switch (movement) {
            case ABSOLUTE: {
                this.cursor = amount;
                break;
            }
            case RELATIVE: {
                this.cursor += amount;
                break;
            }
            case END: {
                this.cursor = this.text.length() + amount;
            }
        }
        this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
        this.cursorChangeListener.run();
        if (!this.selecting) {
            this.selectionEnd = this.cursor;
        }
    }

    /**
     * Moves the cursor by {@code offset} lines. This method attempts to keep the
     * relative position within the line the same. Does nothing if {@code offset} is zero.
     */
    public void moveCursorLine(int offset) {
        if (offset == 0) {
            return;
        }
        int i = this.textRenderer.getWidth(this.text.substring(this.getCurrentLine().beginIndex, this.cursor)) + 2;
        Substring substring = this.getOffsetLine(offset);
        int j = this.textRenderer.trimToWidth(this.text.substring(substring.beginIndex, substring.endIndex), i).length();
        this.moveCursor(CursorMovement.ABSOLUTE, substring.beginIndex + j);
    }

    /**
     * Moves the cursor to the specified position relative to the edit box.
     */
    public void moveCursor(double x, double y) {
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y / (double)this.textRenderer.fontHeight);
        Substring substring = this.lines.get(MathHelper.clamp(j, 0, this.lines.size() - 1));
        int k = this.textRenderer.trimToWidth(this.text.substring(substring.beginIndex, substring.endIndex), i).length();
        this.moveCursor(CursorMovement.ABSOLUTE, substring.beginIndex + k);
    }

    /**
     * Handles the special keys, such as copy, cut, linebreak, and cursor movements.
     */
    public boolean handleSpecialKey(int keyCode) {
        this.selecting = Screen.hasShiftDown();
        if (Screen.isSelectAll(keyCode)) {
            this.cursor = this.text.length();
            this.selectionEnd = 0;
            return true;
        }
        if (Screen.isCopy(keyCode)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            return true;
        }
        if (Screen.isPaste(keyCode)) {
            this.replaceSelection(MinecraftClient.getInstance().keyboard.getClipboard());
            return true;
        }
        if (Screen.isCut(keyCode)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            this.replaceSelection("");
            return true;
        }
        switch (keyCode) {
            case 263: {
                if (Screen.hasControlDown()) {
                    Substring substring = this.getPreviousWordAtCursor();
                    this.moveCursor(CursorMovement.ABSOLUTE, substring.beginIndex);
                } else {
                    this.moveCursor(CursorMovement.RELATIVE, -1);
                }
                return true;
            }
            case 262: {
                if (Screen.hasControlDown()) {
                    Substring substring = this.getNextWordAtCursor();
                    this.moveCursor(CursorMovement.ABSOLUTE, substring.beginIndex);
                } else {
                    this.moveCursor(CursorMovement.RELATIVE, 1);
                }
                return true;
            }
            case 265: {
                if (!Screen.hasControlDown()) {
                    this.moveCursorLine(-1);
                }
                return true;
            }
            case 264: {
                if (!Screen.hasControlDown()) {
                    this.moveCursorLine(1);
                }
                return true;
            }
            case 266: {
                this.moveCursor(CursorMovement.ABSOLUTE, 0);
                return true;
            }
            case 267: {
                this.moveCursor(CursorMovement.END, 0);
                return true;
            }
            case 268: {
                if (Screen.hasControlDown()) {
                    this.moveCursor(CursorMovement.ABSOLUTE, 0);
                } else {
                    this.moveCursor(CursorMovement.ABSOLUTE, this.getCurrentLine().beginIndex);
                }
                return true;
            }
            case 269: {
                if (Screen.hasControlDown()) {
                    this.moveCursor(CursorMovement.END, 0);
                } else {
                    this.moveCursor(CursorMovement.ABSOLUTE, this.getCurrentLine().endIndex);
                }
                return true;
            }
            case 259: {
                if (Screen.hasControlDown()) {
                    Substring substring = this.getPreviousWordAtCursor();
                    this.delete(substring.beginIndex - this.cursor);
                } else {
                    this.delete(-1);
                }
                return true;
            }
            case 261: {
                if (Screen.hasControlDown()) {
                    Substring substring = this.getNextWordAtCursor();
                    this.delete(substring.beginIndex - this.cursor);
                } else {
                    this.delete(1);
                }
                return true;
            }
            case 257: 
            case 335: {
                this.replaceSelection("\n");
                return true;
            }
        }
        return false;
    }

    /**
     * {@return the lines of the edit box's text}
     */
    public Iterable<Substring> getLines() {
        return this.lines;
    }

    /**
     * {@return whether the edit box has a selected text}
     */
    public boolean hasSelection() {
        return this.selectionEnd != this.cursor;
    }

    /**
     * {@return the text that is currently selected, or an empty string if there is no selection}
     */
    @VisibleForTesting
    public String getSelectedText() {
        Substring substring = this.getSelection();
        return this.text.substring(substring.beginIndex, substring.endIndex);
    }

    /**
     * {@return the line that the cursor is located at}
     */
    private Substring getCurrentLine() {
        return this.getOffsetLine(0);
    }

    /**
     * {@return the line offset by {@code offsetFromCurrent} from the cursor's line}
     */
    private Substring getOffsetLine(int offsetFromCurrent) {
        int i = this.getCurrentLineIndex();
        if (i < 0) {
            throw new IllegalStateException("Cursor is not within text (cursor = " + this.cursor + ", length = " + this.text.length() + ")");
        }
        return this.lines.get(MathHelper.clamp(i + offsetFromCurrent, 0, this.lines.size() - 1));
    }

    /**
     * {@return the substring of a word whose start position is before the cursor}
     * 
     * <p>A word is a string consisting entirely of non-whitespace characters. If the
     * cursor is in the middle of a word, the start position is that of the word; if not,
     * the start position is that of the first word before the cursor.
     * 
     * @see #getNextWordAtCursor
     */
    @VisibleForTesting
    public Substring getPreviousWordAtCursor() {
        int i;
        if (this.text.isEmpty()) {
            return Substring.EMPTY;
        }
        for (i = MathHelper.clamp(this.cursor, 0, this.text.length() - 1); i > 0 && Character.isWhitespace(this.text.charAt(i - 1)); --i) {
        }
        while (i > 0 && !Character.isWhitespace(this.text.charAt(i - 1))) {
            --i;
        }
        return new Substring(i, this.getWordEndIndex(i));
    }

    /**
     * {@return the substring of a word whose start position is after the cursor}
     * 
     * <p>A word is a string consisting entirely of non-whitespace characters. If the
     * cursor is in the middle of a word, the start position is that of the first word
     * after the cursor; if not, the start position is that of the next word.
     * 
     * @see #getPreviousWordAtCursor
     */
    @VisibleForTesting
    public Substring getNextWordAtCursor() {
        int i;
        if (this.text.isEmpty()) {
            return Substring.EMPTY;
        }
        for (i = MathHelper.clamp(this.cursor, 0, this.text.length() - 1); i < this.text.length() && !Character.isWhitespace(this.text.charAt(i)); ++i) {
        }
        while (i < this.text.length() && Character.isWhitespace(this.text.charAt(i))) {
            ++i;
        }
        return new Substring(i, this.getWordEndIndex(i));
    }

    /**
     * {@return the end index of the word starting at {@code startIndex}}
     * 
     * <p>A word is a string consisting entirely of non-whitespace characters. Therefore,
     * the end index is the index of the character whose succeeding character is the first
     * whitespace since {@code startIndex}.
     */
    private int getWordEndIndex(int startIndex) {
        int i;
        for (i = startIndex; i < this.text.length() && !Character.isWhitespace(this.text.charAt(i)); ++i) {
        }
        return i;
    }

    /**
     * Called when the text changes. This rewraps the text, calls
     * {@link #changeListener}, then calls {@link #cursorChangeListener}.
     */
    private void onChange() {
        this.rewrap();
        this.changeListener.accept(this.text);
        this.cursorChangeListener.run();
    }

    /**
     * Rewraps the text. This is called whenever the text changes.
     */
    private void rewrap() {
        this.lines.clear();
        if (this.text.isEmpty()) {
            this.lines.add(Substring.EMPTY);
            return;
        }
        this.textRenderer.getTextHandler().wrapLines(this.text, this.width, Style.EMPTY, false, (style, start, end) -> this.lines.add(new Substring(start, end)));
        if (this.text.charAt(this.text.length() - 1) == '\n') {
            this.lines.add(new Substring(this.text.length(), this.text.length()));
        }
    }

    /**
     * {@return {@code value} truncated to at most {@link #maxLength} characters}
     * 
     * @see #truncate
     */
    private String truncateForReplacement(String value) {
        if (this.hasMaxLength()) {
            return StringHelper.truncate(value, this.maxLength, false);
        }
        return value;
    }

    /**
     * {@return {@code value} truncated to fit in the current text}
     * <p>For example, if the edit box with 100 characters limit currently
     * has 90 characters, this method will return at most 10 characters.
     * 
     * @see #truncateForReplacement
     */
    private String truncate(String value) {
        if (this.hasMaxLength()) {
            int i = this.maxLength - this.text.length();
            return StringHelper.truncate(value, i, false);
        }
        return value;
    }

    @Environment(value=EnvType.CLIENT)
    protected record Substring(int beginIndex, int endIndex) {
        static final Substring EMPTY = new Substring(0, 0);
    }
}

