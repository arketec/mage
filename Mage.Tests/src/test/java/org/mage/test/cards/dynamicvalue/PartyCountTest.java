package org.mage.test.cards.dynamicvalue;

import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.dynamicvalue.common.PartyCount;
import mage.abilities.effects.common.GainLifeEffect;
import mage.constants.CardType;
import mage.constants.PhaseStep;
import mage.constants.SubType;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author TheElk801
 */
public class PartyCountTest extends CardTestPlayerBase {

    private void makeTester() {
        addCustomCardWithAbility(
                "tester", playerA,
                new SimpleActivatedAbility(
                        new GainLifeEffect(PartyCount.instance), new ManaCostsImpl<>("{0}")
                )
        );
    }

    private void useTester() {
        activateAbility(1, PhaseStep.POSTCOMBAT_MAIN, playerA, "{0}:");
    }

    private void makeCreature(String name, SubType... subTypes) {
        addCustomCardWithAbility(
                name, playerA, null, null,
                CardType.CREATURE, "{1}", Zone.BATTLEFIELD, subTypes
        );
    }

    @Test
    public void testSingleMember() {
        makeTester();
        makeCreature("crt1", SubType.CLERIC);

        useTester();
        setStopAt(1, PhaseStep.END_TURN);
        setStrictChooseMode(true);
        execute();
        assertAllCommandsUsed();

        assertLife(playerA, 21);
    }

    @Test
    public void testSingleMember2() {
        makeTester();
        makeCreature("crt1", SubType.CLERIC, SubType.WIZARD);

        useTester();
        setStopAt(1, PhaseStep.END_TURN);
        setStrictChooseMode(true);
        execute();
        assertAllCommandsUsed();

        assertLife(playerA, 21);
    }

    @Test
    public void testTwoMembers() {
        makeTester();
        makeCreature("crt1", SubType.CLERIC);
        makeCreature("crt2", SubType.WARRIOR);

        useTester();
        setStopAt(1, PhaseStep.END_TURN);
        setStrictChooseMode(true);
        execute();
        assertAllCommandsUsed();

        assertLife(playerA, 22);
    }

    @Test
    public void testTwoMembers2() {
        makeTester();
        makeCreature("crt1", SubType.CLERIC);
        makeCreature("crt2", SubType.CLERIC);

        useTester();
        setStopAt(1, PhaseStep.END_TURN);
        setStrictChooseMode(true);
        execute();
        assertAllCommandsUsed();

        assertLife(playerA, 21);
    }

    @Test
    public void testThreeMembers() {
        makeTester();
        makeCreature("crt1", SubType.CLERIC);
        makeCreature("crt2", SubType.WARRIOR);
        makeCreature("crt3", SubType.WIZARD);

        useTester();
        setStopAt(1, PhaseStep.END_TURN);
        setStrictChooseMode(true);
        execute();
        assertAllCommandsUsed();

        assertLife(playerA, 23);
    }

    @Test
    public void testThreeMembers2() {
        makeTester();
        makeCreature("crt1", SubType.CLERIC, SubType.WARRIOR);
        makeCreature("crt2", SubType.CLERIC, SubType.WARRIOR);
        makeCreature("crt3", SubType.CLERIC, SubType.WARRIOR, SubType.WIZARD);

        useTester();
        setStopAt(1, PhaseStep.END_TURN);
        setStrictChooseMode(true);
        execute();
        assertAllCommandsUsed();

        assertLife(playerA, 23);
    }

    @Test
    public void testOddCombos() {
        makeTester();
        makeCreature("crt1", SubType.ROGUE, SubType.WIZARD, SubType.WARRIOR);
        makeCreature("crt2", SubType.ROGUE, SubType.CLERIC);
        makeCreature("crt3", SubType.CLERIC, SubType.WIZARD);
        makeCreature("crt4", SubType.WARRIOR, SubType.WIZARD);

        useTester();
        setStopAt(1, PhaseStep.END_TURN);
        setStrictChooseMode(true);
        execute();
        assertAllCommandsUsed();

        assertLife(playerA, 24);
    }

    @Test
    public void testChangelings() {
        makeTester();
        addCard(Zone.BATTLEFIELD, playerA, "Impostor of the Sixth Pride", 3);

        useTester();

        setStopAt(1, PhaseStep.END_TURN);
        setStrictChooseMode(true);
        execute();
        assertAllCommandsUsed();

        assertLife(playerA, 23);
    }
}