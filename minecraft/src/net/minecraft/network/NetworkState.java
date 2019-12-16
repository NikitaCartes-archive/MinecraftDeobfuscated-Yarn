package net.minecraft.network;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
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
import net.minecraft.client.network.packet.CraftFailedResponseS2CPacket;
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
import net.minecraft.client.network.packet.OpenContainerS2CPacket;
import net.minecraft.client.network.packet.OpenWrittenBookS2CPacket;
import net.minecraft.client.network.packet.PaintingSpawnS2CPacket;
import net.minecraft.client.network.packet.ParticleS2CPacket;
import net.minecraft.client.network.packet.PlaySoundFromEntityS2CPacket;
import net.minecraft.client.network.packet.PlaySoundIdS2CPacket;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.client.network.packet.PlayerActionResponseS2CPacket;
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
import net.minecraft.client.network.packet.SetTradeOffersS2CPacket;
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
import net.minecraft.network.listener.PacketListener;
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
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;

public enum NetworkState {
	HANDSHAKING(
		-1,
		createPacketHandlerInitializer().setup(NetworkSide.SERVERBOUND, new NetworkState.PacketHandler().register(HandshakeC2SPacket.class, HandshakeC2SPacket::new))
	),
	PLAY(
		0,
		createPacketHandlerInitializer()
			.setup(
				NetworkSide.CLIENTBOUND,
				new NetworkState.PacketHandler()
					.register(EntitySpawnS2CPacket.class, EntitySpawnS2CPacket::new)
					.register(ExperienceOrbSpawnS2CPacket.class, ExperienceOrbSpawnS2CPacket::new)
					.register(EntitySpawnGlobalS2CPacket.class, EntitySpawnGlobalS2CPacket::new)
					.register(MobSpawnS2CPacket.class, MobSpawnS2CPacket::new)
					.register(PaintingSpawnS2CPacket.class, PaintingSpawnS2CPacket::new)
					.register(PlayerSpawnS2CPacket.class, PlayerSpawnS2CPacket::new)
					.register(EntityAnimationS2CPacket.class, EntityAnimationS2CPacket::new)
					.register(StatisticsS2CPacket.class, StatisticsS2CPacket::new)
					.register(PlayerActionResponseS2CPacket.class, PlayerActionResponseS2CPacket::new)
					.register(BlockBreakingProgressS2CPacket.class, BlockBreakingProgressS2CPacket::new)
					.register(BlockEntityUpdateS2CPacket.class, BlockEntityUpdateS2CPacket::new)
					.register(BlockActionS2CPacket.class, BlockActionS2CPacket::new)
					.register(BlockUpdateS2CPacket.class, BlockUpdateS2CPacket::new)
					.register(BossBarS2CPacket.class, BossBarS2CPacket::new)
					.register(DifficultyS2CPacket.class, DifficultyS2CPacket::new)
					.register(ChatMessageS2CPacket.class, ChatMessageS2CPacket::new)
					.register(ChunkDeltaUpdateS2CPacket.class, ChunkDeltaUpdateS2CPacket::new)
					.register(CommandSuggestionsS2CPacket.class, CommandSuggestionsS2CPacket::new)
					.register(CommandTreeS2CPacket.class, CommandTreeS2CPacket::new)
					.register(ConfirmGuiActionS2CPacket.class, ConfirmGuiActionS2CPacket::new)
					.register(GuiCloseS2CPacket.class, GuiCloseS2CPacket::new)
					.register(InventoryS2CPacket.class, InventoryS2CPacket::new)
					.register(GuiUpdateS2CPacket.class, GuiUpdateS2CPacket::new)
					.register(GuiSlotUpdateS2CPacket.class, GuiSlotUpdateS2CPacket::new)
					.register(CooldownUpdateS2CPacket.class, CooldownUpdateS2CPacket::new)
					.register(CustomPayloadS2CPacket.class, CustomPayloadS2CPacket::new)
					.register(PlaySoundIdS2CPacket.class, PlaySoundIdS2CPacket::new)
					.register(DisconnectS2CPacket.class, DisconnectS2CPacket::new)
					.register(EntityStatusS2CPacket.class, EntityStatusS2CPacket::new)
					.register(ExplosionS2CPacket.class, ExplosionS2CPacket::new)
					.register(UnloadChunkS2CPacket.class, UnloadChunkS2CPacket::new)
					.register(GameStateChangeS2CPacket.class, GameStateChangeS2CPacket::new)
					.register(GuiOpenS2CPacket.class, GuiOpenS2CPacket::new)
					.register(KeepAliveS2CPacket.class, KeepAliveS2CPacket::new)
					.register(ChunkDataS2CPacket.class, ChunkDataS2CPacket::new)
					.register(WorldEventS2CPacket.class, WorldEventS2CPacket::new)
					.register(ParticleS2CPacket.class, ParticleS2CPacket::new)
					.register(LightUpdateS2CPacket.class, LightUpdateS2CPacket::new)
					.register(GameJoinS2CPacket.class, GameJoinS2CPacket::new)
					.register(MapUpdateS2CPacket.class, MapUpdateS2CPacket::new)
					.register(SetTradeOffersS2CPacket.class, SetTradeOffersS2CPacket::new)
					.register(EntityS2CPacket.MoveRelative.class, EntityS2CPacket.MoveRelative::new)
					.register(EntityS2CPacket.RotateAndMoveRelative.class, EntityS2CPacket.RotateAndMoveRelative::new)
					.register(EntityS2CPacket.Rotate.class, EntityS2CPacket.Rotate::new)
					.register(EntityS2CPacket.class, EntityS2CPacket::new)
					.register(VehicleMoveS2CPacket.class, VehicleMoveS2CPacket::new)
					.register(OpenWrittenBookS2CPacket.class, OpenWrittenBookS2CPacket::new)
					.register(OpenContainerS2CPacket.class, OpenContainerS2CPacket::new)
					.register(SignEditorOpenS2CPacket.class, SignEditorOpenS2CPacket::new)
					.register(CraftFailedResponseS2CPacket.class, CraftFailedResponseS2CPacket::new)
					.register(PlayerAbilitiesS2CPacket.class, PlayerAbilitiesS2CPacket::new)
					.register(CombatEventS2CPacket.class, CombatEventS2CPacket::new)
					.register(PlayerListS2CPacket.class, PlayerListS2CPacket::new)
					.register(LookAtS2CPacket.class, LookAtS2CPacket::new)
					.register(PlayerPositionLookS2CPacket.class, PlayerPositionLookS2CPacket::new)
					.register(UnlockRecipesS2CPacket.class, UnlockRecipesS2CPacket::new)
					.register(EntitiesDestroyS2CPacket.class, EntitiesDestroyS2CPacket::new)
					.register(RemoveEntityEffectS2CPacket.class, RemoveEntityEffectS2CPacket::new)
					.register(ResourcePackSendS2CPacket.class, ResourcePackSendS2CPacket::new)
					.register(PlayerRespawnS2CPacket.class, PlayerRespawnS2CPacket::new)
					.register(EntitySetHeadYawS2CPacket.class, EntitySetHeadYawS2CPacket::new)
					.register(SelectAdvancementTabS2CPacket.class, SelectAdvancementTabS2CPacket::new)
					.register(WorldBorderS2CPacket.class, WorldBorderS2CPacket::new)
					.register(SetCameraEntityS2CPacket.class, SetCameraEntityS2CPacket::new)
					.register(HeldItemChangeS2CPacket.class, HeldItemChangeS2CPacket::new)
					.register(ChunkRenderDistanceCenterS2CPacket.class, ChunkRenderDistanceCenterS2CPacket::new)
					.register(ChunkLoadDistanceS2CPacket.class, ChunkLoadDistanceS2CPacket::new)
					.register(ScoreboardDisplayS2CPacket.class, ScoreboardDisplayS2CPacket::new)
					.register(EntityTrackerUpdateS2CPacket.class, EntityTrackerUpdateS2CPacket::new)
					.register(EntityAttachS2CPacket.class, EntityAttachS2CPacket::new)
					.register(EntityVelocityUpdateS2CPacket.class, EntityVelocityUpdateS2CPacket::new)
					.register(EntityEquipmentUpdateS2CPacket.class, EntityEquipmentUpdateS2CPacket::new)
					.register(ExperienceBarUpdateS2CPacket.class, ExperienceBarUpdateS2CPacket::new)
					.register(HealthUpdateS2CPacket.class, HealthUpdateS2CPacket::new)
					.register(ScoreboardObjectiveUpdateS2CPacket.class, ScoreboardObjectiveUpdateS2CPacket::new)
					.register(EntityPassengersSetS2CPacket.class, EntityPassengersSetS2CPacket::new)
					.register(TeamS2CPacket.class, TeamS2CPacket::new)
					.register(ScoreboardPlayerUpdateS2CPacket.class, ScoreboardPlayerUpdateS2CPacket::new)
					.register(PlayerSpawnPositionS2CPacket.class, PlayerSpawnPositionS2CPacket::new)
					.register(WorldTimeUpdateS2CPacket.class, WorldTimeUpdateS2CPacket::new)
					.register(TitleS2CPacket.class, TitleS2CPacket::new)
					.register(PlaySoundFromEntityS2CPacket.class, PlaySoundFromEntityS2CPacket::new)
					.register(PlaySoundS2CPacket.class, PlaySoundS2CPacket::new)
					.register(StopSoundS2CPacket.class, StopSoundS2CPacket::new)
					.register(PlayerListHeaderS2CPacket.class, PlayerListHeaderS2CPacket::new)
					.register(TagQueryResponseS2CPacket.class, TagQueryResponseS2CPacket::new)
					.register(ItemPickupAnimationS2CPacket.class, ItemPickupAnimationS2CPacket::new)
					.register(EntityPositionS2CPacket.class, EntityPositionS2CPacket::new)
					.register(AdvancementUpdateS2CPacket.class, AdvancementUpdateS2CPacket::new)
					.register(EntityAttributesS2CPacket.class, EntityAttributesS2CPacket::new)
					.register(EntityPotionEffectS2CPacket.class, EntityPotionEffectS2CPacket::new)
					.register(SynchronizeRecipesS2CPacket.class, SynchronizeRecipesS2CPacket::new)
					.register(SynchronizeTagsS2CPacket.class, SynchronizeTagsS2CPacket::new)
			)
			.setup(
				NetworkSide.SERVERBOUND,
				new NetworkState.PacketHandler()
					.register(TeleportConfirmC2SPacket.class, TeleportConfirmC2SPacket::new)
					.register(QueryBlockNbtC2SPacket.class, QueryBlockNbtC2SPacket::new)
					.register(UpdateDifficultyC2SPacket.class, UpdateDifficultyC2SPacket::new)
					.register(ChatMessageC2SPacket.class, ChatMessageC2SPacket::new)
					.register(ClientStatusC2SPacket.class, ClientStatusC2SPacket::new)
					.register(ClientSettingsC2SPacket.class, ClientSettingsC2SPacket::new)
					.register(RequestCommandCompletionsC2SPacket.class, RequestCommandCompletionsC2SPacket::new)
					.register(GuiActionConfirmC2SPacket.class, GuiActionConfirmC2SPacket::new)
					.register(ButtonClickC2SPacket.class, ButtonClickC2SPacket::new)
					.register(ClickWindowC2SPacket.class, ClickWindowC2SPacket::new)
					.register(GuiCloseC2SPacket.class, GuiCloseC2SPacket::new)
					.register(CustomPayloadC2SPacket.class, CustomPayloadC2SPacket::new)
					.register(BookUpdateC2SPacket.class, BookUpdateC2SPacket::new)
					.register(QueryEntityNbtC2SPacket.class, QueryEntityNbtC2SPacket::new)
					.register(PlayerInteractEntityC2SPacket.class, PlayerInteractEntityC2SPacket::new)
					.register(KeepAliveC2SPacket.class, KeepAliveC2SPacket::new)
					.register(UpdateDifficultyLockC2SPacket.class, UpdateDifficultyLockC2SPacket::new)
					.register(PlayerMoveC2SPacket.PositionOnly.class, PlayerMoveC2SPacket.PositionOnly::new)
					.register(PlayerMoveC2SPacket.Both.class, PlayerMoveC2SPacket.Both::new)
					.register(PlayerMoveC2SPacket.LookOnly.class, PlayerMoveC2SPacket.LookOnly::new)
					.register(PlayerMoveC2SPacket.class, PlayerMoveC2SPacket::new)
					.register(VehicleMoveC2SPacket.class, VehicleMoveC2SPacket::new)
					.register(BoatPaddleStateC2SPacket.class, BoatPaddleStateC2SPacket::new)
					.register(PickFromInventoryC2SPacket.class, PickFromInventoryC2SPacket::new)
					.register(CraftRequestC2SPacket.class, CraftRequestC2SPacket::new)
					.register(UpdatePlayerAbilitiesC2SPacket.class, UpdatePlayerAbilitiesC2SPacket::new)
					.register(PlayerActionC2SPacket.class, PlayerActionC2SPacket::new)
					.register(ClientCommandC2SPacket.class, ClientCommandC2SPacket::new)
					.register(PlayerInputC2SPacket.class, PlayerInputC2SPacket::new)
					.register(RecipeBookDataC2SPacket.class, RecipeBookDataC2SPacket::new)
					.register(RenameItemC2SPacket.class, RenameItemC2SPacket::new)
					.register(ResourcePackStatusC2SPacket.class, ResourcePackStatusC2SPacket::new)
					.register(AdvancementTabC2SPacket.class, AdvancementTabC2SPacket::new)
					.register(SelectVillagerTradeC2SPacket.class, SelectVillagerTradeC2SPacket::new)
					.register(UpdateBeaconC2SPacket.class, UpdateBeaconC2SPacket::new)
					.register(UpdateSelectedSlotC2SPacket.class, UpdateSelectedSlotC2SPacket::new)
					.register(UpdateCommandBlockC2SPacket.class, UpdateCommandBlockC2SPacket::new)
					.register(UpdateCommandBlockMinecartC2SPacket.class, UpdateCommandBlockMinecartC2SPacket::new)
					.register(CreativeInventoryActionC2SPacket.class, CreativeInventoryActionC2SPacket::new)
					.register(UpdateJigsawC2SPacket.class, UpdateJigsawC2SPacket::new)
					.register(UpdateStructureBlockC2SPacket.class, UpdateStructureBlockC2SPacket::new)
					.register(UpdateSignC2SPacket.class, UpdateSignC2SPacket::new)
					.register(HandSwingC2SPacket.class, HandSwingC2SPacket::new)
					.register(SpectatorTeleportC2SPacket.class, SpectatorTeleportC2SPacket::new)
					.register(PlayerInteractBlockC2SPacket.class, PlayerInteractBlockC2SPacket::new)
					.register(PlayerInteractItemC2SPacket.class, PlayerInteractItemC2SPacket::new)
			)
	),
	STATUS(
		1,
		createPacketHandlerInitializer()
			.setup(
				NetworkSide.SERVERBOUND,
				new NetworkState.PacketHandler()
					.register(QueryRequestC2SPacket.class, QueryRequestC2SPacket::new)
					.register(QueryPingC2SPacket.class, QueryPingC2SPacket::new)
			)
			.setup(
				NetworkSide.CLIENTBOUND,
				new NetworkState.PacketHandler()
					.register(QueryResponseS2CPacket.class, QueryResponseS2CPacket::new)
					.register(QueryPongS2CPacket.class, QueryPongS2CPacket::new)
			)
	),
	LOGIN(
		2,
		createPacketHandlerInitializer()
			.setup(
				NetworkSide.CLIENTBOUND,
				new NetworkState.PacketHandler()
					.register(LoginDisconnectS2CPacket.class, LoginDisconnectS2CPacket::new)
					.register(LoginHelloS2CPacket.class, LoginHelloS2CPacket::new)
					.register(LoginSuccessS2CPacket.class, LoginSuccessS2CPacket::new)
					.register(LoginCompressionS2CPacket.class, LoginCompressionS2CPacket::new)
					.register(LoginQueryRequestS2CPacket.class, LoginQueryRequestS2CPacket::new)
			)
			.setup(
				NetworkSide.SERVERBOUND,
				new NetworkState.PacketHandler()
					.register(LoginHelloC2SPacket.class, LoginHelloC2SPacket::new)
					.register(LoginKeyC2SPacket.class, LoginKeyC2SPacket::new)
					.register(LoginQueryResponseC2SPacket.class, LoginQueryResponseC2SPacket::new)
			)
	);

