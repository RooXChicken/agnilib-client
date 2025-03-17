package com.rooxchicken.agnilib.networking.senders;

import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.networking.AgniLibDataSender;
import com.rooxchicken.agnilib.networking.handlers.PlayerModificationHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TargetDataSender
{
    public static void sendData()
    {
        MinecraftClient _client = MinecraftClient.getInstance();
        ByteBuf _buf = Unpooled.buffer();
        _buf.writeShort(PlayerModificationHandler.playerModification);
        _buf.writeShort(PlayerModificationHandler.playerGetTarget);

        HitResult _result = findCrosshairTarget(_client.player, 0, 256, 1.0f);
        if(_result == null)
        {
            _buf.writeBoolean(false);
            return;
        }

        _buf.writeDouble(_result.getPos().getX());
        _buf.writeDouble(_result.getPos().getY());
        _buf.writeDouble(_result.getPos().getZ());

        if(_result instanceof EntityHitResult _entity)
            AgniLibClient.writeString(_entity.getEntity().getUuidAsString(), _buf);
        else
            AgniLibClient.writeString("", _buf);

        AgniLibClient.sendData(_buf.array());
    }

    private static HitResult findCrosshairTarget(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta)
	{
		double d = Math.max(blockInteractionRange, entityInteractionRange);
		double e = MathHelper.square(d);
		Vec3d vec3d = camera.getCameraPosVec(tickDelta);
		
		Vec3d vec3d2 = camera.getRotationVec(tickDelta);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
		Box box = camera.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
		EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, vec3d, vec3d3, box, entity -> !entity.isSpectator() && entity.canHit(), e);
		return entityHitResult;
	}
}
