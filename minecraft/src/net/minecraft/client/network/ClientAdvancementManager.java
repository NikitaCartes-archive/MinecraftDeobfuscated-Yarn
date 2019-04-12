package net.minecraft.client.network;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.AdvancementUpdateS2CPacket;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientAdvancementManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final AdvancementManager manager = new AdvancementManager();
	private final Map<SimpleAdvancement, AdvancementProgress> advancementProgresses = Maps.<SimpleAdvancement, AdvancementProgress>newHashMap();
	@Nullable
	private ClientAdvancementManager.Listener listener;
	@Nullable
	private SimpleAdvancement selectedTab;

	public ClientAdvancementManager(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void onAdvancements(AdvancementUpdateS2CPacket advancementUpdateS2CPacket) {
		if (advancementUpdateS2CPacket.shouldClearCurrent()) {
			this.manager.clear();
			this.advancementProgresses.clear();
		}

		this.manager.removeAll(advancementUpdateS2CPacket.getAdvancementIdsToRemove());
		this.manager.load(advancementUpdateS2CPacket.getAdvancementsToEarn());

		for (Entry<Identifier, AdvancementProgress> entry : advancementUpdateS2CPacket.getAdvancementsToProgress().entrySet()) {
			SimpleAdvancement simpleAdvancement = this.manager.get((Identifier)entry.getKey());
			if (simpleAdvancement != null) {
				AdvancementProgress advancementProgress = (AdvancementProgress)entry.getValue();
				advancementProgress.init(simpleAdvancement.getCriteria(), simpleAdvancement.getRequirements());
				this.advancementProgresses.put(simpleAdvancement, advancementProgress);
				if (this.listener != null) {
					this.listener.setProgress(simpleAdvancement, advancementProgress);
				}

				if (!advancementUpdateS2CPacket.shouldClearCurrent()
					&& advancementProgress.isDone()
					&& simpleAdvancement.getDisplay() != null
					&& simpleAdvancement.getDisplay().shouldShowToast()) {
					this.client.getToastManager().add(new AdvancementToast(simpleAdvancement));
				}
			} else {
				LOGGER.warn("Server informed client about progress for unknown advancement {}", entry.getKey());
			}
		}
	}

	public AdvancementManager getManager() {
		return this.manager;
	}

	public void selectTab(@Nullable SimpleAdvancement simpleAdvancement, boolean bl) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler != null && simpleAdvancement != null && bl) {
			clientPlayNetworkHandler.sendPacket(AdvancementTabC2SPacket.open(simpleAdvancement));
		}

		if (this.selectedTab != simpleAdvancement) {
			this.selectedTab = simpleAdvancement;
			if (this.listener != null) {
				this.listener.selectTab(simpleAdvancement);
			}
		}
	}

	public void setListener(@Nullable ClientAdvancementManager.Listener listener) {
		this.listener = listener;
		this.manager.setListener(listener);
		if (listener != null) {
			for (Entry<SimpleAdvancement, AdvancementProgress> entry : this.advancementProgresses.entrySet()) {
				listener.setProgress((SimpleAdvancement)entry.getKey(), (AdvancementProgress)entry.getValue());
			}

			listener.selectTab(this.selectedTab);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface Listener extends AdvancementManager.Listener {
		void setProgress(SimpleAdvancement simpleAdvancement, AdvancementProgress advancementProgress);

		void selectTab(@Nullable SimpleAdvancement simpleAdvancement);
	}
}
