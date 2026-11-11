package com.emoniph.witchery.client.renderer;

import com.emoniph.witchery.blocks.BlockApothecaryJar;
import com.emoniph.witchery.client.model.ModelApothecaryJar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderApothecaryJar extends TileEntitySpecialRenderer {

    private final ModelApothecaryJar model = new ModelApothecaryJar();
    private static final ResourceLocation TEXTURE_URL = new ResourceLocation("witchery", "textures/blocks/apothecary_jar.png");

    private final RenderItem3d renderItems = new RenderItem3d(true) {
        public byte getMiniItemCountForItemStack(ItemStack stack) {
            return (byte) 1;
        }

        public byte getMiniBlockCountForItemStack(ItemStack stack) {
            return (byte) 1;
        }

        @Override
        public boolean shouldBob() {
            return false;
        }

        @Override
        public boolean shouldSpreadItems() {
            return false;
        }
    };

    private EntityItem dummyEntityItem;

    public RenderApothecaryJar() {
        this.renderItems.setRenderManager(RenderManager.instance);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        BlockApothecaryJar.TileEntityApothecaryJar jar = (BlockApothecaryJar.TileEntityApothecaryJar) tileEntity;

        // 1. Render Opaque Parts of Model (Base, Neck, Lid)
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        this.bindTexture(TEXTURE_URL);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(0.0F, -1.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.model.renderBase(0.0625F);
        GL11.glPopMatrix();

        // 2. Render Stored Item Floating in 3D Inside Jar (rendered BEFORE glass so depth buffer and transparency work correctly)
        if (jar != null && jar.getStoredItem() != null && jar.getCount() > 0) {
            ItemStack renderStack = jar.getStoredItem().copy();
            renderStack.stackSize = 1;

            World world = jar.getWorldObj();
            if (world == null) {
                world = Minecraft.getMinecraft().theWorld;
            }

            if (world != null) {
                if (this.dummyEntityItem == null || this.dummyEntityItem.worldObj != world) {
                    this.dummyEntityItem = new EntityItem(world);
                }
                this.dummyEntityItem.hoverStart = 0.0F;
                this.dummyEntityItem.setEntityItemStack(renderStack);

                float rotation = (float) (Minecraft.getSystemTime() % 720000L) / 3000.0F * 300.0F;

                GL11.glPushMatrix();
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                GL11.glTranslatef((float) x + 0.5F, (float) y + 0.38F, (float) z + 0.5F);
                GL11.glScalef(0.45F, 0.45F, 0.45F);
                GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                if (jar.getWorldObj() != null) {
                    int light = jar.getWorldObj().getLightBrightnessForSkyBlocks(jar.xCoord, jar.yCoord, jar.zCoord, 0);
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(light % 65536), (float)(light / 65536));
                } else {
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200.0F, 200.0F);
                }

                this.renderItems.doRender(this.dummyEntityItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                GL11.glPopMatrix();
            }
        }

        // 3. Render Transparent Glass Shell (with depthMask false so it blends over the item and world without occlusion)
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        this.bindTexture(TEXTURE_URL);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(0.0F, -1.0F, 0.0F);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);

        this.model.renderGlass(0.0625F);

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();

        // 4. Render Count Label if player is looking at the jar within range
        if (jar != null && jar.getStoredItem() != null && jar.getCount() > 0) {
            Minecraft mc = Minecraft.getMinecraft();
            MovingObjectPosition mop = mc.objectMouseOver;
            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK 
                    && mop.blockX == jar.xCoord && mop.blockY == jar.yCoord && mop.blockZ == jar.zCoord) {
                if (mc.thePlayer != null) {
                    double distSq = mc.thePlayer.getDistanceSq(jar.xCoord + 0.5D, jar.yCoord + 0.5D, jar.zCoord + 0.5D);
                    if (distSq <= 36.0D) {
                        renderFloatingText(jar.getCount() + " " + jar.getStoredItem().getDisplayName(), (float) x + 0.5F, (float) y + 0.95F, (float) z + 0.5F);
                    }
                }
            }
        }
    }

    private void renderFloatingText(String text, float x, float y, float z) {
        FontRenderer fontRenderer = this.func_147498_b();
        if (fontRenderer == null) {
            fontRenderer = Minecraft.getMinecraft().fontRenderer;
        }
        RenderManager rm = RenderManager.instance;
        float scale = 0.014F;

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int textWidth = fontRenderer.getStringWidth(text);
        fontRenderer.drawStringWithShadow(text, -textWidth / 2, 0, 0xFFFFFF);

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
