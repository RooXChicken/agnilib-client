package com.rooxchicken.agnilib.networking.handlers;

import java.util.HashMap;
import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.networking.AgniLibDataHandler;
import com.rooxchicken.agnilib.objects.Component;
import com.rooxchicken.agnilib.objects.Image;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class ImageHandler extends AgniLibDataHandler
{
    public ImageHandler(AgniLibClient _client)
    {
        super(_client);
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        Image _finishedImage = (Image)agnilib.getComponent(_buf, Image.class);

        _finishedImage.name = agnilib.readString(_buf);

        _finishedImage.r = _buf.readFloat();
        _finishedImage.g = _buf.readFloat();
        _finishedImage.b = _buf.readFloat();
        _finishedImage.a = _buf.readFloat();
        _finishedImage.blend = _buf.readBoolean();
    }
}
