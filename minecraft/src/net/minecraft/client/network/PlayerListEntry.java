package net.minecraft.client.network;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.logging.LogUtils;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PlayerListEntry {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final GameProfile profile;
	private final Map<Type, Identifier> textures = Maps.newEnumMap(Type.class);
	private GameMode gameMode;
	private int latency;
	private boolean texturesLoaded;
	@Nullable
	private String model;
	@Nullable
	private Text displayName;
	private int lastHealth;
	private int health;
	private long lastHealthTime;
	private long blinkingHeartTime;
	private long showTime;
	@Nullable
	private final PlayerPublicKey publicKeyData;

	public PlayerListEntry(PlayerListS2CPacket.Entry playerListPacketEntry, MinecraftSessionService sessionService) {
		this.profile = playerListPacketEntry.getProfile();
		this.gameMode = playerListPacketEntry.getGameMode();
		this.latency = playerListPacketEntry.getLatency();
		this.displayName = playerListPacketEntry.getDisplayName();
		PlayerPublicKey playerPublicKey = null;

		try {
			PlayerPublicKey.PublicKeyData publicKeyData = playerListPacketEntry.getPublicKeyData();
			if (publicKeyData != null) {
				playerPublicKey = PlayerPublicKey.verifyAndDecode(sessionService, publicKeyData);
			}
		} catch (InsecurePublicKeyException | NetworkEncryptionException var5) {
			LOGGER.error("Failed to retrieve publicKey property for profile {}", this.profile.getId(), var5);
		}

		this.publicKeyData = playerPublicKey;
	}

	public GameProfile getProfile() {
		return this.profile;
	}

	@Nullable
	public PlayerPublicKey getPublicKeyData() {
		return this.publicKeyData;
	}

	@Nullable
	public GameMode getGameMode() {
		return this.gameMode;
	}

	protected void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public int getLatency() {
		return this.latency;
	}

	protected void setLatency(int latency) {
		this.latency = latency;
	}

	public boolean hasCape() {
		return this.getCapeTexture() != null;
	}

	/**
	 * Checks if the player represented by this entry has a custom skin.
	 * 
	 * <p>If the player has the default skin, this will return false.
	 */
	public boolean hasSkinTexture() {
		return this.getSkinTexture() != null;
	}

	public String getModel() {
		return this.model == null ? DefaultSkinHelper.getModel(this.profile.getId()) : this.model;
	}

	public Identifier getSkinTexture() {
		this.loadTextures();
		return MoreObjects.firstNonNull((Identifier)this.textures.get(Type.SKIN), DefaultSkinHelper.getTexture(this.profile.getId()));
	}

	@Nullable
	public Identifier getCapeTexture() {
		this.loadTextures();
		return (Identifier)this.textures.get(Type.CAPE);
	}

	@Nullable
	public Identifier getElytraTexture() {
		this.loadTextures();
		return (Identifier)this.textures.get(Type.ELYTRA);
	}

	@Nullable
	public Team getScoreboardTeam() {
		return MinecraftClient.getInstance().world.getScoreboard().getPlayerTeam(this.getProfile().getName());
	}

	protected void loadTextures() {
		synchronized (this) {
			if (!this.texturesLoaded) {
				this.texturesLoaded = true;
				MinecraftClient.getInstance().getSkinProvider().loadSkin(this.profile, (type, id, texture) -> {
					this.textures.put(type, id);
					if (type == Type.SKIN) {
						this.model = texture.getMetadata("model");
						if (this.model == null) {
							this.model = "default";
						}
					}
				}, true);
			}
		}
	}

	public void setDisplayName(@Nullable Text displayName) {
		this.displayName = displayName;
	}

	@Nullable
	public Text getDisplayName() {
		return this.displayName;
	}

	public int getLastHealth() {
		return this.lastHealth;
	}

	public void setLastHealth(int lastHealth) {
		this.lastHealth = lastHealth;
	}

	public int getHealth() {
		return this.health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public long getLastHealthTime() {
		return this.lastHealthTime;
	}

	public void setLastHealthTime(long lastHealthTime) {
		this.lastHealthTime = lastHealthTime;
	}

	public long getBlinkingHeartTime() {
		return this.blinkingHeartTime;
	}

	public void setBlinkingHeartTime(long blinkingHeartTime) {
		this.blinkingHeartTime = blinkingHeartTime;
	}

	public long getShowTime() {
		return this.showTime;
	}

	public void setShowTime(long showTime) {
		this.showTime = showTime;
	}
}
