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
	private final Identifier field_3252;

	public TeleportToSpecificPlayerSpectatorCommand(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		Map<Type, MinecraftProfileTexture> map = minecraftClient.method_1582().getTextures(gameProfile);
		if (map.containsKey(Type.SKIN)) {
			this.field_3252 = minecraftClient.method_1582().method_4656((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
		} else {
			this.field_3252 = DefaultSkinHelper.method_4648(PlayerEntity.getUuidFromProfile(gameProfile));
		}
	}

	@Override
	public void use(SpectatorMenu spectatorMenu) {
		MinecraftClient.getInstance().method_1562().method_2883(new SpectatorTeleportC2SPacket(this.gameProfile.getId()));
	}

	@Override
	public TextComponent method_16892() {
		return new StringTextComponent(this.gameProfile.getName());
	}

	@Override
	public void renderIcon(float f, int i) {
		MinecraftClient.getInstance().method_1531().method_4618(this.field_3252);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, (float)i / 255.0F);
		DrawableHelper.drawTexturedRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
		DrawableHelper.drawTexturedRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
	}

	@Override
	public boolean enabled() {
		return true;
	}
}
