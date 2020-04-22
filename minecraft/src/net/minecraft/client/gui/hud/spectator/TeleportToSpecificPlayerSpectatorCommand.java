package net.minecraft.client.gui.hud.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
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
	public void use(SpectatorMenu menu) {
		MinecraftClient.getInstance().getNetworkHandler().sendPacket(new SpectatorTeleportC2SPacket(this.gameProfile.getId()));
	}

	@Override
	public Text getName() {
		return new LiteralText(this.gameProfile.getName());
	}

	@Override
	public void renderIcon(MatrixStack matrixStack, float f, int i) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(this.skinId);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, (float)i / 255.0F);
		DrawableHelper.drawTexture(matrixStack, 2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
		DrawableHelper.drawTexture(matrixStack, 2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
