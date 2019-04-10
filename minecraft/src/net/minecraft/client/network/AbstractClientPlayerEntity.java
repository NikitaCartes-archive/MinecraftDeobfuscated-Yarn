package net.minecraft.client.network;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.SkinRemappingImageFilter;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public abstract class AbstractClientPlayerEntity extends PlayerEntity {
	private PlayerListEntry cachedScoreboardEntry;
	public float field_3900;
	public float field_3899;
	public float field_3898;
	public final ClientWorld field_17892;

	public AbstractClientPlayerEntity(ClientWorld clientWorld, GameProfile gameProfile) {
		super(clientWorld, gameProfile);
		this.field_17892 = clientWorld;
	}

	@Override
	public boolean isSpectator() {
		PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getScoreboardEntry(this.getGameProfile().getId());
		return playerListEntry != null && playerListEntry.getGameMode() == GameMode.field_9219;
	}

	@Override
	public boolean isCreative() {
		PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getScoreboardEntry(this.getGameProfile().getId());
		return playerListEntry != null && playerListEntry.getGameMode() == GameMode.field_9220;
	}

	public boolean hasScoreboardEntry() {
		return this.getScoreboardEntry() != null;
	}

	@Nullable
	protected PlayerListEntry getScoreboardEntry() {
		if (this.cachedScoreboardEntry == null) {
			this.cachedScoreboardEntry = MinecraftClient.getInstance().getNetworkHandler().getScoreboardEntry(this.getUuid());
		}

		return this.cachedScoreboardEntry;
	}

	public boolean hasSkinTexture() {
		PlayerListEntry playerListEntry = this.getScoreboardEntry();
		return playerListEntry != null && playerListEntry.hasSkinTexture();
	}

	public Identifier getSkinTexture() {
		PlayerListEntry playerListEntry = this.getScoreboardEntry();
		return playerListEntry == null ? DefaultSkinHelper.getTexture(this.getUuid()) : playerListEntry.getSkinTexture();
	}

	@Nullable
	public Identifier method_3119() {
		PlayerListEntry playerListEntry = this.getScoreboardEntry();
		return playerListEntry == null ? null : playerListEntry.getCapeTexture();
	}

	public boolean method_3126() {
		return this.getScoreboardEntry() != null;
	}

	@Nullable
	public Identifier method_3122() {
		PlayerListEntry playerListEntry = this.getScoreboardEntry();
		return playerListEntry == null ? null : playerListEntry.getElytraTexture();
	}

	public static PlayerSkinTexture loadSkin(Identifier identifier, String string) {
		TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
		Texture texture = textureManager.getTexture(identifier);
		if (texture == null) {
			texture = new PlayerSkinTexture(
				null,
				String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", ChatUtil.stripTextFormat(string)),
				DefaultSkinHelper.getTexture(getOfflinePlayerUuid(string)),
				new SkinRemappingImageFilter()
			);
			textureManager.registerTexture(identifier, texture);
		}

		return (PlayerSkinTexture)texture;
	}

	public static Identifier getSkinId(String string) {
		return new Identifier("skins/" + Hashing.sha1().hashUnencodedChars(ChatUtil.stripTextFormat(string)));
	}

	public String method_3121() {
		PlayerListEntry playerListEntry = this.getScoreboardEntry();
		return playerListEntry == null ? DefaultSkinHelper.getModel(this.getUuid()) : playerListEntry.getModel();
	}

	public float method_3118() {
		float f = 1.0F;
		if (this.abilities.flying) {
			f *= 1.1F;
		}

		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
		f = (float)((double)f * ((entityAttributeInstance.getValue() / (double)this.abilities.getWalkSpeed() + 1.0) / 2.0));
		if (this.abilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
			f = 1.0F;
		}

		if (this.isUsingItem() && this.getActiveItem().getItem() == Items.field_8102) {
			int i = this.method_6048();
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

	@Override
	protected boolean wouldPoseNotCollide(EntityPose entityPose) {
		BoundingBox boundingBox = this.method_20343(entityPose);
		BoundingBox boundingBox2 = boundingBox.contract(0.1, 0.0, 0.1);
		return this.world.doesNotCollide(this, boundingBox2);
	}
}
