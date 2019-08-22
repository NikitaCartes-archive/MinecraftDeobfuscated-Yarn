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
import net.minecraft.util.SystemUtil;
import org.apache.logging.log4j.LogManager;

public enum NetworkState {
	HANDSHAKING(
		-1, method_22308().method_22315(NetworkSide.SERVERBOUND, new NetworkState.class_4532().method_22313(HandshakeC2SPacket.class, HandshakeC2SPacket::new))
	),
	PLAY(
		0,
		method_22308()
			.method_22315(
				NetworkSide.CLIENTBOUND,
				new NetworkState.class_4532()
					.method_22313(EntitySpawnS2CPacket.class, EntitySpawnS2CPacket::new)
					.method_22313(ExperienceOrbSpawnS2CPacket.class, ExperienceOrbSpawnS2CPacket::new)
					.method_22313(EntitySpawnGlobalS2CPacket.class, EntitySpawnGlobalS2CPacket::new)
					.method_22313(MobSpawnS2CPacket.class, MobSpawnS2CPacket::new)
					.method_22313(PaintingSpawnS2CPacket.class, PaintingSpawnS2CPacket::new)
					.method_22313(PlayerSpawnS2CPacket.class, PlayerSpawnS2CPacket::new)
					.method_22313(EntityAnimationS2CPacket.class, EntityAnimationS2CPacket::new)
					.method_22313(StatisticsS2CPacket.class, StatisticsS2CPacket::new)
					.method_22313(BlockPlayerActionS2CPacket.class, BlockPlayerActionS2CPacket::new)
					.method_22313(BlockBreakingProgressS2CPacket.class, BlockBreakingProgressS2CPacket::new)
					.method_22313(BlockEntityUpdateS2CPacket.class, BlockEntityUpdateS2CPacket::new)
					.method_22313(BlockActionS2CPacket.class, BlockActionS2CPacket::new)
					.method_22313(BlockUpdateS2CPacket.class, BlockUpdateS2CPacket::new)
					.method_22313(BossBarS2CPacket.class, BossBarS2CPacket::new)
					.method_22313(DifficultyS2CPacket.class, DifficultyS2CPacket::new)
					.method_22313(ChatMessageS2CPacket.class, ChatMessageS2CPacket::new)
					.method_22313(ChunkDeltaUpdateS2CPacket.class, ChunkDeltaUpdateS2CPacket::new)
					.method_22313(CommandSuggestionsS2CPacket.class, CommandSuggestionsS2CPacket::new)
					.method_22313(CommandTreeS2CPacket.class, CommandTreeS2CPacket::new)
					.method_22313(ConfirmGuiActionS2CPacket.class, ConfirmGuiActionS2CPacket::new)
					.method_22313(GuiCloseS2CPacket.class, GuiCloseS2CPacket::new)
					.method_22313(InventoryS2CPacket.class, InventoryS2CPacket::new)
					.method_22313(GuiUpdateS2CPacket.class, GuiUpdateS2CPacket::new)
					.method_22313(GuiSlotUpdateS2CPacket.class, GuiSlotUpdateS2CPacket::new)
					.method_22313(CooldownUpdateS2CPacket.class, CooldownUpdateS2CPacket::new)
					.method_22313(CustomPayloadS2CPacket.class, CustomPayloadS2CPacket::new)
					.method_22313(PlaySoundIdS2CPacket.class, PlaySoundIdS2CPacket::new)
					.method_22313(DisconnectS2CPacket.class, DisconnectS2CPacket::new)
					.method_22313(EntityStatusS2CPacket.class, EntityStatusS2CPacket::new)
					.method_22313(ExplosionS2CPacket.class, ExplosionS2CPacket::new)
					.method_22313(UnloadChunkS2CPacket.class, UnloadChunkS2CPacket::new)
					.method_22313(GameStateChangeS2CPacket.class, GameStateChangeS2CPacket::new)
					.method_22313(GuiOpenS2CPacket.class, GuiOpenS2CPacket::new)
					.method_22313(KeepAliveS2CPacket.class, KeepAliveS2CPacket::new)
					.method_22313(ChunkDataS2CPacket.class, ChunkDataS2CPacket::new)
					.method_22313(WorldEventS2CPacket.class, WorldEventS2CPacket::new)
					.method_22313(ParticleS2CPacket.class, ParticleS2CPacket::new)
					.method_22313(LightUpdateS2CPacket.class, LightUpdateS2CPacket::new)
					.method_22313(GameJoinS2CPacket.class, GameJoinS2CPacket::new)
					.method_22313(MapUpdateS2CPacket.class, MapUpdateS2CPacket::new)
					.method_22313(SetTradeOffersPacket.class, SetTradeOffersPacket::new)
					.method_22313(EntityS2CPacket.MoveRelative.class, EntityS2CPacket.MoveRelative::new)
					.method_22313(EntityS2CPacket.RotateAndMoveRelative.class, EntityS2CPacket.RotateAndMoveRelative::new)
					.method_22313(EntityS2CPacket.Rotate.class, EntityS2CPacket.Rotate::new)
					.method_22313(EntityS2CPacket.class, EntityS2CPacket::new)
					.method_22313(VehicleMoveS2CPacket.class, VehicleMoveS2CPacket::new)
					.method_22313(OpenWrittenBookS2CPacket.class, OpenWrittenBookS2CPacket::new)
					.method_22313(OpenContainerPacket.class, OpenContainerPacket::new)
					.method_22313(SignEditorOpenS2CPacket.class, SignEditorOpenS2CPacket::new)
					.method_22313(CraftFailedResponseS2CPacket.class, CraftFailedResponseS2CPacket::new)
					.method_22313(PlayerAbilitiesS2CPacket.class, PlayerAbilitiesS2CPacket::new)
					.method_22313(CombatEventS2CPacket.class, CombatEventS2CPacket::new)
					.method_22313(PlayerListS2CPacket.class, PlayerListS2CPacket::new)
					.method_22313(LookAtS2CPacket.class, LookAtS2CPacket::new)
					.method_22313(PlayerPositionLookS2CPacket.class, PlayerPositionLookS2CPacket::new)
					.method_22313(UnlockRecipesS2CPacket.class, UnlockRecipesS2CPacket::new)
					.method_22313(EntitiesDestroyS2CPacket.class, EntitiesDestroyS2CPacket::new)
					.method_22313(RemoveEntityEffectS2CPacket.class, RemoveEntityEffectS2CPacket::new)
					.method_22313(ResourcePackSendS2CPacket.class, ResourcePackSendS2CPacket::new)
					.method_22313(PlayerRespawnS2CPacket.class, PlayerRespawnS2CPacket::new)
					.method_22313(EntitySetHeadYawS2CPacket.class, EntitySetHeadYawS2CPacket::new)
					.method_22313(SelectAdvancementTabS2CPacket.class, SelectAdvancementTabS2CPacket::new)
					.method_22313(WorldBorderS2CPacket.class, WorldBorderS2CPacket::new)
					.method_22313(SetCameraEntityS2CPacket.class, SetCameraEntityS2CPacket::new)
					.method_22313(HeldItemChangeS2CPacket.class, HeldItemChangeS2CPacket::new)
					.method_22313(ChunkRenderDistanceCenterS2CPacket.class, ChunkRenderDistanceCenterS2CPacket::new)
					.method_22313(ChunkLoadDistanceS2CPacket.class, ChunkLoadDistanceS2CPacket::new)
					.method_22313(ScoreboardDisplayS2CPacket.class, ScoreboardDisplayS2CPacket::new)
					.method_22313(EntityTrackerUpdateS2CPacket.class, EntityTrackerUpdateS2CPacket::new)
					.method_22313(EntityAttachS2CPacket.class, EntityAttachS2CPacket::new)
					.method_22313(EntityVelocityUpdateS2CPacket.class, EntityVelocityUpdateS2CPacket::new)
					.method_22313(EntityEquipmentUpdateS2CPacket.class, EntityEquipmentUpdateS2CPacket::new)
					.method_22313(ExperienceBarUpdateS2CPacket.class, ExperienceBarUpdateS2CPacket::new)
					.method_22313(HealthUpdateS2CPacket.class, HealthUpdateS2CPacket::new)
					.method_22313(ScoreboardObjectiveUpdateS2CPacket.class, ScoreboardObjectiveUpdateS2CPacket::new)
					.method_22313(EntityPassengersSetS2CPacket.class, EntityPassengersSetS2CPacket::new)
					.method_22313(TeamS2CPacket.class, TeamS2CPacket::new)
					.method_22313(ScoreboardPlayerUpdateS2CPacket.class, ScoreboardPlayerUpdateS2CPacket::new)
					.method_22313(PlayerSpawnPositionS2CPacket.class, PlayerSpawnPositionS2CPacket::new)
					.method_22313(WorldTimeUpdateS2CPacket.class, WorldTimeUpdateS2CPacket::new)
					.method_22313(TitleS2CPacket.class, TitleS2CPacket::new)
					.method_22313(PlaySoundFromEntityS2CPacket.class, PlaySoundFromEntityS2CPacket::new)
					.method_22313(PlaySoundS2CPacket.class, PlaySoundS2CPacket::new)
					.method_22313(StopSoundS2CPacket.class, StopSoundS2CPacket::new)
					.method_22313(PlayerListHeaderS2CPacket.class, PlayerListHeaderS2CPacket::new)
					.method_22313(TagQueryResponseS2CPacket.class, TagQueryResponseS2CPacket::new)
					.method_22313(ItemPickupAnimationS2CPacket.class, ItemPickupAnimationS2CPacket::new)
					.method_22313(EntityPositionS2CPacket.class, EntityPositionS2CPacket::new)
					.method_22313(AdvancementUpdateS2CPacket.class, AdvancementUpdateS2CPacket::new)
					.method_22313(EntityAttributesS2CPacket.class, EntityAttributesS2CPacket::new)
					.method_22313(EntityPotionEffectS2CPacket.class, EntityPotionEffectS2CPacket::new)
					.method_22313(SynchronizeRecipesS2CPacket.class, SynchronizeRecipesS2CPacket::new)
					.method_22313(SynchronizeTagsS2CPacket.class, SynchronizeTagsS2CPacket::new)
			)
			.method_22315(
				NetworkSide.SERVERBOUND,
				new NetworkState.class_4532()
					.method_22313(TeleportConfirmC2SPacket.class, TeleportConfirmC2SPacket::new)
					.method_22313(QueryBlockNbtC2SPacket.class, QueryBlockNbtC2SPacket::new)
					.method_22313(UpdateDifficultyC2SPacket.class, UpdateDifficultyC2SPacket::new)
					.method_22313(ChatMessageC2SPacket.class, ChatMessageC2SPacket::new)
					.method_22313(ClientStatusC2SPacket.class, ClientStatusC2SPacket::new)
					.method_22313(ClientSettingsC2SPacket.class, ClientSettingsC2SPacket::new)
					.method_22313(RequestCommandCompletionsC2SPacket.class, RequestCommandCompletionsC2SPacket::new)
					.method_22313(GuiActionConfirmC2SPacket.class, GuiActionConfirmC2SPacket::new)
					.method_22313(ButtonClickC2SPacket.class, ButtonClickC2SPacket::new)
					.method_22313(ClickWindowC2SPacket.class, ClickWindowC2SPacket::new)
					.method_22313(GuiCloseC2SPacket.class, GuiCloseC2SPacket::new)
					.method_22313(CustomPayloadC2SPacket.class, CustomPayloadC2SPacket::new)
					.method_22313(BookUpdateC2SPacket.class, BookUpdateC2SPacket::new)
					.method_22313(QueryEntityNbtC2SPacket.class, QueryEntityNbtC2SPacket::new)
					.method_22313(PlayerInteractEntityC2SPacket.class, PlayerInteractEntityC2SPacket::new)
					.method_22313(KeepAliveC2SPacket.class, KeepAliveC2SPacket::new)
					.method_22313(UpdateDifficultyLockC2SPacket.class, UpdateDifficultyLockC2SPacket::new)
					.method_22313(PlayerMoveC2SPacket.PositionOnly.class, PlayerMoveC2SPacket.PositionOnly::new)
					.method_22313(PlayerMoveC2SPacket.Both.class, PlayerMoveC2SPacket.Both::new)
					.method_22313(PlayerMoveC2SPacket.LookOnly.class, PlayerMoveC2SPacket.LookOnly::new)
					.method_22313(PlayerMoveC2SPacket.class, PlayerMoveC2SPacket::new)
					.method_22313(VehicleMoveC2SPacket.class, VehicleMoveC2SPacket::new)
					.method_22313(BoatPaddleStateC2SPacket.class, BoatPaddleStateC2SPacket::new)
					.method_22313(PickFromInventoryC2SPacket.class, PickFromInventoryC2SPacket::new)
					.method_22313(CraftRequestC2SPacket.class, CraftRequestC2SPacket::new)
					.method_22313(UpdatePlayerAbilitiesC2SPacket.class, UpdatePlayerAbilitiesC2SPacket::new)
					.method_22313(PlayerActionC2SPacket.class, PlayerActionC2SPacket::new)
					.method_22313(ClientCommandC2SPacket.class, ClientCommandC2SPacket::new)
					.method_22313(PlayerInputC2SPacket.class, PlayerInputC2SPacket::new)
					.method_22313(RecipeBookDataC2SPacket.class, RecipeBookDataC2SPacket::new)
					.method_22313(RenameItemC2SPacket.class, RenameItemC2SPacket::new)
					.method_22313(ResourcePackStatusC2SPacket.class, ResourcePackStatusC2SPacket::new)
					.method_22313(AdvancementTabC2SPacket.class, AdvancementTabC2SPacket::new)
					.method_22313(SelectVillagerTradeC2SPacket.class, SelectVillagerTradeC2SPacket::new)
					.method_22313(UpdateBeaconC2SPacket.class, UpdateBeaconC2SPacket::new)
					.method_22313(UpdateSelectedSlotC2SPacket.class, UpdateSelectedSlotC2SPacket::new)
					.method_22313(UpdateCommandBlockC2SPacket.class, UpdateCommandBlockC2SPacket::new)
					.method_22313(UpdateCommandBlockMinecartC2SPacket.class, UpdateCommandBlockMinecartC2SPacket::new)
					.method_22313(CreativeInventoryActionC2SPacket.class, CreativeInventoryActionC2SPacket::new)
					.method_22313(UpdateJigsawC2SPacket.class, UpdateJigsawC2SPacket::new)
					.method_22313(UpdateStructureBlockC2SPacket.class, UpdateStructureBlockC2SPacket::new)
					.method_22313(UpdateSignC2SPacket.class, UpdateSignC2SPacket::new)
					.method_22313(HandSwingC2SPacket.class, HandSwingC2SPacket::new)
					.method_22313(SpectatorTeleportC2SPacket.class, SpectatorTeleportC2SPacket::new)
					.method_22313(PlayerInteractBlockC2SPacket.class, PlayerInteractBlockC2SPacket::new)
					.method_22313(PlayerInteractItemC2SPacket.class, PlayerInteractItemC2SPacket::new)
			)
	),
	STATUS(
		1,
		method_22308()
			.method_22315(
				NetworkSide.SERVERBOUND,
				new NetworkState.class_4532()
					.method_22313(QueryRequestC2SPacket.class, QueryRequestC2SPacket::new)
					.method_22313(QueryPingC2SPacket.class, QueryPingC2SPacket::new)
			)
			.method_22315(
				NetworkSide.CLIENTBOUND,
				new NetworkState.class_4532()
					.method_22313(QueryResponseS2CPacket.class, QueryResponseS2CPacket::new)
					.method_22313(QueryPongS2CPacket.class, QueryPongS2CPacket::new)
			)
	),
	LOGIN(
		2,
		method_22308()
			.method_22315(
				NetworkSide.CLIENTBOUND,
				new NetworkState.class_4532()
					.method_22313(LoginDisconnectS2CPacket.class, LoginDisconnectS2CPacket::new)
					.method_22313(LoginHelloS2CPacket.class, LoginHelloS2CPacket::new)
					.method_22313(LoginSuccessS2CPacket.class, LoginSuccessS2CPacket::new)
					.method_22313(LoginCompressionS2CPacket.class, LoginCompressionS2CPacket::new)
					.method_22313(LoginQueryRequestS2CPacket.class, LoginQueryRequestS2CPacket::new)
			)
			.method_22315(
				NetworkSide.SERVERBOUND,
				new NetworkState.class_4532()
					.method_22313(LoginHelloC2SPacket.class, LoginHelloC2SPacket::new)
					.method_22313(LoginKeyC2SPacket.class, LoginKeyC2SPacket::new)
					.method_22313(LoginQueryResponseC2SPacket.class, LoginQueryResponseC2SPacket::new)
			)
	);

