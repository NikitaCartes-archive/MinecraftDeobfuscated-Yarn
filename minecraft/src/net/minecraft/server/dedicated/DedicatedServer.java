package net.minecraft.server.dedicated;

public interface DedicatedServer {
	ServerPropertiesHandler getProperties();

	String getHostname();

	int getPort();

	String getMotd();

	String getVersion();

	int getCurrentPlayerCount();

	int getMaxPlayerCount();

	String[] getPlayerNames();

	String getLevelName();

	String getPlugins();

	String executeRconCommand(String command);
}
