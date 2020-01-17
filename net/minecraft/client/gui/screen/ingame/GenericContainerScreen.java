/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GenericContainerScreen
extends ContainerScreen<GenericContainer>
implements ContainerProvider<GenericContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private final int rows;

    public GenericContainerScreen(GenericContainer container, PlayerInventory inventory, Text title) {
        super(container, inventory, title);
        this.passEvents = false;
        int i = 222;
        int j = 114;
        this.rows = container.getRows();
        this.containerHeight = 114 + this.rows * 18;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        this.font.draw(this.title.asFormattedString(), 8.0f, 6.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.containerWidth) / 2;
        int j = (this.height - this.containerHeight) / 2;
        this.blit(i, j, 0, 0, this.containerWidth, this.rows * 18 + 17);
        this.blit(i, j + this.rows * 18 + 17, 0, 126, this.containerWidth, 96);
    }
}

