## 🏆 DexRewards 🏆
DexRewards is a server-side mod for Cobblemon 1.8.0 (Minecraft 1.21.1) that rewards players for completing their Pokédex!

Check out the [Wiki](https://github.com/Unariginal/Cobblemon-DexRewards/wiki) for the full documentation.

### 🏆 Features 🏆
- Reward tiers that unlock at a percentage of Pokédex completion
- Multiple dex types, each with its own Pokédex, filters and rewards
- Caught, seen, shiny-caught and shiny-seen dexes
- All-forms dexes that count every form separately (Alolan Vulpix, Rotom appliances and more)
- Filter species by name or label (generation, legendary, starter and more)
- Item, command and Pokémon rewards, with full item component support
- Configurable chest or hopper rewards GUI with paging
- Progress updates automatically when players see, scan or catch Pokémon
- Placeholder API support

### 🏆 Counting Modes 🏆
| `require_caught` | `require_shiny` | A Pokémon counts when the player has... |
|---|---|---|
| `true` | `false` | Caught it |
| `false` | `false` | Seen it |
| `true` | `true` | Obtained a shiny one |
| `false` | `true` | Seen a shiny one |

Add `require_all_forms` to any mode to count every form instead of every species.

### 🏆 Commands 🏆
Base Command:
- /dex (alias: /dexrewards)

Sub Commands:
- /dex rewards [<dex-type>]
- /dex reload
- /dex update <player>
- /dex reset <player> <dex-type>

### 🏆 Permissions 🏆
| Permission | Default |
|---|---|
| `dexrewards.rewards` | Everyone |
| `dexrewards.reload` | OP |
| `dexrewards.update` | OP |
| `dexrewards.reset` | OP |

### 🏆 Placeholders 🏆
Placeholders work in DexRewards' messages and GUI, and in other mods through [Placeholder API](https://modrinth.com/mod/placeholder-api) under the `dexrewards:` namespace. Dex type placeholders take an optional dex type, e.g. `%dexrewards:player_dex_count kanto%`.

- `prefix`, `player`
- `dex_type`, `pokedex_total`, `dex_total_groups`
- `player_dex_count`, `player_dex_percent`, `player_reward_group`
- `group_name`, `group_percent` *(messages and GUI only)*

### 🏆 Configs 🏆
All files are in `config/DexRewards/`.

config.json
- General settings: the default dex type, and whether unimplemented or invalid species count

messages.json
- All chat messages

reward_gui.json
- The rewards GUI's layout, items and text

dex_types/<dex-type>.json
- One file per dex type: its Pokédex, filters, counting mode and reward groups. A National Dex is included by default

players/<player-uuid>.json
- Each player's progress, claimed rewards and shiny record

[Join The Discord!](https://discord.com/invite/2tWgRJQPyB)
