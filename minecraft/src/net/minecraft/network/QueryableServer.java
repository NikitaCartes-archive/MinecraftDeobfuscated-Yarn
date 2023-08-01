package net.minecraft.network;

public interface QueryableServer {
	String getServerMotd();

	String getVersion();

	int getCurrentPlayerCount();

	int getMaxPlayerCount();
}
