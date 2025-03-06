package com.rooxchicken.pmc.networking.handlers;

import com.rooxchicken.pmc.PMCClient;
import com.rooxchicken.pmc.networking.PMCDataHandler;
import com.rooxchicken.pmc.objects.Component;
import com.rooxchicken.pmc.objects.Text;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class TextDataHandler extends PMCDataHandler
{
    public TextDataHandler(PMCClient _client)
    {
        super(_client);
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        Text _text = (Text)pmc.getComponent(_buf, Text.class);

        _text.text = pmc.readString(_buf);
        _text.color = _buf.readInt();
    }
}
