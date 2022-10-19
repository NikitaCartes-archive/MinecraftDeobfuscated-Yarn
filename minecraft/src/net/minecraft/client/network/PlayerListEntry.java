package net.minecraft.client.network;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.message.MessageVerifier;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class PlayerListEntry {
	private final GameProfile profile;
	private final Map<Type, Identifier> textures = Maps.newEnumMap(Type.class);
	private GameMode gameMode = GameMode.DEFAULT;
	private int latency;
	private boolean texturesLoaded;
	@Nullable
	private String model;
	@Nullable
	private Text displayName;
	@Nullable
	private PublicPlayerSession session;
	private MessageVerifier messageVerifier = MessageVerifier.UNVERIFIED;

	public PlayerListEntry(GameProfile profile) {
		this.profile = profile;
	}

	public GameProfile getProfile() {
		return this.profile;
	}

	@Nullable
	public PublicPlayerSession getSession() {
		return this.session;
	}

	public MessageVerifier getMessageVerifier() {
		return this.messageVerifier;
	}

	public boolean hasPublicKey() {
		return this.session != null && this.session.hasPublicKey();
	}

	protected void setSession(@Nullable PublicPlayerSession session) {
		this.session = session;
		this.messageVerifier = Util.mapOrElse(session, PublicPlayerSession::createVerifier, MessageVerifier.UNVERIFIED);
	}

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
}
