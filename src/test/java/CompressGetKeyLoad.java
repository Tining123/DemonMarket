import com.tining.demonmarket.common.util.PluginUtil;

import java.io.IOException;

public class CompressGetKeyLoad {
    public static void main(String[] args) throws IOException {
        System.out.println("test");

        String get = PluginUtil.compress("测试一下");

        System.out.println(get);
        get = "H4sIAAAAAAAA_wvw9_EM9nB1iXfx9A_yDHG1qtbJTLFSys3MS00uSkwrsSrIz8kszkhNiU_JzC_KLElVqgUAZOPEvjMAAAA=";
        System.out.println(PluginUtil.decompress(get));


    }
}
