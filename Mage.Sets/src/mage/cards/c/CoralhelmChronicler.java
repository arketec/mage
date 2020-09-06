package mage.cards.c;

import mage.MageInt;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.common.SpellCastControllerTriggeredAbility;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.abilities.effects.common.DrawDiscardControllerEffect;
import mage.abilities.effects.common.LookLibraryAndPickControllerEffect;
import mage.abilities.keyword.KickerAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.filter.FilterSpell;
import mage.filter.predicate.mageobject.AbilityPredicate;
import mage.filter.predicate.mageobject.KickedPredicate;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class CoralhelmChronicler extends CardImpl {

    private static final FilterSpell filter = new FilterSpell("a kicked spell");
    private static final FilterCard filter2 = new FilterCard("card with a kicker ability");

    static {
        filter.add(KickedPredicate.instance);
        filter2.add(new AbilityPredicate(KickerAbility.class));
    }

    public CoralhelmChronicler(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{U}");

        this.subtype.add(SubType.MERFOLK);
        this.subtype.add(SubType.WIZARD);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Whenever you cast a kicked spell, draw a card, then discard a card.
        this.addAbility(new SpellCastControllerTriggeredAbility(
                new DrawDiscardControllerEffect(1, 1, false), filter, false
        ));

        // When Coralhelm Chronicler enters the battlefield, look at the top five cards of your library. You may reveal a card with a kicker ability from among them and put it into your hand. Put the rest on the bottom of your library in a random order.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new LookLibraryAndPickControllerEffect(
                StaticValue.get(5), false, StaticValue.get(1), filter2, Zone.LIBRARY, false,
                true, false, Zone.HAND, true, false, false
        ).setBackInRandomOrder(true).setText("look at the top five cards of your library. " +
                "You may reveal a card with a kicker ability from among them and put it into your hand. " +
                "Put the rest on the bottom of your library in a random order")));
    }

    private CoralhelmChronicler(final CoralhelmChronicler card) {
        super(card);
    }

    @Override
    public CoralhelmChronicler copy() {
        return new CoralhelmChronicler(this);
    }
}