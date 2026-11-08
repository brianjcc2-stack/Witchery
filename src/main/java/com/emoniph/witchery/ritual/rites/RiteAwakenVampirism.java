package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockCircle;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.entity.EntityWitchHunter;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.Config;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;

public class RiteAwakenVampirism extends Rite {

    @Override
    public void addSteps(ArrayList steps, int initialStage) {
        steps.add(new StepAwakenVampirism(this));
    }

    private static class StepAwakenVampirism extends RitualStep {
        private final RiteAwakenVampirism rite;

        public StepAwakenVampirism(RiteAwakenVampirism rite) {
            super(false);
            this.rite = rite;
        }

        @Override
        public RitualStep.Result process(World world, int posX, int posY, int posZ, long ticks, BlockCircle.TileEntityCircle.ActivatedRitual ritual) {
            if (ticks % 20L != 0L) {
                return RitualStep.Result.STARTING;
            }
            if (!world.isRemote) {
                EntityPlayer initiator = ritual.getInitiatingPlayer(world);
                EntityLivingBase target = null;

                Iterator i$ = ritual.sacrificedItems.iterator();
                while (i$.hasNext()) {
                    RitualStep.SacrificedItem item = (RitualStep.SacrificedItem) i$.next();
                    if (item.itemstack.getItem() == Witchery.Items.TAGLOCK_KIT && item.itemstack.getItemDamage() == 1) {
                        target = Witchery.Items.TAGLOCK_KIT.getBoundEntity(world, (Entity) null, item.itemstack, Integer.valueOf(1));
                        break;
                    }
                }

                if (target == null) {
                    target = initiator;
                }

                if (target == null || !(target instanceof EntityPlayer)) {
                    if (initiator != null) {
                        ChatUtil.sendTranslated(EnumChatFormatting.RED, initiator, "witchery.rite.wolfcurse.nothuman", new Object[0]);
                    }
                    return RitualStep.Result.ABORTED_REFUND;
                }

                EntityPlayer playerTarget = (EntityPlayer) target;
                ExtendedPlayer playerEx = ExtendedPlayer.get(playerTarget);
                if (playerEx == null) {
                    return RitualStep.Result.ABORTED_REFUND;
                }

                if (!Config.instance().allowVampireWolfHybrids && playerEx.getWerewolfLevel() > 0) {
                    if (initiator != null) {
                        ChatUtil.sendTranslated(EnumChatFormatting.RED, initiator, "witchery.rite.wolfcurse.hybridsnotallow", new Object[0]);
                    }
                    return RitualStep.Result.ABORTED_REFUND;
                }

                if (playerEx.getVampireLevel() > 0) {
                    if (initiator != null) {
                        ChatUtil.sendTranslated(EnumChatFormatting.RED, initiator, "witchery.rite.wolfcurse.alreadyactive", new Object[0]);
                    }
                    return RitualStep.Result.ABORTED_REFUND;
                }

                // Awaken Vampirism!
                playerEx.setVampireLevel(1);
                playerEx.setBloodPower(playerEx.getMaxBloodPower());
                if (initiator != null) {
                    EntityWitchHunter.blackMagicPerformed(initiator);
                }

                ChatUtil.sendTranslated(EnumChatFormatting.DARK_RED, playerTarget, "witchery.vampire.awaken", new Object[0]);
                SoundEffect.WITCHERY_MOB_LILITH_TALK.playAtPlayer(world, playerTarget);
                SoundEffect.WITCHERY_RANDOM_DRINK.playAtPlayer(world, playerTarget);
                ParticleEffect.REDDUST.send(SoundEffect.NONE, world, playerTarget.posX, playerTarget.posY + 1.0D, playerTarget.posZ, 1.0D, 1.5D, 30);
                ParticleEffect.PORTAL.send(SoundEffect.NONE, world, 0.5D + (double) posX, 0.1D + (double) posY, 0.5D + (double) posZ, 1.5D, 2.0D, 24);
            }
            return RitualStep.Result.COMPLETED;
        }
    }
}
