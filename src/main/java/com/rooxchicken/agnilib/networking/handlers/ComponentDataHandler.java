package com.rooxchicken.agnilib.networking.handlers;

import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.networking.AgniLibDataHandler;
import com.rooxchicken.agnilib.objects.Component;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class ComponentDataHandler extends AgniLibDataHandler
{
    public ComponentDataHandler(AgniLibClient _client)
    {
        super(_client);
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        Component _component = agnilib.getComponent(_buf, Component.class);

        _component.positionType = _buf.readBoolean();
        _component.posX = _buf.readDouble();
        _component.posY = _buf.readDouble();
        
        _component.scaleX = _buf.readDouble();
        _component.scaleY = _buf.readDouble();
    }
}
