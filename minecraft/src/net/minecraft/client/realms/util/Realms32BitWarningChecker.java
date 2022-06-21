package net.minecraft.client.realms.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Realms32BitWarningScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class Realms32BitWarningChecker {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final MinecraftClient client;
	@Nullable
	private CompletableFuture<Boolean> subscriptionRetrievalFuture;
	private boolean skipDisplay;

	public Realms32BitWarningChecker(MinecraftClient client) {
		this.client = client;
	}

	public void showWarningIfNeeded(Screen parent) {
		if (!this.client.is64Bit() && !this.client.options.skipRealms32BitWarning && !this.skipDisplay && this.isSubscribed()) {
			this.client.setScreen(new Realms32BitWarningScreen(parent));
			this.skipDisplay = true;
		}
	}

	private Boolean isSubscribed() {
		if (this.subscriptionRetrievalFuture == null) {
			this.subscriptionRetrievalFuture = CompletableFuture.supplyAsync(this::isSubscribedInternal, Util.getMainWorkerExecutor());
		}

		try {
			return (Boolean)this.subscriptionRetrievalFuture.getNow(false);
		} catch (CompletionException var2) {
			LOGGER.warn("Failed to retrieve realms subscriptions", (Throwable)var2);
			this.skipDisplay = true;
			return false;
		}
	}

	private boolean isSubscribedInternal() {
		try {
			return RealmsClient.createRealmsClient(this.client)
				.listWorlds()
				.servers
				.stream()
				.anyMatch(server -> server.ownerUUID != null && !server.expired && server.ownerUUID.equals(this.client.getSession().getUuid()));
		} catch (RealmsServiceException var2) {
			return false;
		}
	}
}
