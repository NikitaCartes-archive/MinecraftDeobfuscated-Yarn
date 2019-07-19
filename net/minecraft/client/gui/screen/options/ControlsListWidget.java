/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.options;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;

@Environment(value=EnvType.CLIENT)
public class ControlsListWidget
extends ElementListWidget<Entry> {
    private final ControlsOptionsScreen gui;
    private int field_2733;

    public ControlsListWidget(ControlsOptionsScreen controlsOptionsScreen, MinecraftClient minecraftClient) {
        super(minecraftClient, controlsOptionsScreen.width + 45, controlsOptionsScreen.height, 43, controlsOptionsScreen.height - 32, 20);
        this.gui = controlsOptionsScreen;
        Object[] keyBindings = ArrayUtils.clone(minecraftClient.options.keysAll);
        Arrays.sort(keyBindings);
        String string = null;
        for (Object keyBinding : keyBindings) {
            int i;
            String string2 = ((KeyBinding)keyBinding).getCategory();
            if (!string2.equals(string)) {
                string = string2;
                this.addEntry(new CategoryEntry(string2));
            }
            if ((i = minecraftClient.textRenderer.getStringWidth(I18n.translate(((KeyBinding)keyBinding).getId(), new Object[0]))) > this.field_2733) {
                this.field_2733 = i;
            }
            this.addEntry(new KeyBindingEntry((KeyBinding)keyBinding));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    @Environment(value=EnvType.CLIENT)
    public class KeyBindingEntry
    extends Entry {
        private final KeyBinding binding;
        private final String bindingName;
        private final ButtonWidget editButton;
        private final ButtonWidget resetButton;

        private KeyBindingEntry(final KeyBinding keyBinding) {
            this.binding = keyBinding;
            this.bindingName = I18n.translate(keyBinding.getId(), new Object[0]);
            this.editButton = new ButtonWidget(0, 0, 75, 20, this.bindingName, buttonWidget -> {
                ((ControlsListWidget)ControlsListWidget.this).gui.focusedBinding = keyBinding;
            }){

                @Override
                protected String getNarrationMessage() {
                    if (keyBinding.isNotBound()) {
                        return I18n.translate("narrator.controls.unbound", KeyBindingEntry.this.bindingName);
                    }
                    return I18n.translate("narrator.controls.bound", KeyBindingEntry.this.bindingName, super.getNarrationMessage());
                }
            };
            this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset", new Object[0]), buttonWidget -> {
                ((ControlsListWidget)ControlsListWidget.this).minecraft.options.setKeyCode(keyBinding, keyBinding.getDefaultKeyCode());
                KeyBinding.updateKeysByCode();
            }){

                @Override
                protected String getNarrationMessage() {
                    return I18n.translate("narrator.controls.reset", KeyBindingEntry.this.bindingName);
                }
            };
        }

        @Override
        public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            boolean bl2 = ((ControlsListWidget)ControlsListWidget.this).gui.focusedBinding == this.binding;
            ((ControlsListWidget)ControlsListWidget.this).minecraft.textRenderer.draw(this.bindingName, k + 90 - ControlsListWidget.this.field_2733, j + m / 2 - ((ControlsListWidget)ControlsListWidget.this).minecraft.textRenderer.fontHeight / 2, 0xFFFFFF);
            this.resetButton.x = k + 190;
            this.resetButton.y = j;
            this.resetButton.active = !this.binding.isDefault();
            this.resetButton.render(n, o, f);
            this.editButton.x = k + 105;
            this.editButton.y = j;
            this.editButton.setMessage(this.binding.getLocalizedName());
            boolean bl3 = false;
            if (!this.binding.isNotBound()) {
                for (KeyBinding keyBinding : ((ControlsListWidget)ControlsListWidget.this).minecraft.options.keysAll) {
                    if (keyBinding == this.binding || !this.binding.equals(keyBinding)) continue;
                    bl3 = true;
                    break;
                }
            }
            if (bl2) {
                this.editButton.setMessage((Object)((Object)Formatting.WHITE) + "> " + (Object)((Object)Formatting.YELLOW) + this.editButton.getMessage() + (Object)((Object)Formatting.WHITE) + " <");
            } else if (bl3) {
                this.editButton.setMessage((Object)((Object)Formatting.RED) + this.editButton.getMessage());
            }
            this.editButton.render(n, o, f);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(this.editButton, this.resetButton);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            if (this.editButton.mouseClicked(d, e, i)) {
                return true;
            }
            return this.resetButton.mouseClicked(d, e, i);
        }

        @Override
        public boolean mouseReleased(double d, double e, int i) {
            return this.editButton.mouseReleased(d, e, i) || this.resetButton.mouseReleased(d, e, i);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class CategoryEntry
    extends Entry {
        private final String name;
        private final int nameWidth;

        public CategoryEntry(String string) {
            this.name = I18n.translate(string, new Object[0]);
            this.nameWidth = ((ControlsListWidget)ControlsListWidget.this).minecraft.textRenderer.getStringWidth(this.name);
        }

        @Override
        public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            ((ControlsListWidget)ControlsListWidget.this).minecraft.textRenderer.draw(this.name, ((ControlsListWidget)ControlsListWidget.this).minecraft.currentScreen.width / 2 - this.nameWidth / 2, j + m - ((ControlsListWidget)ControlsListWidget.this).minecraft.textRenderer.fontHeight - 1, 0xFFFFFF);
        }

        @Override
        public boolean changeFocus(boolean bl) {
            return false;
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry
    extends ElementListWidget.Entry<Entry> {
    }
}

