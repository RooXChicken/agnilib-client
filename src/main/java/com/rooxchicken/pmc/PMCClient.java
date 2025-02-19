package com.rooxchicken.pmc;

import java.nio.charset.Charset;
import java.util.ArrayList;

import com.rooxchicken.pmc.data.Component;
import com.rooxchicken.pmc.data.Text;
import com.rooxchicken.pmc.event.DrawGUICallback;
import com.rooxchicken.pmc.networking.PMCPacket;

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
import net.minecraft.network.encoding.VarInts;
import net.minecraft.network.packet.CustomPayload.Id;
import net.minecraft.util.Identifier;

public class PMCClient implements ClientModInitializer
{
	public static ArrayList<Component> components = new ArrayList<Component>();

	@Override
	public void onInitializeClient()
	{
		HudRenderCallback.EVENT.register(new DrawGUICallback());

		ClientPlayConnectionEvents.DISCONNECT.register
		((handler, client) ->
		{
			components.clear();
		});
		
		ClientPlayNetworking.registerGlobalReceiver
		(PMCPacket.PACKET_ID, (_payload, _context) ->
		{
			PacketByteBuf _buf = new PacketByteBuf(Unpooled.copiedBuffer(_payload.buf()));
			short _status = _buf.readShort();

			switch(_status)
			{
				case Component.componentID:
					Component _component = getComponent(_buf);

					_component.posX = _buf.readDouble();
					_component.posY = _buf.readDouble();
					_component.scaleX = _buf.readDouble();
					_component.scaleY = _buf.readDouble();
				break;

				case Text.textID:
					Text _text = getText(_buf);

					_text.text = readString(_buf);
					_text.color = _buf.readInt();
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

	private Component getComponent(PacketByteBuf _buf)
	{
		String _id = readString(_buf);
		int _cID = getComponentID(_id);
		
		if(_cID == -1)
		{
			_cID = components.size();
			components.add(new Component(_id));
		}

		return components.get(_cID);
	}

	private Text getText(PacketByteBuf _buf)
	{
		String _id = readString(_buf);
		int _cID = getComponentID(_id);
		
		if(_cID == -1)
		{
			_cID = components.size();
			components.add(new Text(_id));
		}

		return (Text)components.get(_cID);
	}

	private String readString(PacketByteBuf _buf)
	{
		return _buf.readCharSequence(_buf.readShort(), Charset.defaultCharset()).toString();
	}
}