package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_4463;
import net.minecraft.client.network.packet.AdvancementUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockActionS2CPacket;
import net.minecraft.client.network.packet.BlockBreakingProgressS2CPacket;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.client.network.packet.BossBarS2CPacket;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.ChunkDataS2CPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateS2CPacket;
import net.minecraft.client.network.packet.ChunkLoadDistanceS2CPacket;
import net.minecraft.client.network.packet.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.client.network.packet.CombatEventS2CPacket;
import net.minecraft.client.network.packet.CommandSuggestionsS2CPacket;
import net.minecraft.client.network.packet.CommandTreeS2CPacket;
import net.minecraft.client.network.packet.ConfirmGuiActionS2CPacket;
import net.minecraft.client.network.packet.CooldownUpdateS2CPacket;
import net.minecraft.client.network.packet.CraftResponseS2CPacket;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.client.network.packet.EntitiesDestroyS2CPacket;
import net.minecraft.client.network.packet.EntityAnimationS2CPacket;
import net.minecraft.client.network.packet.EntityAttachS2CPacket;
import net.minecraft.client.network.packet.EntityAttributesS2CPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityPassengersSetS2CPacket;
import net.minecraft.client.network.packet.EntityPositionS2CPacket;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.client.network.packet.EntityS2CPacket;
import net.minecraft.client.network.packet.EntitySetHeadYawS2CPacket;
import net.minecraft.client.network.packet.EntitySpawnGlobalS2CPacket;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.client.network.packet.EntityTrackerUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityVelocityUpdateS2CPacket;
import net.minecraft.client.network.packet.ExperienceBarUpdateS2CPacket;
import net.minecraft.client.network.packet.ExperienceOrbSpawnS2CPacket;
import net.minecraft.client.network.packet.ExplosionS2CPacket;
import net.minecraft.client.network.packet.GameJoinS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.client.network.packet.GuiCloseS2CPacket;
import net.minecraft.client.network.packet.GuiOpenS2CPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.client.network.packet.GuiUpdateS2CPacket;
import net.minecraft.client.network.packet.HealthUpdateS2CPacket;
import net.minecraft.client.network.packet.HeldItemChangeS2CPacket;
import net.minecraft.client.network.packet.InventoryS2CPacket;
import net.minecraft.client.network.packet.ItemPickupAnimationS2CPacket;
import net.minecraft.client.network.packet.KeepAliveS2CPacket;
import net.minecraft.client.network.packet.LightUpdateS2CPacket;
import net.minecraft.client.network.packet.LoginCompressionS2CPacket;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.client.network.packet.LoginHelloS2CPacket;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.client.network.packet.LoginSuccessS2CPacket;
import net.minecraft.client.network.packet.LookAtS2CPacket;
import net.minecraft.client.network.packet.MapUpdateS2CPacket;
import net.minecraft.client.network.packet.MobSpawnS2CPacket;
import net.minecraft.client.network.packet.OpenContainerPacket;
import net.minecraft.client.network.packet.OpenWrittenBookS2CPacket;
import net.minecraft.client.network.packet.PaintingSpawnS2CPacket;
import net.minecraft.client.network.packet.ParticleS2CPacket;
import net.minecraft.client.network.packet.PlaySoundFromEntityS2CPacket;
import net.minecraft.client.network.packet.PlaySoundIdS2CPacket;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.client.network.packet.PlayerListHeaderS2CPacket;
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket;
import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnPositionS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnS2CPacket;
import net.minecraft.client.network.packet.QueryPongS2CPacket;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectS2CPacket;
import net.minecraft.client.network.packet.ResourcePackSendS2CPacket;
import net.minecraft.client.network.packet.ScoreboardDisplayS2CPacket;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.client.network.packet.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.client.network.packet.SelectAdvancementTabS2CPacket;
import net.minecraft.client.network.packet.SetCameraEntityS2CPacket;
import net.minecraft.client.network.packet.SetTradeOffersPacket;
import net.minecraft.client.network.packet.SignEditorOpenS2CPacket;
import net.minecraft.client.network.packet.StatisticsS2CPacket;
import net.minecraft.client.network.packet.StopSoundS2CPacket;
import net.minecraft.client.network.packet.SynchronizeRecipesS2CPacket;
import net.minecraft.client.network.packet.SynchronizeTagsS2CPacket;
import net.minecraft.client.network.packet.TagQueryResponseS2CPacket;
import net.minecraft.client.network.packet.TeamS2CPacket;
import net.minecraft.client.network.packet.TitleS2CPacket;
import net.minecraft.client.network.packet.UnloadChunkS2CPacket;
import net.minecraft.client.network.packet.UnlockRecipesS2CPacket;
import net.minecraft.client.network.packet.VehicleMoveS2CPacket;
import net.minecraft.client.network.packet.WorldBorderS2CPacket;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateS2CPacket;
import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import net.minecraft.server.network.packet.BoatPaddleStateC2SPacket;
import net.minecraft.server.network.packet.BookUpdateC2SPacket;
import net.minecraft.server.network.packet.ButtonClickC2SPacket;
import net.minecraft.server.network.packet.ChatMessageC2SPacket;
import net.minecraft.server.network.packet.ClickWindowC2SPacket;
import net.minecraft.server.network.packet.ClientCommandC2SPacket;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.server.network.packet.CraftRequestC2SPacket;
import net.minecraft.server.network.packet.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.network.packet.GuiActionConfirmC2SPacket;
import net.minecraft.server.network.packet.GuiCloseC2SPacket;
import net.minecraft.server.network.packet.HandSwingC2SPacket;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.server.network.packet.KeepAliveC2SPacket;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.server.network.packet.LoginKeyC2SPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.server.network.packet.PickFromInventoryC2SPacket;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.network.packet.PlayerInputC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.packet.PlayerMoveC2SPacket;
import net.minecraft.server.network.packet.QueryBlockNbtC2SPacket;
import net.minecraft.server.network.packet.QueryEntityNbtC2SPacket;
import net.minecraft.server.network.packet.QueryPingC2SPacket;
import net.minecraft.server.network.packet.QueryRequestC2SPacket;
import net.minecraft.server.network.packet.RecipeBookDataC2SPacket;
import net.minecraft.server.network.packet.RenameItemC2SPacket;
import net.minecraft.server.network.packet.RequestCommandCompletionsC2SPacket;
import net.minecraft.server.network.packet.ResourcePackStatusC2SPacket;
import net.minecraft.server.network.packet.SelectVillagerTradeC2SPacket;
import net.minecraft.server.network.packet.SpectatorTeleportC2SPacket;
import net.minecraft.server.network.packet.TeleportConfirmC2SPacket;
import net.minecraft.server.network.packet.UpdateBeaconC2SPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockC2SPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.server.network.packet.UpdateDifficultyC2SPacket;
import net.minecraft.server.network.packet.UpdateDifficultyLockC2SPacket;
import net.minecraft.server.network.packet.UpdateJigsawC2SPacket;
import net.minecraft.server.network.packet.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.packet.UpdateSignC2SPacket;
import net.minecraft.server.network.packet.UpdateStructureBlockC2SPacket;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;
import org.apache.logging.log4j.LogManager;

