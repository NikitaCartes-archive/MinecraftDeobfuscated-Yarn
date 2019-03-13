package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.server.PlayerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedPlayerManager extends PlayerManager {
	private static final Logger field_13804 = LogManager.getLogger();

	public DedicatedPlayerManager(MinecraftDedicatedServer minecraftDedicatedServer) {
		super(minecraftDedicatedServer, minecraftDedicatedServer.method_16705().maxPlayers);
		ServerPropertiesHandler serverPropertiesHandler = minecraftDedicatedServer.method_16705();
		this.setViewDistance(serverPropertiesHandler.viewDistance, serverPropertiesHandler.viewDistance - 2);
		super.setWhitelistEnabled(serverPropertiesHandler.field_16804.get());
		if (!minecraftDedicatedServer.isSinglePlayer()) {
			this.method_14563().setEnabled(true);
			this.getIpBanList().setEnabled(true);
		}

		this.loadUserBanList();
		this.saveUserBanList();
		this.loadIpBanList();
		this.saveIpBanList();
		this.loadOpList();
		this.loadWhitelist();
		this.saveOpList();
		if (!this.method_14590().getFile().exists()) {
			this.saveWhitelist();
		}
	}

	@Override
	public void setWhitelistEnabled(boolean bl) {
		super.setWhitelistEnabled(bl);
		this.method_13938().setUseWhitelist(bl);
	}

	@Override
	public void addToOperators(GameProfile gameProfile) {
		super.addToOperators(gameProfile);
		this.saveOpList();
	}

	@Override
	public void removeFromOperators(GameProfile gameProfile) {
		super.removeFromOperators(gameProfile);
		this.saveOpList();
	}

	@Override
	public void reloadWhitelist() {
		this.loadWhitelist();
	}

	private void saveIpBanList() {
		try {
			this.getIpBanList().save();
		} catch (IOException var2) {
			field_13804.warn("Failed to save ip banlist: ", (Throwable)var2);
		}
	}

	private void saveUserBanList() {
		try {
			this.method_14563().save();
		} catch (IOException var2) {
			field_13804.warn("Failed to save user banlist: ", (Throwable)var2);
		}
	}

	private void loadIpBanList() {
		try {
			this.getIpBanList().load();
		} catch (IOException var2) {
			field_13804.warn("Failed to load ip banlist: ", (Throwable)var2);
		}
	}

	private void loadUserBanList() {
		try {
			this.method_14563().load();
		} catch (IOException var2) {
			field_13804.warn("Failed to load user banlist: ", (Throwable)var2);
		}
	}

	private void loadOpList() {
		try {
			this.method_14603().load();
		} catch (Exception var2) {
			field_13804.warn("Failed to load operators list: ", (Throwable)var2);
		}
	}

	private void saveOpList() {
		try {
			this.method_14603().save();
		} catch (Exception var2) {
			field_13804.warn("Failed to save operators list: ", (Throwable)var2);
		}
	}

	private void loadWhitelist() {
		try {
			this.method_14590().load();
		} catch (Exception var2) {
			field_13804.warn("Failed to load white-list: ", (Throwable)var2);
		}
	}

	private void saveWhitelist() {
		try {
			this.method_14590().save();
		} catch (Exception var2) {
			field_13804.warn("Failed to save white-list: ", (Throwable)var2);
		}
	}

	@Override
	public boolean isWhitelisted(GameProfile gameProfile) {
		return !this.isWhitelistEnabled() || this.isOperator(gameProfile) || this.method_14590().method_14653(gameProfile);
	}

	public MinecraftDedicatedServer method_13938() {
		return (MinecraftDedicatedServer)super.getServer();
	}

	@Override
	public boolean canBypassPlayerLimit(GameProfile gameProfile) {
		return this.method_14603().isOp(gameProfile);
	}
}
