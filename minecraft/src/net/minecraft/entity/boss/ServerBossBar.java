package net.minecraft.entity.boss;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import net.minecraft.client.network.packet.BossBarClientPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.MathHelper;

public class ServerBossBar extends BossBar {
	private final Set<ServerPlayerEntity> players = Sets.<ServerPlayerEntity>newHashSet();
	private final Set<ServerPlayerEntity> field_13914 = Collections.unmodifiableSet(this.players);
	private boolean visible = true;

	public ServerBossBar(TextComponent textComponent, BossBar.Color color, BossBar.Overlay overlay) {
		super(MathHelper.randomUUID(), textComponent, color, overlay);
	}

	@Override
	public void setPercent(float f) {
		if (f != this.percent) {
			super.setPercent(f);
			this.sendPacket(BossBarClientPacket.Type.UPDATE_PCT);
		}
	}

	@Override
	public void setColor(BossBar.Color color) {
		if (color != this.color) {
			super.setColor(color);
			this.sendPacket(BossBarClientPacket.Type.UPDATE_STYLE);
		}
	}

	@Override
	public void setOverlay(BossBar.Overlay overlay) {
		if (overlay != this.overlay) {
			super.setOverlay(overlay);
			this.sendPacket(BossBarClientPacket.Type.UPDATE_STYLE);
		}
	}

	@Override
	public BossBar setDarkenSky(boolean bl) {
		if (bl != this.darkenSky) {
			super.setDarkenSky(bl);
			this.sendPacket(BossBarClientPacket.Type.UPDATE_FLAGS);
		}

		return this;
	}

	@Override
	public BossBar setDragonMusic(boolean bl) {
		if (bl != this.dragonMusic) {
			super.setDragonMusic(bl);
			this.sendPacket(BossBarClientPacket.Type.UPDATE_FLAGS);
		}

		return this;
	}

	@Override
	public BossBar setThickenFog(boolean bl) {
		if (bl != this.thickenFog) {
			super.setThickenFog(bl);
			this.sendPacket(BossBarClientPacket.Type.UPDATE_FLAGS);
		}

		return this;
	}

	@Override
	public void setName(TextComponent textComponent) {
		if (!Objects.equal(textComponent, this.name)) {
			super.setName(textComponent);
			this.sendPacket(BossBarClientPacket.Type.UPDATE_TITLE);
		}
	}

	private void sendPacket(BossBarClientPacket.Type type) {
		if (this.visible) {
			BossBarClientPacket bossBarClientPacket = new BossBarClientPacket(type, this);

			for (ServerPlayerEntity serverPlayerEntity : this.players) {
				serverPlayerEntity.networkHandler.sendPacket(bossBarClientPacket);
			}
		}
	}

	public void addPlayer(ServerPlayerEntity serverPlayerEntity) {
		if (this.players.add(serverPlayerEntity) && this.visible) {
			serverPlayerEntity.networkHandler.sendPacket(new BossBarClientPacket(BossBarClientPacket.Type.ADD, this));
		}
	}

	public void removePlayer(ServerPlayerEntity serverPlayerEntity) {
		if (this.players.remove(serverPlayerEntity) && this.visible) {
			serverPlayerEntity.networkHandler.sendPacket(new BossBarClientPacket(BossBarClientPacket.Type.REMOVE, this));
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
				serverPlayerEntity.networkHandler.sendPacket(new BossBarClientPacket(bl ? BossBarClientPacket.Type.ADD : BossBarClientPacket.Type.REMOVE, this));
			}
		}
	}

	public Collection<ServerPlayerEntity> getPlayers() {
		return this.field_13914;
	}
}
