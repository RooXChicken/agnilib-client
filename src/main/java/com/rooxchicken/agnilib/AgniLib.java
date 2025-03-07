package com.rooxchicken.agnilib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rooxchicken.agnilib.networking.AgniLibPacket;

public class AgniLib implements ModInitializer
{
	public static final String CHANNEL = "agnilib:channel";
	
	public static final String MOD_ID = "agnilib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
	{
		PayloadTypeRegistry.playS2C().register(AgniLibPacket.PACKET_ID, AgniLibPacket.PACKET_CODEC);

		LOGGER.info("Allowing S2C & C2S since 1987! [made by roo]");
	}
}