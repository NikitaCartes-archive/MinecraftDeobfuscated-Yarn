package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.class_3806;
import net.minecraft.server.PlayerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedPlayerManager extends PlayerManager {
	private static final Logger field_13804 = LogManager.getLogger();

	public DedicatedPlayerManager(MinecraftDedicatedServer minecraftDedicatedServer) {
		super(minecraftDedicatedServer, minecraftDedicatedServer.method_16705().field_16814);
		class_3806 lv = minecraftDedicatedServer.method_16705();
		this.setViewDistance(lv.field_16844);
		super.setWhitelistEnabled(lv.field_16804.get());
		if (!minecraftDedicatedServer.isSinglePlayer()) {
			this.getUserBanList().setEnabled(true);
			this.getIpBanList().setEnabled(true);
		}

		this.loadUserBanList();
		this.saveUserBanList();
		this.loadIpBanList();
		this.saveIpBanList();
		this.loadOpList();
		this.loadWhitelist();
		this.saveOpList();
		if (!this.getWhitelist().getFile().exists()) {
			this.saveWhitelist();
		}
	}

	@Override
	public void setWhitelistEnabled(boolean bl) {
		super.setWhitelistEnabled(bl);
		this.getDedicatedServer().method_16712(bl);
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
			this.getUserBanList().save();
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
			this.getUserBanList().load();
		} catch (IOException var2) {
			field_13804.warn("Failed to load user banlist: ", (Throwable)var2);
		}
	}

	private void loadOpList() {
		try {
			this.getOpList().load();
		} catch (Exception var2) {
			field_13804.warn("Failed to load operators list: ", (Throwable)var2);
		}
	}

	private void saveOpList() {
		try {
			this.getOpList().save();
		} catch (Exception var2) {
			field_13804.warn("Failed to save operators list: ", (Throwable)var2);
		}
	}

	private void loadWhitelist() {
		try {
			this.getWhitelist().load();
		} catch (Exception var2) {
			field_13804.warn("Failed to load white-list: ", (Throwable)var2);
		}
	}

	private void saveWhitelist() {
		try {
			this.getWhitelist().save();
		} catch (Exception var2) {
			field_13804.warn("Failed to save white-list: ", (Throwable)var2);
		}
	}

	@Override
	public boolean isWhitelisted(GameProfile gameProfile) {
		return !this.isWhitelistEnabled() || this.isOperator(gameProfile) || this.getWhitelist().method_14653(gameProfile);
	}

	public MinecraftDedicatedServer getDedicatedServer() {
		return (MinecraftDedicatedServer)super.getServer();
	}

	@Override
	public boolean canBypassPlayerLimit(GameProfile gameProfile) {
		return this.getOpList().isOp(gameProfile);
	}
}
