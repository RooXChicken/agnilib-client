package com.rooxchicken.pmc.objects;

import java.util.ArrayList;

public class Component
{
    public static final short componentID = 0;
    public static final short removeID = 1;

    public String id = "";

    public double posX = 0;
    public double posY = 0;

    public double scaleX = 1;
    public double scaleY = 1;

    public Component(String _id)
    {
        id = _id;
    }

    public void onDestroy() {}
}
