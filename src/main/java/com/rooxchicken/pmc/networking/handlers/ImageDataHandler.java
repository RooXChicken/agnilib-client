package com.rooxchicken.pmc.networking.handlers;

import java.util.HashMap;
import com.rooxchicken.pmc.PMCClient;
import com.rooxchicken.pmc.networking.PMCDataHandler;
import com.rooxchicken.pmc.objects.Component;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class ImageDataHandler extends PMCDataHandler
{
    public HashMap<String, byte[]> workingTetxures;

    public ImageDataHandler(PMCClient _client)
    {
        super(_client);
        workingTetxures = new HashMap<String, byte[]>();
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        String _imageID = pmc.readString(_buf);
        int _imgSize = Integer.parseInt(pmc.readString(_buf));

        int _start = Integer.parseInt(pmc.readString(_buf));
        int _size = Integer.parseInt(pmc.readString(_buf));
        
        if(_start == 0)
            workingTetxures.put(_imageID, new byte[_imgSize]);

        byte[] _imageData = workingTetxures.get(_imageID);
        
        for(int i = _start; i < _start+_size; i++)
            _imageData[i] = _buf.readByte();
    }
}
