package com.rooxchicken.pmc.networking;

import com.rooxchicken.pmc.PMCClient;

import io.netty.buffer.ByteBuf;

public abstract class PMCDataHandler
{
    protected PMCClient pmc;
    
    public PMCDataHandler(PMCClient _pmc)
    {
        pmc = _pmc;
    }

    public void handleData(ByteBuf _buf) {}
}
