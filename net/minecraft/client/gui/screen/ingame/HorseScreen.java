/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HorseScreen
extends HandledScreen<HorseScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/horse.png");
    private final AbstractHorseEntity entity;
    private float mouseX;
    private float mouseY;

    public HorseScreen(HorseScreenHandler handler, PlayerInventory inventory, AbstractHorseEntity entity) {
        super(handler, inventory, entity.getDisplayName());
        this.entity = entity;
        this.passEvents = false;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        AbstractDonkeyEntity abstractDonkeyEntity;
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        HorseScreen.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (this.entity instanceof AbstractDonkeyEntity && (abstractDonkeyEntity = (AbstractDonkeyEntity)this.entity).hasChest()) {
            HorseScreen.drawTexture(matrices, i + 79, j + 17, 0, this.backgroundHeight, abstractDonkeyEntity.getInventoryColumns() * 18, 54);
        }
        if (this.entity.canBeSaddled()) {
            HorseScreen.drawTexture(matrices, i + 7, j + 35 - 18, 18, this.backgroundHeight + 54, 18, 18);
        }
        if (this.entity.hasArmorSlot()) {
            if (this.entity instanceof LlamaEntity) {
                HorseScreen.drawTexture(matrices, i + 7, j + 35, 36, this.backgroundHeight + 54, 18, 18);
            } else {
                HorseScreen.drawTexture(matrices, i + 7, j + 35, 0, this.backgroundHeight + 54, 18, 18);
            }
        }
        InventoryScreen.drawEntity(matrices, i + 51, j + 60, 17, (float)(i + 51) - this.mouseX, (float)(j + 75 - 50) - this.mouseY, (LivingEntity)this.entity);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}

