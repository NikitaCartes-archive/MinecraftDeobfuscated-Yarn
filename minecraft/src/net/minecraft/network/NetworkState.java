package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.AdvancementUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockActionS2CPacket;
import net.minecraft.client.network.packet.BlockBreakingProgressS2CPacket;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockPlayerActionS2CPacket;
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
	field_11689(-1) {
		{
			this.addPacket(NetworkSide.field_11941, HandshakeC2SPacket.class);
		}
	},
	field_11690(0) {
		{
			this.addPacket(NetworkSide.field_11942, EntitySpawnS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ExperienceOrbSpawnS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntitySpawnGlobalS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, MobSpawnS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, PaintingSpawnS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, PlayerSpawnS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntityAnimationS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, StatisticsS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, BlockBreakingProgressS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, BlockEntityUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, BlockActionS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, BlockUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, BossBarS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, DifficultyS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ChatMessageS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ChunkDeltaUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, CommandSuggestionsS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, CommandTreeS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ConfirmGuiActionS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, GuiCloseS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, InventoryS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, GuiUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, GuiSlotUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, CooldownUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, CustomPayloadS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, PlaySoundIdS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, DisconnectS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntityStatusS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ExplosionS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, UnloadChunkS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, GameStateChangeS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, GuiOpenS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, KeepAliveS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ChunkDataS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, WorldEventS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ParticleS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, LightUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, GameJoinS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, MapUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, SetTradeOffersPacket.class);
			this.addPacket(NetworkSide.field_11942, EntityS2CPacket.MoveRelative.class);
			this.addPacket(NetworkSide.field_11942, EntityS2CPacket.RotateAndMoveRelative.class);
			this.addPacket(NetworkSide.field_11942, EntityS2CPacket.Rotate.class);
			this.addPacket(NetworkSide.field_11942, EntityS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, VehicleMoveS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, OpenWrittenBookS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, OpenContainerPacket.class);
			this.addPacket(NetworkSide.field_11942, SignEditorOpenS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, CraftResponseS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, PlayerAbilitiesS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, CombatEventS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, PlayerListS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, LookAtS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, PlayerPositionLookS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, UnlockRecipesS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntitiesDestroyS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, RemoveEntityEffectS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ResourcePackSendS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, PlayerRespawnS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntitySetHeadYawS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, SelectAdvancementTabS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, WorldBorderS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, SetCameraEntityS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, HeldItemChangeS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ChunkRenderDistanceCenterS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ChunkLoadDistanceS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ScoreboardDisplayS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntityTrackerUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntityAttachS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntityVelocityUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntityEquipmentUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ExperienceBarUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, HealthUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ScoreboardObjectiveUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntityPassengersSetS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, TeamS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ScoreboardPlayerUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, PlayerSpawnPositionS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, WorldTimeUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, TitleS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, PlaySoundFromEntityS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, PlaySoundS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, StopSoundS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, PlayerListHeaderS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, TagQueryResponseS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, ItemPickupAnimationS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntityPositionS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, AdvancementUpdateS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntityAttributesS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, EntityPotionEffectS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, SynchronizeRecipesS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, SynchronizeTagsS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, BlockPlayerActionS2CPacket.class);
			this.addPacket(NetworkSide.field_11941, TeleportConfirmC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, QueryBlockNbtC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, UpdateDifficultyC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, ChatMessageC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, ClientStatusC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, ClientSettingsC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, RequestCommandCompletionsC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, GuiActionConfirmC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, ButtonClickC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, ClickWindowC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, GuiCloseC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, CustomPayloadC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, BookUpdateC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, QueryEntityNbtC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, PlayerInteractEntityC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, KeepAliveC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, UpdateDifficultyLockC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, PlayerMoveC2SPacket.PositionOnly.class);
			this.addPacket(NetworkSide.field_11941, PlayerMoveC2SPacket.Both.class);
			this.addPacket(NetworkSide.field_11941, PlayerMoveC2SPacket.LookOnly.class);
			this.addPacket(NetworkSide.field_11941, PlayerMoveC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, VehicleMoveC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, BoatPaddleStateC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, PickFromInventoryC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, CraftRequestC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, UpdatePlayerAbilitiesC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, PlayerActionC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, ClientCommandC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, PlayerInputC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, RecipeBookDataC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, RenameItemC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, ResourcePackStatusC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, AdvancementTabC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, SelectVillagerTradeC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, UpdateBeaconC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, UpdateSelectedSlotC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, UpdateCommandBlockC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, UpdateCommandBlockMinecartC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, CreativeInventoryActionC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, UpdateJigsawC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, UpdateStructureBlockC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, UpdateSignC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, HandSwingC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, SpectatorTeleportC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, PlayerInteractBlockC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, PlayerInteractItemC2SPacket.class);
		}
	},
	field_11691(1) {
		{
			this.addPacket(NetworkSide.field_11941, QueryRequestC2SPacket.class);
			this.addPacket(NetworkSide.field_11942, QueryResponseS2CPacket.class);
			this.addPacket(NetworkSide.field_11941, QueryPingC2SPacket.class);
			this.addPacket(NetworkSide.field_11942, QueryPongS2CPacket.class);
		}
	},
	field_11688(2) {
		{
			this.addPacket(NetworkSide.field_11942, LoginDisconnectS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, LoginHelloS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, LoginSuccessS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, LoginCompressionS2CPacket.class);
			this.addPacket(NetworkSide.field_11942, LoginQueryRequestS2CPacket.class);
			this.addPacket(NetworkSide.field_11941, LoginHelloC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, LoginKeyC2SPacket.class);
			this.addPacket(NetworkSide.field_11941, LoginQueryResponseC2SPacket.class);
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
