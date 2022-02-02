package net.minecraft.entity.boss;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
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
	public void setPercent(float percent) {
		if (percent != this.percent) {
			super.setPercent(percent);
			this.sendPacket(BossBarS2CPacket::updateProgress);
		}
	}

	@Override
	public void setColor(BossBar.Color color) {
		if (color != this.color) {
			super.setColor(color);
			this.sendPacket(BossBarS2CPacket::updateStyle);
		}
	}

	@Override
	public void setStyle(BossBar.Style style) {
		if (style != this.style) {
			super.setStyle(style);
			this.sendPacket(BossBarS2CPacket::updateStyle);
		}
	}

	@Override
	public BossBar setDarkenSky(boolean darkenSky) {
		if (darkenSky != this.darkenSky) {
			super.setDarkenSky(darkenSky);
			this.sendPacket(BossBarS2CPacket::updateProperties);
		}

		return this;
	}

	@Override
	public BossBar setDragonMusic(boolean dragonMusic) {
		if (dragonMusic != this.dragonMusic) {
			super.setDragonMusic(dragonMusic);
			this.sendPacket(BossBarS2CPacket::updateProperties);
		}

		return this;
	}

	@Override
	public BossBar setThickenFog(boolean thickenFog) {
		if (thickenFog != this.thickenFog) {
			super.setThickenFog(thickenFog);
			this.sendPacket(BossBarS2CPacket::updateProperties);
		}

		return this;
	}

	@Override
	public void setName(Text name) {
		if (!Objects.equal(name, this.name)) {
			super.setName(name);
			this.sendPacket(BossBarS2CPacket::updateName);
		}
	}

	private void sendPacket(Function<BossBar, BossBarS2CPacket> bossBarToPacketFunction) {
		if (this.visible) {
			BossBarS2CPacket bossBarS2CPacket = (BossBarS2CPacket)bossBarToPacketFunction.apply(this);

			for (ServerPlayerEntity serverPlayerEntity : this.players) {
				serverPlayerEntity.networkHandler.sendPacket(bossBarS2CPacket);
			}
		}
	}

	public void addPlayer(ServerPlayerEntity player) {
		if (this.players.add(player) && this.visible) {
			player.networkHandler.sendPacket(BossBarS2CPacket.add(this));
		}
	}

	public void removePlayer(ServerPlayerEntity player) {
		if (this.players.remove(player) && this.visible) {
			player.networkHandler.sendPacket(BossBarS2CPacket.remove(this.getUuid()));
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
				serverPlayerEntity.networkHandler.sendPacket(visible ? BossBarS2CPacket.add(this) : BossBarS2CPacket.remove(this.getUuid()));
			}
		}
	}

	public Collection<ServerPlayerEntity> getPlayers() {
		return this.unmodifiablePlayers;
	}
}
