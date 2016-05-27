package patrick96.friendlyfier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {

    public static String getOriginalName(Entity e) {
        String s = EntityList.getEntityString(e);

        if (s == null)
        {
            s = "generic";
        }

        return I18n.translateToLocal("entity." + s + ".name");
    }

    public static void log(Level l, String msg) {
        FMLLog.log(Friendlyfier.MODID, l, msg);
    }

    public static boolean friendlyfy(EntityCreature entity) {
        return friendlyfy(entity, false);
    }

    public static boolean friendlyfy(EntityCreature entity, boolean onSpawn) {
        if(entity.worldObj.isRemote || !entity.isNonBoss() || onSpawn != entity.getEntityData().getBoolean("friendlyfied")) {
            return false;
        }

        entity.getEntityData().setBoolean("friendlyfied", true);

        List<EntityAIBase> tasks = new ArrayList<>();
        List<EntityAIBase> targetTasks = new ArrayList<>();

        for(EntityAITasks.EntityAITaskEntry t : entity.tasks.taskEntries) {
            tasks.add(t.action);
        }

        for(EntityAITasks.EntityAITaskEntry t : entity.targetTasks.taskEntries) {
            targetTasks.add(t.action);
        }

        for(EntityAIBase b : tasks) {
            entity.tasks.removeTask(b);
        }

        for(EntityAIBase b : targetTasks) {
            entity.targetTasks.removeTask(b);
        }

        if(entity instanceof EntityCreeper) {
            // TODO config for this
            EntityCreeper creeper = (EntityCreeper) entity;
            try {
                creeper.setCreeperState(-1);
                creeper.getDataManager().set((DataParameter) ReflectionHelper.findField(EntityCreeper.class, "IGNITED", "field_184715_c").get(creeper), false);
            } catch(IllegalAccessException e) {
                Utils.log(Level.ERROR, "Failed to defuse creeper. It's gonna blow!!!");
                e.printStackTrace();
            }
        }

        entity.tasks.addTask(0, new EntityAISwimming(entity));
        entity.tasks.addTask(1, new EntityAIWander(entity, 1.0D));
        entity.tasks.addTask(2, new EntityAILookIdle(entity));

        entity.enablePersistence();

        return true;
    }

}
