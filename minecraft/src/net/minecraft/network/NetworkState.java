package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ConfirmGuiActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectVillagerTradeC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockActionS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseContainerS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.ConfirmGuiActionS2CPacket;
import net.minecraft.network.packet.s2c.play.ContainerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ContainerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnGlobalS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenContainerS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseContainerS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.PaintingSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.TagQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
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
			this.addPacket(NetworkSide.CLIENTBOUND, CloseContainerS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, InventoryS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ContainerPropertyUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ContainerSlotUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, CooldownUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, CustomPayloadS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlaySoundIdS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, DisconnectS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityStatusS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ExplosionS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, UnloadChunkS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, GameStateChangeS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, OpenHorseContainerS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, KeepAliveS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ChunkDataS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, WorldEventS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, ParticleS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, LightUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, GameJoinS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, MapUpdateS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, SetTradeOffersS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityS2CPacket.MoveRelative.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityS2CPacket.RotateAndMoveRelative.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityS2CPacket.Rotate.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntityS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, VehicleMoveS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, OpenWrittenBookS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, OpenContainerS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, SignEditorOpenS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, CraftFailedResponseS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlayerAbilitiesS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, CombatEventS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlayerListS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, LookAtS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlayerPositionLookS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, UnlockRecipesS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, EntitiesDestroyS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, RemoveEntityStatusEffectS2CPacket.class);
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
			this.addPacket(NetworkSide.CLIENTBOUND, EntityStatusEffectS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, SynchronizeRecipesS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, SynchronizeTagsS2CPacket.class);
			this.addPacket(NetworkSide.CLIENTBOUND, PlayerActionResponseS2CPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, TeleportConfirmC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, QueryBlockNbtC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, UpdateDifficultyC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, ChatMessageC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, ClientStatusC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, ClientSettingsC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, RequestCommandCompletionsC2SPacket.class);
			this.addPacket(NetworkSide.SERVERBOUND, ConfirmGuiActionC2SPacket.class);
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

	protected NetworkState addPacket(NetworkSide receivingSide, Class<? extends Packet<?>> packetClass) {
		BiMap<Integer, Class<? extends Packet<?>>> biMap = (BiMap<Integer, Class<? extends Packet<?>>>)this.packetHandlerMap.get(receivingSide);
		if (biMap == null) {
			biMap = HashBiMap.create();
			this.packetHandlerMap.put(receivingSide, biMap);
		}

		if (biMap.containsValue(packetClass)) {
			String string = receivingSide + " packet " + packetClass + " is already known to ID " + biMap.inverse().get(packetClass);
			LogManager.getLogger().fatal(string);
			throw new IllegalArgumentException(string);
		} else {
			biMap.put(biMap.size(), packetClass);
			return this;
		}
	}

	public Integer getPacketId(NetworkSide side, Packet<?> packet) throws Exception {
		return (Integer)((BiMap)this.packetHandlerMap.get(side)).inverse().get(packet.getClass());
	}

	@Nullable
	public Packet<?> getPacketHandler(NetworkSide side, int packetId) throws IllegalAccessException, InstantiationException {
		Class<? extends Packet<?>> class_ = (Class<? extends Packet<?>>)((BiMap)this.packetHandlerMap.get(side)).get(packetId);
		return class_ == null ? null : (Packet)class_.newInstance();
	}

	public int getId() {
		return this.id;
	}

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
