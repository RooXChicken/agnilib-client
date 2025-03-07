package com.rooxchicken.agnilib.networking.handlers;

import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.networking.AgniLibDataHandler;
import com.rooxchicken.agnilib.objects.Component;
import com.rooxchicken.agnilib.objects.Text;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class TextDataHandler extends AgniLibDataHandler
{
    public TextDataHandler(AgniLibClient _client)
    {
        super(_client);
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        Text _text = (Text)agnilib.getComponent(_buf, Text.class);

        _text.text = agnilib.readString(_buf);
        _text.color = _buf.readInt();
    }
}
