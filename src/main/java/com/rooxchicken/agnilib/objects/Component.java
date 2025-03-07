package com.rooxchicken.agnilib.objects;

import java.util.ArrayList;

public class Component
{
    public static final short componentID = 1;
    public static final short removeID = 2;

    public String id = "";

    public boolean positionType = false;
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
