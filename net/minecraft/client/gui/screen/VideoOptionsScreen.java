/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4667;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.FullScreenOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class VideoOptionsScreen
extends class_4667 {
    private ButtonListWidget list;
    private static final Option[] OPTIONS = new Option[]{Option.GRAPHICS, Option.RENDER_DISTANCE, Option.AO, Option.FRAMERATE_LIMIT, Option.VSYNC, Option.VIEW_BOBBING, Option.GUI_SCALE, Option.ATTACK_INDICATOR, Option.GAMMA, Option.CLOUDS, Option.FULLSCREEN, Option.PARTICLES, Option.MIPMAP_LEVELS, Option.ENTITY_SHADOWS, Option.BIOME_BLEND_RADIUS};
    private int mipmapLevels;

    public VideoOptionsScreen(Screen screen, GameOptions gameOptions) {
        super(screen, gameOptions, new TranslatableText("options.videoTitle", new Object[0]));
    }

    @Override
    protected void init() {
        this.mipmapLevels = this.field_21336.mipmapLevels;
        this.list = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addSingleOptionEntry(new FullScreenOption(this.minecraft.getWindow()));
        this.list.addAll(OPTIONS);
        this.children.add(this.list);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> {
            this.minecraft.options.write();
            this.minecraft.getWindow().method_4475();
            this.minecraft.openScreen(this.field_21335);
        }));
    }

    @Override
    public void removed() {
        if (this.field_21336.mipmapLevels != this.mipmapLevels) {
            this.minecraft.getSpriteAtlas().setMipLevel(this.field_21336.mipmapLevels);
            this.minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            this.minecraft.getSpriteAtlas().setFilter(false, this.field_21336.mipmapLevels > 0);
            this.minecraft.reloadResourcesConcurrently();
        }
        super.removed();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        int j = this.field_21336.guiScale;
        if (super.mouseClicked(d, e, i)) {
            if (this.field_21336.guiScale != j) {
                this.minecraft.onResolutionChanged();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        int j = this.field_21336.guiScale;
        if (super.mouseReleased(d, e, i)) {
            return true;
        }
        if (this.list.mouseReleased(d, e, i)) {
            if (this.field_21336.guiScale != j) {
                this.minecraft.onResolutionChanged();
            }
            return true;
        }
        return false;
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.list.render(i, j, f);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 5, 0xFFFFFF);
        super.render(i, j, f);
    }
}

