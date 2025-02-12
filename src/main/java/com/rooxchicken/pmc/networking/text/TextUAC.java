package com.rooxchicken.pmc.networking.text;

import com.rooxchicken.pmc.PMC;
import com.rooxchicken.pmc.data.Text;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registry;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Style.Codecs;
import net.minecraft.util.Identifier;

public record TextUAC(byte[] buf) implements CustomPayload
{
    public static final CustomPayload.Id<TextUAC> PACKET_ID = new CustomPayload.Id<>(Identifier.of(PMC.MOD_ID, "channel"));
    public static final PacketCodec<RegistryByteBuf, TextUAC> PACKET_CODEC = PacketCodecs.BYTE_ARRAY.xmap(TextUAC::new, TextUAC::buf).cast();
    
    @Override
    public Id<? extends CustomPayload> getId() { return PACKET_ID; }
}