public enum NetworkState {
	HANDSHAKING(-1) {
		{
			this.addPacket(NetworkSide.SERVERBOUND, HandshakeC2SPacket.class);
		}
	},
	PLAY(0) {
		{
			this.addPacket(NetworkSide.CLIENTBOUND, EntitySpawnS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ExperienceOrbSpawnS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntitySpawnGlobalS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, MobSpawnS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PaintingSpawnS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlayerSpawnS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityAnimationS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, StatisticsS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, BlockBreakingProgressS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, BlockEntityUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, BlockActionS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, BlockUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, BossBarS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, DifficultyS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ChatMessageS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ChunkDeltaUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, CommandSuggestionsS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, CommandTreeS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ConfirmGuiActionS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, GuiCloseS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, InventoryS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, GuiUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, GuiSlotUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, CooldownUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, CustomPayloadS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlaySoundIdS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, DisconnectS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityStatusS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ExplosionS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, UnloadChunkS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, GameStateChangeS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, GuiOpenS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, KeepAliveS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ChunkDataS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, WorldEventS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ParticleS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, LightUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, GameJoinS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, MapUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, SetTradeOffersPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityS2CPacket.MoveRelative.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityS2CPacket.RotateAndMoveRelative.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityS2CPacket.Rotate.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, VehicleMoveS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, OpenWrittenBookS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, OpenContainerPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, SignEditorOpenS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, CraftResponseS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlayerAbilitiesS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, CombatEventS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlayerListS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, LookAtS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlayerPositionLookS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, UnlockRecipesS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntitiesDestroyS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, RemoveEntityEffectS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ResourcePackSendS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlayerRespawnS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntitySetHeadYawS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, SelectAdvancementTabS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, WorldBorderS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, SetCameraEntityS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, HeldItemChangeS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ChunkRenderDistanceCenterS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ChunkLoadDistanceS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ScoreboardDisplayS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityTrackerUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityAttachS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityVelocityUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityEquipmentUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ExperienceBarUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, HealthUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ScoreboardObjectiveUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityPassengersSetS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, TeamS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ScoreboardPlayerUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlayerSpawnPositionS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, WorldTimeUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, TitleS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlaySoundFromEntityS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlaySoundS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, StopSoundS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlayerListHeaderS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, TagQueryResponseS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ItemPickupAnimationS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityPositionS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, AdvancementUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityAttributesS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityPotionEffectS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, SynchronizeRecipesS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, SynchronizeTagsS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, class_4463.class);
			this.addPacket(NetworkSide.SERVERBOUND, TeleportConfirmC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, QueryBlockNbtC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, UpdateDifficultyC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, ChatMessageC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, ClientStatusC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, ClientSettingsC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, RequestCommandCompletionsC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, GuiActionConfirmC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, ButtonClickC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, ClickWindowC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, GuiCloseC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, CustomPayloadC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, BookUpdateC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, QueryEntityNbtC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, PlayerInteractEntityC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, KeepAliveC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, UpdateDifficultyLockC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, PlayerMoveC2SPacket.PositionOnly.class);
			this.addPacket(NetworkSide.SERVERBOUND, PlayerMoveC2SPacket.Both.class);
			this.addPacket(NetworkSide.SERVERBOUND, PlayerMoveC2SPacket.LookOnly.class);
			this.addPacket(NetworkSide.SERVERBOUND, PlayerMoveC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, VehicleMoveC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, BoatPaddleStateC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, PickFromInventoryC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, CraftRequestC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, UpdatePlayerAbilitiesC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, PlayerActionC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, ClientCommandC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, PlayerInputC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, RecipeBookDataC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, RenameItemC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, ResourcePackStatusC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, AdvancementTabC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, SelectVillagerTradeC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, UpdateBeaconC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, UpdateSelectedSlotC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, UpdateCommandBlockC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, UpdateCommandBlockMinecartC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, CreativeInventoryActionC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, UpdateJigsawC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, UpdateStructureBlockC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, UpdateSignC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, HandSwingC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, SpectatorTeleportC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, PlayerInteractBlockC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, PlayerInteractItemC2SPacket.class);
		}
	},
	STATUS(1) {
		{
			this.addPacket(NetworkSide.SERVERBOUND, QueryRequestC2SPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, QueryResponseS2CPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, QueryPingC2SPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, QueryPongS2CPacket.class);
		}
	},
	LOGIN(2) {
		{
			this.addPacket(NetworkSide.CLIENTBOUND, LoginDisconnectS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, LoginHelloS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, LoginSuccessS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, LoginCompressionS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, LoginQueryRequestS2CPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, LoginHelloC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, LoginKeyC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, LoginQueryResponseC2SPacket.class);
		}
	};

	private static final NetworkState[] STATES = new NetworkState[4];
	private static final Map<Class<? extends Packet<?>>, NetworkState> HANDLER_STATE_MAP = Maps.<Class<? extends Packet<?>>, NetworkState>newHashMap();
	private final int id;
	private final Map<NetworkSide, BiMap<Integer, Class<? extends Packet<?>>>> packetHandlerMap = Maps.newEnumMap(NetworkSide.class);

	private NetworkState(int j) {
		this.id = j;
	}

	protected NetworkState addPacket(NetworkSide networkSide, Class<? extends Packet<?>> class_) {
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
