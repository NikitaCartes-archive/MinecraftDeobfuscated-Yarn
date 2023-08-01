package net.minecraft.server.dedicated;

import net.minecraft.network.QueryableServer;

public interface DedicatedServer extends QueryableServer {
	ServerPropertiesHandler getProperties();

	String getHostname();

	int getPort();

	String getMotd();

	String[] getPlayerNames();

	String getLevelName();

	String getPlugins();

	String executeRconCommand(String command);
}