	private static final NetworkState[] STATES = new NetworkState[4];
	private static final Map<Class<? extends Packet<?>>, NetworkState> HANDLER_STATE_MAP = Maps.<Class<? extends Packet<?>>, NetworkState>newHashMap();
	private final int stateId;
	private final Map<NetworkSide, ? extends NetworkState.PacketHandler<?>> packetHandlers;

	private static NetworkState.PacketHandlerInitializer createPacketHandlerInitializer() {
		return new NetworkState.PacketHandlerInitializer();
	}

	private NetworkState(int j, NetworkState.PacketHandlerInitializer packetHandlerInitializer) {
		this.stateId = j;
		this.packetHandlers = packetHandlerInitializer.packetHandlers;
	}

	@Nullable
	public Integer getPacketId(NetworkSide side, Packet<?> packet) {
		return ((NetworkState.PacketHandler)this.packetHandlers.get(side)).getId(packet.getClass());
	}

	@Nullable
	public Packet<?> getPacketHandler(NetworkSide side, int i) {
		return ((NetworkState.PacketHandler)this.packetHandlers.get(side)).createPacket(i);
	}

	public int getId() {
		return this.stateId;
	}

	@Nullable
	public static NetworkState byId(int id) {
		return id >= -1 && id <= 2 ? STATES[id - -1] : null;
	}

