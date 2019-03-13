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
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class ScoreboardEntry {
	private final GameProfile profile;
	private final Map<Type, Identifier> field_3742 = Maps.newEnumMap(Type.class);
	private GameMode gameMode;
	private int latency;
	private boolean field_3740;
	private String field_3745;
	private TextComponent field_3743;
	private int field_3738;
	private int field_3736;
	private long field_3737;
	private long field_3747;
	private long field_3746;

	public ScoreboardEntry(GameProfile gameProfile) {
		this.profile = gameProfile;
	}

	public ScoreboardEntry(PlayerListS2CPacket.class_2705 arg) {
		this.profile = arg.method_11726();
		this.gameMode = arg.method_11725();
		this.latency = arg.method_11727();
		this.field_3743 = arg.method_11724();
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

	public boolean isSkinTextureLoaded() {
		return this.method_2968() != null;
	}

	public String method_2977() {
		return this.field_3745 == null ? DefaultSkinHelper.getModel(this.profile.getId()) : this.field_3745;
	}

	public Identifier method_2968() {
		this.method_2969();
		return MoreObjects.firstNonNull((Identifier)this.field_3742.get(Type.SKIN), DefaultSkinHelper.method_4648(this.profile.getId()));
	}

	@Nullable
	public Identifier method_2979() {
		this.method_2969();
		return (Identifier)this.field_3742.get(Type.CAPE);
	}

	@Nullable
	public Identifier method_2957() {
		this.method_2969();
		return (Identifier)this.field_3742.get(Type.ELYTRA);
	}

	@Nullable
	public ScoreboardTeam getScoreboardTeam() {
		return MinecraftClient.getInstance().field_1687.method_8428().getPlayerTeam(this.getProfile().getName());
	}

	protected void method_2969() {
		synchronized (this) {
			if (!this.field_3740) {
				this.field_3740 = true;
				MinecraftClient.getInstance().method_1582().method_4652(this.profile, (type, identifier, minecraftProfileTexture) -> {
					switch (type) {
						case SKIN:
							this.field_3742.put(Type.SKIN, identifier);
							this.field_3745 = minecraftProfileTexture.getMetadata("model");
							if (this.field_3745 == null) {
								this.field_3745 = "default";
							}
							break;
						case CAPE:
							this.field_3742.put(Type.CAPE, identifier);
							break;
						case ELYTRA:
							this.field_3742.put(Type.ELYTRA, identifier);
					}
				}, true);
			}
		}
	}

	public void method_2962(@Nullable TextComponent textComponent) {
		this.field_3743 = textComponent;
	}

	@Nullable
	public TextComponent method_2971() {
		return this.field_3743;
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
