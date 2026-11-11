package lach_01298.qmd.multiblock.accelerator;

import lach_01298.qmd.block.entity.accelerator.AbstractAcceleratorBlockEntity;
import lach_01298.qmd.config.AcceleratorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import java.util.*;

/** Monotone fixed-point placement rules: a cooler cycle cannot enable itself without an active root. */
public final class AcceleratorCoolers {

    public static final String[] NAMES = { "water", "iron", "redstone", "quartz", "obsidian", "nether_brick",
        "glowstone", "lapis", "gold", "prismarine", "slime", "end_stone", "purpur", "diamond", "emerald", "copper",
        "tin", "lead", "boron", "lithium", "magnesium", "manganese", "aluminum", "silver", "fluorite", "villiaumite",
        "carobbiite", "arsenic", "liquid_nitrogen", "liquid_helium", "enderium", "cryotheum" };
    public static final int[] COOLING = { 60, 55, 115, 75, 70, 90, 110, 130, 95, 85, 165, 50, 100, 185, 135, 80, 120,
        65, 105, 125, 150, 180, 175, 160, 155, 170, 140, 145, 195, 200, 190, 205 };
    public static final String[] RULES = { "one cavity", "one magnet", "one cavity && one magnet",
        "one redstone cooler", "two glowstone coolers", "one obsidian cooler", "two different magnets",
        "one yoke && one magnet", "two iron coolers", "two water coolers", "two lead coolers && one water cooler",
        "one yoke", "two end_stone coolers", "one gold cooler && one prismarine cooler",
        "one cavity && one prismarine cooler", "one water cooler", "two lapis coolers", "one iron cooler",
        "one yoke && one cavity", "one boron cooler", "one end_stone cooler && one prismarine cooler",
        "one gold cooler && one quartz cooler", "one tin cooler && one quartz cooler", "two arsenic coolers",
        "three gold coolers", "one purpur cooler && one prismarine cooler", "one end_stone cooler && one gold cooler",
        "two different cavity", "one lapis cooler && one gold cooler", "one boron cooler && one lapis cooler",
        "three purpur coolers", "three tin coolers" };

    private AcceleratorCoolers() {}

    private static String normalized(String rule) {
        return rule.toLowerCase(Locale.ROOT)
            .trim()
            .replaceAll("\\s+", " ");
    }

    private static int amount(String token) {
        return switch (token) {
            case "one", "1" -> 1;
            case "two", "2" -> 2;
            case "three", "3" -> 3;
            case "four", "4" -> 4;
            case "five", "5" -> 5;
            case "six", "6" -> 6;
            default -> -1;
        };
    }

    public static boolean validRule(String rule) {
        if (rule == null || rule.isBlank()) return false;
        for (String condition : normalized(rule).split("\\s*&&\\s*", -1)) {
            String[] t = condition.split(" ");
            if (t.length < 2 || amount(t[0]) < 1) return false;
            int index = t[1].equals("different") ? 2 : 1;
            if (t.length <= index) return false;
            String target = t[index];
            if (Set.of("cavity", "cavities", "magnet", "magnets", "yoke")
                .contains(target)) {
                if (t.length != index + 1) return false;
            } else if (t.length != index + 2 || !Set.of("cooler", "coolers")
                .contains(t[index + 1])
                || !Arrays.asList(NAMES)
                    .contains(target))
                return false;
        }
        return true;
    }

    public static int cooling(int variant) {
        return AcceleratorConfig.COOLERS.get(NAMES[variant])
            .cooling()
            .get();
    }

    public static Set<BlockPos> active(Map<BlockPos, AbstractAcceleratorBlockEntity> parts, Set<BlockPos> components) {
        Set<BlockPos> active = new HashSet<>(components);
        boolean changed;
        do {
            changed = false;
            for (var part : parts.values())
                if (part.partType().role == AcceleratorPartType.Role.COOLER && !active.contains(part.getBlockPos())
                    && test(
                        AcceleratorConfig.COOLERS.get(NAMES[part.partType().variant])
                            .rule()
                            .get(),
                        part.getBlockPos(),
                        parts,
                        active))
                    changed |= active.add(part.getBlockPos());
        } while (changed);
        active.removeAll(components);
        return Set.copyOf(active);
    }

    public static boolean test(String rule, BlockPos pos, Map<BlockPos, AbstractAcceleratorBlockEntity> parts,
        Set<BlockPos> active) {
        if (!validRule(rule)) throw new IllegalArgumentException("Unknown cooler rule: " + rule);
        for (String condition : normalized(rule).split("\\s*&&\\s*")) {
            String[] tokens = condition.split(" ");
            int required = amount(tokens[0]);
            boolean different = tokens[1].equals("different");
            String target = tokens[different ? 2 : 1];
            int count = 0;
            Set<String> variants = new HashSet<>();
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = pos.relative(direction);
                var part = parts.get(neighbor);
                if (part == null) continue;
                var type = part.partType();
                boolean matches = switch (target) {
                    case "cavity", "cavities" -> type.role == AcceleratorPartType.Role.CAVITY
                        && active.contains(neighbor);
                    case "magnet", "magnets" -> type.role == AcceleratorPartType.Role.MAGNET
                        && active.contains(neighbor);
                    case "yoke" -> type.role == AcceleratorPartType.Role.YOKE && active.contains(neighbor);
                    default -> type.role == AcceleratorPartType.Role.COOLER && active.contains(neighbor)
                        && NAMES[type.variant].equals(target);
                };
                if (matches && (!different || variants.add(type.id))) count++;
            }
            if (count < required) return false;
        }
        return true;
    }
}
