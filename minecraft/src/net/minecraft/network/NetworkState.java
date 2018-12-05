package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_2695;
import net.minecraft.class_2707;
import net.minecraft.class_2713;
import net.minecraft.class_2757;
import net.minecraft.class_2765;
import net.minecraft.class_2770;
import net.minecraft.class_2774;
import net.minecraft.class_2779;
import net.minecraft.class_2793;
import net.minecraft.class_2795;
import net.minecraft.class_2799;
import net.minecraft.class_2805;
import net.minecraft.class_2809;
import net.minecraft.class_2813;
import net.minecraft.class_2822;
import net.minecraft.class_2827;
import net.minecraft.class_2828;
import net.minecraft.class_2833;
import net.minecraft.class_2836;
import net.minecraft.class_2838;
import net.minecraft.class_2842;
import net.minecraft.class_2848;
import net.minecraft.class_2851;
import net.minecraft.class_2853;
import net.minecraft.class_2855;
import net.minecraft.class_2856;
import net.minecraft.class_2859;
import net.minecraft.class_2863;
import net.minecraft.class_2866;
import net.minecraft.class_2868;
import net.minecraft.class_2870;
import net.minecraft.class_2871;
import net.minecraft.class_2873;
import net.minecraft.class_2875;
import net.minecraft.class_2877;
import net.minecraft.class_2879;
import net.minecraft.class_2884;
import net.minecraft.class_2899;
import net.minecraft.class_2901;
import net.minecraft.class_2905;
import net.minecraft.class_2907;
import net.minecraft.class_2909;
import net.minecraft.class_2913;
import net.minecraft.client.network.packet.BlockActionClientPacket;
import net.minecraft.client.network.packet.BlockBreakingProgressClientPacket;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.client.network.packet.BlockUpdateClientPacket;
import net.minecraft.client.network.packet.BossBarClientPacket;
import net.minecraft.client.network.packet.ChatMessageClientPacket;
import net.minecraft.client.network.packet.ChunkDataClientPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateClientPacket;
import net.minecraft.client.network.packet.CombatEventClientPacket;
import net.minecraft.client.network.packet.CommandSuggestionsClientPacket;
import net.minecraft.client.network.packet.CommandTreeClientPacket;
import net.minecraft.client.network.packet.CooldownUpdateClientPacket;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.client.network.packet.DifficultyClientPacket;
import net.minecraft.client.network.packet.DisconnectClientPacket;
import net.minecraft.client.network.packet.EntitiesDestroyClientPacket;
import net.minecraft.client.network.packet.EntityAnimationClientPacket;
import net.minecraft.client.network.packet.EntityAttachClientPacket;
import net.minecraft.client.network.packet.EntityAttributesClientPacket;
import net.minecraft.client.network.packet.EntityClientPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateClientPacket;
import net.minecraft.client.network.packet.EntityPassengersSetClientPacket;
import net.minecraft.client.network.packet.EntityPositionClientPacket;
import net.minecraft.client.network.packet.EntityPotionEffectClientPacket;
import net.minecraft.client.network.packet.EntitySetHeadYawClientPacket;
import net.minecraft.client.network.packet.EntitySpawnClientPacket;
import net.minecraft.client.network.packet.EntitySpawnGlobalClientPacket;
import net.minecraft.client.network.packet.EntityStatusClientPacket;
import net.minecraft.client.network.packet.EntityTrackerUpdateClientPacket;
import net.minecraft.client.network.packet.EntityVelocityUpdateClientPacket;
import net.minecraft.client.network.packet.ExperienceBarUpdateClientPacket;
import net.minecraft.client.network.packet.ExperienceOrbSpawnClientPacket;
import net.minecraft.client.network.packet.ExplosionClientPacket;
import net.minecraft.client.network.packet.GameJoinClientPacket;
import net.minecraft.client.network.packet.GameStateChangeClientPacket;
import net.minecraft.client.network.packet.GuiActionConfirmClientPacket;
import net.minecraft.client.network.packet.GuiCloseClientPacket;
import net.minecraft.client.network.packet.GuiOpenClientPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateClientPacket;
import net.minecraft.client.network.packet.GuiUpdateClientPacket;
import net.minecraft.client.network.packet.HealthUpdateClientPacket;
import net.minecraft.client.network.packet.HeldItemChangeClientPacket;
import net.minecraft.client.network.packet.InventoryClientPacket;
import net.minecraft.client.network.packet.ItemPickupAnimationClientPacket;
import net.minecraft.client.network.packet.KeepAliveClientPacket;
import net.minecraft.client.network.packet.LightUpdateClientPacket;
import net.minecraft.client.network.packet.MapUpdateClientPacket;
import net.minecraft.client.network.packet.MobSpawnClientPacket;
import net.minecraft.client.network.packet.PaintingSpawnClientPacket;
import net.minecraft.client.network.packet.ParticleClientPacket;
import net.minecraft.client.network.packet.PlaySoundClientPacket;
import net.minecraft.client.network.packet.PlaySoundIdClientPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesClientPacket;
import net.minecraft.client.network.packet.PlayerListClientPacket;
import net.minecraft.client.network.packet.PlayerListHeaderClientPacket;
import net.minecraft.client.network.packet.PlayerPositionLookClientPacket;
import net.minecraft.client.network.packet.PlayerRespawnClientPacket;
import net.minecraft.client.network.packet.PlayerSpawnClientPacket;
import net.minecraft.client.network.packet.PlayerSpawnPositionClientPacket;
import net.minecraft.client.network.packet.PlayerUseBedClientPacket;
import net.minecraft.client.network.packet.QueryPongClientPacket;
import net.minecraft.client.network.packet.QueryResponseClientPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectClientPacket;
import net.minecraft.client.network.packet.ResourcePackSendClientPacket;
import net.minecraft.client.network.packet.ScoreboardDisplayClientPacket;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateClientPacket;
import net.minecraft.client.network.packet.SetCameraEntityClientPacket;
import net.minecraft.client.network.packet.SignEditorOpenClientPacket;
import net.minecraft.client.network.packet.StatisticsClientPacket;
import net.minecraft.client.network.packet.SynchronizeRecipesClientPacket;
import net.minecraft.client.network.packet.SynchronizeTagsClientPacket;
import net.minecraft.client.network.packet.TeamClientPacket;
import net.minecraft.client.network.packet.TitleClientPacket;
import net.minecraft.client.network.packet.UnloadChunkClientPacket;
import net.minecraft.client.network.packet.UnlockRecipesClientPacket;
import net.minecraft.client.network.packet.VehicleMoveClientPacket;
import net.minecraft.client.network.packet.WorldBorderClientPacket;
import net.minecraft.client.network.packet.WorldEventClientPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateClientPacket;
import net.minecraft.server.network.packet.BookUpdateServerPacket;
import net.minecraft.server.network.packet.ChatMessageServerPacket;
import net.minecraft.server.network.packet.ClientSettingsServerPacket;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.server.network.packet.GuiCloseServerPacket;
import net.minecraft.server.network.packet.HandshakeServerPacket;
import net.minecraft.server.network.packet.LoginKeyServerPacket;
import net.minecraft.server.network.packet.PlayerActionServerPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockServerPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityServerPacket;
import net.minecraft.server.network.packet.PlayerInteractItemServerPacket;
import net.minecraft.server.network.packet.QueryPingServerPacket;
import net.minecraft.server.network.packet.QueryRequestServerPacket;
import net.minecraft.server.network.packet.RecipeClickServerPacket;
import net.minecraft.server.packet.LoginHelloServerPacket;
import org.apache.logging.log4j.LogManager;

