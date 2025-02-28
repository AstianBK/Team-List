package com.tbk.teamlist.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tbk.teamlist.TeamList;
import com.tbk.teamlist.TeamListClient;
import com.tbk.teamlist.team.TLIconsRegistry;
import com.tbk.teamlist.team.Team;
import com.tbk.teamlist.team.TeamManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(EntityRenderer.class)
public class PlayerRendererMixin {
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void modifyNameTag(Entity abstractClientPlayerEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i1, float f1, CallbackInfo ci) {
        ci.cancel(); // Evita que el método original se ejecute
        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        String name = abstractClientPlayerEntity.getDisplayName().getString();
        text=Text.literal(name);
        double d = dispatcher.getSquaredDistanceToCamera(abstractClientPlayerEntity);
        if (!(d > 4096.0)) {
            Vec3d vec3d = abstractClientPlayerEntity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, abstractClientPlayerEntity.getYaw(f1));
            if (vec3d != null) {
                Team team = TeamManager.getTeam(name);
                boolean bl = !abstractClientPlayerEntity.isSneaky();
                int i = "deadmau5".equals(text.getString()) ? -10 : 0;
                int color = team!=null ? team.color : Colors.WHITE;
                matrixStack.push();
                matrixStack.translate(vec3d.x, vec3d.y + 0.5, vec3d.z);
                matrixStack.multiply(dispatcher.getRotation());
                matrixStack.scale(0.025F, -0.025F, 0.025F);
                Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
                float f = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
                int j = (int)(f * 255.0F) << 24;
                float g = (float) -textRenderer.getTextHandler().getWidth(text) / 2;
                textRenderer.draw(
                        text, g, (float)i, 553648127, false, matrix4f, vertexConsumerProvider, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, j, i1
                );
                if (bl) {
                    textRenderer.draw(text, g, (float)i, color, false, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, i1);
                }
                if (team!=null){
                    text=Text.literal(team.name);
                    g = (float) -textRenderer.getTextHandler().getWidth(text) / 2;
                    textRenderer.draw(
                            text, g, (float)i-15, 553648127, false, matrix4f, vertexConsumerProvider, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, j, i1
                    );
                    if (bl) {
                        textRenderer.draw(text, g, (float)i-15, color, false, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, i1);
                    }
                    matrixStack.scale(0.8F,0.8F,0.8F);

                    drawTexture(team.icon.getLocation(),matrix4f,matrixStack,g-16-textRenderer.getTextHandler().getWidth(text)*0.15F,-23,16,16,vertexConsumerProvider,i1);
                }

                matrixStack.pop();
            }
        }
    }

    private static void drawTexture(Identifier identifier,Matrix4f matrix, MatrixStack stack, float x, float y, int width, int height, VertexConsumerProvider consumer, int light) {
        VertexConsumer vertexConsumer = consumer.getBuffer(RenderLayer.getEntityCutout(identifier));

        float minU = 0.0F, maxU = 1.0F;
        float minV = 0.0F, maxV = 1.0F;

        vertexConsumer.vertex(matrix, x, y + height, 0).color(255, 255, 255, 255).texture(minU, maxV).overlay(OverlayTexture.DEFAULT_UV).light( 15728880).normal(stack.peek(), 0, 0, -1);
        vertexConsumer.vertex(matrix, x + width, y + height, 0).color(255, 255, 255, 255).texture(maxU, maxV).overlay(OverlayTexture.DEFAULT_UV).light( 15728880).normal(stack.peek(), 0, 0, -1);
        vertexConsumer.vertex(matrix, x + width, y, 0).color(255, 255, 255, 255).texture(maxU, minV).overlay(OverlayTexture.DEFAULT_UV).light( 15728880).normal(stack.peek(), 0, 0, -1);
        vertexConsumer.vertex(matrix, x, y, 0).color(255, 255, 255, 255).texture(minU, minV).overlay(OverlayTexture.DEFAULT_UV).light( 15728880).normal(stack.peek(), 0, 0, -1);
    }
}
