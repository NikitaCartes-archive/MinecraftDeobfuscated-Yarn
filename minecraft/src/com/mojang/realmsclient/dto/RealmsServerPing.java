package com.mojang.realmsclient.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;

@Environment(EnvType.CLIENT)
public class RealmsServerPing extends class_4352 {
	public volatile String nrOfPlayers = "0";
	public volatile String playerList = "";
}
