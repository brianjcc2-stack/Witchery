package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.blocks.BlockCircle;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RiteVociferador extends Rite {
    private final int radius;

    public RiteVociferador(int radius) {
        this.radius = radius;
    }

    @Override
    public void addSteps(ArrayList steps, int initialStage) {
        steps.add(new StepVociferador(this, initialStage));
    }

    private static class StepVociferador extends RitualStep {
        private final RiteVociferador rite;

        public StepVociferador(RiteVociferador rite, int initialStage) {
            super(false);
            this.rite = rite;
        }

        @Override
        public RitualStep.Result process(World world, int posX, int posY, int posZ, long ticks, BlockCircle.TileEntityCircle.ActivatedRitual ritual) {
            if (ticks % 20L != 0L) {
                return RitualStep.Result.STARTING;
            } else {
                if (!world.isRemote) {
                    String message = "AHHHH!";
                    if (ritual.sacrificedItems != null) {
                        for (Object obj : ritual.sacrificedItems) {
                            RitualStep.SacrificedItem item = (RitualStep.SacrificedItem) obj;
                            if (item != null && item.itemstack != null && item.itemstack.getItem() == Items.paper) {
                                if (item.itemstack.hasDisplayName()) {
                                    message = item.itemstack.getDisplayName();
                                }
                            }
                        }
                    }

                    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox((double)(posX - this.rite.radius), (double)posY - 5, (double)(posZ - this.rite.radius), (double)(posX + this.rite.radius + 1), (double)(posY + 5), (double)(posZ + this.rite.radius + 1));
                    List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, bounds);
                    
                    String senderName = ritual.getInitiatingPlayerName();
                    
                    for (EntityPlayer player : players) {
                        ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "[Vociferador de " + senderName + "]: " + message);
                        player.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
                        SoundEffect.MOB_ENDERDRAGON_GROWL.playAtPlayer(world, player);
                        ParticleEffect.EXPLODE.send(SoundEffect.NONE, player, 1.0, 2.0, 16);
                    }
                }
                return RitualStep.Result.COMPLETED;
            }
        }
    }
}
