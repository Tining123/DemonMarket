# DemonMarket - No More Mounting Inflation [中文Wiki](https://github.com/Tining123/DemonMarket/blob/main/README_cn.md)
![logo](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/logo.png)

#### A market plugin to prevent mounting inflation in minecraft servers. The optimized power index function and the inverse function are used to converge the selling price. It could solve the problem of server economic imbalance caused by the high production capacity of certain items in industrial type servers. In Summary, The richer the player is, the less profit the player receive.
By using a convergence function based on the player's assets, items sold by the player will gradually depreciate. Items depreciate rapidly after reaching the specified baseline, and gradually decrease towards 0 but never reach 0. Using this method, players can begin to adapt to price convergence at the beginning of the entry and effectively curb the further expansion of server oligarch players' assets.

#### To prevent player from trading money by others plugin, DemonMarket will disable every command except /mt pay.
#### You could turn this off in config.yml.

![GUI操作](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/gui_thumb2.gif)
![Shop操作](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/adminshop-min.gif)
![管理员Shop操作](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/shop-min.gif)

## Usage
#### Players could use /demonmarket or /mt
+ /mt gui - Open the acquire box
+ /mt list - Show the acquire list
+ /mt sell - Sell items in your hand
+ /mt sell all - Sell items in your hand and the same things in your inventory
+ /mt pay - Pay someone money
+ /mt price - Check the price
+ /mt help - Check helps
+ /mt shop - Buy stuff from shop

Also ：dm, dmt, demonmarket

#### Admins could use /demonmarketadmin or /mtadmin
+ /mtadmin set [price] - Set price for the item
+ /mtadmin nbtset [price] - Set price for the nbt item
+ /mtadmin name - Check name info
+ /mtadmin nbt - Check nbt info
+ /mtadmin reload - Reload plugin
+ /mtadmin shopset [price] - Set price for the item in the shop
+ /mtadmin shopnbtset [price] - Set price for the nbt item in the shop
+ /mtadmin shop - Shop management panel

Also ：dmadmin, dmtadmin, demonmarketadmin

## Example
Set diamond block with $440.

![800资产效果](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/800_en.png)
- You could receive $432 if you have only $800

![60w资产效果](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/60w_en.png)
- You could receive $217 if you have $600000

![5000w资产效果](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/5000w_en.png)
- You could receive $0.69 and even sold 64 diamond blocks if you have $50000000


## Permission
+ demonmarket.use - Player need this permission node to use demonmarket
+ If you are using Groupmanager, try /mangaddp builder demonmarket.use or /mangaddp default demonmarket.use
+ If you are using luckperm, try /lp editor
## config.yml
+ lang: Set language manually.
+ TaxRate: Tax rate.
+ OP: The tax beneficiary. If you don't want to use it, just leave it blank.
+ BasicProperty: The average balance of single player in mathematical expectation.
+ Round: Enable and make the numbers approximate(shorter and easier to read)
+ enable-demon-tax: Enable progressive tax rate, the most basic function of this plugin
+ Fitler: For SlimeFun Plugin. Enable this option to block any items with lore being sold. Attention, this might cause the NBT support disable
+ disable-pay: Enable this option to prevent user from paying others by using ess or others plugin.
+ disable-pay-list: Set the paying command you want to block.
+ disable-sell: Enable this option to prevent user from selling others by using ess or others plugin.
+ disable-sell-list: Set the selling command you want to block.
+ may-pay: Max amount of money can be transfer in a transaction.Set -1 to disable limitation, set 0 to disable pay command.
+ pay-unit: Transfer unit. All transfer amount will be split as pay-unit to transfer. The tax will be calculated times.
+ payer-tax: whether the payer pays the transfer tax (the tax payed by the receiver in default)
+ auto-refresh: Enable to auto refresh the price in gui
+ auto-refresh-gap: Refresh interval (second per time)
+ disable-shop: Disable mt shop command.
## Mathematical Theory
+ TAX=（1 - TaxRate）

![实际最终公式](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/theory.png)


### Set the price of diamond block is $440
+ When the BasicProperty was set to 5000, the profit goes with players deposit like follow

![5000基线](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/5kbasic.png)
+ When the BasicProperty was set to 500000, the profit goes with players deposit like follow

![50w基线](https://raw.githubusercontent.com/Tining123/DemonMarket/master/src/main/img/50wbasic.png)

### You can also draw some charts like these by using my another tool, the [DemonCalculator](https://github.com/Tining123/DemonCalculator), so that you could decide the basic property setting.

## Developing
+ Acquire list [✓]
+ GUI support [✓]
+ NBT Support [✓]
+ Use command to set price [✓]
+ Check item name with command [✓]
## About
+ MIT lisence
+ If you have any suggestion, complain or recommend function, don't be hesitated and contact me via GitHub or spigot.
## Contact
- Github: https://github.com/Tining123/DemonMarket
- Email: tingave201@outlook.com
