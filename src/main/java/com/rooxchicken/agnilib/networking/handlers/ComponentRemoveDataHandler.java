package com.rooxchicken.agnilib.networking.handlers;

import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.networking.AgniLibDataHandler;
import com.rooxchicken.agnilib.objects.Component;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class ComponentRemoveDataHandler extends AgniLibDataHandler
{
    public ComponentRemoveDataHandler(AgniLibClient _client)
    {
        super(_client);
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        Component _toRemove = agnilib.getComponent(_buf, Component.class);
        AgniLibClient.components.remove(_toRemove);
    }
}
