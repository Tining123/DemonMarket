# DemonMarket 魔鬼商店
![logo](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/logo.png)

#### 一个系统商店插件，玩家可以出售物品。采用调优后的幂指函数与反函数混合收敛物价。用于解决工业类服务器某些物品产能过高导致服务器经济失衡的问题。
通过基于玩家资产的收敛函数，玩家出售物品会逐渐缓步的贬值。在达到指定基线后物品迅速贬值，并且逐步趋减趋近于0但不会到达0。使用这种手段，在玩家初期进入就能开始适应物价收敛，并且可以有效遏制服务器寡头玩家的资产进一步膨胀。

![GUI操作](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/gui_thumb2.gif)

## 使用方法
#### 使用用户命令/demonmarket 命令简写为/mt
+ /mt gui 打开一个收购箱
+ /mt sell 卖掉手里的东西
+ /mt sell all 卖掉背包里和手中同样的所有东西
+ /mt price 预估手中物品的出售收益
+ /mt help 查看帮助

可用别名：dm,dmt,demonmarket

#### 使用管理员命令/demonmarketadmin 命令简写为/mtadmin
+ /mtadmin set [价格] 为手中的物品新增或修改价格
+ /mtadmin name 查看手中物品在配置中的名称
+ /mtadmin nbt 查看手中物品在配置中的nbt明细
+ /mtadmin reload 重载插件配置

可用别名：dmadmin,dmtadmin,demonmarketadmin

## 使用效果
![5000w资产效果](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/5000w.jpg)
![60w资产效果](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/60w.jpg)
![800资产效果](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/800.jpg)


以上分别为资产5000万，60w以及800的玩家出售钻石块后所得收益
## 插件权限列表
+ demonmarket.use 需要给玩家组这个权限才能使用
+ 例如使用Groupmanager命令 /mangaddp builder demonmarket.use 或者 /mangaddp default demonmarket.use
+ luckperm则可以直接通过 /lp editor界面添加
## 配置文件 config.yml
+ TaxRate: 税率，是经过收敛公式之后，在进行一次税率收取。默认千分之5，
+ OP: 服主，或者指定税率受益人。所有税收会打入这个玩家的账户。不想使用可以留空。
+ BasicProperty: 资产基线，最重要的配置。指定期望平均单个玩家持有的储蓄金额。可以根据服务器自身情况增加或者降低。当前默认50万。
+ Round: 是否开启小数点后两位近似功能
+ Fitler: 此功能针对粘液科技等其他插件，这些插件可能发放带有功能性的原版物品，启用该功能将禁止销售手中的这些特殊物品。
+ worth: 物价表，使用物品名称。如果想要加入新的物品出售，或者修改，可以使用/mtadmin set命令，查看价格则可以使用/mt price命令
+ nbtworth: 带有nbt的物品物价表，同样使用/mtadmin set命令和/mt price命令进行修改和计算
## 理论支持
以下为未调优之前的幂函数大致收敛情况，以及计算价格的实际公式。 其中
+ price=物品设定价格
+ money=玩家资产
+ BASE=资产基线
+ TAX=（1 - 税收）

![未调优曲线图](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/chart.png)
![实际最终公式](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/math.png)

## 未来功能
+ 添加系统商店，玩家可进行购买[-]
+ 试用GUI查看所有可出售物品[-]
+ 支持GUI界面[✓]
+ 支持NBT [✓]
+ 通过命令直接调整物价 [✓]
+ 通过命令直接查看物品名称 [✓]
## 关于项目
+ 采用MIT协议，欢迎提建议，issue，fork或者直接下载使用
+ 如果有问题或者需求可以联系QQ 1340212468
+ 该插件已由纸劈刀搬运至mcbbs
## 感谢
代码指导 EnderTheCoder

技术指导 LiteSignIn 插件
