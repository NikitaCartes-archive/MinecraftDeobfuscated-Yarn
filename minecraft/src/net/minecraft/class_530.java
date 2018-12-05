package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuElement;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuImpl;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_530 implements SpectatorMenuElement {
	private final GameProfile field_3253;
	private final Identifier field_3252;

	public class_530(GameProfile gameProfile) {
		this.field_3253 = gameProfile;
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		Map<Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().method_4654(gameProfile);
		if (map.containsKey(Type.SKIN)) {
			this.field_3252 = minecraftClient.getSkinProvider().method_4656((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
		} else {
			this.field_3252 = DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile));
		}
	}

	@Override
	public void selectElement(SpectatorMenuImpl spectatorMenuImpl) {
		MinecraftClient.getInstance().getNetworkHandler().sendPacket(new class_2884(this.field_3253.getId()));
	}

	@Override
	public TextComponent method_16892() {
		return new StringTextComponent(this.field_3253.getName());
	}

	@Override
	public void renderIcon(float f, int i) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(this.field_3252);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, (float)i / 255.0F);
		Drawable.drawTexturedRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
		Drawable.drawTexturedRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
	}

	@Override
	public boolean enabled() {
		return true;
	}
}
