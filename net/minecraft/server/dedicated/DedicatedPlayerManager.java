/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedPlayerManager
extends PlayerManager {
    private static final Logger LOGGER = LogManager.getLogger();

    public DedicatedPlayerManager(MinecraftDedicatedServer minecraftDedicatedServer, DimensionTracker.Modifiable modifiable, WorldSaveHandler worldSaveHandler) {
        super(minecraftDedicatedServer, modifiable, worldSaveHandler, minecraftDedicatedServer.getProperties().maxPlayers);
        ServerPropertiesHandler serverPropertiesHandler = minecraftDedicatedServer.getProperties();
        this.setViewDistance(serverPropertiesHandler.viewDistance);
        super.setWhitelistEnabled(serverPropertiesHandler.whiteList.get());
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
    public void setWhitelistEnabled(boolean whitelistEnabled) {
        super.setWhitelistEnabled(whitelistEnabled);
        this.getServer().setUseWhitelist(whitelistEnabled);
    }

    @Override
    public void addToOperators(GameProfile profile) {
        super.addToOperators(profile);
        this.saveOpList();
    }

    @Override
    public void removeFromOperators(GameProfile profile) {
        super.removeFromOperators(profile);
        this.saveOpList();
    }

    @Override
    public void reloadWhitelist() {
        this.loadWhitelist();
    }

    private void saveIpBanList() {
        try {
            this.getIpBanList().save();
        } catch (IOException iOException) {
            LOGGER.warn("Failed to save ip banlist: ", (Throwable)iOException);
        }
    }

    private void saveUserBanList() {
        try {
            this.getUserBanList().save();
        } catch (IOException iOException) {
            LOGGER.warn("Failed to save user banlist: ", (Throwable)iOException);
        }
    }

    private void loadIpBanList() {
        try {
            this.getIpBanList().load();
        } catch (IOException iOException) {
            LOGGER.warn("Failed to load ip banlist: ", (Throwable)iOException);
        }
    }

    private void loadUserBanList() {
        try {
            this.getUserBanList().load();
        } catch (IOException iOException) {
            LOGGER.warn("Failed to load user banlist: ", (Throwable)iOException);
        }
    }

    private void loadOpList() {
        try {
            this.getOpList().load();
        } catch (Exception exception) {
            LOGGER.warn("Failed to load operators list: ", (Throwable)exception);
        }
    }

    private void saveOpList() {
        try {
            this.getOpList().save();
        } catch (Exception exception) {
            LOGGER.warn("Failed to save operators list: ", (Throwable)exception);
        }
    }

    private void loadWhitelist() {
        try {
            this.getWhitelist().load();
        } catch (Exception exception) {
            LOGGER.warn("Failed to load white-list: ", (Throwable)exception);
        }
    }

    private void saveWhitelist() {
        try {
            this.getWhitelist().save();
        } catch (Exception exception) {
            LOGGER.warn("Failed to save white-list: ", (Throwable)exception);
        }
    }

    @Override
    public boolean isWhitelisted(GameProfile profile) {
        return !this.isWhitelistEnabled() || this.isOperator(profile) || this.getWhitelist().isAllowed(profile);
    }

    @Override
    public MinecraftDedicatedServer getServer() {
        return (MinecraftDedicatedServer)super.getServer();
    }

    @Override
    public boolean canBypassPlayerLimit(GameProfile profile) {
        return this.getOpList().isOp(profile);
    }

    @Override
    public /* synthetic */ MinecraftServer getServer() {
        return this.getServer();
    }
}

