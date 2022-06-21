package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.RealmsServer;

@Environment(EnvType.CLIENT)
public class class_7580 {
	private final MinecraftClient field_39692;
	private final Set<RealmsServer> field_39693 = Sets.<RealmsServer>newHashSet();
	private List<RealmsServer> field_39694 = Lists.<RealmsServer>newArrayList();

	public class_7580(MinecraftClient minecraftClient) {
		this.field_39692 = minecraftClient;
	}

	public List<RealmsServer> method_44623(List<RealmsServer> list) {
		List<RealmsServer> list2 = new ArrayList(list);
		list2.sort(new RealmsServer.McoServerComparator(this.field_39692.getSession().getUsername()));
		boolean bl = list2.removeAll(this.field_39693);
		if (!bl) {
			this.field_39693.clear();
		}

		this.field_39694 = list2;
		return List.copyOf(this.field_39694);
	}

	public synchronized List<RealmsServer> method_44622(RealmsServer realmsServer) {
		this.field_39694.remove(realmsServer);
		this.field_39693.add(realmsServer);
		return List.copyOf(this.field_39694);
	}
}
