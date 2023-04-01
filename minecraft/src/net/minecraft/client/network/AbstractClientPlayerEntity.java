package net.minecraft.client.network;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_8293;
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
import net.minecraft.util.Uuids;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public abstract class AbstractClientPlayerEntity extends PlayerEntity {
	private static final String SKIN_URL = "http://skins.minecraft.net/MinecraftSkins/%s.png";
	@Nullable
	private PlayerListEntry playerListEntry;
	@Nullable
	private PlayerListEntry field_44390;
	protected Vec3d lastVelocity = Vec3d.ZERO;
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
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry != null && playerListEntry.getGameMode() == GameMode.SPECTATOR;
	}

	@Override
	public boolean isCreative() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry != null && playerListEntry.getGameMode() == GameMode.CREATIVE;
	}

	public boolean canRenderCapeTexture() {
		return this.method_51023() != null;
	}

	@Nullable
	protected PlayerListEntry method_51023() {
		GameProfile gameProfile = this.getTransformedLook().playerSkin();
		if (gameProfile == null) {
			return this.getPlayerListEntry();
		} else {
			if (this.field_44390 == null || !this.field_44390.getProfile().equals(gameProfile)) {
				this.field_44390 = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(gameProfile.getId());
				if (this.field_44390 == null) {
					this.field_44390 = new PlayerListEntry(gameProfile, false);
				}
			}

			return this.field_44390;
		}
	}

	@Nullable
	protected PlayerListEntry getPlayerListEntry() {
		if (this.playerListEntry == null) {
			this.playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getUuid());
		}

		return this.playerListEntry;
	}

	@Override
	public void tick() {
		this.lastVelocity = this.getVelocity();
		super.tick();
	}

	public Vec3d lerpVelocity(float tickDelta) {
		return this.lastVelocity.lerp(this.getVelocity(), (double)tickDelta);
	}

	public boolean hasSkinTexture() {
		PlayerListEntry playerListEntry = this.method_51023();
		return playerListEntry != null && playerListEntry.hasSkinTexture();
	}

	public Identifier getSkinTexture() {
		if (class_8293.field_43512.method_50116()) {
			return DefaultSkinHelper.getTexture(this.getUuid());
		} else {
			PlayerListEntry playerListEntry = this.method_51023();
			return playerListEntry == null ? DefaultSkinHelper.getTexture(this.getUuid()) : playerListEntry.getSkinTexture();
		}
	}

	@Nullable
	public Identifier getCapeTexture() {
		PlayerListEntry playerListEntry = this.method_51023();
		return playerListEntry == null ? null : playerListEntry.getCapeTexture();
	}

	public boolean canRenderElytraTexture() {
		return this.method_51023() != null;
	}

	@Nullable
	public Identifier getElytraTexture() {
		PlayerListEntry playerListEntry = this.method_51023();
		return playerListEntry == null ? null : playerListEntry.getElytraTexture();
	}

	public static void loadSkin(Identifier id, String playerName) {
		TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
		AbstractTexture abstractTexture = textureManager.getOrDefault(id, MissingSprite.getMissingSpriteTexture());
		if (abstractTexture == MissingSprite.getMissingSpriteTexture()) {
			AbstractTexture var4 = new PlayerSkinTexture(
				null,
				String.format(Locale.ROOT, "http://skins.minecraft.net/MinecraftSkins/%s.png", StringHelper.stripTextFormat(playerName)),
				DefaultSkinHelper.getTexture(Uuids.getOfflinePlayerUuid(playerName)),
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
		PlayerListEntry playerListEntry = this.method_51023();
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

		return MathHelper.lerp(MinecraftClient.getInstance().options.getFovEffectScale().getValue().floatValue(), 1.0F, f);
	}
}
