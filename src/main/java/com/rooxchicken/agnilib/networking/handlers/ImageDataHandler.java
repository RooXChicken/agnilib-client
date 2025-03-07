package com.rooxchicken.agnilib.networking.handlers;

import java.util.HashMap;
import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.networking.AgniLibDataHandler;
import com.rooxchicken.agnilib.objects.Component;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class ImageDataHandler extends AgniLibDataHandler
{
    public HashMap<String, byte[]> workingTetxures;

    public ImageDataHandler(AgniLibClient _client)
    {
        super(_client);
        workingTetxures = new HashMap<String, byte[]>();
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        String _imageID = agnilib.readString(_buf);
        int _imgSize = Integer.parseInt(agnilib.readString(_buf));

        int _start = Integer.parseInt(agnilib.readString(_buf));
        int _size = Integer.parseInt(agnilib.readString(_buf));
        
        if(_start == 0)
            workingTetxures.put(_imageID, new byte[_imgSize]);

        byte[] _imageData = workingTetxures.get(_imageID);
        
        for(int i = _start; i < _start+_size; i++)
            _imageData[i] = _buf.readByte();
    }
}
