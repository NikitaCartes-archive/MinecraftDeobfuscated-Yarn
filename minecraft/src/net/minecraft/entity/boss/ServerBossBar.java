package net.minecraft.entity.boss;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import net.minecraft.client.network.packet.BossBarS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ServerBossBar extends BossBar {
	private final Set<ServerPlayerEntity> players = Sets.<ServerPlayerEntity>newHashSet();
	private final Set<ServerPlayerEntity> unmodifiablePlayers = Collections.unmodifiableSet(this.players);
	private boolean visible = true;

	public ServerBossBar(Text text, BossBar.Color color, BossBar.Style style) {
		super(MathHelper.randomUUID(), text, color, style);
	}

	@Override
	public void setPercent(float f) {
		if (f != this.percent) {
			super.setPercent(f);
			this.sendPacket(BossBarS2CPacket.Type.field_12080);
		}
	}

	@Override
	public void setColor(BossBar.Color color) {
		if (color != this.color) {
			super.setColor(color);
			this.sendPacket(BossBarS2CPacket.Type.field_12081);
		}
	}

	@Override
	public void setOverlay(BossBar.Style style) {
		if (style != this.style) {
			super.setOverlay(style);
			this.sendPacket(BossBarS2CPacket.Type.field_12081);
		}
	}

	@Override
	public BossBar setDarkenSky(boolean bl) {
		if (bl != this.darkenSky) {
			super.setDarkenSky(bl);
			this.sendPacket(BossBarS2CPacket.Type.field_12083);
		}

		return this;
	}

	@Override
	public BossBar setDragonMusic(boolean bl) {
		if (bl != this.dragonMusic) {
			super.setDragonMusic(bl);
			this.sendPacket(BossBarS2CPacket.Type.field_12083);
		}

		return this;
	}

	@Override
	public BossBar setThickenFog(boolean bl) {
		if (bl != this.thickenFog) {
			super.setThickenFog(bl);
			this.sendPacket(BossBarS2CPacket.Type.field_12083);
		}

		return this;
	}

	@Override
	public void method_5413(Text text) {
		if (!Objects.equal(text, this.field_5777)) {
			super.method_5413(text);
			this.sendPacket(BossBarS2CPacket.Type.field_12084);
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

	public void addPlayer(ServerPlayerEntity serverPlayerEntity) {
		if (this.players.add(serverPlayerEntity) && this.visible) {
			serverPlayerEntity.networkHandler.sendPacket(new BossBarS2CPacket(BossBarS2CPacket.Type.field_12078, this));
		}
	}

	public void removePlayer(ServerPlayerEntity serverPlayerEntity) {
		if (this.players.remove(serverPlayerEntity) && this.visible) {
			serverPlayerEntity.networkHandler.sendPacket(new BossBarS2CPacket(BossBarS2CPacket.Type.field_12082, this));
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

	public void setVisible(boolean bl) {
		if (bl != this.visible) {
			this.visible = bl;

			for (ServerPlayerEntity serverPlayerEntity : this.players) {
				serverPlayerEntity.networkHandler.sendPacket(new BossBarS2CPacket(bl ? BossBarS2CPacket.Type.field_12078 : BossBarS2CPacket.Type.field_12082, this));
			}
		}
	}

	public Collection<ServerPlayerEntity> getPlayers() {
		return this.unmodifiablePlayers;
	}
}
