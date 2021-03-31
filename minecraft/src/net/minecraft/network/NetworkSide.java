package net.minecraft.network;

public enum NetworkSide {
	SERVERBOUND,
	CLIENTBOUND;

	public NetworkSide getOpposite() {
		return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
	}
}
