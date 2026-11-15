package cn.epicmc.foundry.common.unit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.List;

public class DependencyFinder {

    /**
     * 获取所有依赖指定插件的插件列表
     *
     * @param targetPluginName 目标插件的名称（必须与目标插件 plugin.yml 中的 name 完全一致，区分大小写）
     * @param includeSoft      是否包含软依赖 (softdepend)
     * @return 依赖该目标插件的插件实例列表
     */
    public static List<Plugin> getDependentPlugins(String targetPluginName, boolean includeSoft) {
        List<Plugin> dependents = new ArrayList<>();

        // 遍历当前服务器加载的所有插件
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            // 跳过目标插件自身
            if (plugin.getName().equalsIgnoreCase(targetPluginName)) {
                continue;
            }

            PluginDescriptionFile desc = plugin.getDescription();

            // 1. 检查硬依赖 (depend)
            if (desc.getDepend().contains(targetPluginName)) {
                dependents.add(plugin);
                continue;
            }

            // 2. 如果需要，检查软依赖 (softdepend)
            if (includeSoft && desc.getSoftDepend().contains(targetPluginName)) {
                dependents.add(plugin);
            }
        }

        return dependents;
    }
}

