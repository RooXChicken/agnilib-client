package com.rooxchicken.agnilib;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.rooxchicken.agnilib.data.AgniLibSettings;
import com.rooxchicken.agnilib.data.PlayerModification;
import com.rooxchicken.agnilib.event.DrawGUICallback;
import com.rooxchicken.agnilib.event.KeybindCallback;
import com.rooxchicken.agnilib.networking.AgniLibDataHandler;
import com.rooxchicken.agnilib.networking.AgniLibPacket;
import com.rooxchicken.agnilib.networking.handlers.ComponentDataHandler;
import com.rooxchicken.agnilib.networking.handlers.ComponentRemoveDataHandler;
import com.rooxchicken.agnilib.networking.handlers.ImageCompleteHandler;
import com.rooxchicken.agnilib.networking.handlers.ImageDataHandler;
import com.rooxchicken.agnilib.networking.handlers.ImageHandler;
import com.rooxchicken.agnilib.networking.handlers.LoginDataHandler;
import com.rooxchicken.agnilib.networking.handlers.PlayerModificationHandler;
import com.rooxchicken.agnilib.networking.handlers.RegisterKeybindHandler;
import com.rooxchicken.agnilib.networking.handlers.TextDataHandler;
import com.rooxchicken.agnilib.networking.senders.TargetDataSender;
import com.rooxchicken.agnilib.objects.Component;
import com.rooxchicken.agnilib.objects.Image;
import com.rooxchicken.agnilib.objects.Text;
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
import net.minecraft.util.math.Vec3d;

public class AgniLibClient implements ClientModInitializer
{
	public static final short AgniLib_VERSION = 1;
	public static boolean hasInitialized = false;

	public static ArrayList<Component> components = new ArrayList<Component>();
	
	private HashMap<Short, AgniLibDataHandler> registeredDataHandlers;
	public KeybindCallback keybindCallback;

	@Override
	public void onInitializeClient()
	{
		HudRenderCallback.EVENT.register(new DrawGUICallback());

		registeredDataHandlers = new HashMap<Short, AgniLibDataHandler>();
		registerHandlers();

		keybindCallback = new KeybindCallback();
		WorldRenderEvents.END.register(keybindCallback);

		ClientPlayConnectionEvents.DISCONNECT.register
		((handler, client) ->
		{
			AgniLibSettings.save(true);
			
			components.clear();
			Image.loadedTextures.clear();

			keybindCallback.unregisterAllCustom();
			hasInitialized = false;
		});
		
		PayloadTypeRegistry.playC2S().register(AgniLibPacket.PACKET_ID, AgniLibPacket.PACKET_CODEC);

		ClientPlayNetworking.registerGlobalReceiver
		(AgniLibPacket.PACKET_ID, (_payload, _context) ->
		{
			ByteBuf _buf = Unpooled.copiedBuffer(_payload.buf());
			short _status = _buf.readShort();

			if(registeredDataHandlers.containsKey(_status))
				registeredDataHandlers.get(_status).handleData(_buf);
			else
				AgniLib.LOGGER.error("There is no registered handler for type: " + _status + "!");
		});

		ClientTickEvents.END_CLIENT_TICK.register
		((client) ->
		{
			PlayerModification.velocity = new Vec3d(0, 0, 0);
			if(!hasInitialized)
			{
				hasInitialized = true;
				ByteBuf _buf = Unpooled.buffer();
				_buf.writeShort(LoginDataHandler.loginID);
				
				sendData(_buf.array());
				AgniLibSettings.load();
			}

			TargetDataSender.sendData();
		});
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
				AgniLib.LOGGER.error("Class does not have this constructor! ", e);
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
            
        ClientPlayNetworking.send(new AgniLibPacket(_out.toByteArray()));
	}

	private void registerHandlers()
	{
		registeredDataHandlers.put(LoginDataHandler.loginID, new LoginDataHandler(this));
		
		registeredDataHandlers.put(Component.componentID, new ComponentDataHandler(this));
		registeredDataHandlers.put(Component.removeID, new ComponentRemoveDataHandler(this));

		registeredDataHandlers.put(Text.textID, new TextDataHandler(this));

		registeredDataHandlers.put(Image.preloadID, new ImageDataHandler(this));
		registeredDataHandlers.put(Image.finishID, new ImageCompleteHandler(this, (ImageDataHandler)registeredDataHandlers.get(Image.preloadID)));
		registeredDataHandlers.put(Image.imageID, new ImageHandler(this));

		registeredDataHandlers.put(KeybindCallback.createKeybindID, new RegisterKeybindHandler(this));

		registeredDataHandlers.put(PlayerModification.playerModificationID, new PlayerModificationHandler(this));
	}
}