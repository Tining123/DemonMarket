import com.tining.demonmarket.common.ref.Updater;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.storage.ConfigFileNameEnum;
import com.tining.demonmarket.storage.ConfigReader;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CompressGetKeyLoadTest {
    public static void main(String[] args) throws IOException {
        System.out.println("test");

        String get = PluginUtil.compress("测试一下");

        System.out.println(get);
        get = "H4sIAAAAAAAA_wvw9_EM9nB1iXfx9A_yDHG1qtbJTLFSys3MS00uSkwrsSrIz8kszkhNiU_JzC_KLElVqgUAZOPEvjMAAAA=";
        System.out.println(PluginUtil.decompress(get));

        Updater.initialize();

        FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File("config.yml"));
        List<String> list = configuration.getStringList("disable-pay-list");
    }
}
