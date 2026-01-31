# BrandHider v3.0

A lightweight **Velocity** plugin that hides and customizes your server brand:
- 🔍 **F3 Debug Screen**
- 🌐 **Server list / MOTD status websites**

---

## ✨ Features
- Hide server software name in **F3**
- Customize server version shown in the **MOTD**
- Configurable via `config.yml`
- Console-only reload command
- Hex color support (where Minecraft allows it)

---

## 📦 Installation
1. Download the latest `BrandHider.jar`
2. Place it in your Velocity `/plugins` folder
3. Restart the proxy
4. Edit `plugins/brandhider/config.yml`

---

## ⚙️ Configuration
```#
#  _    _ _     _
# | |  | (_)   | |
# | |__| |_  __| | ___
# |  __  | |/ _` |/ _ \
# | |  | | | (_| |  __/
# |_|  |_|_|\__,_|\___|
#
# BrandHider v3.0 Made by BlowTobacco
#

# This hides the server software in game (F3)
# Colors ARE allowed here (HEX CODES OR ANY OTHER COLOR THAT ISN'T THE MINECRAFT DEFAULT https://htmlcolorcodes.com/minecraft-color-codes/ WILL NOT WORK)
custom-brand: 'BrandHider-3.0'

# This hides the server software in the MOTD / status websites
# Colors are NOT allowed here (Minecraft clients will ignore them)
motd-version: 'BrandHider-3.0'