public enum NetworkState {
	HANDSHAKE(-1) {
		{
			this.method_10784(NetworkSide.SERVER, HandshakeServerPacket.class);
		}
	},
	GAME(0) {
		{
			this.method_10784(NetworkSide.CLIENT, EntitySpawnClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, ExperienceOrbSpawnClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntitySpawnGlobalClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, MobSpawnClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, PaintingSpawnClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, PlayerSpawnClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntityAnimationClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, StatisticsClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, BlockBreakingProgressClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, BlockEntityUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, BlockActionClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, BlockUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, BossBarClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, DifficultyClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, ChatMessageClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, ChunkDeltaUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, CommandSuggestionsClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, CommandTreeClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, GuiActionConfirmClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, GuiCloseClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, GuiOpenClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, InventoryClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, GuiUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, GuiSlotUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, CooldownUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, CustomPayloadClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, PlaySoundIdClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, DisconnectClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntityStatusClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, class_2774.class);
			this.method_10784(NetworkSide.CLIENT, ExplosionClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, UnloadChunkClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, GameStateChangeClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, KeepAliveClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, ChunkDataClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, WorldEventClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, ParticleClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, GameJoinClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, MapUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntityClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntityClientPacket.MoveRelative.class);
			this.method_10784(NetworkSide.CLIENT, EntityClientPacket.RotateAndMoveRelative.class);
			this.method_10784(NetworkSide.CLIENT, EntityClientPacket.Rotate.class);
			this.method_10784(NetworkSide.CLIENT, VehicleMoveClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, SignEditorOpenClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, class_2695.class);
			this.method_10784(NetworkSide.CLIENT, PlayerAbilitiesClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, CombatEventClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, PlayerListClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, class_2707.class);
			this.method_10784(NetworkSide.CLIENT, PlayerPositionLookClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, PlayerUseBedClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, class_2713.class);
			this.method_10784(NetworkSide.CLIENT, EntitiesDestroyClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, RemoveEntityEffectClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, ResourcePackSendClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, PlayerRespawnClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntitySetHeadYawClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, UnlockRecipesClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, WorldBorderClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, SetCameraEntityClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, HeldItemChangeClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, ScoreboardDisplayClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntityTrackerUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntityAttachClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntityVelocityUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntityEquipmentUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, ExperienceBarUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, HealthUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, ScoreboardObjectiveUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntityPassengersSetClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, TeamClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, class_2757.class);
			this.method_10784(NetworkSide.CLIENT, PlayerSpawnPositionClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, WorldTimeUpdateClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, TitleClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, class_2770.class);
			this.method_10784(NetworkSide.CLIENT, PlaySoundClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, class_2765.class);
			this.method_10784(NetworkSide.CLIENT, PlayerListHeaderClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, ItemPickupAnimationClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntityPositionClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, class_2779.class);
			this.method_10784(NetworkSide.CLIENT, EntityAttributesClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, EntityPotionEffectClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, SynchronizeRecipesClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, SynchronizeTagsClientPacket.class);
			this.method_10784(NetworkSide.CLIENT, LightUpdateClientPacket.class);
			this.method_10784(NetworkSide.SERVER, class_2793.class);
			this.method_10784(NetworkSide.SERVER, class_2795.class);
			this.method_10784(NetworkSide.SERVER, ChatMessageServerPacket.class);
			this.method_10784(NetworkSide.SERVER, class_2799.class);
			this.method_10784(NetworkSide.SERVER, ClientSettingsServerPacket.class);
			this.method_10784(NetworkSide.SERVER, class_2805.class);
			this.method_10784(NetworkSide.SERVER, class_2809.class);
			this.method_10784(NetworkSide.SERVER, ButtonClickServerPacket.class);
			this.method_10784(NetworkSide.SERVER, class_2813.class);
			this.method_10784(NetworkSide.SERVER, GuiCloseServerPacket.class);
			this.method_10784(NetworkSide.SERVER, CustomPayloadServerPacket.class);
			this.method_10784(NetworkSide.SERVER, BookUpdateServerPacket.class);
			this.method_10784(NetworkSide.SERVER, class_2822.class);
			this.method_10784(NetworkSide.SERVER, PlayerInteractEntityServerPacket.class);
			this.method_10784(NetworkSide.SERVER, class_2827.class);
			this.method_10784(NetworkSide.SERVER, class_2828.class);
			this.method_10784(NetworkSide.SERVER, class_2828.class_2829.class);
			this.method_10784(NetworkSide.SERVER, class_2828.class_2830.class);
			this.method_10784(NetworkSide.SERVER, class_2828.class_2831.class);
			this.method_10784(NetworkSide.SERVER, class_2833.class);
			this.method_10784(NetworkSide.SERVER, class_2836.class);
			this.method_10784(NetworkSide.SERVER, class_2838.class);
			this.method_10784(NetworkSide.SERVER, RecipeClickServerPacket.class);
			this.method_10784(NetworkSide.SERVER, class_2842.class);
			this.method_10784(NetworkSide.SERVER, PlayerActionServerPacket.class);
			this.method_10784(NetworkSide.SERVER, class_2848.class);
			this.method_10784(NetworkSide.SERVER, class_2851.class);
			this.method_10784(NetworkSide.SERVER, class_2853.class);
			this.method_10784(NetworkSide.SERVER, class_2855.class);
			this.method_10784(NetworkSide.SERVER, class_2856.class);
			this.method_10784(NetworkSide.SERVER, class_2859.class);
			this.method_10784(NetworkSide.SERVER, class_2863.class);
			this.method_10784(NetworkSide.SERVER, class_2866.class);
			this.method_10784(NetworkSide.SERVER, class_2868.class);
			this.method_10784(NetworkSide.SERVER, class_2870.class);
			this.method_10784(NetworkSide.SERVER, class_2871.class);
			this.method_10784(NetworkSide.SERVER, class_2873.class);
			this.method_10784(NetworkSide.SERVER, class_2875.class);
			this.method_10784(NetworkSide.SERVER, class_2877.class);
			this.method_10784(NetworkSide.SERVER, class_2879.class);
			this.method_10784(NetworkSide.SERVER, class_2884.class);
			this.method_10784(NetworkSide.SERVER, PlayerInteractBlockServerPacket.class);
			this.method_10784(NetworkSide.SERVER, PlayerInteractItemServerPacket.class);
		}
	},
	QUERY(1) {
		{
			this.method_10784(NetworkSide.SERVER, QueryRequestServerPacket.class);
			this.method_10784(NetworkSide.CLIENT, QueryResponseClientPacket.class);
			this.method_10784(NetworkSide.SERVER, QueryPingServerPacket.class);
			this.method_10784(NetworkSide.CLIENT, QueryPongClientPacket.class);
		}
	},
	LOGIN(2) {
		{
			this.method_10784(NetworkSide.CLIENT, class_2909.class);
			this.method_10784(NetworkSide.CLIENT, class_2905.class);
			this.method_10784(NetworkSide.CLIENT, class_2901.class);
			this.method_10784(NetworkSide.CLIENT, class_2907.class);
			this.method_10784(NetworkSide.CLIENT, class_2899.class);
			this.method_10784(NetworkSide.SERVER, LoginHelloServerPacket.class);
			this.method_10784(NetworkSide.SERVER, LoginKeyServerPacket.class);
			this.method_10784(NetworkSide.SERVER, class_2913.class);
		}
	};

