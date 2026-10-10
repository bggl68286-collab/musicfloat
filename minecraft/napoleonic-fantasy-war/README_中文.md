# 拿破仑奇幻战争（Forge 1.20.1）

统一模组包含四套互相配合的装备：

- **普鲁士骷髅骠骑兵军服**：军帽、合身军上衣、马裤、长靴四件套。服装细节绑定到头、躯干、手臂和腿部骨骼，行走、跳跃和挥剑时跟随人物动作。
- **帝国鹰首军剑**：普通挥斩、蓄力剑气和范围冲击，带金色剑气、法阵粒子和专用持剑动作。
- **格里博瓦尔手持大炮**：海绵清膛、装药、装弹、推弹、瞄准、点火和复位七段动作；实心炮弹与霰弹罐；炮口火焰、烟雾、火星、扬尘和炮声。
- **法国沙勒维尔1777型燧发枪**：单兵纸包弹枪口装填、通条、燧发机构、单发射击、后坐白烟与套筒刺刀安装/突刺。

服装 Blockbench 源文件在 `art/source/prussian_skull_hussar_uniform.bbmodel`，材质原图在 `art/source/hussar_materials.png`，并提供经典与 Slim 两套 Blockbench 几何源。军服材质使用生成的黑色羊毛、红色领口、银色绳饰、白灰毛领、灰色马裤和黑色皮革区域；四个装备槽使用独立模型层，服装会随人物动作贴合。

## 安装

把 `napoleonic_fantasy-1.0.0.jar` 放进 Minecraft 1.20.1 Forge 47.3.22 的 `mods` 文件夹。模组只需要 Forge，不依赖其他模组。

## 开发构建

```text
Java 17
Gradle 8.8
gradle build
gradle runGameTestServer
gradle runClient
```

服务器 GameTest 已通过 13 项：炮、弹道、装填、粒子网络、军剑动作与伤害、四件军服注册和四槽位穿戴。

## 物品 ID

```text
napoleonic_fantasy:prussian_skull_hussar_shako
napoleonic_fantasy:prussian_skull_hussar_jacket
napoleonic_fantasy:prussian_skull_hussar_breeches
napoleonic_fantasy:prussian_skull_hussar_boots
napoleonic_fantasy:eagle_sabre
napoleonic_fantasy:hand_cannon
napoleonic_fantasy:round_shot
napoleonic_fantasy:canister_shot
napoleonic_fantasy:charleville_1777
```

燧发枪操作：空枪按住右键完成约 5 秒装填，松开右键击发；射击后必须重新装填。Shift + 右键安装或卸下套筒刺刀，安装后右键为近战突刺。枪声、纸包弹、通条、燧发机构和刺刀音效均已接入。
