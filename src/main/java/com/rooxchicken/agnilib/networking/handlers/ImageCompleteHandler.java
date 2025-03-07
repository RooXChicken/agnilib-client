package com.rooxchicken.agnilib.networking.handlers;

import java.util.HashMap;
import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.networking.AgniLibDataHandler;
import com.rooxchicken.agnilib.objects.Component;
import com.rooxchicken.agnilib.objects.Image;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class ImageCompleteHandler extends AgniLibDataHandler
{
    private ImageDataHandler imgHandler;

    public ImageCompleteHandler(AgniLibClient _client, ImageDataHandler _imgHandler)
    {
        super(_client);
        imgHandler = _imgHandler;
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        String _finishedImageID = agnilib.readString(_buf);
        Image.loadImage(_finishedImageID, imgHandler.workingTetxures.get(_finishedImageID));

        imgHandler.workingTetxures.remove(_finishedImageID);
    }
}
