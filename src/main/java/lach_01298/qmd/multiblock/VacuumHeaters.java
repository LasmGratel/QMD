package lach_01298.qmd.multiblock;

import lach_01298.qmd.block.entity.chamber.AbstractChamberBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import java.util.*;

/** Reloadable placement expressions with exact counts, axial pairs and rooted heater dependencies. */
public final class VacuumHeaters {

    public static final String[] RULES = { "one casing", "one beam", "two glass",
        "exactly one quartz heater && exactly one redstone heater", "two axial obsidian heaters",
        "exactly one redstone heater && two iron heaters", "one obsidian heater && one quartz heater", "one nozzle" };

    public static boolean valid(String expression) {
        for (String or : expression.split(" \\|\\| "))
            for (String condition : or.split(" && ")) if (parse(condition) == null) return false;
        return !expression.isBlank();
    }

    private record Rule(int amount, boolean exact, boolean axial, String target) {}

    private static Rule parse(String text) {
        String[] t = text.trim()
            .split(" +");
        int i = 0;
        boolean exact = t.length > 0 && t[0].equals("exactly");
        if (exact) i++;
        if (i >= t.length) return null;
        int amount = switch (t[i++]) {
            case "one" -> 1;
            case "two" -> 2;
            case "three" -> 3;
            case "four" -> 4;
            case "five" -> 5;
            case "six" -> 6;
            default -> -1;
        };
        boolean axial = i < t.length && t[i].equals("axial");
        if (axial) i++;
        if (amount < 0 || i >= t.length) return null;
        String target = t[i++];
        if (Set.of("casing", "wall", "beam", "glass", "nozzle")
            .contains(target)) return i == t.length ? new Rule(amount, exact, axial, target) : null;
        if (!Set.of("iron", "redstone", "quartz", "obsidian", "glowstone", "lapis", "gold", "diamond")
            .contains(target) || i != t.length - 1
            || !Set.of("heater", "heaters")
                .contains(t[i]))
            return null;
        return new Rule(amount, exact, axial, target);
    }

    public static boolean test(String expression, AbstractChamberBlockEntity part, Set<BlockPos> active) {
        for (String or : expression.split(" \\|\\| ")) {
            boolean satisfied = true;
            for (String condition : or.split(" && ")) {
                Rule rule = parse(condition);
                if (rule == null) {
                    satisfied = false;
                    break;
                }
                int count = 0;
                EnumSet<Direction> directions = EnumSet.noneOf(Direction.class);
                for (Direction direction : Direction.values()) {
                    BlockPos p = part.getBlockPos()
                        .relative(direction);
                    if (!(part.getLevel()
                        .getBlockEntity(p) instanceof AbstractChamberBlockEntity neighbor)) continue;
                    var role = neighbor.role();
                    boolean match = switch (rule.target) {
                        case "casing", "wall" -> role.vacuum() && !role.interior;
                        case "beam" -> role == ChamberPart.VACUUM_BEAM;
                        case "glass" -> role == ChamberPart.PLASMA_GLASS;
                        case "nozzle" -> role == ChamberPart.PLASMA_NOZZLE;
                        default -> role.heater() && role.id.equals("vacuum_chamber_heater_" + rule.target)
                            && active.contains(p);
                    };
                    if (match) {
                        count++;
                        directions.add(direction);
                    }
                }
                if (rule.axial) {
                    count = 0;
                    for (Direction.Axis axis : Direction.Axis.values()) {
                        Direction d = Direction.get(Direction.AxisDirection.POSITIVE, axis);
                        if (directions.contains(d) && directions.contains(d.getOpposite())) count += 2;
                    }
                }
                if (rule.exact ? count != rule.amount : count < rule.amount) {
                    satisfied = false;
                    break;
                }
            }
            if (satisfied) return true;
        }
        return false;
    }

    private VacuumHeaters() {}
}
