# ❄️ Neyugame – Christmas Snowball Minigame

**Name:** Neyugame  
**Author:** vanhauluonsuy
**Version:** 1.0  
**Minecraft API:** 1.20  
**Plugin Type:** Christmas / Event Minigame  

---

## 🎄 English Description

**Neyugame** is a Christmas-themed Minecraft minigame plugin where players **collect and sell snowballs to earn money and compete on leaderboards**.

The plugin is designed specifically for **Christmas (Noel) events**, encouraging players to actively participate, grind snowballs, and race for the top rankings.

Perfect for:
- Seasonal events
- Christmas servers
- Economy-based competitions

---

## 🎄 Mô tả Tiếng Việt

**Neyugame** là plugin **minigame sự kiện Noel**, cho phép người chơi **thu thập và bán bóng tuyết để kiếm tiền và đua top**.

Plugin giúp:
- Tạo không khí Noel cho server
- Khuyến khích người chơi online, cày sự kiện
- Đua top nhận thưởng hấp dẫn

Phù hợp cho:
- Event Giáng Sinh
- Server sinh tồn / economy
- Mini-event ngắn hạn hoặc dài ngày

---

## ✨ Features | Tính năng

### ✅ English
- Sell snowballs for money
- Economy integration via Vault
- Leaderboard competition (top players)
- Simple command system
- Optimized for Christmas events

### ✅ Tiếng Việt
- Bán bóng tuyết lấy tiền
- Tích hợp kinh tế qua Vault
- Đua top người chơi
- Lệnh đơn giản, dễ dùng
- Tối ưu cho sự kiện Noel

---

## 📦 Requirements | Yêu cầu

| Plugin | Required |
|------|---------|
| Vault | ✅ |
| Economy Plugin (EssentialsX, CMI, etc.) | ✅ |
| PlaceholderAPI | ❌ (Optional) |

---

## ⚙️ plugin.yml

```yaml
name: Neyugame
version: 1.0
main: me.neyucity.project.neyugame.Neyugame
api-version: 1.20
depend: [Vault]
softdepend: [PlaceholderAPI]
commands:
  neyu:
    description: Lệnh chính của Neyugame
