package net.minecraft.network.listener;

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
import net.minecraft.client.network.packet.PlayerActionResponseS2CPacket;
import net.minecraft.client.network.packet.PlayerListHeaderS2CPacket;
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket;
import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnPositionS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnS2CPacket;
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

public interface ClientPlayPacketListener extends PacketListener {
	void onEntitySpawn(EntitySpawnS2CPacket packet);

	void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket packet);

	void onEntitySpawnGlobal(EntitySpawnGlobalS2CPacket packet);

	void onMobSpawn(MobSpawnS2CPacket packet);

	void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet);

	void onPaintingSpawn(PaintingSpawnS2CPacket packet);

	void onPlayerSpawn(PlayerSpawnS2CPacket packet);

	void onEntityAnimation(EntityAnimationS2CPacket packet);

	void onStatistics(StatisticsS2CPacket packet);

	void onUnlockRecipes(UnlockRecipesS2CPacket packet);

	void onBlockDestroyProgress(BlockBreakingProgressS2CPacket packet);

	void onSignEditorOpen(SignEditorOpenS2CPacket packet);

	void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet);

	void onBlockAction(BlockActionS2CPacket packet);

	void onBlockUpdate(BlockUpdateS2CPacket packet);

	void onChatMessage(ChatMessageS2CPacket packet);

	void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet);

	void onMapUpdate(MapUpdateS2CPacket packet);

	void onGuiActionConfirm(ConfirmGuiActionS2CPacket packet);

	void onGuiClose(GuiCloseS2CPacket packet);

	void onInventory(InventoryS2CPacket packet);

	void onGuiOpen(GuiOpenS2CPacket packet);

	void onGuiUpdate(GuiUpdateS2CPacket packet);

	void onGuiSlotUpdate(GuiSlotUpdateS2CPacket packet);

	void onCustomPayload(CustomPayloadS2CPacket packet);

	void onDisconnect(DisconnectS2CPacket packet);

	void onEntityStatus(EntityStatusS2CPacket packet);

	void onEntityAttach(EntityAttachS2CPacket packet);

	void onEntityPassengersSet(EntityPassengersSetS2CPacket packet);

	void onExplosion(ExplosionS2CPacket packet);

	void onGameStateChange(GameStateChangeS2CPacket packet);

	void onKeepAlive(KeepAliveS2CPacket packet);

	void onChunkData(ChunkDataS2CPacket packet);

	void onUnloadChunk(UnloadChunkS2CPacket packet);

	void onWorldEvent(WorldEventS2CPacket packet);

	void onGameJoin(GameJoinS2CPacket packet);

	void onEntityUpdate(EntityS2CPacket packet);

	void onPlayerPositionLook(PlayerPositionLookS2CPacket packet);

	void onParticle(ParticleS2CPacket packet);

	void onPlayerAbilities(PlayerAbilitiesS2CPacket packet);

	void onPlayerList(PlayerListS2CPacket packet);

	void onEntitiesDestroy(EntitiesDestroyS2CPacket packet);

	void onRemoveEntityEffect(RemoveEntityEffectS2CPacket packet);

	void onPlayerRespawn(PlayerRespawnS2CPacket packet);

	void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet);

	void onHeldItemChange(HeldItemChangeS2CPacket packet);

	void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet);

	void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet);

	void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet);

	void onEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet);

	void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet);

	void onHealthUpdate(HealthUpdateS2CPacket packet);

	void onTeam(TeamS2CPacket packet);

	void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket packet);

	void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet);

	void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet);

	void onPlaySound(PlaySoundS2CPacket packet);

	void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet);

	void onPlaySoundId(PlaySoundIdS2CPacket packet);

	void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet);

	void onEntityPosition(EntityPositionS2CPacket packet);

	void onEntityAttributes(EntityAttributesS2CPacket packet);

	void onEntityPotionEffect(EntityPotionEffectS2CPacket packet);

	void onSynchronizeTags(SynchronizeTagsS2CPacket packet);

	void onCombatEvent(CombatEventS2CPacket packet);

	void onDifficulty(DifficultyS2CPacket packet);

	void onSetCameraEntity(SetCameraEntityS2CPacket packet);

	void onWorldBorder(WorldBorderS2CPacket packet);

	void onTitle(TitleS2CPacket packet);

	void onPlayerListHeader(PlayerListHeaderS2CPacket packet);

	void onResourcePackSend(ResourcePackSendS2CPacket packet);

	void onBossBar(BossBarS2CPacket packet);

	void onCooldownUpdate(CooldownUpdateS2CPacket packet);

	void onVehicleMove(VehicleMoveS2CPacket packet);

	void onAdvancements(AdvancementUpdateS2CPacket packet);

	void onSelectAdvancementTab(SelectAdvancementTabS2CPacket packet);

	void onCraftFailedResponse(CraftFailedResponseS2CPacket packet);

	void onCommandTree(CommandTreeS2CPacket packet);

	void onStopSound(StopSoundS2CPacket packet);

	void onCommandSuggestions(CommandSuggestionsS2CPacket packet);

	void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet);

	void onLookAt(LookAtS2CPacket packet);

	void onTagQuery(TagQueryResponseS2CPacket packet);

	void onLightUpdate(LightUpdateS2CPacket packet);

	void onOpenWrittenBook(OpenWrittenBookS2CPacket packet);

	void onOpenContainer(OpenContainerPacket packet);

	void onSetTradeOffers(SetTradeOffersPacket packet);

	void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet);

	void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet);

	void handlePlayerActionResponse(PlayerActionResponseS2CPacket packet);
}
