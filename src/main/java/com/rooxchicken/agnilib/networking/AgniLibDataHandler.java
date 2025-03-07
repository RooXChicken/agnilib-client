package com.rooxchicken.agnilib.networking;

import com.rooxchicken.agnilib.AgniLibClient;

import io.netty.buffer.ByteBuf;

public abstract class AgniLibDataHandler
{
    protected AgniLibClient agnilib;
    
    public AgniLibDataHandler(AgniLibClient _agnilib)
    {
        agnilib = _agnilib;
    }

    public void handleData(ByteBuf _buf) {}
}
