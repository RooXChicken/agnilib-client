package com.rooxchicken.pmc.networking.handlers;

import com.rooxchicken.pmc.PMCClient;
import com.rooxchicken.pmc.networking.PMCDataHandler;
import com.rooxchicken.pmc.objects.Component;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class ComponentDataHandler extends PMCDataHandler
{
    public ComponentDataHandler(PMCClient _client)
    {
        super(_client);
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        Component _component = pmc.getComponent(_buf, Component.class);

        _component.posX = _buf.readDouble();
        _component.posY = _buf.readDouble();
        _component.scaleX = _buf.readDouble();
        _component.scaleY = _buf.readDouble();
    }
}
