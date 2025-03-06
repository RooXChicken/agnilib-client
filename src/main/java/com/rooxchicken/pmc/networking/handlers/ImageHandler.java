package com.rooxchicken.pmc.networking.handlers;

import java.util.HashMap;
import com.rooxchicken.pmc.PMCClient;
import com.rooxchicken.pmc.networking.PMCDataHandler;
import com.rooxchicken.pmc.objects.Component;
import com.rooxchicken.pmc.objects.Image;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class ImageHandler extends PMCDataHandler
{
    public ImageHandler(PMCClient _client)
    {
        super(_client);
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        Image _finishedImage = (Image)pmc.getComponent(_buf, Image.class);

        _finishedImage.name = pmc.readString(_buf);

        _finishedImage.r = _buf.readFloat();
        _finishedImage.g = _buf.readFloat();
        _finishedImage.b = _buf.readFloat();
        _finishedImage.a = _buf.readFloat();
        _finishedImage.blend = _buf.readBoolean();
    }
}
