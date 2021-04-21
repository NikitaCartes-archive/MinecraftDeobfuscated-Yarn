/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.ResettingWorldTask;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ResettingWorldTemplateTask
extends ResettingWorldTask {
    private final WorldTemplate template;

    public ResettingWorldTemplateTask(WorldTemplate template, long serverId, Text title, Runnable callback) {
        super(serverId, title, callback);
        this.template = template;
    }

    @Override
    protected void resetWorld(RealmsClient client, long worldId) throws RealmsServiceException {
        client.resetWorldWithTemplate(worldId, this.template.id);
    }
}

