package lach_01298.qmd.multiblock.accelerator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AcceleratorCoolerRulesTest {

    @Test
    void everyShippedPlacementRuleIsAcceptedByTheConfigurationValidator() {
        for (String rule : AcceleratorCoolers.RULES) assertTrue(AcceleratorCoolers.validRule(rule), rule);
    }

    @Test
    void numberWordsAndPluralCoolersRetainTheLegacyConfigurationSpelling() {
        for (String amount : new String[] { "one", "two", "three", "four", "five", "six", "1", "2", "3", "4", "5",
            "6" }) {
            assertTrue(AcceleratorCoolers.validRule(amount + " tin coolers"));
            assertTrue(AcceleratorCoolers.validRule(amount + " different magnets"));
        }
        assertTrue(AcceleratorCoolers.validRule(" TWO  LAPIS COOLERS&&one cavity "));
        assertFalse(AcceleratorCoolers.validRule("seven tin coolers"));
        assertFalse(AcceleratorCoolers.validRule("one nonexistent cooler"));
        assertFalse(AcceleratorCoolers.validRule("one cavity &&"));
    }
}
