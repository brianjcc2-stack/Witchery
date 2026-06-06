package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.blocks.BlockCircle;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.Coord;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TimeUtil;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class RiteUnbreakableVow extends Rite {
    @Override
    public void addSteps(ArrayList steps, int initialStage) {
        steps.add(new StepRiteUnbreakableVow(this, initialStage));
    }

    private static class StepRiteUnbreakableVow extends RitualStep {
        private final RiteUnbreakableVow rite;

        public StepRiteUnbreakableVow(RiteUnbreakableVow rite, int initialStage) {
            super(false);
            this.rite = rite;
        }

        @Override
        public RitualStep.Result process(World world, int x, int y, int z, long ticks, BlockCircle.TileEntityCircle.ActivatedRitual circleType) {
            if (ticks % 20L != 0L) {
                return RitualStep.Result.STARTING;
            }
            if (!world.isRemote) {
                int dur = TimeUtil.minsToTicks(10);
                boolean blessed = false;
                AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox((double)(x - 6), (double)(y - 2), (double)(z - 6), (double)(x + 6), (double)(y + 4), (double)(z + 6));
                List players = world.getEntitiesWithinAABB(EntityPlayer.class, bounds);
                Iterator i$ = players.iterator();
                while (i$.hasNext()) {
                    EntityPlayer player = (EntityPlayer)i$.next();
                    if (Coord.distance(player.posX, player.posY, player.posZ, (double)x + 0.5D, (double)y, (double)z + 0.5D) <= 6.0D) {
                        player.addPotionEffect(new PotionEffect(Potion.resistance.id, dur, 1));
                        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, dur, 0));
                        player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, dur, 0));
                        player.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, dur, 0));
                        ChatUtil.sendTranslated(EnumChatFormatting.GOLD, player, "witchery.rite.unbreakablevow.blessed", new Object[0]);
                        blessed = true;
                    }
                }

                if (!blessed) {
                    return RitualStep.Result.ABORTED_REFUND;
                }
                ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_LEVELUP, world, 0.5D + (double)x, (double)y + 1.0D, 0.5D + (double)z, 2.0D, 2.0D, 24);
            }
            return RitualStep.Result.COMPLETED;
        }
    }
}
