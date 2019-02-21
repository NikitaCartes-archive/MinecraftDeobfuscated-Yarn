package net.minecraft.client.gui.hud.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.packet.SpectatorTeleportC2SPacket;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TeleportToSpecificPlayerSpectatorCommand implements SpectatorMenuCommand {
	private final GameProfile gameProfile;
	private final Identifier skinId;

	public TeleportToSpecificPlayerSpectatorCommand(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		Map<Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(gameProfile);
		if (map.containsKey(Type.SKIN)) {
			this.skinId = minecraftClient.getSkinProvider().loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
		} else {
			this.skinId = DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile));
		}
	}

	@Override
	public void use(SpectatorMenu spectatorMenu) {
		MinecraftClient.getInstance().getNetworkHandler().sendPacket(new SpectatorTeleportC2SPacket(this.gameProfile.getId()));
	}

	@Override
	public TextComponent getName() {
		return new StringTextComponent(this.gameProfile.getName());
	}

	@Override
	public void renderIcon(float f, int i) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(this.skinId);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, (float)i / 255.0F);
		DrawableHelper.drawTexturedRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
		DrawableHelper.drawTexturedRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
	}

	@Override
	public boolean enabled() {
		return true;
	}
}
