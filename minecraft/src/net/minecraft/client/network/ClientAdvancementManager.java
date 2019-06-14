package net.minecraft.client.network;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
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
	private final Map<Advancement, AdvancementProgress> advancementProgresses = Maps.<Advancement, AdvancementProgress>newHashMap();
	@Nullable
	private ClientAdvancementManager.Listener listener;
	@Nullable
	private Advancement selectedTab;

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
			Advancement advancement = this.manager.get((Identifier)entry.getKey());
			if (advancement != null) {
				AdvancementProgress advancementProgress = (AdvancementProgress)entry.getValue();
				advancementProgress.init(advancement.getCriteria(), advancement.getRequirements());
				this.advancementProgresses.put(advancement, advancementProgress);
				if (this.listener != null) {
					this.listener.setProgress(advancement, advancementProgress);
				}

				if (!advancementUpdateS2CPacket.shouldClearCurrent()
					&& advancementProgress.isDone()
					&& advancement.getDisplay() != null
					&& advancement.getDisplay().shouldShowToast()) {
					this.client.method_1566().add(new AdvancementToast(advancement));
				}
			} else {
				LOGGER.warn("Server informed client about progress for unknown advancement {}", entry.getKey());
			}
		}
	}

	public AdvancementManager getManager() {
		return this.manager;
	}

	public void selectTab(@Nullable Advancement advancement, boolean bl) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.method_1562();
		if (clientPlayNetworkHandler != null && advancement != null && bl) {
			clientPlayNetworkHandler.sendPacket(AdvancementTabC2SPacket.open(advancement));
		}

		if (this.selectedTab != advancement) {
			this.selectedTab = advancement;
			if (this.listener != null) {
				this.listener.selectTab(advancement);
			}
		}
	}

	public void setListener(@Nullable ClientAdvancementManager.Listener listener) {
		this.listener = listener;
		this.manager.setListener(listener);
		if (listener != null) {
			for (Entry<Advancement, AdvancementProgress> entry : this.advancementProgresses.entrySet()) {
				listener.setProgress((Advancement)entry.getKey(), (AdvancementProgress)entry.getValue());
			}

			listener.selectTab(this.selectedTab);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface Listener extends AdvancementManager.Listener {
		void setProgress(Advancement advancement, AdvancementProgress advancementProgress);

		void selectTab(@Nullable Advancement advancement);
	}
}
