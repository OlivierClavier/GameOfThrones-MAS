package fr.junia.got.interaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MessageBagTest {

    @Test
    void refusesDuplicates() {
        MessageBag bag = new MessageBag();
        assertTrue(bag.add("STARK-01"));
        assertFalse(bag.add("STARK-01"));
        assertEquals(1, bag.size());
    }

    @Test
    void unionAddsOnlyNewMessages() {
        MessageBag a = new MessageBag();
        a.add("STARK-01");
        a.add("STARK-02");
        MessageBag b = new MessageBag();
        b.add("STARK-02");
        b.add("STARK-03");
        assertEquals(1, a.addAll(b));
        assertEquals(3, a.size());
    }

    @Test
    void stolenMessagesLeaveTheLoser() {
        MessageBag winner = new MessageBag();
        MessageBag loser = new MessageBag();
        loser.add("LANNISTER-01");
        loser.add("LANNISTER-02");
        assertEquals(2, winner.takeRandomMissing(loser, 5));
        assertEquals(2, winner.size());
        assertEquals(0, loser.size());
    }

    @Test
    void sharedMessagesStayWithTheGiver() {
        MessageBag receiver = new MessageBag();
        MessageBag giver = new MessageBag();
        giver.add("TARGARYEN-01");
        assertEquals(1, receiver.copyRandomMissing(giver, 3));
        assertEquals(1, giver.size());
    }
}
