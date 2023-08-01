package net.minecraft.client.gui.hud.spectator;

import com.mojang.authlib.GameProfile;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TeleportToSpecificPlayerSpectatorCommand implements SpectatorMenuCommand {
	private final GameProfile gameProfile;
	private final Supplier<SkinTextures> skinTexturesSupplier;
	private final Text name;

	public TeleportToSpecificPlayerSpectatorCommand(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
		this.skinTexturesSupplier = MinecraftClient.getInstance().getSkinProvider().getSkinTexturesSupplier(gameProfile);
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
	public void renderIcon(DrawContext context, float brightness, int alpha) {
		context.setShaderColor(1.0F, 1.0F, 1.0F, (float)alpha / 255.0F);
		PlayerSkinDrawer.draw(context, (SkinTextures)this.skinTexturesSupplier.get(), 2, 2, 12);
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
