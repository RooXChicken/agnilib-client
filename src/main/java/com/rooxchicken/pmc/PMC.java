package com.rooxchicken.pmc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rooxchicken.pmc.networking.text.TextUAC;

public class PMC implements ModInitializer
{
	public static final String CHANNEL = "pmc:channel";
	
	public static final String MOD_ID = "pmc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
	{
		PayloadTypeRegistry.playS2C().register(TextUAC.PACKET_ID, TextUAC.PACKET_CODEC);

		LOGGER.info("Allowing S2C & C2S since 1987! [made by roo]");
	}
}