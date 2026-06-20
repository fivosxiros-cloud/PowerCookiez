# PowerCookiez

Player and admin guide for the `PowerCookiez` plugin.

This plugin adds two parallel power systems:

- **Power Cookies**: consume a custom cookie to gain passives and gear abilities.
- **Power Rings**: activate a ring item and use ring abilities while holding it.

## Quick Start

1. Use `/cookiez` to open the cookie menu and pick a cookie.
2. Use `/mycookie` to check your current cookie, gear level, and toggle state.
3. Use `/rings` to browse rings.
4. Hold a ring and run `/pwring` to activate it.
5. Use `/myring` to view/toggle your active ring.

## Controls

Ability input mapping in current code:

| Input | Cookie | Ring |
|---|---|---|
| Shift + Right Click | Gear 1 | Ability A |
| Shift + Left Click | Gear 2 | Ability B |
| Triple Shift | Gear 3 | Ability D |
| Double Shift + Right Click | Gear 4 | Ability C |
| Double Shift + Left Click | Gear 5 | Ability C (current implementation) |
| Hold Shift ~3s | Gear 6 | N/A |

Notes:

- Ring actions are used when a valid ring is held and ring powers are enabled.
- Cookie actions are used when no ring action takes over.

## Commands

| Command | Description |
|---|---|
| `/cookiez` | Open all cookies menu |
| `/mycookie` | Show your cookie info and toggle cookie effects |
| `/setgear <player> <level>` | Set a player's gear level for their current cookie |
| `/pwring` | Activate the ring in your main hand |
| `/myring` | Show active ring and enable/disable controls |
| `/rings` | Open all registered rings |

## Cookies

### FrostyFrostCookie

- **Passives**: resistance/slowness profile, freeze immunity behavior, close-range frost aura, periodic larger frost pulse.
- **Weakness**: fire punishes the user.
- **Gear 1**: Ice Dash combo (3-hit chain).
- **Gear 2**: Ice Walls.
- **Gear 3**: Absolute Zero (large freeze AoE).

Cooldowns:

| Gear | Cooldown |
|---|---|
| 1 | 6s |
| 2 | 12s |
| 3 | 18s |

### SmokySmokeCookie

- **Passives**: speed, periodic smoke aura damage, reduced fire/freeze pressure.
- **Weakness**: extra underwater pressure/damage.
- **Gear 1**: Smoke Levitation + Darkness Burst.
- **Gear 2**: Smoke Cage with DoT.
- **Gear 3**: Smoke Teleport + invisibility + heal.
- **Gear 4**: Smoke Wall Blast.
- **Gear 5**: Smoke Hands (grab/drag/crush).
- **Gear 6**: Smoke Black Hole (pull + implosion).

Cooldowns:

| Gear | Cooldown |
|---|---|
| 1 | 6s |
| 2 | 12s |
| 3 | 18s |
| 4 | 15s |
| 5 | 20s |
| 6 | 45s |

### ZeroGravityCookie

- **Passives**: fall-control behavior, speed bonus, water damage drawback.
- **Gear 1**: Gravity Push.
- **Gear 2**: Gravy Graber (grab/hold/throw target).
- **Gear 3**: Zero-G Jump sequence.
- **Gear 4**: G-Laser Destroyer.
- **Gear 5**: Not implemented yet.
- **Gear 6**: Not implemented yet.

Cooldowns:

| Gear | Cooldown |
|---|---|
| 1 | 20s |
| 2 | 30s |
| 3 | 30s |
| 4 | 40s |
| 5 | 45s (placeholder) |
| 6 | 60s (placeholder) |

## Rings

Rings are grouped into two categories:

- `ELE` (Elemental)
- `ANI` (Animal)

### Elemental Rings

| Ring | Passives | A | B | C | D |
|---|---|---|---|---|---|
| AmberFlameRing | Fire resistance + flame pulse | Flame Burst | Fire Dash | Flame Chains | Inferno Field |
| PlanetaryEarthRing | Stone-skin style resistance/stability | Earth Slam | Rock Shield | Boulder Throw | Earthquake |
| StrikyWindsRing | Speed + fall reset | Air Dash | Tornado Lift | Wind Cutter | Cyclone Burst |
| SplashyWaterRing | Drown immunity + water/rain regen | Water Jet Dash | Bubble Prison | Healing Splash | Tsunami Wave |

### Animal Rings

| Ring | A | B | C | D |
|---|---|---|---|---|
| FoxyFoxRing | Fox form toggle | Coming soon | Coming soon | Coming soon |
| WolfyWolfRing | Wolf form toggle | Coming soon | Coming soon | Coming soon |
| ShadowyDragonRing | Dragon form toggle | Shadow Dash | Void Breath | Corruption Burst |

## Persistence and State

- The plugin stores player cookie and gear data in `playerdata.yml`.
- Cooldowns and some temporary runtime states are in-memory and not fully persisted.

## Practical Notes

- Cookie consumption has a short global cooldown.
- Gear level gates which abilities are available.
- Most ring effects require the ring item to remain in your main hand.
- Some systems are still in progress (especially selected ANI ring abilities and higher ZeroGravity gears).

