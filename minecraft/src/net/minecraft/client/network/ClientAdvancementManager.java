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
	private final Map<SimpleAdvancement, AdvancementProgress> field_3681 = Maps.<SimpleAdvancement, AdvancementProgress>newHashMap();
	@Nullable
	private ClientAdvancementManager.class_633 field_3682;
	@Nullable
	private SimpleAdvancement field_3685;

	public ClientAdvancementManager(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void method_2861(AdvancementUpdateS2CPacket advancementUpdateS2CPacket) {
		if (advancementUpdateS2CPacket.shouldClearCurrent()) {
			this.manager.clear();
			this.field_3681.clear();
		}

		this.manager.removeAll(advancementUpdateS2CPacket.getAdvancementIdsToRemove());
		this.manager.load(advancementUpdateS2CPacket.getAdvancementsToEarn());

		for (Entry<Identifier, AdvancementProgress> entry : advancementUpdateS2CPacket.getAdvancementsToProgress().entrySet()) {
			SimpleAdvancement simpleAdvancement = this.manager.get((Identifier)entry.getKey());
			if (simpleAdvancement != null) {
				AdvancementProgress advancementProgress = (AdvancementProgress)entry.getValue();
				advancementProgress.init(simpleAdvancement.getCriteria(), simpleAdvancement.getRequirements());
				this.field_3681.put(simpleAdvancement, advancementProgress);
				if (this.field_3682 != null) {
					this.field_3682.method_2865(simpleAdvancement, advancementProgress);
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

		if (this.field_3685 != simpleAdvancement) {
			this.field_3685 = simpleAdvancement;
			if (this.field_3682 != null) {
				this.field_3682.method_2866(simpleAdvancement);
			}
		}
	}

	public void setGui(@Nullable ClientAdvancementManager.class_633 arg) {
		this.field_3682 = arg;
		this.manager.setListener(arg);
		if (arg != null) {
			for (Entry<SimpleAdvancement, AdvancementProgress> entry : this.field_3681.entrySet()) {
				arg.method_2865((SimpleAdvancement)entry.getKey(), (AdvancementProgress)entry.getValue());
			}

			arg.method_2866(this.field_3685);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface class_633 extends AdvancementManager.Listener {
		void method_2865(SimpleAdvancement simpleAdvancement, AdvancementProgress advancementProgress);

		void method_2866(@Nullable SimpleAdvancement simpleAdvancement);
	}
}
