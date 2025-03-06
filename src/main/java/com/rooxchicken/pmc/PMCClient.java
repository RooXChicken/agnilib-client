package com.rooxchicken.pmc;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.rooxchicken.pmc.event.DrawGUICallback;
import com.rooxchicken.pmc.event.KeybindCallback;
import com.rooxchicken.pmc.networking.PMCDataHandler;
import com.rooxchicken.pmc.networking.PMCPacket;
import com.rooxchicken.pmc.networking.handlers.ComponentDataHandler;
import com.rooxchicken.pmc.networking.handlers.ComponentRemoveDataHandler;
import com.rooxchicken.pmc.networking.handlers.ImageCompleteHandler;
import com.rooxchicken.pmc.networking.handlers.ImageDataHandler;
import com.rooxchicken.pmc.networking.handlers.ImageHandler;
import com.rooxchicken.pmc.networking.handlers.LoginDataHandler;
import com.rooxchicken.pmc.networking.handlers.RegisterKeybindHandler;
import com.rooxchicken.pmc.networking.handlers.TextDataHandler;
import com.rooxchicken.pmc.objects.Component;
import com.rooxchicken.pmc.objects.Image;
import com.rooxchicken.pmc.objects.Text;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.encoding.VarInts;

public class PMCClient implements ClientModInitializer
{
	public static final short PMC_VERSION = 1;

	private static final short loginID = 0;
	private HashMap<Short, PMCDataHandler> registeredDataHandlers;

	private KeybindCallback keybindCallback;

	public static ArrayList<Component> components = new ArrayList<Component>();

	private boolean hasInitialized = false;

	@Override
	public void onInitializeClient()
	{
		HudRenderCallback.EVENT.register(new DrawGUICallback());

		registeredDataHandlers = new HashMap<Short, PMCDataHandler>();
		registerHandlers();

		ClientPlayConnectionEvents.DISCONNECT.register
		((handler, client) ->
		{
			components.clear();
			Image.loadedTextures.clear();

			keybindCallback.unregisterAllCustom();
			hasInitialized = false;
		});
		
		PayloadTypeRegistry.playC2S().register(PMCPacket.PACKET_ID, PMCPacket.PACKET_CODEC);

		ClientPlayNetworking.registerGlobalReceiver
		(PMCPacket.PACKET_ID, (_payload, _context) ->
		{
			ByteBuf _buf = Unpooled.copiedBuffer(_payload.buf());
			short _status = _buf.readShort();

			if(registeredDataHandlers.containsKey(_status))
				registeredDataHandlers.get(_status).handleData(_buf);
			else
				PMC.LOGGER.error("There is no registered handler for type: " + _status + "!");
		});

		ClientTickEvents.END_WORLD_TICK.register
		((client) ->
		{
			if(!hasInitialized)
			{
				hasInitialized = true;
				ByteBuf _buf = Unpooled.buffer();
				_buf.writeShort(loginID);

				sendData(_buf.array());
			}
		});

		keybindCallback = new KeybindCallback();
		WorldRenderEvents.END.register(keybindCallback);
	}

	public int getComponentID(String _id)
	{
		for(int i = 0; i < components.size(); i++)
			if(components.get(i).id.equals(_id))
				return i;

		return -1;
	}

	public Component getComponent(ByteBuf _buf, Class<?> _clazz)
	{
		String _id = readString(_buf);
		int _cID = getComponentID(_id);
		
		if(_cID == -1)
		{
			_cID = components.size();
			try
			{
				Constructor<?> _constructor = _clazz.getConstructor(String.class);
				Object _component = _constructor.newInstance(new Object[] { _id });
				
				components.add((Component)_component);
			}
			catch(Exception e)
			{
				PMC.LOGGER.error("Class does not have this constructor! ", e);
			}
		}

		return components.get(_cID);
	}

	public String readString(ByteBuf _buf)
	{
		return _buf.readCharSequence(_buf.readShort(), Charset.defaultCharset()).toString();
	}

	public static void writeString(String _string, ByteBuf _buf)
    {
        _buf.writeShort(_string.trim().length());
        _buf.writeCharSequence(_string, Charset.defaultCharset());
    }

	public static void sendData(byte[] _data)
	{
        ByteBuf _buf = Unpooled.buffer(0);
        VarInts.write(_buf, _data.length);

        byte[] _lengthData = new byte[_buf.readableBytes()];
        for(int i = 0; i < _lengthData.length; i++)
            _lengthData[i] = _buf.readByte();

        ByteArrayDataOutput _out = ByteStreams.newDataOutput();
        _out.write(_lengthData);
        _out.write(_data);
            
        ClientPlayNetworking.send(new PMCPacket(_out.toByteArray()));
	}

	private void registerHandlers()
	{
		registeredDataHandlers.put(loginID, new LoginDataHandler(this));
		
		registeredDataHandlers.put(Component.componentID, new ComponentDataHandler(this));
		registeredDataHandlers.put(Component.removeID, new ComponentRemoveDataHandler(this));

		registeredDataHandlers.put(Text.textID, new TextDataHandler(this));

		registeredDataHandlers.put(Image.preloadID, new ImageDataHandler(this));
		registeredDataHandlers.put(Image.finishID, new ImageCompleteHandler(this, (ImageDataHandler)registeredDataHandlers.get(Image.preloadID)));
		registeredDataHandlers.put(Image.imageID, new ImageHandler(this));

		registeredDataHandlers.put(KeybindCallback.createKeybindID, new RegisterKeybindHandler(this));
	}
}