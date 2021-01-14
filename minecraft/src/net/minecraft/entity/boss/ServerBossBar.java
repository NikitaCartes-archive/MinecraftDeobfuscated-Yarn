package net.minecraft.entity.boss;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ServerBossBar extends BossBar {
	private final Set<ServerPlayerEntity> players = Sets.<ServerPlayerEntity>newHashSet();
	private final Set<ServerPlayerEntity> unmodifiablePlayers = Collections.unmodifiableSet(this.players);
	private boolean visible = true;

	public ServerBossBar(Text displayName, BossBar.Color color, BossBar.Style style) {
		super(MathHelper.randomUuid(), displayName, color, style);
	}

	@Override
	public void setPercent(float percentage) {
		if (percentage != this.percent) {
			super.setPercent(percentage);
			this.sendPacket(BossBarS2CPacket.Type.UPDATE_PCT);
		}
	}

	@Override
	public void setColor(BossBar.Color color) {
		if (color != this.color) {
			super.setColor(color);
			this.sendPacket(BossBarS2CPacket.Type.UPDATE_STYLE);
		}
	}

	@Override
	public void setStyle(BossBar.Style style) {
		if (style != this.style) {
			super.setStyle(style);
			this.sendPacket(BossBarS2CPacket.Type.UPDATE_STYLE);
		}
	}

	@Override
	public BossBar setDarkenSky(boolean darkenSky) {
		if (darkenSky != this.darkenSky) {
			super.setDarkenSky(darkenSky);
			this.sendPacket(BossBarS2CPacket.Type.UPDATE_PROPERTIES);
		}

		return this;
	}

	@Override
	public BossBar setDragonMusic(boolean dragonMusic) {
		if (dragonMusic != this.dragonMusic) {
			super.setDragonMusic(dragonMusic);
			this.sendPacket(BossBarS2CPacket.Type.UPDATE_PROPERTIES);
		}

		return this;
	}

	@Override
	public BossBar setThickenFog(boolean thickenFog) {
		if (thickenFog != this.thickenFog) {
			super.setThickenFog(thickenFog);
			this.sendPacket(BossBarS2CPacket.Type.UPDATE_PROPERTIES);
		}

		return this;
	}

	@Override
	public void setName(Text name) {
		if (!Objects.equal(name, this.name)) {
			super.setName(name);
			this.sendPacket(BossBarS2CPacket.Type.UPDATE_NAME);
		}
	}

	private void sendPacket(BossBarS2CPacket.Type type) {
		if (this.visible) {
			BossBarS2CPacket bossBarS2CPacket = new BossBarS2CPacket(type, this);

			for (ServerPlayerEntity serverPlayerEntity : this.players) {
				serverPlayerEntity.networkHandler.sendPacket(bossBarS2CPacket);
			}
		}
	}

	public void addPlayer(ServerPlayerEntity player) {
		if (this.players.add(player) && this.visible) {
			player.networkHandler.sendPacket(new BossBarS2CPacket(BossBarS2CPacket.Type.ADD, this));
		}
	}

	public void removePlayer(ServerPlayerEntity player) {
		if (this.players.remove(player) && this.visible) {
			player.networkHandler.sendPacket(new BossBarS2CPacket(BossBarS2CPacket.Type.REMOVE, this));
		}
	}

	public void clearPlayers() {
		if (!this.players.isEmpty()) {
			for (ServerPlayerEntity serverPlayerEntity : Lists.newArrayList(this.players)) {
				this.removePlayer(serverPlayerEntity);
			}
		}
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		if (visible != this.visible) {
			this.visible = visible;

			for (ServerPlayerEntity serverPlayerEntity : this.players) {
				serverPlayerEntity.networkHandler.sendPacket(new BossBarS2CPacket(visible ? BossBarS2CPacket.Type.ADD : BossBarS2CPacket.Type.REMOVE, this));
			}
		}
	}

	public Collection<ServerPlayerEntity> getPlayers() {
		return this.unmodifiablePlayers;
	}
}
