package com.rooxchicken.pmc.networking.handlers;

import com.rooxchicken.pmc.PMCClient;
import com.rooxchicken.pmc.networking.PMCDataHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class LoginDataHandler extends PMCDataHandler
{
    public LoginDataHandler(PMCClient _client)
    {
        super(_client);
    }
    
    @Override
    public void handleData(ByteBuf _buf)
    {
        int _version = _buf.readInt();

        if(_version > PMCClient.PMC_VERSION)
            MinecraftClient.getInstance().player.sendMessage(net.minecraft.text.Text.of("§4Your mod version is out of date! Expect bugs! C: " + PMCClient.PMC_VERSION + " S: " + _version));
        else if(_version < PMCClient.PMC_VERSION)
            MinecraftClient.getInstance().player.sendMessage(net.minecraft.text.Text.of("§4Your mod version is too new! Expect bugs! C: " + PMCClient.PMC_VERSION + " S: " + _version));
    }
}
