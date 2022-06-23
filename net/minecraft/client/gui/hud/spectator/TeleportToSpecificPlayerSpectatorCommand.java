/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TeleportToSpecificPlayerSpectatorCommand
implements SpectatorMenuCommand {
    private final GameProfile gameProfile;
    private final Identifier skinId;
    private final Text name;

    public TeleportToSpecificPlayerSpectatorCommand(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        this.skinId = minecraftClient.getSkinProvider().loadSkin(gameProfile);
        this.name = Text.literal(gameProfile.getName());
    }

    @Override
    public void use(SpectatorMenu menu) {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new SpectatorTeleportC2SPacket(this.gameProfile.getId()));
    }

    @Override
    public Text getName() {
        return this.name;
    }

    @Override
    public void renderIcon(MatrixStack matrices, float brightness, int alpha) {
        RenderSystem.setShaderTexture(0, this.skinId);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, (float)alpha / 255.0f);
        PlayerSkinDrawer.draw(matrices, 2, 2, 12);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