	private static final NetworkState[] STATES = new NetworkState[4];
	private static final Map<Class<? extends Packet<?>>, NetworkState> HANDLER_STATE_MAP = Maps.<Class<? extends Packet<?>>, NetworkState>newHashMap();
	private final int id;
	private final Map<NetworkSide, BiMap<Integer, Class<? extends Packet<?>>>> packetHandlerMap = Maps.newEnumMap(NetworkSide.class);

	private NetworkState(int j) {
		this.id = j;
	}

	protected NetworkState method_10784(NetworkSide networkSide, Class<? extends Packet<?>> class_) {
		BiMap<Integer, Class<? extends Packet<?>>> biMap = (BiMap<Integer, Class<? extends Packet<?>>>)this.packetHandlerMap.get(networkSide);
		if (biMap == null) {
			biMap = HashBiMap.create();
			this.packetHandlerMap.put(networkSide, biMap);
		}

		if (biMap.containsValue(class_)) {
			String string = networkSide + " packet " + class_ + " is already known to ID " + biMap.inverse().get(class_);
			LogManager.getLogger().fatal(string);
			throw new IllegalArgumentException(string);
		} else {
			biMap.put(biMap.size(), class_);
			return this;
		}
	}

	public Integer getPacketId(NetworkSide networkSide, Packet<?> packet) throws Exception {
		return (Integer)((BiMap)this.packetHandlerMap.get(networkSide)).inverse().get(packet.getClass());
	}

