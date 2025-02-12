package com.rooxchicken.pmc;

import java.nio.charset.Charset;
import java.util.ArrayList;

import com.rooxchicken.pmc.data.Component;
import com.rooxchicken.pmc.data.Text;
import com.rooxchicken.pmc.event.DrawGUICallback;
import com.rooxchicken.pmc.networking.text.TextUAC;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.payload.PayloadHelper;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistryPacketHandler.Payload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload.Id;
import net.minecraft.util.Identifier;

public class PMCClient implements ClientModInitializer
{
	public static ArrayList<Component> components = new ArrayList<Component>();
	public static ArrayList<Text> text = new ArrayList<Text>();

	private int working = -1;

	@Override
	public void onInitializeClient()
	{
		HudRenderCallback.EVENT.register(new DrawGUICallback());

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
		{
			text.clear();
		});
		
		ClientPlayNetworking.registerGlobalReceiver(TextUAC.PACKET_ID, (_payload, _context) ->
		{
			PacketByteBuf _buf = new PacketByteBuf(Unpooled.copiedBuffer(_payload.buf()));
			byte _status = _buf.readByte();

			switch(_status)
			{
				case 7:
					String _componentID = getString(_buf);
					working = getComponentID(_componentID);

					if(working != -1)
						return;

					working = components.size();
					components.add(new Component(_componentID));
				break;
				case 8:
					components.get(working).posX = getDouble(_buf);
				break;
				case 9:
					components.get(working).posY = getDouble(_buf);
				break;
				case 10:
					components.get(working).scaleX = getDouble(_buf);
				break;
				case 11:
					components.get(working).scaleY = getDouble(_buf);
				break;

				case 12:
					String _textID = getString(_buf);
					working = getTextID(_textID);

					if(working != -1)
						return;

					working = text.size();
					text.add(new Text(components.get(getComponentID(_textID))));
				break;
				case 13:
					text.get(working).text = getString(_buf);
				break;
				case 14:
					text.get(working).color = getInt(_buf);
				break;
			}
		});
	}

	private int getComponentID(String _id)
	{
		for(int i = 0; i < components.size(); i++)
			if(components.get(i).id.equals(_id))
				return i;

		return -1;
	}

	private int getTextID(String _id)
	{
		for(int i = 0; i < text.size(); i++)
			if(text.get(i).component.id.equals(_id))
				return i;

		return -1;
	}

	private String getString(PacketByteBuf _buf)
	{
		return _buf.readCharSequence(_buf.readByte(), Charset.defaultCharset()).toString();
	}

	private int getInt(PacketByteBuf _buf)
	{
		return Integer.parseInt(getString(_buf));
	}

	private double getDouble(PacketByteBuf _buf)
	{
		return Double.parseDouble(getString(_buf));
	}
}