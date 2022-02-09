package net.minecraft.client.network;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public abstract class AbstractClientPlayerEntity extends PlayerEntity {
	private static final String SKIN_URL = "http://skins.minecraft.net/MinecraftSkins/%s.png";
	public static final int field_32659 = 8;
	public static final int field_32660 = 8;
	public static final int field_32661 = 8;
	public static final int field_32667 = 8;
	public static final int field_32668 = 40;
	public static final int field_32669 = 8;
	public static final int field_32662 = 8;
	public static final int field_32663 = 8;
	public static final int field_32664 = 64;
	public static final int field_32665 = 64;
	@Nullable
	private PlayerListEntry cachedScoreboardEntry;
	public float elytraPitch;
	public float elytraYaw;
	public float elytraRoll;
	public final ClientWorld clientWorld;

	public AbstractClientPlayerEntity(ClientWorld world, GameProfile profile) {
		super(world, world.getSpawnPos(), world.getSpawnAngle(), profile);
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

	public static void loadSkin(Identifier id, String playerName) {
		TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
		AbstractTexture abstractTexture = textureManager.getOrDefault(id, MissingSprite.getMissingSpriteTexture());
		if (abstractTexture == MissingSprite.getMissingSpriteTexture()) {
			AbstractTexture var4 = new PlayerSkinTexture(
				null,
				String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringHelper.stripTextFormat(playerName)),
				DefaultSkinHelper.getTexture(getOfflinePlayerUuid(playerName)),
				true,
				null
			);
			textureManager.registerTexture(id, var4);
		}
	}

	public static Identifier getSkinId(String playerName) {
		return new Identifier("skins/" + Hashing.sha1().hashUnencodedChars(StringHelper.stripTextFormat(playerName)));
	}

	public String getModel() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry == null ? DefaultSkinHelper.getModel(this.getUuid()) : playerListEntry.getModel();
	}

	public float getFovMultiplier() {
		float f = 1.0F;
		if (this.getAbilities().flying) {
			f *= 1.1F;
		}

		f *= ((float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) / this.getAbilities().getWalkSpeed() + 1.0F) / 2.0F;
		if (this.getAbilities().getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
			f = 1.0F;
		}

		ItemStack itemStack = this.getActiveItem();
		if (this.isUsingItem()) {
			if (itemStack.isOf(Items.BOW)) {
				int i = this.getItemUseTime();
				float g = (float)i / 20.0F;
				if (g > 1.0F) {
					g = 1.0F;
				} else {
					g *= g;
				}

				f *= 1.0F - g * 0.15F;
			} else if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && this.isUsingSpyglass()) {
				return 0.1F;
			}
		}

		return MathHelper.lerp(MinecraftClient.getInstance().options.fovEffectScale, 1.0F, f);
	}
}
