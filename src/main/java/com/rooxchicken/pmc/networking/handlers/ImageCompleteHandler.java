package com.rooxchicken.pmc.networking.handlers;

import java.util.HashMap;
import com.rooxchicken.pmc.PMCClient;
import com.rooxchicken.pmc.networking.PMCDataHandler;
import com.rooxchicken.pmc.objects.Component;
import com.rooxchicken.pmc.objects.Image;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class ImageCompleteHandler extends PMCDataHandler
{
    private ImageDataHandler imgHandler;

    public ImageCompleteHandler(PMCClient _client, ImageDataHandler _imgHandler)
    {
        super(_client);
        imgHandler = _imgHandler;
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        String _finishedImageID = pmc.readString(_buf);
        Image.loadImage(_finishedImageID, imgHandler.workingTetxures.get(_finishedImageID));

        imgHandler.workingTetxures.remove(_finishedImageID);
    }
}