	public static NetworkState getPacketHandlerState(Packet<?> handler) {
		return (NetworkState)HANDLER_STATE_MAP.get(handler.getClass());
	}

	static {
		for (NetworkState networkState : values()) {
			int i = networkState.getId();
			if (i < -1 || i > 2) {
				throw new Error("Invalid protocol ID " + Integer.toString(i));
			}

			STATES[i - -1] = networkState;
			networkState.packetHandlers
				.forEach(
					(networkSide, packetHandler) -> packetHandler.getPacketTypes()
							.forEach(
								class_ -> {
									if (HANDLER_STATE_MAP.containsKey(class_) && HANDLER_STATE_MAP.get(class_) != networkState) {
										throw new IllegalStateException(
											"Packet " + class_ + " is already assigned to protocol " + HANDLER_STATE_MAP.get(class_) + " - can't reassign to " + networkState
										);
									} else {
										HANDLER_STATE_MAP.put(class_, networkState);
									}
								}
							)
				);
		}
	}

	static class PacketHandler<T extends PacketListener> {
		private final Object2IntMap<Class<? extends Packet<T>>> packetIds = Util.make(
			new Object2IntOpenHashMap<>(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1)
		);
		private final List<Supplier<? extends Packet<T>>> packetFactories = Lists.<Supplier<? extends Packet<T>>>newArrayList();

