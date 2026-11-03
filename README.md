# Elytra Rocket Control
Server-side mod that controls firework rocket boost while gliding with elytra.

- No dependencies
- JSON config in `config/elytra-rocket-control.json`

## Config
```json
{
  "enabled": true,
  "boost_multiplier": 0.0,
  "cancel_use_if_no_boost": true,
  "message": "Firework boost is disabled while gliding."
}
```

| Key | Default | Meaning |
|-----|---------|---------|
| `enabled` | `true` | Master switch |
| `boost_multiplier` | `0.0` | `0` = no boost, `1` = vanilla, `0.5` = half |
| `cancel_use_if_no_boost` | `true` | If multiplier ≤ 0, block using rockets while gliding (no consume) |
| `message` | (see above) | Sent when use is cancelled; empty = silent |

Ground use of fireworks (launch as projectile) is unchanged.

## Known Issues:
- `boost_multiplier` is not applied [correctly].
