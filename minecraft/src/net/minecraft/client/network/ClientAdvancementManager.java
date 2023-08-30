package net.minecraft.client.network;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientAdvancementManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final MinecraftClient client;
	private final WorldSession worldSession;
	private final AdvancementManager manager = new AdvancementManager();
	private final Map<AdvancementEntry, AdvancementProgress> advancementProgresses = new Object2ObjectOpenHashMap<>();
	@Nullable
	private ClientAdvancementManager.Listener listener;
	@Nullable
	private AdvancementEntry selectedTab;

	public ClientAdvancementManager(MinecraftClient client, WorldSession worldSession) {
		this.client = client;
		this.worldSession = worldSession;
	}

	public void onAdvancements(AdvancementUpdateS2CPacket packet) {
		if (packet.shouldClearCurrent()) {
			this.manager.clear();
			this.advancementProgresses.clear();
		}

		this.manager.removeAll(packet.getAdvancementIdsToRemove());
		this.manager.addAll(packet.getAdvancementsToEarn());

		for (Entry<Identifier, AdvancementProgress> entry : packet.getAdvancementsToProgress().entrySet()) {
			PlacedAdvancement placedAdvancement = this.manager.get((Identifier)entry.getKey());
			if (placedAdvancement != null) {
				AdvancementProgress advancementProgress = (AdvancementProgress)entry.getValue();
				advancementProgress.init(placedAdvancement.getAdvancement().requirements());
				this.advancementProgresses.put(placedAdvancement.getAdvancementEntry(), advancementProgress);
				if (this.listener != null) {
					this.listener.setProgress(placedAdvancement, advancementProgress);
				}

				if (!packet.shouldClearCurrent() && advancementProgress.isDone()) {
					if (this.client.world != null) {
						this.worldSession.onAdvancementMade(this.client.world, placedAdvancement.getAdvancementEntry());
					}

					Optional<AdvancementDisplay> optional = placedAdvancement.getAdvancement().display();
					if (optional.isPresent() && ((AdvancementDisplay)optional.get()).shouldShowToast()) {
						this.client.getToastManager().add(new AdvancementToast(placedAdvancement.getAdvancementEntry()));
					}
				}
			} else {
				LOGGER.warn("Server informed client about progress for unknown advancement {}", entry.getKey());
			}
		}
	}

	public AdvancementManager getManager() {
		return this.manager;
	}

	public void selectTab(@Nullable AdvancementEntry tab, boolean local) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler != null && tab != null && local) {
			clientPlayNetworkHandler.sendPacket(AdvancementTabC2SPacket.open(tab));
		}

		if (this.selectedTab != tab) {
			this.selectedTab = tab;
			if (this.listener != null) {
				this.listener.selectTab(tab);
			}
		}
	}

	public void setListener(@Nullable ClientAdvancementManager.Listener listener) {
		this.listener = listener;
		this.manager.setListener(listener);
		if (listener != null) {
			this.advancementProgresses.forEach((advancement, progress) -> {
				PlacedAdvancement placedAdvancement = this.manager.get(advancement);
				if (placedAdvancement != null) {
					listener.setProgress(placedAdvancement, progress);
				}
			});
			listener.selectTab(this.selectedTab);
		}
	}

	@Nullable
	public AdvancementEntry get(Identifier id) {
		PlacedAdvancement placedAdvancement = this.manager.get(id);
		return placedAdvancement != null ? placedAdvancement.getAdvancementEntry() : null;
	}

	@Environment(EnvType.CLIENT)
	public interface Listener extends AdvancementManager.Listener {
		void setProgress(PlacedAdvancement advancement, AdvancementProgress progress);

		void selectTab(@Nullable AdvancementEntry advancement);
	}
}
