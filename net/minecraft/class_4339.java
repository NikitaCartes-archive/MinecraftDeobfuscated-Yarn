/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.realmsclient.dto.RegionPingResult;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class class_4339 {
    public static List<RegionPingResult> method_20984(class_4340 ... args) {
        for (class_4340 lv : args) {
            class_4339.method_20982(lv.field_19574);
        }
        ArrayList<RegionPingResult> list = new ArrayList<RegionPingResult>();
        for (class_4340 lv2 : args) {
            list.add(new RegionPingResult(lv2.field_19573, class_4339.method_20982(lv2.field_19574)));
        }
        Collections.sort(list, new Comparator<RegionPingResult>(){

            public int method_20986(RegionPingResult regionPingResult, RegionPingResult regionPingResult2) {
                return regionPingResult.ping() - regionPingResult2.ping();
            }

            @Override
            public /* synthetic */ int compare(Object object, Object object2) {
                return this.method_20986((RegionPingResult)object, (RegionPingResult)object2);
            }
        });
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int method_20982(String string) {
        int i = 700;
        long l = 0L;
        Socket socket = null;
        for (int j = 0; j < 5; ++j) {
            try {
                InetSocketAddress socketAddress = new InetSocketAddress(string, 80);
                socket = new Socket();
                long m = class_4339.method_20985();
                socket.connect(socketAddress, 700);
                l += class_4339.method_20985() - m;
                class_4339.method_20983(socket);
                continue;
            } catch (Exception exception) {
                l += 700L;
                continue;
            } finally {
                class_4339.method_20983(socket);
            }
        }
        return (int)((double)l / 5.0);
    }

    private static void method_20983(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static long method_20985() {
        return System.currentTimeMillis();
    }

    public static List<RegionPingResult> method_20981() {
        return class_4339.method_20984(class_4340.values());
    }

    @Environment(value=EnvType.CLIENT)
    static enum class_4340 {
        US_EAST_1("us-east-1", "ec2.us-east-1.amazonaws.com"),
        US_WEST_2("us-west-2", "ec2.us-west-2.amazonaws.com"),
        US_WEST_1("us-west-1", "ec2.us-west-1.amazonaws.com"),
        EU_WEST_1("eu-west-1", "ec2.eu-west-1.amazonaws.com"),
        AP_SOUTHEAST_1("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com"),
        AP_SOUTHEAST_2("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com"),
        AP_NORTHEAST_1("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com"),
        SA_EAST_1("sa-east-1", "ec2.sa-east-1.amazonaws.com");

        private final String field_19573;
        private final String field_19574;

        private class_4340(String string2, String string3) {
            this.field_19573 = string2;
            this.field_19574 = string3;
        }
    }
}

