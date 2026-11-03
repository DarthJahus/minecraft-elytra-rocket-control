package net.jahus.elytrarocket.debug;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public final class SpeedMonitor {

    public static final boolean ENABLED = true;

    private static final int WINDOW = 200; // 10 s
    private static final int BOOST_DISPLAY_TICKS = 40;
    private static final Map<UUID, State> STATES = new HashMap<>();

    private static final class State {
        final double[] samples = new double[WINDOW];
        int head;
        int count;
        Vec3 last;
        int ticksSinceLast;
        String boost = "";
        int boostAge = Integer.MAX_VALUE;

        void push(double v) {
            samples[head] = v;
            head = (head + 1) % WINDOW;
            count = Math.min(count + 1, WINDOW);
        }

        String format(double y) {
            double cur = samples[(head - 1 + WINDOW) % WINDOW];
            double min = Double.MAX_VALUE;
            double max = 0.0D;
            for (int i = 0; i < count; i++) {
                min = Math.min(min, samples[i]);
                max = Math.max(max, samples[i]);
            }
            String s = String.format(Locale.ROOT,
                    "v=%.3f | min=%.3f | max=%.3f (%ds) | y=%.1f",
                    cur, min, max, count / 20, y);
            return boostAge < BOOST_DISPLAY_TICKS ? s + " | " + boost : s;
        }
    }

    private SpeedMonitor() {}

    /** Called once per tick per player (cf. ServerPlayerMixin). */
    public static void tick(ServerPlayer player) {
        if (!ENABLED) {
            return;
        }

        UUID id = player.getUUID();
        if (!player.isFallFlying()) {
            STATES.remove(id);  // reset window when flight ends
            return;
        }

        State s = STATES.computeIfAbsent(id, k -> new State());
        if (s.boostAge < Integer.MAX_VALUE) {
            s.boostAge++;
        }

        Vec3 pos = player.position();
        s.ticksSinceLast++;

        if (s.last == null) {
            s.last = pos;
            s.ticksSinceLast = 0;
            return;
        }

        double dist = pos.distanceTo(s.last);
        if (dist < 1.0E-6D) {
            return;  // no new position received during the tick
        }

        s.push(dist / s.ticksSinceLast);
        s.ticksSinceLast = 0;
        s.last = pos;

        player.sendOverlayMessage(Component.literal(s.format(pos.y)));
    }

    public static void recordBoost(ServerPlayer player, double base, double pre, double scale, double theoretical) {
        if (!ENABLED) {
            return;
        }
        State s = STATES.computeIfAbsent(player.getUUID(), k -> new State());
        s.boost = String.format(Locale.ROOT,
                "rocket: dm=%.3f pre=%.3f x%.2f th=%.3f",
                base, pre, scale, theoretical
        );
        s.boostAge = 0;
    }
}