	private static final NetworkState[] STATES = new NetworkState[4];
	private static final Map<Class<? extends Packet<?>>, NetworkState> HANDLER_STATE_MAP = Maps.<Class<? extends Packet<?>>, NetworkState>newHashMap();
	private final int field_20594;
	private final Map<NetworkSide, ? extends NetworkState.class_4532<?>> field_20595;

	private static NetworkState.class_4533 method_22308() {
		return new NetworkState.class_4533();
	}

	private NetworkState(int j, NetworkState.class_4533 arg) {
		this.field_20594 = j;
		this.field_20595 = arg.field_20598;
	}

	@Nullable
	public Integer getPacketId(NetworkSide networkSide, Packet<?> packet) {
		return ((NetworkState.class_4532)this.field_20595.get(networkSide)).method_22312(packet.getClass());
	}

	@Nullable
	public Packet<?> getPacketHandler(NetworkSide networkSide, int i) {
		return ((NetworkState.class_4532)this.field_20595.get(networkSide)).method_22310(i);
	}

	public int getId() {
		return this.field_20594;
	}

	@Nullable
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
			networkState.field_20595
				.forEach(
					(networkSide, arg) -> arg.method_22309()
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

	static class class_4532<T extends PacketListener> {
		private final Object2IntMap<Class<? extends Packet<T>>> field_20596 = SystemUtil.consume(
			new Object2IntOpenHashMap<>(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1)
		);
		private final List<Supplier<? extends Packet<T>>> field_20597 = Lists.<Supplier<? extends Packet<T>>>newArrayList();

		private class_4532() {
		}

		public <P extends Packet<T>> NetworkState.class_4532<T> method_22313(Class<P> class_, Supplier<P> supplier) {
			int i = this.field_20597.size();
			int j = this.field_20596.put(class_, i);
			if (j != -1) {
				String string = "Packet " + class_ + " is already registered to ID " + j;
				LogManager.getLogger().fatal(string);
				throw new IllegalArgumentException(string);
			} else {
				this.field_20597.add(supplier);
				return this;
			}
		}

		@Nullable
		public Integer method_22312(Class<?> class_) {
			int i = this.field_20596.getInt(class_);
			return i == -1 ? null : i;
		}

		@Nullable
		public Packet<?> method_22310(int i) {
			Supplier<? extends Packet<T>> supplier = (Supplier<? extends Packet<T>>)this.field_20597.get(i);
			return supplier != null ? (Packet)supplier.get() : null;
		}

		public Iterable<Class<? extends Packet<?>>> method_22309() {
			return Iterables.unmodifiableIterable(this.field_20596.keySet());
		}
	}

	static class class_4533 {
		private final Map<NetworkSide, NetworkState.class_4532<?>> field_20598 = Maps.newEnumMap(NetworkSide.class);

		private class_4533() {
		}

		public <T extends PacketListener> NetworkState.class_4533 method_22315(NetworkSide networkSide, NetworkState.class_4532<T> arg) {
			this.field_20598.put(networkSide, arg);
			return this;
		}
	}
}