	@Nullable
	public Packet<?> getPacketHandler(NetworkSide networkSide, int i) throws IllegalAccessException, InstantiationException {
		Class<? extends Packet<?>> class_ = (Class<? extends Packet<?>>)((BiMap)this.packetHandlerMap.get(networkSide)).get(i);
		return class_ == null ? null : (Packet)class_.newInstance();
	}

	public int getId() {
		return this.id;
	}

	public static NetworkState byId(int i) {
		return i >= -1 && i <= 2 ? STATES[i - -1] : null;
	}

	public static NetworkState getPacketHandlerState(Packet<?> packet) {
		return (NetworkState)HANDLER_STATE_MAP.get(packet.getClass());
	}

	static {
		for (NetworkState networkState : values()) {
			int i = networkState.getId();
			if (i < -1 || i > 2) {
				throw new Error("Invalid protocol ID " + Integer.toString(i));
			}

			STATES[i - -1] = networkState;

			for (NetworkSide networkSide : networkState.packetHandlerMap.keySet()) {
				for (Class<? extends Packet<?>> class_ : ((BiMap)networkState.packetHandlerMap.get(networkSide)).values()) {
					if (HANDLER_STATE_MAP.containsKey(class_) && HANDLER_STATE_MAP.get(class_) != networkState) {
						throw new Error("Packet " + class_ + " is already assigned to protocol " + HANDLER_STATE_MAP.get(class_) + " - can't reassign to " + networkState);
					}

					try {
						class_.newInstance();
					} catch (Throwable var10) {
						throw new Error("Packet " + class_ + " fails instantiation checks! " + class_);
					}

					HANDLER_STATE_MAP.put(class_, networkState);
				}
			}
		}
	}
}
