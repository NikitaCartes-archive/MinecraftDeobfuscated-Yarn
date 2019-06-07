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
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class PlayerListEntry {
	private final GameProfile profile;
	private final Map<Type, Identifier> textures = Maps.newEnumMap(Type.class);
	private GameMode gameMode;
	private int latency;
	private boolean texturesLoaded;
	private String model;
	private Text displayName;
	private int field_3738;
	private int field_3736;
	private long field_3737;
	private long field_3747;
	private long field_3746;

	public PlayerListEntry(GameProfile gameProfile) {
		this.profile = gameProfile;
	}

	public PlayerListEntry(PlayerListS2CPacket.Entry entry) {
		this.profile = entry.getProfile();
		this.gameMode = entry.getGameMode();
		this.latency = entry.getLatency();
		this.displayName = entry.getDisplayName();
	}

	public GameProfile getProfile() {
		return this.profile;
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

	protected void setLatency(int i) {
		this.latency = i;
	}

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
				MinecraftClient.getInstance().getSkinProvider().loadSkin(this.profile, (type, identifier, minecraftProfileTexture) -> {
					switch (type) {
						case SKIN:
							this.textures.put(Type.SKIN, identifier);
							this.model = minecraftProfileTexture.getMetadata("model");
							if (this.model == null) {
								this.model = "default";
							}
							break;
						case CAPE:
							this.textures.put(Type.CAPE, identifier);
							break;
						case ELYTRA:
							this.textures.put(Type.ELYTRA, identifier);
					}
				}, true);
			}
		}
	}

	public void setDisplayName(@Nullable Text text) {
		this.displayName = text;
	}

	@Nullable
	public Text getDisplayName() {
		return this.displayName;
	}

	public int method_2973() {
		return this.field_3738;
	}

	public void method_2972(int i) {
		this.field_3738 = i;
	}

	public int method_2960() {
		return this.field_3736;
	}

	public void method_2965(int i) {
		this.field_3736 = i;
	}

	public long method_2974() {
		return this.field_3737;
	}

	public void method_2978(long l) {
		this.field_3737 = l;
	}

	public long method_2961() {
		return this.field_3747;
	}

	public void method_2975(long l) {
		this.field_3747 = l;
	}

	public long method_2976() {
		return this.field_3746;
	}

	public void method_2964(long l) {
		this.field_3746 = l;
	}
}