		private PacketHandler() {
		}

		public <P extends Packet<T>> NetworkState.PacketHandler<T> register(Class<P> type, Supplier<P> factory) {
			int i = this.packetFactories.size();
			int j = this.packetIds.put(type, i);
			if (j != -1) {
				String string = "Packet " + type + " is already registered to ID " + j;
				LogManager.getLogger().fatal(string);
				throw new IllegalArgumentException(string);
			} else {
				this.packetFactories.add(factory);
				return this;
			}
		}

		@Nullable
		public Integer getId(Class<?> packet) {
			int i = this.packetIds.getInt(packet);
			return i == -1 ? null : i;
		}

		@Nullable
		public Packet<?> createPacket(int id) {
			Supplier<? extends Packet<T>> supplier = (Supplier<? extends Packet<T>>)this.packetFactories.get(id);
			return supplier != null ? (Packet)supplier.get() : null;
		}

		public Iterable<Class<? extends Packet<?>>> getPacketTypes() {
			return Iterables.unmodifiableIterable(this.packetIds.keySet());
		}
	}

	static class PacketHandlerInitializer {
		private final Map<NetworkSide, NetworkState.PacketHandler<?>> packetHandlers = Maps.newEnumMap(NetworkSide.class);

		private PacketHandlerInitializer() {
		}

		public <T extends PacketListener> NetworkState.PacketHandlerInitializer setup(NetworkSide side, NetworkState.PacketHandler<T> packetHandler) {
			this.packetHandlers.put(side, packetHandler);
			return this;
		}
	}
}
