# BrandHider v4.0

A lightweight **Velocity** plugin that hides and customizes your server brand:
- 🔍 **F3 Debug Screen**
- 🌐 **Server list / MOTD status websites**
- 🎞️ **Animated brand text**

---

## ✨ Features
- Hide server software name in **F3**
- Customize server version shown in the **MOTD**
- **Animated brand text** (color / frame based)
- Configurable via `config.yml`
- Console-only reload command
- Minecraft color code support (`&`)

---

## 📦 Installation
1. Download the latest `BrandHider.jar` from **Releases**
2. Place it in your Velocity `/plugins` folder
3. Restart the proxy
4. Edit `plugins/brandhider/config.yml`

---

## ⌨️ Commands
| Command                 | Description                        |
| ----------------------- | ---------------------------------- |
| `/brandhider`           | Plugin info                        |
| `/brandhider help`      | Command list                       |
| `/brandhider version`   | Plugin version                     |
| `/brandhider reload`    | Reload config (**console only**)   |

---

## ⚙️ Configuration

```yml
#  _    _ _     _
# | |  | (_)   | |
# | |__| |_  __| | ___
# |  __  | |/ _` |/ _ \
# | |  | | | (_| |  __/
# |_|  |_|_|\__,_|\___|
#
# BrandHider v4.0
# Made by BlowTobacco

# If enabled, the brand will be animated
# If false, only custom-brand will be used
animated-brand: true

# Static brand (used if animated-brand = false)
# Supported: All minecraft legacy colors, &k, &l, &m, &n, &o, &r
# Hex colors WILL NOT work due to Minecraft limitations
custom-brand: '&cBrandHider-4.0'

# This hides the server software in the MOTD / status websites
# Colors are NOT allowed here
motd-version: 'BrandHider-4.0'

# Update period for animation (milliseconds)
# Lower = smoother but more packets
animation-period: 1000

# Animated brand frames
# Uses MiniMessage-like formatting, BUT will be converted
# Hex colors WILL NOT work due to Minecraft limitations
brand-animation:
  - '&4B&crandHider'
  - '&fB&4r&candHider'
  - '&fBr&4a&cndHider'
  - '&fBra&4n&cdHider'
  - '&fBran&4d&cHider'
  - '&fBrand&4H&cider'
  - '&fBrandH&4i&cder'
  - '&fBrandHi&4d&cer'
  - '&fBrandHid&4e&cr'
  - '&fBrandHide&4r'
