package net.minecraft.client.network;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public abstract class AbstractClientPlayerEntity extends PlayerEntity {
	private PlayerListEntry cachedScoreboardEntry;
	public float elytraPitch;
	public float elytraYaw;
	public float elytraRoll;
	public final ClientWorld clientWorld;

	public AbstractClientPlayerEntity(ClientWorld world, GameProfile profile) {
		super(world, profile);
		this.clientWorld = world;
	}

	@Override
	public boolean isSpectator() {
		PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getGameProfile().getId());
		return playerListEntry != null && playerListEntry.getGameMode() == GameMode.SPECTATOR;
	}

	@Override
	public boolean isCreative() {
		PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getGameProfile().getId());
		return playerListEntry != null && playerListEntry.getGameMode() == GameMode.CREATIVE;
	}

	public boolean canRenderCapeTexture() {
		return this.getPlayerListEntry() != null;
	}

	@Nullable
	protected PlayerListEntry getPlayerListEntry() {
		if (this.cachedScoreboardEntry == null) {
			this.cachedScoreboardEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getUuid());
		}

		return this.cachedScoreboardEntry;
	}

	public boolean hasSkinTexture() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry != null && playerListEntry.hasSkinTexture();
	}

	public Identifier getSkinTexture() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry == null ? DefaultSkinHelper.getTexture(this.getUuid()) : playerListEntry.getSkinTexture();
	}

	@Nullable
	public Identifier getCapeTexture() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry == null ? null : playerListEntry.getCapeTexture();
	}

	public boolean canRenderElytraTexture() {
		return this.getPlayerListEntry() != null;
	}

	@Nullable
	public Identifier getElytraTexture() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry == null ? null : playerListEntry.getElytraTexture();
	}

	public static PlayerSkinTexture loadSkin(Identifier id, String playerName) {
		TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
		AbstractTexture abstractTexture = textureManager.getTexture(id);
		if (abstractTexture == null) {
			abstractTexture = new PlayerSkinTexture(
				null,
				String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", ChatUtil.stripTextFormat(playerName)),
				DefaultSkinHelper.getTexture(getOfflinePlayerUuid(playerName)),
				true,
				null
			);
			textureManager.registerTexture(id, abstractTexture);
		}

		return (PlayerSkinTexture)abstractTexture;
	}

	public static Identifier getSkinId(String playerName) {
		return new Identifier("skins/" + Hashing.sha1().hashUnencodedChars(ChatUtil.stripTextFormat(playerName)));
	}

	public String getModel() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry == null ? DefaultSkinHelper.getModel(this.getUuid()) : playerListEntry.getModel();
	}

	public float getSpeed() {
		float f = 1.0F;
		if (this.abilities.flying) {
			f *= 1.1F;
		}

		f = (float)((double)f * ((this.method_26825(EntityAttributes.GENERIC_MOVEMENT_SPEED) / (double)this.abilities.getWalkSpeed() + 1.0) / 2.0));
		if (this.abilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
			f = 1.0F;
		}

		if (this.isUsingItem() && this.getActiveItem().getItem() == Items.BOW) {
			int i = this.getItemUseTime();
			float g = (float)i / 20.0F;
			if (g > 1.0F) {
				g = 1.0F;
			} else {
				g *= g;
			}

			f *= 1.0F - g * 0.15F;
		}

		return f;
	}
}
