# Elytra Rocket Control
Server-side mod that cancels firework rocket use while gliding and/or underwater.

- No dependencies
- JSON config in `config/elytra-rocket-control.json`

## Config
```json
{
  "enable": true,
  "cancel_rockets_in_flight": true,
  "cancel_rockets_under_water": "flight",
  "message": "Firework rockets cannot be used right now."
}
```

| Key | Default | Meaning |
|-----|---------|---------|
| `enable` | `true` | Master switch |
| `cancel_rockets_in_flight` | `true` | Block rockets while fall-flying |
| `cancel_rockets_under_water` | `"flight"` | `"flight"` = in water only while gliding; `"always"` = any rocket use in water (even if `cancel_rockets_in_flight` is false) |
| `message` | (see above) | Action bar; empty = silent |

Ground use of fireworks (launch as projectile) is unchanged.

### Examples
- Gliding only: `cancel_rockets_in_flight: true`, `cancel_rockets_under_water: "flight"`
- Also ban fireworks in water: `cancel_rockets_under_water: "always"`
- Only ban water rockets, allow elytra rockets: `cancel_rockets_in_flight: false`, `cancel_rockets_under_water: "always"`

## To-do
- `boost_multiplier`:
  - experiments on the **26.3-debug** branch.
