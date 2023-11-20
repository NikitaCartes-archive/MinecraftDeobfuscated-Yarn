package net.minecraft.client.resource.server;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Downloader;

@Environment(EnvType.CLIENT)
public interface DownloadQueuer {
	void enqueue(Map<UUID, Downloader.DownloadEntry> entries, Consumer<Downloader.DownloadResult> callback);
}
