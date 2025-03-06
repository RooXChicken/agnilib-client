package com.rooxchicken.pmc.networking.handlers;

import com.rooxchicken.pmc.PMCClient;
import com.rooxchicken.pmc.networking.PMCDataHandler;
import com.rooxchicken.pmc.objects.Component;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class ComponentRemoveDataHandler extends PMCDataHandler
{
    public ComponentRemoveDataHandler(PMCClient _client)
    {
        super(_client);
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        Component _toRemove = pmc.getComponent(_buf, Component.class);
        PMCClient.components.remove(_toRemove);
    }
}
