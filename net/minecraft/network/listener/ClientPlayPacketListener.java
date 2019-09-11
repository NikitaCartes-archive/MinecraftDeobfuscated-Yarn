/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.network.listener.PacketListener;

public interface ClientPlayPacketListener
extends PacketListener {
    public void onEntitySpawn(EntitySpawnS2CPacket var1);

    public void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket var1);

    public void onEntitySpawnGlobal(EntitySpawnGlobalS2CPacket var1);

    public void onMobSpawn(MobSpawnS2CPacket var1);

    public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket var1);

    public void onPaintingSpawn(PaintingSpawnS2CPacket var1);

    public void onPlayerSpawn(PlayerSpawnS2CPacket var1);

    public void onEntityAnimation(EntityAnimationS2CPacket var1);

    public void onStatistics(StatisticsS2CPacket var1);

    public void onUnlockRecipes(UnlockRecipesS2CPacket var1);

    public void onBlockDestroyProgress(BlockBreakingProgressS2CPacket var1);

    public void onSignEditorOpen(SignEditorOpenS2CPacket var1);

    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket var1);

    public void onBlockAction(BlockActionS2CPacket var1);

    public void onBlockUpdate(BlockUpdateS2CPacket var1);

    public void onChatMessage(ChatMessageS2CPacket var1);

    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket var1);

    public void onMapUpdate(MapUpdateS2CPacket var1);

    public void onGuiActionConfirm(ConfirmGuiActionS2CPacket var1);

    public void onGuiClose(GuiCloseS2CPacket var1);

    public void onInventory(InventoryS2CPacket var1);

    public void onGuiOpen(GuiOpenS2CPacket var1);

    public void onGuiUpdate(GuiUpdateS2CPacket var1);

    public void onGuiSlotUpdate(GuiSlotUpdateS2CPacket var1);

    public void onCustomPayload(CustomPayloadS2CPacket var1);

    public void onDisconnect(DisconnectS2CPacket var1);

    public void onEntityStatus(EntityStatusS2CPacket var1);

    public void onEntityAttach(EntityAttachS2CPacket var1);

    public void onEntityPassengersSet(EntityPassengersSetS2CPacket var1);

    public void onExplosion(ExplosionS2CPacket var1);

    public void onGameStateChange(GameStateChangeS2CPacket var1);

    public void onKeepAlive(KeepAliveS2CPacket var1);

    public void onChunkData(ChunkDataS2CPacket var1);

    public void onUnloadChunk(UnloadChunkS2CPacket var1);

    public void onWorldEvent(WorldEventS2CPacket var1);

    public void onGameJoin(GameJoinS2CPacket var1);

    public void onEntityUpdate(EntityS2CPacket var1);

    public void onPlayerPositionLook(PlayerPositionLookS2CPacket var1);

    public void onParticle(ParticleS2CPacket var1);

    public void onPlayerAbilities(PlayerAbilitiesS2CPacket var1);

    public void onPlayerList(PlayerListS2CPacket var1);

    public void onEntitiesDestroy(EntitiesDestroyS2CPacket var1);

    public void onRemoveEntityEffect(RemoveEntityEffectS2CPacket var1);

    public void onPlayerRespawn(PlayerRespawnS2CPacket var1);

    public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket var1);

    public void onHeldItemChange(HeldItemChangeS2CPacket var1);

    public void onScoreboardDisplay(ScoreboardDisplayS2CPacket var1);

    public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket var1);

    public void onVelocityUpdate(EntityVelocityUpdateS2CPacket var1);

    public void onEquipmentUpdate(EntityEquipmentUpdateS2CPacket var1);

    public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket var1);

    public void onHealthUpdate(HealthUpdateS2CPacket var1);

    public void onTeam(TeamS2CPacket var1);

    public void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket var1);

    public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket var1);

    public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket var1);

    public void onPlaySound(PlaySoundS2CPacket var1);

    public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket var1);

    public void onPlaySoundId(PlaySoundIdS2CPacket var1);

    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket var1);

    public void onEntityPosition(EntityPositionS2CPacket var1);

    public void onEntityAttributes(EntityAttributesS2CPacket var1);

    public void onEntityPotionEffect(EntityPotionEffectS2CPacket var1);

    public void onSynchronizeTags(SynchronizeTagsS2CPacket var1);

    public void onCombatEvent(CombatEventS2CPacket var1);

    public void onDifficulty(DifficultyS2CPacket var1);

    public void onSetCameraEntity(SetCameraEntityS2CPacket var1);

    public void onWorldBorder(WorldBorderS2CPacket var1);

    public void onTitle(TitleS2CPacket var1);

    public void onPlayerListHeader(PlayerListHeaderS2CPacket var1);

    public void onResourcePackSend(ResourcePackSendS2CPacket var1);

    public void onBossBar(BossBarS2CPacket var1);

    public void onCooldownUpdate(CooldownUpdateS2CPacket var1);

    public void onVehicleMove(VehicleMoveS2CPacket var1);

    public void onAdvancements(AdvancementUpdateS2CPacket var1);

    public void onSelectAdvancementTab(SelectAdvancementTabS2CPacket var1);

    public void onCraftFailedResponse(CraftFailedResponseS2CPacket var1);

    public void onCommandTree(CommandTreeS2CPacket var1);

    public void onStopSound(StopSoundS2CPacket var1);

    public void onCommandSuggestions(CommandSuggestionsS2CPacket var1);

    public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket var1);

    public void onLookAt(LookAtS2CPacket var1);

    public void onTagQuery(TagQueryResponseS2CPacket var1);

    public void onLightUpdate(LightUpdateS2CPacket var1);

    public void onOpenWrittenBook(OpenWrittenBookS2CPacket var1);

    public void onOpenContainer(OpenContainerPacket var1);

    public void onSetTradeOffers(SetTradeOffersPacket var1);

    public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket var1);

    public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket var1);

    public void handlePlayerActionResponse(PlayerActionResponseS2CPacket var1);
}

